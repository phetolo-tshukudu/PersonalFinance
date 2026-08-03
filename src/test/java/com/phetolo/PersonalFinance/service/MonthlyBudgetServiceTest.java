package com.phetolo.PersonalFinance.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.phetolo.PersonalFinance.dto.MonthlyBudgetDTO;
import com.phetolo.PersonalFinance.enums.AccountType;
import com.phetolo.PersonalFinance.exception.BudgetNotFoundException;
import com.phetolo.PersonalFinance.exception.UserNotFoundException;
import com.phetolo.PersonalFinance.model.Account;
import com.phetolo.PersonalFinance.model.MonthlyBudget;
import com.phetolo.PersonalFinance.model.User;
import com.phetolo.PersonalFinance.repository.AccountRepository;
import com.phetolo.PersonalFinance.repository.MonthlyBudgetRepository;
import com.phetolo.PersonalFinance.repository.UserRepository;
import com.phetolo.PersonalFinance.services.MonthlyBudgetService;

@ExtendWith(MockitoExtension.class)
public class MonthlyBudgetServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private MonthlyBudgetRepository budgetRepository;

	@InjectMocks
	private MonthlyBudgetService budgetService;

	@Test
	void testCreateBudget_WhenUserExistsAndNoExistingBudget_ShouldCreateBudget() throws Exception {
		// Arrange
		Long userId = 1L;
		User user = new User();
		user.setId(userId);

		Account mainAccount = new Account();
		mainAccount.setId(1L);
		mainAccount.setType(AccountType.MAIN);
		mainAccount.setUser(user);

		List<Account> accounts = new ArrayList<>();
		accounts.add(mainAccount);

		MonthlyBudget budget = new MonthlyBudget();
		budget.setAmount(new BigDecimal("5000"));

		MonthlyBudget savedBudget = new MonthlyBudget();
		savedBudget.setId(1L);
		savedBudget.setAmount(new BigDecimal("5000"));
		savedBudget.setUser(user);
		savedBudget.setAccount(mainAccount);

		when(userRepository.existsById(userId)).thenReturn(true);
		when(budgetRepository.findByUser_Id(userId)).thenReturn(null);
		when(accountRepository.existsByUser_IdAndType(userId, AccountType.MAIN)).thenReturn(true);
		when(accountRepository.findByUser_Id(userId)).thenReturn(accounts);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(budgetRepository.save(any(MonthlyBudget.class))).thenReturn(savedBudget);

		// Act
		MonthlyBudgetDTO result = budgetService.createBudget(userId, budget);

		// Assert
		assertNotNull(result);
		assertEquals(new BigDecimal("5000"), savedBudget.getAmount());
	}

	@Test
	void testCreateBudget_WhenUserNotExists_ShouldThrowUserNotFoundException() {
		// Arrange
		Long userId = 1L;
		MonthlyBudget budget = new MonthlyBudget();

		when(userRepository.existsById(userId)).thenReturn(false);

		// Act & Assert
		assertThrows(UserNotFoundException.class, () -> budgetService.createBudget(userId, budget));
	}

	@Test
	void testCreateBudget_WhenBudgetAlreadyExists_ShouldThrowException() {
		// Arrange
		Long userId = 1L;
		MonthlyBudget existingBudget = new MonthlyBudget();
		existingBudget.setId(1L);

		MonthlyBudget budget = new MonthlyBudget();

		when(userRepository.existsById(userId)).thenReturn(true);
		when(budgetRepository.findByUser_Id(userId)).thenReturn(existingBudget);

		// Act & Assert
		assertThrows(Exception.class, () -> budgetService.createBudget(userId, budget));
	}

	@Test
	void testCreateBudget_WhenNoMainAccount_ShouldThrowException() throws Exception {
		// Arrange
		Long userId = 1L;
		MonthlyBudget budget = new MonthlyBudget();

		when(userRepository.existsById(userId)).thenReturn(true);
		when(budgetRepository.findByUser_Id(userId)).thenReturn(null);
		when(accountRepository.existsByUser_IdAndType(userId, AccountType.MAIN)).thenReturn(false);

		// Act & Assert
		assertThrows(Exception.class, () -> budgetService.createBudget(userId, budget));
	}

	@Test
	void testUpdateBudget_WhenUserExists_ShouldUpdateBudgetAmount() throws Exception {
		// Arrange
		Long userId = 1L;
		BigDecimal newLimit = new BigDecimal("7000");

		MonthlyBudget updatedBudget = new MonthlyBudget();
		updatedBudget.setId(1L);
		updatedBudget.setAmount(newLimit);

		when(userRepository.existsById(userId)).thenReturn(true);
		when(budgetRepository.findByUser_Id(userId)).thenReturn(null);
		when(budgetRepository.updateBudget(userId, newLimit)).thenReturn(updatedBudget);

		// Act
		MonthlyBudgetDTO result = budgetService.updateBudget(userId, newLimit);

		// Assert
		assertNotNull(result);
		assertEquals(newLimit, updatedBudget.getAmount());
	}

	@Test
	void testUpdateBudget_WhenUserNotExists_ShouldThrowUserNotFoundException() {
		// Arrange
		Long userId = 1L;
		BigDecimal newLimit = new BigDecimal("7000");

		when(userRepository.existsById(userId)).thenReturn(false);

		// Act & Assert
		assertThrows(UserNotFoundException.class, () -> budgetService.updateBudget(userId, newLimit));
	}

	@Test
	void testDeleteBudget_WhenUserExists_ShouldDeleteBudget() throws Exception {
		// Arrange
		Long userId = 1L;
		Long budgetId = 1L;

		when(userRepository.existsById(userId)).thenReturn(true);
		when(budgetRepository.findById(budgetId)).thenReturn(Optional.empty());

		// Act & Assert - should not throw exception
		budgetService.deleteBudget(userId, budgetId);
	}

	@Test
	void testDeleteBudget_WhenUserNotExists_ShouldThrowUserNotFoundException() {
		// Arrange
		Long userId = 1L;
		Long budgetId = 1L;

		when(userRepository.existsById(userId)).thenReturn(false);

		// Act & Assert
		assertThrows(UserNotFoundException.class, () -> budgetService.deleteBudget(userId, budgetId));
	}

	@Test
	void testGetBudget_WhenUserAndBudgetExist_ShouldReturnMonthlyBudgetDTO() throws UserNotFoundException, BudgetNotFoundException {
		// Arrange
		Long userId = 1L;
		User user = new User();
		user.setId(userId);

		MonthlyBudget budget = new MonthlyBudget();
		budget.setId(1L);
		budget.setAmount(new BigDecimal("5000"));
		budget.setUser(user);

		when(userRepository.existsById(userId)).thenReturn(true);
		when(budgetRepository.existsByUser_Id(userId)).thenReturn(true);
		when(budgetRepository.findByUser_Id(userId)).thenReturn(budget);

		// Act
		MonthlyBudgetDTO result = budgetService.getBudget(userId);

		// Assert
		assertNotNull(result);
		assertEquals(new BigDecimal("5000"), budget.getAmount());
	}

	@Test
	void testGetBudget_WhenUserNotExists_ShouldThrowUserNotFoundException() {
		// Arrange
		Long userId = 1L;

		when(userRepository.existsById(userId)).thenReturn(false);

		// Act & Assert
		assertThrows(UserNotFoundException.class, () -> budgetService.getBudget(userId));
	}

	@Test
	void testGetBudget_WhenBudgetNotExists_ShouldThrowBudgetNotFoundException() {
		// Arrange
		Long userId = 1L;

		when(userRepository.existsById(userId)).thenReturn(true);
		when(budgetRepository.existsByUser_Id(userId)).thenReturn(false);

		// Act & Assert
		assertThrows(BudgetNotFoundException.class, () -> budgetService.getBudget(userId));
	}
}
