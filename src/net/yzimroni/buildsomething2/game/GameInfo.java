package net.yzimroni.buildsomething2.game;

import java.util.ArrayList;
import java.util.List;

public class GameInfo {
	
	/*
	 * TODO:
	 * add waitTime (how much time the game was in lobby and lobby countdown mode, from the init of the game until the starting)
	 */

	private int gameId = -1; //V
	private int mapId = -1; //V
	private int wordId = -1; //V
	private String plotId ; //V
	//private int gameType = -1; //V 0 = 1 builder game, 1 = two (or more) builders game, 2 = bot game
	private GameType gameType = null;
	private List<PlayerInfo> players = new ArrayList<PlayerInfo>(); //V
	private long date = -1; //V
	private int knowCount = -1; //V
	//private int plotType = -1; //V 0 = normal plot, 1 = bot plot, 2 = build by plot in this game (the plot_id will be the original plot, but the plot type not)
	private PlotType plotType = null;
	private long gameLength = -1; //V
	
	public GameInfo() {
		
	}
	
	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	public int getWordId() {
		return wordId;
	}

	public void setWordId(int wordId) {
		this.wordId = wordId;
	}

	public String getPlotId() {
		return plotId;
	}

	public void setPlotId(String plotId) {
		this.plotId = plotId;
	}

	public GameType getGameType() {
		return gameType;
	}

	public void setGameType(GameType gameType) {
		this.gameType = gameType;
	}

	public List<PlayerInfo> getPlayers() {
		return players;
	}
	
	public void addPlayer(PlayerInfo player) {
		if (!players.contains(player)) {
			players.add(player);
		}
	}

	public void setPlayers(List<PlayerInfo> players) {
		this.players = players;
	}
	
	public int getPlayersCount() {
		return this.players.size();
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public int getKnowCount() {
		return knowCount;
	}

	public void setKnowCount(int knowCount) {
		this.knowCount = knowCount;
	}

	public PlotType getPlotType() {
		return plotType;
	}

	public void setPlotType(PlotType plotType) {
		this.plotType = plotType;
	}

	public long getGameLength() {
		return gameLength;
	}

	public void setGameLength(long gameLength) {
		this.gameLength = gameLength;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (date ^ (date >>> 32));
		result = prime * result + gameId;
		result = prime * result + (int) (gameLength ^ (gameLength >>> 32));
		result = prime * result + ((gameType == null) ? 0 : gameType.hashCode());
		result = prime * result + knowCount;
		result = prime * result + mapId;
		result = prime * result + ((players == null) ? 0 : players.hashCode());
		result = prime * result + ((plotId == null) ? 0 : plotId.hashCode());
		result = prime * result + ((plotType == null) ? 0 : plotType.hashCode());
		result = prime * result + wordId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof GameInfo)) {
			return false;
		}
		GameInfo other = (GameInfo) obj;
		if (date != other.date) {
			return false;
		}
		if (gameId != other.gameId) {
			return false;
		}
		if (gameLength != other.gameLength) {
			return false;
		}
		if (gameType != other.gameType) {
			return false;
		}
		if (knowCount != other.knowCount) {
			return false;
		}
		if (mapId != other.mapId) {
			return false;
		}
		if (players == null) {
			if (other.players != null) {
				return false;
			}
		} else if (!players.equals(other.players)) {
			return false;
		}
		if (plotId == null) {
			if (other.plotId != null) {
				return false;
			}
		} else if (!plotId.equals(other.plotId)) {
			return false;
		}
		if (plotType != other.plotType) {
			return false;
		}
		if (wordId != other.wordId) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "GameInfo [gameId=" + gameId + ", mapId=" + mapId + ", wordId=" + wordId + ", plotId=" + plotId + ", gameType=" + gameType
			+ ", players=" + players + ", date=" + date + ", knowCount=" + knowCount + ", plotType=" + plotType + ", gameLength=" + gameLength + "]";
	}

	public enum PlotType {
		NORMAL_PLOT(0), BOT_PLOT(1), PLOT_BUILT_BY_BOT(2);
		private int id;
		
		private PlotType(int i) {
			this.id = i;
		}
		
		public int getId() {
			return this.id;
		}
	}
		
	public enum GameType {
		SINGLE_BUILDER_GAME(0), MULTI_BUILDERS_GAME(1), BOT_GAME(2);
		
		private int id;
		
		private GameType(int i) {
			this.id = i;
		}
		
		public int getId() {
			return this.id;
		}

	}
	
}