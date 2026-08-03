package com.phetolo.PersonalFinance.mapper;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import com.phetolo.PersonalFinance.dto.UserDTO;
import com.phetolo.PersonalFinance.enums.Role;
import com.phetolo.PersonalFinance.model.User;

public class UserMapperTest {

	@Test
	void testMapToDTO_WithValidUser_ShouldReturnUserDTOWithSameData() {
		// Arrange
		User user = new User();
		user.setId(1L);
		user.setName("John Doe");
		user.setEmail("john@example.com");
		user.setPassword("password123");
		user.setRole(Role.USER);

		// Act
		UserDTO dto = UserMapper.mapToDTO(user);

		// Assert
		assertNotNull(dto);
		assertEquals("John Doe", dto.getName());
		assertEquals("john@example.com", dto.getEmail());
		assertEquals("password123", dto.getPassword());
		assertEquals(Role.USER, dto.getRole());
	}

	@Test
	void testMapToDTO_WithNullUser_ShouldThrowNullPointerException() {
		// Act & Assert
		assertThrows(NullPointerException.class, () -> UserMapper.mapToDTO(null));
	}

	@Test
	void testMapToEntity_WithValidUserDTO_ShouldReturnUserWithSameData() {
		// Arrange
		UserDTO dto = new UserDTO("Jane Doe", "jane@example.com", "securepass456", Role.ADMIN);

		// Act
		User user = UserMapper.mapToEntity(dto);

		// Assert
		assertNotNull(user);
		assertEquals("Jane Doe", user.getName());
		assertEquals("jane@example.com", user.getEmail());
		assertEquals("securepass456", user.getPassword());
		assertEquals(Role.ADMIN, user.getRole());
	}

	@Test
	void testMapToEntity_WithNullDTO_ShouldThrowNullPointerException() {
		// Act & Assert
		assertThrows(NullPointerException.class, () -> UserMapper.mapToEntity(null));
	}

	@Test
	void testMapToDTO_And_MapToEntity_ShouldBeReversible() {
		// Arrange
		User originalUser = new User();
		originalUser.setName("Test User");
		originalUser.setEmail("test@example.com");
		originalUser.setPassword("testpass123");
		originalUser.setRole(Role.USER);

		// Act
		UserDTO dto = UserMapper.mapToDTO(originalUser);
		User mappedUser = UserMapper.mapToEntity(dto);

		// Assert
		assertEquals(originalUser.getName(), mappedUser.getName());
		assertEquals(originalUser.getEmail(), mappedUser.getEmail());
		assertEquals(originalUser.getPassword(), mappedUser.getPassword());
		assertEquals(originalUser.getRole(), mappedUser.getRole());
	}
}
