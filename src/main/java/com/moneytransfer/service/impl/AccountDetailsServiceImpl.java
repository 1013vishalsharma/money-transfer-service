package com.moneytransfer.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moneytransfer.exception.AccountNotFoundException;
import com.moneytransfer.model.BankAccount;
import com.moneytransfer.repository.AccountDetailsRepository;
import com.moneytransfer.service.AccountDetailsService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class AccountDetailsServiceImpl implements AccountDetailsService {

	@Autowired
	AccountDetailsRepository accountDetailsRepository;
	
	@Override
	public BankAccount getAccountDetails(String accountNumber) {
		log.info("Fetching details for account with accountNumber={}", accountNumber);
		return accountDetailsRepository.findById(accountNumber).orElseThrow(() -> {
			log.error("No account found for id={}", accountNumber);
			throw new AccountNotFoundException("No account found for id=" + accountNumber);
		});
	}

}
