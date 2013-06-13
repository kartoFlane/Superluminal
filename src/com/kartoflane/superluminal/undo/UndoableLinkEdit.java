package com.kartoflane.superluminal.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.elements.FTLDoor;
import com.kartoflane.superluminal.painter.PaintBox;

public class UndoableLinkEdit extends AbstractUndoableEdit {
	private static final long serialVersionUID = -7325290746851080827L;

	private PaintBox box;
	private int old;
	private int current;
	private boolean left;

	public UndoableLinkEdit(PaintBox box, boolean left) {
		this.box = box;
		this.left = left;

		if (left)
			old = ((FTLDoor) box).leftId;
		else
			old = ((FTLDoor) box).rightId;
	}

	/**
	 * @return True if the event regards left ID, false if right ID.
	 */
	public boolean isLeft() {
		return left;
	}

	public int getOldValue() {
		return old;
	}

	public int getCurrentValue() {
		return current;
	}

	public void setOldValue(int id) {
		old = id;
	}

	public void setCurrentValue(int id) {
		current = id;
	}

	public String getPresentationName() {
		return String.format("link door");
	}

	public void undo() throws CannotUndoException {
		super.undo();
		if (left)
			((FTLDoor) box).leftId = old;
		else
			((FTLDoor) box).rightId = old;

		if (old != -2) {
			Rectangle db = new Rectangle(0, 0, 0, 0);
			Rectangle rb = new Rectangle(0, 0, 0, 0);
			Main.copyRect(((FTLDoor) box).getBounds(), db);
			Main.copyRect(Main.ship.getRoomWithId(old).getBounds(), rb);
			// door center
			db.x = db.x + db.width / 2;
			db.y = db.y + db.height / 2;
			// room center
			rb.x = rb.x + rb.width / 2;
			rb.y = rb.y + rb.height / 2;
			// distance
			db.width = Math.abs(db.x - rb.x);
			db.height = Math.abs(db.y - rb.y);
			Main.canvas.redraw(Math.min(db.x, rb.x), Math.min(db.y, rb.y), db.width, db.height, true);
		}
		if (current != -2) {
			Rectangle db = new Rectangle(0, 0, 0, 0);
			Rectangle rb = new Rectangle(0, 0, 0, 0);
			Main.copyRect(((FTLDoor) box).getBounds(), db);
			Main.copyRect(Main.ship.getRoomWithId(current).getBounds(), rb);
			// door center
			db.x = db.x + db.width / 2;
			db.y = db.y + db.height / 2;
			// room center
			rb.x = rb.x + rb.width / 2;
			rb.y = rb.y + rb.height / 2;
			// distance
			db.width = Math.abs(db.x - rb.x);
			db.height = Math.abs(db.y - rb.y);
			Main.canvas.redraw(Math.min(db.x, rb.x), Math.min(db.y, rb.y), db.width, db.height, true);
		}
	}

	public void redo() throws CannotRedoException {
		super.redo();
		if (left)
			((FTLDoor) box).leftId = current;
		else
			((FTLDoor) box).rightId = current;

		if (old != -2) {
			Rectangle db = new Rectangle(0, 0, 0, 0);
			Rectangle rb = new Rectangle(0, 0, 0, 0);
			Main.copyRect(((FTLDoor) box).getBounds(), db);
			Main.copyRect(Main.ship.getRoomWithId(old).getBounds(), rb);
			// door center
			db.x = db.x + db.width / 2;
			db.y = db.y + db.height / 2;
			// room center
			rb.x = rb.x + rb.width / 2;
			rb.y = rb.y + rb.height / 2;
			// distance
			db.width = Math.abs(db.x - rb.x);
			db.height = Math.abs(db.y - rb.y);
			Main.canvas.redraw(Math.min(db.x, rb.x), Math.min(db.y, rb.y), db.width, db.height, true);
		}
		if (current != -2) {
			Rectangle db = new Rectangle(0, 0, 0, 0);
			Rectangle rb = new Rectangle(0, 0, 0, 0);
			Main.copyRect(((FTLDoor) box).getBounds(), db);
			Main.copyRect(Main.ship.getRoomWithId(current).getBounds(), rb);
			// door center
			db.x = db.x + db.width / 2;
			db.y = db.y + db.height / 2;
			// room center
			rb.x = rb.x + rb.width / 2;
			rb.y = rb.y + rb.height / 2;
			// distance
			db.width = Math.abs(db.x - rb.x);
			db.height = Math.abs(db.y - rb.y);
			Main.canvas.redraw(Math.min(db.x, rb.x), Math.min(db.y, rb.y), db.width, db.height, true);
		}
	}
}
