package sookmyung.noonsongmaker.Dto.event;

import lombok.Getter;

import java.util.List;

@Getter
public class SemesterEventsResponseDto {
    private String message;
    private List<String> events; // 활성화된 이벤트 목록

    public SemesterEventsResponseDto(String message, List<String> events) {
        this.message = message;
        this.events = events;
    }
}