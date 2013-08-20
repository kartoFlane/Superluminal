package com.kartoflane.superluminal.ui;

import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.AbstractUndoableEdit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.elements.FTLDoor;
import com.kartoflane.superluminal.undo.Undoable;
import com.kartoflane.superluminal.undo.UndoableDoorPropertiesEdit;

public class DoorPropertiesWindow {
	protected Shell shell;
	
	public int fid = -2;
	public int sid = -2;

	public boolean selectingFirst = false;
	public boolean selectingSecond = false;
	private Label lblLeft;
	private Label lblRight;
	private Button btnSelectFirst;
	private Button btnSelectSecond;
	
	private FTLDoor currentDoor = null;
	AbstractUndoableEdit ume = null;
	
	public DoorPropertiesWindow(Shell parent) {
		shell = new Shell(parent, SWT.BORDER | SWT.TITLE);
		shell.setFont(Main.appFont);
		shell.setLocation(parent.getLocation().x + 100, parent.getLocation().y + 100);
		shell.setLayout(new GridLayout(4, false));
		shell.setText(Main.APPNAME + " - Door Properties");
		
		Label lblLeftId = new Label(shell, SWT.NONE);
		lblLeftId.setToolTipText("(-1 means airlock, -2 means the door will be automatically connected)");
		lblLeftId.setText("First ID:");
		lblLeftId.setFont(Main.appFont);
		
		lblLeft = new Label(shell, SWT.NONE);
		lblLeft.setAlignment(SWT.CENTER);
		lblLeft.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblLeft.setText("000");
		lblLeft.setFont(Main.appFont);
		
		btnSelectFirst = new Button(shell, SWT.NONE);
		btnSelectFirst.setText("Select Room");
		btnSelectFirst.setFont(Main.appFont);
		
		Button btnResetFirst = new Button(shell, SWT.NONE);
		btnResetFirst.setText("Reset");
		btnResetFirst.setFont(Main.appFont);
		
		Label lblRightId = new Label(shell, SWT.NONE);
		lblRightId.setToolTipText("(-1 means airlock, -2 means the door will be automatically connected)");
		lblRightId.setText("Second ID:");
		lblRightId.setFont(Main.appFont);
		
		lblRight = new Label(shell, SWT.NONE);
		lblRight.setAlignment(SWT.CENTER);
		lblRight.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblRight.setText("000");
		lblRight.setFont(Main.appFont);
		
		btnSelectSecond = new Button(shell, SWT.NONE);
		btnSelectSecond.setText("Select Room");
		btnSelectSecond.setFont(Main.appFont);
		
		Button btnResetSecond = new Button(shell, SWT.NONE);
		btnResetSecond.setText("Reset");
		btnResetSecond.setFont(Main.appFont);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		
		Button btnOk = new Button(shell, SWT.NONE);
		GridData gd_btnOk = new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 2, 1);
		gd_btnOk.widthHint = 80;
		btnOk.setLayoutData(gd_btnOk);
		btnOk.setText("OK");
		btnOk.setFont(Main.appFont);
		
		Button btnCancel = new Button(shell, SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.RIGHT, SWT.BOTTOM, false, true, 2, 1);
		gd_btnCancel.widthHint = 80;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setText("Cancel");
		btnCancel.setFont(Main.appFont);

		shell.pack();
		
		btnSelectFirst.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectingFirst = true;
				enable(false);
			}
		});
		
		btnSelectSecond.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectingSecond = true;
				enable(false);
			}
		});
		
		btnResetFirst.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectingFirst = true;
				setId(-2);
				enable(true);
				selectingFirst = false;
			}
		});
		
		btnResetSecond.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectingSecond = true;
				setId(-2);
				enable(true);
				selectingSecond = false;
			}
		});
		
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				close();
			}
		});
		
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (currentDoor.leftId != fid || currentDoor.rightId != sid)
					registerDown(Undoable.DOOR_PROP);
				currentDoor.leftId = fid;
				currentDoor.rightId = sid;
				
				registerUp(Undoable.DOOR_PROP);
				
				currentDoor.selectNoMove();
				close();
			}
		});
		
		shell.addListener(SWT.Traverse, new Listener() {
			@Override
			public void handleEvent(Event e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE) {
					e.doit = false;
					close();
				}
			}
		});
	}
	
	public void setId(int id) {
		if (selectingFirst) {
			fid = id;
			lblLeft.setText(""+fid);
		} else if (selectingSecond) {
			sid = id;
			lblRight.setText(""+sid);
		}
		if (selectingFirst || selectingSecond) {
			selectingFirst = false;
			selectingSecond = false;
			enable(true);
			if (Main.selectedRoom != null)
				Main.selectedRoom.deselect();
			if (currentDoor != null)
				currentDoor.selectNoMove();
		}
	}
	
	public void open() {
		shell.open();
		currentDoor = Main.selectedDoor;

		if (currentDoor != null) {
			fid = currentDoor.leftId;
			sid = currentDoor.rightId;
			lblLeft.setText("" + fid);
			lblRight.setText("" + sid);
		} else {
			lblLeft.setText("");
			lblRight.setText("");
		}
	}
	
	public void close() {
		selectingFirst = false;
		selectingSecond = false;
		enable(true);
		shell.setVisible(false);
		Main.canvas.redraw();
		currentDoor = null;
	}
	
	public boolean isVisible() {
		return shell.isVisible();
	}
	
	public void enable(boolean enable) {
		btnSelectFirst.setEnabled(enable);
		btnSelectSecond.setEnabled(enable);
	}
	
	private void registerDown(int undoable) {
		if (undoable == Undoable.DOOR_PROP) {
			ume = new UndoableDoorPropertiesEdit(currentDoor);
			currentDoor.undoListener.undoableEditHappened(new UndoableEditEvent(currentDoor, ume));
		}
	}
	
	private void registerUp(int undoable) {
		if (ume != null) {
			if (undoable == Undoable.DOOR_PROP) {
				((UndoableDoorPropertiesEdit) ume).setCurrentValue(true, ((FTLDoor) currentDoor).leftId);
				((UndoableDoorPropertiesEdit) ume).setCurrentValue(false, ((FTLDoor) currentDoor).rightId);
				Main.addEdit(ume);
			}
		}
		ume = null;
	}
}
