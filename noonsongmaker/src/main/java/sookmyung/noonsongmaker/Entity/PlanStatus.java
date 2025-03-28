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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private boolean isActivated;

    @Builder.Default
    @Column(nullable = false)
    private int remainingSemesters = 16;

    public void setActivated(boolean activated) {
        this.isActivated = activated;
    }

    public void setRemainingSemesters(int remainingSemesters) {
        this.remainingSemesters = remainingSemesters;
    }
}
