package sookmyung.noonsongmaker.Controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import sookmyung.noonsongmaker.Dto.Response;
import sookmyung.noonsongmaker.Dto.auth.LoginRequestDto;
import sookmyung.noonsongmaker.Dto.auth.MailSendDto;
import sookmyung.noonsongmaker.Dto.auth.SignupRequestDto;
import sookmyung.noonsongmaker.Dto.auth.VerificationRequestDto;
import sookmyung.noonsongmaker.Service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import sookmyung.noonsongmaker.Service.auth.EmailService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<Response<String>> signUp(@RequestBody SignupRequestDto signupRequestDto) {
        authService.signUp(signupRequestDto);
        return ResponseEntity.ok(Response.buildResponse(null, "회원가입 성공"));
    }

    @PostMapping("/send-email")
    public ResponseEntity<Response<String>> sendVerificationCode(@RequestBody MailSendDto mailSendDto) {
        emailService.sendEmail(mailSendDto);
        return ResponseEntity.ok(Response.buildResponse(null, "인증 코드가 전송되었습니다."));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Response<Boolean>> verifyCode(@RequestBody VerificationRequestDto verificationRequestDto) {
        boolean isVerified = emailService.verifyCode(verificationRequestDto);
        return ResponseEntity.ok(Response.buildResponse(isVerified, isVerified ? "인증 성공" : "인증 실패"));
    }

    @PostMapping("/login")
    public ResponseEntity<Response<Map<String, String>>> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String email = authService.login(loginRequestDto, response);

        Map<String, String> responseData = new HashMap<>();
        responseData.put("email", email);
        return ResponseEntity.ok(Response.buildResponse(responseData, "로그인 성공"));
    }

    @GetMapping("/refresh")
    public ResponseEntity<Response<Map<String, String>>> refreshToken(@CookieValue(value = "REFRESH_TOKEN", required = false) String refreshToken, HttpServletResponse response) {
        if (refreshToken == null) {
            throw new IllegalArgumentException("쿠키에 Refresh Token이 없습니다.");
        }

        String email = authService.refreshAccessToken(refreshToken, response);

        Map<String, String> responseData = new HashMap<>();
        responseData.put("email", email);
        return ResponseEntity.ok(Response.buildResponse(responseData, "Access Token 갱신 성공"));
    }

    @GetMapping("/logout")
    public ResponseEntity<Response<String>> logout(@CookieValue(value = "REFRESH_TOKEN", required = false) String refreshToken, HttpServletResponse response) {
        if (refreshToken != null) {
            authService.logout(refreshToken, response);
        }

        return ResponseEntity.ok(Response.buildResponse(null, "로그아웃 성공"));
    }
}
