package com.kartoflane.superluminal.ui;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.core.ShipIO;
import com.kartoflane.superluminal.elements.FTLGib;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Group;


public class GibPropertiesWindow {
	protected Shell shell;
	private Button btnOk;
	private Composite composite;
	private Group grpVelocity;
	private FTLGib gib; 
	private Spinner spVelMin;
	private Spinner spVelMax;
	private Spinner spAngMin;
	private Spinner spAngMax;
	private Scale scMinDir;
	private Scale scMaxDir;
	
	private Point oldDir = new Point(0,0);
	private Label lblTip;

	public GibPropertiesWindow(Shell parent) {
		shell = new Shell(parent, SWT.BORDER | SWT.TITLE);
		shell.setFont(Main.appFont);
		shell.setLocation(parent.getLocation().x+100, parent.getLocation().y+100);
		shell.setLayout(new GridLayout(3, false));
		
		grpVelocity = new Group(shell, SWT.NONE);
		grpVelocity.setLayout(new GridLayout(2, false));
		grpVelocity.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		grpVelocity.setText("Velocity");
		grpVelocity.setFont(Main.appFont);
		
		Label lblVelMin = new Label(grpVelocity, SWT.NONE);
		lblVelMin.setBounds(0, 0, 55, 15);
		lblVelMin.setText("Min:");
		lblVelMin.setFont(Main.appFont);
		
		spVelMin = new Spinner(grpVelocity, SWT.BORDER);
		spVelMin.setToolTipText("Pixels per second" + ShipIO.lineDelimiter + "1.0 = 10 px/s");
		spVelMin.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		spVelMin.setMinimum(-100);
		spVelMin.setDigits(1);
		spVelMin.setFont(Main.appFont);
		
		Label lblVelMax = new Label(grpVelocity, SWT.NONE);
		lblVelMax.setBounds(0, 0, 55, 15);
		lblVelMax.setText("Max");
		lblVelMax.setFont(Main.appFont);
		
		spVelMax = new Spinner(grpVelocity, SWT.BORDER);
		spVelMax.setToolTipText("Pixels per second" + ShipIO.lineDelimiter + "1.0 = 10 px/s");
		spVelMax.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		spVelMax.setDigits(1);
		spVelMax.setMinimum(-100);
		spVelMax.setFont(Main.appFont);
		
		Group grpAngular = new Group(shell, SWT.NONE);
		grpAngular.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpAngular.setText("Angular");
		grpAngular.setLayout(new GridLayout(2, false));
		grpAngular.setFont(Main.appFont);
		
		Label lblAngMin = new Label(grpAngular, SWT.NONE);
		lblAngMin.setText("Min:");
		lblAngMin.setFont(Main.appFont);
		
		spAngMin = new Spinner(grpAngular, SWT.BORDER);
		spAngMin.setToolTipText("Degrees per second" + ShipIO.lineDelimiter + "1.0 = 6°/s" + ShipIO.lineDelimiter + "10.0 = full revolution" + ShipIO.lineDelimiter + "Positive values = clockwise rotation");
		spAngMin.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		spAngMin.setMinimum(-100);
		spAngMin.setDigits(1);
		spAngMin.setFont(Main.appFont);
		
		Label lblAngMax = new Label(grpAngular, SWT.NONE);
		lblAngMax.setText("Max");
		lblAngMax.setFont(Main.appFont);
		
		spAngMax = new Spinner(grpAngular, SWT.BORDER);
		spAngMax.setToolTipText("Degrees per second" + ShipIO.lineDelimiter + "1.0 = 6°/s" + ShipIO.lineDelimiter + "10.0 = full revolution" + ShipIO.lineDelimiter + "Positive values = clockwise rotation");
		spAngMax.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		spAngMax.setMinimum(-100);
		spAngMax.setDigits(1);
		spAngMax.setFont(Main.appFont);
		
		Group grpDirection = new Group(shell, SWT.NONE);
		grpDirection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		grpDirection.setText("Direction");
		GridLayout gl_grpDirection = new GridLayout(2, false);
		gl_grpDirection.horizontalSpacing = 0;
		gl_grpDirection.marginWidth = 0;
		grpDirection.setLayout(gl_grpDirection);
		grpDirection.setFont(Main.appFont);
		
		Label lblDirMin = new Label(grpDirection, SWT.NONE);
		lblDirMin.setText("Min:");
		lblDirMin.setFont(Main.appFont);
		
		scMinDir = new Scale(grpDirection, SWT.NONE);
		scMinDir.setMaximum(720);
		scMinDir.setSelection(360);
		scMinDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		scMinDir.setFont(Main.appFont);
		scMinDir.setToolTipText("0");
		
		Label lblDirMax = new Label(grpDirection, SWT.NONE);
		lblDirMax.setText("Max");
		lblDirMax.setFont(Main.appFont);
		
		scMaxDir = new Scale(grpDirection, SWT.NONE);
		scMaxDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		scMaxDir.setMaximum(720);
		scMaxDir.setSelection(360);
		scMaxDir.setFont(Main.appFont);
		scMaxDir.setToolTipText("0");
		
		lblTip = new Label(shell, SWT.WRAP);
		GridData gd_lblTip = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gd_lblTip.widthHint = 211;
		lblTip.setLayoutData(gd_lblTip);
		lblTip.setText("You can use Ctrl+Arrow keys to precisely control the sliders");
		
		composite = new Composite(shell, SWT.NONE);
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 5, 2));
		
		btnOk = new Button(composite, SWT.NONE);
		GridData gd_btnOk = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnOk.minimumWidth = 80;
		btnOk.setLayoutData(gd_btnOk);
		btnOk.setFont(Main.appFont);
		btnOk.setText("OK");
		
		Button btnCancel = new Button(composite, SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnCancel.widthHint = 80;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setFont(Main.appFont);
		btnCancel.setText("Cancel");
		
		shell.pack();
		
		btnOk.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				gib.minVel = spVelMin.getSelection()*0.1;
				gib.maxVel = spVelMax.getSelection()*0.1;
				gib.minAng = spAngMin.getSelection()*0.1;
				gib.maxAng = spAngMax.getSelection()*0.1;
				gib.minDir = scMinDir.getSelection()-360;
				gib.maxDir = scMaxDir.getSelection()-360;
				
				shell.setVisible(false);
				Main.gibDialog.list.setEnabled(true);
			}
		});
		
		btnCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				gib.minDir = oldDir.x;
				gib.maxDir = oldDir.y;

				shell.setVisible(false);
				Main.gibDialog.list.setEnabled(true);
			}
		});

		shell.addListener(SWT.Traverse, new Listener() {
			@Override
			public void handleEvent(Event e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE) {
					e.doit = false;
					
					gib.minDir = oldDir.x;
					gib.maxDir = oldDir.y;
					
					shell.setVisible(false);
					Main.gibDialog.list.setEnabled(true);
				}
			}
		});
		
		scMinDir.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				gib.minDir = scMinDir.getSelection()-360;
				Main.canvasRedraw(gib.getBounds(), false);
				scMinDir.setToolTipText(""+gib.minDir);
			}
		});
		
		scMaxDir.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				gib.maxDir = scMaxDir.getSelection()-360;
				Main.canvasRedraw(gib.getBounds(), false);
				scMaxDir.setToolTipText(""+gib.maxDir);
			}
		});
	}

	public void open() {
		shell.setLocation(Main.shell.getLocation().x+100, Main.shell.getLocation().y+100);
		shell.open();
		shell.setVisible(true);
		
		gib = Main.selectedGib;
		
		shell.setText("Gib " + gib.ID);
		
		oldDir.x = gib.minDir; oldDir.y = gib.maxDir;
		
		spVelMin.setSelection((int)(gib.minVel*10));
		spVelMax.setSelection((int)(gib.maxVel*10));
		spAngMin.setSelection((int)(gib.minAng*10));
		spAngMax.setSelection((int)(gib.maxAng*10));
		scMinDir.setSelection(gib.minDir+360);
		scMaxDir.setSelection(gib.maxDir+360);
		scMinDir.setToolTipText(""+gib.minDir);
		scMaxDir.setToolTipText(""+gib.maxDir);
		
		Main.gibDialog.list.setEnabled(false);
	}
	
	public Shell getShell() {
		return shell;
	}
	
	public void escape() {
		if (gib != null && shell.isVisible()) {
			gib.minDir = oldDir.x;
			gib.maxDir = oldDir.y;
		}
		
		shell.setVisible(false);
		Main.gibDialog.list.setEnabled(true);
	}
	
	public boolean isVisible() {
		return shell.isVisible();
	}
}
