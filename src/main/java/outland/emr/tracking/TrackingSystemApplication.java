package outland.emr.tracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TrackingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrackingSystemApplication.class, args);
	}

}
