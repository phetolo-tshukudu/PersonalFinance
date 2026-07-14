package com.phetolo.PersonalFinance.dto;

import java.math.BigDecimal;


import com.phetolo.PersonalFinance.enums.Category;


import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TransactionDTO {
	
	@Nonnull
	private BigDecimal amount;
	@Nonnull
	private Category category;
	
	
	
}
