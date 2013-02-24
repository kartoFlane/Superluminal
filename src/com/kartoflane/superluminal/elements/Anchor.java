package com.kartoflane.superluminal.elements;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.painter.PaintBox;


@SuppressWarnings("serial")
public class Anchor extends PaintBox implements DraggableBox {
	private Color color;
	private Color pressedColor;
	private Color borderColor;
	private Color xLine;
	private Color yLine;
	private Color vLine;
	private Rectangle box;
	public boolean moveAnchor;
	public boolean moveVertical;

	public Anchor() {
		super();
		visible = false;
		color = new Color(Main.shell.getDisplay(), 0, 255, 255);
		pressedColor = new Color(Main.shell.getDisplay(), 0, 128, 128);
		borderColor = new Color(Main.shell.getDisplay(), 0, 0, 0);
		xLine = new Color(Main.shell.getDisplay(), 255, 0, 0);
		yLine = new Color(Main.shell.getDisplay(), 0, 255, 0);
		vLine = new Color(Main.shell.getDisplay(), 0, 0, 255);
		box = new Rectangle(0,0,FTLShip.ANCHOR,FTLShip.ANCHOR);
		
		moveAnchor = false;
		moveVertical = false;
	}
	
	public void setLocation(int x, int y) {
		setLocation(x, y, true);
	}
	
	public void setLocation(int x, int y, boolean updateElements) {
		if (Main.ship != null && ((x - Main.ship.anchor.x)/35 != 0 || (y - Main.ship.anchor.y)/35 != 0) && updateElements)
			Main.ship.updateElements(new Point(x, y), FTLShip.AxisFlag.BOTH);
		bounds.x = x;
		bounds.y = y;
		if (Main.ship != null && Main.ship.vertical > 0)
			bounds.y -= Main.ship.vertical - 2;
		
		box.x = x;
		box.y = y;
		
		if (x != 0)
			box.x -=FTLShip.ANCHOR;
		if (y != 0)
			box.y -=FTLShip.ANCHOR;
	}
	
	public Rectangle getBox() {
		return box;
	}
	
	@Override
	public void paintControl(PaintEvent e) {
		Color prevFgColor = e.gc.getForeground();
		
		e.gc.setForeground(xLine);
		if (Main.tltmGib.getSelection()) {
			e.gc.drawLine(Main.hullBox.getBounds().x, Main.hullBox.getBounds().y, Main.hullBox.getBounds().x, Main.GRID_H*35);
			e.gc.drawLine(Main.hullBox.getBounds().x, Main.hullBox.getBounds().y, Main.GRID_W*35, Main.hullBox.getBounds().y);
		}
		
		if (Main.showAnchor && !Main.tltmGib.getSelection()) {
			super.paintControl(e);
			Color prevBgColor = e.gc.getBackground();
			
			int prevAlpha = e.gc.getAlpha();
			int prevWidth = e.gc.getLineWidth();
	
			e.gc.setAlpha(255);
			e.gc.setLineWidth(2);
			
			// anchor bounds lines
			e.gc.setForeground(yLine);
			e.gc.drawLine(Main.ship.anchor.x, Main.ship.anchor.y, Main.ship.anchor.x, Main.GRID_H*35);
			e.gc.setForeground(xLine);
			e.gc.drawLine(Main.ship.anchor.x, Main.ship.anchor.y, Main.GRID_W*35, Main.ship.anchor.y);
			// draw vertical line
			if (Main.ship.vertical != 0) {
				e.gc.setForeground(vLine);
				e.gc.drawLine(Main.ship.anchor.x, Main.ship.anchor.y - Main.ship.vertical, Main.GRID_W*35, Main.ship.anchor.y - Main.ship.vertical);
			}
	
			if (moveAnchor) {
				e.gc.setBackground(pressedColor);
			} else {
				e.gc.setBackground(color);
			}
			
			e.gc.setForeground(borderColor);
			
			e.gc.fillRectangle(box.x, box.y, FTLShip.ANCHOR, FTLShip.ANCHOR);
			e.gc.drawRectangle(box.x, box.y, FTLShip.ANCHOR, FTLShip.ANCHOR);
			
			e.gc.setBackground(prevBgColor);
			e.gc.setForeground(prevFgColor);
			e.gc.setAlpha(prevAlpha);
			e.gc.setLineWidth(prevWidth);
		}
	}

