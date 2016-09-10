package net.yzimroni.buildsomething2.player;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.GameInfo.GameType;
import net.yzimroni.buildsomething2.player.achievement.AchievementInfo;
import net.yzimroni.buildsomething2.player.achievement.BAchievement;
import net.yzimroni.buildsomething2.player.stats.GameTypeStats;
import net.yzimroni.buildsomething2.player.stats.GlobalStats;

public class PlayerData {
	
	private BuildSomethingPlugin plugin;
	private UUID uuid;
	
	private List<Integer> blocks = new ArrayList<Integer>();
	private List<Integer> new_blocks = new ArrayList<Integer>();
	private List<Integer> effects = new ArrayList<Integer>();
	private List<Integer> new_effects = new ArrayList<Integer>();
	protected HashMap<Integer, Integer> bonuses = new HashMap<Integer, Integer>();
	protected HashMap<Integer, Integer> views_effects = new HashMap<Integer, Integer>();
	private List<Integer> views = new ArrayList<Integer>();
	private List<Integer> new_views = new ArrayList<Integer>();
	
	private List<AchievementInfo> achievements = new ArrayList<AchievementInfo>();
	private HashMap<GameType, GameTypeStats> stats = new HashMap<GameType, GameTypeStats>();
	private GlobalStats globalStats;
	
	private String hotbar_items;
	
