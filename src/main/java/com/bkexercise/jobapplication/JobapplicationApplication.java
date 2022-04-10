package com.bkexercise.jobapplication;

import com.bkexercise.jobapplication.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class JobapplicationApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobapplicationApplication.class, args);
	}

}
