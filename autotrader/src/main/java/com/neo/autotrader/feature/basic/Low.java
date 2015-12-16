package com.neo.autotrader.feature.basic;

import java.util.List;
import com.neo.autotrader.data.DailyInfo;

public class Low extends Basic {

	public Low(List<DailyInfo> historyInfos) {
		super(historyInfos);
	}

	@Override
	protected Double innerEvaluate(DailyInfo dailyInfo) {
		return dailyInfo.low;
	}
}
