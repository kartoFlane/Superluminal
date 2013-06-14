package com.kartoflane.superluminal.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

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
	
	public UndoableImageEdit(PaintBox box) {
		this.box = box;
		
		if (box instanceof FTLRoom) {
			old = new String(((FTLRoom) box).interiorData.interiorPath);
		} else if (box instanceof HullBox) {
			old = new String(((HullBox) box).getPath());
		} else if (box instanceof ShieldBox) {
			old = new String(((ShieldBox) box).getPath());
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
			return String.format("modify %s'" +(!"%s".endsWith("s") ? "s" : "")+ " image", ((FTLRoom) box).getSystem().toString().toLowerCase());
		} else {
			return String.format("modify %s's image", box.getClass().getSimpleName());
		}
	}
	
	public void undo() throws CannotUndoException {
		super.undo();
		if (box instanceof FTLRoom) {
			((FTLRoom) box).setInterior(old);
		} else if (box instanceof HullBox) {
			Main.hullBox.setHullImage(old);

			Main.ship.imageRect.width = Main.hullBox.getBounds().width;
			Main.ship.imageRect.height = Main.hullBox.getBounds().height;

			Main.canvasRedraw(Main.hullBox.getBounds(), true);
		} else if (box instanceof ShieldBox) {
			Main.shieldBox.setImage(old, true);

			Main.canvasRedraw(Main.shieldBox.getBounds(), true);
		}
	}
	
	public void redo() throws CannotRedoException {
		super.redo();
		if (box instanceof FTLRoom) {
			((FTLRoom) box).setInterior(current);
		} else if (box instanceof HullBox) {
			Main.hullBox.setHullImage(current);

			Main.ship.imageRect.width = Main.hullBox.getBounds().width;
			Main.ship.imageRect.height = Main.hullBox.getBounds().height;

			Main.canvasRedraw(Main.hullBox.getBounds(), true);
		} else if (box instanceof ShieldBox) {
			Main.shieldBox.setImage(current, true);

			Main.canvasRedraw(Main.shieldBox.getBounds(), true);
		}
	}
}
