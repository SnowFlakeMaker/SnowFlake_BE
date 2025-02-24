package sookmyung.noonsongmaker.Dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MailSendDto {
    @NotBlank(message = "숙명 이메일 아이디를 입력하세요.")
    private String emailId;

    public MailSendDto(String emailId) {
        this.emailId = emailId;
    }
}
