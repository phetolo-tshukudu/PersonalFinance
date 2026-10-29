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
    
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepo.findByEmail(request.getUsername());

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }
	public UserDTO addUser(User user) throws UserNotFoundException {
		if(userRepo.existsByEmail(user.getEmail()))
			throw new UserNotFoundException("user "+ user.getEmail()+" alredady exists!");
		
		return UserMapper.mapToDTO(userRepo.save(user));
	}
	
	public List<UserDTO> getAllUsers(){
		List<User> us = userRepo.findAll();
		List<UserDTO> dt = new ArrayList<UserDTO>();
		for(User s:us) {
			dt.add(UserMapper.mapToDTO(s));
		}
		return dt;
	}
	
	public UserDTO getUser(Long id) throws UserNotFoundException {
		if(!userRepo.existsById(id))
			throw new UserNotFoundException("user "+ id+" not found!");
		return UserMapper.mapToDTO(userRepo.findById(id).get());
	}
	
	public UserDTO getUser(String email) throws UserNotFoundException {
		if(!userRepo.existsByEmail(email))
			throw new UserNotFoundException("user "+email+" not found!");
		return UserMapper.mapToDTO(userRepo.findByEmail(email));
	}
	
}
