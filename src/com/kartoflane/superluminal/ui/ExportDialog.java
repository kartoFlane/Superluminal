package com.kartoflane.superluminal.ui;

import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.layout.GridData;

import com.kartoflane.superluminal.core.ConfigIO;
import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.core.ShipIO;


public class ExportDialog extends Dialog {
	public Shell shell;
	private Text newName;
	
	public String blueprintName;
	private Text exportDir;
	private Button btnAdd;
	private Button btnReplace;
	private Combo replaceCombo;

	public ExportDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM);
		setText(Main.APPNAME + " - Export");
		createContents();
	}

	public void open() {
		Main.shell.setEnabled(false);
		
		shell.open();
		shell.layout();
		
		newName.setVisible(btnAdd.getSelection());
		replaceCombo.setVisible(btnReplace.getSelection());

		if (!ShipIO.isNull(Main.dataPath) && (ShipIO.playerBlueprintNames == null || ShipIO.playerBlueprintNames.size() == 0)) {
			ShipIO.loadDeclarationsFromFile(null);
		}
		
		SortedSet<String> ts = new TreeSet<String>(); 
		for (String s : ShipIO.playerBlueprintNames) {
			s = s + " (" + ShipIO.playerShipNames.get(s) + ")";
			ts.add(s);
		}
		final String[] player = ts.toArray(new String[0]);

		ts.clear();
		for (String s : ShipIO.enemyBlueprintNames) {
			s = s + " (" + ShipIO.enemyShipNames.get(s) + ")";
			ts.add(s);
		}
		
		final String[] enemy = ts.toArray(new String[0]);
		
		if (Main.ship.isPlayer) {
			replaceCombo.setItems(player);
		} else {
			replaceCombo.setItems(enemy);
		}
		if (!ShipIO.isNull(Main.exportPath))
			exportDir.setText(Main.exportPath);
		
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void createContents() {
		shell = new Shell(getParent(), SWT.BORDER | SWT.TITLE);
		shell.setSize(270, 260);
		shell.setText(getText());
		shell.setLocation(Main.shell.getLocation().x+100, Main.shell.getLocation().y+50);
		shell.setLayout(new GridLayout(2, false));
		
		Group grpSaving = new Group(shell, SWT.NONE);
		grpSaving.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		grpSaving.setText("Saving");
		grpSaving.setFont(Main.appFont);
		grpSaving.setLayout(new GridLayout(3, false));
		
		btnAdd = new Button(grpSaving, SWT.RADIO);
		btnAdd.setFont(Main.appFont);
		GridData gd_btnAdd = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd_btnAdd.minimumWidth = 90;
		btnAdd.setLayoutData(gd_btnAdd);
		btnAdd.setToolTipText("");
		btnAdd.setEnabled(!Main.ship.isPlayer);
		btnAdd.setText("Add new ship");
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		
		btnReplace = new Button(grpSaving, SWT.RADIO);
		btnReplace.setFont(Main.appFont);
		GridData gd_btnReplace = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_btnReplace.minimumWidth = 100;
		btnReplace.setLayoutData(gd_btnReplace);
		btnReplace.setSelection(true);
		btnReplace.setText("Replace existing");
		
		Composite composite_3 = new Composite(shell, SWT.NONE);
		composite_3.setLayout(new GridLayout(2, false));
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		final Button btnExport = new Button(composite_3, SWT.NONE);
		GridData gd_btnExport = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnExport.minimumWidth = 80;
		btnExport.setLayoutData(gd_btnExport);
		btnExport.setFont(Main.appFont);
		btnExport.setEnabled(false);
		btnExport.setText("Export");
		
		Button btnCancel = new Button(composite_3, SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnCancel.widthHint = 80;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setFont(Main.appFont);
		btnCancel.setText("Cancel");
		
		Label lblBlueprintNameid = new Label(composite, SWT.NONE);
		lblBlueprintNameid.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblBlueprintNameid.setFont(Main.appFont);
		lblBlueprintNameid.setText("Blueprint name (ID):");
		lblBlueprintNameid.setToolTipText("Blueprint name (the ID of your ship, not visible in-game)"
				+ShipIO.lineDelimiter+"This name will be used to identify your ship in the autoBlueprints.xml and blueprints.xml files");
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 2));
		
		replaceCombo = new Combo(composite_1, SWT.READ_ONLY);
		replaceCombo.setSize(244, 23);
		replaceCombo.setFont(Main.appFont);
		replaceCombo.setText("Select a ship to be replaced...");
		replaceCombo.setVisible(false);
		
		newName = new Text(composite_1, SWT.BORDER);
		newName.setSize(244, 23);
		newName.setTextLimit(32);
		newName.setFont(Main.appFont);
		newName.setVisible(false);
		
		Label lblSaveDirectory = new Label(composite, SWT.NONE);
		lblSaveDirectory.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblSaveDirectory.setFont(Main.appFont);
		lblSaveDirectory.setText("Save directory:");
		
		exportDir = new Text(composite, SWT.BORDER);
		exportDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		exportDir.setFont(Main.appFont);
		exportDir.setToolTipText("Export directory");
		
		Button btnBrowse = new Button(composite, SWT.NONE);
		btnBrowse.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		btnBrowse.setFont(Main.appFont);
		btnBrowse.setText("Browse");
		
		Composite composite_2 = new Composite(composite, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		GridLayout gl_composite_2 = new GridLayout(2, false);
		gl_composite_2.marginBottom = -5;
		composite_2.setLayout(gl_composite_2);
		
		final Button btnDontCheck = new Button(composite_2, SWT.CHECK);
		btnDontCheck.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		btnDontCheck.setFont(Main.appFont);
		btnDontCheck.setToolTipText("When checked, overriden shield and cloak images will be exported." + ShipIO.lineDelimiter
									+"Overrides are images within the default archives, that ships can" + ShipIO.lineDelimiter
									+"reuse, so there's no need to export them.");
		btnDontCheck.setText("Include overridden (default) images");
		btnDontCheck.setSelection(ShipIO.dontCheck);
		
		final Button btnCreateFtl = new Button(composite_2, SWT.CHECK);
		btnCreateFtl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		btnCreateFtl.setFont(Main.appFont);
		btnCreateFtl.setToolTipText("When checked, the ship is exported to an .ftl archive.");
		btnCreateFtl.setText("Create .ftl file");
		btnCreateFtl.setSelection(ShipIO.createFtl);
		
		final Button btnDeleteTemp = new Button(composite_2, SWT.CHECK);
		btnDeleteTemp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		btnDeleteTemp.setFont(Main.appFont);
		btnDeleteTemp.setToolTipText("When checked, temporary unpacked files are deleted after the .ftl file is created.");
		btnDeleteTemp.setEnabled(false);
		btnDeleteTemp.setText("Delete temp folder");
		btnDeleteTemp.setSelection(ShipIO.deleteTemp);
		btnDeleteTemp.setEnabled(ShipIO.createFtl);

		btnCreateFtl.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ShipIO.createFtl = btnCreateFtl.getSelection();
				btnDeleteTemp.setEnabled(ShipIO.createFtl);
				if (!btnDeleteTemp.getEnabled()) {
					btnDeleteTemp.setSelection(false);
					ShipIO.deleteTemp = false;
				}
			}
		});
		
		btnDeleteTemp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ShipIO.deleteTemp = btnDeleteTemp.getSelection();
			}
		});
		
		
		newName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				btnExport.setEnabled(!ShipIO.isNull(newName.getText()) && !ShipIO.isNull(Main.exportPath));
			}
		});
		
		newName.addListener(SWT.Verify, new Listener() {
			public void handleEvent(Event e) {
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
				for (int i = 0; i < chars.length; i++) {
					if (!('0' <= chars[i] && chars[i] <= '9') && !('a' <= chars[i] && chars[i] <= 'z') && !('A' <= chars[i] && chars[i] <= 'Z') && !('_' == chars[i])) {
						e.doit = false;
						return;
					}
				}
				e.text = e.text.toUpperCase();
			}
		});
		
		replaceCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String[] s = replaceCombo.getItem(replaceCombo.getSelectionIndex()).split(" ");

				blueprintName = s[0];
				btnExport.setEnabled(replaceCombo.getSelectionIndex() != -1 && !ShipIO.isNull(Main.exportPath));
			}
		});
		
		exportDir.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				Main.exportPath = exportDir.getText();
			}
		});
		
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(shell);
				dialog.setFilterPath(Main.exportPath);
				
				Main.exportPath = dialog.open();
				
				if (!ShipIO.isNull(Main.exportPath)) {
					exportDir.setText(Main.exportPath);
					ConfigIO.saveConfig();
					btnExport.setEnabled(!ShipIO.isNull(newName.getText()) || replaceCombo.getSelectionIndex() != -1);
				}
			}
		});
		
		btnDontCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ShipIO.dontCheck = btnDontCheck.getSelection();
			}
		});
		
		shell.pack();
		
		btnReplace.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				replaceCombo.setVisible(true);
				newName.setVisible(false);

				btnExport.setEnabled(replaceCombo.getSelectionIndex() != -1 && !ShipIO.isNull(Main.exportPath));
			}
		});
		
		btnAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				replaceCombo.setVisible(false);
				newName.setVisible(true);
				if (!ShipIO.isNull(newName.getText())) {
					btnExport.setEnabled(true);
				} else {
					btnExport.setEnabled(false);
				}
			}
		});
		btnCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Main.shell.setEnabled(true);
				shell.dispose();
			}
		});
		
		btnExport.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Main.shell.setEnabled(true);
				
				if (btnAdd.getSelection() && !ShipIO.isNull(newName.getText())) {
					Main.ship.blueprintName = newName.getText();
				} else {
					Main.ship.blueprintName = blueprintName;
				}
				ShipIO.export(Main.exportPath, Main.ship.layout);
				
				if (ShipIO.errors.size() == 0) {
					Main.print("Successfully exported " + Main.ship.shipClass + " as " + Main.ship.blueprintName + ".");
				} else {
					Main.print("Errors occured during ship exporting.");
					if (Main.debug) {
						Main.erDialog.printErrors(ShipIO.errors);
						ShipIO.errors.clear();
					}
				}
				shell.dispose();
			}
		});
	}
}
