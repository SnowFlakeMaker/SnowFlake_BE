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
        Map<String, Object> results = new HashMap<>();

        if (requestDto.getEssential() != null) {
            int count = requestDto.getEssential();
            boolean[] resultArray = new boolean[count];

            for (int i = 0; i < count; i++) {
                boolean updated = policy.isMajorUpdatePossible();
                if (updated) course.updateCoreCredits(1);
                resultArray[i] = updated;
            }

            results.put("coreCredits", resultArray);
        }

        if (requestDto.getElective() != null) {
            int count = requestDto.getElective();
            boolean[] resultArray = new boolean[count];

            for (int i = 0; i < count; i++) {
                boolean updated = policy.isMajorUpdatePossible();
                if (updated) course.updateElectiveCredits(1);
                resultArray[i] = updated;
            }

            results.put("electiveCredits", resultArray);
        }

        if (requestDto.getDmCredits() != null) {
            int count = requestDto.getDmCredits();
            boolean[] resultArray = new boolean[count];

            for (int i = 0; i < count; i++) {
                boolean updated = policy.isDmUpdatePossible();
                if (updated) course.updateDmCredits(1);
                resultArray[i] = updated;
            }

            results.put("dmCredits", resultArray);
        }

        if (requestDto.getRequiredDigital() != null) {
            boolean updated = policy.isLibUpdatePossible();
            if (updated) course.updateRequiredDigital(requestDto.getRequiredDigital());
            results.put("디사의", updated);
        }
        if (requestDto.getRequiredFuture() != null) {
            boolean updated = policy.isLibUpdatePossible();
            if (updated) course.updateRequiredFuture(requestDto.getRequiredFuture());
            results.put("미래설계", updated);
        }
        if (requestDto.getRequiredEng() != null) {
            boolean updated = policy.isLibUpdatePossible();
            if (updated) course.updateRequiredEng(requestDto.getRequiredEng());
            results.put("영교필", updated);
        }
        if (requestDto.getRequiredLogic() != null) {
            boolean updated = policy.isLibUpdatePossible();
            if (updated) course.updateRequiredLogic(requestDto.getRequiredLogic());
            results.put("논사소", updated);
        }

        if (requestDto.getCore1() != null) {
            int count = requestDto.getCore1();
            boolean[] resultArray = new boolean[count];

            for (int i = 0; i < count; i++) {
                boolean updated = policy.isLibUpdatePossible();
                if (updated) course.updateCore1((short) 1);
                resultArray[i] = updated;
            }

            results.put("core1", resultArray);
        }
        if (requestDto.getCore2() != null) {
            int count = requestDto.getCore2();
            boolean[] resultArray = new boolean[count];

            for (int i = 0; i < count; i++) {
                boolean updated = policy.isLibUpdatePossible();
                if (updated) course.updateCore2((short) 1);
                resultArray[i] = updated;
            }

            results.put("core2", resultArray);
        }
        if (requestDto.getCore3() != null) {
            int count = requestDto.getCore3();
            boolean[] resultArray = new boolean[count];

            for (int i = 0; i < count; i++) {
                boolean updated = policy.isLibUpdatePossible();
                if (updated) course.updateCore3((short) 1);
                resultArray[i] = updated;
            }

            results.put("core3", resultArray);
        }
        if (requestDto.getCore4() != null) {
            int count = requestDto.getCore4();
            boolean[] resultArray = new boolean[count];

            for (int i = 0; i < count; i++) {
                boolean updated = policy.isLibUpdatePossible();
                if (updated) course.updateCore4((short) 1);
                resultArray[i] = updated;
            }

            results.put("core4", resultArray);
        }


        return TimetableSubmitResponseDto.builder()
                .updateResults(results)
                .build();
    }
}
