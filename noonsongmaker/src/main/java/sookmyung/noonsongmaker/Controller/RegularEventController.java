package sookmyung.noonsongmaker.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sookmyung.noonsongmaker.Dto.Response;
import sookmyung.noonsongmaker.Dto.event.*;
import sookmyung.noonsongmaker.Service.UserProfileService;
import sookmyung.noonsongmaker.jwt.CurrentUser;
import sookmyung.noonsongmaker.Entity.User;
import sookmyung.noonsongmaker.Service.UserService;
import sookmyung.noonsongmaker.Service.event.RegularEventService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class RegularEventController {

    private final RegularEventService regularEventService;
    private final UserProfileService userProfileService;

    @GetMapping("/available")
    public ResponseEntity<Response<Map<String, List<String>>>> getAvailableEvents(@CurrentUser User user) {
        List<String> availableEvents = regularEventService.getAvailableEvents(user.getId());

        Map<String, List<String>> response = new HashMap<>();
        response.put("available_events", availableEvents);

        return ResponseEntity.ok(Response.buildResponse(response, "현재 학기에서 활성화된 이벤트 목록"));
    }

    // 개강총회
    @PostMapping("/orientation")
    public ResponseEntity<Response<Map<String, StatsResponseDto>>> attendOrientation(@CurrentUser User user) {
        StatsResponseDto updatedStats = regularEventService.processOrientation(user.getId());

        Map<String, StatsResponseDto> response = new HashMap<>();
        response.put("updated_stats", updatedStats);

        return ResponseEntity.ok(Response.buildResponse(response, "개강총회 참석 완료, 스탯이 업데이트 되었습니다."));
    }

    // MT
    @PostMapping("/mt")
    public ResponseEntity<Response<Map<String, StatsResponseDto>>> attendMT(@CurrentUser User user) {
        StatsResponseDto updatedStats = regularEventService.attendMT(user.getId());

        Map<String, StatsResponseDto> response = new HashMap<>();
        response.put("updated_stats", updatedStats);

        return ResponseEntity.ok(Response.buildResponse(response, "MT 참석 완료, 스탯이 업데이트 되었습니다."));
    }

    // 축제
    @PostMapping("/festival")
    public ResponseEntity<Response<Map<String, StatsResponseDto>>> attendFestival(@CurrentUser User user) {
        StatsResponseDto updatedStats = regularEventService.attendFestival(user.getId());

        Map<String, StatsResponseDto> response = new HashMap<>();
        response.put("updated_stats", updatedStats);

        return ResponseEntity.ok(Response.buildResponse(response, "축제 참석 완료, 스탯이 업데이트 되었습니다."));
    }

    // 등록금 납부
    @PostMapping("/tuition")
    public ResponseEntity<Response<Map<String, CoinResponseDto>>> payTuition(@CurrentUser User user) {
        CoinResponseDto updatedStats = regularEventService.payTuition(user.getId());

        Map<String, CoinResponseDto> response = new HashMap<>();
        response.put("updated_coin", updatedStats);

        return ResponseEntity.ok(Response.buildResponse(response, "등록금 납부 완료"));
    }

    // 국가장학금 신청
    @PostMapping("/scholarships")
    public ResponseEntity<Response<Map<String, CoinResponseDto>>> applyScholarship(@CurrentUser User user) {
        CoinResponseDto updatedStats = regularEventService.applyScholarship(user.getId());

        Map<String, CoinResponseDto> response = new HashMap<>();
        response.put("updated_coin", updatedStats);

        return ResponseEntity.ok(Response.buildResponse(response, "국가장학금 신청 완료"));
    }

    // 등록금 대리납부
    @PostMapping("/tuition_help")
    public ResponseEntity<Response<Map<String, CoinAndStressResponseDto>>> requestTuitionHelp(
            @CurrentUser User user,
            @RequestParam int parentSupport) {

        CoinAndStressResponseDto updatedStats = regularEventService.requestTuitionHelp(user.getId(), parentSupport);

        Map<String, CoinAndStressResponseDto> response = new HashMap<>();
        response.put("updated_stats", updatedStats);

        return ResponseEntity.ok(Response.buildResponse(response, "등록금 대리납부 완료"));
    }

    // 성적 장학금 지급 (유저 요청 시 실행)
    @PostMapping("/merit")
    public ResponseEntity<Response<Map<String, CoinResponseDto>>> grantMeritScholarship(@CurrentUser User user) {
        CoinResponseDto updatedStats = regularEventService.grantMeritScholarship(user.getId());

        Map<String, CoinResponseDto> response = new HashMap<>();
        response.put("updated_coin", updatedStats);

        return ResponseEntity.ok(Response.buildResponse(response, "성적 장학금 지급 완료"));
    }

    // 동아리 지원
    @PostMapping("/club")
    public ResponseEntity<Response<Object>> applyForClub(@CurrentUser User user) {
        Response<Object> response = regularEventService.applyForClub(user.getId());
        return ResponseEntity.ok(response);
    }

    // 전공학회 지원
    @PostMapping("/conference")
    public ResponseEntity<Response<String>> applyForMajorClub(@CurrentUser User user) {
        regularEventService.applyForMajorClub(user.getId());
        return ResponseEntity.ok(new Response<>("전공 학회 지원 완료", null));
    }

    // 대외활동 지원
    @PostMapping("/external")
    public ResponseEntity<Response<String>> applyForExternalActivity(@CurrentUser User user) {
        regularEventService.applyForExternalActivity(user.getId());
        return ResponseEntity.ok(new Response<>("대외활동 지원 완료", null));
    }

    // 리더십 그룹 지원
    @PostMapping("/leadership")
    public ResponseEntity<Response<String>> applyForLeadershipGroup(@CurrentUser User user) {
        Response<String> response = regularEventService.applyForLeadershipGroup(user.getId());
        return ResponseEntity.ok(response);
    }

    // 전공 신청
    @PostMapping("/major")
    public ResponseEntity<Response<MajorInfoResponseDto>> applyForMajor(@CurrentUser User user,
                                                                        @RequestParam(required = false) String majorChoice) {
        userProfileService.applyForMajor(user.getId(), majorChoice);
        MajorInfoResponseDto majorInfo = new MajorInfoResponseDto(userProfileService.getUserProfile(user));

        return ResponseEntity.ok(new Response<>("전공 신청 완료", majorInfo));
    }
}