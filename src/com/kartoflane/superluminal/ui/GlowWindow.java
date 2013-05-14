package com.kartoflane.superluminal.ui;

import java.io.File;
import java.nio.file.Paths;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.core.ShipIO;
import com.kartoflane.superluminal.elements.Systems;

import org.eclipse.swt.widgets.Composite;

public class GlowWindow {
	protected Shell shell;
	private Text glowText1;
	private Text glowText2;
	private Text glowText3;
	private Button btnBrowse1;
	private Button btnBrowse2;
	private Button btnBrowse3;
	private Button btnClose;
	private Composite composite;
	
	public GlowWindow(Shell parent) {
		shell = new Shell(parent, SWT.BORDER | SWT.TITLE);
		shell.setLayout(new GridLayout(3, false));
		
		shell.setText(Main.APPNAME + " - Glow Images Browser");
		
		createContents();
		
		shell.pack();
	}
	
	public void open() {
		Main.shell.setEnabled(false);

		if (Main.selectedRoom != null && Main.selectedRoom.sysBox != null) {
			if (Main.selectedRoom.interiorData.glowPath1 != null) {
				glowText1.setText(Main.selectedRoom.interiorData.glowPath1);
			} else {
				glowText1.setText("");
			}
			if (Main.selectedRoom.interiorData.glowPath2 != null) {
				glowText2.setText(Main.selectedRoom.interiorData.glowPath2);
			} else {
				glowText2.setText("");
			}
			if (Main.selectedRoom.interiorData.glowPath3 != null) {
				glowText3.setText(Main.selectedRoom.interiorData.glowPath3);
			} else {
				glowText3.setText("");
			}

			btnBrowse1.setEnabled(true);
			btnBrowse2.setEnabled(!Main.selectedRoom.sysBox.getSystemName().equals(Systems.CLOAKING));
			btnBrowse3.setEnabled(!Main.selectedRoom.sysBox.getSystemName().equals(Systems.CLOAKING));
		}
		
		shell.open();
		
	}
	
