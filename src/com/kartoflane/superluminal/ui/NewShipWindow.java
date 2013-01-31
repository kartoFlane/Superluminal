package com.kartoflane.superluminal.ui;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.kartoflane.superluminal.core.Main;


public class NewShipWindow extends Dialog
{

	protected int result = 1;
	protected Shell shell;

	public NewShipWindow(Shell parent)
	{
		
		super(parent, SWT.BORDER | SWT.TITLE);
		setText(Main.APPNAME + " - New Ship");
	}

	public int open()
	{
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

	private void createContents()
	{
		shell = new Shell(getParent(), SWT.BORDER | SWT.TITLE);
		shell.setText(getText());

		shell.setSize(225, 100);
		shell.setLocation(Main.shell.getLocation().x+100, Main.shell.getLocation().y+50);
		
		Button btnPlayerShip = new Button(shell, SWT.RADIO);
		btnPlayerShip.setSelection(true);
		btnPlayerShip.setBounds(10, 10, 90, 16);
		btnPlayerShip.setText("Player Ship");
		
		Button btnEnemyShip = new Button(shell, SWT.RADIO);
		btnEnemyShip.setBounds(119, 10, 90, 16);
		btnEnemyShip.setText("Enemy Ship");
		
		Button btnOk = new Button(shell, SWT.NONE);
		btnOk.setBounds(20, 37, 75, 25);
		btnOk.setText("Confirm");
		
		Button btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(125, 37, 75, 25);
		btnCancel.setText("Cancel");

		btnPlayerShip.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = 1;
			}
		});
		
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
	}
}
