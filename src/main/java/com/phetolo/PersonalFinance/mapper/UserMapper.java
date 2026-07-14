package com.phetolo.PersonalFinance.mapper;

import com.phetolo.PersonalFinance.dto.UserDTO;
import com.phetolo.PersonalFinance.model.User;

public class UserMapper {
	public static UserDTO mapToDTO(User user) {
		return new UserDTO(user.getName(),user.getEmail(),user.getPassword(),user.getRole());
	}
	
	public static User mapToEntity(UserDTO dto) {
		User u = new User();
		u.setEmail(dto.getEmail());
		u.setName(dto.getName());
		u.setPassword(dto.getPassword());
		u.setRole(dto.getRole());
		return u;
	}
}
