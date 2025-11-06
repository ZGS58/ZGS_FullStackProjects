package ZGS.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {
    private Long id;
    private int rating;
    private String comment;
    private String username;
    private Long userId;
    private Long productId;
    private Long roomId;
    private LocalDateTime createdAt;
}
