package com.kartoflane.superluminal.elements;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;

import com.kartoflane.superluminal.core.Main;

public class Tooltip extends Composite {
	private Text text;

	public Tooltip() {
		super(Main.canvas, SWT.NONE);
		
		text = new Text(this, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		text.setEditable(false);
		text.setBounds(0, 0, 250, 200);
		text.setEnabled(true);
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
