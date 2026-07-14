package com.phetolo.PersonalFinance.mapper;

import com.phetolo.PersonalFinance.dto.MonthlyBudgetDTO;
import com.phetolo.PersonalFinance.model.MonthlyBudget;

public class MonthlyBudgetMapper {
	public static MonthlyBudgetDTO mapToDTO(MonthlyBudget mb) {
		return new MonthlyBudgetDTO(mb.getAmount(),mb.getAccount().getType());
	}
	
	public static MonthlyBudget mapToEntity(MonthlyBudgetDTO dto) {
		MonthlyBudget m = new MonthlyBudget();
		
		m.setAmount(dto.getAmount());
		
		return m;
	}
}
