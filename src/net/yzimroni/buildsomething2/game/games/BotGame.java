/**
 * TODO
 * Create a system that save the builds (for know, use the normal plot world, after this maybe other plot world?)
 * 2 plots world, 1 normal and the other is the bot plot world
 * there will be a table in the db (bot_plot) with the plot id, and info about the build (word id, who build that etc...)
 * 
 * System that randomaly choose the build to build (if one of the players in the game played in this build, so not this build)
 * System that build the blocks V
 * Ensure that special blocks dont drop (like ladder) V
 * 
 * Maybe emulte time to end X
 * 
 */

package net.yzimroni.buildsomething2.game.games;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.yzimroni.buildsomething2.game.BotPlot;
import net.yzimroni.buildsomething2.game.GameInfo.GameType;
import net.yzimroni.buildsomething2.game.GameInfo.PlotType;
import net.yzimroni.buildsomething2.game.GameManager;
import net.yzimroni.buildsomething2.game.Gamemode;
import net.yzimroni.buildsomething2.game.PlayerInfo;
import net.yzimroni.buildsomething2.game.effects.effects.lib.ParticleEffect;
import net.yzimroni.buildsomething2.player.economy.RewardInfo;
import net.yzimroni.buildsomething2.utils.BlockLocation;
import net.yzimroni.buildsomething2.utils.Cuboid;
import net.yzimroni.buildsomething2.utils.IntWarpper;
import net.yzimroni.buildsomething2.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMeCoreManager;

public class BotGame extends Game {
	
	private BotPlot plot;
	
	private List<BlockLocation> blocks;
	private int block_index = 0;
	private int l_x = 0, l_y = 0, l_z = 0;
	private IntWarpper build_block_task = new IntWarpper();

	public BotGame(GameManager gm, int maxPlayers) {
		super(gm, maxPlayers);
		gameInfo.setGameType(GameType.BOT_GAME);
	}
	
	/*
	 *  /botplot send (Require accept) - Add the player and the plot (with the time) to an hashmap (for now will do /botplot send accept)
	 *  /botplot send accept - Set bot_request in the game tables to '1' (for now not)
	 *  /botplot admin list - Show all games with bot_request set to '1' (V)
	 *  /botplot admin accept <ID> - Set bot_request in the game id to '0', 
	 *  copy the plot from the plotworld to the plotworld, set plot_type to '1' and plot_id to the new plot id (PlotManager#movePlotToBot)
	 *  
	 *  /botplot admin deny <ID> - Set bot_request in the game id to '0'
	 */
	
	@Override
	protected void start() {
		if (checkClose()) {
			stop(true, false);
			return;
		}
		if (!checkLessPlayersStart()) {
			return;
		}
		if (mode != Gamemode.LOBBY_COUNTDOWN) return;
		setMode(Gamemode.RUNNING);

		plot = manager.randomBotPlot(getPlayersBukkit());
		System.out.println("plot id: " + plot.getPlotId() + "," + plot.getId()); //TODO its just for testing, remvoe it later
		word = manager.getWord(plot.getWordId());
		
		
		for (Player p : getPlayersBukkit()) {
			Utils.sendTitleSub(p, ChatColor.BLUE + "The game started!", ChatColor.AQUA + "This is a bot game, enjoy!", (int) (0.5 * 20), 4 * 20, (int) (0.5 * 20));
			p.setGameMode(GameMode.ADVENTURE);
			p.teleport(map.getNormal());
		}
		
		super.start();
				
		initBlocks();
		
		if (plot.isShowBuilders()) {
			if (!plot.getBuilders().isEmpty()) {
				if (plot.getBuilders().size() == 1) {
					message("The original builder is " + Bukkit.getOfflinePlayer(plot.getBuilders().get(0)).getName());
				} else {
					/*String result = "";
					String last_name = "";
					for (UUID u : plot.getBuilders()) {
						String name = Bukkit.getOfflinePlayer(u).getName();
						if (!last_name.isEmpty()) {
							if (!result.isEmpty()) {
								result += ", ";
							}
							result += last_name;
						}
						last_name = name;
					}
					if (!last_name.isEmpty()) {
						if (!result.isEmpty()) {
							result += " and ";
						}
						result += last_name;
					}*/
					
					String result = Utils.formatPlayerList(plot.getBuilders()); //TODO check
					
					message("The original builders are " + result);
				}
			}
		}
	}
	
