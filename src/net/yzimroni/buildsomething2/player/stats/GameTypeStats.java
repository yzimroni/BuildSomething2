package net.yzimroni.buildsomething2.player.stats;

import java.sql.ResultSet;
import java.util.UUID;

import net.yzimroni.buildsomething2.game.GameInfo.GameType;

public class GameTypeStats {
	
	/*
	 * How much times their build built in bot game (one for single builders and one for multi builders)
	 * For each game type (single builder, multi builders, bot game):
	 * 		Total games (in this game type)
	 * 		builder (not fot bot game)
	 * 		normal player
	 * 		know the word
	 * 		know the word first
	 * 		All know
	 */

	private UUID uuid;
	private GameType gameType;
	
	private boolean updated;
	private boolean loaded;

	private int totalGames;
	private int builder;
	private int normal;
	private int know;
	private int knowFirst;
	private int allKnow;

	public GameTypeStats(UUID uuid, GameType gameType) {
		this.uuid = uuid;
		this.gameType = gameType;
	}
	
	public void load(ResultSet rs) {
		if (!loaded) {
			try {
				totalGames = rs.getInt("totalGames");
				builder = rs.getInt("builder");
				normal = rs.getInt("normal");
				know = rs.getInt("know");
				knowFirst = rs.getInt("knowFirst");
				allKnow = rs.getInt("knowFirst");
				loaded = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void createEmpty() {
		if (!loaded) {
			totalGames = 0;
			builder = 0;
			normal = 0;
			know = 0;
			knowFirst = 0;
			allKnow = 0;
			loaded = true;
		}
	}
	
	public int getTotalGames() {
		return totalGames;
	}

	public void setTotalGames(int totalGames) {
		this.updated = true;
		this.totalGames = totalGames;
	}
	
	public void addTotalGame() {
		setTotalGames(getTotalGames() + 1);
	}

	public int getBuilder() {
		return builder;
	}

	public void setBuilder(int builder) {
		this.updated = true;
		this.builder = builder;
	}
	
	public void addBuilder() {
		setBuilder(getBuilder() + 1);
	}

	public int getNormal() {
		return normal;
	}

	public void setNormal(int normal) {
		this.updated = true;
		this.normal = normal;
	}
	
	public void addNormal() {
		setNormal(getNormal() + 1);
	}

	public int getKnow() {
		return know;
	}

	public void setKnow(int know) {
		this.updated = true;
		this.know = know;
	}
	
	public void addKnow() {
		setKnow(getKnow() + 1);
	}

	public int getKnowFirst() {
		return knowFirst;
	}

	public void setKnowFirst(int knowFirst) {
		this.updated = true;
		this.knowFirst = knowFirst;
	}
	
	public void addKnowFirst() {
		setKnowFirst(getKnowFirst() + 1);
	}

	public int getAllKnow() {
		return allKnow;
	}

	public void setAllKnow(int allKnow) {
		this.updated = true;
		this.allKnow = allKnow;
	}
	
	public void addAllKnow() {
		setAllKnow(getAllKnow() + 1); 
	}

	public UUID getUuid() {
		return uuid;
	}

	public GameType getGameType() {
		return gameType;
	}

	public boolean isUpdated() {
		return updated;
	}
	
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	public boolean isLoaded() {
		return loaded;
	}
	
}
