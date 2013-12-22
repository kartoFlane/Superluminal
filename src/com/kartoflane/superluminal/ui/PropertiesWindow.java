package com.kartoflane.superluminal.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.core.ShipIO;
import com.kartoflane.superluminal.elements.FTLRoom;
import com.kartoflane.superluminal.elements.Systems;

public class PropertiesWindow {
	protected Shell shell;
	private Spinner spLevel;
	private Spinner spPower;
	private int max;
	private Systems sys;
	private Scale scaleLevel;
	private Scale scalePower;
	private Button btnOk;
	private Button btnAvailable;
	private Composite composite;

	private Label lblLevel;
	private Label lblPower;
	
	private FTLRoom currentRoom;
	private Label lblRoomId;
	private Spinner spId;

	public PropertiesWindow(Shell parent) {
		shell = new Shell(parent, SWT.BORDER | SWT.TITLE);
		shell.setFont(Main.appFont);
		shell.setSize(186, 235);
		shell.setLocation(parent.getLocation().x + 100, parent.getLocation().y + 100);
		shell.setLayout(new GridLayout(2, false));
		
		lblRoomId = new Label(shell, SWT.NONE);
		lblRoomId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblRoomId.setText("Room ID:");
		lblRoomId.setToolTipText("The room's number.\nRoom with a lower ID is on higher layer than a room with higher ID.");
		lblRoomId.setFont(Main.appFont);
		
		spId = new Spinner(shell, SWT.BORDER);
		spId.setMaximum(999);
		spId.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, true, 1, 1));
		spId.setFont(Main.appFont);

		lblLevel = new Label(shell, SWT.NONE);
		lblLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblLevel.setFont(Main.appFont);
		lblLevel.setText("Level:");

		spLevel = new Spinner(shell, SWT.BORDER | SWT.CENTER);
		spLevel.setMinimum(1);
		spLevel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, true, 1, 1));
		spLevel.setFont(Main.appFont);
		spLevel.setTextLimit(2);

		scaleLevel = new Scale(shell, SWT.NONE);
		scaleLevel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 5, 2));
		scaleLevel.setFont(Main.appFont);
		scaleLevel.setPageIncrement(1);
		scaleLevel.setMinimum(1);
		scaleLevel.setMaximum(8);

		lblPower = new Label(shell, SWT.NONE);
		lblPower.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblPower.setFont(Main.appFont);
		lblPower.setText("Power");

		spPower = new Spinner(shell, SWT.BORDER | SWT.CENTER);
		spPower.setMinimum(1);
		spPower.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, true, 1, 1));
		spPower.setFont(Main.appFont);
		spPower.setTextLimit(2);

		scalePower = new Scale(shell, SWT.NONE);
		scalePower.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 2));
		scalePower.setFont(Main.appFont);
		scalePower.setPageIncrement(1);
		scalePower.setMaximum(8);

		btnAvailable = new Button(shell, SWT.CHECK);
		GridData gd_btnAvailable = new GridData(SWT.LEFT, SWT.CENTER, true, true, 3, 1);
		gd_btnAvailable.horizontalIndent = 5;
		btnAvailable.setLayoutData(gd_btnAvailable);
		btnAvailable.setFont(Main.appFont);
		btnAvailable.setToolTipText("When set to true, this system will be available and installed at the beggining of the game.\n"
				+ "When set to false, the system will have to be bought at a store to unlock it.\n"
				+ "Systems that have not been placed on the ship will not be functional, even after buying.");
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

		spLevel.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				scaleLevel.setSelection(spLevel.getSelection());
			}
		});

		spPower.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				scalePower.setSelection(spPower.getSelection());
				if (!Main.ship.isPlayer) {
					scaleLevel.setMaximum(scalePower.getSelection());
					spLevel.setMaximum(scalePower.getSelection());
				}
			}
		});

		btnCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.setVisible(false);
			}
		});

		btnOk.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int newId = spId.getSelection();
				int oldId = currentRoom.id;
				if (newId != oldId) {
					FTLRoom other = Main.ship.getRoomWithId(newId);
					
					Main.ship.rooms.remove(currentRoom);
					if (other != null) {
						Main.ship.rooms.remove(other);
						other.id = oldId;
					}
					currentRoom.id = newId;
					
					Main.ship.rooms.add(currentRoom);
					if (other != null)
						Main.ship.rooms.add(other);
				}
				
				if (!currentRoom.getSystem().equals(Systems.EMPTY)) {
					if (Main.ship.isPlayer) {
						Main.ship.levelMap.put(sys, Integer.valueOf(spLevel.getText()));
					} else {
						Main.ship.levelMap.put(sys, Integer.valueOf(spPower.getText()));
						int i = Integer.valueOf(spLevel.getText());
						Main.ship.powerMap.put(sys, ((i <= Integer.valueOf(spPower.getText()) ? i : Integer.valueOf(spPower.getText()))));
					}
	
					Main.ship.startMap.put(sys, btnAvailable.getSelection());
					Main.systemsMap.get(sys).setAvailable(btnAvailable.getSelection());

					currentRoom.updateColor();

					Main.canvasRedraw(currentRoom.getBounds(), true);
				}

				shell.setVisible(false);
			}
		});

		scaleLevel.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				spLevel.setSelection(scaleLevel.getSelection());
			}
		});

		scalePower.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				spPower.setSelection(scalePower.getSelection());
				if (!Main.ship.isPlayer) {
					scaleLevel.setMaximum(scalePower.getSelection());
					spLevel.setMaximum(scalePower.getSelection());
				}
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

	public void open() {
		if (Main.selectedRoom == null)
			return;
		
		currentRoom = Main.selectedRoom;

		scalePower.setEnabled(false);
		scaleLevel.setEnabled(false);
		spPower.setEnabled(false);
		spLevel.setEnabled(false);
		btnOk.setEnabled(true);
		// btnAvailable.setEnabled(false);
		
		spId.setSelection(currentRoom.id);

		String s = currentRoom.getSystem().toString().toLowerCase();
		s = s.substring(0, 1).toUpperCase() + s.substring(1, s.length());
		shell.setText(s);

		int level = 0;
		int power = 0;

		sys = currentRoom.getSystem();
		if (Main.ship.isPlayer) {
			level = Main.ship.levelMap.get(sys);
		} else {
			// inverted, so that in the System Properties window the first slider sets the minimum value, and not the maximum value
			// more intuitive that way
			level = Main.ship.powerMap.get(sys);
			power = Main.ship.levelMap.get(sys);
		}

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

		if (Main.ship.isPlayer) {
			scaleLevel.setMaximum(max);
			scaleLevel.setSelection(level);
			spLevel.setMaximum(scaleLevel.getMaximum());
			spLevel.setSelection(((level < 1) ? 1 : level));
		} else {
			scalePower.setMaximum(max);
			scalePower.setSelection(power);
			spPower.setMaximum(scalePower.getMaximum());
			spPower.setSelection(((power < 1) ? 1 : power));
			scaleLevel.setMaximum((power < 1) ? 1 : power);
			scaleLevel.setSelection(level);
			spLevel.setMaximum(scaleLevel.getMaximum());
			spLevel.setSelection(level);
		}

		if (sys.equals(Systems.EMPTY)) {
			btnAvailable.setSelection(true);
			btnAvailable.setEnabled(false);
		} else {
			btnAvailable.setSelection(Main.systemsMap.get(sys).isAvailable());
			btnAvailable.setEnabled(true);
		}

		if (!sys.equals(Systems.EMPTY) && !sys.equals(Systems.PILOT) && !sys.equals(Systems.DOORS) && !sys.equals(Systems.SENSORS)) {
			scalePower.setEnabled(true);
			spPower.setEnabled(true);
		}

		if (max > 0) {
			scaleLevel.setEnabled(true);
			spLevel.setEnabled(true);
			btnOk.setEnabled(true);
		}

		if (Main.ship.isPlayer) {
			scalePower.setEnabled(false);
			spPower.setEnabled(false);
			lblLevel.setText("Starting level");
			lblLevel.setToolTipText("The level of the system at the beggining of the game.");
			lblPower.setText("Power");
			lblPower.setToolTipText(null);
			btnAvailable.setToolTipText("When unchecked, the system will not be initially available to the player," + ShipIO.lineDelimiter + "and will have to be bought to be made available.");
			lblPower.setVisible(false);
			spPower.setVisible(false);
			scalePower.setVisible(false);
			btnAvailable.setText("Available At Start");
		} else {
			scalePower.setEnabled(true);
			spPower.setEnabled(true);
			lblLevel.setText("Minimum level");
			lblLevel.setToolTipText("Lower bound for the system's level. FTL's difficulty system picks a value" + ShipIO.lineDelimiter + "between the lower and higher bounds you specify here.");
			lblPower.setText("Maximum level");
			lblPower.setToolTipText("Higher bound for the system's level. FTL's difficulty system picks a value" + ShipIO.lineDelimiter + "between the lower and higher bounds you specify here.");
			btnAvailable.setToolTipText("When unchecked, the system will only sometimes be available to the ship." + ShipIO.lineDelimiter + "Whether it appears or not is decided by FTL's difficulty system.");
			lblPower.setVisible(true);
			spPower.setVisible(true);
			scalePower.setVisible(true);
			btnAvailable.setText("Always Available");
		}

		shell.setLocation(Main.shell.getLocation().x + 100, Main.shell.getLocation().y + 100);
		shell.open();

		Display display = shell.getParent().getDisplay();
		while (!shell.isVisible()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
