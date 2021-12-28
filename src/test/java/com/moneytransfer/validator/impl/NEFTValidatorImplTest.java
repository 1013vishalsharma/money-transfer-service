package com.moneytransfer.validator.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.moneytransfer.exception.AccountNotFoundException;
import com.moneytransfer.exception.InvalidTranferRequestException;
import com.moneytransfer.model.AccountStatus;
import com.moneytransfer.model.AccountType;
import com.moneytransfer.model.BankAccount;
import com.moneytransfer.model.Customer;
import com.moneytransfer.model.MoneyTransferRequest;
import com.moneytransfer.model.PaymentType;
import com.moneytransfer.model.TransferMode;
import com.moneytransfer.repository.AccountDetailsRepository;

@ExtendWith(MockitoExtension.class)
public class NEFTValidatorImplTest {
	
	private static final String FROM_ACCOUNT = "ACC0001";
	private static final String TO_ACCOUNT = "ACC0002";
	
	Customer fromCustomer, toCustomer;
	BankAccount fromAccount, toAccount;
	MoneyTransferRequest request;
	
	@Mock
	AccountDetailsRepository mockAccountDetailsRepository;

	@InjectMocks
	NEFTValidatorImpl mockValidatorImpl;
	
	@BeforeEach
	public void setUp() {
		fromCustomer = createCustomer("John", "Smith", "axscd8765s");
		fromAccount = createAccount(FROM_ACCOUNT, AccountStatus.ACTIVE, AccountType.CURRENT_ACCOUNT, 100000.0, fromCustomer);
		toCustomer = createCustomer("Steve", "Smith", "mknjb8765s");
		toAccount = createAccount(TO_ACCOUNT, AccountStatus.ACTIVE, AccountType.CURRENT_ACCOUNT, 1000.0, toCustomer);
		request = createMoneyTransferRequest();
	}
	
	public void testGetTransferMode() {
		assertEquals(TransferMode.NEFT, mockValidatorImpl.getTransferMode());
	}
	
	@Test
	public void testValidate() {
		when(mockAccountDetailsRepository.findById(FROM_ACCOUNT)).thenReturn(Optional.of(fromAccount));
		when(mockAccountDetailsRepository.findById(TO_ACCOUNT)).thenReturn(Optional.of(toAccount));
		assertDoesNotThrow(() -> mockValidatorImpl.validate(request));
	}
	
	@Test
	public void testValidateWithAccountError() {
		when(mockAccountDetailsRepository.findById(FROM_ACCOUNT)).thenReturn(Optional.empty());
		AccountNotFoundException exception =  assertThrows(AccountNotFoundException.class, 
				() -> mockValidatorImpl.validate(request), "Account not found exception should be thrown");
		assertNotNull(exception);
		assertEquals("From account for NEFT trnasaction does not exist", exception.getMessage());
	}
	
	@Test
	public void testValidateWithNegativeAmount() {
		request.setAmount(-1.0);
		when(mockAccountDetailsRepository.findById(FROM_ACCOUNT)).thenReturn(Optional.of(fromAccount));
		InvalidTranferRequestException exception =  assertThrows(InvalidTranferRequestException.class, 
				() -> mockValidatorImpl.validate(request), "Amount cannot be less than 1 for NEFT fund transfer");
		assertNotNull(exception);
		assertEquals("Amount cannot be less than 1 for NEFT fund transfer", exception.getMessage());
	}
	
	@Test
	public void testValidateWithBalanceLessThanTransferAmount() {
		fromAccount.setBalance(0.0);
		when(mockAccountDetailsRepository.findById(FROM_ACCOUNT)).thenReturn(Optional.of(fromAccount));
		InvalidTranferRequestException exception =  assertThrows(InvalidTranferRequestException.class, 
				() -> mockValidatorImpl.validate(request), "Amount cannot be less than 1 for NEFT fund transfer");
		assertNotNull(exception);
		assertEquals("Insufficient balance for NEFT funds transfer", exception.getMessage());
	}
	
	@Test
	public void testValidateWithInactiveAccountError() {
		fromAccount.setAccountStatus(AccountStatus.INACTIVE);
		when(mockAccountDetailsRepository.findById(FROM_ACCOUNT)).thenReturn(Optional.of(fromAccount));
		InvalidTranferRequestException exception =  assertThrows(InvalidTranferRequestException.class, 
				() -> mockValidatorImpl.validate(request), "Account not found exception should be thrown");
		assertNotNull(exception);
		assertEquals("Account is inactive", exception.getMessage());
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
