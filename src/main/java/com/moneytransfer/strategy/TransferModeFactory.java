package com.moneytransfer.strategy;

import java.util.EnumMap;
import java.util.List;

import org.springframework.stereotype.Component;

import com.moneytransfer.model.TransferMode;

@Component
public class TransferModeFactory {
	
	private EnumMap<TransferMode, TransferModeStrategy> transferModeMap;
	
	public TransferModeFactory(List<TransferModeStrategy> transferModeStrategies) {
		transferModeMap = new EnumMap<>(TransferMode.class);
		transferModeStrategies.stream().forEach(transferModeStrategy -> transferModeMap
				.put(transferModeStrategy.getTransferMode(), transferModeStrategy));
	}
	
	public TransferModeStrategy getTransferModeStrategy(TransferMode transferMode) {
		return transferModeMap.get(transferMode);
	}

}
