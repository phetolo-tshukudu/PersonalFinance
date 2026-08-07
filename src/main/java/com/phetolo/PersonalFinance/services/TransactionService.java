package com.phetolo.PersonalFinance.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import javax.security.auth.login.AccountNotFoundException;


import org.springframework.stereotype.Service;

import com.phetolo.PersonalFinance.dto.TransactionDTO;
import com.phetolo.PersonalFinance.enums.Category;
import com.phetolo.PersonalFinance.enums.TransactionType;
import com.phetolo.PersonalFinance.exception.BudgetExceededException;
import com.phetolo.PersonalFinance.exception.TransactionNotFoundException;
import com.phetolo.PersonalFinance.exception.UserNotFoundException;
import com.phetolo.PersonalFinance.mapper.TransactionMapper;
import com.phetolo.PersonalFinance.model.Account;
import com.phetolo.PersonalFinance.model.MonthlyBudget;
import com.phetolo.PersonalFinance.model.Transaction;
import com.phetolo.PersonalFinance.model.User;
import com.phetolo.PersonalFinance.repository.AccountRepository;
import com.phetolo.PersonalFinance.repository.MonthlyBudgetRepository;
import com.phetolo.PersonalFinance.repository.TransactionRepository;
import com.phetolo.PersonalFinance.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter

@Service

public class TransactionService {
	private final TransactionRepository transactionRepo;
	private final UserRepository userRepo;
	private final AccountRepository accountRepo;
	private final MonthlyBudgetRepository budgetRepo;
	
	/**
	 * Adds a new transaction to a user's account.
	 * 
	 * This method processes a transaction (income or expense) for a specified user and account.
	 * It validates the user and account exist, creates a default budget if needed, validates
	 * against the monthly budget limit for expenses, classifies the transaction category,
	 * and updates the associated account balance accordingly.
	 * 
	 * @param userid the ID of the user adding the transaction
	 * @param transaction the Transaction object containing transaction details
	 * @return TransactionDTO containing the created transaction information
	 * @throws UserNotFoundException if the user does not exist
	 * @throws AccountNotFoundException if the account does not exist
	 * @throws BudgetExceededException if the expense transaction exceeds monthly budget limit
	 */
	@Transactional
	public TransactionDTO addTransaction(Long userid,Transaction transaction) throws UserNotFoundException, AccountNotFoundException, BudgetExceededException {
		// Validate user exists
		if(!userRepo.existsById(userid))
			throw new UserNotFoundException("user "+ userid+" not found!");
		
		User user = userRepo.findById(userid).get();
		
		// Validate account exists
		if(!accountRepo.existsById(transaction.getAccount().getId()))
			throw new AccountNotFoundException("Account "+transaction.getAccount().getId()+" not found!");
		
		Account acc = accountRepo.findById(transaction.getAccount().getId()).get();
		
		// Verify the account belongs to the specified user
		if(!user.getId().equals(acc.getUser().getId()))
			throw new RuntimeException();
		
		// Retrieve or create monthly budget for the user
		MonthlyBudget b;
		if((b = budgetRepo.findByUser_Id(userid))==null) {
			// Create default budget with $500 limit if none exists
			b = new MonthlyBudget();
			b.setAccount(acc);
			b.setUser(user);
			b.setAmount(new BigDecimal(500));
		}
		budgetRepo.save(b);
		
		// Validate expense transactions against monthly budget
		if(transaction.getType().equals(TransactionType.EXPENSE)) {
			if(b.getAmount().doubleValue()<=transaction.getAmount().doubleValue()+acc.getBalance().doubleValue())
				throw new BudgetExceededException("Budget exceeded, cannot add transaction");
		}
		
		// Classify and assign transaction category
		transaction.setCategory(classifyCategory(transaction.getCategory()));
		
		// Set transaction metadata (user and timestamp)
		transaction.setUser(user);
		transaction.setCreatedAt(LocalDateTime.now());
		transactionRepo.save(transaction);
		
		// Update account balance based on transaction type
		if(transaction.getType().equals(TransactionType.INCOME)) {
			// Income increases account balance
			acc.setBalance(acc.getBalance().add(transaction.getAmount()));
		}else {
			// Expense decreases account balance
			acc.setBalance(acc.getBalance().subtract(transaction.getAmount()));
		}
		accountRepo.save(acc);	
		return TransactionMapper.mapToDTO(transaction);
	}
	
