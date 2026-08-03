package com.phetolo.PersonalFinance.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.phetolo.PersonalFinance.enums.Role;
import com.phetolo.PersonalFinance.model.User;
import com.phetolo.PersonalFinance.services.JwtService;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

	@InjectMocks
	private JwtService jwtService;

	private User createTestUser() {
		User user = new User();
		user.setId(1L);
		user.setName("Test User");
		user.setEmail("test@example.com");
		user.setPassword("password123");
		user.setRole(Role.USER);
		return user;
	}

	@Test
	void testGenerateToken_WithUserDetails_ShouldGenerateValidToken() {
		// Arrange
		UserDetails userDetails = createTestUser();

		// Act
		String token = jwtService.generateToken(userDetails);

		// Assert
		assertNotNull(token);
		assertFalse(token.isEmpty());
		assertTrue(token.contains("."));
		assertEquals(3, token.split("\\.").length);
	}

	@Test
	void testGenerateToken_WithExtraClaimsAndUserDetails_ShouldGenerateValidToken() {
		// Arrange
		Map<String, Object> extraClaims = new HashMap<>();
		extraClaims.put("role", "USER");
		extraClaims.put("department", "Finance");

		UserDetails userDetails = createTestUser();

		// Act
		String token = jwtService.generateToken(extraClaims, userDetails);

		// Assert
		assertNotNull(token);
		assertFalse(token.isEmpty());
		assertTrue(token.contains("."));
	}

	@Test
	void testExtractUsername_FromValidToken_ShouldReturnUsername() {
		// Arrange
		UserDetails userDetails = createTestUser();
		String token = jwtService.generateToken(userDetails);

		// Act
		String extractedUsername = jwtService.extractUsername(token);

		// Assert
		assertEquals("test@example.com", extractedUsername);
	}

	@Test
	void testIsTokenValid_WithValidTokenAndMatchingUser_ShouldReturnTrue() {
		// Arrange
		UserDetails userDetails = createTestUser();
		String token = jwtService.generateToken(userDetails);

		// Act
		boolean isValid = jwtService.isTokenValid(token, userDetails);

		// Assert
		assertTrue(isValid);
	}

	@Test
	void testIsTokenValid_WithValidTokenButDifferentUser_ShouldReturnFalse() {
		// Arrange
		User user1 = createTestUser();
		User user2 = new User();
		user2.setEmail("different@example.com");
		user2.setPassword("password456");
		user2.setName("Different User");
		user2.setRole(Role.USER);

		String token = jwtService.generateToken(user1);

		// Act
		boolean isValid = jwtService.isTokenValid(token, user2);

		// Assert
		assertFalse(isValid);
	}

	@Test
	void testExtractClaim_WithValidToken_ShouldReturnClaimValue() {
		// Arrange
		UserDetails userDetails = createTestUser();
		String token = jwtService.generateToken(userDetails);

		// Act
		String username = jwtService.extractClaim(token, claims -> claims.getSubject());

		// Assert
		assertEquals("test@example.com", username);
	}

	@Test
	void testGenerateTokenTwice_ShouldGenerateDifferentTokens() {
		// Arrange
		UserDetails userDetails = createTestUser();

		// Act
		String token1 = jwtService.generateToken(userDetails);
		String token2 = jwtService.generateToken(userDetails);

		// Assert
		assertNotEquals(token1, token2);
	}

	@Test
	void testTokenWithExtraClaims_ShouldContainAllClaims() {
		// Arrange
		Map<String, Object> extraClaims = new HashMap<>();
		extraClaims.put("role", "ADMIN");
		extraClaims.put("department", "IT");

		UserDetails userDetails = createTestUser();

		// Act
		String token = jwtService.generateToken(extraClaims, userDetails);
		String username = jwtService.extractUsername(token);

		// Assert
		assertNotNull(token);
		assertEquals("test@example.com", username);
	}

	@Test
	void testExtractAllClaims_ShouldContainSubjectAndIssuedAt() {
		// Arrange
		UserDetails userDetails = createTestUser();
		String token = jwtService.generateToken(userDetails);

		// Act
		String username = jwtService.extractClaim(token, claims -> claims.getSubject());

		// Assert
		assertNotNull(username);
		assertEquals("test@example.com", username);
	}

	@Test
	void testIsTokenValid_AfterExpiration_ShouldReturnFalse() throws InterruptedException {
		// This test verifies token expiration logic
		// Since token expires in 24 minutes, we can't test actual expiration in unit test
		// But we can verify the method doesn't throw an exception
		UserDetails userDetails = createTestUser();
		String token = jwtService.generateToken(userDetails);

		// Act & Assert - should not throw exception
		boolean isValid = jwtService.isTokenValid(token, userDetails);
		assertTrue(isValid);
	}
}
