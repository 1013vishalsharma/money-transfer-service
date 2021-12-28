package com.moneytransfer.strategy;

import java.util.EnumMap;
import java.util.List;

import org.springframework.stereotype.Component;

import com.moneytransfer.model.TransferMode;

@Component
public class ValidatorFactory {
	
private EnumMap<TransferMode, ValidatorStrategy> validatorMap;
	
	public ValidatorFactory(List<ValidatorStrategy> validatorStrategies) {
		validatorMap = new EnumMap<>(TransferMode.class);
		validatorStrategies.stream().forEach(transferModeStrategy -> validatorMap
				.put(transferModeStrategy.getTransferMode(), transferModeStrategy));
	}
	
	public ValidatorStrategy getValidationStrategy(TransferMode transferMode) {
		return validatorMap.get(transferMode);
	}

}
