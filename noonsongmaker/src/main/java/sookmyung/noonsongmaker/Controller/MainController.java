package sookmyung.noonsongmaker.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sookmyung.noonsongmaker.Dto.Response;
import sookmyung.noonsongmaker.Dto.main.ChapterResponseDto;
import sookmyung.noonsongmaker.Dto.main.PlayerInfoResponseDto;
import sookmyung.noonsongmaker.Service.main.MainInfoService;


@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainController {

    private final MainInfoService mainInfoService;

    @GetMapping("/chapter/{userId}")
    public ResponseEntity<Response<ChapterResponseDto>> getCurrentChapter(@PathVariable Long userId) {
        ChapterResponseDto chapterResponse = mainInfoService.getCurrentChapter(userId);
        return ResponseEntity.ok(new Response<>("플레이어의 현재 학기 정보를 조회합니다.", chapterResponse));
    }

    @GetMapping("/player/{userId}")
    public ResponseEntity<Response<PlayerInfoResponseDto>> getPlayerInfo(@PathVariable Long userId) {
        PlayerInfoResponseDto playerInfo = mainInfoService.getPlayerInfo(userId);
        return ResponseEntity.ok(new Response<>("플레이어 정보", playerInfo));
    }
}
