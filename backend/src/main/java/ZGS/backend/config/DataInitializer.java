package ZGS.backend.config;

import ZGS.backend.entity.Role;
import ZGS.backend.entity.User;
import ZGS.backend.repository.RoleRepository;
import ZGS.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 建立角色
        if (roleRepository.findByName("USER").isEmpty()) {
            roleRepository.save(Role.builder().name("USER").build());
        }
        if (roleRepository.findByName("ADMIN").isEmpty()) {
            roleRepository.save(Role.builder().name("ADMIN").build());
        }

        // 建立預設 admin 帳號
        if (userRepository.findByUsername("admin").isEmpty()) {
            Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
            Role userRole = roleRepository.findByName("USER").orElseThrow();

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setFullname("管理員");
            admin.setRoles(Set.of(adminRole, userRole));
            userRepository.save(admin);
        }

        // 建立預設 user 帳號
        if (userRepository.findByUsername("user").isEmpty()) {
            Role userRole = roleRepository.findByName("USER").orElseThrow();

            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmail("user@example.com");
            user.setFullname("一般使用者");
            user.setRoles(Set.of(userRole));
            userRepository.save(user);
        }
    }
}
