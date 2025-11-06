package ZGS.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ZGS.backend.dto.BookingDto;
import ZGS.backend.entity.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "userId", source = "user.id")
    BookingDto toDto(Booking booking);

    @Mapping(target = "room", ignore = true)
    @Mapping(target = "user", ignore = true)
    Booking toEntity(BookingDto dto);
}