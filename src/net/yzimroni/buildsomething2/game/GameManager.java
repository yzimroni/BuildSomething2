package net.yzimroni.buildsomething2.game;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.blocks.BlockManager;
import net.yzimroni.buildsomething2.game.bonuses.BonusesManager;
import net.yzimroni.buildsomething2.game.effects.EffectsManager;
import net.yzimroni.buildsomething2.game.games.BotGame;
import net.yzimroni.buildsomething2.game.games.BuildersGame;
import net.yzimroni.buildsomething2.game.games.Game;
import net.yzimroni.buildsomething2.game.plots.PlotInfo;
import net.yzimroni.buildsomething2.game.plots.PlotManager;
import net.yzimroni.buildsomething2.player.BPlayer;
import net.yzimroni.buildsomething2.utils.IntWarpper;
import net.yzimroni.party.parties.Party;

public class GameManager implements Listener {

	private List<Map> maps = new ArrayList<Map>();
	private List<Game> games = new ArrayList<Game>();
	private List<IntWarpper> words = new ArrayList<IntWarpper>();
	private List<IntWarpper> last_words = new ArrayList<IntWarpper>();
	private List<UUID> last_builders = new ArrayList<UUID>();

	public WorldGuardPlugin worldguard = null;

	private PlotManager plotmanager;
	private BlockManager blockmanager;
	private BonusesManager bonusesmanager;
	private EffectsManager effectsmanager;

	private boolean openNewGames = true;
	private boolean remove = true;
	private int maxPlayers = 12;

	public BuildSomethingPlugin plugin;

