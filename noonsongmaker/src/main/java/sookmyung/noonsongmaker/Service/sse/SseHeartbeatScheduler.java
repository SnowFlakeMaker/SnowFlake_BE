package sookmyung.noonsongmaker.Service.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SseHeartbeatScheduler {

    private final SseEmitterRegistry emitterRegistry;

    @Scheduled(fixedRate = 10000)
    public void sendHeartbeats() {
        for (SseEmitter emitter : emitterRegistry.getAllEmitters()) {
            try {
                emitter.send(SseEmitter.event().name("heartbeat").data("ping"));
            } catch (IOException e) {
                emitter.complete();
            }
        }
    }
}
