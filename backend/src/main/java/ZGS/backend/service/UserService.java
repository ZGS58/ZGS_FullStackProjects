package ZGS.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ZGS.backend.dto.UserDto;
import ZGS.backend.entity.Role;
import ZGS.backend.entity.User;
import ZGS.backend.mapper.UserMapper;
import ZGS.backend.repository.RoleRepository;
import ZGS.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User update(Long id, UserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));

        // 更新基本資料
        userMapper.updateEntityFromDto(dto, user);

        // 更新角色（如果有提供）
        if (dto.getRoleNames() != null && !dto.getRoleNames().isEmpty()) {
            Set<Role> roles = dto.getRoleNames().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("角色不存在: " + roleName)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        return userRepository.save(user);
    }

    public void updatePassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public Page<User> getPaged(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> searchPaged(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return userRepository.findAll(pageable);
        }
        String k = keyword.trim();
        return userRepository
                .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrFullnameContainingIgnoreCase(k, k, k,
                        pageable);
    }
}
