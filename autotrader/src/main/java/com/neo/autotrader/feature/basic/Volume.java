package com.neo.autotrader.feature.basic;

import java.util.List;
import com.neo.autotrader.data.DailyInfo;

public class Volume extends Basic {

	public Volume(List<DailyInfo> historyInfos) {
		super(historyInfos);
	}

	@Override
	protected Double innerEvaluate(DailyInfo dailyInfo) {
		return (double) dailyInfo.volume;
	}
}
