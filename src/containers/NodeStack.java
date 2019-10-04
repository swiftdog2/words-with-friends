package containers;

import java.util.Stack;

public class NodeStack extends Stack<Node> {
	public NodeStack() {
		
	}
	
	public NodeStack(Node[] nodes) {
		for(Node node : nodes)
			add(node);
	}

	public NodeStack(NodeStack wordStack) {
		addAll(wordStack);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int x = 0; x < this.size(); x++)
			sb.append(this.get(x).getValue());
		return sb.toString();
	}
}
