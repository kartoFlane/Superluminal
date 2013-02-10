package com.kartoflane.superluminal.core;
import com.kartoflane.superluminal.elements.CursorBox;
import com.kartoflane.superluminal.elements.DraggableBox;
import com.kartoflane.superluminal.elements.FTLDoor;
import com.kartoflane.superluminal.elements.FTLRoom;
import com.kartoflane.superluminal.painter.ImageBox;
import com.kartoflane.superluminal.painter.LayeredPainter;
import com.kartoflane.superluminal.painter.PaintBox;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Rectangle;


public class MouseInputAdapter implements MouseListener, MouseMoveListener, MouseTrackListener, MouseWheelListener {
	private Integer[] selectableLayerIds;
	private Rectangle oldBounds;
	private DraggableBox dragee = null;
	private int offsetX = 0, offsetY = 0;
	private boolean moved = false;
	private int origX = 0, origY = 0; // Idea: Remember pre-drag pos to revert bad drops.

	/**
	 * 
	 * @param layerIds layers to be ignored by selection
	 */
	public MouseInputAdapter(Integer[] layerIds) {
		Integer[] allLayerIds = Main.layeredPainter.getLayers();
		selectableLayerIds = new Integer[allLayerIds.length];

		for (int i=0; i < allLayerIds.length; i++) {
			// Leave uninteresting layers as null.
			if (layerIds == null || !isLayerContained(allLayerIds[i], layerIds)) {
				selectableLayerIds[i] = allLayerIds[i];
			}
		}
	}
	
	private boolean isLayerContained(Integer layer, Integer[] layerIds) {
		for (int i=0; i < layerIds.length; i++) {
			if (layerIds[i].equals(layer)) return true;
		}
		return false;
	}

	@Override
	public void mouseDown(MouseEvent e) {
		Main.anchor.mouseDown(e);
		
		if (!Main.anchor.moveAnchor && !Main.anchor.moveVertical) {
			for (int i=selectableLayerIds.length-1; i >= 0; i--) {
				if (selectableLayerIds[i] != null) {
					dragee = (DraggableBox) Main.layeredPainter.getBoxAt(e.x, e.y, selectableLayerIds[i]);
					if (dragee != null) {
						dragee.mouseDown(e);
						break;
					}
				}
			}
		}
		if (!(dragee instanceof FTLRoom)) {
			if (Main.selectedRoom != null) Main.selectedRoom.deselect();
			Main.selectedRoom = null;
		}
		/*
		if (Main.ship != null) {
			for (FTLRoom r : Main.ship.rooms) {
				r.mouseDown(e);
			}
			for (FTLDoor d : Main.ship.doors) {
				d.mouseDown(e);
			}
		}
		*/
		// Idea: Extra criteria to detect unselectable objects and return.
		/*
		setSelectedBox(dragee);
		if (dragee != null) {
			Rectangle drageeBounds = dragee.getBounds();
			origX = drageeBounds.x;
			origY = drageeBounds.y;
			offsetX = e.x-drageeBounds.x;
			offsetY = e.y-drageeBounds.y;
			cursorBox.setVisible(false);
			canvasRedraw(cursorBox.getBounds(), false);
		}
		*/
	}

	@Override
	public void mouseMove(MouseEvent e) {
		Main.mousePos.x = e.x;
		Main.mousePos.y = e.y;
		
		Main.anchor.mouseMove(e);
		Main.cursor.mouseMove(e);
		if (Main.ship != null) {
			for (FTLRoom r : Main.ship.rooms) {
				r.mouseMove(e);
			}
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {
		Main.anchor.mouseUp(e);
		Main.cursor.mouseUp(e);
		if (Main.ship != null) {
			for (FTLRoom r : Main.ship.rooms) {
				r.mouseUp(e);
			}
		}
		
		// Idea: Math.min()/Math.max() x,y to limit draggable region.
		
		Main.cursor.setVisible(true);
		Main.canvasRedraw(Main.cursor.getBounds(), false);
	}

	@Override
	public void mouseEnter(MouseEvent e) {
		repositionBox(Main.cursor, e.x-Main.cursor.getBounds().width/2, e.y-Main.cursor.getBounds().height/2);
		Main.cursor.setVisible(true);
		Main.canvasRedraw(Main.cursor.getBounds(), false);
	}

	@Override
	public void mouseExit(MouseEvent e) {
		if (Main.cursor.isVisible()) {
			Main.cursor.setVisible(false);
			Main.canvasRedraw(Main.cursor.getBounds(), false);
		}
	}

	/** Set a PaintBox's location, redrawing immediately, if visible. */
	private void repositionBox(PaintBox box, int x, int y) {
		if (box.isVisible()) {
			Rectangle boxBounds;
			if (box instanceof ImageBox) {
				boxBounds = ((ImageBox)box).getRedrawBounds();
			} else {
				boxBounds = box.getBounds();
			}
			oldBounds = cloneRect(boxBounds);
			
			box.setLocation(x, y);
			box.setVisible(Main.canvas.getBounds().contains(box.getLocation()));

			Main.canvas.redraw(oldBounds.x-box.getBorderThickness()/2, oldBounds.y-box.getBorderThickness()/2, oldBounds.width-1+box.getBorderThickness(), oldBounds.height-1+box.getBorderThickness(), false);
			Main.canvasRedraw(boxBounds, false);
		} else {
			box.setLocation(x, y);
		}
	}

	@Override
	public void mouseScrolled(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseHover(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDoubleClick(MouseEvent e)
	{
		if (Main.ship != null) {
			for (FTLRoom r : Main.ship.rooms) {
				r.mouseDoubleClick(e);
			}
		}
	}
	
	public static Rectangle cloneRect(Rectangle rect) {
		return new Rectangle(rect.x, rect.y, rect.width, rect.height);
	}
}
