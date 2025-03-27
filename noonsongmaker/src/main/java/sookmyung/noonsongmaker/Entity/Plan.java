package sookmyung.noonsongmaker.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "plans")
@Getter
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
}

