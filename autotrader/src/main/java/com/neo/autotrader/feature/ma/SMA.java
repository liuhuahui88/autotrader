package com.neo.autotrader.feature.ma;

import com.neo.autotrader.feature.Function;
import com.neo.autotrader.feature.Value;

public class SMA implements Function {
	
	private int width;
	private int m;

	public SMA(int width, int m) {
		this.width = width;
		this.m = m;
	}

	@Override
	public Double evaluate(Value self, int base, Value... values) {
		Value value = values[0];

		Double previous = self.get(base, -1);
		Double v = value.get(base, 0);
		if (previous == null) {
			return v;
		} else {
			return ((width - m) * previous + m * v) / width;
		}
	}
}
