package com.phetolo.PersonalFinance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.phetolo.PersonalFinance.dto.AccountDTO;
import com.phetolo.PersonalFinance.enums.AccountType;
import com.phetolo.PersonalFinance.enums.Category;
import com.phetolo.PersonalFinance.enums.TransactionType;
import com.phetolo.PersonalFinance.exception.UserNotFoundException;
import com.phetolo.PersonalFinance.model.Account;
import com.phetolo.PersonalFinance.model.MonthlyBudget;
import com.phetolo.PersonalFinance.model.Transaction;
import com.phetolo.PersonalFinance.model.User;
import com.phetolo.PersonalFinance.repository.AccountRepository;
import com.phetolo.PersonalFinance.repository.MonthlyBudgetRepository;
import com.phetolo.PersonalFinance.repository.TransactionRepository;
import com.phetolo.PersonalFinance.repository.UserRepository;
import com.phetolo.PersonalFinance.services.AccountService;


@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
	@Mock
	private AccountRepository accountRepo;
	@Mock
	private UserRepository userRepo;
	@Mock
    private TransactionRepository transactionRepo;

    @Mock
    private MonthlyBudgetRepository budgetRepo;
    
    
	@InjectMocks
	private AccountService accountService;
	
	
    @Test
    void createAccount_WhenUserExists_ShouldReturnAccountDTO() throws Exception {

        // Arrange
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        Account account = new Account();
        account.setId(2L);
        account.setType(AccountType.SAVINGS);
        account.setBalance(BigDecimal.valueOf(12345));
        Account existingAccount = new Account();
        existingAccount.setType(AccountType.MAIN);
        List<Account> existingAccounts = new ArrayList<>();
        existingAccounts.add(existingAccount);

        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(accountRepo.findByUser_Id(userId)).thenReturn(existingAccounts);
        when(accountRepo.save(any(Account.class))).thenReturn(account);

        // Act
        AccountDTO dto = accountService.createAccount(userId, account);

        // Assert
	        assertNotNull(dto);
	        assertEquals(account.getBalance(), dto.getBalance());
	        assertEquals(account.getType(), dto.getAccountType());
	        
	        
	        verify(userRepo).existsById(userId);
	        verify(userRepo).findById(userId);
	        verify(accountRepo).findByUser_Id(userId);
	        verify(accountRepo).save(account);
    }
    
    @Test
    void createAccount_ThrowsException_WhenUserDNE() {
    	Long userId = 1L;

       

        Account account = new Account();
        account.setId(2L);
        account.setType(AccountType.SAVINGS);
        account.setBalance(BigDecimal.valueOf(12345));
        
        when(userRepo.existsById(userId)).thenReturn(false);
        assertThrows(UserNotFoundException.class, ()-> accountService.createAccount(userId, account));
        
        verify(userRepo).existsById(userId);
        verifyNoInteractions(accountRepo);
    }
    
    
    
    @Test
    void getAllAccountsReturnsAllAccountsOfAUser() {
    	Long userId = 1L;
    	User user = new User();
    	user.setId(userId);
    	List<Account> existingAccounts = new ArrayList<>();
    	
    	Account acc1 = new Account(), acc2 = new Account();
    	acc1.setUser(user);
    	acc1.setType(AccountType.SAVINGS);
    	acc2.setUser(user);
    	acc2.setType(AccountType.EMERGENCY);
    	
    	existingAccounts.add(acc1);
    	existingAccounts.add(acc2);
    	when(accountRepo.findByUser_Id(userId)).thenReturn(existingAccounts);
    	
    	//Action 
    	List<AccountDTO> accounts = accountService.getAllAccounts(userId);
    	assertNotNull(accounts);
    	assertEquals(2, accounts.size());
    	assertEquals(acc1.getType(), accounts.get(0).getAccountType());
    	assertEquals(acc2.getType(), accounts.get(1).getAccountType());
    	
    	verify(accountRepo).findByUser_Id(userId);
    }
    
    @Test
    void transferShouldTransferMoneyIfUsersExist() throws Exception {
    	Long userFrom = 1L;
    	Long userTo = 2L;
    	User userF = new User();
    	User userT = new User();
    	userF.setId(userFrom);
    	userT.setId(userTo);
    	BigDecimal amount = new BigDecimal(100);
    	
    	MonthlyBudget b =  new MonthlyBudget();
    	b.setAmount(new BigDecimal(500));
    	
    	
    	Account acc1 = new Account(), acc2 = new Account();
    	acc1.setUser(userF);
    	acc1.setType(AccountType.MAIN);
    	acc1.setBalance(new BigDecimal(200));
    	acc2.setUser(userF);
    	acc2.setType(AccountType.EMERGENCY);
    	
    	Account acc1AfterTransfer = new Account();
    	acc1AfterTransfer.setUser(userF);
    	acc1AfterTransfer.setType(AccountType.MAIN);
    	acc1AfterTransfer.setBalance(new BigDecimal(100));
    	
    	List<Account> existingAccounts = List.of(acc1, acc2) ;
    	
    	Account to = new Account();
    	to.setUser(userT);
    	to.setType(AccountType.MAIN);
    	to.setBalance(amount);
    	Transaction tfrom = new Transaction(), tTo = new Transaction();
    	tfrom.setAccount(acc1);
    	tfrom.setAmount(amount);
    	tfrom.setType(TransactionType.EXPENSE);
    	tfrom.setCategory(Category.TRANSFER);
    	tfrom.setUser(userF);
    	
    	tTo.setAccount(to);
    	tTo.setAmount(amount);
    	tTo.setType(TransactionType.INCOME);
    	tTo.setCategory(Category.TRANSFER);
    	tTo.setUser(userT);
    	
    	when(userRepo.existsById(userFrom)).thenReturn(true);
    	when(userRepo.existsById(userTo)).thenReturn(true);
    	when(budgetRepo.findByUser_Id(userFrom)).thenReturn(b);
    	when(accountRepo.findByUser_Id(userFrom)).thenReturn(existingAccounts);
    	when(accountRepo.subtractBalance(userFrom, amount)).thenReturn(acc1AfterTransfer);
    	when(accountRepo.save(any(Account.class))).thenReturn(acc1AfterTransfer).thenReturn(to);
    	when(transactionRepo.save(any(Transaction.class))).thenReturn(tfrom).thenReturn(tTo);
    	when(accountRepo.addBalance(userTo, amount)).thenReturn(to);
    	

    	
    	//Action
    	AccountDTO result = accountService.transfer(userFrom, userTo, amount);
    	assertNotNull(result);
    	assertEquals(result.getBalance(), acc1AfterTransfer.getBalance());
    	assertEquals(AccountType.MAIN, result.getAccountType());
    	
    	verify(userRepo).existsById(userFrom);
    	verify(userRepo).existsById(userTo);
    	verify(budgetRepo).findByUser_Id(userFrom);
    	verify(accountRepo).findByUser_Id(userFrom);
    	verify(accountRepo).subtractBalance(userFrom, amount);
    	verify(accountRepo,times(2)).save(any(Account.class));
    	verify(transactionRepo,times(2)).save(any(Transaction.class));
    	verify(accountRepo).addBalance(userTo, amount);
    	
    }
    
    @Test
    void transferShouldThrowWhenUserDNE() {
    	Long userFrom = 1L;
    	Long userTo = 2L;
    	BigDecimal amount = new BigDecimal(100);
    	
    	when(userRepo.existsById(any(Long.class))).thenReturn(false);
    	assertThrows(UserNotFoundException.class, ()->accountService.transfer(userFrom, userTo, amount));
    	
    	verify(userRepo).existsById(userFrom);

        // Execution should stop immediately
        verify(userRepo, never()).existsById(userTo);
        verifyNoInteractions(budgetRepo);
        verifyNoInteractions(accountRepo);
        verifyNoInteractions(transactionRepo);
    }
}
