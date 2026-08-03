package com.phetolo.PersonalFinance.mapper;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import com.phetolo.PersonalFinance.dto.MonthlyBudgetDTO;
import com.phetolo.PersonalFinance.enums.AccountType;
import com.phetolo.PersonalFinance.model.Account;
import com.phetolo.PersonalFinance.model.MonthlyBudget;
import com.phetolo.PersonalFinance.model.User;

public class MonthlyBudgetMapperTest {

	@Test
	void testMapToDTO_WithValidMonthlyBudget_ShouldReturnMonthlyBudgetDTOWithSameData() {
		// Arrange
		User user = new User();
		user.setId(1L);

		Account account = new Account();
		account.setId(1L);
		account.setType(AccountType.MAIN);
		account.setUser(user);

		MonthlyBudget budget = new MonthlyBudget();
		budget.setId(1L);
		budget.setAmount(new BigDecimal("5000.00"));
		budget.setAccount(account);
		budget.setUser(user);

		// Act
		MonthlyBudgetDTO dto = MonthlyBudgetMapper.mapToDTO(budget);

		// Assert
		assertNotNull(dto);
		assertEquals(new BigDecimal("5000.00"), dto.getAmount());
		assertEquals(AccountType.MAIN, dto.getType());
	}

	@Test
	void testMapToDTO_WithSavingsAccount_ShouldReturnCorrectAccountType() {
		// Arrange
		User user = new User();
		Account account = new Account();
		account.setType(AccountType.SAVINGS);
		account.setUser(user);

		MonthlyBudget budget = new MonthlyBudget();
		budget.setAmount(new BigDecimal("2500.00"));
		budget.setAccount(account);
		budget.setUser(user);

		// Act
		MonthlyBudgetDTO dto = MonthlyBudgetMapper.mapToDTO(budget);

		// Assert
		assertNotNull(dto);
		assertEquals(AccountType.SAVINGS, dto.getType());
		assertEquals(new BigDecimal("2500.00"), dto.getAmount());
	}

	@Test
	void testMapToDTO_WithNullBudget_ShouldThrowNullPointerException() {
		// Act & Assert
		assertThrows(NullPointerException.class, () -> MonthlyBudgetMapper.mapToDTO(null));
	}

	@Test
	void testMapToEntity_WithValidMonthlyBudgetDTO_ShouldReturnMonthlyBudgetWithSameAmount() {
		// Arrange
		MonthlyBudgetDTO dto = new MonthlyBudgetDTO(new BigDecimal("3000.00"), AccountType.EMERGENCY);

		// Act
		MonthlyBudget budget = MonthlyBudgetMapper.mapToEntity(dto);

		// Assert
		assertNotNull(budget);
		assertEquals(new BigDecimal("3000.00"), budget.getAmount());
	}

	@Test
	void testMapToEntity_WithNullDTO_ShouldThrowNullPointerException() {
		// Act & Assert
		assertThrows(NullPointerException.class, () -> MonthlyBudgetMapper.mapToEntity(null));
	}

	@Test
	void testMapToDTO_WithDifferentAmounts_ShouldReturnCorrectAmounts() {
		// Arrange
		BigDecimal[] amounts = {
			new BigDecimal("1000.00"),
			new BigDecimal("5000.00"),
			new BigDecimal("10000.00"),
			new BigDecimal("500.50")
		};

		User user = new User();
		Account account = new Account();
		account.setType(AccountType.MAIN);
		account.setUser(user);

		for (BigDecimal amount : amounts) {
			// Arrange
			MonthlyBudget budget = new MonthlyBudget();
			budget.setAmount(amount);
			budget.setAccount(account);
			budget.setUser(user);

			// Act
			MonthlyBudgetDTO dto = MonthlyBudgetMapper.mapToDTO(budget);

			// Assert
			assertEquals(amount, dto.getAmount());
		}
	}

	@Test
	void testMapToEntity_WithLargeAmount_ShouldReturnCorrectEntity() {
		// Arrange
		BigDecimal largeAmount = new BigDecimal("99999.99");
		MonthlyBudgetDTO dto = new MonthlyBudgetDTO(largeAmount, AccountType.SAVINGS);

		// Act
		MonthlyBudget budget = MonthlyBudgetMapper.mapToEntity(dto);

		// Assert
		assertNotNull(budget);
		assertEquals(largeAmount, budget.getAmount());
	}

	@Test
	void testMapToEntity_WithSmallAmount_ShouldReturnCorrectEntity() {
		// Arrange
		BigDecimal smallAmount = new BigDecimal("0.50");
		MonthlyBudgetDTO dto = new MonthlyBudgetDTO(smallAmount, AccountType.MAIN);

		// Act
		MonthlyBudget budget = MonthlyBudgetMapper.mapToEntity(dto);

		// Assert
		assertNotNull(budget);
		assertEquals(smallAmount, budget.getAmount());
	}

	@Test
	void testMapToDTO_WithAllAccountTypes_ShouldMapCorrectly() {
		// Test mapping for each account type
		AccountType[] accountTypes = {
			AccountType.MAIN, AccountType.SAVINGS, AccountType.EMERGENCY
		};

		User user = new User();

		for (AccountType type : accountTypes) {
			// Arrange
			Account account = new Account();
			account.setType(type);
			account.setUser(user);

			MonthlyBudget budget = new MonthlyBudget();
			budget.setAmount(new BigDecimal("5000.00"));
			budget.setAccount(account);
			budget.setUser(user);

			// Act
			MonthlyBudgetDTO dto = MonthlyBudgetMapper.mapToDTO(budget);

			// Assert
			assertEquals(type, dto.getType());
		}
	}
}
