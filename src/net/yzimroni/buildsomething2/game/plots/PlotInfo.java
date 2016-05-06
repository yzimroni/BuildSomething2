package net.yzimroni.buildsomething2.game.plots;

import java.util.HashMap;
import java.util.UUID;

public class PlotInfo {

	private String plotId;
	private HashMap<UUID, Integer> builders = new HashMap<UUID, Integer>();

	public PlotInfo(String plotId) {
		this.plotId = plotId;
	}

	public String getPlotId() {
		return plotId;
	}

	public void setPlotId(String plotId) {
		this.plotId = plotId;
	}

	public HashMap<UUID, Integer> getBuilders() {
		return builders;
	}

	public void setBuilders(HashMap<UUID, Integer> builders) {
		this.builders = builders;
	}

	public void addBuilder(UUID player, int npcId) {
		if (!builders.containsKey(player)) {
			builders.put(player, npcId);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((builders == null) ? 0 : builders.hashCode());
		result = prime * result + ((plotId == null) ? 0 : plotId.hashCode());
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
		if (!(obj instanceof PlotInfo)) {
			return false;
		}
		PlotInfo other = (PlotInfo) obj;
		if (builders == null) {
			if (other.builders != null) {
				return false;
			}
		} else if (!builders.equals(other.builders)) {
			return false;
		}
		if (plotId == null) {
			if (other.plotId != null) {
				return false;
			}
		} else if (!plotId.equals(other.plotId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "PlotInfo [plotId=" + plotId + ", builders=" + builders + "]";
	}

}
