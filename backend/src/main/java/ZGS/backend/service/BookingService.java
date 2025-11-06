package ZGS.backend.service;

import ZGS.backend.dto.BookingDto;
import ZGS.backend.entity.Booking;
import ZGS.backend.entity.Room;
import ZGS.backend.entity.User;
import ZGS.backend.repository.BookingRepository;
import ZGS.backend.repository.RoomRepository;
import ZGS.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    // 獲取用戶的所有訂房記錄

    public List<BookingDto> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId, Pageable.unpaged())
                .getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 創建訂房

    @Transactional
    public BookingDto createBooking(Long userId, Long roomId, LocalDate checkIn, LocalDate checkOut,
            Integer guestCount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("房間不存在"));

        // 檢查房間是否可預訂
        if (room.getAvailable() != null && !room.getAvailable()) {
            throw new IllegalArgumentException("此房型暫不開放預訂");
        }

        // 檢查庫存
        if (room.getStock() != null && room.getStock() <= 0) {
            throw new IllegalArgumentException("此房型已無剩餘房間");
        }

        // 檢查容納人數
        if (guestCount != null && room.getCapacity() != null && guestCount > room.getCapacity()) {
            throw new IllegalArgumentException("入住人數超過房型容納上限（" + room.getCapacity() + "人）");
        }

        // 計算天數和總價
        long days = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (days <= 0) {
            throw new IllegalArgumentException("退房日期必須在入住日期之後");
        }

        Double totalPrice = room.getPrice() * days;

        Booking booking = Booking.builder()
                .checkIn(checkIn)
                .checkOut(checkOut)
                .totalPrice(totalPrice)
                .status("PENDING")
                .guestCount(guestCount)
                .user(user)
                .room(room)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        // 扣減房間庫存
        if (room.getStock() != null) {
            room.setStock(room.getStock() - 1);
            roomRepository.save(room);
        }

        return convertToDto(savedBooking);
    }

    // 取消訂房（只有本人可以取消）

    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("訂房記錄不存在"));

        if (!booking.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("無權取消此訂房");
        }

        // 只有 PENDING 或 CONFIRMED 狀態可以取消
        if (!"PENDING".equals(booking.getStatus()) && !"CONFIRMED".equals(booking.getStatus())) {
            throw new IllegalArgumentException("此訂房狀態無法取消");
        }

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);

        // 恢復房間庫存
        Room room = booking.getRoom();
        if (room.getStock() != null) {
            room.setStock(room.getStock() + 1);
            roomRepository.save(room);
        }
    }

    // 獲取所有訂房記錄（管理員）- 分頁

    public Page<BookingDto> getAllBookings(Pageable pageable) {
        return bookingRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    public Page<BookingDto> searchAllBookings(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBookings(pageable);
        }
        String k = keyword.trim();
        return bookingRepository
                .findByUser_UsernameContainingIgnoreCaseOrRoom_NameContainingIgnoreCaseOrStatusContainingIgnoreCase(k,
                        k,
                        k, pageable)
                .map(this::convertToDto);
    }

    // 更新訂房狀態（管理員）

    @Transactional
    public BookingDto updateBookingStatus(Long bookingId, String newStatus) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("訂房記錄不存在"));

        // 驗證狀態值
        if (!isValidStatus(newStatus)) {
            throw new IllegalArgumentException("無效的訂房狀態");
        }

        booking.setStatus(newStatus);
        Booking updatedBooking = bookingRepository.save(booking);
        return convertToDto(updatedBooking);
    }

    // 刪除訂房記錄（管理員）

    @Transactional
    public void deleteBooking(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new RuntimeException("訂房記錄不存在");
        }
        bookingRepository.deleteById(bookingId);
    }

    // 驗證訂房狀態是否有效

    private boolean isValidStatus(String status) {
        return status != null &&
                (status.equals("PENDING") || status.equals("CONFIRMED") ||
                        status.equals("CANCELLED") || status.equals("COMPLETED"));
    }

    // 轉換為 DTO

    private BookingDto convertToDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .checkIn(booking.getCheckIn())
                .checkOut(booking.getCheckOut())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .userId(booking.getUser().getId())
                .username(booking.getUser().getUsername())
                .roomId(booking.getRoom().getId())
                .roomName(booking.getRoom().getName())
                .build();
    }
}
