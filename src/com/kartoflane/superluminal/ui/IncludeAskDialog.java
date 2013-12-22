package com.kartoflane.superluminal.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal.core.Main;

public class IncludeAskDialog {
	public Shell shell;
	public int result = 0;
	private Display display;

	public IncludeAskDialog(Shell parent) {
		shell = new Shell(parent, SWT.BORDER | SWT.TITLE);
		shell.setLayout(new GridLayout(1, false));
		shell.setText("Include Mod - Specify PNG Type");
		
		display = Display.getDefault();
		
		createContents();
	}

	private void createContents() {
		Label lblPngFileDetected = new Label(shell, SWT.WRAP);
		GridData gd_lblPngFileDetected = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 2);
		gd_lblPngFileDetected.widthHint = 200;
		lblPngFileDetected.setLayoutData(gd_lblPngFileDetected);
		lblPngFileDetected.setText("PNG file detected. Please specify what kind of graphic it represents, so that Superluminal knows where to put it:");
		lblPngFileDetected.setFont(Main.appFont);
		
		Composite choice = new Composite(shell, SWT.NONE);
		choice.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		choice.setLayout(new GridLayout(1, false));
		
		Button btnShip = new Button(choice, SWT.RADIO);
		btnShip.setToolTipText("Hull, shield, floor, cloak or gib images, etc.");
		btnShip.setSize(90, 16);
		btnShip.setText("Ship-related graphic");
		btnShip.setFont(Main.appFont);
		
		Button btnInterior = new Button(choice, SWT.RADIO);
		btnInterior.setToolTipText("Room interiors and their glow images.");
		btnInterior.setBounds(0, 0, 90, 16);
		btnInterior.setText("Room interior-related graphic");
		btnInterior.setFont(Main.appFont);
		
		Button btnWeapon = new Button(choice, SWT.RADIO);
		btnWeapon.setToolTipText("Weapon animation sprites, weapon glow images, missile sprites, etc.");
		btnWeapon.setBounds(0, 0, 90, 16);
		btnWeapon.setText("Weapon-related graphic");
		btnWeapon.setFont(Main.appFont);
		
		Label lblIfTheFile = new Label(shell, SWT.WRAP);
		GridData gd_lblIfTheFile = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_lblIfTheFile.widthHint = 200;
		lblIfTheFile.setLayoutData(gd_lblIfTheFile);
		lblIfTheFile.setText("If the file doesn't match any of the above, then it doesn't have to be included in the editor.");
		lblIfTheFile.setFont(Main.appFont);
		
		Composite buttons = new Composite(shell, SWT.NONE);
		GridData gd_buttons = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_buttons.widthHint = 200;
		buttons.setLayoutData(gd_buttons);
		GridLayout gl_buttons = new GridLayout(2, false);
		gl_buttons.marginWidth = 0;
		gl_buttons.marginHeight = 0;
		buttons.setLayout(gl_buttons);
		
		final Button btnAccept = new Button(buttons, SWT.NONE);
		btnAccept.setEnabled(false);
		GridData gd_btnAccept = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_btnAccept.widthHint = 80;
		btnAccept.setLayoutData(gd_btnAccept);
		btnAccept.setSize(80, 25);
		btnAccept.setText("Accept");
		btnAccept.setFont(Main.appFont);
		
		Button btnCancel = new Button(buttons, SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnCancel.widthHint = 80;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setSize(48, 25);
		btnCancel.setText("Cancel");
		btnCancel.setFont(Main.appFont);
		
		shell.pack();
		
		btnShip.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = 1;
				btnAccept.setEnabled(true);
			}
		});
		
		btnInterior.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = 2;
				btnAccept.setEnabled(true);
			}
		});
		
		btnWeapon.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = 2;
				btnAccept.setEnabled(true);
			}
		});
		
		btnAccept.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
				shell.dispose();
				Main.shell.setEnabled(true);
			}
		});
		
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = -1;
				shell.close();
				shell.dispose();
				Main.shell.setEnabled(true);
			}
		});
	}
	
	public void open() {
		shell.open();
		Main.shell.setEnabled(false);
		
		while(!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
