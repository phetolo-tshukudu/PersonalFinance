package com.phetolo.PersonalFinance.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.phetolo.PersonalFinance.dto.AuthResponse;
import com.phetolo.PersonalFinance.dto.UserDTO;
import com.phetolo.PersonalFinance.exception.UserNotFoundException;
import com.phetolo.PersonalFinance.mapper.UserMapper;
import com.phetolo.PersonalFinance.model.LoginRequest;
import com.phetolo.PersonalFinance.model.User;
import com.phetolo.PersonalFinance.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

@Service
@Transactional
public class UserService {
	private final UserRepository userRepo;
	private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    
    /**
     * Authenticates a user with the provided credentials and generates a JWT token.
     * 
     * This method performs user authentication using Spring Security's AuthenticationManager.
     * It validates the username/password combination and generates a JWT token upon successful
     * authentication.
     * 
     * @param request the LoginRequest containing username and password credentials
     * @return AuthResponse containing the generated JWT token for authenticated user
     */
    public AuthResponse login(LoginRequest request) {
        // Authenticate user credentials against the repository
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Retrieve the authenticated user from the database
        User user = userRepo.findByEmail(request.getUsername());

        // Generate JWT token for the authenticated user
        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }
    
	/**
	 * Creates and registers a new user in the system.
	 * 
	 * Validates that the email address is unique before creating the user. If a user with the same
	 * email already exists, an exception is thrown.
	 * 
	 * @param user the User object containing registration details (name, email, password, role)
	 * @return UserDTO containing the created user's details
	 * @throws UserNotFoundException if a user with the same email already exists
	 */
	public UserDTO addUser(User user) throws UserNotFoundException {
		// Check if user already exists by email
		if(userRepo.existsByEmail(user.getEmail()))
			throw new UserNotFoundException("user "+ user.getEmail()+" alredady exists!");
		
		// Persist the user to the database and return as DTO
		return UserMapper.mapToDTO(userRepo.save(user));
	}
	
	/**
	 * Retrieves all users currently registered in the system.
	 * 
	 * Fetches all users from the repository and converts them to UserDTO objects for API response.
	 * 
	 * @return List of UserDTO objects representing all registered users
	 */
	public List<UserDTO> getAllUsers(){
		// Fetch all users from repository
		List<User> us = userRepo.findAll();
		List<UserDTO> dt = new ArrayList<UserDTO>();
		
		// Map each User entity to UserDTO
		for(User s:us) {
			dt.add(UserMapper.mapToDTO(s));
		}
		return dt;
	}
	
	/**
	 * Retrieves a user by their unique identifier (ID).
	 * 
	 * Searches for a user by ID and returns their details as a UserDTO.
	 * 
	 * @param id the unique identifier of the user to retrieve
	 * @return UserDTO containing the requested user's details
	 * @throws UserNotFoundException if no user with the given ID exists
	 */
	public UserDTO getUser(Long id) throws UserNotFoundException {
		// Verify user exists before attempting to retrieve
		if(!userRepo.existsById(id))
			throw new UserNotFoundException("user "+ id+" not found!");
		// Return user details mapped to DTO
		return UserMapper.mapToDTO(userRepo.findById(id).get());
	}
	
	/**
	 * Retrieves a user by their email address.
	 * 
	 * Searches for a user by email and returns their details as a UserDTO.
	 * 
	 * @param email the email address of the user to retrieve
	 * @return UserDTO containing the requested user's details
	 * @throws UserNotFoundException if no user with the given email exists
	 */
	public UserDTO getUser(String email) throws UserNotFoundException {
		// Verify user exists by email before attempting to retrieve
		if(!userRepo.existsByEmail(email))
			throw new UserNotFoundException("user "+email+" not found!");
		// Return user details mapped to DTO
		return UserMapper.mapToDTO(userRepo.findByEmail(email));
	}
	
}
