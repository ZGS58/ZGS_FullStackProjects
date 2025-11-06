package ZGS.backend.dto;

import ZGS.backend.entity.Order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private Long id;
    private Long userId;
    private String username;
    private List<OrderItemDto> items;
    private Double totalPrice;
    private Integer totalItems;
    private OrderStatus status;
    private String shippingAddress;
    private String phoneNumber;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
