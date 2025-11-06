package ZGS.backend.dto;

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
public class CartDto {
    private Long id;
    private Long userId;
    private String username;
    private List<CartItemDto> items;
    private Double totalPrice;
    private Integer totalItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
