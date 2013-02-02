package com.kartoflane.superluminal.ui;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.core.ShipIO;
import com.kartoflane.superluminal.elements.FTLItem;
import com.kartoflane.superluminal.elements.Systems;


public class ShipPropertiesWindow extends Dialog
{

	protected Shell shell;
	private TabItem tbtmShipInfo;
	private Text textName;
	private Text textClass;
	private Text textLayout;
	private Text textImage;
	private Text textDesc;
	private Spinner spHealth;
	private Spinner spReactor;
	private Spinner spMax;
	private Spinner spHuman;
	private Spinner spEngi;
	private Spinner spZoltan;
	private Spinner spMantis;
	private Spinner spSlug;
	private Spinner spRock;
	private Spinner spCrystal;
	
	private Spinner spMaxWeapons;
	private Spinner spMaxDrones;
	private List listWeapons;
	private List listDrones;
	private Combo weapons;
	private Combo drones;
	private Spinner spSlots;
	private Spinner spSlotsDr;
	private Button btnUseSet;
	private Button btnUseSetDr;
	private Combo presets;
	private Combo presetsDr;
	private Button btnExplicit;
	private Button btnExplicitDr;
	private Combo categories;
	private Combo categoriesDr;
	
	private Label lblPilotInfo;
	private Label lblDoorsInfo;
	private Label lblShieldsInfo;
	private Label lblSensorsInfo;
	private Label lblEnginesInfo;
	private Label lblMedbayInfo;
	private Label lblDronesInfo;
	private Label lblOxygenInfo;
	private Label lblTeleInfo;
	private Label lblCloakInfo;
	private Label lblArtilleryInfo;
	private Label lblWeaponsInfo;
	private Label lblReqPower;
	private Label lblReqStart;
	private List listAugments;
	private Spinner spMissiles;
	private Spinner spDrones;
	private Spinner spGhost;

	public ShipPropertiesWindow(Shell parent)
	{
		super(parent, SWT.DIALOG_TRIM);
		setText(Main.APPNAME + " - Ship Properties");
		createContents();
	}

