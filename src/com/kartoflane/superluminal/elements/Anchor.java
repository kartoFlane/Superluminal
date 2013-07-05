package com.kartoflane.superluminal.elements;
import java.awt.AWTException;
import java.awt.Robot;

import javax.swing.event.UndoableEditEvent;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.painter.PaintBox;
import com.kartoflane.superluminal.undo.Undoable;
import com.kartoflane.superluminal.undo.UndoableMoveEdit;
import com.kartoflane.superluminal.undo.UndoableOffsetEdit;


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
	private boolean initialClick = false;
	private Point offsetEdit = new Point(0,0);

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
			Main.ship.updateElements(new Point(x, y), AxisFlag.BOTH);
		bounds.x = x;
		bounds.y = y;
		if (Main.ship != null && Main.ship.vertical > 0)
			bounds.y -= Main.ship.vertical - 2;
		if (Main.ship != null && Main.ship.horizontal > 0)
			bounds.x -= Main.ship.horizontal - 2;
		
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
		
		/*
		if (Main.tltmGib.getSelection()) {
			e.gc.setForeground(xLine);
			e.gc.drawLine(Main.hullBox.getBounds().x, Main.hullBox.getBounds().y, Main.hullBox.getBounds().x, Main.GRID_H*35);
			e.gc.drawLine(Main.hullBox.getBounds().x, Main.hullBox.getBounds().y, Main.GRID_W*35, Main.hullBox.getBounds().y);
		}
		*/
		
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
			// draw vertical and horizontal lines
			if (Main.ship.vertical != 0) {
				e.gc.setForeground(vLine);
				e.gc.drawLine(Main.ship.anchor.x, Main.ship.anchor.y - Main.ship.vertical, Main.GRID_W*35, Main.ship.anchor.y - Main.ship.vertical);
			}
			if (Main.ship.horizontal != 0) {
				e.gc.setForeground(vLine);
				e.gc.drawLine(Main.ship.anchor.x - Main.ship.horizontal, Main.ship.anchor.y, Main.ship.anchor.x - Main.ship.horizontal, Main.GRID_H*35);
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
	public void registerDown(int undoable) {
		if (undoListener != null) {
			if (undoable == Undoable.MOVE) {
				ume = new UndoableMoveEdit(this);
				((UndoableMoveEdit) ume).setOldPos((box.x==FTLShip.ANCHOR ? 0 : box.x+FTLShip.ANCHOR), (box.y==FTLShip.ANCHOR ? 0 : box.y+FTLShip.ANCHOR));
				offsetEdit.x = Main.ship.offset.x;
				offsetEdit.y = Main.ship.offset.y;
				undoListener.undoableEditHappened(new UndoableEditEvent(this, ume));
			} else if (undoable == Undoable.OFFSET) {
				ume = new UndoableOffsetEdit(this, undoable);
				undoListener.undoableEditHappened(new UndoableEditEvent(this, ume));
			}
		}
	}
	
	@Override
	public void registerUp(int undoable) {
		if (ume != null) {
			if (undoable == Undoable.MOVE) {
				// the shift-drag functionality is implemented in mouseMove listener, which would result in a flood of undoableEdit calls...
				// no way to differentiate between undoable MOVE and SHIP_OFFSET at mouse click listeners, gotta check it here 
				if (offsetEdit.x != Main.ship.offset.x || offsetEdit.y != Main.ship.offset.y) {
					ume = new UndoableOffsetEdit(this, undoable);
					// luckily the AbstractUndoableEdit framework is pretty flexible...
					((UndoableOffsetEdit) ume).setOldOffset(offsetEdit.x, offsetEdit.y);
					((UndoableOffsetEdit) ume).setCurrentOffset(Main.ship.offset.x, Main.ship.offset.y);
					undoListener.undoableEditHappened(new UndoableEditEvent(this, ume));
					Main.addEdit(ume);
				} else {
					Point pt = ((UndoableMoveEdit) ume).getOldPos();
					if (pt.x != (box.x==FTLShip.ANCHOR ? 0 : box.x+FTLShip.ANCHOR) || pt.y != (box.y==FTLShip.ANCHOR ? 0 : box.y+FTLShip.ANCHOR)) {
						((UndoableMoveEdit) ume).setCurrentPos((box.x==FTLShip.ANCHOR ? 0 : box.x+FTLShip.ANCHOR), (box.y==FTLShip.ANCHOR ? 0 : box.y+FTLShip.ANCHOR));
						Main.addEdit(ume);
					}
				}
			} else if (undoable == Undoable.OFFSET) {
				Point temp = ((UndoableOffsetEdit) ume).getOldOffset();
				if (temp.x != Main.ship.horizontal || temp.y != Main.ship.vertical) {
					((UndoableOffsetEdit) ume).setCurrentOffset(Main.ship.horizontal, Main.ship.vertical);
					Main.addEdit(ume);
				}
			}
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {
		if (moveAnchor || moveVertical) {
			if (!moveVertical)
				registerUp(Undoable.MOVE);
			if (!moveAnchor)
				registerUp(Undoable.OFFSET);
			moveAnchor = false;
			moveVertical = false;
			Main.cursor.setVisible(true);
			Main.canvasRedraw(box, false);
		}
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (box.contains(e.x, e.y) && isVisible() && Main.showAnchor) {
			Main.cursor.setVisible(false);
			if (e.button == 1) {
				moveAnchor = true;
			} else if (e.button == 3) {
				moveVertical = true;
				initialClick = true;
				if (e.count == 2) {
					registerDown(Undoable.OFFSET);
					mouseDoubleClick(e);
					registerUp(Undoable.OFFSET);
				}
			}
			if (!moveVertical)
				registerDown(Undoable.MOVE);
			if (!moveAnchor)
				registerDown(Undoable.OFFSET);
			Main.canvasRedraw(box, false);
		}
	}

	@Override
	public void mouseMove(MouseEvent e) {
		if (moveAnchor) {
			int x,y;
			Rectangle oldBounds = Main.cloneRect(bounds);
			oldBounds.x -= 15;
			oldBounds.y -= 15;
			if (Main.ship.vertical > 0) {
				oldBounds.y = Main.ship.anchor.y - Main.ship.vertical-2;
				oldBounds.height += Main.ship.vertical-4;
			}
			if (Main.ship.horizontal > 0) {
				oldBounds.x = Main.ship.anchor.x - Main.ship.horizontal-2;
				oldBounds.width += Main.ship.horizontal-4;
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

			x = Math.min(x, Main.GRID_W*35-35 - size.x - Main.ship.offset.x*35);
			y = Math.min(y, Main.GRID_H*35-35 - size.y - Main.ship.offset.y*35);
			x = Math.max(x, 0);
			y = Math.max(y, 0);
			
			x = Main.roundToGrid(x);
			y = Main.roundToGrid(y);

			setLocation(x,y,!Main.modShift);
			Main.ship.anchor.x = x;
			Main.ship.anchor.y = y;
			setSize(Main.GRID_W*35, Main.GRID_H*35);
			
			Main.canvasRedraw(oldBounds, false);
		} else if (moveVertical) {
			Point p = null;
			
			if (Main.modShift) {
				p = Main.canvas.toDisplay(Main.ship.anchor.x - Main.ship.horizontal, Main.ship.anchor.y);
				int prevH = Main.ship.horizontal;
				Main.ship.horizontal = Main.ship.anchor.x - e.x;
				if (Main.ship.horizontal > 0) {
					bounds.x -= Main.ship.horizontal;
					bounds.width += Main.ship.horizontal;
				}
				Main.canvas.redraw(Main.ship.anchor.x - prevH - 2, Main.ship.anchor.y, 4, Main.GRID_H*35, false);
				Main.canvas.redraw(Main.ship.anchor.x - Main.ship.horizontal - 2, Main.ship.anchor.y, 4, Main.GRID_H*35, false);
				
				Main.tooltip.setText(""+Main.ship.horizontal);
				Main.tooltip.setLocation(e.x+1, Main.ship.anchor.y);
			} else {
				p = Main.canvas.toDisplay(Main.ship.anchor.x, Main.ship.anchor.y - Main.ship.vertical);
				int prevV = Main.ship.vertical;
				Main.ship.vertical = Main.ship.anchor.y - e.y;
				if (Main.ship.vertical > 0) {
					bounds.y -= Main.ship.vertical;
					bounds.height += Main.ship.vertical;
				}
				Main.canvas.redraw(Main.ship.anchor.x, Main.ship.anchor.y - prevV - 2, Main.GRID_W*35, 4, false);
				Main.canvas.redraw(Main.ship.anchor.x, Main.ship.anchor.y - Main.ship.vertical - 2, Main.GRID_W*35, 4, false);

				Main.tooltip.setText(""+Main.ship.vertical);
				Main.tooltip.setLocation(Main.ship.anchor.x, e.y+1);
			}
			
			if (initialClick) {
				initialClick = false;
				Robot robot;
				try {
					robot = new Robot();
					robot.mouseMove(p.x, p.y);
				} catch (AWTException ex) {
				}
			}
			
			Main.tooltip.setVisible(true);
		}
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		if (e.button == 3) {
			if (Main.modShift) {
				int prevH = Main.ship.horizontal;
				Main.ship.horizontal = 0;
				Main.canvas.redraw(Main.ship.anchor.x - prevH - 2, Main.ship.anchor.y, 4, Main.GRID_H*35, false);
				Main.canvas.redraw(Main.ship.anchor.x - Main.ship.horizontal - 2, Main.ship.anchor.y, 4, Main.GRID_H*35, false);
			} else {
				int prevV = Main.ship.vertical;
				Main.ship.vertical = 0;
				Main.canvas.redraw(Main.ship.anchor.x, Main.ship.anchor.y - prevV - 2, Main.GRID_W*35, 4, false);
				Main.canvas.redraw(Main.ship.anchor.x, Main.ship.anchor.y - Main.ship.vertical - 2, Main.GRID_W*35, 4, false);
			}
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
