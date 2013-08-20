package com.kartoflane.superluminal.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.elements.FTLDoor;
import com.kartoflane.superluminal.painter.PaintBox;

public class UndoableDoorPropertiesEdit extends AbstractUndoableEdit {
	private static final long serialVersionUID = -7325290746851080827L;

	private PaintBox box;
	private int oldL;
	private int oldR;
	private int currentL;
	private int currentR;

	public UndoableDoorPropertiesEdit(PaintBox box) {
		this.box = box;

		oldL = ((FTLDoor) box).leftId;
		oldR = ((FTLDoor) box).rightId;
	}

	public int getOldValue(boolean left) {
		if (left)
			return oldL;
		else
			return oldR;
	}

	public int getCurrentValue(boolean left) {
		if (left)
			return currentL;
		else
			return currentR;
	}

	public void setOldValue(boolean left, int id) {
		if (left)
			oldL = id;
		else
			oldR = id;
	}

	public void setCurrentValue(boolean left, int id) {
		if (left)
			currentL = id;
		else
			currentR = id;
	}

	public String getPresentationName() {
		return String.format("modify door properties");
	}

	public void undo() throws CannotUndoException {
		super.undo();
		((FTLDoor) box).leftId = oldL;
		((FTLDoor) box).rightId = oldR;

		if (oldL != -2) {
			Rectangle db = new Rectangle(0, 0, 0, 0);
			Rectangle rb = new Rectangle(0, 0, 0, 0);
			Main.copyRect(((FTLDoor) box).getBounds(), db);
			Main.copyRect(Main.ship.getRoomWithId(oldL).getBounds(), rb);
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
		if (oldR != -2) {
			Rectangle db = new Rectangle(0, 0, 0, 0);
			Rectangle rb = new Rectangle(0, 0, 0, 0);
			Main.copyRect(((FTLDoor) box).getBounds(), db);
			Main.copyRect(Main.ship.getRoomWithId(oldR).getBounds(), rb);
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
		if (currentL != -2) {
			Rectangle db = new Rectangle(0, 0, 0, 0);
			Rectangle rb = new Rectangle(0, 0, 0, 0);
			Main.copyRect(((FTLDoor) box).getBounds(), db);
			Main.copyRect(Main.ship.getRoomWithId(currentL).getBounds(), rb);
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
		if (currentR != -2) {
			Rectangle db = new Rectangle(0, 0, 0, 0);
			Rectangle rb = new Rectangle(0, 0, 0, 0);
			Main.copyRect(((FTLDoor) box).getBounds(), db);
			Main.copyRect(Main.ship.getRoomWithId(currentR).getBounds(), rb);
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
		((FTLDoor) box).leftId = currentL;
		((FTLDoor) box).rightId = currentR;

		if (oldL != -2) {
			Rectangle db = new Rectangle(0, 0, 0, 0);
			Rectangle rb = new Rectangle(0, 0, 0, 0);
			Main.copyRect(((FTLDoor) box).getBounds(), db);
			Main.copyRect(Main.ship.getRoomWithId(oldL).getBounds(), rb);
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
		if (oldR != -2) {
			Rectangle db = new Rectangle(0, 0, 0, 0);
			Rectangle rb = new Rectangle(0, 0, 0, 0);
			Main.copyRect(((FTLDoor) box).getBounds(), db);
			Main.copyRect(Main.ship.getRoomWithId(oldR).getBounds(), rb);
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
		if (currentL != -2) {
			Rectangle db = new Rectangle(0, 0, 0, 0);
			Rectangle rb = new Rectangle(0, 0, 0, 0);
			Main.copyRect(((FTLDoor) box).getBounds(), db);
			Main.copyRect(Main.ship.getRoomWithId(currentL).getBounds(), rb);
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
		if (currentR != -2) {
			Rectangle db = new Rectangle(0, 0, 0, 0);
			Rectangle rb = new Rectangle(0, 0, 0, 0);
			Main.copyRect(((FTLDoor) box).getBounds(), db);
			Main.copyRect(Main.ship.getRoomWithId(currentR).getBounds(), rb);
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
