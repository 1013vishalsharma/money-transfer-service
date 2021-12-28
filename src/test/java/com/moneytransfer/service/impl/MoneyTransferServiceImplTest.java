package com.moneytransfer.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.moneytransfer.exception.InvalidTranferRequestException;
import com.moneytransfer.model.MoneyTransferRequest;
import com.moneytransfer.model.MoneyTransferResponse;
import com.moneytransfer.model.PaymentType;
import com.moneytransfer.model.TransferMode;
import com.moneytransfer.strategy.TransferModeFactory;
import com.moneytransfer.strategy.ValidatorFactory;
import com.moneytransfer.strategy.impl.NEFTTransferStrategyImpl;
import com.moneytransfer.strategy.impl.RTGSTransferStrategyImpl;
import com.moneytransfer.validator.impl.NEFTValidatorImpl;
import com.moneytransfer.validator.impl.RTGSValidatorImpl;

@ExtendWith(MockitoExtension.class)
public class MoneyTransferServiceImplTest {
	
	private static final String FROM_ACCOUNT = "ACC0001";
	private static final String TO_ACCOUNT = "ACC0002";
	
	@Mock
	TransferModeFactory mockTransferModeFactory;
	
	@Mock
	NEFTTransferStrategyImpl mockNeftTransferStrategyImpl;
	
	@Mock 
	RTGSTransferStrategyImpl mockRtgsTransferStrategyImpl;
	
	@Mock
	ValidatorFactory mockValidatorFactory;
	
	@Mock
	NEFTValidatorImpl mockNeftValidatorImpl;
	
	@Mock
	RTGSValidatorImpl mockRtgsValidatorImpl;

	@InjectMocks
	MoneyTransferServiceImpl mockMoneyTransferServiceImpl;
	
	@Test
	public void testGetTransferFunds() {
		MoneyTransferRequest request = createMoneyTransferRequest();
		MoneyTransferResponse response = createMoneyTransferResponse();
		when(mockTransferModeFactory.getTransferModeStrategy(request.getTransferMode())).thenReturn(mockNeftTransferStrategyImpl);
		when(mockValidatorFactory.getValidationStrategy(request.getTransferMode())).thenReturn(mockNeftValidatorImpl);
		doNothing().when(mockNeftValidatorImpl).validate(request);
		when(mockNeftTransferStrategyImpl.processPayment(request)).thenReturn(response);
		
		MoneyTransferResponse transferResponse =  mockMoneyTransferServiceImpl.transferFunds(request);
		assertNotNull(transferResponse);
	}
	
	@Test
	public void testGetTransferFundsWithValidationFailure() {
		MoneyTransferRequest request = createMoneyTransferRequest();
		when(mockTransferModeFactory.getTransferModeStrategy(request.getTransferMode())).thenReturn(mockNeftTransferStrategyImpl);
		when(mockValidatorFactory.getValidationStrategy(request.getTransferMode())).thenReturn(mockNeftValidatorImpl);
		doThrow(InvalidTranferRequestException.class).when(mockNeftValidatorImpl).validate(request);
		
		InvalidTranferRequestException exception =  assertThrows(InvalidTranferRequestException.class, 
				() -> mockMoneyTransferServiceImpl.transferFunds(request), "InvalidTransferRequest exception is thrown here");
		assertNotNull(exception);
	}
	
	@Test
	public void testGetTransferFundsWithRTGS() {
		MoneyTransferRequest request = createMoneyTransferRequest();
		MoneyTransferResponse response = createMoneyTransferResponse();
		when(mockTransferModeFactory.getTransferModeStrategy(request.getTransferMode())).thenReturn(mockRtgsTransferStrategyImpl);
		when(mockValidatorFactory.getValidationStrategy(request.getTransferMode())).thenReturn(mockRtgsValidatorImpl);
		doNothing().when(mockRtgsValidatorImpl).validate(request);
		when(mockRtgsTransferStrategyImpl.processPayment(request)).thenReturn(response);
		
		MoneyTransferResponse transferResponse =  mockMoneyTransferServiceImpl.transferFunds(request);
		assertNotNull(transferResponse);
	}
	
	@Test
	public void testGetTransferFundsWithRTGSValidationFailure() {
		MoneyTransferRequest request = createMoneyTransferRequest();
		when(mockTransferModeFactory.getTransferModeStrategy(request.getTransferMode())).thenReturn(mockRtgsTransferStrategyImpl);
		when(mockValidatorFactory.getValidationStrategy(request.getTransferMode())).thenReturn(mockRtgsValidatorImpl);
		doThrow(InvalidTranferRequestException.class).when(mockRtgsValidatorImpl).validate(request);
		
		InvalidTranferRequestException exception =  assertThrows(InvalidTranferRequestException.class, 
				() -> mockMoneyTransferServiceImpl.transferFunds(request), "InvalidTransferRequest exception is thrown here");
		assertNotNull(exception);
	}
	
	private MoneyTransferRequest createMoneyTransferRequest() {
		MoneyTransferRequest transferRequest = new MoneyTransferRequest();
		transferRequest.setFromAccount(FROM_ACCOUNT);
		transferRequest.setToAccount(TO_ACCOUNT);
		transferRequest.setAmount(4000.0);
		transferRequest.setPaymentType(PaymentType.BANK_TO_BANK);
		transferRequest.setTransferMode(TransferMode.IMPS);
		return transferRequest;
	}
	
	private MoneyTransferResponse createMoneyTransferResponse() {
		MoneyTransferResponse response = new MoneyTransferResponse();
		response.setFromAccountBalance(6000.0);
		response.setToAccountBalance(14000.0);
		response.setMessage("Funds transferred successfully");
		return response;
	}
}
