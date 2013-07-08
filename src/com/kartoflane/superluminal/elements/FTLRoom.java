package com.kartoflane.superluminal.elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.UndoableEditEvent;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.core.ShipIO;
import com.kartoflane.superluminal.painter.Cache;
import com.kartoflane.superluminal.painter.ColorBox;
import com.kartoflane.superluminal.painter.LayeredPainter;
import com.kartoflane.superluminal.painter.PaintBox;
import com.kartoflane.superluminal.undo.Undoable;
import com.kartoflane.superluminal.undo.UndoableImageEdit;
import com.kartoflane.superluminal.undo.UndoableResizeEdit;
import com.kartoflane.superluminal.undo.UndoableSplitEdit;
import com.kartoflane.superluminal.undo.UndoableSystemEdit;

/**
 * Class representing a single room in a ship.
 */
public class FTLRoom extends ColorBox implements Serializable, Comparable<FTLRoom>, DraggableBox {
	private static final long serialVersionUID = 2768589659254502754L;
	public int id;
	public Point[] corners = new Point[4];
	private Point selectedCorner;
	private Point offset;
	private Systems sys;
	public SystemBox sysBox;
	public int slot;
	public Slide dir;

	/**
	 * Path to the interior image; kept for compatibility
	 */
	public String img;
	public FTLInterior interiorData = null;

	private Color grid_color;
	private Rectangle origin;
	private Color slotColor;
	private RGB slot_rgb;

	private boolean move = false;
	private boolean resize = false;

	public void stripUnserializable() {
		grid_color = null;
		Cache.checkInColor(this, slot_rgb);
		slotColor = null;
		if (sysBox != null) {
			if (interiorData != null)
				img = interiorData.interiorPath;
			interiorData = sysBox.interiorData;
		}
		sysBox = null;
		// Cache.checkInImageAbsolute(this, img);
		// sysImg = null;
		super.stripUnserializable();
	}

	public void loadUnserializable() {
		grid_color = Main.grid.getCellAt(1, 1).getGridColor();
		slotColor = Cache.checkOutColor(this, slot_rgb);
		sysBox = Main.systemsMap.get(sys);
		if (sysBox != null) {
			sysBox.interiorData = interiorData;
			if (interiorData != null)
				interiorData.interiorPath = img;
		}
		// sysImg = Cache.checkOutImageAbsolute(this, img);
		super.loadUnserializable();
	}

	public FTLRoom(int x, int y, int w, int h) {
		super(new RGB(230, 225, 220));

		x = Main.roundToGrid(x);
		y = Main.roundToGrid(y);
		w = Main.roundToGrid(w);
		h = Main.roundToGrid(h);

		origin = new Rectangle(x, y, w, h);
		offset = new Point(0, 0);
		setBorderColor(new RGB(0, 0, 0));
		setBorderThickness(2);
		setBorderMode(PaintBox.BORDER_INSIDE);
		this.setLocation(x, y);
		this.setSize(w, h);
		this.id = -1;
		this.corners[0] = new Point(x, y);
		this.corners[1] = new Point(x + w, y);
		this.corners[2] = new Point(x, y + h);
		this.corners[3] = new Point(x + w, y + h);
		this.sys = Systems.EMPTY;
		this.slot = -2;
		this.dir = Slide.UP;
		grid_color = Main.grid.getCellAt(1, 1).getGridColor();
		slot_rgb = new RGB(128, 0, 128);
		slotColor = Cache.checkOutColor(this, slot_rgb);
	}

	public FTLRoom(Rectangle rect) {
		this(rect.x, rect.y, rect.width, rect.height);
	}

	public void updateCorners() {
		this.corners[0] = new Point(this.getBounds().x, this.getBounds().y);
		this.corners[1] = new Point(this.getBounds().x + this.getBounds().width, this.getBounds().y);
		this.corners[2] = new Point(this.getBounds().x, this.getBounds().y + this.getBounds().height);
		this.corners[3] = new Point(this.getBounds().x + this.getBounds().width, this.getBounds().y + this.getBounds().height);
	}

