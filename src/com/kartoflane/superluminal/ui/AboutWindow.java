package com.kartoflane.superluminal.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.core.ShipIO;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

public class AboutWindow {
	public Shell shell;
	
	public AboutWindow(Shell parent) {
		shell = new Shell(parent, SWT.BORDER | SWT.TITLE);
		shell.setText(Main.APPNAME + " - About");
		shell.setLayout(new GridLayout(1, false));
		
		Label label = new Label(shell, SWT.WRAP | SWT.CENTER);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		label.setText("Superluminal - a ship editor for FTL: Faster Than Light" + ShipIO.lineDelimiter
					+ "Version: " + Main.VERSION + ShipIO.lineDelimiter + ShipIO.lineDelimiter
					+ "Created by kartoFlane");
		
		Button btnOK = new Button(shell, SWT.NONE);
		GridData gd_btnOK = new GridData(SWT.CENTER, SWT.BOTTOM, true, false, 1, 1);
		gd_btnOK.widthHint = 80;
		btnOK.setLayoutData(gd_btnOK);
		btnOK.setText("OK");
		
		btnOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.setVisible(false);
				Main.shell.setEnabled(true);
				Main.shell.setActive();
				
				shell.dispose();
				shell = null;
			}
		});
		
		shell.pack();
		
		shell.setLocation(parent.getLocation().x + parent.getSize().x/2 - shell.getSize().x/2, parent.getLocation().y + parent.getSize().y/4 - shell.getSize().y/2);
	}
}
