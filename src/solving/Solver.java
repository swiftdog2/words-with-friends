package solving;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import containers.CharList;
import containers.Mask;
import containers.Node;
import containers.Solution;
import ui.Board;
import ui.Tile;
import util.Debug;
import util.Stuff;

public class Solver {
	/*
	 * Instance variables
	 */
	private ArrayList<Mask> masks;
	private Scanalyser scan;
	
	/*
	 * Constructors
	 */
	public Solver(Scanalyser scan) {
		this.scan = scan;
		masks = scan.getMasks();
	}
	
	/*
	 * Solves all the masks
	 */
	public void run() {
		Debug.outln(Debug.SOLVING, "\n----- 2. [SOLVING] -----");
		
		//Solve the masks
		solve(masks);
		
		//Prune solutions which are not valid
		verifyAll(masks);
	}
	
	/*
	 * Verifies the masks specified
	 */
	private void verifyAll(ArrayList<Mask> masks) {
		Debug.outln(Debug.VERIFYING, "\n----- 3. [VERIFYING] -----");
		
		ArrayList<Mask> newMasks = new ArrayList<Mask>();
		
		//Look at each mask
		for(int x = 0; x < masks.size(); x++) {
			//Cache the mask
			Mask mask = masks.get(x);
			
			//Return verified masks
			ArrayList<Mask> verifiedMasks = verify(mask);
			
			if(verifiedMasks == null) {
				//If the mask wasn't verified, remove it
				Debug.outln(Debug.VERIFYING, "Mask: " + mask.getLetters() + " was removed...");
				masks.set(x, null);
			} else if(verifiedMasks.size() > 1) {
				Debug.outln(Debug.VERIFYING, "Mask: " + mask.getLetters() + " was replaced by masks:");
				Stuff.printMasks(verifiedMasks, Debug.VERIFYING);
				//If we returned adjacent masks which supersede this one, add them
				newMasks.addAll(verifiedMasks);
				//And remove this one
				masks.set(x, null);
			}
		}

		//Remove null masks
		Iterator<Mask> maskIter = masks.iterator();
		while(maskIter.hasNext())
			if(maskIter.next() == null)
				maskIter.remove();
		
		//Add in the new masks
		masks.addAll(newMasks);
		
		Stuff.printMasks(masks, Debug.VERIFYING);
	}
	
