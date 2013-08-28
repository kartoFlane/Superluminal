package com.kartoflane.superluminal.elements;

import javax.swing.event.UndoableEditEvent;

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
import com.kartoflane.superluminal.undo.Undoable;
import com.kartoflane.superluminal.undo.UndoableImageEdit;

@SuppressWarnings("serial")
public class HullBox extends ImageBox implements DraggableBox {
	public String floorPath;
	protected Image floorImage;
	public String cloakPath;
	protected Image cloakImage;
	public boolean move = false;
	private Point orig;
	private Point offset;

	public HullBox() {
		super();
		orig = new Point(0, 0);
		offset = new Point(0, 0);
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
			try {
				setSize(image.getBounds().width, image.getBounds().height);
			} catch (NullPointerException e) {
				Main.erDialog.add("Error: tried to load " + path + " as hull, returned null; image not found.");
			}
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

			for (FTLGib g : Main.ship.gibs)
				g.setLocationRelative(g.position.x, g.position.y);
			for (FTLMount m : Main.ship.mounts)
				m.updatePos();
		}

		// setVisible(Main.canvas.getBounds().contains(x, y) && Main.canvas.getBounds().contains(x+bounds.width, y+bounds.height));
		Main.canvasRedraw(oldBounds, false);
		Main.canvasRedraw(bounds, false);

