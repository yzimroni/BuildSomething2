package net.yzimroni.buildsomething2.player.achievement;

public class AchievementInfo {

	private BAchievement achievement;
	private boolean saved; //To know if we need to insert the achievemnet into the database (only new set to false, once saved set to true,
	//when load from db automaticlly set to true)
	private long date;
	private boolean messageSent = true;

	public AchievementInfo(BAchievement achievement, boolean saved, long date) {
		this.achievement = achievement;
		this.saved = saved;
		this.date = date;
	}
	
	public AchievementInfo(BAchievement achievement) {
		this(achievement, false, System.currentTimeMillis());
	}

	public BAchievement getAchievement() {
		return achievement;
	}

	public long getDate() {
		return date;
	}

	public boolean hasSaved() {
		return saved;
	}

	public void setSaved(boolean saved) {
		this.saved = saved;
	}

	public boolean isMessageSent() {
		return messageSent;
	}

	public void setMessageSent(boolean messageSent) {
		this.messageSent = messageSent;
	}

	@Override
	public String toString() {
		return "AchievementInfo [achievement=" + achievement + ", saved=" + saved + ", date=" + date + ", messageSent=" + messageSent + "]";
	}
	
}
