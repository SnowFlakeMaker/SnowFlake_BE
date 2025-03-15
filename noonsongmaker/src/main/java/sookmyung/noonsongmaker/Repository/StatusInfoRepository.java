package sookmyung.noonsongmaker.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import sookmyung.noonsongmaker.Entity.StatusInfo;
import sookmyung.noonsongmaker.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusInfoRepository extends JpaRepository<StatusInfo, Long> {
    Optional<StatusInfo> findByUser(User user); // 특정 유저의 스탯 조회

    @Modifying
    @Query("UPDATE StatusInfo s SET s.hasScholarship = false WHERE s.user.id = :userId")
    void resetScholarshipForUser(@Param("userId") Long userId);
}
