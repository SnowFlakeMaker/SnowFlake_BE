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

    public void updateCredits(Integer core, Integer elective) {
        if (core != null) this.coreCredits = core;
        if (elective != null) this.electiveCredits = elective;
    }

    public void updateRequired(Boolean digital, Boolean future, Boolean eng, Boolean logic) {
        if (digital != null) this.requiredDigital = digital;
        if (future != null) this.requiredFuture = future;
        if (eng != null) this.requiredEng = eng;
        if (logic != null) this.requiredLogic = logic;
    }

    public void updateCore(Short core1, Short core2, Short core3, Short core4) {
        if (core1 != null) this.core1 = core1;
        if (core2 != null) this.core2 = core2;
        if (core3 != null) this.core3 = core3;
        if (core4 != null) this.core4 = core4;
    }
}
