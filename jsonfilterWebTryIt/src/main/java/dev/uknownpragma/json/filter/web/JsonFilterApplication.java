package dev.uknownpragma.json.filter.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class JsonFilterApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(JsonFilterApplication.class, args);
	}

}