	/**
	 * "Lower" room has lower ID, equal rooms have equal IDs (are the same room), etc
	 */
	public int compareTo(FTLRoom r) {
		return id - r.id;
	}

	public void setInterior(String path) {
		if (sysBox != null) {
			if (interiorData.interiorPath != null)
				Cache.checkInImageAbsolute(sysBox, interiorData.interiorPath);
			interiorData.interiorPath = null;
			if (!sys.equals(Systems.EMPTY) && !sys.equals(Systems.TELEPORTER)) {
				// Main.ship.interiorMap.get(sys).interiorPath = path;
				interiorData.interiorPath = path;
				sysBox.interior = Cache.checkOutImageAbsolute(sysBox, interiorData.interiorPath);
			}

			Main.canvasRedraw(bounds, false);
		}
	}

	public void assignSystem(SystemBox box) {
		if (sysBox != null)
			sysBox.unassign();
		if (box != null) {
			sysBox = box;
			sysBox.setRoom(this);
			sysBox.setVisible(true);
			sys = box.getSystemName();
			if (!sysBox.isAvailable()) {
				setColor(new RGB(220, 100, 100));
			} else {
				setColor(new RGB(230, 225, 220));
			}
		} else {
			sysBox = null;
			sys = Systems.EMPTY;
			setColor(new RGB(230, 225, 220));
		}
	}

	public void assignSystem(Systems sysName) {
		if (sysBox != null)
			sysBox.unassign();
		if (!sysName.equals(Systems.EMPTY)) {
			sysBox = Main.systemsMap.get(sysName);
			sysBox.setRoom(this);
			sysBox.setVisible(true);
			sys = sysName;
			if (!sysBox.isAvailable()) {
				setColor(new RGB(220, 100, 100));
			} else {
				setColor(new RGB(230, 225, 220));
			}
		} else {
			sysBox = null;
			sys = sysName;
			setColor(new RGB(230, 225, 220));
		}
	}

	public void setVisible(boolean vis) {
		visible = vis;
		if (sysBox != null)
			sysBox.setVisible(vis);
	}

	public Systems getSystem() {
		return sys;
	}

	public SystemBox getSysBox() {
		return sysBox;
	}

	public void setLocation(int x, int y) {
		Rectangle temp;
		origin.x = bounds.x;
		origin.y = bounds.y;

		Point oldLow = null;
		Point oldHigh = null;
		if (Main.ship != null) {
			oldLow = Main.ship.findLowBounds();
			oldHigh = Main.ship.findHighBounds();
			oldLow.x = oldLow.x + (oldHigh.x - oldLow.x) / 2;
			oldLow.y = oldLow.y + (oldHigh.y - oldLow.y) / 2;
		}

		if (Main.ship != null) {
			x = Math.max(Main.ship.anchor.x, x);
			y = Math.max(Main.ship.anchor.y, y);
		}

		temp = Main.getRectAt(x, y);
		if (temp != null) {
			temp.width = bounds.width;
			temp.height = bounds.height;
			if (Main.ship != null && !Main.doesRectOverlap(temp, bounds)) {
				if (temp.x >= Main.ship.anchor.x && temp.x + temp.width <= Main.GRID_W * 35)
					bounds.x = temp.x;
				if (temp.y >= Main.ship.anchor.y && temp.y + temp.height <= Main.GRID_H * 35)
					bounds.y = temp.y;
			} else if (Main.ship == null) {
				bounds.x = temp.x;
				bounds.y = temp.y;
			}

			updateCorners();
			Main.removeUnalignedDoors();

			Main.canvas.redraw(origin.x - 2, origin.y - 2, bounds.width + 4, bounds.height + 4, false);
			Main.canvasRedraw(bounds, false);

			origin.x = bounds.x;
			origin.y = bounds.y;

			if (Main.ship != null) {
				Point p = Main.ship.findLowBounds();
				Main.ship.offset.x = (p.x - Main.ship.anchor.x + 10) / 35;
				Main.ship.offset.y = (p.y - Main.ship.anchor.y + 10) / 35;
			}

			if (sysBox != null)
				sysBox.updateLocation();

			Main.updateSelectedPosText();
		}

		if (Main.ship != null) {
			Point p = Main.ship.findLowBounds();
			Point pt = Main.ship.findHighBounds();
			p.x = p.x + (pt.x - p.x) / 2;
			p.y = p.y + (pt.y - p.y) / 2;

			pt.x = p.x - oldLow.x;
			pt.y = p.y - oldLow.y;

			p = Main.shieldBox.getLocation();
			Main.shieldBox.setLocation(p.x + pt.x, p.y + pt.y);
		}
	}

