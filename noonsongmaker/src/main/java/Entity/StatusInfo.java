package Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StatusInfo {

    @Id
    @Column(name = "status_info_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO user 테이블 구현 완료되면 단방향 1:1 맵핑하기

    @Builder.Default()
    @ColumnDefault("25")
    private Integer intelligence = 25;

    @Builder.Default
    @ColumnDefault("20")
    private Integer foreignLang = 20;

    private Integer grit;
    private Integer strength;
    private Integer social;
    private Integer stress;
    private Integer leadership;

    @Builder.Default
    @ColumnDefault("20")
    private Integer generalAssess = 20;

    @Builder.Default
    @ColumnDefault("20")
    private Integer hobbyAssess = 0;

    @Builder.Default
    @ColumnDefault("20")
    private Integer workAssess = 0;

    @Builder.Default
    @ColumnDefault("20")
    private Integer serviceAssess = 0;

    @Builder.Default
    @ColumnDefault("20")
    private Integer globalAssess = 0;
}
