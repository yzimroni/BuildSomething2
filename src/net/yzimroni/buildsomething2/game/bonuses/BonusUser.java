package net.yzimroni.buildsomething2.game.bonuses;

public enum BonusUser {
	NORMAL, BUILDER, BOTH;
	
	public boolean canUse(boolean builder) {
		if (this == BOTH) return true;
		if (builder && this == BUILDER) return true;
		if (!builder && this == NORMAL) return true;
		return false;
	}
}
