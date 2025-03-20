package sookmyung.noonsongmaker.Service.main;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sookmyung.noonsongmaker.Dto.intro.StatusInfoResponse;
import sookmyung.noonsongmaker.Dto.main.ChapterResponseDto;
import sookmyung.noonsongmaker.Dto.main.PlayerInfoResponseDto;
import sookmyung.noonsongmaker.Entity.StatusInfo;
import sookmyung.noonsongmaker.Entity.User;
import sookmyung.noonsongmaker.Entity.UserProfile;
import sookmyung.noonsongmaker.Repository.EventRepository;
import sookmyung.noonsongmaker.Repository.StatusInfoRepository;
import sookmyung.noonsongmaker.Repository.UserProfileRepository;
import sookmyung.noonsongmaker.Repository.UserRepository;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MainInfoService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final StatusInfoRepository statusInfoRepository;


    // 플레이어의 현재 학기
    @Transactional(readOnly = true)
    public ChapterResponseDto getCurrentChapter(Long userId) {
        // 유저 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));

        // 현재 학기 정보를 DTO로 변환하여 반환
        return new ChapterResponseDto(user.getCurrentChapter());
    }

    // 플레이어 정보
    @Transactional(readOnly = true)
    public PlayerInfoResponseDto getPlayerInfo(Long userId) {
        // 유저 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));

        // 해당 유저의 프로필 조회
        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("유저 프로필이 존재하지 않습니다."));

        return new PlayerInfoResponseDto(userProfile);
    }

    // 스탯 정보
    @Transactional(readOnly = true)
    public StatusInfoResponse getStatusInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        StatusInfo statusInfo = statusInfoRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("유저의 상태 정보를 찾을 수 없습니다."));

        return new StatusInfoResponse(statusInfo);
    }

}