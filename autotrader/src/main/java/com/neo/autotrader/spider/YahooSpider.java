package com.neo.autotrader.spider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.neo.autotrader.data.DailyInfo;
import com.neo.autotrader.data.StockInfo;
import com.neo.autotrader.util.Utility;

public class YahooSpider {
	
	private static final String URL_TEMPLATE =
			"http://table.finance.yahoo.com/table.csv?s=";
	private static final SimpleDateFormat DATE_FORMAT =
			new SimpleDateFormat("yyyy-MM-dd");
	
	public static StockInfo spide(String id) {
		List<DailyInfo> historyInfo = spideHistoryInfo(id);
		return historyInfo == null ? null : new StockInfo(id, id, historyInfo);
	}
	
	private static List<DailyInfo> spideHistoryInfo(String id) {
		List<DailyInfo> historyInfo = null;		
		HttpURLConnection connection = null;
		try {
			URL url = new URL(URL_TEMPLATE + id);
			connection = (HttpURLConnection) url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			historyInfo = parse(reader);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return historyInfo;
	}
	
	private static List<DailyInfo> parse(BufferedReader reader)
			throws IOException, ParseException {
		List<DailyInfo> historyInfo = new ArrayList<DailyInfo>();
		String line = reader.readLine();	// ignore title
		while ((line = reader.readLine()) != null) {
			DailyInfo dailyInfo = parse(line);
			if (dailyInfo == null || dailyInfo.volume == 0) {
				continue;
			}
			historyInfo.add(dailyInfo);
		}
		Utility.reverse(historyInfo);
		return historyInfo;
	}
	
	private static DailyInfo parse(String line) throws ParseException {
		String elems[] = line.split(",");
		Date date = DATE_FORMAT.parse(elems[0]);
		double open = Double.parseDouble(elems[1]);
		double high = Double.parseDouble(elems[2]);
		double low = Double.parseDouble(elems[3]);
		double close = Double.parseDouble(elems[4]);
		int volume = Integer.parseInt(elems[5]);
		double adjClose = Double.parseDouble(elems[6]);
		return new DailyInfo(date, open, high, low,
				close, volume, adjClose);
	}

	public static void main(String[] args) {
		System.out.println(spideHistoryInfo("600000.SS"));
	}
}
