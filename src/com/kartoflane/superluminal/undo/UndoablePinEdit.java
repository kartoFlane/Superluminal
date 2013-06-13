package com.kartoflane.superluminal.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.painter.PaintBox;

public class UndoablePinEdit extends AbstractUndoableEdit {
	private static final long serialVersionUID = -3165329298301115013L;

	private PaintBox box;
	private boolean old;
	
	public UndoablePinEdit(PaintBox box) {
		this.box = box;
		old = box.isPinned();
	}
	
	public boolean getOldValue() {
		return old;
	}
	
	public String getPresentationName() {
		return String.format("pin %s", box.getClass().getSimpleName());
	}
	
	public void undo() throws CannotUndoException {
		super.undo();
		box.setPinned(old);
		Main.canvasRedraw(box.getBounds(), true);
	}
	
	public void redo() throws CannotRedoException {
		super.redo();
		box.setPinned(!old);
		Main.canvasRedraw(box.getBounds(), true);
	}
}

