package sookmyung.noonsongmaker.Controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sookmyung.noonsongmaker.Dto.Response;
import sookmyung.noonsongmaker.Dto.course.CoreResponseDto;
import sookmyung.noonsongmaker.Dto.course.CreditResponseDto;
import sookmyung.noonsongmaker.Dto.course.RequiredResponseDto;
import sookmyung.noonsongmaker.Dto.course.TimetableSubmitRequestDto;
import sookmyung.noonsongmaker.Entity.User;
import sookmyung.noonsongmaker.Service.CourseService;
import sookmyung.noonsongmaker.jwt.CurrentUser;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
@Slf4j
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/main")
    public ResponseEntity<Response<Map<String, CreditResponseDto>>>  getCreditStatus(@CurrentUser User user) {
        CreditResponseDto responseDto = courseService.getCurrentCreditStatus(user);

        Map<String, CreditResponseDto> response = new HashMap<>();
        response.put("current_credit", responseDto);
        return ResponseEntity.ok(Response.buildResponse(response, "직전 학기까지의 수강 현황"));
    }

    @GetMapping("/required")
    public ResponseEntity<Response<Map<String, RequiredResponseDto>>> getRequiredList(@CurrentUser User user) {
        RequiredResponseDto responseDto = courseService.getRequiredList(user);

        Map<String, RequiredResponseDto> response = new HashMap<>();
        response.put("required_list", responseDto);
        return ResponseEntity.ok(Response.buildResponse(response, "수강해야 하는 교필 리스트"));
    }

    @GetMapping("/core")
    public ResponseEntity<Response<Map<String, CoreResponseDto>>> getCoreList(@CurrentUser User user) {
        CoreResponseDto responseDto = courseService.getCoreList(user);

        Map<String, CoreResponseDto> response = new HashMap<>();
        response.put("core_list", responseDto);
        return ResponseEntity.ok(Response.buildResponse(response, "교핵 영역별 수강 횟수"));
    }

    @PatchMapping("/submit")
    public ResponseEntity<Response<String>> submitTimetable(@CurrentUser User user, @RequestBody TimetableSubmitRequestDto timetableSubmitRequestDto) {
        courseService.updateTimetable(user, timetableSubmitRequestDto);
        return ResponseEntity.ok(Response.buildResponse(null, "확정 시간표"));
    }
}
