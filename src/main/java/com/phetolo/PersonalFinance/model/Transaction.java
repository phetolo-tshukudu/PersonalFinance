package com.phetolo.PersonalFinance.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.phetolo.PersonalFinance.enums.Category;
import com.phetolo.PersonalFinance.enums.TransactionType;

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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data

@Entity
@Table(name="_transaction",indexes = {@Index(name="index_user_id",columnList="user_id, createdAt DESC"),@Index(name="index_acc_id",columnList="account_id, createdAt DESC")})
public class Transaction {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(precision = 12,scale=2,nullable=false)
	private BigDecimal amount;
	@Enumerated(EnumType.STRING)
	@Column(name="type")
	private TransactionType type;
	@Enumerated(EnumType.STRING)
	@Column(name="category")
	private Category category;
	@ManyToOne
	@JoinColumn(name="account_id")
	private Account account;
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	private LocalDateTime createdAt;
	
	
}
