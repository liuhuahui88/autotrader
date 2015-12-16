package com.neo.autotrader.feature.util;

import com.neo.autotrader.feature.Function;
import com.neo.autotrader.feature.Value;

public class Const implements Function {
	
	private Double defaultValue;
	
	public Const(Double defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public Double evaluate(Value self, int base, Value... values) {
		return defaultValue;
	}
}
