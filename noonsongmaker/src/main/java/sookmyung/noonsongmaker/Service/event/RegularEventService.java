package sookmyung.noonsongmaker.Service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sookmyung.noonsongmaker.Dto.event.StatsResponseDto;
import sookmyung.noonsongmaker.Entity.StatusInfo;
import sookmyung.noonsongmaker.Entity.User;
import sookmyung.noonsongmaker.Entity.Chapter;
import sookmyung.noonsongmaker.Entity.UserProfile;
import sookmyung.noonsongmaker.Repository.StatusInfoRepository;
import sookmyung.noonsongmaker.Repository.UserProfileRepository;
import sookmyung.noonsongmaker.Repository.UserRepository;
import sookmyung.noonsongmaker.Util.EventAvailabilityChecker;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RegularEventService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final StatusInfoRepository statusInfoRepository;

    // 개강총회
    @Transactional
    public StatsResponseDto processOrientation(Long userId) {

        User user = getUser(userId);
        UserProfile userProfile = getUserProfile(user);
        StatusInfo statusInfo = getUserStatus(user);

        // 이벤트 활성화 여부 확인
        if (!EventAvailabilityChecker.isEventAvailable("orientation", user.getCurrentChapter())) {
            throw new IllegalArgumentException("현재 학기에는 개강총회 이벤트가 없습니다.");
        }

        // 개강총회 효과 적용
        statusInfo.modifyStat("social", 5);  // 사회성 증가
        if (userProfile.getMbti().name().startsWith("I")) {
            statusInfo.modifyStat("stress", 5);  // 내향형이면 스트레스 증가
        }

        statusInfoRepository.save(statusInfo);

        return new StatsResponseDto(statusInfo);
    }


    // MT
    @Transactional
    public StatsResponseDto attendMT(Long userId) {
        User user = getUser(userId);
        UserProfile userProfile = getUserProfile(user);
        StatusInfo statusInfo = getUserStatus(user);

        // 이벤트 실행 가능 여부 확인
        if (!EventAvailabilityChecker.isEventAvailable("mt", user.getCurrentChapter())) {
            throw new IllegalArgumentException("현재 학기에는 MT 이벤트가 없습니다.");
        }

        statusInfo.modifyStat("social", 5);
        if (userProfile.getMbti().name().startsWith("I")) {
            statusInfo.modifyStat("stress", 5);
        }

        statusInfoRepository.save(statusInfo);
        return new StatsResponseDto(statusInfo);
    }

    // 축제
    @Transactional
    public StatsResponseDto attendFestival(Long userId) {
        User user = getUser(userId);
        StatusInfo statusInfo = getUserStatus(user);

        // 이벤트 실행 가능 여부 확인
        if (!EventAvailabilityChecker.isEventAvailable("festival", user.getCurrentChapter())) {
            throw new IllegalArgumentException("현재 학기에는 축제 이벤트가 없습니다.");
        }

        // 코인 체크
        if (statusInfo.getCoin() < 5) {
            throw new IllegalArgumentException("코인이 부족하여 축제에 참석할 수 없습니다. 현재 보유 코인: " + statusInfo.getCoin());
        }

        statusInfo.modifyStat("social", 5);
        statusInfo.modifyStat("stress", -5);
        statusInfo.modifyStat("coin", -5);

        statusInfoRepository.save(statusInfo);
        return new StatsResponseDto(statusInfo);
    }


    // 유저 정보 조회 메소드들
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
    }

    private UserProfile getUserProfile(User user) {
        return userProfileRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("유저 프로필이 존재하지 않습니다."));
    }

    private StatusInfo getUserStatus(User user) {
        return statusInfoRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("유저 상태 정보가 존재하지 않습니다."));
    }
}