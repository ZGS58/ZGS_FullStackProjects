package ZGS.backend.mapper;

import ZGS.backend.dto.OrderDto;
import ZGS.backend.dto.OrderItemDto;
import ZGS.backend.entity.Order;
import ZGS.backend.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    OrderDto toDto(Order order);

    @Mapping(source = "product.id", target = "productId")
    OrderItemDto toItemDto(OrderItem item);
}
