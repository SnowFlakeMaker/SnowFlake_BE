package sookmyung.noonsongmaker.Dto.course;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimetableSubmitRequestDto {
    private Integer coreCredits;
    private Integer electiveCredits;

    @JsonProperty("디사의")
    private Boolean requiredDigital;
    @JsonProperty("미래설계")
    private Boolean requiredFuture;
    @JsonProperty("영교필")
    private Boolean requiredEng;
    @JsonProperty("논사소")
    private Boolean requiredLogic;

    private Short core1;
    private Short core2;
    private Short core3;
    private Short core4;
}
