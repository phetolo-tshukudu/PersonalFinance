package com.phetolo.PersonalFinance.services;

import java.math.BigDecimal;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import com.phetolo.PersonalFinance.dto.MonthlyBudgetDTO;
import com.phetolo.PersonalFinance.enums.AccountType;
import com.phetolo.PersonalFinance.exception.BudgetNotFoundException;
import com.phetolo.PersonalFinance.exception.UserNotFoundException;
import com.phetolo.PersonalFinance.mapper.MonthlyBudgetMapper;
import com.phetolo.PersonalFinance.model.Account;
import com.phetolo.PersonalFinance.model.MonthlyBudget;
import com.phetolo.PersonalFinance.repository.AccountRepository;
import com.phetolo.PersonalFinance.repository.MonthlyBudgetRepository;
import com.phetolo.PersonalFinance.repository.UserRepository;

import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonthlyBudgetService {
	
	private final UserRepository userRepo;
	private final AccountRepository accountRepo;
	private final MonthlyBudgetRepository budgetRepo;
	
	/**
	 * Creates a new monthly budget for a user.
	 * 
	 * This method initializes a monthly budget with a specified limit for expense tracking.
	 * It validates that the user exists, no budget already exists (one budget per user),
	 * and the user has a MAIN account. The budget is associated with the user's main account.
	 * 
	 * @param userid the ID of the user creating the budget
	 * @param budget the MonthlyBudget object containing the budget amount/limit
	 * @return MonthlyBudgetDTO containing the created budget information
	 * @throws UserNotFoundException if the user does not exist
	 * @throws Exception if budget already exists or user has no main account
	 */
	@Transactional
	public MonthlyBudgetDTO createBudget(Long userid, MonthlyBudget budget) throws Exception {
		// Validate user exists
		if(!userRepo.existsById(userid))
			throw new UserNotFoundException("User "+userid+ " does not exist.");
		
		// Ensure only one budget per user
		if(budgetRepo.findByUser_Id(userid)!=null)
			throw new Exception("Budget already exists. You can only update the monthly budget once one is created");
		
		// Verify user has a MAIN account
		if(!accountRepo.existsByUser_IdAndType(userid,AccountType.MAIN))
			throw new AccountNotFoundException("Not account exist for this user.");
		
		// Retrieve the user's main account
		Account acc = findAccountType(accountRepo.findByUser_Id(userid), AccountType.MAIN);
		
		// Verify the account belongs to the specified user
		if(!acc.getUser().getId().equals(userid))
			throw new Exception("Account does not belong to user "+ userid);
		
		// Associate budget with user and their main account
		budget.setAccount(acc);
		budget.setUser(userRepo.findById(userid).get());
		
		// Save and return the new budget as DTO
		return MonthlyBudgetMapper.mapToDTO(budgetRepo.save(budget));
	}
	
	/**
	 * Updates the monthly budget limit for an existing user budget.
	 * 
	 * Modifies the spending limit of an existing monthly budget.
	 * 
	 * @param userid the ID of the user whose budget to update
	 * @param newLimit the new BigDecimal budget limit
	 * @return MonthlyBudgetDTO containing the updated budget information
	 * @throws UserNotFoundException if the user does not exist
	 * @throws Exception if no budget exists for the user
	 */
	@Modifying
	@Transactional
	public  MonthlyBudgetDTO updateBudget(Long userid, BigDecimal newLimit) throws Exception {
		// Validate user exists
		if(!userRepo.existsById(userid))
			throw new UserNotFoundException("User "+userid+ " does not exist.");
		
		// Check if budget exists before updating
		if(budgetRepo.findByUser_Id(userid)!=null)
			throw new Exception("Budget already exists. You can only update the monthly budget once one is created");
		
		// Update the budget limit in repository and return as DTO
		return MonthlyBudgetMapper.mapToDTO(budgetRepo.updateBudget(userid, newLimit));
	}
	
	/**
	 * Searches for an account of a specific type within a list of accounts.
	 * 
	 * Helper method that locates a user's account matching a specified account type.
	 * Used to validate and retrieve specific account types such as MAIN or SAVINGS.
	 * 
	 * @param accounts the list of accounts to search through
	 * @param type the AccountType to find
	 * @return the Account matching the type
	 * @throws Exception if no accounts exist or matching account type not found
	 */
	private Account findAccountType(List<Account> accounts, AccountType type) throws Exception {
		// Validate accounts list is not null
		if(accounts == null) {
			throw new Exception("No accounts exist for this user");
		}
		
		// Iterate through accounts to find matching type
		for(Account s:accounts) {
			if(s.getType().equals(type)) {
				return s;
			}
		
		}
		// Throw exception if the required account type is not found
		throw new InvalidTransactionException("No "+type+ " account found. Transactions can only be made from a main account.");
	}
	
	/**
	 * Deletes a monthly budget for a user.
	 * 
	 * Removes a budget record from the system. Validates the user exists before deletion.
	 * 
	 * @param userid the ID of the user whose budget to delete
	 * @param id the ID of the budget to delete
	 * @throws UserNotFoundException if the user does not exist
	 * @throws Exception if budget does not exist or other issues
	 */
	@Transactional
	public void deleteBudget(Long userid,Long id) throws Exception {
		// Validate user exists
		if(!userRepo.existsById(userid))
			throw new UserNotFoundException("User "+userid+ " does not exist.");
		
		// Verify budget exists before attempting deletion
		if(budgetRepo.findById(id)!=null)
			throw new Exception("Budget already exists. You can only update the monthly budget once one is created");
		
		// Delete the budget from repository
		budgetRepo.deleteById(id);
		return ;
	}

	/**
	 * Retrieves the monthly budget for a specific user.
	 * 
	 * Fetches the monthly budget configuration and limit for a user.
	 * 
	 * @param userId the ID of the user whose budget to retrieve
	 * @return MonthlyBudgetDTO containing the user's budget information
	 * @throws UserNotFoundException if the user does not exist
	 * @throws BudgetNotFoundException if no budget exists for the user
	 */
	public MonthlyBudgetDTO getBudget(Long userId) throws UserNotFoundException, BudgetNotFoundException {
		// Validate user exists
		if(!userRepo.existsById(userId))
			throw new UserNotFoundException("User "+userId+ " does not exist.");
		
		// Verify budget exists for the user
		if(!budgetRepo.existsByUser_Id(userId))
			throw new BudgetNotFoundException("Budget does not exist!");
		
		// Return user's budget mapped to DTO
		return MonthlyBudgetMapper.mapToDTO(budgetRepo.findByUser_Id(userId));
	}
	
}
