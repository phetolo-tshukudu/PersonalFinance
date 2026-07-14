package com.phetolo.PersonalFinance.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.phetolo.PersonalFinance.exception.AccountNotFoundException;
import com.phetolo.PersonalFinance.exception.BudgetNotFoundException;
import com.phetolo.PersonalFinance.exception.InvalidTransactionException;
import com.phetolo.PersonalFinance.exception.TransactionNotFoundException;
import com.phetolo.PersonalFinance.exception.UserNotFoundException;

@Controller
public class GlobalExceptionController {
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<Map<String,Object>> handlerUserNotFound(UserNotFoundException ex){
		Map<String,Object> err = new HashMap<>();
		err.put("timestamp", LocalDateTime.now());
		err.put("status", HttpStatus.NOT_FOUND);
		err.put("message", ex.getMessage());
		return ResponseEntity.ok(err);
	}
	
	@ExceptionHandler(BudgetNotFoundException.class)
	public ResponseEntity<Map<String,Object>> handleBudgetNotFound(BudgetNotFoundException ex){
		Map<String,Object> err = new HashMap<String, Object>();
		err.put("timestamp", LocalDateTime.now());
		err.put("status", HttpStatus.NOT_FOUND);
		err.put("message", ex.getMessage());
		return ResponseEntity.ok(err);
	}
	
	@ExceptionHandler(TransactionNotFoundException.class)
	public ResponseEntity<Map<String,Object>> handleTransactionNotFound(TransactionNotFoundException ex){
		Map<String,Object> err = new HashMap<String, Object>();
		err.put("timestamp", LocalDateTime.now());
		err.put("status", HttpStatus.NOT_FOUND);
		err.put("message", ex.getMessage());
		return ResponseEntity.ok(err);
	}
	
	@ExceptionHandler(AccountNotFoundException.class)
	public ResponseEntity<Map<String,Object>> handleAccountNotFound(AccountNotFoundException ex){
		Map<String,Object> err = new HashMap<String, Object>();
		err.put("timestamp", LocalDateTime.now());
		err.put("status", HttpStatus.NOT_FOUND);
		err.put("message", ex.getMessage());
		return ResponseEntity.ok(err);
	}
	
	@ExceptionHandler(InvalidTransactionException.class)
	public ResponseEntity<Map<String,Object>> handleInvalidTransaction(InvalidTransactionException ex){
		Map<String,Object> err = new HashMap<String, Object>();
		err.put("timestamp", LocalDateTime.now());
		err.put("status", HttpStatus.NOT_FOUND);
		err.put("message", ex.getMessage());
		return ResponseEntity.ok(err);
	}
}
