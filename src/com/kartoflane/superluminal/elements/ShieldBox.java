package com.kartoflane.superluminal.elements;


import java.awt.AWTException;
import java.awt.Robot;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.painter.Cache;
import com.kartoflane.superluminal.painter.ImageBox;

@SuppressWarnings("serial")
public class ShieldBox extends ImageBox implements DraggableBox {
	public boolean move = false;
	private Point orig;
	private Point offset;
	private Color ovalColor = null;
	private RGB oval_rgb = null;
	private boolean resize = false;

	public ShieldBox() {
		super();
		orig = new Point(0,0);
		offset = new Point(0,0);
		oval_rgb = new RGB(32,170,255);
		ovalColor = Cache.checkOutColor(this, oval_rgb);
	}
	
	public void setLocation(int x, int y) {
		Rectangle oldBounds = Main.cloneRect(bounds);
		bounds.x = x;
		bounds.y = y;
		Main.shieldEllipse.x = x;
		Main.shieldEllipse.y = y;
		
		if (Main.ship != null) {
			Main.ship.ellipse.x = (Main.shieldEllipse.x + Main.shieldEllipse.width/2) - (Main.ship.findLowBounds().x + Main.ship.computeShipSize().x/2);
			Main.ship.ellipse.y = (Main.shieldEllipse.y + Main.shieldEllipse.height/2) - (Main.ship.findLowBounds().y + Main.ship.computeShipSize().y/2) - ((Main.ship.isPlayer) ? 0 : 110);
		}
		
		Main.canvasRedraw(oldBounds, false);
		Main.canvasRedraw(bounds, false);
		
		Main.updateSelectedPosText();
	}
	
	public void setLocationCenter(int x, int y) {
		setLocation(x - bounds.width/2, y - bounds.height/2);
	}
	
	public void setSize(int w, int h) {
		Rectangle oldBounds = Main.cloneRect(bounds);
		bounds.width = w;
		bounds.height = h;
		Main.shieldEllipse.width = w;
		Main.shieldEllipse.height = h;
		
		if (Main.ship != null) {
			Main.ship.ellipse.width = Main.shieldEllipse.width/2;
			Main.ship.ellipse.height = Main.shieldEllipse.height/2;
		}
		
		Main.canvasRedraw(oldBounds, false);
		Main.canvasRedraw(bounds, false);
	}
	
	public void setImage(String path, boolean shrinkWrap) {
		if (this.path != null) {
			Cache.checkInImage(this, this.path);
		}

		this.shrinkWrap = shrinkWrap;
		Main.ship.shieldPath = path;
		this.path = path;
		image = null;
		
		if (path != null)
			image = Cache.checkOutImageAbsolute(this, path);
		
		if (Main.ship.isPlayer) {
			if (Main.loadShield && image != null) {
				Rectangle temp = image.getBounds();
				Main.shieldEllipse.x = Main.ship.anchor.x + Main.ship.offset.x*35 + Main.ship.computeShipSize().x/2 - temp.width/2 + Main.ship.ellipse.x;
				Main.shieldEllipse.y = Main.ship.anchor.y + Main.ship.offset.y*35 + Main.ship.computeShipSize().y/2 - temp.height/2 + Main.ship.ellipse.y;
				Main.shieldEllipse.width = temp.width;
				Main.shieldEllipse.height = temp.height;
			} else {
				Main.shieldEllipse.x = Main.ship.anchor.x + Main.ship.offset.x*35 + Main.ship.computeShipSize().x/2 - Main.ship.ellipse.width + Main.ship.ellipse.x;
				Main.shieldEllipse.y = Main.ship.anchor.y + Main.ship.offset.y*35 + Main.ship.computeShipSize().y/2 - Main.ship.ellipse.height + Main.ship.ellipse.y;
				Main.shieldEllipse.width = Main.ship.ellipse.width*2;
				Main.shieldEllipse.height = Main.ship.ellipse.height*2;
			}
		} else {
			Main.shieldEllipse.width = Main.ship.ellipse.width*2;
			Main.shieldEllipse.height = Main.ship.ellipse.height*2;
			Main.shieldEllipse.x = Main.ship.anchor.x + Main.ship.offset.x*35 + Main.ship.computeShipSize().x/2 + Main.ship.ellipse.x - Main.ship.ellipse.width;
			Main.shieldEllipse.y = Main.ship.anchor.y + Main.ship.offset.y*35 + Main.ship.computeShipSize().y/2 + Main.ship.ellipse.y - Main.ship.ellipse.height + 110;
		}

		setSize(Main.shieldEllipse.width, Main.shieldEllipse.height);
		setLocation(Main.shieldEllipse.x, Main.shieldEllipse.y);
	}
	
	@Override
	protected void paintBorder(PaintEvent e) {
		if (borderColor != null && selected && !Main.tltmGib.getSelection()) {
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
		if (Main.ship != null && isVisible() && Main.showShield && !Main.tltmGib.getSelection()) {
			int prevAlpha = e.gc.getAlpha();
			Color prevBg = e.gc.getBackground();
			
			if (selected) {
				e.gc.setAlpha(32);
				e.gc.setBackground(borderColor);
				e.gc.fillRectangle(bounds);
			}
			
			if (Main.loadShield && image != null) {
				e.gc.setAlpha(255);
				e.gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, bounds.x, bounds.y, bounds.width, bounds.height);
			} else {
				e.gc.setAlpha(64);
				e.gc.setBackground(ovalColor);
				
				e.gc.fillOval(bounds.x, bounds.y, bounds.width, bounds.height);
			}
			paintBorder(e);
			
			if (selected && isPinned())
				e.gc.drawImage(pin, bounds.x+5, bounds.y+5);
			
			e.gc.setAlpha(prevAlpha);
			e.gc.setBackground(prevBg);
		}
	}
	
