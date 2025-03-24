package sookmyung.noonsongmaker.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sookmyung.noonsongmaker.Dto.Response;
import sookmyung.noonsongmaker.Dto.intro.StatusInfoResponse;
import sookmyung.noonsongmaker.Dto.main.ChapterResponseDto;
import sookmyung.noonsongmaker.Dto.main.PlayerInfoResponseDto;
import sookmyung.noonsongmaker.Entity.Chapter;
import sookmyung.noonsongmaker.jwt.CurrentUser;
import sookmyung.noonsongmaker.Entity.User;
import sookmyung.noonsongmaker.Service.UserService;
import sookmyung.noonsongmaker.Service.main.MainInfoService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainController {

    private final MainInfoService mainInfoService;
    private final UserService userService;

    // 현재 챕터 조회
    @GetMapping("/chapter")
    public ResponseEntity<Response<Map<String, ChapterResponseDto>>> getCurrentChapter(@CurrentUser User user) {
        ChapterResponseDto chapterResponse = mainInfoService.getCurrentChapter(user.getId());

        Map<String, ChapterResponseDto> response = new HashMap<>();
        response.put("current_chapter", chapterResponse);

        return ResponseEntity.ok(Response.buildResponse(response, "플레이어의 현재 학기 정보를 조회합니다."));
    }

    // 플레이어 정보 조회
    @GetMapping("/player")
    public ResponseEntity<Response<Map<String, PlayerInfoResponseDto>>> getPlayerInfo(@CurrentUser User user) {
        PlayerInfoResponseDto playerInfo = mainInfoService.getPlayerInfo(user.getId());

        Map<String, PlayerInfoResponseDto> response = new HashMap<>();
        response.put("player_info", playerInfo);

        return ResponseEntity.ok(Response.buildResponse(response, "플레이어 정보를 조회합니다."));
    }

    // 학기 변경
    @PostMapping("/change-semester")
    public ResponseEntity<Response<ChapterResponseDto>> changeSemester(
            @CurrentUser User user) {

        ChapterResponseDto response = userService.changeSemester(user.getId());
        return ResponseEntity.ok(new Response<>("학기가 변경되었습니다.", response));
    }

    // 현재 스탯 조회
    @GetMapping("/status")
    public ResponseEntity<Response<StatusInfoResponse>> getStatus(@CurrentUser User user) {
        StatusInfoResponse statusInfoResponse = mainInfoService.getStatusInfo(user.getId());
        Response<StatusInfoResponse> response = Response.buildResponse(statusInfoResponse, "현재 스탯을 조회합니다.");

        return ResponseEntity.ok(response);
    }

    // 현재 스탯 조회
    @GetMapping("/status")
    public ResponseEntity<Response<StatusInfoResponse>> getStatus(@CurrentUser User user) {
        StatusInfoResponse statusInfoResponse = mainInfoService.getStatusInfo(user.getId());
        Response<StatusInfoResponse> response = Response.buildResponse(statusInfoResponse, "현재 스탯을 조회합니다.");

        return ResponseEntity.ok(response);
    }
}