package com.phetolo.PersonalFinance.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Indexed;

import com.phetolo.PersonalFinance.enums.AccountType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Data;



@Data
@Entity
@Table(name="accounts",indexes = {@Index(name="index_user_id",columnList="user_id, createdAt DESC")})
public class Account {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id; //severs as an account number since it is a primary(unique)
	@Enumerated(EnumType.STRING)
	@Column(name="type")
	private AccountType type;
	@Column(precision = 12,scale=2,nullable=false)
	private BigDecimal balance;
	@ManyToOne
	@JoinColumn(name="user_id",nullable = false)
	private User user; //users can have multiple accounts 
	private LocalDateTime createdAt;
}
