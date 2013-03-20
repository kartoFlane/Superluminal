package com.kartoflane.superluminal.elements;

import java.io.Serializable;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Transform;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.painter.Cache;
import com.kartoflane.superluminal.painter.ImageBox;
import com.kartoflane.superluminal.painter.LayeredPainter;

public class FTLGib extends ImageBox implements Serializable, DraggableBox {
	private static final long serialVersionUID = 6190867454456719261L;
	
	public double minVel = -0.3;
	public double maxVel = 0.3;
	public double minAng = -0.3;
	public double maxAng = 0.3;
	public int minDir = 0;
	public int maxDir = 360;
	public int number = 0;
	
	private Color green;
	private RGB green_rgb = new RGB(0,255,0);
	
	public Point original;
	public Point offset;
	public String ID;
	public boolean move = false;
	
	/**
	 * Relative to ship's hull - this is the value that gets exported!
	 * Changed only when gib is manually moved around
	 * (bounds is only temporary for drawing and animation!)
	 */
	public Point position;
	
	public void stripUnserializable() {
		super.stripUnserializable();
		Cache.checkInColor(this, green_rgb);
		green = null;
	}
	
	public void loadUnserializable() {
		super.loadUnserializable();
		image = Cache.checkOutImageAbsolute(this, path);
		green = Cache.checkOutColor(this, green_rgb);
	}
	
	public FTLGib() {
		super();
		position = new Point(0,0);
		original = new Point(0,0);
		offset = new Point(0,0);
		minVel = -0.3;
		maxVel = 0.3;
		minAng = -0.3;
		maxAng = 0.3;
		minDir = 0;
		maxDir = 360;
		number = 0;
		green = Cache.checkOutColor(this, green_rgb);
	}
	
	public String toString() {
		return number +". gib " + ID;
	}
	
	public void add(FTLShip ship) {
		ship.gibs.add(this);
		Main.layeredPainter.addAsFirst(this, LayeredPainter.GIB);
		
		ID = getFreeLetter();
		Main.gibDialog.letters.add(ID);
		Main.gibDialog.list.add(number + ". gib " + ID);
	}
	
	private String getFreeLetter() {
		char[] alphabet = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
		for (char ch : alphabet) {
			if (!Main.gibDialog.letters.contains(String.valueOf(ch))) return String.valueOf(ch);
		}
		return null;
	}
	
	public void setImage(String path, boolean shrinkWrap) {
		if (this.path != null) {
			Cache.checkInImageAbsolute(this, this.path);
		}

		this.shrinkWrap = shrinkWrap;
		this.path = path;
		image = Cache.checkOutImageAbsolute(this, path);
		setSize(image.getBounds().width, image.getBounds().height);
	}
	
	/**
	 * Position relative to the ship's hull
	 */
	public void setLocationRelative(int x, int y) {
		Point old = new Point(position.x, position.y);
		position.x = x;
		position.y = y;
		setLocation(Main.hullBox.getBounds().x + x, Main.hullBox.getBounds().y + y);
		
		Main.canvasRedraw(bounds, false);
		Main.canvas.redraw(Main.hullBox.getBounds().x + old.x, Main.hullBox.getBounds().y + old.y, bounds.width, bounds.height, false);
	}
	
	/**
	 * Absolute position on canvas.
	 */
	public void setLocationAbsolute(int x, int y) {
		Point old = new Point(position.x, position.y);
		position.x = x - Main.hullBox.getBounds().x;
		position.y = y - Main.hullBox.getBounds().y;
		setLocation(x, y);
		
		Main.canvasRedraw(bounds, false);
		Main.canvas.redraw(Main.hullBox.getBounds().x + old.x, Main.hullBox.getBounds().y + old.y, bounds.width, bounds.height, false);
	}
	
	@Override
	public void setOffset(int x, int y) {
		original.x = x;
		original.y = y;
	}

	@Override
	public Point getOffset() {
		return original;
	}
	
	@Override
	public void paintControl(PaintEvent e) {
		if (Main.tltmGib.getSelection()) {
			int prevAlpha = e.gc.getAlpha();
			Color prevFg = e.gc.getForeground();
			Color prevBg = e.gc.getBackground();
			int prevLine = e.gc.getLineWidth();
			
			e.gc.setAdvanced(true);
			e.gc.setLineWidth(2);
			e.gc.setAlpha(alpha);
			
			Transform transform = null;
			transform = new Transform(e.gc.getDevice());
			transform.translate(bounds.x + bounds.width/2, bounds.y + bounds.height/2);
			transform.rotate(rotation);
			transform.translate(-bounds.x - bounds.width/2, -bounds.y - bounds.height/2);
			e.gc.setTransform(transform);

			super.paintControl(e);
			
			if (selected) {
				e.gc.setAlpha(64);
				e.gc.setBackground(borderColor);
				e.gc.fillRectangle(bounds);
				
				e.gc.setAlpha(255);
				e.gc.setLineWidth(borderThickness);
				e.gc.setForeground(borderColor);
				e.gc.drawRectangle(bounds.x+2, bounds.y+2, bounds.width-4, bounds.height-4);
			}
				
			if (selected && isPinned())
				e.gc.drawImage(pin, bounds.x+5, bounds.y+5);

			e.gc.setTransform(Main.currentTransform);
			transform.dispose();
			
			e.gc.setLineWidth(prevLine);
			e.gc.setAlpha(prevAlpha);
			e.gc.setForeground(prevFg);
			e.gc.setBackground(prevBg);
			e.gc.setAdvanced(false);
		}
	}
	
