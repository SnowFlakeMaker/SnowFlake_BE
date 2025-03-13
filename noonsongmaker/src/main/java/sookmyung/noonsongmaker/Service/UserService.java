package sookmyung.noonsongmaker.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sookmyung.noonsongmaker.Entity.*;
import sookmyung.noonsongmaker.Repository.EventRepository;
import sookmyung.noonsongmaker.Repository.PlanRepository;
import sookmyung.noonsongmaker.Repository.StatusInfoRepository;
import sookmyung.noonsongmaker.Repository.UserRepository;
import sookmyung.noonsongmaker.Service.event.RegularEventService;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final StatusInfoRepository statusInfoRepository;
    private final RegularEventService regularEventService;
    private final PlanRepository planRepository;


    // 학기 변경
    @Transactional
    public void changeSemester(Long userId, Chapter newChapter) {
        User user = getUser(userId);
        StatusInfo statusInfo = getUserStatus(user);

        // 현재 학기에 맞게 계획을 활성화/비활성화
        List<Plan> userPlans = planRepository.findByUser(user);

        for (Plan plan : userPlans) {
            boolean shouldActivate = false;

            if (plan.getPeriod() == Period.ACADEMIC && newChapter.name().startsWith("SEM_")) {
                shouldActivate = true; // 학기 중 계획 활성화
            }
            if (plan.getPeriod() == Period.VACATION && newChapter.name().startsWith("VAC_")) {
                shouldActivate = true; // 방학 중 계획 활성화
            }

            plan.setActivated(shouldActivate);

            if (plan.getRemainingSemesters() > 0) {
                plan.setRemainingSemesters(plan.getRemainingSemesters() - 1);
            }

            // 남은 학기가 0이면 비활성화
            if (plan.getRemainingSemesters() == 0) {
                plan.setActivated(false);
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

        // 학기 변경
        user.setCurrentChapter(newChapter);
        userRepository.save(user);

        // 국가장학금 초기화
        statusInfo.resetScholarship();
        statusInfoRepository.save(statusInfo);
    }

    public StatusInfo getUserStatus(User user) {
        return statusInfoRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("유저 상태 정보가 존재하지 않습니다."));
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
    }
}