package sookmyung.noonsongmaker.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 12, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Setter
    @Enumerated(EnumType.STRING)
    private Chapter currentChapter;
}
