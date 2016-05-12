package net.yzimroni.buildsomething2.player.stats;

import net.yzimroni.buildsomething2.player.PlayerData;

public class GlobalStats {

	private PlayerData data;

	public GlobalStats(PlayerData data) {
		super();
		this.data = data;
	}

	public int getTotalGames() {
		int totalGames = 0;
		for (GameTypeStats stat : data.getStats().values()) {
			totalGames += stat.getTotalGames();
		}
		return totalGames;
	}

	public int getBuilder() {
		int builder = 0;
		for (GameTypeStats stat : data.getStats().values()) {
			builder += stat.getBuilder();
		}
		return builder;
	}

	public int getNormal() {
		int normal = 0;
		for (GameTypeStats stat : data.getStats().values()) {
			normal += stat.getNormal();
		}
		return normal;
	}

	public int getKnow() {
		int know = 0;
		for (GameTypeStats stat : data.getStats().values()) {
			know += stat.getKnow();
		}
		return know;
	}

	public int getKnowFirst() {
		int knowFirst = 0;
		for (GameTypeStats stat : data.getStats().values()) {
			knowFirst += stat.getKnowFirst();
		}
		return knowFirst;
	}

	public int getAllKnow() {
		int allKnow = 0;
		for (GameTypeStats stat : data.getStats().values()) {
			allKnow += stat.getAllKnow();
		}
		return allKnow;
	}

}
