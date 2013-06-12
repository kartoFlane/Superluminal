package com.kartoflane.superluminal.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.elements.FTLRoom;
import com.kartoflane.superluminal.elements.Systems;
import com.kartoflane.superluminal.painter.PaintBox;

/**
 * Used for un/assignment of systems, NOT for modifying their properties.
 * @author kartoFlane
 */
public class UndoableSystemEdit extends AbstractUndoableEdit {
	private static final long serialVersionUID = -7325290746851080827L;

	private PaintBox box;
	private Systems old = null;
	private Systems current = null;
	
	public UndoableSystemEdit(PaintBox box) {
		this.box = box;
		if (!(box instanceof FTLRoom))
			throw new IllegalArgumentException("Argument isn't of type FTLRoom.");
		old = ((FTLRoom) box).getSystem();
	}
	
	public Systems getOldValue() {
		return old;
	}
	
	public Systems getCurrentValue() {
		return current;
	}
	
	public void setOldValue(Systems sys) {
		old = sys;
	}
	
	public void setCurrentValue(Systems sys) {
		current = sys;
	}
	
	public String getPresentationName() {
		return String.format("assign system", box.getClass().getSimpleName());
	}
	
	public void undo() throws CannotUndoException {
		super.undo();
		Rectangle bounds = ((FTLRoom) box).getBounds();
		((FTLRoom) box).assignSystem(old);
		Main.canvas.redraw(bounds.x, bounds.y, bounds.width, bounds.height, true);
	}
	
	public void redo() throws CannotRedoException {
		super.redo();
		Rectangle bounds = ((FTLRoom) box).getBounds();
		((FTLRoom) box).assignSystem(current);
		Main.canvas.redraw(bounds.x, bounds.y, bounds.width, bounds.height, true);
	}
}
