package ibas.inchelin.web.controller;

import ibas.inchelin.domain.user.service.UserService;
import ibas.inchelin.web.dto.user.MyInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MyInfoResponse> getMyInfo(Authentication authentication) {
        String sub = authentication.getName();
        return ResponseEntity.ok(userService.getMyInfo(sub));
    }
}
