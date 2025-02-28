package sookmyung.noonsongmaker.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sookmyung.noonsongmaker.Dto.Response;
import sookmyung.noonsongmaker.Service.event.RegularEventService;
import sookmyung.noonsongmaker.Dto.event.StatsResponseDto;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class RegularEventController {

    private final RegularEventService regularEventService;

    @PostMapping("/orientation/{userId}")
    public ResponseEntity<Response<StatsResponseDto>> attendOrientation(@PathVariable Long userId) {
        StatsResponseDto updatedStats = regularEventService.processOrientation(userId);
        return ResponseEntity.ok(new Response<>("개강총회 참석 완료, 스탯이 업데이트 되었습니다.", updatedStats));
    }
}