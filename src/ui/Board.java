package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import containers.CharList;
import containers.Mask;
import solving.Scanalyser;
import solving.Scorer;
import solving.Solver;
import util.Constants;
import util.Debug;
import words.WordTrie;

public class Board {
	public int SIZE;

	private Tile[][] tiles;
	private CharList pool;
	private boolean autoPlace;
	private WordTrie trie;
	
	/*
	 * Constructor
	 */
	public Board(int SIZE) {
		this.SIZE = SIZE;
	}
	
	/*
	 * Loads the board UI
	 */
	public void load() {
		//Set a pointer to this object instance
		Board board = this;

		//Create the frame and set some basic properties
		JFrame scrabble = new JFrame("Scrabble V" + Debug.VERSION);
		scrabble.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		scrabble.setLocationRelativeTo(null);
		
		//Create the panel
		JPanel panel = new JPanel();
		panel.setPreferredSize(Constants.getPanelSize(SIZE));
		panel.setBackground(Constants.BOARD_BACKGROUND);

		//Add the panel to the frame
		scrabble.add(panel);
		
		//Load the tiles
		tiles = new Tile[SIZE][SIZE];
		
		for(int row = 0; row < SIZE; row++) {
			for(int col = 0; col < SIZE; col++) {
				//Create the new tile
				Tile tile = new Tile(row, col, SIZE);
				
				//Finally, put the tile in its place
				tiles[row][col] = tile;

				//Add the panel to the tile
				panel.add(tile);
				
				//Set the tile click handler
				tile.addMouseListener(new MouseAdapter() {
		            public void mouseReleased(MouseEvent e) {
		            	//Create the letter input frame
		            	JDialog dialog = new JDialog();
		            	dialog.setTitle("Enter letters");
						dialog.setPreferredSize(Constants.INPUT_DIALOG_SIZE);
						dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
						dialog.setLocationRelativeTo(null);
						dialog.pack();
						dialog.setVisible(true);
						
						//Create the letter input panel
						JPanel panel = new JPanel();
						
						//Create the text input box
						JTextField textField = new JTextField();
						textField.setPreferredSize(Constants.INPUT_BOX_SIZE);
						textField.setText(textField.getText().toUpperCase());
						
						//Set the key press handler for the text field
						textField.addKeyListener(new KeyListener() {
							@Override
							public void keyPressed(KeyEvent event) {
						          if(event.getKeyChar() == KeyEvent.VK_ENTER) {
										//If no characters are entered
										if (textField.getText().length() == 0) {
											tile.clear();
										} else {
											//If right-clicked, fill the letters vertically
											board.fill(tile, textField.getText(), SwingUtilities.isRightMouseButton(e));
										}
										dialog.dispose();  
						          } else if(event.getKeyChar() == KeyEvent.VK_ESCAPE) {
						        	  dialog.dispose();
						          }
							}

							@Override
							public void keyReleased(KeyEvent event) {
							}

							@Override
							public void keyTyped(KeyEvent event) {
							}
						});
						
						//Add a focus listener to the text field
						textField.addFocusListener(new FocusListener() {
							@Override
							public void focusGained(FocusEvent arg0) {
								
							}

							@Override
							public void focusLost(FocusEvent arg0) {
								dialog.dispose();
							}
						});
						
						//Add the components to the panel
						panel.add(textField);

						//Add the panel to the frame
						dialog.add(panel);
		            }
				});
			}
		}

		//Create the pieces text field
		JTextField piecesBox = new JTextField();
		piecesBox.setPreferredSize(Constants.PIECES_BOX_SIZE);

		//Create the analyse button
		JButton analyseBtn = new JButton("Analyse");

		//Passes the board to Scanalyser for scanning & analysis
		analyseBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Set the board's pool
				setPool(new CharList(piecesBox.getText().toLowerCase().toCharArray()));
				
				//Scanalyse the board
				Scanalyser sca = new Scanalyser(board);
				sca.scan();
				 
				//Solve the masks scanned
				Solver sol = new Solver(sca);
				sol.run();
				
				//Score the solutions
				Scorer sco = new Scorer(sol, board);
				sco.scoreAll();
			}
		});

		//Adds a key listener for the pieces box
		piecesBox.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent event) {
				if(event.getKeyCode() == KeyEvent.VK_ENTER)
					analyseBtn.doClick();
			}

			@Override
			public void keyReleased(KeyEvent event) {
			}

			@Override
			public void keyTyped(KeyEvent event) {
			}
		});

		JCheckBox autoPlaceBox = new JCheckBox("Auto place");
		autoPlaceBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
	        	board.setAutoPlace(arg0.getStateChange() == ItemEvent.SELECTED);
			}
		});
		
		
		//Add the finishing components to the panel
		panel.add(piecesBox);
		panel.add(analyseBtn);
		panel.add(autoPlaceBox);

		//Pack the UI and fire it up
		scrabble.pack();
		scrabble.setVisible(true);
	}
	
	protected void setAutoPlace(boolean autoPlace) {
		this.autoPlace = autoPlace;
	}

	private void fill(Tile tile, String word, boolean vertical) {
		//Add the letters to this line, but don't flow over to the next
		for(int x = 0; x < word.length(); x++) {
			//Cache the letter
			char letter = word.toLowerCase().charAt(x);
			if(letter == Constants.NULL_CHAR)
				continue;
			
			//If the character is a letter, make sure it's upper-case
			if(letter >= Constants.CHAR_START && letter <= Constants.CHAR_END) {
				tile.setText(String.valueOf(letter).toUpperCase()); // Recapitalises the letter
				tile.setLetter(letter);
			} else {
				//Otherwise, clear the letter
				tile.setText(null);
				tile.setLetter(Constants.NULL_CHAR);
			}				
			
			if(vertical)
				tile = this.getDown(tile);
			else
				tile = this.getRight(tile);
			if(tile == null)
				return;
		}
	}

	/*
	 * Returns the tile at the given row and column index
	 */
	private Tile getTile(int rowIndex, int colIndex) {
		if(rowIndex < 0 || colIndex < 0 || rowIndex >= SIZE || colIndex >= SIZE)
			return null;
		return tiles[rowIndex][colIndex];
	}
	
	/*
	 * Gets the tiles of this board
	 */
	public Tile[][] getTiles() {
		return tiles;
	}

	/*
	 * Gets the row at the specified index
	 */
	public Tile[] getRow(int rowIndex) {
		return tiles[rowIndex];
	}
	
	/*
	 * Takes the absolute index and converts it into a row index
	 */
	public int getRowIndex(int absolute) {
		return absolute / SIZE;
	}

	/*
	 * Takes the absolute index and converts it into a column index
	 */
	public int getColIndex(int absolute) {
		return absolute % SIZE;
	}
	
	/*
	 * Returns the column as a 1D array
	 */
	public Tile[] getColumn(int colIndex) {
		Tile[] column = new Tile[SIZE];

		for(int rowIndex = 0; rowIndex < SIZE; rowIndex++)
			column[rowIndex] = getTile(rowIndex, colIndex);
		
		return column;
	}
	
	/*
	 * Gets the word connected to the specified tile in the specified direction
	 */
	public Mask getWord(Tile tile, int type) {
		Mask word = new Mask();
		word.setType(type);
		
		//Add the initial tile
		word.add(tile);
		
		//Look LEFT or UP as much as we can can for connected tiles
		while(tile != null) {
			tile = getPrev(tile, type);
			if(tile == null)
				break;
			if(tile.isEmpty())
				break;
			word.addFirst(tile);
		}

		//Jump back to the empty tile we started at
		tile = word.getLast();
		
		//Look RIGHT as much as we can can for connected tiles
		while(tile != null) {
			tile = getNext(tile, type);
			if(tile == null)
				break;
			if(tile.isEmpty())
				break;
			word.addLast(tile);
		}

		return word;
	}
	
	public Mask getPerpendicularWord(Tile tile, int type) {
		if(type == Mask.TYPE_HORIZONTAL)
			return getWord(tile, Mask.TYPE_VERTICAL);
		else if(type == Mask.TYPE_VERTICAL)
			return getWord(tile, Mask.TYPE_HORIZONTAL);
		return null;
	}
	
	public Tile getUp(Tile tile) {
		return getTile(tile.getRow() - 1, tile.getCol());
	}
	
	public Tile getDown(Tile tile) {
		return getTile(tile.getRow() + 1, tile.getCol());
	}
	
	public Tile getRight(Tile tile) {
		return getTile(tile.getRow(), tile.getCol() + 1);
	}

	public Tile getLeft(Tile tile) {
		return getTile(tile.getRow(), tile.getCol() - 1);
	}
	
	public CharList getPool() {
		return pool;
	}
	
	public void setPool(CharList pool) {
		this.pool = filterPool(pool);
	}
	
	public static CharList filterPool(CharList pool) {
		Iterator<Character> charIter = pool.iterator();
		while(charIter.hasNext()) {
			Character next = charIter.next();
			if(next < Constants.CHAR_START || next > Constants.CHAR_END)
				charIter.remove();
		}
		return pool;
	}

	public Tile getNext(Tile tile, int direction) {
		if(direction == Mask.TYPE_HORIZONTAL)
			return getRight(tile);
		else if(direction == Mask.TYPE_VERTICAL)
			return getDown(tile);
		return tile;
	}
	
	public Tile getPrev(Tile tile, int type) {
		if(type == Mask.TYPE_HORIZONTAL)
			return getLeft(tile);
		else if(type == Mask.TYPE_VERTICAL)
			return getUp(tile);
		return tile;
	}

	public boolean autoPlace() {
		return autoPlace;
	}

	public WordTrie getTrie() {
		return trie;
	}

	public void setTrie(WordTrie graph) {
		this.trie = graph;
	}
}
