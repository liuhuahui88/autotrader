package com.neo.autotrader.feature.basic;

import java.util.List;
import com.neo.autotrader.data.DailyInfo;

public class Open extends Basic {
	
	public Open(List<DailyInfo> historyInfos) {
		super(historyInfos);
	}

	@Override
	protected Double innerEvaluate(DailyInfo dailyInfo) {
		return dailyInfo.open;
	}
}