	public void paintOverlay(PaintEvent e) {
		if (Main.tltmGib.getSelection() && selected && isVisible()) {
			int prevAlpha = e.gc.getAlpha();
			Color prevFg = e.gc.getForeground();
			Color prevBg = e.gc.getBackground();
			int prevLine = e.gc.getLineWidth();
			
			if (!Main.gibDialog.animating && minDir != maxDir && green != null) {
				e.gc.setBackground(green);
				e.gc.setForeground(green);
				
				e.gc.setLineWidth(2);
				e.gc.setAlpha(32);
				
				int d = Math.min(bounds.width, bounds.height)*1/2;
				e.gc.fillArc(bounds.x+bounds.width/2-d/2,
						bounds.y+bounds.height/2-d/2,
						d, d, minDir+90, maxDir - minDir);
				e.gc.setAlpha(128);
				e.gc.drawArc(bounds.x+bounds.width/2-d/2,
						bounds.y+bounds.height/2-d/2,
						d, d, minDir+90, maxDir - minDir);
			}
			
			e.gc.setLineWidth(prevLine);
			e.gc.setAlpha(prevAlpha);
			e.gc.setForeground(prevFg);
			e.gc.setBackground(prevBg);
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {
		Main.cursor.setVisible(true);
		if (move) {
			Main.copyRect(bounds, Main.cursor.getBounds());
		}
		
		if (!Main.canvas.getBounds().contains(bounds.x+bounds.width/2, bounds.y+bounds.height/2)) {
			setLocationRelative(original.x, original.y);
		} else {
			setOffset(position.x, position.y);
		}
		
		move = false;
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (e.button==1 && bounds.contains(e.x, e.y)) {
			select();
			offset.x = e.x - (Main.hullBox.getBounds().x + position.x);
			offset.y = e.y - (Main.hullBox.getBounds().y + position.y);
		} else if (e.button==1) {
			deselect();
		} else if (e.button==3) {
			move = !isPinned();
			offset.x = e.x - (Main.hullBox.getBounds().x + position.x);
			offset.y = e.y - (Main.hullBox.getBounds().y + position.y);
		}
		Main.cursor.setVisible(false);
	}

	@Override
	public void mouseMove(MouseEvent e) {
		if (move && selected) {
			if (Main.modShift) { // dragging in one direction, decide direction
				if (Math.pow((bounds.x + offset.x - e.x),2)+Math.pow((bounds.y + offset.y - e.y),2) >= 3 && (Main.dragDir == null || Main.dragDir==AxisFlag.BOTH)) { // to prevent picking wrong direction due to unintended mouse movement
					float angle = Main.getAngle(bounds.x+offset.x, bounds.y+offset.y, e.x, e.y);
					Main.debug(angle);
					if ((angle > 315 || angle <= 45) || (angle > 135 && angle <= 225)) { // Y axis
						Main.dragDir = AxisFlag.Y;
					} else if ((angle > 45 && angle <= 135) || (angle > 225 && angle <= 315)) { // X axis
						Main.dragDir = AxisFlag.X;
					}
				}
			}
			
			if (Main.modCtrl) { // precision mode
				setLocationRelative((Main.dragDir==AxisFlag.Y) ? bounds.x-Main.hullBox.getBounds().x : original.x - Main.hullBox.getBounds().x - (original.x + offset.x - e.x)/10,
						(Main.dragDir==AxisFlag.X) ? bounds.y-Main.hullBox.getBounds().y : original.y - Main.hullBox.getBounds().y - (original.y + offset.y - e.y)/10);
			} else { // normal dragging
				setLocationAbsolute((Main.dragDir==AxisFlag.Y) ? bounds.x : e.x - offset.x,
						(Main.dragDir==AxisFlag.X) ? bounds.y : e.y - offset.y);
			}
		}
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		if (!Main.gibWindow.isVisible())
			Main.gibWindow.open();
	}

	@Override
	public void mouseHover(MouseEvent e) {}

	@Override
	public void select() {
		if (Main.selectedMount != null) Main.selectedMount.deselect();
		Main.selectedMount = null;
		if (Main.selectedGib != null)
			Main.selectedGib.deselect();
		Main.selectedGib = this;
		
		selected = true;
		move = !isPinned();
		Main.gibDialog.select(number-1);
		setBorderColor(new RGB(0, 0, 255));
		Main.canvasRedraw(bounds, false);
		
		Main.updateSelectedPosText();
	}

	@Override
	public void deselect() {
		if (Main.selectedGib == this) {
			Main.gibDialog.select(-1);
			Main.selectedGib = null;
		}
		move = false;
		selected = false;
		setBorderColor(null);
		
		Main.canvasRedraw(bounds, false);
	}

	@Override
	public void dispose() {
		super.dispose();
		Cache.checkInColor(this, green_rgb);
		green = null;
		Cache.checkInImageAbsolute(this, path);
		Main.layeredPainter.remove(this);
	}
}
