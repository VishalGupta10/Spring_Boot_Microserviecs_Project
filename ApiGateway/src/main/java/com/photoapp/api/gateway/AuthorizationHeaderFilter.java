package com.photoapp.api.gateway;

import java.util.Base64;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config>{
	
	@Autowired
	private Environment env;
	
	public AuthorizationHeaderFilter() {
		super(Config.class);
	}
	
	public static class Config {
		// Put Configuration Here.
	}

	@Override
	public GatewayFilter apply(Config config) {
		//This exchange data type is used to read HTTP request and then it will read the Authorization header.
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
				return onError(exchange, "No Authorization Header", HttpStatus.UNAUTHORIZED);
			}
			String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
			String jwt = authorizationHeader.replace("Bearer ", "");
			if(!isJWTValid(jwt)) {
				return onError(exchange, "JWT Token is not valid", HttpStatus.UNAUTHORIZED);
			}
			return chain.filter(exchange);
		};
	}

	private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(httpStatus);
		return response.setComplete();
	}
	
	private boolean isJWTValid(String jwt) {
		//Here, we'll check that JWT token is valid or not.
		boolean returnValue = true;
		String subject = null;
		try {
			String tokenSecret = env.getProperty("token.secret");
			byte secretKeyBytes[] = Base64.getEncoder().encode(tokenSecret.getBytes());
			SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);
			JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
			subject = jwtParser.parseSignedClaims(jwt).getPayload().getSubject();
		} catch(Exception ex) {
			returnValue = false;
		}
		if(subject == null || subject.isEmpty()) {
			returnValue = false;
		}
		return returnValue;
	}

}
