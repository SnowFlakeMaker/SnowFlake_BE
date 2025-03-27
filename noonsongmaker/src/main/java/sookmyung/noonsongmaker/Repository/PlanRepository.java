package sookmyung.noonsongmaker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sookmyung.noonsongmaker.Entity.Plan;
import sookmyung.noonsongmaker.Entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

   Optional<Plan> findByPlanName(String planName);
}
