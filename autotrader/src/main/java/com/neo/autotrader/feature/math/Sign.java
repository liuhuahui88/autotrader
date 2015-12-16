package com.neo.autotrader.feature.math;

import com.neo.autotrader.feature.Function;
import com.neo.autotrader.feature.Value;

public class Sign implements Function {

	@Override
	public Double evaluate(Value self, int base, Value... values) {
		return Math.signum(values[0].get(base, 0));
	}
}
