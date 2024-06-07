package com.currencyratetracker.crt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.currencyratetracker.crt.config.GlobalExceptionHandler;

@SpringBootApplication
@Import(GlobalExceptionHandler.class)
public class CrtApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrtApplication.class, args);
	}

}
		