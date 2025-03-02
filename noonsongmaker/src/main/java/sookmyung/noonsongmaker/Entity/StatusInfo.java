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

    @Builder.Default
    @ColumnDefault("100")
    private Integer coin = 100;

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
    
}
