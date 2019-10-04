package containers;
import util.Constants;

public class Node {
	private char value;
	private Node[] children;
	private boolean terminal;
	
	/*
	 * Constructors
	 */
	public Node() {
		children = new Node[26];
	}
	
	public Node(char value) {
		this();
		this.value = value;
	}
	
	/*
	 * Gets the letter value of this Node
	 */
	public char getValue() {
		return value;
	}
	
	/*
	 * Gets the child Node at the char index
	 */
	public Node getChild(char value) {
		return children[charIndex(value)];
	}

	/*
	 * Sets the child Node at the specified index
	 */
	public void setChild(Node child) {
		children[charIndex(child.getValue())] = child;
	}
	
	/*
	 * Returns the children this node has
	 */
	public Node[] getChildren() {
		return children;
	}

	/*
	 * Denotes that this Node is the end of a word
	 */
	public void setTerminates() {
		terminal = true;
	}
	
	/*
	 * Returns true if the node is terminal
	 */
	public boolean isTerminal() {
		return terminal;
	}
	
	/*
	 * Returns the relative character index
	 */
	public static int charIndex(char letter) {
		return letter - Constants.CHAR_START;
	}
}
