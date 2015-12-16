package com.neo.autotrader.util;

import java.util.List;

public final class Utility {

	public static <T> void reverse(List<T> list) {
		int length = list.size();
		for (int i = 0; i < length / 2; i++) {
			int counterpart = length - 1 - i;
			T dailyInfo = list.get(i);
			list.set(i, list.get(counterpart));
			list.set(counterpart, dailyInfo);
		}
	}
}
