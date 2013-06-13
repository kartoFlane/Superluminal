package com.kartoflane.superluminal.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.elements.FTLRoom;
import com.kartoflane.superluminal.painter.PaintBox;


public class UndoableResizeEdit extends AbstractUndoableEdit {
	private static final long serialVersionUID = -3165329298301115013L;

	private PaintBox box;
	private Rectangle oldBounds = new Rectangle(0,0,0,0);
	private Rectangle currentBounds = new Rectangle(0,0,0,0);
	
	public UndoableResizeEdit(PaintBox box) {
		this.box = box;
		Main.copyRect(box.getBounds(), oldBounds);
	}
	
	public void setCurrentBounds(Rectangle rect) {
		Main.copyRect(rect, currentBounds);
	}
	
	public Rectangle getCurrentBounds() {
		return currentBounds;
	}
	
	public Rectangle getOldBounds() {
		return oldBounds;
	}
	
	public String getPresentationName() {
		return "resize " + box.getClass().getSimpleName();
	}
	
	public void undo() throws CannotUndoException {
		super.undo();
		box.setSize(oldBounds.width, oldBounds.height);
		box.setLocation(oldBounds.x, oldBounds.y);
		if (box instanceof FTLRoom) {
			((FTLRoom) box).updateCorners();
		}
	}
	
	public void redo() throws CannotRedoException {
		super.redo();
		box.setSize(currentBounds.width, currentBounds.height);
		box.setLocation(currentBounds.x, currentBounds.y);
		if (box instanceof FTLRoom) {
			((FTLRoom) box).updateCorners();
		}
	}
}
