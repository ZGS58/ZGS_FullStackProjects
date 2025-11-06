package ZGS.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import ZGS.backend.BaseResponse;
import ZGS.backend.dto.UserDto;
import ZGS.backend.entity.User;
import ZGS.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ===== 一般用戶功能 =====

    // 獲取當前用戶資料
    @GetMapping("/me")
    public ResponseEntity<BaseResponse<User>> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.error("請先登入"));
        }

        String username = authentication.getName();
        User user = userService.getByUsername(username)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
        return ResponseEntity.ok(BaseResponse.ok(user));
    }

    // 更新當前用戶資料
    @PutMapping("/me")
    public ResponseEntity<BaseResponse<User>> updateCurrentUser(
            @Valid @RequestBody UserDto dto,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.error("請先登入"));
        }

        String username = authentication.getName();
        User user = userService.getByUsername(username)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));

        User updated = userService.update(user.getId(), dto);
        return ResponseEntity.ok(BaseResponse.ok(updated));
    }

    // 修改當前用戶密碼
    @PutMapping("/me/password")
    public ResponseEntity<BaseResponse<String>> updatePassword(
            @RequestBody Map<String, String> request,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.error("請先登入"));
        }

        String username = authentication.getName();
        User user = userService.getByUsername(username)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));

        String newPassword = request.get("newPassword");
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error("新密碼不能為空"));
        }

        userService.updatePassword(user.getId(), newPassword);
        return ResponseEntity.ok(BaseResponse.ok("密碼修改成功"));
    }

    // 管理員功能

    // 獲取所有用戶（管理員）
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<List<User>>> getAll() {
        List<User> userList = userService.getAll();
        return ResponseEntity.ok(BaseResponse.ok(userList));
    }

    // 根據 ID 獲取用戶（管理員）
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<User>> getById(@PathVariable Long id) {
        User user = userService.getById(id)
                .orElseThrow(() -> new RuntimeException("找不到該用戶"));
        return ResponseEntity.ok(BaseResponse.ok(user));
    }

    // 更新用戶資料（管理員）
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<User>> update(
            @PathVariable Long id,
            @Valid @RequestBody UserDto dto) {
        User updated = userService.update(id, dto);
        return ResponseEntity.ok(BaseResponse.ok(updated));
    }

    // 刪除用戶（管理員）
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<String>> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok("用戶刪除成功"));
    }

    // 分頁查詢用戶（管理員）
    @GetMapping("/paged")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Page<User>>> getPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        Page<User> userPage = userService.searchPaged(keyword, PageRequest.of(page, size));
        return ResponseEntity.ok(BaseResponse.ok(userPage));
    }
}
