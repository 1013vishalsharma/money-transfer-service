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
public class RTGSTransferStrategyImpl implements TransferModeStrategy{
	
	@Autowired
	AccountDetailsRepository accountDetailsRepository;

	public TransferMode getTransferMode() {
		return TransferMode.RTGS;
	}

	@Override
	public MoneyTransferResponse processPayment(MoneyTransferRequest moneyTransferRequest) {
		log.info("Processing payment for RTGS transaction..");
		BankAccount fromAccount = accountDetailsRepository.findById(moneyTransferRequest.getFromAccount()).get();
		BankAccount toAccount = accountDetailsRepository.findById(moneyTransferRequest.getToAccount()).get();
		double charges = calaculateBankCharges(moneyTransferRequest.getAmount());
		double finalFromAccountBalance = fromAccount.getBalance() - moneyTransferRequest.getAmount() - charges;
		fromAccount.setBalance(finalFromAccountBalance);
		double finalToAccountBalance = toAccount.getBalance() + moneyTransferRequest.getAmount();
		toAccount.setBalance(finalToAccountBalance);
		accountDetailsRepository.saveAll(List.of(fromAccount, toAccount));
		MoneyTransferResponse response = new MoneyTransferResponse();
		response.setFromAccountBalance(fromAccount.getBalance());
		response.setToAccountBalance(toAccount.getBalance());
		response.setMessage("Funds transferred successfully");
		return response;
	}
	
	private double calaculateBankCharges(double transferamount) {
		double charges = 0.0;
		if(transferamount >= 200000.0 && transferamount <= 500000.0) {
			charges = 20.0;
		} else {
			charges = 50.0;
		}
		
		charges = (charges * 18)/100;
		return charges;
	}
}
