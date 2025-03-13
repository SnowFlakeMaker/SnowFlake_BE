package sookmyung.noonsongmaker.Dto.course;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreResponseDto {
    @JsonProperty("교핵_1영역")
    private Short core1;
    @JsonProperty("교핵_2영역")
    private Short core2;
    @JsonProperty("교핵_3영역")
    private Short core3;
    @JsonProperty("교핵_4영역")
    private Short core4;
}
