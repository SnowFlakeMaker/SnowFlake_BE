package sookmyung.noonsongmaker.Dto.course;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimetableSubmitRequestDto {
    private Integer coreCredits;
    private Integer electiveCredits;

    private Boolean requiredDigital;
    private Boolean requiredFuture;
    private Boolean requiredEng;
    private Boolean requiredLogic;

    private Short core1;
    private Short core2;
    private Short core3;
    private Short core4;
}
