package com.kartoflane.superluminal.elements;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

import com.kartoflane.superluminal.core.Main;


public class ExportProgress {
	public Shell shell;
	public ProgressBar progressBar;
	public Label lblExporting;
	private double progress = 0.0;
	
	public ExportProgress () {
		shell = new Shell(Main.shell, SWT.BORDER);
		shell.setSize(160, 60);
		
		shell.setLayout(new GridLayout(1, false));
		
		lblExporting = new Label(shell, SWT.NONE);
		lblExporting.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 2));
		lblExporting.setText("Exporting...");
		
		progressBar = new ProgressBar(shell, SWT.SMOOTH);
		progressBar.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1));
		progressBar.setSelection(0);
		progressBar.setMaximum(100);
	}
	
	public void open(Shell parent) {
		shell.open();
		
		Rectangle bounds = parent.getBounds();
		shell.setLocation(bounds.x+bounds.width/2-80,
						  bounds.y+bounds.height/2-30);
	}
	
	public void increment(double d) {
		progress += d;
		progressBar.setSelection((int) Math.round(progress));
	}
	
	public void setText(Object o) {
		lblExporting.setText(""+o);
	}
	
	public void dispose() {
		shell.dispose();
	}
}
