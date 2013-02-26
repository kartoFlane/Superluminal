package com.kartoflane.superluminal.ui;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kartoflane.superluminal.core.ConfigIO;
import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.core.ShipIO;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

import com.kurosaru.ftl.archive.datLib;

public class DirectoriesWindow {
	public static Shell shell;
	FileDialog dialog;
	private Text textData;
	private Text textResources;
	
	private String data = "";
	private String resources = "";
	
	private datLib dataLib;
	private datLib resLib;
	public Label label;
	public Button btnClose;
	
	private String dialog_path = null;

	public DirectoriesWindow(Shell parent) {
		shell = new Shell(parent, SWT.BORDER | SWT.TITLE);
		shell.setLayout(new GridLayout(3, false));
		shell.setText(Main.APPNAME + " - Archives Setup");
		
		createContents();
		
		if (!ShipIO.isNull(Main.dataPath))
			textData.setText(Main.dataPath);
		if (!ShipIO.isNull(Main.resPath))
			textResources.setText(Main.resPath);

		btnClose.setEnabled(false);
		
		shell.pack();
	}

	public void open() {
		textData.setText(data);
		textResources.setText(resources);
		shell.open();
	}
	
	public void createContents() {
		Label lblData = new Label(shell, SWT.NONE);
		lblData.setText("Data:");
		lblData.setFont(Main.appFont);
		
		textData = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
		textData.setFont(Main.appFont);
		GridData gd_textData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_textData.widthHint = 250;
		textData.setLayoutData(gd_textData);
		
		Button btnData = new Button(shell, SWT.NONE);
		btnData.setFont(Main.appFont);
		GridData gd_btnData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnData.minimumWidth = 70;
		btnData.setLayoutData(gd_btnData);
		btnData.setText("Browse...");
		
		Label lblResources = new Label(shell, SWT.NONE);
		lblResources.setFont(Main.appFont);
		lblResources.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblResources.setText("Resources:");
		
		textResources = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
		textResources.setFont(Main.appFont);
		GridData gd_textResources = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_textResources.widthHint = 250;
		textResources.setLayoutData(gd_textResources);
		
		Button btnResources = new Button(shell, SWT.NONE);
		btnResources.setFont(Main.appFont);
		GridData gd_btnResources = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnResources.minimumWidth = 70;
		btnResources.setLayoutData(gd_btnResources);
		btnResources.setText("Browse...");
		
		Label separator = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		label = new Label(shell, SWT.WRAP);
		label.setText("Navigate" + ShipIO.lineDelimiter + ShipIO.lineDelimiter + ShipIO.lineDelimiter + ShipIO.lineDelimiter);
		label.setFont(Main.appFont);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		btnClose = new Button(shell, SWT.NONE);
		btnClose.setFont(Main.appFont);
		btnClose.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, false, 1, 1));
		btnClose.setText("Close");
		
		dialog = new FileDialog(Main.shell, SWT.OPEN);
		String[] filterExtensions = new String[] {"*.dat"};
		dialog.setFilterExtensions(filterExtensions);
		dialog.setFilterPath(dialog_path);
		
		btnData.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dialog.setFilterPath(dialog_path);
				String s = dialog.open();
				if (!ShipIO.isNull(s)) {
					data = s;
					dialog_path = new String(s);
					textData.setText(data);
					
					if (!data.contains("data.dat")) {
						label.setText("Selected file is not valid data archive; please select the correct data.dat file.");
					} else if (!ShipIO.isNull(resources)) {
						File f = new File("archives");
						if (f.exists()) {
							ShipIO.deleteFolderContents(f);
							ShipIO.rmdir(f);
						}

						label.setText("Unpacking... This might take a moment. Please wait.");
						
						dataLib = new datLib(data);
						dataLib.Extract("", "archives");
						resLib = new datLib(resources);
						resLib.Extract("img", "archives" + ShipIO.pathDelimiter + "resources");

						Main.dataPath = f.getAbsolutePath() + ShipIO.pathDelimiter + "data";
						Main.resPath = f.getAbsolutePath() + ShipIO.pathDelimiter + "resources";
						
						ShipBrowser.clearTrees();
						ShipIO.reloadBlueprints();
						ShipBrowser.tree.setEnabled(true);
						ConfigIO.saveConfig();
						
						label.setText("Loaded.");
						
						shell.setVisible(false);
						Main.shell.setEnabled(true);
						Main.shell.setActive();
					}
				}
			}
		});
		
		btnResources.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dialog.setFilterPath(dialog_path);
				String s = dialog.open();
				if (!ShipIO.isNull(s)) {
					resources = s;
					dialog_path = new String(s);
					textResources.setText(resources);

					if (!resources.contains("resource.dat")) {
						label.setText("Selected file is not valid resource archive; please select the correct resource.dat file.");
					} else if (!ShipIO.isNull(data)) {
						File f = new File("archives");
						if (f.exists()) {
							ShipIO.deleteFolderContents(f);
							ShipIO.rmdir(f);
						}
						
						label.setText("Unpacking... This might take a moment. Please wait.");
						
						dataLib = new datLib(data);
						dataLib.Extract("", "archives");
						resLib = new datLib(resources);
						resLib.Extract("img", "archives" + ShipIO.pathDelimiter + "resources");

						Main.dataPath = f.getAbsolutePath() + ShipIO.pathDelimiter + "data";
						Main.resPath = f.getAbsolutePath() + ShipIO.pathDelimiter + "resources";
						
						ShipBrowser.clearTrees();
						ShipIO.reloadBlueprints();
						ShipBrowser.tree.setEnabled(true);
						ConfigIO.saveConfig();
						
						label.setText("Loaded.");
						
						shell.setVisible(false);
						Main.shell.setEnabled(true);
						Main.shell.setActive();
					}
				}
			}
		});

		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.setVisible(false);
				Main.shell.setEnabled(true);
				Main.shell.setActive();
			}
		});
	}
}
