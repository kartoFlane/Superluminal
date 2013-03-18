package com.kartoflane.superluminal.elements;

import java.io.Serializable;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.painter.Cache;
import com.kartoflane.superluminal.painter.LayeredPainter;
import com.kartoflane.superluminal.painter.PaintBox;


/**
 * Class representing a single door in a ship.
 *
 */
public class FTLDoor extends PaintBox implements Serializable, DraggableBox {
	private static final long serialVersionUID = 4263579449630517639L;
	public boolean horizontal;
	private Color lineColor = null;
	private RGB line_rgb = null;
	private Color doorColor = null;
	private RGB door_rgb = null;
	private boolean move = false;
	
	public int leftId = -2;
	public int rightId = -2;
	
	public void stripUnserializable() {
		super.stripUnserializable();
		Cache.checkInColor(this, line_rgb);
		lineColor = null;
		Cache.checkInColor(this, door_rgb);
		doorColor = null;
	}
	
	public void loadUnserializable() {
		super.loadUnserializable();
		lineColor = Cache.checkOutColor(this, line_rgb);
		doorColor = Cache.checkOutColor(this, door_rgb);
	}
	
	public FTLDoor() {
		super();
		setBorderThickness(2);
		setLineColor(new RGB(0,0,0));
		setDoorColor(new RGB(255, 150, 48));
	}
	
	public FTLDoor(int x, int y, boolean horizontal) {
		this();
		this.setBounds((horizontal) ? (new Rectangle(x, y, 31, 6)) : (new Rectangle(x, y, 6, 31)));
		this.horizontal = horizontal;
	}
	
	public FTLDoor(Point pos, boolean horizontal) {
		this(pos.x, pos.y, horizontal);
	}
	
	public void fixRectOrientation() {
		this.getBounds().width = (horizontal) ? (31) : (6);
		this.getBounds().height = (horizontal) ? (6) : (31);
	}
	
	public void setLocation(int x, int y) {
		Rectangle oldBounds = Main.cloneRect(bounds);
		Rectangle temp = Main.cloneRect(bounds);
		
		temp.x = Math.round(x / 35) * 35 + (horizontal ? 2 : -3);
		temp.y = Math.round(y / 35) * 35 + (horizontal ? -3 : 2);
		
		if (Main.wallToDoor(temp) == null && Main.isDoorAtWall(temp)) {
			bounds.x = temp.x;
			bounds.y = temp.y;
		}
		Main.canvas.redraw(oldBounds.x-2, oldBounds.y-2, oldBounds.width+4, oldBounds.height+4, false);
		Main.canvas.redraw(bounds.x-2, bounds.y-2, bounds.width+4, bounds.height+4, false);
		
		Main.updateSelectedPosText();
	}
	
	public void setLocationAbsolute(int x, int y) {
		Rectangle oldBounds = Main.cloneRect(bounds);
		bounds.x = Math.round(x / 35) * 35 + (horizontal ? 2 : -3);
		bounds.y = Math.round(y / 35) * 35 + (horizontal ? -3 : 2);
		
		Main.canvas.redraw(oldBounds.x-2, oldBounds.y-2, oldBounds.width+4, oldBounds.height+4, false);
		Main.canvas.redraw(bounds.x-2, bounds.y-2, bounds.width+4, bounds.height+4, false);
	}
	
	public Point getLocation() {
		// no idea why, but if those values are not added, then the doors are shifted a bit towards upper left
		return new Point(bounds.x+10, bounds.y+4);
	}
	
	public void setLineColor(RGB rgb) {
		if (line_rgb != null)
			Cache.checkInColor(this, line_rgb);
		lineColor = Cache.checkOutColor(this, rgb);
		line_rgb = rgb;
	}
	
	public void setDoorColor(RGB rgb) {
		if (door_rgb != null)
			Cache.checkInColor(this, door_rgb);
		doorColor = Cache.checkOutColor(this, rgb);
		door_rgb = rgb;
	}
	
