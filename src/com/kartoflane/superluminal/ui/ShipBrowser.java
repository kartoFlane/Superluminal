package com.kartoflane.superluminal.ui;

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
import org.eclipse.swt.widgets.Text;

import com.kartoflane.superluminal.core.ConfigIO;
import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.core.ShipIO;
import com.kartoflane.superluminal.elements.FTLShip;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;


public class ShipBrowser
{
	public static Shell shell;
	public boolean abort;
	DirectoryDialog dialog;
	
	private Text dataDir;
	private Text resDir;
	
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

	public ShipBrowser(Shell sh)
	{
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
		if (!isNull(Main.dataPath)) {
			dataDir.setText(Main.dataPath);
		}
		if (!isNull(Main.resPath)) {
			resDir.setText(Main.resPath);
		}
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents()
	{
		shell.setText(Main.APPNAME + " - Ship Browser");
		shell.setFont(Main.appFont);
		shell.setSize(400, 400);
		shell.setLayout(new GridLayout(2, false));
		
		Composite composite_2 = new Composite(shell, SWT.NONE);
		composite_2.setLayout(new GridLayout(2, false));
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		
		dataDir = new Text(composite_2, SWT.BORDER);
		dataDir.setFont(Main.appFont);
		GridData gd_dataDir = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
		gd_dataDir.minimumWidth = 290;
		dataDir.setLayoutData(gd_dataDir);
		dataDir.setText("Data-unpacked directory");
		
		Button btnData = new Button(composite_2, SWT.NONE);
		btnData.setFont(Main.appFont);
		GridData gd_btnData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_btnData.minimumWidth = 70;
		btnData.setLayoutData(gd_btnData);
		btnData.setText("Browse...");
		
		resDir = new Text(composite_2, SWT.BORDER);
		resDir.setFont(Main.appFont);
		GridData gd_resDir = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
		gd_resDir.minimumWidth = 290;
		resDir.setLayoutData(gd_resDir);
		resDir.setText("Resources-unpacked directory");
		
		Button btnRes = new Button(composite_2, SWT.NONE);
		btnRes.setFont(Main.appFont);
		GridData gd_btnRes = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_btnRes.minimumWidth = 70;
		btnRes.setLayoutData(gd_btnRes);
		btnRes.setText("Browse...");
		
		tree = new Tree(shell, SWT.BORDER);
		tree.setFont(Main.appFont);
		GridData gd_tree = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2);
		gd_tree.heightHint = 200;
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
		
		btnData.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String s = dialog.open();
				if (!isNull(s)) {
					Main.dataPath = s;
					dataDir.setText(Main.dataPath);
					
					// check if the other path is set too
					if (!isNull(Main.resPath)) {
						clearTrees();
						ShipIO.reloadBlueprints();
						tree.setEnabled(true);
						ConfigIO.saveConfig();
					}
				}
			}
		});
		
		btnRes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String s = dialog.open();
				if (!isNull(s)) {
					Main.resPath = s;
					resDir.setText(Main.resPath);
					
					// check if the other path is set too
					if (!isNull(Main.dataPath)) {
						ShipIO.reloadBlueprints();
						tree.setEnabled(true);
						ConfigIO.saveConfig();
					}
				}
			}
		});
		
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

				Main.mntmClose.notifyListeners(SWT.Selection, null);
				
				ShipIO.loadShip(selectedShip, true);

				//Main.ship.updateReactor();
				
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
			trtm.dispose();
		}
		trtmPlayer.clearAll(true);
		for (TreeItem trtm : trtmEnemy.getItems()) {
			trtm.dispose();
		}
		trtmEnemy.clearAll(true);
		for (TreeItem trtm : trtmOther.getItems()) {
			trtm.dispose();
		}
		trtmOther.clearAll(true);
	}
}