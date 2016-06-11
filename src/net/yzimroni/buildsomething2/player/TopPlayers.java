package net.yzimroni.buildsomething2.player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;

public class TopPlayers {
	private BuildSomethingPlugin plugin;
	private List<UUID> tops;
	
	public TopPlayers(BuildSomethingPlugin p) {
		plugin = p;
		tops = new ArrayList<UUID>();
	}
	
	public void update() {
		tops.clear();
		try {
			//TODO
			/*ResultSet rs = plugin.getDB().get("SELECT * FROM players ORDER BY ((total*2) + (know * 100)) DESC LIMIT 3");
			while (rs.next()) {
				tops.add(UUID.fromString(rs.getString("UUID")));
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		updateViews();
	}
	
	private void updateViews() {
		
	}
}