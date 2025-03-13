package sookmyung.noonsongmaker.Controller;

import org.springframework.data.util.Pair;
import sookmyung.noonsongmaker.Dto.Response;
import sookmyung.noonsongmaker.Dto.intro.StatusInfoResponse;
import sookmyung.noonsongmaker.Dto.intro.UserProfileRequest;
import sookmyung.noonsongmaker.Dto.intro.UserProfileResponse;
import sookmyung.noonsongmaker.jwt.CurrentUser;
import sookmyung.noonsongmaker.Entity.StatusInfo;
import sookmyung.noonsongmaker.Entity.User;
import sookmyung.noonsongmaker.Entity.UserProfile;
import sookmyung.noonsongmaker.Service.intro.IntroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/intro")
public class IntroController {

    private final IntroService userProfileService;

    // 회원 정보 작성(스탯 세팅)
    @PostMapping("/info-new")
    public ResponseEntity<Response<Map<String, UserProfileResponse>>> createUserProfile(
            @CurrentUser User user,
            @RequestBody UserProfileRequest request) {

        Pair<UserProfile, StatusInfo> result = userProfileService.createUserProfile(user.getId(), request);

        StatusInfoResponse statusInfoResponse = new StatusInfoResponse(result.getSecond());
        UserProfileResponse responseDto = new UserProfileResponse(result.getFirst(), statusInfoResponse);

        Map<String, UserProfileResponse> response = new HashMap<>();
        response.put("user_profile", responseDto);

        return ResponseEntity.ok(Response.buildResponse(response, "초기 설정이 완료되었습니다."));
    }

    // 플레이어 초기 세팅 조회
    @GetMapping("/entrance")
    public ResponseEntity<Response<Map<String, UserProfileResponse>>> getUserProfile(@CurrentUser User user) {
        Pair<UserProfile, StatusInfo> result = userProfileService.getUserProfile(user.getId());

        StatusInfoResponse statusInfoResponse = new StatusInfoResponse(result.getSecond());
        UserProfileResponse responseDto = new UserProfileResponse(result.getFirst(), statusInfoResponse);

        Map<String, UserProfileResponse> response = new HashMap<>();
        response.put("user_profile", responseDto);

        return ResponseEntity.ok(Response.buildResponse(response, "유저 프로필을 조회합니다."));
    }
}