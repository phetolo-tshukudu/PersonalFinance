package com.phetolo.PersonalFinance.dto;

import java.math.BigDecimal;

import com.phetolo.PersonalFinance.enums.AccountType;


import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class MonthlyBudgetDTO {
	@Nonnull
	private BigDecimal amount;
	@Nonnull
	private AccountType type;
}
