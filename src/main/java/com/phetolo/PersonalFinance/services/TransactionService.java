package com.phetolo.PersonalFinance.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import javax.security.auth.login.AccountNotFoundException;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import com.phetolo.PersonalFinance.dto.AccountDTO;
import com.phetolo.PersonalFinance.dto.TransactionDTO;
import com.phetolo.PersonalFinance.enums.Category;
import com.phetolo.PersonalFinance.enums.TransactionType;
import com.phetolo.PersonalFinance.exception.BudgetExceededException;
import com.phetolo.PersonalFinance.exception.UserNotFoundException;
import com.phetolo.PersonalFinance.mapper.AccountMapper;
import com.phetolo.PersonalFinance.mapper.TransactionMapper;
import com.phetolo.PersonalFinance.model.Account;
import com.phetolo.PersonalFinance.model.MonthlyBudget;
import com.phetolo.PersonalFinance.model.Transaction;
import com.phetolo.PersonalFinance.model.User;
import com.phetolo.PersonalFinance.repository.AccountRepository;
import com.phetolo.PersonalFinance.repository.MonthlyBudgetRepository;
import com.phetolo.PersonalFinance.repository.TransactionRepository;
import com.phetolo.PersonalFinance.repository.UserRepository;

import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Service

public class TransactionService {
	private TransactionRepository transactionRepo;
	private UserRepository userRepo;
	private AccountRepository accountRepo;
	private MonthlyBudgetRepository budgetRepo;
	
	@Transactional
	public TransactionDTO addTransaction(Long userid,Transaction transaction) throws UserNotFoundException, AccountNotFoundException, BudgetExceededException {
		if(!userRepo.existsById(userid))
			throw new UserNotFoundException("user "+ userid+" not found!");
		User user = userRepo.findById(userid).get();
		if(!accountRepo.existsById(transaction.getAccount().getId()))
			throw new AccountNotFoundException("Account "+transaction.getAccount().getId()+" not found!");
		Account acc = accountRepo.findById(transaction.getAccount().getId()).get();
		if(!user.getId().equals(acc.getUser().getId()))
			throw new RuntimeException();
		
		MonthlyBudget b;
		if((b = budgetRepo.findByUser_Id(userid))==null) {
			b = new MonthlyBudget();
			b.setAccount(acc);
			b.setUser(user);
			b.setAmount(new BigDecimal(500));
		}
		budgetRepo.save(b);
		if(transaction.getType().equals(TransactionType.EXPENSE)) {
			if(b.getAmount().doubleValue()<=transaction.getAmount().doubleValue()+acc.getBalance().doubleValue())
				throw new BudgetExceededException("Budget exceeded, cannot add transaction");
		}
		
		//classify category from helper
		
		transaction.setUser(user);
		transaction.setCreatedAt(LocalDateTime.now());
		transactionRepo.save(transaction);
		if(transaction.getType().equals(TransactionType.INCOME)) {
			acc.setBalance(acc.getBalance().add(transaction.getAmount()));
		}else {
			acc.setBalance(acc.getBalance().subtract(transaction.getAmount()));
		}
		accountRepo.save(acc);	
		return TransactionMapper.mapToDTO(transaction);
	}
	
	public BigDecimal getTotalIncome(Long userid) throws UserNotFoundException {
		if(!userRepo.existsById(userid))
			throw new UserNotFoundException("user "+ userid+" not found!");
		return transactionRepo.getIncomeSum(userid);
	}
	
	public BigDecimal getTotalExpense(Long userid) throws UserNotFoundException {
		if(!userRepo.existsById(userid))
			throw new UserNotFoundException("user "+ userid+" not found!");
		return transactionRepo.getExpenseSum(userid);
	}
	
	public List<TransactionDTO> getTransaction(Long userid) throws UserNotFoundException {
		if(!userRepo.existsById(userid))
			throw new UserNotFoundException("user "+ userid+" not found!");
		List<Transaction> t = transactionRepo.findByUser_Id(userid);
		List<TransactionDTO> dt = new ArrayList<TransactionDTO>();
		for(Transaction ts:t) {
			dt.add(TransactionMapper.mapToDTO(ts));
		}
		return dt;
	}
	
	
}
