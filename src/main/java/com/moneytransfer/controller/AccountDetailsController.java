package com.moneytransfer.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moneytransfer.exception.InvalidTranferRequestException;
import com.moneytransfer.model.BankAccount;
import com.moneytransfer.model.ErrorMessage;
import com.moneytransfer.service.AccountDetailsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("accounts")
@Slf4j
public class AccountDetailsController {
	
	@Autowired
	AccountDetailsService accountDetailsService;
	
	@Operation(description = "Get the details about a particular account by its account id(account number)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully found the acoount details",
				content = {
						@Content(mediaType = "application/json", schema = @Schema(implementation = BankAccount.class))
				}),
			@ApiResponse(responseCode = "400", description = "The request was unacceptable, often due to missing a required parameter.", 
		    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))), 
			@ApiResponse(responseCode = "404", description = "The requested resource doesn't exist.", 
		    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
			@ApiResponse(responseCode = "500", description = "Something went wronge on money transfer API's end.", 
		    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
	})
	@GetMapping("/{accountNumber}")
	public BankAccount getAccountDetails(@PathVariable String accountNumber) {
		if(StringUtils.isBlank(accountNumber)) {
			log.error("Account number cannot be null or empty");
			throw new InvalidTranferRequestException("Account number cannot be null or empty");
		}
		return accountDetailsService.getAccountDetails(accountNumber);
	}

}
