package net.yzimroni.buildsomething2.command;

import java.util.UUID;

import net.yzimroni.buildsomething2.game.games.Game;

public class CommandSenderData {
	
	private UUID uuid;
	private Game game;
	
	public CommandSenderData() {
		super();
	}

	public CommandSenderData(UUID uuid, Game game) {
		super();
		this.uuid = uuid;
		this.game = game;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	@Override
	public String toString() {
		return "CommandSenderData [uuid=" + uuid + ", game=" + game + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((game == null) ? 0 : game.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
		if (!(obj instanceof CommandSenderData)) {
			return false;
		}
		CommandSenderData other = (CommandSenderData) obj;
		if (game == null) {
			if (other.game != null) {
				return false;
			}
		} else if (!game.equals(other.game)) {
			return false;
		}
		if (uuid == null) {
			if (other.uuid != null) {
				return false;
			}
		} else if (!uuid.equals(other.uuid)) {
			return false;
		}
		return true;
	}

}
