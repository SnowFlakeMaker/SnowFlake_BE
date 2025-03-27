package sookmyung.noonsongmaker.Service.sse;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sookmyung.noonsongmaker.Dto.sse.SseEventType;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SseHeartbeatScheduler {

    private final SseEmitterRegistry emitterRegistry;
    private static final Logger log = LoggerFactory.getLogger(SseHeartbeatScheduler.class);

    @Scheduled(fixedRate = 10000)
    public void sendHeartbeats() {
        //log.info("Heartbeat scheduler triggered. Total emitters: {}", emitterRegistry.getAllEmitters().size());
        for (Map.Entry<Long, SseEmitter> entry : emitterRegistry.getAllEmittersWithIds().entrySet()) {
            Long userId = entry.getKey();
            SseEmitter emitter = entry.getValue();
            //log.info("Sending heartbeat to an emitter...");
            try {
                emitter.send(SseEmitter.event().
                        name(SseEventType.HEARTBEAT.name().toLowerCase())
                        .data("ping"));
            } catch (IOException e) {
                emitter.complete();
                emitterRegistry.remove(userId);
                //log.warn("Emitter failed during heartbeat and was completed due to: {}", e.getMessage());
            }
        }
    }
}
