package sookmyung.noonsongmaker.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PlanStatus {

    @Id
    @Column(name = "plan_status_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planStatusId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private boolean isActivated = true;

    @Builder.Default
    @Column(nullable = false)
    private int remainingSemesters = 16;
}
