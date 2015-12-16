package com.neo.autotrader.backtester;

public class Account {

	private double cash;
	private int stock;
	private double taxRatio;

	public Account(double cash, int stock, double taxRatio) {
		this.cash = cash;
		this.stock = stock;
		this.taxRatio = taxRatio;
	}

	public double getCash() {
		return cash;
	}

	public int getStock() {
		return stock;
	}

	public double getTaxRatio() {
		return taxRatio;
	}

	public double taxedBuyPrice(double price) {
		return price * (1 + taxRatio);
	}

	public double taxedSellPrice(double price) {
		return price * (1 - taxRatio);
	}

	public double evaluate(double price) {
		return cash + stock * taxedSellPrice(price);
	}

	public int maxBuyVolume(double price) {
		return (int)(cash / taxedBuyPrice(price));
	}

	public int maxSellVolume() {
		return stock;
	}

	public boolean buy(int volume, double price) {
		if (volume > maxBuyVolume(price)) {
			return false;
		}
		//if (volume != 0) System.out.println("buy " + volume + " at " + price);
		double cost = volume * taxedBuyPrice(price);
		cash -= cost;
		stock += volume;
		return true;
	}

	public boolean sell(int volume, double price) {
		if (volume > maxSellVolume()) {
			return false;
		}
		//if (volume != 0) System.out.println("sell " + volume + " at " + price);
		double earn = volume * taxedSellPrice(price);
		cash += earn;
		stock -= volume;
		return true;
	}
}
