package com.kartoflane.superluminal.elements;

import java.io.Serializable;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.core.ShipIO;
import com.kartoflane.superluminal.painter.Cache;
import com.kartoflane.superluminal.painter.ImageBox;
import com.kartoflane.superluminal.painter.LayeredPainter;

/**
 * Weapon mount class.
 */
public class FTLMount extends ImageBox implements Serializable, DraggableBox {
	private static final long serialVersionUID = 6519211644795332479L;
	
	public Point pos;
	public Point mountPoint;
	private Point orig;
	private boolean move;
	private Color dirColor = null;
	private RGB dir_rgb = null;
	private Point offset = null;
	private int frameW = 0;
	private Rectangle redrawBounds;
	private boolean powered = true;
	
	/**
	 * True means the weapons are oriented horizontally, while false means they're oriented vertically
	 * (Bascially true for player ships, false for enemy ships)
	 */
	private boolean rotate;
	
	/**
	 * When set to true, mirrors the weapon's graphic along vertical or horizontal axis.<br>
	 * (if rotate=true - horizotanl axis, rotate=false - vertical)
	 */
	public boolean mirror;
	//public FTLMount mirrorCounterpart;
	/**
	 * The direction in which the weapon slides when it becomes active.
	 */
	public Slide slide;
	
	/**
	 * Gib id to which the weapon is attached when the ship explodes TODO
	 */
	public int gib;
	
	public void stripUnserializable() {
		super.stripUnserializable();
		Cache.checkInColor(this, dir_rgb);
		dirColor = null;
		Cache.checkInImage(this, path);
		Cache.checkInImageAbsolute(this, path);
		image = null;
	}
	
	public void loadUnserializable() {
		super.loadUnserializable();
		dirColor = Cache.checkOutColor(this, dir_rgb);
		//image taken care of by ShipIO.loadWeaponImages
	}
	
	public FTLMount() {
		super();
		pos = new Point(0,0);
		redrawBounds = new Rectangle(0,0,0,0);
		mountPoint = new Point(2,36);
		this.shrinkWrap = true;
		setImage(null);
		setBorderThickness(2);
		setDirColor(new RGB(255, 255, 0));
		offset = new Point(0,0);
		slide = Slide.NO;
		orig = new Point(0,0);
	}

	public void setImage(String path, boolean shrinkWrap) {
		this.shrinkWrap = shrinkWrap;
		if (this.path != null) {
			Cache.checkInImage(this, this.path);
			Cache.checkInImageAbsolute(this, this.path);
		}
		if (path == null) {
			this.path = "/img/weapon.png";
			image = Cache.checkOutImage(this, this.path);
		} else {
			this.path = path;
			image = Cache.checkOutImageAbsolute(this, this.path);
		}
	}
	
	public void setImage(String path) {
		setImage(path, true);
		setSize(image.getBounds().width, image.getBounds().height);
		frameW = 0;
	}
	
	public void setImage(String path, int frameW) {
		setImage(path, true);
		this.frameW = frameW;
		if (image == null) {
			Main.erDialog.add("Error: tried to load " + path + " as weapon image, but no file was found. Reverting to default image.");
			setImage(null);
			setSize((rotate ? image.getBounds().height : image.getBounds().width), (!rotate ? image.getBounds().height : image.getBounds().width));
			return;
		}
		setSize((rotate ? image.getBounds().height : frameW), (!rotate ? image.getBounds().height : frameW));
		//setRotated(rotate);
		updateRedrawBounds();
	}
	
	public void setSize(int w, int h) {
		bounds.width = w;
		bounds.height = h;
		updateRedrawBounds();
	}
	
	public void setDirColor(RGB rgb) {
		if (dir_rgb != null)
			Cache.checkInColor(this, dir_rgb);
		dir_rgb = rgb;
		dirColor = Cache.checkOutColor(this, dir_rgb);
	}
	
