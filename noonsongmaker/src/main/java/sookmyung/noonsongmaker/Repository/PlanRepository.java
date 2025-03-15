package sookmyung.noonsongmaker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sookmyung.noonsongmaker.Entity.Plan;
import sookmyung.noonsongmaker.Entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    // 특정 유저의 모든 계획 조회
    List<Plan> findByUser(User user);

    // 특정 유저의 특정 활동 조회
    Optional<Plan> findByUserAndPlanName(User user, String planName);

    // 특정 유저가 추가한 "동아리 활동" 조회
    boolean existsByUserAndPlanName(User user, String planName);
  
   Optional<Plan> findByPlanName(String planName);
}
