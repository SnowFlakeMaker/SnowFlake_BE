package sookmyung.noonsongmaker.Controller;

import sookmyung.noonsongmaker.Dto.Response;
import sookmyung.noonsongmaker.Dto.auth.MailSendDto;
import sookmyung.noonsongmaker.Dto.auth.SignupRequestDto;
import sookmyung.noonsongmaker.Dto.auth.VerificationRequestDto;
import sookmyung.noonsongmaker.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sookmyung.noonsongmaker.Service.EmailService;

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
}
