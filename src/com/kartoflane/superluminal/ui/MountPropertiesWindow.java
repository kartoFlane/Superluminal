package com.kartoflane.superluminal.ui;

import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.AbstractUndoableEdit;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Spinner;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.core.ShipIO;
import com.kartoflane.superluminal.elements.FTLMount;
import com.kartoflane.superluminal.elements.Slide;
import com.kartoflane.superluminal.undo.Undoable;
import com.kartoflane.superluminal.undo.UndoableMountPropertiesEdit;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class MountPropertiesWindow {
	protected Shell shell;
	FTLMount currentMount = null;
	private Spinner spNumber;
	private Spinner spGib;
	private Button btnMirrored;
	private Button btnRotated;
	private Button btnPowered;
	private Combo comboDir;
	private Button btnOk;
	private Button btnCancel;
	
	private boolean toolMode = false;
	AbstractUndoableEdit ume = null;

	public MountPropertiesWindow(Shell parent) {
		shell = new Shell(parent, SWT.BORDER | SWT.TITLE);
		shell.setFont(Main.appFont);
		shell.setLocation(parent.getLocation().x + 100, parent.getLocation().y + 100);
		shell.setLayout(new GridLayout(2, false));
		shell.setText(Main.APPNAME + " - Mount Properties");

		Label lblNumber = new Label(shell, SWT.NONE);
		lblNumber.setToolTipText("Index of this mount.\nFirst index corresponds to first weapon equipped, etc.");
		lblNumber.setText("Mount number:");
		lblNumber.setFont(Main.appFont);

		spNumber = new Spinner(shell, SWT.BORDER);
		spNumber.setMaximum(10);
		spNumber.setMinimum(1);
		spNumber.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		spNumber.setFont(Main.appFont);

		Label lblAttachedToGib = new Label(shell, SWT.NONE);
		lblAttachedToGib.setToolTipText("Number of gib to which this mount is attached.\nMount will float along with the gib when the ship explodes.\n0 for no gib (mount won't be shown during the animation)");
		lblAttachedToGib.setText("Attached to gib:");
		lblAttachedToGib.setFont(Main.appFont);

		spGib = new Spinner(shell, SWT.BORDER);
		spGib.setMaximum(10);
		spGib.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		spGib.setFont(Main.appFont);

		btnMirrored = new Button(shell, SWT.CHECK);
		btnMirrored.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnMirrored.setText("Mirrored");
		btnMirrored.setToolTipText("Mirrors the mount along its axis.\n(Alt-right click)");
		btnMirrored.setFont(Main.appFont);

		btnRotated = new Button(shell, SWT.CHECK);
		btnRotated.setToolTipText("Whether the mount is rotated.\nTrue for player shis, false for enemy ships.\n(Right-click)");
		btnRotated.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnRotated.setText("Rotated");
		btnRotated.setFont(Main.appFont);

		btnPowered = new Button(shell, SWT.CHECK);
		btnPowered.setToolTipText("Allows to view powered and un-powered states of this mount's weapon.\n(Alt-left-click)");
		btnPowered.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnPowered.setText("Powered (in editor only)");
		btnPowered.setFont(Main.appFont);

		Label lblDirection = new Label(shell, SWT.NONE);
		lblDirection.setToolTipText("Direction in which the mount will extend once powered.\n(Shift-right-click)");
		lblDirection.setText("Direction:");
		lblDirection.setFont(Main.appFont);
		new Label(shell, SWT.NONE);

		comboDir = new Combo(shell, SWT.READ_ONLY);
		comboDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		comboDir.setFont(Main.appFont);
		comboDir.add("None");
		comboDir.add("Up");
		comboDir.add("Right");
		comboDir.add("Down");
		comboDir.add("Left");

		btnOk = new Button(shell, SWT.NONE);
		GridData gd_btnOk = new GridData(SWT.LEFT, SWT.BOTTOM, false, true, 1, 1);
		gd_btnOk.widthHint = 80;
		btnOk.setLayoutData(gd_btnOk);
		btnOk.setText("OK");
		btnOk.setFont(Main.appFont);

		btnCancel = new Button(shell, SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.RIGHT, SWT.BOTTOM, false, true, 1, 1);
		gd_btnCancel.widthHint = 80;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setText("Cancel");
		btnCancel.setFont(Main.appFont);

		shell.pack();

		btnMirrored.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (toolMode) {
					Main.mountToolMirror = btnMirrored.getSelection();
				}
			}
		});
		
		btnRotated.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (toolMode) {
					Main.mountToolHorizontal = btnRotated.getSelection();
				}
			}
		});
		
		comboDir.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (toolMode) {
					Main.mountToolSlide = Slide.values()[comboDir.getSelectionIndex()];
				}
			}
		});

		btnCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				close();
			}
		});

		btnOk.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (currentMount.index != spNumber.getSelection() - 1 || currentMount.gib != spGib.getSelection() ||
						currentMount.mirror != btnMirrored.getSelection() || currentMount.rotate != btnRotated.getSelection() ||
						currentMount.powered != btnPowered.getSelection() || currentMount.slide != Slide.values()[comboDir.getSelectionIndex()])
					registerDown(Undoable.MOUNT_PROP);
				
				int oldIndex = currentMount.index;
				int newIndex = spNumber.getSelection() - 1;
				if (oldIndex != newIndex) {
					FTLMount m = Main.ship.getMountWithIndex(newIndex);
					currentMount.index = newIndex;
					if (m != null)
						m.index = oldIndex;
				}
				
				currentMount.gib = spGib.getSelection();
				currentMount.setMirrored(btnMirrored.getSelection());
				currentMount.setRotated(btnRotated.getSelection());
				currentMount.setPowered(btnPowered.getSelection());

				Slide slideOld = currentMount.slide;
				int index = comboDir.getSelectionIndex();
				if (index != -1) {
					currentMount.slide = Slide.values()[index];
					currentMount.redrawLoc(slideOld);
				}
				ShipIO.loadWeaponImages(Main.ship);
				ShipIO.remapMountsToWeapons();
				Main.canvasRedraw(currentMount.getBounds(), true);
				
				registerUp(Undoable.MOUNT_PROP);

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

	public void open() {
		shell.open();
		currentMount = Main.selectedMount;

		if (currentMount != null) {
			spNumber.setSelection(currentMount.index + 1);
			spGib.setSelection(currentMount.gib);
			btnMirrored.setSelection(currentMount.isMirrored());
			btnRotated.setSelection(currentMount.isRotated());
			btnPowered.setSelection(currentMount.isPowered());
	
			comboDir.select(currentMount.slide.ordinal());
	
			spNumber.setMaximum(Main.ship.mounts.size());
			spGib.setMaximum(Main.ship.gibs.size());
		}
		
		btnOk.setEnabled(true);
		btnCancel.setEnabled(true);
		spNumber.setEnabled(true);
		spGib.setEnabled(true);
		btnPowered.setEnabled(true);
		
		toolMode = false;
	}
	
	public Shell getShell() {
		return shell;
	}
	
	public boolean isVisible() {
		return shell.isVisible();
	}
	
	public void close() {
		shell.setVisible(false);
		currentMount = null;
	}
	
	public void refresh() {
		if (toolMode) {
			btnMirrored.setSelection(Main.mountToolMirror);
			btnRotated.setSelection(Main.mountToolHorizontal);
			comboDir.select(Main.mountToolSlide.ordinal());
		} else if (currentMount != null) {
			btnMirrored.setSelection(currentMount.isMirrored());
			btnRotated.setSelection(currentMount.isRotated());
			btnPowered.setSelection(currentMount.isPowered());
			comboDir.select(currentMount.slide.ordinal());
		}
	}
	
	private void registerDown(int undoable) {
		if (undoable == Undoable.MOUNT_PROP) {
			ume = new UndoableMountPropertiesEdit(currentMount);
			currentMount.undoListener.undoableEditHappened(new UndoableEditEvent(currentMount, ume));
		}
	}
	
	private void registerUp(int undoable) {
		if (ume != null) {
			if (undoable == Undoable.MOUNT_PROP) {
				FTLMount current = new FTLMount();
				current.index = currentMount.index;
				current.gib = currentMount.gib;
				current.rotate = currentMount.rotate;
				current.mirror = currentMount.mirror;
				current.powered = currentMount.powered;
				current.slide = currentMount.slide;
				
				((UndoableMountPropertiesEdit) ume).setCurrentValue(current);
				Main.addEdit(ume);
			}
		}
		ume = null;
	}
}
