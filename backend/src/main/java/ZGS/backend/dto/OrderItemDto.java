package ZGS.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private Long id;
    private Long productId;
    private String productName;
    private Double productPrice;
    private Integer quantity;
    private Double subtotal;
}
