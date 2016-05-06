package net.yzimroni.buildsomething2.game;

import java.util.List;
import java.util.UUID;

public class BotPlot {

	/*
	 * TO DO showBuilders (show the builders names?)
	 */

	private int id;
	private int wordId;
	private String plotId;
	private List<UUID> builders;
	private boolean showBuilders = true;

	public BotPlot(int id, int wordId, String plotId, List<UUID> builders, boolean showBuilders) {
		this.id = id;
		this.wordId = wordId;
		this.plotId = plotId;
		this.builders = builders;
		this.showBuilders = showBuilders;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public boolean isShowBuilders() {
		return showBuilders;
	}

	public void setShowBuilders(boolean showBuilders) {
		this.showBuilders = showBuilders;
	}

	public List<UUID> getBuilders() {
		return builders;
	}

	public void setBuilders(List<UUID> builders) {
		this.builders = builders;
	}

}
