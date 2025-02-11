package sookmyung.noonsongmaker.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "plans")
@Getter
@Setter
@NoArgsConstructor
public class Plan {

    @Id
    @Column(name = "plan_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planId;

    @Column(nullable = true)
    private String planName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Period period;

    @Column(nullable = false)
    private boolean isActivated = true;

}

enum Period {
    ACADEMIC, VACATION, BOTH, SPECIAL
}
