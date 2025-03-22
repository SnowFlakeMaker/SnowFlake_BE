package sookmyung.noonsongmaker.Service.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sookmyung.noonsongmaker.Dto.sse.SseEventResponseDto;
import sookmyung.noonsongmaker.Dto.sse.SseEventType;
import sookmyung.noonsongmaker.Entity.User;
import sookmyung.noonsongmaker.Service.event.EventService;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SseService {

    private final SseEmitterRegistry emitterRegistry;
    private final EventService eventService;

    public SseEmitter createEmitter(Long userId) {
        SseEmitter emitter = new SseEmitter(60L * 60L * 1000L);
        emitterRegistry.register(userId, emitter);

        sendSse(userId, SseEventType.INIT, "connected");
        return emitter;
    }

    public void sendRegularEventsList(User user) {
        List<String> regularEventNames = eventService.getRegularEvents(user);
        sendSse(user.getId(), SseEventType.REGULAR_EVENT, regularEventNames);
    }

    public void sendOneTimeEventList(User user) {
        List<String> oneTimeEventsName = eventService.getOneTimeEvents(user);

        sendSse(user.getId(), SseEventType.ONETIME_EVENT, oneTimeEventsName);
    }

    private void sendSse(Long userId, SseEventType eventType, Object data) {
        SseEmitter emitter = emitterRegistry.get(userId);
        try {
            SseEventResponseDto response = new SseEventResponseDto(data);
            emitter.send(SseEmitter.event()
                    .name(eventType.name().toLowerCase())
                    .data(response));
        } catch (IOException e) {
            log.error("SSE 전송 실패. user_id {}: {}", userId, e.getMessage());
            emitter.complete();
            emitterRegistry.remove(userId);
        }
    }
}
