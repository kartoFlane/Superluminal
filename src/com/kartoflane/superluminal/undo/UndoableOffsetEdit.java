package com.kartoflane.superluminal.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.elements.Anchor;
import com.kartoflane.superluminal.painter.PaintBox;

/**
 * Used for ship's X/Y offset values, but also for HORIZONTAL/VERTICAL precise offsets.
 * 
 * @author kartoFlane
 * 
 */
public class UndoableOffsetEdit extends AbstractUndoableEdit {
	private static final long serialVersionUID = -3165329298301115013L;

	private PaintBox box;
	private Point old = new Point(0, 0);
	private Point current = new Point(0, 0);
	private int undoable;

	public UndoableOffsetEdit(PaintBox box, int undoable) {
		this.box = box;
		this.undoable = undoable;
		if (undoable == Undoable.MOVE) {
			old.x = Main.ship.offset.x;
			old.y = Main.ship.offset.y;
		} else {
			old.x = Main.ship.horizontal;
			old.y = Main.ship.vertical;
		}
	}

	/**
	 * For precise offsets, x holds HORIZONTAL and y holds VERTICAL.
	 */
	public void setOldOffset(int x, int y) {
		old.x = x;
		old.y = y;
	}

	/**
	 * For precise offsets, x holds HORIZONTAL and y holds VERTICAL.
	 */
	public void setCurrentOffset(int x, int y) {
		current.x = x;
		current.y = y;
	}

	/**
	 * For precise offsets, x holds HORIZONTAL and y holds VERTICAL.
	 */
	public Point getCurrentOffset() {
		return current;
	}

	/**
	 * For precise offsets, x holds HORIZONTAL and y holds VERTICAL.
	 */
	public Point getOldOffset() {
		return old;
	}

	public String getPresentationName() {
		if (undoable == Undoable.MOVE) {
			return "modify ship offset";
		} else {
			return "modify precise offset";
		}
	}

	public void undo() throws CannotUndoException {
		super.undo();
		if (undoable == Undoable.MOVE) {
			Point size = Main.ship.findLowBounds();
			int x = size.x - old.x * 35;
			int y = size.y - old.y * 35;
			((Anchor) box).setLocation(x, y, false);
			Main.ship.anchor.x = x;
			Main.ship.anchor.y = y;
			Main.canvas.redraw();
		} else {
			Main.ship.horizontal = old.x;
			Main.ship.vertical = old.y;
			if (Main.ship.horizontal > 0) {
				Main.anchor.getBounds().x -= Main.ship.horizontal;
				Main.anchor.getBounds().width += Main.ship.horizontal;
			}
			if (Main.ship.vertical > 0) {
				Main.anchor.getBounds().y -= Main.ship.vertical;
				Main.anchor.getBounds().height += Main.ship.vertical;
			}
			Main.canvasRedraw(Main.anchor.getBounds(), true);
		}
	}

	public void redo() throws CannotRedoException {
		super.redo();
		if (undoable == Undoable.MOVE) {
			Point size = Main.ship.findLowBounds();
			int x = size.x - current.x * 35;
			int y = size.y - current.y * 35;
			((Anchor) box).setLocation(x, y, false);
			Main.ship.anchor.x = x;
			Main.ship.anchor.y = y;
			Main.canvas.redraw();
		} else {
			Main.ship.horizontal = current.x;
			Main.ship.vertical = current.y;
			if (Main.ship.horizontal > 0) {
				Main.anchor.getBounds().x -= Main.ship.horizontal;
				Main.anchor.getBounds().width += Main.ship.horizontal;
			}
			if (Main.ship.vertical > 0) {
				Main.anchor.getBounds().y -= Main.ship.vertical;
				Main.anchor.getBounds().height += Main.ship.vertical;
			}
			Main.canvasRedraw(Main.anchor.getBounds(), true);
		}
	}
}
