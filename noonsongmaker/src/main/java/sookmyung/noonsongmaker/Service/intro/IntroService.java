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
    private final PlanRepository planRepository;
    private final PlanStatusRepository planStatusRepository;

    @Transactional
    public Pair<UserProfile, StatusInfo> createUserProfile(UserProfileRequest request) {

        // 이메일로 유저 찾기
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (user.getCurrentChapter() == null) {
            user.setCurrentChapter(Chapter.NEWBIE);
            userRepository.save(user); // 변경사항 저장
        }

        // 기존 프로필 존재 확인
        if (userProfileRepository.existsByUser(user)) {
            throw new IllegalArgumentException("이미 프로필이 존재합니다.");
        }

        // 유저 프로필 생성
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
                .dmCredits(0)
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
            boolean isInitiallyActivated = !event.getName().equals("교환학생 진행")
                    && !event.getName().equals("성적장학금")
                    && !event.getName().equals("학석사 연계과정 신청")
                    && !event.getName().equals("대학원생 시퀀스 진행")
                    && !event.getName().equals("인턴 합격");


            EventChapters eventChapter = EventChapters.builder()
                    .event(event)
                    .user(user)
                    .isActivated(isInitiallyActivated)
                    .build();
            eventChaptersList.add(eventChapter);
        }

        eventChaptersRepository.saveAll(eventChaptersList);

        // 자소서 작성에 대한 planstatus 초기화
        Plan resumePlan = planRepository.findByPlanName("자소서 작성")
                .orElseThrow(() -> new IllegalArgumentException("자소서 작성 Plan이 존재하지 않습니다."));

        PlanStatus resumePlanStatus = PlanStatus.builder()
                .user(user)
                .plan(resumePlan)
                .isActivated(false) // 처음에는 비활성화
                .remainingSemesters(16)
                .build();
        planStatusRepository.save(resumePlanStatus);


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
