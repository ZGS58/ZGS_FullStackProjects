package ZGS.backend.repository;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ZGS.backend.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Page<Role> findAll(Pageable pageable);
    Optional<Role> findByName(String name);
}
