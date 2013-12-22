package com.kartoflane.superluminal.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.elements.FTLMount;
import com.kartoflane.superluminal.elements.Slide;
import com.kartoflane.superluminal.elements.SystemBox;
import com.kartoflane.superluminal.elements.Systems;
import com.kartoflane.superluminal.painter.PaintBox;

/**
 * Only applicable for FTLMount and SystemBox (system station direction) classes
 * @author kartoFlane
 *
 */
public class UndoableDirectionEdit extends AbstractUndoableEdit {
	private static final long serialVersionUID = -3165329298301115013L;

	private PaintBox box;
	private Slide oldSlide;
	private Slide currentSlide;
	
	public UndoableDirectionEdit(PaintBox box) {
		this.box = box;
		if (box instanceof FTLMount) {
			oldSlide = ((FTLMount) box).slide;
		} else if (box instanceof SystemBox) {
			oldSlide = Main.ship.slotDirMap.get(((SystemBox) box).getSystemName());
		} else {
			throw new IllegalArgumentException("Must be either FTLMount or SystemBox.");
		}
	}
	
	public UndoableDirectionEdit(Systems sys) {
		this(Main.getRoomWithSystem(sys).sysBox);
	}
	
	public void setCurrentSlide(Slide slide) {
		currentSlide = slide;
	}
	
	public Slide getCurrentSlide() {
		return currentSlide;
	}
	
	public Slide getOldSlide() {
		return oldSlide;
	}
	
	public String getPresentationName() {
		if (box instanceof FTLMount) {
			return "change " + box.getClass().getSimpleName() + " direction";
		} else if (box instanceof SystemBox) {
			return "change " + ((SystemBox) box).getSystemName().toString().toLowerCase() + " station direction";
		}
		return "(invalid directionEdit)";
	}
	
	public void undo() throws CannotUndoException {
		super.undo();
		if (box instanceof FTLMount) {
			Slide temp = ((FTLMount) box).slide;
			((FTLMount) box).slide = oldSlide;
			((FTLMount) box).redrawLoc(temp);
		} else if (box instanceof SystemBox) {
			Systems sys = ((SystemBox) box).getSystemName();
			Main.ship.slotDirMap.put(sys, oldSlide);
			Main.canvasRedraw(Main.getRoomWithSystem(sys).getBounds(), false);
		}
	}
	
	public void redo() throws CannotRedoException {
		super.redo();
		if (box instanceof FTLMount) {
			Slide temp = ((FTLMount) box).slide;
			((FTLMount) box).slide = currentSlide;
			((FTLMount) box).redrawLoc(temp);
		} else if (box instanceof SystemBox) {
			Systems sys = ((SystemBox) box).getSystemName();
			Main.ship.slotDirMap.put(sys, currentSlide);
			Main.canvasRedraw(Main.getRoomWithSystem(sys).getBounds(), false);
		}
	}
}

