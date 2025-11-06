package ZGS.backend.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ZGS.backend.BaseResponse;
import ZGS.backend.dto.RoomDto;
import ZGS.backend.entity.Room;
import ZGS.backend.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<Room>>> getAll() {
        List<Room> roomList = roomService.getAll();
        return ResponseEntity.ok(BaseResponse.ok(roomList));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<Room>> getById(@PathVariable Long id) {
        Room room = roomService.getById(id)
                .orElseThrow(() -> new RuntimeException("找不到該房型"));
        return ResponseEntity.ok(BaseResponse.ok(room));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Room>> create(@Valid @RequestBody RoomDto dto) {
        Room created = roomService.create(dto);
        return ResponseEntity.ok(BaseResponse.ok(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Room>> update(@PathVariable Long id, @Valid @RequestBody RoomDto dto) {
        Room updated = roomService.update(id, dto);
        return ResponseEntity.ok(BaseResponse.ok(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<String>> delete(@PathVariable Long id) {
        roomService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok("房型刪除成功"));
    }

    @GetMapping("/paged")
    public ResponseEntity<BaseResponse<Page<Room>>> getPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<Room> roomPage = roomService.getPaged(PageRequest.of(page, size));
        return ResponseEntity.ok(BaseResponse.ok(roomPage));
    }
}
