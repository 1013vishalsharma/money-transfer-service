package com.moneytransfer.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.moneytransfer.exception.AccountNotFoundException;
import com.moneytransfer.model.AccountStatus;
import com.moneytransfer.model.AccountType;
import com.moneytransfer.model.BankAccount;
import com.moneytransfer.model.Customer;
import com.moneytransfer.repository.AccountDetailsRepository;

@ExtendWith(MockitoExtension.class)
public class AccountDetailsServiceImplTest {
	
	private static final String ACCOUNT_NO = "ACC00001";
	
	@Mock
	AccountDetailsRepository mockAccountDetailsRepository;
	
	@InjectMocks
	AccountDetailsServiceImpl mockAccountDetailsService;
	
	@Test
	public void testGetAccountDetails() {
		Customer customer = createCustomer("John", "Smith", "ASDFG1234T");
		BankAccount account = createAccount(AccountStatus.ACTIVE, AccountType.CURRENT_ACCOUNT, 100.0, customer);
		Optional<BankAccount> optionalAccount = Optional.of(account);
		when(mockAccountDetailsRepository.findById(ACCOUNT_NO)).thenReturn(optionalAccount);
		
		BankAccount actualBankAccount = mockAccountDetailsService.getAccountDetails(ACCOUNT_NO);
		assertNotNull(actualBankAccount);
		assertNotNull(actualBankAccount.getCustomer());
		assertEquals(ACCOUNT_NO, actualBankAccount.getAccountNumber());
		assertEquals(AccountStatus.ACTIVE, actualBankAccount.getAccountStatus());
	}
	
	@Test
	public void testGetAccountDetailsWithInavlidAccountNumber() {
		Optional<BankAccount> optionalAccount = Optional.empty();
		when(mockAccountDetailsRepository.findById(ACCOUNT_NO)).thenReturn(optionalAccount);
		
		AccountNotFoundException exception =  assertThrows(AccountNotFoundException.class, 
					() -> mockAccountDetailsService.getAccountDetails(ACCOUNT_NO), "Account not found exception should be thrown");
		assertNotNull(exception);
		assertEquals("No account found for id="+ACCOUNT_NO, exception.getMessage());
	}
	
	private BankAccount createAccount(AccountStatus accountStatus, AccountType accountType, double balance, Customer customer) {
		BankAccount account = new BankAccount();
		account.setAccountNumber(ACCOUNT_NO);
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
