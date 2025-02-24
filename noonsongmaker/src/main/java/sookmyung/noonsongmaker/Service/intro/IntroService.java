package sookmyung.noonsongmaker.Service.intro;

import org.springframework.data.util.Pair;
import sookmyung.noonsongmaker.Dto.intro.UserProfileRequest;
import sookmyung.noonsongmaker.Entity.StatusInfo;
import sookmyung.noonsongmaker.Entity.User;
import sookmyung.noonsongmaker.Entity.UserProfile;
import sookmyung.noonsongmaker.Repository.StatusInfoRepository;
import sookmyung.noonsongmaker.Repository.UserProfileRepository;
//import Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sookmyung.noonsongmaker.Repository.UserRepository;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class IntroService {

    private final UserProfileRepository userProfileRepository;
    private final StatusInfoRepository statusInfoRepository;
    private final MBTIStatusService mbtiStatusService;
    private final UserRepository userRepository;

    @Transactional
    public Pair<UserProfile, StatusInfo> createUserProfile(Long userId, UserProfileRequest request) {

        // 유저 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));


        // 기존 프로필 존재 확인 확인
        if (userProfileRepository.existsByUser(user)) {
            throw new IllegalArgumentException("이미 프로필이 존재합니다.");
        }

        // 유저 프로필 생성 및 저장
        UserProfile userProfile = new UserProfile();
        userProfile.setUser(user);
        userProfile.setNickname(request.getNickname());
        userProfile.setMajor(request.getMajor());
        userProfile.setBirthday(request.getBirthday());
        userProfile.setMbti(request.getMbti());
        userProfile.setHobby(request.getHobby());
        userProfile.setDream(request.getDream());
        userProfileRepository.save(userProfile);

        // MBTI에 따른 초기 스탯 설정
        StatusInfo statusInfo = mbtiStatusService.createInitialStatus(user, request.getMbti());
        statusInfoRepository.save(statusInfo);

        return Pair.of(userProfile, statusInfo);
    }

    @Transactional(readOnly = true)
    public Pair<UserProfile, StatusInfo> getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("유저가 존재하지 않습니다."));

        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("유저 프로필이 존재하지 않습니다."));

        StatusInfo statusInfo = statusInfoRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("유저 상태 정보가 존재하지 않습니다."));

        return Pair.of(userProfile, statusInfo);
    }


}
