package sookmyung.noonsongmaker.Dto.UserProfile;

import lombok.Data;
import sookmyung.noonsongmaker.Entity.MBTI;

@Data
public class UserProfileRequest {
    private String nickname;
    private String birthday;
    private MBTI mbti;
    private String hobby;
    private String dream;
}