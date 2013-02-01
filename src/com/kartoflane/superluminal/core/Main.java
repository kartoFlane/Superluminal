package com.kartoflane.superluminal.core;

import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.graphics.*;


import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

import com.kartoflane.superluminal.elements.FTLDoor;
import com.kartoflane.superluminal.elements.FTLMount;
import com.kartoflane.superluminal.elements.FTLRoom;
import com.kartoflane.superluminal.elements.FTLShip;
import com.kartoflane.superluminal.elements.Slide;
import com.kartoflane.superluminal.elements.Systems;
import com.kartoflane.superluminal.ui.ExportDialog;
import com.kartoflane.superluminal.ui.NewShipWindow;
import com.kartoflane.superluminal.ui.PropertiesWindow;
import com.kartoflane.superluminal.ui.ShipBrowser;
import com.kartoflane.superluminal.ui.ShipPropertiesWindow;

public class Main
{
		// === CONSTANTS
	/**
	 * Frequency of canvas redrawing (if constantRedraw == true)
	 */
	private final static int INTERVAL = 33;
	/**
	 * Size of corner indicators on currently selected room
	 */
	private final static int CORNER = 10;
	/**
	 * Width of the drawing area, in grid cells
	 */
	public static int GRID_W = 26;
	public static final int GRID_W_MAX = 26;
	/**
	 * Height of the drawing area, in grid cells
	 */
	public static int GRID_H = 20;
	public static final int GRID_H_MAX = 20;
	
	public final static int REACTOR_MAX_PLAYER = 25;
	public final static int REACTOR_MAX_ENEMY = 32;
	
	
	public final static String APPNAME = "Superluminal";
	public final static String VERSION = "2013.02.1" + "-";
	
		// === Important objects
	public static Shell shell;
	private static Canvas canvas;
	public static FTLShip ship;
	public static ShipPropertiesWindow shipDialog;
	public static PropertiesWindow sysDialog;
	public static ExportDialog exDialog;
	public static MessageBox box;
	
		// === Preferences
		// ship explorer
	public static String dataPath = "null";
	public static String resPath = "null";
		// edit menu
	public static boolean removeDoor = true;
	public static boolean snapMounts = false;
	public static boolean snapMountsToHull = true;
		// view menu
	public static boolean showAnchor = true;
	public static boolean showMounts = true;
	public static boolean showRooms = true;
	public static boolean showHull = true;
	public static boolean loadFloor = true;
	public static boolean loadShield = true;
	public static boolean loadSystem = true;
		// export dialog
	public static String exportPath = "null";
		// other
	public static boolean constantRedraw = true;
	
		// ===  Mouse related
	public static Point mousePos = new Point(0,0);
	public static Point mousePosLastClick = new Point(0,0);
	public static Point dragRoomAnchor = new Point(0,0);
	public static boolean leftMouseDown = false;
	public static boolean rightMouseDown = false;
	public static boolean inBounds = false;
	
		// === Generic booleans
	private static boolean onCanvas = false;
	private static boolean moveSelected = false;
	private static boolean resizeSelected = false;
	private static boolean hullSelected = false;
	private static boolean shieldSelected = false;
	public static boolean canvasActive = false;
	public static boolean modShift = false;
	public static boolean moveAnchor = false;
	public static boolean allowRoomPlacement = true;
	
		// === Internal
	public static boolean debug = true;
	/**
	 * when set to true, all ship data is pre-loaded into hashmaps/sets when the ship browser is opened for the first time.
	 * no need to do this, only one ship is being used at any given time.
	 */
	public static boolean dataPreloading = false;
	/**
	 * Used when dataPreloading is enabled, set to true once data loading has been finished.
	 */
	public static boolean dataLoaded = false;
	
		// === Variables used to store a specific element out of a set (currently selected room, etc)
	public static FTLRoom selectedRoom = null;
	public static FTLDoor selectedDoor = null;
	public static FTLMount selectedMount = null;
	
	private static FTLRoom parseRoom = null;
	private static Rectangle parseRect = null;
	private static Rectangle phantomRect = null;
	public static Rectangle mountRect = new Rectangle(0,0,0,0);
	public static Rectangle shieldEllipse = new Rectangle(0,0,0,0);
	
	private static Slide mountToolSlide = Slide.UP;
	private static boolean mountToolMirror = true;
	private static boolean mountToolHorizontal = true;
	
		// === Image holders
	public static Image hullImage = null;
	public static Image floorImage = null;
	public static Image shieldImage = null;
	public static Image cloakImage = null;
	public static Image tempImage;
	public static Image pinImage = null;
	
		// === Miscellaneous
	private static Rectangle[] corners = new Rectangle[4];
	private static Color highlightColor = null;
	private static String lastMsg = "";
	/**
	 * Path of current project file, for quick saving via Ctrl+S
	 */
	public static String currentPath = null;

	/**
	 * Contains room ids currently in use.
	 */
	public static HashSet<Integer> idList = new HashSet<Integer>();
	/**
	 * Preloaded data is stored in this map.
	 */
	public static HashMap<String, Object> loadedData = new HashMap<String, Object>();
	/**
	 * Images (tools and systems) are loaded once and then references are held in this map for easy access.
	 */
	public static HashMap<String, Image> toolsMap = new HashMap<String, Image>();
	/**
	 * Holds Image objects for easy reference, without the need to load them every time they're needed.
	 */
	public static HashMap<Systems, Image> systemsMap = new HashMap<Systems, Image>();

	// === GUI elements' variables, for use in listeners and functions that reference them
	private static Menu menuSystem;
	private static Label helpIcon;
	private static Label text;
	private static Label mPosText;
	private static Label shipInfoText;
	public static MenuItem mntmClose;

