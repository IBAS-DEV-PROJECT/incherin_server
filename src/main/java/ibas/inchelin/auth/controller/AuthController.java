package ibas.inchelin.auth.controller;

import ibas.inchelin.auth.jwt.JwtProvider;
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

import java.time.LocalDateTime;
import java.util.HashMap;
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
    public ResponseEntity<Map<String, String>> loginInfo() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "인증 정보입니다.");
        // Google OAuth2 URL을 직접 생성
        String googleOAuth2Url = String.format(
                "https://accounts.google.com/o/oauth2/v2/auth?client_id=%s&redirect_uri=%s&scope=profile%%20email&response_type=code",
                googleClientId, redirectUri
        );
        response.put("googleOAuth2Url", googleOAuth2Url);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/oauth2/google/token")
    public ResponseEntity<Map<String, String>> exchangeCodeForToken(@RequestBody Map<String, String> request) {
        String code = request.get("code");

        if (code == null || code.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Authorization code is required");
            return ResponseEntity.status(400).body(error);
        }

        try {
            // URL 디코딩 처리
            String decodedCode = java.net.URLDecoder.decode(code, "UTF-8");

            // 1. Google API로 인가 코드를 액세스 토큰으로 교환
            String googleAccessToken = exchangeCodeForGoogleToken(decodedCode);

            // 2. Google API로 사용자 정보 조회
            Map<String, Object> userInfo = getUserInfoFromGoogle(googleAccessToken);

            String email = (String) userInfo.get("email");
            String name = (String) userInfo.get("name");

            if (email == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Failed to get user email from Google");
                return ResponseEntity.status(400).body(error);
            }

            // 3. 사용자 등록/조회
            Role role = Role.USER;
            Optional<User> userOpt = userRepository.findByEmail(email);
            User user = userOpt.orElseGet(() -> userRepository.save(User.builder()
                    .email(email)
                    .name(name)
                    .role(role)
                    .build()));

            // 4. JWT 토큰 생성
            String accessToken = jwtProvider.createAccessToken(user.getSub(), user.getRole());
            String refreshToken = jwtProvider.createRefreshToken(user.getSub());

            // 5. RefreshToken DB 저장
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiresAt = now.plusDays(7);

            refreshTokenRepository.findByUser(user)
                    .ifPresentOrElse(
                            rt -> {
                                rt.renew(refreshToken, now, expiresAt); // 도메인 메서드로 갱신
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

            Map<String, String> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("email", user.getEmail());
            response.put("name", user.getName());
            response.put("role", user.getRole().toString());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "토큰 교환 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // access token 재발급
    @PostMapping("/token/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || !jwtProvider.validateToken(refreshToken)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid refresh token");
            return ResponseEntity.status(401).body(error);
        }

        Optional<RefreshToken> refreshTokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken);
        if (refreshTokenEntity.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Refresh token not found");
            return ResponseEntity.status(401).body(error);
        }

        RefreshToken tokenEntity = refreshTokenEntity.get();

        if (!tokenEntity.getIsActive() || LocalDateTime.now().isAfter(tokenEntity.getExpiresAt())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Refresh token expired or inactive");
            return ResponseEntity.status(401).body(error);
        }

        User user = tokenEntity.getUser();
        if (user == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found");
            return ResponseEntity.status(401).body(error);
        }

        String newAccessToken = jwtProvider.createAccessToken(user.getSub(), user.getRole());

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", newAccessToken);
        response.put("refreshToken", refreshToken);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestBody(required = false) Map<String, String> request) {
        String providedRefreshToken = request != null ? request.get("refreshToken") : null;
        Map<String, String> response = new HashMap<>();

        if (providedRefreshToken != null && !providedRefreshToken.isBlank()) {
            // 1) 명시적 refreshToken 기반 로그아웃
            Optional<RefreshToken> refreshTokenEntity = refreshTokenRepository.findByRefreshToken(providedRefreshToken);
            if (refreshTokenEntity.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Refresh token not found");
                return ResponseEntity.status(401).body(error);
            }
            RefreshToken tokenEntity = refreshTokenEntity.get();
            if (tokenEntity.getIsActive()) {
                tokenEntity.deactivate();
                refreshTokenRepository.save(tokenEntity);
            }
            response.put("message", "로그아웃 성공");
            return ResponseEntity.ok(response);
        } else {
            // 2) refreshToken 미제공: 현재 인증(sub) 기반
            String sub = null;
            if (org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() != null) {
                Object principal = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (principal instanceof String) {
                    sub = (String) principal; // JwtAuthenticationFilter에서 sub를 principal로 저장
                }
            }
            if (sub == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "refreshToken 또는 유효한 Access Token 필요");
                return ResponseEntity.status(400).body(error);
            }
            Optional<User> userOpt = userRepository.findBySub(sub);
            if (userOpt.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.status(401).body(error);
            }
            User user = userOpt.get();
            Optional<RefreshToken> activeRtOpt = refreshTokenRepository.findByUserAndIsActive(user, true);
            if (activeRtOpt.isEmpty()) {
                response.put("message", "활성 refresh token 없음");
                return ResponseEntity.ok(response);
            }
            RefreshToken activeRt = activeRtOpt.get();
            activeRt.deactivate();
            refreshTokenRepository.save(activeRt);
            response.put("message", "로그아웃 성공");
            return ResponseEntity.ok(response);
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
