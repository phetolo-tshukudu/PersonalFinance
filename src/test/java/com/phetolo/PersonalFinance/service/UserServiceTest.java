package com.phetolo.PersonalFinance.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;

import com.phetolo.PersonalFinance.dto.AuthResponse;
import com.phetolo.PersonalFinance.dto.UserDTO;
import com.phetolo.PersonalFinance.enums.Role;
import com.phetolo.PersonalFinance.exception.UserNotFoundException;
import com.phetolo.PersonalFinance.model.LoginRequest;
import com.phetolo.PersonalFinance.model.User;
import com.phetolo.PersonalFinance.repository.UserRepository;
import com.phetolo.PersonalFinance.services.JwtService;
import com.phetolo.PersonalFinance.services.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private JwtService jwtService;

	@InjectMocks
	private UserService userService;

	@Test
	void testAddUser_WhenUserDoesNotExist_ShouldSaveAndReturnUserDTO() throws UserNotFoundException {
		// Arrange
		User user = new User();
		user.setName("John Doe");
		user.setEmail("john@example.com");
		user.setPassword("password123");
		user.setRole(Role.USER);

		User savedUser = new User();
		savedUser.setId(1L);
		savedUser.setName("John Doe");
		savedUser.setEmail("john@example.com");
		savedUser.setPassword("password123");
		savedUser.setRole(Role.USER);

		when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
		when(userRepository.save(any(User.class))).thenReturn(savedUser);

		// Act
		UserDTO result = userService.addUser(user);

		// Assert
		assertNotNull(result);
		assertEquals("john@example.com", result.getEmail());
		assertEquals("John Doe", result.getName());
	}

	@Test
	void testAddUser_WhenUserExists_ShouldThrowUserNotFoundException() {
		// Arrange
		User user = new User();
		user.setEmail("existing@example.com");

		when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

		// Act & Assert
		assertThrows(UserNotFoundException.class, () -> userService.addUser(user));
	}

	@Test
	void testGetAllUsers_ShouldReturnListOfUserDTOs() {
		// Arrange
		User user1 = new User();
		user1.setId(1L);
		user1.setName("User One");
		user1.setEmail("user1@example.com");
		user1.setPassword("user1password");
		user1.setRole(Role.USER);

		User user2 = new User();
		user2.setId(2L);
		user2.setName("User Two");
		user2.setEmail("user2@example.com");
		user2.setPassword("user2password");
		user2.setRole(Role.USER);

		List<User> userList = new ArrayList<>();
		userList.add(user1);
		userList.add(user2);

		when(userRepository.findAll()).thenReturn(userList);

		// Act
		List<UserDTO> result = userService.getAllUsers();

		// Assert
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("user1@example.com", result.get(0).getEmail());
		assertEquals("user2@example.com", result.get(1).getEmail());
	}

	@Test
	void testGetUser_ByIdWhenUserExists_ShouldReturnUserDTO() throws UserNotFoundException {
		// Arrange
		Long userId = 1L;
		User user = new User();
		user.setId(userId);
		user.setName("John Doe");
		user.setEmail("john@example.com");
		user.setPassword("userpassword");
		user.setRole(Role.USER);

		when(userRepository.existsById(userId)).thenReturn(true);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// Act
		UserDTO result = userService.getUser(userId);

		// Assert
		assertNotNull(result);
		assertEquals("john@example.com", result.getEmail());
	}

	@Test
	void testGetUser_ByIdWhenUserNotExists_ShouldThrowUserNotFoundException() {
		// Arrange
		Long userId = 1L;

		when(userRepository.existsById(userId)).thenReturn(false);

		// Act & Assert
		assertThrows(UserNotFoundException.class, () -> userService.getUser(userId));
	}

	@Test
	void testGetUser_ByEmailWhenUserExists_ShouldReturnUserDTO() throws UserNotFoundException {
		// Arrange
		String email = "john@example.com";
		User user = new User();
		user.setId(1L);
		user.setName("John Doe");
		user.setEmail(email);
		user.setRole(Role.USER);
		user.setPassword("userpassword");

		when(userRepository.existsByEmail(email)).thenReturn(true);
		when(userRepository.findByEmail(email)).thenReturn(user);

		// Act
		UserDTO result = userService.getUser(email);

		// Assert
		assertNotNull(result);
		assertEquals(email, result.getEmail());
	}

	@Test
	void testGetUser_ByEmailWhenUserNotExists_ShouldThrowUserNotFoundException() {
		// Arrange
		String email = "nonexistent@example.com";

		when(userRepository.existsByEmail(email)).thenReturn(false);

		// Act & Assert
		assertThrows(UserNotFoundException.class, () -> userService.getUser(email));
	}

	@Test
	void testLogin_WhenCredentialsAreValid_ShouldReturnAuthResponse() {
		// Arrange
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setUsername("john@example.com");
		loginRequest.setPassword("password123");

		User user = new User();
		user.setId(1L);
		user.setEmail("john@example.com");
		user.setPassword("password123");
		user.setRole(Role.USER);

		when(authenticationManager.authenticate(any())).thenReturn(null);
		when(userRepository.findByEmail("john@example.com")).thenReturn(user);
		when(jwtService.generateToken(any())).thenReturn("jwt_token_123");

		// Act
		AuthResponse result = userService.login(loginRequest);

		// Assert
		assertNotNull(result);
		assertEquals("jwt_token_123", result.getToken());
	}
}
