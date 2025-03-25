package sookmyung.noonsongmaker.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.lang.reflect.Field;

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
    @ColumnDefault("0")
    private Integer hobbyAssess = 0;

    @Builder.Default
    @ColumnDefault("0")
    private Integer workAssess = 0;

    @Builder.Default
    @ColumnDefault("0")
    private Integer serviceAssess = 0;

    @Builder.Default
    @ColumnDefault("0")
    private Integer globalAssess = 0;

    @Builder.Default
    @ColumnDefault("300")
    private Integer coin = 100;

    @Builder.Default
    private boolean hasScholarship = false; // 국가장학금 신청 여부

    @Column(nullable = false)
    private int scholarshipAmount = 0; // 성적장학금 (0 = 없음, 200 = 반액, 400 = 전액)

    @Column(nullable = false)
    private boolean eligibleForMeritScholarship = false; // 성적 장학금 받을 자격 여부

    // 국가장학금 신청 여부 등록
    public void applyScholarship() {
        this.hasScholarship = true;
    }

    public void resetScholarship() {
        this.hasScholarship = false;
    }
    public void setEligibleForMeritScholarship(boolean eligible) {
        this.eligibleForMeritScholarship = eligible;
    }

    public void setScholarshipAmount(int amount) {
        this.scholarshipAmount = amount;
    }

    /**
     * 특정 스탯 값을 증가/감소 (0~150 자동 제한)
     * @param fieldName 스탯 이름 (예: "social", "stress")
     * @param amount 증감량
     */
    public void modifyStat(String fieldName, int amount) {
        try {
            Field field = this.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            int currentValue = (int) field.get(this);
            int newValue;

            // 코인은 0 이하로만 떨어지지 않고, 최대값 제한 없음
            if ("coin".equals(fieldName)) {
                newValue = Math.max(0, currentValue + amount); // 코인은 최소 0 이상 유지
            } else {
                newValue = Math.max(0, Math.min(150, currentValue + amount)); // 일반 스탯은 0~150 유지
            }

            field.set(this, newValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("존재하지 않는 스탯 필드: " + fieldName, e);
        }
    }

    private Integer changeValueWithinBound(Integer status, int amount) {
        return Math.max(0, Math.min(150, status + amount));
    }

    public void updateIntelligence(int amount) { this.intelligence = changeValueWithinBound(this.intelligence, amount); }

    public void updateForeignLang(int amount) { this.foreignLang = changeValueWithinBound(this.foreignLang, amount); }

    public void updateGrit(int amount) { this.grit = changeValueWithinBound(this.grit, amount); }

    public void updateStrength(int amount) { this.strength = changeValueWithinBound(this.strength, amount); }

    public void updateSocial(int amount) { this.social = changeValueWithinBound(this.social, amount); }

    public void updateStress(int amount) { this.stress = changeValueWithinBound(this.stress, amount); }

    public void updateLeadership(int amount) { this.leadership = changeValueWithinBound(this.leadership, amount); }

    public void updateGeneralAssess(int amount) { this.generalAssess = changeValueWithinBound(this.generalAssess, amount); }

    public void updateHobbyAssess(int amount) { this.hobbyAssess = changeValueWithinBound(this.hobbyAssess, amount); }

    public void updateWorkAssess(int amount) { this.workAssess = changeValueWithinBound(this.workAssess, amount); }

    public void updateServiceAssess(int amount) { this.serviceAssess = changeValueWithinBound(this.serviceAssess, amount); }

    public void updateGlobalAssess(int amount) { this.globalAssess = changeValueWithinBound(this.globalAssess, amount); }

    public void updateCoin(int amount) { this.coin = Math.max(0, this.coin + amount); }
}
