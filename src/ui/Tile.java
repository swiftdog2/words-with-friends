package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JButton;

import util.Constants;

public class Tile extends JButton {
	private static final long serialVersionUID = 6630689015053350489L;

	public static final int
			TYPE_DEFAULT = 0,
			TYPE_DOUBLE_LETTER = 1,
			TYPE_TRIPLE_LETTER = 2,
			TYPE_DOUBLE_WORD = 3,
			TYPE_TRIPLE_WORD = 4;

	private int row, col;
	private int type = TYPE_DEFAULT;
	
	private char letter;

	/*
	 * Constructor
	 */

	Tile(int row, int col, int boardSize) {
		setPreferredSize(new Dimension(Constants.TILE_SIZE, Constants.TILE_SIZE));
		setMargin(new Insets(0, 0, 0, 0));
		setForeground(Color.BLACK);

		setRow(row);
		setCol(col);
		setTileType(boardSize);
		updateColour();
	}
	
	/*
	 * Updates the background colour of the tile
	 */
	public void updateColour() {
		switch (this.type) {
			case TYPE_DOUBLE_LETTER:
				setBackground(Color.CYAN);
				break;
			case TYPE_TRIPLE_LETTER:
				setBackground(Color.GREEN);
				break;
			case TYPE_DOUBLE_WORD:
				setBackground(Color.RED);
				break;
			case TYPE_TRIPLE_WORD:
				setBackground(Color.ORANGE);
				break;
			default:
				setBackground(Color.WHITE);
		}
	}
	
	/*
	 * Returns the points type of the tile
	 */
	public int getTileType() {
		return this.type;
	}
	
	public void setTileType(int SIZE) {
		int[][][] DL = null, TL = null, DW = null, TW = null;
		
		if(SIZE == Constants.BOARD_15) {		
			 DL = new int[][][] {
				//{{rows}, {columns}}
				{{2, 14}, {3, 13}},
				{{3, 13}, {2, 5, 11, 14}},
				{{5, 11}, {3, 7, 9, 13}},
				{{7, 9}, {5, 11}},
			};
			
			TL = new int[][][] {
				{{1, 15}, {7, 9}},
				{{4, 12}, {4, 12}},
				{{6, 10}, {6, 10}},
				{{7, 9}, {1, 15}},
			};
			
			DW = new int[][][] {
				{{2, 14}, {6, 10}},
				{{4, 12}, {8}},
				{{6, 10}, {2, 14}},
				{{8}, {4, 12}},
			};
			
			TW = new int[][][] {
				{{1, 15}, {4, 12}},
				{{4, 12}, {1, 15}},
			};
		} else if (SIZE == Constants.BOARD_11) {
			 DL = new int[][][] {
				{{3, 9}, {3, 5, 7, 9}},
				{{5, 7}, {3, 9}},
			};
			
			TL = new int[][][] {
				{{1, 11}, {1, 11}},
				{{4, 8}, {4, 8}},
			};
			
			DW = new int[][][] {
				{{2, 10}, {2, 6, 10}},
				{{6}, {2, 10}},
			};
			
			TW = new int[][][] {
				{{1, 11}, {3, 9}},
				{{3, 9}, {1, 11}},
			};
		}
		
		if(inGroup(DL))
			this.type = TYPE_DOUBLE_LETTER;
		else if(inGroup(TL))
			this.type = TYPE_TRIPLE_LETTER;
		else if(inGroup(DW))
			this.type = TYPE_DOUBLE_WORD;
		else if(inGroup(TW))
			this.type = TYPE_TRIPLE_WORD;
	}
	
	/*
	 * Converts the row and column index to the absolute index
	 */
	private boolean inGroup(int[][][] group) {
		//Go through each row/column group
		for(int x = 0; x < group.length; x++) {
			int[] row = group[x][0];
			int[] col = group[x][1];
			
			//For each row index
			for(int y = 0; y < row.length; y++) {
				if(this.getRow() == row[y] - 1) {
					//Check each column index
					for(int z = 0; z < col.length; z++) {
						if(this.getCol() == col[z] - 1) {
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}

	/*
	 * Returns true if the tile character is empty
	 */
	public boolean isEmpty() {
		return this.letter == Constants.NULL_CHAR;
	}

	/*
	 * Gets the tile's letter
	 */
	public char getLetter() {
		return this.letter;
	}

	/*
	 * Sets the tile's letter
	 */
	public void setLetter(char letter) {
		this.letter = letter;
	}
	
	/*
	 * Clears the letter
	 */
	public void clearLetter() {
		this.letter = Constants.NULL_CHAR;
	}

	/*
	 * Gets the column index of the tile
	 */
	public int getCol() {
		return col;
	}
	
	/*
	 * Sets the column index of the tile
	 */
	public void setCol(int col) {
		this.col = col;
	}

	/*
	 * Sets the row index of the tile
	 */
	public int getRow() {
		return row;
	}

	/*
	 * Sets the row index of the tile
	 */
	public void setRow(int row) {
		this.row = row;
	}

	public void clear() {
		//Clear the letter from the JButton
		setText("");
		
		//Clear the letter from the Tile
		clearLetter();
	}
}
