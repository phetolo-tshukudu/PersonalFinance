package com.phetolo.PersonalFinance.dto;





import com.phetolo.PersonalFinance.enums.Role;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class UserDTO {
	@Nonnull
	private String name;
	@Nonnull
	private String email;
	@Nonnull
	private String password;
	private Role role;
}
