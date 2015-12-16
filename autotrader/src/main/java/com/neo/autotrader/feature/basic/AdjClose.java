package com.neo.autotrader.feature.basic;

import java.util.List;
import com.neo.autotrader.data.DailyInfo;

public class AdjClose extends Basic {

	public AdjClose(List<DailyInfo> historyInfos) {
		super(historyInfos);
	}

	@Override
	protected Double innerEvaluate(DailyInfo dailyInfo) {
		return dailyInfo.adjClose;
	}
}
