package com.kartoflane.superluminal.core;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import com.kartoflane.superluminal.elements.ExportProgress;
import com.kartoflane.superluminal.elements.FTLDoor;
import com.kartoflane.superluminal.elements.FTLItem;
import com.kartoflane.superluminal.elements.FTLMount;
import com.kartoflane.superluminal.elements.FTLRoom;
import com.kartoflane.superluminal.elements.FTLShip;
import com.kartoflane.superluminal.elements.Slide;
import com.kartoflane.superluminal.elements.Systems;
import com.kartoflane.superluminal.ui.ShipBrowser;



public class ShipIO
{
	public static Set<String> errors = new HashSet<String>();
	public static Set<String> playerBlueprintNames = new TreeSet<String>();
	public static Set<String> enemyBlueprintNames = new TreeSet<String>();
	static Set<String> otherBlueprintNames = new TreeSet<String>();
	public static Map<String, String> playerShipNames = new HashMap<String, String>();
	public static Map<String, String> enemyShipNames = new HashMap<String, String>();
	static Map<String, String> otherShipNames = new HashMap<String, String>();
	public static Map<String, FTLItem> weaponMap = new HashMap<String, FTLItem>();
	public static Map<String, FTLItem> droneMap = new HashMap<String, FTLItem>();
	public static Map<String, FTLItem> augMap = new HashMap<String, FTLItem>();
	public static Map<String, HashSet<String>> weaponSetMap = new HashMap<String, HashSet<String>>();
	public static Map<String, HashSet<String>> droneSetMap = new HashMap<String, HashSet<String>>();
	
	static FTLShip shipBeingLoaded;
	public static String pathDelimiter = System.getProperty("file.separator");
	public static String lineDelimiter = System.getProperty("line.separator");
	static boolean namesFetched = false;
	
	public static boolean createFtl = false;
	public static boolean deleteTemp = false;
	public static boolean dontCheck = false;
	
	/**
	 * false - loading using Image class
	 * true - loading using SWTResourceManager
	 */
	static boolean loadingSwitch = false;
	
	static final boolean IOdebug = false;
	
	// =================================================================
	
	public static boolean isNull(String path) {
		return path == null || (path != null && (path.equals("") || path.equals("null")));
	}
	
	public static void debug(String msg) {
		if (IOdebug) {
			System.out.println(msg);
		}
	}
	
// =======================
// === IMPORTING / LOADING

	/**
	 * Clears the tree list and updates it.
	 */
	public static void loadTree() {
		TreeItem blueprint;

		ShipBrowser.clearTrees();
		
		for (String s : playerBlueprintNames) {
			blueprint = new TreeItem(ShipBrowser.trtmPlayer, SWT.NONE);
			blueprint.setText(playerShipNames.get(s) + " (" + s + ")");
	    	ShipBrowser.ships.add(blueprint);
		}
		for (String s : enemyBlueprintNames) {
			blueprint = new TreeItem(ShipBrowser.trtmEnemy, SWT.NONE);
			blueprint.setText(enemyShipNames.get(s) + " (" + s + ")");
	    	ShipBrowser.ships.add(blueprint);
		}
		for (String s : otherBlueprintNames) {
			blueprint = new TreeItem(ShipBrowser.trtmOther, SWT.NONE);
			blueprint.setText(otherShipNames.get(s) + " (" + s + ")");
	    	ShipBrowser.ships.add(blueprint);
		}
	}
	
	/**
	 * Clears the blueprint and name maps, fills then anew and then reloads the tree list.
	 */
	public static void reloadBlueprints() {
		playerBlueprintNames.clear();
		enemyBlueprintNames.clear();
		otherBlueprintNames.clear();
		playerShipNames.clear();
		enemyShipNames.clear();
		otherShipNames.clear();
		
		fetchShipNames();
		loadTree();
	}
	
	/**
	 * Used for data preloading, loads ships to a map. Not used, TBS.
	 */
	public static void loadData() {
		if (Main.dataPreloading) {
			for (String blueprintName : playerBlueprintNames) {
				loadShip(blueprintName, false);
			}
			for (String blueprintName : enemyBlueprintNames) {
				loadShip(blueprintName, false);
			}
		}
	}
	
	/**
	 * Since using the scanner class there's no way of knowing what lies ahead in the file, I have to do a pre-scan of the file and count number of
	 * declarations for the given blueprint, so that I can then load the latest (modded default ships)
	 * @param blueprintName
	 * @return
	 */
	public static int preScan(String blueprintName, String fileName) {
		FileReader fr;
		Scanner sc = null;
		String s;
		Pattern pattern;
		Matcher matcher;
		int blueprintCount = 0;
		
		try {
			fr = new FileReader(Main.dataPath + pathDelimiter + fileName);
			sc = new Scanner(fr);
			sc.useDelimiter(Pattern.compile(lineDelimiter));
			
			pattern = Pattern.compile("(.*?)(\""+blueprintName+"\")(.*?)");
			
			while(sc.hasNext()) {
				s = sc.next();
				matcher = pattern.matcher(s);
				if (matcher.find()) blueprintCount++;
			}
		} catch (FileNotFoundException e) {
		} finally {
			if (sc!=null)
				sc.close();
		}
		
		return blueprintCount;
	}
	
