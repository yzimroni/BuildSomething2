package net.yzimroni.buildsomething2.game;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.event.Listener;

import net.yzimroni.buildsomething2.game.bonuses.bonuses.Bonus;
import net.yzimroni.buildsomething2.game.games.Game;
import net.yzimroni.buildsomething2.player.BPlayer;

public class LanguageOptions implements Listener {
	
	//private Game game;
	private HashMap<UUID, String> langs = new HashMap<UUID, String>();
	private HashMap<UUID, Bonus> choose = new HashMap<UUID, Bonus>();
	
	public LanguageOptions(Game game) {
		//this.game = game;
	}
	
	public boolean haveChooseLang(BPlayer p) {
		//TO DO first check if the player want to see both langs
		if (p.isHebrewWords()) {
			return langs.containsKey(p.getUUID());
		} else {
			return true;
		}
	}
	
	public String getLang(BPlayer p) {
		if (p.isHebrewWords()) {
			return langs.get(p.getUUID());
		} else {
			return "english";
		}
	}
	
	public void setLang(BPlayer p, String lang) {
		langs.remove(p.getUUID());
		langs.put(p.getUUID(), lang);
	}
	
	public void chooseLang(BPlayer p, Bonus b) {
		if (p.isHebrewWords()) {
			
			choose.put(p.getUUID(), b);
			
			
			/*//TODO do
			String lang = "hebrew";//new Random().nextBoolean() ? "hebrew" : "english";
			setLang(p, lang);
			p.getPlayer().sendMessage("You choose " + lang);*/
		} else {
			return; //The player using only one lang
		}
	}

}
