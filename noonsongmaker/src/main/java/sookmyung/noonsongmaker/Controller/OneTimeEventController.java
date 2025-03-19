//package sookmyung.noonsongmaker.Controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import sookmyung.noonsongmaker.Dto.Response;
//import sookmyung.noonsongmaker.Dto.event.MajorInfoResponseDto;
//import sookmyung.noonsongmaker.Dto.event.StatsResponseDto;
//import sookmyung.noonsongmaker.Entity.User;
//import sookmyung.noonsongmaker.Service.UserProfileService;
//import sookmyung.noonsongmaker.Service.event.OneTimeEventService;
//import sookmyung.noonsongmaker.jwt.CurrentUser;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/event")
//@RequiredArgsConstructor
//public class OneTimeEventController {
//
//    private final OneTimeEventService oneTimeEventService;
//    private final UserProfileService userProfileService;
//
//    // 학생회 지원
//    @PostMapping("/council")
//    public ResponseEntity<Response<Object>> applyForStudentCouncil(@CurrentUser User user) {
//        Response<Object> response =oneTimeEventService.applyForStudentCouncil(user.getId());
//
//        return ResponseEntity.ok(response);
//    }
//
//    // 전공 포기
//    @PostMapping("/major/drop")
//    public ResponseEntity<Response<MajorInfoResponseDto>> dropMajor(@CurrentUser User user) {
//        userProfileService.dropMajor(user.getId());
//        MajorInfoResponseDto majorInfo = new MajorInfoResponseDto(userProfileService.getUserProfile(user));
//        return ResponseEntity.ok(new Response<>("전공 포기 완료", majorInfo));
//    }
//
//    // 졸업인증제
//    @GetMapping("/graduation")
//    public ResponseEntity<Response<Map<String, Object>>> checkGraduationEligibility(@CurrentUser User user) {
//        Response<Map<String, Object>> response = oneTimeEventService.checkGraduationEligibility(user.getId());
//        return ResponseEntity.ok(response);
//    }
//}
