package sookmyung.noonsongmaker.Entity;

import jakarta.persistence.*;
import lombok.*;

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

    private Integer dmCredits; // 복수전공(double) 혹은 부전공(minor) 학점

    private Boolean requiredDigital;
    private Boolean requiredFuture;
    private Boolean requiredEng;
    private Boolean requiredLogic;

    private Short core1;
    private Short core2;
    private Short core3;
    private Short core4;

    public void updateCoreCredits(Integer core) {
        if (core != null) this.coreCredits += core;
    }
    public void updateElectiveCredits(Integer elective) {
        if (elective != null) this.electiveCredits += elective;
    }

    public void updateDmCredits(Integer dm) { if (dm != null) this.dmCredits += dm; }

    public void updateRequiredDigital(Boolean digital) {
        if (digital != null) this.requiredDigital = digital;
    }
    public void updateRequiredFuture(Boolean future) {
        if (future != null) this.requiredFuture = future;
    }
    public void updateRequiredEng(Boolean eng) {
        if (eng != null) this.requiredEng = eng;
    }
    public void updateRequiredLogic(Boolean logic) {
        if (logic != null) this.requiredLogic = logic;
    }

    public void updateCore1(Short core1) {
        if (core1 != null) this.core1 = (short)(this.core1 + core1);
    }
    public void updateCore2(Short core2) {
        if (core2 != null) this.core2 = (short)(this.core2 + core2);
    }
    public void updateCore3(Short core3) {
        if (core3 != null) this.core3 = (short)(this.core3 + core3);
    }
    public void updateCore4(Short core4) {
        if (core4 != null) this.core4 = (short)(this.core4 + core4);
    }
}
