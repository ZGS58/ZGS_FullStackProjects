package ZGS.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ZGS.backend.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByUserId(Long userId, Pageable pageable);

    // 關鍵字搜尋：用戶名、房型名稱、狀態
    Page<Booking> findByUser_UsernameContainingIgnoreCaseOrRoom_NameContainingIgnoreCaseOrStatusContainingIgnoreCase(
            String username, String roomName, String status, Pageable pageable);
}
