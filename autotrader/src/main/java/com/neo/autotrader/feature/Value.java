package com.neo.autotrader.feature;

import java.util.ArrayList;

public class Value {

	private String name;
	private ArrayList<Double> values;
	
	public Value(String name) {
		this.name = name;
		values = new ArrayList<Double>();
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isValid(int base, int index) {
		int actualIndex = base + index;
		return actualIndex >= 0 && actualIndex < values.size();
	}
	
	public void append(Double d) {
		values.add(d);
	}

	public Double get(int base, int index) {
		int actualIndex = base + index;
		if (actualIndex >= 0 && actualIndex < values.size()) {
			return values.get(actualIndex);
		} else {
			return null;
		}
	}
}
