package com.neo.autotrader.backtester;

import com.neo.autotrader.data.DailyInfo;

public interface Strategy {

	public void rebalance(DailyInfo dailyInfo, Operator operator);
}
