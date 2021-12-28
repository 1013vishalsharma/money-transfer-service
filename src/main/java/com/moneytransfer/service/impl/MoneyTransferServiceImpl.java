package com.moneytransfer.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.moneytransfer.model.MoneyTransferRequest;
import com.moneytransfer.model.MoneyTransferResponse;
import com.moneytransfer.model.TransferMode;
import com.moneytransfer.service.MoneyTransferService;
import com.moneytransfer.strategy.TransferModeFactory;
import com.moneytransfer.strategy.TransferModeStrategy;
import com.moneytransfer.strategy.ValidatorFactory;
import com.moneytransfer.strategy.ValidatorStrategy;

@Service
public class MoneyTransferServiceImpl implements MoneyTransferService {
	
	@Autowired
	TransferModeFactory transferModeFactory;
	
	@Autowired
	ValidatorFactory validatorFactory;
	
	@Override
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public MoneyTransferResponse transferFunds(MoneyTransferRequest moneyTransferRequest) {
		TransferMode transferMode = moneyTransferRequest.getTransferMode();
		TransferModeStrategy strategy = transferModeFactory.getTransferModeStrategy(transferMode);
		ValidatorStrategy validatorStrategy = validatorFactory.getValidationStrategy(transferMode);
		validatorStrategy.validate(moneyTransferRequest);
		return strategy.processPayment(moneyTransferRequest);
	}

}
