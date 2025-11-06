package ZGS.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ZGS.backend.dto.RoomDto;
import ZGS.backend.entity.Room;
import ZGS.backend.mapper.RoomMapper;
import ZGS.backend.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    public List<Room> getAll() {
        return roomRepository.findAll();
    }

    public Optional<Room> getById(Long id) {
        return roomRepository.findById(id);
    }

    public Room create(RoomDto dto) {
        Room room = roomMapper.toEntity(dto);
        return roomRepository.save(room);
    }

    public Room update(Long id, RoomDto dto) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("房型不存在"));
        roomMapper.updateEntityFormDto(dto, room);
        return roomRepository.save(room);
    }

    public void delete(Long id) {
        roomRepository.deleteById(id);
    }

    public Page<Room> getPaged(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }
}