	private void createContents() {
		Label lblGlow1 = new Label(shell, SWT.NONE);
		lblGlow1.setFont(Main.appFont);
		lblGlow1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblGlow1.setText("Glow 1: ");
		
		glowText1 = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
		glowText1.setFont(Main.appFont);
		GridData gd_glowText1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_glowText1.widthHint = 300;
		glowText1.setLayoutData(gd_glowText1);
		
		btnBrowse1 = new Button(shell, SWT.NONE);
		btnBrowse1.setFont(Main.appFont);
		GridData gd_btnBrowse1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnBrowse1.widthHint = 80;
		btnBrowse1.setLayoutData(gd_btnBrowse1);
		btnBrowse1.setText("Browse...");
		
		Label lblGlow2 = new Label(shell, SWT.NONE);
		lblGlow2.setFont(Main.appFont);
		lblGlow2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblGlow2.setText("Glow 2: ");
		
		glowText2 = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
		glowText2.setFont(Main.appFont);
		GridData gd_glowText2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_glowText2.widthHint = 300;
		glowText2.setLayoutData(gd_glowText2);
		
		btnBrowse2 = new Button(shell, SWT.NONE);
		btnBrowse2.setFont(Main.appFont);
		GridData gd_btnBrowse2 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnBrowse2.widthHint = 80;
		btnBrowse2.setLayoutData(gd_btnBrowse2);
		btnBrowse2.setText("Browse...");
		
		Label lblGlow3 = new Label(shell, SWT.NONE);
		lblGlow3.setFont(Main.appFont);
		lblGlow3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblGlow3.setText("Glow 3: ");
		
		glowText3 = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
		glowText3.setFont(Main.appFont);
		GridData gd_glowText3 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_glowText3.widthHint = 300;
		glowText3.setLayoutData(gd_glowText3);
		
		btnBrowse3 = new Button(shell, SWT.NONE);
		btnBrowse3.setFont(Main.appFont);
		GridData gd_btnBrowse3 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnBrowse3.widthHint = 80;
		btnBrowse3.setLayoutData(gd_btnBrowse3);
		btnBrowse3.setText("Browse...");
		
		new Label(shell, SWT.NONE);
		
		composite = new Composite(shell, SWT.NONE);
		GridLayout gl_composite = new GridLayout(3, false);
		gl_composite.marginHeight = 0;
		gl_composite.marginWidth = 0;
		gl_composite.verticalSpacing = 0;
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		
		final Button btnDel1 = new Button(composite, SWT.NONE);
		btnDel1.setFont(Main.appFont);
		btnDel1.setText("Clear Glow #1");
		
		final Button btnDel2 = new Button(composite, SWT.NONE);
		btnDel2.setFont(Main.appFont);
		btnDel2.setText("Clear Glow #2");
		
		final Button btnDel3 = new Button(composite, SWT.NONE);
		btnDel3.setFont(Main.appFont);
		btnDel3.setText("Clear Glow #3");
		
		composite.pack();
		
		btnClose = new Button(shell, SWT.NONE);
		btnClose.setFont(Main.appFont);
		GridData gd_btnClose = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_btnClose.widthHint = 80;
		btnClose.setLayoutData(gd_btnClose);
		btnClose.setText("Close");
		
		// === LISTENERS
		
		btnClose.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.setVisible(false);
				Main.shell.setEnabled(true);
			}
		});
		
		SelectionAdapter sadapter = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (Main.selectedRoom!=null) {
					if (e.widget == btnDel1) {
						Main.selectedRoom.sysBox.setGlowImage(null, 1);
						glowText1.setText("");
					}
					if (e.widget == btnDel2) {
						Main.selectedRoom.sysBox.setGlowImage(null, 2);
						glowText2.setText("");
					}
					if (e.widget == btnDel3) {
						Main.selectedRoom.sysBox.setGlowImage(null, 3);
						glowText3.setText("");
					}
				}
			}
		};

		btnDel1.addSelectionListener(sadapter);
		btnDel2.addSelectionListener(sadapter);
		btnDel3.addSelectionListener(sadapter);

		SelectionAdapter badapter = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (Main.selectedRoom!=null) {
					FileDialog dialog = new FileDialog(shell, SWT.OPEN);
					String[] filterExtensions = new String[] {"*.png"};
					dialog.setFilterExtensions(filterExtensions);
					dialog.setFilterPath(Main.interiorPath);
					dialog.setFileName(Main.interiorPath);
					
					String path = null;
					
					if (e.widget == btnBrowse1) {
						dialog.setFilterPath(Main.selectedRoom.sysBox.interiorData.glowPath1);
						dialog.setFileName(Main.selectedRoom.sysBox.interiorData.glowPath1);
					} else if (e.widget == btnBrowse2) {
						dialog.setFilterPath(Main.selectedRoom.sysBox.interiorData.glowPath2);
						dialog.setFileName(Main.selectedRoom.sysBox.interiorData.glowPath2);
					} else if (e.widget == btnBrowse3) {
						dialog.setFilterPath(Main.selectedRoom.sysBox.interiorData.glowPath3);
						dialog.setFileName(Main.selectedRoom.sysBox.interiorData.glowPath3);
					}
					
					path = dialog.open();
					
					if (path != null && new File(path).exists()) {
						Main.interiorPath = path;
						if (e.widget==btnBrowse1) {
							Main.selectedRoom.sysBox.setGlowImage(path, 1);
							glowText1.setText(Main.selectedRoom.sysBox.interiorData.glowPath1);
						} else if (e.widget==btnBrowse2) {
							Main.selectedRoom.sysBox.setGlowImage(path, 2);
							glowText2.setText(Main.selectedRoom.sysBox.interiorData.glowPath2);
						} else if (e.widget==btnBrowse3) {
							Main.selectedRoom.sysBox.setGlowImage(path, 3);
							glowText3.setText(Main.selectedRoom.sysBox.interiorData.glowPath3);
						}
					} else {
						MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
						box.setMessage(""+Paths.get(path).getFileName() + ShipIO.lineDelimiter + "File was not found." + ShipIO.lineDelimiter + "Check the file's name and try again.");
						box.open();
					}
				}
			}
		};

		btnBrowse1.addSelectionListener(badapter);
		btnBrowse2.addSelectionListener(badapter);
		btnBrowse3.addSelectionListener(badapter);
	}
}
