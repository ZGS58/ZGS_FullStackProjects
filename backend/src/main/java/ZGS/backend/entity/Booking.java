//預約
package ZGS.backend.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate checkIn;
    private LocalDate checkOut;
    private Double totalPrice;
    private String status; // PENDING(待處理), CONFIRMED(已確認), CANCELLED(已取消)
    private Integer guestCount; // 入住人數

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({ "bookings", "reviews", "registrations", "roles", "password" })
    private User user;

    @ManyToOne
    @JoinColumn(name = "room_id")
    @JsonIgnoreProperties({ "bookings", "reviews" })
    private Room room;

}
