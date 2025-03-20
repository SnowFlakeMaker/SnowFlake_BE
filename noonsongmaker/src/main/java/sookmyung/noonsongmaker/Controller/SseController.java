package sookmyung.noonsongmaker.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sookmyung.noonsongmaker.Entity.User;
import sookmyung.noonsongmaker.Service.sse.SseService;
import sookmyung.noonsongmaker.jwt.CurrentUser;

@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@CurrentUser User user) {
        return sseService.createEmitter(user.getId());
    }
}
