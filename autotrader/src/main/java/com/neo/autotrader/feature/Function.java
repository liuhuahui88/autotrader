package com.neo.autotrader.feature;

public interface Function {

	public Double evaluate(Value self, int base, Value... values);
}
