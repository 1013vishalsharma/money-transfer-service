package com.moneytransfer.strategy;


import com.moneytransfer.model.MoneyTransferRequest;
import com.moneytransfer.model.MoneyTransferResponse;
import com.moneytransfer.model.TransferMode;

public interface TransferModeStrategy {
	
	TransferMode getTransferMode();
	
	MoneyTransferResponse processPayment(MoneyTransferRequest moneyTransferRequest);

}
