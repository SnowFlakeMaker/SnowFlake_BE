package sookmyung.noonsongmaker.Service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sookmyung.noonsongmaker.Dto.Response;
import sookmyung.noonsongmaker.Dto.event.CoinAndStressResponseDto;
import sookmyung.noonsongmaker.Dto.event.CoinResponseDto;
import sookmyung.noonsongmaker.Dto.event.StatsResponseDto;
import sookmyung.noonsongmaker.Entity.*;
import sookmyung.noonsongmaker.Repository.*;
import sookmyung.noonsongmaker.Service.plan.PlanService;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RegularEventService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final StatusInfoRepository statusInfoRepository;
    private final ScheduleRepository scheduleRepository;
    private final PlanRepository planRepository;
    private final EventService eventService;
    private final EventChaptersRepository eventChaptersRepository;
    private final EventRepository eventRepository;
    private final PlanStatusRepository planStatusRepository;

    private static final double CLUB_SELECTION_PROBABILITY = 0.8;

    // 개강총회
    @Transactional
    public StatsResponseDto processOrientation(Long userId) {

        User user = getUser(userId);
        UserProfile userProfile = getUserProfile(user);
        StatusInfo statusInfo = getUserStatus(user);

        // 활성화 여부 확인
        boolean isEventAvailable = eventChaptersRepository.existsByEventAndActivatedChapter(
                eventRepository.findByName("개강총회").orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트입니다.")),
                user.getCurrentChapter()
        );

        if (!isEventAvailable) {
            throw new IllegalArgumentException("현재 학기에는 개강총회 이벤트가 없습니다.");
        }

        statusInfo.modifyStat("social", 5);  // 사회성 증가
        if (userProfile.getMbti().name().startsWith("I")) {
            statusInfo.modifyStat("stress", 5);  // 내향형이면 스트레스 증가
        }

        statusInfoRepository.save(statusInfo);
        return new StatsResponseDto(statusInfo);
    }


    // MT
    @Transactional
    public StatsResponseDto attendMT(Long userId) {
        User user = getUser(userId);
        UserProfile userProfile = getUserProfile(user);
        StatusInfo statusInfo = getUserStatus(user);

        // 활성화 여부 확인
        Event mtEvent = eventRepository.findByName("MT")
                .orElseThrow(() -> new IllegalArgumentException("MT 이벤트가 존재하지 않습니다."));

        boolean isEventAvailable = eventChaptersRepository.existsByEventAndActivatedChapter(mtEvent, user.getCurrentChapter());

        if (!isEventAvailable) {
            throw new IllegalArgumentException("현재 학기에는 MT 이벤트가 없습니다.");
        }

        statusInfo.modifyStat("social", 5);
        if (userProfile.getMbti().name().startsWith("I")) {
            statusInfo.modifyStat("stress", 5);
        }

        statusInfoRepository.save(statusInfo);
        return new StatsResponseDto(statusInfo);
    }

    // 축제
    @Transactional
    public StatsResponseDto attendFestival(Long userId) {
        User user = getUser(userId);
        StatusInfo statusInfo = getUserStatus(user);

        Event festivalEvent = eventRepository.findByName("축제")
                .orElseThrow(() -> new IllegalArgumentException("축제 이벤트가 존재하지 않습니다."));

        boolean isEventAvailable = eventChaptersRepository.existsByEventAndActivatedChapter(festivalEvent, user.getCurrentChapter());

        if (!isEventAvailable) {
            throw new IllegalArgumentException("현재 학기에는 축제 이벤트가 없습니다.");
        }

        if (statusInfo.getCoin() < 5) {
            throw new IllegalArgumentException("코인이 부족하여 축제에 참석할 수 없습니다. 현재 보유 코인: " + statusInfo.getCoin());
        }

        statusInfo.modifyStat("social", 5);
        statusInfo.modifyStat("stress", -5);
        statusInfo.modifyStat("coin", -5);

        statusInfoRepository.save(statusInfo);
        return new StatsResponseDto(statusInfo);
    }

    // 등록금 납부
    @Transactional
    public CoinResponseDto payTuition(Long userId) {
        User user = getUser(userId);
        StatusInfo statusInfo = getUserStatus(user);

        int tuitionFee = statusInfo.isHasScholarship() ? 200 : 400; // 국장 신청 여부에 따라 등록금 결정

        if (statusInfo.getCoin() < tuitionFee) {
            throw new IllegalArgumentException("코인이 부족하여 등록금을 납부할 수 없습니다.");
        }

        statusInfo.modifyStat("coin", -tuitionFee);
        statusInfoRepository.save(statusInfo);
        return new CoinResponseDto(statusInfo);
    }


    // 국가장학금 신청
    @Transactional
    public CoinResponseDto applyScholarship(Long userId) {
        User user = getUser(userId);
        StatusInfo statusInfo = getUserStatus(user);

        // 국가장학금 적용
        if (statusInfo.isHasScholarship()) {
            throw new IllegalArgumentException("이미 국가장학금을 신청하였습니다.");
        }

        statusInfo.applyScholarship();
        statusInfoRepository.save(statusInfo);
        return new CoinResponseDto(statusInfo);
    }

    // 등록금 대리납부 (국가장학금 반영)
    @Transactional
    public CoinAndStressResponseDto requestTuitionHelp(Long userId, int parentSupport) {
        User user = getUser(userId);
        StatusInfo statusInfo = getUserStatus(user);

        // 국가장학금 적용 여부에 따라 등록금 결정
        int tuitionFee = statusInfo.isHasScholarship() ? 200 : 400;
        int remainingAmount = tuitionFee - statusInfo.getCoin(); // 최소 필요 금액

        // 빌릴 코인이 최소 필요 금액 이상, 등록금 이하인지 확인
        if (parentSupport < remainingAmount || parentSupport > tuitionFee) {
            throw new IllegalArgumentException("대리납부 가능한 범위는 " + remainingAmount + " ~ " + tuitionFee + " 코인 사이여야 합니다.");
        }

        // 현재 가진 코인 모두 사용 후 부모님이 지원한 코인 추가
        statusInfo.modifyStat("coin", -statusInfo.getCoin());
        statusInfo.modifyStat("coin", parentSupport);

        // 빌린 금액의 10%만큼 스트레스 증가
        int stressIncrease = (int) Math.ceil(parentSupport * 0.1);
        statusInfo.modifyStat("stress", stressIncrease);

        statusInfoRepository.save(statusInfo);
        return new CoinAndStressResponseDto(statusInfo);
    }

    // 학기가 끝날 때 장학금 자격 여부 저장 (학기 중 수업/공부 체크)
    @Transactional
    public void evaluateMeritScholarshipEligibility(Long userId) {
        User user = getUser(userId);
        StatusInfo statusInfo = getUserStatus(user);

        // 현재 학기 일정 조회
        List<Schedule> semesterSchedules = scheduleRepository.findByUserIdAndIsVacation(userId, false);
        int studyCount = semesterSchedules.stream()
                .mapToInt(Schedule::getCount)
                .sum();

        // 공부/수업 기준 충족 여부 확인
        if (studyCount >= 15) {
            statusInfo.setEligibleForMeritScholarship(true); // 다음 방학에서 봉사활동 체크 필요
        }
        statusInfoRepository.save(statusInfo);
    }

    // 방학이 끝날 때 봉사활동 시간 체크
    @Transactional
    public void checkVolunteerHoursForScholarship(Long userId) {
        User user = getUser(userId);
        StatusInfo statusInfo = getUserStatus(user);

        // 방학 일정에서 "봉사" 활동 확인
        List<Schedule> vacationSchedules = scheduleRepository.findByUserIdAndIsVacation(userId, true);

        long serviceCount = vacationSchedules.stream()
                .map(Schedule::getPlan)
                .filter(plan -> plan.getPlanName() != null && plan.getPlanName().equals("봉사"))
                .count();

        // 2칸 이상이면 성적 장학금 지급 가능
        if (statusInfo.isEligibleForMeritScholarship() && serviceCount >= 2) {
            statusInfo.setEligibleForMeritScholarship(true);  // 유지
        } else {
            statusInfo.setEligibleForMeritScholarship(false); // 봉사 시간 부족 → 지급 불가
        }

        // 변경 사항 저장
        statusInfoRepository.save(statusInfo);
    }

    // 방학이 끝날 때 성적장학금 지급 (봉사활동 체크 후 지급)
    @Transactional
    public CoinResponseDto grantMeritScholarship(Long userId) {
        User user = getUser(userId);
        StatusInfo statusInfo = getUserStatus(user);

        // 장학금 자격이 없는 경우 지급하지 않음
        if (!statusInfo.isEligibleForMeritScholarship()) {
            throw new IllegalArgumentException("성적 장학금 지급 요건을 충족하지 못했습니다.");
        }

        // 성적 장학금 지급 금액 결정
        int scholarshipAmount = statusInfo.isEligibleForMeritScholarship() ? 400 : 200;

        // 성적 장학금 지급
        statusInfo.applyMeritScholarship(scholarshipAmount);
        statusInfo.modifyStat("generalAssess", 10);
        statusInfo.modifyStat("intelligence", 10);

        // 지급 후 상태 초기화
        statusInfo.setEligibleForMeritScholarship(false);

        statusInfoRepository.save(statusInfo);
        return new CoinResponseDto(statusInfo);
    }


    // 동아리 지원 (확률 기반)
    @Transactional
    public Response<Object> applyForClub(Long userId) {
        User user = getUser(userId);
        StatusInfo statusInfo = getUserStatus(user);

        // 이벤트 정보 조회
        Event clubEvent = eventRepository.findByName("동아리 지원")
                .orElseThrow(() -> new IllegalArgumentException("동아리 지원 이벤트가 존재하지 않습니다."));

        // 현재 학기에서의 활성화 여부 확인
        boolean isEventAvailable = eventChaptersRepository.existsByEventAndActivatedChapter(clubEvent, user.getCurrentChapter());

        if (!isEventAvailable) {
            throw new IllegalArgumentException("이미 동아리에 가입되어 있거나, 동아리 지원이 불가능합니다.");
        }

        float clubSelectionProbability = clubEvent.getProbability() != null ? clubEvent.getProbability() : 0.8f;

        // 확률 계산
        boolean isSelected = Math.random() < clubSelectionProbability;
        if (!isSelected) {
            return Response.buildResponse(null, "동아리 지원 불합격");
        }

        // 동아리 가입 처리
        statusInfo.joinClub();

        // 동아리 활동을 계획표에 추가
        Plan clubActivity = Plan.builder()
                .planName("동아리 활동")
                .period(Period.ACADEMIC)
                .user(user)
                .build();
        planRepository.save(clubActivity);

        // 동아리 지원 이벤트 삭제 (가입 후에는 다시 지원 불가능하도록)
        eventChaptersRepository.deleteByEventAndActivatedChapter(clubEvent, user.getCurrentChapter());

        statusInfoRepository.save(statusInfo);

        return Response.buildResponse(null, "동아리 지원 합격. 활동이 추가되었습니다.");
    }


    // 전공학회 지원
    @Transactional
    public void applyForMajorClub(Long userId) {
        User user = getUser(userId);
        StatusInfo statusInfo = getUserStatus(user);

        Event majorClubEvent = eventRepository.findByName("전공학회 지원")
                .orElseThrow(() -> new IllegalArgumentException("전공 학회 지원 이벤트가 존재하지 않습니다."));

        boolean isEventAvailable = eventChaptersRepository.existsByEventAndActivatedChapter(majorClubEvent, user.getCurrentChapter());
        if (!isEventAvailable) {
            throw new IllegalArgumentException("현재 학기에는 전공 학회 지원이 불가능합니다.");
        }

        // 지원 조건 확인 (지력 50 이상)
        if (statusInfo.getIntelligence() < 50) {
            throw new IllegalArgumentException("전공 학회 지원 요건을 충족하지 못했습니다. (지력 50 이상 필요)");
        }

        // Plan 객체 생성 (중복 방지를 위해 먼저 검색)
        Plan majorClubPlan = planRepository.findByUserAndPlanName(user, "전공 학회 활동")
                .orElseGet(() -> {
                    Plan newPlan = Plan.builder()
                            .planName("전공 학회 활동")
                            .period(Period.ACADEMIC)
                            .user(user)
                            .build();
                    return planRepository.save(newPlan);
                });

        // PlanStatus 추가 (1년 유지, 즉 2학기 동안 활성화)
        PlanStatus majorClubPlanStatus = PlanStatus.builder()
                .plan(majorClubPlan)
                .user(user)
                .isActivated(true)
                .remainingSemesters(4) // 1년(2학기) 동안 유지
                .build();

        planStatusRepository.save(majorClubPlanStatus);
    }

    // 대외활동 지원
    @Transactional
    public void applyForExternalActivity(Long userId) {
        User user = getUser(userId);
        StatusInfo statusInfo = getUserStatus(user);

        Event externalActivityEvent = eventRepository.findByName("대외활동 지원")
                .orElseThrow(() -> new IllegalArgumentException("대외활동 지원 이벤트가 존재하지 않습니다."));

        boolean isEventAvailable = eventChaptersRepository.existsByEventAndActivatedChapter(externalActivityEvent, user.getCurrentChapter());
        if (!isEventAvailable) {
            throw new IllegalArgumentException("현재 학기에는 대외활동 지원이 불가능합니다.");
        }

        if (statusInfo.getSocial() < 40) {
            throw new IllegalArgumentException("대외활동 지원 요건을 충족하지 못했습니다. (사회성 40 이상 필요)");
        }

        // 계획표 추가
        Plan externalActivityPlan = Plan.builder()
                .planName("대외활동")
                .period(Period.ACADEMIC) // 학기 중 활동
                .user(user)
                .build();
        planRepository.save(externalActivityPlan);

        // 계획 상태 추가 - 1년 유지
        PlanStatus planStatus = PlanStatus.builder()
                .plan(externalActivityPlan)
                .user(user)
                .isActivated(true)
                .remainingSemesters(4)
                .build();
        planStatusRepository.save(planStatus);
    }

    @Transactional
    public Response<String> applyForLeadershipGroup(Long userId) {
        User user = getUser(userId);

        // 이벤트 존재 여부 확인
        Event leadershipEvent = eventRepository.findByName("리더십그룹 지원")
                .orElseThrow(() -> new IllegalArgumentException("리더십그룹 지원 이벤트가 존재하지 않습니다."));

        boolean isEventAvailable = eventChaptersRepository.existsByEventAndActivatedChapter(leadershipEvent, user.getCurrentChapter());

        if (!isEventAvailable) {
            throw new IllegalArgumentException("이미 리더십그룹에 가입되어 있거나, 지원이 불가능합니다.");
        }

        // 합격 확률 설정 (디폴트 80%)
        float selectionProbability = leadershipEvent.getProbability() != null ? leadershipEvent.getProbability() : 0.8f;

        boolean isSelected = Math.random() < selectionProbability;
        if (!isSelected) {
            return Response.buildResponse(null, "리더십그룹 지원 불합격");
        }

        // Plan 객체 생성 (중복 방지를 위해 먼저 검색)
        Plan leadershipPlan = planRepository.findByUserAndPlanName(user, "리더십그룹 활동")
                .orElseGet(() -> {
                    Plan newPlan = Plan.builder()
                            .planName("리더십그룹 활동")
                            .period(Period.ACADEMIC)
                            .user(user)
                            .build();
                    return planRepository.save(newPlan);
                });

        // PlanStatus 추가 (1년 유지, 즉 2학기 동안 활성화)
        PlanStatus leadershipPlanStatus = PlanStatus.builder()
                .plan(leadershipPlan)
                .user(user)
                .isActivated(true)
                .remainingSemesters(4)
                .build();

        planStatusRepository.save(leadershipPlanStatus);

        return Response.buildResponse(null, "리더십그룹 합격. 활동이 추가되었습니다.");
    }

    public List<String> getAvailableEvents(Long userId) {
        User user = getUser(userId);
        return eventService.getAvailableEvents(userId, user.getCurrentChapter());
    }


    // 유저 정보 조회 메소드들
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
    }

    private UserProfile getUserProfile(User user) {
        return userProfileRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("유저 프로필이 존재하지 않습니다."));
    }

    private StatusInfo getUserStatus(User user) {
        return statusInfoRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("유저 상태 정보가 존재하지 않습니다."));
    }
}