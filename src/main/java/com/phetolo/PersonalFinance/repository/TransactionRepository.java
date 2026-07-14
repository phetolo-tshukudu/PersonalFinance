package com.phetolo.PersonalFinance.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.phetolo.PersonalFinance.enums.Category;
import com.phetolo.PersonalFinance.enums.TransactionType;
import com.phetolo.PersonalFinance.model.Transaction;
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	List<Transaction> findByUser_Id(Long user_id);
	List<Transaction> findByAccount_Id(Long account_Id);
	List<Transaction> findByUser_IdAndCategory(Long user_id,Category category);
	List<Transaction> findByAccount_IdAndCategory(Long account_id,Category category);
	List<Transaction> findByUser_IdAndType(Long user_id,TransactionType type);
	List<Transaction> findByAccount_IdAndType(Long account_id,TransactionType type);
	
	//custom
	@Query("SELECT COALESCE(SUM(t.amount),0) FROM Transaction t WHERE t.user.id=:userId AND t.type='INCOME'")
	BigDecimal getIncomeSum(Long userId);
	@Query("SELECT COALESCE(SUM(t.amount),0) FROM Transaction t WHERE t.user.id=:userId AND t.type='EXPENSE'")
	BigDecimal getExpenseSum(Long userId);
	
	
}
