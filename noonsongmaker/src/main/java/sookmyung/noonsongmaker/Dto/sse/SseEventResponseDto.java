package sookmyung.noonsongmaker.Dto.sse;


import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SseEventResponseDto {
    private Object data;
    private LocalDateTime timestamp;

    public SseEventResponseDto(Object data) {
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
}
