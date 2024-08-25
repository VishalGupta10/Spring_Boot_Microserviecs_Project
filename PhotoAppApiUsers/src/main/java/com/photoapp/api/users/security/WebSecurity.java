package com.photoapp.api.users.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.photoapp.api.users.service.UsersService;

@Configuration
@EnableWebSecurity
public class WebSecurity {
	
	//Why ctor based dependency injection and not by using autowired.
	private UsersService usersService;
	private Environment environment;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	//@Autowired - optional
	public WebSecurity(UsersService usersService, Environment environment, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.usersService = usersService;
		this.environment = environment;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	
	
	@Bean
	protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
		
		//Configure AuthenticationManagerBuilder
		AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
		authenticationManagerBuilder.userDetailsService(usersService).passwordEncoder(bCryptPasswordEncoder);
		AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
		
		//Create AuthenticationFilter
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(usersService, environment, authenticationManager);
		authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url.path"));
		http.csrf((csrf)-> csrf.disable());
		
		http.authorizeHttpRequests((authz) -> authz
		//.requestMatchers(new AntPathRequestMatcher("/**"))
		//.access(new WebExpressionAuthorizationManager("hasIpAddress('"+environment.getProperty("gateway.ip")+"')"))
		.requestMatchers(new AntPathRequestMatcher("/users", "POST"))
		.permitAll()
		.requestMatchers(new AntPathRequestMatcher("/users/**", "GET"))
		.permitAll()
		//To avoid 403 forbidden error and 404 not found so add authorization
		.requestMatchers(new AntPathRequestMatcher("/users/status/check", "GET"))
		.permitAll()
		.requestMatchers(new AntPathRequestMatcher("/actuator/**", "GET"))
		.permitAll())
		
		//.and()
		.addFilter(authenticationFilter)
		.authenticationManager(authenticationManager)
		.sessionManagement((session)->session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.disable()));
		return http.build();
	}

}
