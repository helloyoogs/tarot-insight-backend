package com.tarot.insight;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // BaseEntity 시간 자동입력
@SpringBootApplication
public class InsightApplication {

	public static void main(String[] args) {
		SpringApplication.run(InsightApplication.class, args);
	}
}