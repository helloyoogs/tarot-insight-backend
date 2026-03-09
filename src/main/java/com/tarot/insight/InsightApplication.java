package com.tarot.insight;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // JPA Auditing 활성화
@SpringBootApplication(excludeName = {
		"org.redisson.spring.starter.RedissonAutoConfiguration",
		"org.redisson.spring.starter.RedissonAutoConfigurationV2"
})
public class InsightApplication {
	public static void main(String[] args) {
		SpringApplication.run(InsightApplication.class, args);
	}
}