	/**
	 * Loads the given ship.
	 * @param blueprintName The blueprint name of the ship that is to be loaded.
	 * @param asActive When set to false, loads the given ship as not active (i.e. it won't be displayed at the main screen, just loaded to the hashmap for easy access). 
	 * NOTE when data preloading is disabled (preferred option), this paramter won't matter and the given ship will always be loaded as active.
	 */
	public static void loadShip(String blueprintName, boolean asActive) {
		FileReader fr;
		Scanner sc;
		String s;
		Pattern pattern;
		Matcher matcher;
		TreeItem section;
		TreeItem ship;
		boolean ignoreNextTag = false;
		FTLRoom r;
		String sysName;
		int level, power, id;
		boolean systemList = false;
		
		int declarationCount = preScan(blueprintName, "autoBlueprints.xml");
		int declarationsOmitted = 0;
		
		if ((!Main.dataLoaded && Main.dataPreloading) || !Main.dataPreloading) {
			// scan autoBlueprints.xml for mention of the blueprintName, if there's one, load it.
			try {
				fr = new FileReader(Main.dataPath+pathDelimiter+"autoBlueprints.xml");
				sc = new Scanner(fr);
				sc.useDelimiter(Pattern.compile(lineDelimiter));
				
scan:			while(sc.hasNext()) {
					s = sc.next();
					if (s.contains("REBELS")) {
ship:					while(sc.hasNext()) {
							s = sc.next();
							if (s.contains("<!--")) ignoreNextTag = true;
							pattern = Pattern.compile("(.*)(<shipBlueprint name=\")(.*?)(\" layout=\")(.*?)(\" img=\")(.*?)(\">)");
							matcher = pattern.matcher(s);
							
							if (matcher.find() && matcher.group(3).equals(blueprintName) && declarationsOmitted < declarationCount) {
								declarationsOmitted++;
								
								if (!ignoreNextTag && declarationsOmitted == declarationCount) {
									shipBeingLoaded = new FTLShip();
									shipBeingLoaded.blueprintName = blueprintName;
									shipBeingLoaded.isPlayer = false;
									
								// === LOAD SHIP LAYOUT
									shipBeingLoaded.layout = matcher.group(5);
									loadShipLayout();
									
								// === LOAD SHIP IMAGE		
									shipBeingLoaded.imageName = matcher.group(7);
									shipBeingLoaded.imagePath = Main.resPath + pathDelimiter + "img" + pathDelimiter + "ship" + pathDelimiter + shipBeingLoaded.imageName + "_base.png";
									shipBeingLoaded.cloakPath = Main.resPath + pathDelimiter + "img" + pathDelimiter + "ship" + pathDelimiter + shipBeingLoaded.imageName + "_cloak.png";
									shipBeingLoaded.floorPath = Main.resPath + pathDelimiter + "img" + pathDelimiter + "ship" + pathDelimiter + shipBeingLoaded.imageName + "_floor.png";
									shipBeingLoaded.shieldPath = Main.resPath + pathDelimiter + "img" + pathDelimiter + "ship" + pathDelimiter + "enemy_shields.png";
									loadShipImages(shipBeingLoaded);
				
									while(!s.contains("</shipBlueprint>") && !s.contains("</ship>")) {
										s = sc.next();
										
										pattern = Pattern.compile("(<class>)(.*?)(</class>)");
										matcher = pattern.matcher(s);
										if (matcher.find()) {
											shipBeingLoaded.shipClass = matcher.group(2);
										}
	
										pattern = Pattern.compile("(<systemList>)");
										matcher = pattern.matcher(s);
										if (matcher.find()) {
											while(!s.contains("</systemList>")) {
												s = sc.next();
												if (s.contains("<!--")) ignoreNextTag = true;
												pattern = Pattern.compile("(.*?<)(.*?)( power=\")(.*?)(\")(.*?)(room=\")(.*?)(\".*?)");
												matcher = pattern.matcher(s);
												if (matcher.find() && !ignoreNextTag) {
													power = Integer.valueOf(matcher.group(4));
													id = Integer.valueOf(matcher.group(8));
													sysName = matcher.group(2).toUpperCase();
													
													pattern = Pattern.compile("(.*?)(max=\")(.*?)(\".*?)");
													matcher = pattern.matcher(s);
													if (matcher.find()) {
														level = Integer.valueOf(matcher.group(3));
													} else {
														level = power;
													}
													
													r = shipBeingLoaded.getRoomWithId(id);
													if (r != null) {
														r.sys = Systems.valueOf(sysName);
														
														shipBeingLoaded.levelMap.put(r.sys, level);
														shipBeingLoaded.powerMap.put(r.sys, power);
									    				shipBeingLoaded.startMap.put(r.sys, true);
													} else {
														errors.add("Error: load ship - rooms with specified ID not found. Some systems' data may be missing.");
													}
												}
												if (s.contains("-->")) ignoreNextTag = false;
											}
										}
										
									// === armaments declaration type 1
										// weapons list
										pattern = Pattern.compile("(<weaponList missiles=\")(.*?)(\" load=\")(.*?)(\"/>)");
										matcher = pattern.matcher(s);
										if (matcher.find()) {
											shipBeingLoaded.weaponsBySet = true;
											
											shipBeingLoaded.weaponSet.add(matcher.group(4));
											shipBeingLoaded.missiles = Integer.valueOf(matcher.group(2));
										}
	
										// drone list
										pattern = Pattern.compile("(<droneList drones=\")(.*?)(\" load=\")(.*?)(\"/>)");
										matcher = pattern.matcher(s);
										if (matcher.find()) {
											shipBeingLoaded.dronesBySet = true;
											
											shipBeingLoaded.droneSet.add(matcher.group(4));
											shipBeingLoaded.drones = Integer.valueOf(matcher.group(2));
										}
										
									// === armaments declaration type 2
										// weapons list
										pattern = Pattern.compile("(<weaponList count=\")(.*?)(\" missiles=\")(.*?)(\">)");
										matcher = pattern.matcher(s);
										if (matcher.find()) {
											shipBeingLoaded.weaponsBySet = false;
											
											shipBeingLoaded.missiles = Integer.valueOf(matcher.group(4));
											shipBeingLoaded.weaponCount = Integer.valueOf(matcher.group(2));
											while(!s.contains("</weaponList>")) {
												s = sc.next();
	
												if (s.contains("<!--")) ignoreNextTag = true;
	
												pattern = Pattern.compile("(<weapon name=\")(.*?)(\"/>)");
												matcher = pattern.matcher(s);
												if (matcher.find() && !ignoreNextTag) {
													shipBeingLoaded.weaponSet.add(matcher.group(2));
												}
												
												if (s.contains("-->")) ignoreNextTag = false;
											}
										}
										
									// drone list
										pattern = Pattern.compile("(<droneList count=\")(.*?)(\" drones=\")(.*?)(\">)");
										matcher = pattern.matcher(s);
										if (matcher.find()) {
											shipBeingLoaded.dronesBySet = false;
											
											shipBeingLoaded.drones = Integer.valueOf(matcher.group(4));
											shipBeingLoaded.droneCount = Integer.valueOf(matcher.group(2));
											while(!s.contains("</droneList>")) {
												s = sc.next();
	
												if (s.contains("<!--")) ignoreNextTag = true;
	
												pattern = Pattern.compile("(<drone name=\")(.*?)(\"/>)");
												matcher = pattern.matcher(s);
												if (matcher.find() && !ignoreNextTag) {
													shipBeingLoaded.droneSet.add(matcher.group(2));
												}
												
												if (s.contains("-->")) ignoreNextTag = false;
											}
										}
										
									// slots
										pattern = Pattern.compile("(<weaponSlots>)(.*?)(</weaponSlots>)");
										matcher = pattern.matcher(s);
										if (matcher.find())
											shipBeingLoaded.weaponSlots = Integer.valueOf(matcher.group(2));
	
										pattern = Pattern.compile("(<droneSlots>)(.*?)(</droneSlots>)");
										matcher = pattern.matcher(s);
										if (matcher.find())
											shipBeingLoaded.droneSlots = Integer.valueOf(matcher.group(2));
										
									// hull health
										pattern = Pattern.compile("(<health amount=\")(.*?)(\"/>)");
										matcher = pattern.matcher(s);
										if (matcher.find()) {
											shipBeingLoaded.hullHealth = Integer.valueOf(matcher.group(2));
										}
										
									// power
										pattern = Pattern.compile("(<maxPower amount=\")(.*?)(\"/>)");
										matcher = pattern.matcher(s);
										if (matcher.find()) {
											shipBeingLoaded.reactorPower = Integer.valueOf(matcher.group(2));
										}
										
									// crew
										pattern = Pattern.compile("(<crewCount amount.*?=.*?\")(.*?)(\" max=\")(.*?)(\" class=\")(.*?)(\"/>)");
										matcher = pattern.matcher(s);
										if (matcher.find()) {
											shipBeingLoaded.crewMax = Integer.valueOf(matcher.group(4));
											shipBeingLoaded.crewMap.put(matcher.group(6), Integer.valueOf(matcher.group(2)));
										}
									
									// augments
										pattern = Pattern.compile("(<aug name=\")(.*?)(\"/>)");
										matcher = pattern.matcher(s);
										if (matcher.find()) {
											shipBeingLoaded.augmentSet.add(matcher.group(2));
										}
									// overrides
										pattern = Pattern.compile("(<shieldImage>)(.*?)(/shieldImage>)");
										matcher = pattern.matcher(s);
										if (matcher.find()) {
											shipBeingLoaded.shieldOverride = Main.resPath + pathDelimiter + "img" + pathDelimiter + "ship" + pathDelimiter + matcher.group(2) + "shield1.png";
										}
										
										// TODO boarding AI ?
									}
									break ship;
								}
							}
							if (s.contains("-->")) ignoreNextTag = false;
						}
					break scan;
					}
				}
				
				sc.close();
			} catch (FileNotFoundException e) {
				errors.add("Error: load ship - autoBlueprints.xml file not found.");
			} catch (NoSuchElementException e) {
				errors.add("Error: load ship - end of file reached - probably does not contain valid ship blueprints.");
			}
			
			
			// if no matching blueprint name was found in the first file, search the blueprints.xml
			if (shipBeingLoaded == null) {
				
				declarationCount = preScan(blueprintName, "blueprints.xml");
				declarationsOmitted = 0;
				
				try {
					fr = new FileReader(Main.dataPath+pathDelimiter+"blueprints.xml");
					sc = new Scanner(fr);
					sc.useDelimiter(Pattern.compile(lineDelimiter));

					pattern = Pattern.compile("(<shipBlueprint name=\")(.*?)(\" layout=\")(.*?)(\" img=\")(.*?)(\">)");
scan:				while(sc.hasNext()) {
						s = sc.next();
						
						if (s.contains("<!--")) ignoreNextTag = true;
						
						matcher = pattern.matcher(s);
					// load section names
						if (matcher.find() && matcher.group(2).equals(blueprintName) && declarationsOmitted < declarationCount) {
							declarationsOmitted++;
							
							if (!ignoreNextTag && declarationsOmitted == declarationCount) {
								shipBeingLoaded = new FTLShip();
								shipBeingLoaded.blueprintName = blueprintName;
								shipBeingLoaded.isPlayer = true;
								
								// create tree nodes for section
								section = new TreeItem(ShipBrowser.trtmPlayer, SWT.NONE);
								section.setText(matcher.group(2));
								
					    		// create tree nodes for ships
						    	ship = new TreeItem(section, SWT.NONE);
						    	ship.setText(matcher.group(2));
						    	
						    	ShipBrowser.ships.add(ship);
						    	
						    // === LOAD SHIP LAYOUT
								shipBeingLoaded.layout = matcher.group(4);
								loadShipLayout();
								
							// === LOAD SHIP IMAGES
								shipBeingLoaded.imageName = matcher.group(6);
								shipBeingLoaded.imagePath = Main.resPath + pathDelimiter + "img" + pathDelimiter + "ship" + pathDelimiter + shipBeingLoaded.imageName + "_base.png";
								shipBeingLoaded.cloakPath = Main.resPath + pathDelimiter + "img" + pathDelimiter + "ship" + pathDelimiter + shipBeingLoaded.imageName + "_cloak.png";
								shipBeingLoaded.floorPath = Main.resPath + pathDelimiter + "img" + pathDelimiter + "ship" + pathDelimiter + shipBeingLoaded.imageName + "_floor.png";
								shipBeingLoaded.miniPath = Main.resPath + pathDelimiter + "img" + pathDelimiter + "customizeUI" + pathDelimiter + "miniship_" + shipBeingLoaded.imageName + ".png";
								
								sysName = shipBeingLoaded.imageName;
								if (sysName.contains("_2")) sysName = sysName.substring(0, sysName.lastIndexOf('_')); 
								shipBeingLoaded.shieldPath = Main.resPath + pathDelimiter + "img" + pathDelimiter + "ship" + pathDelimiter + sysName + "_shields1.png";
								loadShipImages(shipBeingLoaded);
								
							    while(!s.contains("</shipBlueprint>") && sc.hasNext()) {
							    	s = sc.next();
							    	
									if (s.contains("<!--")) ignoreNextTag = true;
		
									if (!ignoreNextTag) {
								    	if (isNull(shipBeingLoaded.shipClass)) {
									    	pattern = Pattern.compile(".*?(<class>)(.*?)(</class>.*?)");
									    	matcher = pattern.matcher(s);
									    	if (matcher.find()) shipBeingLoaded.shipClass = matcher.group(2);
								    	}
								    	if (isNull(shipBeingLoaded.shipName)) {
								    		pattern = Pattern.compile(".*?(<name>)(.*?)(</name>)");
								    		matcher = pattern.matcher(s);
								    		if (matcher.find()) shipBeingLoaded.shipName = matcher.group(2);
								    	}
								    	if (isNull(shipBeingLoaded.descr)) {
								    		pattern = Pattern.compile(".*?(<desc>)(.*?)(</desc>)");
								    		matcher = pattern.matcher(s);
								    		if (matcher.find()) shipBeingLoaded.descr = matcher.group(2);
								    	}
								    	if (!systemList && s.contains("<systemList>")) {
											systemList = true;
								    		while (!s.contains("</systemList>")) {
								    			s = sc.next();
								    			
												if (s.contains("<!--")) ignoreNextTag = true;
								    			
								    			if (!s.contains("artillery") && !ignoreNextTag) {
									    			pattern = Pattern.compile("(.*?)(<)(.*?)( power=\")(.*?)(\" room=\")(.*?)(\" start=\")(.*?)(\")(.*?)(>|/>)");
									    			matcher = pattern.matcher(s);
									    			if (matcher.find()) {
									    				debug(matcher.group(3) + " - looking for ID: "+matcher.group(7));
									    				r = shipBeingLoaded.getRoomWithId(Integer.valueOf(matcher.group(7)));
									    				r.sys = Systems.valueOf(matcher.group(3).toUpperCase());
		
										    			shipBeingLoaded.levelMap.put(r.sys, Integer.valueOf(matcher.group(5)));
									    				shipBeingLoaded.powerMap.put(r.sys, Integer.valueOf(matcher.group(5)));
									    				shipBeingLoaded.startMap.put(r.sys, Boolean.valueOf(matcher.group(9)));
									    				
									    				String str = matcher.group(12);
									    				s = matcher.group(11);
										    			pattern = Pattern.compile("(img=\")(.*?)(\")");
										    			matcher = pattern.matcher(s);
									    				if (matcher.find()) {
									    					r.img = matcher.group(2);
									    					r.img = Main.resPath + pathDelimiter + "img" + pathDelimiter + "ship" + pathDelimiter + "interior" + pathDelimiter + r.img + ".png";
									    					File f = new File(r.img);
									    					if (Main.loadSystem) {
									    						if (loadingSwitch) { 
									    							r.sysImg = SWTResourceManager.getImage(r.img);
									    						} else if (f.exists()) {
									    							r.sysImg = new Image(Main.shell.getDisplay(), r.img);
									    						} else {
									    							errors.add("Error: load system images - system image not found.");
									    						}
									    					}
									    				} else if (!r.sys.equals(Systems.TELEPORTER) && !r.sys.equals(Systems.EMPTY)) {
									    					// load default sysImg for the room (teleporter doesn't have default graphic)
									    					if (Main.loadSystem) {
										    					r.img = "room_"+r.sys.toString().toLowerCase();
										    					r.img = Main.resPath + pathDelimiter + "img" + pathDelimiter + "ship" + pathDelimiter + "interior" + pathDelimiter + r.img + ".png";
										    					File f = new File(r.img);
									    						if (loadingSwitch) { 
									    							r.sysImg = SWTResourceManager.getImage(r.img);
									    						} else if (f.exists()) {
									    							r.sysImg = new Image(Main.shell.getDisplay(), r.img);
									    						} else {
									    							errors.add("Error: load system images - system image not found.");
									    						}
									    					}
									    				}
									    				
									    				if (str.equals(">")) {
									    					s = sc.next(); // <slot>
									    					s = sc.next(); // <direction>
									    					pattern = Pattern.compile("(.*?)(<direction>)(.*?)(</direction>)");
									    					matcher = pattern.matcher(s);
									    					if (matcher.find()) {
									    						r.dir = Slide.valueOf(matcher.group(3).toUpperCase());
										    					s = sc.next(); // <number>
									    					}
									    					pattern = Pattern.compile("(.*?)(<number>)(.*?)(</number>)");
									    					matcher = pattern.matcher(s);
									    					if (matcher.find()) {
									    						r.slot = Integer.valueOf(matcher.group(3));
									    					}
									    				} else if (r.sys.equals(Systems.WEAPONS) || r.sys.equals(Systems.SHIELDS) || r.sys.equals(Systems.ENGINES) || r.sys.equals(Systems.PILOT) || r.sys.equals(Systems.MEDBAY)) {
									    					// get defaults
									    					r.slot = FTLRoom.getDefaultSlot(r.sys);
									    					r.dir = FTLRoom.getDefaultDir(r.sys);
									    				}
									    			}
								    			} else if (!ignoreNextTag) {
									    			pattern = Pattern.compile("(.*?)(<)(.*?)( power=\")(.*?)(\" room=\")(.*?)(\")(.*?)(>|/>)");
									    			matcher = pattern.matcher(s);
									    			if (matcher.find()) {
									    				r = shipBeingLoaded.getRoomWithId(Integer.valueOf(matcher.group(7)));
									    				r.sys = Systems.valueOf(matcher.group(3).toUpperCase());
									    				
										    			shipBeingLoaded.levelMap.put(r.sys, Integer.valueOf(matcher.group(5)));
									    				shipBeingLoaded.powerMap.put(r.sys, Integer.valueOf(matcher.group(5)));
									    			}
								    			}
								    			
												if (s.contains("-->")) ignoreNextTag = false;
								    		}
								    	}
		
									// weapons list
										pattern = Pattern.compile("(<weaponList count=\")(.*?)(\" missiles=\")(.*?)(\">)");
										matcher = pattern.matcher(s);
										if (matcher.find()) {
											shipBeingLoaded.weaponsBySet = false;
											
											shipBeingLoaded.missiles = Integer.valueOf(matcher.group(4));
											shipBeingLoaded.weaponCount = Integer.valueOf(matcher.group(2));
											while(!s.contains("</weaponList>")) {
												s = sc.next();
		
												if (s.contains("<!--")) ignoreNextTag = true;
		
												pattern = Pattern.compile("(<weapon name=\")(.*?)(\"/>)");
												matcher = pattern.matcher(s);
												if (matcher.find() && !ignoreNextTag) {
													shipBeingLoaded.weaponSet.add(matcher.group(2));
												}
												
												if (s.contains("-->")) ignoreNextTag = false;
											}
										}
										
									// drone list
										pattern = Pattern.compile("(<droneList count=\")(.*?)(\" drones=\")(.*?)(\">)");
										matcher = pattern.matcher(s);
										if (matcher.find()) {
											shipBeingLoaded.dronesBySet = false;
											
											shipBeingLoaded.drones = Integer.valueOf(matcher.group(4));
											shipBeingLoaded.droneCount = Integer.valueOf(matcher.group(2));
											while(!s.contains("</droneList>")) {
												s = sc.next();
		
												if (s.contains("<!--")) ignoreNextTag = true;
		
												pattern = Pattern.compile("(<drone name=\")(.*?)(\"/>)");
												matcher = pattern.matcher(s);
												if (matcher.find() && !ignoreNextTag) {
													shipBeingLoaded.droneSet.add(matcher.group(2));
												}
												
												if (s.contains("-->")) ignoreNextTag = false;
											}
										}
								    	
									// crew
								    	pattern = Pattern.compile(".*?(<crewCount amount.*?=.*?\")(.*?)(\" class=\")(.*?)(\"/>.*?)");
								    	matcher = pattern.matcher(s);
								    	if (matcher.find()) {
								    		shipBeingLoaded.crewMap.put(matcher.group(4), Integer.valueOf(matcher.group(2)));
								    	}
										
									// augments
										pattern = Pattern.compile("(<aug name=\")(.*?)(\"/>)");
										matcher = pattern.matcher(s);
										if (matcher.find()) {
											shipBeingLoaded.augmentSet.add(matcher.group(2));
										}
										
									// overrides
										pattern = Pattern.compile("(<shieldImage>)(.*?)(/shieldImage>)");
										matcher = pattern.matcher(s);
										if (matcher.find()) {
											shipBeingLoaded.shieldOverride = Main.resPath + pathDelimiter + "img" + pathDelimiter + "ship" + pathDelimiter + matcher.group(2) + "shield1.png";
										}
									}
									
									if (s.contains("-->")) ignoreNextTag = false;
							    }
							    break scan;
							}
						}
						if (s.contains("-->")) ignoreNextTag = false;
					}
					sc.close();
				} catch (FileNotFoundException e) {
					errors.add("Error: load ship - blueprints.xml file not found.");
				}
			}
		} else if (Main.dataPreloading) {
			// if the data was already loaded, then retrieve it from the hashmap
			shipBeingLoaded = (FTLShip) Main.loadedData.get(blueprintName);
		}
		
		if (shipBeingLoaded != null) {
			Point p = shipBeingLoaded.computeShipSize();
			p = new Point( 35 * Math.round((Main.GRID_W*35 - p.x)/70 - shipBeingLoaded.offset.x) , 35 * Math.round((Main.GRID_H*35 - p.y)/70 - shipBeingLoaded.offset.y) );
	        shipBeingLoaded.updateElements(p, FTLShip.AxisFlag.BOTH);
			shipBeingLoaded.anchor = p;
			shipBeingLoaded.imageRect.x += shipBeingLoaded.anchor.x;
			shipBeingLoaded.imageRect.y += shipBeingLoaded.anchor.y;
			
			for (FTLMount m : shipBeingLoaded.mounts) {
				m.rect.x = shipBeingLoaded.imageRect.x + m.pos.x;
				m.rect.y = shipBeingLoaded.imageRect.y + m.pos.y;
				m.rect.x -= (m.rotate) ? (FTLMount.MOUNT_WIDTH/2) : (FTLMount.MOUNT_HEIGHT/2);
				m.rect.y -= (m.rotate) ? (FTLMount.MOUNT_HEIGHT/2) : (FTLMount.MOUNT_WIDTH/2);
			}
			
	        if (Main.dataPreloading) Main.loadedData.put(shipBeingLoaded.blueprintName, shipBeingLoaded);
	        
	        if ((Main.dataPreloading && asActive) || !Main.dataPreloading) {
	        	Main.ship = shipBeingLoaded;
	        	fixImgNames();
	        	loadWeaponImages(Main.ship);
	        }
	        shipBeingLoaded = null;
		}
	}

