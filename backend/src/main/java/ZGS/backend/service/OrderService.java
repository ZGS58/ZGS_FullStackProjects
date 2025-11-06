package ZGS.backend.service;

import ZGS.backend.dto.OrderDto;
import ZGS.backend.entity.*;
import ZGS.backend.mapper.OrderMapper;
import ZGS.backend.repository.CartRepository;
import ZGS.backend.repository.OrderRepository;
import ZGS.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final OrderMapper orderMapper;

    // 從購物車建立訂單

    @Transactional
    public OrderDto createOrderFromCart(String username, String shippingAddress, String phoneNumber, String note) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("購物車不存在"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("購物車是空的");
        }

        // 檢查所有產品庫存是否足夠
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (product.getStock() != null && product.getStock() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("商品「" + product.getName() + "」庫存不足");
            }
        }

        // 建立訂單
        Order order = Order.builder()
                .user(user)
                .totalPrice(cart.getTotalPrice())
                .totalItems(cart.getTotalItems())
                .shippingAddress(shippingAddress)
                .phoneNumber(phoneNumber)
                .note(note)
                .status(Order.OrderStatus.PENDING)
                .build();

        // 將購物車項目轉為訂單項目，並扣減庫存
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    Product product = cartItem.getProduct();

                    // 扣減庫存
                    if (product.getStock() != null) {
                        product.setStock(product.getStock() - cartItem.getQuantity());
                    }

                    OrderItem item = OrderItem.builder()
                            .order(order)
                            .product(product)
                            .productName(product.getName())
                            .productPrice((double) product.getPrice())
                            .quantity(cartItem.getQuantity())
                            .subtotal(cartItem.getSubtotal())
                            .build();
                    return item;
                })
                .collect(Collectors.toList());

        order.setItems(orderItems);

        // 儲存訂單
        Order savedOrder = orderRepository.save(order);

        // 清空購物車
        cart.getItems().clear();
        cartRepository.save(cart);

        return orderMapper.toDto(savedOrder);
    }

    // 取得使用者的所有訂單

    public List<OrderDto> getUserOrders(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        return orderRepository.findByUserId(user.getId())
                .stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    // 取得所有訂單（分頁）- 管理員用

    public Page<OrderDto> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderMapper::toDto);
    }

    public Page<OrderDto> searchAllOrders(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllOrders(pageable);
        }
        String k = keyword.trim();
        Page<Order> page = orderRepository
                .findByUser_UsernameContainingIgnoreCaseOrShippingAddressContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(
                        k, k, k, pageable);

        // 額外處理：若關鍵字為數字，嘗試比對訂單ID
        try {
            Long id = Long.parseLong(k);
            Order byId = orderRepository.findById(id).orElse(null);
            if (byId != null && !page.getContent().contains(byId)) {
                // 合併單筆結果到分頁內容（簡易處理：放到第一筆）
                List<Order> merged = new java.util.ArrayList<>(page.getContent());
                merged.add(0, byId);
                Page<Order> newPage = new org.springframework.data.domain.PageImpl<>(merged, pageable,
                        page.getTotalElements() + 1);
                return newPage.map(orderMapper::toDto);
            }
        } catch (NumberFormatException ignored) {
        }

        return page.map(orderMapper::toDto);
    }

    // 取得訂單詳情

    public OrderDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("訂單不存在"));
        return orderMapper.toDto(order);
    }

    // 更新訂單狀態 - 管理員用

    @Transactional
    public OrderDto updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("訂單不存在"));

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);

        return orderMapper.toDto(updatedOrder);
    }

    // 取消訂單 - 使用者或管理員

    @Transactional
    public void cancelOrder(Long orderId, String username, boolean isAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("訂單不存在"));

        // 檢查權限：必須是本人或管理員
        if (!order.getUser().getUsername().equals(username) && !isAdmin) {
            throw new IllegalArgumentException("無權取消此訂單");
        }

        // 只有 PENDING 狀態可以取消
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new IllegalArgumentException("此訂單狀態無法取消");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);

        // 恢復產品庫存
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            if (product != null && product.getStock() != null) {
                product.setStock(product.getStock() + item.getQuantity());
            }
        }
    }

    // 刪除訂單 - 管理員用

    @Transactional
    public void deleteOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new RuntimeException("訂單不存在");
        }
        orderRepository.deleteById(orderId);
    }
}
