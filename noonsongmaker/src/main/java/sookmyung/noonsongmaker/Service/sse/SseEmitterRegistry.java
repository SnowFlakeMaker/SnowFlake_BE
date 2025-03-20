package sookmyung.noonsongmaker.Service.sse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseEmitterRegistry {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void register(Long userId, SseEmitter emitter) {
        emitters.put(userId, emitter);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));
    }

    public SseEmitter get(Long userId) {
        return emitters.get(userId);
    }

    public void remove(Long userId) {
        emitters.remove(userId);
    }

    public Collection<SseEmitter> getAllEmitters() {
        return emitters.values();
    }
}