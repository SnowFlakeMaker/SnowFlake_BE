package sookmyung.noonsongmaker.Service.plan;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sookmyung.noonsongmaker.Dto.plan.PlanExecuteRequestDto;
import sookmyung.noonsongmaker.Dto.plan.PlanExecuteResponseDto;
import sookmyung.noonsongmaker.Entity.*;
import sookmyung.noonsongmaker.Exception.StressOverflowException;
import sookmyung.noonsongmaker.Repository.*;
import sookmyung.noonsongmaker.Service.sse.SseService;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PlanService {
    private final Logger log = LoggerFactory.getLogger(PlanService.class);
    private final StatusInfoRepository statusInfoRepository;
    private final PlanRepository planRepository;
    private final PlanStatusRepository planStatusRepository;
    private final EffectRepository effectRepository;
    private final ScheduleRepository scheduleRepository;
    private final SseService sseService;

    @Transactional
    public List<PlanExecuteResponseDto> executePlan(List<PlanExecuteRequestDto> requestDto, User user) {
        sseService.sendOneTimeEventList(user);

        List<PlanExecuteResponseDto> response = new ArrayList<>();
        StatusInfo status = statusInfoRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Status info not found"));
        Map<Plan, Integer> planCounts = new HashMap<>();

        for (PlanExecuteRequestDto task : requestDto) {
            // log.info("태스크 '{}' 실행 시도 중", task.getTaskName());
            Plan plan = planRepository.findByPlanName(task.getTaskName())
                    .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + task.getTaskName()));
            PlanExecuteResponseDto result = applyPlanAndCollectResult(status, task.getTaskName(), plan);

            if (!result.getTaskName().equals("코인부족"))
                planCounts.merge(plan, 1, Integer::sum);
            response.add(result);
            // log.info("태스크 '{}' 실행 성공", task.getTaskName());
        }

        applyAssessment(planCounts, status);
        statusInfoRepository.save(status);
        savePlanCounts(planCounts, user);

        return response;
    }


    private void applyAssessment(Map<Plan, Integer> planCounts, StatusInfo status) {
        int totalCount = planCounts.values().stream().mapToInt(Integer::intValue).sum();

        for (Map.Entry<Plan, Integer> entry : planCounts.entrySet()) {
            String planName = entry.getKey().getPlanName();
            int count = entry.getValue();
            double ratio = (double) count / totalCount;

            switch (planName) {
                case "수업": case "공부":
                    if (ratio >= 0.6) {
                        status.updateGeneralAssess(status.getGeneralAssess() + 4);
                    } else if (ratio <= 0.3) {
                        status.updateGeneralAssess(status.getGeneralAssess() - 4);
                    }
                    break;
                case "동아리":
                    if (ratio >= 0.2) {
                        status.updateHobbyAssess(status.getHobbyAssess() + 4);
                    } else if (ratio <= 0.1) {
                        status.updateHobbyAssess(status.getHobbyAssess() - 4);
                    }
                    break;
                case "취미":
                    if (ratio >= 0.3) {
                        status.updateHobbyAssess(status.getHobbyAssess() + 4);
                    }
                    break;
                case "아르바이트":
                    if (ratio >= 0.4) {
                        status.updateWorkAssess(status.getWorkAssess() + 4);
                    }
                    break;
                case "봉사":
                    if (count >= 4) {
                        status.updateServiceAssess(status.getServiceAssess() + 4);
                    }
                    break;
                // TODO 외국어 평가 관련 재논의 필요
            }
        }
    }

    public List<String> getSpecialPlan(User user) {
        List<PlanStatus> activatedPlans = planStatusRepository.findByUserAndIsActivatedTrue(user);
        List<String> activatedPlanNames = new ArrayList<>();

        for (PlanStatus task : activatedPlans) {
            activatedPlanNames.add(task.getPlan().getPlanName());
        }

        return activatedPlanNames;
    }

    private PlanExecuteResponseDto applyPlanAndCollectResult(StatusInfo status, String taskName, Plan plan) {
        List<Effect> effects = effectRepository.findAllByPlan(plan);
        PlanExecuteResponseDto result = new PlanExecuteResponseDto(taskName);

        Optional<Effect> coinEffect = effects.stream()
                .filter(e -> e.getStatusName() == StatusName.COIN)
                .findFirst();
        if (coinEffect.isPresent()) {
            Integer currentCoin = status.getCoin();
            int updatedCoin = currentCoin + coinEffect.get().getChangeAmount();

            if (updatedCoin < 0) {
                result.closeIfNoCoin();
                return result;
            }
        }

        for (Effect effect : effects) {
            applyEffect(status, effect);
            result.addEffect(effect.getStatusName(), effect.getChangeAmount());
        }

        return result;
    }

    private void applyEffect(StatusInfo status, Effect effect) {
        if (effect.getStatusName() == StatusName.STRESS) {
            int currentStress = status.getStress();
            int updatedStress = currentStress + effect.getChangeAmount();

            if (updatedStress >= 100) {
                // log.error(실행 중 스트레스 100 도달로 중단됨");
                throw new StressOverflowException();
            }
        }
        switch (effect.getStatusName()) {
            case INTELLIGENCE -> status.updateIntelligence(effect.getChangeAmount());
            case FOREIGNLANG -> status.updateForeignLang(effect.getChangeAmount());
            case GRIT -> status.updateGrit(effect.getChangeAmount());
            case STRENGTH -> status.updateStrength(effect.getChangeAmount());
            case SOCIAL -> status.updateSocial(effect.getChangeAmount());
            case STRESS -> status.updateStress(effect.getChangeAmount());
            case LEADERSHIP -> status.updateLeadership(effect.getChangeAmount());
            case COIN -> status.updateCoin(effect.getChangeAmount());
        }
    }

    private void savePlanCounts(Map<Plan, Integer> planCounts, User user) {
        for (Map.Entry<Plan, Integer> entry : planCounts.entrySet()) {
            Schedule schedule = Schedule.builder()
                    .plan(entry.getKey())
                    .user(user)
                    .count(entry.getValue())
                    .currentChapter(user.getCurrentChapter())
                    .build();
            scheduleRepository.save(schedule);
        }
    }
}
