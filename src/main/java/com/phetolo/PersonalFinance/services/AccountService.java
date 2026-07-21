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
	
	@Transactional
	public AccountDTO createAccount(Long userid, Account account) throws Exception {
		if(!userRepo.existsById(userid))
			throw new UserNotFoundException("user "+ userid+" not found!");
		
		List<Account> acc = null;
		if((acc = accountRepo.findByUser_Id(userid))==null) {
			//no other accounts if there isn't a main account
			account.setType(AccountType.MAIN);
		}
		
		if(acc.size()>3)
			throw new Exception("Limit for number of accounts reached!");
		if(findAccountType(acc, account.getType())!=null)
			throw new Exception("Account type already exists. No duplicates!");
		
		account.setCreatedAt(LocalDateTime.now());
		account.setUser(userRepo.findById(userid).get());
		
		
		return AccountMapper.mapToDTO(accountRepo.save(account));
	}
	
	public List<AccountDTO> getAllAccounts(Long userid){
		List<Account> acc = accountRepo.findByUser_Id(userid);
		List<AccountDTO> dto = new ArrayList<>();
		for(Account a:acc) {
			dto.add(AccountMapper.mapToDTO(a));
		}
		return dto;
	}
	
	@Modifying
	@Transactional
	public AccountDTO transfer(Long userfrom,Long userto,BigDecimal amount) throws Exception {
		if(!userRepo.existsById(userfrom))
			throw new UserNotFoundException("user "+ userfrom+" not found!");
		if(!userRepo.existsById(userto))
			throw new UserNotFoundException("user "+ userto+" not found!");
		MonthlyBudget b = budgetRepo.findByUser_Id(userfrom);
		List<Account> accounts = accountRepo.findByUser_Id(userfrom);
		Account sender = null;
		
		//Transfers can only be made from the MAIN account
		sender = findAccountType(accounts,AccountType.MAIN);
			
		
		if(sender.getBalance().add(amount).doubleValue() >= b.getAmount().doubleValue() || sender.getBalance().doubleValue()<amount.doubleValue())
			throw new InvalidTransactionException("Monthly limit or balance exceeded");
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

	private Account findAccountType(List<Account> accounts, AccountType type) throws Exception {
		// TODO Auto-generated method stub
		if(accounts == null) {
			throw new Exception("No accounts exist for this user");
		}
		
		for(Account s:accounts) {
			if(s.getType().equals(type)) {
				return s;
			}
		
		}
		throw new InvalidTransactionException("No "+type+ " account found. Transactions can only be made from a main account.");
	}

	public AccountDTO getAccount(Long userId, Long id) throws UserNotFoundException, AccountNotFoundException {
		// TODO Auto-generated method stub
		if(!userRepo.existsById(userId))
			throw new UserNotFoundException("user "+ userId+" not found!");
		if(!accountRepo.existsById(id))
			throw new AccountNotFoundException("Account does not exist!");
		return AccountMapper.mapToDTO(accountRepo.findById(id).get());
	}
	
	
	
}