	public void open()
	{
		shell.open();
		shell.layout();
		shell.setLocation(Main.shell.getLocation().x+100, Main.shell.getLocation().y+50);
		
	// === Update information
	// ship info
		if (!ShipIO.isNull(Main.ship.shipName))
			textName.setText(Main.ship.shipName);
		if (!ShipIO.isNull(Main.ship.layout))
			textLayout.setText(Main.ship.layout);
		if (!ShipIO.isNull(Main.ship.shipClass))
			textClass.setText(Main.ship.shipClass);
		if (!ShipIO.isNull(Main.ship.imageName))
			textImage.setText(Main.ship.imageName);
		if (!ShipIO.isNull(Main.ship.descr))
			textDesc.setText(Main.ship.descr);
		/*
		if (!ShipIO.isNull(Main.ship.cloakOverride))
			textCloakO.setText(Main.ship.cloakOverride);
		if (!ShipIO.isNull(Main.ship.shieldOverride))
			textShieldO.setText(Main.ship.shieldOverride);
		 */
		
		textName.setEnabled(Main.ship.isPlayer);
		if (!ShipIO.isNull(Main.ship.shipName))
			textName.setText(Main.ship.shipName);
		if (!ShipIO.isNull(Main.ship.layout))
			textLayout.setText(Main.ship.layout);
		if (!ShipIO.isNull(Main.ship.shipClass))
			textClass.setText(Main.ship.shipClass);
		if (!ShipIO.isNull(Main.ship.imageName))
			textImage.setText(Main.ship.imageName);
		textDesc.setEnabled(Main.ship.isPlayer);
		if (!ShipIO.isNull(Main.ship.descr))
			textDesc.setText(Main.ship.descr);

		/*
		if (!ShipIO.isNull(Main.ship.cloakOverride))
			textCloakO.setText(Main.ship.cloakOverride);
		if (!ShipIO.isNull(Main.ship.shieldOverride))
			textShieldO.setText(Main.ship.shieldOverride);
		*/
		
	// power & health
		
		lblPilotInfo.setText(Main.ship.powerMap.get(Systems.PILOT) + " / " + Main.ship.levelMap.get(Systems.PILOT));
		lblWeaponsInfo.setText(Main.ship.powerMap.get(Systems.WEAPONS) + " / " + Main.ship.levelMap.get(Systems.WEAPONS));
		lblDoorsInfo.setText(Main.ship.powerMap.get(Systems.DOORS) + " / " + Main.ship.levelMap.get(Systems.DOORS));
		lblShieldsInfo.setText(Main.ship.powerMap.get(Systems.SHIELDS) + " / " + Main.ship.levelMap.get(Systems.SHIELDS));
		lblSensorsInfo.setText(Main.ship.powerMap.get(Systems.SENSORS) + " / " + Main.ship.levelMap.get(Systems.SENSORS));
		lblEnginesInfo.setText(Main.ship.powerMap.get(Systems.ENGINES) + " / " + Main.ship.levelMap.get(Systems.ENGINES));
		lblMedbayInfo.setText(Main.ship.powerMap.get(Systems.MEDBAY) + " / " + Main.ship.levelMap.get(Systems.MEDBAY));
		lblDronesInfo.setText(Main.ship.powerMap.get(Systems.DRONES) + " / " + Main.ship.levelMap.get(Systems.DRONES));
		lblOxygenInfo.setText(Main.ship.powerMap.get(Systems.OXYGEN) + " / " + Main.ship.levelMap.get(Systems.OXYGEN));
		lblTeleInfo.setText(Main.ship.powerMap.get(Systems.TELEPORTER) + " / " + Main.ship.levelMap.get(Systems.TELEPORTER));
		lblCloakInfo.setText(Main.ship.powerMap.get(Systems.CLOAKING) + " / " + Main.ship.levelMap.get(Systems.CLOAKING));
		lblArtilleryInfo.setText(Main.ship.powerMap.get(Systems.ARTILLERY) + " / " + Main.ship.levelMap.get(Systems.ARTILLERY));
		
		int i = 0,j = 0;
		for (Systems s : Main.ship.levelMap.keySet()) {
			if (Main.isSystemAssigned(s) && !s.equals(Systems.EMPTY) && !s.equals(Systems.PILOT) && !s.equals(Systems.DOORS) && !s.equals(Systems.SENSORS))
				i += Main.ship.levelMap.get(s);
			if (Main.isSystemAssigned(s) && Main.ship.startMap.get(s) && !s.equals(Systems.EMPTY) && !s.equals(Systems.PILOT) && !s.equals(Systems.DOORS) && !s.equals(Systems.SENSORS))
				j += Main.ship.levelMap.get(s);
		}
		
		lblReqPower.setText(String.valueOf(i));
		lblReqStart.setText(String.valueOf(j));
		
		spReactor.setSelection(Main.ship.reactorPower);
		spHealth.setSelection(Main.ship.hullHealth);
		
	// crew
		spMax.setSelection(Main.ship.crewMax);
		
		spHuman.setMaximum(Main.ship.crewMax);
		spEngi.setMaximum(Main.ship.crewMax);
		spZoltan.setMaximum(Main.ship.crewMax);
		spMantis.setMaximum(Main.ship.crewMax);
		spSlug.setMaximum(Main.ship.crewMax);
		spRock.setMaximum(Main.ship.crewMax);
		spCrystal.setMaximum(Main.ship.crewMax);
		spGhost.setMaximum(Main.ship.crewMax);
		
		//spMax.setEnabled(!Main.ship.isPlayer);
		spMax.setIncrement((Main.ship.isPlayer ? 8 : 1));
		spHuman.setSelection(Main.ship.crewMap.get("human"));
		spEngi.setSelection(Main.ship.crewMap.get("engi"));
		spZoltan.setSelection(Main.ship.crewMap.get("zoltan"));
		spMantis.setSelection(Main.ship.crewMap.get("mantis"));
		spSlug.setSelection(Main.ship.crewMap.get("slug"));
		spRock.setSelection(Main.ship.crewMap.get("rock"));
		spCrystal.setSelection(Main.ship.crewMap.get("crystal"));
		spGhost.setSelection(Main.ship.crewMap.get("ghost"));
		
	// weapons
		spMissiles.setSelection(Main.ship.missiles);
		spSlots.setSelection(Main.ship.weaponSlots);
		spMaxWeapons.setMaximum(spSlots.getSelection());
		spMaxWeapons.setSelection(Main.ship.weaponCount);

		btnUseSet.setEnabled(!Main.ship.isPlayer);
		btnUseSet.setSelection(Main.ship.weaponsBySet);
		btnExplicit.setSelection(!Main.ship.weaponsBySet);
		
		listWeapons.removeAll();
		
		if (Main.ship.weaponsBySet) {
			presets.setEnabled(true);
			categories.setEnabled(false);
			weapons.setEnabled(false);
			listWeapons.setEnabled(false);
			
			int sel = -1;
search:		for (String s : presets.getItems()) {
				sel++;
				if (Main.ship.weaponSet.contains(s)) {
					break search;
				}
			}
			
			presets.select(sel);
		} else {
			presets.setEnabled(false);
			categories.setEnabled(true);
			weapons.setEnabled(true);
			listWeapons.setEnabled(true);
			
			FTLItem it;
			for (String blue : Main.ship.weaponSet) {
				if (ShipIO.weaponMap.keySet().contains(blue)) {
					it = ShipIO.weaponMap.get(blue);
					listWeapons.add(it.name + " (" + blue + ")");
				}
			}
		}
		
	// drones
		spDrones.setSelection(Main.ship.drones);
		spSlotsDr.setSelection(Main.ship.droneSlots);
		spMaxDrones.setMaximum(spSlotsDr.getSelection());
		spMaxDrones.setSelection(Main.ship.droneCount);

		btnUseSetDr.setEnabled(!Main.ship.isPlayer);
		btnUseSetDr.setSelection(Main.ship.dronesBySet);
		btnExplicitDr.setSelection(!Main.ship.dronesBySet);
		
		listDrones.removeAll();
		
		if (Main.ship.dronesBySet) {
			presetsDr.setEnabled(true);
			categoriesDr.setEnabled(false);
			drones.setEnabled(false);
			listDrones.setEnabled(false);
			
			int sel = -1;
search:		for (String s : presetsDr.getItems()) {
				sel++;
				if (Main.ship.droneSet.contains(s)) {
					break search;
				}
			}
			
			presetsDr.select(sel);
		} else {
			presetsDr.setEnabled(false);
			categoriesDr.setEnabled(true);
			drones.setEnabled(true);
			listDrones.setEnabled(true);
			
			FTLItem it;
			for (String blue : Main.ship.droneSet) {
				if (ShipIO.droneMap.keySet().contains(blue)) {
					it = ShipIO.droneMap.get(blue);
					listDrones.add(it.name + " (" + blue + ")");
				}
			}
		}
		
	// augments
		listAugments.removeAll();
		FTLItem it = null;
		for (String s : Main.ship.augmentSet) {
			it = ShipIO.getItem(s);
			listAugments.add(it.name + " (" + it.blueprint + ")");
		}
		
		
		Display display = getParent().getDisplay();
		while (shell.isVisible()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents()
	{
		shell = new Shell(getParent(), SWT.BORDER | SWT.TITLE);
		shell.setSize(450, 305);
		shell.setText(getText());
		shell.setLayout(new GridLayout(1, false));
		shell.setFont(Main.appFont);
		
		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setFont(Main.appFont);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		
	// === Properties -> Ship Information
		tbtmShipInfo = new TabItem(tabFolder, SWT.NONE);
		tbtmShipInfo.setText("Ship Information");
		
		Composite shipInfoC = new Composite(tabFolder, SWT.NONE);
		tbtmShipInfo.setControl(shipInfoC);
		shipInfoC.setLayout(new GridLayout(5, false));
		
		Label lblName = new Label(shipInfoC, SWT.NONE);
		lblName.setFont(Main.appFont);
		GridData gd_lblName = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblName.minimumWidth = 40;
		gd_lblName.widthHint = 40;
		lblName.setLayoutData(gd_lblName);
		lblName.setText("Name:");
		
		textName = new Text(shipInfoC, SWT.BORDER);
		textName.setFont(Main.appFont);
		GridData gd_textName = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_textName.widthHint = 167;
		textName.setLayoutData(gd_textName);
		
		Label lblLayout = new Label(shipInfoC, SWT.NONE);
		lblLayout.setFont(Main.appFont);
		lblLayout.setToolTipText("Name of the .txt and .xml files used by the ship.");
		GridData gd_lblLayout = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblLayout.minimumWidth = 40;
		gd_lblLayout.widthHint = 40;
		lblLayout.setLayoutData(gd_lblLayout);
		lblLayout.setText("Layout:");
		
		textLayout = new Text(shipInfoC, SWT.BORDER);
		textLayout.setFont(Main.appFont);
		GridData gd_textLayout = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_textLayout.widthHint = 80;
		textLayout.setLayoutData(gd_textLayout);
		
		Label lblClass = new Label(shipInfoC, SWT.NONE);
		lblClass.setFont(Main.appFont);
		GridData gd_lblClass = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblClass.minimumWidth = 40;
		gd_lblClass.widthHint = 40;
		lblClass.setLayoutData(gd_lblClass);
		lblClass.setText("Class:");
		
		textClass = new Text(shipInfoC, SWT.BORDER);
		textClass.setFont(Main.appFont);
		textClass.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label lblImage = new Label(shipInfoC, SWT.NONE);
		lblImage.setFont(Main.appFont);
		lblImage.setToolTipText("Prefix for images used by this ship, the name under which they'll be exported.\n"
								+"For example, the Kestrel has this field set to \"kestral\" by default, so it will\n"
								+"use kestral_base, kestral_shields1, etc for its graphics.");
		GridData gd_lblImage = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblImage.minimumWidth = 40;
		gd_lblImage.widthHint = 40;
		lblImage.setLayoutData(gd_lblImage);
		lblImage.setText("Image:");
		
		textImage = new Text(shipInfoC, SWT.BORDER);
		textImage.setFont(Main.appFont);
		GridData gd_textImage = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_textImage.widthHint = 80;
		textImage.setLayoutData(gd_textImage);
		
		Label lblDescription = new Label(shipInfoC, SWT.NONE);
		lblDescription.setFont(Main.appFont);
		lblDescription.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblDescription.setText("Description:");
		new Label(shipInfoC, SWT.NONE);
		new Label(shipInfoC, SWT.NONE);
		new Label(shipInfoC, SWT.NONE);
		
		textDesc = new Text(shipInfoC, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		textDesc.setFont(Main.appFont);
		GridData gd_textDesc = new GridData(SWT.FILL, SWT.FILL, false, true, 2, 5);
		gd_textDesc.widthHint = 212;
		textDesc.setLayoutData(gd_textDesc);
		new Label(shipInfoC, SWT.NONE);
		new Label(shipInfoC, SWT.NONE);
		new Label(shipInfoC, SWT.NONE);
		new Label(shipInfoC, SWT.NONE);
		new Label(shipInfoC, SWT.NONE);
		new Label(shipInfoC, SWT.NONE);
		
		Label lblMissiles = new Label(shipInfoC, SWT.NONE);
		lblMissiles.setFont(Main.appFont);
		GridData gd_lblMissiles = new GridData(SWT.LEFT, SWT.CENTER, false, true, 2, 1);
		gd_lblMissiles.widthHint = 70;
		lblMissiles.setLayoutData(gd_lblMissiles);
		lblMissiles.setText("Missiles:");
		
		spMissiles = new Spinner(shipInfoC, SWT.BORDER);
		spMissiles.setFont(Main.appFont);
		spMissiles.setMaximum(99);
		GridData gd_spMissiles = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_spMissiles.minimumWidth = 20;
		gd_spMissiles.widthHint = 20;
		spMissiles.setLayoutData(gd_spMissiles);
		
		Label lblDroneParts = new Label(shipInfoC, SWT.NONE);
		lblDroneParts.setFont(Main.appFont);
		GridData gd_lblDroneParts = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_lblDroneParts.widthHint = 70;
		lblDroneParts.setLayoutData(gd_lblDroneParts);
		lblDroneParts.setText("Drone parts:");
		
		spDrones = new Spinner(shipInfoC, SWT.BORDER);
		spDrones.setMaximum(99);
		spDrones.setFont(Main.appFont);
		GridData gd_spDrones = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_spDrones.minimumWidth = 20;
		gd_spDrones.widthHint = 20;
		spDrones.setLayoutData(gd_spDrones);
		
		Label lblHullHealth = new Label(shipInfoC, SWT.NONE);
		lblHullHealth.setFont(Main.appFont);
		GridData gd_lblHullHealth = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_lblHullHealth.widthHint = 70;
		lblHullHealth.setLayoutData(gd_lblHullHealth);
		lblHullHealth.setText("Hull Health:");
		
		spHealth = new Spinner(shipInfoC, SWT.BORDER);
		spHealth.setMaximum(99);
		spHealth.setFont(Main.appFont);
		GridData gd_spHealth = new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 1);
		gd_spHealth.widthHint = 20;
		gd_spHealth.minimumWidth = 20;
		spHealth.setLayoutData(gd_spHealth);
		
	// === Properties -> Systems Overview
		TabItem tbtmSystemsOverview = new TabItem(tabFolder, SWT.NONE);
		tbtmSystemsOverview.setText("Systems Overview");
		
		Composite systemsC = new Composite(tabFolder, SWT.NONE);
		tbtmSystemsOverview.setControl(systemsC);
		systemsC.setLayout(new GridLayout(2, false));
		
		Group grpSystems = new Group(systemsC, SWT.NONE);
		grpSystems.setFont(Main.appFont);
		grpSystems.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		grpSystems.setText("Systems");
		grpSystems.setLayout(new GridLayout(5, false));
		
			// === Pilot
		Label lblPilot = new Label(grpSystems, SWT.NONE);
		lblPilot.setFont(Main.appFont);
		GridData gd_lblPilot = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblPilot.minimumWidth = 60;
		gd_lblPilot.widthHint = 60;
		lblPilot.setLayoutData(gd_lblPilot);
		lblPilot.setText("Pilot:");
		
		lblPilotInfo = new Label(grpSystems, SWT.RIGHT);
		lblPilotInfo.setFont(Main.appFont);
		GridData gd_lblPilotInfo = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblPilotInfo.widthHint = 36;
		lblPilotInfo.setLayoutData(gd_lblPilotInfo);
		
		Label label_1 = new Label(grpSystems, SWT.SEPARATOR | SWT.VERTICAL);
		label_1.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 8));
		
			// === Weapons
		Label lblWeapons = new Label(grpSystems, SWT.NONE);
		lblWeapons.setFont(Main.appFont);
		GridData gd_lblWeapons = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblWeapons.minimumWidth = 60;
		gd_lblWeapons.widthHint = 60;
		lblWeapons.setLayoutData(gd_lblWeapons);
		lblWeapons.setText("Weapons:");
		
		lblWeaponsInfo = new Label(grpSystems, SWT.RIGHT);
		lblWeaponsInfo.setFont(Main.appFont);
		GridData gd_lblWeaponsInfo = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblWeaponsInfo.widthHint = 36;
		lblWeaponsInfo.setLayoutData(gd_lblWeaponsInfo);
		
			// === Doors
		Label lblDoors = new Label(grpSystems, SWT.NONE);
		lblDoors.setFont(Main.appFont);
		GridData gd_lblDoors = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblDoors.minimumWidth = 60;
		gd_lblDoors.widthHint = 60;
		lblDoors.setLayoutData(gd_lblDoors);
		lblDoors.setText("Doors:");
		
		lblDoorsInfo = new Label(grpSystems, SWT.RIGHT);
		lblDoorsInfo.setFont(Main.appFont);
		GridData gd_lblDoorsInfo = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblDoorsInfo.widthHint = 36;
		lblDoorsInfo.setLayoutData(gd_lblDoorsInfo);
		
			// === Shields
		Label lblShields = new Label(grpSystems, SWT.NONE);
		lblShields.setFont(Main.appFont);
		GridData gd_lblShields = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblShields.minimumWidth = 60;
		gd_lblShields.widthHint = 60;
		lblShields.setLayoutData(gd_lblShields);
		lblShields.setText("Shields:");
		
		lblShieldsInfo = new Label(grpSystems, SWT.RIGHT);
		lblShieldsInfo.setFont(Main.appFont);
		GridData gd_lblShieldsInfo = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblShieldsInfo.widthHint = 36;
		lblShieldsInfo.setLayoutData(gd_lblShieldsInfo);
		
			// === Sensors
		Label lblSensors = new Label(grpSystems, SWT.NONE);
		lblSensors.setFont(Main.appFont);
		GridData gd_lblSensors = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblSensors.minimumWidth = 60;
		gd_lblSensors.widthHint = 60;
		lblSensors.setLayoutData(gd_lblSensors);
		lblSensors.setText("Sensors:");
		
		lblSensorsInfo = new Label(grpSystems, SWT.RIGHT);
		lblSensorsInfo.setFont(Main.appFont);
		GridData gd_lblSensorsInfo = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblSensorsInfo.widthHint = 36;
		lblSensorsInfo.setLayoutData(gd_lblSensorsInfo);
		
			// === Engines
		Label lblEngines = new Label(grpSystems, SWT.NONE);
		lblEngines.setFont(Main.appFont);
		GridData gd_lblEngines = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblEngines.minimumWidth = 60;
		gd_lblEngines.widthHint = 60;
		lblEngines.setLayoutData(gd_lblEngines);
		lblEngines.setText("Engines:");
		
		lblEnginesInfo = new Label(grpSystems, SWT.RIGHT);
		lblEnginesInfo.setFont(Main.appFont);
		GridData gd_lblEnginesInfo = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblEnginesInfo.widthHint = 36;
		lblEnginesInfo.setLayoutData(gd_lblEnginesInfo);
		
		Label label = new Label(grpSystems, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		
			// === Medbay
		Label lblMedbay = new Label(grpSystems, SWT.NONE);
		lblMedbay.setFont(Main.appFont);
		GridData gd_lblMedbay = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblMedbay.minimumWidth = 60;
		gd_lblMedbay.widthHint = 60;
		lblMedbay.setLayoutData(gd_lblMedbay);
		lblMedbay.setText("Medbay:");
		
		lblMedbayInfo = new Label(grpSystems, SWT.SHADOW_IN | SWT.RIGHT);
		lblMedbayInfo.setFont(Main.appFont);
		GridData gd_lblMedbayInfo = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblMedbayInfo.widthHint = 36;
		lblMedbayInfo.setLayoutData(gd_lblMedbayInfo);
		
			// === Drones
		Label lblDrones = new Label(grpSystems, SWT.NONE);
		lblDrones.setFont(Main.appFont);
		GridData gd_lblDrones = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblDrones.minimumWidth = 60;
		gd_lblDrones.widthHint = 60;
		lblDrones.setLayoutData(gd_lblDrones);
		lblDrones.setText("Drones:");
		
		lblDronesInfo = new Label(grpSystems, SWT.RIGHT);
		lblDronesInfo.setFont(Main.appFont);
		GridData gd_lblDronesInfo = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblDronesInfo.widthHint = 36;
		lblDronesInfo.setLayoutData(gd_lblDronesInfo);
		
			// === Oxygen
		Label lblOxygen = new Label(grpSystems, SWT.NONE);
		lblOxygen.setFont(Main.appFont);
		GridData gd_lblOxygen = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblOxygen.minimumWidth = 60;
		gd_lblOxygen.widthHint = 60;
		lblOxygen.setLayoutData(gd_lblOxygen);
		lblOxygen.setText("Oxygen:");
		
		lblOxygenInfo = new Label(grpSystems, SWT.RIGHT);
		lblOxygenInfo.setFont(Main.appFont);
		GridData gd_lblOxygenInfo = new GridData(SWT.RIGHT, SWT.BOTTOM, false, false, 1, 1);
		gd_lblOxygenInfo.widthHint = 36;
		lblOxygenInfo.setLayoutData(gd_lblOxygenInfo);
		
			// === Teleporter
		Label lblTeleporter = new Label(grpSystems, SWT.NONE);
		lblTeleporter.setFont(Main.appFont);
		GridData gd_lblTeleporter = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblTeleporter.minimumWidth = 60;
		gd_lblTeleporter.widthHint = 60;
		lblTeleporter.setLayoutData(gd_lblTeleporter);
		lblTeleporter.setText("Teleporter:");
		
		lblTeleInfo = new Label(grpSystems, SWT.RIGHT);
		lblTeleInfo.setFont(Main.appFont);
		GridData gd_lblTeleInfo = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblTeleInfo.widthHint = 36;
		lblTeleInfo.setLayoutData(gd_lblTeleInfo);
		new Label(grpSystems, SWT.NONE);
		new Label(grpSystems, SWT.NONE);
		
			// === Cloaking
		Label lblCloaking = new Label(grpSystems, SWT.NONE);
		lblCloaking.setFont(Main.appFont);
		GridData gd_lblCloaking = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblCloaking.minimumWidth = 60;
		gd_lblCloaking.widthHint = 60;
		lblCloaking.setLayoutData(gd_lblCloaking);
		lblCloaking.setText("Cloaking:");
		
		lblCloakInfo = new Label(grpSystems, SWT.RIGHT);
		lblCloakInfo.setFont(Main.appFont);
		GridData gd_lblCloakInfo = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblCloakInfo.widthHint = 36;
		lblCloakInfo.setLayoutData(gd_lblCloakInfo);
		new Label(grpSystems, SWT.NONE);
		new Label(grpSystems, SWT.NONE);
		
			// === Artillery
		Label lblArtillery = new Label(grpSystems, SWT.NONE);
		lblArtillery.setFont(Main.appFont);
		GridData gd_lblArtillery = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd_lblArtillery.minimumWidth = 60;
		gd_lblArtillery.widthHint = 60;
		lblArtillery.setLayoutData(gd_lblArtillery);
		lblArtillery.setText("Artillery:");
		
		lblArtilleryInfo = new Label(grpSystems, SWT.RIGHT);
		lblArtilleryInfo.setFont(Main.appFont);
		GridData gd_lblArtilleryInfo = new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1);
		gd_lblArtilleryInfo.widthHint = 36;
		lblArtilleryInfo.setLayoutData(gd_lblArtilleryInfo);
		new Label(grpSystems, SWT.NONE);
		new Label(grpSystems, SWT.NONE);
		
		Group grpReactor = new Group(systemsC, SWT.NONE);
		grpReactor.setFont(Main.appFont);
		grpReactor.setLayout(new GridLayout(3, false));
		grpReactor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpReactor.setText("Reactor");
		
		Label lblReq = new Label(grpReactor, SWT.NONE);
		lblReq.setFont(Main.appFont);
		GridData gd_lblReq = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblReq.widthHint = 120;
		lblReq.setLayoutData(gd_lblReq);
		lblReq.setToolTipText("Power required to run");
		lblReq.setText("Power req. to run...");
		new Label(grpReactor, SWT.NONE);
		new Label(grpReactor, SWT.NONE);
		
		Label lblAllSystems = new Label(grpReactor, SWT.NONE);
		lblAllSystems.setFont(Main.appFont);
		GridData gd_lblAllSystems = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblAllSystems.widthHint = 120;
		lblAllSystems.setLayoutData(gd_lblAllSystems);
		lblAllSystems.setText("     all systems:");

		lblReqPower = new Label(grpReactor, SWT.CENTER);
		lblReqPower.setFont(Main.appFont);
		GridData gd_lblReqPower = new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1);
		gd_lblReqPower.widthHint = 40;
		lblReqPower.setLayoutData(gd_lblReqPower);
		
