package com.kartoflane.superluminal.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.elements.FTLDoor;
import com.kartoflane.superluminal.elements.FTLGib;
import com.kartoflane.superluminal.elements.FTLMount;
import com.kartoflane.superluminal.elements.FTLRoom;
import com.kartoflane.superluminal.painter.PaintBox;

public class UndoableCreateEdit extends AbstractUndoableEdit {
	private static final long serialVersionUID = -7325290746851080827L;

	private PaintBox box;
	private boolean deleteOnDie = false;
	
	public UndoableCreateEdit(PaintBox box) {
		if (!(box instanceof FTLRoom || box instanceof FTLDoor || box instanceof FTLMount || box instanceof FTLGib)) {
			throw new IllegalArgumentException("Not an instance of creatable object.");
		}
		this.box = box;
	}
	
	public String getPresentationName() {
		return String.format("create %s", box.getClass().getSimpleName());
	}
	
	public void undo() throws CannotUndoException {
		super.undo();
		Main.deleteObject(box);
		deleteOnDie = true;
	}
	
	public void redo() throws CannotRedoException {
		super.redo();
		Main.recreateObject(box);
		deleteOnDie = false;
	}
	
	public void die() {
		super.die();
		if (deleteOnDie)
			Main.reallyDeleteObject(box);
	}
}
