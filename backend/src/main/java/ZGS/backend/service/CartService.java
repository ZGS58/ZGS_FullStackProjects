package ZGS.backend.service;

import ZGS.backend.dto.CartDto;
import ZGS.backend.entity.Cart;
import ZGS.backend.entity.CartItem;
import ZGS.backend.entity.Product;
import ZGS.backend.entity.User;
import ZGS.backend.mapper.CartMapper;
import ZGS.backend.repository.CartItemRepository;
import ZGS.backend.repository.CartRepository;
import ZGS.backend.repository.ProductRepository;
import ZGS.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    // 獲取或創建用戶的購物車
    @Transactional
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("使用者不存在"));
                    Cart cart = Cart.builder()
                            .user(user)
                            .items(new ArrayList<>())
                            .build();
                    return cartRepository.save(cart);
                });
    }

    // 添加商品到購物車
    @Transactional
    public CartDto addToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        // 檢查購物車中是否已有該商品
        CartItem existingItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElse(null);

        if (existingItem != null) {
            // 更新數量
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            // 新增商品
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .build();
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        return cartMapper.toDto(cart);
    }

    // 更新購物車商品數量
    @Transactional
    public CartDto updateCartItemQuantity(Long userId, Long cartItemId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("購物車項目不存在"));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("無權限操作此購物車項目");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
            cart.getItems().remove(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }

        return cartMapper.toDto(cart);
    }

    // 從購物車移除商品
    @Transactional
    public CartDto removeFromCart(Long userId, Long cartItemId) {
        Cart cart = getOrCreateCart(userId);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("購物車項目不存在"));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("無權限操作此購物車項目");
        }

        cart.getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        return cartMapper.toDto(cart);
    }

    // 清空購物車
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("購物車不存在"));
        // 直接刪除整個購物車
        cartRepository.delete(cart);
    }

    // 獲取用戶購物車
    public CartDto getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return cartMapper.toDto(cart);
    }

    // 管理員獲取所有購物車
    public List<CartDto> getAllCarts() {
        return cartRepository.findAll().stream()
                .map(cartMapper::toDto)
                .collect(Collectors.toList());
    }
}
