package solving;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

import containers.Mask;
import containers.Solution;
import ui.Board;
import ui.Tile;
import util.Debug;
import util.Points;
import util.Stuff;

public class Scorer {
	private Solver solver;
	private Board board;
	
	public Scorer(Solver solver, Board board) {
		this.solver = solver;
		this.board = board;
	}

	public void scoreAll() {
		//Cache the masks
		ArrayList<Mask> masks = solver.getMasks();
		
		//For each mask
		for(int x = 0; x < masks.size(); x++) {
			//Cache the mask
			Mask mask = masks.get(x);
			
			//Score the mask solutions
			score(mask);
		}
		
		sort(masks);
	}
	
	/*
	 * Sorts solutions first by points (highest to lowest)
	 */
	public void sort(ArrayList<Mask> masks) {
		//Sort the solutions of each mask by point value (highest to lowest)
		for(int x = 0; x < masks.size(); x++)
			Collections.sort(masks.get(x).getSolutions());
		
		//Sort the masks by solution value (highest to lowest)
		Collections.sort(masks);
		
		//For each mask
		for(int x = 0; x < masks.size(); x++) {
			//Cache the mask
			Mask mask = masks.get(x);
			
			//Cache the mask solutions
			ArrayList<Solution> solutions = mask.getSolutions();
			
			if(solutions.size() > 0) {
				//Cache the solution
				Solution solution = solutions.get(0);
				
				//Print out the mask's highest valued solution
				Debug.out(Debug.PRINT_ALL_POINTS, " Solution: " + solution.toString() + ", score: " + solution.getPoints() + " for " + mask.hashCode() + " ");
				Debug.outln(Debug.PRINT_ALL_POINTS, mask);
			}
		}
		
		//Prints out the highest value solution and highlights the pieces
		if(masks.size() == 0) {
			Debug.outln(Debug.PRINT_ALL_POINTS, "No masks");
			return;
		}
		
		if(masks.size() > 0) {
			Mask bestMask = masks.get(0);
			if(bestMask.getSolutions().size() > 0) {
				Solution bestSolution = bestMask.getSolutions().get(0);
				
				if(board.autoPlace()) {
					for(int x = 0; x < bestSolution.size(); x++) {
						Tile tile = bestMask.get(x);
						//tile.setBackground(Color.YELLOW);
						char letter = bestSolution.get(x);
						tile.setLetter(letter);
						tile.setText(String.valueOf(letter).toUpperCase());
						tile.setBackground(Color.YELLOW);
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									Thread.sleep(1000);
									tile.updateColour();
								} catch (InterruptedException ex) {
								}
							}
						}).start();
					}
				}
				System.out.println("Best solution : " + bestSolution.toString() + ", points: " + bestSolution.getPoints());
			}
		}
	}
	
	/*
	 * Scores the solutions for a given mask and sorts them by point value (highest to lowest)
	 */
	public void score(Mask mask) {
		//Cache the mask solutions
		ArrayList<Solution> solutions = mask.getSolutions();
		
		Debug.out(true,  "Scoring " + mask.hashCode() + ": ");
		Debug.outln(true, mask);
		
		//For each solution
		for(int x = 0; x < solutions.size(); x++) {
			//Cache the solution
			Solution solution = solutions.get(x);
			
			//The number of letters placed
			int emptyFilled = 0;

			Debug.outln(true, "\tscoring solution: " + solution.toString());
			
			//The points for the base word
			int baseMultiplier = 1;
			int basePoints = 0;
			
			//The total number of extra points given from perpendicular words
			int extraPoints = 0;
			
			//For each letter in the solution
			for(int y = 0; y < solution.size(); y++) {
				//Cache the tile
				Tile tile = mask.get(y);
				
				//Cache the solution letter
				char letter = solution.get(y);
				
				int letterPoints = Points.getValue(letter);
				
				//If the tile is empty, we are placing a tile
				if(tile.isEmpty()) {
					emptyFilled++;
					
					//Add the points for the solution letter
					basePoints += letterPoints * Points.getLetterMultiplier(tile.getTileType());
					
					//Apply word multiplier (if any) from the tile (applies to basePoints only)
					baseMultiplier *= Points.getWordMultiplier(tile.getTileType());

					Debug.outln(true, "Tile is empty, letter: " + letter + " (" + letterPoints + "), base now: " + basePoints + " x" + baseMultiplier);

					//Get the mask representing words connected perpendicularly to this tile
					Mask perpendicular = board.getWord(tile, Stuff.getPerpendicularType(mask.getType()));
					Debug.outln(true, perpendicular);

					if(perpendicular.size() > 1) {
						int perpMultiplier = 1;
						int perpPoints = 0;

						//For each tile in the perpendicular mask
						for(int z = 0; z < perpendicular.size(); z++) {
							//Cache the perpendicular tile
							Tile tile2 = perpendicular.get(z);
	
							//Add the points for the solution letter
							if(tile2.isEmpty()) {
								perpPoints += Points.getValue(letter) * Points.getLetterMultiplier(tile2.getTileType());
								perpMultiplier *= Points.getWordMultiplier(tile2.getTileType());
							} else {
								perpPoints += Points.getValue(tile2.getLetter());
							}
							
							Debug.outln(true, "Perpendicular tile " + z + ": " + tile2.getLetter() + ", perp now: " + perpPoints + " x" + perpMultiplier);
						}
						
						extraPoints += perpPoints * perpMultiplier;
					}
				} else {
					//Add only the base tile point value
					basePoints += letterPoints;
				}
			}
			
			if(emptyFilled >= 7)
				extraPoints += 35;
			solution.setPoints((basePoints * baseMultiplier) + extraPoints);
		}
	}
}