	/*
	 * Verify that the mask solutions don't form invalid words perpendicular to tiles placed
	 */
	private ArrayList<Mask> verify(Mask mask) {
		ArrayList<Mask> newMasks = new ArrayList<Mask>();
		
		//Cache the board
		Board board = scan.getBoard();
		
		Debug.out(Debug.VERIFYING, "\nVerifying solutions for mask: ");
		Debug.outln(Debug.VERIFYING, mask);
		
		//Cache the solutions
		ArrayList<Solution> solutions = mask.getSolutions();
		
		//Cache the mask letters
		CharList letters = mask.getLetters();
		
		//The base mask has at least one solution
		boolean hasSolution = false;
		
		//For each solution to this mask
		Iterator<Solution> solveIter = solutions.iterator();
		while(solveIter.hasNext()) {
			//Cache the solution
			Solution solution = solveIter.next();
			
			//Debug.outln(Debug.VERIFYING, "\tChecking solution: '" + solution.toString() + "'");
			
			//Whether this solution is valid for all empty tiles filled
			boolean valid = true;
			
			//Look at each of the letters in the mask
			for(int solIndex = 0; solIndex < solution.size(); solIndex++) {
				//Cache the tile from the solution
				Tile tile = mask.get(solIndex);
				
				//Cache the letter
				Character letter = solution.get(solIndex);
				
				if(letters.isEmpty(solIndex)) {
					//If the empty letter index is within the solution
					if(solIndex < solution.size()) {
						//Temporarily set the empty letter
						tile.setLetter(letter);
						
						//Verify the solutions perpendicular to the empty letter
						Mask perpendicular = board.getPerpendicularWord(tile, mask.getType());
						
						//Debug.out(Debug.VERIFYING, "\t\tEmpty at " + solIndex + ", " + perpendicular.getTypeString() +" mask: ");
						//Debug.outln(Debug.VERIFYING, perpendicular);
						
						boolean exists = true;
						
						//If we formed a perpendicular word
						if(perpendicular.size() > 1)
							//Check if the word exists
							exists = scan.getBoard().getTrie().findWord(perpendicular.getLetters());
						
						//If it doesn't, we formed an invalid perpendicular word
						if(!exists) {
							//Therefore this solution is invalid
							valid = false;
							Debug.outln(Debug.VERIFYING, "\tSolution: '" + solution.toString() + "' FAILED: " + perpendicular.getLetters() + " \uf071");

							//Clear the empty letter tile
							tile.clearLetter();
							
							//Remove this solution from the mask
							solveIter.remove();
							
							//And remove the highlight tile colour
							tile.updateColour();
							break;
						}

						//If we only placed one letter, then this is verified
						if(solution.size() == mask.getBaseSize() + 1) {
							//If it touches the left or right end of the word
							if(solIndex == mask.getRelativeStart() - 1 || solIndex == mask.getRelativeEnd() + 1) {
								//Form the perpendicular adjacent mask
								Mask adjacent = new Mask();
								
								//Set the mask type as perpendicular to the base mask
								adjacent.setType(Stuff.getPerpendicularType(mask.getType()));
								
								//Add the tile we filled with the one letter to the adjacent mask
								adjacent.add(tile);

								Debug.outln(Debug.VERIFYING2, "------ 4. [ADJACENT MASKS] -------");
								Debug.outln(Debug.VERIFYING2, adjacent);
								
								Tile[] array = null;
								
								if(adjacent.getType() == Mask.TYPE_HORIZONTAL)
									array = board.getRow(tile.getRow());
								else if(adjacent.getType() == Mask.TYPE_VERTICAL)
									array = board.getColumn(tile.getCol());
									
								//Temporarily remove the letter we placed from the pool
								getPool().remove(letter);
								
								//Generate adjacent masks
								ArrayList<Mask> adjacentMasks = scan.scanFromTile2(array, adjacent.getType(), adjacent.getFirstIndex());
								
								//Print out the resultant masks
								Stuff.printMasks(adjacentMasks, Debug.VERIFYING2);
								
								//Solve them
								solve(adjacentMasks);
								
								verifyAll2(adjacentMasks);
								
								//And add the letter back to the pool
								getPool().add(letter);
								
								//Clear the empty letter tile
								tile.clearLetter();

								//These adjacent masks supersede this mask
								newMasks.addAll(adjacentMasks);
							}
						}
						
						//Clear the empty letter tile
						tile.clearLetter();
					}
				}
			}
			
			//If this solution passed all perpendicular word tests, it has been fully verified
			if(valid) {
				Debug.outln(Debug.VERIFYING, "\tSolution: '" + solution.toString() + "' verified \uf42e");
				hasSolution = true;
			}
		}
		
		//If the base mask has a solution
		if(hasSolution)
			newMasks.add(mask);
		
		/*
		//If this mask has no solutions, or all solutions failed, there's no need for this mask
		if(mask.getSolutions().size() == 0)
			return null;
		*/
		
		Debug.outln(Debug.VERIFYING, "These are the new masks:");
		//Stuff.printMasks(newMasks, Debug.VERIFYING);

		return newMasks;
	}
	
	/*
	 * Verify that the mask solutions don't form invalid words perpendicular to tiles placed
	 */
	/*
	 * Verifies the masks specified
	 */
	private void verifyAll2(ArrayList<Mask> masks) {
		Debug.outln(Debug.VERIFYING, "\n----- 5. [VERIFYING 2] -----");
		
		//Look at each mask
		for(int x = 0; x < masks.size(); x++) {
			//Cache the mask
			Mask mask = masks.get(x);
			
			//Return true if the mask has valid solutions
			boolean verified = verify2(mask);
			
			//If the mask wasn't verified, remove it
			if(!verified) {
				Debug.outln(Debug.VERIFYING, "Mask : " + mask.getLetters() + " was removed...");
				masks.set(x, null);
			}
		}

		//Remove null masks
		Iterator<Mask> maskIter = masks.iterator();
		while(maskIter.hasNext())
			if(maskIter.next() == null)
				maskIter.remove();
		
		Stuff.printMasks(masks, Debug.VERIFYING);
	}

