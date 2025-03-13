package sookmyung.noonsongmaker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sookmyung.noonsongmaker.Entity.Event;
import sookmyung.noonsongmaker.Entity.EventChapters;
import sookmyung.noonsongmaker.Entity.Chapter;

import java.util.List;

@Repository
public interface EventChaptersRepository extends JpaRepository<EventChapters, Long> {

    // 특정 이벤트가 해당 챕터에 이미 존재하는지 확인 (중복 등록 방지)
    boolean existsByEventAndActivatedChapter(Event event, Chapter activatedChapter);

    // 특정 이벤트 삭제
    void deleteByEventAndActivatedChapter(Event event, Chapter activatedChapter);

    // 특정 챕터에서 활성화된 이벤트 목록 조회
    List<EventChapters> findByActivatedChapter(Chapter activatedChapter);
}