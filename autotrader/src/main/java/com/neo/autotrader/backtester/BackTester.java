package com.neo.autotrader.backtester;

import com.neo.autotrader.data.DailyInfo;
import com.neo.autotrader.data.StockInfo;
import com.neo.autotrader.spider.YahooSpider;

public class BackTester {
	
	private final StockInfo stockInfo;
	private final double taxRatio;
	
	public BackTester(StockInfo stockInfo, double taxRatio) {
		this.stockInfo = stockInfo;
		this.taxRatio = taxRatio;
	}
	
	public double evaluate(double cash, Strategy strategy) {
		Account account = new Account(cash, 0, taxRatio);
		replay(account, strategy);
		int lastIndex = stockInfo.historyInfo.size() - 1;
		double lastPrice = stockInfo.historyInfo.get(lastIndex).adjClose;
		return (account.evaluate(lastPrice) - cash) / cash;
	}
	
	private void replay(Account account, Strategy strategy) {
		for (DailyInfo dailyInfo : stockInfo.historyInfo) {
			Operator operator = new Operator(account, dailyInfo.adjClose);
			strategy.rebalance(dailyInfo, operator);
		}
	}

	public static void main(String[] args) {
		final String id = "600000.SS";
		
		final double cash = 50000;
		final double taxRatio = 0.001F;
		
		StockInfo stockInfo = YahooSpider.spide(id);
		System.out.println("spider done");
		System.out.println(stockInfo.historyInfo.get(0));
		int lastIndex = stockInfo.historyInfo.size() - 1;
		System.out.println(stockInfo.historyInfo.get(lastIndex));
		BackTester backTester = new BackTester(stockInfo, taxRatio);
		System.out.println(backTester.evaluate(cash, new BaselineStrategy()));
		System.out.println(backTester.evaluate(cash, new WindowStrategy(1)));
	}
}
