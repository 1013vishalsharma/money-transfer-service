package com.moneytransfer.model;

import com.moneytransfer.annotations.Generated;

import lombok.Data;

@Generated
@Data
public class MoneyTransferResponse {
	
	private double fromAccountBalance;
	private double toAccountBalance;
	private String message;

}
