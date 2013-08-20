package com.kartoflane.superluminal.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.core.ShipIO;
import com.kartoflane.superluminal.elements.FTLMount;
import com.kartoflane.superluminal.painter.PaintBox;

public class UndoableMountPropertiesEdit extends AbstractUndoableEdit {
	private static final long serialVersionUID = -7325290746851080827L;
	
	private FTLMount mount;
	private FTLMount old;
	private FTLMount current;
	
	public UndoableMountPropertiesEdit(PaintBox box) {
		mount = (FTLMount) box;
		
		old = new FTLMount();
		old.index = mount.index;
		old.gib = mount.gib;
		old.rotate = mount.rotate;
		old.mirror = mount.mirror;
		old.powered = mount.powered;
		old.slide = mount.slide;
	}

	public FTLMount getOldValue() {
		return old;
	}

	public FTLMount getCurrentValue() {
		return current;
	}

	public void setCurrentValue(FTLMount m) {
		current = m;
	}

	public String getPresentationName() {
		return String.format("modify mount properties");
	}

	public void undo() throws CannotUndoException {
		super.undo();
		
		mount.index = old.index;
		mount.gib = old.gib;
		if (mount.rotate != old.rotate)
			mount.setRotated(old.rotate);
		if (mount.mirror != old.mirror)
			mount.setMirrored(old.mirror);
		if (mount.powered != old.powered)
			mount.setPowered(old.powered);
		mount.slide = old.slide;

		ShipIO.loadWeaponImages(Main.ship);
		ShipIO.remapMountsToWeapons();
		mount.redrawLoc(current.slide);
	}

	public void redo() throws CannotRedoException {
		super.redo();
		
		mount.index = current.index;
		mount.gib = current.gib;
		if (mount.rotate != current.rotate)
			mount.setRotated(current.rotate);
		if (mount.mirror != current.mirror)
			mount.setMirrored(current.mirror);
		if (mount.powered != current.powered)
			mount.setPowered(current.powered);
		mount.slide = current.slide;

		ShipIO.loadWeaponImages(Main.ship);
		ShipIO.remapMountsToWeapons();
		mount.redrawLoc(old.slide);
	}
}
