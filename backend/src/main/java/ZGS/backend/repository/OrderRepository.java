package ZGS.backend.repository;

import ZGS.backend.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    Page<Order> findByUserId(Long userId, Pageable pageable);

    // 關鍵字搜尋：用戶名、配送地址、電話
    Page<Order> findByUser_UsernameContainingIgnoreCaseOrShippingAddressContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(
            String username, String shippingAddress, String phoneNumber, Pageable pageable);
}
