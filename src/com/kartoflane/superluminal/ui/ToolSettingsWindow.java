package com.kartoflane.superluminal.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.kartoflane.superluminal.elements.Slide;
import com.kartoflane.superluminal.core.Main;

public class ToolSettingsWindow {
	protected Shell shell;
	private Button btnCreate;
	private Button btnSplit;
	private Composite compMount;
	private Composite compRoom;
	private Composite compSys;
	private Button btnPlace;
	private Button btnRemove;

	public ToolSettingsWindow(Shell parent) {
		shell = new Shell(parent, SWT.BORDER | SWT.TITLE);
		shell.setLayout(new StackLayout());
		shell.setText("Tool Settings");
		
		compMount = new Composite(shell, SWT.NONE);
		compMount.setLayout(new GridLayout(1, false));
		
		final Button btnMirrored = new Button(compMount, SWT.CHECK);
		btnMirrored.setToolTipText("Alt-right-click to edit.");
		btnMirrored.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnMirrored.setText("Mirrored");
		btnMirrored.setSelection(Main.mountToolMirror);
		
		final Button btnRotated = new Button(compMount, SWT.CHECK);
		btnRotated.setToolTipText("Right-click to edit.");
		btnRotated.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnRotated.setText("Rotated");
		btnRotated.setSelection(Main.mountToolHorizontal);
		
		final Combo comboMount = new Combo(compMount, SWT.READ_ONLY);
		comboMount.setToolTipText("Shift-right-click to edit.");
		comboMount.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboMount.add("No");
		comboMount.add("Up");
		comboMount.add("Right");
		comboMount.add("Down");
		comboMount.add("Left");
		comboMount.select(Main.mountToolSlide.ordinal());
		
		
		compRoom = new Composite(shell, SWT.NONE);
		compRoom.setLayout(new GridLayout(1, false));
		
		btnCreate = new Button(compRoom, SWT.RADIO);
		btnCreate.setToolTipText("Left-click to place.");
		btnCreate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnCreate.setText("Create New Rooms");
		btnCreate.setSelection(true);
		
		btnSplit = new Button(compRoom, SWT.RADIO);
		btnSplit.setToolTipText("Shift-left-click to split.");
		btnSplit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnSplit.setText("Split Existing Rooms");
		
		
		compSys = new Composite(shell, SWT.NONE);
		compSys.setLayout(new GridLayout(1, false));
		
		btnPlace = new Button(compSys, SWT.RADIO);
		btnPlace.setToolTipText("Left-click to place.\nShift-left-click to rotate station.");
		btnPlace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnPlace.setText("Place Station");
		btnPlace.setSelection(true);
		
		btnRemove = new Button(compSys, SWT.RADIO);
		btnRemove.setToolTipText("Right-click to remove station.");
		btnRemove.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnRemove.setText("Remove Station");
		
		shell.pack();
		
		compMount.setVisible(false);
		compRoom.setVisible(false);
		compSys.setVisible(false);

		btnMirrored.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Main.mountToolMirror = btnMirrored.getSelection();
			}
		});
		
		btnRotated.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Main.mountToolHorizontal = btnRotated.getSelection();
			}
		});
		
		comboMount.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Main.mountToolSlide = Slide.values()[comboMount.getSelectionIndex()];
			}
		});
		
		btnCreate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Main.roomToolCreate = btnCreate.getSelection();
			}
		});
		
		btnSplit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Main.roomToolCreate = btnCreate.getSelection();
			}
		});
		
		btnPlace.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Main.sysToolPlace = btnPlace.getSelection();
			}
		});
		
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Main.sysToolPlace = btnPlace.getSelection();
			}
		});
		
		shell.addListener(SWT.Traverse, new Listener() {
			@Override
			public void handleEvent(Event e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE) {
					e.doit = false;
				}
			}
		});
	}
	
	public void open() {
		shell.open();

		compMount.setVisible(Main.getSelectedTool() == Main.tltmMount);
		compRoom.setVisible(Main.getSelectedTool() == Main.tltmRoom);
		compSys.setVisible(Main.getSelectedTool() == Main.tltmSystem);

		btnSplit.setSelection(false);
		btnSplit.notifyListeners(SWT.Selection, null);
		btnCreate.setSelection(true);
		btnCreate.notifyListeners(SWT.Selection, null);
		btnRemove.setSelection(false);
		btnRemove.notifyListeners(SWT.Selection, null);
		btnPlace.setSelection(true);
		btnPlace.notifyListeners(SWT.Selection, null);
		
		shell.pack();
	}
	
	public void close() {
		shell.setVisible(false);
	}
	
	public boolean isVisible() {
		return shell.isVisible();
	}
}

