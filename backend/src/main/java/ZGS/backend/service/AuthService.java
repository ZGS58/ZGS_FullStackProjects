package ZGS.backend.service;

import ZGS.backend.dto.UserDto;
import ZGS.backend.entity.Role;
import ZGS.backend.entity.User;
import ZGS.backend.repository.RoleRepository;
import ZGS.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    // 用戶註冊

    @Transactional
    public UserDto register(String username, String email, String fullname, String password) {
        // 檢查使用者名稱是否已存在
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("使用者名稱已被使用");
        }

        // 檢查 email 是否已存在
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("電子郵件已被使用");
        }

        // 建立新用戶
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFullname(fullname);
        user.setPassword(passwordEncoder.encode(password));

        // 分配 USER 角色
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER 角色不存在"));
        user.setRoles(Set.of(userRole));

        // 儲存用戶
        User savedUser = userRepository.save(user);

        // 轉換為 DTO
        return convertToDto(savedUser);
    }
    // 檢查使用者名稱是否可用

    public boolean isUsernameAvailable(String username) {
        return userRepository.findByUsername(username).isEmpty();
    }

    // 檢查電子郵件是否可用

    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    // 轉換 User 為 UserDto
    
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullname(user.getFullname());
        return dto;
    }
}
