package com.kartoflane.superluminal.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.kartoflane.superluminal.elements.FTLMount;
import com.kartoflane.superluminal.painter.PaintBox;

public class UndoableRotateEdit extends AbstractUndoableEdit {
	private static final long serialVersionUID = -3165329298301115013L;

	private PaintBox box;
	private boolean old;
	
	public UndoableRotateEdit(PaintBox box) {
		this.box = box;
		old = ((FTLMount) box).isRotated();
	}
	
	public boolean getOldValue() {
		return old;
	}
	
	public String getPresentationName() {
		return String.format("rotate %s", box.getClass().getSimpleName());
	}
	
	public void undo() throws CannotUndoException {
		super.undo();
		if (box instanceof FTLMount) {
			((FTLMount) box).setRotated(old);
		}
	}
	
	public void redo() throws CannotRedoException {
		super.redo();
		if (box instanceof FTLMount) {
			((FTLMount) box).setRotated(!old);
		}
	}
}