		Main.updateSelectedPosText();
	}

	@Override
	protected void paintBorder(PaintEvent e) {
		if (borderColor != null && !Main.tltmGib.getSelection()) {
			Color prevColor = e.gc.getForeground();
			int prevLineWidth = e.gc.getLineWidth();

			if (pinned) {
				e.gc.setForeground(pinColor);
			} else {
				e.gc.setForeground(borderColor);
			}
			e.gc.setLineWidth(borderThickness);

			e.gc.drawRectangle(bounds.x + borderThickness / 2, bounds.y + borderThickness / 2, (bounds.width - 1) - borderThickness, (bounds.height - 1) - borderThickness);

			e.gc.setForeground(prevColor);
			e.gc.setLineWidth(prevLineWidth);
		}
	}

	@Override
	public void paintControl(PaintEvent e) {
		int prevAlpha = e.gc.getAlpha();
		if (!Main.tltmGib.getSelection()) {
			Color prevBg = e.gc.getBackground();

			paintBorder(e);

			if (borderColor != null)
				if (pinned)
					e.gc.setBackground(pinColor);
				else
					e.gc.setBackground(borderColor);

			e.gc.setAlpha(Main.btnCloaked.getSelection() ? 0 : alpha); // alpha/3
			if (image != null && Main.showHull)
				e.gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, bounds.x, bounds.y, bounds.width, bounds.height);
			if (floorImage != null && Main.showFloor)
				e.gc.drawImage(floorImage, 0, 0, floorImage.getBounds().width, floorImage.getBounds().height, bounds.x, bounds.y, bounds.width, bounds.height);
			if (cloakImage != null && (Main.showHull || Main.showFloor) && Main.btnCloaked.getSelection()) {
				e.gc.setAlpha(alpha);
				e.gc.drawImage(cloakImage, 0, 0, cloakImage.getBounds().width, cloakImage.getBounds().height, bounds.x - 10, bounds.y - 10, bounds.width + 20, bounds.height + 20);
			}

			e.gc.setAlpha(alpha / 255 * 32);
			if (selected)
				e.gc.fillRectangle(bounds);

			e.gc.setBackground(prevBg);
		} else {
			e.gc.setAlpha(alpha / 3);
			if (image != null)
				e.gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, bounds.x, bounds.y, bounds.width, bounds.height);
		}
		e.gc.setAlpha(prevAlpha);
	}
	
	@Override
	public void registerDown(int undoable) {
		super.registerDown(undoable);
		if (undoListener != null) {
			if (undoable == Undoable.IMAGE || undoable == Undoable.FLOOR || undoable == Undoable.CLOAK) {
				ume = new UndoableImageEdit(this, undoable);
				undoListener.undoableEditHappened(new UndoableEditEvent(this, ume));
			}
		}
	}
	
	@Override
	public void registerUp(int undoable) {
		super.registerUp(undoable);
		if (ume != null) {
			String path = null;
			if (undoable == Undoable.IMAGE) {
				path = ((UndoableImageEdit) ume).getOldValue();
				if (path != getPath() || (path != null && getPath() != null && !path.equals(getPath())))
					((UndoableImageEdit) ume).setCurrentValue(getPath());
				Main.addEdit(ume);
			} else if (undoable == Undoable.FLOOR) {
				path = ((UndoableImageEdit) ume).getOldValue();
				if (path != floorPath || (path != null && floorPath != null && !path.equals(floorPath)))
					((UndoableImageEdit) ume).setCurrentValue(floorPath);
				Main.addEdit(ume);
			} else if (undoable == Undoable.CLOAK) {
				path = ((UndoableImageEdit) ume).getOldValue();
				if (path != cloakPath || (path != null && cloakPath != null && !path.equals(cloakPath)))
					((UndoableImageEdit) ume).setCurrentValue(cloakPath);
				Main.addEdit(ume);
			}
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {
		if (move) {
			registerUp(Undoable.MOVE);
			Main.cursor.setLocationAbsolute(bounds.x, bounds.y);
			Main.cursor.setSize(bounds.width, bounds.height);
		}
		move = false;
		Main.cursor.setVisible(true);
		if (Main.canvas.getBounds().contains(bounds.x + bounds.width - 35, bounds.y + bounds.height - 35) || Main.canvas.getBounds().contains(bounds.x + 35, bounds.y + 35)
				|| Main.canvas.getBounds().contains(bounds.x + 35, bounds.y + bounds.height - 35) || Main.canvas.getBounds().contains(bounds.x + bounds.width - 35, bounds.y + 35)) {
			orig.x = bounds.x;
			orig.y = bounds.y;
		} else {
			Point p = new Point(bounds.x, bounds.y);
			bounds.x = orig.x;
			bounds.y = orig.y;
			Main.canvas.redraw(p.x, p.y, bounds.width, bounds.height, false);
			Main.canvasRedraw(bounds, false);
		}
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (bounds.contains(e.x, e.y) && (Main.showHull || Main.showFloor)) {
			if (e.button == 1) {
				registerDown(Undoable.MOVE);
				select();
				offset.x = e.x - bounds.x;
				offset.y = e.y - bounds.y;
				Main.cursor.setVisible(false);
				Main.shieldBox.deselect();
			} else if (e.button == 3) {
				deselect();
				Main.shieldBox.mouseDown(e);
			}
		} else if (selected) {
			deselect();
		}
	}

	@Override
	public void mouseMove(MouseEvent e) {
		if (move && !isPinned()) {
			if (isVisible()) {
				Rectangle oldBounds = Main.cloneRect(bounds);

				if (Main.modShift) { // dragging in one direction, decide direction
					if (Math.pow((orig.x + offset.x - e.x), 2) + Math.pow((orig.y + offset.y - e.y), 2) >= 3 && (Main.dragDir == null || Main.dragDir == AxisFlag.BOTH)) { // to prevent picking wrong
																																											// direction due to
																																											// unintended mouse movement
						float angle = Main.getAngle(orig.x + offset.x, orig.y + offset.y, e.x, e.y);
						// Main.debug(angle);
						if ((angle > 315 || angle <= 45) || (angle > 135 && angle <= 225)) { // Y axis
							Main.dragDir = AxisFlag.Y;
						} else if ((angle > 45 && angle <= 135) || (angle > 225 && angle <= 315)) { // X axis
							Main.dragDir = AxisFlag.X;
						}
					}
				}

				if (Main.modCtrl) { // precision mode
					setLocation((Main.dragDir == AxisFlag.Y) ? bounds.x : orig.x - (orig.x + offset.x - e.x) / 10,
							(Main.dragDir == AxisFlag.X) ? bounds.y : orig.y - (orig.y + offset.y - e.y) / 10);
				} else { // normal dragging
					setLocation((Main.dragDir == AxisFlag.Y) ? bounds.x : e.x - offset.x,
							(Main.dragDir == AxisFlag.X) ? bounds.y : e.y - offset.y);
				}

				Main.canvasRedraw(oldBounds, false);
				Main.canvasRedraw(bounds, false);
			} else {
				setLocation(e.x - offset.x, e.y - offset.y);
			}
		}
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
	}

	public void select() {
		selected = true;
		move = true;
		setBorderColor(new RGB(0, 0, 255));

		Main.canvasRedraw(bounds, false);
	}

	@Override
	public void deselect() {
		selected = false;
		move = false;
		setBorderColor(null);

		Main.canvasRedraw(bounds, false);
	}

	@Override
	public void dispose() {
		if (cloakPath != null)
			Cache.checkInImageAbsolute(this, cloakPath);
		if (floorPath != null)
			Cache.checkInImageAbsolute(this, floorPath);
		if (path != null)
			Cache.checkInImageAbsolute(this, path);
		floorPath = null;
		path = null;
		super.dispose();
	}

	@Override
	public void mouseHover(MouseEvent e) {
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
	
	public void reset() {
		selected = false;
		move = false;
		setBorderColor(null);
	}
}
