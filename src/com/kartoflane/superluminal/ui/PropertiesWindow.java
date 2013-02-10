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
		lblLevel_1.setFont(Main.appFont);
		lblLevel_1.setText("Level:");
		
		textLevel = new Spinner(shell, SWT.BORDER | SWT.CENTER);
		textLevel.setMinimum(1);
		textLevel.setMaximum(8);
		GridData gd_textLevel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_textLevel.widthHint = 20;
		textLevel.setLayoutData(gd_textLevel);
		textLevel.setFont(Main.appFont);
		textLevel.setTextLimit(1);
		
		scaleLevel = new Scale(shell, SWT.NONE);
		GridData gd_scaleLevel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 5, 2);
		gd_scaleLevel.heightHint = 40;
		scaleLevel.setLayoutData(gd_scaleLevel);
		scaleLevel.setFont(Main.appFont);
		scaleLevel.setPageIncrement(1);
		scaleLevel.setMinimum(1);
		scaleLevel.setMaximum(8);
		
		Label lblPower = new Label(shell, SWT.NONE);
		lblPower.setFont(Main.appFont);
		lblPower.setText("Power");
		
		textPower = new Spinner(shell, SWT.BORDER | SWT.CENTER);
		textPower.setMinimum(1);
		textPower.setMaximum(8);
		GridData gd_textPower = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_textPower.widthHint = 20;
		textPower.setLayoutData(gd_textPower);
		textPower.setFont(Main.appFont);
		textPower.setTextLimit(1);
		
		scalePower = new Scale(shell, SWT.NONE);
		GridData gd_scalePower = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 2);
		gd_scalePower.heightHint = 40;
		scalePower.setLayoutData(gd_scalePower);
		scalePower.setFont(Main.appFont);
		scalePower.setPageIncrement(1);
		scalePower.setMaximum(8);
		
		btnAvailable = new Button(shell, SWT.CHECK);
		btnAvailable.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		btnAvailable.setFont(Main.appFont);
		btnAvailable.setToolTipText("When set to true, this system will be available to the player since the beggining of the game.\n"
											+"When set to false, the player will have to buy the system at a store to unlock it.\n"
											+"The player won't be able to buy systems that have not been placed on the ship.");
		btnAvailable.setText("Available At Start");
		
		composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 5, 1));
		
		btnOk = new Button(composite, SWT.NONE);
		GridData gd_btnOk = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnOk.heightHint = 25;
		gd_btnOk.widthHint = 80;
		btnOk.setLayoutData(gd_btnOk);
		btnOk.setFont(Main.appFont);
		btnOk.setText("OK");
		
		Button btnCancel = new Button(composite, SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnCancel.heightHint = 25;
		gd_btnCancel.widthHint = 80;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setFont(Main.appFont);
		btnCancel.setText("Cancel");
		
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
		
		shell.open();
		
		String s = Main.selectedRoom.getSystem().toString().toLowerCase();
		s = s.substring(0, 1).toUpperCase() + s.substring(1, s.length());
		shell.setText(s);
		shell.setLocation(Main.shell.getLocation().x+100, Main.shell.getLocation().y+100);

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

		btnAvailable.setSelection(Main.systemsMap.get(sys).isAvailable());
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
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
