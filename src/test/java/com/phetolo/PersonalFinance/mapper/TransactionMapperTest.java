package com.phetolo.PersonalFinance.mapper;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import com.phetolo.PersonalFinance.dto.TransactionDTO;
import com.phetolo.PersonalFinance.enums.Category;
import com.phetolo.PersonalFinance.model.Transaction;

public class TransactionMapperTest {

	@Test
	void testMapToDTO_WithValidTransaction_ShouldReturnTransactionDTOWithSameData() {
		// Arrange
		Transaction transaction = new Transaction();
		transaction.setId(1L);
		transaction.setAmount(new BigDecimal("100.00"));
		transaction.setCategory(Category.FOOD);

		// Act
		TransactionDTO dto = TransactionMapper.mapToDTO(transaction);

		// Assert
		assertNotNull(dto);
		assertEquals(new BigDecimal("100.00"), dto.getAmount());
		assertEquals(Category.FOOD, dto.getCategory());
	}

	@Test
	void testMapToDTO_WithTransportCategory_ShouldReturnCorrectCategory() {
		// Arrange
		Transaction transaction = new Transaction();
		transaction.setAmount(new BigDecimal("50.00"));
		transaction.setCategory(Category.TRANSPORT);

		// Act
		TransactionDTO dto = TransactionMapper.mapToDTO(transaction);

		// Assert
		assertNotNull(dto);
		assertEquals(Category.TRANSPORT, dto.getCategory());
	}

	@Test
	void testMapToDTO_WithEntertainmentCategory_ShouldReturnCorrectCategory() {
		// Arrange
		Transaction transaction = new Transaction();
		transaction.setAmount(new BigDecimal("75.50"));
		transaction.setCategory(Category.ENTERTAINMENT);

		// Act
		TransactionDTO dto = TransactionMapper.mapToDTO(transaction);

		// Assert
		assertNotNull(dto);
		assertEquals(Category.ENTERTAINMENT, dto.getCategory());
		assertEquals(new BigDecimal("75.50"), dto.getAmount());
	}

	@Test
	void testMapToDTO_WithNullTransaction_ShouldThrowNullPointerException() {
		// Act & Assert
		assertThrows(NullPointerException.class, () -> TransactionMapper.mapToDTO(null));
	}

	@Test
	void testMapToEntity_WithValidTransactionDTO_ShouldReturnTransactionWithSameData() {
		// Arrange
		TransactionDTO dto = new TransactionDTO(new BigDecimal("200.00"), Category.UTILITIES, null);

		// Act
		Transaction transaction = TransactionMapper.mapToEntity(dto);

		// Assert
		assertNotNull(transaction);
		assertEquals(new BigDecimal("200.00"), transaction.getAmount());
		assertEquals(Category.UTILITIES, transaction.getCategory());
	}

	@Test
	void testMapToEntity_WithNullDTO_ShouldThrowNullPointerException() {
		// Act & Assert
		assertThrows(NullPointerException.class, () -> TransactionMapper.mapToEntity(null));
	}

	@Test
	void testMapToDTO_And_MapToEntity_ShouldBeReversible() {
		// Arrange
		Transaction originalTransaction = new Transaction();
		originalTransaction.setAmount(new BigDecimal("150.75"));
		originalTransaction.setCategory(Category.SALARY);

		// Act
		TransactionDTO dto = TransactionMapper.mapToDTO(originalTransaction);
		Transaction mappedTransaction = TransactionMapper.mapToEntity(dto);

		// Assert
		assertEquals(originalTransaction.getAmount(), mappedTransaction.getAmount());
		assertEquals(originalTransaction.getCategory(), mappedTransaction.getCategory());
	}

	@Test
	void testMapToDTO_WithLargeAmount_ShouldReturnCorrectDTO() {
		// Arrange
		Transaction transaction = new Transaction();
		transaction.setAmount(new BigDecimal("9999.99"));
		transaction.setCategory(Category.TRANSFER);

		// Act
		TransactionDTO dto = TransactionMapper.mapToDTO(transaction);

		// Assert
		assertNotNull(dto);
		assertEquals(new BigDecimal("9999.99"), dto.getAmount());
		assertEquals(Category.TRANSFER, dto.getCategory());
	}

	@Test
	void testMapToEntity_WithAllCategories_ShouldMapCorrectly() {
		// Test mapping for each category
		Category[] categories = {
			Category.FOOD, Category.TRANSPORT, Category.ENTERTAINMENT,
			Category.UTILITIES, Category.SALARY, Category.TRANSFER, Category.OTHER
		};

		for (Category category : categories) {
			// Arrange
			TransactionDTO dto = new TransactionDTO(new BigDecimal("100.00"), category, null);

			// Act
			Transaction transaction = TransactionMapper.mapToEntity(dto);

			// Assert
			assertEquals(category, transaction.getCategory());
		}
	}

	@Test
	void testMapToDTO_WithSmallAmount_ShouldReturnCorrectDTO() {
		// Arrange
		Transaction transaction = new Transaction();
		transaction.setAmount(new BigDecimal("0.50"));
		transaction.setCategory(Category.FOOD);

		// Act
		TransactionDTO dto = TransactionMapper.mapToDTO(transaction);

		// Assert
		assertNotNull(dto);
		assertEquals(new BigDecimal("0.50"), dto.getAmount());
	}
}
