package com.phetolo.PersonalFinance.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.phetolo.PersonalFinance.enums.AccountType;
import com.phetolo.PersonalFinance.model.Account;
@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{
	Account findByUser_IdAndType(Long user_id,AccountType type);
	
	boolean existsByUser_IdAndType(Long userId,AccountType type);
	List<Account> findByUser_Id(Long user_Id);
	@Modifying
	@Query("UPDATE Account a SET a.balance= a.balance + :amount WHERE a.user.id=:userid")
	Account addBalance(Long userid,BigDecimal amount);
	@Query("UPDATE Account a SET a.balance= a.balance - :amount WHERE a.user.id=:userid")
	Account subtractBalance(Long userid,BigDecimal amount);
}