	public GameManager(BuildSomethingPlugin p) {
		plugin = p;
		loadWords();
		
		worldguard = getWorldGuard();
		if (worldguard == null) {
			plugin.log.warning("WorldGuard not found!");
			Bukkit.getPluginManager().disablePlugin(plugin);
			return;
		}
		
		plotmanager = new PlotManager(plugin);
		blockmanager = new BlockManager(plugin);
		effectsmanager = new EffectsManager(plugin);
		bonusesmanager = new BonusesManager(plugin);
		
		openNewGames = plugin.getConfig().getBoolean("options.openNewGames", true);
		plugin.log.info("openNewGames: " + openNewGames);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				initMaps();
				startIfNeeded(-1);
			}
		}, 1 * 20);
	}
	
	public void startIfNeeded(int i) {
		if (!openNewGames) return;
		if (getLobbyGamesCount(true) < getMaxGameLimit()) {
			if (!hasOpenMaps()) {
				System.out.println("startIfNeeded(" + i + ") called but we dont have more open maps");
				return;
			}
			plugin.log.info("Starting a new game, i: " + i);
			Game g = createNewGame();
			if (g == null) {
				System.out.println("g is null");
				return;
			}
			if (i != -1) {
				games.add(i, g);
			} else {
				games.add(g);
				if (getLobbyGamesCount(true) < getMaxGameLimit()) {
					startIfNeeded(-1);
				}
			}
		} else {
			plugin.log.warning("startIfNeeded(" + i + ") called when we dont need more games");
		}
	}
	
	private boolean hasOpenMaps() {
		for (Map m : maps) {
			if (!m.isUse()) {
				return true;
			}
		}
		return false;
	}
	
	private Game createNewGame() {
		int builder1 = 0, builders = 0, bot = 0;
		for (Game g : games) {
			if (g.getMode() == Gamemode.LOBBY || g.getMode() == Gamemode.LOBBY_COUNTDOWN) {
				if (g instanceof BotGame) {
					bot++;
				} else if (g instanceof BuildersGame) {
					BuildersGame bs = (BuildersGame) g;
					if (bs.getBuildersCount() == 1) {
						builder1++;
					} else {
						builders++;
					}
				} else {
					System.out.println("Game is unknown type: " + g + " " + g != null ? g.getClass() : "(null)");
				}
			}
		}
		
		if (builder1 == 0) {
			return new BuildersGame(this, 1, maxPlayers);
		}
		if (builders == 0) {
			return new BuildersGame(this, 2, maxPlayers);
		}
		if (Bukkit.getOnlinePlayers().size() <= 5) {
			if (bot == 0) {
				return new BotGame(this, maxPlayers);
			}
		}
		System.out.println("we dont need more games 153");
		return null;
	}
	
	public int getLobbyGamesCount(boolean countdown) {
		int count = 0;
		for (Game g : games) {
			if (g.getMode() == Gamemode.LOBBY) {
				count++;
			} else if (countdown && g.getMode() == Gamemode.LOBBY_COUNTDOWN) {
				count++;
			}
		}
		return count;
	}
	
	public ItemStack getArrowItem(int page) {
		ItemStack i = new ItemStack(Material.ARROW);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName("Page " + page);
		i.setItemMeta(im);
		return i;
	}
		
	public void onDisable() {
		plugin.getConfig().set("options.openNewGames", openNewGames);
		plugin.saveConfig();
		remove = false;
		for (int i = 0; i<games.size(); i++) {
			Game g = games.get(i);
			if (g.getMode() == Gamemode.RUNNING) {
					g.stop(true, true);
			}
			List<BPlayer> temp = new ArrayList<BPlayer>();
			for (BPlayer b : g.getPlayers()) {
				temp.add(b);
			}
			for (BPlayer b : temp) {
				g.removePlayer(b.getPlayer());
			}
		}
	}
	
	private void initMaps() {
		//maps.add(new Map(1, "Forest", new Location(Bukkit.getWorld("Arena"), -36.5, 10, -7.5), new Location(Bukkit.getWorld("Arena"), -36, 5, -40), "map_forest_1", true, 100));
		maps.add(new Map(2, "Forest", new Location(Bukkit.getWorld("Game"), 0.5, 66, 34.5, 180, 0), new Location(Bukkit.getWorld("Game"), 0.5, 61, 0.5), "map_forest_2", true, 100));
		maps.add(new Map(3, "Forest", new Location(Bukkit.getWorld("Game"), 1000.5, 66, 34.5, 180, 0), new Location(Bukkit.getWorld("Game"), 1000.5, 61, 0.5), "map_forest_3", true, 100));
		maps.add(new Map(4, "Forest", new Location(Bukkit.getWorld("Game"), 0.5, 66, 1034.5, 180, 0), new Location(Bukkit.getWorld("Game"), 0.5, 61, 1000.5), "map_forest_4", true, 100));
		maps.add(new Map(5, "Forest", new Location(Bukkit.getWorld("Game"), 1000.5, 66, 1034.5, 180, 0), new Location(Bukkit.getWorld("Game"), 1000.5, 61, 1000.5), "map_forest_5", true, 100));
		
		maps.forEach(this::makeOutlineBorderBlocks);
	}
	
	private void makeOutlineBorderBlocks(Map m) {
		int y = getRegion(m).getMinimumPoint().getBlockY() - 1;
		int max_x = getRegion(m).getMaximumPoint().getBlockX() + 1;
		int mi_x = getRegion(m).getMinimumPoint().getBlockX() - 1;
		int max_z = getRegion(m).getMaximumPoint().getBlockZ() + 1;
		int mi_z = getRegion(m).getMinimumPoint().getBlockZ() - 1;
		List<Location> blocks = new ArrayList<Location>();
		for (int x = mi_x; x <= max_x; x++) {
			blocks.add(new Location(m.getBuilder().getWorld(), x, y, mi_z));
			blocks.add(new Location(m.getBuilder().getWorld(), x, y, max_z));
		}
		
		for (int z = mi_z; z <= max_z; z++) {
			blocks.add(new Location(m.getBuilder().getWorld(), mi_x, y, z));
			blocks.add(new Location(m.getBuilder().getWorld(), max_x, y, z));
		}
		
		m.setYbase(y);
		m.setOutlineBorderBlocks(blocks);
	}
	
	private ProtectedRegion getRegion(Map map) {
		return getWorldGuard().getRegionManager(map.getBuilder().getWorld()).getRegion(map.getWg_id());
	}
	
	private void loadWords() {
		words.clear();
		try {
			ResultSet rs = plugin.getDB().get("SELECT ID FROM words");
			while (rs.next()) {
				words.add(new IntWarpper(rs.getInt("ID")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		plugin.log.info("loaded " + words.size() + " words");
	}
	
	
	public Map randomMap() {
		for (int i = 0; i<100; i++) {
			Map r = maps.get(new Random().nextInt(maps.size()));
			if (r.isUse()) continue;
			return r;
		}
		return null;
	}
	
	public void randomBuilders(BuildersGame g) {
		g.getBuilders().clear();
		if (g.getBuildersCount() == 1) {
			g.getBuilders().addBuilder(randomOneBuilder(g.getPlayersBukkit()));
			addLastBuilders(g.getBuilders());
		} else if (g.getBuildersCount() >= 2) {
			List<Player> nonparty = new ArrayList<Player>();
			for (Player p : g.getPlayersBukkit()) {
				if (!plugin.getPlayerManager().getPartyManager().isParty(p) || plugin.getPlayerManager().getPartyManager().getParty(p).size() == 1) {
					nonparty.add(p);
				}
			}
			for (int num = 0; num < 5; num++) {
				g.getBuilders().clear();
				Player b = randomOneBuilder(g.getPlayersBukkit());
				g.getBuilders().addBuilder(b);
				Party p = plugin.getPlayerManager().getPartyManager().getParty(b);
				if (p != null && p.size() >= 2) {
					if (p.size() >= g.getBuildersCount()) {
						for (int i = 0; i < g.getBuildersCount() * 5; i++) {
							Player pt = p.getPlayers().get(new Random().nextInt(p.getPlayers().size()));
							if (g.getBuilders().isBuilder(b)) {
								continue; //The same player...
							}
							g.getBuilders().addBuilder(pt);
							if (g.getBuilders().size() >= g.getBuildersCount()) {
								addLastBuilders(g.getBuilders());
								return;
							}
						}
					} else {
						continue;
					}
				} else {
					if (nonparty.size() >= g.getBuildersCount()) {
						for (int i = 0; i < g.getBuildersCount() * 5; i++) {
							Player pt = randomOneBuilder(nonparty);
							if (g.getBuilders().isBuilder(b)) {
								continue; //The same player...
							}
							g.getBuilders().addBuilder(pt);
							if (g.getBuilders().size() >= g.getBuildersCount()) {
								addLastBuilders(g.getBuilders());
								return;
							}
						}
					} else {
						continue;
					}
				}
			}
			
		}
	}
	
	private void addLastBuilders(Builders builders) {
		for (Player p : builders.getPlayers()) {
			if (!last_builders.contains(p.getUniqueId())) {
				last_builders.add(p.getUniqueId());
				if (last_builders.size() > 5) {
					last_builders.remove(0);
				}
			}
		}
	}
	
	private Player randomOneBuilder(List<Player> players) {
		for (int i = 0; i<50; i++) {
			Player b = players.get(new Random().nextInt(players.size()));
			if (last_builders.contains(b.getUniqueId())) continue;
			return b;
		}
		Player b = players.get(new Random().nextInt(players.size()));
		return b;
	}
	
	public BotPlot randomBotPlot(List<Player> players) {
		try {
			ResultSet rs = plugin.getDB().get("SELECT * FROM games WHERE plot_id IS NOT NULL AND plot_type='1' ORDER BY RAND() DESC LIMIT 1"); // TODO RAND() has some performance issues
			if (rs.first()) {
				List<UUID> builders = new ArrayList<UUID>();
				int game_id = rs.getInt("ID");
				PreparedStatement pre = plugin.getDB().getPrepare("SELECT UUID FROM game_players WHERE game_id=? AND player_type='1'");
				pre.setInt(1, game_id);
				ResultSet bu = pre.executeQuery();
				while (bu.next()) {
					builders.add(UUID.fromString(bu.getString("UUID")));
				}
				bu.close();
				pre.close();
				BotPlot b = new BotPlot(game_id, rs.getInt("word_id"), rs.getString("plot_id"), builders, true);
				return b;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Word randomWord() {
		for (int i = 0; i<words.size(); i++) {
			IntWarpper id = words.get(new Random().nextInt(words.size()));
			if (last_words.contains(id)) continue;
			last_words.add(id);
			if (last_words.size() > 30) {
				last_words.remove(0);
			}
			return getWord(id.getValue());
		}
		if (last_words.size() > 30) {
			last_words.remove(0);
		}
		IntWarpper id = words.get(new Random().nextInt(words.size()));
		if (!last_words.contains(id)) {
			last_words.add(id);
		}
		return getWord(id.getValue());
	}
	
	public Word getWord(int id) {
		try {
			PreparedStatement pre = plugin.getDB().getPrepare("SELECT * FROM words WHERE ID=?");
			pre.setInt(1, id);
			ResultSet rs = pre.executeQuery();
			if (rs.first()) {
				Word w = new Word(rs.getInt("ID"), rs.getString("word_english"), rs.getString("word_hebrew"));
				return w;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Game> getGames() {
		return games;
	}
	
	public Game getGameById(UUID id) {
		for (Game g : games) {
			if (g.getGameId().equals(id)) {
				return g;
			}
		}
		return null;
	}
	
	public boolean joinGame(Game g, Player p) {
		Party party = plugin.getPlayerManager().getPartyManager().getParty(p);
		if (party != null && party.size() > 1) {
			if (party.getOwner().equals(p.getUniqueId())) {
				return joinParty(g, party);
			} else {
				p.sendMessage("You are in a party, only the party owner can join into a game");
				return false;
			}
		}
		for (Game gm : games) {
			if (gm.check(p)) {
				return false;
			}
		}
		return g.addPlayer(p);
	}
	
	public boolean joinParty(Game g, Party p) {
		if (!g.isJoinable()) {
			return false;
		}
		for (Player player : p.getPlayers()) {
			for (Game ga : games) {
				if (ga.check(player)) {
					ga.removePlayer(player);
					break;
				}
			}
			g.addPlayer(player);
		}
		p.message("The party owner joined a game");
		return true;
		
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		for (Game g : games) {
			if (g.check(e.getPlayer())) {
				g.onPlayerQuit(e);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		boolean ingame = false;
		for (Game g : games) {
			if (g.check(e.getPlayer())) {
				ingame = true;
				g.onPlayerChat(e);
			}
		}
		if (!ingame) {
			e.getRecipients().clear();
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (!checkPlayer(p)) {
					e.getRecipients().add(p);
				}
			}
		}
	}
	
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		boolean ingame = false;
		for (Game g : games) {
			if (g.check(e.getPlayer())) {
				ingame = true;
				g.onBlockBreak(e);
			}
		}
		//TODO make worldguard handle this
		if (!ingame) {
			if (!e.getPlayer().isOp()) {
				e.setCancelled(true);
			}
		}
	}
		
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		boolean ingame = false;
		for (Game g : games) {
			if (g.check(e.getPlayer())) {
				ingame = true;
				g.onBlockPlace(e);
			}
		}
		//TODO make worldguard handle this
		if (!ingame) {
			if (!e.getPlayer().isOp()) {
				e.setCancelled(true);
				e.setBuild(false);
			}
		}
	}
	
	@EventHandler
	public void onPlayerIneract(PlayerInteractEvent e) {
		for (Game g : games) {
			if (g.check(e.getPlayer())) {
				g.onPlayerIneract(e);
			}
		}
	}
	
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			for (Game g : games) {
				if (g.check(p)) {
					g.onInventoryClick(e);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		for (Game g : games) {
			if (g.check(e.getPlayer())) {
				g.onPlayerCommand(e);
			}
		}
	}
		
	public boolean checkPlayer(Player p) {
		for (Game g : games) {
			if (g.check(p)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isBuilder(Player p) {
		for (Game g : games) {
			if (g instanceof BuildersGame) {
				if (((BuildersGame) g).isBuilder(p)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public WorldGuardPlugin getWorldGuard() {
		Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin("WorldGuard");

		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null;
		}

		return (WorldGuardPlugin) plugin;
	}
			
	
	public void gameEnded(Game g) {
		//TO DO
		if (remove) {
			int index = games.indexOf(g);
			games.remove(index);
			startIfNeeded(index);
		}
	}
	
	public int saveGame(GameInfo info) {
		int id = -1;
		try {
			PreparedStatement game = plugin.getDB().getPrepareAutoKeys("INSERT INTO games (game_id,map_id,word_id,plot_id,players_count,game_type,date,know_count,plot_type,game_length,openTime) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
			game.setInt(1, info.getGameId());
			game.setInt(2, info.getMapId());
			game.setInt(3, info.getWordId());
			game.setString(4, info.getPlotId());
			game.setInt(5, info.getPlayersCount());
			game.setInt(6, info.getGameType() != null ? info.getGameType().getId() : -1);
			game.setLong(7, info.getDate());
			game.setInt(8, info.getKnowCount());
			game.setInt(9, info.getPlotType() != null ? info.getPlotType().getId() : -1);
			game.setLong(10, info.getGameLength());
			game.setLong(11, info.getOpenTime());
			game.executeUpdate();
			
			id = plugin.getDB().getIdFromPrepared(game);
			game.close();
			System.out.println(game.toString());
			PreparedStatement players = plugin.getDB().getPrepare("INSERT INTO game_players (game_id,UUID,player_type,npc_id,know_time) VALUES (?,?,?,?,?)");
			for (PlayerInfo p : info.getPlayers()) {
				players.clearParameters();
				players.setInt(1, id);
				players.setString(2, p.getPlayer().toString());
				players.setInt(3, p.getPlayerType());
				players.setInt(4, p.getNpcId());
				players.setLong(5, p.getKnowTime());
				players.executeUpdate();
			}
			players.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}
	
	public int sendReport(Player reporter, String reason, Game g, PlotInfo plot) {
		try {
			PreparedStatement report = plugin.getDB().getPrepareAutoKeys("INSERT INTO reports (reporterUUID,gameId,reason,plotId,date) VALUES(?,?,?,?,?)");
			report.setString(1, reporter.getUniqueId().toString());
			report.setInt(2, g.getNumberId());
			report.setString(3, reason);
			report.setString(4, plot.getPlotId());
			report.setLong(5, System.currentTimeMillis());
			report.executeUpdate();
			
			return plugin.getDB().getIdFromPrepared(report);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public void closeAll() {
		remove = false;
		for (Game g : games) {
			g.close();
		}
		games.clear();
		remove = true;
		startIfNeeded(-1);
	}
	
	private int getMaxGameLimit() {
		return 3;
	}

	/**
	 * @return the plotsmanager
	 */
	public PlotManager getPlotManager() {
		return plotmanager;
	}

	/**
	 * @param plotsmanager the plotsmanager to set
	 */
	public void setPlotsManager(PlotManager plotsmanager) {
		this.plotmanager = plotsmanager;
	}

	public BlockManager getBlockManager() {
		return blockmanager;
	}

	public void setBlockmanager(BlockManager blockmanager) {
		this.blockmanager = blockmanager;
	}

	/**
	 * @return the bonusesmanager
	 */
	public BonusesManager getBonusesManager() {
		return bonusesmanager;
	}

	/**
	 * @param bonusesmanager the bonusesmanager to set
	 */
	public void setBonusesManager(BonusesManager bonusesmanager) {
		this.bonusesmanager = bonusesmanager;
	}

	/**
	 * @return the effectsmanager
	 */
	public EffectsManager getEffectsManager() {
		return effectsmanager;
	}

	/**
	 * @param effectsmanager the effectsmanager to set
	 */
	public void setEffectsManager(EffectsManager effectsmanager) {
		this.effectsmanager = effectsmanager;
	}

	/**
	 * @return the startNewGames
	 */
	public boolean isOpenNewGames() {
		return openNewGames;
	}

	/**
	 * @param startNewGames the startNewGames to set
	 */
	public void setOpenNewGames(boolean startNewGames) {
		this.openNewGames = startNewGames;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
		games.forEach(game -> game.setMaxPlayers(maxPlayers));
	}
	
}
