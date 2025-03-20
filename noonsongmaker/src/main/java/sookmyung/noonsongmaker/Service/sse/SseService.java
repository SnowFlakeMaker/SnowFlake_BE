package sookmyung.noonsongmaker.Service.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sookmyung.noonsongmaker.Entity.User;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SseService {

    private final SseEmitterRegistry emitterRegistry;

    public SseEmitter createEmitter(Long userId) {
        SseEmitter emitter = new SseEmitter(60L * 60L * 1000L);
        emitterRegistry.register(userId, emitter);

        try {
            emitter.send(SseEmitter.event().name("init").data("connected"));
        } catch (IOException e) {
            emitter.complete();
            emitterRegistry.remove(userId);
        }
        return emitter;
    }

    public void sendRegularEventsList(Long userId) {}

    public void sendOneTimeEventList(Long userId) {}
}
