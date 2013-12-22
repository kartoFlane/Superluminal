package com.kartoflane.superluminal.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.kartoflane.superluminal.core.ConfigIO;
import com.kartoflane.superluminal.core.Main;
import org.eclipse.swt.widgets.Text;

public class TipWindow {
	protected Shell shell;
	private GridData tipGD;
	private int width;
	private int height;
	public static int currentTip = 0;
	private Button btnPrev;
	private Button btnNext;
	private Text tipText;

	public TipWindow(Shell parent) {
		shell = new Shell(parent, SWT.DIALOG_TRIM);
		shell.setLayout(new GridLayout(3, false));
		shell.setText(Main.APPNAME + " - Tip");

		createContents();

		shell.pack();

		width = shell.getClientArea().width;
		height = shell.getBounds().height;
		tipGD.widthHint = width;
		tipText.setLayoutData(tipGD);

		currentTip = (int) (Math.random() * (Main.tipsList.size() - 1));
		setText(Main.tipsList.get(currentTip));
	}

	public void setText(String text) {
		tipText.setText(text);
		shell.setText(Main.APPNAME + " - Tip " + "#" + (currentTip + 1));
		shell.pack();
		shell.setLocation(shell.getLocation().x, shell.getLocation().y + (height - shell.getBounds().height));
		height = shell.getBounds().height;
	}

	public void open() {
		Main.shell.setEnabled(false);
		shell.open();
	}
	
	public void setLocation(int x, int y) {
		shell.setLocation(x, y);
	}

	protected void createContents() {
		
		tipText = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
		tipGD = new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1);
		tipGD.widthHint = 260;
		tipText.setLayoutData(tipGD);
		tipText.setFont(Main.appFont);

		Label label = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		final Button btnDontShow = new Button(shell, SWT.CHECK);
		btnDontShow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
		btnDontShow.setText("Don't show these messages again");
		btnDontShow.setSelection(!Main.showTips);
		btnDontShow.setFont(Main.appFont);

		btnPrev = new Button(shell, SWT.NONE);
		GridData gd_btnPrev = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnPrev.widthHint = 80;
		btnPrev.setLayoutData(gd_btnPrev);
		btnPrev.setText("<< Prev");
		btnPrev.setFont(Main.appFont);

		Button btnOk = new Button(shell, SWT.NONE);
		GridData gd_btnOk = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_btnOk.widthHint = 80;
		btnOk.setLayoutData(gd_btnOk);
		btnOk.setText("OK");
		btnOk.setFont(Main.appFont);

		btnNext = new Button(shell, SWT.NONE);
		GridData gd_btnNext = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnNext.widthHint = 80;
		btnNext.setLayoutData(gd_btnNext);
		btnNext.setText("Next >>");
		btnNext.setFont(Main.appFont);

		// Listeners

		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Main.shell.setEnabled(true);
				shell.setVisible(false);
			}
		});

		btnDontShow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Main.showTips = !btnDontShow.getSelection();
				ConfigIO.saveConfig();
			}
		});

		btnPrev.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				currentTip--;
				setText(Main.tipsList.get(currentTip));
				updateButtons();
			}
		});

		btnNext.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				currentTip++;
				setText(Main.tipsList.get(currentTip));
				updateButtons();
			}
		});
		
		shell.addListener(SWT.Traverse, new Listener() {
			@Override
			public void handleEvent(Event e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE) {
					e.doit = false;

					Main.shell.setEnabled(true);
					shell.setVisible(false);
				}
			}
		});
	}

	public void updateButtons() {
		btnPrev.setEnabled(currentTip != 0);
		btnNext.setEnabled(currentTip != Main.tipsList.size() - 1);
	}
}
