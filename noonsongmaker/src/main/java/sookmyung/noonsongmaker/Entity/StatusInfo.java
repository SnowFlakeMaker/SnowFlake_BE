package sookmyung.noonsongmaker.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StatusInfo {

    @Id
    @Column(name = "status_info_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default()
    @ColumnDefault("25")
    private Integer intelligence = 25;

    @Builder.Default
    @ColumnDefault("20")
    private Integer foreignLang = 20;

    private Integer grit;
    private Integer strength;
    private Integer social;
    private Integer stress;
    private Integer leadership;

    @Builder.Default
    @ColumnDefault("20")
    private Integer generalAssess = 20;

    @Builder.Default
    @ColumnDefault("20")
    private Integer hobbyAssess = 0;

    @Builder.Default
    @ColumnDefault("20")
    private Integer workAssess = 0;

    @Builder.Default
    @ColumnDefault("20")
    private Integer serviceAssess = 0;

    @Builder.Default
    @ColumnDefault("20")
    private Integer globalAssess = 0;


    public void updateIntelligence(int amount) {
        this.intelligence += amount;
    }

    public void updateForeignLang(int amount) {
        this.foreignLang += amount;
    }

    public void updateGrit(int amount) {
        this.grit += amount;
    }

    public void updateStrength(int amount) {
        this.strength += amount;
    }

    public void updateSocial(int amount) {
        this.social += amount;
    }

    public void updateStress(int amount) {
        this.stress += amount;
    }

    public void updateLeadership(int amount) {
        this.leadership += amount;
    }
}
