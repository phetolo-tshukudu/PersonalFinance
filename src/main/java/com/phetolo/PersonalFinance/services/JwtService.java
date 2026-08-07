package com.phetolo.PersonalFinance.services;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service

public class JwtService {
	
	private final String SECRETE_KEY = "N6L71LPbE+rIkICJN1esZgcp211lS/s2qJ4bH7sz9uM=";

	/**
	 * Extracts the username (subject) from a JWT token.
	 * 
	 * Parses the JWT token and retrieves the username claim stored as the token's subject.
	 * 
	 * @param token the JWT token string
	 * @return String representing the username/subject claim from the token
	 */
	public String extractUsername(String token) {
		// Extract and return the subject claim from the token
		return extractClaim(token,Claims::getSubject);
	}
	
	/**
	 * Extracts a specific claim from a JWT token using a provided resolver function.
	 * 
	 * This generic method allows extraction of any claim type from a JWT token by
	 * providing a custom resolver function.
	 * 
	 * @param token the JWT token string
	 * @param claimResolver a function that extracts and returns the desired claim
	 * @return the extracted claim value of type T
	 */
	public <T> T extractClaim(String token, Function<Claims,T> claimResolver){
		// Parse all claims from the token
		final Claims claims = extractAllClaims(token);
		// Apply the resolver function to extract the specific claim
		return claimResolver.apply(claims);
	}
	
	/**
	 * Generates a JWT token for a user with default (empty) claims.
	 * 
	 * Creates a JWT token without additional custom claims.
	 * 
	 * @param userdetails the UserDetails of the user for whom to generate the token
	 * @return String containing the generated JWT token
	 */
	public String generateToken(UserDetails userdetails) {
		// Generate token with empty additional claims
		return generateToken(new HashMap<>(),userdetails);
	}
	
	/**
	 * Generates a JWT token for a user with custom claims.
	 * 
	 * Creates a JWT token containing the provided custom claims, the user's username as subject,
	 * current timestamp as issued-at time, and an expiration time of 24 hours from generation.
	 * The token is signed using HMAC SHA-256 algorithm.
	 * 
	 * @param extractClaims a Map of custom claims to include in the token
	 * @param details the UserDetails of the user for whom to generate the token
	 * @return String containing the signed JWT token
	 */
	public String generateToken(Map<String,Object> extractClaims, UserDetails details) {
		// Build JWT token with claims, subject, timestamps, and signature
		return Jwts.builder()
				// Add custom claims to the token
				.setClaims(extractClaims)
				// Set the username as the subject
				.setSubject(details.getUsername())
				// Set current time as issued-at timestamp
				.setIssuedAt(new Date(System.currentTimeMillis()))
				// Set expiration to 24 hours from now (1000ms * 60s * 24h)
				.setExpiration(new Date(System.currentTimeMillis() + 1000*60*24))
				// Sign the token using the secret key and HS256 algorithm
				.signWith(getSignInKey(),SignatureAlgorithm.HS256)
				// Serialize the token to compact string format
				.compact();
	}
	
	/**
	 * Parses and extracts all claims from a JWT token.
	 * 
	 * Decodes the JWT token using the secret signing key and retrieves all embedded claims.
	 * 
	 * @param token the JWT token string
	 * @return Claims object containing all embedded claims from the token
	 */
	private Claims extractAllClaims(String token) {
		// Build JWT parser with the signing key for validation and parsing
		return Jwts
			   .parserBuilder()
			   .setSigningKey(getSignInKey())
			   .build()
			   // Parse and validate the token's JWS signature
			   .parseClaimsJws(token)
			   // Extract the claims payload
			   .getBody();
	}
	
	/**
	 * Validates whether a JWT token is valid for a specific user.
	 * 
	 * Checks if the token's username matches the provided user and the token has not expired.
	 * 
	 * @param token the JWT token string to validate
	 * @param userDetails the UserDetails of the user to validate against
	 * @return boolean true if token is valid (username matches and not expired), false otherwise
	 */
	public boolean isTokenValid(String token, UserDetails userDetails) {
		// Extract username from token
		final String username = extractUsername(token);
		// Token is valid if username matches and token hasn't expired
		return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}

	/**
	 * Checks if a JWT token has expired.
	 * 
	 * Compares the token's expiration timestamp with the current time.
	 * 
	 * @param token the JWT token string
	 * @return boolean true if token has expired, false if still valid
	 */
	private boolean isTokenExpired(String token) {
		// Extract expiration date and compare with current time
		return extractExpiration(token).before(new Date());
	}

	/**
	 * Extracts the expiration date from a JWT token.
	 * 
	 * Retrieves the expiration timestamp claim from the token.
	 * 
	 * @param token the JWT token string
	 * @return Date representing the token's expiration time
	 */
	private Date extractExpiration(String token) {
		// Extract and return the expiration date claim from the token
		return extractClaim(token, Claims::getExpiration);
	}

	/**
	 * Retrieves the signing key used for JWT token validation and generation.
	 * 
	 * Decodes the Base64-encoded secret key and converts it to an HMAC-SHA key object.
	 * 
	 * @return Key object used for HMAC SHA-256 signing operations
	 */
	private Key getSignInKey() {
		// Decode the Base64-encoded secret key
		byte[] bytes = Decoders.BASE64.decode(SECRETE_KEY);
		
		// Convert bytes to HMAC-SHA key for signing
		return Keys.hmacShaKeyFor(bytes);
	}
}
