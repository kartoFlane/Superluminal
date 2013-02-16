package com.kartoflane.superluminal.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigIO {
	static FileWriter fw;
	static BufferedWriter out;
	static FileReader fr;
	static BufferedReader in;
	
	public static void saveConfig() {
		try {
			fw = new FileWriter("superluminal.ini");
			out = new BufferedWriter(fw);
			
			// === WRITE VARIABLES
		// === exporter
			out.write("exportPath = " + Main.exportPath + ShipIO.lineDelimiter);
			out.write("projectPath = " + Main.projectPath + ShipIO.lineDelimiter);
		// === browser
			out.write("dataPath = " + Main.dataPath + ShipIO.lineDelimiter);
			out.write("resPath = " + Main.resPath + ShipIO.lineDelimiter);
		// === edit
			out.write("removeDoor = " + Main.removeDoor + ShipIO.lineDelimiter);
			//out.write("snapMounts = " + Main.snapMounts + ShipIO.lineDelimiter);
			//out.write("snapMountsToHull = " + Main.snapMountsToHull + ShipIO.lineDelimiter);
			out.write("arbitraryPosOverride = " + Main.arbitraryPosOverride + ShipIO.lineDelimiter);
		// === view
			//out.write("showAnchor = " + Main.showAnchor + ShipIO.lineDelimiter);
			//out.write("showMounts = " + Main.showMounts + ShipIO.lineDelimiter);
			//out.write("showRooms = " + Main.showRooms + ShipIO.lineDelimiter);
			//out.write("showHull = " + Main.showHull + ShipIO.lineDelimiter);
			//out.write("showFloor = " + Main.showFloor + ShipIO.lineDelimiter);
			//out.write("showShield = " + Main.showShield + ShipIO.lineDelimiter);
			out.write("loadFloor = " + Main.loadFloor + ShipIO.lineDelimiter);
			out.write("loadShield = " + Main.loadShield + ShipIO.lineDelimiter);
			out.write("loadSystem = " + Main.loadSystem + ShipIO.lineDelimiter);
			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean configExists() {
		File f = new File("superluminal.ini");
		return f.exists();
	}
	
	/**
	 * Reads and returns the value of specified variable as a string.
	 * 
	 * @param name Name of the variable that the function should look for.
	 * @return Value of variable passed in argument.
	 */
	public static String scourFor(String name) {
		String s = null;
		Pattern pattern;
		Matcher matcher;
		
		try {
			fr = new FileReader("superluminal.ini");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		Scanner sc = new Scanner(fr);
		sc.useDelimiter(Pattern.compile("[\\n]"));
		pattern = Pattern.compile("(.*?)( = )(.*?)$");
scan:	while(sc.hasNext()) {
			s = sc.next();
			matcher = pattern.matcher(s);
			if (matcher.find()) {
				if (matcher.group(1).contains(name)) {
					s = matcher.group(3);
					break scan;
				}
			}
		}
		
		sc.close();

		return s;
	}

	public static boolean getBoolean(String name) {
		return Boolean.valueOf(ConfigIO.scourFor(name));
	}
	public static int getInt(String name) {
		return Integer.valueOf(ConfigIO.scourFor(name));
	}
}
