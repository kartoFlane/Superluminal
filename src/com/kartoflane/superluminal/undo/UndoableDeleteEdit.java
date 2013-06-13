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

public class UndoableDeleteEdit extends AbstractUndoableEdit {
	private static final long serialVersionUID = -7325290746851080827L;

	private PaintBox box;
	private boolean deleteOnDie = false;
	
	public UndoableDeleteEdit(PaintBox box) {
		if (!(box instanceof FTLRoom || box instanceof FTLDoor || box instanceof FTLMount || box instanceof FTLGib)) {
			throw new IllegalArgumentException("Not an instance of deletable object.");
		}
		this.box = box;
	}
	
	public String getPresentationName() {
		return String.format("delete %s", box.getClass().getSimpleName());
	}
	
	public void undo() throws CannotUndoException {
		super.undo();
		Main.recreateObject(box);
		deleteOnDie = false;
		
		/*
		} else if (boxClass.equals(FTLMount.class.getSimpleName())) {
			FTLMount m = new FTLMount();
			m.pos.x = dimensions.x + dimensions.width/2 - Main.ship.imageRect.x;
			m.pos.y = dimensions.y + dimensions.height/2 - Main.ship.imageRect.y;
			m.setRotated(mount_rotate);
			m.setMirrored(mount_mirror);
			m.gib = 0;
			m.slide = mount_slide;
			
			m.setLocation(dimensions.x + dimensions.width/2, dimensions.y + dimensions.height/2);
			
			m.add(Main.ship);
		} else if (boxClass.equals(FTLGib.class.getSimpleName())) {
		}
		*/
	}
	
	public void redo() throws CannotRedoException {
		super.redo();
		Main.deleteObject(box);
		deleteOnDie = true;
		/*
		} else if (box instanceof FTLGib) {
			box = (FTLGib) box;
			
			if (Main.gibWindow.isVisible()) Main.gibWindow.escape();
			
			Main.gibDialog.list.remove(((FTLGib) box).number-1);
			box.dispose();
			Main.ship.gibs.remove(box);
			Main.gibDialog.letters.remove(((FTLGib) box).ID);
			redrawBounds = box.getBounds();
			Main.canvas.redraw(redrawBounds.x-1, redrawBounds.y-1, redrawBounds.width+2, redrawBounds.height+2, false);
			Main.gibDialog.btnDeleteGib.setEnabled(false);
			
			redrawBounds = null;
			Main.gibDialog.refreshList();
		}
		*/
	}
	
	public void die() {
		super.die();
		if (deleteOnDie)
			Main.reallyDeleteObject(box);
	}
}
