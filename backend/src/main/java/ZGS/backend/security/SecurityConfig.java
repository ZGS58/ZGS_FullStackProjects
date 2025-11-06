package ZGS.backend.security;

import ZGS.backend.entity.User;
import ZGS.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // 允許跨域預檢請求
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 開放認證相關 API（註冊、登入）
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/debug/**").permitAll()

                        // 開放前台 API（不需登入）
                        .requestMatchers(HttpMethod.GET, "/api/product/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/rooms/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/contact").permitAll()

                        // 評論相關 API（GET 可公開，POST/DELETE 需要登入）
                        .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()

                        // 管理員專用 API
                        .requestMatchers(HttpMethod.GET, "/api/contact/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/contact/**").hasRole("ADMIN")

                        // 購物車 API（需要登入）- 已在 anyRequest().authenticated() 中處理

                        // 開放 H2 Console
                        .requestMatchers("/h2-console/**").permitAll()

                        // 其他 API 需要認證
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            // 檢查是否是 API 請求
                            String requestURI = request.getRequestURI();
                            if (requestURI.startsWith("/api/")) {
                                // API 請求返回 JSON 錯誤
                                response.setContentType("application/json;charset=UTF-8");
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                PrintWriter out = response.getWriter();
                                out.print("{\"status\":\"ERROR\",\"message\":\"請先登入\"}");
                                out.flush();
                            } else {
                                // 非 API 請求重定向到登入頁面
                                response.sendRedirect("/login");
                            }
                        }))
                .formLogin(form -> form
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler((request, response, authentication) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_OK);

                            // 獲取用戶角色
                            String username = authentication.getName();
                            User user = userRepository.findByUsername(username).orElse(null);
                            String rolesJson = "[]";
                            if (user != null && user.getRoles() != null) {
                                rolesJson = user.getRoles().stream()
                                        .map(role -> {
                                            // 確保角色名稱有 ROLE_ 前綴
                                            String roleName = role.getName();
                                            if (!roleName.startsWith("ROLE_")) {
                                                roleName = "ROLE_" + roleName;
                                            }
                                            return "\"" + roleName + "\"";
                                        })
                                        .collect(Collectors.joining(",", "[", "]"));
                            }

                            PrintWriter out = response.getWriter();
                            out.print(
                                    "{\"success\":true,\"username\":\"" + username + "\",\"roles\":" + rolesJson + "}");
                            out.flush();
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            PrintWriter out = response.getWriter();
                            out.print("{\"success\":false,\"message\":\"帳號或密碼錯誤\"}");
                            out.flush();
                        })
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_OK);
                            PrintWriter out = response.getWriter();
                            out.print("{\"success\":true}");
                            out.flush();
                        })
                        .permitAll());

        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // CORS(解決跨預請求問題)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
