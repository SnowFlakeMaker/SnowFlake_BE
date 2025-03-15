package sookmyung.noonsongmaker.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sookmyung.noonsongmaker.Entity.*;
import sookmyung.noonsongmaker.Repository.UserProfileRepository;
import sookmyung.noonsongmaker.Repository.UserRepository;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    // 전공 신청
    @Transactional
    public void applyForMajor(Long userId, String majorChoice) {
        User user = getUser(userId);
        UserProfile userProfile = getUserProfile(user);
        int currentSemester = user.getCurrentChapter().getSemester();

        if (currentSemester < 3 || currentSemester > 5) {
            throw new IllegalArgumentException("전공 신청은 3~5학기에만 가능합니다.");
        }

        MajorType majorType = determineMajorType(majorChoice, currentSemester);
        userProfile.setMajorType(majorType);
        userProfileRepository.save(userProfile);
    }

    // 전공 포기 (복수전공/부전공만 가능, 학기 제한 없음)
    @Transactional
    public void dropMajor(Long userId) {
        User user = getUser(userId);
        UserProfile userProfile = getUserProfile(user);
        int currentSemester = user.getCurrentChapter().getSemester();

        if (userProfile.getMajorType() != MajorType.DOUBLE_MAJOR && userProfile.getMajorType() != MajorType.SUB_MAJOR) {
            throw new IllegalArgumentException("전공 포기는 복수전공 또는 부전공에만 가능합니다.");
        }

        // 5학기 이하 -> 미정, 6학기 이상 -> 심화전공
        MajorType newMajorType = (currentSemester < 5) ? MajorType.UNKNOWN : MajorType.ADVANCED_MAJOR;
        userProfile.setMajorType(newMajorType);

        userProfileRepository.save(userProfile);
    }

    // 전공 유형 결정 로직
    private MajorType determineMajorType(String majorChoice, int currentSemester) {
        if (majorChoice == null || majorChoice.equals("선택안함")) {
            return (currentSemester < 6) ? MajorType.UNKNOWN : MajorType.ADVANCED_MAJOR;
        }

        return switch (majorChoice) {
            case "복수전공" -> MajorType.DOUBLE_MAJOR;
            case "부전공" -> MajorType.SUB_MAJOR;
            case "심화전공" -> MajorType.ADVANCED_MAJOR;
            default -> throw new IllegalArgumentException("올바르지 않은 전공 선택값입니다.");
        };
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
    }

    public UserProfile getUserProfile(User user) {
        return userProfileRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("유저 프로필이 존재하지 않습니다."));
    }
}