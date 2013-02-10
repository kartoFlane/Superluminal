package com.kartoflane.superluminal.elements;

import java.io.Serializable;

import org.eclipse.swt.SWT;
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
public class FTLDoor extends PaintBox implements Serializable, DraggableBox
{
	private static final long serialVersionUID = 4263579449630517639L;
	public boolean horizontal;
	private Color lineColor = null;
	private RGB line_rgb = null;
	private Color doorColor = null;
	private RGB door_rgb = null;
	
	public FTLDoor() {
		super();
		setLineColor(new RGB(0,0,0));
		setDoorColor(new RGB(255, 150, 48));
	}
	
	public FTLDoor(int x, int y, boolean horizontal) {
		new FTLDoor();
		this.setBounds((horizontal) ? (new Rectangle(x, y, 31, 6)) : (new Rectangle(x, y, 6, 31)));
		this.horizontal = horizontal;
	}
	
	public FTLDoor(Point pos, boolean horizontal) {
		new FTLDoor(pos.x, pos.y, horizontal);
	}
	
	public void fixRectOrientation() {
		this.getBounds().width = (horizontal) ? (31) : (6);
		this.getBounds().height = (horizontal) ? (6) : (31);
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
		super.paintControl(e);
		
		Color prevBg = e.gc.getBackground();
		Color prevFg = e.gc.getForeground();
		int prevAlpha = e.gc.getAlpha();
		int prevWidth = e.gc.getLineWidth();
		
		e.gc.setAlpha(255);
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
		} else {
			e.gc.fillRectangle(getBounds().x+1, getBounds().y, 4, 5);
			e.gc.fillRectangle(getBounds().x+1, getBounds().y+getBounds().height-4, 4, 5);
			e.gc.drawRectangle(getBounds().x, getBounds().y+5, 5, 21);

			e.gc.setBackground(doorColor);
			
			e.gc.fillRectangle(getBounds().x+1, getBounds().y+6, 4, 20);
			e.gc.drawRectangle(getBounds().x, getBounds().y+15, 5, 1);
		}
		
		e.gc.setAlpha(prevAlpha);
		e.gc.setLineWidth(prevWidth);
		e.gc.setBackground(prevBg);
		e.gc.setForeground(prevFg);
	}

	@Override
	public void mouseUp(MouseEvent e) {
		
	}

	@Override
	public void mouseDown(MouseEvent e)
	{
		if (bounds.contains(e.x, e.y)) {
			selected = true;
			setBorderColor(new RGB(0, 0, 255));
			Main.selectedDoor = this;
		} else if (!Main.modShift) {
			if (selected) {
				setBorderColor(null);
				Main.selectedDoor = null;
			}
			selected = false;
		}
	}

	@Override
	public void mouseMove(MouseEvent e) {
		
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {}
	
	public void add(FTLShip ship) {
		ship.doors.add(this);
		Main.layeredPainter.add(this, LayeredPainter.DOOR);
	}

	public void dispose() {
		Main.layeredPainter.remove(this);
		Cache.checkInColor(this, line_rgb);
		Cache.checkInColor(this, door_rgb);
		super.dispose();
	}
}
