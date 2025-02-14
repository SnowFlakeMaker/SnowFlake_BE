package sookmyung.noonsongmaker.Repository;

import sookmyung.noonsongmaker.Entity.StatusInfo;
import sookmyung.noonsongmaker.Entity.User;
import sookmyung.noonsongmaker.Entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    boolean existsByUser(User user); // 특정 유저의 프로필 존재 여부 확인
    Optional<UserProfile> findByUser(User user);


}
