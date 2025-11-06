package ZGS.backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ZGS.backend.entity.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>{
    Page<Room> findAll(Pageable pageable);
    List<Room> findByType(String type);
}
