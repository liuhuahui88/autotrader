package com.neo.autotrader.feature.kdj;

import com.neo.autotrader.feature.Function;
import com.neo.autotrader.feature.Value;
import com.neo.autotrader.feature.math.HHV;
import com.neo.autotrader.feature.math.LLV;

public class RSV implements Function {
	
	private int width;
	private HHV hhv;
	private LLV llv;
	
	public RSV(int width) {
		this.width = width;
		hhv = new HHV(width);
		llv = new LLV(width);
	}

	@Override
	public Double evaluate(Value self, int base, Value... values) {
		Value high = values[0];
		Value low = values[1];
		Value price = values[2];
		
		if (!price.isValid(base, 1 - width)) {
			return 0.5D;
		}
		
		Double highest = hhv.evaluate(null, base, new Value[]{high});
		Double lowest = llv.evaluate(null, base, new Value[]{low});
		if (Math.abs(highest - lowest) <= 1e-6) {
			return 0.5D;
		}
		
		Double v = price.get(base, 0);
		return (v - lowest) / (highest - lowest);
	}
}
