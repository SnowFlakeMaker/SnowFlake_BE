package sookmyung.noonsongmaker.Entity;

import jakarta.persistence.*;
import lombok.*;
import sookmyung.noonsongmaker.Dto.course.TimetableSubmitRequestDto;

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

    private Integer coreCredits;
    private Integer electiveCredits;

    private Boolean requiredDigital;
    private Boolean requiredFuture;
    private Boolean requiredEng;
    private Boolean requiredLogic;

    private Short core1;
    private Short core2;
    private Short core3;
    private Short core4;


}
