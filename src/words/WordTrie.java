package words;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import containers.CharList;
import containers.Node;
import util.Constants;

public class WordTrie {
	private Node root = new Node();
	
	public static WordTrie load(String directory) {
		WordTrie trie = new WordTrie();
		
		try {
			long start = System.currentTimeMillis();
			BufferedReader br = new BufferedReader(new FileReader(new File(Constants.DICTIONARY)));
			String lineRead;
			while((lineRead = br.readLine()) != null)
				trie.addWord(lineRead.toLowerCase().toCharArray());
			System.out.println("Loaded in " + (System.currentTimeMillis() - start) + "ms");
			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(0);
		}
		
		return trie;
	}
	
	/*
	 * Loads the full words into the tries associated with each letter
	 */
	private void addWord(char[] word) {
		Node parent = getRoot();
		
		//For each letter in the word
		for(int x = 0; x < word.length; x++) {
			//Cache the letter
			char letter = word[x];
			
			//Try to find the child node
			Node child = parent.getChild(letter);
			
			//If the letter is not a child
			if(child == null) {
				//Create the new node
				child = new Node(letter);
				
				//Set it as a child of the parent node
				parent.setChild(child);
			}
			
			//Go down the branch
			parent = child;
			
			if(x == word.length - 1)
				parent.setTerminates();
		}
	}
	
	public Node findWordNode(char[] word) {
		//Start at the root node
		Node node = root;
		
		//For each letter in the word
		for(int x = 0; x < word.length; x++) {
			//Cache the letter
			char letter = word[x];
			
			//Try to find the child node
			Node child = node.getChild(letter);
			
			//If it's null, we couldn't find that letter as a child
			if(child == null)
				return null;
			
			node = child;
		}
		
		return node;
	}

	public Node findWordNode(CharList word) {
		char[] array = new char[word.size()];
		for(int x = 0; x < array.length; x++)
			array[x] = word.get(x);
		return findWordNode(array);
	}

	public boolean findWord(CharList word) {
		Node node = findWordNode(word);
		if(node == null)
			return false;
		return node.isTerminal();
	}
	
	public Node getRoot() {
		return root;
	}
}
