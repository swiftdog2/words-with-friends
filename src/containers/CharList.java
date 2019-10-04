package containers;

import java.util.ArrayList;

import util.Constants;

public class CharList extends ArrayList<Character> {
	private static final long serialVersionUID = -211663257387137972L;

	/*
	 * Copy Constructor
	 */
	public CharList(CharList chars) {
		addAll(chars);
	}

	public CharList(char[] chars) {
		for(int x = 0; x < chars.length; x++)
			add(chars[x]);
	}

	public CharList() {
		
	}
	
	public CharList(String string) {
		this(string.toCharArray());
	}

	/*
	 * Returns true if the letter at the specified index is empty
	 */
	public boolean isEmpty(int index) {
		return get(index) == Constants.NULL_CHAR;
	}
	
	/*
	 * Returns if the chars in each CharArray are equivalent
	 */
	public boolean equals(CharList array) {
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
		for(char c : this)
			sb.append(c == Constants.NULL_CHAR ? "-" : c);
		return sb.toString();
	}
	
	public void add(char[] charArray) {
		addAll(new CharList(charArray));
	}
}
