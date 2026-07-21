package com.phetolo.PersonalFinance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.phetolo.PersonalFinance.dto.AuthResponse;
import com.phetolo.PersonalFinance.dto.UserDTO;
import com.phetolo.PersonalFinance.exception.UserNotFoundException;
import com.phetolo.PersonalFinance.mapper.UserMapper;
import com.phetolo.PersonalFinance.model.LoginRequest;
import com.phetolo.PersonalFinance.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
	private final UserService userService;
	
	
	@PostMapping("/register")
	public ResponseEntity<UserDTO> register(@RequestBody UserDTO userdto) throws UserNotFoundException {
		return ResponseEntity.ok(userService.addUser(UserMapper.mapToEntity(userdto)));
	}
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
		return ResponseEntity.ok(userService.login(request));
	}
	
}
