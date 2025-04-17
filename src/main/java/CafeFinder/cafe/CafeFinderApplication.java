package CafeFinder.cafe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CafeFinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(CafeFinderApplication.class, args);
    }

}
