package com.phetolo.PersonalFinance.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.phetolo.PersonalFinance.model.Account;
import com.phetolo.PersonalFinance.model.MonthlyBudget;
import com.phetolo.PersonalFinance.model.Transaction;
import com.phetolo.PersonalFinance.model.User;
import com.phetolo.PersonalFinance.repository.AccountRepository;
import com.phetolo.PersonalFinance.repository.MonthlyBudgetRepository;
import com.phetolo.PersonalFinance.repository.TransactionRepository;
import com.phetolo.PersonalFinance.repository.UserRepository;
import com.phetolo.PersonalFinance.services.TransactionService;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
	@Mock
	private  TransactionRepository transactionRepo;
	@Mock
	private  UserRepository userRepo;
	@Mock
	private  AccountRepository accountRepo;
	@Mock
	private  MonthlyBudgetRepository budgetRepo;
	@InjectMocks
	private  TransactionService transactionService;
	
	@Test
	void addTransaction_WhenUserExists_ShouldReturnTransactionDTO() throws Exception {
		Long userId = 1L;
		User user = new User();
		user.setId(userId);
		
		Account account = new Account();
		account.setId(3L);
		account.setUser(user);
		account.setBalance(new java.math.BigDecimal("1000"));
		
		Transaction transaction = new Transaction();
		transaction.setId(2L);
		transaction.setAccount(account);
		transaction.setUser(user);
		transaction.setAmount(new java.math.BigDecimal("100"));
		transaction.setType(com.phetolo.PersonalFinance.enums.TransactionType.EXPENSE);
		transaction.setCategory(com.phetolo.PersonalFinance.enums.Category.FOOD);
		
		MonthlyBudget budget = new MonthlyBudget();
		budget.setId(4L);
		budget.setUser(user);
		budget.setAccount(account);
		budget.setAmount(new java.math.BigDecimal("500"));
		
		when(userRepo.existsById(userId)).thenReturn(true);
		when(userRepo.findById(userId)).thenReturn(java.util.Optional.of(user));
		when(accountRepo.existsById(account.getId())).thenReturn(true);
		when(accountRepo.findById(account.getId())).thenReturn(java.util.Optional.of(account));
		when(budgetRepo.findByUser_Id(userId)).thenReturn(budget);
		when(transactionRepo.save(any(Transaction.class))).thenReturn(transaction);
		when(accountRepo.save(any(Account.class))).thenReturn(account);
		
		com.phetolo.PersonalFinance.dto.TransactionDTO result = transactionService.addTransaction(userId, transaction);
		
		org.junit.jupiter.api.Assertions.assertNotNull(result);
		org.mockito.Mockito.verify(transactionRepo).save(any(Transaction.class));
	}
	
	@Test
	void addTransaction_WhenUserNotExists_ShouldThrowUserNotFoundException() throws Exception {
		Long userId = 1L;
		Transaction transaction = new Transaction();
		
		when(userRepo.existsById(userId)).thenReturn(false);
		
		org.junit.jupiter.api.Assertions.assertThrows(
			com.phetolo.PersonalFinance.exception.UserNotFoundException.class,
			() -> transactionService.addTransaction(userId, transaction)
		);
	}
	
	@Test
	void getTotalIncome_WhenUserExists_ShouldReturnBigDecimal() throws Exception {
		Long userId = 1L;
		java.math.BigDecimal expectedIncome = new java.math.BigDecimal("5000");
		
		when(userRepo.existsById(userId)).thenReturn(true);
		when(transactionRepo.getIncomeSum(userId)).thenReturn(expectedIncome);
		
		java.math.BigDecimal result = transactionService.getTotalIncome(userId);
		
		org.junit.jupiter.api.Assertions.assertEquals(expectedIncome, result);
	}
	
	@Test
	void getTotalIncome_WhenUserNotExists_ShouldThrowUserNotFoundException() throws Exception {
		Long userId = 1L;
		
		when(userRepo.existsById(userId)).thenReturn(false);
		
		org.junit.jupiter.api.Assertions.assertThrows(
			com.phetolo.PersonalFinance.exception.UserNotFoundException.class,
			() -> transactionService.getTotalIncome(userId)
		);
	}
	
	@Test
	void getTotalExpense_WhenUserExists_ShouldReturnBigDecimal() throws Exception {
		Long userId = 1L;
		java.math.BigDecimal expectedExpense = new java.math.BigDecimal("2000");
		
		when(userRepo.existsById(userId)).thenReturn(true);
		when(transactionRepo.getExpenseSum(userId)).thenReturn(expectedExpense);
		
		java.math.BigDecimal result = transactionService.getTotalExpense(userId);
		
		org.junit.jupiter.api.Assertions.assertEquals(expectedExpense, result);
	}
	
	@Test
	void getTotalExpense_WhenUserNotExists_ShouldThrowUserNotFoundException() throws Exception {
		Long userId = 1L;
		
		when(userRepo.existsById(userId)).thenReturn(false);
		
		org.junit.jupiter.api.Assertions.assertThrows(
			com.phetolo.PersonalFinance.exception.UserNotFoundException.class,
			() -> transactionService.getTotalExpense(userId)
		);
	}
	
	@Test
	void getAllTransaction_WhenUserExists_ShouldReturnListOfTransactionDTO() throws Exception {
		Long userId = 1L;
		User user = new User();
		user.setId(userId);
		
		java.util.List<Transaction> transactions = new java.util.ArrayList<>();
		Transaction transaction1 = new Transaction();
		transaction1.setId(1L);
		transaction1.setUser(user);
		transactions.add(transaction1);
		
		when(userRepo.existsById(userId)).thenReturn(true);
		when(transactionRepo.findByUser_Id(userId)).thenReturn(transactions);
		
		java.util.List<com.phetolo.PersonalFinance.dto.TransactionDTO> result = transactionService.getAllTransaction(userId);
		
		org.junit.jupiter.api.Assertions.assertNotNull(result);
		org.junit.jupiter.api.Assertions.assertEquals(1, result.size());
	}
	
	@Test
	void getAllTransaction_WhenUserNotExists_ShouldThrowUserNotFoundException() throws Exception {
		Long userId = 1L;
		
		when(userRepo.existsById(userId)).thenReturn(false);
		
		org.junit.jupiter.api.Assertions.assertThrows(
			com.phetolo.PersonalFinance.exception.UserNotFoundException.class,
			() -> transactionService.getAllTransaction(userId)
		);
	}
	
	@Test
	void getTransaction_WhenBothUserAndTransactionExist_ShouldReturnTransactionDTO() throws Exception {
		Long userId = 1L;
		Long transactionId = 1L;
		User user = new User();
		user.setId(userId);
		
		Transaction transaction = new Transaction();
		transaction.setId(transactionId);
		transaction.setUser(user);
		
		when(userRepo.existsById(userId)).thenReturn(true);
		when(transactionRepo.existsById(transactionId)).thenReturn(true);
		when(transactionRepo.findById(transactionId)).thenReturn(java.util.Optional.of(transaction));
		
		com.phetolo.PersonalFinance.dto.TransactionDTO result = transactionService.getTransaction(userId, transactionId);
		
		org.junit.jupiter.api.Assertions.assertNotNull(result);
	}
	
	@Test
	void getTransaction_WhenUserNotExists_ShouldThrowUserNotFoundException() throws Exception {
		Long userId = 1L;
		Long transactionId = 1L;
		
		when(userRepo.existsById(userId)).thenReturn(false);
		
		org.junit.jupiter.api.Assertions.assertThrows(
			com.phetolo.PersonalFinance.exception.UserNotFoundException.class,
			() -> transactionService.getTransaction(userId, transactionId)
		);
	}
	
	@Test
	void getTransaction_WhenTransactionNotExists_ShouldThrowTransactionNotFoundException() throws Exception {
		Long userId = 1L;
		Long transactionId = 1L;
		
		when(userRepo.existsById(userId)).thenReturn(true);
		when(transactionRepo.existsById(transactionId)).thenReturn(false);
		
		org.junit.jupiter.api.Assertions.assertThrows(
			com.phetolo.PersonalFinance.exception.TransactionNotFoundException.class,
			() -> transactionService.getTransaction(userId, transactionId)
		);
	}
}
