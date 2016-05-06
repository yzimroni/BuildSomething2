package net.yzimroni.buildsomething2.player.achievement;

public enum BAchievement {
	JOIN("Join the server", "Join the server");
	
	private BAchievement(String name, String description) {
		this.name = name;
		this.description = description;
	}
	private String name;
	private String description;
	
	
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	
}
