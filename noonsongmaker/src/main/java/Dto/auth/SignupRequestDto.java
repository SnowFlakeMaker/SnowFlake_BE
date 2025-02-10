package Dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor
public class SignupRequestDto {

    @NotBlank(message = "숙명 이메일 아이디는 필수 입력값입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;

    @Builder
    public SignupRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