	public static Font appFont = (SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
	
	
	// =================================================================================================== //
	
	/*
	 * === TODO
	 * 
	 * - GMM nie zmienia rzeczy w bluepritns.xml, tylko dokleja na koncu pliku -> trzeba chyba zrobic wstepny skan czy sa drugie blueprinty, jak tak to wczytaj najdalszy / ostatni
	 * - wczytuj default weapon image zamiast zoltych prostokatow
	 * - mouse coords -> dodaj box wyswietlajacy pixel mousePos, + box gdzie mozna ustawic x,y wybranego elementu
	 * 
	 * === DONE
	 * NEEDS TESTING - rozne font sizes fuck up alignment w okienkach, ustaw explicitly szerokosc/wysokosc tak zeby nie zalezaly od font size lub ustaw globalny font size dla calej aplikacji (bo inaczej dziedziczy po OS) 
	 * DONE - button do "przypiecia" wybranego elementu -> ze jak sa przypiete to nie mozna ich ruszyc (variable zrob w FTLShip, zeby zapisywalo razem z projektem)
	 * DONE - modify shield size for enemy ships
	 * DONE - fix shield graphic alignment on enemy ships
	 * DONE - make room interiors stretch to match room dimensions
	 * X - see if its possible to make tooltips display for a longer period of time --> NOT possible, would have to implement custom tooltip system yourself
	 * 
	 * =========================================================================
	 * - gibs -> male okienko gdzie ustawiasz kat (ko³o ze wskaznikiem), predkosc liniowa i katowa (slidery)
	 * - gibs -> ujemne angular velocity obraca w lewa strone
	 * 		  -> 10 angular velocty = pelen obrot
	 * 
	 * 
	 * - app settings window -> zmiana rozmiarow grid, constantRedraw, loadingSwitch, etc
	 * 
	 * - weapon mount tool -> mirror x/y axis, stick to other mounts (np if deltaX < 10 then snap)
	 *   * mirror x/y -> poprzez przeciagniece i upusczenie jednego mounta nad drugim (upuszczany jest receiver) -> update pozycji tylko w 
	 *        mouseMove::   m.x = val, m.y = val, if (mirrorCounterpart != null) then mirror.x = m.x, etc 
	 * 
	 */
	
	// =================================================================================================== //
	
	public static void main(String[] args)
	{
		try {
			Main window = new Main();
			window.open();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void open()
	{
		final Display display = Display.getDefault();
		
		// create config file if it doesn't exist already
		if (!ConfigIO.configExists()) {
			ConfigIO.saveConfig();
		}
		
		// resize the window as to not exceed screen dimensions, with maximum size being defined by GRID_W_MAX and GRID_H_MAX
		GRID_W = ((int) (display.getClientArea().width * 0.7)/35);
		GRID_H = ((int) (display.getClientArea().height * 0.8)/35);
		GRID_W = (GRID_W > GRID_W_MAX) ? GRID_W_MAX : GRID_W;
		GRID_H = (GRID_H > GRID_H_MAX) ? GRID_H_MAX : GRID_H;
		
		// load values from config
		exportPath = ConfigIO.scourFor("exportPath");
		dataPath = ConfigIO.scourFor("dataPath");
		resPath = ConfigIO.scourFor("resPath");
		removeDoor = ConfigIO.getBoolean("removeDoor");
		snapMounts = ConfigIO.getBoolean("snapMounts");
		showAnchor = ConfigIO.getBoolean("showAnchor");
		showMounts = ConfigIO.getBoolean("showMounts");
		showRooms = ConfigIO.getBoolean("showRooms");
		showHull = ConfigIO.getBoolean("showHull");
		snapMounts = ConfigIO.getBoolean("snapMounts");
		snapMountsToHull = ConfigIO.getBoolean("snapMountsToHull");
		loadFloor = ConfigIO.getBoolean("loadFloor");
		loadShield = ConfigIO.getBoolean("loadShield");
		loadSystem = ConfigIO.getBoolean("loadSystem");
		constantRedraw = ConfigIO.getBoolean("constantRedraw");
		
		// several levels of redundancy, in case someone, somewhere, somehow, didn't have one of those fonts...
		if (appFont == null) {
			appFont = (SWTResourceManager.getFont("Arial", 9, SWT.NORMAL));
		}
		if (appFont == null) {
			appFont = (SWTResourceManager.getFont("Times New Roman", 9, SWT.NORMAL));
		}
		
		createContents();
		ShipIO.fetchShipNames();
		
		shell.setMinimumSize(GRID_W*35, GRID_H*35);
		shell.open();
		
		sysDialog = new PropertiesWindow(shell);
		shipDialog = new ShipPropertiesWindow(shell);
		
		
		display.timerExec(INTERVAL, new Runnable() {
			public void run() {
				if (canvas.isDisposed()) return;

				if (canvasActive) {
					if (constantRedraw && onCanvas)
						canvas.redraw();
					
					// === update info text fields; mousePos and rudimentary ship info
					mPosText.setText("(" + (int)(1+Math.floor(mousePos.x/35)) + ", " + (int)(1+Math.floor(mousePos.y/35)) + ")");
					shipInfoText.setText("rooms: " + ship.rooms.size() + ",  doors: " + ship.doors.size());
				}
				display.timerExec(INTERVAL, this);
			}
		});
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	protected void createContents()
	{
		shell = new Shell(SWT.BORDER | SWT.CLOSE | SWT.TITLE | SWT.MIN);
		shell.setText(APPNAME + " - Ship Editor");
		shell.setFont(appFont);
		
		highlightColor = shell.getDisplay().getSystemColor(SWT.COLOR_GREEN);
		shell.setLayout(new GridLayout(2, false));
		
		// Info label
		helpIcon = new Label(shell, SWT.NONE);
		helpIcon.setFont(appFont);
		helpIcon.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		tempImage = SWTResourceManager.getImage(Main.class, "/org/eclipse/jface/dialogs/images/help.gif");
		helpIcon.setImage(SWTResourceManager.getImage(Main.class, "/img/message_info.gif"));
		helpIcon.setToolTipText(" -Use the Q, W, E, A, S keys to quickly select the tools (use Alt+[Key] for the first press)."
								+ShipIO.lineDelimiter+" -Press Delete or Shift+D to delete selected object (except hull and shields)"
								+ShipIO.lineDelimiter+" -Click on the anchor and hold to move the entire ship around"
								+ShipIO.lineDelimiter+" -Press down Shift key while dragging the anchor to move only the anchor w/o moving the ship"
								+ShipIO.lineDelimiter+" -R-click on the anchor and drag to set the vertical offset of the ship");
					
		
	// === Load images to a map for easy access
		
		tempImage = SWTResourceManager.getImage(Main.class, "/img/systems/s_"+Systems.PILOT.toString().toLowerCase()+"_overlay.png");
		systemsMap.put(Systems.PILOT, tempImage);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/systems/s_"+Systems.DOORS.toString().toLowerCase()+"_overlay.png");
		systemsMap.put(Systems.DOORS, tempImage);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/systems/s_"+Systems.SENSORS.toString().toLowerCase()+"_overlay.png");
		systemsMap.put(Systems.SENSORS, tempImage);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/systems/s_"+Systems.OXYGEN.toString().toLowerCase()+"_overlay.png");
		systemsMap.put(Systems.OXYGEN, tempImage);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/systems/s_"+Systems.MEDBAY.toString().toLowerCase()+"_overlay.png");
		systemsMap.put(Systems.MEDBAY, tempImage);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/systems/s_"+Systems.SHIELDS.toString().toLowerCase()+"_overlay.png");
		systemsMap.put(Systems.SHIELDS, tempImage);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/systems/s_"+Systems.WEAPONS.toString().toLowerCase()+"_overlay.png");
		systemsMap.put(Systems.WEAPONS, tempImage);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/systems/s_"+Systems.ENGINES.toString().toLowerCase()+"_overlay.png");
		systemsMap.put(Systems.ENGINES, tempImage);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/systems/s_"+Systems.DRONES.toString().toLowerCase()+"_overlay.png");
		systemsMap.put(Systems.DRONES, tempImage);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/systems/s_"+Systems.TELEPORTER.toString().toLowerCase()+"_overlay.png");
		systemsMap.put(Systems.TELEPORTER, tempImage);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/systems/s_"+Systems.CLOAKING.toString().toLowerCase()+"_overlay.png");
		systemsMap.put(Systems.CLOAKING, tempImage);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/systems/s_"+Systems.ARTILLERY.toString().toLowerCase()+"_overlay.png");
		systemsMap.put(Systems.ARTILLERY, tempImage);
				
		tempImage = SWTResourceManager.getImage(Main.class, "/img/room.png");
		toolsMap.put("room", tempImage);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/door.png");
		toolsMap.put("door", tempImage);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/pointer.png");
		toolsMap.put("pointer", tempImage);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/mount.png");
		toolsMap.put("mount", tempImage);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/system.png");
		toolsMap.put("system", tempImage);
		
		pinImage = SWTResourceManager.getImage(Main.class, "/img/pin.png");

	// === Menu bar
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

	// === File menu
		
		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");
		Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);
	
		// === File -> New ship
		final MenuItem mntmNewShip = new MenuItem(menu_1, SWT.NONE);
		mntmNewShip.setText("New Ship \tCtrl + N");
		
		// === File -> Load ship
		new MenuItem(menu_1, SWT.SEPARATOR);
		final MenuItem mntmLoadShip = new MenuItem(menu_1, SWT.NONE);
		mntmLoadShip.setText("Load Ship...\tCtrl + L");
		
		// === File -> Open project
		final MenuItem mntmLoadShipProject = new MenuItem(menu_1, SWT.NONE);
		mntmLoadShipProject.setText("Open Project...\tCtrl + O");

		new MenuItem(menu_1, SWT.SEPARATOR);
		
		// === File -> Save project
		final MenuItem mntmSaveShip = new MenuItem(menu_1, SWT.NONE);
		mntmSaveShip.setText("Save Project \tCtrl + S");
		mntmSaveShip.setEnabled(false);
		
		// === File -> Save project as
		final MenuItem mntmSaveShipAs = new MenuItem(menu_1, SWT.NONE);
		mntmSaveShipAs.setText("Save Project As...");
		mntmSaveShipAs.setEnabled(false);
		
		// === File -> Export ship
		final MenuItem mntmExport = new MenuItem(menu_1, SWT.NONE);
		mntmExport.setText("Export Ship... \tCtrl + E");
		mntmExport.setEnabled(false);
		
		new MenuItem(menu_1, SWT.SEPARATOR);
		
		// === File -> Close project
		mntmClose = new MenuItem(menu_1, SWT.NONE);
		mntmClose.setText("Close Project");
		mntmClose.setEnabled(false);
			

	// === Edit menu
		
		MenuItem mntmEdit = new MenuItem(menu, SWT.CASCADE);
		mntmEdit.setText("Edit");
		Menu menu_2 = new Menu(mntmEdit);
		mntmEdit.setMenu(menu_2);
		
		// === Edit -> Automatic door clean
		MenuItem mntmRemoveDoors = new MenuItem(menu_2, SWT.CHECK);
		mntmRemoveDoors.setSelection(true);
		mntmRemoveDoors.setText("Automatic Door Cleanup");
		mntmRemoveDoors.setSelection(removeDoor);
		
		// === Edit -> Weapon mount aiign
		MenuItem mntmWeaponMountSnapping = new MenuItem(menu_2, SWT.CHECK);
		mntmWeaponMountSnapping.setEnabled(false);
		mntmWeaponMountSnapping.setText("Weapon Mount Aligning");
		mntmWeaponMountSnapping.setSelection(snapMounts);
		
	// === View menu
		
		MenuItem mntmView = new MenuItem(menu, SWT.CASCADE);
		mntmView.setText("View");
		Menu menu_7 = new Menu(mntmView);
		mntmView.setMenu(menu_7);
		
		// === View -> Show anchor
		final MenuItem mntmShowAnchor = new MenuItem(menu_7, SWT.CHECK);
		mntmShowAnchor.setText("Show Anchor \t&1");
		mntmShowAnchor.setSelection(showAnchor);
		
		// === View -> Show mounts
		final MenuItem mntmShowMounts = new MenuItem(menu_7, SWT.CHECK);
		mntmShowMounts.setText("Show Mounts \t&2");
		mntmShowMounts.setSelection(showMounts);
		
		// === View -> show rooms
		final MenuItem mntmShowRooms = new MenuItem(menu_7, SWT.CHECK);
		mntmShowRooms.setText("Show Rooms And Doors \t&3");
		mntmShowRooms.setSelection(showRooms);
		
		// === View -> show hull
		final MenuItem mntmShowHull = new MenuItem(menu_7, SWT.CHECK);
		mntmShowHull.setText("Show Graphics \t&4");
		mntmShowHull.setSelection(showHull);
		
		new MenuItem(menu_7, SWT.SEPARATOR);
		
		// === View -> load floor
		final MenuItem mntmLoadFloorGraphic = new MenuItem(menu_7, SWT.CHECK);
		mntmLoadFloorGraphic.setText("Load Floor Graphic");
		mntmLoadFloorGraphic.setSelection(loadFloor);
		
		// === View -> load shield
		final MenuItem mntmLoadShieldGraphic = new MenuItem(menu_7, SWT.CHECK);
		mntmLoadShieldGraphic.setText("Load Shield Graphic");
		mntmLoadShieldGraphic.setSelection(loadShield);
		
		// === View -> load system graphic
		MenuItem mntmLoadSystem = new MenuItem(menu_7, SWT.CHECK);
		mntmLoadSystem.setText("Load System Graphics");
		mntmLoadSystem.setSelection(loadSystem);
		
		new MenuItem(menu_7, SWT.SEPARATOR);
		
		final MenuItem mntmConstantRedraw = new MenuItem(menu_7, SWT.CHECK);
		mntmConstantRedraw.setText("Constant Redraw");
		mntmConstantRedraw.setSelection(constantRedraw);
		
	// === Text Info Fields
		
		Composite textHolder = new Composite(shell, SWT.NONE);
		GridData gd_textHolder = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_textHolder.heightHint = 20;
		textHolder.setLayoutData(gd_textHolder);
		textHolder.setLayout(new FormLayout());
		
		// === Position of the pointer on the grid
		mPosText = new Label(textHolder, SWT.BORDER);
		mPosText.setFont(appFont);
		FormData fd_mPosText = new FormData();
		fd_mPosText.height = 20;
		fd_mPosText.right = new FormAttachment(0, 50);
		fd_mPosText.top = new FormAttachment(0);
		fd_mPosText.left = new FormAttachment(0);
		mPosText.setLayoutData(fd_mPosText);
		
		// === Number of rooms and doors in the ship
		shipInfoText = new Label(textHolder, SWT.BORDER);
		shipInfoText.setFont(appFont);
		FormData fd_shipInfoText = new FormData();
		fd_shipInfoText.height = 20;
		fd_shipInfoText.right = new FormAttachment(0, 185);
		fd_shipInfoText.top = new FormAttachment(0);
		fd_shipInfoText.left = new FormAttachment(0, 55);
		shipInfoText.setLayoutData(fd_shipInfoText);
		
		// === Status bar
		text = new Label(textHolder, SWT.WRAP | SWT.BORDER);
		text.setFont(appFont);
		FormData fd_text = new FormData();
		fd_text.height = 20;
		fd_text.right = new FormAttachment(100);
		fd_text.left = new FormAttachment(0, 190);
		fd_text.top = new FormAttachment(0);
		text.setLayoutData(fd_text);
		
		Label label_1 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		label_1.setFont(appFont);
		
		
	// === Canvas
		
		Composite canvasHolder = new Composite(shell, SWT.NONE);
		GridData gd_canvasHolder = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 4);
		gd_canvasHolder.widthHint = GRID_W*35+35;
		gd_canvasHolder.heightHint = GRID_H*35+35;
		canvasHolder.setLayoutData(gd_canvasHolder);
		canvasHolder.setLayout(new FormLayout());
		
		// Main screen where ships are displayed
		canvas = new Canvas(canvasHolder, SWT.NONE | SWT.TRANSPARENT | SWT.BORDER | SWT.DOUBLE_BUFFERED);
		Color c = new Color(shell.getDisplay(), 96, 96, 96);
		canvas.setBackground(c);
		c.dispose();
		FormData fd_canvas = new FormData();
		fd_canvas.height = GRID_H*35;
		fd_canvas.width = GRID_W*35;
		fd_canvas.bottom = new FormAttachment(0, GRID_H*35);
		fd_canvas.right = new FormAttachment(0, GRID_W*35);
		fd_canvas.top = new FormAttachment(0);
		fd_canvas.left = new FormAttachment(0);
		canvas.setLayoutData(fd_canvas);
		
		// canvas supposed to handle drawing of non-dynamic elements of the display (hull, grid, shields, etc), but not really used
		// REMINDER to be scrapped, find a dedicated 2d graphical library and use that
		Canvas bgCanvas = new Canvas(canvasHolder, SWT.NONE | SWT.BORDER);
		c = new Color(shell.getDisplay(), 96, 96, 96);
		bgCanvas.setBackground(c);
		c.dispose();
		FormData fd_bgCanvas = new FormData();
		fd_bgCanvas.height = GRID_H*35;
		fd_bgCanvas.width = GRID_W*35;
		fd_bgCanvas.bottom = new FormAttachment(0, GRID_H*35);
		fd_bgCanvas.right = new FormAttachment(0, GRID_W*35);
		fd_bgCanvas.top = new FormAttachment(0);
		fd_bgCanvas.left = new FormAttachment(0);
		bgCanvas.setLayoutData(fd_bgCanvas);
		
		Label canvasBg = new Label(canvasHolder, SWT.NONE);
		c = canvas.getDisplay().getSystemColor((SWT.COLOR_DARK_GRAY));
		canvasBg.setBackground(c);
		c.dispose();
		FormData fd_canvasBg = new FormData();
		fd_canvasBg.bottom = new FormAttachment(100);
		fd_canvasBg.right = new FormAttachment(100);
		fd_canvasBg.top = new FormAttachment(0);
		fd_canvasBg.left = new FormAttachment(0);
		canvasBg.setLayoutData(fd_canvasBg);
		
		
		Label lblTools = new Label(shell, SWT.NONE);
		GridData gd_lblTools = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_lblTools.minimumWidth = 70;
		gd_lblTools.widthHint = 70;
		lblTools.setLayoutData(gd_lblTools);
		lblTools.setAlignment(SWT.CENTER);
		lblTools.setFont(appFont);
		lblTools.setText("Tools");
		
	// === Tool bar
		
		// === Container - holds all the items on the left side of the screen
		Composite toolBarHolder = new Composite(shell, SWT.NONE);
		GridData gd_toolBarHolder = new GridData(SWT.CENTER, SWT.FILL, false, true, 1, 2);
		gd_toolBarHolder.minimumWidth = 70;
		gd_toolBarHolder.widthHint = 70;
		toolBarHolder.setLayoutData(gd_toolBarHolder);
		GridLayout gl_toolBarHolder = new GridLayout(1, false);
		gl_toolBarHolder.horizontalSpacing = 0;
		gl_toolBarHolder.marginWidth = 0;
		gl_toolBarHolder.marginHeight = 0;
		toolBarHolder.setLayout(gl_toolBarHolder);

		// === Container -> Tools - tool bar containing the tool icons
		final ToolBar toolBar = new ToolBar(toolBarHolder, SWT.FLAT | SWT.RIGHT | SWT.VERTICAL);
		toolBar.setFont(appFont);
		GridData gd_toolBar = new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1);
		gd_toolBar.minimumWidth = 70;
		gd_toolBar.widthHint = 70;
		toolBar.setLayoutData(gd_toolBar);
		
		// === Container -> Tools -> Pointer
		final ToolItem tltmPointer = new ToolItem(toolBar, SWT.RADIO);
		tltmPointer.setWidth(60);
		tltmPointer.setSelection(true);
		tltmPointer.setImage(toolsMap.get("pointer"));
		tltmPointer.setToolTipText("Selection tool"
									+ShipIO.lineDelimiter+" -Click to selet an object"
									+ShipIO.lineDelimiter+" -Click and hold to move the object around"
									+ShipIO.lineDelimiter+" -For rooms, click on a corner and drag to resize the room" 
									+ShipIO.lineDelimiter+" -R-click to assign a system to the selected room"
									+ShipIO.lineDelimiter+" -Double click on a room to set its' system's level and power"
									+ShipIO.lineDelimiter+" -For weapon mounts, hull and shields, press down Shift for precision mode");
		tltmPointer.setText("&Q");
		
		// === Container -> Tools -> Room creation
		final ToolItem tltmRoom = new ToolItem(toolBar, SWT.RADIO);
		tltmRoom.setWidth(60);
		tltmRoom.setToolTipText("Room creation tool"
								+ShipIO.lineDelimiter+" -Click and drag to create a room"
								+ShipIO.lineDelimiter+" -Hold down Shift and click to split rooms");
		tltmRoom.setImage(toolsMap.get("room"));
		tltmRoom.setText("&W");
		
		// === Container -> Tools -> Door creation
		final ToolItem tltmDoor = new ToolItem(toolBar, SWT.RADIO);
		tltmDoor.setWidth(60);
		tltmDoor.setToolTipText("Door creation tool"
								+ShipIO.lineDelimiter+" - Hover over an edge of a room and click to place door");
		tltmDoor.setImage(toolsMap.get("door"));
		tltmDoor.setText("&E");
		
		// === Container -> Tools -> Weapon mounting
		final ToolItem tltmMount = new ToolItem(toolBar, SWT.RADIO);
		tltmMount.setWidth(60);
		tltmMount.setToolTipText("Weapon mounting tool"
									+ShipIO.lineDelimiter+" -Click to place a weapon mount"
									+ShipIO.lineDelimiter+" -R-click to change the mount's rotation"
									+ShipIO.lineDelimiter+" -Shift-click to mirror the mount along its axis"
									+ShipIO.lineDelimiter+" -Shift-R-click to change the direction in which the weapon opens"
									+ShipIO.lineDelimiter+" (the last three also work with Selection Tool)");
		tltmMount.setImage(toolsMap.get("mount"));
		tltmMount.setText("&A");

		// === Container -> Tools -> System operating slot
		final ToolItem tltmSystem = new ToolItem(toolBar, SWT.RADIO);
		tltmSystem.setWidth(60);
		tltmSystem.setToolTipText("System operating station tool"
									+ShipIO.lineDelimiter+" - Click to place an operating station (only mannable systems + medbay)"
									+ShipIO.lineDelimiter+" - R-click to reset the station to default"
									+ShipIO.lineDelimiter+" - Shift-click to change facing of the station");
		tltmSystem.setImage(toolsMap.get("system"));
		tltmSystem.setText("&S");
		
		
		tltmPointer.setEnabled(false);
		tltmRoom.setEnabled(false);
		tltmDoor.setEnabled(false);
		tltmMount.setEnabled(false);
		tltmSystem.setEnabled(false);
				
		Label label = new Label(toolBarHolder, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setFont(appFont);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		// === Container -> Hull image button
		final Button btnHull = new Button(toolBarHolder, SWT.NONE);
		btnHull.setFont(appFont);
		GridData gd_btnHull = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_btnHull.widthHint = 70;
		btnHull.setLayoutData(gd_btnHull);
		btnHull.setEnabled(false);
		btnHull.setText("Hull");
		
		final Button btnShields = new Button(toolBarHolder, SWT.NONE);
		btnShields.setFont(appFont);
		btnShields.setToolTipText("Shield is aligned in relation to rooms. Place a room before choosing shield graphic.");
		btnShields.setEnabled(false);
		GridData gd_btnShields = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_btnShields.widthHint = 70;
		btnShields.setLayoutData(gd_btnShields);
		btnShields.setText("Shields");
		
		final Button btnFloor = new Button(toolBarHolder, SWT.NONE);
		btnFloor.setFont(appFont);
		btnFloor.setEnabled(false);
		GridData gd_btnFloor = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_btnFloor.widthHint = 70;
		btnFloor.setLayoutData(gd_btnFloor);
		btnFloor.setText("Floor");
		
		final Button btnCloak = new Button(toolBarHolder, SWT.NONE);
		btnCloak.setFont(appFont);
		btnCloak.setEnabled(false);
		GridData gd_btnCloak = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_btnCloak.widthHint = 70;
		btnCloak.setLayoutData(gd_btnCloak);
		btnCloak.setText("Cloak");
		
		Label label_2 = new Label(toolBarHolder, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_2.setFont(appFont);
		label_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		final Button btnShipProperties = new Button(toolBarHolder, SWT.NONE);
		btnShipProperties.setFont(appFont);
		GridData gd_btnShipProperties = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_btnShipProperties.minimumWidth = 60;
		gd_btnShipProperties.widthHint = 70;
		btnShipProperties.setLayoutData(gd_btnShipProperties);
		btnShipProperties.setText("Properties");
		btnShipProperties.setEnabled(false);

		
	// === System assignment context menu
			
		// === Systems
		menuSystem = new Menu(canvas);
			
		// === Systems -> Empty
		final MenuItem mntmEmpty = new MenuItem(menuSystem, SWT.RADIO);
		mntmEmpty.setSelection(true);
		mntmEmpty.setText("None");
		
		// === Systems -> Systems
		MenuItem mntmSystems = new MenuItem(menuSystem, SWT.CASCADE);
		mntmSystems.setText("Systems");
		Menu menu_3 = new Menu(mntmSystems);
		mntmSystems.setMenu(menu_3);

		// === Systems -> Systems -> Oxygen
		final MenuItem mntmOxygen = new MenuItem(menu_3, SWT.RADIO);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/smallsys/smalloxygen.png");
		mntmOxygen.setImage(tempImage);
		mntmOxygen.setText("Oxygen");

		// === Systems -> Systems -> Medbay
		final MenuItem mntmMedbay = new MenuItem(menu_3, SWT.RADIO);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/smallsys/smallmedbay.png");
		mntmMedbay.setImage(tempImage);
		mntmMedbay.setText("Medbay");

		// === Systems -> Systems -> Shields
		final MenuItem mntmShields = new MenuItem(menu_3, SWT.RADIO);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/smallsys/smallshields.png");
		mntmShields.setImage(tempImage);
		mntmShields.setText("Shields");

		// === Systems -> Systems -> Weapons
		final MenuItem mntmWeapons = new MenuItem(menu_3, SWT.RADIO);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/smallsys/smallweapons.png");
		mntmWeapons.setImage(tempImage);
		mntmWeapons.setText("Weapons");

		// === Systems -> Systems -> Engines
		final MenuItem mntmEngines = new MenuItem(menu_3, SWT.RADIO);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/smallsys/smallengines.png");
		mntmEngines.setImage(tempImage);
		mntmEngines.setText("Engines");

		// === Systems -> Subsystems
		MenuItem mntmSubsystems = new MenuItem(menuSystem, SWT.CASCADE);
		mntmSubsystems.setText("Subsystems");
		Menu menu_4 = new Menu(mntmSubsystems);
		mntmSubsystems.setMenu(menu_4);

		// === Systems -> Subsystems -> Pilot
		final MenuItem mntmPilot = new MenuItem(menu_4, SWT.RADIO);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/smallsys/smallpilot.png");
		mntmPilot.setImage(tempImage);
		mntmPilot.setText("Pilot");

		// === Systems -> Subsystems -> Doors
		final MenuItem mntmDoors = new MenuItem(menu_4, SWT.RADIO);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/smallsys/smalldoor.png");
		mntmDoors.setImage(tempImage);
		mntmDoors.setText("Doors");

		// === Systems -> Subsystems -> Sensors
		final MenuItem mntmSensors = new MenuItem(menu_4, SWT.RADIO);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/smallsys/smallsensors.png");
		mntmSensors.setImage(tempImage);
		mntmSensors.setText("Sensors");

		// === Systems -> Special
		MenuItem mntmSpecial = new MenuItem(menuSystem, SWT.CASCADE);
		mntmSpecial.setText("Special");
		Menu menu_5 = new Menu(mntmSpecial);
		mntmSpecial.setMenu(menu_5);

		// === Systems -> Special -> Drones
		final MenuItem mntmDrones = new MenuItem(menu_5, SWT.RADIO);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/smallsys/smalldrones.png");
		mntmDrones.setImage(tempImage);
		mntmDrones.setText("Drones");

		// === Systems -> Special -> Teleporter
		final MenuItem mntmTeleporter = new MenuItem(menu_5, SWT.RADIO);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/smallsys/smallteleporter.png");
		mntmTeleporter.setImage(tempImage);
		mntmTeleporter.setText("Teleporter");

		// === Systems -> Special -> Cloaking
		final MenuItem mntmCloaking = new MenuItem(menu_5, SWT.RADIO);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/smallsys/smallcloak.png");
		mntmCloaking.setImage(tempImage);
		mntmCloaking.setText("Cloaking");

		// === Systems -> Special -> Artillery
		final MenuItem mntmArtillery = new MenuItem(menu_5, SWT.RADIO);
		tempImage = SWTResourceManager.getImage(Main.class, "/img/smallsys/smallartillery.png");
		mntmArtillery.setImage(tempImage);
		mntmArtillery.setText("Artillery");
		
		new MenuItem(menuSystem, SWT.SEPARATOR);
		
		// === Systems -> Set System Image
		final MenuItem mntmSysImage = new MenuItem(menuSystem, SWT.NONE);
		mntmSysImage.setEnabled(false);
		mntmSysImage.setText("Set System Image...");
				
				
		shell.pack();
		shell.setMinimumSize(shell.getSize());
		
	//=============================================================
	//=== LISTENERS
		// === BOOKMARK: PAINT
		
		// the method used to draw stuff on the main display.
		// I'm pretty sure I should NOT be using it this way, putting so many conditions in there, but then I can't really think of another way to do it.
		// Split into more paintControl methods and redraw them separately only when needed to save a bit on performance?
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e)
			{
				Rectangle tempRoom = null;
				Rectangle tempDoor = null;
				FTLMount tempMount = null;
				FTLRoom tempFTLRoom = null;
		        Color c;
		        Point p, pt;
				
		        e.gc.setAlpha(255);
				e.gc.setFont(appFont);
				
				if (canvasActive) {
				// === DRAW SHIP HULL IMAGE
					if (showHull) {
						if (hullImage != null && !hullImage.isDisposed())
							e.gc.drawImage(hullImage, ship.imageRect.x, ship.imageRect.y);
						if (floorImage != null && !floorImage.isDisposed() && loadFloor)
							e.gc.drawImage(floorImage, ship.imageRect.x, ship.imageRect.y);
						
						// find geometrical center of the ship's rooms
						if (ship.rooms.size() > 0) {
							p = ship.findLowBounds();
							pt = ship.findHighBounds();
						} else {
							p = new Point(ship.imageRect.x, ship.imageRect.y);
							pt = new Point(ship.imageRect.width, ship.imageRect.height);
						}
						pt.x = (p.x+pt.x)/2;
						pt.y = (p.y+pt.y)/2;
						
						if (shieldImage != null && !shieldImage.isDisposed() && loadShield) {
							if (ship.isPlayer) {
								// that's pretty alright
								//e.gc.drawImage(shieldImage, pt.x+ship.ellipse.x-shieldImage.getBounds().width/2, pt.y+ship.ellipse.y-shieldImage.getBounds().height/2);
								e.gc.drawImage(shieldImage, shieldEllipse.x, shieldEllipse.y);
							} else {
								e.gc.drawImage(shieldImage,  0, 0, shieldImage.getBounds().width, shieldImage.getBounds().height,
										shieldEllipse.x,
										shieldEllipse.y,
										shieldEllipse.width, shieldEllipse.height);
								/*
								// it appears that there's some sort of arbitrary offset, no idea where it comes from, but now
								// everything is showing up correctly both in the editor and the game.
								e.gc.drawImage(shieldImage, 0, 0, shieldImage.getBounds().width, shieldImage.getBounds().height,
										pt.x+ship.ellipse.x-ship.ellipse.width,
										pt.y+ship.ellipse.y-ship.ellipse.height + 110,
										ship.ellipse.width*2, ship.ellipse.height*2);
										*/
							}
							e.gc.drawPoint(pt.x, pt.y);
						} else {
							c = e.display.getSystemColor(SWT.COLOR_DARK_BLUE);
							e.gc.setBackground(c);
							e.gc.setAlpha(36);
							e.gc.fillOval(pt.x+ship.ellipse.x-ship.ellipse.width, pt.y+ship.ellipse.y-ship.ellipse.height, ship.ellipse.width*2, ship.ellipse.height*2);
							e.gc.setAlpha(255);
							c.dispose();
						}
					}
					
				// === DRAW ALREADY PLACED ROOMS (INSIDES)
					// drawn separetely, so that room borders are drawn over the grid lines, looks nicer that way.
					if (showRooms) {
						e.gc.setAlpha(255);
						for (FTLRoom rm : ship.rooms) {
							c = new Color(e.display, 230, 225, 220);
							e.gc.setBackground(c);
							c.dispose();
							// draw regular room color
							e.gc.fillRectangle(rm.rect.x, rm.rect.y, rm.rect.width, rm.rect.height);
							// draw room images
							if (loadSystem && !ShipIO.isNull(rm.img) && rm.sysImg != null && !rm.sysImg.isDisposed()) {
								e.gc.drawImage(rm.sysImg, 0, 0, rm.sysImg.getBounds().width, rm.sysImg.getBounds().height, rm.rect.x, rm.rect.y, rm.rect.width, rm.rect.height);
							}
							// draw start-enabled overlay
							if (!ship.startMap.get(rm.sys) && !rm.sys.equals(Systems.EMPTY)) {
								c = e.display.getSystemColor(SWT.COLOR_DARK_RED);
								e.gc.setBackground(c);
								c.dispose();
								e.gc.setAlpha(128);
								e.gc.fillRectangle(rm.rect.x, rm.rect.y, rm.rect.width, rm.rect.height);
								e.gc.setAlpha(255);
							}
							// draw slot overlay
							if (rm.slot != -2) {
								e.gc.setAlpha(192);
								c = e.display.getSystemColor(SWT.COLOR_DARK_CYAN);
								e.gc.setBackground(c);
								if (!rm.sys.equals(Systems.MEDBAY)) {
									e.gc.fillRectangle(getStationDirected(rm));
								} else {
									e.gc.fillRectangle(getRectFromStation(rm));
								}
								c.dispose();
								e.gc.setAlpha(255);
							}
							// draw room id
							if (debug) {
								e.gc.drawString(""+rm.id, rm.rect.x+5, rm.rect.y+rm.rect.height-17, true);
							}
						}
					}
					
				// === ROOM CREATION TOOL
					if (tltmRoom.getSelection() && onCanvas && !modShift && phantomRect != null && leftMouseDown) {
						int signX = (phantomRect.x >= mousePos.x) ? (-1) : (0);
						int signY = (phantomRect.y >= mousePos.y) ? (-1) : (0);
						e.gc.setAlpha(255);
						e.gc.setLineWidth(3);
						
						allowRoomPlacement = (mousePosLastClick.x > ship.anchor.x && mousePosLastClick.y > ship.anchor.y && mousePos.x > ship.anchor.x+1 && mousePos.y > ship.anchor.y+1 && !doesRectOverlap(fixRect(phantomRect), null));
						if (allowRoomPlacement) {
							c = e.display.getSystemColor(SWT.COLOR_GREEN);
							e.gc.setForeground(c);
							c.dispose();
							c = new Color(e.display, 230, 225, 220);
							e.gc.setBackground(c);
							e.gc.fillRectangle(phantomRect);
							c.dispose();
						} else {
							c = e.display.getSystemColor(SWT.COLOR_RED);
							e.gc.setForeground(c);
							c.dispose();
						}
						e.gc.drawRectangle(phantomRect.x + 2*(signX+1), phantomRect.y + 2*(signY+1), phantomRect.width - 4*(signX+1), phantomRect.height - 4*(signY+1));
					}
					
				// === DRAW GRID
					c = e.display.getSystemColor(SWT.COLOR_DARK_GRAY);
					e.gc.setForeground(c);
					c.dispose();
					e.gc.setAlpha(255);
					e.gc.setLineWidth(1);
					
					for (int i=0; i <= GRID_W; i++)
						e.gc.drawLine(i*35, 0, i*35, canvas.getSize().y);
					for (int i=0; i <= GRID_H; i++)
						e.gc.drawLine(0, i*35, canvas.getSize().x, i*35);
					
				// === DRAW ALREADY PLACED ROOMS (BORDERS) AND SYSTEM ICONS
					if (showRooms) {
						e.gc.setAlpha(255);
						for (FTLRoom rm : ship.rooms) {
							e.gc.setLineWidth(4);
							c = e.display.getSystemColor(SWT.COLOR_BLACK);
							e.gc.setForeground(c);
							c.dispose();
							e.gc.drawRectangle(rm.rect.x, rm.rect.y, rm.rect.width, rm.rect.height);
							if (rm.sys != Systems.EMPTY) {
								e.gc.drawImage(systemsMap.get(rm.sys), rm.rect.x+(rm.rect.width-32)/2, rm.rect.y+(rm.rect.height-32)/2);
							}
						}
						
				// === DRAW ALREADY PLACED DOORS
						e.gc.setLineWidth(1);
						for (FTLDoor dr : ship.doors) {
							dr.drawDoor(e);
						}
					}
					
				// === DRAW ALREADY PLACES MOUNTS
					if (showMounts) {
						for (FTLMount m : ship.mounts) {
							c = (selectedMount == m) ? e.display.getSystemColor(SWT.COLOR_MAGENTA) : e.display.getSystemColor(SWT.COLOR_YELLOW);
							e.gc.setForeground(c);
							c.dispose();
							c = (selectedMount == m) ? e.display.getSystemColor(SWT.COLOR_DARK_MAGENTA) : e.display.getSystemColor(SWT.COLOR_DARK_YELLOW);
							e.gc.setBackground(c);
							c.dispose();
							
							e.gc.setLineWidth(2);
							e.gc.setAlpha(255);
							e.gc.drawRectangle(m.rect);
							e.gc.setAlpha(192);
							e.gc.fillRectangle(m.rect);

							e.gc.setAlpha(255);
							c = (selectedMount == m) ? e.display.getSystemColor(SWT.COLOR_YELLOW) : e.display.getSystemColor(SWT.COLOR_DARK_MAGENTA);
							e.gc.setForeground(c);
							c.dispose();
							FTLMount.drawDirection(e, m.slide, m.rect);
							
							e.gc.setLineWidth(1);
							c = e.display.getSystemColor(SWT.COLOR_YELLOW);
							e.gc.setForeground(c);
							c.dispose();
							c = e.display.getSystemColor(SWT.COLOR_DARK_MAGENTA);
							e.gc.setBackground(c);
							c.dispose();
							FTLMount.drawMirror(e, m.rotate, m.mirror, m.rect);
						}
					}
					

				// === DRAW ROOM & DOOR HIGHLIGHT FOR TOOLS
					if (onCanvas && canvasActive) {
						if (showMounts)
							tempMount = getMountFromMouse();
						if (showRooms) {
							tempRoom = getRectFromMouse();
							tempDoor = getDoorFromMouse();
							tempFTLRoom = getRoomContainingRect(tempRoom);
						}
						
						// === pointer tool highlights
						if (tltmPointer.getSelection() && !moveAnchor) {

							if (highlightColor != null && !highlightColor.isDisposed())
								highlightColor.dispose();
							highlightColor = e.display.getSystemColor(SWT.COLOR_BLUE);
							e.gc.setForeground(highlightColor);
							e.gc.setAlpha(255);
							
							// door highlight
							if (showRooms && tempDoor != null && !moveSelected && !resizeSelected) {
								e.gc.setLineWidth(3);
								e.gc.drawRectangle(tempDoor);
								
							// mount highlight
							} else if (showMounts && tempMount != null && !moveSelected && !resizeSelected) {
								e.gc.setLineWidth(3);
								e.gc.drawRectangle(tempMount.rect);
								
							// highlight already placed rooms
							} else if (showRooms && tempRoom != null && tempFTLRoom != null && !moveSelected && !resizeSelected) {
								e.gc.setLineWidth(4);
								e.gc.drawRectangle(tempFTLRoom.rect.x+2, tempFTLRoom.rect.y+2, tempFTLRoom.rect.width-4, tempFTLRoom.rect.height-4);
								
							// tile highlight (empty grid cells)
							} else if (tempRoom != null && !moveSelected && !resizeSelected) { 
								e.gc.setLineWidth(2);
								e.gc.drawRectangle(tempRoom.x+1, tempRoom.y+1, 34, 34);
							}
							
						// === room creation tool highlight - colored outline
						} else if (tltmRoom.getSelection() && !leftMouseDown) {
							if (!modShift && tempRoom != null) {
								if (!inBounds || doesRectOverlap(tempRoom, null)) {
									c = e.display.getSystemColor(SWT.COLOR_RED);
									e.gc.setForeground(c);
									c.dispose();
									allowRoomPlacement = false;
								} else {
									c = e.display.getSystemColor(SWT.COLOR_GREEN);
									e.gc.setForeground(c);
									c.dispose();
									allowRoomPlacement = true;
								}
								e.gc.setLineWidth(2);
								e.gc.drawRectangle(tempRoom.x, tempRoom.y, 35, 35);
								
							// === room splitting
							} else if (modShift && tempDoor != null) {
								tempFTLRoom = getRoomContainingRect(tempDoor);
								if (tempFTLRoom != null) {
									e.gc.setAlpha(255);
									e.gc.setLineWidth(3);
									if (!inBounds || isDoorAtWall(tempDoor)) {
										c = e.display.getSystemColor(SWT.COLOR_RED);
										e.gc.setForeground(c);
										c.dispose();
										allowRoomPlacement = false;
									} else {
										c = e.display.getSystemColor(SWT.COLOR_GREEN);
										e.gc.setForeground(c);
										c.dispose();
										allowRoomPlacement = true;
										parseRoom = tempFTLRoom;
										parseRect = tempDoor;
									}
									if (tempDoor.width == 31) {
										e.gc.drawRectangle(tempFTLRoom.rect.x, tempDoor.y+1, tempFTLRoom.rect.width, 4);
									} else {
										e.gc.drawRectangle(tempDoor.x+1, tempFTLRoom.rect.y, 4, tempFTLRoom.rect.height);
									}
								}
							}
							
						// === door creation tool highlight
						} else if (tltmDoor.getSelection() && !leftMouseDown && tempDoor != null) {
							e.gc.setForeground(highlightColor);
							e.gc.setBackground(highlightColor);
							e.gc.setAlpha(64);
							e.gc.fillRectangle(tempDoor);
							e.gc.setAlpha(255);
							e.gc.setLineWidth(2);
							e.gc.drawRectangle(tempDoor);
							
						}
					}
					
				// === DRAW SELECTION INDICATORS
						
					// weapon mount selection
					if (showMounts && selectedMount != null) {
						// handled by part drawing already placed mounts, thata way /\
					// door selection
					} else if (showRooms && selectedDoor != null) {
						c = e.display.getSystemColor(SWT.COLOR_BLUE);
						e.gc.setBackground(c);
						c.dispose();
						c = e.display.getSystemColor(SWT.COLOR_BLUE);
						e.gc.setForeground(c);
						c.dispose();
						e.gc.setAlpha(128);
						e.gc.fillRectangle(selectedDoor.rect.x-2, selectedDoor.rect.y-2, selectedDoor.rect.width+4, selectedDoor.rect.height+4);
						
						e.gc.setAlpha(196);
						e.gc.setLineWidth(2);
						e.gc.drawRectangle(selectedDoor.rect);
						
					// room selection
					} else if (showRooms && selectedRoom != null) {
						e.gc.setAlpha(255);
						e.gc.setLineWidth(2);
						c = e.display.getSystemColor(SWT.COLOR_DARK_BLUE);
						e.gc.setBackground(c);
						c.dispose();
						c = e.display.getSystemColor(SWT.COLOR_DARK_BLUE);
						e.gc.setForeground(c);
						c.dispose();
						e.gc.drawRectangle(selectedRoom.rect.x+1, selectedRoom.rect.y+1, selectedRoom.rect.width-2, selectedRoom.rect.height-2);
						for (int i=0; i<4; i++)
							e.gc.fillRectangle(corners[i]);
						
						c = e.display.getSystemColor(SWT.COLOR_BLUE);
						e.gc.setBackground(c);
						c.dispose();
						e.gc.setAlpha(64);
						e.gc.fillRectangle(selectedRoom.rect);
						
					// hull selection
					} else if (showHull && hullSelected) {
						e.gc.setAlpha(255);
						e.gc.setLineWidth(3);
						c = e.display.getSystemColor(SWT.COLOR_BLUE);
						e.gc.setForeground(c);
						c.dispose();
						e.gc.drawRectangle(ship.imageRect);
					
					// shield selection
					} else if (showHull && shieldSelected) {
						e.gc.setAlpha(255);
						e.gc.setLineWidth(3);
						c = e.display.getSystemColor(SWT.COLOR_BLUE);
						e.gc.setForeground(c);
						c.dispose();
						e.gc.drawRectangle(shieldEllipse);
						if (!ship.isPlayer) {
							e.gc.setLineWidth(2);
							c = e.display.getSystemColor(SWT.COLOR_CYAN);
							e.gc.setBackground(c);
							c.dispose();
							c = e.display.getSystemColor(SWT.COLOR_BLACK);
							e.gc.setForeground(c);
							c.dispose();
							if (tempRoom == null) tempRoom = new Rectangle(0,0,0,0);
							tempRoom.x = shieldEllipse.x + shieldEllipse.width-15;
							tempRoom.y = shieldEllipse.y + shieldEllipse.height-15;
							tempRoom.width = 15;
							tempRoom.height = 15;
							e.gc.fillRectangle(tempRoom);
							e.gc.drawRectangle(tempRoom);
						}
					}
					
				// === DOOR CREATION TOOL
					if (tltmDoor.getSelection()) {
						if (highlightColor != null && !highlightColor.isDisposed())
							highlightColor.dispose();
						highlightColor = e.display.getSystemColor(SWT.COLOR_RED);
						phantomRect = getDoorFromMouse();
						if (phantomRect != null && isDoorAtWall(phantomRect) && wallToDoor(getDoorFromMouse()) == null) {
							if (highlightColor != null && !highlightColor.isDisposed())
								highlightColor.dispose();
							highlightColor = e.display.getSystemColor(SWT.COLOR_GREEN);
							parseRect = (leftMouseDown) ? phantomRect : null;
						}
					}
					
				// === WEAPON MOUNTING TOOL
					if (tltmMount.getSelection()) {
						e.gc.setLineWidth(2);
						e.gc.setAlpha(255);
						if (mountToolHorizontal) {
							phantomRect = new Rectangle(mousePos.x-FTLMount.MOUNT_WIDTH/2, mousePos.y-FTLMount.MOUNT_HEIGHT/2, FTLMount.MOUNT_WIDTH, FTLMount.MOUNT_HEIGHT);
						} else {
							phantomRect = new Rectangle(mousePos.x-FTLMount.MOUNT_HEIGHT/2, mousePos.y-FTLMount.MOUNT_WIDTH/2, FTLMount.MOUNT_HEIGHT, FTLMount.MOUNT_WIDTH);
						}
						if ((!mountToolSlide.equals(Slide.NO) && ship.mounts.size()==Main.ship.weaponSlots) || (ship.mounts.size()==Main.ship.weaponSlots+1)) {
							c = e.display.getSystemColor(SWT.COLOR_RED);
							e.gc.setForeground(c);
							c.dispose();
						} else {
							c = e.display.getSystemColor(SWT.COLOR_YELLOW);
							e.gc.setForeground(c);
							c.dispose();
						}

						e.gc.setAlpha(255);
						e.gc.drawRectangle(phantomRect);
						
						c = e.display.getSystemColor(SWT.COLOR_DARK_MAGENTA);
						e.gc.setForeground(c);
						c.dispose();
						FTLMount.drawDirection(e, mountToolSlide, phantomRect);
						
						e.gc.setLineWidth(1);
						c = e.display.getSystemColor(SWT.COLOR_YELLOW);
						e.gc.setForeground(c);
						c.dispose();
						FTLMount.drawMirror(e, mountToolHorizontal, mountToolMirror, phantomRect);
						phantomRect = null;
					}

				// === SYSTEM OPERATING STATION TOOL
					 if (tltmSystem.getSelection()) {
						tempRoom = getRectFromMouse();
						
						if (tempRoom != null) {
							tempFTLRoom = getRoomContainingRect(tempRoom);
							if (leftMouseDown || rightMouseDown) {
								parseRoom = tempFTLRoom;
								
								parseRect = new Rectangle(0,0,0,0);
								parseRect.x = tempRoom.x;
								parseRect.y = tempRoom.y;
								parseRect.width = tempRoom.width;
								parseRect.height = tempRoom.height;
							} else {
								if (tempFTLRoom != null && (!tempRoom.intersects(getRectFromStation(tempFTLRoom)) || tempFTLRoom.slot == -2)
										&& (tempFTLRoom.sys.equals(Systems.PILOT) || tempFTLRoom.sys.equals(Systems.SHIELDS) || tempFTLRoom.sys.equals(Systems.WEAPONS)
												|| tempFTLRoom.sys.equals(Systems.ENGINES) || tempFTLRoom.sys.equals(Systems.MEDBAY))) {
									c = e.display.getSystemColor(SWT.COLOR_GREEN);
									e.gc.setForeground(c);
									c.dispose();
								} else {
									c = e.display.getSystemColor(SWT.COLOR_RED);
									e.gc.setForeground(c);
									c.dispose();
								}
								
								e.gc.setLineWidth(2);
								e.gc.setAlpha(255);
								e.gc.drawRectangle(tempRoom.x, tempRoom.y, 35, 35);
							}
						}
					}

				// === DRAW SHIP ANCHOR
					if (ship != null && canvasActive && showAnchor) {
						if (ship.vertical != 0) {
							e.gc.setAlpha(196);
							e.gc.setLineWidth(2);
							c = e.display.getSystemColor(SWT.COLOR_BLUE);
							e.gc.setForeground(c);
							c.dispose();
							e.gc.drawLine(0, ship.anchor.y-ship.vertical, GRID_W*35, ship.anchor.y-ship.vertical);
						}
						ship.drawShipAnchor(e);
					}
					
					if (phantomRect != null && debug) {
						e.gc.setLineWidth(2);
						c = e.display.getSystemColor(SWT.COLOR_DARK_MAGENTA);
						e.gc.setForeground(c);
						c.dispose();
						e.gc.setAlpha(128);
						e.gc.drawRectangle(phantomRect);
					}
					
				// === Pinned indicator
					e.gc.setAlpha(255);
					if (selectedRoom != null && selectedRoom.pinned) // room pin indicator
						e.gc.drawImage(pinImage, selectedRoom.rect.x+3, selectedRoom.rect.y+3);
					if (selectedDoor != null && selectedDoor.pinned) // door pin indicator
						e.gc.drawImage(pinImage, (selectedDoor.horizontal) ? selectedDoor.rect.x+8 : selectedDoor.rect.x+7, (selectedDoor.horizontal) ? selectedDoor.rect.y-17 : selectedDoor.rect.y+8);
					if (selectedMount != null && selectedMount.pinned) // mount pin indicator
						e.gc.drawImage(pinImage, selectedMount.rect.x-16, selectedMount.rect.y);
					if (hullSelected && ship.hullPinned) // hull pin indicator
						e.gc.drawImage(pinImage, ship.imageRect.x+5, ship.imageRect.y+5);
					if (shieldSelected && ship.shieldPinned) // shield pin indicator
						e.gc.drawImage(pinImage, shieldEllipse.x+5, shieldEllipse.y+5);
					
					
					// === Warnings 
					/*
					if (ship.isPlayer) {
						for (FTLRoom room : ship.rooms) {
							if ((room!=null) && ((room.rect.width > 70) || (room.rect.height > 70))) {
								c = e.display.getSystemColor(SWT.COLOR_RED);
								e.gc.setForeground(c);
								c.dispose();
								c = e.display.getSystemColor(SWT.COLOR_WHITE);
								e.gc.setBackground(c);
								c.dispose();
								Font font = new Font(shell.getDisplay(), "Helvetica", 10, SWT.BOLD);
								e.gc.setFont(font);
								e.gc.setAlpha(255);
								e.gc.drawString("WARNING: Player ships cannot have rooms bigger than 2x2. The game will crash.", 10, 10, false);
								font.dispose();
								break;
							}
						}
					}
					*/
					
				} else {
					c = e.display.getSystemColor(SWT.COLOR_WHITE);
					e.gc.setForeground(c);
					c.dispose();
					Font font = new Font(shell.getDisplay(), "Helvetica", 12, SWT.BOLD);
					e.gc.setFont(font);
					String s = "No ship is loaded. Use the file menu to create a new ship or load an existing one.";
					p = e.gc.stringExtent(s);
					e.gc.drawString(s, (GRID_W*35-p.x)/2, 100, true);
					font.dispose();
				}
			}
		});
		
				
	// === BOOKMARK: MOUSE MOVE
		
		canvas.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				int x = 0, y = 0;

				onCanvas = true;
				
				mousePos.x = e.x;
				mousePos.y = e.y;
				
			// === MOVE
				if (tltmPointer.getSelection() && moveSelected && canvasActive) {
					
					// === Move weapon mounts
					if (selectedMount != null && !selectedMount.pinned) {
						if ((modShift || !leftMouseDown) && dragRoomAnchor.x == 0 && dragRoomAnchor.y == 0) {
							dragRoomAnchor.x = selectedMount.rect.x + selectedMount.rect.width/2;
							dragRoomAnchor.y = selectedMount.rect.y + selectedMount.rect.height/2;
						}
						if (!modShift) {
							if (dragRoomAnchor.x != 0 && dragRoomAnchor.y != 0) {
								dragRoomAnchor.x = 0;
								dragRoomAnchor.y = 0;
								selectedMount.rect.x = (int)(dragRoomAnchor.x + ((-dragRoomAnchor.x + mousePos.x)/10) - ship.offset.x*35 - selectedMount.rect.width/2);
								selectedMount.rect.y = (int)(dragRoomAnchor.y + ((-dragRoomAnchor.y + mousePos.y)/10) - ship.offset.y*35 - selectedMount.rect.height/2);
							} else {
								selectedMount.rect.x = mousePos.x - selectedMount.rect.width/2;
								selectedMount.rect.y = mousePos.y - selectedMount.rect.height/2;
							}
						} else {
							selectedMount.rect.x = (int)(dragRoomAnchor.x + (-dragRoomAnchor.x + mousePos.x)/10 - selectedMount.rect.width/2);
							selectedMount.rect.y = (int)(dragRoomAnchor.y + (-dragRoomAnchor.y + mousePos.y)/10 - selectedMount.rect.height/2);
						}
						
						canvas.redraw();
		
					// === Move door
					} else if (selectedDoor != null && !selectedDoor.pinned) {
						phantomRect = new Rectangle(selectedDoor.rect.x, selectedDoor.rect.y, selectedDoor.rect.width, selectedDoor.rect.height);
						x = e.x;
						y = e.y;
						
						phantomRect.x = Math.round(x / 35) * 35 + ((selectedDoor.horizontal) ? (2) : (-3));
						phantomRect.y = Math.round(y / 35) * 35 + ((selectedDoor.horizontal) ? (-3) : (2));
						if (x >= ship.anchor.x && x + selectedDoor.rect.width < GRID_W * 35 + 35 && wallToDoor(phantomRect) == null && isDoorAtWall(phantomRect)) {
							selectedDoor.rect.x = phantomRect.x;
						}
						if (y >= ship.anchor.y && y + selectedDoor.rect.height < GRID_H * 35 + 35 && wallToDoor(phantomRect) == null && isDoorAtWall(phantomRect)) {
							selectedDoor.rect.y = phantomRect.y;
						}
						
						canvas.redraw();
						
					// === Move room
					} else if (selectedRoom != null && !selectedRoom.pinned) {
						phantomRect = new Rectangle(selectedRoom.rect.x, selectedRoom.rect.y, selectedRoom.rect.width, selectedRoom.rect.height);
						x = e.x - dragRoomAnchor.x;
						y = e.y - dragRoomAnchor.y;
						
						phantomRect.x = roundToGrid(x);
						phantomRect.y = roundToGrid(y);
						
						phantomRect.x = (phantomRect.x+phantomRect.width < GRID_W*35+35) ? (phantomRect.x) : (selectedRoom.rect.x);
						phantomRect.y = (phantomRect.y+phantomRect.height < GRID_H*35+35) ? (phantomRect.y) : (selectedRoom.rect.y);
						
						phantomRect.width =  (phantomRect.x < ship.anchor.x)
												? (phantomRect.width + (ship.anchor.x - phantomRect.x))
												: (selectedRoom.rect.width);
												
						phantomRect.height = (phantomRect.y < ship.anchor.y)
												? (phantomRect.height + (ship.anchor.y - phantomRect.y))
												: (selectedRoom.rect.height);
						
						if (!doesRectOverlap(phantomRect, selectedRoom.rect)) {
							selectedRoom.rect.x = (x >= ship.anchor.x)
													? ((x + selectedRoom.rect.width < GRID_W * 35 + 35)
														? (phantomRect.x)
														: (selectedRoom.rect.x))
													: (ship.anchor.x);
							selectedRoom.rect.y = (y >= ship.anchor.y)
													? ((y + selectedRoom.rect.height < GRID_H * 35 + 35)
														? (phantomRect.y)
														: (selectedRoom.rect.y))
													: (ship.anchor.y);
						}
						updateCorners(selectedRoom);
						
						removeUnalignedDoors();
						
						canvas.redraw();
						
					// === Move ship hull
					} else if (hullSelected && !ship.hullPinned) {
						if (!modShift) {
							ship.imageRect.x = e.x - dragRoomAnchor.x;
							ship.imageRect.y = e.y - dragRoomAnchor.y;
						} else {
							ship.imageRect.x = phantomRect.width + (int)((e.x - phantomRect.x)/10);
							ship.imageRect.y = phantomRect.height + (int)((e.y - phantomRect.y)/10);
						}
						canvas.redraw();
						
					// === Move shield
					} else if (shieldSelected && !ship.shieldPinned) {
						if (!modShift) {
							shieldEllipse.x = e.x - dragRoomAnchor.x;
							shieldEllipse.y = e.y - dragRoomAnchor.y;
						} else {
							shieldEllipse.x = phantomRect.width + (e.x - dragRoomAnchor.x)/10;
							shieldEllipse.y = phantomRect.height + (e.y - dragRoomAnchor.y)/10;
						}
						ship.ellipse.x = (shieldEllipse.x + shieldEllipse.width/2) - (ship.findLowBounds().x + ship.computeShipSize().x/2);
						ship.ellipse.y = (shieldEllipse.y + shieldEllipse.height/2) - (ship.findLowBounds().y + ship.computeShipSize().y/2) - ((ship.isPlayer) ? 0 : 110);
						ship.ellipse.width = shieldEllipse.width/2;
						ship.ellipse.height = shieldEllipse.height/2;
							
						canvas.redraw();
					}
						
			// === RESIZE
				// === Room
				} else if (tltmPointer.getSelection() && resizeSelected && selectedRoom != null && canvasActive && !selectedRoom.pinned) {
					phantomRect = new Rectangle(selectedRoom.rect.x, selectedRoom.rect.y, selectedRoom.rect.width, selectedRoom.rect.height);
		
					x = e.x - dragRoomAnchor.x;
					y = e.y - dragRoomAnchor.y;
						
					x = (int) ((x > 0) ? roundToGrid(x)+35 : roundToGrid(x)-35);
					y = (int) ((y > 0) ? roundToGrid(y)+35 : roundToGrid(y)-35);
						
						
					if (dragRoomAnchor.equals(((FTLRoom) selectedRoom).corners[0])) {
						phantomRect.width = x;
						phantomRect.height = y;
					} else if (dragRoomAnchor.equals(((FTLRoom) selectedRoom).corners[1])) {
						phantomRect.x = dragRoomAnchor.x+x;
						phantomRect.width = -x;
						phantomRect.height = y;
					} else if (dragRoomAnchor.equals(((FTLRoom) selectedRoom).corners[2])) {
						phantomRect.width = x;
						phantomRect.height = -y;
						phantomRect.y = dragRoomAnchor.y+y;
					} else if (dragRoomAnchor.equals(((FTLRoom) selectedRoom).corners[3])) {
						phantomRect.x = dragRoomAnchor.x+x;
						phantomRect.y = dragRoomAnchor.y+y;
						phantomRect.width = -x;
						phantomRect.height = -y;
					}
		
					phantomRect = fixRect(phantomRect);
					
					if (phantomRect.x >= ship.anchor.x && phantomRect.x+phantomRect.width < GRID_W*35+35 && !doesRectOverlap(phantomRect, selectedRoom.rect)) {
						selectedRoom.rect.x = phantomRect.x;
						selectedRoom.rect.width = (phantomRect.width == 0) ? 35 : phantomRect.width;
					}
					if (phantomRect.y >= ship.anchor.y && phantomRect.y+phantomRect.height < GRID_H * 35+35 && !doesRectOverlap(phantomRect, selectedRoom.rect)) {
						selectedRoom.rect.y = phantomRect.y;
						selectedRoom.rect.height = (phantomRect.height == 0) ? 35 : phantomRect.height;
					}
					
					updateCorners(selectedRoom);
					
					removeUnalignedDoors();
					
					canvas.redraw();
					
				// === Shield
				} else if (shieldSelected && !ship.shieldPinned && resizeSelected) {
					shieldEllipse.width = e.x - shieldEllipse.x;
					shieldEllipse.height = e.y - shieldEllipse.y;
					
			// === MOVE ANCHOR
				} else if (moveAnchor && canvasActive) {
					if (leftMouseDown) {
						Point p = ship.computeShipSize();
						Point low = ship.findLowBounds();
						Point a;
						x = downToGrid(e.x);
						y = downToGrid(e.y);
						
						if (e.x >= 0 && (x + p.x + low.x - ship.anchor.x) <= GRID_W*35 && e.x < GRID_W*35+35) {
							if (!modShift) {
								a = new Point(x, ship.anchor.y);
								ship.updateElements(a, FTLShip.AxisFlag.X);
								ship.anchor.x = x;
							} else if (e.x < low.x+35) {
								ship.anchor.x = x;
								ship.offset.x = (ship.findLowBounds().x - ship.anchor.x) / 35;
							}
						}
						if (e.y >= 0 && (y + p.y + low.y - ship.anchor.y) <= GRID_H*35 && e.y < GRID_H*35+35) {
							if (!modShift) {
								a = new Point(ship.anchor.x, y);
								ship.updateElements(a, FTLShip.AxisFlag.Y);
								ship.anchor.y = y;
							} else if (e.y < low.y+35) {
								ship.anchor.y = y;
								ship.offset.y = (ship.findLowBounds().y - ship.anchor.y) / 35;
							}
						}
					} else if (rightMouseDown) {
						ship.vertical = ship.anchor.y-e.y;
					}

					canvas.redraw();
					
			// === ROOM CREATION (DRAGGING)
				} else if (tltmRoom.getSelection() && phantomRect != null && leftMouseDown) {
		
					phantomRect.x = downToGrid(mousePosLastClick.x) + ((e.x > mousePosLastClick.x) ? (0) : (35));
					phantomRect.y = downToGrid(mousePosLastClick.y) + ((e.y > mousePosLastClick.y) ? (0) : (35));
					
					x = ((e.x > mousePosLastClick.x)
							? Math.min(GRID_W * 35 - phantomRect.x, upToGrid(e.x - phantomRect.x)+35)
							: upToGrid(mousePos.x - phantomRect.x)-35);
					y = ((e.y > mousePosLastClick.y)
							? Math.min(GRID_H * 35 - phantomRect.y, upToGrid(e.y - phantomRect.y)+35)
							: upToGrid(mousePos.y - phantomRect.y)-35);
					
					phantomRect.width = x;
					phantomRect.height = y;
					
					canvas.redraw();
				}
				
				if (ship != null && (e.x >= ship.anchor.x && e.y >= ship.anchor.y)) {
					inBounds = true;
				} else {
					inBounds = false;
				}
			}
		});
		
		canvas.addMouseTrackListener(new MouseTrackListener() {
			public void mouseEnter(MouseEvent e) {
				onCanvas = true;
			}
			public void mouseExit(MouseEvent e) {
				onCanvas = false;
			}
			public void mouseHover(MouseEvent e) {
				// so that the canvas is not being redrawn if no changes are being made.
				onCanvas = false;
			}
		});


	// === BOOKMARK: MOUSE DOWN
		
		canvas.addMouseListener(new MouseListener() {
			@Override
			public void mouseDown(MouseEvent e) {
				mousePosLastClick.x = e.x;
				mousePosLastClick.y = e.y;
				
				if (e.button == 1)
					leftMouseDown = true;
				if (e.button == 3)
					rightMouseDown = true;
				
				onCanvas = true;
				
				if (canvasActive) {
					if (tltmPointer.getSelection() && showAnchor && (e.x >= ship.anchor.x-FTLShip.ANCHOR && ((ship.anchor.x == 0) ? (e.x <= FTLShip.ANCHOR) : ((e.x <= ship.anchor.x)))
							&& e.y >= ship.anchor.y-FTLShip.ANCHOR && ((ship.anchor.y == 0) ? (e.y <= FTLShip.ANCHOR) : ((e.y <= ship.anchor.y))))) {
						moveAnchor = true;
					}
					if (!moveAnchor && tltmPointer.getSelection() && onCanvas) {
						hullSelected = false;
						shieldSelected = false;
						
						// selection priorities; door > mount > room > hull
						// cheking if previous selection variable is null prevents selecting multiple objects at once.
						selectedDoor = (showRooms) ? wallToDoor(getDoorFromMouse()) : null;

						selectedMount = (showMounts && selectedDoor == null) ? getMountFromMouse() : null;
						
						selectedRoom = (showRooms && selectedMount == null && selectedDoor == null) ? getRoomContainingRect(getRectFromClick()) : null;
						
						if (showHull && selectedRoom == null && selectedDoor == null && selectedMount == null) {
							hullSelected = ship.imageRect.contains(mousePos) && leftMouseDown && !rightMouseDown;
							shieldSelected = shieldEllipse.contains(mousePos) && !leftMouseDown && rightMouseDown;
							
							if (phantomRect == null) phantomRect = new Rectangle(0,0,0,0);
							phantomRect.x = shieldEllipse.x + shieldEllipse.width-15;
							phantomRect.y = shieldEllipse.y + shieldEllipse.height-15;
							phantomRect.width = 15;
							phantomRect.height = 15;
							
							resizeSelected = shieldSelected && phantomRect.contains(mousePos);

							moveSelected = hullSelected || (shieldSelected && !resizeSelected);
							if (hullSelected) {
								dragRoomAnchor.x = e.x - ship.imageRect.x;
								dragRoomAnchor.y = e.y - ship.imageRect.y;
							}
							if (shieldSelected && moveSelected) {
								dragRoomAnchor.x = e.x - shieldEllipse.x;
								dragRoomAnchor.y = e.y - shieldEllipse.y;
							}
						}
						if (selectedRoom != null) {
							updateCorners(selectedRoom);
							selectedDoor = null;
						}
					}
					
					if (tltmPointer.getSelection() && onCanvas && e.button == 1) {
						if (selectedMount != null && selectedMount.rect.contains(mousePosLastClick)) {
							moveSelected = true;
						} if (selectedRoom != null && selectedRoom.rect.contains(mousePos)) {
							if (corners[0].contains(mousePosLastClick) || corners[1].contains(mousePosLastClick) || corners[2].contains(mousePosLastClick) || corners[3].contains(mousePosLastClick)) {
								dragRoomAnchor = findFarthestCorner((FTLRoom) selectedRoom, mousePosLastClick);
								resizeSelected = true;
							} else if (selectedRoom.rect.contains(mousePosLastClick)) {
								moveSelected = true;
								dragRoomAnchor.x = e.x - selectedRoom.rect.x;
								dragRoomAnchor.y = e.y - selectedRoom.rect.y;
							}
						} else if (selectedDoor != null && selectedDoor.rect.contains(mousePos)) {
							moveSelected = true;
						}
					} else if (tltmRoom.getSelection() && onCanvas) {
					   	phantomRect = getRectFromClick();
					} else if (e.button == 1) {
						moveSelected = false;
						resizeSelected = false;
					}
				}
				
				canvas.redraw();
			}
			
	// === BOOKMARK: MOUSE UP
			@Override
			public void mouseUp(MouseEvent e) {
				onCanvas = true;
				moveSelected = false;
				if (highlightColor != null && !highlightColor.isDisposed())
					highlightColor.dispose();
				highlightColor = shell.getDisplay().getSystemColor(SWT.COLOR_GREEN);
				if (canvasActive) {
					if (selectedRoom != null && resizeSelected) {
						selectedRoom.rect = fixRect(selectedRoom.rect);
					}
					
					if (tltmPointer.getSelection()) {
						if (selectedMount != null) {
							if (leftMouseDown && e.button == 1) {
								dragRoomAnchor.x = 0;
								dragRoomAnchor.y = 0;
							}
							if (((modShift && leftMouseDown) || !leftMouseDown) && onCanvas) {
								selectedMount.mirror = (modShift && e.button == 1) ? !selectedMount.mirror : selectedMount.mirror;
								selectedMount.slide = (modShift && e.button == 3)
										? ((selectedMount.slide.equals(Slide.UP))
											? (Slide.RIGHT)
											: (selectedMount.slide.equals(Slide.RIGHT))
												? (Slide.DOWN)
												: (selectedMount.slide.equals(Slide.DOWN))
													? (Slide.LEFT)
													: (selectedMount.slide.equals(Slide.LEFT))
														? (Slide.NO)
														: (selectedMount.slide.equals(Slide.NO))
															? (Slide.UP)
															: selectedMount.slide)
										: selectedMount.slide;
								if (!modShift && e.button==3) {
									selectedMount.rect.x += (selectedMount.rotate) ? (selectedMount.rect.width/2-selectedMount.rect.height/2) : (-selectedMount.rect.height/2+selectedMount.rect.width/2);
									selectedMount.rect.y += (selectedMount.rotate) ? (selectedMount.rect.height/2-selectedMount.rect.width/2) : (-selectedMount.rect.width/2+selectedMount.rect.height/2);
									selectedMount.rotate = (!modShift && e.button == 3) ? !selectedMount.rotate : selectedMount.rotate;
									selectedMount.rect.width = (selectedMount.rotate) ? (FTLMount.MOUNT_WIDTH) : (FTLMount.MOUNT_HEIGHT);
									selectedMount.rect.height = (selectedMount.rotate) ? (FTLMount.MOUNT_HEIGHT) : (FTLMount.MOUNT_WIDTH);
								}
							} else if (leftMouseDown && !modShift && e.button == 3 && onCanvas) { // reset the position
								ship.updateMount(selectedMount);
							}
							
							// === move weapon mounts (update actual pos)
							if (moveSelected && e.button != 3) {
								// if the weapon mount gets dragged off the screen, then don't update the actual position (the mount will revert to it's last position)
								// the below condition actually passes when the position IS to be updated.
								if (modShift || (e.x > 0 && e.y > 0 && e.x < GRID_W*35 && e.y < GRID_H*35)) {
									mountRect.x = selectedMount.rect.x + selectedMount.rect.width/2 + ((hullImage != null) ? (-ship.anchor.x - ship.offset.x*35) : (-ship.imageRect.x));
									mountRect.y = selectedMount.rect.y + selectedMount.rect.height/2 + ((hullImage != null) ? (-ship.anchor.y - ship.offset.y*35) : (-ship.imageRect.y));

									selectedMount.rect.x = (Main.hullImage != null) ? (ship.anchor.x + ship.offset.x*35 + Main.mountRect.x) : (Main.mountRect.x);
									selectedMount.rect.y = (Main.hullImage != null) ? (ship.anchor.y + ship.offset.y*35 + Main.mountRect.y) : (Main.mountRect.y);
										
									selectedMount.pos.x = selectedMount.rect.x - ship.imageRect.x;
									selectedMount.pos.y = selectedMount.rect.y - ship.imageRect.y;
									
									selectedMount.rect.x -= (selectedMount.rotate) ? (FTLMount.MOUNT_WIDTH/2) : (FTLMount.MOUNT_HEIGHT/2);
									selectedMount.rect.y -= (selectedMount.rotate) ? (FTLMount.MOUNT_HEIGHT/2) : (FTLMount.MOUNT_WIDTH/2);
								}
							}
						} else if (hullSelected && moveSelected && e.button == 1 && snapMountsToHull) {
							if (snapMountsToHull) {
								for (FTLMount m : ship.mounts) {
									mountRect.x = m.pos.x - ((m.rotate) ? (FTLMount.MOUNT_WIDTH/2) : (FTLMount.MOUNT_HEIGHT/2));
									mountRect.y = m.pos.y - ((m.rotate) ? (FTLMount.MOUNT_HEIGHT/2) : (FTLMount.MOUNT_WIDTH/2));
									
									m.rect.x = (hullImage != null) ? (ship.imageRect.x + mountRect.x) : (mountRect.x);
									m.rect.y = (hullImage != null) ? (ship.imageRect.y + mountRect.y) : (mountRect.y);
								}
							}
						}
					} else if (tltmRoom.getSelection() && onCanvas && e.button == 1 && phantomRect != null) {
						
							//	=== room creation
						if (!modShift && allowRoomPlacement) {
							parseRect = fixRect(phantomRect);
						
							FTLRoom r = new FTLRoom(parseRect);
	
							r.id = getLowestId();
							idList.add(r.id);
	
							ship.rooms.add(r);
							parseRect = null;
							phantomRect = null;
							
							if (ship.rooms.size() > 0) {
								btnShields.setEnabled(true);
								btnShields.setToolTipText(null);
							}
							
							// === room splitting
						} else if (modShift && parseRoom != null && parseRect != null) {
							FTLRoom r1 = null;
							parseRect.x = roundToGrid(parseRect.x)+35;
							parseRect.y = roundToGrid(parseRect.y)+35;
							if (parseRect.width == 31) {
								// horizontal
								r1 = new FTLRoom(parseRoom.rect.x, parseRoom.rect.y, parseRoom.rect.width, parseRect.y-parseRoom.rect.y);
								parseRoom.rect.height = parseRoom.rect.y+parseRoom.rect.height-parseRect.y;
								parseRoom.rect.y = parseRect.y;
							} else {
								// vertical
								r1 = new FTLRoom(parseRoom.rect.x, parseRoom.rect.y, parseRect.x - parseRoom.rect.x, parseRoom.rect.height);
								parseRoom.rect.width = parseRoom.rect.x+parseRoom.rect.width-parseRect.x;
								parseRoom.rect.x = parseRect.x;
							}
							parseRoom.sys = Systems.EMPTY;
							parseRoom = null;
							selectedRoom = null;
							
							ship.rooms.add(r1);
							r1.id = getLowestId();
							idList.add(r1.id);
							ship.reassignID();
						}
						
						// === door creation
					} else if (tltmDoor.getSelection() && onCanvas && parseRect != null && e.button == 1) {
						boolean horizontal = parseRect.height == 6;
						FTLDoor d = new FTLDoor(new Point(parseRect.x, parseRect.y), horizontal);
						
						ship.doors.add(d);
						parseRect = null;
						
						// === weapon mount creation
					} else if (tltmMount.getSelection() && onCanvas) {
						mountToolHorizontal = (!modShift && e.button == 3) ? ( (mountToolHorizontal) ? (false) : (true) ) : (mountToolHorizontal);
						mountToolSlide = (modShift && e.button == 3)
											? ((mountToolSlide.equals(Slide.UP))
												? (Slide.RIGHT)
												: (mountToolSlide.equals(Slide.RIGHT))
													? (Slide.DOWN)
													: (mountToolSlide.equals(Slide.DOWN))
														? (Slide.LEFT)
														: (mountToolSlide.equals(Slide.LEFT))
															? (Slide.NO)
															: (mountToolSlide.equals(Slide.NO))
																? (Slide.UP)
																: mountToolSlide)
											: mountToolSlide;
						mountToolMirror = (modShift && e.button == 1) ? !mountToolMirror : mountToolMirror ;
						
						if (((!mountToolSlide.equals(Slide.NO) && ship.mounts.size()<Main.ship.weaponSlots) || (mountToolSlide.equals(Slide.NO) && ship.mounts.size()<Main.ship.weaponSlots+1)) && e.button == 1 && !modShift) {
							FTLMount m = new FTLMount();

							m.rotate = mountToolHorizontal;
							m.mirror = mountToolMirror;
							m.gib = 0; // TODO gibs
							m.slide = mountToolSlide;
							m.rect.x = e.x + m.rect.width/2;
							m.rect.y = e.y + m.rect.height/2;
							
							mountRect = new Rectangle(0,0,0,0);
							mountRect.x = ((hullImage != null) ? (m.rect.x) : (m.rect.x));
							mountRect.y = ((hullImage != null) ? (m.rect.y) : (m.rect.y));
							
							m.pos.x = m.rect.x - ship.imageRect.x;
							m.pos.y = m.rect.y - ship.imageRect.y;

							m.rect.width = (m.rotate) ? (FTLMount.MOUNT_WIDTH) : (FTLMount.MOUNT_HEIGHT);
							m.rect.height = (m.rotate) ? (FTLMount.MOUNT_HEIGHT) : (FTLMount.MOUNT_WIDTH);
							m.rect.x -= (m.rotate) ? (FTLMount.MOUNT_WIDTH/2) : (FTLMount.MOUNT_HEIGHT/2);
							m.rect.y -= (m.rotate) ? (FTLMount.MOUNT_HEIGHT/2) : (FTLMount.MOUNT_WIDTH/2);
							
							ship.mounts.add(m);
						}
						
						// === operating slot creation tool
					} else if (tltmSystem.getSelection() && onCanvas && parseRect != null && parseRoom != null
							&& (parseRoom.sys.equals(Systems.PILOT) || parseRoom.sys.equals(Systems.SHIELDS) || parseRoom.sys.equals(Systems.WEAPONS)
									|| parseRoom.sys.equals(Systems.ENGINES) || parseRoom.sys.equals(Systems.MEDBAY))) {
						if (!modShift) {
							if (e.button == 1) {
								parseRoom.slot = getStationFromRect(parseRect);
							} else if (e.button == 3) {
								parseRoom.slot = -2;
							}
						} else if (e.button == 1 && parseRoom.slot != -2) {
							parseRoom.dir =((parseRoom.dir.equals(Slide.UP))
											? (Slide.RIGHT)
											: (parseRoom.dir.equals(Slide.RIGHT))
												? (Slide.DOWN)
												: (parseRoom.dir.equals(Slide.DOWN))
													? (Slide.LEFT)
													: (parseRoom.dir.equals(Slide.LEFT))
														? (Slide.UP)
														: (parseRoom.dir));
						}
					}
					
					parseRect = getRectFromMouse();
					if (!shieldSelected && selectedMount == null && selectedDoor == null && !hullSelected && e.button == 3 && tltmPointer.getSelection() && onCanvas && parseRect !=null  && doesRectOverlap(parseRect, null)) {
						menuSystem.setVisible(true);
						selectedRoom = getRoomContainingRect(parseRect);
						updateCorners(selectedRoom);
					}
				}
					
				parseRect = null;

				moveAnchor = false;
				moveSelected = false;
				resizeSelected = false;
				if (e.button == 1) {
					leftMouseDown = false;
				} else if (e.button == 3) {
					rightMouseDown = false;
				}
				
				canvas.redraw();
			}
			
			
			public void mouseDoubleClick(MouseEvent e) {
				if (canvasActive) {
					if (tltmPointer.getSelection()) {
							// open the room level and power editing dialog
						if (selectedRoom != null) {
							sysDialog.open();
						}
					}
				}
			}
		});
	
		
		
	// === SHELL
		
		shell.getDisplay().addFilter(SWT.KeyUp, new Listener() {
			public void handleEvent(Event e)
			{
            	if (modShift && (hullSelected || shieldSelected) && moveSelected) {
            		hullSelected = false;
            		shieldSelected = false;
            		moveSelected = false;
            	}
            	modShift = false;
            	if ((hullSelected || shieldSelected) && moveSelected) {
            		dragRoomAnchor.x = mousePos.x - ship.imageRect.x;
            		dragRoomAnchor.y = mousePos.y - ship.imageRect.y;
            	}
			}
		});
		
		shell.getDisplay().addFilter(SWT.KeyDown, new Listener() {
            public void handleEvent(Event e) {
            	if (!modShift) {
            		modShift = e.keyCode == SWT.SHIFT;
            		if (hullSelected && moveSelected) {
            			if (phantomRect == null) phantomRect = new Rectangle(0,0,0,0);
            			phantomRect.x = mousePos.x;
            			phantomRect.y = mousePos.y;
            			phantomRect.width = ship.imageRect.x;
            			phantomRect.height = ship.imageRect.y;
            		}
            		if (shieldSelected && moveSelected) {
            			if (phantomRect == null) phantomRect = new Rectangle(0,0,0,0);
            			phantomRect.x = mousePos.x;
            			phantomRect.y = mousePos.y;
            			phantomRect.width = shieldEllipse.x;
            			phantomRect.height = shieldEllipse.y;
            		}
            	}
            	
            	if (shell.isEnabled()) {
	            	if (canvasActive && (selectedMount != null || selectedRoom != null || selectedDoor != null) && (e.keyCode == SWT.DEL || (e.stateMask == SWT.SHIFT && e.keyCode == 'd'))) {
	            		if (ship.rooms.remove(selectedRoom)) {
	            			idList.remove(selectedRoom.id);
	            			selectedRoom = null;
	                		removeUnalignedDoors();
	    					ship.reassignID();
		            		canvas.redraw();
		            		if (ship.rooms.size() == 0) {
		            			btnShields.setEnabled(false);
		            			btnShields.setToolTipText("Shield is aligned in relation to rooms. Place a room before choosing shield graphic.");
		            		}
	            		} else if (ship.doors.remove(selectedDoor)) {
	            			selectedDoor = null;
	            			canvas.redraw();
	            		} else if (ship.mounts.remove(selectedMount)) {
	            			selectedMount = null;
	            			canvas.redraw();
	            		}
	            		
	            	} else if (e.keyCode == SWT.ESC) {
	            		selectedRoom = null;
	            		selectedDoor = null;
	            		selectedMount = null;
	            		canvas.redraw();
	            	} else if (e.stateMask == SWT.CTRL && e.keyCode == 's') {
	            		mntmSaveShip.notifyListeners(SWT.Selection, null);
	            	} else if (e.stateMask == SWT.CTRL && e.keyCode == 'n') {
	            		mntmNewShip.notifyListeners(SWT.Selection, null);
	            	} else if (e.stateMask == SWT.CTRL && e.keyCode == 'l') {
	            		mntmLoadShip.notifyListeners(SWT.Selection, null);
	            	} else if (e.stateMask == SWT.CTRL && e.keyCode == 'o') {
	            		mntmLoadShipProject.notifyListeners(SWT.Selection, null);
	            	} else if (e.stateMask == SWT.CTRL && e.keyCode == 'e' && mntmExport.getEnabled()) {
	            		mntmExport.notifyListeners(SWT.Selection, null);
	            	} else if (e.keyCode == '1') {
	            		showAnchor = (showAnchor) ? false : true;
	            		mntmShowAnchor.setSelection(showAnchor);
	            		canvas.redraw();
	            	} else if (e.keyCode == '2') {
	            		showMounts = (showMounts) ? false : true;
	            		mntmShowMounts.setSelection(showMounts);
	            		canvas.redraw();
	            	} else if (e.keyCode == '3') {
	            		showRooms = (showRooms) ? false : true;
	            		mntmShowRooms.setSelection(showRooms);
	            		canvas.redraw();
	            	} else if (e.keyCode == '4') {
	            		showHull = (showHull) ? false : true;
	            		mntmShowHull.setSelection(showHull);
	            		canvas.redraw();
	            	} else if (e.keyCode == '`') {
	            		if (selectedRoom != null) selectedRoom.pinned = !selectedRoom.pinned;
	            		if (selectedDoor != null) selectedDoor.pinned = !selectedDoor.pinned;
	            		if (selectedMount != null) selectedMount.pinned = !selectedMount.pinned;
	            		if (hullSelected) ship.hullPinned = !ship.hullPinned;
	            		if (shieldSelected) ship.shieldPinned = !ship.shieldPinned;
	            		canvas.redraw();
	            	}
            	}
            }
        });
		
		
	// === SIDEBAR
		
		btnShipProperties.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.setEnabled(false);
				shipDialog.open();
				
				shell.setEnabled(true);
				canvas.redraw();
			}
		});
		
		btnHull.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				String[] filterExtensions = new String[] {"*.png"};
				dialog.setFilterExtensions(filterExtensions);
				dialog.setFilterPath(resPath);
				String path = dialog.open();
				
				if (!ShipIO.isNull(path)) {
					Main.ship.imagePath = path;
					
					//Main.ship.cloakPath = path.substring(0, path.lastIndexOf('_')) + "_cloak.png";
					//Main.ship.floorPath = path.substring(0, path.lastIndexOf('_')) + "_floor.png";
					
					ShipIO.loadImage(path, "hull");
					canvas.redraw();
				}
			}
		});
		
		btnShields.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				String[] filterExtensions = new String[] {"*.png"};
				dialog.setFilterExtensions(filterExtensions);
				dialog.setFilterPath(resPath);
				String path = dialog.open();
				
				if (!ShipIO.isNull(path)) {
					Main.ship.shieldPath = path;
					
					ShipIO.loadImage(path, "shields");
					
					if (ship.isPlayer)
						if (shieldImage != null && !shieldImage.isDisposed()) {
							Rectangle temp = shieldImage.getBounds();
							shieldEllipse.x = ship.anchor.x + ship.offset.x*35 + ship.computeShipSize().x/2 - temp.width/2 + ship.ellipse.x;
							shieldEllipse.y = ship.anchor.y + ship.offset.y*35 + ship.computeShipSize().y/2 - temp.height/2 + ship.ellipse.y;
							shieldEllipse.width = temp.width;
							shieldEllipse.height = temp.height;
						}
					
					canvas.redraw();
				}
			}
		});
		
		btnCloak.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				String[] filterExtensions = new String[] {"*.png"};
				dialog.setFilterExtensions(filterExtensions);
				dialog.setFilterPath(resPath);
				String path = dialog.open();
				
				if (!ShipIO.isNull(path)) {
					Main.ship.cloakPath = path;
					
					ShipIO.loadImage(path, "cloak");
					canvas.redraw();
				}
			}
		});
		
		btnFloor.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				String[] filterExtensions = new String[] {"*.png"};
				dialog.setFilterExtensions(filterExtensions);
				dialog.setFilterPath(resPath);
				String path = dialog.open();
				
				if (!ShipIO.isNull(path)) {
					Main.ship.floorPath = path;
					
					ShipIO.loadImage(path, "floor");
					canvas.redraw();
				}
			}
		});
		
	// === SYSTEM CONTEXT MENU
		
		menuSystem.addMenuListener(new MenuAdapter() {
			@Override
			public void menuShown(MenuEvent e) {
				if (selectedRoom != null) {
					mntmEmpty.setSelection(selectedRoom.sys == Systems.EMPTY);
					// === subsystems
					mntmPilot.setSelection(selectedRoom.sys == Systems.PILOT);
					mntmPilot.setEnabled(!isSystemAssigned(Systems.PILOT, selectedRoom));
					mntmSensors.setSelection(selectedRoom.sys == Systems.SENSORS);
					mntmSensors.setEnabled(!isSystemAssigned(Systems.SENSORS, selectedRoom));
					mntmDoors.setSelection(selectedRoom.sys == Systems.DOORS);
					mntmDoors.setEnabled(!isSystemAssigned(Systems.DOORS, selectedRoom));
					// === systems
					mntmEngines.setSelection(selectedRoom.sys == Systems.ENGINES);
					mntmEngines.setEnabled(!isSystemAssigned(Systems.ENGINES, selectedRoom));
					mntmMedbay.setSelection(selectedRoom.sys == Systems.MEDBAY);
					mntmMedbay.setEnabled(!isSystemAssigned(Systems.MEDBAY, selectedRoom));
					mntmOxygen.setSelection(selectedRoom.sys == Systems.OXYGEN);
					mntmOxygen.setEnabled(!isSystemAssigned(Systems.OXYGEN, selectedRoom));
					mntmShields.setSelection(selectedRoom.sys == Systems.SHIELDS);
					mntmShields.setEnabled(!isSystemAssigned(Systems.SHIELDS, selectedRoom));
					mntmWeapons.setSelection(selectedRoom.sys == Systems.WEAPONS);
					mntmWeapons.setEnabled(!isSystemAssigned(Systems.WEAPONS, selectedRoom));
					// === special
					mntmArtillery.setSelection(selectedRoom.sys == Systems.ARTILLERY);
					mntmArtillery.setEnabled(!isSystemAssigned(Systems.ARTILLERY, selectedRoom));
					mntmCloaking.setSelection(selectedRoom.sys == Systems.CLOAKING);
					mntmCloaking.setEnabled(!isSystemAssigned(Systems.CLOAKING, selectedRoom));
					mntmDrones.setSelection(selectedRoom.sys == Systems.DRONES);
					mntmDrones.setEnabled(!isSystemAssigned(Systems.DRONES, selectedRoom));
					mntmTeleporter.setSelection(selectedRoom.sys == Systems.TELEPORTER);
					mntmTeleporter.setEnabled(!isSystemAssigned(Systems.TELEPORTER, selectedRoom));
					// ===
					mntmSysImage.setEnabled(!selectedRoom.sys.equals(Systems.EMPTY));
				}
			}
		});
		
		mntmEmpty.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectedRoom.sys = Systems.EMPTY;
				canvas.redraw(selectedRoom.rect.x, selectedRoom.rect.y, selectedRoom.rect.width, selectedRoom.rect.height, true);
				if (selectedRoom.sysImg != null && !selectedRoom.sysImg.isDisposed() && !ShipIO.loadingSwitch)
					selectedRoom.sysImg.dispose();
				canvas.redraw();
			} });
		mntmOxygen.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (!isSystemAssigned(Systems.OXYGEN, selectedRoom)) {
					selectedRoom.sys = Systems.OXYGEN;
					canvas.redraw(selectedRoom.rect.x, selectedRoom.rect.y, selectedRoom.rect.width, selectedRoom.rect.height, true);
				}
			} });
		mntmMedbay.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (!isSystemAssigned(Systems.MEDBAY, selectedRoom)) {
					selectedRoom.sys = Systems.MEDBAY;
					canvas.redraw(selectedRoom.rect.x, selectedRoom.rect.y, selectedRoom.rect.width, selectedRoom.rect.height, true);
				}
			} });
		mntmShields.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (!isSystemAssigned(Systems.SHIELDS, selectedRoom)) {
					selectedRoom.sys = Systems.SHIELDS;
					canvas.redraw(selectedRoom.rect.x, selectedRoom.rect.y, selectedRoom.rect.width, selectedRoom.rect.height, true);
				}
			} });
		mntmWeapons.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (!isSystemAssigned(Systems.WEAPONS, selectedRoom)) {
					selectedRoom.sys = Systems.WEAPONS;
					canvas.redraw(selectedRoom.rect.x, selectedRoom.rect.y, selectedRoom.rect.width, selectedRoom.rect.height, true);
				}
			} });
		mntmEngines.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (!isSystemAssigned(Systems.ENGINES, selectedRoom)) {
					selectedRoom.sys = Systems.ENGINES;
					canvas.redraw(selectedRoom.rect.x, selectedRoom.rect.y, selectedRoom.rect.width, selectedRoom.rect.height, true);
				}
			} });
		mntmDoors.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (!isSystemAssigned(Systems.DOORS, selectedRoom)) {
					selectedRoom.sys = Systems.DOORS;
					canvas.redraw(selectedRoom.rect.x, selectedRoom.rect.y, selectedRoom.rect.width, selectedRoom.rect.height, true);
				}
			} });
		mntmPilot.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (!isSystemAssigned(Systems.PILOT, selectedRoom)) {
					selectedRoom.sys = Systems.PILOT;
					canvas.redraw(selectedRoom.rect.x, selectedRoom.rect.y, selectedRoom.rect.width, selectedRoom.rect.height, true);
				}
			} });
		mntmSensors.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (!isSystemAssigned(Systems.SENSORS, selectedRoom)) {
					selectedRoom.sys = Systems.SENSORS;
					canvas.redraw(selectedRoom.rect.x, selectedRoom.rect.y, selectedRoom.rect.width, selectedRoom.rect.height, true);
				}
			} });
		mntmDrones.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (!isSystemAssigned(Systems.DRONES, selectedRoom)) {
					selectedRoom.sys = Systems.DRONES;
					canvas.redraw(selectedRoom.rect.x, selectedRoom.rect.y, selectedRoom.rect.width, selectedRoom.rect.height, true);
				}
			} });
		mntmArtillery.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (!isSystemAssigned(Systems.ARTILLERY, selectedRoom)) {
					selectedRoom.sys = Systems.ARTILLERY;
					canvas.redraw(selectedRoom.rect.x, selectedRoom.rect.y, selectedRoom.rect.width, selectedRoom.rect.height, true);
				}
			} });
		mntmTeleporter.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (!isSystemAssigned(Systems.TELEPORTER, selectedRoom)) {
					selectedRoom.sys = Systems.TELEPORTER;
					canvas.redraw(selectedRoom.rect.x, selectedRoom.rect.y, selectedRoom.rect.width, selectedRoom.rect.height, true);
				}
			} });
		mntmCloaking.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (!isSystemAssigned(Systems.CLOAKING, selectedRoom)) {
					selectedRoom.sys = Systems.CLOAKING;
					canvas.redraw(selectedRoom.rect.x, selectedRoom.rect.y, selectedRoom.rect.width, selectedRoom.rect.height, true);
				}
			} });
		mntmSysImage.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				String[] filterExtensions = new String[] {"*.png"};
				dialog.setFilterExtensions(filterExtensions);
				dialog.setFilterPath(resPath);
				String path = dialog.open();
				
				if (!ShipIO.isNull(path) && selectedRoom != null) {
					selectedRoom.img = path;

					ShipIO.loadSystemImage(selectedRoom);
					canvas.redraw();
				}
			}
		});
		
		
	// === FILE MENU
		
		mntmNewShip.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				int create = new NewShipWindow(shell).open();
				shell.setEnabled(true);
				if (create != 0) {
	        		mntmClose.notifyListeners(SWT.Selection, null);
	        		
					ship = new FTLShip();
					ship.isPlayer = create == 1;
					ship.anchor.x = 140;
					ship.anchor.y = 140;
					print("New ship created.");
					
					canvasActive = true;
					tltmPointer.setEnabled(true);
					tltmRoom.setEnabled(true);
					tltmDoor.setEnabled(true);
					tltmMount.setEnabled(true);
					tltmSystem.setEnabled(true);
					btnHull.setEnabled(true);
					if (ship.rooms.size() > 0) {
						btnShields.setEnabled(ship.isPlayer);
						btnShields.setToolTipText(null);
					}
					btnCloak.setEnabled(true);
					btnFloor.setEnabled(ship.isPlayer);
					btnShipProperties.setEnabled(true);
					
					mntmSaveShip.setEnabled(true);
					mntmSaveShipAs.setEnabled(true);
					mntmExport.setEnabled(true);
					mntmClose.setEnabled(true);
					
					currentPath = null;

					canvas.redraw();
				}
			}
		});
		
		mntmLoadShip.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("static-access")
			public void widgetSelected(SelectionEvent e) {
				ShipBrowser shipBrowser = new ShipBrowser(shell);
				shipBrowser.shell.open();
				
				shipBrowser.shell.addDisposeListener(new DisposeListener() {
					@Override
					public void widgetDisposed(DisposeEvent e)
					{
						if (ship != null ) {
							canvasActive = true;
							tltmPointer.setEnabled(true);
							tltmRoom.setEnabled(true);
							tltmDoor.setEnabled(true);
							tltmMount.setEnabled(true);
							tltmSystem.setEnabled(true);
							btnHull.setEnabled(true);
							if (ship.rooms.size() > 0) {
								btnShields.setEnabled(ship.isPlayer);
								btnShields.setToolTipText(null);
							}
							btnCloak.setEnabled(true);
							btnFloor.setEnabled(ship.isPlayer);
							btnShipProperties.setEnabled(true);
							
							mntmSaveShip.setEnabled(true);
							mntmSaveShipAs.setEnabled(true);
							mntmExport.setEnabled(true);
							mntmClose.setEnabled(true);
							
							if (ship.isPlayer) {
								if (shieldImage != null && !shieldImage.isDisposed()) {
									Rectangle temp = shieldImage.getBounds();
									shieldEllipse.x = ship.anchor.x + ship.offset.x*35 + ship.computeShipSize().x/2 - temp.width/2 + ship.ellipse.x;
									shieldEllipse.y = ship.anchor.y + ship.offset.y*35 + ship.computeShipSize().y/2 - temp.height/2 + ship.ellipse.y;
									shieldEllipse.width = temp.width;
									shieldEllipse.height = temp.height;
								}
							} else {
								shieldEllipse.width = ship.ellipse.width*2;
								shieldEllipse.height = ship.ellipse.height*2;
								shieldEllipse.x = ship.anchor.x + ship.offset.x*35 + ship.computeShipSize().x/2 + ship.ellipse.x - ship.ellipse.width;
								shieldEllipse.y = ship.anchor.y + ship.offset.y*35 + ship.computeShipSize().y/2 + ship.ellipse.y - ship.ellipse.height + 110;
							}
							
							currentPath = null;
							
							canvas.redraw();
						}
					}
				});
			}
		});
		
		mntmSaveShip.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (currentPath == null) {
					ShipIO.askSaveDir();
				} else {
					ShipIO.saveShipProject(currentPath);
					print("Project saved successfully.");
				}
				
				ConfigIO.saveConfig();
			}
		});
		
		mntmSaveShipAs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ShipIO.askSaveDir();
				print("Project saved successfully.");
				
				ConfigIO.saveConfig();
			}
		});
		
		mntmExport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				exDialog = new ExportDialog(shell);
				exDialog.open();
				
				shell.setEnabled(true);
			}
		});
		
		mntmLoadShipProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ShipIO.askLoadDir();
				
				if (ship != null) {
					canvasActive = true;
					tltmPointer.setEnabled(true);
					tltmRoom.setEnabled(true);
					tltmDoor.setEnabled(true);
					tltmMount.setEnabled(true);
					tltmSystem.setEnabled(true);
					btnHull.setEnabled(true);
					if (ship.rooms.size() > 0) {
						btnShields.setEnabled(ship.isPlayer);
						btnShields.setToolTipText(null);
					}
					btnCloak.setEnabled(true);
					btnFloor.setEnabled(ship.isPlayer);
					btnShipProperties.setEnabled(true);

					if (ship.isPlayer) {
						if (shieldImage != null && !shieldImage.isDisposed()) {
							Rectangle temp = shieldImage.getBounds();
							shieldEllipse.x = ship.anchor.x + ship.offset.x*35 + ship.computeShipSize().x/2 - temp.width/2 + ship.ellipse.x;
							shieldEllipse.y = ship.anchor.y + ship.offset.y*35 + ship.computeShipSize().y/2 - temp.height/2 + ship.ellipse.y;
							shieldEllipse.width = temp.width;
							shieldEllipse.height = temp.height;
						}
					} else {
						shieldEllipse.width = ship.ellipse.width*2;
						shieldEllipse.height = ship.ellipse.height*2;
						shieldEllipse.x = ship.anchor.x + ship.offset.x*35 + ship.computeShipSize().x/2 + ship.ellipse.x - ship.ellipse.width;
						shieldEllipse.y = ship.anchor.x + ship.offset.x*35 + ship.computeShipSize().y/2 + ship.ellipse.y - ship.ellipse.height + 110;
					}

					mntmSaveShip.setEnabled(true);
					mntmSaveShipAs.setEnabled(true);
					mntmExport.setEnabled(true);
					mntmClose.setEnabled(true);
					
					canvas.redraw();
				}
			}
		});
		
		mntmClose.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (ship != null) {
					ship.rooms.clear();
					ship.doors.clear();
					if (!ShipIO.loadingSwitch && hullImage != null && !hullImage.isDisposed()) {
						hullImage.dispose();
					}
					if (!ShipIO.loadingSwitch && shieldImage != null && !shieldImage.isDisposed()) {
						shieldImage.dispose();
					}
					if (!ShipIO.loadingSwitch && floorImage != null && !floorImage.isDisposed()) {
						floorImage.dispose();
					}
				}
				hullImage = null;
				shieldImage = null;
				floorImage = null;
				ship = null;
				idList.clear();
				currentPath = null;

				canvasActive = false;
				
				tltmPointer.setEnabled(false);
				tltmRoom.setEnabled(false);
				tltmDoor.setEnabled(false);
				tltmMount.setEnabled(false);
				tltmSystem.setEnabled(false);
				btnHull.setEnabled(false);
				btnShields.setEnabled(false);
				btnShields.setToolTipText("Shield is aligned in relation to rooms. Place a room before choosing shield graphic.");
				btnCloak.setEnabled(false);
				btnFloor.setEnabled(false);
				btnShipProperties.setEnabled(false);

				mntmSaveShip.setEnabled(false);
				mntmSaveShipAs.setEnabled(false);
				mntmExport.setEnabled(false);
				mntmClose.setEnabled(false);
				
				canvas.redraw();
			}
		});
		
	// === EDIT MENU
		
		mntmRemoveDoors.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				removeDoor = ((MenuItem) e.widget).getSelection();
				ConfigIO.saveConfig();
				canvas.redraw();
			}
		});

		mntmWeaponMountSnapping.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				snapMounts = ((MenuItem) e.widget).getSelection();
				ConfigIO.saveConfig();
			}
		});
	
	// === VIEW MENU

		mntmShowAnchor.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				showAnchor = ((MenuItem) e.widget).getSelection();
				ConfigIO.saveConfig();
				canvas.redraw();
			}
		});
		
		mntmShowMounts.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				showMounts = ((MenuItem) e.widget).getSelection();
				ConfigIO.saveConfig();
				canvas.redraw();
			}
		});
		
		mntmShowRooms.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				showRooms = ((MenuItem) e.widget).getSelection();
				ConfigIO.saveConfig();
				canvas.redraw();
			}
		});
		
		mntmShowHull.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				showHull = ((MenuItem) e.widget).getSelection();
				ConfigIO.saveConfig();
				canvas.redraw();
			}
		});

		mntmLoadFloorGraphic.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				loadFloor = ((MenuItem) e.widget).getSelection();
				ConfigIO.saveConfig();
				canvas.redraw();
			}
		});

		mntmLoadShieldGraphic.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				loadShield = ((MenuItem) e.widget).getSelection();
				ConfigIO.saveConfig();
				canvas.redraw();
			}
		});

		mntmLoadSystem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				loadSystem = ((MenuItem) e.widget).getSelection();
				ConfigIO.saveConfig();
				canvas.redraw();
			}
		});
		
		mntmConstantRedraw.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				constantRedraw = mntmConstantRedraw.getSelection();
				ConfigIO.saveConfig();
			}
		});
	}
	