	public void updateRedrawBounds() {
		Rectangle old = Main.cloneRect(redrawBounds);
		redrawBounds.x = bounds.x;
		redrawBounds.y = bounds.y;
		redrawBounds.width = bounds.width;
		redrawBounds.height = bounds.height;
		if (image != null) {
			if (rotate) {
				//redrawBounds.height *= 3/2;
				redrawBounds.x = bounds.x + bounds.width/2 - mountPoint.x;
				redrawBounds.y = bounds.y + bounds.height/2 + (mountPoint.y+bounds.height/2) * (mirror ? -1 : 1);
			} else {
				//redrawBounds.width *= 3/2;
				redrawBounds.x = Main.hullBox.getLocation().x + pos.x + (bounds.width + mountPoint.x) * (mirror ? -1 : 1);
				redrawBounds.y = Main.hullBox.getLocation().y + pos.y - bounds.height/2 + (bounds.width-bounds.height)/2 - mountPoint.y;
			}
		}
		if (!powered) {
			if (slide == Slide.LEFT || slide == Slide.RIGHT)
				redrawBounds.x += (slide == Slide.LEFT ? 1 : -1)*((rotate ? bounds.width : bounds.height));
			if (slide == Slide.UP || slide == Slide.DOWN)
				redrawBounds.y += (slide == Slide.UP ? 1 : -1)*((rotate ? bounds.height : bounds.width));
			
			if (rotate && !mirror && slide == Slide.DOWN) redrawBounds.y -= bounds.height*2;
			if (rotate && mirror && slide == Slide.UP) redrawBounds.y += bounds.height*2;
			if (!rotate && slide == Slide.UP) redrawBounds.y += bounds.height*2;
		}
		
		redrawBounds.add(bounds);
		Main.canvasRedraw(old, false);
	}
	
	public Rectangle getRedrawBounds() {
		return redrawBounds;
	}
	
	public void setLocation(int x, int y) {
		Point oldLoc = new Point(bounds.x, bounds.y);
		bounds.x = x - bounds.width/2;
		bounds.y = y - bounds.height/2;
		if (Main.ship != null) {
			pos.x = x - Main.ship.imageRect.x;
			pos.y = y - Main.ship.imageRect.y;
		}

		updateRedrawBounds(); 
		
		redrawLoc(oldLoc.x, oldLoc.y);
		
		Main.updateSelectedPosText();
	}
	
	public void setLocationAbsolute(int x, int y) {
		Point oldLoc = new Point(bounds.x, bounds.y);
		
		bounds.x = x;
		bounds.y = y;
		pos.x = x - Main.ship.imageRect.x + bounds.width/2;
		pos.y = y - Main.ship.imageRect.y + bounds.height/2;

		updateRedrawBounds();
		
		redrawLoc(oldLoc.x, oldLoc.y);
		
		Main.updateSelectedPosText();
	}
	
	private void redrawLoc(int xOld, int yOld) {
		Main.canvasRedraw(redrawBounds, false);
		if (slide.equals(Slide.NO)) {
			Main.canvas.redraw(xOld-3, yOld-3, bounds.width+6, bounds.height+6, false);
			Main.canvas.redraw(bounds.x-3, bounds.y-3, bounds.width+6, bounds.height+6, false);
		} else if (slide.equals(Slide.UP)) {
			Main.canvas.redraw(xOld-3, yOld-43, bounds.width+6, bounds.height+46, false);
			Main.canvas.redraw(bounds.x-3, bounds.y-43, bounds.width+6, bounds.height+46, false);
		} else if (slide.equals(Slide.RIGHT)) {
			Main.canvas.redraw(xOld-3, yOld-3, bounds.width+46, bounds.height+6, false);
			Main.canvas.redraw(bounds.x-3, bounds.y-3, bounds.width+46, bounds.height+6, false);
		} else if (slide.equals(Slide.DOWN)) {
			Main.canvas.redraw(xOld-3, yOld-3, bounds.width+6, bounds.height+46, false);
			Main.canvas.redraw(bounds.x-3, bounds.y-3, bounds.width+6, bounds.height+46, false);
		} else if (slide.equals(Slide.LEFT)) {
			Main.canvas.redraw(xOld-43, yOld-3, bounds.width+46, bounds.height+6, false);
			Main.canvas.redraw(bounds.x-43, bounds.y-3, bounds.width+46, bounds.height+6, false);
		}
	}
	