		Label lblOnlyAvailableSystems = new Label(grpReactor, SWT.NONE);
		lblOnlyAvailableSystems.setFont(Main.appFont);
		GridData gd_lblOnlyAvailableSystems = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblOnlyAvailableSystems.widthHint = 120;
		lblOnlyAvailableSystems.setLayoutData(gd_lblOnlyAvailableSystems);
		lblOnlyAvailableSystems.setText("     available systems:");
		
		lblReqStart = new Label(grpReactor, SWT.CENTER);
		lblReqStart.setAlignment(SWT.CENTER);
		lblReqStart.setFont(Main.appFont);
		GridData gd_lblReqStart = new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1);
		gd_lblReqStart.widthHint = 40;
		lblReqStart.setLayoutData(gd_lblReqStart);
		
		Label lblMaxPossibleThrough = new Label(grpReactor, SWT.NONE);
		lblMaxPossibleThrough.setFont(Main.appFont);
		lblMaxPossibleThrough.setToolTipText("Maximum possible reactor power achievable through in-game upgrading.");
		GridData gd_lblMaxPossibleThrough = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_lblMaxPossibleThrough.widthHint = 120;
		lblMaxPossibleThrough.setLayoutData(gd_lblMaxPossibleThrough);
		lblMaxPossibleThrough.setText("Max. possible by upg.:");
		
		Label lblReqMax = new Label(grpReactor, SWT.CENTER);
		lblReqMax.setFont(Main.appFont);
		GridData gd_lblReqMax = new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1);
		gd_lblReqMax.widthHint = 40;
		lblReqMax.setLayoutData(gd_lblReqMax);
		lblReqMax.setText("25");
		
		Label label_3 = new Label(grpReactor, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		Composite composite_1 = new Composite(grpReactor, SWT.NONE);
		GridLayout gl_composite_1 = new GridLayout(2, false);
		gl_composite_1.verticalSpacing = 0;
		gl_composite_1.marginWidth = 0;
		gl_composite_1.horizontalSpacing = 0;
		gl_composite_1.marginHeight = 0;
		composite_1.setLayout(gl_composite_1);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
			
			// === Reactor
		Label lblReactorPower = new Label(composite_1, SWT.NONE);
		lblReactorPower.setFont(Main.appFont);
		lblReactorPower.setToolTipText("Reactor level at the start of the game.");
		GridData gd_lblReactorPower = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblReactorPower.widthHint = 120;
		lblReactorPower.setLayoutData(gd_lblReactorPower);
		lblReactorPower.setText("Reactor power: ");
		
		spReactor = new Spinner(composite_1, SWT.BORDER);
		spReactor.setFont(Main.appFont);
		GridData gd_spReactor = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_spReactor.minimumWidth = 40;
		gd_spReactor.widthHint = 40;
		spReactor.setLayoutData(gd_spReactor);

	// === Properties -> Crew & Augments
		TabItem tbtmCrew = new TabItem(tabFolder, SWT.NONE);
		tbtmCrew.setText("Crew && Augments ");
		
		Composite crewInfoC = new Composite(tabFolder, SWT.NONE);
		tbtmCrew.setControl(crewInfoC);
		crewInfoC.setLayout(new GridLayout(2, false));
		
		Group grpCrew = new Group(crewInfoC, SWT.NONE);
		grpCrew.setFont(Main.appFont);
		GridLayout gl_grpCrew = new GridLayout(10, false);
		gl_grpCrew.horizontalSpacing = 2;
		grpCrew.setLayout(gl_grpCrew);
		grpCrew.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 8, 1));
		grpCrew.setText("Crew");
		
		Label lblCrewMax = new Label(grpCrew, SWT.NONE);
		lblCrewMax.setFont(Main.appFont);
		GridData gd_lblCrewMax = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblCrewMax.minimumWidth = 60;
		gd_lblCrewMax.widthHint = 60;
		lblCrewMax.setLayoutData(gd_lblCrewMax);
		lblCrewMax.setToolTipText("Only affects enemy ships.");
		lblCrewMax.setText("Crew max:");
		
		spMax = new Spinner(grpCrew, SWT.BORDER);
		GridData gd_spMax = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_spMax.widthHint = 5;
		spMax.setLayoutData(gd_spMax);
		spMax.setFont(Main.appFont);
		spMax.setMaximum(8);
		spMax.setMinimum(0);
		
		Label lblHuman = new Label(grpCrew, SWT.NONE);
		lblHuman.setFont(Main.appFont);
		GridData gd_lblHuman = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblHuman.minimumWidth = 50;
		gd_lblHuman.widthHint = 50;
		lblHuman.setLayoutData(gd_lblHuman);
		lblHuman.setText("Human:");
		
		spHuman = new Spinner(grpCrew, SWT.BORDER);
		GridData gd_spHuman = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_spHuman.widthHint = 5;
		spHuman.setLayoutData(gd_spHuman);
		spHuman.setFont(Main.appFont);
		spHuman.setMaximum(8);
		
		Label lblEngi = new Label(grpCrew, SWT.NONE);
		lblEngi.setFont(Main.appFont);
		GridData gd_lblEngi = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblEngi.minimumWidth = 30;
		gd_lblEngi.widthHint = 30;
		lblEngi.setLayoutData(gd_lblEngi);
		lblEngi.setText("Engi:");
		
		spEngi = new Spinner(grpCrew, SWT.BORDER);
		GridData gd_spEngi = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_spEngi.widthHint = 5;
		spEngi.setLayoutData(gd_spEngi);
		spEngi.setFont(Main.appFont);
		spEngi.setMaximum(8);
		
		Label lblZoltan = new Label(grpCrew, SWT.NONE);
		lblZoltan.setFont(Main.appFont);
		GridData gd_lblZoltan = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblZoltan.minimumWidth = 40;
		gd_lblZoltan.widthHint = 40;
		lblZoltan.setLayoutData(gd_lblZoltan);
		lblZoltan.setText("Zoltan:");
		
		spZoltan = new Spinner(grpCrew, SWT.BORDER);
		GridData gd_spZoltan = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_spZoltan.widthHint = 5;
		spZoltan.setLayoutData(gd_spZoltan);
		spZoltan.setFont(Main.appFont);
		spZoltan.setMaximum(8);
		
		Label lblGhost = new Label(grpCrew, SWT.NONE);
		lblGhost.setFont(Main.appFont);
		lblGhost.setText("Ghost:");
		
		spGhost = new Spinner(grpCrew, SWT.BORDER);
		spGhost.setFont(Main.appFont);
		spGhost.setMaximum(8);
		GridData gd_spGhost = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_spGhost.widthHint = 5;
		spGhost.setLayoutData(gd_spGhost);
		
		Label lblMantis = new Label(grpCrew, SWT.NONE);
		lblMantis.setFont(Main.appFont);
		GridData gd_lblMantis = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblMantis.minimumWidth = 60;
		gd_lblMantis.widthHint = 60;
		lblMantis.setLayoutData(gd_lblMantis);
		lblMantis.setText("Mantis:");
		
		spMantis = new Spinner(grpCrew, SWT.BORDER);
		GridData gd_spMantis = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_spMantis.widthHint = 5;
		spMantis.setLayoutData(gd_spMantis);
		spMantis.setFont(Main.appFont);
		spMantis.setMaximum(8);
		
		Label lblSlug = new Label(grpCrew, SWT.NONE);
		lblSlug.setFont(Main.appFont);
		GridData gd_lblSlug = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblSlug.minimumWidth = 50;
		gd_lblSlug.widthHint = 50;
		lblSlug.setLayoutData(gd_lblSlug);
		lblSlug.setText("Slug:");
		
		spSlug = new Spinner(grpCrew, SWT.BORDER);
		GridData gd_spSlug = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_spSlug.widthHint = 5;
		spSlug.setLayoutData(gd_spSlug);
		spSlug.setFont(Main.appFont);
		spSlug.setMaximum(8);
		
		Label lblRock = new Label(grpCrew, SWT.NONE);
		lblRock.setFont(Main.appFont);
		GridData gd_lblRock = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblRock.minimumWidth = 30;
		gd_lblRock.widthHint = 30;
		lblRock.setLayoutData(gd_lblRock);
		lblRock.setText("Rock:");
		
		spRock = new Spinner(grpCrew, SWT.BORDER);
		GridData gd_spRock = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_spRock.widthHint = 5;
		spRock.setLayoutData(gd_spRock);
		spRock.setFont(Main.appFont);
		spRock.setMaximum(8);
		
		Label lblCrystal = new Label(grpCrew, SWT.NONE);
		lblCrystal.setFont(Main.appFont);
		GridData gd_lblCrystal = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblCrystal.minimumWidth = 40;
		gd_lblCrystal.widthHint = 40;
		lblCrystal.setLayoutData(gd_lblCrystal);
		lblCrystal.setText("Crystal:");
		
		spCrystal = new Spinner(grpCrew, SWT.BORDER);
		GridData gd_spCrystal = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_spCrystal.widthHint = 5;
		spCrystal.setLayoutData(gd_spCrystal);
		spCrystal.setFont(Main.appFont);
		spCrystal.setMaximum(8);
		new Label(grpCrew, SWT.NONE);
		new Label(grpCrew, SWT.NONE);
		
		Group grpAugments = new Group(crewInfoC, SWT.NONE);
		grpAugments.setFont(Main.appFont);
		grpAugments.setLayout(new GridLayout(1, false));
		grpAugments.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		grpAugments.setText("Augments");
		
		listAugments = new List(grpAugments, SWT.BORDER);
		listAugments.setFont(Main.appFont);
		listAugments.setToolTipText("Double-click on an item to delete it.");
		listAugments.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		final Combo augments = new Combo(grpAugments, SWT.READ_ONLY);
		augments.setFont(Main.appFont);
		augments.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
		augments.setBounds(0, 0, 400, 23);
		FTLItem it = null;
		for (String s : ShipIO.augMap.keySet()) {
			it = ShipIO.augMap.get(s);
			augments.add(it.name + " (" + it.blueprint + ")");
		}
		
	// === Weapons
		TabItem tbtmWeapons = new TabItem(tabFolder, SWT.NONE);
		tbtmWeapons.setText("Weapons");
		
		Composite weaponsC = new Composite(tabFolder, SWT.NONE);
		tbtmWeapons.setControl(weaponsC);
		weaponsC.setLayout(new GridLayout(4, false));
		
		Group grpWeapons = new Group(weaponsC, SWT.NONE);
		grpWeapons.setFont(Main.appFont);
		grpWeapons.setText("Define weapons");
		grpWeapons.setLayout(new GridLayout(1, false));
		GridData gd_grpWeapons = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 3);
		gd_grpWeapons.widthHint = 95;
		grpWeapons.setLayoutData(gd_grpWeapons);
		
		btnExplicit = new Button(grpWeapons, SWT.RADIO);
		btnExplicit.setFont(Main.appFont);
		GridData gd_btnExplicit = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnExplicit.minimumWidth = 80;
		gd_btnExplicit.widthHint = 80;
		btnExplicit.setLayoutData(gd_btnExplicit);
		btnExplicit.setText("Explicitly");
		
		btnUseSet = new Button(grpWeapons, SWT.RADIO);
		btnUseSet.setFont(Main.appFont);
		GridData gd_btnUseSet = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnUseSet.minimumWidth = 80;
		gd_btnUseSet.widthHint = 80;
		btnUseSet.setLayoutData(gd_btnUseSet);
		btnUseSet.setText("By preset");
		
		Composite composite_2 = new Composite(weaponsC, SWT.NONE);
		composite_2.setLayout(new GridLayout(2, false));
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 3));
		
		Label lblWeaponSlots = new Label(composite_2, SWT.NONE);
		lblWeaponSlots.setFont(Main.appFont);
		GridData gd_lblWeaponSlots = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblWeaponSlots.minimumWidth = 85;
		gd_lblWeaponSlots.widthHint = 85;
		lblWeaponSlots.setLayoutData(gd_lblWeaponSlots);
		lblWeaponSlots.setToolTipText("Defines the number of weapon slots available to the player.");
		lblWeaponSlots.setText("Weapon Slots:");
		
		spSlots = new Spinner(composite_2, SWT.BORDER);
		spSlots.setFont(Main.appFont);
		GridData gd_spSlots = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_spSlots.widthHint = 10;
		spSlots.setLayoutData(gd_spSlots);
		spSlots.setMaximum(4);
		
		Label lblMaxWeapons = new Label(composite_2, SWT.NONE);
		lblMaxWeapons.setFont(Main.appFont);
		GridData gd_lblMaxWeapons = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblMaxWeapons.minimumWidth = 85;
		gd_lblMaxWeapons.widthHint = 85;
		lblMaxWeapons.setLayoutData(gd_lblMaxWeapons);
		lblMaxWeapons.setSize(77, 15);
		lblMaxWeapons.setToolTipText("Defines how many weapons are to be picked from the preset.");
		lblMaxWeapons.setText("Weapon Count:");
		
		spMaxWeapons = new Spinner(composite_2, SWT.BORDER);
		spMaxWeapons.setFont(Main.appFont);
		GridData gd_spMaxWeapons = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_spMaxWeapons.widthHint = 10;
		spMaxWeapons.setLayoutData(gd_spMaxWeapons);
		spMaxWeapons.setSize(35, 22);
		spMaxWeapons.setMaximum(spSlots.getSelection());
		
		Group grpPresetDefinition = new Group(weaponsC, SWT.NONE);
		grpPresetDefinition.setFont(Main.appFont);
		grpPresetDefinition.setLayout(new GridLayout(2, false));
		grpPresetDefinition.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
		grpPresetDefinition.setText("Preset definition");
		
		presets = new Combo(grpPresetDefinition, SWT.READ_ONLY);
		presets.setFont(Main.appFont);
		presets.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1));
		presets.setEnabled(false);
		for (String s : ShipIO.weaponSetMap.keySet()) {
			presets.add(s);
		}
		
		Group grpExplicitDefinition = new Group(weaponsC, SWT.NONE);
		grpExplicitDefinition.setFont(Main.appFont);
		grpExplicitDefinition.setLayout(new GridLayout(2, false));
		GridData gd_grpExplicitDefinition = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
		gd_grpExplicitDefinition.heightHint = 114;
		grpExplicitDefinition.setLayoutData(gd_grpExplicitDefinition);
		grpExplicitDefinition.setText("Explicit definition");
		
		listWeapons = new List(grpExplicitDefinition, SWT.BORDER);
		listWeapons.setFont(Main.appFont);
		listWeapons.setToolTipText("Double-click on an item to delete it.");
		listWeapons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		categories = new Combo(grpExplicitDefinition, SWT.READ_ONLY);
		categories.setFont(Main.appFont);
		GridData gd_categories = new GridData(SWT.FILL, SWT.BOTTOM, false, false, 1, 1);
		gd_categories.widthHint = 100;
		categories.setLayoutData(gd_categories);
		categories.add("Laser Weapons");
		categories.add("Missile Weapons");
		categories.add("Ion Weapons");
		categories.add("Beam Weapons");
		categories.add("Bomb Weapons");
		categories.select(-1);
		
		weapons = new Combo(grpExplicitDefinition, SWT.READ_ONLY);
		weapons.setFont(Main.appFont);
		weapons.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
		
		
	// === Drones
		TabItem tbtmDrones = new TabItem(tabFolder, 0);
		tbtmDrones.setText("Drones");
		
		Composite dronesC = new Composite(tabFolder, SWT.NONE);
		tbtmDrones.setControl(dronesC);
		dronesC.setLayout(new GridLayout(4, false));
		
		Group grpDrones = new Group(dronesC, SWT.NONE);
		grpDrones.setFont(Main.appFont);
		grpDrones.setText("Define drones");
		grpDrones.setLayout(new GridLayout(1, false));
		GridData gd_grpDrones = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 3);
		gd_grpDrones.widthHint = 95;
		grpDrones.setLayoutData(gd_grpDrones);
		
		btnExplicitDr = new Button(grpDrones, SWT.RADIO);
		btnExplicitDr.setFont(Main.appFont);
		GridData gd_btnExplicitDr = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnExplicitDr.minimumWidth = 80;
		gd_btnExplicitDr.widthHint = 80;
		btnExplicitDr.setLayoutData(gd_btnExplicitDr);
		btnExplicitDr.setText("Explicitly");
		
		btnUseSetDr = new Button(grpDrones, SWT.RADIO);
		btnUseSetDr.setFont(Main.appFont);
		GridData gd_btnUseSetDr = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnUseSetDr.minimumWidth = 80;
		gd_btnUseSetDr.widthHint = 80;
		btnUseSetDr.setLayoutData(gd_btnUseSetDr);
		btnUseSetDr.setText("By preset");
		
		Composite composite_2dr = new Composite(dronesC, SWT.NONE);
		composite_2dr.setLayout(new GridLayout(2, false));
		composite_2dr.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 3));
		
		Label lblDroneSlots = new Label(composite_2dr, SWT.NONE);
		lblDroneSlots.setFont(Main.appFont);
		GridData gd_lblDroneSlots = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblDroneSlots.minimumWidth = 85;
		gd_lblDroneSlots.widthHint = 85;
		lblDroneSlots.setLayoutData(gd_lblDroneSlots);
		lblDroneSlots.setToolTipText("Defines the number of drone slots available to the player.");
		lblDroneSlots.setText("Drone Slots:");
		
		spSlotsDr = new Spinner(composite_2dr, SWT.BORDER);
		spSlotsDr.setFont(Main.appFont);
		GridData gd_spSlotsDr = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_spSlotsDr.minimumWidth = 10;
		gd_spSlotsDr.widthHint = 10;
		spSlotsDr.setLayoutData(gd_spSlotsDr);
		spSlotsDr.setMaximum(4);
		
		Label lblMaxDrones = new Label(composite_2dr, SWT.NONE);
		lblMaxDrones.setFont(Main.appFont);
		GridData gd_lblMaxDrones = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblMaxDrones.minimumWidth = 85;
		gd_lblMaxDrones.widthHint = 85;
		lblMaxDrones.setLayoutData(gd_lblMaxDrones);
		lblMaxDrones.setSize(77, 15);
		lblMaxDrones.setToolTipText("Defines how many drones are to be picked from the preset.");
		lblMaxDrones.setText("Drone Count:");
		
		spMaxDrones = new Spinner(composite_2dr, SWT.BORDER);
		spMaxDrones.setFont(Main.appFont);
		GridData gd_spMaxDrones = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_spMaxDrones.minimumWidth = 10;
		gd_spMaxDrones.widthHint = 10;
		spMaxDrones.setLayoutData(gd_spMaxDrones);
		spMaxDrones.setSize(35, 22);
		spMaxDrones.setMaximum(spSlotsDr.getSelection());
		
		Group grpPresetDefinitionDr = new Group(dronesC, SWT.NONE);
		grpPresetDefinitionDr.setFont(Main.appFont);
		grpPresetDefinitionDr.setLayout(new GridLayout(2, false));
		grpPresetDefinitionDr.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
		grpPresetDefinitionDr.setText("Preset definition");
		
		presetsDr = new Combo(grpPresetDefinitionDr, SWT.READ_ONLY);
		presetsDr.setFont(Main.appFont);
		presetsDr.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1));
		presetsDr.setEnabled(false);
		for (String s : ShipIO.droneSetMap.keySet()) {
			presetsDr.add(s);
		}
		
		Group grpExplicitDefinitionDr = new Group(dronesC, SWT.NONE);
		grpExplicitDefinitionDr.setFont(Main.appFont);
		grpExplicitDefinitionDr.setLayout(new GridLayout(2, false));
		GridData gd_grpExplicitDefinitionDr = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
		gd_grpExplicitDefinitionDr.heightHint = 114;
		grpExplicitDefinitionDr.setLayoutData(gd_grpExplicitDefinitionDr);
		grpExplicitDefinitionDr.setText("Explicit definition");
		
		listDrones = new List(grpExplicitDefinitionDr, SWT.BORDER);
		listDrones.setFont(Main.appFont);
		listDrones.setToolTipText("Double-click on an item to delete it.");
		listDrones.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		categoriesDr = new Combo(grpExplicitDefinitionDr, SWT.READ_ONLY);
		categoriesDr.setFont(Main.appFont);
		GridData gd_categoriesDr = new GridData(SWT.FILL, SWT.BOTTOM, false, false, 1, 1);
		gd_categoriesDr.widthHint = 100;
		categoriesDr.setLayoutData(gd_categoriesDr);
		categoriesDr.add("Defensive drones");
		categoriesDr.add("Offensive drones");
		categoriesDr.select(-1);
		
		drones = new Combo(grpExplicitDefinitionDr, SWT.READ_ONLY);
		drones.setFont(Main.appFont);
		drones.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
		
	// === Bottom buttons
		
		Composite composite = new Composite(shell, SWT.NONE);
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.verticalSpacing = 0;
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		composite.setLayout(gl_composite);
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_composite.heightHint = 42;
		composite.setLayoutData(gd_composite);
		
		Button btnConfirm = new Button(composite, SWT.NONE);
		btnConfirm.setFont(Main.appFont);
		GridData gd_btnConfirm = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnConfirm.heightHint = 25;
		gd_btnConfirm.widthHint = 80;
		btnConfirm.setLayoutData(gd_btnConfirm);
		btnConfirm.setText("Confirm");
		
		Button btnCancel = new Button(composite, SWT.NONE);
		btnCancel.setFont(Main.appFont);
		GridData gd_btnCancel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnCancel.heightHint = 25;
		gd_btnCancel.widthHint = 80;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setText("Cancel");

	// === LISTENERS

		btnCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.setVisible(false);
			}
		});
		
		btnConfirm.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// ship info
				Main.ship.shipName = textName.getText();
				Main.ship.shipClass = textClass.getText();
				Main.ship.descr = textDesc.getText();
				Main.ship.layout = textLayout.getText();
				Main.ship.imageName = textImage.getText();

				/*
				// cloak override
				Main.ship.cloakOverride = textCloakO.getText();
				
				// shield override
				if (Main.loadShield && ShipIO.isNull(textShieldO.getText()) && !ShipIO.isNull(Main.ship.shieldOverride)) {
					if (!ShipIO.loadingSwitch && Main.shieldImage != null && !Main.shieldImage.isDisposed() )
						Main.shieldImage.dispose();
					Main.shieldImage = null;
					String sub = Main.ship.imageName;
					
					if (sub.contains("_2"))
						sub = sub.substring(0, sub.lastIndexOf("_"));
					if (ShipIO.loadingSwitch) {
						Main.shieldImage = SWTResourceManager.getImage(Main.resPath + ShipIO.pathDelimiter + "img" + ShipIO.pathDelimiter + "ship" + ShipIO.pathDelimiter + sub + "_shields1.png");
					} else {
						Main.shieldImage = new Image(Main.shell.getDisplay(), Main.resPath + ShipIO.pathDelimiter + "img" + ShipIO.pathDelimiter + "ship" + ShipIO.pathDelimiter + sub + "_shields1.png");
					}
				} else if (!ShipIO.isNull(textShieldO.getText())) {
					Main.ship.shieldOverride = textShieldO.getText();
				}
				*/
				
				// crew
				Main.ship.crewMax = spMax.getSelection();
				Main.ship.crewMap.put("human", spHuman.getSelection());
				Main.ship.crewMap.put("engi", spEngi.getSelection());
				Main.ship.crewMap.put("zoltan", spZoltan.getSelection());
				Main.ship.crewMap.put("mantis", spMantis.getSelection());
				Main.ship.crewMap.put("slug", spSlug.getSelection());
				Main.ship.crewMap.put("rock", spRock.getSelection());
				Main.ship.crewMap.put("crystal", spCrystal.getSelection());
				Main.ship.crewMap.put("ghost", spGhost.getSelection());
				
				// health & power
				Main.ship.reactorPower = spReactor.getSelection();
				Main.ship.hullHealth = spHealth.getSelection();
				
				// armaments
				Main.ship.missiles = spMissiles.getSelection();
				Main.ship.drones = spDrones.getSelection();
				
				Main.ship.weaponsBySet = btnUseSet.getSelection();
				Main.ship.dronesBySet = btnUseSetDr.getSelection();
				
				Main.ship.weaponSlots = spSlots.getSelection();
				Main.ship.weaponCount = spMaxWeapons.getSelection();
				Main.ship.droneSlots = spSlotsDr.getSelection();
				Main.ship.droneCount = spMaxDrones.getSelection();
				
				Main.ship.weaponSet.clear();
				if (Main.ship.weaponsBySet) {
					Main.ship.weaponSet.add(presets.getText());
				} else {
					for (String s : listWeapons.getItems()) {
						s = s.substring(s.lastIndexOf("(")+1, s.lastIndexOf(")"));
						Main.ship.weaponSet.add(s);
					}
				}
				
				Main.ship.droneSet.clear();
				if (Main.ship.dronesBySet) {
					Main.ship.droneSet.add(presetsDr.getText());
				} else {
					for (String s : listDrones.getItems()) {
						s = s.substring(s.lastIndexOf("(")+1, s.lastIndexOf(")"));
						Main.ship.droneSet.add(s);
					}
				}
				
				Main.ship.augmentSet.clear();
				for (String aug : listAugments.getItems()) {
					aug = aug.substring(aug.lastIndexOf("(")+1, aug.lastIndexOf(")"));
					Main.ship.augmentSet.add(aug);
				}
				
				ShipIO.fixImgNames();
				ShipIO.loadWeaponStrips();
				ShipIO.loadWeaponImages(Main.ship);
				
				shell.setVisible(false);
			}
		});
		
		btnExplicit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				categories.setEnabled(true);
				weapons.setEnabled(true);
				listWeapons.setEnabled(true);
				presets.setEnabled(false);
				spMaxWeapons.setEnabled(false);
			}
		});
		
		btnUseSet.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				categories.setEnabled(false);
				weapons.setEnabled(false);
				listWeapons.setEnabled(false);
				presets.setEnabled(true);
				spMaxWeapons.setEnabled(true);
			}
		});
		
		btnExplicitDr.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				categoriesDr.setEnabled(true);
				drones.setEnabled(true);
				listDrones.setEnabled(true);
				presetsDr.setEnabled(false);
				spMaxDrones.setEnabled(false);
			}
		});
		
		btnUseSetDr.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				categoriesDr.setEnabled(false);
				drones.setEnabled(false);
				listDrones.setEnabled(false);
				presetsDr.setEnabled(true);
				spMaxDrones.setEnabled(true);
			}
		});
		
		spMax.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				spHuman.setMaximum(spMax.getSelection());
				spEngi.setMaximum(spMax.getSelection());
				spZoltan.setMaximum(spMax.getSelection());
				spMantis.setMaximum(spMax.getSelection());
				spSlug.setMaximum(spMax.getSelection());
				spRock.setMaximum(spMax.getSelection());
				spCrystal.setMaximum(spMax.getSelection());
			}
		});

		listAugments.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				if (listAugments.getItemCount() != 0)
					listAugments.remove(listAugments.getSelection()[0]);
				augments.setEnabled(listAugments.getItemCount()!=3);
			}
		});

		listWeapons.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				if (listWeapons.getItemCount() != 0)
					listWeapons.remove(listWeapons.getSelection()[0]);
				weapons.setEnabled(listWeapons.getItemCount()!=spSlots.getSelection());
			}
		});

		listDrones.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				if (listDrones.getItemCount() != 0)
					listDrones.remove(listDrones.getSelection()[0]);
				drones.setEnabled(listDrones.getItemCount()!=spSlotsDr.getSelection());
			}
		});
		
		weapons.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (listWeapons.getItemCount() < spSlots.getSelection()) {
					listWeapons.add(weapons.getText());
					weapons.setEnabled(listWeapons.getItemCount()!=spSlots.getSelection());
				}
			}
		});
		
		drones.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (listDrones.getItemCount() < spSlotsDr.getSelection()) {
					listDrones.add(drones.getText());
					drones.setEnabled(listDrones.getItemCount()!=spSlotsDr.getSelection());
				}
			}
		});
		
		augments.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (listAugments.getItemCount() < 3) {
					listAugments.add(augments.getText());
					augments.setEnabled(listAugments.getItemCount()!=3);
				}
			}
		});
		
        shell.addListener(SWT.Traverse, new Listener() {
        	@Override
        	public void handleEvent(Event e) {
        		if (e.detail == SWT.TRAVERSE_ESCAPE) {
        			e.doit = false;
    				Main.shell.setEnabled(true);
    				shell.setVisible(false);
        		}
        	}
        });
        
		spSlots.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				spMaxWeapons.setMaximum(spSlots.getSelection());
			}
		});
        
		categories.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				weaponsListSet(categories.getText());
			}
		});
        
		spSlotsDr.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				spMaxDrones.setMaximum(spSlotsDr.getSelection());
			}
		});
        
		categoriesDr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dronesListSet(categoriesDr.getText());
			}
		});
	}
	
	// === AUXILIARY
	
	public void weaponsListSet(String s) {
		FTLItem i = null;
		String[] st = s.split(" ");
		s = st[0].toUpperCase();
		
		if (s.equals("MISSILE")) s = s+"S";
		
		weapons.removeAll();
		
		for (String str : ShipIO.weaponMap.keySet()) {
			i = ShipIO.weaponMap.get(str);
			if ((i.category.equals(s) && (!s.equals("LASER") || (s.equals("LASER") && !i.blueprint.contains("ION"))) || (s.equals("ION") && i.blueprint.contains(s)))) {
				weapons.add(i.name + " (" + i.blueprint + ")");
			}
		}
	}
	
	public void dronesListSet(String s) {
		FTLItem i = null;
		String[] st = s.split(" ");
		s = st[0].toUpperCase();
		
		drones.removeAll();

		for (String str : ShipIO.droneMap.keySet()) {
			i = ShipIO.droneMap.get(str);
			if ((s.equals("DEFENSIVE") && (i.category.equals("DEFENSE") || i.category.contains("REPAIR")
					|| i.category.equals("BATTLE"))) || (s.equals("OFFENSIVE") && !(i.category.equals("DEFENSE") || i.category.contains("REPAIR") || i.category.equals("BATTLE")))) {
				drones.add(i.name + " (" + i.blueprint + ")");
			}
		}
	}
}









