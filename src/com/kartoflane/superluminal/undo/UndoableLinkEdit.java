package com.kartoflane.superluminal.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.elements.FTLDoor;
import com.kartoflane.superluminal.elements.FTLRoom;
import com.kartoflane.superluminal.painter.PaintBox;

public class UndoableLinkEdit extends AbstractUndoableEdit {
	private static final long serialVersionUID = -7325290746851080827L;

	private PaintBox box;
	private FTLRoom old;
	private FTLRoom current;
	private boolean left;

	public UndoableLinkEdit(PaintBox box, boolean left) {
		this.box = box;
		this.left = left;

		if (left)
			old = ((FTLDoor) box).leftRoom;
		else
			old = ((FTLDoor) box).rightRoom;
	}

	/**
	 * @return True if the event regards left ID, false if right ID.
	 */
	public boolean isLeft() {
		return left;
	}

	public FTLRoom getOldValue() {
		return old;
	}

	public FTLRoom getCurrentValue() {
		return current;
	}

	public void setOldValue(FTLRoom room) {
		old = room;
	}

	public void setCurrentValue(FTLRoom room) {
		current = room;
	}

	public String getPresentationName() {
		return String.format("link door");
	}

	public void undo() throws CannotUndoException {
		super.undo();
		if (left)
			((FTLDoor) box).setLeftRoom(old);
		else
			((FTLDoor) box).setRightRoom(old);

		if (old != null) {
			Rectangle db = new Rectangle(0, 0, 0, 0);
			Rectangle rb = new Rectangle(0, 0, 0, 0);
			Main.copyRect(((FTLDoor) box).getBounds(), db);
			Main.copyRect(old.getBounds(), rb);
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
		if (current != null) {
			Rectangle db = new Rectangle(0, 0, 0, 0);
			Rectangle rb = new Rectangle(0, 0, 0, 0);
			Main.copyRect(((FTLDoor) box).getBounds(), db);
			Main.copyRect(current.getBounds(), rb);
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
			((FTLDoor) box).setLeftRoom(current);
		else
			((FTLDoor) box).setRightRoom(current);

		if (old != null) {
			Rectangle db = new Rectangle(0, 0, 0, 0);
			Rectangle rb = new Rectangle(0, 0, 0, 0);
			Main.copyRect(((FTLDoor) box).getBounds(), db);
			Main.copyRect(old.getBounds(), rb);
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
		if (current != null) {
			Rectangle db = new Rectangle(0, 0, 0, 0);
			Rectangle rb = new Rectangle(0, 0, 0, 0);
			Main.copyRect(((FTLDoor) box).getBounds(), db);
			Main.copyRect(current.getBounds(), rb);
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
