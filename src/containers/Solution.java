package containers;

public class Solution extends CharList implements Comparable<Solution> {
	private static final long serialVersionUID = -5347360551912700985L;
	/*
	 * Instance variables
	 */
	private int points = 0;
	
	/*
	 * Constructors
	 */
	public Solution(CharList chars) {
		super(chars);
	}

	/*
	 * Gets the point value of this solution
	 */
	public int getPoints() {
		return points;
	}
	
	public void setPoints(int points) {
		this.points += points;
	}
	
	/*
	 * Compares two solutions by point value
	 */
	@Override
	public int compareTo(Solution solution2) {
		if (getPoints() > solution2.getPoints())
			return -1;
		else if (getPoints() < solution2.getPoints())
			return 1;
		return 0;
	}
}