	/**
	 * Only used by anchor position update. Ommits all collion checks.
	 */
	public void setLocationAbsolute(int x, int y) {
		origin.x = x;
		origin.y = y;
		bounds.x = x;
		bounds.y = y;

		updateCorners();

		if (sysBox != null)
			sysBox.updateLocation();

		Main.updateSelectedPosText();
	}

	public void setSize(int w, int h) {
		Rectangle temp;
		bounds = Main.fixRect(bounds);
		origin.x = bounds.x;
		origin.y = bounds.y;
		origin.width = bounds.width;
		origin.height = bounds.height;

		Point oldLow = null;
		Point oldHigh = null;
		if (Main.ship != null) {
			oldLow = Main.ship.findLowBounds();
			oldHigh = Main.ship.findHighBounds();
			oldLow.x = oldLow.x + (oldHigh.x - oldLow.x) / 2;
			oldLow.y = oldLow.y + (oldHigh.y - oldLow.y) / 2;
		}

		w = Main.roundToGrid(w);
		h = Main.roundToGrid(h);

		temp = Main.cloneRect(bounds);
		temp.width = w;
		temp.height = h;

		if (resize && !Main.doesRectOverlap(temp, bounds)) {
			if (selectedCorner.equals(corners[0])) {
				temp.width = w;
				temp.height = h;
			} else if (selectedCorner.equals(corners[1])) {
				temp.x = selectedCorner.x + w;
				temp.width = -w;
				temp.height = h;
			} else if (selectedCorner.equals(corners[2])) {
				temp.y = selectedCorner.y + h;
				temp.width = w;
				temp.height = -h;
			} else if (selectedCorner.equals(corners[3])) {
				temp.x = selectedCorner.x + w;
				temp.y = selectedCorner.y + h;
				temp.width = -w;
				temp.height = -h;
			}

			temp = Main.fixRect(temp);

			if (temp.x >= Main.ship.anchor.x && temp.x + temp.width < Main.GRID_W * 35 + 35 && !Main.doesRectOverlap(temp, bounds)) {
				bounds.x = temp.x;
				bounds.width = (temp.width == 0) ? 35 : temp.width;
			}
			if (temp.y >= Main.ship.anchor.y && temp.y + temp.height < Main.GRID_H * 35 + 35 && !Main.doesRectOverlap(temp, bounds)) {
				bounds.y = temp.y;
				bounds.height = (temp.height == 0) ? 35 : temp.height;
			}

			Main.updateCorners(this);
			updateCorners();
			Main.removeUnalignedDoors();

			Main.canvas.redraw(origin.x - 2, origin.y - 2, origin.width + 4, origin.height + 4, false);
			Main.canvas.redraw(temp.x, temp.y, temp.width, temp.height, false);
		} else if (!resize) {
			bounds.width = w;
			bounds.height = h;
			origin.x = bounds.x;
			origin.y = bounds.y;
			origin.width = bounds.width;
			origin.height = bounds.height;
		}

		if (Main.ship != null) {
			Point p = Main.ship.findLowBounds();
			Point pt = Main.ship.findHighBounds();
			p.x = p.x + (pt.x - p.x) / 2;
			p.y = p.y + (pt.y - p.y) / 2;

			pt.x = p.x - oldLow.x;
			pt.y = p.y - oldLow.y;

			p = Main.shieldBox.getLocation();
			Main.shieldBox.setLocation(p.x + pt.x, p.y + pt.y);
		}

		if (Main.ship != null) {
			Point p = Main.ship.findLowBounds();
			Main.ship.offset.x = (p.x - Main.ship.anchor.x + 10) / 35;
			Main.ship.offset.y = (p.y - Main.ship.anchor.y + 10) / 35;
		}

		if (sysBox != null)
			sysBox.updateLocation();
	}

