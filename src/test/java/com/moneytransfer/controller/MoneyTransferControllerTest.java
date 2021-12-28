package com.moneytransfer.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneytransfer.exception.InvalidTranferRequestException;
import com.moneytransfer.model.MoneyTransferRequest;
import com.moneytransfer.model.MoneyTransferResponse;
import com.moneytransfer.model.PaymentType;
import com.moneytransfer.model.TransferMode;
import com.moneytransfer.service.MoneyTransferService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MoneyTransferController.class)
public class MoneyTransferControllerTest {
	
	private static final String FROM_ACCOUNT = "ACC0001";
	private static final String TO_ACCOUNT = "ACC0002";
	
	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	MoneyTransferService mockmMoneyTransferService;
	
	@Test
	public void testTransferFunds() throws Exception{
		
		MoneyTransferRequest transferRequest = createMoneyTransferRequest();
		MoneyTransferResponse transferResponse = createMoneyTransferResponse();
		when(mockmMoneyTransferService.transferFunds(transferRequest)).thenReturn(transferResponse);
		
		mockMvc
			.perform(post("/transfers")
			.content(asJsonString(transferRequest))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk());
		
	}
	
	@Test
	public void testTransferFundsWithInvalidRequest() throws Exception{
		
		MoneyTransferRequest transferRequest = null;
		
		mockMvc
			.perform(post("/transfers")
			.content(asJsonString(transferRequest))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpMessageNotReadableException))
			.andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains("Required request body is missing")));
		
	}
	
	@Test
	public void testTransferFundsWithInvalidFromAccountInRequest() throws Exception{
		
		MoneyTransferRequest transferRequest = new MoneyTransferRequest();
		
		mockMvc
			.perform(post("/transfers")
			.content(asJsonString(transferRequest))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidTranferRequestException))
			.andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains("MoneyTransferRequest object cannot have fields as null or empty")));
		
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
	
	private static String asJsonString(final Object obj) {
	    try {
	        return new ObjectMapper().writeValueAsString(obj);
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}

}
