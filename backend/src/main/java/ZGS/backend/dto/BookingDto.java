package ZGS.backend.dto;

import java.time.LocalDate;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {

    private Long id;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Double totalPrice;
    private String status;
    private Integer guestCount;
    private Long userId;
    private Long roomId;
    private String roomName;
    private String username;
}
