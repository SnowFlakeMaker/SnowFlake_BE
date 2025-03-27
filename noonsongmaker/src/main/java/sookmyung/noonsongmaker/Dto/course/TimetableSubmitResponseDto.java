package sookmyung.noonsongmaker.Dto.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class TimetableSubmitResponseDto {
    private Map<String, Object> updateResults;
}
