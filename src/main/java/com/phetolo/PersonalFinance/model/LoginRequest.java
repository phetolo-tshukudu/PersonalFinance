package com.phetolo.PersonalFinance.model;



import jakarta.annotation.Nonnull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {
	@Nonnull
	private String username;
	@Nonnull
	private String password;
}
