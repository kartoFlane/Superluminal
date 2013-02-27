package com.kartoflane.superluminal.painter;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal.core.Main;

/**
 * @author Vhati
 * @author kartoFlane - minor modifications
 */
public class LayeredPainter implements PaintListener {
	public static final Integer SHIELD = new Integer(0);
	public static final Integer MOUNT = new Integer(1);
	public static final Integer HULL = new Integer(2);
	public static final Integer GRID = new Integer(3);
	public static final Integer ROOM = new Integer(4);
	public static final Integer ROOM_INTERIOR = new Integer(5);
	public static final Integer SYSTEM_ICON = new Integer(6);
	public static final Integer DOOR = new Integer(7);
	public static final Integer GIB = new Integer(8);
	public static final Integer OVERLAY = new Integer(9);
	public static final Integer ANCHOR = new Integer(10);
	public static final Integer SELECTION = new Integer(11);
	private static Integer[] layerIds = {SHIELD, MOUNT, HULL, GRID, ROOM, ROOM_INTERIOR, SYSTEM_ICON, DOOR, GIB, OVERLAY, ANCHOR, SELECTION};
	
	private HashMap<Integer, ArrayList<PaintBox>> layerMap = new HashMap<Integer, ArrayList<PaintBox>>();
	
	private boolean suppress = false;

	public LayeredPainter() {
		// Add a bunch of empty lists to hold layers.
		for (Integer layerId : layerIds) {
			layerMap.put(layerId, new ArrayList<PaintBox>());
		}
	}

	public void add(PaintBox box, Integer layer) {
		ArrayList<PaintBox> boxList = layerMap.get(layer);
		boxList.add(box);
	}
	
	public void addAsFirst(PaintBox box, Integer layer) {
		ArrayList<PaintBox> boxList = layerMap.get(layer);
		boxList.add(0, box);
	}

	public void remove(PaintBox box) {
		for (Integer layerId : layerIds) {
			if (layerMap.get(layerId).remove(box) == true) break;
		}
	}
	
	public Integer[] getLayers() {
		return layerIds;
	}
	
	public HashMap<Integer, ArrayList<PaintBox>> getLayerMap() {
		return layerMap;
	}

	/**
	 * Returns the topmost visible PaintBox in a layer at a point, or null.
	 * For multiple layers, remember to loop through layerIds backward.
	 */
	public PaintBox getBoxAt(int x, int y, Integer layerId) {
		for (PaintBox box : layerMap.get(layerId)) {
			if (box.isVisible() && box.getBounds().contains(x, y)) {
				return box;
			}
		}
		return null;
	}

	/**
	 * Returns the bottommost visible PaintBox in a layer at a point, or null.
	 */
	public PaintBox getBottomBoxAt(int x, int y, Integer layerId) {
		PaintBox tempBox = null;
		for (PaintBox box : layerMap.get(layerId)) {
			if (box.isVisible() && box.getBounds().contains(x, y)) {
				tempBox = box;
			}
		}
		return tempBox;
	}

	public void paintControl(PaintEvent e) {
		if (!suppress) {
			Rectangle dirtyRect = new Rectangle(e.x-2, e.y-2, e.width+4, e.height+4);
			e.gc.setFont(Main.appFont);
	
			for (Integer layerId : layerIds) {
				for (PaintBox box : layerMap.get(layerId)) {
					if (box.getBounds().intersects(dirtyRect)) {
						box.redraw(e);
					}
				}
				if (Main.tltmGib.getSelection() && Main.selectedGib != null)
					Main.selectedGib.paintOverlay(e);
			}
		}
		e.gc.dispose();
	}
	
	/**
	 * Allows to suppress the painter, preventing it from performing drawing.
	 */
	public void setSuppressed(boolean suppress) {
		this.suppress = suppress;
	}
	
	public boolean isSuppressed() {
		return suppress;
	}
}
