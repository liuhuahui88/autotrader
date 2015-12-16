package com.neo.autotrader.backtester;

import com.neo.autotrader.data.DailyInfo;

public class BaselineStrategy implements Strategy {

	@Override
	public void rebalance(DailyInfo dailyInfo, Operator operator) {
		int maxBuyVolume = operator.maxBuyVolume();
		if (maxBuyVolume > 0) {
			operator.buy(maxBuyVolume);
		}
	}
}