	private boolean verify2(Mask mask) {
		//Cache the board
		Board board = scan.getBoard();
		
		Debug.out(Debug.VERIFYING, "\n(2) Verifying solutions for mask: ");
		Debug.outln(Debug.VERIFYING, mask);
		
		//Cache the solutions
		ArrayList<Solution> solutions = mask.getSolutions();
		
		//Cache the mask letters
		CharList letters = mask.getLetters();
		
		//For each solution to this mask
		Iterator<Solution> solveIter = solutions.iterator();
		while(solveIter.hasNext()) {
			//Cache the solution
			Solution solution = solveIter.next();
			
			//Debug.outln(Debug.VERIFYING, "\tChecking solution: '" + solution.toString() + "'");
			
			//Whether this solution is valid for all empty tiles filled
			boolean valid = true;
			
			//Look at each of the letters in the mask
			for(int solIndex = 0; solIndex < solution.size(); solIndex++) {
				//Cache the tile from the solution
				Tile tile = mask.get(solIndex);
				
				//Cache the letter
				Character letter = solution.get(solIndex);
				
				if(letters.isEmpty(solIndex)) {
					//If the empty letter index is within the solution
					if(solIndex < solution.size()) {
						//Temporarily set the empty letter
						tile.setLetter(letter);
						
						//Verify the solutions perpendicular to the empty letter
						Mask perpendicular = board.getPerpendicularWord(tile, mask.getType());
						
						Debug.out(Debug.VERIFYING, "\t\tEmpty at " + solIndex + ", " + perpendicular.getTypeString() +" mask: ");
						Debug.outln(Debug.VERIFYING, perpendicular);
						
						boolean exists = true;
						
						//If we formed a perpendicular word
						if(perpendicular.size() > 1)
							//Check if the word exists
							exists = scan.getBoard().getTrie().findWord(perpendicular.getLetters());
						
						//Clear the empty letter tile
						tile.clearLetter();

						//If it doesn't, we formed an invalid perpendicular word
						if(!exists) {
							//Therefore this solution is invalid
							valid = false;
							Debug.outln(Debug.VERIFYING, "\tSolution: '" + solution.toString() + "' FAILED \uf071");

							//Remove this solution from the mask
							solveIter.remove();
							
							break;
						}
					}
				}
			}
			
			//If this solution passed all perpendicular word tests, it has been fully verified
			if(valid)
				Debug.outln(Debug.VERIFYING, "\tSolution: '" + solution.toString() + "' verified \uf42e");
		}
		
		//Returns true if we have valid solutions for this mask
		return mask.getSolutions().size() > 0;
	}

	/*
	 * Solves all of the masks we're supplied with
	 */
	private void solve(ArrayList<Mask> masks) {
		Iterator<Mask> maskIter = masks.iterator();
		while(maskIter.hasNext()) {
			//Cache the mask
			Mask mask = maskIter.next();

			//Produce the relevant solutions to the mask
			ArrayList<Solution> solutions = getSolutions(mask);
				
			if(solutions.size() == 0) {
				//If no solutions were produced, delete the mask
				Debug.outln(Debug.SOLVING, "\t\uf071 Removing mask: " + mask + " (no solutions produced)");
				maskIter.remove();
			} else {
				//Otherwise, set the solutions for the mask
				mask.setSolutions(solutions);
			}
		}
	}
	
	private static ArrayList<Solution> solutions;
	private static CharList poolStack, letterStack;
	private static CharList wordStack = new CharList();
	private static int startDepth = 0, minDepth = 0;
	
