package com.neo.autotrader.feature.math;

import com.neo.autotrader.feature.Function;
import com.neo.autotrader.feature.Value;

public class Multiply implements Function {

	@Override
	public Double evaluate(Value self, int base, Value... values) {
		return values[0].get(base, 0) * values[1].get(base, 0);
	}
}
