package sookmyung.noonsongmaker.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
