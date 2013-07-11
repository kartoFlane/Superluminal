package com.kartoflane.superluminal.ui;

import java.io.File;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.core.ShipIO;

public class ShipChoiceDialog {
	Shell shell;
	List list;
	private Button btnCancel;
	private Button btnLoad;
	private Display display;

	public String result;
	public int declaration;
	private String blueprint;
	private boolean exit = false;

	private Label lblMessage;

	public ShipChoiceDialog(Shell parent) {
		shell = new Shell(parent, SWT.BORDER | SWT.RESIZE | SWT.TITLE);
		shell.setText(Main.APPNAME + " - Ship Choice");
		shell.setLayout(new GridLayout(1, false));

		display = Display.getCurrent();

		createContents();

		shell.pack();
		shell.setSize(320, shell.getSize().y);
		shell.setMinimumSize(320, shell.getSize().y);
	}

	public String open() {
		Main.shell.setEnabled(false);
		btnLoad.setEnabled(false);
		shell.setVisible(true);
		shell.setActive();
		result = "";
		exit = false;
		lblMessage.setText(String.format("Superluminal has found several ships sharing the %s blueprint name.\nChoose which one you'd like to load:", blueprint));
		shell.layout();

		while (shell.isVisible()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		return (exit) ? null : result;
	}

	private void createContents() {
		lblMessage = new Label(shell, SWT.WRAP);
		lblMessage.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		lblMessage.setText("\n\n\n");
		lblMessage.setFont(Main.appFont);

		list = new List(shell, SWT.BORDER | SWT.V_SCROLL);
		GridData gd_list = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_list.minimumHeight = 100;
		list.setLayoutData(gd_list);
		list.setFont(Main.appFont);

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.marginHeight = 0;
		gl_composite.marginWidth = 0;
		composite.setLayout(gl_composite);

		btnLoad = new Button(composite, SWT.NONE);
		GridData gd_btnLoad = new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 1);
		gd_btnLoad.widthHint = 80;
		btnLoad.setLayoutData(gd_btnLoad);
		btnLoad.setFont(Main.appFont);
		btnLoad.setText("Load");

		btnCancel = new Button(composite, SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.RIGHT, SWT.BOTTOM, false, false, 1, 1);
		gd_btnCancel.widthHint = 80;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setFont(Main.appFont);
		btnCancel.setText("Cancel");

		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.setVisible(false);
				// Main.shell.setEnabled(true);
				Main.shell.setActive();
				exit = true;
			}
		});

		btnLoad.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = blueprint;
				declaration = 0;
				for (String s : list.getItems()) {
					declaration++;
					if (s.equals(list.getSelection()[0]))
						break;
				}

				shell.setVisible(false);
				// Main.shell.setEnabled(true);
				Main.shell.setActive();
			}
		});

		shell.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event event) {
				btnCancel.notifyListeners(SWT.Selection, null);
			}
		});

		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if (list.getSelectionCount() > 0)
					btnLoad.notifyListeners(SWT.Selection, null);
			}
		});

		list.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btnLoad.setEnabled(list.getSelection() != null && list.getSelection().length > 0 && list.getSelection()[0] != null);
			}
		});
	}

	public void setChoices(Collection<String> list, File fileToScan) {
		this.list.removeAll();
		blueprint = list.toArray()[0].toString();
		int i = 1;
		for (String s : list) {
			this.list.add(String.format("%s", ShipIO.getShipName(s, fileToScan, i)));
			i++;
		}
	}
}
