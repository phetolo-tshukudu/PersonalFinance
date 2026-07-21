package com.phetolo.PersonalFinance.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.phetolo.PersonalFinance.dto.AccountDTO;
import com.phetolo.PersonalFinance.dto.TransactionDTO;
import com.phetolo.PersonalFinance.exception.AccountNotFoundException;
import com.phetolo.PersonalFinance.exception.InvalidTransactionException;
import com.phetolo.PersonalFinance.exception.UserNotFoundException;
import com.phetolo.PersonalFinance.mapper.AccountMapper;
import com.phetolo.PersonalFinance.model.User;
import com.phetolo.PersonalFinance.services.AccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")

public class AccountController {
	private final AccountService accountService;
	
	@PostMapping
	public ResponseEntity<AccountDTO> addAccount(@AuthenticationPrincipal User user,@RequestBody AccountDTO accountDto) throws Exception{
		return ResponseEntity.ok(accountService.createAccount(user.getId(), AccountMapper.mapToEntity(accountDto)));
	}
	
	@GetMapping
	public ResponseEntity<List<AccountDTO>> getAllAccount(@AuthenticationPrincipal User user){
		return ResponseEntity.ok(accountService.getAllAccounts(user.getId()));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<AccountDTO> getAccount(@AuthenticationPrincipal User user, @PathVariable Long id) throws UserNotFoundException, AccountNotFoundException{
		return ResponseEntity.ok(accountService.getAccount(user.getId(),id));
	}
	
	@PostMapping("/transfer")
	public ResponseEntity<AccountDTO> transfer(@AuthenticationPrincipal User user,@RequestBody TransactionDTO dto) throws Exception{
		if(dto.getTransferId()==null)
			throw new InvalidTransactionException("Unspecified recipient, specify recipient!");
		
		return ResponseEntity.ok(accountService.transfer(user.getId(), dto.getTransferId(), dto.getAmount()));
	}
	
}
