package com.kartoflane.superluminal.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.elements.FTLGib;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;

public class RenameGibDialog {
	protected Shell shell;
	private Text nameText;
	private FTLGib gib;
	private Shell parent;
	
	public RenameGibDialog(Shell parent) {
		shell = new Shell(parent, SWT.DIALOG_TRIM);
		shell.setLayout(new GridLayout(2, false));
		shell.setText("Rename gib");
		this.parent = parent;
		
		createContents();
		
		shell.pack();
	}
	
	public void open(FTLGib g) {
		shell.setLocation(parent.getLocation().x + parent.getSize().x/2 - shell.getSize().x/2, parent.getLocation().y + parent.getSize().y/3 - shell.getSize().y/2);
		this.gib = g;
		shell.open();
		
		nameText.setFocus();
		nameText.setText(g.ID);
		nameText.selectAll();
		Main.shell.setEnabled(false);
		Main.gibDialog.getShell().setEnabled(false);
	}
	
	protected void createContents() {
		nameText = new Text(shell, SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		nameText.setTextLimit(30);
		
		final Button btnConfirm = new Button(shell, SWT.NONE);
		GridData gd_btnConfirm = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnConfirm.widthHint = 80;
		btnConfirm.setLayoutData(gd_btnConfirm);
		btnConfirm.setText("Confirm");
		
		final Button btnCancel = new Button(shell, SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnCancel.widthHint = 80;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setText("Cancel");
		
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.setVisible(false);
				nameText.setText("");
				Main.shell.setEnabled(true);
				Main.gibDialog.getShell().setEnabled(true);
			}
		});
		
		btnConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.setVisible(false);
				gib.rename(nameText.getText());
				nameText.setText("");
				Main.gibDialog.refreshList();
				Main.shell.setEnabled(true);
				Main.gibDialog.getShell().setEnabled(true);
				Main.gibDialog.list.select(gib.number - 1);
			}
		});
		
		shell.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event event) {
				event.doit = false;
				btnCancel.notifyListeners(SWT.Selection, null);
			}
		});
		
		shell.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_RETURN) {
					e.doit = false;
					btnConfirm.notifyListeners(SWT.Selection, null);
				}
			}
		});
	}
	
	public boolean isVisible() {
		return shell.isVisible();
	}
}
