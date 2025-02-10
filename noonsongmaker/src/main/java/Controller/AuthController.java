package Controller;

import Dto.Response;
import Dto.auth.SignupRequestDto;
import Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Response<String>> signUp(@RequestBody SignupRequestDto signupRequestDto) {
            authService.signUp(signupRequestDto);
        return ResponseEntity.ok(Response.buildResponse(null, "회원가입 성공"));
    }
}
