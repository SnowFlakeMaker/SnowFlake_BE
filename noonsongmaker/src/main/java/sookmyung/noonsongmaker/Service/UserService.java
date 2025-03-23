package sookmyung.noonsongmaker.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sookmyung.noonsongmaker.Entity.*;
import sookmyung.noonsongmaker.Repository.*;
import sookmyung.noonsongmaker.Service.event.RegularEventService;
import sookmyung.noonsongmaker.Service.sse.SseService;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final StatusInfoRepository statusInfoRepository;
    private final RegularEventService regularEventService;
    private final PlanStatusRepository planStatusRepository;
    private final EventRepository eventRepository;
    private final EventChaptersRepository eventChaptersRepository;
    private final SseService sseService;


    // 학기 변경
    @Transactional
    public void changeSemester(Long userId) {
        User user = getUser(userId);
        StatusInfo statusInfo = getUserStatus(user);

        Chapter nextChapter = Chapter.getNextChapter(user.getCurrentChapter());
        if (nextChapter == null) {
            throw new IllegalStateException("마지막 학기 이후에는 더 이상 학기를 변경할 수 없습니다.");
        }


        // 현재 학기에 맞게 계획을 활성화/비활성화
        List<PlanStatus> userPlanStatuses = planStatusRepository.findByUser(user);

        for (PlanStatus planStatus : userPlanStatuses) {
            boolean shouldActivate = false;

            if (planStatus.getPlan().getPeriod() == Period.ACADEMIC && nextChapter.name().startsWith("SEM_")) {
                shouldActivate = true;
            }

            if (planStatus.getPlan().getPeriod() == Period.VACATION && nextChapter.name().startsWith("VAC_")) {
                shouldActivate = true;
            }

            if (planStatus.getPlan().getPeriod() == Period.SPECIAL) {
                shouldActivate = true;
            }
            // "자소서 작성" 활동은 VAC_W_3에서만 활성화
            if (planStatus.getPlan().getPeriod() == Period.SPECIAL && planStatus.getPlan().getPlanName().equals("자소서 작성") && !nextChapter.name().equals("VAC_W_3")) {
                shouldActivate = false;
            }

            planStatus.setActivated(shouldActivate);

            // 남은 학기가 있으면 감소
            if (planStatus.getRemainingSemesters() > 0) {
                planStatus.setRemainingSemesters(planStatus.getRemainingSemesters() - 1);
            }

            // 남은 학기가 0이면 자동 비활성화
            if (planStatus.getRemainingSemesters() == 0) {
                planStatus.setActivated(false);
            }
        }

        // 현재 챕터가 "학기(SEM_)" -> 공부 시간 체크하여 성적 장학금 후보 설정
        if (user.getCurrentChapter().name().startsWith("SEM_")) {
            regularEventService.evaluateMeritScholarshipEligibility(userId);
        }

        // 현재 챕터가 "방학(VAC_)" -> 봉사 시간 체크
        if (user.getCurrentChapter().name().startsWith("VAC_")) {
            regularEventService.checkVolunteerHoursForScholarship(userId);
        }

        // 조건 충족 시 학석사 연계과정 활성화
        if (statusInfo.getGrit() >= 60 && statusInfo.getIntelligence() >= 70) {
            activateGraduateIntegratedEvent(user);
        }

        // 학기 변경
        user.setCurrentChapter(nextChapter);
        userRepository.save(user);

        // 국가장학금 초기화
        statusInfo.resetScholarship();
        statusInfoRepository.save(statusInfo);

        // 정기이벤트 전송
        sseService.sendRegularEventsList(user);
    }

    public StatusInfo getUserStatus(User user) {
        return statusInfoRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("유저 상태 정보가 존재하지 않습니다."));
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
    }

    private void activateGraduateIntegratedEvent(User user) {
        Event graduateEvent = eventRepository.findByName("학석사 연계과정 신청")
                .orElseThrow(() -> new IllegalArgumentException("학석사 연계과정 이벤트가 존재하지 않습니다."));

        EventChapters eventChapter = eventChaptersRepository.findByEventAndUser(graduateEvent, user)
                .orElseThrow(() -> new IllegalArgumentException("학석사 연계과정 이벤트가 존재하지 않습니다."));

        eventChapter.setIsActivated(true);
        eventChaptersRepository.save(eventChapter);
    }
}
