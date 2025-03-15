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

    // 특정 유저의 활성화된 계획 조회
    List<PlanStatus> findByUserAndIsActivatedTrue(User user);

    // 특정 유저의 모든 계획 상태 조회
    List<PlanStatus> findByUser(User user);

    // 특정 Plan에 대한 PlanStatus 찾기 (Plan이 존재하는지 확인)
    Optional<PlanStatus> findByPlan(Plan plan);

    // 특정 유저의 특정 계획을 비활성화 (삭제 대신 비활성화 처리)
    void deleteByUserAndPlan_PlanId(User user, Long planId);
}