package sookmyung.noonsongmaker.Entity;

import jakarta.persistence.*;
import lombok.*;
import sookmyung.noonsongmaker.Dto.course.TimetableSubmitRequestDto;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "courses")
public class Course {

    @Id
    @Column(name = "course_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

    public void updateCourse(TimetableSubmitRequestDto dto) {
        if (dto.getCoreCredits() != null) this.coreCredits = dto.getCoreCredits();
        if (dto.getElectiveCredits() != null) this.electiveCredits = dto.getElectiveCredits();

        if (dto.getRequiredDigital() != null) this.requiredDigital = dto.getRequiredDigital();
        if (dto.getRequiredFuture() != null) this.requiredFuture = dto.getRequiredFuture();
        if (dto.getRequiredEng() != null) this.requiredEng = dto.getRequiredEng();
        if (dto.getRequiredLogic() != null) this.requiredLogic = dto.getRequiredLogic();

        if (dto.getCore1() != null) this.core1 = dto.getCore1();
        if (dto.getCore2() != null) this.core2 = dto.getCore2();
        if (dto.getCore3() != null) this.core3 = dto.getCore3();
        if (dto.getCore4() != null) this.core4 = dto.getCore4();
    }
}
