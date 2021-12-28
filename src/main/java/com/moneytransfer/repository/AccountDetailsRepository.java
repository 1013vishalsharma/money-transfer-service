package com.moneytransfer.repository;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import com.moneytransfer.model.BankAccount;

@Repository
public interface AccountDetailsRepository extends JpaRepository<BankAccount, String>{
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<BankAccount> findById(String id);

}