	public static void redrawLoc(Rectangle oldBounds, Rectangle bounds, Slide slide) {
		if (slide.equals(Slide.NO)) {
			Main.canvas.redraw(oldBounds.x-3, oldBounds.y-3, bounds.width+6, bounds.height+6, false);
			Main.canvas.redraw(bounds.x-3, bounds.y-3, bounds.width+6, bounds.height+6, false);
		} else if (slide.equals(Slide.UP)) {
			Main.canvas.redraw(oldBounds.x-3, oldBounds.y-43, bounds.width+6, bounds.height+46, false);
			Main.canvas.redraw(bounds.x-3, bounds.y-43, bounds.width+6, bounds.height+46, false);
		} else if (slide.equals(Slide.RIGHT)) {
			Main.canvas.redraw(oldBounds.x-3, oldBounds.y-3, bounds.width+46, bounds.height+6, false);
			Main.canvas.redraw(bounds.x-3, bounds.y-3, bounds.width+46, bounds.height+6, false);
		} else if (slide.equals(Slide.DOWN)) {
			Main.canvas.redraw(oldBounds.x-3, oldBounds.y-3, bounds.width+6, bounds.height+46, false);
			Main.canvas.redraw(bounds.x-3, bounds.y-3, bounds.width+6, bounds.height+46, false);
		} else if (slide.equals(Slide.LEFT)) {
			Main.canvas.redraw(oldBounds.x-43, oldBounds.y-3, bounds.width+46, bounds.height+6, false);
			Main.canvas.redraw(bounds.x-43, bounds.y-3, bounds.width+46, bounds.height+6, false);
		}
	}
	
	private void redrawLoc(Slide slideOld) {
		redrawLoc(bounds.x, bounds.y);
		
		if (slideOld.equals(Slide.NO)) {
			Main.canvas.redraw(bounds.x-3, bounds.y-3, bounds.width+6, bounds.height+6, false);
		} else if (slideOld.equals(Slide.UP)) {
			Main.canvas.redraw(bounds.x-3, bounds.y-43, bounds.width+6, bounds.height+46, false);
		} else if (slideOld.equals(Slide.RIGHT)) {
			Main.canvas.redraw(bounds.x-3, bounds.y-3, bounds.width+46, bounds.height+6, false);
		} else if (slideOld.equals(Slide.DOWN)) {
			Main.canvas.redraw(bounds.x-3, bounds.y-3, bounds.width+6, bounds.height+46, false);
		} else if (slideOld.equals(Slide.LEFT)) {
			Main.canvas.redraw(bounds.x-43, bounds.y-3, bounds.width+46, bounds.height+6, false);
		}
	}
	
	public Point getLocation() {
		return new Point(bounds.x + bounds.width/2, bounds.y + bounds.height/2);
	}
	
	public Point getLocationAbsolute() {
		return new Point(bounds.x, bounds.y);
	}
	
	public Rectangle getBounds() {
		return bounds;
	}
	
	public void setRotated(boolean rotated) {
		rotate = rotated;
		setRotation(rotate ? 90 : 0);
		Point p = getLocation();
		if (!rotate) {
			setSize((frameW==0) ? image.getBounds().width : frameW, image.getBounds().height);
		} else {
			setSize(image.getBounds().height, (frameW==0) ? image.getBounds().width : frameW);
		}
		if (Main.ship != null)
			setLocation(p.x, p.y);
		updateRedrawBounds(); 
	}
	
	public boolean isRotated() {
		return rotate;
	}
	
	public void setMirrored(boolean mirrored) {
		mirror = mirrored;
		updateRedrawBounds(); 
		redrawLoc(bounds.x, bounds.y);
	}
	
	public boolean isMirrored() {
		return mirror;
	}
	
	public void setPowered(boolean power) {
		powered = power;
		updateRedrawBounds();
		redrawLoc(bounds.x, bounds.y);
	}
	
	public boolean isPowered() {
		return powered;
	}
	
	/**
	 * Draws a direction arrow at the center of given rectangle, indicating given Slide
	 */
	public static void drawDirection(PaintEvent e, Slide s, Rectangle rect) {
		final int LENGTH = 40;
		final int ARROWHEAD = 8;
		int xDir = (s.equals(Slide.UP) || s.equals(Slide.DOWN))
						? (rect.x+rect.width/2)
						: ((s.equals(Slide.LEFT))
							? (rect.x+rect.width/2-LENGTH)
							: (s.equals(Slide.RIGHT))
								? (rect.x+rect.width/2+LENGTH)
								: (rect.x+rect.width/2));
		int yDir = (s.equals(Slide.LEFT) || s.equals(Slide.RIGHT))
						? (rect.y+rect.height/2)
						: ((s.equals(Slide.UP))
							? (rect.y+rect.height/2-LENGTH)
							: (s.equals(Slide.DOWN))
								? (rect.y+rect.height/2+LENGTH)
								: (rect.y+rect.height/2));
		
		// draw main line
		e.gc.drawLine(rect.x+rect.width/2, rect.y+rect.height/2, xDir, yDir);

		// draw arrowhead
		if (s.equals(Slide.UP)) {
			e.gc.drawLine(xDir, yDir, xDir-ARROWHEAD/2, yDir+ARROWHEAD);
			e.gc.drawLine(xDir, yDir, xDir+ARROWHEAD/2, yDir+ARROWHEAD);
		} else if (s.equals(Slide.DOWN)) {
			e.gc.drawLine(xDir, yDir, xDir-ARROWHEAD/2, yDir-ARROWHEAD);
			e.gc.drawLine(xDir, yDir, xDir+ARROWHEAD/2, yDir-ARROWHEAD);
		} else if (s.equals(Slide.LEFT)) {
			e.gc.drawLine(xDir, yDir, xDir+ARROWHEAD, yDir-ARROWHEAD/2);
			e.gc.drawLine(xDir, yDir, xDir+ARROWHEAD, yDir+ARROWHEAD/2);
		} else if (s.equals(Slide.RIGHT)) {
			e.gc.drawLine(xDir, yDir, xDir-ARROWHEAD, yDir-ARROWHEAD/2);
			e.gc.drawLine(xDir, yDir, xDir-ARROWHEAD, yDir+ARROWHEAD/2);
		} else {
			e.gc.drawLine(xDir-ARROWHEAD, yDir-ARROWHEAD, xDir+ARROWHEAD, yDir+ARROWHEAD);
			e.gc.drawLine(xDir-ARROWHEAD, yDir+ARROWHEAD, xDir+ARROWHEAD, yDir-ARROWHEAD);
		}
	}
	
