package com.moneytransfer.model;

import com.moneytransfer.annotations.Generated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Generated
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoneyTransferRequest {
	
	private String fromAccount;
	private String toAccount;
	private double amount;
	private PaymentType paymentType;
	private TransferMode transferMode;

}
