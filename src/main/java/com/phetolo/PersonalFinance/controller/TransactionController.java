package com.phetolo.PersonalFinance.controller;

import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.phetolo.PersonalFinance.dto.TransactionDTO;
import com.phetolo.PersonalFinance.exception.BudgetExceededException;
import com.phetolo.PersonalFinance.exception.TransactionNotFoundException;
import com.phetolo.PersonalFinance.exception.UserNotFoundException;
import com.phetolo.PersonalFinance.mapper.TransactionMapper;
import com.phetolo.PersonalFinance.model.User;
import com.phetolo.PersonalFinance.services.TransactionService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

@RestController
@RequestMapping("/transaction")
public class TransactionController {
	private final TransactionService transactionService;
	
	@PostMapping
	public ResponseEntity<TransactionDTO> addTransaction(@AuthenticationPrincipal User user,@RequestBody TransactionDTO dto ) throws AccountNotFoundException, UserNotFoundException, BudgetExceededException{
		
		TransactionDTO transaction =  transactionService.addTransaction(user.getId(), TransactionMapper.mapToEntity(dto));
		
		return ResponseEntity.ok(transaction);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<TransactionDTO> getTransaction(@AuthenticationPrincipal User user,@PathVariable Long id) throws UserNotFoundException, TransactionNotFoundException{
		return ResponseEntity.ok(transactionService.getTransaction(user.getId(),id));
	}
	
	@GetMapping
	public ResponseEntity<List<TransactionDTO>> getAllTransactions(@AuthenticationPrincipal User user) throws UserNotFoundException{
		return ResponseEntity.ok(transactionService.getAllTransaction(user.getId()));
	}
}
