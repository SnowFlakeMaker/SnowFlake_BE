package sookmyung.noonsongmaker.Dto.intro;

import lombok.Data;
import sookmyung.noonsongmaker.Entity.MBTI;

@Data
public class UserProfileRequest {
    private String email;
    private String nickname;
    private String birthday;
    private String major;
    private MBTI mbti;
    private String hobby;
    private String dream;
}