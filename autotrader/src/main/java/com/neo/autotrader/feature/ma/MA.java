package com.neo.autotrader.feature.ma;

import com.neo.autotrader.feature.Function;
import com.neo.autotrader.feature.Value;

public class MA implements Function {
	
	private int width;

	public MA(int width) {
		this.width = width;
	}

	@Override
	public Double evaluate(Value self, int base, Value... values) {
		Value value = values[0];
		
		Double sum = 0D;
		int count = 0;
		for (int i = 0; i > -width; i--) {
			Double v = value.get(base, i);
			if (v == null) {
				break;
			}
			sum += v;
			count++;
		}
		return count == 0 ? 0D : sum / count;
	}
}
