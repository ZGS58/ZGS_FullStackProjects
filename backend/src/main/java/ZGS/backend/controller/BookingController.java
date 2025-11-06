package ZGS.backend.controller;

import ZGS.backend.BaseResponse;
import ZGS.backend.dto.BookingDto;
import ZGS.backend.entity.User;
import ZGS.backend.repository.UserRepository;
import ZGS.backend.service.BookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequiredArgsConstructor
public class BookingController {

        private final BookingService bookingService;
        private final UserRepository userRepository;

        // 獲取使用者的所有訂房記錄
        @GetMapping("/my")
        public ResponseEntity<BaseResponse<List<BookingDto>>> getMyBookings(Authentication authentication) {
                if (authentication == null || !authentication.isAuthenticated()) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(BaseResponse.error("請先登入"));
                }

                String username = authentication.getName();
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("使用者不存在"));

                List<BookingDto> bookings = bookingService.getUserBookings(user.getId());
                return ResponseEntity.ok(BaseResponse.ok(bookings));
        }

        // 新增訂房
        @PostMapping("/room/{roomId}")
        public ResponseEntity<BaseResponse<BookingDto>> createBooking(
                        @PathVariable Long roomId,
                        @RequestBody Map<String, Object> request,
                        Authentication authentication) {

                if (authentication == null || !authentication.isAuthenticated()) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(BaseResponse.error("請先登入才能訂房"));
                }

                try {
                        String username = authentication.getName();
                        User user = userRepository.findByUsername(username)
                                        .orElseThrow(() -> new RuntimeException("使用者不存在"));

                        LocalDate checkIn = LocalDate.parse((String) request.get("checkIn"));
                        LocalDate checkOut = LocalDate.parse((String) request.get("checkOut"));

                        // 提取 guestCount（可選參數）
                        Integer guestCount = null;
                        if (request.containsKey("guestCount") && request.get("guestCount") != null) {
                                Object guestCountObj = request.get("guestCount");
                                if (guestCountObj instanceof Number) {
                                        guestCount = ((Number) guestCountObj).intValue();
                                }
                        }

                        BookingDto booking = bookingService.createBooking(user.getId(), roomId, checkIn, checkOut,
                                        guestCount);
                        return ResponseEntity.ok(BaseResponse.ok(booking));

                } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest()
                                        .body(BaseResponse.error(e.getMessage()));
                }
        }

        // 取消訂房
        @PutMapping("/{bookingId}/cancel")
        public ResponseEntity<BaseResponse<String>> cancelBooking(
                        @PathVariable Long bookingId,
                        Authentication authentication) {

                if (authentication == null || !authentication.isAuthenticated()) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(BaseResponse.error("請先登入"));
                }

                try {
                        String username = authentication.getName();
                        User user = userRepository.findByUsername(username)
                                        .orElseThrow(() -> new RuntimeException("使用者不存在"));

                        bookingService.cancelBooking(bookingId, user.getId());
                        return ResponseEntity.ok(BaseResponse.ok("訂房已取消"));

                } catch (IllegalArgumentException e) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                        .body(BaseResponse.error(e.getMessage()));
                }
        }

        // 管理員 API - 獲取所有訂房記錄（分頁）
        @GetMapping("/admin/all")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<BaseResponse<Page<BookingDto>>> getAllBookings(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(required = false) String keyword) {

                Page<BookingDto> bookingPage = bookingService.searchAllBookings(keyword, PageRequest.of(page, size));
                return ResponseEntity.ok(BaseResponse.ok(bookingPage));
        }

        // 管理員 API - 更新訂房狀態
        @PutMapping("/admin/{bookingId}/status")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<BaseResponse<BookingDto>> updateBookingStatus(
                        @PathVariable Long bookingId,
                        @RequestBody Map<String, String> request) {

                try {
                        String newStatus = request.get("status");
                        BookingDto booking = bookingService.updateBookingStatus(bookingId, newStatus);
                        return ResponseEntity.ok(BaseResponse.ok(booking));

                } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest()
                                        .body(BaseResponse.error(e.getMessage()));
                }
        }

        // 管理員 API - 刪除訂房記錄
        @DeleteMapping("/admin/{bookingId}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<BaseResponse<String>> deleteBooking(@PathVariable Long bookingId) {
                try {
                        bookingService.deleteBooking(bookingId);
                        return ResponseEntity.ok(BaseResponse.ok("訂房記錄已刪除"));

                } catch (RuntimeException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(BaseResponse.error(e.getMessage()));
                }
        }
}
