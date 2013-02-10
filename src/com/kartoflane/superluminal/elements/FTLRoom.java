package com.kartoflane.superluminal.elements;

import java.io.Serializable;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.painter.Cache;
import com.kartoflane.superluminal.painter.ColorBox;
import com.kartoflane.superluminal.painter.LayeredPainter;

/**
 * Class representing a single room in a ship.
 *
 */
public class FTLRoom extends ColorBox implements Serializable, Comparable<FTLRoom>, DraggableBox
{
	private static final long serialVersionUID = 2768589659254502754L;
	public int id;
	public Point[] corners = new Point[4];
	private Point selectedCorner;
	private Systems sys;
	private SystemBox sysBox;
	public int slot;
	public Slide dir;
	public String img;
	public Image sysImg;
	private Color grid_color;
	private Rectangle origin;
	private boolean move = false;
	private boolean resize = false;
	
	public FTLRoom(int x, int y, int w, int h) {
		super(new RGB(230, 225, 220));
		origin = new Rectangle(x, y, w, h);
		setBorderColor(new RGB(0, 0, 0));
		this.setLocation(x, y);
		this.setSize(w, h);
		this.id = -1;
		this.corners[0] = new Point(x, y);
		this.corners[1] = new Point(x+w, y);
		this.corners[2] = new Point(x, y+h);
		this.corners[3] = new Point(x+w, y+h);
		this.sys = Systems.EMPTY;
		this.slot = -2;
		this.dir = Slide.UP;
		grid_color = Main.grid.getCellAt(1,1).getGridColor(this);
	}
	
	public FTLRoom(Rectangle rect) {
		this(rect.x, rect.y, rect.width, rect.height);
	}
	
	public void updateCorners() {
		this.corners[0] = new Point(this.getBounds().x, this.getBounds().y);
		this.corners[1] = new Point(this.getBounds().x+this.getBounds().width, this.getBounds().y);
		this.corners[2] = new Point(this.getBounds().x, this.getBounds().y+this.getBounds().height);
		this.corners[3] = new Point(this.getBounds().x+this.getBounds().width, this.getBounds().y+this.getBounds().height);
	}
	
	/**
	 * "Lower" room has lower ID, equal rooms have equal IDs (are the same room), etc
	 */
	public int compareTo(FTLRoom r) {
		return id - r.id;
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
		
		temp = Main.getRectAt(x, y);
		if (temp != null) {
			temp.width = bounds.width;
			temp.height = bounds.height;
			
			if (Main.ship != null && !Main.doesRectOverlap(temp, bounds)) {
				if (temp.x >= Main.ship.anchor.x && temp.x+temp.width <= Main.GRID_W*35)
					bounds.x = temp.x;
				if (temp.y >= Main.ship.anchor.y && temp.y+temp.height <= Main.GRID_H*35)
					bounds.y = temp.y;
			} else if (Main.ship == null) {
				bounds.x = temp.x;
				bounds.y = temp.y;
			}
			
			updateCorners();
			Main.removeUnalignedDoors();

			Main.canvas.redraw(origin.x-2, origin.y-2, bounds.width+4, bounds.height+4, false);
			Main.canvasRedraw(bounds, false);
			
			origin.x = bounds.x;
			origin.y = bounds.y;
			
			if (sysBox != null)
				sysBox.updateLocation();
		}
	}
	
	/**
	 * Only used by anchor position update. Ommits all collion checks.
	 */
	public void setAbsoluteLocation(int x, int y) {
		origin.x = x;
		origin.y = y;
		bounds.x = x;
		bounds.y = y;
		
		updateCorners();
		
		if (sysBox != null)
			sysBox.updateLocation();
	}
	
	public void setSize(int w, int h) {
		Rectangle temp;
		bounds = Main.fixRect(bounds);
		origin.x = bounds.x;
		origin.y = bounds.y;
		origin.width = bounds.width;
		origin.height = bounds.height;
		
		w = Main.roundToGrid(w);
		h = Main.roundToGrid(h);
		
		temp = Main.cloneRect(bounds);
		temp.width = w;
		temp.height = h;
		
		if (Main.ship != null && !Main.doesRectOverlap(temp, bounds)) {
			if (selectedCorner.equals(corners[0])) {
				temp.width = w;
				temp.height = h;
			} else if (selectedCorner.equals(corners[1])) {
				temp.x = selectedCorner.x+w;
				temp.width = -w;
				temp.height = h;
			} else if (selectedCorner.equals(corners[2])) {
				temp.y = selectedCorner.y+h;
				temp.width = w;
				temp.height = -h;
			} else if (selectedCorner.equals(corners[3])) {
				temp.x = selectedCorner.x+w;
				temp.y = selectedCorner.y+h;
				temp.width = -w;
				temp.height = -h;
			}

			temp = Main.fixRect(temp);
			
			if (temp.x >= Main.ship.anchor.x && temp.x+temp.width < Main.GRID_W*35+35 && !Main.doesRectOverlap(temp, bounds)) {
				bounds.x = temp.x;
				bounds.width = (temp.width == 0) ? 35 : temp.width;
			}
			if (temp.y >= Main.ship.anchor.y && temp.y+temp.height < Main.GRID_H * 35+35 && !Main.doesRectOverlap(temp, bounds)) {
				bounds.y = temp.y;
				bounds.height = (temp.height == 0) ? 35 : temp.height;
			}

			Main.updateCorners(this);
			updateCorners();
			Main.removeUnalignedDoors();
			
			Main.canvas.redraw(origin.x-2, origin.y-2, origin.width+4, origin.height+4, false);
			Main.canvas.redraw(temp.x, temp.y, temp.width, temp.height, false);
		} else if (Main.ship == null) {
			bounds.width = w;
			bounds.height = h;
			origin.x = bounds.x;
			origin.y = bounds.y;
			origin.width = bounds.width;
			origin.height = bounds.height;
		}
		
		if (sysBox != null)
			sysBox.updateLocation();
	}
	
