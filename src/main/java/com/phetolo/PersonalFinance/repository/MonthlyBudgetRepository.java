package com.phetolo.PersonalFinance.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.phetolo.PersonalFinance.model.MonthlyBudget;
@Repository
public interface MonthlyBudgetRepository extends JpaRepository<MonthlyBudget, Long>{
	MonthlyBudget findByUser_Id(Long user_id);
	List<MonthlyBudget> findByAccount_Id(Long account_id);
	
	@Query("UPDATE MonthlyBudget b SET b.amount =:amount WHERE b.user.id=:userid")
	MonthlyBudget updateBudget(Long userid,BigDecimal amount);
	boolean existsByUser_Id(Long userId);
}
