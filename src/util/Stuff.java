package util;

import java.util.ArrayList;
import java.util.Iterator;

import containers.CharList;
import containers.Mask;
import ui.Tile;

public class Stuff {
	public static CharList getUniques(CharList list) {
		CharList uniques = new CharList();
		
		Iterator<Character> iter = list.iterator();
		while(iter.hasNext()) {
			Character next = iter.next();
			
			if(!uniques.contains(next))
				uniques.add(next);
		}
		return uniques;
	}

	public static char[] getStart(CharList letters) {
		CharList start = new CharList();
		
		//Look at each letter
		for(int x = 0; x < letters.size(); x++) {
			//If the letter is not empty
			if(!letters.isEmpty(x))
				//Add it to the start list
				start.add(letters.get(x));
			else
				break;
		}
		
		//Cache the Characters in a char array
		char[] startChars = new char[start.size()];
		for(int x = 0; x < start.size(); x++)
			startChars[x] = start.get(x);
		
		return startChars;
	}
	
	/*
	 * Returns a CharList of the array containing elements in the specified indices
	 */
	public static CharList asCharList(CharList list, int endIndex) {
		CharList list2 = new CharList();
		for(int x = 0; x <= endIndex; x++)
			list2.add(list.get(x));
		return list2;
	}
	
	public static CharList getBaseWord(Mask mask, Tile[] row) {
		CharList baseWord = new CharList();
		for(int x = mask.getBaseStart(); x <= mask.getBaseEnd(); x++)
			baseWord.add(row[x].getLetter());
		return baseWord;
	}

	public static void printRow(Tile[] row) {
		for(int x = 0; x < row.length; x++)
			Debug.out(Debug.SCAN, (row[x].isEmpty() ? "-" : row[x].getLetter()) + "");
		Debug.outln(Debug.SCAN, "");
	}
	
	public static int getPerpendicularType(int type) {
		if(type == Mask.TYPE_HORIZONTAL)
			return Mask.TYPE_VERTICAL;
		else if(type == Mask.TYPE_VERTICAL)
			return Mask.TYPE_HORIZONTAL;
		return -1;
	}
	
	/*
	 * Prints the specified mask list
	 */
	public static void printMasks(ArrayList<Mask> masks, boolean debug, int indent) {
		for(int x = 0; x < indent; x++)
			Debug.out(debug, "\t");
		Debug.outln(debug, "----- [PRINTING MASKS] -----");
		for(Mask mask : masks) {
			for(int x = 0; x < indent; x++)
				Debug.out(debug, "\t");
			Debug.outln(debug, mask);
		}
	}
	
	public static void printMasks(ArrayList<Mask> masks) {
		printMasks(masks, true);
	}

	public static void printMasks(ArrayList<Mask> masks, int indent) {
		printMasks(masks, true, indent);
	}

	public static void printMasks(ArrayList<Mask> masks, boolean debug) {
		printMasks(masks, debug, 1);
	}

	public static CharList getTrieWord(CharList letters) {
		boolean addedLetter = false;
		CharList trieWord = new CharList();
		
		for(int x = 0; x < letters.size(); x++) {
			Character letter = letters.get(x);
			//If the letter is empty
			if(letters.isEmpty(x)) {
				//If we've already added a letter, stop
				if(addedLetter)
					break;
			}
			
			trieWord.add(letter);

			if(!letters.isEmpty(x))
				addedLetter = true;
		}
		
		return trieWord;
	}
}
