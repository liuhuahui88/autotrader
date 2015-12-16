package com.neo.autotrader.feature.basic;

import java.util.List;
import com.neo.autotrader.data.DailyInfo;
import com.neo.autotrader.feature.Function;
import com.neo.autotrader.feature.Value;

public abstract class Basic implements Function {

	private List<DailyInfo> historyInfos;
	
	public Basic(List<DailyInfo> historyInfos) {
		this.historyInfos = historyInfos;
	}
	
	@Override
	public final Double evaluate(Value self, int base, Value... values) {
		return innerEvaluate(historyInfos.get(base));
	}
	
	protected abstract Double innerEvaluate(DailyInfo dailyInfo);
}
