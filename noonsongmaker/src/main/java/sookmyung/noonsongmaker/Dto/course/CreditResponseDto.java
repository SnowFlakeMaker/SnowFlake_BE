package sookmyung.noonsongmaker.Dto.course;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sookmyung.noonsongmaker.Entity.Chapter;
import sookmyung.noonsongmaker.Entity.MajorType;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreditResponseDto {
    private Chapter semester;
    private String major;
    private MajorType majorType;
    private Integer CurrentCoreCredits;
    private Integer CurrentElectivesCredits;


}
