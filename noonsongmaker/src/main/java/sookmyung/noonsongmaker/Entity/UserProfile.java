package sookmyung.noonsongmaker.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
public class UserProfile{

    @Id
    @Column(name = "user_profile_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 6)
    private String nickname;

    @Column(nullable = false, length = 15)
    private String major; // 추가

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MajorType majorType = MajorType.UNKNOWN;

    @Column(nullable = false)
    private String birthday;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MBTI mbti;

    @Column(nullable = false)
    private String hobby;

    @Column(nullable = false, length = 10)
    private String dream;


}

