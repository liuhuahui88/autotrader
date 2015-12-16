package com.neo.autotrader.feature.math;

import com.neo.autotrader.feature.Function;
import com.neo.autotrader.feature.Value;

public class Power implements Function {
	
	private int pow;
	
	public Power(int pow) {
		this.pow = pow;
	}

	@Override
	public Double evaluate(Value self, int base, Value... values) {
		return Math.pow(values[0].get(base, 0), pow);
	}
}
