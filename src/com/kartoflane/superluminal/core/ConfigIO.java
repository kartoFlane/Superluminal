package com.kartoflane.superluminal.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigIO {
	static FileWriter fw;
	static BufferedWriter out;
	static FileReader fr;
	static BufferedReader in;
	
	public static void saveConfig() {
		if (Main.propertiesSwitch) {
			// settings using Java Properties class
			Properties properties = new Properties();
			FileReader fr = null;
			try {
				fr = new FileReader("superluminal.ini");
				properties.load(fr);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fr != null)
						fr.close();
				} catch (IOException e1) {
				}
			}

			properties.setProperty("exportPath", Main.exportPath);
			properties.setProperty("projectPath", Main.projectPath);
			properties.setProperty("installPath", Main.installPath);
			properties.setProperty("dataPath", Main.dataPath);
			properties.setProperty("resPath", Main.resPath);
			properties.setProperty("removeDoor", new Boolean(Main.removeDoor).toString());
			properties.setProperty("arbitraryPosOverride", new Boolean(Main.arbitraryPosOverride).toString());
			properties.setProperty("forbidBossLoading", new Boolean(Main.forbidBossLoading).toString());
			properties.setProperty("shownIncludeWarning", new Boolean(Main.shownIncludeWarning).toString());
			properties.setProperty("showTips", new Boolean(Main.showTips).toString());
			properties.setProperty("enableZeroRooms", new Boolean(Main.enableZeroRooms).toString());
			
			try {
				fw = new FileWriter("superluminal.ini");
				properties.store(fw, "");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fw != null)
						fw.close();
				} catch (IOException e1) {
				}
			}
		} else {
			// settings using writing directly to a file
			try {
				fw = new FileWriter("superluminal.ini");
				out = new BufferedWriter(fw);
	
				// === WRITE VARIABLES
			// === exporter
				out.write("exportPath = " + Main.exportPath + ShipIO.lineDelimiter);
				out.write("projectPath = " + Main.projectPath + ShipIO.lineDelimiter);
				out.write("installPath = " + Main.installPath + ShipIO.lineDelimiter);
			// === browser
				out.write("dataPath = " + Main.dataPath + ShipIO.lineDelimiter);
				out.write("resPath = " + Main.resPath + ShipIO.lineDelimiter);
			// === edit
				out.write("removeDoor = " + Main.removeDoor + ShipIO.lineDelimiter);
				out.write("arbitraryPosOverride = " + Main.arbitraryPosOverride + ShipIO.lineDelimiter);
				out.write("enableZeroRooms = " + Main.enableZeroRooms + ShipIO.lineDelimiter);
			// === view
				out.write("forbidBossLoading = " + Main.forbidBossLoading + ShipIO.lineDelimiter);
				out.write("shownIncludeWarning = " + Main.shownIncludeWarning + ShipIO.lineDelimiter);
				out.write("showTips = " + Main.showTips + ShipIO.lineDelimiter);
				
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
		sc.useDelimiter(Pattern.compile(ShipIO.lineDelimiter));
		pattern = Pattern.compile("(.*?)(\\s*=\\s*)(.*?)$");
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
