package kkotdari.learn.batch;

import kkotdari.learn.batch.config.BatchConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BatchApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(BatchApplication.class, args);
		context.getBean(BatchConfig.class).runJob();
	}

}
