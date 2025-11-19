package ibas.inchelin.auth.web.controller;

import ibas.inchelin.auth.jwt.JwtProvider;
import ibas.inchelin.auth.web.dto.LoginInfoResponse;
import ibas.inchelin.auth.web.dto.GoogleOAuthTokenRequest;
import ibas.inchelin.auth.web.dto.GoogleOAuthTokenResponse;
import ibas.inchelin.auth.web.dto.ApiErrorResponse;
import ibas.inchelin.auth.web.dto.RefreshTokenRequest;
import ibas.inchelin.auth.web.dto.RefreshTokenResponse;
import ibas.inchelin.auth.web.dto.LogoutRequest;
import ibas.inchelin.auth.web.dto.LogoutResponse;
import ibas.inchelin.domain.user.Role;
import ibas.inchelin.domain.user.entity.RefreshToken;
import ibas.inchelin.domain.user.entity.User;
import ibas.inchelin.domain.user.repository.RefreshTokenRepository;
import ibas.inchelin.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @GetMapping("/oauth2/google/url")
    public ResponseEntity<LoginInfoResponse> loginInfo() {
        String googleOAuth2Url = String.format(
                "https://accounts.google.com/o/oauth2/v2/auth?client_id=%s&redirect_uri=%s&scope=profile%%20email&response_type=code",
                googleClientId, redirectUri
        );
        return ResponseEntity.ok(new LoginInfoResponse(googleOAuth2Url));
    }

    @PostMapping("/oauth2/google/token")
    public ResponseEntity<?> exchangeCodeForToken(@RequestBody GoogleOAuthTokenRequest requestDto) {
        String code = requestDto.getCode();
        if (code == null || code.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponse(
                            "INVALID_CODE",
                            "Authorization code is required"
                    ));
        }
        try {
            String decodedCode = java.net.URLDecoder.decode(code, StandardCharsets.UTF_8);
            String googleAccessToken = exchangeCodeForGoogleToken(decodedCode);
            Map<String, Object> userInfo = getUserInfoFromGoogle(googleAccessToken);
            String email = (String) userInfo.get("email");
            String name = (String) userInfo.get("name");
            if (email == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiErrorResponse(
                                "EMAIL_NOT_FOUND",
                                "Failed to get user email from Google"
                        ));
            }
            Role role = Role.USER;
            Optional<User> userOpt = userRepository.findByEmail(email);
            User user = userOpt.orElseGet(() -> userRepository.save(User.builder()
                    .email(email)
                    .name(name)
                    .role(role)
                    .build()));
            String accessToken = jwtProvider.createAccessToken(user.getSub(), user.getRole());
            String refreshToken = jwtProvider.createRefreshToken(user.getSub());
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiresAt = now.plusDays(7);
            refreshTokenRepository.findByUser(user)
                    .ifPresentOrElse(
                            rt -> {
                                rt.renew(refreshToken, now, expiresAt);
                                refreshTokenRepository.save(rt);
                            },
                            () -> refreshTokenRepository.save(RefreshToken.builder()
                                    .user(user)
                                    .refreshToken(refreshToken)
                                    .issuedAt(now)
                                    .expiresAt(expiresAt)
                                    .isActive(true)
                                    .build())
                    );
            return ResponseEntity.ok(new GoogleOAuthTokenResponse(
                    accessToken,
                    refreshToken,
                    user.getEmail(),
                    user.getName(),
                    user.getRole().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiErrorResponse(
                            "TOKEN_EXCHANGE_ERROR",
                            "토큰 교환 중 오류: " + e.getMessage()
                    ));
        }
    }

    // access token 재발급
    @PostMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest requestDto) {
        String refreshToken = requestDto.getRefreshToken();
        if (refreshToken == null || !jwtProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiErrorResponse(
                            "INVALID_REFRESH_TOKEN",
                            "Invalid refresh token"
                    ));
        }
        Optional<RefreshToken> refreshTokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken);
        if (refreshTokenEntity.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiErrorResponse(
                            "REFRESH_TOKEN_NOT_FOUND",
                            "Refresh token not found"
                    ));
        }
        RefreshToken tokenEntity = refreshTokenEntity.get();
        if (!tokenEntity.getIsActive() || LocalDateTime.now().isAfter(tokenEntity.getExpiresAt())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiErrorResponse(
                            "REFRESH_TOKEN_EXPIRED",
                            "Refresh token expired or inactive"
                    ));
        }
        User user = tokenEntity.getUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiErrorResponse(
                            "USER_NOT_FOUND",
                            "User not found"
                    ));
        }
        String newAccessToken = jwtProvider.createAccessToken(user.getSub(), user.getRole());
        return ResponseEntity.ok(new RefreshTokenResponse(newAccessToken, refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody(required = false) LogoutRequest requestDto) {
        String providedRefreshToken = requestDto != null ? requestDto.getRefreshToken() : null;
        if (providedRefreshToken != null && !providedRefreshToken.isBlank()) {
            Optional<RefreshToken> refreshTokenEntity = refreshTokenRepository.findByRefreshToken(providedRefreshToken);
            if (refreshTokenEntity.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiErrorResponse(
                                "REFRESH_TOKEN_NOT_FOUND",
                                "Refresh token not found"
                        ));
            }
            RefreshToken tokenEntity = refreshTokenEntity.get();
            if (tokenEntity.getIsActive()) {
                tokenEntity.deactivate();
                refreshTokenRepository.save(tokenEntity);
            }
            return ResponseEntity.ok(new LogoutResponse("로그아웃 성공 (refresh token 사용)"));
        } else {
            String sub = null;
            if (org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() != null) {
                Object principal = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (principal instanceof String) {
                    sub = (String) principal;
                }
            }
            if (sub == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiErrorResponse(
                                "AUTH_REQUIRED",
                                "refreshToken 또는 유효한 Access Token 필요"
                        ));
            }
            Optional<User> userOpt = userRepository.findBySub(sub);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiErrorResponse(
                                "USER_NOT_FOUND",
                                "User not found"
                        ));
            }
            User user = userOpt.get();
            Optional<RefreshToken> activeRtOpt = refreshTokenRepository.findByUserAndIsActive(user, true);
            if (activeRtOpt.isEmpty()) {
                return ResponseEntity.ok(new LogoutResponse("활성 refresh token 없음"));
            }
            RefreshToken activeRt = activeRtOpt.get();
            activeRt.deactivate();
            refreshTokenRepository.save(activeRt);
            return ResponseEntity.ok(new LogoutResponse("로그아웃 성공 (인증정보 사용)"));
        }
    }

    private String exchangeCodeForGoogleToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("code", code);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(
                "https://oauth2.googleapis.com/token",
                request,
                (Class<Map<String, Object>>) (Class<?>) Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null) {
            throw new RuntimeException("Google token response is null");
        }
        return (String) responseBody.get("access_token");
    }

    private Map<String, Object> getUserInfoFromGoogle(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v2/userinfo",
                HttpMethod.GET,
                entity,
                (Class<Map<String, Object>>) (Class<?>) Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null) {
            throw new RuntimeException("Google user info response is null");
        }
        return responseBody;
    }
}
