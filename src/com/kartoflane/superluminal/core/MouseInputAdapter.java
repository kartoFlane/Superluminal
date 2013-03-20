package com.kartoflane.superluminal.core;
import com.kartoflane.superluminal.elements.DraggableBox;
import com.kartoflane.superluminal.elements.FTLDoor;
import com.kartoflane.superluminal.elements.FTLGib;
import com.kartoflane.superluminal.elements.FTLMount;
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
	public DraggableBox dragee = null;

	/**
	 * @param layerIds layers to be ignored by selection
	 */
	public MouseInputAdapter(Integer[] layerIds) {
		Integer[] allLayerIds = Main.layeredPainter.getLayers();
		selectableLayerIds = new Integer[allLayerIds.length];

		for (int i=0; i < allLayerIds.length; i++) {
			// Leave uninteresting layers as null.
			if (layerIds == null || !containsLayer(layerIds, allLayerIds[i])) {
				selectableLayerIds[i] = allLayerIds[i];
			}
		}
	}
	
	private boolean containsLayer(Integer[] layerIds, Integer layer) {
		for (int i=0; i < layerIds.length; i++) {
			if (layerIds[i].equals(layer)) return true;
		}
		return false;
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (e.button == 1) Main.leftMouseDown = true;
		if (e.button == 3) Main.rightMouseDown = true;
		
		if (Main.ship == null) return;
		
		Main.cursor.mouseDown(e);
		
		if (Main.tltmPointer.getSelection()) {
			Main.tooltip.setVisible(false);
			Main.anchor.mouseDown(e);
			
			if (!Main.anchor.moveAnchor && !Main.anchor.moveVertical) {
				for (int i=selectableLayerIds.length-1; i >= 0; i--) {
					if (selectableLayerIds[i] != null) {
						dragee = (DraggableBox) Main.layeredPainter.getBoxAt(e.x, e.y, selectableLayerIds[i]);
						if (dragee != null && dragee.isVisible() && (Main.getMountFromPoint(e.x, e.y)==null || !Main.showMounts || selectableLayerIds[i] != LayeredPainter.HULL)) {
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
			if (!(dragee instanceof FTLDoor)) {
				if (Main.selectedDoor != null) Main.selectedDoor.deselect();
				Main.selectedDoor = null;
			}
			if (!(dragee instanceof FTLMount)) {
				if (Main.selectedMount != null) Main.selectedMount.deselect();
				Main.selectedMount = null;
			}
			if (!Main.hullBox.move && Main.hullSelected) {
				Main.hullBox.deselect();
				Main.hullSelected = false;
			}
			if (!Main.shieldBox.move && Main.shieldSelected) {
				Main.shieldBox.deselect();
				Main.shieldSelected = false;
			}
			
			Main.updateSelectedPosText();
			
		} else if (Main.tltmGib.getSelection()) {
			if (e.button==1 && !Main.gibWindow.isVisible()) {
				Integer[] layers = {LayeredPainter.MOUNT, LayeredPainter.GIB};
				for (int i=0; i <= layers.length-1; i++) {
					if (layers[i] != null) {
						dragee = (DraggableBox) Main.layeredPainter.getBottomBoxAt(e.x, e.y, layers[i]);
						if (dragee != null && dragee.isVisible()) {
							dragee.mouseDown(e);
							break;
						} else {
							if (Main.selectedMount != null) Main.selectedMount.deselect();
							Main.selectedMount = null;
							for (FTLGib g : Main.ship.gibs)
								g.deselect();
						}
					}
				}
			} else if (e.button==3) {
				for (FTLMount m : Main.ship.mounts) m.mouseDown(e);
				if (Main.selectedGib != null) Main.selectedGib.mouseDown(e);
			}
		}
	}

	@Override
	public void mouseMove(MouseEvent e) {
		Main.mousePos.x = e.x;
		Main.mousePos.y = e.y;
		Main.tooltip.setVisible(false);
		
		Main.cursor.mouseMove(e);
		
		if (Main.ship == null) return;
		
		Main.anchor.mouseMove(e);
		Main.hullBox.mouseMove(e);
		Main.shieldBox.mouseMove(e);
		if (Main.ship != null) {
			for (FTLRoom r : Main.ship.rooms) {
				r.mouseMove(e);
			}
			for (FTLDoor d : Main.ship.doors) {
				d.mouseMove(e);
			}
			for (FTLMount m : Main.ship.mounts) {
				m.mouseMove(e);
			}
			if (Main.tltmGib.getSelection())
				for (FTLGib g : Main.ship.gibs)
					g.mouseMove(e);
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {
		if (e.button == 1) Main.leftMouseDown = false;
		if (e.button == 3) Main.rightMouseDown = false;
		
		if (Main.ship == null) return;
		
		Main.cursor.mouseUp(e);
		
		if (Main.tltmPointer.getSelection()) {
			Main.anchor.mouseUp(e);
			Main.hullBox.mouseUp(e);
			Main.shieldBox.mouseUp(e);
			
			if (Main.ship != null) {
				for (FTLRoom r : Main.ship.rooms) {
					if (r.isVisible())
						r.mouseUp(e);
				}
				for (FTLDoor d : Main.ship.doors) {
					if (d.isVisible())
						d.mouseUp(e);
				}
				for (FTLMount m : Main.ship.mounts) {
					if (m.isVisible())
						m.mouseUp(e);
				}
			}
			
			Main.cursor.setVisible(true);
			Main.canvasRedraw(Main.cursor.getBounds(), false);
		} else if (Main.tltmGib.getSelection()) {
			if (Main.selectedMount != null) Main.selectedMount.mouseUp(e);
			for (FTLGib g : Main.ship.gibs)
				g.mouseUp(e);
		}
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
	public void mouseScrolled(MouseEvent e) {}

	@Override
	public void mouseHover(MouseEvent e) {}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		if (Main.tltmPointer.getSelection()) {
			if (Main.ship != null) {
				for (FTLRoom r : Main.ship.rooms) {
					r.mouseDoubleClick(e);
				}
			}
			if (e.button == 1 && dragee != null && !(dragee instanceof FTLRoom)) {
				dragee.mouseHover(e);
			}
		} else if (Main.tltmGib.getSelection()) {
			if (Main.selectedGib != null) Main.selectedGib.mouseDoubleClick(e);
			if (Main.selectedMount != null) Main.selectedMount.mouseHover(e);
		}
	}
	
	public static Rectangle cloneRect(Rectangle rect) {
		return new Rectangle(rect.x, rect.y, rect.width, rect.height);
	}
}
