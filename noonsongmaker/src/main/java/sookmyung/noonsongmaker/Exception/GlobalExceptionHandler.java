package sookmyung.noonsongmaker.Exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import sookmyung.noonsongmaker.Dto.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // 401 Unauthorized
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Response<Object>> handleAuthenticationException(AuthenticationException ex) {
        Response<Object> response = new Response<>(
                "인증에 실패했습니다.",
                null
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // 400 Bad Request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response<Object>> handleBadRequestException(IllegalArgumentException ex) {
        Response<Object> response = new Response<>(
                "잘못된 요청입니다.",
                ex.getMessage()
        );
        return ResponseEntity.badRequest().body(response);
    }

    // 400 Bad Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        Response<Object> response = new Response<>(
                "입력 값이 올바르지 않습니다.",
                errorMessage
        );
        return ResponseEntity.badRequest().body(response);
    }

    // 403 Forbidden
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        Response<Object> response = new Response<>(
                "접근 권한이 없습니다.",
                null
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // 404 Not Found
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Response<Object>> handleNoSuchElementException(NoSuchElementException ex) {
        Response<Object> response = new Response<>(
                "해당 리소스를 찾을 수 없습니다.",
                null
        );
        return ResponseEntity.status(404).body(response);
    }

    // 404 Not Found (잘못된 URL 요청)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Response<Object>> handleNotFoundException(NoHandlerFoundException ex) {
        Response<Object> response = new Response<>(
                "요청한 URL을 찾을 수 없습니다.",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Object>> handleGlobalException(Exception ex) {
        Response<Object> response = new Response<>(
                "서버 내부 오류가 발생했습니다.",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(StressOverflowException.class)
    public ResponseEntity<Response<Object>> handleStressEnding(Exception ex) {
        Response<Object> response = new Response<>(
                ex.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response); // 405
    }

    @ExceptionHandler(ActionRefusedException.class)
    public ResponseEntity<Response<Object>> handleActionRefused(ActionRefusedException ex) {
        Response<Object> response = new Response<>(
                ex.getMessage(),
                ex.getData() // isSuccess: false
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // 409 Conflict
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Object>> handleGlobalException(HttpServletRequest request, Exception ex) {
        if ("text/event-stream".equals(request.getHeader("Accept"))) {
            log.warn("SSE 요청 중 예외 발생, 에러 전송 생략");
            return null; // 응답 무시
        }

        log.error("예외 발생", ex);
        Response<Object> response = new Response<>(
                "알 수 없는 오류가 발생했습니다.",
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}