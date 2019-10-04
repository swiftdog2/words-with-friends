package util;

import java.awt.Color;
import java.awt.Dimension;

public class Constants {
	/*
	 * Character constants
	 */
	public static final char NULL_CHAR = '\u0000';
	public static final char CHAR_START = 97;
	public static final char CHAR_END = 122;

	/*
	 * Board sizes
	 */
	public static final int BOARD_15 = 15;
	public static final int BOARD_11 = 11;

	/*
	 * Graph/WordTrie constants
	 */
	public static final int WORD_LENGTH = 15;
	public static final int LETTERS_LENGTH = 26;
	public static final String DICTIONARY = "src/data/wwf.txt";

	/*
	 * UI Constants
	 */
	public static final int TILE_SIZE = 25;
	public static final Color BOARD_BACKGROUND = Color.WHITE;
	public static final Dimension INPUT_DIALOG_SIZE = new Dimension(160, 75);
	public static final Dimension INPUT_BOX_SIZE = new Dimension(130, 25);
	public static final Dimension PIECES_BOX_SIZE = new Dimension(150, 25);
	public static final Character WILD_TILE = '?';

	public static Dimension getPanelSize(int boardSize) {
		return new Dimension(boardSize * 30 + 5, boardSize * 30 + 45);
	}
}
