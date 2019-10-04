package containers;

import java.util.ArrayList;
import java.util.LinkedList;

import ui.Tile;

public class Mask extends LinkedList<Tile> implements Comparable<Mask> {
	private static final long serialVersionUID = 753922582197219296L;

	public static final int TYPE_HORIZONTAL = 0, TYPE_VERTICAL = 1;

	private ArrayList<Solution> solutions;
	private int type, baseStart, baseEnd;
	CharList baseWord;
	
	/*
	 * Constructor
	 */
	public Mask() {
		
	}
	
	/*
	 * Copy constructor
	 */
	public Mask(Mask mask) {
		this.addAll(mask);
		setType(mask.getType());
		baseStart = mask.getBaseStart();
		baseEnd = mask.getBaseEnd();
		baseWord = mask.getBaseWord();
	}
	
	/*
	 * Returns the CharArray representing the letters in this mask
	 */
	public CharList getLetters() {
		CharList letters = new CharList();
		for(int x = 0; x < size(); x++)
			letters.add(get(x).getLetter());
		return letters;
	}
	
	/*
	 * Print out the mask letters
	 */
	public void print() {
		System.out.println("[" + getTypeString() + ": " + toString() + ", " + getIndexesString());
	}
	
	/*
	 * Returns the string of the indexes that comprise this mask
	 */
	public CharList getIndexesString() {
		CharList indexes = new CharList();
		for(int x = 0; x < getLetters().size(); x++) {
			if(getType() == TYPE_HORIZONTAL) {
				indexes.add(String.valueOf(this.get(x).getCol()).toCharArray());
			} else if(getType() == TYPE_VERTICAL) {
				indexes.add(String.valueOf(this.get(x).getRow()).toCharArray());
			}
			indexes.add(x == getLetters().size() - 1 ? ']' : ' ');
		}
		return indexes;
	}
	
	/*
	 * Returns the type's name
	 */
	public String getTypeString() {
		switch(type) {
			case TYPE_HORIZONTAL:
				return "H";
			case TYPE_VERTICAL:
				return "V";
		}
		return "Invalid type";
	}
	
	/*
	 * Sets the direction type of the mask
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	/*
	 * Returns the String value of the CharArray letters
	 */
	public String toString() {
		return getLetters().toString();
	}

	/*
	 * Gets the solutions for this mask
	 */
	public ArrayList<Solution> getSolutions() {
		return solutions;
	}

	/*
	 * Sets the solutions list for this mask
	 */
	public void setSolutions(ArrayList<Solution> solutions) {
		this.solutions = solutions;
	}
	
	/*
	 * Gets the direction type of the mask
	 */
	public int getType() {
		return type;
	}

	/*
	 * Removes the specified solution from the solution array
	 */
	public void clearSolution(Solution solution) {
		solutions.remove(solution);
		//solutions.set(solutions.indexOf(solution), null);
	}

	/*
	 * Return the index before the base word
	 */
	public int getBaseStart() {
		return baseStart;
	}

	/*
	 * Sets the index before the base word
	 */
	public void setBaseStart(int baseStart) {
		this.baseStart = baseStart;
	}

	/*
	 * Gets the index after the base word
	 */
	public int getBaseEnd() {
		return baseEnd;
	}

	/*
	 * Sets the index after the base word
	 */
	public void setBaseEnd(int baseEnd) {
		this.baseEnd = baseEnd;
	}
	
	/*
	 * Gets the size of the base word
	 */
	public int getBaseSize() {
		return getBaseEnd() - getBaseStart() + 1;
	}

	public void setBaseWord(CharList baseWord) {
		this.baseWord = baseWord;
	}

	public CharList getBaseWord() {
		return baseWord;
	}

	public int getRelativeStart() {
		return getBaseStart() - getFirstIndex();
	}

	public int getRelativeEnd() {
		return getBaseEnd() - getBaseStart();
	}
	
	public int getFirstIndex() {
		if(getType() == Mask.TYPE_HORIZONTAL)
			return getFirst().getCol();
		else if (getType() == Mask.TYPE_VERTICAL)
			return getFirst().getRow();
		return -8008;
	}
	
	public int getLastIndex() {
		if(getType() == Mask.TYPE_HORIZONTAL)
			return getLast().getCol();
		else if (getType() == Mask.TYPE_VERTICAL)
			return getLast().getRow();
		return -6009;
	}

	@Override
	public int compareTo(Mask mask2) {
		ArrayList<Solution> solution1 = getSolutions();
		ArrayList<Solution> solution2 = mask2.getSolutions();
		
		if(solution1.size() == 0 && solution2.size() == 0)
			return 0;
		else if(solution1.size() == 0)
			return 1;
		else if(solution2.size() == 0)
			return -1;
		
		int points1 = getSolutions().get(0).getPoints();
		int points2 = mask2.getSolutions().get(0).getPoints();
		
		if(points1 > points2)
			return -1;
		if(points1 < points2)
			return 1;
		return 0;
	}
}
