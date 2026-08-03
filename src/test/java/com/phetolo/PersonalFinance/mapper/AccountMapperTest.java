package com.phetolo.PersonalFinance.mapper;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import com.phetolo.PersonalFinance.dto.AccountDTO;
import com.phetolo.PersonalFinance.enums.AccountType;
import com.phetolo.PersonalFinance.model.Account;

public class AccountMapperTest {

	@Test
	void testMapToDTO_WithValidAccount_ShouldReturnAccountDTOWithSameData() {
		// Arrange
		Account account = new Account();
		account.setId(1L);
		account.setType(AccountType.MAIN);
		account.setBalance(new BigDecimal("5000.00"));

		// Act
		AccountDTO dto = AccountMapper.mapToDTO(account);

		// Assert
		assertNotNull(dto);
		assertEquals(AccountType.MAIN, dto.getAccountType());
		assertEquals(new BigDecimal("5000.00"), dto.getBalance());
	}

	@Test
	void testMapToDTO_WithSavingsAccount_ShouldReturnCorrectType() {
		// Arrange
		Account account = new Account();
		account.setType(AccountType.SAVINGS);
		account.setBalance(new BigDecimal("2500.50"));

		// Act
		AccountDTO dto = AccountMapper.mapToDTO(account);

		// Assert
		assertNotNull(dto);
		assertEquals(AccountType.SAVINGS, dto.getAccountType());
		assertEquals(new BigDecimal("2500.50"), dto.getBalance());
	}

	@Test
	void testMapToDTO_WithNullAccount_ShouldThrowNullPointerException() {
		// Act & Assert
		assertThrows(NullPointerException.class, () -> AccountMapper.mapToDTO(null));
	}

	@Test
	void testMapToEntity_WithValidAccountDTO_ShouldReturnAccountWithSameData() {
		// Arrange
		AccountDTO dto = new AccountDTO(AccountType.EMERGENCY, new BigDecimal("1000.00"));

		// Act
		Account account = AccountMapper.mapToEntity(dto);

		// Assert
		assertNotNull(account);
		assertEquals(AccountType.EMERGENCY, account.getType());
		assertEquals(new BigDecimal("1000.00"), account.getBalance());
	}

	@Test
	void testMapToEntity_WithNullDTO_ShouldThrowNullPointerException() {
		// Act & Assert
		assertThrows(NullPointerException.class, () -> AccountMapper.mapToEntity(null));
	}

	@Test
	void testMapToDTO_And_MapToEntity_ShouldBeReversible() {
		// Arrange
		Account originalAccount = new Account();
		originalAccount.setType(AccountType.SAVINGS);
		originalAccount.setBalance(new BigDecimal("3000.75"));

		// Act
		AccountDTO dto = AccountMapper.mapToDTO(originalAccount);
		Account mappedAccount = AccountMapper.mapToEntity(dto);

		// Assert
		assertEquals(originalAccount.getType(), mappedAccount.getType());
		assertEquals(originalAccount.getBalance(), mappedAccount.getBalance());
	}

	@Test
	void testMapToDTO_WithZeroBalance_ShouldReturnCorrectDTO() {
		// Arrange
		Account account = new Account();
		account.setType(AccountType.MAIN);
		account.setBalance(new BigDecimal("0.00"));

		// Act
		AccountDTO dto = AccountMapper.mapToDTO(account);

		// Assert
		assertNotNull(dto);
		assertEquals(new BigDecimal("0.00"), dto.getBalance());
	}

	@Test
	void testMapToEntity_WithLargeBalance_ShouldReturnCorrectEntity() {
		// Arrange
		BigDecimal largeBalance = new BigDecimal("999999.99");
		AccountDTO dto = new AccountDTO(AccountType.SAVINGS, largeBalance);

		// Act
		Account account = AccountMapper.mapToEntity(dto);

		// Assert
		assertNotNull(account);
		assertEquals(largeBalance, account.getBalance());
	}
}
