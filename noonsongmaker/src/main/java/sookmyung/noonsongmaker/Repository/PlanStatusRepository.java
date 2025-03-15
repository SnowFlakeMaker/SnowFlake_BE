package sookmyung.noonsongmaker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sookmyung.noonsongmaker.Entity.PlanStatus;
import sookmyung.noonsongmaker.Entity.User;

import java.util.Optional;
import java.util.List;

@Repository
public interface PlanStatusRepository extends JpaRepository<PlanStatus, Long> {

    // 특정 유저의 활성화된 계획 조회
    List<PlanStatus> findByUserAndIsActivatedTrue(User user);

    // 특정 유저의 모든 계획 상태 조회
    List<PlanStatus> findByUser(User user);

    // 특정 유저와 계획 ID로 계획 상태 조회
    Optional<PlanStatus> findByUserAndPlan_PlanId(User user, Long planId);

    // 특정 유저의 특정 계획을 비활성화 (삭제 대신 비활성화 처리)
    void deleteByUserAndPlan_PlanId(User user, Long planId);
}