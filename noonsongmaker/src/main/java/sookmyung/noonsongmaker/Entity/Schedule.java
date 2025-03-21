package sookmyung.noonsongmaker.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "schedules")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    @Id
    @Column(name = "schedule_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Chapter currentChapter;

    @Column(nullable = false)
    private int count;

    @Column(nullable = false)
    private boolean isVacation;

    public Schedule(Plan plan, User user, Chapter currentChapter, int count) {
        this.plan = plan;
        this.user = user;
        this.currentChapter = currentChapter;
        this.count = count;
    }
}
