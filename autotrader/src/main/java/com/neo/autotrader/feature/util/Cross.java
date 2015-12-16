package com.neo.autotrader.feature.util;

import com.neo.autotrader.feature.Function;
import com.neo.autotrader.feature.Value;

public class Cross implements Function {

	@Override
	public Double evaluate(Value self, int base, Value... values) {
		Value a = values[0];
		Value b = values[1];
		if (!a.isValid(base, -1)) {
			return 0D;
		}
		return a.get(base, -1) < b.get(base, -1) &&
				a.get(base, 0) > b.get(base, 0) ? 1D : 0D;
	}
}
