package com.kartoflane.superluminal.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.StateEdit;

import com.kartoflane.superluminal.painter.PaintBox;

/**
 * Scratch it, or find a use for it
 *
 */
public class UndoableModifyEdit extends AbstractUndoableEdit {
	private static final long serialVersionUID = -7325290746851080827L;

	private PaintBox box;
	private StateEdit state;
	
	public UndoableModifyEdit(PaintBox box) {
		this.box = box;
		//state = new StateEdit(box);
	}
	
	public StateEdit getOldValue() {
		return state;
	}
	
	public void setOldValue(StateEdit state) {
		this.state = state;
	}
	
	public String getPresentationName() {
		return String.format("modify ", box.getClass().getSimpleName());
	}
	
	public void undo() throws CannotUndoException {
		super.undo();
	}
	
	public void redo() throws CannotRedoException {
		super.redo();
	}
}
