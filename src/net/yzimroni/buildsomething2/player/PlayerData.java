package net.yzimroni.buildsomething2.player;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.player.achievement.AchievementInfo;
import net.yzimroni.buildsomething2.player.achievement.BAchievement;

public class PlayerData {
	private UUID uuid;
	private int totalgames;
	private int builder;
	private int normalplayer;
	private int knowtheword;
	private int knowthewordfirst;
	private int allKnow;
	private BuildSomethingPlugin plugin;
	private List<Integer> blocks = new ArrayList<Integer>();
	private List<Integer> new_blocks = new ArrayList<Integer>();
	private List<Integer> effects = new ArrayList<Integer>();
	private List<Integer> new_effects = new ArrayList<Integer>();
	protected HashMap<Integer, Integer> bonuses = new HashMap<Integer, Integer>();
	protected HashMap<Integer, Integer> views_effects = new HashMap<Integer, Integer>();
	private List<Integer> views = new ArrayList<Integer>();
	private List<Integer> new_views = new ArrayList<Integer>();
	private String hotbar_items;
	private List<AchievementInfo> achievements = new ArrayList<AchievementInfo>();
	
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
			d.setTotalGames(rs.getInt("total"));
			d.setBuilder(rs.getInt("builder"));
			d.setNormalPlayer(rs.getInt("normal"));
			d.setKnow(rs.getInt("know"));
			d.setKnowFirst(rs.getInt("knowfirst"));
			d.setHotbarItems(rs.getString("hotbar_items"));
			d.setAllKnow(rs.getInt("allknow"));
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
		//TO DO pref
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
		
	/*public static PlayerData create(UUID u, int total, int builder, int normal, int know, int knowfirst, List<Integer> blocks, int allknow) {
		PlayerData d = new PlayerData();
		d.setUuid(u);
		d.setTotalGames(total);
		d.setBuilder(builder);
		d.setNormalPlayer(normal);
		d.setKnow(know);
		d.setKnowFirst(knowfirst);
		d.setBlocks(blocks);
		d.setAllKnow(allknow);
		return d;
	}*/
	
	public static PlayerData createNew(UUID u) {
		PlayerData d = new PlayerData();
		d.setUuid(u);
		//d.setBlocks(new ArrayList<Integer>()); TODO check
		
		return d;
	}
	
	private PlayerData() {
		
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
	/**
	 * @return the totalgames
	 */
	public int getTotalGames() {
		return totalgames;
	}
	/**
	 * @param totalgames the totalgames to set
	 */
	public void setTotalGames(int totalgames) {
		this.totalgames = totalgames;
	}
	/**
	 * @return the builder
	 */
	public int getBuilder() {
		return builder;
	}
	/**
	 * @param builder the builder to set
	 */
	public void setBuilder(int builder) {
		this.builder = builder;
	}
	/**
	 * @return the normalplayer
	 */
	public int getNormalPlayer() {
		return normalplayer;
	}
	/**
	 * @param normalplayer the normalplayer to set
	 */
	public void setNormalPlayer(int normalplayer) {
		this.normalplayer = normalplayer;
	}
	/**
	 * @return the knowtheword
	 */
	public int getKnow() {
		return knowtheword;
	}
	/**
	 * @param knowtheword the knowtheword to set
	 */
	public void setKnow(int knowtheword) {
		this.knowtheword = knowtheword;
	}
	/**
	 * @return the knowthewordfirst
	 */
	public int getKnowFirst() {
		return knowthewordfirst;
	}
	/**
	 * @param knowthewordfirst the knowthewordfirst to set
	 */
	public void setKnowFirst(int knowthewordfirst) {
		this.knowthewordfirst = knowthewordfirst;
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

	/**
	 * @return the allKnow
	 */
	public int getAllKnow() {
		return allKnow;
	}

	/**
	 * @param allKnow the allKnow to set
	 */
	public void setAllKnow(int allKnow) {
		this.allKnow = allKnow;
	}
	
	public List<AchievementInfo> getAchievements() {
		return achievements;
	}

	public void setAchievements(List<AchievementInfo> achievements) {
		this.achievements = achievements;
	}
	
}