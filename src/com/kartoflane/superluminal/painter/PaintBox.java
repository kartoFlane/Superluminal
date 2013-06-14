package com.kartoflane.superluminal.painter;

import java.io.Serializable;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.AbstractUndoableEdit;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.undo.Undoable;
import com.kartoflane.superluminal.undo.UndoableMoveEdit;
import com.kartoflane.superluminal.undo.UndoablePinEdit;

public class PaintBox implements Serializable {
	private static final long serialVersionUID = 1856558606237917617L;

	public static final int BORDER_RECTANGLE = 0;
	public static final int BORDER_OVAL = 1;
	public static final int BORDER_OUTSIDE = 0;
	public static final int BORDER_INSIDE = 1;
	/** Default mode */
	public static final int BORDER_CENTER = 2;

	protected Rectangle bounds = new Rectangle(0, 0, 0, 0);
	protected Color borderColor = null;
	protected Color pinColor = null;
	protected RGB border_rgb = null;
	protected RGB pin_rgb = null;
	protected int borderThickness = 3;
	protected int borderMode = BORDER_CENTER;
	protected int borderShape = BORDER_RECTANGLE;
	protected boolean visible = true;
	protected boolean drawBorder = true;
	protected boolean selected = false;
	protected boolean pinned = false;
	@Deprecated
	protected Image pin;
	@Deprecated
	protected String pathPin;
	protected int alpha;

	public UndoableEditListener undoListener;
	public AbstractUndoableEdit ume = null;

	public void stripUnserializable() {
		Cache.checkInColor(this, border_rgb);
		borderColor = null;
		Cache.checkInColor(this, pin_rgb);
		pinColor = null;
		//Cache.checkInImage(this, pathPin);
		//pin = null;
		ume = null;
		undoListener = null;
	}

	public void loadUnserializable() {
		borderColor = Cache.checkOutColor(this, border_rgb);
		pinColor = Cache.checkOutColor(this, pin_rgb);
		//pin = Cache.checkOutImage(this, pathPin);
		undoListener = Main.ueListener;
	}

	public PaintBox() {
		this(PaintBox.BORDER_RECTANGLE);
		//pathPin = "/img/pin.png";
		//pin = Cache.checkOutImage(this, pathPin);
	}

	public PaintBox(int borderShape) {
		pin_rgb = new RGB(196, 196, 0);
		pinColor = Cache.checkOutColor(this, pin_rgb);
		bounds.width = 35;
		bounds.height = 35;
		this.borderShape = borderShape;

		addUndoableEditListener(Main.ueListener);
	}

	public void setPinned(boolean pinned) {
		this.pinned = pinned;
	}

	public boolean isPinned() {
		return pinned;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setAlpha(int newAlpha) {
		alpha = newAlpha;
	}

	public int getAlpha() {
		return alpha;
	}

	/**
	 * Sets a border color, or null for none.
	 */
	public void setBorderColor(RGB rgb) {
		if (border_rgb != null)
			Cache.checkInColor(this, border_rgb);
		borderColor = Cache.checkOutColor(this, rgb);
		border_rgb = rgb;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderThickness(int n) {
		borderThickness = n;
	}

	public int getBorderThickness() {
		return borderThickness;
	}

	public void setBorderMode(int m) {
		borderMode = m;
	}

	public int getBorderMode() {
		return borderMode;
	}

	public void setLocation(int x, int y) {
		bounds.x = x;
		bounds.y = y;
	}

	public Point getLocation() {
		return new Point(bounds.x, bounds.y);
	}

	public void setSize(int width, int height) {
		bounds.width = width;
		bounds.height = height;
	}

	public Point getSize() {
		return new Point(bounds.width, bounds.height);
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle rect) {
		setLocation(rect.x, rect.y);
		setSize(rect.width, rect.height);
	}

	public void setVisible(boolean b) {
		visible = b;
	}

	public boolean isVisible() {
		return visible;
	}

	/**
	 * Paints this and optionally, a border on top.
	 */
	public void redraw(PaintEvent e) {
		if (visible) {
			paintControl(e);
			if (drawBorder)
				paintBorder(e);
		}
	}

	protected void paintControl(PaintEvent e) {
		// Override this, and draw on the e.gc graphics context.
	}

	protected void paintBorder(PaintEvent e) {
		if (borderColor != null) {
			Color prevColor = e.gc.getForeground();
			int prevLineWidth = e.gc.getLineWidth();
			int prevAlpha = e.gc.getAlpha();

			// Lines grow out from the center, which makes
			// math a little funky to accomodate odd/even widths.
			if (pinned && selected) {
				e.gc.setForeground(pinColor);
			} else {
				e.gc.setForeground(borderColor);
			}
			e.gc.setLineWidth(borderThickness);
			e.gc.setAlpha(alpha);

			if (borderShape == PaintBox.BORDER_RECTANGLE) {
				if (borderMode == PaintBox.BORDER_INSIDE) {
					e.gc.drawRectangle(bounds.x - 1 + borderThickness, bounds.y - 1 + borderThickness, bounds.width - borderThickness, bounds.height - borderThickness);
				} else if (borderMode == PaintBox.BORDER_OUTSIDE) {
					e.gc.drawRectangle(bounds.x - borderThickness / 2, bounds.y - borderThickness / 2, (bounds.width - 1) + borderThickness, (bounds.height - 1) + borderThickness);
				} else {
					e.gc.drawRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
				}
			} else if (borderShape == PaintBox.BORDER_OVAL) {
				e.gc.drawOval(bounds.x, bounds.y, bounds.width, bounds.height);
			}

			e.gc.setForeground(prevColor);
			e.gc.setLineWidth(prevLineWidth);
			e.gc.setAlpha(prevAlpha);
		}
	}

	public void dispose() {
		Cache.checkInColor(this, border_rgb);
		Cache.checkInColor(this, pin_rgb);
		Cache.checkInImage(this, pathPin);
	}

	public void addUndoableEditListener(UndoableEditListener l) {
		undoListener = l;
	}

	public void removeUndoableEditListener(UndoableEditListener l) {
		undoListener = null;
	}

	public void registerDown(int undoable) {
		if (undoListener != null) {
			if (undoable == Undoable.MOVE) {
				ume = new UndoableMoveEdit(this);
				undoListener.undoableEditHappened(new UndoableEditEvent(this, ume));
			} else if (undoable == Undoable.PIN) {
				ume = new UndoablePinEdit(this);
				undoListener.undoableEditHappened(new UndoableEditEvent(this, ume));
				Main.addEdit(ume);
			}
		}
	}

	public void registerUp(int undoable) {
		if (ume != null && ume instanceof UndoableMoveEdit) {
			if (undoable == Undoable.MOVE) {
				Point pt = ((UndoableMoveEdit) ume).getOldPos();
				if (pt.x != bounds.x || pt.y != bounds.y) {
					((UndoableMoveEdit) ume).setCurrentPos(bounds.x, bounds.y);
					Main.addEdit(ume);
				}
			}
		}
	}
}
