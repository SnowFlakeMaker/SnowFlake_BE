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
import java.util.Optional;

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

        EventChapters eventChapter = validateEventParticipation("개강총회", user);

        if (!eventChapter.getIsActivated()) {
            throw new IllegalArgumentException("개강총회가 비활성화되어 진행할 수 없습니다.");
        }

        statusInfo.modifyStat("social", 5);
        if (userProfile.getMbti().name().startsWith("I")) {
            statusInfo.modifyStat("stress", 5);
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

        EventChapters eventChapter = validateEventParticipation("MT", user);

        if (!eventChapter.getIsActivated()) {
            throw new IllegalArgumentException("MT가 비활성화되어 진행할 수 없습니다.");
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

        EventChapters eventChapter = validateEventParticipation("축제", user);

        if (!eventChapter.getIsActivated()) {
            throw new IllegalArgumentException("축제가 비활성화되어 진행할 수 없습니다.");
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
            throw new IllegalArgumentException("코인이 부족하여 등록금을 납부할 수 없습니다. 등록금 대리납부를 진행해 주세요.");
        }

        statusInfo.resetScholarship();
        statusInfo.modifyStat("coin", -tuitionFee);
        statusInfoRepository.save(statusInfo);
        return new CoinResponseDto(statusInfo);
    }


    // 국가장학금 신청
    @Transactional
    public Response<Object> applyScholarship(Long userId) {
        User user = getUser(userId);
        StatusInfo statusInfo = getUserStatus(user);

        // 국가장학금 적용
        if (statusInfo.isHasScholarship()) {
            throw new IllegalArgumentException("이미 국가장학금을 신청하였습니다.");
        }

        statusInfo.applyScholarship();
        statusInfoRepository.save(statusInfo);
        return Response.buildResponse(null, "국가장학금 신청이 완료되었습니다.");
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

        statusInfo.modifyStat("coin", parentSupport);
        statusInfo.modifyStat("coin", -tuitionFee);

        statusInfo.resetScholarship();

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

        // 현재 학기 일정 조회 (일정이 없을 경우 기본값 0)
        List<Schedule> userSchedules = scheduleRepository.findByUser(user);

        int studyCount = userSchedules.isEmpty() ? 0 : userSchedules.stream()
                .filter(schedule -> schedule.getCurrentChapter().equals(user.getCurrentChapter())) // 현재 학기 일정만 필터링
                .mapToInt(Schedule::getCount)
                .sum();

        // 공부/수업 기준 충족 여부 확인
        if (studyCount >= 15) {
            statusInfo.setEligibleForMeritScholarship(true);
        } else {
            statusInfo.setEligibleForMeritScholarship(false);
        }

        statusInfoRepository.save(statusInfo);
    }

    // 방학이 끝날 때 봉사활동 시간 체크
    @Transactional
    public void checkVolunteerHoursForScholarship(Long userId) {
        User user = getUser(userId);
        StatusInfo statusInfo = getUserStatus(user);

        // 현재 학기 일정 조회 (일정이 없을 경우 기본값 0)
        List<Schedule> userSchedules = scheduleRepository.findByUser(user);

        long serviceCount = userSchedules.isEmpty() ? 0 : userSchedules.stream()
                .filter(schedule -> schedule.getCurrentChapter().equals(user.getCurrentChapter())) // 현재 학기 일정만 필터링
                .map(Schedule::getPlan)
                .filter(plan -> "봉사".equals(plan.getPlanName()))
                .count();

        // 봉사 시간이 2칸 이상이어야 성적 장학금 지급 가능
        if (statusInfo.isEligibleForMeritScholarship() && serviceCount >= 2) {
            statusInfo.setEligibleForMeritScholarship(true);
        } else {
            statusInfo.setEligibleForMeritScholarship(false); // 봉사 시간 부족 → 지급 불가
        }

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

        EventChapters eventChapter = validateEventParticipation("동아리 지원", user);

        if (!eventChapter.getIsActivated()) {
            throw new IllegalArgumentException("동아리 지원이 비활성화되어 진행할 수 없습니다.");
        }

        float clubSelectionProbability = eventChapter.getEvent().getProbability() != null
                ? eventChapter.getEvent().getProbability()
                : 0.8f;

        boolean isSelected = Math.random() < clubSelectionProbability;
        if (!isSelected) {
            return Response.buildResponse(null, "동아리 지원 불합격");
        }

        Plan clubActivity = planRepository.findByPlanName("동아리 활동")
                .orElseThrow(() -> new IllegalArgumentException("동아리 활동 계획이 존재하지 않습니다."));

        PlanStatus planStatus = planStatusRepository.findByPlanAndUser(clubActivity, user)
                .orElseGet(() -> {
                    PlanStatus newPlanStatus = PlanStatus.builder()
                            .plan(clubActivity)
                            .user(user)
                            .isActivated(true)
                            .remainingSemesters(16)
                            .build();
                    return planStatusRepository.save(newPlanStatus);
                });

        if (!planStatus.isActivated()) {
            planStatus.setActivated(true);
            planStatus.setRemainingSemesters(16);
            planStatusRepository.save(planStatus);
        }

        // 동아리 지원 이벤트 비활성화
        eventChapter.setIsActivated(false);
        eventChaptersRepository.save(eventChapter);

        statusInfoRepository.save(statusInfo);

        return Response.buildResponse(null, "동아리 지원 합격. 활동이 추가되었습니다.");
    }


    // 전공학회 지원
    @Transactional
    public void applyForMajorClub(Long userId) {
        User user = getUser(userId);
        StatusInfo statusInfo = getUserStatus(user);

        EventChapters eventChapter = validateEventParticipation("전공학회 지원", user);

        if (!eventChapter.getIsActivated()) {
            throw new IllegalArgumentException("전공 학회 지원이 비활성화되어 진행할 수 없습니다.");
        }

        // 지원 조건 확인 (지력 50 이상)
        if (statusInfo.getIntelligence() < 50) {
            throw new IllegalArgumentException("전공 학회 지원 요건을 충족하지 못했습니다. (지력 50 이상 필요)");
        }

        Plan majorClubPlan = planRepository.findByPlanName("전공 학회 활동")
                .orElseThrow(() -> new IllegalArgumentException("전공 학회 활동 계획이 존재하지 않습니다."));

        PlanStatus planStatus = planStatusRepository.findByPlanAndUser(majorClubPlan, user)
                .orElseGet(() -> {
                    PlanStatus newPlanStatus = PlanStatus.builder()
                            .plan(majorClubPlan)
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
    }

    // 대외활동 지원
    @Transactional
    public void applyForExternalActivity(Long userId) {
        User user = getUser(userId);
        StatusInfo statusInfo = getUserStatus(user);

        EventChapters eventChapter = validateEventParticipation("대외활동 지원", user);

        if (!eventChapter.getIsActivated()) {
            throw new IllegalArgumentException("대외활동 지원이 비활성화되어 진행할 수 없습니다.");
        }

        if (statusInfo.getSocial() < 40) {
            throw new IllegalArgumentException("대외활동 지원 요건을 충족하지 못했습니다. (사회성 40 이상 필요)");
        }

        Plan externalActivityPlan = planRepository.findByPlanName("대외활동")
                .orElseThrow(() -> new IllegalArgumentException("대외활동 계획이 존재하지 않습니다."));

        PlanStatus planStatus = planStatusRepository.findByPlanAndUser(externalActivityPlan, user)
                .orElseGet(() -> {
                    PlanStatus newPlanStatus = PlanStatus.builder()
                            .plan(externalActivityPlan)
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
    }

    // 리더십 그룹 지원
    @Transactional
    public Response<String> applyForLeadershipGroup(Long userId) {
        User user = getUser(userId);

        EventChapters eventChapter = validateEventParticipation("리더십그룹 지원", user);

        if (!eventChapter.getIsActivated()) {
            throw new IllegalArgumentException("리더십그룹 지원이 비활성화되어 진행할 수 없습니다.");
        }

        float selectionProbability = eventChapter.getEvent().getProbability() != null
                ? eventChapter.getEvent().getProbability()
                : 0.8f;

        boolean isSelected = Math.random() < selectionProbability;
        if (!isSelected) {
            return Response.buildResponse(null, "리더십그룹 지원 불합격");
        }

        Plan leadershipPlan = planRepository.findByPlanName("리더십그룹 활동")
                .orElseThrow(() -> new IllegalArgumentException("리더십그룹 활동 계획이 존재하지 않습니다."));

        PlanStatus planStatus = planStatusRepository.findByPlanAndUser(leadershipPlan, user)
                .orElseGet(() -> {
                    PlanStatus newPlanStatus = PlanStatus.builder()
                            .plan(leadershipPlan)
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

        return Response.buildResponse(null, "리더십그룹 합격. 활동이 추가되었습니다.");
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