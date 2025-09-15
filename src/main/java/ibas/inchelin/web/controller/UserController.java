package ibas.inchelin.web.controller;

import ibas.inchelin.S3Service;
import ibas.inchelin.domain.user.service.UserService;
import ibas.inchelin.web.dto.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final S3Service s3Service;

    @GetMapping("/users/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MyInfoResponse> getMyInfo(Authentication authentication) {
        return ResponseEntity.ok(userService.getMyInfo(authentication.getName()));
    }

    @PatchMapping("/users/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MyInfoResponse> updateMyInfo(Authentication authentication, @RequestBody MyInfoUpdateRequest request) {
        return ResponseEntity.ok(userService.updateMyInfo(request.getNickname(), request.getBio(), authentication.getName()));
    }

    @PutMapping(value = "/users/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> updateProfileImage(Authentication authentication, @RequestParam MultipartFile file) throws IOException {
        String url = s3Service.uploadOne(file);
        userService.updateProfileImage(authentication.getName(), url);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profiles/{userId}")
    public ResponseEntity<OtherUserInfoResponse> getOtherUserInfo(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.otherUserInfo(userId));
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<FollowerListResponse> getFollowers(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getFollowers(userId));
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<FollowingListResponse> getFollowing(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getFollowing(userId));
    }

    @PostMapping("/users/{targetUserId}/follow")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> follow(Authentication authentication, @PathVariable Long targetUserId) {
        userService.follow(authentication.getName(), targetUserId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/users/{targetUserId}/follow")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> unfollow(Authentication authentication, @PathVariable Long targetUserId) {
        userService.unfollow(authentication.getName(), targetUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/me/lists")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MyListResponse> getMyLists(Authentication authentication) {
        return ResponseEntity.ok(userService.getMyLists(authentication.getName()));
    }

    @PostMapping("/users/me/lists")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> addMyList(Authentication authentication, @RequestBody MyListAddRequest request) {
        userService.addMyList(authentication.getName(), request.getListName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/users/me/lists/{listId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteMyList(Authentication authentication, @PathVariable Long listId) {
        userService.deleteMyList(authentication.getName(), listId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/me/lists/{listId}/items")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MyListItemListResponse> getMyListItemList(Authentication authentication, @PathVariable Long listId) {
        return ResponseEntity.ok(userService.getMyListItems(authentication.getName(), listId));
    }

    @PostMapping("/users/me/lists/{listId}/items")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> addMyListItem(Authentication authentication, @PathVariable Long listId, @RequestBody MyListItemAddRequest request) {
        userService.addMyListItem(authentication.getName(), listId, request.getStoreId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/users/me/lists/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteMyListItem(Authentication authentication, @PathVariable Long itemId) {
        userService.deleteMyListItem(authentication.getName(), itemId);
        return ResponseEntity.noContent().build();
    }
}
