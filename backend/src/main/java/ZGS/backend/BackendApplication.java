package ZGS.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
		System.out.println("Resort Backend 已啟動成功！");
        System.out.println("API 伺服器：http://localhost:8080");
        System.out.println("H2 Console：http://localhost:8080/h2-console");
        System.out.println("管理員帳號：admin / admin123");
	}

}
