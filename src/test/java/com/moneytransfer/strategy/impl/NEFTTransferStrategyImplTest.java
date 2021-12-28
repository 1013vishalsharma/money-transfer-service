package com.moneytransfer.strategy.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;

import com.moneytransfer.model.AccountStatus;
import com.moneytransfer.model.AccountType;
import com.moneytransfer.model.BankAccount;
import com.moneytransfer.model.Customer;
import com.moneytransfer.model.MoneyTransferRequest;
import com.moneytransfer.model.MoneyTransferResponse;
import com.moneytransfer.model.PaymentType;
import com.moneytransfer.model.TransferMode;
import com.moneytransfer.repository.AccountDetailsRepository;

@ExtendWith(MockitoExtension.class)
public class NEFTTransferStrategyImplTest {
	
	private static final String FROM_ACCOUNT = "ACC0001";
	private static final String TO_ACCOUNT = "ACC0002";
	
	Customer fromCustomer, toCustomer;
	BankAccount fromAccount, toAccount;
	MoneyTransferRequest request;
	
	@Mock
	AccountDetailsRepository mockAccountDetailsRepository;

	@InjectMocks
	NEFTTransferStrategyImpl mockNeftTransferStrategyImpl;
	
	@BeforeEach
	public void setUp() {
		fromCustomer = createCustomer("John", "Smith", "axscd8765s");
		fromAccount = createAccount(FROM_ACCOUNT, AccountStatus.ACTIVE, AccountType.CURRENT_ACCOUNT, 100000.0, fromCustomer);
		toCustomer = createCustomer("Steve", "Smith", "mknjb8765s");
		toAccount = createAccount(TO_ACCOUNT, AccountStatus.ACTIVE, AccountType.CURRENT_ACCOUNT, 1000.0, toCustomer);
		request = createMoneyTransferRequest();
	}
	
	@Test
	public void testProcessPayment() {
		when(mockAccountDetailsRepository.findById(FROM_ACCOUNT)).thenReturn(Optional.of(fromAccount));
		when(mockAccountDetailsRepository.findById(TO_ACCOUNT)).thenReturn(Optional.of(toAccount));
		double initialToBalance = toAccount.getBalance();
		double initialFromBalance = fromAccount.getBalance();
		MoneyTransferResponse response = mockNeftTransferStrategyImpl.processPayment(request);
		assertNotNull(response);
		assertEquals(request.getAmount() + initialToBalance, response.getToAccountBalance());
		assertEquals(initialFromBalance - request.getAmount(), response.getFromAccountBalance());
	}
	
	@Test
	public void testProcessPaymentWithExceptionWhilePersisting() {
		when(mockAccountDetailsRepository.findById(FROM_ACCOUNT)).thenReturn(Optional.of(fromAccount));
		when(mockAccountDetailsRepository.findById(TO_ACCOUNT)).thenReturn(Optional.of(toAccount));
		doThrow(new DataRetrievalFailureException("")).when(mockAccountDetailsRepository).saveAll(List.of(fromAccount, toAccount));
		Exception response = assertThrows(DataAccessException.class,
				() -> mockNeftTransferStrategyImpl.processPayment(request));
		assertNotNull(response);
		assertEquals(response.getClass(), DataRetrievalFailureException.class);
	}
	
	@Test
	public void testProcessPaymentWithDataAccessException() {
		doThrow(new DataRetrievalFailureException("")).when(mockAccountDetailsRepository).findById(FROM_ACCOUNT);
		Exception response = assertThrows(DataAccessException.class,
				() -> mockNeftTransferStrategyImpl.processPayment(request));
		assertNotNull(response);
		assertEquals(response.getClass(), DataRetrievalFailureException.class);
	}
	
	@Test
	public void testGetTransferMode() {
		assertEquals(TransferMode.NEFT, mockNeftTransferStrategyImpl.getTransferMode());
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
	
	private BankAccount createAccount(String accountNumber, AccountStatus accountStatus, AccountType accountType,
			double balance, Customer customer) {
		BankAccount account = new BankAccount();
		account.setAccountNumber(accountNumber);
		account.setAccountStatus(accountStatus);
		account.setAccountType(accountType);
		account.setBalance(balance);
		account.setCustomer(customer);
		return account;
	}
	
	private Customer createCustomer(String firstName, String lastName, String pan) {
		Customer customer = new Customer();
		customer.setCustomerId(UUID.randomUUID().toString());
		customer.setFirstName(firstName);
		customer.setLastName(lastName);
		customer.setPan(pan);
		return customer;
	}
}
