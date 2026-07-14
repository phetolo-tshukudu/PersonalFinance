package com.phetolo.PersonalFinance.mapper;

import com.phetolo.PersonalFinance.dto.AccountDTO;
import com.phetolo.PersonalFinance.model.Account;

public class AccountMapper {
	public static AccountDTO mapToDTO(Account acc) {
		
		return new AccountDTO(acc.getType(),acc.getBalance());
				
	}
	
	public static Account mapToEntity(AccountDTO dto) {
		Account a = new  Account();
		a.setType(dto.getAccountType());
		a.setBalance(dto.getBalance());
		return a;
	}
}
