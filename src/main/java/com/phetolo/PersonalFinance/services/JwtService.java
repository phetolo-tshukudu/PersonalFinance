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

	public String extractUsername(String token) {
		// TODO Auto-generated method stub
		return extractClaim(token,Claims::getSubject);
	}
	
	public <T> T extractClaim(String token, Function<Claims,T> claimResolver){
		final Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}
	
	public String generateToken(UserDetails userdetails) {
		return generateToken(new HashMap<>(),userdetails);
	}
	
	public String generateToken(Map<String,Object> extractClaims, UserDetails details) {
		return Jwts.builder()
				.setClaims(extractClaims)
				.setSubject(details.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000*60*24))
				.signWith(getSignInKey(),SignatureAlgorithm.HS256)
				.compact();
	}
	private Claims extractAllClaims(String token) {
		return Jwts
			   .parserBuilder()
			   .setSigningKey(getSignInKey())
			   .build()
			   .parseClaimsJws(token)
			   .getBody();
	}
	
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {
		// TODO Auto-generated method stub
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		// TODO Auto-generated method stub
		return extractClaim(token, Claims::getExpiration);
	}

	private Key getSignInKey() {
		// TODO Auto-generated method stub
		byte[] bytes = Decoders.BASE64.decode(SECRETE_KEY);
		
		return Keys.hmacShaKeyFor(bytes);
	}
}