	/**
	 * Loads the layout declared for the ship that is currently being loaded.
	 */
	public static void loadShipLayout() {
		FileReader filer;
		Scanner scanner;
		String s;
		int i = 0;
		FTLRoom room;
		FTLDoor door;
		Rectangle ellipse;
		boolean foundSection = false;
		
		Pattern pattern;
		Matcher matcher;

		FTLShip ship = shipBeingLoaded;
		
	// === LOAD TXT FILE
		
	    try {
	    	filer = new FileReader(Main.dataPath + pathDelimiter + ship.layout + ".txt");
	    	scanner = new Scanner(filer);
	    	
	    	while(scanner.hasNext()) {
				s = scanner.next();
				if (s.equals("X_OFFSET")) {
					ship.offset.x = scanner.nextInt();
					debug("X_OFFSET section found: " + ship.offset.x);
					foundSection = true;
				} else if (s.equals("Y_OFFSET")) {
					ship.offset.y = scanner.nextInt();
					debug("Y_OFFSET section found: " + ship.offset.y);
					foundSection = true;
				} else if (s.equals("VERTICAL")) {
					ship.vertical = Integer.valueOf(scanner.next());
					debug("VERTICAL section found: " + ship.vertical);
					foundSection = true;
				} else if (s.equals("ELLIPSE")) {
					ellipse = new Rectangle(scanner.nextInt(),scanner.nextInt(),scanner.nextInt(),scanner.nextInt());
					// ellipse has format of (width, height, xoffset, yoffset), while Rectangle class has (x, y, width, height)
					// so we've got to sort it out
					ship.ellipse.x = ellipse.width;
					ship.ellipse.y = ellipse.height;
					ship.ellipse.width = ellipse.x;
					ship.ellipse.height = ellipse.y;

					debug("ELLIPSE section found: "+ellipse);
					foundSection = true;
				} else if (s.equals("ROOM")) {
					room = new FTLRoom(0,0,0,0);
					room.id = scanner.nextInt();
						
					i = scanner.nextInt();
					room.rect.x = ship.offset.x*35 + i*35;
					i = scanner.nextInt();
					room.rect.y = ship.offset.y*35 + i*35;
					i = scanner.nextInt();
					room.rect.width = i*35;
					i = scanner.nextInt();
					room.rect.height = i*35;
						
					ship.rooms.add(room);
					Main.idList.add(room.id);
					debug("ROOM: (" + room.id + ") " + room.rect);
					foundSection = true;
				} else if (s.equals("DOOR")) {
					door = new FTLDoor();
					
					i = scanner.nextInt();
					door.rect.x = ship.offset.x*35 + i*35;
					i = scanner.nextInt();
					door.rect.y = ship.offset.y*35 + i*35;
					scanner.nextInt(); scanner.nextInt();
					
					i = scanner.nextInt();
					door.horizontal = (i == 0) ? true : false;
					
					door.rect.x += ((door.horizontal) ? (2) : (-3));
					door.rect.y += ((door.horizontal) ? (-3) : (2));
					door.fixRectOrientation();
						
					ship.doors.add(door);
					debug("DOOR: (" + ((door.horizontal)?("HORIZONTAL"):("VERTICAL")) + ") " + door.rect.x/35 +", "+door.rect.y/35);
					foundSection = true;
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			errors.add("Error: load ship layout - txt - file not found.");
		} catch (InputMismatchException e) {
			errors.add("Error: load ship layout - txt - data structure differs from expected. File might be corrupt.");
		}
	    
    // === LOAD XML FILE

		try {
			filer = new FileReader(Main.dataPath + pathDelimiter + ship.layout + ".xml");
			scanner = new Scanner(filer);
			scanner.useDelimiter(Pattern.compile(lineDelimiter));

			while (scanner.hasNext()) {
				s = scanner.next();
				// regex groups: 2, 4, 6, 8
				if (ship.imageRect.width == 0 && ship.imageRect.height == 0) {
					pattern = Pattern.compile("(<img x=\")(.*?)(\" y=\")(.*?)(\" w=\")(.*?)(\" h=\")(.*?)(\"/>)");
					matcher = pattern.matcher(s);
					if (matcher.find()) {
						ship.imageRect = new Rectangle(0,0,0,0);
						ship.imageRect.x = ship.offset.x*35 + Integer.valueOf(matcher.group(2));
						ship.imageRect.y = ship.offset.y*35 + Integer.valueOf(matcher.group(4));
						ship.imageRect.width = Integer.valueOf(matcher.group(6));
						ship.imageRect.height = Integer.valueOf(matcher.group(8));
					}
				} else if (ship.mounts.size() == 0 && s.contains("<weaponMounts>")) {
					FTLMount m;
					ship.mounts = new LinkedList<FTLMount>();

					while (!s.contains("</weaponMounts>") && !s.contains("mounts testing")) {
						s = scanner.next();
						pattern = Pattern.compile("(<mount x=\")(.*?)(\" y=\")(.*?)(\" rotate=\")(.*?)(\" mirror=\")(.*?)(\" gib=\")(.*?)(\" slide=\")(.*?)(\"/>)");
						matcher = pattern.matcher(s);
						if (matcher.find()) {
							m = new FTLMount();
							m.pos.x = Integer.valueOf(matcher.group(2));
							m.pos.y = Integer.valueOf(matcher.group(4));
							m.rotate = Boolean.valueOf(matcher.group(6));
							m.mirror = Boolean.valueOf(matcher.group(8));
							m.gib = Integer.valueOf(matcher.group(10));
							m.slide = Slide.valueOf(matcher.group(12).toUpperCase());
							
							ship.mounts.add(m);
						}
					}
				} /* else if (e.contains("<explosion>") {
				} */ // TODO gibs reading
			}
			
			scanner.close();
		} catch (FileNotFoundException e) {
			errors.add("Error: load ship layout - xml - file not found.");
		} catch (InputMismatchException e) {
			errors.add("Error: load ship layout - xml - data structure differs from expected. File might be corrupt.");
		}
	    
		if (foundSection && ship != null) {
			Main.print(ship.layout + " loaded successfully.");
		} else {
			errors.add("Error: load ship layout - no matching section headers found, no data was loaded.");
		}
	}
	
	public static void loadShipImages(FTLShip ship) {
		if (Main.hullImage != null && !Main.hullImage.isDisposed() && !loadingSwitch)
			Main.hullImage.dispose();
		Main.hullImage = null;
		if (Main.floorImage != null && !Main.floorImage.isDisposed() && !loadingSwitch)
			Main.floorImage.dispose();
		Main.floorImage = null;
		if (Main.shieldImage != null && !Main.shieldImage.isDisposed() && !loadingSwitch)
			Main.shieldImage.dispose();
		Main.shieldImage = null;
		
		try {
			if (!isNull(Main.resPath) && ship != null) {
				if (loadingSwitch) {
					Main.hullImage = SWTResourceManager.getImage(ship.imagePath);
					// load floor
					if (ship.isPlayer && new File(ship.floorPath).exists() && Main.loadFloor)
						Main.floorImage = SWTResourceManager.getImage(ship.floorPath);
					// load shield
					if (Main.loadShield) {
						if (isNull(ship.shieldOverride)) {
							Main.shieldImage = SWTResourceManager.getImage(ship.shieldPath);
						} else {
							Main.shieldImage = SWTResourceManager.getImage(ship.shieldOverride);
						}
					}
				} else {
					if (new File(ship.imagePath).exists()) {
						Main.hullImage = new Image(Main.shell.getDisplay(), ship.imagePath);
					} else {
						errors.add("Error: load ship image - hull image not found.");
					}
					// load floor
					File f = new File(ship.floorPath);
					if (ship.isPlayer && f.exists() && Main.loadFloor) {
							Main.floorImage = new Image(Main.shell.getDisplay(), ship.floorPath);
					} else if (ship.isPlayer && !f.exists()) {
						errors.add("Error: load ship image - floor image not found.");
					}
					// load shield
					if (Main.loadShield) {
						if (isNull(ship.shieldOverride)) {
							f = new File(ship.shieldPath);
							if (f.exists()) {
								Main.shieldImage = new Image(Main.shell.getDisplay(), ship.shieldPath);
							} else {
								errors.add("Error: load ship image - shield image not found.");
							}
						} else {
							f = new File(ship.shieldOverride);
							Main.debug(ship.shieldOverride);
							if (f.exists()) {
								Main.shieldImage = new Image(Main.shell.getDisplay(), ship.shieldOverride);
							} else {
								errors.add("Error: load ship image - shield override image not found.");
							}
						}
					}
				}
			} else {
				errors.add("Error: load ship image - resource path is not set, unable to find images.");
			}
		} catch (SWTException e) {
			errors.add("Error: load ships images - one of the ship's images could not be found.");
			if (Main.hullImage == null) Main.ship.imagePath = null;
			if (Main.floorImage == null) Main.ship.floorPath = null;
			if (Main.shieldImage == null) Main.ship.shieldPath = null;
			if (Main.cloakImage == null) Main.ship.cloakPath = null;
		}
	}
	
	public static void loadImage(String path, String mod) {
		File f = null;
		// === Hull
		if (mod.equals("hull")) {
			if (!loadingSwitch && Main.hullImage != null && !Main.hullImage.isDisposed() && !loadingSwitch)
				Main.hullImage.dispose();
			Main.hullImage = null;
			
			Main.ship.imageName = path.substring(path.lastIndexOf(pathDelimiter)+1, path.lastIndexOf("_"));
			
			Main.ship.imagePath = path;
			f = new File(Main.ship.imagePath);
			if (loadingSwitch) {
				Main.hullImage = SWTResourceManager.getImage(path);
			} else if (f.exists()) {
				Main.hullImage = new Image(Main.shell.getDisplay(), path);
			} else {
				errors.add("Error: load image - hull image not found.");
			}
			
			Main.ship.imageRect.width = Main.hullImage.getBounds().width;
			Main.ship.imageRect.height = Main.hullImage.getBounds().height;
			
		// === Cloak
		} else if (mod.equals("cloak")) {
			if (!loadingSwitch && Main.cloakImage != null && !Main.cloakImage.isDisposed() )
				Main.cloakImage.dispose();
			Main.cloakImage = null;
			
			f = new File(Main.ship.cloakPath);
			if (loadingSwitch) {
				Main.cloakImage = SWTResourceManager.getImage(path);
			} else if (f.exists()) {
				Main.cloakImage = new Image(Main.shell.getDisplay(), path);
			} else {
				errors.add("Error: load image - cloak image not found.");
			}
			
		// === Shields
		} else if (mod.equals("shields")) {
			if (!loadingSwitch && Main.shieldImage != null && !Main.shieldImage.isDisposed() )
				Main.shieldImage.dispose();
			Main.shieldImage = null;
			
			f = new File(Main.ship.shieldPath);
			if (Main.loadShield) {
				if (loadingSwitch) {
					Main.shieldImage = SWTResourceManager.getImage(path);
				} else if (f.exists()) {
					Main.shieldImage = new Image(Main.shell.getDisplay(), path);
				} else {
					errors.add("Error: load image - cloak image not found.");
				}
			}
			
		// === Floor
		} else if (mod.equals("floor")) {
			if (!loadingSwitch && Main.floorImage != null && !Main.floorImage.isDisposed() )
				Main.floorImage.dispose();
			Main.floorImage = null;

			f= new File(Main.ship.floorPath);
			if (Main.loadFloor) {
				if (loadingSwitch) { // fileName.contains(img + "_strip")
					Main.floorImage = SWTResourceManager.getImage(path);
				} else if (f.exists()) { 
					Main.floorImage = new Image(Main.shell.getDisplay(), path);
				} else {
					errors.add("Error: load image - cloak image not found.");
				}
			}
		}
	}
	
	/**
	 * Loads the number of strips (sprite frames) for weapons' art, needed to extract a single frame later during the drawing process.
	 */
	public static void loadWeaponStrips() {
		String path = null;
		String[] img = null;
		File weaponDir;
		
		Main.weaponStripMap.clear();
		
		path = Main.resPath + pathDelimiter + "img" + pathDelimiter + "weapons" + pathDelimiter;
		weaponDir = new File(path);
		for (String name : weaponDir.list()) {
			if (name.contains("_strip")) {
				img = name.split("_strip");
				
				img[1] = img[1].substring(0, img[1].lastIndexOf("."));
				
				Main.weaponStripMap.put(img[0], Integer.valueOf(img[1]));
			}
		}
	}
	
	public static void fixImgNames() {
		FTLItem it = null;
		for (String blue : Main.ship.weaponSet) {
			it = getItem(blue);
			if (it.img.contains("laser_burst_")) it.img = it.img.replace("_burst_", "");
			if (it.img.contains("laser_heavy")) it.img = it.img.replace("laser_", "");
			if (it.img.contains("_player")) it.img = it.img.replace("_player", "");
		}
	}

	public static void loadWeaponImages(FTLShip ship) {
		int strips = 0;
		String path = null;
		Image temp = null;
		FTLItem wpn = null;
		int index = -1;
		
		// === clear and dispose variant lists
		for (Image i : Main.flipped) if (i != null && !i.isDisposed()) {
			i.dispose();
			i = null;
		}
		for (Image i : Main.rotated) if (i != null && !i.isDisposed()) {
			i.dispose();
			i = null;
		}
		for (Image i : Main.rotatedFlipped) if (i != null && !i.isDisposed()) {
			i.dispose();
			i = null;
		}
		
		Main.flipped.clear();
		Main.rotated.clear();
		Main.rotatedFlipped.clear();
		
		if (!ship.weaponsBySet) {
			for (String blue : ship.weaponSet) {
				index++;
				wpn = getItem(blue);
				strips = Main.weaponStripMap.get(wpn.img);
				path = Main.resPath + pathDelimiter + "img" + pathDelimiter + "weapons" + pathDelimiter + wpn.img.toLowerCase() + "_strip" + strips + ".png";
				
				if (loadingSwitch) {
					Main.weaponImgMap.put(blue, SWTResourceManager.getImage(path));
				} else {
					temp = Main.weaponImgMap.get(blue);
					if (temp != null && !temp.isDisposed())
						temp.dispose();
					temp = null;
					
					Main.weaponImgMap.put(blue, new Image(Main.shell.getDisplay(), path));
				}
				
			// === prepare other variants
				Main.rotated.add(new Image(Main.shell.getDisplay(), Main.rotate(Main.weaponImgMap.get(blue).getImageData(), SWT.RIGHT)));
				Main.flipped.add(new Image(Main.shell.getDisplay(), Main.flip(Main.weaponImgMap.get(blue).getImageData(), !Main.ship.isPlayer)));
				Main.rotatedFlipped.add(new Image(Main.shell.getDisplay(), Main.flip(Main.rotated.get(index).getImageData(), Main.ship.isPlayer)));
			}
		} else {
			// placeholder or default weapon image;
		}
	}
	
	
// =============
// === EXPORTING
// =============
	
	/**
	 * Checks if the specified file exists in specified directory or its subdirectories.<br>
	 * WARNING: this method only checks the files' names, not contents.
	 * Since scanning the entire directory proved to be kinda inefficient, I just dumped the <br>
	 * default filenames to a .txt, and now the export function scans that file for matching entries (isDefaultResources method)
	 * 
	 * @param dir Directory in which the search is to be performed.
	 * @param file File the method is to look for.
	 * @return True is the file exists in the directory or any of its subdirectories, false otherwise.
	 */
	public static boolean searchDirForFile(File dir, File file) {
		boolean result = false;
search: for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				result = searchDirForFile(f, file);
			} else if (f.getName().endsWith(".png")){
				Main.debug(f.getName());
				result = f.getName().equals(file.getName());
			}
			if (result) break search;
		}
		return result;
	}
	