	public static int getDefaultSlot(Systems sys) {
		int i = -2;
		if ((ShipIO.shipBeingLoaded != null && ShipIO.shipBeingLoaded.isPlayer) || (Main.ship != null && Main.ship.isPlayer)) {
			i = (sys.equals(Systems.ENGINES))
					? 2
					: (sys.equals(Systems.PILOT))
							? 0
							: (sys.equals(Systems.SHIELDS))
									? 0
									: (sys.equals(Systems.WEAPONS))
											? 1
											: (sys.equals(Systems.MEDBAY))
													? 1
													: -2;
		} else if (ShipIO.shipBeingLoaded != null || Main.ship != null) {
			// random slot for enemy ships
			/*
			 * i = (sys.equals(Systems.MEDBAY))
			 * ? -2
			 * : (sys.equals(Systems.PILOT))
			 * ? 0
			 * : (int) (Math.random()*1);
			 */
		}

		return i;
	}

	public static Slide getDefaultDir(Systems sys) {
		Slide dir = null;
		if ((ShipIO.shipBeingLoaded != null && ShipIO.shipBeingLoaded.isPlayer) || (Main.ship != null && Main.ship.isPlayer)) {
			dir = (sys.equals(Systems.ENGINES))
					? Slide.DOWN
					: (sys.equals(Systems.PILOT))
							? Slide.RIGHT
							: (sys.equals(Systems.SHIELDS))
									? Slide.LEFT
									: (sys.equals(Systems.WEAPONS))
											? Slide.UP
											: Slide.NO;
		} else if (ShipIO.shipBeingLoaded != null || Main.ship != null) {
			dir = Slide.UP;
			// random direction for enemy ships
			/*
			 * dir = (sys.equals(Systems.MEDBAY))
			 * ? Slide.NO
			 * : (sys.equals(Systems.PILOT))
			 * ? Slide.UP
			 * : getStationRandomDir(sys);
			 */
		}

		return dir;
	}

	public static Slide getRandomDir() {
		int r = (int) (Math.random() * 3);
		return r == 0 ? Slide.UP : (r == 1 ? Slide.RIGHT : (r == 2 ? Slide.DOWN : (r == 3 ? Slide.LEFT : Slide.NO)));
	}

	public static Slide getStationRandomDir(Systems sys) {
		FTLRoom r = Main.systemsMap.get(sys).getRoom();

		List<Slide> list = new ArrayList<Slide>();
		list.add(Slide.DOWN);
		list.add(Slide.UP);
		list.add(Slide.LEFT);
		list.add(Slide.RIGHT);

		if (ShipIO.shipBeingLoaded.slotMap.get(sys) == 0) {
			if (r.getBounds().width > 35)
				list.remove(Slide.RIGHT);
			if (r.getBounds().height > 35)
				list.remove(Slide.DOWN);
		} else {
			if (r.getBounds().width > 35)
				list.remove(Slide.LEFT);
			if (r.getBounds().height > 35)
				list.remove(Slide.UP);
		}

		return list.get((int) ((Math.random() * list.size())));
	}

