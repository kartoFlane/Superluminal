package com.kartoflane.superluminal.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.elements.Anchor;
import com.kartoflane.superluminal.painter.PaintBox;

public class UndoableOffsetEdit extends AbstractUndoableEdit {
	private static final long serialVersionUID = -3165329298301115013L;

	private PaintBox box;
	private Point old;
	private Point current;
	
	public UndoableOffsetEdit(PaintBox box) {
		this.box = box;
		old = new Point(Main.ship.offset.x, Main.ship.offset.y);
	}
	
	public void setOldOffset(int x, int y) {
		old = new Point(x, y);
	}
	
	public void setCurrentOffset(int x, int y) {
		current = new Point(x, y);
	}
	
	public Point getCurrentOffset() {
		return current;
	}
	
	public Point getOldOffset() {
		return old;
	}
	
	public String getPresentationName() {
		return "modify offset";
	}
	
	public void undo() throws CannotUndoException {
		super.undo();
		Point size = Main.ship.findLowBounds();
		int x = size.x - old.x * 35;
		int y = size.y - old.y * 35;
		((Anchor) box).setLocation(x, y, false);
		Main.ship.anchor.x = x;
		Main.ship.anchor.y = y;
		Main.canvas.redraw();
	}
	
	public void redo() throws CannotRedoException {
		super.redo();
		Point size = Main.ship.findLowBounds();
		int x = size.x - current.x * 35;
		int y = size.y - current.y * 35;
		((Anchor) box).setLocation(x, y, false);
		Main.ship.anchor.x = x;
		Main.ship.anchor.y = y;
		Main.canvas.redraw();
	}
}

