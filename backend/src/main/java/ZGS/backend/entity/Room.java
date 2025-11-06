//房型
package ZGS.backend.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;
    private Double price;
    private String description;
    private Integer capacity; // 可容納人數

    @Builder.Default
    private Boolean available = true; // 是否可預訂

    @Builder.Default
    private Integer stock = 0; // 可預訂數量

    @OneToMany(mappedBy = "room")
    @JsonIgnore
    private List<Booking> bookings;

    @OneToMany(mappedBy = "room")
    @JsonIgnore
    private List<Review> reviews;

}
