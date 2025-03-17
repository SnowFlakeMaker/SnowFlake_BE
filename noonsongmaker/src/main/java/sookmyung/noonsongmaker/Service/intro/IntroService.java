package sookmyung.noonsongmaker.Service.intro;

import org.springframework.data.util.Pair;
import sookmyung.noonsongmaker.Dto.intro.UserProfileRequest;
import sookmyung.noonsongmaker.Entity.*;
import sookmyung.noonsongmaker.Repository.*;
//import Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class IntroService {

    private final UserProfileRepository userProfileRepository;
    private final StatusInfoRepository statusInfoRepository;
    private final MBTIStatusService mbtiStatusService;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EventRepository eventRepository;
    private final EventChaptersRepository eventChaptersRepository;

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

        // Course 객체 생성 및 초기화
        Course course = Course.builder()
                .user(user)
                .coreCredits(0)
                .electiveCredits(0)
                .requiredDigital(false)
                .requiredFuture(false)
                .requiredEng(false)
                .requiredLogic(false)
                .core1((short) 0)
                .core2((short) 0)
                .core3((short) 0)
                .core4((short) 0)
                .build();

        courseRepository.save(course);

        // MBTI에 따른 초기 스탯 설정
        StatusInfo statusInfo = mbtiStatusService.createInitialStatus(user, request.getMbti());
        statusInfoRepository.save(statusInfo);

        // 모든 이벤트 가져오기
        List<Event> allEvents = eventRepository.findAll();

        // EventChapters 초기화
        List<EventChapters> eventChaptersList = new ArrayList<>();
        for (Event event : allEvents) {
            EventChapters eventChapter = EventChapters.builder()
                    .event(event)
                    .user(user)
                    .isActivated(true)  // 모든 이벤트 초기 활성화
                    .build();
            eventChaptersList.add(eventChapter);
        }

        eventChaptersRepository.saveAll(eventChaptersList);


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
