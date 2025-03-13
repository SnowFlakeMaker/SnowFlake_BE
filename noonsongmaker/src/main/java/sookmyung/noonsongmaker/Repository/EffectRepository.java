package sookmyung.noonsongmaker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sookmyung.noonsongmaker.Entity.Effect;
import sookmyung.noonsongmaker.Entity.Plan;

import java.util.List;

@Repository
public interface EffectRepository extends JpaRepository<Effect, Long> {
    List<Effect> findAllByPlan(Plan plan);
}
