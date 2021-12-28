package com.moneytransfer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.moneytransfer.controller.AccountDetailsController;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class MoneyTransferServiceApplicationTests {
	
	@Autowired
	AccountDetailsController accountDetailsController;

	@Test
    public void contextLoads() {
        Assertions.assertThat(accountDetailsController).isNotNull();
    }
}
