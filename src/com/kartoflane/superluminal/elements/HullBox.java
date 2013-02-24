package com.kartoflane.superluminal.elements;


import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.painter.Cache;
import com.kartoflane.superluminal.painter.ImageBox;

@SuppressWarnings("serial")
public class HullBox extends ImageBox implements DraggableBox {
	protected String floorPath;
	protected Image floorImage;
	protected String cloakPath;
	protected Image cloakImage;
	public boolean move = false;
	private Point orig;
	private Point offset;

	public HullBox() {
		super();
		orig = new Point(0,0);
		offset = new Point(0,0);
	}
	
	public void setHullImage(String path) {
		if (this.path != null)
			Cache.checkInImageAbsolute(this, this.path);
		
		Main.ship.imagePath = path;
		this.shrinkWrap = true;
		this.path = path;
		image = null;

		if (path != null) {
			image = Cache.checkOutImageAbsolute(this, path);
			setSize(image.getBounds().width, image.getBounds().height);
		}
	}
	
	public void setFloorImage(String path) {
		if (this.floorPath != null)
			Cache.checkInImageAbsolute(this, this.floorPath);

		Main.ship.floorPath = path;
		this.shrinkWrap = true;
		this.floorPath = path;
		floorImage = null;
		
		if (path != null)
			floorImage = Cache.checkOutImageAbsolute(this, path);
	}
	
	public void setCloakImage(String path) {
		if (this.cloakPath != null)
			Cache.checkInImageAbsolute(this, this.cloakPath);

		Main.ship.cloakPath = path;
		this.shrinkWrap = true;
		this.cloakPath = path;
		cloakImage = null;
		
		if (path != null)
			cloakImage = Cache.checkOutImageAbsolute(this, path);
	}
		
	
	public void setLocation(int x, int y) {
		Rectangle oldBounds = Main.cloneRect(bounds);
		bounds.x = x;
		bounds.y = y;
		
		if (Main.ship != null) {
			Main.ship.imageRect.x = x;
			Main.ship.imageRect.y = y;
			
			for (FTLGib g : Main.ship.gibs) {
				g.setLocationRelative(g.position.x, g.position.y);
			}
		}
		
		//setVisible(Main.canvas.getBounds().contains(x, y) && Main.canvas.getBounds().contains(x+bounds.width, y+bounds.height));
		Main.canvasRedraw(oldBounds, false);
		Main.canvasRedraw(bounds, false);
		
		Main.updateSelectedPosText();
	}
	
	@Override
	protected void paintBorder(PaintEvent e) {
		if (borderColor != null && !Main.tltmGib.getSelection()) {
			Color prevColor = e.gc.getForeground();
			int prevLineWidth = e.gc.getLineWidth();
			
			e.gc.setForeground(borderColor);
			e.gc.setLineWidth(borderThickness);

			e.gc.drawRectangle(bounds.x+borderThickness/2, bounds.y+borderThickness/2, (bounds.width-1)-borderThickness, (bounds.height-1)-borderThickness);

			e.gc.setForeground(prevColor);
			e.gc.setLineWidth(prevLineWidth);
		}
	}
	
	@Override
	public void paintControl(PaintEvent e) {
		if (!Main.tltmGib.getSelection()) {
			int prevAlpha = e.gc.getAlpha();
			Color prevBg = e.gc.getBackground();
			
			paintBorder(e);
	
			e.gc.setAlpha(alpha/255 * 32);
			if (borderColor != null)
				e.gc.setBackground(borderColor);
			
			if (Main.hullSelected)
				e.gc.fillRectangle(bounds);
			
			e.gc.setAlpha(Main.btnCloaked.getSelection() ? alpha/3 : alpha);
			if (image != null && Main.showHull)
				e.gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, bounds.x, bounds.y, bounds.width, bounds.height);
			if (floorImage != null && Main.showFloor)
				e.gc.drawImage(floorImage, 0, 0, floorImage.getBounds().width, floorImage.getBounds().height, bounds.x, bounds.y, bounds.width, bounds.height);
			if (cloakImage != null && (Main.showHull || Main.showFloor) && Main.btnCloaked.getSelection()) {
				e.gc.setAlpha(alpha);
				e.gc.drawImage(cloakImage, 0, 0, cloakImage.getBounds().width, cloakImage.getBounds().height, bounds.x - 10, bounds.y - 10, bounds.width + 20, bounds.height + 20);
			}
			
			if (Main.hullSelected && isPinned())
				e.gc.drawImage(pin, bounds.x+5, bounds.y+5);
	
			e.gc.setAlpha(prevAlpha);
			e.gc.setBackground(prevBg);
		}
	}
	
	@Override
	public void mouseUp(MouseEvent e)
	{
		if (move) {
			Main.cursor.setLocationAbsolute(bounds.x, bounds.y);
			Main.cursor.setSize(bounds.width, bounds.height);
		}
		move = false;
		Main.cursor.setVisible(true);
		if (!Main.canvas.getBounds().contains(bounds.x+bounds.width-35, bounds.y+bounds.height-35) 
				&& !Main.canvas.getBounds().contains(bounds.x+35, bounds.y+35)) {
			Point p = new Point(bounds.x, bounds.y);
			bounds.x = orig.x;
			bounds.y = orig.y;
			Main.canvas.redraw(p.x, p.y, bounds.width, bounds.height, false);
			Main.canvasRedraw(bounds, false);
		} else {
			orig.x = bounds.x;
			orig.y = bounds.y;
		}
	}

	@Override
	public void mouseDown(MouseEvent e)
	{
		if (bounds.contains(e.x, e.y) && (Main.showHull || Main.showFloor)) {
			if (e.button == 1) {
				select();
				offset.x = e.x - bounds.x;
				offset.y = e.y - bounds.y;
				Main.cursor.setVisible(false);
				Main.shieldBox.deselect();
			} else if (e.button == 3) {
				deselect();
				Main.shieldBox.mouseDown(e);
			}
		} else if (Main.hullSelected) {
			deselect();
		}
	}

	@Override
	public void mouseMove(MouseEvent e)
	{
		if (move && !isPinned()) {
			if (isVisible()) {
				Rectangle oldBounds = Main.cloneRect(bounds);
				
				if (Main.modCtrl) {
					setLocation(orig.x - (orig.x + offset.x - e.x)/10, orig.y - (orig.y + offset.y - e.y)/10);
				} else {
					setLocation(e.x - offset.x, e.y - offset.y);
				}
				Main.canvasRedraw(oldBounds, false);
				Main.canvasRedraw(bounds, false);
			} else {
				setLocation(e.x - offset.x, e.y - offset.y);
			}
		}
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {}

	public void select() {
		Main.hullSelected = true;
		move = true;
		setBorderColor(new RGB(0,0,255));
		
		Main.canvasRedraw(bounds, false);
	}
	
	public void deselect() {
		Main.hullSelected = false;
		move = false;
		setBorderColor(null);
		
		Main.canvasRedraw(bounds, false);
	}
	
	@Override
	public void dispose() {
		if (cloakPath != null) Cache.checkInImageAbsolute(this, cloakPath);
		if (floorPath != null) Cache.checkInImageAbsolute(this, floorPath);
		if (path != null) Cache.checkInImageAbsolute(this, path);
		floorPath = null;
		path = null;
		super.dispose();
	}

	@Override
	public void mouseHover(MouseEvent e) {}

	@Override
	public void setOffset(int x, int y) {
		orig.x = x;
		orig.y = y;
	}

	@Override
	public Point getOffset() {
		return offset;
	}
}