	@Override
	public void mouseUp(MouseEvent e)
	{
		moveAnchor = false;
		moveVertical = false;
		Main.cursor.setVisible(true);
		Main.canvasRedraw(box, false);
	}

	@Override
	public void mouseDown(MouseEvent e)
	{
		if (box.contains(e.x, e.y) && isVisible() && Main.showAnchor) {
			Main.cursor.setVisible(false);
			if (e.button == 1) {
				moveAnchor = true;
			} else if (e.button == 3) {
				moveVertical = true;
				if (e.count == 2) {
					mouseDoubleClick(e);
				}
			}
			Main.canvasRedraw(box, false);
		}
	}

	@Override
	public void mouseMove(MouseEvent e)
	{
		if (moveAnchor) {
			int x,y;
			Rectangle oldBounds = Main.cloneRect(bounds);
			oldBounds.x -= 15;
			oldBounds.y -= 15;
			if (Main.ship.vertical != 0) {
				if (Main.ship.vertical > 0) {
					oldBounds.y = Main.ship.anchor.y - Main.ship.vertical-2;
					oldBounds.height += Main.ship.vertical-4;
				}
			}
			
			x = e.x;
			y = e.y;
			
			Point size = Main.ship.computeShipSize();

			if (Main.modShift) {
				Point p = Main.ship.findLowBounds();
				x = Math.min(x, p.x);
				y = Math.min(y, p.y);
				
				Main.ship.offset.x = (p.x - x)/35;
				Main.ship.offset.y = (p.y - y)/35;
			}
			
			x = Math.max(x, 0);
			y = Math.max(y, 0);
			x = Math.min(x, Main.GRID_W*35-35 - size.x - Main.ship.offset.x*35);
			y = Math.min(y, Main.GRID_H*35-35 - size.y - Main.ship.offset.y*35);
			
			
			x = Main.roundToGrid(x);
			y = Main.roundToGrid(y);

			setLocation(x,y,!Main.modShift);
			Main.ship.anchor.x = x;
			Main.ship.anchor.y = y;
			setSize(Main.GRID_W*35, Main.GRID_H*35);
			
			Main.canvasRedraw(oldBounds, false);
			/*
			Main.canvas.redraw(0, Main.ship.anchor.y + Main.ship.vertical - 2, Main.GRID_W*35, 4, false);
			Main.canvas.redraw(Main.ship.anchor.x - FTLShip.ANCHOR - 2, Main.ship.anchor.y - FTLShip.ANCHOR - 2, Main.GRID_W*35, FTLShip.ANCHOR + 4, false);
			Main.canvas.redraw(Main.ship.anchor.x - FTLShip.ANCHOR - 2, Main.ship.anchor.y - FTLShip.ANCHOR - 2, FTLShip.ANCHOR + 4, Main.GRID_H*35, false);
			*/
		} else if (moveVertical) {
			int prevV = Main.ship.vertical;
			Main.ship.vertical = Main.ship.anchor.y - e.y;
			if (Main.ship.vertical > 0) {
				bounds.y -= Main.ship.vertical;
				bounds.height += Main.ship.vertical;
			}
			Main.canvas.redraw(Main.ship.anchor.x, Main.ship.anchor.y - prevV - 2, Main.GRID_W*35, 4, false);
			Main.canvas.redraw(Main.ship.anchor.x, Main.ship.anchor.y - Main.ship.vertical - 2, Main.GRID_W*35, 4, false);
		}
	}

	@Override
	public void mouseDoubleClick(MouseEvent e)
	{
		if (e.button == 3) {
			int prevV = Main.ship.vertical;
			Main.ship.vertical = 0;
			Main.canvas.redraw(Main.ship.anchor.x, Main.ship.anchor.y - prevV - 2, Main.GRID_W*35, 4, false);
			Main.canvas.redraw(Main.ship.anchor.x, Main.ship.anchor.y - Main.ship.vertical - 2, Main.GRID_W*35, 4, false);
		}
	}

	@Override
	public void dispose() {
		color.dispose();
		pressedColor.dispose();
		xLine.dispose();
		yLine.dispose();
		vLine.dispose();
		super.dispose();
	}

	@Override
	public void select() {}

	@Override
	public void deselect() {}

	@Override
	public void mouseHover(MouseEvent e) {}

	@Override
	public void setOffset(int x, int y) {}

	@Override
	public Point getOffset() {
		return null;
	}
}
