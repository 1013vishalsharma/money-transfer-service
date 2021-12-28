package com.moneytransfer.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.moneytransfer.annotations.Generated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Generated
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {
	
	@Id
	private String accountNumber;
	
	@OneToOne
	@JoinColumn(name = "customer")
	private Customer customer;
	
	private double balance;
	
	@Enumerated(EnumType.STRING)
	private AccountType accountType;
	
	@Enumerated(EnumType.STRING)
	private AccountStatus accountStatus;
}
