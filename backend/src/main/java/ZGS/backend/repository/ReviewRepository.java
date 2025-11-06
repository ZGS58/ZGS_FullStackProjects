package ZGS.backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ZGS.backend.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>{
    Page<Review> findAll(Pageable pageable);
    List<Review> findByUserId(Long userId);
    List<Review> findByProductId(Long productId);
    List<Review> findByRoomId(Long roomId);
}
