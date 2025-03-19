package sookmyung.noonsongmaker.Service.plan;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sookmyung.noonsongmaker.Dto.plan.PlanExecuteRequestDto;
import sookmyung.noonsongmaker.Dto.plan.PlanExecuteResponseDto;
import sookmyung.noonsongmaker.Entity.*;
import sookmyung.noonsongmaker.Repository.EffectRepository;
import sookmyung.noonsongmaker.Repository.PlanRepository;
import sookmyung.noonsongmaker.Repository.ScheduleRepository;
import sookmyung.noonsongmaker.Repository.StatusInfoRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PlanService {
    private final StatusInfoRepository statusInfoRepository;
    private final PlanRepository planRepository;
    private final EffectRepository effectRepository;
    private final ScheduleRepository scheduleRepository;

    public List<PlanExecuteResponseDto> executePlan(List<PlanExecuteRequestDto> requestDto, User user) {
        List<PlanExecuteResponseDto> response = new ArrayList<>();
        StatusInfo status = statusInfoRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Status info not found"));
        Map<Plan, Integer> planCounts = new HashMap<>();

        for (PlanExecuteRequestDto task : requestDto) {
            Plan plan = planRepository.findByPlanName(task.getTaskName())
                    .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + task.getTaskName()));
            planCounts.merge(plan, 1, Integer::sum);
            PlanExecuteResponseDto result = applyPlanAndCollectResult(status, task.getTaskName(), plan);
            response.add(result);
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
                case "알바":
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

    public void getSpecialPlan(User user) {
        // 
    }

    private PlanExecuteResponseDto applyPlanAndCollectResult(StatusInfo status, String taskName, Plan plan) {
        List<Effect> effects = effectRepository.findAllByPlan(plan);
        PlanExecuteResponseDto result = new PlanExecuteResponseDto(taskName);

        for (Effect effect : effects) {
            applyEffect(status, effect);
            result.addEffect(effect.getStatusName(), effect.getChangeAmount());
        }

        return result;
    }

    private void applyEffect(StatusInfo status, Effect effect) {
        switch (effect.getStatusName()) {
            case INTELLIGENCE -> status.updateIntelligence(effect.getChangeAmount());
            case FOREIGNLANG -> status.updateForeignLang(effect.getChangeAmount());
            case GRIT -> status.updateGrit(effect.getChangeAmount());
            case STRENGTH -> status.updateStrength(effect.getChangeAmount());
            case SOCIAL -> status.updateSocial(effect.getChangeAmount());
            case STRESS -> status.updateStress(effect.getChangeAmount());
            case LEADERSHIP -> status.updateLeadership(effect.getChangeAmount());
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
