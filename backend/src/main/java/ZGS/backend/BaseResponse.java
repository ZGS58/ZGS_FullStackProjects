// 全域回傳格式
package ZGS.backend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {
    private boolean success;
    private String message;
    private T data;
    
    public static <T> BaseResponse<T> ok(T data) {
        return new BaseResponse<>(true, "success", data);
    }

    public static <T> BaseResponse<T> error(String message) {
        return new BaseResponse<>(false, message, null);
    }
}
