package sookmyung.noonsongmaker.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sookmyung.noonsongmaker.Entity.*;
import sookmyung.noonsongmaker.Repository.EventChaptersRepository;
import sookmyung.noonsongmaker.Repository.EventRepository;
import sookmyung.noonsongmaker.Repository.UserProfileRepository;
import sookmyung.noonsongmaker.Repository.UserRepository;
import sookmyung.noonsongmaker.Service.event.RegularEventService;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final EventChaptersRepository eventChaptersRepository;
    private final EventRepository eventRepository;

    // 전공 신청
    @Transactional
    public void applyForMajor(Long userId, String majorChoice) {
        User user = getUser(userId);
        UserProfile userProfile = getUserProfile(user);

        validateEventParticipation("전공신청", user);

        MajorType majorType = determineMajorType(majorChoice, user.getCurrentChapter().getSemester());
        userProfile.setMajorType(majorType);
        userProfileRepository.save(userProfile);
    }

    // 전공 포기 (복수전공/부전공만 가능, 학기 제한 없음 -> 이벤트 조건으로 대체)
    @Transactional
    public void dropMajor(Long userId) {
        User user = getUser(userId);
        UserProfile userProfile = getUserProfile(user);

        validateEventParticipation("전공포기", user);

        if (userProfile.getMajorType() != MajorType.DOUBLE_MAJOR && userProfile.getMajorType() != MajorType.SUB_MAJOR) {
            throw new IllegalArgumentException("전공 포기는 복수전공 또는 부전공에만 가능합니다.");
        }

        int currentSemester = user.getCurrentChapter().getSemester();
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

    public EventChapters validateEventParticipation(String eventName, User user) {
        // 이벤트 존재 여부 확인
        Event event = eventRepository.findByName(eventName)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트입니다."));

        // 현재 챕터에서 해당 이벤트가 활성화되었는지 확인
        if (!event.getActivatedChapters().contains(user.getCurrentChapter())) {
            throw new IllegalArgumentException("현재 학기에는 " + eventName + " 이벤트를 신청할 수 없습니다.");
        }

        // 유저의 이벤트 활성화 여부 확인
        return eventChaptersRepository.findByEventAndUser(event, user)
                .orElseThrow(() -> new IllegalArgumentException("이벤트 진행 기록을 찾을 수 없습니다."));
    }

}