package ZGS.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDto {
    private Long id;
    private String name;
    private String type;
    private Double price;
    private String description;
    private Integer capacity;
    private Boolean available;
    private Integer stock;
}
