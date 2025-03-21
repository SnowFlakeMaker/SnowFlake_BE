package sookmyung.noonsongmaker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sookmyung.noonsongmaker.Entity.PlanStatus;
import sookmyung.noonsongmaker.Entity.User;
import sookmyung.noonsongmaker.Entity.Plan;

import java.util.Optional;
import java.util.List;

@Repository
public interface PlanStatusRepository extends JpaRepository<PlanStatus, Long> {

    Optional<PlanStatus> findByPlanAndUser(Plan plan, User user);

    // 특정 유저의 모든 계획 상태 조회
    List<PlanStatus> findByUser(User user);

    List<PlanStatus> findByUserAndIsActivatedTrue(User user);
}