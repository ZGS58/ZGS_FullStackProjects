package ZGS.backend.repository;

import ZGS.backend.entity.User;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findAll(Pageable pageable);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    // 確認email是否存在
    boolean existsByEmail(String email);

    // 關鍵字搜尋：使用者名稱、Email、姓名
    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrFullnameContainingIgnoreCase(
            String username, String email, String fullname, Pageable pageable);
}