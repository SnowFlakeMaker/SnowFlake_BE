package Exception;

import Dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 401 Unauthorized
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseDto<Object>> handleAuthenticationException(AuthenticationException ex) {
        ResponseDto<Object> response = new ResponseDto<>(
                "인증에 실패했습니다.",
                null
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // 400 Bad Request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDto<Object>> handleBadRequestException(IllegalArgumentException ex) {
        ResponseDto<Object> response = new ResponseDto<>(
                "잘못된 요청입니다.",
                ex.getMessage()
        );
        return ResponseEntity.badRequest().body(response);
    }

    // 400 Bad Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ResponseDto<Object> response = new ResponseDto<>(
                "입력 값이 올바르지 않습니다.",
                errorMessage
        );
        return ResponseEntity.badRequest().body(response);
    }

    // 403 Forbidden
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseDto<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        ResponseDto<Object> response = new ResponseDto<>(
                "접근 권한이 없습니다.",
                null
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // 404 Not Found
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ResponseDto<Object>> handleNoSuchElementException(NoSuchElementException ex) {
        ResponseDto<Object> response = new ResponseDto<>(
                "해당 리소스를 찾을 수 없습니다.",
                null
        );
        return ResponseEntity.status(404).body(response);
    }

    // 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Object>> handleGlobalException(Exception ex) {
        ResponseDto<Object> response = new ResponseDto<>(
                "서버 내부 오류가 발생했습니다.",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}