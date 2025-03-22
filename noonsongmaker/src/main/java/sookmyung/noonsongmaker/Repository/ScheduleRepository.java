package sookmyung.noonsongmaker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sookmyung.noonsongmaker.Entity.Chapter;
import sookmyung.noonsongmaker.Entity.Plan;
import sookmyung.noonsongmaker.Entity.Schedule;
import sookmyung.noonsongmaker.Entity.User;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByUserAndCurrentChapter(User user, Chapter currentChapter);

    List<Schedule> findByUserAndCurrentChapterAndPlan(User user, Chapter currentChapter, Plan plan);


}

