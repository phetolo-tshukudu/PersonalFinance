package com.phetolo.PersonalFinance.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.phetolo.PersonalFinance.dto.UserDTO;
import com.phetolo.PersonalFinance.exception.UserNotFoundException;
import com.phetolo.PersonalFinance.mapper.UserMapper;
import com.phetolo.PersonalFinance.model.User;
import com.phetolo.PersonalFinance.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

@Service
@Transactional
public class UserService {
	private UserRepository userRepo;
	
	public UserDTO addUser(Long id,User User) throws UserNotFoundException {
		if(!userRepo.existsById(id))
			throw new UserNotFoundException("user "+ id+" not found!");
		User user = userRepo.findById(id).get();
		return UserMapper.mapToDTO(user);
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
	
}
