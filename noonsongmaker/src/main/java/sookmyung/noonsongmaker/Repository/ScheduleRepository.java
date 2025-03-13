package sookmyung.noonsongmaker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sookmyung.noonsongmaker.Entity.Schedule;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 특정 유저의 학기 일정 조회 (isVacation = false)
    List<Schedule> findByUserIdAndIsVacation(Long userId, boolean isVacation);
}