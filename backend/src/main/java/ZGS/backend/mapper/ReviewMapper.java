package ZGS.backend.mapper;

import ZGS.backend.dto.ReviewDto;
import ZGS.backend.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "room.id", target = "roomId")
    @Mapping(target = "createdAt", ignore = true)
    ReviewDto toDto(Review review);
}
