package com.moneytransfer.validator.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;

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
public class NEFTValidatorImpl implements ValidatorStrategy{
	
	@Autowired
	AccountDetailsRepository accountDetailsRepository;
	
	@Override
	public TransferMode getTransferMode() {
		return TransferMode.NEFT;
	}

	@Override
	public void validate(MoneyTransferRequest moneyTransferRequest) {

		addDelayForNEFT();
		DayOfWeek dayOfWeek = DayOfWeek.from(LocalDate.now());
		if(dayOfWeek.equals(DayOfWeek.SATURDAY) || dayOfWeek.equals(DayOfWeek.SUNDAY)) {
			log.error("NEFT Transfers not allowed on Saturday and Sunday");
			throw new InvalidTranferRequestException("NEFT Transfers not allowed on Saturday and Sunday");
		}
		BankAccount fromAccount = validateAccount(moneyTransferRequest.getFromAccount());
		validateTransferAmountAndBalance(fromAccount, moneyTransferRequest);
		validateAccount(moneyTransferRequest.getToAccount());
	
	}
	
	private BankAccount validateAccount(String accountId) {
		BankAccount account = accountDetailsRepository.findById(accountId).orElseThrow(() -> {
			log.error("From account for NEFT trnasaction does not exist");
			throw new AccountNotFoundException("From account for NEFT trnasaction does not exist");
		});
		
		if(account.getAccountStatus() == AccountStatus.INACTIVE) {
			log.error("Account is inactive");
			throw new InvalidTranferRequestException("Account is inactive");
		}
		return account;
	}
	
	private boolean validateTransferAmountAndBalance(BankAccount account, MoneyTransferRequest moneyTransferRequest) {
		if(moneyTransferRequest.getAmount() < 1.0) {
			log.error("Amount cannot be less than 1.0 for NEFT funds transfer");
			throw new InvalidTranferRequestException("Amount cannot be less than 1 for NEFT fund transfer");
		}
		
		if(account.getBalance() < moneyTransferRequest.getAmount()) {
			log.error("Insufficient balance for NEFT transfer");
			throw new InvalidTranferRequestException("Insufficient balance for NEFT funds transfer");
		}
		return true;
	}
	
	private void addDelayForNEFT() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
