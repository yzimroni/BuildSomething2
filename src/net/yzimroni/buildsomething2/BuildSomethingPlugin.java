package net.yzimroni.buildsomething2;

import java.util.logging.Logger;

import net.yzimroni.buildsomething2.command.CommandManager;
import net.yzimroni.buildsomething2.game.GameManager;
import net.yzimroni.buildsomething2.player.PlayerManager;
import net.yzimroni.buildsomething2.player.TopPlayers;
import net.yzimroni.buildsomething2.scoreboard.ScoreboardManager;
import net.yzimroni.buildsomething2.utils.ActionBar;
import net.yzimroni.buildsomething2.utils.BSProtocolLib;
import net.yzimroni.buildsomething2.utils.MCSQL;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

public class BuildSomethingPlugin extends JavaPlugin {
	public final Logger log = Logger.getLogger("BuildSomething");
	
	private MCSQL sql;

	private Events events;
	private PlayerManager playermanager;
	private GameManager gamemanager;
	private CommandManager commandmanager;
    private ProtocolManager protocollib;
    //private PotionsEffectRemover pfr;
    private BSProtocolLib bsprotocollib;
    private ScoreboardManager scoreboardmanager;
    private TopPlayers top;
    private ActionBar actionbar;

	@Override
	public void onEnable() {
		try {
			sql = new MCSQL("127.0.0.1", "3306", "buildsomething", "root", "2YPzXyPb");
		} catch (Exception e) {
			log.info("cant connect to the DB");
			Bukkit.setWhitelist(true);
			if (Bukkit.getOnlinePlayers().size() > 0) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.kickPlayer("SERVER ERROR");
				}
			}
			return;
		}
		
		getConfig().options().copyDefaults(true);
		
		protocollib = ProtocolLibrary.getProtocolManager();
		
		top = new TopPlayers(this);
		actionbar = new ActionBar(this);
		scoreboardmanager = new ScoreboardManager(this);
		scoreboardmanager.initTeams(Bukkit.getScoreboardManager().getMainScoreboard());
		events = new Events(this);
		playermanager = new PlayerManager(this);
		gamemanager = new GameManager(this);
		commandmanager = new CommandManager(this);
		//pfr = new PotionsEffectRemover(this);
		bsprotocollib = new BSProtocolLib(this);
		
		
		Bukkit.getPluginManager().registerEvents(events, this);
		Bukkit.getPluginManager().registerEvents(playermanager, this);
		Bukkit.getPluginManager().registerEvents(gamemanager, this);
		gamemanager.getBlockManager().getBlockShop().initManager();
		
		
	}
	
	@Override
	public void onDisable() {
		protocollib.removePacketListeners(this);
		protocollib = null;
		commandmanager = null;
		gamemanager.onDisable();
		gamemanager = null;
		events = null;
		playermanager.onDisable();
		playermanager = null;
		actionbar.onDisable();
		actionbar = null;
		bsprotocollib = null;
		top = null;
		if (sql != null){
			sql.closeConnecting();
		}
		sql = null;
	}
		
	public String hebrewMessage(Player p, String s) {
		String lang = p.spigot().getLocale();
		boolean rev = needRevrese(lang);
		if (rev) {
			return new StringBuilder(s).reverse().toString();
		} else {
			return s;
		}
	}
	
	private boolean needRevrese(String lang) {
		switch (lang) {
		case "he_IL":
			return false;
		default:
			return true;
		}
	}
	
	public MCSQL getDB() {
		return sql;
	}

	public Events getEvents() {
		return events;
	}

	public PlayerManager getPlayerManager() {
		return playermanager;
	}

	public GameManager getGameManager() {
		return gamemanager;
	}

	/**
	 * @return the protocollib
	 */
	public ProtocolManager getProtocolLib() {
		return protocollib;
	}

	/**
	 * @param protocollib the protocollib to set
	 */
	public void setProtocollib(ProtocolManager protocollib) {
		this.protocollib = protocollib;
	}

	public CommandManager getCommandManager() {
		return commandmanager;
	}

	public void setCommandmanager(CommandManager commandmanager) {
		this.commandmanager = commandmanager;
	}

	public BSProtocolLib getBsprotocolLib() {
		return bsprotocollib;
	}

	public void setBsprotocollib(BSProtocolLib bsprotocollib) {
		this.bsprotocollib = bsprotocollib;
	}

	/**
	 * @return the scoreboardmanager
	 */
	public ScoreboardManager getScoreboardManager() {
		return scoreboardmanager;
	}

	/**
	 * @param scoreboardmanager the scoreboardmanager to set
	 */
	public void setScoreboardManager(ScoreboardManager scoreboardmanager) {
		this.scoreboardmanager = scoreboardmanager;
	}

	/**
	 * @return the top
	 */
	public TopPlayers getTopPlayers() {
		return top;
	}

	/**
	 * @param top the top to set
	 */
	public void setTopPlayers(TopPlayers top) {
		this.top = top;
	}
	
	/**
	 * @return the actionbar
	 */
	public ActionBar getActionBar() {
		return actionbar;
	}

	/**
	 * @param actionbar the actionbar to set
	 */
	public void setActionBar(ActionBar actionbar) {
		this.actionbar = actionbar;
	}
	
}