	public static int getDefaultSlot(Systems sys) {
		int i = (sys.equals(Systems.ENGINES))
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
		return i;
	}
	
	public static Slide getDefaultDir(Systems sys) {
		Slide dir = (sys.equals(Systems.ENGINES))
						? Slide.DOWN
						: (sys.equals(Systems.PILOT))
							? Slide.RIGHT
							: (sys.equals(Systems.SHIELDS))
								? Slide.LEFT
								: (sys.equals(Systems.WEAPONS))
									? Slide.UP
									: Slide.NO;
		return dir;
	}
	
	@Override
	public void paintControl(PaintEvent e) {
		super.paintControl(e);
		
		int prevAlpha = e.gc.getAlpha();
		int prevWidth = e.gc.getLineWidth();
		Color prevBg = e.gc.getForeground();
		
		e.gc.setAlpha(255);
		e.gc.setLineWidth(1);
		e.gc.setForeground(grid_color);
		
		for (int i=1; i < bounds.width/35; i++)
			e.gc.drawLine(bounds.x + i*35, bounds.y, bounds.x + i*35, bounds.y+bounds.height);
		for (int i=1; i < bounds.height/35; i++)
			e.gc.drawLine(bounds.x, bounds.y + i*35, bounds.x+bounds.width, bounds.y + i*35);

		e.gc.setForeground(prevBg);
		if (selected) {
			prevBg = e.gc.getBackground();

			e.gc.setBackground(getBorderColor());
			e.gc.setAlpha(128);
			// resize corners
			e.gc.fillRectangle(bounds.x, bounds.y, 10, 10);
			e.gc.fillRectangle(bounds.x+bounds.width-10, bounds.y, 10, 10);
			e.gc.fillRectangle(bounds.x, bounds.y+bounds.height-10, 10, 10);
			e.gc.fillRectangle(bounds.x+bounds.width-10, bounds.y+bounds.height-10, 10, 10);
			
			e.gc.setBackground(prevBg);
		}
		
		e.gc.setLineWidth(prevWidth);
		e.gc.setAlpha(prevAlpha);
	}
	
	@Override
	public void mouseDown(MouseEvent e)
	{
		if (bounds.contains(e.x, e.y)) {
			select();
			Main.dragRoomAnchor.x = e.x - bounds.x;
			Main.dragRoomAnchor.y = e.y - bounds.y;
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
	}
	
	@Override
	public void mouseUp(MouseEvent e) {
		if (e.button == 1) {
			if (resize)
				bounds = Main.fixRect(bounds);
			move = false;
			resize = false;
			if (!bounds.contains(e.x, e.y) && selected);
				//deselect();
		} else if (e.button == 3) {
			if (bounds.contains(e.x, e.y)) {
				Main.menuSystem.setVisible(true);
			}
		}
		Main.cursor.setVisible(true);
	}

	@Override
	public void mouseMove(MouseEvent e) {
		if (selected && move) {
			setLocation(e.x - Main.dragRoomAnchor.x, e.y - Main.dragRoomAnchor.y);
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
	
	public void select() {
		if (Main.selectedRoom != null)
			Main.selectedRoom.deselect();
		selected = true;
		move = !isPinned();
		setBorderColor(new RGB(0, 0, 255));
		if (sysBox == null || sysBox.isAvailable()) {
			setColor(new RGB(170, 170, 255));
		} else if (sysBox != null && !sysBox.isAvailable()) {
			setColor(new RGB(180, 100, 220));
		}
		Main.selectedRoom = this;
		Main.updateCorners(this);
		Main.canvasRedraw(bounds, false);
	}
	
	public void deselect() {
		selected = false;
		move = false;
		setBorderColor(new RGB(0, 0, 0));
		if (sysBox == null || sysBox.isAvailable()) {
			setColor(new RGB(230, 225, 220));
		} else if (sysBox != null && !sysBox.isAvailable()) {
			setColor(new RGB(220, 100, 100));
		}
		Main.selectedRoom = null;
		Main.canvas.redraw(bounds.x-2, bounds.y-2, bounds.width+4, bounds.height+4, false);
	}
	
	public void add(FTLShip ship) {
		ship.rooms.add(this);
		Main.layeredPainter.add(this, LayeredPainter.ROOM);
		Main.idList.add(id);
	}

	public void dispose() {
		if (sysBox != null)
			sysBox.unassign();
		Cache.checkInImage(this, img);
		Main.layeredPainter.remove(this);
		Main.idList.remove(id);
		super.dispose();
		grid_color = null;
		sysImg = null;
		img = null;
	}
}
