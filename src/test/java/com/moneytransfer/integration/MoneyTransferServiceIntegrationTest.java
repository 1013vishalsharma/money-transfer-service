package com.moneytransfer.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.moneytransfer.controller.AccountDetailsController;
import com.moneytransfer.controller.MoneyTransferController;
import com.moneytransfer.exception.InvalidTranferRequestException;
import com.moneytransfer.model.MoneyTransferRequest;
import com.moneytransfer.model.MoneyTransferResponse;
import com.moneytransfer.model.PaymentType;
import com.moneytransfer.model.TransferMode;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class MoneyTransferServiceIntegrationTest {
	
	private static final String ACCOUNT1 = "ACC00001";
	private static final String ACCOUNT2 = "ACC00002";
	private static final String ACCOUNT3 = "ACC00003";
	private static final String ACCOUNT4 = "ACC00004";
	
	@Autowired
	AccountDetailsController accountDetailsController;
	
	@Autowired
	MoneyTransferController moneyTransferController;
	
	@Test
	public void testSingleTransferFunds() {
		MoneyTransferRequest request = createMoneyTransferRequest();
		double initialFromBalance = accountDetailsController.getAccountDetails(ACCOUNT1).getBalance();
		double initialToBalance = accountDetailsController.getAccountDetails(ACCOUNT2).getBalance();
		MoneyTransferResponse response = moneyTransferController.transferFunds(request);
		double finalFromBalance = accountDetailsController.getAccountDetails(ACCOUNT1).getBalance();
		double finalToBalance = accountDetailsController.getAccountDetails(ACCOUNT2).getBalance();
		assertNotNull(response);
		assertEquals(initialFromBalance-request.getAmount(), finalFromBalance);
		assertEquals(initialToBalance + request.getAmount(), finalToBalance);
	}
	
	@Test
	public void testSingleTransferFundsToSameAccount() {
		MoneyTransferRequest request = createMoneyTransferRequest();
		request.setToAccount(request.getFromAccount());
		double initialFromBalance = accountDetailsController.getAccountDetails(request.getFromAccount()).getBalance();
		double initialToBalance = accountDetailsController.getAccountDetails(request.getToAccount()).getBalance();
		MoneyTransferResponse response = moneyTransferController.transferFunds(request);
		double finalFromBalance = accountDetailsController.getAccountDetails(request.getFromAccount()).getBalance();
		double finalToBalance = accountDetailsController.getAccountDetails(request.getToAccount()).getBalance();
		assertNotNull(response);
		assertEquals(initialFromBalance, finalFromBalance);
		assertEquals(initialToBalance, finalToBalance);
	}
	
	@Test
	public void testTwoTransferFunds() {
		MoneyTransferRequest request = createMoneyTransferRequest();
		MoneyTransferRequest request2= createMoneyTransferRequest();
		request2.setFromAccount(ACCOUNT3);
		
		// initial account balances
		double account1Balance = accountDetailsController.getAccountDetails(ACCOUNT1).getBalance();
		double account2Balance = accountDetailsController.getAccountDetails(ACCOUNT2).getBalance();
		double account3Balance = accountDetailsController.getAccountDetails(ACCOUNT3).getBalance();
		
		// transfer money between acc1 -> acc2
		MoneyTransferResponse response = moneyTransferController.transferFunds(request);
		
		// transfer money between acc3 -> acc2
		MoneyTransferResponse response2 = moneyTransferController.transferFunds(request2);
				
		// final account balances
		double finalAccount1Balance = accountDetailsController.getAccountDetails(ACCOUNT1).getBalance();
		double finalAccount2Balance = accountDetailsController.getAccountDetails(ACCOUNT2).getBalance();
		double finalAccount3Balance = accountDetailsController.getAccountDetails(ACCOUNT3).getBalance();
		
		assertNotNull(response);
		assertNotNull(response2);
		
		assertEquals(account1Balance-request.getAmount(), finalAccount1Balance);
		assertEquals(account3Balance-request2.getAmount(), finalAccount3Balance);
		assertEquals(account2Balance + request.getAmount() + request2.getAmount(), finalAccount2Balance);
	}
	
	@Test
	public void testFourTransferFunds() throws InterruptedException, ExecutionException {
		
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		
		// request 1 (acc1 -> acc2)
		MoneyTransferRequest request = createMoneyTransferRequest();
		
		// request 2 (acc3 -> acc2)
		MoneyTransferRequest request2= createMoneyTransferRequest();
		request2.setFromAccount(ACCOUNT3);
		
		// request 3 (acc3 -> acc1)
		MoneyTransferRequest request3= createMoneyTransferRequest();
		request3.setFromAccount(ACCOUNT3);
		request3.setToAccount(ACCOUNT1);
				
		// request 4 (acc2 -> acc4)
		MoneyTransferRequest request4= createMoneyTransferRequest();
		request4.setFromAccount(ACCOUNT2);
		request4.setToAccount(ACCOUNT4);
		
		// initial account balances
		double account1Balance = accountDetailsController.getAccountDetails(ACCOUNT1).getBalance();
		double account2Balance = accountDetailsController.getAccountDetails(ACCOUNT2).getBalance();
		double account3Balance = accountDetailsController.getAccountDetails(ACCOUNT3).getBalance();
		double account4Balance = accountDetailsController.getAccountDetails(ACCOUNT4).getBalance();
		
		List<MoneyTransferRequest> requestList = List.of(request, request2, request3, request4);
		List<Future<MoneyTransferResponse>> responseList = new ArrayList<>();
		for(MoneyTransferRequest requestObj: requestList) {
			responseList.add(executorService.submit(() -> moneyTransferController.transferFunds(requestObj)));
			Thread.sleep(100);
		}
		
		Thread.sleep(10000);
				
		// final account balances
		double finalAccount1Balance = accountDetailsController.getAccountDetails(ACCOUNT1).getBalance();
		double finalAccount2Balance = accountDetailsController.getAccountDetails(ACCOUNT2).getBalance();
		double finalAccount3Balance = accountDetailsController.getAccountDetails(ACCOUNT3).getBalance();
		double finalAccount4Balance = accountDetailsController.getAccountDetails(ACCOUNT4).getBalance();
		
		assertEquals(account1Balance - request.getAmount() + request3.getAmount() , finalAccount1Balance);
		assertEquals(account3Balance - request2.getAmount() - request3.getAmount(), finalAccount3Balance);
		assertEquals(account2Balance + request.getAmount() + request2.getAmount() - request4.getAmount(), finalAccount2Balance);
		assertEquals(account4Balance + request4.getAmount(), finalAccount4Balance);
	}
	
	@Test
	public void testTransferFundsByRTGS() {
		MoneyTransferRequest request = createMoneyTransferRequest();
		request.setTransferMode(TransferMode.RTGS);
		InvalidTranferRequestException exception = assertThrows(InvalidTranferRequestException.class, 
				() -> moneyTransferController.transferFunds(request));
		assertNotNull(exception);
		assertEquals("Amount cannot be less than 200000.0 for RTGS funds transfer", exception.getMessage());
	}
	
	
	@Test
	public void testTransferFundsByRTGSWithInsufficientFunds() {
		
		MoneyTransferRequest request = createMoneyTransferRequest();
		request.setTransferMode(TransferMode.RTGS);
		request.setAmount(400000.0);
		InvalidTranferRequestException exception = assertThrows(InvalidTranferRequestException.class, 
				() -> moneyTransferController.transferFunds(request));
		assertNotNull(exception);
		assertEquals("Insufficient balance for RTGS funds transfer", exception.getMessage());
	}
	
	private MoneyTransferRequest createMoneyTransferRequest() {
		MoneyTransferRequest transferRequest = new MoneyTransferRequest();
		transferRequest.setFromAccount(ACCOUNT1);
		transferRequest.setToAccount(ACCOUNT2);
		transferRequest.setAmount(100.0);
		transferRequest.setPaymentType(PaymentType.BANK_TO_BANK);
		transferRequest.setTransferMode(TransferMode.NEFT);
		return transferRequest;
	}
}
