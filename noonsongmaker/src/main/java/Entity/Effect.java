package Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "effects")
@Getter
@Setter
@NoArgsConstructor
public class Effect {
    @Id
    @Column(name = "effect_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = true)
    private Plan plan;

    @Column(nullable = false)
    private boolean isPlan;

    @Column(nullable = true)
    private Short changeAmount;
}