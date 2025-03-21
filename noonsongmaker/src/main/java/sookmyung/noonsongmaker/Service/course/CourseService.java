package sookmyung.noonsongmaker.Service.course;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sookmyung.noonsongmaker.Dto.course.*;
import sookmyung.noonsongmaker.Entity.Course;
import sookmyung.noonsongmaker.Entity.User;
import sookmyung.noonsongmaker.Entity.UserProfile;
import sookmyung.noonsongmaker.Repository.CourseRepository;
import sookmyung.noonsongmaker.Repository.UserProfileRepository;
import sookmyung.noonsongmaker.Service.sse.SseService;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserProfileRepository userProfileRepository;
    private final SseService sseService;

    public CreditResponseDto getCurrentCreditStatus(User user) {
        Course courseResult = courseRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("수강 정보 소유자 없음"));
        UserProfile userProfileResult = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("사용자 없음"));


        return CreditResponseDto.builder()
                .semester(user.getCurrentChapter())
                .major(userProfileResult.getMajor())
                .majorType(userProfileResult.getMajorType())
                .CurrentCoreCredits(courseResult.getCoreCredits())
                .CurrentElectivesCredits(courseResult.getElectiveCredits())
                .build();
    }

    public RequiredResponseDto getRequiredList(User user) {
        Course courseResult = courseRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("수강 정보 소유자 없음"));

        return RequiredResponseDto.builder()
                .requiredDigital(courseResult.getRequiredDigital())
                .requiredFuture(courseResult.getRequiredFuture())
                .requiredEng(courseResult.getRequiredEng())
                .requiredLogic(courseResult.getRequiredLogic())
                .build();
    }

    public CoreResponseDto getCoreList(User user) {
        Course courseResult = courseRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("수강 정보 소유자 없음"));

        return CoreResponseDto.builder()
                .core1(courseResult.getCore1())
                .core2(courseResult.getCore2())
                .core3(courseResult.getCore3())
                .core4(courseResult.getCore4())
                .build();
    }

    @Transactional
    public TimetableSubmitResponseDto updateTimetable(User user, TimetableSubmitRequestDto requestDto) {
        Course course = courseRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("수강 정보 소유자 없음"));
        TimetableUpdatePolicy policy = new TimetableUpdatePolicy(user.getCurrentChapter());
        Map<String, Boolean> results = new HashMap<>();

        if (requestDto.getCoreCredits() != null) {
            boolean updated = policy.isMajorUpdatePossible();
            if (updated) course.updateCoreCredits(requestDto.getCoreCredits());
            results.put("coreCredits", updated);
        }
        if (requestDto.getElectiveCredits() != null) {
            boolean updated = policy.isMajorUpdatePossible();
            if (updated) course.updateElectiveCredits(requestDto.getElectiveCredits());
            results.put("electiveCredits", updated);
        }

        if (requestDto.getRequiredDigital() != null) {
            boolean updated = policy.isLibUpdatePossible();
            if (updated) course.updateRequiredDigital(requestDto.getRequiredDigital());
            results.put("requiredDigital", updated);
        }
        if (requestDto.getRequiredFuture() != null) {
            boolean updated = policy.isLibUpdatePossible();
            if (updated) course.updateRequiredFuture(requestDto.getRequiredFuture());
            results.put("requiredFuture", updated);
        }
        if (requestDto.getRequiredEng() != null) {
            boolean updated = policy.isLibUpdatePossible();
            if (updated) course.updateRequiredEng(requestDto.getRequiredEng());
            results.put("requiredEng", updated);
        }
        if (requestDto.getRequiredLogic() != null) {
            boolean updated = policy.isLibUpdatePossible();
            if (updated) course.updateRequiredLogic(requestDto.getRequiredLogic());
            results.put("requiredLogic", updated);
        }

        if (requestDto.getCore1() != null) {
            boolean updated = policy.isLibUpdatePossible();
            if (updated) course.updateCore1(requestDto.getCore1());
            results.put("core1", updated);
        }
        if (requestDto.getCore2() != null) {
            boolean updated = policy.isLibUpdatePossible();
            if (updated)course.updateCore2(requestDto.getCore2());
            results.put("core2", updated);
        }
        if (requestDto.getCore3() != null) {
            boolean updated = policy.isLibUpdatePossible();
            if (updated) course.updateCore3(requestDto.getCore3());
            results.put("core3", updated);
        }
        if (requestDto.getCore4() != null) {
            boolean updated = policy.isLibUpdatePossible();
            if (updated) course.updateCore4(requestDto.getCore4());
            results.put("core4", updated);
        }

        sseService.sendOneTimeEventList(user);

        return TimetableSubmitResponseDto.builder()
                .updateResults(results)
                .build();
        // TODO 시간표 저장 : 장학금 코드에서 시간표 데이터 어떻게 받는지 확인 필요, 저장할 필요 없으면 미구현할 것
    }
}
