package sookmyung.noonsongmaker.Dto.plan;

import lombok.Builder;
import lombok.Getter;
import sookmyung.noonsongmaker.Entity.StatusName;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PlanExecuteResponseDto {
    private final String taskName;
    private final Map<StatusName, Integer> effects;

    public PlanExecuteResponseDto(String taskName) {
        this.taskName = taskName;
        this.effects = new HashMap<>();
    }

    public void addEffect(StatusName statusName, int changeAmount) {
        effects.put(statusName, changeAmount);
    }
}