	@Override
	public void paintControl(PaintEvent e) {
		drawBorder = !Main.tltmGib.getSelection();
		if (!Main.tltmGib.getSelection()) {

			setAlpha(Main.btnCloaked.getSelection() ? 255 / 3 : 255);
			if (sysBox != null)
				sysBox.setAlpha(Main.btnCloaked.getSelection() ? 255 / 3 : 255);

			int prevAlpha = e.gc.getAlpha();
			int prevWidth = e.gc.getLineWidth();
			Color prevBg = e.gc.getForeground();
			super.paintControl(e);

			e.gc.setAlpha(alpha);
			e.gc.setLineWidth(1);
			e.gc.setForeground(grid_color);

			// inner grid
			for (int i = 1; i < bounds.width / 35; i++)
				e.gc.drawLine(bounds.x + i * 35, bounds.y, bounds.x + i * 35, bounds.y + bounds.height);
			for (int i = 1; i < bounds.height / 35; i++)
				e.gc.drawLine(bounds.x, bounds.y + i * 35, bounds.x + bounds.width, bounds.y + i * 35);

			if (sysBox != null && sysBox.interior != null)
				e.gc.drawImage(sysBox.interior, 0, 0, sysBox.interior.getBounds().width, sysBox.interior.getBounds().height, bounds.x, bounds.y, bounds.width, bounds.height);

			e.gc.setForeground(prevBg);
			if (selected) {
				prevBg = e.gc.getBackground();

				if (pinned && selected) {
					e.gc.setBackground(pinColor);
				} else {
					e.gc.setBackground(borderColor);
				}
				e.gc.setAlpha(128);
				// resize corners
				e.gc.fillRectangle(bounds.x, bounds.y, 10, 10);
				e.gc.fillRectangle(bounds.x + bounds.width - 10, bounds.y, 10, 10);
				e.gc.fillRectangle(bounds.x, bounds.y + bounds.height - 10, 10, 10);
				e.gc.fillRectangle(bounds.x + bounds.width - 10, bounds.y + bounds.height - 10, 10, 10);

				e.gc.setAlpha(255);
				/*
				 * if (isPinned())
				 * e.gc.drawImage(pin, bounds.x + 10, bounds.y + 3);
				 */
			}
			// working stations
			if (Main.ship != null && Main.ship.slotMap.keySet().contains(sys) && Main.ship.slotMap.get(getSystem()) != -2 && Main.showStations) {
				e.gc.setAlpha(Main.btnCloaked.getSelection() ? 80 : 160);
				e.gc.setBackground(slotColor);

				if (!sys.equals(Systems.MEDBAY)) {
					e.gc.fillRectangle(Main.getStationDirected(this));
				} else {
					e.gc.fillRectangle(Main.getRectFromStation(this));
				}
			}

			e.gc.setBackground(prevBg);
			e.gc.setLineWidth(prevWidth);
			e.gc.setAlpha(prevAlpha);
		}
	}

	@Override
	public void registerUp(int undoable) {
		super.registerUp(undoable);
		if (ume != null) {
			if (undoable == Undoable.RESIZE) {
				Rectangle rect = ((UndoableResizeEdit) ume).getOldBounds();
				if (rect.x != bounds.x || rect.y != bounds.y || rect.width != bounds.width || rect.height != bounds.height) {
					((UndoableResizeEdit) ume).setCurrentBounds(bounds);
					Main.addEdit(ume);
				}
			} else if (undoable == Undoable.ASSIGN_SYSTEM) {
				Systems temp = ((UndoableSystemEdit) ume).getOldValue();
				if (!temp.equals(sys)) {
					((UndoableSystemEdit) ume).setCurrentValue(sys);
					Main.addEdit(ume);
				}
			} else if (undoable == Undoable.IMAGE) {
				String path = ((UndoableImageEdit) ume).getOldValue();
				if (path != interiorData.interiorPath || (path != null && interiorData.interiorPath != null && !path.equals(interiorData.interiorPath))) {
					((UndoableImageEdit) ume).setCurrentValue(interiorData.interiorPath);
					Main.addEdit(ume);
				}
			} else if (undoable == Undoable.SPLIT) {
				((UndoableSplitEdit) ume).setCurrentBounds(bounds);
				Main.addEdit(ume);
			}
		}
	}

