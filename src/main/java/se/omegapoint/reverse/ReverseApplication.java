package se.omegapoint.reverse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@EntityScan("se.omegapoint.reverse.model")
@ComponentScan({ "se.omegapoint.reverse"})
public class ReverseApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReverseApplication.class, args);
	}
}