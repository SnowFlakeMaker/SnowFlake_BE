package Entity;

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

    // TODO user 테이블 구현 완료되면 단방향 1:1 맵핑하기

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