	@Override
	public void registerDown(int undoable) {
		super.registerDown(undoable);
		if (undoListener != null) {
			if (undoable == Undoable.RESIZE) {
				ume = new UndoableResizeEdit(this);
				undoListener.undoableEditHappened(new UndoableEditEvent(this, ume));
			} else if (undoable == Undoable.ASSIGN_SYSTEM) {
				ume = new UndoableSystemEdit(this);
				undoListener.undoableEditHappened(new UndoableEditEvent(this, ume));
			} else if (undoable == Undoable.IMAGE) {
				ume = new UndoableImageEdit(this, undoable);
				undoListener.undoableEditHappened(new UndoableEditEvent(this, ume));
			} else if (undoable == Undoable.SPLIT) {
				ume = new UndoableSplitEdit(this);
				undoListener.undoableEditHappened(new UndoableEditEvent(this, ume));
			}
		}
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (bounds.contains(e.x, e.y)) {
			select();
			offset.x = e.x - bounds.x;
			offset.y = e.y - bounds.y;
		} else {
			deselect();
		}

		if (e.button == 1) {
			if (Main.corners[0].contains(e.x, e.y) || Main.corners[1].contains(e.x, e.y) || Main.corners[2].contains(e.x, e.y) || Main.corners[3].contains(e.x, e.y)) {
				selectedCorner = Main.findFarthestCorner(this, new Point(e.x, e.y));
				resize = !isPinned();
				move = false;
			}
			Main.cursor.setVisible(false);
		} else if (e.button == 3) {
			move = false;
		}

		if (resize) {
			registerDown(Undoable.RESIZE);
		} else if (move) {
			registerDown(Undoable.MOVE);
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {
		Main.cursor.setVisible(true);
		if (resize) {
			registerUp(Undoable.RESIZE);
		} else if (move) {
			registerUp(Undoable.MOVE);
		}

		if (move) {
			Main.cursor.setLocation(bounds.x, bounds.y);
		}
		if (e.button == 1) {
			if (resize)
				bounds = Main.fixRect(bounds);
			move = false;
			resize = false;
			/*
			 * if (!bounds.contains(e.x, e.y) && selected)
			 * //deselect();
			 */
		} else if (e.button == 3) {
			if (bounds.contains(e.x, e.y) && selected) {
				Main.menuSystem.setVisible(true);
			}
		}
	}

	@Override
	public void mouseMove(MouseEvent e) {
		if (selected && move) {
			setLocation(e.x - offset.x, e.y - offset.y);
		} else if (selected && resize && !move) {
			Point p = new Point(e.x - selectedCorner.x, e.y - selectedCorner.y);
			p.x += (p.x > 0) ? 35 : -35;
			p.y += (p.y > 0) ? 35 : -35;
			setSize(p.x, p.y);
		}
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		if (selected)
			Main.sysDialog.open();
	}

	public void updateColor() {
		if (sysBox == null || sysBox.isAvailable()) {
			if (selected) {
				if (pinned)
					setColor(new RGB(183, 200, 127));
				else
					setColor(new RGB(170, 170, 255));
			} else {
				setColor(new RGB(230, 225, 220));
			}
		} else if (sysBox != null && !sysBox.isAvailable()) {
			if (selected) {
				if (pinned)
					setColor(new RGB(188, 148, 110));
				else
					setColor(new RGB(180, 100, 220));
			} else {
				setColor(new RGB(220, 100, 100));
			}
		}
	}

	public void select() {
		if (Main.selectedRoom != null)
			Main.selectedRoom.deselect();
		selected = true;
		move = !isPinned();
		setBorderColor(new RGB(0, 0, 255));
		updateColor();
		Main.selectedRoom = this;
		Main.updateCorners(this);
		Main.canvasRedraw(bounds, false);
	}

	public void deselect() {
		selected = false;
		move = false;
		setBorderColor(new RGB(0, 0, 0));
		updateColor();
		Main.selectedRoom = null;
		Main.canvas.redraw(bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4, false);
	}

	public void add(FTLShip ship) {
		Point oldLow = null;
		Point oldHigh = null;
		if (Main.ship != null) {
			oldLow = Main.ship.findLowBounds();
			oldHigh = Main.ship.findHighBounds();
			oldLow.x = oldLow.x + (oldHigh.x - oldLow.x) / 2;
			oldLow.y = oldLow.y + (oldHigh.y - oldLow.y) / 2;
		}

		ship.rooms.add(this);
		Main.layeredPainter.add(this, LayeredPainter.ROOM);
		Main.idList.add(id);

		if (ship.rooms.size() > 0) {
			Main.btnShields.setEnabled(ship.isPlayer);
			Main.btnShields.setToolTipText(null);
		}

		if (Main.ship != null) {
			Point p = Main.ship.findLowBounds();
			int x = Math.min(bounds.x, p.x);
			int y = Math.min(bounds.y, p.y);

			Main.ship.offset.x = (p.x - x) / 35;
			Main.ship.offset.y = (p.y - y) / 35;
		}

		if (Main.ship != null) {
			Point p = Main.ship.findLowBounds();
			Point pt = Main.ship.findHighBounds();
			p.x = p.x + (pt.x - p.x) / 2;
			p.y = p.y + (pt.y - p.y) / 2;

			pt.x = p.x - oldLow.x;
			pt.y = p.y - oldLow.y;

			p = Main.shieldBox.getLocation();
			Main.shieldBox.setLocation(p.x + pt.x, p.y + pt.y);
		}
	}

	/**
	 * Flag X is horizontal split, flag Y is vertical split.
	 * 
	 * @param gridCells
	 *            How many grid cells, from top or left, the new room is to have
	 * @param axis
	 *            either X or Y - BOTH flag has no effect
	 * @return the second, newly created room
	 */
	public FTLRoom split(int gridCells, AxisFlag axis) {
		if (axis.equals(AxisFlag.BOTH) || gridCells == 0)
			return null;

		FTLRoom newRoom = null;
		if (axis.equals(AxisFlag.X)) {
			newRoom = new FTLRoom(bounds.x, bounds.y + gridCells * 35, bounds.width, gridCells * 35);
			newRoom.id = Main.getLowestId();

			Main.ship.rooms.add(newRoom);
			Main.layeredPainter.add(newRoom, LayeredPainter.ROOM);
			Main.idList.add(newRoom.id);

			newRoom.setSize(bounds.width, gridCells * 35);
			newRoom.setLocationAbsolute(bounds.x, bounds.y);
			setLocationAbsolute(bounds.x, bounds.y + gridCells * 35);
			setSize(bounds.width, bounds.height - gridCells * 35);
		} else {
			newRoom = new FTLRoom(bounds.x + gridCells * 35, bounds.y, gridCells * 35, gridCells);
			newRoom.id = Main.getLowestId();

			Main.ship.rooms.add(newRoom);
			Main.layeredPainter.add(newRoom, LayeredPainter.ROOM);
			Main.idList.add(newRoom.id);

			newRoom.setSize(gridCells * 35, bounds.height);
			newRoom.setLocationAbsolute(bounds.x, bounds.y);
			setLocationAbsolute(bounds.x + gridCells * 35, bounds.y);
			setSize(bounds.width - gridCells * 35, bounds.height);
		}

		if (newRoom.getBounds().width < 35 || newRoom.getBounds().height < 35) {
			newRoom.dispose();
			Main.ship.rooms.remove(newRoom);
		}

		if (bounds.width < 35 || bounds.height < 35) {
			dispose();
			Main.ship.rooms.remove(this);
		}

		if (newRoom != null) {
			Point p = Main.ship.findLowBounds();
			int x = Math.min(newRoom.bounds.x, p.x);
			int y = Math.min(newRoom.bounds.y, p.y);

			Main.ship.offset.x = (p.x - x) / 35;
			Main.ship.offset.y = (p.y - y) / 35;
		}

		if (Main.ship != null) {
			Point p = Main.ship.findLowBounds();
			Point pt = Main.ship.findHighBounds();
			p.x = p.x + (pt.x - p.x) / 2;
			p.y = p.y + (pt.y - p.y) / 2;

			Main.shieldBox.setLocationCenter(p.x, p.y);
		}

		if (Main.ship.rooms.contains(newRoom))
			return newRoom;
		return null;
	}

	public void dispose() {
		if (sysBox != null)
			sysBox.unassign();

		Cache.checkInColor(this, slot_rgb);
		// Cache.checkInImageAbsolute(this, img);
		Main.layeredPainter.remove(this);
		Main.idList.remove(id);

		Main.canvas.redraw(bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4, false);

		super.dispose();
		grid_color = null;
		img = null;
	}

	@Override
	public void mouseHover(MouseEvent e) {
	}

	@Override
	public void setOffset(int x, int y) {
	}

	@Override
	public Point getOffset() {
		return offset;
	}
}
