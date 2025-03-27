package sookmyung.noonsongmaker.Dto.main;

import lombok.Getter;
import sookmyung.noonsongmaker.Entity.MajorType;
import sookmyung.noonsongmaker.Entity.UserProfile;

@Getter
public class PlayerInfoResponseDto {
    private final String nickname;
    private final String major;
    private final String majorType;
    private final String birthday;
    private final String mbti;
    private final String hobby;
    private final String dream;

    public PlayerInfoResponseDto(UserProfile userProfile) {
        this.nickname = userProfile.getNickname();
        this.major = userProfile.getMajor();
        this.majorType = convertMajorType(userProfile.getMajorType());
        this.birthday = userProfile.getBirthday();
        this.mbti = userProfile.getMbti().name();
        this.hobby = userProfile.getHobby();
        this.dream = userProfile.getDream();
    }

    private String convertMajorType(MajorType majorType) {
        if (majorType == null || majorType == MajorType.UNKNOWN) {
            return "미정";
        }

        return switch (majorType) {
            case DOUBLE_MAJOR -> "복수전공";
            case SUB_MAJOR -> "부전공";
            case ADVANCED_MAJOR -> "심화전공";
            default -> "미정";
        };
    }
}