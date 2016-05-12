package net.yzimroni.buildsomething2.game.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.GameInfo;
import net.yzimroni.buildsomething2.game.GameManager;
import net.yzimroni.buildsomething2.game.Gamemode;
import net.yzimroni.buildsomething2.game.LanguageOptions;
import net.yzimroni.buildsomething2.game.Map;
import net.yzimroni.buildsomething2.game.Word;
import net.yzimroni.buildsomething2.game.bonuses.bonuses.Bonus;
import net.yzimroni.buildsomething2.player.BPlayer;
import net.yzimroni.buildsomething2.player.economy.RewardInfo;
import net.yzimroni.buildsomething2.scoreboard.SimpleScoreboard;
import net.yzimroni.buildsomething2.utils.IntWarpper;
import net.yzimroni.buildsomething2.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
public abstract class Game {
	
	public static final int MIN_PLAYERS_START = 2;
	public static final int LOOBY_COUNT_DOWN_TIME = 10/* 30 */;
	public static final int GAME_TIME = 300;

	protected BuildSomethingPlugin plugin;
	protected GameManager manager;

	private UUID gameId;
	private int gameNumberId;

	protected GameInfo gameInfo;

	protected Gamemode mode = Gamemode.LOBBY;
	protected Map map;
	protected int maxPlayers;
	protected List<BPlayer> players = new ArrayList<BPlayer>();
	protected Word word = null;
	protected List<UUID> knows = new ArrayList<UUID>();

	protected boolean forceStart = false;
	protected LanguageOptions languageOptions;

	protected SimpleScoreboard scoreboard;
	protected HashMap<String, String> metadata = new HashMap<String, String>();
	protected HashMap<UUID, RewardInfo> rewards = new HashMap<UUID, RewardInfo>();
	protected HashMap<UUID, ChatInfo> chats = new HashMap<UUID, ChatInfo>();

	protected IntWarpper task_id_lobby = new IntWarpper();
	protected IntWarpper task_id_game = new IntWarpper();

	protected IntWarpper lobby_count_down;
	protected IntWarpper game_count_down;

	private static List<String> symbols;	
	
	public Game(GameManager gm, int maxPlayers) {
		gameId = UUID.randomUUID();
		long i = (gameId.getLeastSignificantBits() * gameId.getMostSignificantBits()); 
		if (i < 0) {
			i = -i;
		}
		while (i > 9999999) {
			i = i / 10;
		}
		gameNumberId = (int) i;
		manager = gm;
		plugin = gm.plugin;
		
		this.maxPlayers = maxPlayers;
		
		lobby_count_down = new IntWarpper();
		lobby_count_down.setValue(LOOBY_COUNT_DOWN_TIME + 1);
		game_count_down = new IntWarpper();
		game_count_down.setValue(GAME_TIME);
		
		map = manager.randomMap();
		map.setUse(true);
		
		scoreboard = plugin.getScoreboardManager().createGameScoreboard(this);
		
		gameInfo = new GameInfo();
		gameInfo.setGameId(gameNumberId);
		gameInfo.setMapId(map.getId());
		gameInfo.setOpenTime(System.currentTimeMillis());
		
		languageOptions = new LanguageOptions(this);
	}
	
	static {
		if (symbols == null) {
			symbols = new ArrayList<String>();
			symbols.add("/");
			symbols.add("\"");
			symbols.add(".");
			symbols.add("`");
			symbols.add(";");
			symbols.add("~");
			symbols.add("!");
			symbols.add("@");
			symbols.add("#");
			symbols.add("$");
			symbols.add("%");
			symbols.add("^");
			symbols.add("&");
			symbols.add("*");
			symbols.add("(");
			symbols.add(")");
			symbols.add("_");
			symbols.add("-");
			symbols.add("+");
			symbols.add("=");
			symbols.add(" ");
			symbols.add("{");
			symbols.add("}");
			symbols.add("[");
			symbols.add("]");
			symbols.add("'");
			symbols.add("<");
			symbols.add(">");
			for (int i = 0; i < 10; i++) {
				symbols.add("" + i);
			}
		}
	}
	