	/**
	 * Scans the defaultResources.txt file for entries matching the file's name.
	 * @param file The file whose name the list is to be scanned for.
	 * @return True if there's a matching filename on the list
	 */
	public static boolean isDefaultResource(File file) {
		boolean result = false;
		FileReader fr;
		Scanner sc = null;
		String s;
		
		try {
			fr = new FileReader("defaultResources.txt");
			sc = new Scanner(fr);
			sc.useDelimiter(Pattern.compile(lineDelimiter));
			
search:		while(sc.hasNext()) {
				s = sc.next();
				result = s.equals(file.getName());
				if (result)
					break search;
			}
			
			sc.close();
		} catch (FileNotFoundException e) {
			Main.debug("Error : default resources list not found.");
		} finally {
			sc = null;
		}
		return result;
	}
	
	public static void deleteFolderContents(File folder) {
	    File[] files = folder.listFiles();
	    if (files != null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if (!f.isDirectory()) {
	                f.delete();
	            } else {
	            	deleteFolderContents(f);
	            }
	        }
	    }
	}
	
	/**
	 * Exports the currently opened ship to file in format recognised by FTL. Creates a new directory at the desstination, named after the ship's blueprint name.
	 * @param path Location of the file to which the ship is to be exporeted.
	 */
	public static void export(String path, String fileName) {
		String pathDir = path + pathDelimiter + Main.ship.blueprintName;
		
		ExportProgress exp = new ExportProgress();
		exp.shell.open();
		
		File destination = new File(pathDir);
		File source;
		//File res = new File(Main.resPath);
		destination.mkdirs();
		deleteFolderContents(destination);
		FileWriter fw = null;

		// ===============
		// === Copy images
		
		// hull
		try {
			source = new File(Main.ship.imagePath);
			destination = new File(pathDir + pathDelimiter + "img" + pathDelimiter + "ship" + pathDelimiter + Main.ship.imageName + "_base.png");
			if ((dontCheck || !isDefaultResource(destination)) && source.exists()) {
				destination.mkdirs();
				java.nio.file.Files.copy(Paths.get(Main.ship.imagePath), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} else if (!source.exists()) {
				errors.add("Error: export - hull image not found.");
			}
		} catch (FileNotFoundException e) {
			errors.add("Error: export - hull image not found.");
		} catch (IOException e) {
		}
		exp.progressBar.setSelection(10);
		
		// shields
		if (Main.ship.isPlayer) {
			try {
				source = new File(Main.ship.shieldPath);
				destination = new File(pathDir + pathDelimiter + "img" + pathDelimiter + "ship" + pathDelimiter + Main.ship.imageName + "_shields1.png");
				if ((dontCheck || !isDefaultResource(destination)) && source.exists()) {
					destination.mkdirs();
					if (isNull(Main.ship.shieldOverride)) {
						java.nio.file.Files.copy(Paths.get(Main.ship.shieldPath), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
					} else {
						java.nio.file.Files.copy(Paths.get(Main.ship.shieldOverride), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
					}
				} else if (!source.exists()) {
					errors.add("Error: export - shield image not found.");
				}
			} catch (FileNotFoundException e) {
				errors.add("Error: export - shield image not found.");
			} catch (IOException e) {
			}
		}
		exp.progressBar.setSelection(20);
		
		// floor
		if (Main.ship.isPlayer) {
			try {
				source = new File(Main.ship.floorPath);
				destination = new File(pathDir + pathDelimiter + "img" + pathDelimiter + "ship" + pathDelimiter + Main.ship.imageName + "_floor.png");
				if ((dontCheck || !isDefaultResource(destination)) && source.exists()) {
					destination.mkdirs();
					java.nio.file.Files.copy(Paths.get(Main.ship.floorPath), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
				} else if (!source.exists()) {
					errors.add("Error: export - floor image not found.");
				}
			} catch (FileNotFoundException e) {
				errors.add("Error: export - floor image not found.");
			} catch (IOException e) {
			}
		}
		exp.progressBar.setSelection(30);
		
		// cloak
		try {
			source = new File(Main.ship.cloakPath);
			destination = new File(pathDir + pathDelimiter + "img" + pathDelimiter + "ship" + pathDelimiter + Main.ship.imageName + "_cloak.png");
			if ((dontCheck || !isDefaultResource(destination)) && source.exists()) {
				destination.mkdirs();
				if (isNull(Main.ship.cloakOverride)) {
					java.nio.file.Files.copy(Paths.get(Main.ship.cloakPath), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
				} else {
					java.nio.file.Files.copy(Paths.get(Main.ship.cloakOverride), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
				}
			} else if (!source.exists()) {
				errors.add("Error: export - cloak image not found.");
			}
		} catch (FileNotFoundException e) {
			errors.add("Error: export - cloak image not found.");
		} catch (IOException e) {
		}
		exp.progressBar.setSelection(40);

		// miniship
		if (Main.ship.isPlayer) {
			try {
				source = new File(Main.ship.miniPath);
				destination = new File(pathDir + pathDelimiter + "img" + pathDelimiter + "customizeUI" + pathDelimiter + "miniship_" + Main.ship.imageName + ".png");
				if ((dontCheck || !isDefaultResource(destination)) && source.exists()) {
					destination.mkdirs();
					java.nio.file.Files.copy(Paths.get(Main.ship.miniPath), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
				} else if (!source.exists()) {
					errors.add("Error: export - miniship image not found.");
				}
			} catch (FileNotFoundException e) {
				errors.add("Error: export - miniship image not found.");
			} catch (IOException e) {
			}
		}
		exp.progressBar.setSelection(50);
		
		// system images
		try {
			double d = 10/Main.ship.rooms.size();
			double progress = 0;
			String img;
			for (FTLRoom r : Main.ship.rooms) {
				if (!r.sys.equals(Systems.EMPTY) && !r.sys.equals(Systems.TELEPORTER) && !isNull(r.img)) {
					img = r.img.substring(r.img.lastIndexOf(pathDelimiter));
					destination = new File(pathDir + pathDelimiter + "img" + pathDelimiter + "ship" + pathDelimiter + "interior" + pathDelimiter + img);
					if (dontCheck || !isDefaultResource(destination)) {
						destination.mkdirs();
						java.nio.file.Files.copy(Paths.get(r.img), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
					}
				}
				progress += d;
				exp.progressBar.setSelection(50 + (int) progress);
			}
		} catch (IOException e) {
		}
		exp.progressBar.setSelection(60);
		
		/*
		// gibs
		try {
		} catch (IOException e) {
		}
		*/
		exp.progressBar.setSelection(70);
		
		
		// ====================
		// === Write layout.txt

		try {
			destination = new File(pathDir + pathDelimiter + "data");
			destination.mkdirs();
				
			fw = new FileWriter(pathDir + pathDelimiter + "data" + pathDelimiter + fileName + ".txt", false);
			
			fw.write("X_OFFSET" + lineDelimiter);
			if (Main.ship.offset.x == 0)
				Main.ship.offset.x = (Main.ship.findLowBounds().x-Main.ship.anchor.x)/35;
			fw.write(Main.ship.offset.x + lineDelimiter);
			
			fw.write("Y_OFFSET" + lineDelimiter);
			if (Main.ship.offset.y == 0)
				Main.ship.offset.y = (Main.ship.findLowBounds().y-Main.ship.anchor.y)/35;
			fw.write(Main.ship.offset.y + lineDelimiter);
			
			fw.write("VERTICAL" + lineDelimiter);
			fw.write(Main.ship.vertical + lineDelimiter);
			
			fw.write("ELLIPSE" + lineDelimiter);
			if (Main.shieldImage != null && !Main.shieldImage.isDisposed() && Main.ship.isPlayer) {
				fw.write(Main.shieldImage.getBounds().width/2 + lineDelimiter);
				fw.write(Main.shieldImage.getBounds().height/2 + lineDelimiter);
			} else {
				fw.write(Main.shieldEllipse.width/2 + lineDelimiter);
				fw.write(Main.shieldEllipse.height/2 + lineDelimiter);
			}
			
			fw.write(Main.ship.ellipse.x + lineDelimiter);
			fw.write(Main.ship.ellipse.y + lineDelimiter);
			
			SortedSet<FTLRoom> set = new TreeSet<FTLRoom>();
			set.addAll(Main.ship.rooms);
			for (FTLRoom r : set) {
				fw.write("ROOM" + lineDelimiter);
				fw.write(r.id + lineDelimiter);
				
				fw.write((r.rect.x-Main.ship.anchor.x-Main.ship.offset.x*35)/35 + lineDelimiter);
				fw.write((r.rect.y-Main.ship.anchor.y-Main.ship.offset.y*35)/35 + lineDelimiter);
				fw.write(r.rect.width/35 + lineDelimiter);
				fw.write(r.rect.height/35 + lineDelimiter);
			}
			

			int x,y;
			for (FTLDoor d : Main.ship.doors) {
				fw.write("DOOR" + lineDelimiter);
				x = (d.rect.x-Main.ship.anchor.x-Main.ship.offset.x*35+(d.horizontal ? -2 : 3))/35;
				y = (d.rect.y-Main.ship.anchor.y-Main.ship.offset.y*35+(d.horizontal ? 3 : -2))/35;
				
				debug("(" + x + ", " + y + ")");
				
				fw.write(x + lineDelimiter);
				fw.write(y + lineDelimiter);
				x = Main.ship.findLeftRoom(d);
				y = Main.ship.findRightRoom(d);
				fw.write(((x==-1)?y:x) + lineDelimiter);
				fw.write(((x!=-1)?y:x) + lineDelimiter);
				fw.write((d.horizontal ? 0 : 1) + lineDelimiter);
			}
			
			fw.close();
		} catch (FileNotFoundException e) {
			errors.add("Error: export - file not found.");
		} catch (IOException e) {
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
			}
			fw = null;
		}
			
		// ====================
		// === Write layout.xml

		try {
			fw = new FileWriter(pathDir + pathDelimiter + "data" + pathDelimiter + fileName + ".xml", false);

			fw.write("<!-- Copyright (c) 2012 by Subset Games. All rights reserved -->" + lineDelimiter);
			fw.write("<!-- Exported file -->"  + lineDelimiter);
			
			fw.write(lineDelimiter);
			
			// hull image positioning
			fw.write("<img x=\"" + (Main.ship.imageRect.x-Main.ship.anchor.x-Main.ship.offset.x*35)
					 + "\" y=\""+ (Main.ship.imageRect.y-Main.ship.anchor.y-Main.ship.offset.y*35)
					 + "\" w=\""+ Main.ship.imageRect.width
					 + "\" h=\""+ Main.ship.imageRect.height +"\"/>" + lineDelimiter);
			
			fw.write(lineDelimiter);
			
			// weapon mounts
			fw.write("<weaponMounts>" + lineDelimiter);
			FTLMount mt = null;
			for (FTLMount m : Main.ship.mounts) {
				if (!m.slide.equals(Slide.NO)) {
					fw.write("\t");
					fw.write("<mount x=\"" + m.pos.x + "\" ");
					fw.write("y=\"" + m.pos.y + "\" ");
					fw.write("rotate=\"" + m.rotate + "\" ");
					fw.write("mirror=\"" + m.mirror + "\" ");
					fw.write("gib=\"" + m.gib + "\" ");
					fw.write("slide=\"" + m.slide.toString().toLowerCase() + "\"/>");
					fw.write(lineDelimiter);
				} else {
					mt = m;
				}
			}
			
			// Slide.NO is associated with artillery, has to be written as the last one on the list
			// otherwise, normal weapons use that mount
			if (mt != null) {
				fw.write("\t");
				fw.write("<mount x=\"" + mt.pos.x + "\" ");
				fw.write("y=\"" + mt.pos.y + "\" ");
				fw.write("rotate=\"" + mt.rotate + "\" ");
				fw.write("mirror=\"" + mt.mirror + "\" ");
				fw.write("gib=\"" + mt.gib + "\" ");
				fw.write("slide=\"" + mt.slide.toString().toLowerCase() + "\"/>");
				fw.write(lineDelimiter);
			}
			
			fw.write("</weaponMounts>" + lineDelimiter);
			
			fw.write(lineDelimiter);
			
			// gibs
			fw.write("<explosion>" + lineDelimiter);
				/* for (FTLGib g : Main.ship.gibs) {
				 *     //TODO gibs writing
				 *     fw.write(lineDelimiter);
				 * }
				 */
			fw.write("</explosion>");
			
			fw.close();
		} catch (FileNotFoundException e) {
			errors.add("Error: export - file not found.");
		} catch (IOException e) {
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
			}
			fw = null;
		}
			
		// ========================
		// === Write blueprints.xml.append
			
		try {
		// === Player Ship
			if (Main.ship.isPlayer) {
				fw = new FileWriter(pathDir + pathDelimiter + "data" + pathDelimiter + "blueprints.xml.append");
				
			// general information
				fw.write("<shipBlueprint name=\""+Main.ship.blueprintName+"\" layout=\""+Main.ship.layout+"\" img=\""+Main.ship.imageName+"\">");
				fw.write(lineDelimiter);
				fw.write("\t<class>"+Main.ship.shipClass+"</class>");
				fw.write(lineDelimiter);
				fw.write("\t<name>"+Main.ship.shipName+"</name>");
				fw.write(lineDelimiter);
				fw.write("\t<desc>"+Main.ship.descr+"</desc>");
				
			// systems
				fw.write(lineDelimiter);
				fw.write("\t<systemList>"+lineDelimiter);
				
				for (FTLRoom r : Main.ship.rooms) {
					if (!r.sys.equals(Systems.EMPTY)) {
						fw.write("\t\t<"+r.sys.toString().toLowerCase()+" power=\""+Main.ship.levelMap.get(r.sys)+"\" room=\""+r.id+"\" start=\""+Main.ship.startMap.get(r.sys)+"\"");
						if (!isNull(r.img)) {
							fw.write(" img=\""+r.img.substring(r.img.lastIndexOf(pathDelimiter)+1, r.img.lastIndexOf('.'))+"\"");
						}
						if (r.slot != -2 || r.sys.equals(Systems.MEDBAY)) {
							fw.write(">");
							fw.write(lineDelimiter);
							fw.write("\t\t\t" + "<slot>" + lineDelimiter);
							if (!r.sys.equals(Systems.MEDBAY))
								fw.write("\t\t\t\t" + "<direction>"+r.dir.toString().toLowerCase()+"</direction>" + lineDelimiter);
							fw.write("\t\t\t\t" + "<number>"+r.slot+"</number>" + lineDelimiter);
							fw.write("\t\t\t" + "</slot>" + lineDelimiter);
							fw.write("\t\t" + "</" + r.sys.toString().toLowerCase() + ">");
						} else {
							fw.write("/>");
						}
						fw.write(lineDelimiter);
					}
				}
				fw.write("\t" + "</systemList>"+lineDelimiter);
				
			// weapons
				fw.write("\t" + "<weaponSlots>"+Main.ship.weaponSlots+"</weaponSlots>" + lineDelimiter);
				fw.write("\t" + "<droneSlots>"+Main.ship.droneSlots+"</droneSlots>" + lineDelimiter);
				
				fw.write("\t" + "<weaponList count=\""+Main.ship.weaponCount+"\" missiles=\""+Main.ship.missiles+"\">" + lineDelimiter);
				
				for (String blue : Main.ship.weaponSet) {
					fw.write("\t\t" + "<weapon name=\"" + blue + "\"/>" + lineDelimiter);
				}
				
				fw.write("\t" + "</weaponList>" + lineDelimiter);
				
			// drones
				fw.write("\t" + "<droneList count=\"" + Main.ship.droneCount + "\" drones=\"" + Main.ship.drones + "\">" + lineDelimiter);
				
				for (String blue : Main.ship.droneSet) {
					fw.write("\t\t" + "<drone name=\"" + blue + "\"/>" + lineDelimiter);
				}
				
				fw.write("\t" + "</droneList>" + lineDelimiter);
				
			// health & power
				fw.write("\t" + "<health amount=\""+Main.ship.hullHealth+"\"/>" + lineDelimiter);
				fw.write("\t" + "<maxPower amount=\""+Main.ship.reactorPower+"\"/>" + lineDelimiter);
				
			// crew
				for (String key : Main.ship.crewMap.keySet()) {
					if (Main.ship.crewMax==0) {
						fw.write("\t" + "<crewCount amount=\"0\" class=\"human\"/>");
						fw.write(lineDelimiter);
					} else {
						if (Main.ship.crewMap.get(key) > 0) {
							fw.write("\t" + "<crewCount amount=\""+Main.ship.crewMap.get(key)+"\" class=\""+key+"\"/>");
							fw.write(lineDelimiter);
						}
					}
				}

			// augments
				for (String aug : Main.ship.augmentSet) {
					fw.write("\t" + "<aug name=\"" + aug + "\"/>" + lineDelimiter);
				}

			// overrides 
				if (!isNull(Main.ship.shieldOverride))
					fw.write("\t"+"<shieldImage>"+Main.ship.shieldOverride.substring(Main.ship.shieldOverride.lastIndexOf(ShipIO.pathDelimiter)+1, Main.ship.shieldOverride.lastIndexOf("_"))+"</shieldImage>" + lineDelimiter);
				if (!isNull(Main.ship.cloakOverride))
					fw.write("\t"+"<cloakImage>"+Main.ship.cloakOverride+"</cloakImage>" + lineDelimiter);
				
				fw.write("</shipBlueprint>");
				
		// === Enemy Ship
			} else {
				fw = new FileWriter(pathDir + pathDelimiter + "data" + pathDelimiter + "autoBlueprints.xml.append");
				
				// general information
				fw.write("<shipBlueprint name=\""+Main.ship.blueprintName+"\" layout=\""+Main.ship.layout+"\" img=\""+Main.ship.imageName+"\">");
				fw.write(lineDelimiter);
				fw.write("\t<class>"+Main.ship.shipClass+"</ckass>");
				fw.write(lineDelimiter);
				
				// systems
				fw.write("\t<systemList>"+lineDelimiter);
				
				for (FTLRoom r : Main.ship.rooms) {
					if (!r.sys.equals(Systems.EMPTY)) {
						fw.write("\t\t<"+r.sys.toString().toLowerCase()+" power=\""+Main.ship.levelMap.get(r.sys)+"\" room=\""+r.id+"\"/>");

						fw.write(lineDelimiter);
					}
				}
				fw.write("\t" + "</systemList>"+lineDelimiter);
				
			// weapons
				if (Main.ship.weaponsBySet) {
					if (Main.ship.weaponSet.size() > 0)
						fw.write("\t" + "<weaponList missiles=\""+Main.ship.missiles+"\" load=\""+Main.ship.weaponSet.toArray()[0]+"\"/>" + lineDelimiter);
				} else {
					fw.write("\t" + "<weaponList count=\"" + Main.ship.weaponCount + "\" missiles=\"" + Main.ship.missiles + "\">" + lineDelimiter);
					for (String blue : Main.ship.weaponSet) {
						fw.write("\t\t" + "<weapon name=\"" + blue + "\"/>" + lineDelimiter);
					}
				}
				
			// drones
				if (Main.ship.dronesBySet) {
					if (Main.ship.droneSet.size() > 0)
						fw.write("\t" + "<droneList missiles=\""+Main.ship.drones+"\" load=\""+Main.ship.droneSet.toArray()[0]+"\"/>" + lineDelimiter);
				} else {
					fw.write("\t" + "<droneList count=\"" + Main.ship.droneCount + "\" drones=\"" + Main.ship.drones + "\">" + lineDelimiter);
					for (String blue : Main.ship.weaponSet) {
						fw.write("\t\t" + "<weapon name=\"" + blue + "\"/>" + lineDelimiter);
					}
				}
				
			// health & power
				fw.write("\t" + "<health amount=\""+Main.ship.hullHealth+"\"/>" + lineDelimiter);
				fw.write("\t" + "<maxPower amount=\""+Main.ship.reactorPower+"\"/>" + lineDelimiter);

			// crew
crew:			for (String key : Main.ship.crewMap.keySet()) {
					if (Main.ship.crewMax==0) {
						fw.write("\t" + "<crewCount amount=\"0\" max=\"0\" class=\"human\"/>");
						fw.write(lineDelimiter);
						break crew;
					} else {
						if (Main.ship.crewMap.get(key) > 0) {
							fw.write("\t" + "<crewCount amount=\""+Main.ship.crewMap.get(key)+"\" max=\""+Main.ship.crewMax+"\" class=\""+key+"\"/>");
							fw.write(lineDelimiter);
						}
					}
				}
				
				if (Main.isSystemAssigned(Systems.TELEPORTER))
					fw.write("\t" + "<boardingAI>sabotage</boardingAI>"); // TODO ?

			// augments
				for (String aug : Main.ship.augmentSet) {
					fw.write("\t" + "<aug name=\"" + aug + "\"/>" + lineDelimiter);
				}
				
			// overrides
				if (!isNull(Main.ship.shieldOverride))
					fw.write("\t"+"<shieldImage>"+Main.ship.shieldOverride+"</shieldImage>" + lineDelimiter);
				if (!isNull(Main.ship.cloakOverride))
					fw.write("\t"+"<cloakImage>"+Main.ship.cloakOverride+"</cloakImage>" + lineDelimiter);
				fw.write("</shipBlueprint>");
			}
			
			fw.close();
		} catch (FileNotFoundException e) {
			errors.add("Error: export - file not found.");
		} catch (IOException e) {
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
			}
			fw = null;
		}
		
		exp.shell.dispose();
		exp = null;
		
		if (createFtl)
			zipToFTL(path, Main.ship.shipClass);
		
		if (deleteTemp) {
			File f = new File(path + pathDelimiter + Main.ship.blueprintName);
			deleteFolderContents(f);
			rmdir(f);
			f = null;
		}	
	}

	public static void rmdir(final File folder) {
		if (folder.isDirectory()) {
			File[] list = folder.listFiles();
			if (list != null) {
				for (int i = 0; i < list.length; i++) {
					File tmpF = list[i];
					if (tmpF.isDirectory()) {
						rmdir(tmpF);
					}
					tmpF.delete();
				}	
			}
			if (!folder.delete()) {
				System.out.println("can't delete folder : " + folder);
			}
		}
	}

	public static void zipToFTL(String path, String fileName) {
		try {
			FileOutputStream fos = new FileOutputStream((path + pathDelimiter + fileName + ".ftl"));
			ZipOutputStream zos = new ZipOutputStream(fos);
			/*
			ZipEntry ze = null;
			
			ze = new ZipEntry("");
			zos.putNextEntry(ze);
			zos.closeEntry();
			
			ze = new ZipEntry("");
			zos.putNextEntry(ze);
			zos.closeEntry();
			*/
			path = path + pathDelimiter + Main.ship.blueprintName;
			
			zipDir(path, zos);
			
			zos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
		}
	}
	
// PROJECT IO
	
	public static void loadSystemImages() {
		for (FTLRoom r : Main.ship.rooms) {
			loadSystemImage(r);
		}
	}
	
	public static void loadSystemImage(FTLRoom r) {
		if (r.sysImg != null && !r.sysImg.isDisposed() && !loadingSwitch)
			r.sysImg.dispose();

		try { 
			if (isNull(r.img) && !r.sys.equals(Systems.TELEPORTER) && !r.sys.equals(Systems.EMPTY)) // load default graphics (teleporter doesn't have default graphic)
				r.img = Main.resPath + pathDelimiter + "img" + pathDelimiter + "ship" + pathDelimiter + "interior" + pathDelimiter + "room_" + r.sys.toString().toLowerCase() + ".png";
			
			if (Main.loadSystem && !isNull(r.img))
				r.sysImg = (loadingSwitch) ? SWTResourceManager.getImage(r.img) : new Image(Main.shell.getDisplay(), r.img);
		} catch (SWTException e) {
			r.img = null;
			r.sysImg = null;
		}
	}
	
	public static void loadShipProject(String path) {
        ObjectInputStream ois;
		try {
			debug(path);
			ois = new ObjectInputStream(new FileInputStream(path));

			if (Main.hullImage != null && !Main.hullImage.isDisposed() && !loadingSwitch)
				Main.hullImage.dispose();
			if (Main.floorImage != null && !Main.floorImage.isDisposed() && !loadingSwitch)
				Main.floorImage.dispose();
			if (Main.shieldImage != null && !Main.shieldImage.isDisposed() && !loadingSwitch)
				Main.shieldImage.dispose();
			Main.hullImage = null;
			Main.floorImage = null;
			Main.shieldImage = null;

			Main.ship = (FTLShip) ois.readObject();
			
			
			loadShipImages(Main.ship);
			
			loadSystemImages();
			
        	fixImgNames();
        	loadWeaponImages(Main.ship);
			
			for (FTLRoom r : Main.ship.rooms) {
				Main.idList.add(r.id);
			}
			if (errors.size() == 0) {
				Main.print("Project loaded successfully.");
			} else {
				Main.print("Errors occured during project loading. Some data may be missing");
			}
			ois.close();
		} catch (FileNotFoundException e) {
			errors.add("Error: load ship project - file not found.");
		} catch (ClassNotFoundException e) {
			errors.add("Error: load ship project - class not found. File does not contain the FTLShip class or comes from another version of the program.");
		} catch (StreamCorruptedException e) {
			errors.add("Error: load ship project - file is corrupted - might have been manually changed.");
		} catch (EOFException e) {
			errors.add("Error: load ship project - end of file reached - file is corrupted, probably due to an error during its saving.");
		} catch (IOException e) {
			e.printStackTrace();
			if (IOdebug)
				e.printStackTrace();
		}
	}
	
	public static void saveShipProject(String path) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));

			for (FTLRoom r : Main.ship.rooms) {
				if (r.sysImg != null && !r.sysImg.isDisposed())
					r.sysImg.dispose();
				r.sysImg = null;
			}
			
            oos.writeObject(Main.ship);
            
			oos.close();
			
			loadSystemImages();
		} catch (IOException e) {
		}
	}
	
		// ASK THE USER TO SPECIFY SAVE DIRECTORY
	public static void askSaveDir()
	{
		FileDialog dialog = new FileDialog(Main.shell, SWT.SAVE);
		String[] filterExtensions = new String[] {"*.shp"};
		dialog.setFilterExtensions(filterExtensions);
		
		Main.currentPath = dialog.open();
		
		if (Main.currentPath != null) {
			saveShipProject(Main.currentPath);
		}
	}
	
		// ASK THE USER TO SPECIFY FILE TO LOAD
	public static void askLoadDir()
	{
		FileDialog dialog = new FileDialog(Main.shell, SWT.OPEN);
		String[] filterExtensions = new String[] {"*.shp"};
		dialog.setFilterExtensions(filterExtensions);
		
		Main.currentPath = dialog.open();
		
		if (Main.currentPath != null) {
	        loadShipProject(Main.currentPath);
		}
	}
	
	
// ========================================
// === AUXILIARY
	
	public static void fetchShipNames() {
		loadDeclarationsSilent();
		
		for (String s : playerBlueprintNames) {
			playerShipNames.put(s, getShipName(s));
		}
		for (String s : enemyBlueprintNames) {
			enemyShipNames.put(s, getShipName(s));
		}
		for (String s : otherBlueprintNames) {
			otherShipNames.put(s, getShipName(s));
		}
		namesFetched = true;
	}
	
	public static String getShipName (String blueprintName) {
		FileReader fr;
		Scanner sc;
		Pattern pattern;
		Matcher matcher;
		String s = null;
		String name = null;
		try {
			fr = new FileReader(Main.dataPath+pathDelimiter+"autoBlueprints.xml");
			sc = new Scanner(fr);
			sc.useDelimiter(Pattern.compile(lineDelimiter));
			
scan:		while(sc.hasNext()) {
				s = sc.next();
				pattern = Pattern.compile("(<shipBlueprint name=\")"+blueprintName+"(\".*?)");
				matcher = pattern.matcher(s);
				if (matcher.find()) {
					while (!s.contains("</shipBlueprint>")) {
						s = sc.next();
						pattern = Pattern.compile("(<class>)(.*?)(</class>)");
						matcher = pattern.matcher(s);
						if (matcher.find()) {
							name = matcher.group(2);
							break scan;
						}
					}
				}
			}
			
			sc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (isNull(name)) {
			try {
				fr = new FileReader(Main.dataPath+pathDelimiter+"blueprints.xml");
				sc = new Scanner(fr);
				sc.useDelimiter(Pattern.compile(lineDelimiter));
				
scan:			while(sc.hasNext()) {
					s = sc.next();
					pattern = Pattern.compile("(<shipBlueprint name=\")(.*?)(\" layout=\")(.*?)(\" img=\")(.*?)(\">)");
					matcher = pattern.matcher(s);
					if (matcher.find() && s.contains(blueprintName)) {
seek:				    while(!s.contains("</blueprintList>") && sc.hasNext()) {
					    	s = sc.next();
						    pattern = Pattern.compile(".*?(<name>)(.*?)(</name>)");
						   	matcher = pattern.matcher(s);
					    	if (matcher.find()) {
					    		name = matcher.group(2);
					    		break seek;
					    	}
					    }
						break scan;
					}
				}
				sc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return name;
	}

	public static void loadDeclarationsSilent() {
		FileReader fr;
		Scanner sc;
		String s;
		Pattern pattern;
		Matcher matcher;
		HashSet<String> tempSet;
		FTLItem tempItem;
		boolean ignoreNextTag = false;
		/**
		 * This flag is changed once the Crystal Bomber ship is reached, since it is the last of the default ships.
		 * All ships below it are then classified as "other", not "enemy".
		 */
		boolean defaultShips = true;
		
		try {
			fr = new FileReader(Main.dataPath+pathDelimiter+"autoBlueprints.xml");
			sc = new Scanner(fr);
			sc.useDelimiter(Pattern.compile(lineDelimiter));
			while(sc.hasNext()) {
				s = sc.next();
				if (s.contains("<!--")) ignoreNextTag = true;
					
				// load section names
				pattern = Pattern.compile("(<shipBlueprint name=\")(.*?)(\" layout=\")(.*?)(\" img=\")(.*?)(\">)");
				matcher = pattern.matcher(s);
				if (matcher.find() && !ignoreNextTag) {
					if (defaultShips) {
						enemyBlueprintNames.add(matcher.group(2));
						matcher.group(2).equals("CRYSTAL_BOMBER");
					} else {
						otherBlueprintNames.add(matcher.group(2));
					}
				} else {
					pattern = Pattern.compile("(<blueprintList name=\")(.*?)(\">)");
					matcher = pattern.matcher(s);
					if (matcher.find()) {
						tempSet = new HashSet<String>();
						
						String set = matcher.group(2);
						
						while(!s.equals("</blueprintList>")) {
							s = sc.next();
							if (s.contains("<!--")) ignoreNextTag = true;
							
							pattern = Pattern.compile("(<name>)(.*?)(</name>)");
							matcher = pattern.matcher(s);
							if (matcher.find()) {
								tempItem = new FTLItem();
								tempItem.blueprint = matcher.group(2);
								tempSet.add(tempItem.blueprint);
							}
							if (s.contains("-->")) ignoreNextTag = false;
						}

						if (set.contains("DRONE")) {
							droneSetMap.put(set, tempSet);
						} else if (set.contains("WEAPON")) {
							weaponSetMap.put(set, tempSet);
						}
					}
				}

				if (s.contains("-->")) ignoreNextTag = false;
			}
		} catch (FileNotFoundException e) {
		}
		
		try {
			fr = new FileReader(Main.dataPath+pathDelimiter+"blueprints.xml");
			sc = new Scanner(fr);
			sc.useDelimiter(Pattern.compile(lineDelimiter));
scan:		while(sc.hasNext()) {
				s = sc.next();

			// weapons
				pattern = Pattern.compile("(<weaponBlueprint name=\")(.*?)(\">)");
				matcher = pattern.matcher(s);
				if (matcher.find()) {
					tempItem = new FTLItem();
					tempItem.blueprint = matcher.group(2);
					
					if (tempItem != null) {
						while(!s.contains("</weaponBlueprint>")) {
							s = sc.next();
							pattern = Pattern.compile("(<type>)(.*?)(</type>)");
							matcher = pattern.matcher(s);
							if (matcher.find())
								tempItem.category = matcher.group(2);
							
							pattern = Pattern.compile("(<title>)(.*?)(</title>)");
							matcher = pattern.matcher(s);
							if (matcher.find())
								tempItem.name = matcher.group(2);
							
							pattern = Pattern.compile("(<weaponArt>)(.*?)(</weaponArt>)");
							matcher = pattern.matcher(s);
							if (matcher.find())
								tempItem.img = matcher.group(2);
						}
						weaponMap.put(tempItem.blueprint, tempItem);
					}
				}

			// drones
				pattern = Pattern.compile("(<droneBlueprint name=\")(.*?)(\">)");
				matcher = pattern.matcher(s);
				if (matcher.find()) {
					tempItem = new FTLItem();
					tempItem.blueprint = matcher.group(2);
					
					if (tempItem != null) {
						while(!s.contains("</droneBlueprint>")) {
							s = sc.next();
							pattern = Pattern.compile("(<type>)(.*?)(</type>)");
							matcher = pattern.matcher(s);
							if (matcher.find())
								tempItem.category = matcher.group(2);
							
							pattern = Pattern.compile("(<title>)(.*?)(</title>)");
							matcher = pattern.matcher(s);
							if (matcher.find())
								tempItem.name = matcher.group(2);
						}
						droneMap.put(tempItem.blueprint, tempItem);
					}
				}
				
			// augments
				if (s.contains("<!--")) ignoreNextTag = true;
				
				pattern = Pattern.compile("(<augBlueprint name=\")(.*?)(\">)");
				matcher = pattern.matcher(s);
				if (matcher.find() && !ignoreNextTag) {
					tempItem = new FTLItem();
					tempItem.blueprint = matcher.group(2);
					
					if (tempItem != null) {
						while(!s.contains("</augBlueprint>")) {
							s = sc.next();

							pattern = Pattern.compile("(<title>)(.*?)(</title>)");
							matcher = pattern.matcher(s);
							if (matcher.find()) {
								tempItem.name = matcher.group(2);
							}
						}
						augMap.put(tempItem.blueprint, tempItem);
					}
				}
				
				if (s.contains("-->")) ignoreNextTag = false;

			// ships
				if (s.contains("SHIP BLUEPRINTS")) {
					while(!s.contains("TUTORIAL SHIPS")) {
						s = sc.next();
						if (s.contains("<!--")) ignoreNextTag = true;
						
						pattern = Pattern.compile("(<shipBlueprint name=\")(.*?)(\" layout=\")(.*?)(\" img=\")(.*?)(\">)");
						matcher = pattern.matcher(s);
						if (matcher.find() && !ignoreNextTag) {
					    	playerBlueprintNames.add(matcher.group(2));
						}
						
						if (s.contains("-->")) ignoreNextTag = false;
					}
					playerBlueprintNames.remove("PLAYER_SHIP_EASY");
					break scan;
				}
			}
			
			loadWeaponStrips();
			
		} catch (FileNotFoundException e) {
		}
	}
	
	public static FTLItem getItem(String blueprint) {
		FTLItem it = null;
		for (String b : weaponMap.keySet()) {
			it = weaponMap.get(b);
			if (it.blueprint.equals(blueprint)) return it;
		}
		for (String b : droneMap.keySet()) {
			it = droneMap.get(b);
			if (it.blueprint.equals(blueprint)) return it;
		}
		for (String b : augMap.keySet()) {
			it = augMap.get(b);
			if (it.blueprint.equals(blueprint)) return it;
		}
		return it;
	}
	
	public static void zipDir(String source, ZipOutputStream zos)
	{
		FileInputStream fis = null;
		ZipEntry ze = null;
		File zipDir, file = null;
		try {
			zipDir = new File(source);

			String[] dirList = zipDir.list();
			byte[] readBuffer = new byte[2156];
			int bytesIn = 0;
			
			for(int i = 0; i < dirList.length; i++) {
				file = new File(zipDir, dirList[i]);
				if(file.isDirectory()) {
					String filePath = file.getPath();
					zipDir(filePath, zos);
					continue;
				}
				
				fis = new FileInputStream(file);
				file = new File(file.getPath().substring(file.getPath().indexOf(Main.ship.blueprintName)+Main.ship.blueprintName.length()+1));
				ze = new ZipEntry(file.getPath());
				zos.putNextEntry(ze);
				
				while((bytesIn = fis.read(readBuffer)) != -1) {
					zos.write(readBuffer, 0, bytesIn);
				}
				
				fis.close();
			}
		} catch(Exception e) {
		} finally {
			try {
				if (fis!=null)
					fis.close();
			} catch (IOException e) {
			}
		}
	}
}


