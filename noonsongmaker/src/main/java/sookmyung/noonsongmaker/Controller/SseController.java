package sookmyung.noonsongmaker.Controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sookmyung.noonsongmaker.Dto.Response;
import sookmyung.noonsongmaker.Dto.ending.EndingResponseDto;
import sookmyung.noonsongmaker.Entity.User;
import sookmyung.noonsongmaker.Service.UserService;
import sookmyung.noonsongmaker.Service.sse.SseService;
import sookmyung.noonsongmaker.jwt.CurrentUser;

import java.io.IOException;

@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;
    private final UserService userService;

    @GetMapping(value = "/get-userid")
    public ResponseEntity<Response<Long>> getUserId(@CurrentUser User user) {
        return ResponseEntity.ok(Response.buildResponse(user.getId(), "사용자 id"));
    }

    @GetMapping(value = "/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@PathVariable("userId") Long userId) {
        SseEmitter emitter = sseService.createEmitter(userId);

        return ResponseEntity.ok()
                .header("Cache-Control", "no-store, no-cache, must-revalidate, proxy-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .header("X-Accel-Buffering", "no")
                .body(emitter);

    }

    @GetMapping("/regular/{userId}")
    public ResponseEntity<Void> replayRegularEvents(@PathVariable("userId") Long userId) {
        User user = userService.getUser(userId);
        sseService.sendRegularEventsList(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/onetime/{userId}")
    public ResponseEntity<Void> replayOneTimeEvents(@PathVariable("userId") Long userId) {
        User user = userService.getUser(userId);
        sseService.sendOneTimeEventList(user);
        return ResponseEntity.ok().build();
    }
}
