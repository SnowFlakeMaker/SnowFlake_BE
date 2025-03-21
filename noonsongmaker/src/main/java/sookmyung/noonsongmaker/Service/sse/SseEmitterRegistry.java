package sookmyung.noonsongmaker.Service.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseEmitterRegistry {

    private static final Logger log = LoggerFactory.getLogger(SseEmitterRegistry.class);
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void register(Long userId, SseEmitter emitter) {
        if (emitters.containsKey(userId)) {
            log.warn("Overwriting existing emitter for userId: {}", userId);
        }
        log.info("Registering emitter for userId: {}", userId);
        emitters.put(userId, emitter);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));
    }

    public SseEmitter get(Long userId) {
        return emitters.get(userId);
    }

    public void remove(Long userId) {
        log.info("Removing emitter for userId: {}", userId);
        emitters.remove(userId);
    }

    public Collection<SseEmitter> getAllEmitters() {
        return emitters.values();
    }
}