package com.kartoflane.superluminal.elements;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal.core.Main;



/**
 * Class representing the ship.
 *
 */
public class FTLShip implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 344200154077072015L;
	/**
	 * Size of the anchor handle;
	 */
	final static public int ANCHOR = 12;

	public enum AxisFlag
	{ X, Y, BOTH };
	
	public boolean isPlayer;
	public boolean weaponsBySet;
	public boolean dronesBySet;
	
	public HashSet<FTLRoom> rooms;
	public HashSet<FTLDoor> doors;
	public HashSet<FTLMount> mounts;
	
	// hull
	public String imagePath;
	public String shieldPath;
	public String floorPath;
	public String cloakPath;
	
	/**
	 *  posiition of the ship on canvas (anchor's x and y)
	 */
	public Point anchor;
	
	// FTL in-game ship information
	public Point offset;
	public Rectangle ellipse;
	public Rectangle imageRect;
	public String blueprintName;
	public String shipClass;
	public String shipName;
	public String layout;
	public String imageName;
	public String descr;
	public int vertical = 0;
	
	public int hullHealth = 30;
	public int reactorPower = 8;
	public int weaponSlots = 4;
	public int droneSlots = 2;
	
	// === player specific
	public int weaponCount = 4;
	public int droneCount = 2;
	public String cloakOverride;
	public String shieldOverride;
	
	// === enemy specific
	public int crewMax = 8;
	
	// ===
	public int missiles = 5;
	public int drones = 5;
	
	public LinkedList<String> weaponSet;
	public LinkedList<String> droneSet;
	public LinkedList<String> augmentSet;
	public HashMap<String, Integer> crewMap;
	public HashMap<Systems, Integer> powerMap;
	public HashMap<Systems, Integer> levelMap;
	public HashMap<Systems, Boolean> startMap;
	
	public FTLShip() {
		rooms = new HashSet<FTLRoom>();
		doors = new HashSet<FTLDoor>();
		mounts = new HashSet<FTLMount>();
		
		weaponSet = new LinkedList<String>();
		droneSet = new LinkedList<String>();
		augmentSet = new LinkedList<String>();
		crewMap = new HashMap<String, Integer>();
		powerMap = new HashMap<Systems, Integer>();
		levelMap = new HashMap<Systems, Integer>();
		startMap = new HashMap<Systems, Boolean>();
		
		ellipse = new Rectangle(0,0,0,0);
		imageRect = new Rectangle(0,0,0,0);
		offset = new Point(0,0);
		anchor = new Point(0,0);
		vertical = 0;
		hullHealth = 30;
		reactorPower = 8;
		weaponSlots = 4;
		droneSlots = 2;
		weaponCount = 4;
		droneCount = 2;
		crewMax = 8;
		missiles = 5;
		drones = 5;
		
		isPlayer = false;
		weaponsBySet = false;
		dronesBySet = false;
		imageName = new String();
		
		crewMap.put("human", 0);
		crewMap.put("engi", 0);
		crewMap.put("zoltan", 0);
		crewMap.put("mantis", 0);
		crewMap.put("slug", 0);
		crewMap.put("rock", 0);
		crewMap.put("crystal", 0);
		
		levelMap.put(Systems.ARTILLERY, 0);
		levelMap.put(Systems.CLOAKING, 0);
		levelMap.put(Systems.DOORS, 0);
		levelMap.put(Systems.DRONES, 0);
		levelMap.put(Systems.EMPTY, 0);
		levelMap.put(Systems.ENGINES, 0);
		levelMap.put(Systems.MEDBAY, 0);
		levelMap.put(Systems.OXYGEN, 0);
		levelMap.put(Systems.PILOT, 0);
		levelMap.put(Systems.SENSORS, 0);
		levelMap.put(Systems.SHIELDS, 0);
		levelMap.put(Systems.TELEPORTER, 0);
		levelMap.put(Systems.WEAPONS, 0);
		
		powerMap.put(Systems.ARTILLERY, 0);
		powerMap.put(Systems.CLOAKING, 0);
		powerMap.put(Systems.DOORS, 0);
		powerMap.put(Systems.DRONES, 0);
		powerMap.put(Systems.EMPTY, 0);
		powerMap.put(Systems.ENGINES, 0);
		powerMap.put(Systems.MEDBAY, 0);
		powerMap.put(Systems.OXYGEN, 0);
		powerMap.put(Systems.PILOT, 0);
		powerMap.put(Systems.SENSORS, 0);
		powerMap.put(Systems.SHIELDS, 0);
		powerMap.put(Systems.TELEPORTER, 0);
		powerMap.put(Systems.WEAPONS, 0);
		
		startMap.put(Systems.ARTILLERY, true);
		startMap.put(Systems.CLOAKING, true);
		startMap.put(Systems.DOORS, true);
		startMap.put(Systems.DRONES, true);
		startMap.put(Systems.EMPTY, true);
		startMap.put(Systems.ENGINES, true);
		startMap.put(Systems.MEDBAY, true);
		startMap.put(Systems.OXYGEN, true);
		startMap.put(Systems.PILOT, true);
		startMap.put(Systems.SENSORS, true);
		startMap.put(Systems.SHIELDS, true);
		startMap.put(Systems.TELEPORTER, true);
		startMap.put(Systems.WEAPONS, true);
	}
	
	public FTLShip(int x, int y) {
		this();
		offset.x = x;
		offset.y = y;
	}

	/**
	 * Override the anchor and automatically snap it to the ship.
	 * Generally not used.
	 */
	public void computeNewAnchor() {
		Point p = null;
		
		p = findLowBounds();
		p.x = (p.x - offset.x*35 >= 0) ? (p.x - offset.x*35) : (offset.x*35);
		p.y = (p.y - offset.y*35 >= 0) ? (p.y - offset.y*35) : (offset.y*35);
		
		updateElements(p, AxisFlag.BOTH);
		anchor = p;
	}

	/**
	 * Update positions of elements of the ship (that's rooms, doors, etc) relative to the anchor.
	 * Should be used in the following way:<br>
	 * 	&nbsp&nbsp&nbsp&nbsp| var1 = (new anchor for the ship);<br>
	 * 	&nbsp&nbsp&nbsp&nbsp| ship.updateElements(var1, AxisFlag);<br>
	 * 	&nbsp&nbsp&nbsp&nbsp| ship.anchor = var1;<br>
	 * 
	 * @param newAnchor the new anchor that the ship is going to use.
	 * @param flag used to state which component of position is be updated.
	 */
	public void updateElements(Point newAnchor, AxisFlag flag) {
		Main.mountRect = new Rectangle(0,0,0,0);
		int dist = 0;
		
		for (FTLRoom r : rooms) {
			dist = newAnchor.x - anchor.x;
			r.rect.x += (flag == AxisFlag.X || flag == AxisFlag.BOTH) ? dist : 0;
			dist = newAnchor.y - anchor.y;
			r.rect.y += (flag == AxisFlag.Y || flag == AxisFlag.BOTH) ? dist : 0;
		}
		for (FTLDoor d : doors) {
			dist = newAnchor.x - anchor.x;
			d.rect.x += (flag == AxisFlag.X || flag == AxisFlag.BOTH) ? dist : 0;
			dist = newAnchor.y - anchor.y;
			d.rect.y += (flag == AxisFlag.Y || flag == AxisFlag.BOTH) ? dist : 0;
		}
		if (Main.ship != null && Main.ship.imageRect != null) {
			dist = newAnchor.x - anchor.x;
			Main.ship.imageRect.x += (flag == AxisFlag.X || flag == AxisFlag.BOTH) ? dist : 0;
			dist = newAnchor.y - anchor.y;
			Main.ship.imageRect.y += (flag == AxisFlag.Y || flag == AxisFlag.BOTH) ? dist : 0;
		}
		for (FTLMount m : mounts) {
			dist = newAnchor.x - anchor.x;
			m.rect.x += (flag == AxisFlag.X || flag == AxisFlag.BOTH) ? dist : 0;
			dist = newAnchor.y - anchor.y;
			m.rect.y += (flag == AxisFlag.Y || flag == AxisFlag.BOTH) ? dist : 0;
			
			m.rect.width = (m.rotate) ? (FTLMount.MOUNT_WIDTH) : (FTLMount.MOUNT_HEIGHT);
			m.rect.height = (m.rotate) ? (FTLMount.MOUNT_HEIGHT) : (FTLMount.MOUNT_WIDTH);
		}
	}
	
	public void updateMount(FTLMount m) {
		m.rect.x = (Main.hullImage != null) ? (anchor.x + offset.x*35 + Main.mountRect.x) : (Main.mountRect.x);
		m.rect.y = (Main.hullImage != null) ? (anchor.y + offset.y*35 + Main.mountRect.y) : (Main.mountRect.y);
		/*
		if (Main.snapMountsToHull) {
			m.rect.x = (Main.hullImage != null) ? (anchor.x + offset.x*35 + Main.mountRect.x) : (Main.mountRect.x);
			m.rect.y = (Main.hullImage != null) ? (anchor.y + offset.y*35 + Main.mountRect.y) : (Main.mountRect.y);
		} else {
			m.rect.x = (Main.hullImage != null) ? (anchor.x + offset.x*35 + Main.mountRect.x) : (Main.mountRect.x);
			m.rect.y = (Main.hullImage != null) ? (anchor.y + offset.y*35 + Main.mountRect.y) : (Main.mountRect.y);
		}*/
		m.pos.x = m.rect.x + m.rect.width - imageRect.x;
		m.pos.y = m.rect.y + m.rect.height - imageRect.y;
		
		m.rect.x -= (m.rotate) ? (FTLMount.MOUNT_WIDTH/2) : (FTLMount.MOUNT_HEIGHT/2);
		m.rect.y -= (m.rotate) ? (FTLMount.MOUNT_HEIGHT/2) : (FTLMount.MOUNT_WIDTH/2);
		m.rect.width = (m.rotate) ? (FTLMount.MOUNT_WIDTH) : (FTLMount.MOUNT_HEIGHT);
		m.rect.height = (m.rotate) ? (FTLMount.MOUNT_HEIGHT) : (FTLMount.MOUNT_WIDTH);
	}
	
	/**
	 * Computes the size of the ship (smallest rectangle in which the entire ship will fit), not the end point of the ship.
	 * @return Point consisting of the ship's width and height.
	 */
	public Point computeShipSize() {
		Point start = findLowBounds();
		Point end = findHighBounds();
		
		start.x = end.x - start.x;
		start.y = end.y - start.y;
		return start;
	}
	
	/**
	 * @return FTLRoom closest to the top left corner of the drawing field.
	 */
	public FTLRoom findRoomClosestToOrigin() {
		int x=1000, y=1000;
		FTLRoom rm = null;
		for (FTLRoom r : rooms) {
			if (r!=null && r.rect.x < x && r.rect.y < y) {
				x = r.rect.x;
				y = r.rect.y;
				rm = r;
			}
		}
		return rm;
	}
	
	/** 
	 * Finds the low bounds of the ship (closest to top left corner).
	 * @return Point consisting of lowest x and y values at which the ship's rooms start.
	 */
	public Point findLowBounds() {
		int x=1000, y=1000;
		for (FTLRoom r : rooms) {
			if (r!=null && r.rect.x <= x) {
				x = r.rect.x;
			}
			if (r!=null && r.rect.y <= y) {
				y = r.rect.y;
			}
		}
		
		x=(y==1000) ? 0 : x;
		y=(y==1000) ? 0 : y;
		
		return new Point(x, y);
	}
	
	/**
	 * Finds the high bounds of the ship (closest to bottom right corner).
	 * @return Point consisting of highest x and y values at which the ship's rooms end.
	 */
	public Point findHighBounds() {
		int x=0, y=0;
		for (FTLRoom r : rooms) {
			if (r!=null && r.rect.x+r.rect.width >= x) {
				x = r.rect.x+r.rect.width;
			}
			if (r!=null && r.rect.y+r.rect.height >= y) {
				y = r.rect.y+r.rect.height;
			}
		}
		return new Point(x, y);
	}
	
	public int findLeftRoom(FTLDoor d) {
		for (FTLRoom r : rooms) {
			if (r.rect.intersects(d.rect)) {
				if (r.rect.intersects(d.rect) && ((d.horizontal && r.rect.y < d.rect.y) || (!d.horizontal && r.rect.x < d.rect.x))) {
					//debug("left: ("+d.rect.x/35 + ", "+d.rect.y/35 + ") -> " + r.id);
					return r.id;
				}
			}
		}
		//debug("left: ("+d.rect.x/35 + ", "+d.rect.y/35 + ") -> " + -1);
		return -1;
	}
	
	public int findRightRoom(FTLDoor d) {
		for (FTLRoom r : rooms) {
			if (r.rect.intersects(d.rect)) {
				if (r.rect.intersects(d.rect) && ((d.horizontal && r.rect.y > d.rect.y) || (!d.horizontal && r.rect.x > d.rect.x))) {
					//debug("right: ("+d.rect.x/35 + ", "+d.rect.y/35 + ") -> " + r.id);
					return r.id;
				}
			}
		}
		//debug("right: ("+d.rect.x/35 + ", "+d.rect.y/35 + ") -> " + -1);
		return -1;
	}
	
	public FTLRoom getRoomWithId(int id) {
		for (FTLRoom r : rooms) {
			if (r!=null && id != -1 && r.id == id) {
				return r;
			}
		}
		return null;
	}
	
	public void drawShipAnchor(PaintEvent e) {
		Color c;
		e.gc.setAlpha(255);
		e.gc.setLineWidth(2);
		
		// anchor bounds lines
		c = e.display.getSystemColor(SWT.COLOR_GREEN);
		e.gc.setForeground(c);
		c.dispose();
		e.gc.drawLine(anchor.x, anchor.y, anchor.x, Main.GRID_H*35);
		c = e.display.getSystemColor(SWT.COLOR_RED);
		e.gc.setForeground(c);
		c.dispose();
		e.gc.drawLine(anchor.x, anchor.y, Main.GRID_W*35, anchor.y);

		if (Main.moveAnchor) {
			c = e.display.getSystemColor(SWT.COLOR_DARK_CYAN);
			e.gc.setBackground(c);
			c.dispose();
		} else {
			c = e.display.getSystemColor(SWT.COLOR_CYAN);
			e.gc.setBackground(c);
			c.dispose();
		}
		c = e.display.getSystemColor(SWT.COLOR_BLACK);
		e.gc.setForeground(c);
		c.dispose();
		
		Point p = new Point(anchor.x, anchor.y);
		if (anchor.x != 0)
			p.x -=ANCHOR;
		if (anchor.y != 0)
			p.y -=ANCHOR;
		
		e.gc.fillRectangle(p.x, p.y, ANCHOR,ANCHOR);
		e.gc.drawRectangle(p.x, p.y, ANCHOR,ANCHOR);
	}
	
	public void reassignID() {
		Main.idList.clear();
		int i = 0;
		for (FTLRoom r : rooms) {
			r.id = i;
			Main.idList.add(i);
			i++;
		}
	}
}