	@Override
	public void paintControl(PaintEvent e) {
		int prevAlpha = e.gc.getAlpha();
		Color prevFg = e.gc.getForeground();
		Color prevBg = e.gc.getBackground();
		int prevLine = e.gc.getLineWidth();

		e.gc.setAdvanced(true);
		e.gc.setAlpha(Main.btnCloaked.getSelection() ? alpha/3 : alpha);
		
		e.gc.setLineWidth(2);
		e.gc.setForeground(dirColor);
		drawDirection(e, slide, bounds);

		Transform transform = null;
		transform = new Transform(e.gc.getDevice());
		transform.translate(bounds.x + bounds.width/2, bounds.y + bounds.height/2);
		transform.rotate(rotation);
		if (mirror) {
			transform.scale(-1, 1);
		}
		transform.translate(-bounds.x - bounds.width/2, -bounds.y - bounds.height/2);
		e.gc.setTransform(transform);

		if (rotate) {
			// rotated 90 degrees -> add height instead of width, scale 1 or -1 -> takes care of positive/negative values on its own
			setImageLoc(bounds.x + (bounds.width-bounds.height)/2 + bounds.height/2 - mountPoint.x
							+ ((slide == Slide.DOWN || slide == Slide.UP)
								? ((powered)
									? 0
									: (slide == Slide.DOWN
										? -bounds.height/2 * (mirror ? -1 : 1) 
										: bounds.height/2 * (mirror ? -1 : 1)))
								: 0),
						bounds.y - (bounds.width-bounds.height)/2 + bounds.width/2 - mountPoint.y
							+ ((slide == Slide.LEFT || slide == Slide.RIGHT)
								? ((powered)
									? 0
									: (slide == Slide.RIGHT
										? bounds.width/2
										: -bounds.width/2))
								: 0));
			
			e.gc.drawImage(image, 0, 0, (frameW==0) ? image.getBounds().width : frameW, image.getBounds().height,
					imageLoc.x, imageLoc.y,
					bounds.height, bounds.width);
		} else {
			setImageLoc(bounds.x + bounds.width/2 - mountPoint.x
							+ ((slide == Slide.LEFT || slide == Slide.RIGHT)
								? ((powered)
									? 0
									: (slide == Slide.RIGHT
										? -bounds.width/2 * (mirror ? -1 : 1)
											: bounds.width/2 * (mirror ? -1 : 1)))
								: 0),
						bounds.y + bounds.height/2 - mountPoint.y
							+ ((slide == Slide.DOWN || slide == Slide.UP)
								? ((powered)
									? 0
									: (slide == Slide.DOWN
										? -bounds.height/2
										: bounds.height/2))
								: 0));

			e.gc.drawImage(image, 0, 0, (frameW==0) ? image.getBounds().width : frameW, image.getBounds().height,
					imageLoc.x, imageLoc.y,
					bounds.width, bounds.height);
		}

		e.gc.setTransform(Main.currentTransform);
		transform.dispose();

		if (selected) {
			e.gc.setAlpha(64);
			e.gc.setBackground(borderColor);
			e.gc.fillRectangle(bounds);
			
			e.gc.setAlpha(255);
			e.gc.setLineWidth(borderThickness);
			e.gc.setForeground(borderColor);
			e.gc.drawRectangle(bounds);
		}
		
		e.gc.setLineWidth(prevLine);
		e.gc.setAlpha(prevAlpha);
		e.gc.setForeground(prevFg);
		e.gc.setBackground(prevBg);
		e.gc.setAdvanced(false);
	}
	
