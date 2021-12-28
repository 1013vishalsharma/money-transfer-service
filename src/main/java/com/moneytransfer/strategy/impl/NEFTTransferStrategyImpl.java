package com.moneytransfer.strategy.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.moneytransfer.model.BankAccount;
import com.moneytransfer.model.MoneyTransferRequest;
import com.moneytransfer.model.MoneyTransferResponse;
import com.moneytransfer.model.TransferMode;
import com.moneytransfer.repository.AccountDetailsRepository;
import com.moneytransfer.strategy.TransferModeStrategy;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NEFTTransferStrategyImpl implements TransferModeStrategy{
	
	@Autowired
	AccountDetailsRepository accountDetailsRepository;
	
	@Override
	public TransferMode getTransferMode() {
		return TransferMode.NEFT;
	}

	@Override
	public MoneyTransferResponse processPayment(MoneyTransferRequest moneyTransferRequest) {
		
		log.info("inside process pay with thread: " + Thread.currentThread().getName());
		BankAccount fromAccount = accountDetailsRepository.findById(moneyTransferRequest.getFromAccount()).get();
		BankAccount toAccount = accountDetailsRepository.findById(moneyTransferRequest.getToAccount()).get();
		log.info("Before trans: " + Thread.currentThread().getName() + " fromAcc: " + fromAccount.getBalance()
				+ " toAcc: " + toAccount.getBalance() + "deduction: " + moneyTransferRequest.getAmount());
		
		double finalFromAccountBalance = fromAccount.getBalance() - moneyTransferRequest.getAmount();
		fromAccount.setBalance(finalFromAccountBalance);
		double finalToAccountBalance = toAccount.getBalance() + moneyTransferRequest.getAmount();
		toAccount.setBalance(finalToAccountBalance);

		log.info("After trans: " + Thread.currentThread().getName() + " fromAcc: " + fromAccount.getBalance()
				+ " toAcc: " + toAccount.getBalance());
		accountDetailsRepository.saveAll(List.of(fromAccount, toAccount));
		MoneyTransferResponse response = new MoneyTransferResponse();
		response.setFromAccountBalance(fromAccount.getBalance());
		response.setToAccountBalance(toAccount.getBalance());
		response.setMessage("Funds transferred successfully");
		return response;
	}
}
