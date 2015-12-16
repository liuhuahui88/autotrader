package com.neo.autotrader.backtester;

import java.util.LinkedList;

import com.neo.autotrader.data.DailyInfo;

public class WindowStrategy implements Strategy {

	private final int length;
	private final LinkedList<DailyInfo> dailyInfos;
	
	public WindowStrategy(int length) {
		this.length = length;
		dailyInfos = new LinkedList<DailyInfo>();
	}

	@Override
	public void rebalance(DailyInfo dailyInfo, Operator operator) {
		if (dailyInfos.size() < length) {
			dailyInfos.addFirst(dailyInfo);
			return;
		}
		
		double threshold = 0.03D;
		
		DailyInfo lastDayInfo = dailyInfos.getFirst();
		if (dailyInfo.adjClose > lastDayInfo.adjClose * (1 + threshold)) {
			operator.buy(operator.maxBuyVolume());
		} else if (dailyInfo.adjClose < lastDayInfo.adjClose * (1 - threshold)) {
			operator.sell(operator.maxSellVolume());
		}
		
		dailyInfos.addFirst(dailyInfo);
		dailyInfos.removeLast();
	}
}
