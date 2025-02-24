package sookmyung.noonsongmaker.Dto.UserProfile;

import lombok.Getter;
import sookmyung.noonsongmaker.Entity.UserProfile;

@Getter
public class UserProfileResponse {
    private Long userId;
    private String nickname;
    private String major;
    private String birthday;
    private String mbti;
    private String hobby;
    private StatusInfoResponse statusInfo; // DTO 변경

    public UserProfileResponse(UserProfile userProfile, StatusInfoResponse statusInfoResponse) {
        this.userId = userProfile.getUser().getId();
        this.nickname = userProfile.getNickname();
        this.major = userProfile.getMajor();
        this.birthday = userProfile.getBirthday();
        this.mbti = userProfile.getMbti().name(); // Enum → String 변환
        this.hobby = userProfile.getHobby();
        this.statusInfo = statusInfoResponse; // DTO 사용
    }
}
