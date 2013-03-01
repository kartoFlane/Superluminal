package com.kartoflane.superluminal.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import com.kartoflane.superluminal.core.ConfigIO;
import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.core.ShipIO;
import com.kartoflane.superluminal.elements.FTLShip;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;


public class ShipBrowser {
	public static Shell shell;
	public boolean abort;
	DirectoryDialog dialog;
	
	// === GUI ELEMENTS' VARIABLES
	Button btnConfirm;
	static Tree tree;
	public static TreeItem trtmPlayer;
	public static TreeItem trtmEnemy;
	public static TreeItem trtmOther;
	
	static HashSet<FTLShip> loadedShips;
	//static HashSet<FTLWeapon> loadedWeapons;
	
	public static HashSet<TreeItem> ships;
	static String selectedShip;

	public ShipBrowser(Shell sh) {
		shell = new Shell(sh, SWT.BORDER | SWT.TITLE);
		dialog = new DirectoryDialog(Main.shell, SWT.OPEN);
		ships = new HashSet<TreeItem>();
		
		createContents();
		
		Main.shell.setEnabled(false);
		shell.setLocation(Main.shell.getLocation().x+100, Main.shell.getLocation().y+50);
		tree.setEnabled(false);
		
		if (!isNull(Main.dataPath) && !isNull(Main.resPath)) {
			tree.setEnabled(true);
			
			ShipIO.loadTree();
		}
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		shell.setText(Main.APPNAME + " - Ship Browser");
		shell.setFont(Main.appFont);
		shell.setSize(400, 400);
		shell.setLayout(new GridLayout(2, false));
		
		tree = new Tree(shell, SWT.BORDER);
		tree.setFont(Main.appFont);
		GridData gd_tree = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2);
		gd_tree.widthHint = 320;
		gd_tree.heightHint = 250;
		tree.setLayoutData(gd_tree);
		
		trtmPlayer = new TreeItem(tree, SWT.NONE);
		trtmPlayer.setText("Player ships");
		
		trtmPlayer.setExpanded(false);
		
		trtmEnemy = new TreeItem(tree, SWT.NONE);
		trtmEnemy.setText("Enemy ships");
		
		trtmOther = new TreeItem(tree, SWT.NONE);
		trtmOther.setText("Other");
		trtmOther.setExpanded(true);
		
		Composite composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		
		btnConfirm = new Button(composite_1, SWT.NONE);
		GridData gd_btnConfirm = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnConfirm.minimumWidth = 80;
		btnConfirm.setLayoutData(gd_btnConfirm);
		btnConfirm.setFont(Main.appFont);
		btnConfirm.setText("Load");
		btnConfirm.setEnabled(false);
		
		Button btnCancel = new Button(composite_1, SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnCancel.widthHint = 80;
		gd_btnCancel.minimumWidth = 80;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setFont(Main.appFont);
		btnCancel.setText("Cancel");
		
		shell.pack();
		
	//=====================================
	// === BOOKMARK LISTENERS
		
		shell.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event event)
			{
				Main.shell.setEnabled(true);
				shell.dispose();
			}
		});

		tree.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				if (btnConfirm.getEnabled())
					btnConfirm.notifyListeners(SWT.Selection, null);
			}
		});
		
		tree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				String s;
				TreeItem[] selection = tree.getSelection();
				
				for (int i = 0; i < selection.length; i++) {
					if (ships.contains(selection[i])) {
						btnConfirm.setEnabled(true);
						
						s = selection[i].getText();
						s = s.substring(s.indexOf("(")+1, s.indexOf(")"));

						selectedShip = s;
					} else {
						btnConfirm.setEnabled(false);
					}
				}
			}
		});

		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Main.shell.setEnabled(true);
				shell.dispose();
			}
		});
		
		btnConfirm.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Main.shell.setEnabled(true);

				File unpacked_blueprints = null;
				unpacked_blueprints = new File("archives" + ShipIO.pathDelimiter + "data" + ShipIO.pathDelimiter + "autoBlueprints.xml");
				ArrayList<String> blueList = new ArrayList<String>();
				ArrayList<String> tempList = null;
				
				tempList = (ArrayList<String>) ShipIO.preScan(unpacked_blueprints, selectedShip);
				
				if (tempList != null) blueList.addAll(tempList);
				
				if (blueList == null || blueList.size()==0) {
					unpacked_blueprints = new File("archives" + ShipIO.pathDelimiter + "data" + ShipIO.pathDelimiter + "blueprints.xml");
					blueList = (ArrayList<String>) ShipIO.preScan(unpacked_blueprints, selectedShip);
					
					if (tempList != null) blueList.addAll(tempList);
				}
				
				if (blueList.size()!=0) {
					if (blueList.size() == 1) {
						Main.mntmClose.notifyListeners(SWT.Selection, null);
						ShipIO.loadShip(blueList.get(0), unpacked_blueprints, -1);
					} else {
						Main.choiceDialog.setChoices(blueList, unpacked_blueprints);
						String blueprint = Main.choiceDialog.open();

						Main.mntmClose.notifyListeners(SWT.Selection, null);
						ShipIO.loadShip(blueprint, unpacked_blueprints, Main.choiceDialog.declaration);
					}
				} else {
					Main.erDialog.print("Error: load ship - no ship declarations found. No data was loaded. This shouldn't ever happen.");
				}
				//ShipIO.loadShip(selectedShip, null, -1);
				
				ConfigIO.saveConfig();
				shell.dispose();
			}
		});
	}
	

	// AUXILIARY
	public static boolean isNull(String path) {
		return path == null || (path != null && (path.equals("") || path.equals("null")));
	}
	
	public static void clearTrees() {
		for (TreeItem trtm : trtmPlayer.getItems()) {
			if (!trtm.isDisposed())
				trtm.dispose();
		}
		trtmPlayer.clearAll(true);
		for (TreeItem trtm : trtmEnemy.getItems()) {
			if (!trtm.isDisposed())
				trtm.dispose();
		}
		trtmEnemy.clearAll(true);
		for (TreeItem trtm : trtmOther.getItems()) {
			if (!trtm.isDisposed())
				trtm.dispose();
		}
		trtmOther.clearAll(true);
	}
}