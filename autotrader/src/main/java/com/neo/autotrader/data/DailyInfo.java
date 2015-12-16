package com.neo.autotrader.data;

import java.util.Date;

public final class DailyInfo {

	public final Date date;
	public final double open;
	public final double high;
	public final double low;
	public final double close;
	public final int volume;
	public final double adjClose;
	
	public DailyInfo(Date date, double open, double high, double low,
			double close, int volume, double adjClose) {
		this.date = date;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
		this.adjClose = adjClose;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Date=").append(date).append(", ");
		builder.append("Open=").append(open).append(", ");
		builder.append("High=").append(high).append(", ");
		builder.append("Low=").append(low).append(", ");
		builder.append("Close=").append(close).append(", ");
		builder.append("Volume=").append(volume).append(", ");
		builder.append("AdjClose=").append(adjClose);
		return builder.toString();
	}
}
