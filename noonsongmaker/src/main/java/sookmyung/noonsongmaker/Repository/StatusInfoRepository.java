package sookmyung.noonsongmaker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sookmyung.noonsongmaker.Entity.StatusInfo;
import sookmyung.noonsongmaker.Entity.User;

import java.util.Optional;

@Repository
public interface StatusInfoRepository extends JpaRepository<StatusInfo, Long> {
    Optional<StatusInfo> findByUser(User user);
}
