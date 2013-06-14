package com.kartoflane.superluminal.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.elements.FTLRoom;
import com.kartoflane.superluminal.elements.HullBox;
import com.kartoflane.superluminal.elements.ShieldBox;
import com.kartoflane.superluminal.painter.PaintBox;

public class UndoableImageEdit extends AbstractUndoableEdit {
	private static final long serialVersionUID = -7325290746851080827L;

	private PaintBox box;
	private String old;
	private String current;
	private int undoable;

	public UndoableImageEdit(PaintBox box, int undoable) {
		this.box = box;
		String temp = null;
		this.undoable = undoable;

		if (box instanceof FTLRoom) {
			temp = ((FTLRoom) box).interiorData.interiorPath;
			old = temp != null ? new String(temp) : null;
		} else if (box instanceof HullBox) {
			if (undoable == Undoable.IMAGE) {
				temp = ((HullBox) box).getPath();
			} else if (undoable == Undoable.FLOOR) {
				temp = ((HullBox) box).floorPath;
			} else if (undoable == Undoable.CLOAK) {
				temp = ((HullBox) box).cloakPath;
			}
			old = temp != null ? new String(temp) : null;
		} else if (box instanceof ShieldBox) {
			temp = ((ShieldBox) box).getPath();
			old = temp != null ? new String(temp) : null;
		}
	}

	public String getOldValue() {
		return old;
	}

	public String getCurrentValue() {
		return current;
	}

	public void setOldValue(String path) {
		this.old = path != null ? new String(path) : null;
	}

	public void setCurrentValue(String path) {
		this.current = path != null ? new String(path) : null;
	}

	public String getPresentationName() {
		if (box instanceof FTLRoom) {
			return String.format("modify %s'" + (!"%s".endsWith("s") ? "s" : "") + " image", ((FTLRoom) box).getSystem().toString().toLowerCase());
		} else {
			return String.format("modify %s's image", box.getClass().getSimpleName());
		}
	}

	public void undo() throws CannotUndoException {
		super.undo();
		if (box instanceof FTLRoom) {
			((FTLRoom) box).setInterior(old);
		} else if (box instanceof HullBox) {
			Rectangle temp = new Rectangle(0, 0, 0, 0);
			Main.copyRect(Main.hullBox.getBounds(), temp);

			if (undoable == Undoable.IMAGE) {
				Main.hullBox.setHullImage(old);
	
				Main.ship.imageRect.width = Main.hullBox.getBounds().width;
				Main.ship.imageRect.height = Main.hullBox.getBounds().height;
			} else if (undoable == Undoable.FLOOR) {
				Main.hullBox.setFloorImage(old);
			} else if (undoable == Undoable.CLOAK) {
				Main.hullBox.setCloakImage(old);
				Main.btnCloaked.setEnabled(old != null);
			}

			Main.canvasRedraw(temp, true);
			Main.canvasRedraw(Main.hullBox.getBounds(), true);
			Main.updateButtonImg();
		} else if (box instanceof ShieldBox) {
			Rectangle temp = new Rectangle(0, 0, 0, 0);
			Main.copyRect(Main.shieldBox.getBounds(), temp);

			Main.shieldBox.setImage(old, true);

			Main.canvasRedraw(temp, true);
			Main.canvasRedraw(Main.shieldBox.getBounds(), true);
			Main.updateButtonImg();
		}
	}

	public void redo() throws CannotRedoException {
		super.redo();
		if (box instanceof FTLRoom) {
			((FTLRoom) box).setInterior(current);
		} else if (box instanceof HullBox) {
			Rectangle temp = new Rectangle(0, 0, 0, 0);
			Main.copyRect(Main.hullBox.getBounds(), temp);

			if (undoable == Undoable.IMAGE) {
				Main.hullBox.setHullImage(current);
				
				Main.ship.imageRect.width = Main.hullBox.getBounds().width;
				Main.ship.imageRect.height = Main.hullBox.getBounds().height;
			} else if (undoable == Undoable.FLOOR) {
				Main.hullBox.setFloorImage(current);
			} else if (undoable == Undoable.CLOAK) {
				Main.hullBox.setCloakImage(current);
				Main.btnCloaked.setEnabled(current != null);
			}

			Main.canvasRedraw(temp, true);
			Main.canvasRedraw(Main.hullBox.getBounds(), true);
			Main.updateButtonImg();
		} else if (box instanceof ShieldBox) {
			Rectangle temp = new Rectangle(0, 0, 0, 0);
			Main.copyRect(Main.shieldBox.getBounds(), temp);

			Main.shieldBox.setImage(current, true);

			Main.canvasRedraw(temp, true);
			Main.canvasRedraw(Main.shieldBox.getBounds(), true);
			Main.updateButtonImg();
		}
	}
}
