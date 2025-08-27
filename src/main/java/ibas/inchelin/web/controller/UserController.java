package ibas.inchelin.web.controller;

import ibas.inchelin.domain.user.service.UserService;
import ibas.inchelin.web.dto.user.FollowerListResponse;
import ibas.inchelin.web.dto.user.MyInfoResponse;
import ibas.inchelin.web.dto.user.MyInfoUpdateRequest;
import ibas.inchelin.web.dto.user.OtherUserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MyInfoResponse> getMyInfo(Authentication authentication) {
        return ResponseEntity.ok(userService.getMyInfo(authentication.getName()));
    }

    @PatchMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MyInfoResponse> updateMyInfo(Authentication authentication, @RequestBody MyInfoUpdateRequest request) {
        return ResponseEntity.ok(userService.updateMyInfo(request.getNickname(), request.getBio(), authentication.getName()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<OtherUserInfoResponse> getOtherUserInfo(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.otherUserInfo(userId));
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<FollowerListResponse> getFollowers(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getFollowers(userId));
    }
}