	public static PlayerData load(BuildSomethingPlugin p, UUID u) {
		try {
			ResultSet rs = p.getDB().get("SELECT * FROM players WHERE UUID='" + u.toString() + "'");
			rs.first();
			return loadRS(u, rs, p);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static PlayerData loadRS(UUID u, ResultSet rs, BuildSomethingPlugin p) {
		PlayerData d = new PlayerData();
		d.setUuid(u);
		try {
			d.setPlugin(p);
			d.loadStats();
			d.loadBlocks();
			d.loadBonuses();
			d.loadEffects();
			d.loadAchievement();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return d;
	}
	
	private void loadStats() {
		try {
			ResultSet rs = getPlugin().getDB().get("SELECT * FROM playerstats WHERE UUID='" + uuid.toString() + "'");
			while (rs.next()) {
				GameType type = GameType.getById(rs.getInt("gameType"));
				GameTypeStats gamestats = new GameTypeStats(uuid, type);
				gamestats.load(rs);
				stats.put(type, gamestats);
				
			}
			initGlobalStats();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initGlobalStats() {
		setGlobalStats(new GlobalStats(this));
	}
	
	private void loadBlocks() {
		try {
			ResultSet rs = getPlugin().getDB().get("SELECT * FROM playerblocks WHERE UUID='" + uuid.toString() + "'");
			while (rs.next()) {
				blocks.add(rs.getInt("block_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadBonuses() {
		try {
			ResultSet rs = getPlugin().getDB().get("SELECT * FROM playerbonuses WHERE UUID='" + uuid.toString() + "'");
			while (rs.next()) {
				bonuses.put(rs.getInt("bonuseid"), rs.getInt("amount"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadEffects() {
		try {
			ResultSet rs = getPlugin().getDB().get("SELECT * FROM playereffects WHERE UUID='" + uuid.toString() + "'");
			while (rs.next()) {
				effects.add(rs.getInt("effect_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			ResultSet rs = getPlugin().getDB().get("SELECT * FROM playereffectschoose WHERE UUID='" + uuid.toString() + "'");
			while (rs.next()) {
				views_effects.put(rs.getInt("view_id"), rs.getInt("effect_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			ResultSet rs = getPlugin().getDB().get("SELECT * FROM playereffectviews WHERE UUID='" + uuid.toString() + "'");
			while (rs.next()) {
				views.add(rs.getInt("view_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadAchievement() {
		try {
			ResultSet rs = getPlugin().getDB().get("SELECT * FROM achievements WHERE UUID='" + uuid.toString() + "'");
			while (rs.next()) {
				AchievementInfo info = new AchievementInfo(BAchievement.valueOf(rs.getString("achievement")), true, rs.getLong("date"));
				info.setMessageSent(rs.getBoolean("messageSent"));
				achievements.add(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	public static PlayerData createNew(UUID u) {
		PlayerData d = new PlayerData();
		d.setUuid(u);
		d.initGlobalStats();
		
		return d;
	}
	
	private PlayerData() {
		
	}
	
	public GameTypeStats getGameTypeStats(GameType type) {
		if (type == null) {
			return null;
		}
		if (stats.containsKey(type)) {
			return stats.get(type);
		} else {
			GameTypeStats gamestats = new GameTypeStats(uuid, type);
			gamestats.createEmpty();
			stats.put(type, gamestats);
			return gamestats;
		}
	}
	
	public HashMap<GameType, GameTypeStats> getStats() {
		return stats;
	}
	
	public List<GameTypeStats> getUpdatedStats() {
		//TODO check that it is add all the updated stats
		List<GameTypeStats> updated = new ArrayList<GameTypeStats>();
		for (GameTypeStats stat : stats.values()) {
			if (stat.isUpdated()) {
				updated.add(stat);
			}
		}
		return updated;
	}

	public int getBonus(int id) {
		if (bonuses.containsKey(id)) {
			return bonuses.get(id);
		}
		return 0;
	}
	
	public boolean hasBonus(int id) {
		return getBonus(id) > 0;
	}
	
	public void setBonus(int id, int amount) {
		if (bonuses.containsKey(id)) {
			bonuses.remove(id);
		}
		bonuses.put(id, amount);
	}
	
	public void addBonus(int id) {
		setBonus(id, getBonus(id) + 1);
	}
	
	public void takeBonus(int id) {
		setBonus(id, Math.max(getBonus(id) - 1, 0));
	}
	
	public void addBlock(int id) {
		if (!blocks.contains(id)) {
			blocks.add(id);
			new_blocks.add(id);
		}
	}
	
	public boolean hasEffect(int id) {
		return effects.contains(id);
	}
	
	public void addEffect(int id) {
		if (!hasEffect(id)) {
			effects.add(id);
			new_effects.add(id);
		}
	}
	
	public boolean hasView(int id) {
		return views.contains(id);
	}
	
	public void addView(int id) {
		if (!hasView(id)) {
			views.add(id);
			new_views.add(id);
		}
	}
	
	public int getEffectChoose(int use_id) {
		if (!views_effects.containsKey(use_id)) {
			return -1;
		}
		return views_effects.get(use_id);
	}
	
	public void setEffectChoose(int use_id, int effect_id) {
		if (views_effects.containsKey(use_id)) {
			views_effects.remove(use_id);
		}
		views_effects.put(use_id, effect_id);
	}
	
	public boolean hasAchievement(BAchievement achievement) {
		for (AchievementInfo a : achievements) {
			if (a.getAchievement() == achievement) {
				return true;
			}
		}
		return false;
	}
	
	public AchievementInfo getAchievement(BAchievement achievement) {
		for (AchievementInfo a : achievements) {
			if (a.getAchievement() == achievement) {
				return a;
			}
		}
		return null;
	}
	
	public void addAchievementDirect(AchievementInfo a) {
		if (!hasAchievement(a.getAchievement())) {
			a.setSaved(false);
			achievements.add(a);
		} else {
			System.out.println("add Achievement while already have: " + a.getAchievement() + " " + a + " " + toString());
		}
	}
	
	/**
	 * @return the uuid
	 */
	public UUID getUuid() {
		return uuid;
	}
	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public List<Integer> getBlocks() {
		return blocks;
	}

	public void setBlocks(List<Integer> blocks) {
		this.blocks = blocks;
	}

	private BuildSomethingPlugin getPlugin() {
		return plugin;
	}

	private void setPlugin(BuildSomethingPlugin plugin) {
		this.plugin = plugin;
	}

	public List<Integer> getNewBlocks() {
		return new_blocks;
	}

	public void setNewBlocks(List<Integer> new_blocks) {
		this.new_blocks = new_blocks;
	}
	
	public List<Integer> getNewEffects() {
		return new_effects;
	}

	public List<Integer> getNewViews() {
		return new_views;
	}

	/**
	 * @return the hotbar_items
	 */
	public String getHotbarItems() {
		return hotbar_items;
	}

	/**
	 * @param hotbar_items the hotbar_items to set
	 */
	public void setHotbarItems(String hotbar_items) {
		this.hotbar_items = hotbar_items;
	}
	
	public List<AchievementInfo> getAchievements() {
		return achievements;
	}

	public void setAchievements(List<AchievementInfo> achievements) {
		this.achievements = achievements;
	}

	public GlobalStats getGlobalStats() {
		return globalStats;
	}

	public void setGlobalStats(GlobalStats globalStats) {
		this.globalStats = globalStats;
	}

	
}