	/*
	 * Generates solutions for a mask
	 */
	private ArrayList<Solution> getSolutions(Mask mask) {
		//Prepare a list for solutions to this mask
		solutions = new ArrayList<Solution>();
		
		Debug.out(Debug.SOLVING, "Solving mask: ");
		Debug.outln(Debug.SOLVING, mask);
		
		//Make a deep copy of the pool characters
		poolStack = new CharList(getPool());
		Collections.sort(poolStack);

		//Cache the letters in the mask
		letterStack = mask.getLetters();

		//Returns the beginning word for this mask
		char[] start = Stuff.getStart(letterStack);
		System.out.println(">>>>>Letter stack: " + letterStack + ", start: " + String.valueOf(start));

		//Add the base word to the word stack
		wordStack.add(start);

		//Set the starting depth for the base word
		startDepth = start.length;
		
		//If the mask begins with a solid word
		if(startDepth > 0)
			//The word must be at least 1 character longer than the starting depth
			minDepth = startDepth + 1;
		else
			//If the word doesn't begin with a solid word, it must at least connect to the tile
			minDepth = mask.getRelativeStart() + 1;
		
		//Get the node in the trie that follows the pattern in the start string
		Node startNode = scan.getBoard().getTrie().findWordNode(start);

		//Recursively solve the mask
		if(startNode != null)
			permuteSolve(startNode, startDepth);
		
		//Cleanup before the next mask is solved
		wordStack.clear();
		
		return solutions;
	}
	
	/*
	 * Recursively search node children to efficiently generate word permutations
	 */
	private static void permuteSolve(Node node, int depth) {
		//If this node is terminal, this node is a word end
		if (node.isTerminal()) {
			//System.out.println("\t>>Node: " + node.getValue() + " is terminal, depth: " + depth);
			//If we've reached the end of the word
			if(depth == letterStack.size()) {
				Debug.outln(Debug.SOLVING, "WORD FOUND 1: " + String.valueOf(wordStack));
				solutions.add(new Solution(wordStack));
			} else {
				//Otherwise, the next tile must be empty or it'd connect to the next
				if(letterStack.isEmpty(depth) && depth >= minDepth) {
					Debug.outln(Debug.SOLVING, "\t>>WORD FOUND 2: " + String.valueOf(wordStack));
					//System.out.println("\t>>Depth: " + depth + ", letter at depth: " + letterStack.get(depth) + ", node: " + node.getValue() + "\n");
					solutions.add(new Solution(wordStack));
				}
			}
		}
		
		//If we've reached the end, but it isn't a valid word, just stop
		if (depth == letterStack.size()) {
			//System.out.println("\tyou what my friend, depth: " + depth + " equals " + letterStack.size());
			return;
		}
		
		//If the letter here is empty
		if (letterStack.isEmpty(depth)) {
			//Get the unique set of letters in the pool
			CharList uniques = Stuff.getUniques(poolStack);
			
			//For each of these unique letters
			for(int x = 0; x < uniques.size(); x++) {
				//Cache the letter
				Character poolLetter = uniques.get(x);
				
				//Get the child of the current node with the pool letter
				Node child = node.getChild(uniques.get(x));
				
				//If the child exists
				if (child != null) {
					wordStack.add(poolLetter); //Add the pool letter to the permutation stack
					poolStack.remove(poolLetter); //Temporarily remove pool letter for this recursive branch
					//System.out.println("-> Word stack: " + wordStack + ", poolStack: " + poolStack + " - depth: " + (depth + 1));
					permuteSolve(child, depth + 1); //Go down the branch
					wordStack.remove(wordStack.size() - 1); //Pop the letter off the stack
					poolStack.add(x, poolLetter); //Add the letter back to the pool after we're done
					//System.out.println("<- Word stack: " + wordStack + ", poolStack: " + poolStack + " - depth: " + (depth + 1));
				} else {
					//System.out.println("Could not find: " + uniques.get(x) + " as child of node: " + node.getValue());
				}
			}
		} else {
			//Cache the letter
			Character letter = letterStack.get(depth);
			
			//Cache the child with the next letter
			Node child = node.getChild(letter);
			
			//If it doesn't exist, this word is invalid
			if (child == null)
				return;

			wordStack.add(letter); //And add it to the permutation stack
			//System.out.println("--> Word stack: " + wordStack + ", poolStack: " + poolStack + " - depth: " + (depth + 1));
			permuteSolve(child, depth + 1); //Go down the branch
			wordStack.remove(wordStack.size() - 1); //And pop it off the stack
			//System.out.println("<-- Word stack: " + wordStack + ", poolStack: " + poolStack + " - depth: " + (depth + 1));
		}
	}
	
	/*
	 * Getters and setters
	 */
	public ArrayList<Mask> getMasks() {
		return masks;
	}
	
	public CharList getPool() {
		return scan.getPool();
	}
}
