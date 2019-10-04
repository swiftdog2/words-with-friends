package solving;

import java.util.ArrayList;

import containers.CharList;
import containers.Mask;
import ui.Board;
import ui.Tile;
import util.Debug;
import util.Stuff;

public class Scanalyser {
	/*
	 * Static flags
	 */
	private static final int LEFT = 0, RIGHT = 1;

	/*
	 * Instance variables
	 */
	private ArrayList<Mask> masks = new ArrayList<Mask>();
	private Board board;
	
	/*
	 * Constructors
	 */
	public Scanalyser(Board board) {
		this.board = board;
	}
	
	/*
	 * Scans the board for masks and caches them for solving
	 */
	public void scan() {
		Debug.outln(Debug.SCAN, "\n----- 1. [SCANNING] -----");

		//Clear the current cache of masks
		masks.clear();
		
		//Scan the grid for masks
		scanRows();
		scanCols();

		//Print out the masks
		Stuff.printMasks(masks, Debug.SCAN);
	}

	/*
	 * Passes along rows for scanning
	 */
	private void scanRows() {
		Debug.outln(Debug.SCAN, "\n----- 1.1 [SCAN ROWS] -----");
		
		//Look at each row
		for(int x = 0; x < board.SIZE; x++) {
			Tile[] row = board.getRow(x);
			//Print out the row
			Stuff.printRow(row);

			//Scan the row
			scanArray(row, Mask.TYPE_HORIZONTAL);
		}
	}
	
	/*
	 * Passes along columns for scanning
	 */
	private void scanCols() {
		Debug.outln(Debug.SCAN, "\n----- 1.2 [SCAN COLS] -----");
		for(int x = 0; x < board.SIZE; x++) {
			Tile[] col = board.getColumn(x);
			scanArray(col, Mask.TYPE_VERTICAL);
		}
	}
	
	/*
	 * Builds masks given a 1D array (used for horizontal and column scanning)
	 */
	private void scanArray(Tile[] row, int type) {
		//Look at each tile in the row
		for(int x = 0; x < board.SIZE;)
			x = scanFromTile(row, type, x);
	}
	
	/*
	 * 
	 */
	public ArrayList<Mask> scanFromTile2(Tile[] row, int type, int index) {
		ArrayList<Mask> masks = new ArrayList<Mask>();
		
		Stuff.printRow(row);

		//Create a FIFO queue to represent mask tiles
		Mask mask = new Mask();
		mask.setType(type);

		//Set the base word start and end indexes, including the word itself
		mask.setBaseStart(index);
		mask.setBaseEnd(getEmpty(row, index, RIGHT) - 1);
		mask.setBaseWord(Stuff.getBaseWord(mask, row));
		
		//If it doesn't the left side
		if(index > 0) {
			Tile prev = row[index - 1];
			if(!prev.isEmpty())
				return masks;
		}
		
		//Consume to the right as much as you can
		int consumed = consumeRight2(row, mask, index, getPool().size());
		//Debug.outln(Debug.VERIFYING2, "(2) Consuming on right side... #" + consumed);
		//Debug.outln(Debug.VERIFYING2, mask);

		//We consumed to the right, so save the initial mask
		if(consumed > 0)
			masks.add(new Mask(mask));

		//See how many extra tiles we can place to the left
		int extra = getPool().size() - consumed;
		//Debug.outln(Debug.VERIFYING2, "Extra tiles for left side: " + extra + " (" + getPool().size() + "-" + consumed + ")");
		
		//Retract only as many characters as we have consumed
		for(int y = 0; y < getPool().size(); y++) {
			//Retract only if we have no extra tiles to spare for the left side
			if(extra-- <= 0) {
				//Debug.out(Debug.VERIFYING2, "Retracting from the right...");
				retract(row, mask, RIGHT);
				//Debug.outln(Debug.VERIFYING2, mask);
			}
			
			//Try to consume the tile to the left
			//Debug.out(Debug.VERIFYING2, "Consuming left side...\t");
			int left = consume(row, mask, LEFT, 1);
			Debug.outln(Debug.VERIFYING2, mask);
			
			//If we couldn't consume to the left (either no space, or touches left side)
			if(left == 0) {
				//Debug.outln(Debug.VERIFYING2, "Couldn't consume left, STOP!!!!!");
				break;
			}
			
			//Save the rolled mask
			masks.add(new Mask(mask));
		}
		
		//Return the masks that were generated
		return masks;
	}
	
