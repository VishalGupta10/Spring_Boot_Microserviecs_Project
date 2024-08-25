package com.photoapp.api.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import reactor.core.publisher.Mono;

@Configuration
public class GlobalFiltersConfiguration {
	
	final Logger logger = LoggerFactory.getLogger(GlobalFiltersConfiguration.class);
	
	@Order(1)
	@Bean
	GlobalFilter secondPreFilter() {
		return (exhange, chain) -> {
			logger.info("My Second Global Pre-Filter is executed...");
			return chain.filter(exhange).then(Mono.fromRunnable(() -> {
				logger.info("My Third Global Post-Filter is executed...");
			}));
		};
	}
	
	@Order(2)
	@Bean
	GlobalFilter thirdPreFilter() {
		return (exhange, chain) -> {
			logger.info("My Third Global Pre-Filter is executed...");
			return chain.filter(exhange).then(Mono.fromRunnable(() -> {
				logger.info("My Second Global Post-Filter is executed...");
			}));
		};
	}
	
	@Order(3)
	@Bean
	GlobalFilter fourthPreFilter() {
		return (exhange, chain) -> {
			logger.info("My Fourth Global Pre-Filter is executed...");
			return chain.filter(exhange).then(Mono.fromRunnable(() -> {
				logger.info("My First Global Post-Filter is executed...");
			}));
		};
	}

}
