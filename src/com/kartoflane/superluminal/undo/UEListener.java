package com.kartoflane.superluminal.undo;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import com.kartoflane.superluminal.core.Main;

public class UEListener implements UndoableEditListener {

	@Override
	public void undoableEditHappened(UndoableEditEvent e) {
		Main.savedSinceAction = false;
		Main.shell.setText("* " + Main.APPNAME + " - Ship Editor");
		Main.updateUndoButtons();
	}

}
