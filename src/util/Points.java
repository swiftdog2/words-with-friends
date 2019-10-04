package util;

import ui.Tile;

public class Points {
	
	/*
	 * Note: wild tiles count as 0 points
	 */
	public static int getValue(char letter) {
		switch (letter) {
		case 'a':
		case 'e':
		case 'i':
		case 'o':
		case 'r':
		case 's':
		case 't':
			return 1;
		case 'd':
		case 'l':
		case 'n':
		case 'u':
			return 2;
		case 'g':
		case 'h':
		case 'y':
			return 3;
		case 'c':
		case 'b':
		case 'f':
		case 'm':
		case 'p':
		case 'w':
			return 4;
		case 'k':
		case 'v':
			return 5;
		case 'x':
			return 8;
		case 'j':
		case 'q':
		case 'z':
			return 10;
		}
		return 0;
	}
	
	public static int getWordMultiplier(int tileType) {
		int multiplier = 1;
		switch(tileType) {
			case Tile.TYPE_DOUBLE_WORD:
				multiplier = 2;
				break;
			case Tile.TYPE_TRIPLE_WORD:
				multiplier = 3;
				break;
		}
		return multiplier;
	}
	
	public static int getLetterMultiplier(int tileType) {
		int multiplier = 1;
		switch(tileType) {
			case Tile.TYPE_DOUBLE_LETTER:
				multiplier = 2;
				break;
			case Tile.TYPE_TRIPLE_LETTER:
				multiplier = 3;
				break;
		}
		return multiplier;
	}
}
