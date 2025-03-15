package sookmyung.noonsongmaker.Entity;

import jakarta.persistence.*;

import lombok.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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
