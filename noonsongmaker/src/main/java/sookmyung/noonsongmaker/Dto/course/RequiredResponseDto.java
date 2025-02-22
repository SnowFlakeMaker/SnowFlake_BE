package sookmyung.noonsongmaker.Dto.course;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequiredResponseDto {
    @JsonProperty("디사의")
    private Boolean requiredDigital;
    @JsonProperty("미래설계")
    private Boolean requiredFuture;
    @JsonProperty("영교필")
    private Boolean requiredEng;
    @JsonProperty("논사소")
    private Boolean requiredLogic;
}
