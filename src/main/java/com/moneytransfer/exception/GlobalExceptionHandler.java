package com.moneytransfer.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.moneytransfer.model.ErrorMessage;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	
	@ExceptionHandler(AccountNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public @ResponseBody ErrorMessage handleException(final AccountNotFoundException exception,
			final HttpServletRequest request) {
		
		log.error("Encountered Exception: ", exception);
		ErrorMessage message  = new ErrorMessage();
		message.setMessage(HttpStatus.NOT_FOUND.name());
		message.setHttpStatus(HttpStatus.NOT_FOUND.value());
		message.setDescription(exception.getMessage());
		return message;
	}
	
	@ExceptionHandler(InvalidTranferRequestException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public @ResponseBody ErrorMessage handleException(final InvalidTranferRequestException exception,
			final HttpServletRequest request) {

		log.error("Encountered Exception: ", exception);
		ErrorMessage message  = new ErrorMessage();
		message.setMessage(HttpStatus.BAD_REQUEST.name());
		message.setHttpStatus(HttpStatus.BAD_REQUEST.value());
		message.setDescription(exception.getMessage());
		return message;
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public @ResponseBody ErrorMessage handleException(final HttpMessageNotReadableException exception,
			final HttpServletRequest request) {

		log.error("Encountered Exception: ", exception);
		ErrorMessage message  = new ErrorMessage();
		message.setMessage(HttpStatus.BAD_REQUEST.name());
		message.setHttpStatus(HttpStatus.BAD_REQUEST.value());
		message.setDescription(exception.getMessage());
		return message;
	}
	
	@ExceptionHandler(Throwable.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ErrorMessage handleException(final Throwable exception,
			final HttpServletRequest request) {

		log.error("Encountered Exception: ", exception);
		ErrorMessage message  = new ErrorMessage();
		message.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
		message.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		if(!checkForDataAccessException(exception, message))
			message.setDescription(exception.getMessage());
		return message;
	}
	
	private boolean checkForDataAccessException(Throwable exception, ErrorMessage message) {
		if(exception instanceof DataAccessException) {
			message.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
			message.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			message.setDescription("Exception while processing the request");
		}
		return true;
	}
}
