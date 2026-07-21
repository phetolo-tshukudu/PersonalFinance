package com.phetolo.PersonalFinance.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.phetolo.PersonalFinance.dto.UserDTO;
import com.phetolo.PersonalFinance.enums.Role;
import com.phetolo.PersonalFinance.exception.AccessDeniedException;
import com.phetolo.PersonalFinance.exception.UserNotFoundException;
import com.phetolo.PersonalFinance.mapper.UserMapper;
import com.phetolo.PersonalFinance.model.User;
import com.phetolo.PersonalFinance.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	
	@GetMapping
	public ResponseEntity<List<UserDTO>> getAllUsers(@AuthenticationPrincipal User admin) throws AccessDeniedException{
		if(!admin.getRole().equals(Role.ADMIN)) {
			throw new AccessDeniedException("Admin access only!");
		}
		
		return ResponseEntity.ok(userService.getAllUsers());
	}
	
	@PostMapping
	public ResponseEntity<UserDTO> addNewUser(@AuthenticationPrincipal User admin,@RequestBody UserDTO user) throws AccessDeniedException, UserNotFoundException{
		if(!admin.getRole().equals(Role.ADMIN)) {
			throw new AccessDeniedException("Admin access only!");
		}
		
		return ResponseEntity.ok(userService.addUser(UserMapper.mapToEntity(user)));
	}
	
	@GetMapping("/{userId}")
	public ResponseEntity<UserDTO> getUser(@AuthenticationPrincipal User admin,@PathVariable Long userId) throws AccessDeniedException, UserNotFoundException{
		if(!admin.getRole().equals(Role.ADMIN)) {
			throw new AccessDeniedException("Admin access only!");
		}
		
		return ResponseEntity.ok(userService.getUser(userId));
	}
}
