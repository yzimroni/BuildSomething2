package net.yzimroni.buildsomething2.game;

public class Word {
	private int id;
	private String word_english;
	private String word_hebrew;
	
	
	public Word(int id, String word_english, String word_hebrew) {
		this.id = id;
		this.word_english = word_english;
		this.word_hebrew = word_hebrew;
	}
	
	public boolean isSame(String s) {
		s = s.toLowerCase();
		String s_rev = new StringBuilder(s).reverse().toString();
		return s.equalsIgnoreCase(word_english) || s.equalsIgnoreCase(word_hebrew) || s_rev.equalsIgnoreCase(word_hebrew);
	}


	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}


	/**
	 * @return the word_english
	 */
	public String getWordEnglish() {
		return word_english;
	}


	/**
	 * @param word_english the word_english to set
	 */
	public void setWordEnglish(String word_english) {
		this.word_english = word_english;
	}


	/**
	 * @return the word_hebrew
	 */
	public String getWordHebrew() {
		return word_hebrew;
	}


	/**
	 * @param word_hebrew the word_hebrew to set
	 */
	public void setWordHebrew(String word_hebrew) {
		this.word_hebrew = word_hebrew;
	}
	
	public String getWordByLang(String lang) {
		switch (lang.toLowerCase()) {
		case "hebrew":
			return getWordHebrew();

		default:
			return getWordEnglish();
		}
	}
	
	
	
}
