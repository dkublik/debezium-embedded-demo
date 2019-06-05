package pl.dk.debeziumdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DebeziumDemoApp {

	public static void main(String[] args) {
		SpringApplication.run(DebeziumDemoApp.class, args);
	}
}