	/**
	 * Classifies and standardizes transaction categories.
	 * 
	 * Helper method that validates and returns the appropriate category for a transaction.
	 * If the category is not recognized, it defaults to OTHER.
	 * 
	 * @param category the Category to classify
	 * @return the standardized Category enum value
	 */
	private Category classifyCategory(Category category) {
		// Validate and return the category, defaulting to OTHER if unknown
		switch(category) {
			case FOOD:
				return Category.FOOD;
			case ENTERTAINMENT:
				return Category.ENTERTAINMENT;
			case TRANSPORT:
				return Category.TRANSPORT;
			case UTILITIES:
				return Category.UTILITIES;
			case TRANSFER:
				return Category.TRANSFER;
			case SALARY:
				return Category.SALARY;
		}
		return Category.OTHER;
	}

	/**
	 * Calculates the total income for a user.
	 * 
	 * Retrieves the sum of all income transactions for a specified user.
	 * 
	 * @param userid the ID of the user
	 * @return BigDecimal representing the total income amount
	 * @throws UserNotFoundException if the user does not exist
	 */
	public BigDecimal getTotalIncome(Long userid) throws UserNotFoundException {
		// Validate user exists
		if(!userRepo.existsById(userid))
			throw new UserNotFoundException("user "+ userid+" not found!");
		
		// Query repository for total income sum
		return transactionRepo.getIncomeSum(userid);
	}
	
	/**
	 * Calculates the total expenses for a user.
	 * 
	 * Retrieves the sum of all expense transactions for a specified user.
	 * 
	 * @param userid the ID of the user
	 * @return BigDecimal representing the total expense amount
	 * @throws UserNotFoundException if the user does not exist
	 */
	public BigDecimal getTotalExpense(Long userid) throws UserNotFoundException {
		// Validate user exists
		if(!userRepo.existsById(userid))
			throw new UserNotFoundException("user "+ userid+" not found!");
		
		// Query repository for total expense sum
		return transactionRepo.getExpenseSum(userid);
	}
	
	/**
	 * Retrieves all transactions for a specific user.
	 * 
	 * Fetches all transactions belonging to a user and returns them as DTOs.
	 * 
	 * @param userid the ID of the user whose transactions to retrieve
	 * @return List of TransactionDTO objects representing all user's transactions
	 * @throws UserNotFoundException if the user does not exist
	 */
	public List<TransactionDTO> getAllTransaction(Long userid) throws UserNotFoundException {
		// Validate user exists
		if(!userRepo.existsById(userid))
			throw new UserNotFoundException("user "+ userid+" not found!");
		
		// Fetch all transactions for the user from repository
		List<Transaction> t = transactionRepo.findByUser_Id(userid);
		List<TransactionDTO> dt = new ArrayList<TransactionDTO>();
		
		// Map each Transaction entity to TransactionDTO
		for(Transaction ts:t) {
			dt.add(TransactionMapper.mapToDTO(ts));
		}
		return dt;
	}

	/**
	 * Retrieves a specific transaction by ID for a user.
	 * 
	 * Fetches a transaction by ID and validates it belongs to the specified user.
	 * 
	 * @param userid the ID of the user
	 * @param id the ID of the transaction to retrieve
	 * @return TransactionDTO containing the transaction details
	 * @throws UserNotFoundException if the user does not exist
	 * @throws TransactionNotFoundException if the transaction does not exist
	 */
	public TransactionDTO getTransaction(Long userid,Long id) throws UserNotFoundException, TransactionNotFoundException {
		// Validate user exists
		if(!userRepo.existsById(userid))
			throw new UserNotFoundException("user "+ userid+" not found!");
		
		// Validate transaction exists
		if(!transactionRepo.existsById(id))
			throw new TransactionNotFoundException("Transaction does not exist!");
		
		// Return transaction mapped to DTO
		return TransactionMapper.mapToDTO(transactionRepo.findById(id).get());
	}
	
	
}
