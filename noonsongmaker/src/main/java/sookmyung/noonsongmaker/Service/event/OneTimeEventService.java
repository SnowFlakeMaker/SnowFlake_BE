package sookmyung.noonsongmaker.Service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sookmyung.noonsongmaker.Dto.Response;
import sookmyung.noonsongmaker.Entity.*;
import sookmyung.noonsongmaker.Repository.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

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

        EventChapters eventChapter = validateEventParticipation("학생회 지원", user);

        if (!eventChapter.getIsActivated()) {
            throw new IllegalArgumentException("학생회 지원이 비활성화되어 진행할 수 없습니다.");
        }

        float selectionProbability = eventChapter.getEvent().getProbability() != null
                ? eventChapter.getEvent().getProbability()
                : 0.8f;

        boolean isSelected = Math.random() < selectionProbability;
        if (!isSelected) {
            return Response.buildResponse(null, "학생회 지원 불합격");
        }

        Plan studentCouncilPlan = planRepository.findByUserAndPlanName(user, "학생회 활동")
                .orElseThrow(() -> new IllegalArgumentException("학생회 활동 계획이 존재하지 않습니다."));

        PlanStatus planStatus = planStatusRepository.findByPlan(studentCouncilPlan)
                .orElseGet(() -> {
                    PlanStatus newPlanStatus = PlanStatus.builder()
                            .plan(studentCouncilPlan)
                            .user(user)
                            .isActivated(true)
                            .remainingSemesters(4) // 1년(2학기) 동안 유지
                            .build();
                    return planStatusRepository.save(newPlanStatus);
                });

        if (!planStatus.isActivated()) {
            planStatus.setActivated(true);
            planStatus.setRemainingSemesters(4);
            planStatusRepository.save(planStatus);
        }

        // 학생회 지원 이벤트 비활성화 (단발성 이벤트)
        eventChapter.setIsActivated(false);
        eventChaptersRepository.save(eventChapter);

        return Response.buildResponse(null, "학생회 지원 합격. 활동이 추가되었습니다.");
    }

    // 졸업인증제
    @Transactional
    public Response<Map<String, Object>> checkGraduationEligibility(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));

        Course course = courseRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("해당 유저의 수강 정보를 찾을 수 없습니다."));

        MajorType majorType = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("유저 프로필을 찾을 수 없습니다."))
                .getMajorType();

        Map<String, Object> response = new HashMap<>();
        boolean isGraduatable = true;

        // 교양 필수(12학점) 체크 (모든 과목을 들어야 함)
        boolean hasCompletedGeneralEducation =
                Boolean.TRUE.equals(course.getRequiredDigital()) &&
                        Boolean.TRUE.equals(course.getRequiredFuture()) &&
                        Boolean.TRUE.equals(course.getRequiredEng()) &&
                        Boolean.TRUE.equals(course.getRequiredLogic());

        int generalEducationCredits = hasCompletedGeneralEducation ? REQUIRED_GENERAL_EDU_CREDITS : 0; // 12학점

        if (!hasCompletedGeneralEducation) {
            response.put("교양 필수 부족", "디지털, 미래, 영어, 논리 과목을 모두 수강해야 합니다.");
            isGraduatable = false;
        }

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

        if (!hasCompletedCoreArea) {
            response.put("교양 핵심 부족", "교양 핵심 과목 중 2개 이상 선택하여 총 15학점 이상 들어야 합니다.");
            isGraduatable = false;
        }

        // 기본 전공 필수 & 선택 학점 검증 (단일 전공 기준)
        boolean hasCompletedBaseMajorCourses =
                course.getCoreCredits() >= REQUIRED_CORE_CREDITS &&  // 전필(24)
                        course.getElectiveCredits() >= REQUIRED_ELECTIVE_CREDITS; // 전선(39)


        if (!hasCompletedBaseMajorCourses) {
            response.put("전공 학점 부족", "전필 24학점, 전선 39학점을 충족해야 합니다.");
            isGraduatable = false;
        }

        // 복수전공/부전공 추가 학점 설정 (전필+전선 합 기준)
        int requiredMajorCredits = REQUIRED_CORE_CREDITS + REQUIRED_ELECTIVE_CREDITS; // 24(전필) + 39(전선) = 63학점

        if (majorType == MajorType.DOUBLE_MAJOR) {
            requiredMajorCredits += DOUBLE_MAJOR_EXTRA_CREDITS;  // 복전: +42 (총 105학점)
        } else if (majorType == MajorType.SUB_MAJOR) {
            requiredMajorCredits += SUB_MAJOR_EXTRA_CREDITS;  // 부전: +21 (총 84학점)
        }

        // 전필 + 전선 학점 검증 (복수전공/부전공 추가 기준 포함)
        boolean hasCompletedTotalMajorCourses =
                (course.getCoreCredits() + course.getElectiveCredits()) >= requiredMajorCredits;


        if (!hasCompletedTotalMajorCourses) {
            response.put("추가 전공 학점 부족", String.format("현재 %d학점, 필요 학점: %d",
                    (course.getCoreCredits() + course.getElectiveCredits()), requiredMajorCredits));
            isGraduatable = false;
        }

        // 총 학점 계산 (교필 + 교핵 + 전필 + 전선 포함)
        int totalCredits = course.getCoreCredits() +
                course.getElectiveCredits() +
                generalEducationCredits +
                totalCoreAreaCredits;

        boolean hasCompletedTotalCredits = totalCredits >= REQUIRED_TOTAL_CREDITS;

        if (!hasCompletedTotalCredits) {
            response.put("총 학점 부족", String.format("현재 %d학점, 졸업에 필요한 학점: %d", totalCredits, REQUIRED_TOTAL_CREDITS));
            isGraduatable = false;
        }

        response.put("isGraduatable", isGraduatable);
        return new Response<>("졸업 요건 충족 여부 확인 완료", response);
    }


    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
    }

    private StatusInfo getUserStatus(User user) {
        return statusInfoRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("유저 상태 정보가 존재하지 않습니다."));
    }

    public EventChapters validateEventParticipation(String eventName, User user) {
        // 이벤트 존재 여부 확인
        Event event = eventRepository.findByName(eventName)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트입니다."));

        // 현재 챕터에서 해당 이벤트가 활성화되었는지 확인
        if (!event.getActivatedChapters().contains(user.getCurrentChapter())) {
            throw new IllegalArgumentException("현재 학기에는 " + eventName + " 이벤트를 신청할 수 없습니다.");
        }

        // 유저의 이벤트 활성화 여부 확인
        return eventChaptersRepository.findByEventAndUser(event, user)
                .orElseThrow(() -> new IllegalArgumentException("이벤트 진행 기록을 찾을 수 없습니다."));
    }
}