package sookmyung.noonsongmaker.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sookmyung.noonsongmaker.Dto.Response;
import sookmyung.noonsongmaker.Dto.event.MajorInfoResponseDto;
import sookmyung.noonsongmaker.Dto.event.StatsResponseDto;
import sookmyung.noonsongmaker.Entity.User;
import sookmyung.noonsongmaker.Service.UserProfileService;
import sookmyung.noonsongmaker.Service.event.EventService;
import sookmyung.noonsongmaker.Service.event.OneTimeEventService;
import sookmyung.noonsongmaker.jwt.CurrentUser;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class OneTimeEventController {

    private final OneTimeEventService oneTimeEventService;
    private final UserProfileService userProfileService;
    private final EventService eventService;

    @GetMapping("/onetime")
    public ResponseEntity<Response<Object>> getCurrentSemesterOnetimeEvents(@CurrentUser User user) {
        List<String> events =  eventService.getOneTimeEvents(user);
        return ResponseEntity.ok(new Response<>("현재 학기의 단발성 이벤트 목록을 불러왔습니다.", events));
    }

    // 학생회 지원
    @PostMapping("/council")
    public ResponseEntity<Response<Object>> applyForStudentCouncil(@CurrentUser User user) {
        Response<Object> response = oneTimeEventService.applyForStudentCouncil(user.getId());

        return ResponseEntity.ok(response);
    }

    // 리더십 그룹 지원
    @PostMapping("/leadership")
    public ResponseEntity<Response<Object>> applyForLeadershipGroup(@CurrentUser User user) {
        Response<Object> response = oneTimeEventService.applyForLeadershipGroup(user.getId());
        return ResponseEntity.ok(response);
    }

    // 전공 포기
    @PostMapping("/major/drop")
    public ResponseEntity<Response<MajorInfoResponseDto>> dropMajor(@CurrentUser User user) {
        userProfileService.dropMajor(user.getId());
        MajorInfoResponseDto majorInfo = new MajorInfoResponseDto(userProfileService.getUserProfile(user));
        return ResponseEntity.ok(new Response<>("전공 포기 완료", majorInfo));
    }

    // 졸업인증제
    @GetMapping("/graduation")
    public ResponseEntity<Response<Map<String, Object>>> checkGraduationEligibility(@CurrentUser User user) {
        Response<Map<String, Object>> response = oneTimeEventService.checkGraduationEligibility(user.getId());
        return ResponseEntity.ok(response);
    }

    // 교환학생 신청
    @PostMapping("exchange/apply")
    public ResponseEntity<Response<Object>> applyForExchangeStudent(@CurrentUser User user) {
        Response<Object> response = oneTimeEventService.applyForExchangeStudent(user.getId());
        return ResponseEntity.ok(response);
    }

    // 교환학생 진행
    @PostMapping("exchange/proceed")
    public ResponseEntity<Response<Object>> proceedExchangeStudent(@CurrentUser User user) {
        Response<Object> response = oneTimeEventService.proceedExchangeStudent(user.getId());
        return ResponseEntity.ok(response);
    }

    // 학석사 연계과정 신청
    @PostMapping("/combined-programs")
    public ResponseEntity<Response<String>> applyForGraduateIntegrated(@CurrentUser User user) {
        return ResponseEntity.ok(oneTimeEventService.applyForGraduateIntegrated(user.getId()));
    }

    // 대학원생 시퀀스 진행 여부 조회
    @GetMapping("/combined-programs/check")
    public ResponseEntity<Response<Boolean>> isGraduateSequenceActive(@CurrentUser User user) {
        return ResponseEntity.ok(oneTimeEventService.isGraduateSequenceActive(user.getId()));
    }

    // 인턴 지원
    @PostMapping("/internship")
    public ResponseEntity<Response<Object>> applyForIntern(@CurrentUser User user) {
        Response<Object> response = oneTimeEventService.applyForInternship(user.getId());
        return ResponseEntity.ok(response);
    }

    // 인턴 합격 여부
    @GetMapping("/internship/check")
    public ResponseEntity<Response<Boolean>> isInternshipActive(@CurrentUser User user) {
        return ResponseEntity.ok(oneTimeEventService.isInternshipActive(user.getId()));
    }

    // 공모전 지원
    @PostMapping("/contest")
    public ResponseEntity<Response<Object>> applyForContest(@CurrentUser User user) {
        Response<Object> response = oneTimeEventService.applyForContest(user.getId());
        return ResponseEntity.ok(response);
    }
}
