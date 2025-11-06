//全域錯誤處理
package ZGS.backend.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ZGS.backend.BaseResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<String>> handleException(Exception ex) {
        return ResponseEntity
            .badRequest()
            .body(BaseResponse.error(ex.getMessage ()));
    }
}
