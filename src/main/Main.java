package main;

import util.Constants;
import words.WordTrie;

public class Main {
	/*
	 * Loads up the "game server"
	 */
	public static void main(String[] args) {
		//Create a trie graph of the dictionary we want to load
		WordTrie trie = WordTrie.load(Constants.DICTIONARY);
		
		Launcher launcher = new Launcher("Scrabble V5");
		launcher.setDictionary(trie);
		launcher.loadUI();
	}
}
