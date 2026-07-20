package com.phetolo.PersonalFinance.config;

import java.io.IOException;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.phetolo.PersonalFinance.services.JwtService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtService jwtService;
	private UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		final String authHeader = request.getHeader("Authentication");
		final String jwt;
		final String username;
		
		if(authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
			//The execution stop her , for invalid header
		}
		
		//extracting from index 7 excludes the "Bearer "
		jwt = authHeader.substring(7);
		
		//convert the token to username and check if the user exists.
		//Jwt service is required for this.
		username = jwtService.extractUsername(jwt);
		if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userdetails = this.userDetailsService.loadUserByUsername(username);
			if(jwtService.isTokenValid(username, userdetails)) {
				//update context
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
						username,null
						, userdetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
		filterChain.doFilter(request, response);
		}
	}

