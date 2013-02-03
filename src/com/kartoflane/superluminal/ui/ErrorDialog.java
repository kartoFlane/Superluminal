package com.kartoflane.superluminal.ui;

import java.util.Set;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.core.ShipIO;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ErrorDialog
{

	protected Shell shell;
	private Text errors;
	private Button btnClose;
	private Composite composite;
	private Button btnClear;

	public ErrorDialog(Shell parent)
	{
		createContents();
	}
	
	public void open()
	{
		shell.open();
	}

	private void createContents()
	{
		shell = new Shell(Main.shell, SWT.BORDER | SWT.TITLE);
		shell.setSize(500, 230);
		shell.setText(Main.APPNAME + " - Errors");
		GridLayout gl_shell = new GridLayout(1, false);
		gl_shell.verticalSpacing = 0;
		gl_shell.marginWidth = 0;
		gl_shell.marginHeight = 0;
		gl_shell.horizontalSpacing = 0;
		shell.setLayout(gl_shell);
		
		Composite consoleC = new Composite(shell, SWT.BORDER);
		consoleC.setLayout(new FillLayout(SWT.HORIZONTAL));
		consoleC.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		errors = new Text(consoleC, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		errors.setFont(Main.appFont);
		errors.setEditable(false);
		
		composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new GridLayout(10, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		btnClear = new Button(composite, SWT.NONE);
		btnClear.setFont(Main.appFont);
		GridData gd_btnClear = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnClear.widthHint = 80;
		btnClear.setLayoutData(gd_btnClear);
		btnClear.setText("Clear");
		
		btnClose = new Button(composite, SWT.NONE);
		btnClose.setFont(Main.appFont);
		GridData gd_btnClose = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnClose.widthHint = 80;
		btnClose.setLayoutData(gd_btnClose);
		btnClose.setText("Close");

		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				errors.setText("");
				ShipIO.errors.clear();
			}
		});
		
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.setVisible(false);
			}
		});
	}
	
	public void print(String s) {
		errors.setText(errors.getText() + (!errors.getText().equals("") ? ShipIO.lineDelimiter : "") + s);
	}
	
	public void printErrors(Set<String> errorSet) {
		for (String s : errorSet) {
			print(s);
		}
	}
}
