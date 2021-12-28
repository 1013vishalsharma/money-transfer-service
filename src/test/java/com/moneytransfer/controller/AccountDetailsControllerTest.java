package com.moneytransfer.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.moneytransfer.exception.InvalidTranferRequestException;
import com.moneytransfer.model.AccountStatus;
import com.moneytransfer.model.AccountType;
import com.moneytransfer.model.BankAccount;
import com.moneytransfer.model.Customer;
import com.moneytransfer.service.AccountDetailsService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AccountDetailsController.class)
public class AccountDetailsControllerTest {
	
	private static final String ACCOUNT_NO = "ACC00001";
	
	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	AccountDetailsService mcokAccountDetailsService;
	
	@Test
	public void testGetAccountDetails() throws Exception{
		
		Customer customer = createCustomer("John", "Smith", "ASDFG1234T");
		BankAccount account = createAccount(AccountStatus.ACTIVE, AccountType.CURRENT_ACCOUNT, 100.0, customer);
		Mockito.when(mcokAccountDetailsService.getAccountDetails(Mockito.any())).thenReturn(account);
		
		mockMvc
			.perform(get("/accounts/" + ACCOUNT_NO))
			.andDo(print())
			.andExpect(status().isOk());
	}
	
	@Test
	public void testGetAccountDetailsWithEmptyAccountNumber() throws Exception{
		
		mockMvc
			.perform(get("/accounts/ ")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidTranferRequestException))
			.andExpect(result -> assertEquals("Account number cannot be null or empty", result.getResolvedException().getMessage()));
	}
	
	private BankAccount createAccount(AccountStatus accountStatus, AccountType accountType, double balance, Customer customer) {
		BankAccount account = new BankAccount();
		account.setAccountNumber(UUID.randomUUID().toString());
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
