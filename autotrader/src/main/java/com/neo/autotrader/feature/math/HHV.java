package com.neo.autotrader.feature.math;

import com.neo.autotrader.feature.Function;
import com.neo.autotrader.feature.Value;

public class HHV implements Function {
	
	private int width;

	public HHV(int width) {
		this.width = width;
	}

	@Override
	public Double evaluate(Value self, int base, Value... values) {
		Value value = values[0];
		
		Double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i > -width; i--) {
			Double v = value.get(base, i);
			if (v == null) {
				break;
			}
			max = v > max ? v : max;
		}
		return max;
	}
}
