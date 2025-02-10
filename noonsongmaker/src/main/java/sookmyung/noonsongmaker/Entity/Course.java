package sookmyung.noonsongmaker.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "courses")
public class Course {

    @Id
    @Column(name = "course_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Integer CoreCredits;
    private Integer ElectiveCredits;

    private Boolean isRequiredDigital;
    private Boolean isRequiredFuture;
    private Boolean isRequiredEng;
    private Boolean isRequiredLogic;

    private Short isCore1;
    private Short isCore2;
    private Short isCore3;
    private Short isCore4;
}
