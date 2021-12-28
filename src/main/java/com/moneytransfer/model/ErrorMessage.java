package com.moneytransfer.model;

import com.moneytransfer.annotations.Generated;

import lombok.Getter;
import lombok.Setter;

@Generated
@Getter
@Setter
public class ErrorMessage {
	
	private String message;
	private String description;
	private int httpStatus;
	
}
