package containers;

import java.util.ArrayList;

public class IntList extends ArrayList<Integer> {
	private static final long serialVersionUID = 7291005504607627831L;

	/*
	 * Returns if the chars in each CharArray are equivalent
	 */
	public boolean equals(IntList array) {
		if(array == null)
			return false;

		if(size() != array.size())
			return false;
		
		for(int x = 0; x < size(); x++)
			if(get(x) != array.get(x))
				return false;
		
		return true;
	}
	
	/*
	 * Override methods
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Integer i : this)
			sb.append(i);
		return sb.toString();
	}
}
