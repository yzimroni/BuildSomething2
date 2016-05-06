package net.yzimroni.buildsomething2.game.bonuses.bonuses;

import java.util.Random;

import net.yzimroni.buildsomething2.BuildSomethingPlugin;
import net.yzimroni.buildsomething2.game.bonuses.BonusUser;
import net.yzimroni.buildsomething2.game.games.Game;
import net.yzimroni.buildsomething2.player.BPlayer;
import net.yzimroni.buildsomething2.utils.MaterialData;
import net.yzimroni.buildsomething2.utils.Utils;

import org.bukkit.Material;

public class RandomLetters extends Bonus {

	public RandomLetters(BuildSomethingPlugin p) {
		super(p);
	}

	@Override
	public boolean needBuildersGame() {
		return false;
	}

	@Override
	public MaterialData getItemType() {
		return new MaterialData(Material.INK_SACK, (byte) 1);
	}

	@Override
	public String getName() {
		return "Random Letters";
	}

	@Override
	public int getId() {
		return 10;
	}

	@Override
	public BonusUser getUsers() {
		return BonusUser.NORMAL;
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
	public void onUse(Game g, BPlayer p) {
		if (canUse(g, p)) {
			if (g.getLanguageOptions().haveChooseLang(p)) {
				String lang = g.getLanguageOptions().getLang(p);
				if (canUseRandomLetters(g.getWord().getWordByLang(lang))) {
					g.setMetadata(metadata(p.getPlayer().getName()), "true");
					p.getPlayer().getInventory().remove(getItem());
					takeBonus(p);
					
					String s = createRandomLettersString(g, p, g.getWord().getWordByLang(lang));
										
					g.messagePlayer(p.getPlayer(), formatMessage(s, lang));
					plugin.getActionBar().sendActionBar(p.getPlayer(), s);
				} else {
					sendCantUseMessage(p.getPlayer());
				}
			} else {
				g.getLanguageOptions().chooseLang(p, this);
			}
		} else {
			sendCantUseMessage(p.getPlayer());
		}
	}
	
	public String formatMessage(String s, String lang) {
		return "The random letters of the " + lang + " word: " + "\n" + s;
	}
	

	public String createRandomLettersString(Game g, BPlayer p, String word) {
		String numberOfLettersMetadata = plugin.getGameManager().getBonusesManager().getById(9).metadata(p.getPlayer().getName());
		boolean usedNumberOfLetters = g.hasMetadata(numberOfLettersMetadata);
		if (usedNumberOfLetters) {
			int[] lettersIndex = null;
			if (g.hasMetadata(metadata(p.getPlayer().getName()))) {
				String data = g.getMetadata(metadata(p.getPlayer().getName()));
				if (data != null && !data.isEmpty() && data.contains(":")) {
					String[] split = data.split(":");
					if (split.length == 2) {
						if (Utils.isInt(split[0]) && Utils.isInt(split[1])) {
							lettersIndex = new int[]{Integer.valueOf(split[0]), Integer.valueOf(split[1])};
						}
					}
				}
			}
			if (lettersIndex == null) {
				lettersIndex = getRandomLetters(word);
				g.removeMetadata(metadata(p.getPlayer().getName()));
				g.setMetadata(metadata(p.getPlayer().getName()), lettersIndex[0] + ":" + lettersIndex[1]);
			}
			String result = "";
			for (int i = 0; i < word.length(); i++) {
				if (lettersIndex[0] == i) {
					result += word.substring(lettersIndex[0], lettersIndex[0] + 1);
				} else if (lettersIndex[1] == i) {
					result += word.substring(lettersIndex[1], lettersIndex[1] + 1);
				} else {
					result += "_";
				}
			}
			result += " (" + word.length() + ")";
			return result;
			
		}  else {
			int[] lettersIndex = getRandomLetters(word);
			g.removeMetadata(metadata(p.getPlayer().getName()));
			g.setMetadata(metadata(p.getPlayer().getName()), lettersIndex[0] + ":" + lettersIndex[1]);
			
			Random random = new Random();
			int length = random.nextInt(7) + 7;
			String result = "";
			boolean oneplaced = false, twoplaced = false;
			boolean placeAsOne = (lettersIndex[0] + 1) == lettersIndex[1];
			//System.out.println(placeAsOne);
			if (lettersIndex[0] == 0) {
				if (placeAsOne) {
					result += word.substring(0, 2);
					twoplaced = true;
				} else {
					result += word.substring(0, 1);
				}
				oneplaced = true;
			}
			
			boolean tryPlace = placeAsOne && (lettersIndex[0] == word.length() - 2);
			boolean placedRoundBefore = false;
			for (int i = 0; i < length; i++) {
				
				if (i != 0 && !tryPlace) {
					if (!oneplaced) {
						boolean place = random.nextInt(4) == 2;
						if ((length - i) <= 3) {
							place = true;
						}
						if (place) {
							if (placeAsOne) {
								result += word.substring(lettersIndex[0], lettersIndex[0] + 2);
								twoplaced = true;
							} else {
								result += word.substring(lettersIndex[0], lettersIndex[0] + 1);
							}
							oneplaced = true;
							placedRoundBefore = true;
							continue;
						}
					} else if (!twoplaced && (lettersIndex[1] != (word.length() - 1))) {
						boolean place = random.nextInt(4) == 2;
						if ((length - i) <= 2) {
							place = true;
						}
						if (placedRoundBefore) {
							place = false;
							placedRoundBefore = false;
						}
						if (place) {
							result += word.substring(lettersIndex[1], lettersIndex[1] + 1);
							twoplaced = true;
							continue;
						}
					}
				}
				//System.out.println("added " + length + " " + i);
				result += "?";
				
			}
			
			if (!twoplaced) {
				if (placeAsOne) {
					result += word.substring(lettersIndex[1] - 1, lettersIndex[1] + 1);
					oneplaced = true;
				} else {
					result += word.substring(lettersIndex[1], lettersIndex[1] + 1);
				}
				twoplaced = true;

				/*result += word.substring(lettersIndex[1], lettersIndex[1] + 1);
				twoplaced = true;*/
			}
			
			return result;
		}
	}

	
	public int[] getRandomLetters(String word) {
		int[] letters = new int[2];
		
		if (word.length() <= 4) {
			return null;
		}
		
		Random random = new Random();
		
		letters[0] = random.nextInt(word.length());
		
		boolean foundAll = false;
		for (int i = 0; i < 200; i++) {
			int second = random.nextInt(word.length());
			if (letters[0] == second) {
				continue;
			}
			letters[1] = second;
			foundAll = true;
			break;
		}
		
		if (!foundAll) { 
			if ((letters[0] == word.length() - 1)) {
				letters[1] = letters[0] - 1;
			} else {
				letters[1] = letters[0] + 1;
			}
		}
		
		if (letters[0] > letters[1]) {
			int temp = letters[0];
			letters[0] = letters[1];
			letters[1] = temp;
		}
		
		return letters;
	}
	
	/*@Override
	public void onUse(Game g, BPlayer p) {
		if (canUse(g, p)) {
			RandomLettersInfo info = getRandomLetters(g.getWord().getWordEnglish(), g.hasMetadata(plugin.getGameManager().getBonusesManager().getById(3).metadataRaw(p.getPlayer().getName())));
			if (info.error) {
				p.getPlayer().sendMessage(info.text);
			} else {
				g.setMetadata(metadata(p.getPlayer().getName()), "true");
				p.getPlayer().getInventory().remove(getItem());
				takeBonus(p);
				g.setMetadata(metadataRaw(p.getPlayer().getName()), "" + info.first + ";" + info.second);
				g.messagePlayer(p.getPlayer(), "The random letters of the english word: " + "\n" + info.text);
				plugin.getActionBar().sendActionBar(p.getPlayer(), info.text);
			}
		} else {
			sendCantUseMessage(p.getPlayer());
		}
	}*/


	@Override
	public void onGameStop(Game g) {
		for (BPlayer p : g.getPlayers()) {
			p.getPlayer().getInventory().remove(getItem());
		}
		
	}
	
	@Override
	public boolean canUse(Game g, BPlayer p) {
		return getUsers().canUse(isBuilder(g, p)) && !g.hasMetadata(metadata(p.getPlayer().getName()));
	}

	@Override
	public void init() {
		
	}
	
	public boolean canUseRandomLetters(String s) {
		if (s == null || s.isEmpty()) return false;
		return s.length() >= 4;
	}

	@Override
	public double getPrice() {
		return 25;
	}


}
