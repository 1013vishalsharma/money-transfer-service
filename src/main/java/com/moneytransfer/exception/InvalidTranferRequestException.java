package com.moneytransfer.exception;

public class InvalidTranferRequestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidTranferRequestException(String message) {
		super(message);
	}
	
	public InvalidTranferRequestException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
