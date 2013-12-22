package com.kartoflane.superluminal.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.elements.FTLDoor;
import com.kartoflane.superluminal.elements.FTLRoom;
import com.kartoflane.superluminal.painter.PaintBox;

public class UndoableDoorPropertiesEdit extends AbstractUndoableEdit {
	private static final long serialVersionUID = -7325290746851080827L;

	private PaintBox box;
	private FTLRoom oldL;
	private FTLRoom oldR;
	private FTLRoom currentL;
	private FTLRoom currentR;

	public UndoableDoorPropertiesEdit(PaintBox box) {
		this.box = box;

		oldL = ((FTLDoor) box).leftRoom;
		oldR = ((FTLDoor) box).rightRoom;
	}

	public FTLRoom getOldValue(boolean left) {
		if (left)
			return oldL;
		else
			return oldR;
	}

	public FTLRoom getCurrentValue(boolean left) {
		if (left)
			return currentL;
		else
			return currentR;
	}

	public void setOldValue(boolean left, FTLRoom room) {
		if (left)
			oldL = room;
		else
			oldR = room;
	}

	public void setCurrentValue(boolean left, FTLRoom room) {
		if (left)
			currentL = room;
		else
			currentR = room;
	}

	public String getPresentationName() {
		return String.format("modify door properties");
	}

	public void undo() throws CannotUndoException {
		super.undo();
		((FTLDoor) box).setLeftRoom(oldL);
		((FTLDoor) box).setRightRoom(oldR);

		if (oldL != null) {
			Rectangle db = new Rectangle(0, 0, 0, 0);
			Rectangle rb = new Rectangle(0, 0, 0, 0);
			Main.copyRect(((FTLDoor) box).getBounds(), db);
			Main.copyRect(oldL.getBounds(), rb);
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
		if (oldR != null) {
			Rectangle db = new Rectangle(0, 0, 0, 0);
			Rectangle rb = new Rectangle(0, 0, 0, 0);
			Main.copyRect(((FTLDoor) box).getBounds(), db);
			Main.copyRect(oldR.getBounds(), rb);
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
		if (currentL != null) {
			Rectangle db = new Rectangle(0, 0, 0, 0);
			Rectangle rb = new Rectangle(0, 0, 0, 0);
			Main.copyRect(((FTLDoor) box).getBounds(), db);
			Main.copyRect(currentL.getBounds(), rb);
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
		if (currentR != null) {
			Rectangle db = new Rectangle(0, 0, 0, 0);
			Rectangle rb = new Rectangle(0, 0, 0, 0);
			Main.copyRect(((FTLDoor) box).getBounds(), db);
			Main.copyRect(currentR.getBounds(), rb);
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
		((FTLDoor) box).setLeftRoom(currentL);
		((FTLDoor) box).setRightRoom(currentR);

		if (oldL != null) {
			Rectangle db = new Rectangle(0, 0, 0, 0);
			Rectangle rb = new Rectangle(0, 0, 0, 0);
			Main.copyRect(((FTLDoor) box).getBounds(), db);
			Main.copyRect(oldL.getBounds(), rb);
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
		if (oldR != null) {
			Rectangle db = new Rectangle(0, 0, 0, 0);
			Rectangle rb = new Rectangle(0, 0, 0, 0);
			Main.copyRect(((FTLDoor) box).getBounds(), db);
			Main.copyRect(oldR.getBounds(), rb);
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
		if (currentL != null) {
			Rectangle db = new Rectangle(0, 0, 0, 0);
			Rectangle rb = new Rectangle(0, 0, 0, 0);
			Main.copyRect(((FTLDoor) box).getBounds(), db);
			Main.copyRect(currentL.getBounds(), rb);
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
		if (currentR != null) {
			Rectangle db = new Rectangle(0, 0, 0, 0);
			Rectangle rb = new Rectangle(0, 0, 0, 0);
			Main.copyRect(((FTLDoor) box).getBounds(), db);
			Main.copyRect(currentR.getBounds(), rb);
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
