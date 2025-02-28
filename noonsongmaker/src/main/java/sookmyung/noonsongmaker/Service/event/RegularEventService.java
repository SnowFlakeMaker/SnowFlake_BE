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

    @Transactional
    public StatsResponseDto processOrientation(Long userId) {

        // 유저 정보 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));

        // 유저 프로필 확인
        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("유저 프로필이 존재하지 않습니다."));

        // 이벤트 활성화 여부 확인
        if (!EventAvailabilityChecker.isEventAvailable("orientation", user.getCurrentChapter())) {
            throw new IllegalArgumentException("현재 학기에는 개강총회 이벤트가 없습니다.");
        }

        // 스탯 세팅 여부 확인
        StatusInfo statusInfo = statusInfoRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("유저 상태 정보가 존재하지 않습니다."));

        // 개강총회 효과 적용
        statusInfo.setSocial(statusInfo.getSocial() + 5); // 사회성 증가
        if (userProfile.getMbti().name().startsWith("I")) { // 내향형일 경우 피로 증가
            statusInfo.setStress(statusInfo.getStress() + 5);
        }

        statusInfoRepository.save(statusInfo);

        return new StatsResponseDto(statusInfo);
    }
}