package ZGS.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactDto {
    
    private Long id;
    @NotBlank(message = "姓名不可為空")
    private String name;

    @Email(message = "請輸入有效的電子郵件地址")
    @NotBlank(message = "電子信箱不可為空")
    private String email;

    @NotBlank(message = "電話不可為空")
    private String phone;

    @NotBlank(message = "內容不可為空")
    private String message;

}
