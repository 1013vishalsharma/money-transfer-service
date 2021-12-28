package com.moneytransfer.service;

import com.moneytransfer.model.BankAccount;

public interface AccountDetailsService {
	
	BankAccount getAccountDetails(String accountNumber);

}
