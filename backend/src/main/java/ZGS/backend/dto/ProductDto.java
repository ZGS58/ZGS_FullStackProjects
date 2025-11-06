package ZGS.backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Integer price;
    private Integer stock;

}
