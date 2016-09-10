package net.yzimroni.buildsomething2.game;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.player.BPlayer;

public class Builders {

	private BuildSomethingPlugin plugin;
	private List<BPlayer> builders;
	
	public Builders(BuildSomethingPlugin p) {
		plugin = p;
		builders = new ArrayList<BPlayer>();
	}
	
	public boolean isBuilder(UUID u) {
		if (u == null) return false;
		if (builders.isEmpty()) return false;
		for (BPlayer p : builders) {
			if (p.getUUID().equals(u) && p.getPlayer() != null) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isBuilder(BPlayer p) {
		if (p == null) return false;
		return p.getPlayer() != null && isBuilder(p.getUUID());
	}
	
	public boolean isBuilder(Player p) {
		if (p == null) return false;
		return isBuilder(p.getUniqueId());
	}
	
	public void addBuilder(BPlayer p) {
		if (p == null) return;
		if (!isBuilder(p)) {
			builders.add(p);
		}
	}
	
	public void clear() {
		builders.clear();
	}
	
	public void addBuilder(Player p) {
		addBuilder(plugin.getPlayerManager().getPlayer(p));
	}
	
	public int size() {
		int i = 0;
		
		for (BPlayer bp : builders) {
			if (bp.getPlayer() != null) {
				i++;
			}
		}
		
		return i;
	}
	
	public boolean isEmpty() {
		return size() == 0;
	}
	
	public List<BPlayer> getBPlayers() {
		List<BPlayer> bps = new ArrayList<BPlayer>();
		for (BPlayer bp : builders) {
			if (bp.getPlayer() != null) {
				bps.add(bp);
			}
		}
		return bps;
	}
	
	public List<Player> getPlayers() {
		List<Player> bps = new ArrayList<Player>();
		for (BPlayer bp : builders) {
			if (bp.getPlayer() != null) {
				bps.add(bp.getPlayer());
			}
		}
		return bps;
	}
	
	public List<UUID> getUUIDs() {
		List<UUID> uds = new ArrayList<UUID>();
		for (BPlayer bp : builders) {
			if (bp.getPlayer() != null) {
				uds.add(bp.getUUID());
			}
		}
		return uds;
	}
	
	public void removeBuilder(Player p) {
		if (isBuilder(p)) {
			BPlayer bp = null;
			for (BPlayer bpo : builders) {
				if (bpo.getUUID().equals(p.getUniqueId())) {
					bp = bpo;
					break;
				}
			}
			if (bp != null) {
				builders.remove(bp);
			} else {
				System.out.println("bp is null in removeBuilder: " + p.toString() + " " + builders.toString());
			}
		}
	}

	@Override
	public String toString() {
		int size = size();
		if (size == 1) {
			return getPlayers().get(0).getName();
		} else if (size > 1) {
			String result = "";
			List<Player> list = getPlayers();
			for (int i = 0; i < list.size(); i++) {
				Player p = list.get(i);
				if (!result.isEmpty()) {
					if (i == (list.size() - 1)) {
						result += " and ";
					} else {
						result += ", ";
					}
				}
				result += p.getName();
			}
			return result;
		} else {
			return "No builders";
		}
	}
	
	
	
}
