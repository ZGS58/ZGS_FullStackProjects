package ZGS.backend.controller;

import ZGS.backend.BaseResponse;
import ZGS.backend.dto.OrderDto;
import ZGS.backend.entity.Order;
import ZGS.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 從購物車建立訂單
     */
    @PostMapping("/checkout")
    public ResponseEntity<BaseResponse<OrderDto>> checkout(
            @RequestBody Map<String, String> request,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.error("請先登入"));
        }

        try {
            String username = authentication.getName();
            String shippingAddress = request.get("shippingAddress");
            String phoneNumber = request.get("phoneNumber");
            String note = request.getOrDefault("note", "");

            if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(BaseResponse.error("請填寫配送地址"));
            }

            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(BaseResponse.error("請填寫聯絡電話"));
            }

            OrderDto order = orderService.createOrderFromCart(username, shippingAddress, phoneNumber, note);
            return ResponseEntity.ok(BaseResponse.ok(order));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.error(e.getMessage()));
        }
    }

    /**
     * 取得當前使用者的訂單列表
     */
    @GetMapping("/my-orders")
    public ResponseEntity<BaseResponse<List<OrderDto>>> getMyOrders(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.error("請先登入"));
        }

        String username = authentication.getName();
        List<OrderDto> orders = orderService.getUserOrders(username);
        return ResponseEntity.ok(BaseResponse.ok(orders));
    }

    /**
     * 取得所有訂單（分頁）- 管理員用
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Page<OrderDto>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        Page<OrderDto> orders = orderService.searchAllOrders(keyword, PageRequest.of(page, size));
        return ResponseEntity.ok(BaseResponse.ok(orders));
    }

    /**
     * 取得訂單詳情
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<BaseResponse<OrderDto>> getOrderById(
            @PathVariable Long orderId,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.error("請先登入"));
        }

        try {
            OrderDto order = orderService.getOrderById(orderId);

            // 檢查是否為本人或管理員
            String username = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!order.getUsername().equals(username) && !isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(BaseResponse.error("無權查看此訂單"));
            }

            return ResponseEntity.ok(BaseResponse.ok(order));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error(e.getMessage()));
        }
    }

    /**
     * 更新訂單狀態 - 管理員用
     */
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<OrderDto>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request) {

        try {
            String statusStr = request.get("status");
            Order.OrderStatus status = Order.OrderStatus.valueOf(statusStr);

            OrderDto updatedOrder = orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(BaseResponse.ok(updatedOrder));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error("無效的訂單狀態"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error(e.getMessage()));
        }
    }

    /**
     * 取消訂單
     */
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<BaseResponse<String>> cancelOrder(
            @PathVariable Long orderId,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.error("請先登入"));
        }

        try {
            String username = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            orderService.cancelOrder(orderId, username, isAdmin);
            return ResponseEntity.ok(BaseResponse.ok("訂單已取消"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(BaseResponse.error(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error(e.getMessage()));
        }
    }

    /**
     * 刪除訂單 - 管理員用
     */
    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<String>> deleteOrder(@PathVariable Long orderId) {
        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok(BaseResponse.ok("訂單已刪除"));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error(e.getMessage()));
        }
    }
}
