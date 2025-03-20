package sookmyung.noonsongmaker.Controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sookmyung.noonsongmaker.Dto.Response;
import sookmyung.noonsongmaker.Dto.plan.PlanExecuteRequestDto;
import sookmyung.noonsongmaker.Dto.plan.PlanExecuteResponseDto;
import sookmyung.noonsongmaker.Entity.Plan;
import sookmyung.noonsongmaker.Entity.User;
import sookmyung.noonsongmaker.Service.plan.PlanService;
import sookmyung.noonsongmaker.jwt.CurrentUser;

import java.util.List;

@RestController
@RequestMapping("/plan")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @PostMapping("/execute")
    public ResponseEntity<Response<List<PlanExecuteResponseDto>>> execute(@RequestBody List<PlanExecuteRequestDto> requestDto, @CurrentUser User user) {
        List<PlanExecuteResponseDto> response = planService.executePlan(requestDto, user);

        return ResponseEntity.ok(Response.buildResponse(response, "성공"));
    }

    @GetMapping("/specialist")
    public ResponseEntity<Response<List<String>>> getSpecialist(@CurrentUser User user) {
        List<String> response = planService.getSpecialPlan(user);
        
        return ResponseEntity.ok(Response.buildResponse(response, "이번 학기 추가 가능한 특별 계획"));
    }
}
