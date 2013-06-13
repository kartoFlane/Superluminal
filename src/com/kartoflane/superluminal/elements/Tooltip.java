package com.kartoflane.superluminal.elements;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class Tooltip extends Composite {
	private Text text;

	public Tooltip(Composite parent) {
		super(parent, SWT.NONE);
		
		text = new Text(this, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		text.setEditable(false);
		text.setBounds(0, 0, 250, 200);
		text.setEnabled(true);
	}
	
	public void setSize(int w, int h) {
		text.setSize(w, h);
	}
	
	public void setText(String msg) {
		if (msg == null) {
			text.setText("");
		} else {
			text.setText(msg);
		}
		text.pack();
		this.pack();
	}
}