	private int scanFromTile(Tile[] row, int type, int index) {
		//Create a FIFO queue to represent mask tiles
		Mask mask = new Mask();
		mask.setType(type);

		//Cache the tile
		Tile tile = row[index];
		
		int skipIndex;

		//Skip past empty tiles
		if(tile.isEmpty()) {
			return index + 1;
		} else {
			//We've encountered a letter tile, work on the mask
			//Debug.outln(Debug.SCAN, "\nLooking at letter: " + letter + " at index " + index);

			//Skip to the tile after the space after we're done with this mask
			skipIndex = getEmpty(row, index, RIGHT);
			//System.out.println("Skip index: " + skipIndex);
			
			//Set the base word start and end indexes, including the word itself
			mask.setBaseStart(index);
			mask.setBaseEnd(skipIndex - 1);
			mask.setBaseWord(Stuff.getBaseWord(mask, row));

			//Consume to the right as much as you can
			int consumed = consume(row, mask, index, RIGHT, getPool().size());
			//Debug.outln(Debug.SCAN, "Consuming on right side... #" + consumed);

			//We consumed to the right, so save the initial mask
			if(consumed > 0)
				save(mask);

			//See how many extra tiles we can place to the left
			int extra = getPool().size() - consumed;
			//Debug.outln(Debug.SCAN, "Extra tiles for left side: " + extra + " (" + getPool().size() + "-" + consumed + ")");
			
			//Retract only as many characters as we have consumed
			for(int y = 0; y < getPool().size(); y++) {
				//Retract only if we have no extra tiles to spare for the left side
				if(extra-- <= 0) {
					//Debug.out(Debug.SCAN, "Retracting from the right...");
					retract(row, mask, RIGHT);
					//Debug.outln(Debug.SCAN, mask);
				}
				
				//Try to consume the tile to the left
				//Debug.out(Debug.SCAN, "Consuming left side...\t");
				int left = consume(row, mask, LEFT, 1);
				//Debug.outln(Debug.SCAN, mask);
				
				//If we couldn't consume to the left (either no space, or touches left side)
				if(left == 0) {
					//Debug.outln(Debug.SCAN, "Couldn't consume left, STOP!!!!!");
					break;
				}
				
				//Save the rolled mask
				save(mask);
			}
			
			//Clear the tiles from the mask
			mask.clear();
			
			//Skips to the beginning tile of the next word
			return skipIndex;
		}		
	}
	
	/*
	 * Consumes N tiles in the specified direction
	 */
	private int consume(Tile[] row, Mask mask, int direction, int quantity) {
		return consume(row, mask, mask.getFirstIndex(), direction, quantity);
	}
	
	/*
	 * Attempts to consume the specified number of empty tiles in the given direction
	 * Returns the number of tiles we actually consumed
	 */
	private int consume(Tile[] row, Mask mask, int startIndex, int direction, int quantity) {
		if(direction == LEFT)
			return consumeLeft(row, mask, startIndex, quantity);
		else if(direction == RIGHT)
			return consumeRight(row, mask, startIndex, quantity);
		
		return -420;
	}
	
