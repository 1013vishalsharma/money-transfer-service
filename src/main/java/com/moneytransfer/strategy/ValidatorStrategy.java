package com.moneytransfer.strategy;

import com.moneytransfer.model.MoneyTransferRequest;
import com.moneytransfer.model.TransferMode;

public interface ValidatorStrategy {
	
	TransferMode getTransferMode();

	void validate(MoneyTransferRequest request);
}
