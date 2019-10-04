package containers;

import java.util.Stack;

public class CharStack extends Stack<Character> {
	public CharStack() {
		
	}
	
	public CharStack(String letters) {
		for(char c : letters.toCharArray())
			add(c);
	}

	public CharStack(char[] letters) {
		for(char c : letters)
			add(c);
	}

	public CharStack(CharStack wordStack) {
		addAll(wordStack);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(char c : this)
			sb.append(c);
		return sb.toString();
	}
}
