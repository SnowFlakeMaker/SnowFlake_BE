package sookmyung.noonsongmaker.Service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sookmyung.noonsongmaker.Dto.Response;
import sookmyung.noonsongmaker.Entity.*;
import sookmyung.noonsongmaker.Repository.*;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class OneTimeEventService {

    private static final int REQUIRED_TOTAL_CREDITS = 130;
    private static final int REQUIRED_CORE_CREDITS = 24;
    private static final int REQUIRED_ELECTIVE_CREDITS = 39;
    private static final int REQUIRED_GENERAL_EDU_CREDITS = 12;
    private static final int REQUIRED_CORE_AREA_CREDITS = 15;
    private static final int REQUIRED_CORE_AREA_COUNT = 2;
    private static final int DOUBLE_MAJOR_EXTRA_CREDITS = 42;
    private static final int SUB_MAJOR_EXTRA_CREDITS = 21;

    private final UserRepository userRepository;
    private final StatusInfoRepository statusInfoRepository;
    private final PlanRepository planRepository;
    private final EventRepository eventRepository;
    private final EventChaptersRepository eventChaptersRepository;
    private final CourseRepository courseRepository;
    private final UserProfileRepository userProfileRepository;
    private final PlanStatusRepository planStatusRepository;

    private static final double STUDENT_COUNCIL_SELECTION_PROBABILITY = 0.8;

    // 학생회 지원 (단발성 이벤트)
    @Transactional
    public Response<Object> applyForStudentCouncil(Long userId) {
        User user = getUser(userId);
        StatusInfo statusInfo = getUserStatus(user);

        // 1학년 1학기(SEM_S_1)에 지원 가능
        if (!user.getCurrentChapter().equals(Chapter.SEM_S_1)) {
            throw new IllegalArgumentException("학생회 지원은 1학년 1학기(SEM_S_1)에서만 가능합니다.");
        }

        Event studentCouncilEvent = eventRepository.findByName("학생회 지원")
                .orElseThrow(() -> new IllegalArgumentException("학생회 지원 이벤트가 존재하지 않습니다."));

        boolean isEventAvailable = eventChaptersRepository.existsByEventAndActivatedChapter(studentCouncilEvent, user.getCurrentChapter());

        if (!isEventAvailable) {
            throw new IllegalArgumentException("현재 학기에는 학생회 지원 이벤트가 없습니다.");
        }

        float selectionProbability = studentCouncilEvent.getProbability() != null ? studentCouncilEvent.getProbability() : 0.8f;

        // 확률 계산
        boolean isSelected = Math.random() < selectionProbability;
        if (!isSelected) {
            return Response.buildResponse(null, "학생회 지원 불합격");
        }

        // Plan 객체 생성 (중복 방지를 위해 먼저 검색)
        Plan studentCouncilPlan = planRepository.findByUserAndPlanName(user, "학생회 활동")
                .orElseGet(() -> {
                    Plan newPlan = Plan.builder()
                            .planName("학생회 활동")
                            .period(Period.ACADEMIC)
                            .user(user)
                            .build();
                    return planRepository.save(newPlan);
                });

        // lanStatus 추가 (1년 유지, 즉 2학기 동안 활성화)
        PlanStatus studentCouncilPlanStatus = PlanStatus.builder()
                .plan(studentCouncilPlan)
                .user(user)
                .isActivated(true)
                .remainingSemesters(4) // 1년(2학기) 동안 유지
                .build();

        planStatusRepository.save(studentCouncilPlanStatus);
        eventChaptersRepository.deleteByEventAndActivatedChapter(studentCouncilEvent, user.getCurrentChapter());

        return Response.buildResponse(null, "학생회 지원 합격. 활동이 추가되었습니다.");
    }

    @Transactional
    public Response<Boolean> checkGraduationEligibility(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));

        Course course = courseRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("해당 유저의 수강 정보를 찾을 수 없습니다."));

        MajorType majorType = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("유저 프로필을 찾을 수 없습니다."))
                .getMajorType();

        // 교양 필수(12학점) 체크 (모든 과목을 들어야 함)
        boolean hasCompletedGeneralEducation =
                Boolean.TRUE.equals(course.getRequiredDigital()) &&
                        Boolean.TRUE.equals(course.getRequiredFuture()) &&
                        Boolean.TRUE.equals(course.getRequiredEng()) &&
                        Boolean.TRUE.equals(course.getRequiredLogic());

        int generalEducationCredits = hasCompletedGeneralEducation ? REQUIRED_GENERAL_EDU_CREDITS : 0; // 12학점

        // 교양 핵심 (최소 2개 영역 선택 & 총 15학점 이상)
        int coreAreasCompleted = 0;
        int totalCoreAreaCredits = 0;

        if (course.getCore1() > 0) {
            coreAreasCompleted++;
            totalCoreAreaCredits += course.getCore1();
        }
        if (course.getCore2() > 0) {
            coreAreasCompleted++;
            totalCoreAreaCredits += course.getCore2();
        }
        if (course.getCore3() > 0) {
            coreAreasCompleted++;
            totalCoreAreaCredits += course.getCore3();
        }
        if (course.getCore4() > 0) {
            coreAreasCompleted++;
            totalCoreAreaCredits += course.getCore4();
        }

        boolean hasCompletedCoreArea = coreAreasCompleted >= REQUIRED_CORE_AREA_COUNT && totalCoreAreaCredits >= REQUIRED_CORE_AREA_CREDITS;

        // 전필(24학점) & 전선(39학점) 확인
        boolean hasCompletedMajorCourses =
                course.getCoreCredits() >= REQUIRED_CORE_CREDITS &&
                        course.getElectiveCredits() >= REQUIRED_ELECTIVE_CREDITS;

        // 복수전공/부전공 추가 학점 설정
        int additionalMajorRequirement = 0;

        if (majorType == MajorType.DOUBLE_MAJOR) {
            additionalMajorRequirement = DOUBLE_MAJOR_EXTRA_CREDITS;  // 42학점 추가
        } else if (majorType == MajorType.SUB_MAJOR) {
            additionalMajorRequirement = SUB_MAJOR_EXTRA_CREDITS;  // 21학점 추가
        }

        // 총 학점 계산 (교필 + 교핵 + 전필 + 전선)
        int totalCredits = course.getCoreCredits() +
                course.getElectiveCredits() +
                generalEducationCredits +
                totalCoreAreaCredits;

        boolean hasCompletedTotalCredits = totalCredits >= (REQUIRED_TOTAL_CREDITS + additionalMajorRequirement);

        // 최종 졸업 가능 여부 판단
        boolean isGraduatable =
                hasCompletedTotalCredits &&
                        hasCompletedGeneralEducation &&
                        hasCompletedCoreArea &&
                        hasCompletedMajorCourses;

        return new Response<>("졸업 요건 충족 여부 확인 완료", isGraduatable);
    }


    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
    }

    private StatusInfo getUserStatus(User user) {
        return statusInfoRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("유저 상태 정보가 존재하지 않습니다."));
    }
}