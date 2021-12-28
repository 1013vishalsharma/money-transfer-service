package com.moneytransfer.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.moneytransfer.exception.InvalidTranferRequestException;
import com.moneytransfer.model.ErrorMessage;
import com.moneytransfer.model.MoneyTransferRequest;
import com.moneytransfer.model.MoneyTransferResponse;
import com.moneytransfer.service.MoneyTransferService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class MoneyTransferController {
	
	@Autowired
	MoneyTransferService moneyTransferService;
	
	@Operation(description = "Transfer amount specified between two accounts,"
			+ " after the transaction is completed, fromAccount is debited with the specified amount "
			+ "and toAccount is credited with the specified amount")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully found the acoount details",
				content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = MoneyTransferResponse.class))
				}),
			@ApiResponse(responseCode = "400", description = "The request was unacceptable, often due to missing a required parameter.", 
		    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
			@ApiResponse(responseCode = "404", description = "The requested resource doesn't exist.", 
		    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
			@ApiResponse(responseCode = "500", description = "Something went wronge on money transfer API's end.", 
		    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
	})
	@PostMapping("/transfers")
	public MoneyTransferResponse transferFunds(@RequestBody MoneyTransferRequest moneyTransferRequest) {
		log.info("processing payment :" + Thread.currentThread().getName());
		if (StringUtils.isAnyBlank(moneyTransferRequest.getFromAccount(), moneyTransferRequest.getToAccount())) {
			log.error("MoneyTransferRequest object cannot have fields as null or empty");
			throw new InvalidTranferRequestException("MoneyTransferRequest object cannot have fields as null or empty");
		}
		return moneyTransferService.transferFunds(moneyTransferRequest);
	}

}
