package com.photoapp.api.users.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.photoapp.api.users.shared.UserDto;

public interface UsersService extends UserDetailsService{
	
	public UserDto createUser(UserDto userDetails);
	public UserDto getUserDetailsByEmail(String email);
	public UserDto getUserByUserId(String userId);

}
