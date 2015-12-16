package com.neo.autotrader.backtester;

public class Operator {

	private final Account account;
	private final double price;
	
	public Operator(Account account, double price) {
		this.account = account;
		this.price = price;
	}
	
	public double getCash() {
		return account.getCash();
	}
	
	public int getStock() {
		return account.getStock();
	}
	
	public double getTaxRatio() {
		return account.getTaxRatio();
	}
	
	public double evaluate() {
		return account.evaluate(price);
	}
	
	public int maxBuyVolume() {
		return account.maxBuyVolume(price);
	}
	
	public int maxSellVolume() {
		return account.maxSellVolume();
	}
	
	public boolean buy(int volume) {
		return account.buy(volume, price);
	}
	
	public boolean sell(int volume) {
		return account.sell(volume, price);
	}
}
