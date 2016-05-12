package net.yzimroni.buildsomething2.game.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import net.yzimroni.buildsomething2.game.Builders;
import net.yzimroni.buildsomething2.game.GameInfo.GameType;
import net.yzimroni.buildsomething2.game.GameInfo.PlotType;
import net.yzimroni.buildsomething2.game.GameManager;
import net.yzimroni.buildsomething2.game.Gamemode;
import net.yzimroni.buildsomething2.game.PlayerInfo;
import net.yzimroni.buildsomething2.game.blocks.BSBlock;
import net.yzimroni.buildsomething2.game.bonuses.bonuses.Bonus;
import net.yzimroni.buildsomething2.game.bonuses.bonuses.worldedit.WorldEditBonus;
import net.yzimroni.buildsomething2.game.effects.effects.Effect;
import net.yzimroni.buildsomething2.game.plots.PlotInfo;
import net.yzimroni.buildsomething2.game.plots.PlotManager;
import net.yzimroni.buildsomething2.player.BPlayer;
import net.yzimroni.buildsomething2.player.economy.RewardInfo;
import net.yzimroni.buildsomething2.utils.JsonBuilder;
import net.yzimroni.buildsomething2.utils.JsonBuilder.ClickAction;
import net.yzimroni.buildsomething2.utils.Utils;
import net.yzimroni.buildsomething2.utils.WorldEditClipboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.domains.DefaultDomain;
public class BuildersGame extends Game {
	
	private int buildersCount;
	private Builders builders;
	
	private PlotInfo plotInfo;
	
	private long lastReport = -1;
	
	public BuildersGame(GameManager gm, int buildersCount, int maxPlayers) {
		super(gm, maxPlayers);
		
		builders = new Builders(plugin);
		this.buildersCount = buildersCount;
		
	}
	
