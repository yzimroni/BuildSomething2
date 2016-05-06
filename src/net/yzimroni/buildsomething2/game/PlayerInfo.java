package net.yzimroni.buildsomething2.game;

import java.util.UUID;

public class PlayerInfo {

	private UUID player; //V
	private int playerType; //V 0 = normal player, 1 = builder, 2 = player in bot game
	private int npcId; //V Only if player_type is 2
	private long knowTime; //V If player type is not 2
	
	public PlayerInfo(UUID player, int playerType, int npcId, long knowTime) {
		this.player = player;
		this.playerType = playerType;
		this.npcId = npcId;
		this.knowTime = knowTime;
	}
	
	public UUID getPlayer() {
		return player;
	}
	public void setPlayer(UUID player) {
		this.player = player;
	}
	public int getPlayerType() {
		return playerType;
	}
	public void setPlayerType(int playerType) {
		this.playerType = playerType;
	}
	public int getNpcId() {
		return npcId;
	}
	public void setNpcId(int npcId) {
		this.npcId = npcId;
	}
	public long getKnowTime() {
		return knowTime;
	}
	public void setKnowTime(long knowTime) {
		this.knowTime = knowTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (knowTime ^ (knowTime >>> 32));
		result = prime * result + npcId;
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		result = prime * result + playerType;
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
		if (!(obj instanceof PlayerInfo)) {
			return false;
		}
		PlayerInfo other = (PlayerInfo) obj;
		if (knowTime != other.knowTime) {
			return false;
		}
		if (npcId != other.npcId) {
			return false;
		}
		if (player == null) {
			if (other.player != null) {
				return false;
			}
		} else if (!player.equals(other.player)) {
			return false;
		}
		if (playerType != other.playerType) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "PlayerInfo [player=" + player + ", playerType=" + playerType + ", npcId=" + npcId + ", knowTime=" + knowTime + "]";
	}


	
	
	
}
