package ZGS.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import ZGS.backend.dto.ProductDto;
import ZGS.backend.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDto toDto(Product product);

    @Mapping(target = "reviews", ignore = true)
    Product toEntity(ProductDto dto);

    @Mapping(target = "reviews", ignore = true)
    void updateEntityFormDto(ProductDto dto, @MappingTarget Product entity);
}