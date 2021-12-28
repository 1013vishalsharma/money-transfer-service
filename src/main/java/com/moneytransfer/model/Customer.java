package com.moneytransfer.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.moneytransfer.annotations.Generated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Generated
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Customer {
	
	@Id
	private String customerId;
	private String firstName;
	private String lastName;
	private String pan;
}
