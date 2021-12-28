package com.moneytransfer.service;

import com.moneytransfer.model.MoneyTransferRequest;
import com.moneytransfer.model.MoneyTransferResponse;

public interface MoneyTransferService {
	
	MoneyTransferResponse transferFunds(MoneyTransferRequest transferRequest);

}
