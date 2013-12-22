package com.kartoflane.superluminal.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.elements.SystemBox;
import com.kartoflane.superluminal.elements.Systems;
import com.kartoflane.superluminal.painter.PaintBox;

public class UndoableSlotEdit extends AbstractUndoableEdit {
	private static final long serialVersionUID = -7325290746851080827L;

	//private PaintBox box;
	private int old;
	private int current;
	private Systems sys;
	
	public UndoableSlotEdit(PaintBox box) {
		//this.box = box;
		if (!(box instanceof SystemBox))
			throw new IllegalArgumentException("Argument isn't of type SystemBox.");
		sys = ((SystemBox) box).getSystemName();
		old = Main.ship.slotMap.get(sys);
	}
	
	public int getOldValue() {
		return old;
	}
	
	public int getCurrentValue() {
		return current;
	}
	
	public void setOldValue(int slot) {
		old = slot;
	}
	
	public void setCurrentValue(int slot) {
		current = slot;
	}
	
	public String getPresentationName() {
		return String.format("set %s station", sys.toString().toLowerCase());
	}
	
	public void undo() throws CannotUndoException {
		super.undo();
		Main.ship.slotMap.put(sys, old);
		Main.getRoomWithSystem(sys).slot = Main.ship.slotMap.get(sys);
		Main.canvasRedraw(Main.getRoomWithSystem(sys).getBounds(), false);
	}
	
	public void redo() throws CannotRedoException {
		super.redo();
		Main.ship.slotMap.put(sys, current);
		Main.getRoomWithSystem(sys).slot = Main.ship.slotMap.get(sys);
		Main.canvasRedraw(Main.getRoomWithSystem(sys).getBounds(), false);
	}
}
