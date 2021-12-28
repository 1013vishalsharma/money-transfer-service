package com.moneytransfer.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.moneytransfer.model.AccountStatus;
import com.moneytransfer.model.BankAccount;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccountDetailsRepositoryTest {
	
	private static final String ACCOUNT_NUMBER_1 = "ACC00001";
	private static final String ACCOUNT_NUMBER_2 = "ACC00002";

	@Autowired
	AccountDetailsRepository accountDetailsRepository;
	
	@Test
	public void testGetAccountDetails() {
		BankAccount account = accountDetailsRepository.findById(ACCOUNT_NUMBER_1).get();
		assertNotNull(account);
		assertEquals(4477.0, account.getBalance());
		assertEquals(AccountStatus.ACTIVE, account.getAccountStatus());
	}
	
	@Test
	public void save() {
		BankAccount fromAccount = accountDetailsRepository.findById(ACCOUNT_NUMBER_1).get();
		BankAccount toAccount = accountDetailsRepository.findById(ACCOUNT_NUMBER_2).get();
		double initialFromAccountBalance = fromAccount.getBalance();
		double initialToAccountBalance = toAccount.getBalance();
		double amount = 1000.0;
		double finalFromAccountBalance = initialFromAccountBalance - amount;
		double finalToAccountBalance = initialToAccountBalance + amount;
		fromAccount.setBalance(finalFromAccountBalance);
		toAccount.setBalance(finalToAccountBalance);
		accountDetailsRepository.saveAll(List.of(fromAccount, toAccount));
		
		fromAccount = accountDetailsRepository.findById(ACCOUNT_NUMBER_1).get();
		toAccount = accountDetailsRepository.findById(ACCOUNT_NUMBER_2).get();
		
		assertEquals(fromAccount.getBalance(), finalFromAccountBalance);
		assertEquals(toAccount.getBalance(), finalToAccountBalance);
	}
}