	@Override
	public void paintControl(PaintEvent e) {
		if (!Main.tltmGib.getSelection()) {
			setAlpha(Main.btnCloaked.getSelection() ? 255/3 : 255);
			super.paintControl(e);
			
			Color prevBg = e.gc.getBackground();
			Color prevFg = e.gc.getForeground();
			int prevAlpha = e.gc.getAlpha();
			int prevWidth = e.gc.getLineWidth();
			
			e.gc.setAlpha(alpha);
			e.gc.setLineWidth(1);
			e.gc.setBackground(lineColor);
			e.gc.setForeground(lineColor);
	
			if (horizontal) {
				e.gc.fillRectangle(getBounds().x, getBounds().y+1, 5, 4);
				e.gc.fillRectangle(getBounds().x+getBounds().width-4, getBounds().y+1, 5, 4);
				e.gc.drawRectangle(getBounds().x+5, getBounds().y, 21, 5);
		
				e.gc.setBackground(doorColor);
				
				e.gc.fillRectangle(getBounds().x+6, getBounds().y+1, 20, 4);
				e.gc.drawRectangle(getBounds().x+15, getBounds().y, 1, 5);
				
				if (selected && isPinned())
					e.gc.drawImage(pin, bounds.x+9, bounds.y-15);
			} else {
				e.gc.fillRectangle(getBounds().x+1, getBounds().y, 4, 5);
				e.gc.fillRectangle(getBounds().x+1, getBounds().y+getBounds().height-4, 4, 5);
				e.gc.drawRectangle(getBounds().x, getBounds().y+5, 5, 21);
	
				e.gc.setBackground(doorColor);
				
				e.gc.fillRectangle(getBounds().x+1, getBounds().y+6, 4, 20);
				e.gc.drawRectangle(getBounds().x, getBounds().y+15, 5, 1);
				
				if (selected && isPinned())
					e.gc.drawImage(pin, bounds.x-15, bounds.y+9);
			}
			
			if (Main.tltmPointer.getSelection() && selected) {
				e.gc.setAlpha(255);
				e.gc.setLineWidth(3);
				
				Color tempColor = null;
				RGB tempRGB = null;
				FTLRoom r = null;
				
				if (leftId != -2) {
					tempRGB = new RGB(128, 0, 128); // magenta
					tempColor = Cache.checkOutColor(this, tempRGB);
					r = Main.ship.getRoomWithId(leftId);
					
					e.gc.setForeground(tempColor);
					e.gc.drawLine(bounds.x + bounds.width/2, bounds.y + bounds.height/2,
							r.getBounds().x + r.getBounds().width/2, r.getBounds().y + r.getBounds().height/2);
					
					Cache.checkInColor(this, tempRGB);
					tempColor = null;
				}
				
				if (rightId != -2) {
					tempRGB = new RGB(0, 128, 128); // teal
					tempColor = Cache.checkOutColor(this, tempRGB);
					r = Main.ship.getRoomWithId(rightId);
					
					e.gc.setForeground(tempColor);
					e.gc.drawLine(bounds.x + bounds.width/2, bounds.y + bounds.height/2,
							r.getBounds().x + r.getBounds().width/2, r.getBounds().y + r.getBounds().height/2);
					
					Cache.checkInColor(this, tempRGB);
					tempColor = null;
				}
			}
			
			e.gc.setAlpha(prevAlpha);
			e.gc.setLineWidth(prevWidth);
			e.gc.setBackground(prevBg);
			e.gc.setForeground(prevFg);
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {
		move = false;
		Main.cursor.setVisible(true);
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (bounds.contains(e.x, e.y) && e.button == 1) {
			if (Main.selectedDoor != null) Main.selectedDoor.deselect();
			select();
		} else {
			deselect();
		}
		Main.cursor.setVisible(false);
	}

	@Override
	public void mouseMove(MouseEvent e) {
		if (move && !isPinned())
			setLocation(e.x,e.y);
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {}
	
	public void add(FTLShip ship) {
		ship.doors.add(this);
		Main.layeredPainter.add(this, LayeredPainter.DOOR);
	}

	public void select() {
		selected = true;
		move = !isPinned();
		setBorderColor(new RGB(0,0,255));
		setDoorColor(new RGB(200, 150, 200));
		Main.selectedDoor = this;
		Main.canvas.redraw(bounds.x-2, bounds.y-2, bounds.width+4, bounds.height+4, false);
		redrawIdLines();
	}
	
	public void deselect() {
		selected = false;
		move = false;
		setBorderColor(null);
		setDoorColor(new RGB(255, 150, 48));
		Main.canvas.redraw(bounds.x-2, bounds.y-2, bounds.width+4, bounds.height+4, false);
		redrawIdLines();
	}
	
	public void dispose() {
		Main.layeredPainter.remove(this);
		Cache.checkInColor(this, line_rgb);
		Cache.checkInColor(this, door_rgb);
		Main.canvas.redraw(bounds.x-2, bounds.y-2, bounds.width+4, bounds.height+4, false);
		redrawIdLines();
		super.dispose();
	}
	
	private void redrawIdLines() {
		FTLRoom r;
		Point temp;
		if (leftId != -2) {
			r = Main.ship.getRoomWithId(leftId);
			temp = new Point(r.getBounds().x + r.getBounds().width/2, r.getBounds().y + r.getBounds().height/2);
			
			Main.canvas.redraw(Math.min(temp.x, bounds.x + bounds.width/2) -5,
					Math.min(temp.y, bounds.y + bounds.height/2) -5,
					Math.max(temp.x, bounds.x + bounds.width/2) + 10,
					Math.max(temp.y, bounds.y + bounds.height/2) + 10,
					false);
		}
		if (rightId != -2) {
			r = Main.ship.getRoomWithId(rightId);
			temp = new Point(r.getBounds().x + r.getBounds().width/2, r.getBounds().y + r.getBounds().height/2);
			
			Main.canvas.redraw(Math.min(temp.x, bounds.x + bounds.width/2) -5,
					Math.min(temp.y, bounds.y + bounds.height/2) -5,
					Math.max(temp.x, bounds.x + bounds.width/2) + 10,
					Math.max(temp.y, bounds.y + bounds.height/2) + 10,
					false);
		}
	}

	@Override
	public void mouseHover(MouseEvent e) {}

	@Override
	public void setOffset(int x, int y) {}

	@Override
	public Point getOffset() {
		return null;
	}
}
