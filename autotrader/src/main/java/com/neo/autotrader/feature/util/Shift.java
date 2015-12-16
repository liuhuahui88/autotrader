package com.neo.autotrader.feature.util;

import com.neo.autotrader.feature.Function;
import com.neo.autotrader.feature.Value;

public class Shift implements Function {
	
	private int offset;
	private Double defaultValue;
	
	public Shift(int offset, Double defaultValue) {
		this.offset = offset;
		this.defaultValue = defaultValue;
	}

	@Override
	public Double evaluate(Value self, int base, Value... values) {
		Value value = values[0];
		
		if (value.isValid(base, offset)) {
			return value.get(base, offset);
		} else {
			return defaultValue;
		}
	}
}
