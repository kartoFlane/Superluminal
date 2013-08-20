package com.kartoflane.superluminal.undo;

/**
 * All undoable edits should fall into one of the categories below, defining the Edit class that defines their undo/redo behaviour.
 * 
 * @author kartoFlane
 *
 */
public class Undoable {
	public static final int MOVE = 0;
	public static final int RESIZE = 1;
	public static final int PIN = 2; // unused
	/** System power/level, gib properties, ship properties */
	public static final int MODIFY = 3;
	/** Assign/unassign system to/from room */
	public static final int ASSIGN_SYSTEM = 4;
	/** Mount rotate flag */
	public static final int ROTATE = 5;
	/** System stations direction, mount slide direction */
	public static final int DIRECTION = 6;
	/** Ex. mount mirror flag */
	public static final int FACING = 7;
	/** System stations */
	public static final int SLOT = 8;
	/** Room interior, hull graphic, etc */
	public static final int IMAGE = 9;
	/** Anchor horizontal/vertical offset */
	public static final int OFFSET = 10;
	public static final int FLOOR = 11;
	public static final int CLOAK = 12;
	/** Door linking, left/top ID */
	public static final int LINK_LEFT = 13;
	/** Door linking, right/bottom ID */
	public static final int LINK_RIGHT = 14;
	/** Gib layering */
	public static final int LAYER = 15; // unused
	public static final int CREATE_ROOM = 16;
	public static final int CREATE_DOOR = 17;
	public static final int CREATE_MOUNT = 18;
	public static final int CREATE_GIB = 19;
	/** Room splitting */
	public static final int SPLIT = 20;
	
	public static final int DOOR_PROP = 21;
	public static final int MOUNT_PROP = 22;
	// TODO list all feasible undoable edits
}
