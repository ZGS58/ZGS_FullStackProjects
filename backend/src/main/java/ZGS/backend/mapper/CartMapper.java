package ZGS.backend.mapper;

import ZGS.backend.dto.CartDto;
import ZGS.backend.dto.CartItemDto;
import ZGS.backend.entity.Cart;
import ZGS.backend.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "items", source = "items")
    @Mapping(target = "totalPrice", expression = "java(cart.getTotalPrice())")
    @Mapping(target = "totalItems", expression = "java(cart.getTotalItems())")
    CartDto toDto(Cart cart);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productDescription", source = "product.description")
    @Mapping(target = "productImageUrl", source = "product.imageUrl")
    @Mapping(target = "productPrice", source = "product.price")
    @Mapping(target = "subtotal", expression = "java(cartItem.getSubtotal())")
    CartItemDto toItemDto(CartItem cartItem);
}
