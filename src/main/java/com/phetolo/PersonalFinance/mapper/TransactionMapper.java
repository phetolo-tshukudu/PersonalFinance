package com.phetolo.PersonalFinance.mapper;

import com.phetolo.PersonalFinance.dto.TransactionDTO;
import com.phetolo.PersonalFinance.model.Transaction;

public class TransactionMapper {
	public static TransactionDTO mapToDTO(Transaction transaction) {
		return new TransactionDTO(transaction.getAmount(),transaction.getCategory());
	}
	
	public static Transaction mapToEntity(TransactionDTO dto) {
		Transaction t = new Transaction();
		t.setAmount(dto.getAmount());
		t.setCategory(dto.getCategory());
		
		return t;
	}
}
