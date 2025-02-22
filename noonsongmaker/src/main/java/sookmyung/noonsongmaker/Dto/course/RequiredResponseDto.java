package sookmyung.noonsongmaker.Dto.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequiredResponseDto {
    private Boolean isRequiredDigital;
    private Boolean isRequiredFuture;
    private Boolean isRequiredEng;
    private Boolean isRequiredLogic;}
