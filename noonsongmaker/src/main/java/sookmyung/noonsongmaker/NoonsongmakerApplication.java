package sookmyung.noonsongmaker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NoonsongmakerApplication {

	public static void main(String[] args) {
		SpringApplication.run(NoonsongmakerApplication.class, args);
	}

}
