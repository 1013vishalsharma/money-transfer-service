package com.moneytransfer.validator.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.moneytransfer.exception.AccountNotFoundException;
import com.moneytransfer.exception.InvalidTranferRequestException;
import com.moneytransfer.model.AccountStatus;
import com.moneytransfer.model.BankAccount;
import com.moneytransfer.model.MoneyTransferRequest;
import com.moneytransfer.model.TransferMode;
import com.moneytransfer.repository.AccountDetailsRepository;
import com.moneytransfer.strategy.ValidatorStrategy;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RTGSValidatorImpl implements ValidatorStrategy {
	
	@Autowired
	AccountDetailsRepository accountDetailsRepository;
	
	@Override
	public TransferMode getTransferMode() {
		return TransferMode.RTGS;
	}

	@Override
	public void validate(MoneyTransferRequest moneyTransferRequest) {
		BankAccount fromAccount = validateAccount(moneyTransferRequest.getFromAccount());
		validateTransferAmountAndBalance(fromAccount, moneyTransferRequest);
		validateAccount(moneyTransferRequest.getToAccount());
	}
	
	private BankAccount validateAccount(String accountId) {
		BankAccount account = accountDetailsRepository.findById(accountId).orElseThrow(() -> {
			log.error("From account for transaction does not exist");
			throw new AccountNotFoundException("From account for RTGS transaction does not exist");
		});
		
		if(account.getAccountStatus() == AccountStatus.INACTIVE) {
			log.error("Account is inactive");
			throw new InvalidTranferRequestException("Account is inactive");
		}
		return account;
	}
	
	private boolean validateTransferAmountAndBalance(BankAccount account, MoneyTransferRequest moneyTransferRequest) {
		if(moneyTransferRequest.getAmount() < 200000.0) {
			log.error("Amount cannot be less than 200000.0 for RTGS funds transfer");
			throw new InvalidTranferRequestException("Amount cannot be less than 200000.0 for RTGS funds transfer");
		}
		
		if(moneyTransferRequest.getAmount() > 2000000.0) {
			log.error("Amount cannot be more than 2000000.0 for RTGS funds transfer");
			throw new InvalidTranferRequestException("Amount cannot be more than 2000000.0 for RTGS funds transfer");
		}
		
		if(account.getBalance() < moneyTransferRequest.getAmount()) {
			log.error("Insufficient balance for RTGS funds transfer");
			throw new InvalidTranferRequestException("Insufficient balance for RTGS funds transfer");
		}
		return true;
	}
}
