package com.phetolo.PersonalFinance.controller;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.phetolo.PersonalFinance.dto.TransactionDTO;
import com.phetolo.PersonalFinance.exception.BudgetExceededException;
import com.phetolo.PersonalFinance.exception.UserNotFoundException;
import com.phetolo.PersonalFinance.mapper.TransactionMapper;
import com.phetolo.PersonalFinance.services.TransactionService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

@RestController
@RequestMapping("/transaction")
public class TransactionController {
	private TransactionService transactionService;
	
	@PostMapping
	public ResponseEntity<TransactionDTO> addTransaction(@PathVariable Long userId,@RequestBody TransactionDTO dto ) throws AccountNotFoundException, UserNotFoundException, BudgetExceededException{
		TransactionDTO transaction =  transactionService.addTransaction(userId, TransactionMapper.mapToEntity(dto));
		
		return ResponseEntity.ok(transaction);
	}
	
//	@GetMapping("/{id}")
//	public ResponseEntity<TransactionDTO> getTransaction(@)

}
