package com.neo.autotrader.feature.math;

import com.neo.autotrader.feature.Function;
import com.neo.autotrader.feature.Value;

public class Compare implements Function {

	@Override
	public Double evaluate(Value self, int base, Value... values) {
		Value value1 = values[0];
		Value value2 = values[1];
		
		Double v1 = value1.get(base, 0);
		Double v2 = value2.get(base, 0);
		return Double.valueOf(v1.compareTo(v2));
	}
}
