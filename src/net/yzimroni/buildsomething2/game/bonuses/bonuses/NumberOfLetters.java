package net.yzimroni.buildsomething2.game.bonuses.bonuses;

import org.bukkit.Material;


import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.bonuses.BonusUser;
import net.yzimroni.buildsomething2.game.games.Game;
import net.yzimroni.buildsomething2.player.BPlayer;
import net.yzimroni.buildsomething2.utils.MaterialData;

public class NumberOfLetters extends Bonus {

	public NumberOfLetters(BuildSomethingPlugin p) {
		super(p);
	}

	@Override
	public boolean needBuildersGame() {
		return false;
	}

	@Override
	public MaterialData getItemType() {
		return new MaterialData(Material.INK_SACK, (byte) 2);
	}

	@Override
	public String getName() {
		return "Number of letters";
	}

	@Override
	public int getId() {
		return 9;
	}

	@Override
	public BonusUser getUsers() {
		return BonusUser.NORMAL;
	}
	
	@Override
	public boolean canUse(Game g, BPlayer p) {
		return getUsers().canUse(isBuilder(g, p)) && !g.hasMetadata(metadata(p.getPlayer().getName()));
	}


	@Override
	public void onGameStart(Game g) {
		for (BPlayer b : g.getPlayers()) {
			if (canUse(g, b) && has(b)) {
				b.getPlayer().getInventory().addItem(getItem());
			}
		}
	}

	@Override
	public void onGameStop(Game g) {
		for (BPlayer p : g.getPlayers()) {
			p.getPlayer().getInventory().remove(getItem());
		}
	}
	
	@Override
	public void onUse(Game g, BPlayer p) {
		if (canUse(g, p)) {
			if (g.getLanguageOptions().haveChooseLang(p)) {
				String lang = g.getLanguageOptions().getLang(p);
				g.setMetadata(metadata(p.getPlayer().getName()), "true");
				g.setMetadata(metadataRaw(p.getPlayer().getName()), "true"); //TODO why?
				p.getPlayer().getInventory().remove(getItem());
				takeBonus(p);
				String s = null;
				String randomLettersMeta = plugin.getGameManager().getBonusesManager().getById(10).metadata(p.getPlayer().getName());
				if (g.hasMetadata(randomLettersMeta)) {
					RandomLetters rl = (RandomLetters) plugin.getGameManager().getBonusesManager().getById(10);
					s = rl.createRandomLettersString(g, p, g.getWord().getWordByLang(lang));
					System.out.println("s: " + s);
					s = rl.formatMessage(randomLettersMeta, lang);
					System.out.println("s after format: " + s);
				} else {
					s = getNumberOfLetters(g.getWord().getWordByLang(lang));
					s = "Number of letters in the " + lang + "  word: " + s;
				}
				//TODO check if the player used random letters, and if so, display it together
				g.messagePlayer(p.getPlayer(), s);
				plugin.getActionBar().sendActionBar(p.getPlayer(), s);
			} else {
				g.getLanguageOptions().chooseLang(p, this);
			}
		} else {
			sendCantUseMessage(p.getPlayer());
		}
	}
	
	public String getNumberOfLetters(String s) {
		String word = "";
		for (int i = 0; i<s.length(); i++) {
			word += "_";
		}
		word += " (" + s.length() + ")";
		return word;
	}

	/*@Override
	public void onUse(Game g, BPlayer p) {
		if (canUse(g, p)) {
			g.setMetadata(metadata(p.getPlayer().getName()), "true");
			g.setMetadata(metadataRaw(p.getPlayer().getName()), "true");
			p.getPlayer().getInventory().remove(getItem());
			takeBonus(p);
			String meta = plugin.getGameManager().getBonusesManager().getById(1).metadataRaw(p.getPlayer().getName());
			String i = "";
			boolean error = false;
			if (g.hasMetadata(meta)) {
				String m = g.getMetadata(meta);
				String[] split = m.split(";");
				RandomLettersInfo ri = ((RandomLetters) plugin.getGameManager().getBonusesManager().getById(1)).formatMessage(g.getWord().getWordEnglish(), Integer.valueOf(split[0]), Integer.valueOf(split[1]), true);
				error = ri.error;
				i = ri.text;
			} else {
				 i = getNumberOfLetters(g.getWord().getWordEnglish());
			}
			if (error) {
				p.getPlayer().sendMessage(i);
			} else {
				g.messagePlayer(p.getPlayer(), "Number of letters in the english word: " + i);
				plugin.getActionBar().sendActionBar(p.getPlayer(), i);
			}
		} else {
			sendCantUseMessage(p.getPlayer());
		}
	}*/

	@Override
	public void init() {
		
	}

	@Override
	public double getPrice() {
		return 15;
	}

	

}