	@Override
	public boolean addPlayer(Player p) {
		if (check(p)) return false;
		if (!isJoinable()) return false;

		if (checkClose()) {
			stop(true, false);
			return false;
		}
		return super.addPlayer(p);
	}
	
	private boolean checkClose() {
		if (Bukkit.getOnlinePlayers().size() > 8) {
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	private void initBlocks() {
		Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				blocks = new ArrayList<BlockLocation>();
				
				World w = Bukkit.getWorld("plotworld");
				Plot pl = PlotMeCoreManager.getInstance().getPlotById(Utils.plotId(plot.getPlotId()), PlotMeCoreManager.getInstance().getWorld(w.getName()));
				Location l1 = new Location(w, pl.getPlotBottomLoc().getBlockX(), 65, pl.getPlotBottomLoc().getBlockZ());
				Location l2 = new Location(w, pl.getPlotTopLoc().getBlockX(), 100, pl.getPlotTopLoc().getBlockZ());

				Cuboid c = new Cuboid(l1, l2);
				int x = c.getLowerX();
				int y = c.getLowerY();
				int z = c.getLowerZ();
				
				for (Block b : c.getBlocks()) {
					handleBlock(b, x, y, z);
				}
				
				ProtectedRegion pr = getRegion();
				l_x = pr.getMinimumPoint().getBlockX();
				l_y = pr.getMinimumPoint().getBlockY();
				l_z = pr.getMinimumPoint().getBlockZ();
				
				buildBlocks();
			}
		});
	}
	
	private void handleBlock(Block b, int x, int y, int z) {
		if (b.getY() <= 64) {
			System.out.println("handleblock return due y is less 64: " + b.getY() + ", " + b);
			return; //We dont want to copy the grass block from the plot
		}
		if (b.getType() == Material.SAND || b.getType() == Material.GRAVEL) {
			Block bc = b.getLocation().add(0, -1, 0).getBlock();
			handleBlock(bc, x, y ,z);
		}
		int offsetX = x - b.getX();
		int offsetY = y - b.getY();
		int offsetZ = z - b.getZ();
		@SuppressWarnings("deprecation")
		BlockLocation bl = new BlockLocation(offsetX, offsetY, offsetZ, b.getType(), b.getData());//createBlockLocation(b, x, y, z);
		if (bl.getType() != Material.AIR && !blocks.contains(bl)) {
			blocks.add(bl);
		}

	}
	
	private void buildBlocks() {
		long time = 5; //In ticks
		final IntWarpper pertime = new IntWarpper(1);
		//If the build will take more then 4 minutes
		while (blocks.size() / (20 / time) > (game_count_down.getValue() - 20) && time > 1) {
			time--;
			System.out.println("time: " + time);
		}
		
		while (time == 1 && pertime.getValue() < 20 && (blocks.size() / pertime.getValue()) / (20 / time) > (game_count_down.getValue() - 20)) {
			pertime.setValue(pertime.getValue() + 1);
			System.out.println("pertime: " + pertime.getValue());
		}
		build_block_task.setValue(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				if (!blocks.isEmpty()) {
					for (int i = 0; i < pertime.getValue(); i++) {
						buildNextBlock();
					}
				} else {
					Bukkit.getScheduler().cancelTask(build_block_task.getValue());
					return;
				}
			}
		}, time, time));
	}
		
	@SuppressWarnings("deprecation")
	private void buildNextBlock() {
		//Maybe remove the block and then always read the index 0
		if (block_index < (blocks.size())) {
			BlockLocation bl = blocks.get(block_index);
			//If not working, set the - to + or set to max instead of min
			Location l = new Location(map.getBuilder().getWorld(), l_x - bl.getOffsetX(), l_y - bl.getOffsetY(), l_z - bl.getOffsetZ());
			l.getBlock().setType(bl.getType());
			l.getBlock().setData(bl.getData());
			
			//ParticleEffect.LAVA.display(new Vector(1, 1, 1), 0, l, 400);
			//ParticleEffect.PORTAL.display(new Vector(1, 1, 1), 2, l, 400);
			//TODO effect and maybe NPC
			block_index++;
		}
	}
	
	@Override
	protected void knowTheWord(Player p) {
		getStats(p).addKnow();
		if (knows.size() == 0) {
			rewardCoins(p, 10, "Know the word first");
			getStats(p).addKnowFirst();
		} else {
			rewardCoins(p, 7, "Know the word");
		}
		super.knowTheWord(p);
		checkEnd();
	}
	
	@Override
	protected void checkEnd() {
		if (mode == Gamemode.RUNNING) {
			if (knows.size() >= (players.size())) {
				stop(false, false);
			}
		}
	}
	@Override
	public void stop(boolean force, boolean nodelay) {
		if (!plugin.isEnabled()) nodelay = true;
		Bukkit.getScheduler().cancelTask(build_block_task.getValue());
		if (word != null) {
			if (!knows.isEmpty()) {
				message("The word was " + word.getWordEnglish() + " (%hebword%)");
				if (plot.isShowBuilders()) {
					double coins = knows.size() * 2;
					if (knows.size() >= players.size()) {
						coins += 6;
					}
					double perb = coins;
					for (UUID u : plot.getBuilders()) {
						plugin.getPlayerManager().getEconomy().addBotCoins(u, perb);
					}
				}
			} else {
				message("No one get the word :(");
			}
		}
		
		gameInfo.setPlotId(plot.getPlotId());
		gameInfo.setPlotType(PlotType.PLOT_BUILT_BY_BOT);
		
		message("The game " + (force ? "stopped" : "ended"));
		
		for (Player p : getPlayersBukkit()) {
			RewardInfo r = getRewardInfo(p);
			long knowTime = r == null ? -1 : r.getTimeTook();
			if (knowTime == 0) {
				knowTime = -1;
			}
			
			gameInfo.addPlayer(new PlayerInfo(p.getUniqueId(), 2, -1, knowTime));
		}
		
		clearMapBuildArea();
		
		super.stop(force, nodelay);
	}
	
	@Override
	protected void afterEnd(boolean stat) {
		for (Player p : getPlayersBukkit()) {
			if (stat) {
				getStats(p).addTotalGame();
			}
		}
		super.afterEnd(stat);
	}
	
	@Override
	public void onBlockBreak(BlockBreakEvent e) {
		if (check(e.getPlayer())) {
			if (!e.getPlayer().isOp()) {
				e.setCancelled(true);
				return;
			}
		}
	}
	
	@Override
	public void onBlockPlace(BlockPlaceEvent e) {
		if (check(e.getPlayer())) {
			if (!e.getPlayer().isOp()) {
				e.setCancelled(true);
				e.setBuild(false);
				return;
			}
		}
	}
	
	public ChatPlayerType getChatPlayerType(Player p) {
		if (check(p)) {
			if (knows.contains(p.getUniqueId())) {
				return ChatPlayerType.KNOW_BUILDER;
			} else {
				return ChatPlayerType.DONT_KNOW;
			}
		} else {
			return ChatPlayerType.OUT_GAME;
		}
	}

	@Override
	public String getGameType() {
		return "Bot Game";
	}
	
	//TO DO get the blocks need to build and build them
	
}
