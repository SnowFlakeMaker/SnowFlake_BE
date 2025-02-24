package sookmyung.noonsongmaker.Dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerificationRequestDto {

    private String emailId;
    private String code;

    public VerificationRequestDto(String emailId, String code) {
        this.emailId = emailId;
        this.code = code;
    }
}
