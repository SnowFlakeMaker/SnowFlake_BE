package sookmyung.noonsongmaker.Dto.ending;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EndingResponseDto {
    private String endingType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String endingText;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dream;
}
