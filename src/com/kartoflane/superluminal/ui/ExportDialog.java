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
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
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


public class ExportDialog extends Dialog
{
	public Shell shell;
	private Text newName;
	
	public String blueprintName;
	private Text exportDir;

	public ExportDialog(Shell parent)
	{
		super(parent, SWT.DIALOG_TRIM);
		setText(Main.APPNAME + " - Export");
		createContents();
	}

	public void open()
	{
		Main.shell.setEnabled(false);
		
		shell.open();
		shell.layout();
		
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void createContents()
	{
		shell = new Shell(getParent(), SWT.BORDER | SWT.TITLE);
		shell.setSize(270, 260);
		shell.setText(getText());
		shell.setLayout(new FormLayout());
		shell.setLocation(Main.shell.getLocation().x+100, Main.shell.getLocation().y+50);
		
		Group grpSaving = new Group(shell, SWT.NONE);
		grpSaving.setText("Saving");
		grpSaving.setFont(Main.appFont);
		grpSaving.setLayout(new GridLayout(4, false));
		FormData fd_grpSaving = new FormData();
		fd_grpSaving.top = new FormAttachment(0, 10);
		fd_grpSaving.right = new FormAttachment(100, -11);
		fd_grpSaving.left = new FormAttachment(0, 10);
		grpSaving.setLayoutData(fd_grpSaving);
		
		final Button btnAdd = new Button(grpSaving, SWT.RADIO);
		btnAdd.setFont(Main.appFont);
		GridData gd_btnAdd = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gd_btnAdd.minimumWidth = 90;
		gd_btnAdd.widthHint = 90;
		btnAdd.setLayoutData(gd_btnAdd);
		btnAdd.setToolTipText("");
		btnAdd.setEnabled(!Main.ship.isPlayer);
		btnAdd.setText("Add new ship");
		
		Composite composite = new Composite(shell, SWT.NONE);
		fd_grpSaving.bottom = new FormAttachment(composite, -6);
		new Label(grpSaving, SWT.NONE);
		
		final Button btnReplace = new Button(grpSaving, SWT.RADIO);
		btnReplace.setFont(Main.appFont);
		GridData gd_btnReplace = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnReplace.minimumWidth = 100;
		gd_btnReplace.widthHint = 100;
		btnReplace.setLayoutData(gd_btnReplace);
		btnReplace.setSelection(true);
		btnReplace.setText("Replace existing");
		
		composite.setLayout(new FormLayout());
		FormData fd_composite = new FormData();
		fd_composite.top = new FormAttachment(0, 59);
		fd_composite.left = new FormAttachment(grpSaving, 0, SWT.LEFT);
		fd_composite.right = new FormAttachment(100, -10);
		composite.setLayoutData(fd_composite);
		
		Button btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setFont(Main.appFont);
		fd_composite.bottom = new FormAttachment(100, -36);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.top = new FormAttachment(composite, 1);
		fd_btnCancel.width = 80;
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");
		
		final Button btnExport = new Button(shell, SWT.NONE);
		btnExport.setFont(Main.appFont);
		fd_btnCancel.left = new FormAttachment(0, 173);
		btnExport.setEnabled(false);
		FormData fd_btnExport = new FormData();
		fd_btnExport.top = new FormAttachment(btnCancel, 0, SWT.TOP);
		fd_btnExport.right = new FormAttachment(btnCancel, -6);
		fd_btnExport.width = 80;
		btnExport.setLayoutData(fd_btnExport);
		btnExport.setText("Export");
		
		final Combo replaceCombo = new Combo(composite, SWT.READ_ONLY);
		replaceCombo.setFont(Main.appFont);
		FormData fd_replaceCombo = new FormData();
		fd_replaceCombo.right = new FormAttachment(0, 244);
		fd_replaceCombo.top = new FormAttachment(0, 21);
		fd_replaceCombo.left = new FormAttachment(0);
		replaceCombo.setLayoutData(fd_replaceCombo);
		replaceCombo.setText("Select a ship to be replaced...");
		
		if (!ShipIO.isNull(Main.dataPath) && (ShipIO.playerBlueprintNames == null || ShipIO.playerBlueprintNames.size() == 0)) {
			ShipIO.loadDeclarationsSilent();
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
		
		newName = new Text(composite, SWT.BORDER);
		newName.setTextLimit(32);
		newName.setFont(Main.appFont);
		FormData fd_newName = new FormData();
		fd_newName.bottom = new FormAttachment(0, 44);
		fd_newName.right = new FormAttachment(0, 244);
		fd_newName.top = new FormAttachment(0, 21);
		fd_newName.left = new FormAttachment(0);
		newName.setLayoutData(fd_newName);
		
		Label lblBlueprintNameid = new Label(composite, SWT.NONE);
		lblBlueprintNameid.setFont(Main.appFont);
		FormData fd_lblBlueprintNameid = new FormData();
		fd_lblBlueprintNameid.right = new FormAttachment(0, 244);
		fd_lblBlueprintNameid.top = new FormAttachment(0);
		fd_lblBlueprintNameid.left = new FormAttachment(0);
		lblBlueprintNameid.setLayoutData(fd_lblBlueprintNameid);
		lblBlueprintNameid.setText("Blueprint name (ID):");
		lblBlueprintNameid.setToolTipText("Blueprint name (the ID of your ship, not visible in-game)"
				+ShipIO.lineDelimiter+"This name will be used to identify your ship in the autoBlueprints.xml and blueprints.xml files");
		
		exportDir = new Text(composite, SWT.BORDER);
		exportDir.setFont(Main.appFont);
		exportDir.setToolTipText("Export directory");
		FormData fd_exportDir = new FormData();
		fd_exportDir.left = new FormAttachment(0);
		exportDir.setLayoutData(fd_exportDir);
		if (!ShipIO.isNull(Main.exportPath))
			exportDir.setText(Main.exportPath);
		
		Button btnBrowse = new Button(composite, SWT.NONE);
		btnBrowse.setFont(Main.appFont);
		fd_exportDir.right = new FormAttachment(btnBrowse, -5);
		FormData fd_btnBrowse = new FormData();
		fd_btnBrowse.top = new FormAttachment(exportDir, -2, SWT.TOP);
		fd_btnBrowse.right = new FormAttachment(replaceCombo, 0, SWT.RIGHT);
		btnBrowse.setLayoutData(fd_btnBrowse);
		btnBrowse.setText("Browse");
		
		Label lblSaveDirectory = new Label(composite, SWT.NONE);
		lblSaveDirectory.setFont(Main.appFont);
		fd_exportDir.top = new FormAttachment(lblSaveDirectory);
		FormData fd_lblSaveDirectory = new FormData();
		fd_lblSaveDirectory.top = new FormAttachment(replaceCombo, 6);
		fd_lblSaveDirectory.left = new FormAttachment(replaceCombo, 0, SWT.LEFT);
		lblSaveDirectory.setLayoutData(fd_lblSaveDirectory);
		lblSaveDirectory.setText("Save directory:");
		
		final Button btnCreateFtl = new Button(composite, SWT.CHECK);
		btnCreateFtl.setFont(Main.appFont);
		btnCreateFtl.setToolTipText("When checked, the export function also"+ShipIO.lineDelimiter
								   +"packs the ship to an .ftl archive.");
		FormData fd_btnCreateFtl = new FormData();
		fd_btnCreateFtl.top = new FormAttachment(82);
		fd_btnCreateFtl.bottom = new FormAttachment(100, -10);
		fd_btnCreateFtl.left = new FormAttachment(0, 10);
		btnCreateFtl.setLayoutData(fd_btnCreateFtl);
		btnCreateFtl.setText("Create .ftl file");
		btnCreateFtl.setSelection(ShipIO.createFtl);
		
		final Button btnDeleteTemp = new Button(composite, SWT.CHECK);
		btnDeleteTemp.setFont(Main.appFont);
		btnDeleteTemp.setToolTipText("When checked, the export function deletes the"+ShipIO.lineDelimiter
									 +"temporary files after the .ftl file is created.");
		btnDeleteTemp.setEnabled(false);
		FormData fd_btnDeleteTemp = new FormData();
		fd_btnDeleteTemp.top = new FormAttachment(btnCreateFtl, 0, SWT.TOP);
		fd_btnDeleteTemp.right = new FormAttachment(100, -10);
		btnDeleteTemp.setLayoutData(fd_btnDeleteTemp);
		btnDeleteTemp.setText("Delete temp folder");
		btnDeleteTemp.setSelection(ShipIO.deleteTemp);
		btnDeleteTemp.setEnabled(ShipIO.createFtl);
		
		final Button btnDontCheck = new Button(composite, SWT.CHECK);
		btnDontCheck.setFont(Main.appFont);
		btnDontCheck.setToolTipText("When checked, even the default images will be exported.");
		FormData fd_btnDontCheck = new FormData();
		fd_btnDontCheck.top = new FormAttachment(70);
		fd_btnDontCheck.right = new FormAttachment(btnDeleteTemp, 0, SWT.RIGHT);
		fd_btnDontCheck.left = new FormAttachment(exportDir, 10, SWT.LEFT);
		fd_btnDontCheck.bottom = new FormAttachment(btnCreateFtl, -3);
		btnDontCheck.setLayoutData(fd_btnDontCheck);
		btnDontCheck.setText("Don't check if files are already present");
		btnDontCheck.setSelection(ShipIO.dontCheck);
		
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
		
		btnDontCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ShipIO.dontCheck = btnDontCheck.getSelection();
			}
		});
		
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
		
		replaceCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String[] s = replaceCombo.getItem(replaceCombo.getSelectionIndex()).split(" ");

				blueprintName = s[0];
				btnExport.setEnabled(replaceCombo.getSelectionIndex() != -1 && !ShipIO.isNull(Main.exportPath));
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
		
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(shell);
				Main.exportPath = dialog.open();
				
				if (!ShipIO.isNull(Main.exportPath)) {
					exportDir.setText(Main.exportPath);
					ConfigIO.saveConfig();
					btnExport.setEnabled(!ShipIO.isNull(newName.getText()) || replaceCombo.getSelectionIndex() != -1);
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
					Main.print("Successfully exported " + Main.ship.shipClass + " as " + blueprintName + ".");
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