	public int consumeRight2(Tile[] row, Mask mask, int startIndex, int quantity) {
		int consumed = 0;
		boolean connected = false; //Whether we've already connected the word letters
		
		//Consume the specified amount starting at the specified index
		for(int x = startIndex; x < board.SIZE && consumed < quantity; x++) {
			//Cache the tile
			Tile tile = row[x];
			
			//If it's not touching the right edge
			if(x < board.SIZE - 1) {
				//Look at the tile after this
				Tile next = row[x + 1];
				
				//If the next tile is a letter
				if(next.isEmpty()) {
					connected = true;
				} else {
					if(connected)
						break;
				}
			}
			
			//If the tile is empty, we "consumed" an empty tile
			if(tile.isEmpty())
				consumed++;
				
			//Add the tile to the mask
			mask.addLast(tile);
		}
		return consumed;
	}
	
	public int consumeRight(Tile[] row, Mask mask, int startIndex, int quantity) {
		int consumed = 0;
		//Consume the specified amount starting at the specified index
		for(int x = startIndex; x < board.SIZE && consumed < quantity; x++) {
			//Cache the tile
			Tile tile = row[x];
			
			//Add the tile to the mask
			mask.addLast(tile);
			
			//If the tile is empty, we "consumed" an empty tile
			if(tile.isEmpty()) {
				consumed++;

				//If we've consumed the last tile
				if(consumed == quantity) {
					//Consume any letters connected to the right of this last tile
					for(int y = x + 1; y < board.SIZE; y++) {
						Tile connect = row[y];
						if(connect.isEmpty())
							break;
						mask.addLast(connect);
					}
				}
			}
		}
		return consumed;		
	}
	
	public static int consumeLeft(Tile[] row, Mask mask, int startIndex, int quantity) {
		int consumed = 0;

		for(int x = startIndex - 1; x >= 0 && consumed < quantity; x--) {
			//Cache the tile
			Tile tile = row[x];
			
			//If it's second tile or later
			if(x > 0) {
				//Look at the tile before this
				Tile prev = row[x - 1];
				
				//If the previous tile is a letter, stop
				if(!prev.isEmpty()) {
					//System.out.println("-- Previous has letter, STOP!");
					break;
				}
			}
			
			//Add the tile to the mask
			mask.addFirst(tile);
			
			//If the tile is empty, we "consumed" an empty tile
			if(tile.isEmpty())
				consumed++;
		}
		return consumed;
	}
	
	/*
	 * Anti-consumes 1 tiles in the specified direction
	 */

	private static void retract(Tile[] row, Mask mask, int direction) {
		//We want to pull inwards from the left
		if(direction == LEFT) {
			//Loop through the mask from the beginning to end
			while(mask.size() > 0) {
				//If the first item is empty, remove it and stop
				if(mask.getFirst().isEmpty()) {
					mask.removeFirst();
					break;
				} else {
					//Otherwise, it's a leading character, keep going
					mask.removeFirst();
				}
			}
		} else if (direction == RIGHT) {
			//Loop through the mask from the end to beginning
			while(mask.size() > 0) {
				//If the last item is empty, remove it and stop
				if(mask.getLast().isEmpty()) {
					mask.removeLast();
					break;
				} else {
					//Otherwise, it's a leading character, keep going
					mask.removeLast();
				}
			}
		}
	}
	
	/*
	 * Finds the first empty tile to the left of this one
	 */
	private static int getEmpty(Tile[] row, int index, int direction) {
		if(direction == LEFT) {
			//Look backwards from the index
			for(int x = index - 1; x >= 0; x--)
				if(row[x].isEmpty())
					return x;
			return -1;
		} else if(direction == RIGHT) {
			//Look ahead of the index
			for(int x = index + 1; x < row.length; x++)
				if(row[x].isEmpty())
					return x;
			return row.length;
		}
		
		return -69;
	}
	
	/*
	 * Saves a deep copy of the mask
	 */
	private void save(Mask mask) {
		//Debug.out(Debug.SCAN, "SAVING MASK: ");
		//Debug.outln(Debug.SCAN, mask);
		masks.add(new Mask(mask));
	}
	
	/*
	 * Getters and setters
	 */
	
	public CharList getPool() {
		return board.getPool();
	}

	public Board getBoard() {
		return board;
	}
	
	public ArrayList<Mask> getMasks() {
		return masks;
	}
}