	public boolean addPlayer(Player p) {
		if (check(p)) return false;
		if (!isJoinable()) return false;
		if (players.size() >= getMaxPlayers() && !p.hasPermission("buildsomething.game.joinfull")) {
			return false;
		}
		BPlayer bp = plugin.getPlayerManager().getPlayer(p.getUniqueId());
		if (bp == null) return false;
		players.add(bp);
		p.teleport(map.getNormal());
		
		p.setGameMode(GameMode.ADVENTURE);
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		
		if (players.size() >= MIN_PLAYERS_START) {
			countdown();
		}
		plugin.getScoreboardManager().updateGameScoreboardTime(this);
		p.setScoreboard(scoreboard.getScoreboard());
		
		if (map.isNightVision()) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, true, false));
		}
		
		return true;
	}
	
	public void countdown() {
		if (mode != Gamemode.LOBBY) return;
		if (task_id_lobby.getValue() != 0) return;
		setMode(Gamemode.LOBBY_COUNTDOWN);
		final IntWarpper create_scoreboard = new IntWarpper();
		create_scoreboard.setValue(0);
		task_id_lobby.setValue(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				if (!checkLessPlayersStart()) {
					return;
				}
				lobby_count_down.setValue(lobby_count_down.getValue() - 1);
				if (lobby_count_down.getValue() == 0) {
					Bukkit.getScheduler().cancelTask(task_id_lobby.getValue());
					start();
					return;
				}
				if (lobby_count_down.getValue() % 5 == 0 || lobby_count_down.getValue() <= 5 && lobby_count_down.getValue() != 0) {
					message("Game starting in " + Utils.timeString(lobby_count_down.getValue())/*lobby_count_down.i + " second" + (lobby_count_down.i == 1 ? "" : "s")*/);
				}
				
				if (create_scoreboard.getValue() == 0) {
					plugin.getScoreboardManager().updateGameScoreboard(Game.this);
					create_scoreboard.setValue(1);
				} else {
					plugin.getScoreboardManager().updateGameScoreboardTime(Game.this);
				}
				
				
			}
		}, 20L, 20L));
	}
	
	protected void gameCountdown() {

		if (mode != Gamemode.RUNNING) return;
		if (task_id_game.getValue() != 0) return;
		task_id_game.setValue(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				game_count_down.setValue(game_count_down.getValue() - 1);
				boolean display_message = false;
				if (game_count_down.getValue() % 30 == 0) {
					display_message = true;
				}
				if (game_count_down.getValue() < 60 && game_count_down.getValue() % 10 == 0) {
					display_message = true;
				}
				if (game_count_down.getValue() < 20 && game_count_down.getValue() % 5 == 0) {
					display_message = true;
				}
				if (game_count_down.getValue() <= 10) {
					display_message = true;
				}
				if (game_count_down.getValue() == 0) {
					Bukkit.getScheduler().cancelTask(task_id_game.getValue());
					display_message = false;
					stop(true, false);
					return;
				}
				
				plugin.getScoreboardManager().updateGameScoreboardTime(Game.this);
				
				if (display_message) {
					message("The game ends in " + Utils.timeString(game_count_down.getValue()));
				}
				
				onGameTick(game_count_down.getValue());
				
			}
		}, 20L, 20L));

	}
	
	protected void onGameTick(int time) {
		
	}
	
	protected boolean checkLessPlayersStart() {
		if (mode == Gamemode.LOBBY_COUNTDOWN) {
			if (!forceStart && players.size() < MIN_PLAYERS_START) {
				setMode(Gamemode.LOBBY);
				Bukkit.getScheduler().cancelTask(task_id_lobby.getValue());
				task_id_lobby.setValue(0);
				lobby_count_down.setValue(LOOBY_COUNT_DOWN_TIME + 1);
				message("Not enough players in the game to start, start cancelled.");
				return false;
			}
			return true;
		} else {
			return true;
		}
	}
	
	protected void start() {
		setMode(Gamemode.RUNNING);
		gameInfo.setDate(System.currentTimeMillis());
		rewards.clear();
		setScoreboard(plugin.getScoreboardManager().createGameScoreboard(this));
		
		for (Player p : getPlayersBukkit()) {
			createRewardsInfo(p);
			p.setScoreboard(scoreboard.getScoreboard());
			p.getInventory().clear();
			p.getInventory().setArmorContents(null);
		}
		
		for (Bonus bo : manager.getBonusesManager().getBonuses()) {
			bo.onGameStart(this);
		}
		
		manager.startIfNeeded(-1);
		
		gameCountdown();
	}
	
	protected void createRewardsInfo(Player p){
		RewardInfo ri = new RewardInfo();
		rewards.put(p.getUniqueId(), ri);
	}
	
	protected RewardInfo getRewardInfo(Player p) {
		return rewards.get(p.getUniqueId());
	}
	
	protected void rewardCoins(Player p, double coins, String s) {
		getRewardInfo(p).addCoins(coins);
		messagePlayer(p, "You earned " + coins + " coins: " + s);
	}
	
	protected boolean isSame(String m) {
		 if (word == null) return false;
		return isSame(word.getWordEnglish(), m) || isSame(word.getWordHebrew(), m) || isSame(new StringBuilder(word.getWordHebrew()).reverse().toString(), m) || isSame(new StringBuilder(word.getWordEnglish()).reverse().toString(), m);
	}
	
	private boolean isSame(String w, String m) {
		if (m.contains(w) || m.toUpperCase().contains(w.toUpperCase()) || w.equalsIgnoreCase(m)) {
			return true;
		}
		for (String s : symbols) {
			if (m.replace(s, "").contains(w.replace(s, "")) || 
					m.replace(s, "").toUpperCase().contains(w.replace(s, "").toUpperCase())
					|| w.replace(s, "").equalsIgnoreCase(m.replace(s, ""))) {
				return true;
			}
		}
		return false;

	}
	
	
	
	protected void giveRewards() {
		for (Player p : getPlayersBukkit()) {
			giveReward(p);
		}
	}
	
	private void giveReward(Player p) {
		RewardInfo ri = getRewardInfo(p);
		if (ri == null) {
			System.out.println("ri null: " + p.getName());
			return;
		}
		if (ri.getCoins() > 0) {
			plugin.getPlayerManager().getEconomy().depositPlayer(p.getUniqueId(), ri.getCoins());
			p.sendMessage(ChatColor.GREEN + "You earnd " + ri.getCoins() + " coins in this game!");
		}
	}
	
	public void addTime(int sec) {
		game_count_down.setValue(game_count_down.getValue() + sec);
	}
	
	public ProtectedRegion getRegion() {
		if (map == null || map.getWg_id() == null || map.getWg_id().isEmpty()) {
			return null;
		}
		return manager.getWorldGuard().getRegionManager(map.getBuilder().getWorld()).getRegion(map.getWg_id());
	}
	
	public void message(String s) {
		for (Player p : getPlayersBukkit()) {
			String msg = s;
			if (msg.contains("%hebword%") && word != null) {
				BPlayer bp = plugin.getPlayerManager().getPlayer(p);
				if (bp.isHebrewWords()) {
					msg = msg.replaceAll("%hebword%", plugin.hebrewMessage(p, word.getWordHebrew()));
				} else {
					msg = msg.replaceAll("%hebword%", "");
					msg = msg.replaceAll("()", "");
				}
			}
			p.sendMessage(ChatColor.GREEN + "[" + ChatColor.AQUA + "BuildSomething" + ChatColor.GREEN + "] " + ChatColor.BLUE + msg);
		}
	}
	
	public void messagePlayer(Player p, String s) {
		//p.sendMessage(s);
		p.sendMessage(ChatColor.GREEN + "[" + ChatColor.DARK_BLUE + "BuildSomething" + ChatColor.GREEN + "] " + ChatColor.BLUE + s);
	}
	
	public List<Player> getPlayersBukkit() {
		List<Player> pl = new ArrayList<Player>();
		for (BPlayer bp : players) {
			Player p = bp.getPlayer();
			if (p != null) {
				pl.add(p);
			}
		}
		return pl;
	}
	
	protected void afterEnd(boolean stat) {
		for (Player p : getPlayersBukkit()) {
			p.closeInventory();
			p.getInventory().clear();			
			
			if (map.isNightVision()) {
				p.removePotionEffect(PotionEffectType.NIGHT_VISION);
			}
			Utils.teleportSpawn(p);
			plugin.getScoreboardManager().createPlayerScoreboard(p);
		}
		scoreboard = null;
		players.clear();
		map.setUse(false);
		manager.gameEnded(this);

	}
	
	public void close() {
		if (mode == Gamemode.LOBBY) {
			afterEnd(false);
		} else if (mode == Gamemode.LOBBY_COUNTDOWN) {
			Bukkit.getScheduler().cancelTask(task_id_lobby.getValue());
			afterEnd(false);
		} else if (mode == Gamemode.RUNNING) {
			stop(true, true);
		}
		
	}
	
	public void stop(boolean force, boolean nodelay) {
		setMode(Gamemode.END);
		Bukkit.getScheduler().cancelTask(task_id_game.getValue());
		Bukkit.getScheduler().cancelTask(task_id_lobby.getValue());
		plugin.getScoreboardManager().updateGameScoreboardTime(this);
		   
		for (Player p : getPlayersBukkit()) {
			plugin.getActionBar().removeActionBar(p);
		}
		
		for (Item i : map.getBuilder().getWorld().getEntitiesByClass(Item.class)) {
			if (i != null) {
				if (!i.isDead() && i.isValid()) {
					if (i.getLocation().distance(map.getBuilder()) < 100) {
						i.remove();
					}
				}
			}
		}
		
		for (Bonus bo : manager.getBonusesManager().getBonuses()) {
			bo.onGameStop(this);
		}
		
		Set<Entry<UUID, RewardInfo>> on = rewards.entrySet();
		List<Entry<UUID, RewardInfo>> o = new ArrayList<Entry<UUID, RewardInfo>>();
		for (Entry<UUID, RewardInfo> oe : on) {
			if (oe.getValue() == null || oe.getValue().getTimeTook().longValue() == 0) {
				continue;
			}
			o.add(oe);
		}
		Collections.sort(o, new Comparator<Entry<UUID, RewardInfo>>() {

			@Override
			public int compare(Entry<UUID, RewardInfo> i, Entry<UUID, RewardInfo> n) {
				return n.getValue().getTimeTook().compareTo(i.getValue().getTimeTook());
			}
		});
		
		if (!o.isEmpty()) {
			message(ChatColor.GREEN + "Fastest players:");
			
			for (int i = 0; i < Math.min(5, o.size()); i++) {
				Entry<UUID, RewardInfo> p = o.get(i);
				message(ChatColor.YELLOW + "" + (i + 1) + ". " + Bukkit.getOfflinePlayer(p.getKey()).getName() + " in " + Utils.foramtTimeShort(p.getValue().getTimeTook().longValue()));
			}
		}
		
		
		giveRewards();
		
		if (word != null) {
			gameInfo.setWordId(word.getId());
		}
		gameInfo.setGameLength(System.currentTimeMillis() - gameInfo.getDate());
		gameInfo.setKnowCount(knows.size());
		
		System.out.println(gameInfo);
		int id = manager.saveGame(gameInfo);
		System.out.println(id);
		onGameSave(id);
		
		if (nodelay) {
			afterEnd(true);
		} else {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				
				@Override
				public void run() {
					afterEnd(true);
				}
			}, 5 * 20);
		}

	}
	
	protected void onGameSave(int id) {
		
	}
	
	@SuppressWarnings("deprecation")
	protected void clearMapWorldEdit() {
		try {
			World world = map.getBuilder().getWorld();
			EditSession es = new EditSession(BukkitUtil.getLocalWorld(world), Integer.MAX_VALUE);
			Polygonal2DRegion weRegion = new Polygonal2DRegion(BukkitUtil.getLocalWorld(world), getRegion().getPoints(), getRegion().getMinimumPoint().getBlockY(), getRegion().getMaximumPoint().getBlockY());
			es.setBlocks(weRegion, new BaseBlock(BlockID.AIR));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public void removePlayer(Player p) {
		if (!check(p)) return;
		BPlayer bp = plugin.getPlayerManager().getPlayer(p.getUniqueId());
		onPlayerRemoved(p, bp);
		
		players.remove(bp);
		rewards.remove(p.getUniqueId());
		chats.remove(p.getUniqueId());
		Utils.teleportSpawn(p);
		if (mode == Gamemode.RUNNING) {
			for (Bonus b : manager.getBonusesManager().getBonuses()) {
				b.onPlayerQuit(this, bp);
			}
			
			if (knows.contains(p.getUniqueId())) {
				knows.remove(p.getUniqueId());
			}
		}
		p.removePotionEffect(PotionEffectType.NIGHT_VISION);
		p.removePotionEffect(PotionEffectType.GLOWING);
		
		plugin.getScoreboardManager().createPlayerScoreboard(p);
		plugin.getScoreboardManager().updateGameScoreboard(this);
		
		if (!checkLessPlayersStart()) {
			return;
		}
		checkEnd();

	}
	
	protected void onPlayerRemoved(Player p, BPlayer bp) {
		
	}
	
	public boolean check(Player p) {
		for (BPlayer bp : players) {
			if (bp.getUUID().equals(p.getUniqueId()))
				return true;
		}
		return false;
	}
	
	public void onPlayerChat(final AsyncPlayerChatEvent e) {
		//TODO check if the game is running
		if (check(e.getPlayer())) {
			ChatInfo ci = chats.get(e.getPlayer().getUniqueId());
			if (ci != null) {
				if ((System.currentTimeMillis() - ci.firstMessage) < 10 * 1000) {
					if ((ci.messageCount + 1) > 3) {
						e.setCancelled(true);
						e.getPlayer().sendMessage(ChatColor.RED + "Please wait");
						return;
					} else {
						ci.messageCount++;
					}
				} else {
					ci.firstMessage = System.currentTimeMillis();
					ci.messageCount = 1;
				}
						
			} else {
				ci = new ChatInfo();
				ci.firstMessage = System.currentTimeMillis();
				ci.messageCount = 1;
				chats.put(e.getPlayer().getUniqueId(), ci);
			}
			ChatPlayerType sender = getChatPlayerType(e.getPlayer());
			if (isSame(e.getMessage()) && sender == ChatPlayerType.KNOW_BUILDER) {
				e.setCancelled(true);
				return;
			}
			
			if (word != null && word.isSame(e.getMessage())) {
				e.setCancelled(true);
				if (sender == ChatPlayerType.DONT_KNOW) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						
						@Override
						public void run() {
							knowTheWord(e.getPlayer()); //Prevent the knowTheWord (and sometime checkEnd and stop) methods to begin call async
							
						}
					});
				}
				return;
			}
			
			e.getRecipients().clear();
			for (Player p : getPlayersBukkit()) {
				ChatPlayerType reciver = getChatPlayerType(p);
				if (sender.canSendTo(reciver)) {
					e.getRecipients().add(p);
				}
			}
		}
	
	}
	
	public abstract ChatPlayerType getChatPlayerType(Player p);
		
	public abstract void onBlockBreak(BlockBreakEvent e);
	
	public abstract void onBlockPlace(BlockPlaceEvent e);
		
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		
	}
	
	protected void knowTheWord(Player p) {
		BPlayer bp = plugin.getPlayerManager().getPlayer(p.getUniqueId());
		getRewardInfo(p).setTimeTook(System.currentTimeMillis() - gameInfo.getDate());
		manager.getEffectsManager().getViewById(0).getEffect(bp).run(p.getEyeLocation(), p);
		knows.add(p.getUniqueId());
		message(p.getName() + " know the word!");
	}
	
	protected abstract void checkEnd();
	
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (check(e.getPlayer())) {
			removePlayer(e.getPlayer());
			//TODO check this
		}
	}
	
	public void onPlayerIneract(PlayerInteractEvent e) {
		if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.hasItem()) {
			Bonus b = manager.getBonusesManager().getByItem(e.getItem());
			if (b != null) {
				b.onUse(this, plugin.getPlayerManager().getPlayer(e.getPlayer()));
			}
		}
	}
		
	public boolean hasMetadata(String key) {
		return metadata.containsKey(key);
	}
	
	public String getMetadata(String key) {
		return metadata.get(key);
	}
	
	public void removeMetadata(String key) {
		metadata.remove(key);
	} 
	
	public void setMetadata(String key, String value) {
		if (hasMetadata(key)) {
			removeMetadata(key);
		}
		metadata.put(key, value);
	}
	
	public void onInventoryClick(InventoryClickEvent e) {
		
	}
	
	public boolean isJoinable() {
		return mode == Gamemode.LOBBY || mode == Gamemode.LOBBY_COUNTDOWN;
	}
	
	/**
	 * @return the mode
	 */
	public Gamemode getMode() {
		return mode;
	}
	/**
	 * @param mode the mode to set
	 */
	public void setMode(Gamemode mode) {
		this.mode = mode;
	}
	/**
	 * @return the players
	 */
	public List<BPlayer> getPlayers() {
		return players;
	}
	/**
	 * @param players the players to set
	 */
	public void setPlayers(List<BPlayer> players) {
		this.players = players;
	}
	/**
	 * @return the word
	 */
	public Word getWord() {
		return word;
	}
	/**
	 * @param word the word to set
	 */
	public void setWord(Word word) {
		this.word = word;
	}

	/**
	 * @return the force_start
	 */
	public boolean isForceStart() {
		return forceStart;
	}

	/**
	 * @param force_start the force_start to set
	 */
	public void setForceStart(boolean force_start) {
		this.forceStart = force_start;
	}

	/**
	 * @return the map
	 */
	public Map getMap() {
		return map;
	}

	/**
	 * @param map the map to set
	 */
	public void setMap(Map map) {
		this.map = map;
	}

	public List<UUID> getKnows() {
		return knows;
	}

	public void setKnows(List<UUID> knows) {
		this.knows = knows;
	}
	
	public IntWarpper getLobbyCountDown() {
		return lobby_count_down;
	}

	public IntWarpper getGameCountDown() {
		return game_count_down;
	}

	/**
	 * @return the gameId
	 */
	public UUID getGameId() {
		return gameId;
	}
	
	/**
	 * @return the gameId
	 */
	public UUID getId() {
		return gameId;
	}
	
	public int getNumberId() {
		return gameNumberId;
	}
	
	public SimpleScoreboard getScoreboard() {
		return scoreboard;
	}
	
	public void setScoreboard(SimpleScoreboard sib) {
		scoreboard = sib;
		for (Player p : getPlayersBukkit()) {
			p.setScoreboard(sib.getScoreboard());
		}
	}
	
	public int getMaxPlayers() {
		return maxPlayers;
	}
	
	public void setMaxPlayers(int m) {
		maxPlayers = m;
	}
	
	public abstract String getGameType();

	public LanguageOptions getLanguageOptions() {
		return languageOptions;
	}

	public void setLanguageOptions(LanguageOptions languageOptions) {
		this.languageOptions = languageOptions;
	}

}
class ChatInfo {
	long firstMessage;
	int messageCount;
}
enum ChatPlayerType{
	DONT_KNOW, KNOW_BUILDER, OUT_GAME;
	
	public boolean canSendTo(ChatPlayerType reciver) {
		if (reciver == this) {
			return true;
		}

		if (reciver == OUT_GAME) {
			return false;
		}

		if (this == DONT_KNOW && reciver == KNOW_BUILDER) {
			return true;
		}

		if (this == KNOW_BUILDER && reciver == DONT_KNOW) {
			return false;
		}

		return false;
	}
}