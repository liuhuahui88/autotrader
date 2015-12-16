package com.neo.autotrader.feature.util;

import com.neo.autotrader.feature.Function;
import com.neo.autotrader.feature.Value;

public class IncreaseCycle implements Function {

	@Override
	public Double evaluate(Value self, int base, Value... values) {
		Value value = values[0];

		double v = value.get(base, 0);
		int count = 0;
		for (int i = -1; value.isValid(base, i) &&
				v > value.get(base, i); i--) {
			v = value.get(base, i);
			count++;
		}
		return (double)count;
	}
}
