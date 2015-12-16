package com.neo.autotrader.data;

import java.util.ArrayList;
import java.util.List;

public final class StockInfo {

	public final String id;
	public final String name;
	public final List<DailyInfo> historyInfo;
	
	public StockInfo(String id, String name) {
		this(id, name, new ArrayList<DailyInfo>());
	}
	
	public StockInfo(String id, String name, List<DailyInfo> historyInfo) {
		this.id = id;
		this.name = name;
		this.historyInfo = historyInfo;
	}
}
