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
import org.eclipse.swt.widgets.Text;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.elements.Systems;


public class PropertiesWindow
{

	protected Shell shell;
	private Text textLevel;
	private Text textPower;
	private int max;
	private Systems sys;
	private Scale scaleLevel;
	private Scale scalePower;
	private Button btnOk;
	private Button btnAvailable;

	public PropertiesWindow(Shell parent)
	{
		shell = new Shell(parent, SWT.BORDER | SWT.TITLE);
		shell.setSize(186, 230);
		shell.setLocation(parent.getLocation().x+100, parent.getLocation().y+100);
		
		Label lblLevel_1 = new Label(shell, SWT.NONE);
		lblLevel_1.setBounds(10, 10, 47, 15);
		lblLevel_1.setText("Level:");
		
		scaleLevel = new Scale(shell, SWT.NONE);
		scaleLevel.setPageIncrement(1);
		scaleLevel.setMinimum(1);
		
		
		scaleLevel.setMaximum(8);
		scaleLevel.setBounds(10, 31, 130, 42);
		
		textLevel = new Text(shell, SWT.BORDER | SWT.CENTER);
		textLevel.setBounds(142, 40, 28, 21);
		textLevel.setTextLimit(1);
		
		Label lblPower = new Label(shell, SWT.NONE);
		lblPower.setBounds(10, 79, 75, 15);
		lblPower.setText("Power");
		
		scalePower = new Scale(shell, SWT.NONE);
		scalePower.setPageIncrement(1);
		scalePower.setMaximum(8);
		scalePower.setBounds(10, 100, 130, 42);
		
		textPower = new Text(shell, SWT.BORDER | SWT.CENTER);
		textPower.setBounds(142, 109, 28, 21);
		textPower.setTextLimit(1);
		
		btnOk = new Button(shell, SWT.NONE);
		btnOk.setBounds(10, 167, 75, 25);
		btnOk.setText("OK");
		
		Button btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setText("Cancel");
		btnCancel.setBounds(95, 167, 75, 25);
		
		btnAvailable = new Button(shell, SWT.CHECK);
		btnAvailable.setToolTipText("When set to true, this system will be available to the player since the beggining of the game.\n"
											+"When set to false, the player will have to buy the system at a store to unlock it.\n"
											+"The player won't be able to buy systems that have not been placed on the ship.");
		btnAvailable.setBounds(10, 148, 160, 16);
		btnAvailable.setText("Available At Start");
		
		
		textLevel.addListener(SWT.Verify, new Listener() {
			public void handleEvent(Event e) {
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
				for (int i = 0; i < chars.length; i++) {
					if (!('0' <= chars[i] && chars[i] <= '9')) {
						e.doit = false;
						return;
					} else {
						scaleLevel.setSelection(Integer.valueOf(e.text));
						scalePower.setMaximum(Integer.valueOf(e.text));
					}
				}
			}
		});
		
		textPower.addListener(SWT.Verify, new Listener() {
			public void handleEvent(Event e) {
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
				for (int i = 0; i < chars.length; i++) {
					if (!('0' <= chars[i] && chars[i] <= '9')) {
						e.doit = false;
						return;
					} else {
						scalePower.setSelection(Integer.valueOf(e.text));
					}
				}
			}
		});
		
		btnOk.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Main.ship.levelMap.put(sys, Integer.valueOf(textLevel.getText()));
				int i = Integer.valueOf(textPower.getText());
				Main.ship.powerMap.put(sys, ((i<=Integer.valueOf(textLevel.getText()) ? i : Integer.valueOf(textLevel.getText()))));

				Main.ship.startMap.put(sys, btnAvailable.getSelection());
				
				shell.setVisible(false);
			}
		});
		
		btnCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.setVisible(false);
			}
		});
		
		scaleLevel.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				textLevel.setText(""+scaleLevel.getSelection());
			}
		});
		scalePower.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				textPower.setText(""+scalePower.getSelection());
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
		btnAvailable.setEnabled(false);
		
		shell.open();
		
		String s = Main.selectedRoom.sys.toString().toLowerCase();
		s = s.substring(0, 1).toUpperCase() + s.substring(1, s.length());
		shell.setText(s);
		shell.setLocation(Main.shell.getLocation().x+100, Main.shell.getLocation().y+100);

		int level = 0;
		int power = 0;
		if (Main.selectedRoom != null) {
			sys = Main.selectedRoom.sys;
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
		}

		shell.setImage(Main.systemsMap.get(sys));
		
		textLevel.setText(""+((level<1)?1:level));
		textPower.setText(""+power);
		scaleLevel.setMaximum(max);
		scalePower.setMaximum((level<1)?1:level);
		scaleLevel.setSelection(level);
		scalePower.setSelection(power);
		if (!sys.equals(Systems.EMPTY) && Main.ship.isPlayer) {
			btnAvailable.setSelection(Main.ship.startMap.get(sys));
			btnAvailable.setEnabled(true);
		} else {
			btnAvailable.setSelection(false);
			btnAvailable.setSelection(true);
		}

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
