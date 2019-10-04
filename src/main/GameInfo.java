package main;

public class GameInfo {
	private String name;
	private int id;
	private int size;
	
	public GameInfo(int id, String name, int size, String data) {
		this.id = id;
		this.name = name;
		this.size = size;
	}
	
	public String toString() {
		return id + ": '" + name + "' (size " + size + ")";
	}
}
