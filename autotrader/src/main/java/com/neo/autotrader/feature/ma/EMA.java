package com.neo.autotrader.feature.ma;

import com.neo.autotrader.feature.Function;
import com.neo.autotrader.feature.Value;

public class EMA implements Function {

	private SMA sma;

	public EMA(int width) {
		sma = new SMA(width + 1, 2);
	}

	@Override
	public Double evaluate(Value self, int base, Value... values) {
		return sma.evaluate(self, base, values);
	}
}
