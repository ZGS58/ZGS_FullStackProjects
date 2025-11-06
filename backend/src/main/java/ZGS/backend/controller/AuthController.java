package ZGS.backend.controller;

import ZGS.backend.BaseResponse;
import ZGS.backend.dto.UserDto;
import ZGS.backend.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString.Exclude;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<UserDto>> register(@RequestBody RegisterRequest request) {
        try {
            UserDto userDto = authService.register(
                    request.getUsername(),
                    request.getEmail(),
                    request.getFullname(),
                    request.getPassword());

            return ResponseEntity.ok(BaseResponse.ok(userDto));

        } catch (IllegalArgumentException e) {
            // 業務邏輯錯誤（如用戶名已存在）
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponse.error(e.getMessage()));

        } catch (Exception e) {
            // 系統錯誤
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.error("註冊失敗：" + e.getMessage()));
        }
    }

    // 內部類別用於接收註冊請求（避免密碼在序列化與日誌中外洩）
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        private String username;
        private String email;
        private String fullname;

        @JsonProperty(access = Access.WRITE_ONLY) // 可接收但不輸出到 JSON
        @Exclude // 不出現於 toString()
        @lombok.EqualsAndHashCode.Exclude // 不參與 equals() 和 hashCode() 比較
        private String password;
    }
}
