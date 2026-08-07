package com.phetolo.PersonalFinance.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import com.phetolo.PersonalFinance.dto.AccountDTO;
import com.phetolo.PersonalFinance.enums.AccountType;
import com.phetolo.PersonalFinance.enums.Category;
import com.phetolo.PersonalFinance.enums.TransactionType;
import com.phetolo.PersonalFinance.exception.AccountNotFoundException;
import com.phetolo.PersonalFinance.exception.UserNotFoundException;
import com.phetolo.PersonalFinance.mapper.AccountMapper;
import com.phetolo.PersonalFinance.model.Account;
import com.phetolo.PersonalFinance.model.MonthlyBudget;
import com.phetolo.PersonalFinance.model.Transaction;
import com.phetolo.PersonalFinance.repository.AccountRepository;
import com.phetolo.PersonalFinance.repository.MonthlyBudgetRepository;
import com.phetolo.PersonalFinance.repository.TransactionRepository;
import com.phetolo.PersonalFinance.repository.UserRepository;

import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
	
	private final TransactionRepository transactionRepo;
	private final UserRepository userRepo;
	private final AccountRepository accountRepo;
	private final MonthlyBudgetRepository budgetRepo;
	
	/**
	 * Creates a new account for a user.
	 * 
	 * This method validates the user exists, enforces account type constraints, and limits
	 * the number of accounts to a maximum of 4. The first account is automatically set as the MAIN account.
	 * Duplicate account types are not allowed.
	 * 
	 * @param userid the ID of the user creating the account
	 * @param account the Account object containing account details
	 * @return AccountDTO containing the created account information
	 * @throws UserNotFoundException if the user does not exist
	 * @throws Exception if account limit exceeded, duplicate account type, or other issues
	 */
	@Transactional
	public AccountDTO createAccount(Long userid, Account account) throws Exception {
		// Validate that the user exists
		if(!userRepo.existsById(userid))
			throw new UserNotFoundException("user "+ userid+" not found!");
		
		// Fetch existing accounts for the user
		List<Account> acc = null;
		if((acc = accountRepo.findByUser_Id(userid))==null) {
			// If no accounts exist, designate this as the main account
			account.setType(AccountType.MAIN);
		}
		
		// Enforce maximum account limit (4 accounts per user)
		if(acc.size()>3)
			throw new Exception("Limit for number of accounts reached!");
		
		// Prevent duplicate account types
		if(findAccountType(acc, account.getType())!=null)
			throw new Exception("Account type already exists. No duplicates!");
		
		// Set creation timestamp and associate user with account
		account.setCreatedAt(LocalDateTime.now());
		account.setUser(userRepo.findById(userid).get());
		
		// Save and return the new account as DTO
		return AccountMapper.mapToDTO(accountRepo.save(account));
	}
	
	/**
	 * Retrieves all accounts belonging to a specific user.
	 * 
	 * Fetches all accounts associated with a user and returns them as DTOs.
	 * 
	 * @param userid the ID of the user whose accounts to retrieve
	 * @return List of AccountDTO objects representing all user's accounts
	 */
	public List<AccountDTO> getAllAccounts(Long userid){
		// Fetch all accounts for the user from repository
		List<Account> acc = accountRepo.findByUser_Id(userid);
		List<AccountDTO> dto = new ArrayList<>();
		
		// Map each Account entity to AccountDTO
		for(Account a:acc) {
			dto.add(AccountMapper.mapToDTO(a));
		}
		return dto;
	}
	
	/**
	 * Transfers funds between two users' main accounts.
	 * 
	 * This method performs an inter-user fund transfer by debiting the sender's main account
	 * and crediting the receiver's main account. It validates both users exist, checks monthly
	 * budget limits, creates transaction records, and updates account balances atomically.
	 * 
	 * @param userfrom the ID of the user sending the funds
	 * @param userto the ID of the user receiving the funds
	 * @param amount the BigDecimal amount to transfer
	 * @return AccountDTO containing the sender's account after transfer
	 * @throws UserNotFoundException if either user does not exist
	 * @throws InvalidTransactionException if transfer exceeds budget or insufficient balance
	 */
	@Modifying
	@Transactional
	public AccountDTO transfer(Long userfrom,Long userto,BigDecimal amount) throws Exception {
		// Validate sender exists
		if(!userRepo.existsById(userfrom))
			throw new UserNotFoundException("user "+ userfrom+" not found!");
		
		// Validate receiver exists
		if(!userRepo.existsById(userto))
			throw new UserNotFoundException("user "+ userto+" not found!");
		
		// Retrieve sender's monthly budget for transfer validation
		MonthlyBudget b = budgetRepo.findByUser_Id(userfrom);
		List<Account> accounts = accountRepo.findByUser_Id(userfrom);
		Account sender = null;
		
		// Transfers can only be made from the MAIN account
		sender = findAccountType(accounts,AccountType.MAIN);
		
		// Validate transfer amount against budget limit and available balance
		if(sender.getBalance().add(amount).doubleValue() >= b.getAmount().doubleValue() || sender.getBalance().doubleValue()<amount.doubleValue())
			throw new InvalidTransactionException("Monthly limit or balance exceeded");
		
		// Debit sender's account and create outgoing transaction record
		Account a = accountRepo.subtractBalance(userfrom, amount);
		accountRepo.save(a);
		Transaction t = new Transaction();
		t.setAccount(a);
		t.setCreatedAt(LocalDateTime.now());
		t.setAmount(amount);
		t.setType(TransactionType.EXPENSE);
		t.setCategory(Category.TRANSFER);
		t.setUser(sender.getUser());
		transactionRepo.save(t);
		
		// Credit receiver's account and create incoming transaction record
		Account a2 = accountRepo.addBalance(userto, amount);
		accountRepo.save(a2);
		Transaction t2 = new Transaction();
		t2.setAccount(a2);
		t2.setAmount(amount);
		t2.setType(TransactionType.INCOME);
		t2.setCategory(Category.TRANSFER);
		t2.setUser(a2.getUser());
		transactionRepo.save(t2);
		
		return AccountMapper.mapToDTO(a);
	}

	/**
	 * Searches for an account of a specific type within a list of accounts.
	 * 
	 * Helper method that searches through a user's accounts to find one matching the specified account type.
	 * 
	 * @param accounts the list of accounts to search through
	 * @param type the AccountType to find
	 * @return the Account matching the type, or null if not found
	 * @throws Exception if the accounts list is null
	 */
	private Account findAccountType(List<Account> accounts, AccountType type) throws Exception {
		// Validate that accounts list is not null
		if(accounts == null) {
			throw new Exception("No accounts exist for this user");
		}
		
		// Iterate through accounts to find matching type
		for(Account s:accounts) {
			if(s.getType().equals(type)) {
				return s;
			}
		
		}
		return null;
	}

	/**
	 * Retrieves a specific account belonging to a user.
	 * 
	 * Fetches an account by ID and validates that it belongs to the specified user.
	 * 
	 * @param userId the ID of the user owning the account
	 * @param id the ID of the account to retrieve
	 * @return AccountDTO containing the account details
	 * @throws UserNotFoundException if the user does not exist
	 * @throws AccountNotFoundException if the account does not exist
	 */
	public AccountDTO getAccount(Long userId, Long id) throws UserNotFoundException, AccountNotFoundException {
		// Validate user exists
		if(!userRepo.existsById(userId))
			throw new UserNotFoundException("user "+ userId+" not found!");
		
		// Validate account exists
		if(!accountRepo.existsById(id))
			throw new AccountNotFoundException("Account does not exist!");
		
		// Return account mapped to DTO
		return AccountMapper.mapToDTO(accountRepo.findById(id).get());
	}
	
	
	
}
