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
    public void updateTimetable(User user, TimetableSubmitRequestDto requestDto) {
        Course course = courseRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("수강 정보 소유자 없음"));
        TimetableUpdatePolicy policy = new TimetableUpdatePolicy(user.getCurrentChapter());

        if (requestDto.getCoreCredits() != null && policy.isMajorUpdatePossible()) {
            course.updateCoreCredits(requestDto.getCoreCredits());
            // TODO responseDto에 결과 저장 : 프론트와 형식 협의 필요
        }
        if (requestDto.getElectiveCredits() != null && policy.isMajorUpdatePossible()) {
            course.updateElectiveCredits(requestDto.getElectiveCredits());
        }

        if (requestDto.getRequiredDigital() != null && policy.isLibUpdatePossible()) {
            course.updateRequiredDigital(requestDto.getRequiredDigital());
        }
        if (requestDto.getRequiredFuture() != null && policy.isLibUpdatePossible()) {
            course.updateRequiredFuture(requestDto.getRequiredFuture());
        }
        if (requestDto.getRequiredEng() != null && policy.isLibUpdatePossible()) {
            course.updateRequiredEng(requestDto.getRequiredEng());
        }
        if (requestDto.getRequiredLogic() != null && policy.isLibUpdatePossible()) {
            course.updateRequiredLogic(requestDto.getRequiredLogic());
        }

        if (requestDto.getCore1() != null && policy.isLibUpdatePossible()) {
            course.updateCore1(requestDto.getCore1());
        }
        if (requestDto.getCore2() != null && policy.isLibUpdatePossible()) {
            course.updateCore2(requestDto.getCore2());
        }
        if (requestDto.getCore3() != null && policy.isLibUpdatePossible()) {
            course.updateCore3(requestDto.getCore3());
        }
        if (requestDto.getCore4() != null && policy.isLibUpdatePossible()) {
            course.updateCore4(requestDto.getCore4());
        }

        // TODO 시간표 저장 : 장학금 코드에서 시간표 데이터 어떻게 받는지 확인 필요, 저장할 필요 없으면 미구현할 것
        // TODO 결과 리턴
    }
}
