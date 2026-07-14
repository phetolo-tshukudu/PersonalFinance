package com.phetolo.PersonalFinance.services;

import java.math.BigDecimal;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import com.phetolo.PersonalFinance.dto.MonthlyBudgetDTO;
import com.phetolo.PersonalFinance.enums.AccountType;
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
	
	private UserRepository userRepo;
	private AccountRepository accountRepo;
	private MonthlyBudgetRepository budgetRepo;
	
	@Transactional
	public MonthlyBudgetDTO createBudget(Long userid, MonthlyBudget budget) throws Exception {
		if(!userRepo.existsById(userid))
			throw new UserNotFoundException("User "+userid+ " does not exist.");
		if(budgetRepo.findByUser_Id(userid)!=null)
			throw new Exception("Budget already exists. You can only update the monthly budget once one is created");
		if(!accountRepo.existsByUser_IdAndType(userid,AccountType.MAIN))
			throw new AccountNotFoundException("Not account exist for this user.");
		
		Account acc = findAccountType(accountRepo.findByUser_Id(userid), AccountType.MAIN);
		
		if(!acc.getUser().getId().equals(userid))
			throw new Exception("Account does not belong to user "+ userid);
		budget.setAccount(acc);
		budget.setUser(userRepo.findById(userid).get());
		
		return MonthlyBudgetMapper.mapToDTO(budgetRepo.save(budget));
	}
	
	@Modifying
	@Transactional
	public  MonthlyBudgetDTO updateBudget(Long userid, BigDecimal newLimit) throws Exception {
		if(!userRepo.existsById(userid))
			throw new UserNotFoundException("User "+userid+ " does not exist.");
		if(budgetRepo.findByUser_Id(userid)!=null)
			throw new Exception("Budget already exists. You can only update the monthly budget once one is created");
		
		
		return MonthlyBudgetMapper.mapToDTO(budgetRepo.updateBudget(userid, newLimit));
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
	
	@Transactional
	public void deleteBudget(Long userid,Long id) throws Exception {
		if(!userRepo.existsById(userid))
			throw new UserNotFoundException("User "+userid+ " does not exist.");
		if(budgetRepo.findById(id)!=null)
			throw new Exception("Budget already exists. You can only update the monthly budget once one is created");
		budgetRepo.deleteById(id);
		return ;
	}
	
}
