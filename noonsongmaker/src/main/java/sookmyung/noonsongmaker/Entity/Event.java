package sookmyung.noonsongmaker.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "events")
public class Event {

    @Id
    @Column(name = "event_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_name", nullable = false)
    private String name;

    private Float probability = 1F;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "event_activated_chapters", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "activated_chapter")
    private List<Chapter> activatedChapters;
}
