package ibas.inchelin.web.controller;

import ibas.inchelin.domain.user.service.UserService;
import ibas.inchelin.web.dto.review.ReviewListResponse;
import ibas.inchelin.web.dto.user.*;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/{userId}/following")
    public ResponseEntity<FollowingListResponse> getFollowing(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getFollowing(userId));
    }

    @PostMapping("/{targetUserId}/follow")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> follow(Authentication authentication, @PathVariable Long targetUserId) {
        userService.follow(authentication.getName(), targetUserId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{targetUserId}/follow")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> unfollow(Authentication authentication, @PathVariable Long targetUserId) {
        userService.unfollow(authentication.getName(), targetUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/reviews")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReviewListResponse> getMyReviews(Authentication authentication, @RequestParam(required = false, defaultValue = "latest") String sort) {
        return ResponseEntity.ok(userService.getMyReviews(authentication.getName(), sort));
    }

    @GetMapping("/me/lists")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MyListResponse> getMyLists(Authentication authentication) {
        return ResponseEntity.ok(userService.getMyLists(authentication.getName()));
    }

    @PostMapping("/me/lists")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> addMyList(Authentication authentication, @RequestBody MyListAddRequest request) {
        userService.addMyList(authentication.getName(), request.getListName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/me/lists/{listId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteMyList(Authentication authentication, @PathVariable Long listId) {
        userService.deleteMyList(authentication.getName(), listId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/lists/{listId}/items")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MyListItemListResponse> getMyListItemList(Authentication authentication, @PathVariable Long listId) {
        return ResponseEntity.ok(userService.getMyListItems(authentication.getName(), listId));
    }
}
