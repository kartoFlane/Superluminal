package com.kartoflane.superluminal.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.elements.Systems;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;


public class PropertiesWindow
{

	protected Shell shell;
	private Spinner textLevel;
	private Spinner textPower;
	private int max;
	private Systems sys;
	private Scale scaleLevel;
	private Scale scalePower;
	private Button btnOk;
	private Button btnAvailable;
	private Composite composite;

	public PropertiesWindow(Shell parent)
	{
		shell = new Shell(parent, SWT.BORDER | SWT.TITLE);
		shell.setFont(Main.appFont);
		shell.setSize(186, 235);
		shell.setLocation(parent.getLocation().x+100, parent.getLocation().y+100);
		shell.setLayout(new GridLayout(2, false));
		
		Label lblLevel_1 = new Label(shell, SWT.NONE);
		lblLevel_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblLevel_1.setFont(Main.appFont);
		lblLevel_1.setText("Level:");
		
		textLevel = new Spinner(shell, SWT.BORDER | SWT.CENTER);
		textLevel.setMinimum(1);
		textLevel.setMaximum(99);
		textLevel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 1, 1));
		textLevel.setFont(Main.appFont);
		textLevel.setTextLimit(2);
		
		scaleLevel = new Scale(shell, SWT.NONE);
		scaleLevel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 5, 2));
		scaleLevel.setFont(Main.appFont);
		scaleLevel.setPageIncrement(1);
		scaleLevel.setMinimum(1);
		scaleLevel.setMaximum(8);
		
		Label lblPower = new Label(shell, SWT.NONE);
		lblPower.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblPower.setFont(Main.appFont);
		lblPower.setText("Power");
		
		textPower = new Spinner(shell, SWT.BORDER | SWT.CENTER);
		textPower.setMinimum(1);
		textPower.setMaximum(99);
		textPower.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 1, 1));
		textPower.setFont(Main.appFont);
		textPower.setTextLimit(2);
		
		scalePower = new Scale(shell, SWT.NONE);
		scalePower.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 2));
		scalePower.setFont(Main.appFont);
		scalePower.setPageIncrement(1);
		scalePower.setMaximum(8);
		
		btnAvailable = new Button(shell, SWT.CHECK);
		btnAvailable.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 3, 1));
		btnAvailable.setFont(Main.appFont);
		btnAvailable.setToolTipText("When set to true, this system will be available and installed at the beggining of the game.\n"
											+"When set to false, the system will have to be bought at a store to unlock it.\n"
											+"Systems that have not been placed on the ship will not be functional, even after buying.");
		btnAvailable.setText("Available At Start");
		
		composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new GridLayout(2, true));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));
		
		btnOk = new Button(composite, SWT.NONE);
		btnOk.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnOk.setFont(Main.appFont);
		btnOk.setText("OK");
		
		Button btnCancel = new Button(composite, SWT.NONE);
		btnCancel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnCancel.setFont(Main.appFont);
		btnCancel.setText("Cancel");
		
		shell.pack();
		
		textLevel.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				scaleLevel.setSelection(textLevel.getSelection());
				scalePower.setMaximum(scaleLevel.getSelection());
				textPower.setMaximum(scaleLevel.getSelection());
			}
		});
		
		textPower.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				scalePower.setSelection(textPower.getSelection());
			}
		});
		
		btnCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.setVisible(false);
			}
		});
		
		btnOk.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Main.ship.levelMap.put(sys, Integer.valueOf(textLevel.getText()));
				int i = Integer.valueOf(textPower.getText());
				Main.ship.powerMap.put(sys, ((i<=Integer.valueOf(textLevel.getText()) ? i : Integer.valueOf(textLevel.getText()))));

				Main.ship.startMap.put(sys, btnAvailable.getSelection());
				Main.systemsMap.get(sys).setAvailable(btnAvailable.getSelection());
				
				shell.setVisible(false);
			}
		});
		
		scaleLevel.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				textLevel.setSelection(scaleLevel.getSelection());
				scalePower.setMaximum(scaleLevel.getSelection());
				textPower.setMaximum(scaleLevel.getSelection());
			}
		});
		
		scalePower.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				textPower.setSelection(scalePower.getSelection());
			}
		});
		

		shell.addListener(SWT.Traverse, new Listener() {
			@Override
			public void handleEvent(Event e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE) {
					e.doit = false;
					shell.setVisible(false);
				}
			}
		});
	}

	public void open()
	{
		scalePower.setEnabled(false);
		scaleLevel.setEnabled(false);
		textPower.setEnabled(false);
		textLevel.setEnabled(false);
		btnOk.setEnabled(false);
		//btnAvailable.setEnabled(false);

		shell.setLocation(Main.shell.getLocation().x+100, Main.shell.getLocation().y+100);
		shell.open();
		
		String s = Main.selectedRoom.getSystem().toString().toLowerCase();
		s = s.substring(0, 1).toUpperCase() + s.substring(1, s.length());
		shell.setText(s);

		int level = 0;
		int power = 0;
		if (Main.selectedRoom != null) {
			sys = Main.selectedRoom.getSystem();
			level = Main.ship.levelMap.get(sys);
			power = Main.ship.powerMap.get(sys);
			max = (sys.equals(Systems.PILOT) || sys.equals(Systems.OXYGEN) || sys.equals(Systems.TELEPORTER) || sys.equals(Systems.CLOAKING)
					 || sys.equals(Systems.MEDBAY) || sys.equals(Systems.SENSORS) || sys.equals(Systems.DOORS))
					? 3
				: (sys.equals(Systems.WEAPONS) || sys.equals(Systems.SHIELDS) || sys.equals(Systems.ENGINES) || sys.equals(Systems.DRONES))
					? 8
				: (sys.equals(Systems.ARTILLERY))
					? 4
					: 0;
			
			max = (!Main.ship.isPlayer && (sys.equals(Systems.WEAPONS) || sys.equals(Systems.ENGINES) || sys.equals(Systems.SHIELDS)))
					? 10
					: max;
		}
		
		textLevel.setSelection(((level<1)?1:level));
		textPower.setSelection(power);
		scaleLevel.setMaximum(max);
		scalePower.setMaximum((level<1)?1:level);
		scaleLevel.setSelection(level);
		scalePower.setSelection(power);
		textLevel.setMaximum(scaleLevel.getMaximum());
		textPower.setMaximum(scalePower.getMaximum());

		if (sys.equals(Systems.EMPTY)) {
			btnAvailable.setSelection(true);
			btnAvailable.setEnabled(false);
		} else {
			btnAvailable.setSelection(Main.systemsMap.get(sys).isAvailable());
			btnAvailable.setEnabled(true);
		}
		/*
		if (!sys.equals(Systems.EMPTY) && Main.ship.isPlayer) {
			btnAvailable.setSelection(Main.ship.startMap.get(sys));
			btnAvailable.setEnabled(true);
		} else {
			btnAvailable.setSelection(true);
		}*/

		if (!sys.equals(Systems.EMPTY) && !sys.equals(Systems.PILOT) && !sys.equals(Systems.DOORS) && !sys.equals(Systems.SENSORS)) {
			scalePower.setEnabled(true);
			textPower.setEnabled(true);
		}
		
		if (max > 0) {
			scaleLevel.setEnabled(true);
			textLevel.setEnabled(true);
			btnOk.setEnabled(true);
		}
		
		
		Display display = shell.getParent().getDisplay();
		while (!shell.isVisible()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
