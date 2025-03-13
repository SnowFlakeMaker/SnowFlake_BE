package sookmyung.noonsongmaker.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Plan {

    @Id
    @Column(name = "plan_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // User 연관관계 추가
    private User user;

    @Column(nullable = true)
    private String planName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Period period;

}