	@Override
	protected void start() {
		if (!checkLessPlayersStart()) {
			return;
		}
		if (mode != Gamemode.LOBBY_COUNTDOWN) return;
		setMode(Gamemode.RUNNING);
		if (builders.isEmpty()) {
			manager.randomBuilders(this);
		}
		if (builders.size() == 1) {
			message(builders + " is the builder!");
		} else {
			message(builders + " are the builders!");
		}
		
		DefaultDomain b = new DefaultDomain();
		for (Player builder : builders.getPlayers()) {
			b.addPlayer(builder.getUniqueId());
		}
		getRegion().setMembers(b);
		word = manager.randomWord();
		
		
		for (Player p : getPlayersBukkit()) {
			if (builders.isBuilder(p)) {
				manager.getBonusesManager().getWorldeditManager().initWorldEditPlayer(p, this, plugin.getGameManager().getBlockManager().getBlocks(plugin.getPlayerManager().getPlayer(p)));
				p.setGameMode(GameMode.SURVIVAL);
				p.teleport(map.getBuilder());
				p.setAllowFlight(true);
				p.setFlying(true);
				sendPlotBorderOutline(p);
				p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, true, false));
			} else {
				Utils.sendTitleSub(p, ChatColor.BLUE + "The game started!", ChatColor.AQUA + "The builder is " + ChatColor.GREEN + builders, (int) (0.5 * 20), 4 * 20, (int) (0.5 * 20));
				p.setGameMode(GameMode.ADVENTURE);
				p.teleport(map.getNormal());
			}
			
		}
		sendWordInfo();
		
		super.start();
		
		gameInfo.setGameType(builders.size() == 1 ? GameType.SINGLE_BUILDER_GAME : GameType.MULTI_BUILDERS_GAME);
		
		giveItems();
		checkWorldEditCommands();
	}
	
	@SuppressWarnings("deprecation")
	protected void sendPlotBorderOutline(Player p) {
		for (Location l : map.getOutlineBorderBlocks()) {
			p.sendBlockChange(l, Material.WOOL, (byte) 14);
		}
	}
	
	private void checkWorldEditCommands() {
		List<WorldEditBonus> bonuses = new ArrayList<WorldEditBonus>();
		for (Bonus b : plugin.getGameManager().getBonusesManager().getBonuses()) {
			if (b instanceof WorldEditBonus) {
				bonuses.add((WorldEditBonus) b);
			}
		}
		for (BPlayer bp : builders.getBPlayers()) {
			Player p = bp.getPlayer();
			List<String> commands = new ArrayList<String>();
			for (WorldEditBonus w : bonuses) {
				if (w.has(bp)) {
					for (String command : w.getCommands().keySet()) {
						commands.add("/" + command.toLowerCase());
					}
				}
			}
			if (!commands.isEmpty()) {
				Collections.sort(commands, String.CASE_INSENSITIVE_ORDER);
				p.getInventory().addItem(new ItemStack(Material.WOOD_AXE));
				String cmds = "";
				for (String cmd : commands) {
					if (!cmds.isEmpty()) {
						cmds += ", ";
					}
					cmds += cmd;
				}
				messagePlayer(p, "Your worldedit commands: " + cmds);
				messagePlayer(p, "For more info about worldedit commands: http://wiki.sk89q.com/wiki/WorldEdit/Reference");
			}
		}
	}
	
	public void sendWordInfo() {
		for (Player builder : builders.getPlayers()) {
			BPlayer bp = plugin.getPlayerManager().getPlayer(builder);
			if (builders.size() == 1) {
				Utils.sendTitleSub(builder, ChatColor.BLUE + "You are the builder!", ChatColor.AQUA + "The word is " + ChatColor.GREEN + word.getWordEnglish() + (bp.isHebrewWords() ? " (" + plugin.hebrewMessage(builder, word.getWordHebrew()) + ")" : ""), (int) (0.5 * 20), 4 * 20, (int) (0.5 * 20));
			} else {
				if (builders.size() == 2) {
					String other_name = "";
					for (Player t : builders.getPlayers()) {
						if (!t.getUniqueId().equals(builder.getUniqueId())) {
							other_name = t.getName();
						}
					}
					Utils.sendTitleSub(builder, ChatColor.BLUE + "You and " + other_name + " are the builders!", ChatColor.AQUA + "The word is " + ChatColor.GREEN + word.getWordEnglish() + (bp.isHebrewWords() ? " (" + plugin.hebrewMessage(builder, word.getWordHebrew()) + ")" : ""), (int) (0.5 * 20), 4 * 20, (int) (0.5 * 20));
				} else {
					/*String result = "";
					String last_name = "";
					for (UUID u : builders.getUUIDs()) {
						if (!u.equals(builder.getUniqueId())) {
							String name = Bukkit.getOfflinePlayer(u).getName();
							if (!last_name.isEmpty()) {
								if (!result.isEmpty()) {
									result += ", ";
								}
								result += last_name;
							}
							last_name = name;
						}
					}
					if (!last_name.isEmpty()) {
						if (!result.isEmpty()) {
							result += " and ";
						}
						result += last_name;
					}*/
					
					String result = Utils.formatPlayerList(builders.getUUIDs(), builder.getUniqueId()); //TODO check
					
					Utils.sendTitleSub(builder, ChatColor.BLUE + "You, " + result + " are the builders!", ChatColor.AQUA + "The word is " + ChatColor.GREEN + word.getWordEnglish() + (bp.isHebrewWords() ? " (" + plugin.hebrewMessage(builder, word.getWordHebrew()) + ")" : ""), (int) (0.5 * 20), 4 * 20, (int) (0.5 * 20));
				}
			}
			plugin.getActionBar().sendActionBar(builder, ChatColor.GREEN + word.getWordEnglish() + (bp.isHebrewWords() ? " (" + plugin.hebrewMessage(builder, word.getWordHebrew()) + ")" : ""));
			messagePlayer(builder, "The word is " + word.getWordEnglish() + (bp.isHebrewWords() ? " (" + plugin.hebrewMessage(builder.getPlayer(), word.getWordHebrew()) + ")" : ""));
		}
	}
	
		
	private void giveItems() {
		if (!builders.isEmpty()) {
			for (BPlayer builder : builders.getBPlayers()) {
				HashMap<Integer, BSBlock> hotbar = manager.getBlockManager().deserializeHotbar(builder.getData().getHotbarItems());
				boolean give_default = false;
				if (hotbar == null || hotbar.isEmpty()) {
					give_default = true;
				} else {
					int give = 0;
					for (Entry<Integer, BSBlock> i : hotbar.entrySet()) {
						if (manager.getBlockManager().hasBlockAndFree(builder, i.getValue())) {
							builder.getPlayer().getInventory().setItem(i.getKey(), i.getValue().toItemStack());
							give++;
						}
					}
					if (give == 0) {
						give_default = true;
					}
				}
				if (give_default) {
					int hotbar_slots = 9;
					List<BSBlock> blocks = plugin.getGameManager().getBlockManager().getBlocks(builder);
					for (int i = 0; i < Math.min(hotbar_slots, blocks.size()); i++) {
						builder.getPlayer().getInventory().addItem(blocks.get(i).toItemStack());
					}
				}
				
				builder.getPlayer().getInventory().setItem(17, getChestItem());
			}
		}
	}
	
	@Override
	protected void onGameTick(int time) {
		if (time % 15 == 0) {
			for (Player p : builders.getPlayers()) {
				sendPlotBorderOutline(p);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void stop(boolean force, boolean nodelay) {
		if (!plugin.isEnabled()) nodelay = true;
		if (word != null) {
			message("The word was " + word.getWordEnglish() + " (%hebword%)");
		}
		if (!builders.isEmpty()) {
			for (Player b : builders.getPlayers()) {
				b.setGameMode(GameMode.ADVENTURE);
				b.setFlying(false);
				b.setAllowFlight(false);
				b.getInventory().clear();
				b.removePotionEffect(PotionEffectType.GLOWING);
				manager.getBonusesManager().getWorldeditManager().removeMask(b);
			}
		}
		if (!builders.isEmpty() && knows.size() > 0 && knows.size() >= (players.size() - builders.size())) {
			for (BPlayer b : builders.getBPlayers()) {
				rewardCoins(b.getPlayer(), 7, "All the players know the word");
				b.getData().setAllKnow(b.getData().getAllKnow() + 1);
			}
		}
		
		DefaultDomain bd = new DefaultDomain();
		getRegion().setMembers(bd);
		message("The game " + (force ? "stopped" : "ended"));
		if (word != null) { //TODO check if this is working
			createBuildPlot();
		}
		clearMapWorldEdit();
		
		gameInfo.setPlotType(PlotType.NORMAL_PLOT);
		
		
		if (!builders.isEmpty()) {
			for (Player b : builders.getPlayers()) {
				for (Location l : map.getOutlineBorderBlocks()) {
					b.sendBlockChange(l, l.getBlock().getType(), l.getBlock().getData());
				}
				Effect e = manager.getEffectsManager().getViewById(1).getEffect(plugin.getPlayerManager().getPlayer(b));
				if (e.getMultiTimesRun() == 1) {
					e.run(map.getBuilder().clone(), b);
				} else {
					for (int i = 0; i < e.getMultiTimesRun(); i++) {
						int x = new Random().nextInt(40) - 20;
						int y = new Random().nextInt(40) - 20;
						int z = new Random().nextInt(40) - 20;
						Location l = map.getBuilder().clone();
						l.setY(l.getY() + 20);
						l = l.add(x, y, z);
						e.run(l, b);
					}
				}
			}
		}		
		if (plotInfo != null) {
			gameInfo.setPlotId(plotInfo.getPlotId());
			
			for (Player p : getPlayersBukkit()) {
				RewardInfo r = getRewardInfo(p);
				int npcId = plotInfo.getBuilders().containsKey(p.getUniqueId()) ? plotInfo.getBuilders().get(p.getUniqueId()) : -1;
				long knowTime = r == null ? -1 : r.getTimeTook();
				if (knowTime == 0) {
					knowTime = -1;
				}
				
				gameInfo.addPlayer(new PlayerInfo(p.getUniqueId(), builders.isBuilder(p) ? 1 : 0, npcId, knowTime));
			}
		}
		
		
		/*
		 * TO DO
		 * copy the plot to the plot world VV
		 * clear the area VV
		 * remove items frames and drops VV	 - we dont need to do that, players cant place items and cant place itemframes
		 * set the name tags VV
		 * effects VV	
		 */
		
		super.stop(force, nodelay);
	}
	
	@Override
	protected void onGameSave(int pid) {
		plugin.getCommandManager().addPlotId(pid, plotInfo.getPlotId());
		JsonBuilder jb = new JsonBuilder().withText("Click Here to teleport to the plot of this game").withColor(ChatColor.GREEN).withClickEvent(ClickAction.RUN_COMMAND, "/plottp " + pid);
		
		for (Player p : getPlayersBukkit()) {
			messagePlayer(p, "This game plot id is " + pid);
			messagePlayer(p, "Use '/plottp " + pid + "' to teleport to the plot of this game");
			jb.sendJson(p);
		}
	}
		
	@Override
	protected void afterEnd(boolean stat) {
		for (Player p : getPlayersBukkit()) {
			if (stat) {
				BPlayer bp = plugin.getPlayerManager().getPlayer(p.getUniqueId());
				
				if (isBuilder(p)) {
					bp.getData().setBuilder(bp.getData().getBuilder() + 1);
				} else {
					bp.getData().setNormalPlayer(bp.getData().getNormalPlayer() + 1);
				}
				
				bp.getData().setTotalGames(bp.getData().getTotalGames() + 1);
			}
		}
		super.afterEnd(stat);
	}
	
	private void createBuildPlot() {
		if (word != null && !builders.isEmpty()) {
			WorldEditClipboard cb = createWorldEditClipboard();
			plotInfo = manager.getPlotManager().createGameBuildPlot(this, builders, cb, PlotManager.CPlotType.NORMAL);
		}
	}
	
	protected void createReport(Player reporter, String reason) {
		if (word != null && !builders.isEmpty()) {
			WorldEditClipboard cb = createWorldEditClipboard();
			PlotInfo reportInfo = manager.getPlotManager().createGameBuildPlot(this, builders, cb, PlotManager.CPlotType.REPORT);
			//TODO send the report data to the db
		}
	}
	
	@Override
	protected void onPlayerRemoved(Player p, BPlayer bp) {
		if (mode == Gamemode.RUNNING) {
			if (isBuilder(p)) {
				plugin.log.warning("The builder " + p.getName() + " removed while in-game");
				builders.removeBuilder(p);
				if (builders.isEmpty()) {
					stop(true, false);
				}
			}
		}
	}
	
	public ChatPlayerType getChatPlayerType(Player p) {
		if (check(p)) {
			if (isBuilder(p) || knows.contains(p.getUniqueId())) {
				return ChatPlayerType.KNOW_BUILDER;
			} else {
				return ChatPlayerType.DONT_KNOW;
			}
		} else {
			return ChatPlayerType.OUT_GAME;
		}
	}
	
	@Override
	public void onBlockBreak(BlockBreakEvent e) {
		if (check(e.getPlayer())) {
			if (isBuilder(e.getPlayer())) {
				if (!getRegion().contains(BukkitUtil.toVector(e.getBlock()))) {
					e.setCancelled(true);
					return;
				}
			} else if (!e.getPlayer().isOp()) {
				e.setCancelled(true);
				return;
			}
		}
	}
	
	@Override
	public void onBlockPlace(BlockPlaceEvent e) {
		if (check(e.getPlayer())) {
			if (isBuilder(e.getPlayer())) {
				if (!getRegion().contains(BukkitUtil.toVector(e.getBlock()))) {
					e.setCancelled(true);
					e.setBuild(false);
					return;
				} else {
					//e.getPlayer().getItemInHand().setAmount(64);
					e.getItemInHand().setAmount(64); //TODO check if this is acully changes the amount
				}
			} else if (!e.getPlayer().isOp()) {
				e.setCancelled(true);
				e.setBuild(false);
				return;
			}
		}
	}
	
	public void changeWord() {
		if (mode == Gamemode.RUNNING) {
			word = manager.randomWord();
		}
	}
	
	@Override
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		if (check(e.getPlayer())) {
			if (isBuilder(e.getPlayer())) {
				BPlayer builder = plugin.getPlayerManager().getPlayer(e.getPlayer());
				if (e.getMessage().toLowerCase().trim().equalsIgnoreCase("/clear") || e.getMessage().toLowerCase().trim().startsWith("/clear ")) {
					e.setCancelled(true);
					int items = 0;
					for (int i = 0; i < e.getPlayer().getInventory().getSize(); i++) {
						ItemStack is = e.getPlayer().getInventory().getItem(i);
						if (is == null || is.getType() == null || is.getType() == Material.AIR || is.getAmount() == 0) {
							continue;
						}
						@SuppressWarnings("deprecation")
						BSBlock b = manager.getBlockManager().getByType(is.getType(), is.getData().getData());
						if (b != null || is.getType() == Material.WOOD_AXE) {
							items += is.getAmount();
							e.getPlayer().getInventory().setItem(i, new ItemStack(Material.AIR));
						}
						
					}
					e.getPlayer().sendMessage("Cleared the inventory of " + e.getPlayer().getDisplayName() + ", removing " + items + " items");
				} else if (e.getMessage().trim().equalsIgnoreCase("/savehotbar") || e.getMessage().toLowerCase().trim().startsWith("/savehotbar "))  {
					e.setCancelled(true);
					HashMap<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();		
					for (int i = 0; i<9; i++) {
						items.put(i, e.getPlayer().getInventory().getItem(i));
					}
					String hotbar = manager.getBlockManager().serializeHotbar(items);
					builder.getData().setHotbarItems(hotbar);
					messagePlayer(e.getPlayer(), "You saved your hotbar!");
				}
			}
		}
	}
	
	@Override
	protected void knowTheWord(Player p) {
		BPlayer bp = plugin.getPlayerManager().getPlayer(p.getUniqueId());
		bp.getData().setKnow(bp.getData().getKnow() + 1);
		if (knows.size() == 0) {
			rewardCoins(p, 10, "Know the word first");
			bp.getData().setKnowFirst(bp.getData().getKnowFirst() + 1);
		} else {
			rewardCoins(p, 7, "Know the word");
		}
		super.knowTheWord(p);
		for (Player builder : builders.getPlayers()) {
			//TODO
			rewardCoins(builder, 3, "A player know the word");
		}
		checkEnd();
	}
	
	@Override
	protected void checkEnd() {
		if (mode == Gamemode.RUNNING) {
			if (knows.size() >= (players.size() - builders.size())) {
				stop(false, false);
			}
		}
	}
	
	@Override
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (check(e.getPlayer())) {
			if (isBuilder(e.getPlayer())) {
				plugin.log.warning("The builder " + e.getPlayer().getName() + " Disconnect while in-game");
				builders.removeBuilder(e.getPlayer());
				if (builders.isEmpty()) {
					stop(true, false);
				}
			}
			super.onPlayerQuit(e);
		}
	}
	
	@Override
	public void onPlayerIneract(final PlayerInteractEvent e) {
		if (e.getHand() != EquipmentSlot.HAND) {
			return; //TODO
		}
		if (check(e.getPlayer())) {
			if (e.getPlayer().getGameMode() == GameMode.SURVIVAL && isBuilder(e.getPlayer())) {
				if (e.hasBlock()) {
					if (map.getOutlineBorderBlocks().contains(e.getClickedBlock().getLocation())) {
						e.setCancelled(true);
						sendBlockUpdateWool(e.getPlayer(), e.getClickedBlock().getLocation());
						return;
					}
					if (map.getOutlineBorderBlocks().contains(e.getClickedBlock().getLocation().add(0, -1, 0).getBlock())) {
						e.setCancelled(true);
						sendBlockUpdateWool(e.getPlayer(), e.getClickedBlock().getLocation().add(0, -1, 0));
						return;
					}
				}
				if (e.hasBlock() && e.getAction() == Action.LEFT_CLICK_BLOCK) {
					if (getRegion().contains(BukkitUtil.toVector(e.getClickedBlock()))) {
						if (e.hasItem() && e.getItem() != null && e.getItem().getType() == Material.WOOD_AXE) {
							return;
						}
						e.getClickedBlock().setType(Material.AIR);
						return;
					}
				} else if (e.hasBlock() && e.getAction() == Action.RIGHT_CLICK_BLOCK && (e.getItem() == null || e.getItem().getType() == null || e.getItem().getType() == Material.AIR || e.getItem().getAmount() == 0)) {
					System.out.println("yes");
					@SuppressWarnings("deprecation")
					BSBlock b = manager.getBlockManager().getByType(e.getClickedBlock().getType(), e.getClickedBlock().getData());
					if (b != null) {
						if (manager.getBlockManager().hasBlockAndFree(plugin.getPlayerManager().getPlayer(e.getPlayer()), b)) {
							ItemStack i = b.toItemStack();
							i.setAmount(64);
							e.getPlayer().setItemInHand(i);
						}
					}
				}
			}
			super.onPlayerIneract(e);
		}
	}
	
	private void sendBlockUpdateWool(final Player p, final Location l) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				p.sendBlockChange(l, Material.WOOL, (byte) 14);
			}
		}, 5);
	}
	
	private void openChest(int page, Player p) {
		BPlayer builder = plugin.getPlayerManager().getPlayer(p);
		List<BSBlock> blocks = plugin.getGameManager().getBlockManager().getBlocks(builder);
		Inventory in = Bukkit.createInventory(p, 6 * 9, "More Blocks - Page " + page);
		int bpp = 45; //Blocks per page
		int min = ((page - 1) * bpp);
		int max = Math.min((page * bpp), blocks.size());
		if (blocks.size() <= min) return;
		for (int i = min; i<max; i++) {
			in.addItem(blocks.get(i).toItemStack());
		}
		if ((page - 1) >= 1) {
			in.setItem(48, manager.getArrowItem(page - 1));
		}
		if (blocks.size() > max) {
			in.setItem(50, manager.getArrowItem(page + 1));
		}
		builder.getPlayer().openInventory(in);
	}
	
	@Override
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			if (check(p)) {
				if (isBuilder(p)) {
					if (e.getCurrentItem() != null && e.getCurrentItem().getType() != null && e.getCurrentItem().getType() != Material.AIR) {
						if (e.getCurrentItem().isSimilar(getChestItem())) {
							e.setCancelled(true);
							openChest(1, p);
						} else if (isBlockInv(e.getClickedInventory())) {
							int page = getArrowItemPage(e.getCurrentItem());
							if (page != -1) {
								openChest(page, p);
								e.setCancelled(true);
								return;
							}
							e.getCurrentItem().setAmount(64);
						}
					}
				}
			}
		}
	}
	
	public boolean report(Player reporter, String reason) {
		if (lastReport != -1 && System.currentTimeMillis() - lastReport < (20 * 1000)) {
			reporter.sendMessage(ChatColor.RED + "You can't reported now, try again later");
			return false;
		}
		createReport(reporter, reason);
		return true;
	}
	
	public int getArrowItemPage(ItemStack i) {
		if (i == null || i.getType() == null || i.getType() != Material.ARROW) {
			return -1;
		}
		if (!i.hasItemMeta() || !i.getItemMeta().hasDisplayName() || i.getItemMeta().getDisplayName() == null || i.getItemMeta().getDisplayName().isEmpty()) {
			return -1;
		}
		if (i.getItemMeta().getDisplayName().startsWith("Page ")) {
			String a = i.getItemMeta().getDisplayName().replaceAll("Page ", "");
			if (Utils.isInt(a)) {
				return Utils.getInt(a);
			}
		}
		return -1;
	}
	
	public boolean isBlockInv(Inventory i) {
		if (i == null || i.getSize() != 6 * 9 || !i.getName().startsWith("More Blocks - Page ")) {
			return false;
		}
		return true;
	}
	
	public ItemStack getChestItem() {
		ItemStack i = new ItemStack(Material.CHEST);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName("More Blocks");
		i.setItemMeta(im);
		return i;
	}
	
	public boolean isBuilder(Player p) {
		return builders.isBuilder(p);
	}
		
	/**
	 * @return the builder
	 */
	public Builders getBuilders() {
		return builders;
	}
	/**
	 * @param builder the builder to set
	 */
	public void setBuilders(Builders builders) {
		this.builders = builders;
	}

	public int getBuildersCount() {
		return buildersCount;
	}

	@Override
	public String getGameType() {
		return getBuildersCount() + " Builder" + (getBuildersCount() == 1 ? "" : "s") + " Game";
	}
	
	public long getLastReport() {
		return lastReport;
	}

	public void setLastReport(long lastReport) {
		this.lastReport = lastReport;
	}
	
}