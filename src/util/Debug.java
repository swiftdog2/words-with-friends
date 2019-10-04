package util;

import containers.Mask;

public class Debug {
	public static final int VERSION = 5;

	/*
	 * Debug flags
	 */
	public static boolean SCAN = true;
	public static boolean SOLVING = true;
	public static boolean SOLVED = true;
	public static boolean VERIFYING = true;
	public static boolean VERIFYING2 = true;
	public static boolean SCORING = true;
	public static boolean POINTS = true;
	public static boolean PRINT_ALL_POINTS = true;

	public static final boolean GRAPH = true;
	
	public static void outln(boolean DEBUG, String s) {
		if(DEBUG)
			System.out.println(s);
	}
	
	public static void outln(boolean DEBUG, Mask mask) {
		if(DEBUG)
			mask.print();
	}
	
	public static void out(boolean DEBUG, String s) {
		if(DEBUG)
			System.out.print(s);
	}
}
