package ZGS.backend.controller;

import ZGS.backend.BaseResponse;
import ZGS.backend.dto.CartDto;
import ZGS.backend.entity.User;
import ZGS.backend.repository.UserRepository;
import ZGS.backend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    // 獲取用戶購物車
    @GetMapping
    public ResponseEntity<BaseResponse<CartDto>> getCart(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.error("請先登入"));
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        CartDto cart = cartService.getCart(user.getId());
        return ResponseEntity.ok(BaseResponse.ok(cart));
    }

    // 添加商品到購物車
    @PostMapping("/add")
    public ResponseEntity<BaseResponse<CartDto>> addToCart(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.error("請先登入"));
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        Long productId = Long.valueOf(request.get("productId").toString());
        Integer quantity = Integer.valueOf(request.get("quantity").toString());

        CartDto cart = cartService.addToCart(user.getId(), productId, quantity);
        return ResponseEntity.ok(BaseResponse.ok(cart));
    }

    // 更新購物車商品數量
    @PutMapping("/item/{cartItemId}")
    public ResponseEntity<BaseResponse<CartDto>> updateCartItem(
            @PathVariable Long cartItemId,
            @RequestBody Map<String, Integer> request,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.error("請先登入"));
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        Integer quantity = request.get("quantity");
        CartDto cart = cartService.updateCartItemQuantity(user.getId(), cartItemId, quantity);
        return ResponseEntity.ok(BaseResponse.ok(cart));
    }

    // 從購物車移除商品
    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<BaseResponse<CartDto>> removeFromCart(
            @PathVariable Long cartItemId,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.error("請先登入"));
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        CartDto cart = cartService.removeFromCart(user.getId(), cartItemId);
        return ResponseEntity.ok(BaseResponse.ok(cart));
    }

    // 清空購物車
    @DeleteMapping("/clear")
    public ResponseEntity<BaseResponse<String>> clearCart(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.error("請先登入"));
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        cartService.clearCart(user.getId());
        return ResponseEntity.ok(BaseResponse.ok("購物車已清空"));
    }

    // 管理員 - 獲取所有購物車
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<List<CartDto>>> getAllCarts() {
        List<CartDto> carts = cartService.getAllCarts();
        return ResponseEntity.ok(BaseResponse.ok(carts));
    }

    // 管理員 - 清空特定用戶購物車
    @DeleteMapping("/admin/clear/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<String>> clearUserCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(BaseResponse.ok("用戶購物車已清空"));
    }
}
