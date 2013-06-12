package com.kartoflane.superluminal.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.elements.FTLDoor;
import com.kartoflane.superluminal.elements.FTLMount;
import com.kartoflane.superluminal.painter.PaintBox;


public class UndoableMoveEdit extends AbstractUndoableEdit {
	private static final long serialVersionUID = -3165329298301115013L;

	private PaintBox box;
	private Point oldPos;
	private Point currentPos;
	
	public UndoableMoveEdit(PaintBox box) {
		this.box = box;
		oldPos = new Point(box.getBounds().x, box.getBounds().y);
	}
	
	public PaintBox getBox() {
		return box;
	}
	
	public void setOldPos(int x, int y) {
		oldPos = new Point(x, y);
	}
	
	public void setCurrentPos(int x, int y) {
		currentPos = new Point(x, y);
	}
	
	public Point getCurrentPos() {
		return currentPos;
	}
	
	public Point getOldPos() {
		return oldPos;
	}
	
	public String getPresentationName() {
		return "move " + box.getClass().getSimpleName();
	}
	
	public void undo() throws CannotUndoException {
		super.undo();
		if (box instanceof FTLMount) {
			((FTLMount) box).setLocationAbsolute(oldPos.x, oldPos.y);
		} else if (box instanceof FTLDoor) {
			((FTLDoor) box).setLocationAbsolute(oldPos.x + 15, oldPos.y); // is shifted one grid cell to the left otherwise
		} else {
			box.setLocation(oldPos.x, oldPos.y);
		}
		Main.canvas.redraw();
	}
	
	public void redo() throws CannotRedoException {
		super.redo();
		if (box instanceof FTLMount) {
			((FTLMount) box).setLocationAbsolute(currentPos.x, currentPos.y);
		} else if (box instanceof FTLDoor) {
			((FTLDoor) box).setLocationAbsolute(currentPos.x + 15, currentPos.y); // is shifted one grid cell to the left otherwise
		} else {
			box.setLocation(currentPos.x, currentPos.y);
		}
		Main.canvas.redraw();
	}
}
