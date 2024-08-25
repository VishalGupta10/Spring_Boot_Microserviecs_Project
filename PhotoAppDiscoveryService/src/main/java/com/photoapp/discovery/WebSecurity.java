package com.photoapp.discovery;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurity {
	
	public SecurityFilterChain configure(HttpSecurity http) throws Exception {
		http.csrf((csrf) -> csrf.disable());
		http.authorizeHttpRequests((requests) -> 
		requests.anyRequest().authenticated()
		//.requestMatchers(new AntPathRequestMatcher("/eureka/**"))
		//.permitAll()
		)
		.httpBasic(Customizer.withDefaults());
		return http.build();
	}

}
