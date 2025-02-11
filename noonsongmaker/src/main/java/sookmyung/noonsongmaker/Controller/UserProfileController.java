package sookmyung.noonsongmaker.Controller;

import org.springframework.data.util.Pair;
import sookmyung.noonsongmaker.Dto.Response;
import sookmyung.noonsongmaker.Dto.UserProfile.StatusInfoResponse;
import sookmyung.noonsongmaker.Dto.UserProfile.UserProfileRequest;
import sookmyung.noonsongmaker.Dto.UserProfile.UserProfileResponse;
import sookmyung.noonsongmaker.Entity.StatusInfo;
import sookmyung.noonsongmaker.Entity.UserProfile;
import sookmyung.noonsongmaker.Service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping("/info-new/{userId}")
    public ResponseEntity<Response<UserProfileResponse>> createUserProfile(
            @PathVariable Long userId,
            @RequestBody UserProfileRequest request) {

        Pair<UserProfile, StatusInfo> result = userProfileService.createUserProfile(userId, request);

        StatusInfoResponse statusInfoResponse = new StatusInfoResponse(result.getSecond());
        UserProfileResponse responseDto = new UserProfileResponse(result.getFirst(), statusInfoResponse);

        // DTO 변환
        return ResponseEntity.ok(new Response<>("초기 설정이 완료되었습니다.", responseDto));
    }
}
