package com.neo.autotrader.feature.math;

import com.neo.autotrader.feature.Function;
import com.neo.autotrader.feature.Value;

public class Sum implements Function {
	
	private int width;

	public Sum(int width) {
		this.width = width;
	}

	@Override
	public Double evaluate(Value self, int base, Value... values) {
		Value value = values[0];
		
		Double sum = 0D;
		for (int i = 0; i > -width; i--) {
			Double v = value.get(base, i);
			if (v == null) {
				break;
			}
			sum += v;
		}
		return sum;
	}
}
