package ZGS.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating; // 1 ~ 5 ★
    private String comment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({ "bookings", "reviews", "registrations", "roles", "password" })
    private User user;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = true)
    @JsonIgnoreProperties({ "bookings", "reviews" })
    private Room room;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = true)
    @JsonIgnoreProperties({ "reviews" })
    private Product product;

}
