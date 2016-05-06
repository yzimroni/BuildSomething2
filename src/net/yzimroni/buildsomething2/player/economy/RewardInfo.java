package net.yzimroni.buildsomething2.player.economy;

public class RewardInfo {
	private double coins;
	private long timeTook;

	/**
	 * @return the coins
	 */
	public double getCoins() {
		return coins;
	}

	/**
	 * @param coins the coins to set
	 */
	public void setCoins(double coins) {
		this.coins = coins;
	}
	
	public void addCoins(double coins) {
		this.coins += coins;
	}

	public Long getTimeTook() {
		return timeTook;
	}

	public void setTimeTook(long timeTook) {
		this.timeTook = timeTook;
	}
}