	public void add(FTLShip ship) {
		ship.mounts.add(this);
		Main.layeredPainter.add(this, LayeredPainter.MOUNT);
	}

	@Override
	public void mouseUp(MouseEvent e) {
		Main.cursor.setVisible(true);
		if (move) {
			Main.copyRect(bounds, Main.cursor.getBounds());
		}
		
		if (!Main.canvas.getBounds().contains(bounds.x+bounds.width/2, bounds.y+bounds.height/2)) {
			setLocationAbsolute(orig.x, orig.y);
		} else {
			orig.x = bounds.x;
			orig.y = bounds.y;
		}
		
		move = false;
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (Main.tltmPointer.getSelection()) {
			if (bounds.contains(e.x, e.y)) {
				select();
				offset.x = e.x - (Main.hullBox.getBounds().x + pos.x);
				offset.y = e.y - (Main.hullBox.getBounds().y + pos.y);
			} else {
				deselect();
			}
			
			if (e.button == 1) {
				if (Main.modShift)
					setMirrored(!mirror);
				if (Main.modAlt)
					setPowered(!powered);
			} else if (e.button == 3) {
				if (Main.modShift) {
					Slide slideOld = slide;
					slide = ((slide.equals(Slide.UP))
								? (Slide.RIGHT)
								: (slide.equals(Slide.RIGHT))
									? (Slide.DOWN)
									: (slide.equals(Slide.DOWN))
										? (Slide.LEFT)
										: (slide.equals(Slide.LEFT))
											? (Slide.NO)
											: (slide.equals(Slide.NO))
												? (Slide.UP)
												: slide);
					
					redrawLoc(slideOld);
				} else {
					setRotated(!rotate);
				}
			}
		} else if (Main.tltmGib.getSelection() && bounds.contains(e.x, e.y)) {
			if (e.button == 1) {
				select();
				move = false;
			} else if (e.button == 3) {
				select();
				move = false;
				Main.menuGib.setVisible(true);
			}
		}
		
		Main.cursor.setVisible(false);
	}

	@Override
	public void mouseMove(MouseEvent e) {
		if (move && selected) {
			
			if (Main.modCtrl) {
				setLocation(orig.x + bounds.width/2 - (orig.x + offset.x - e.x)/10, orig.y + bounds.height/2 - (orig.y + offset.y - e.y)/10);
			} else {
				setLocation(e.x - offset.x, e.y - offset.y);
			}
		}
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {}
	
	public void mouseHover(MouseEvent e) {
		if (bounds.contains(e.x, e.y)) {
			Main.tooltip.setText("Mount number: "+(Main.getMountIndex(this)+1)
					+ ShipIO.lineDelimiter + "Attached gib: " +gib
					+ ShipIO.lineDelimiter + "Direction: " + slide.toString().toLowerCase()
					+ ShipIO.lineDelimiter + (isPowered() ? "Powered" : "Unpowered")
					+ ((isPinned()) ? (ShipIO.lineDelimiter + "Pinned") : "")
					+ ((isMirrored()) ? (ShipIO.lineDelimiter + "Mirrored") : ""));
			Main.tooltip.setLocation(e.x+1, e.y+1);
			Main.tooltip.setVisible(true);
		}
	}

	@Override
	public void select() {
		if (Main.tltmGib.getSelection()) {
			if (Main.selectedGib != null) Main.selectedGib.deselect();
			Main.selectedGib = null;
		}
		if (Main.selectedMount != null)
			Main.selectedMount.deselect();
		selected = true;
		move = !isPinned();
		setBorderColor(new RGB(0, 0, 255));
		Main.selectedMount = this;
		Main.canvasRedraw(bounds, false);
	}

	@Override
	public void deselect() {
		selected = false;
		move = false;
		setBorderColor(null);
		Main.selectedMount = null;
		Main.canvas.redraw(bounds.x-2, bounds.y-2, bounds.width+4, bounds.height+4, false);
	}
	
	@Override
	public void dispose() {
		Cache.checkInImage(this, this.path);
		Cache.checkInImageAbsolute(this, this.path);
		Cache.checkInColor(this, dir_rgb);
		Main.layeredPainter.remove(this);
		super.dispose();
		dirColor = null;
		dir_rgb = null;
	}

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
