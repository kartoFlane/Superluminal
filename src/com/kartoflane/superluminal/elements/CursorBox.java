package com.kartoflane.superluminal.elements;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.painter.Cache;
import com.kartoflane.superluminal.painter.PaintBox;


public class CursorBox extends PaintBox implements DraggableBox {
	public CursorBox() {
	}

	public CursorBox(RGB rgb) {
		this();
		setColor(rgb);
	}

	public void setColor(RGB rgb) {
		if (border_rgb != null)
			Cache.checkInColor(this, border_rgb);
		borderColor = Cache.checkOutColor(this, rgb);
		border_rgb = rgb;
	}
	
	public void setLocation(int x, int y) {
		bounds.x = Main.roundToGrid(x);
		bounds.y = Main.roundToGrid(y);
	}
	
	public void setSize(int w, int h) {
		bounds.width = w;
		bounds.height = h;
	}
	
	@Override
	public void paintControl(PaintEvent e) {
		if (borderColor != null) {
			Color prevColor = e.gc.getForeground();
			int prevLineWidth = e.gc.getLineWidth();
			
			e.gc.setForeground(borderColor);
			e.gc.setLineWidth(borderThickness);
			
			e.gc.drawRectangle(bounds.x+1, bounds.y+1, bounds.width-2, bounds.height-2);
		
			e.gc.setForeground(prevColor);
			e.gc.setLineWidth(prevLineWidth);
		}
	}
	
	@Override
	public void paintBorder(PaintEvent e) {}
	
	public int round(double d) {
		return (int) Math.floor(d);
	}

	@Override
	public void dispose() {
		Cache.checkInColor(this, border_rgb);
		super.dispose();
	}

	@Override
	public void mouseUp(MouseEvent e)
	{
		
	}

	@Override
	public void mouseDown(MouseEvent e)
	{
		
	}

	@Override
	public void mouseMove(MouseEvent e)
	{
		if (isVisible()) {
				Rectangle oldBounds = Main.cloneRect(bounds);
				Rectangle temp;
				
				if (Main.tltmPointer.getSelection()) {
					temp = Main.getDoorAt(e.x, e.y);
					if (temp != null) {
						Main.copyRect(temp, bounds);
					} else {
						temp = Main.getRectAt(e.x, e.y);
						Main.copyRect(temp, bounds);
						if (Main.ship != null && Main.doesRectOverlap(bounds, null)) {
							Main.copyRect(Main.getRoomContainingRect(bounds).getBounds(), bounds);
						}
					}
				} else if (Main.tltmRoom.getSelection()) {
					temp = Main.getRectAt(e.x, e.y);
					Main.copyRect(temp, bounds);
				}
				setSize(bounds.width+1, bounds.height+1);
	
				Main.canvas.redraw(oldBounds.x, oldBounds.y, oldBounds.width+2, oldBounds.height+2, false);
				Main.canvasRedraw(bounds, false);
		} else {
			setLocation(e.x, e.y);
		}
	}

	@Override
	public void mouseDoubleClick(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}
}
