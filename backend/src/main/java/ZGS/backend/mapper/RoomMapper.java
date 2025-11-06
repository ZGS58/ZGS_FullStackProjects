package ZGS.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import ZGS.backend.dto.RoomDto;
import ZGS.backend.entity.Room;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    Room toEntity(RoomDto dto);

    RoomDto toDto(Room entity);

    void updateEntityFormDto(RoomDto dto, @MappingTarget Room entity);
}
