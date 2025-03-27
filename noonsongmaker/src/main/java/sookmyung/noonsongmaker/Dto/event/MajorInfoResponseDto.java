package sookmyung.noonsongmaker.Dto.event;
import lombok.Getter;
import sookmyung.noonsongmaker.Entity.MajorType;
import sookmyung.noonsongmaker.Entity.UserProfile;

@Getter
public class MajorInfoResponseDto {
    private final String major;       // 전공 이름 (ex. "컴퓨터공학과")
    private final String majorType;   // 전공 유형 (ex. "복수전공" / "부전공" / "심화전공" / "미정")

    public MajorInfoResponseDto(UserProfile userProfile) {
        this.major = userProfile.getMajor();
        this.majorType = convertMajorType(userProfile.getMajorType());
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