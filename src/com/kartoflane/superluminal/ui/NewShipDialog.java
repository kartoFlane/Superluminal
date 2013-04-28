package com.kartoflane.superluminal.ui;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.kartoflane.superluminal.core.Main;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;


public class NewShipDialog extends Dialog {
	protected int result = 1;
	protected Shell shell;

	public NewShipDialog(Shell parent) {
		
		super(parent, SWT.BORDER | SWT.TITLE);
		setText(Main.APPNAME + " - New Ship");
	}

	public int open() {
		createContents();
		shell.open();
		shell.layout();
		Main.shell.setEnabled(false);
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void createContents() {
		shell = new Shell(getParent(), SWT.BORDER | SWT.TITLE);
		shell.setText(getText());
		shell.setFont(Main.appFont);

		shell.setSize(225, 100);
		shell.setLocation(Main.shell.getLocation().x+100, Main.shell.getLocation().y+50);
		shell.setLayout(new GridLayout(4, false));
		
		Button btnPlayerShip = new Button(shell, SWT.RADIO);
		btnPlayerShip.setFont(Main.appFont);
		btnPlayerShip.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 1, 1));
		btnPlayerShip.setSelection(true);
		btnPlayerShip.setText("Player Ship");
		
		Button btnEnemyShip = new Button(shell, SWT.RADIO);
		btnEnemyShip.setFont(Main.appFont);
		btnEnemyShip.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 3, 1));
		btnEnemyShip.setText("Enemy Ship");
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 4, 1));
		GridLayout gl_composite = new GridLayout(4, false);
		gl_composite.marginHeight = 0;
		gl_composite.marginWidth = 0;
		composite.setLayout(gl_composite);
		
		Button btnOk = new Button(composite, SWT.NONE);
		btnOk.setFont(Main.appFont);
		GridData gd_btnOk = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
		gd_btnOk.minimumWidth = 80;
		btnOk.setLayoutData(gd_btnOk);
		btnOk.setSize(48, 25);
		btnOk.setText("Confirm");

		Button btnCancel = new Button(composite, SWT.NONE);
		btnCancel.setFont(Main.appFont);
		GridData gd_btnCancel = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnCancel.minimumWidth = 80;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setSize(80, 25);
		btnCancel.setText("Cancel");
		
		shell.pack();
		
		btnEnemyShip.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = 2;
			}
		});
		
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		
		btnCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				result = 0;
				shell.dispose();
			}
		});

		btnPlayerShip.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = 1;
			}
		});
	}
}