	@Override
	public void mouseUp(MouseEvent e) {
		if (move) {
			Main.cursor.setLocationAbsolute(bounds.x, bounds.y);
			if (e.button == 3) 
				Main.cursor.setSize(bounds.width, bounds.height);
		}
		move = false;
		resize = false;
		Main.cursor.setVisible(true);
		
		if (Main.canvas.getBounds().contains(bounds.x+bounds.width-35, bounds.y+bounds.height-35) || Main.canvas.getBounds().contains(bounds.x+35, bounds.y+35)
				|| Main.canvas.getBounds().contains(bounds.x+35, bounds.y+bounds.height-35) || Main.canvas.getBounds().contains(bounds.x+bounds.width-35, bounds.y+35)) {
			orig.x = bounds.x;
			orig.y = bounds.y;
		} else {
			Point p = new Point(bounds.x, bounds.y);
			setLocation(orig.x, orig.y);
			Main.canvas.redraw(p.x, p.y, bounds.width, bounds.height, false);
			Main.canvasRedraw(bounds, false);
		}
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (bounds.contains(e.x, e.y) && Main.showShield) {
			select();
			if (!Main.modAlt) {
				offset.x = e.x - bounds.x;
				offset.y = e.y - bounds.y;
			} else if (resize) {
				offset.x = Main.shieldEllipse.x + Main.shieldEllipse.width/2;
				offset.y = Main.shieldEllipse.y + Main.shieldEllipse.height/2;

				Point p = Main.canvas.toDisplay(Main.shieldEllipse.x+Main.shieldEllipse.width, Main.shieldEllipse.y+Main.shieldEllipse.height);
				Robot robot;
				try {
					robot = new Robot();
					robot.mouseMove(p.x, p.y);
				} catch (AWTException ex) {
				}
			}
			Main.cursor.setVisible(false);
		} else if (selected) {
			deselect();
		}
	}

	@Override
	public void mouseMove(MouseEvent e) {
		if (isVisible()) {
			if (!Main.modAlt && move && !isPinned()) {
				Rectangle oldBounds = Main.cloneRect(bounds);

				if (Main.modShift) { // dragging in one direction, decide direction
					if (Math.pow((orig.x + offset.x - e.x),2)+Math.pow((orig.y + offset.y - e.y),2) >= 3 && (Main.dragDir == null || Main.dragDir==AxisFlag.BOTH)) { // to prevent picking wrong direction due to unintended mouse movement
						float angle = Main.getAngle(orig.x+offset.x, orig.y+offset.y, e.x, e.y);
						//Main.debug(angle);
						if ((angle > 315 || angle <= 45) || (angle > 135 && angle <= 225)) { // Y axis
							Main.dragDir = AxisFlag.Y;
						} else if ((angle > 45 && angle <= 135) || (angle > 225 && angle <= 315)) { // X axis
							Main.dragDir = AxisFlag.X;
						}
					}
				}
				
				if (Main.modCtrl) { // precision mode
					setLocation((Main.dragDir==AxisFlag.Y) ? bounds.x : orig.x - (orig.x + offset.x - e.x)/10,
							(Main.dragDir==AxisFlag.X) ? bounds.y : orig.y - (orig.y + offset.y - e.y)/10);
				} else { // normal dragging
					setLocation((Main.dragDir==AxisFlag.Y) ? bounds.x : e.x - offset.x,
							(Main.dragDir==AxisFlag.X) ? bounds.y : e.y - offset.y);
				}
				
				Main.canvasRedraw(oldBounds, false);
				Main.canvasRedraw(bounds, false);
			} else if (resize && !isPinned()) {
				Rectangle oldBounds = Main.cloneRect(bounds);
				
				int d = Math.abs(e.x - offset.x);
				Main.shieldEllipse.x = offset.x - d;
				Main.shieldEllipse.width = 2*d;

				d = Math.abs(e.y - offset.y);
				Main.shieldEllipse.y = offset.y - d;
				Main.shieldEllipse.height = 2*d;
				
				setLocation(Main.shieldEllipse.x, Main.shieldEllipse.y);
				setSize(Main.shieldEllipse.width, Main.shieldEllipse.height);
				Main.canvasRedraw(oldBounds, false);
				Main.canvasRedraw(bounds, false);
			}
		} else {
			setLocation(e.x - offset.x, e.y - offset.y);
		}
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {}

	public void select() {
		selected = true;
		move = true;
		resize = !Main.ship.isPlayer;
		setBorderColor(new RGB(0,0,255));
		
		Main.canvasRedraw(bounds, false);
	}
	
	public void deselect() {
		selected = false;
		move = false;
		resize = false;
		setBorderColor(null);
		
		Main.canvasRedraw(bounds, false);
	}
	
	@Override
	public void dispose() {
		Cache.checkInColor(this, oval_rgb);
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