//======================================================
// === BOOKMARK: AUXILIARY METHODS
	
	// === ROUNDING TO GRID
	
	/**
	 * Aligns to closest line of the grid.
	 */
	public int roundToGrid(int a) {
		return Math.round(a/35)*35;
	}
	/**
	 * Aligns to the lowest (left-most / top-most) line of the grid.
	 */
	public int downToGrid(int a) {
		return (int) (Math.ceil(a/35)*35);
	}
	/**
	 * Aligns to the highest (right-most / bottom-most) line of the grid.
	 */
	public int upToGrid(int a) {
		return (int) (Math.floor(a/35)*35);
	}
	
	

	//=================
	// === GENERAL

	public Rectangle fixRect(Rectangle r) {
		Rectangle rect = new Rectangle(0,0,0,0);
		rect.x = r.width<0 ? r.x+r.width : r.x;
		rect.y = r.height<0 ? r.y+r.height : r.y;
		rect.width = r.width<0 ? -r.width : r.width;
		rect.height = r.height<0 ? -r.height : r.height;
		return rect;
	}

	/**
	 * Checks if given rect overlaps any of the already placed rooms. If given rect is inside the roomsList set, it doesn't perform check against that rect (meaning it won't return true).
	 * 
	 * @param rect rectangle to be checked
	 * @param treatAs if set to another rectangle, the self-exclusive check will be performed against that rectangle, and not the one in the first parameter. Can be set to null if not used.
	 * @return true if rect given in parameter overlaps any of already placed rooms/rects.
	 */
	public boolean doesRectOverlap(Rectangle rect, Rectangle treatAs) {
		for (FTLRoom r : ship.rooms) {
			if (rect.intersects(r.rect) && ((treatAs != null && r.rect != treatAs) || (treatAs == null && r.rect != rect)) ) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsRect(Rectangle r1, Rectangle r2) {
		return r1.contains(r2.x, r2.y) && r1.contains(r2.x+r2.width, r2.y+r2.height); 
	}
	
	public Rectangle getRectFromClick() {
		Rectangle tempRect = new Rectangle(0,0,35,35);
		for(int x=0; x<GRID_W; x++) {
			for(int y=0; y<GRID_H; y++) {
				tempRect.x = x*35;
				tempRect.y = y*35;
				if (tempRect.contains(mousePosLastClick)) {
					return tempRect;
				}
			}
		}
		return null;
	}
	
	public Rectangle getRectFromMouse() {
		Rectangle tempRect = new Rectangle(0,0,35,35);
		for(int x=0; x<GRID_W; x++) {
			for(int y=0; y<GRID_H; y++) {
				tempRect.x = x*35;
				tempRect.y = y*35;
				if (tempRect.contains(mousePos)) {
					return tempRect;
				}
			}
		}
		return null;
	}

	

	//=================
	// === ROOM RELATED
	
	public void updateCorners(FTLRoom r) {
		corners[0] = new Rectangle(r.rect.x, r.rect.y, CORNER, CORNER);
		corners[1] = new Rectangle(r.rect.x+r.rect.width-CORNER, r.rect.y, CORNER, CORNER);
		corners[2] = new Rectangle(r.rect.x, r.rect.y+r.rect.height-CORNER, CORNER, CORNER);
		corners[3] = new Rectangle(r.rect.x+r.rect.width-CORNER, r.rect.y+r.rect.height-CORNER, CORNER, CORNER);
		r.updateCorners();
	}

	public int getLowestId() {
		int i = -1;
		idList.add(-1);
		while(i < GRID_W*GRID_H && idList.contains(i)) {
			i++;
		}
		return i;
	}
	
	public Point findFarthestCorner(FTLRoom r, Point p) {
		double d = 0;
		double t = 0;
		Point pt = null;
		for (int i = 0; i < 4; i++) {
			t = Math.sqrt( Math.pow(r.corners[i].x - p.x, 2) + Math.pow(r.corners[i].y - p.y,2) );
			if (d<t) {
				d = t;
				pt = r.corners[i];
			}	
		}
		return pt;
	}
	
	public static FTLRoom getRoomContainingRect(Rectangle rect) {
		if (rect != null) {
			for (FTLRoom r : ship.rooms) {
				if (r.rect.intersects(rect))
					return r;
			}
		}
		return null;
	}

	public static boolean isSystemAssigned(Systems sys, FTLRoom r) {
		for (FTLRoom rm : ship.rooms) {
			if (r != null && rm != r && rm.sys == sys)
				return true;
		}
		return false;
	}
	
	public static boolean isSystemAssigned(Systems sys) {
		for (FTLRoom rm : ship.rooms) {
			if (rm.sys == sys)
				return true;
		}
		return false;
	}
	
	public static FTLRoom getRoomWithSystem(Systems sys) {
		for (FTLRoom rm : ship.rooms) {
			if (rm.sys.equals(sys))
				return rm;
		}
		return null;
	}
	
	public static Rectangle getRectFromStation(FTLRoom r) {
		int w = r.rect.width/35;
		int y = (int) Math.floor(r.slot/w);
		int x = r.slot - y* w;
		
		return new Rectangle(r.rect.x+x*35, r.rect.y+y*35, 35, 35);
	}

	public static int getStationFromRect(Rectangle rect) {
		int x,y,slot=-2;
		for (FTLRoom r : ship.rooms) {
			if (r.rect.intersects(rect)) {
				x = (rect.x - r.rect.x)/35;
				y = (rect.y - r.rect.y)/35;
				slot = r.rect.width/35 * y + x;
			}
		}
		
		return slot;
	}
	
	public static Rectangle getStationDirected(FTLRoom r) {
		final int STATION_SIZE = 15;
		Rectangle rect = getRectFromStation(r);
		
		if (r.dir.equals(Slide.UP)) {
			rect.height = STATION_SIZE;
		} else if (r.dir.equals(Slide.RIGHT)) {
			rect.x += 35 - STATION_SIZE;
			rect.width = STATION_SIZE;
		} else if (r.dir.equals(Slide.DOWN)) {
			rect.y += 35 - STATION_SIZE;
			rect.height = STATION_SIZE;
		} else if (r.dir.equals(Slide.LEFT)) {
			rect.width = STATION_SIZE;
		}
		
		return rect;
	}
	
	//=================
	// === DOOR RELATED
	
	public void removeUnalignedDoors() {
		if (removeDoor) {
			Object[] array = ship.doors.toArray();
			for (Object o : array) {
				FTLDoor d = (FTLDoor) o;
				if (!isDoorAtWall(d.rect)) {
					ship.doors.remove(d);
					d = null;
				}
			}
			array = null;
		}
	}
	
	/**
	 * 
	 * @param rect Rectangle which matches the parameters of a wall;
	 * @return FTLDoor at the given rect, if there is one.
	 */
	public FTLDoor wallToDoor(Rectangle rect) {
		for (FTLDoor dr : ship.doors) {
			if (rect != null && rect.intersects(dr.rect) && rect.width == dr.rect.width) {
				return dr;
			}
		}
		return null;
	}

	public boolean isDoorAtWall(Rectangle rect) {
		for (FTLRoom r : ship.rooms) {
			if (rect != null && r != null && rect.intersects(r.rect) && !containsRect(r.rect, rect))
				return true;
		}
		return false;
	}
	
	public Rectangle getDoorFromMouse() {
		Rectangle dr = new Rectangle(0,0,0,0);
		for(int x=0; x<GRID_W; x++) {
			for(int y=0; y<GRID_H; y++) {
				// horizontal
				dr.x = x*35+2; dr.y = y*35-3; dr.width = 31; dr.height = 6;
				if (dr.contains(mousePos))
					return dr;
				
				dr.x = x*35-3; dr.y = y*35+2; dr.width = 6; dr.height = 31;
				if (dr.contains(mousePos))
					return dr;
			}
		}
		return null;
	}
	
	//=================
	// === MOUNT RELATED
	
	public FTLMount getMountFromMouse() {
		for (FTLMount m : ship.mounts) {
			if (m.rect.contains(mousePos)) {
				return m;
			}
		}
		return null;
	}

	
	//=================
	// === AUXILIARY / LAZYNESS
	
	public void drawToolIcon(PaintEvent e, String name) {
		e.gc.setAlpha(255);
		e.gc.drawImage(toolsMap.get(name),0, 0, 24, 24, mousePos.x+10, mousePos.y+10, 19, 20);
	}
	
	/**
	 * Prints given message to the box in top right corner of the app.
	 */
	public static void print(String msg) {
		if (!msg.equals("")) {
			lastMsg = msg;
		}
		text.setText(lastMsg);
	}
	
	public static void debug(String msg) {
		if (debug)
			System.out.println(msg);
	}
}


