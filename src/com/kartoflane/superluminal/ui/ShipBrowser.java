package com.kartoflane.superluminal.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.core.ShipIO;
import com.kartoflane.superluminal.elements.FTLShip;

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

	public static HashSet<TreeItem> ships;
	static String selectedShip;
	private Group sortGroup;
	private Button btnSortClass;
	private Button btnSortBlueprint;
	
	public static boolean sortByBlueprint = true;

	public ShipBrowser(Shell sh) {
		shell = new Shell(sh, SWT.BORDER | SWT.TITLE);
		dialog = new DirectoryDialog(Main.shell, SWT.OPEN);
		ships = new HashSet<TreeItem>();

		createContents();

		Main.shell.setEnabled(false);
		shell.setLocation(Main.shell.getLocation().x + 100, Main.shell.getLocation().y + 50);
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

		sortGroup = new Group(shell, SWT.NONE);
		sortGroup.setText("Sort by:");
		sortGroup.setLayout(new GridLayout(2, false));
		sortGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 2, 1));

		btnSortBlueprint = new Button(sortGroup, SWT.RADIO);
		btnSortBlueprint.setSelection(true);
		btnSortBlueprint.setText("Blueprint name");

		btnSortClass = new Button(sortGroup, SWT.RADIO);
		btnSortClass.setText("Ship class name");

		tree = new Tree(shell, SWT.BORDER);
		tree.setFont(Main.appFont);
		GridData gd_tree = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2);
		gd_tree.widthHint = 320;
		gd_tree.heightHint = 250;
		tree.setLayoutData(gd_tree);

		trtmPlayer = new TreeItem(tree, SWT.NONE);
		trtmPlayer.setText("Player ships");
		trtmPlayer.setFont(Main.appFont);
		trtmPlayer.setExpanded(false);

		trtmEnemy = new TreeItem(tree, SWT.NONE);
		trtmEnemy.setText("Enemy ships");
		trtmEnemy.setFont(Main.appFont);

		trtmOther = new TreeItem(tree, SWT.NONE);
		trtmOther.setText("Other");
		trtmOther.setFont(Main.appFont);

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
		btnCancel.setText("Close");

		shell.pack();

		// =====================================
		// === BOOKMARK LISTENERS

		shell.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Main.shell.setEnabled(true);
				shell.dispose();
			}
		});
		
		btnSortClass.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sortByBlueprint = false;
				ShipIO.loadTree();
			}
		});
		
		btnSortBlueprint.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sortByBlueprint = true;
				ShipIO.loadTree();
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
						s = s.substring(s.indexOf("(") + 1, s.indexOf(")"));

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
				File unpacked_blueprints = null;
				unpacked_blueprints = new File("archives" + ShipIO.pathDelimiter + "data" + ShipIO.pathDelimiter + "autoBlueprints.xml");
				ArrayList<String> blueList = new ArrayList<String>();
				ArrayList<String> tempList = null;

				tempList = (ArrayList<String>) ShipIO.preScan(unpacked_blueprints, selectedShip);

				if (tempList != null)
					blueList.addAll(tempList);

				if (blueList == null || blueList.size() == 0) {
					unpacked_blueprints = new File("archives" + ShipIO.pathDelimiter + "data" + ShipIO.pathDelimiter + "blueprints.xml");
					blueList = (ArrayList<String>) ShipIO.preScan(unpacked_blueprints, selectedShip);

					if (tempList != null)
						blueList.addAll(tempList);
				}

				if (blueList.size() != 0) {
					if (blueList.size() == 1) {
						Main.mntmClose.notifyListeners(SWT.Selection, null);
						ShipIO.loadShip(blueList.get(0), unpacked_blueprints, -1);
					} else {
						shell.setEnabled(false);
						Main.choiceDialog.setChoices(blueList, unpacked_blueprints);
						Rectangle bounds = shell.getBounds();
						Main.choiceDialog.shell.setLocation(bounds.x + bounds.width / 2 - Main.choiceDialog.shell.getBounds().width / 2,
								bounds.y + bounds.height / 3 - Main.choiceDialog.shell.getBounds().height / 2);
						String blueprint = Main.choiceDialog.open();

						shell.setEnabled(true);
						if (blueprint != null) {
							Main.mntmClose.notifyListeners(SWT.Selection, null);
							ShipIO.loadShip(blueprint, unpacked_blueprints, Main.choiceDialog.declaration);
						}
					}
				} else {
					Main.erDialog.print("Error: load ship - no ship declarations found. No data was loaded. This shouldn't ever happen.");
				}
			}
		});
	}

	// AUXILIARY
	public static boolean isNull(String path) {
		return path == null || (path != null && (path.equals("") || path.equals("null")));
	}

	public static void clearTrees() {
		if (!trtmPlayer.isDisposed()) {
			for (TreeItem trtm : trtmPlayer.getItems())
				if (trtm != null && !trtm.isDisposed())
					trtm.dispose();
			trtmPlayer.clearAll(true);
		}

		if (!trtmEnemy.isDisposed()) {
			for (TreeItem trtm : trtmEnemy.getItems())
				if (trtm != null && !trtm.isDisposed())
					trtm.dispose();
			trtmEnemy.clearAll(true);
		}

		if (!trtmOther.isDisposed()) {
			for (TreeItem trtm : trtmOther.getItems())
				if (trtm != null && !trtm.isDisposed())
					trtm.dispose();
			trtmOther.clearAll(true);
		}
	}
}