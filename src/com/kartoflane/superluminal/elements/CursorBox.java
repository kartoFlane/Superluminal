package com.kartoflane.superluminal.elements;
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


@SuppressWarnings("serial")
public class CursorBox extends PaintBox implements DraggableBox {
	private Point lastClick;
	private Color greenColor;
	private RGB green_rgb = new RGB(0,255,0);
	private Color redColor;
	private RGB red_rgb = new RGB(255,0,0);
	
	private boolean room_canBePlaced = false;
	private boolean door_canBePlaced = false;
	private boolean mount_canBePlaced = false;
	private boolean slot_canBePlaced = false;
	private Systems slot_sys = null;
	
	public CursorBox() {
		lastClick = new Point(0,0);
		greenColor = Cache.checkOutColor(this, green_rgb);
		redColor = Cache.checkOutColor(this, red_rgb);
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
	
	public void setLocationAbsolute(int x, int y) {
		bounds.x = x;
		bounds.y = y;
	}
	
	public void setSize(int w, int h) {
		bounds.width = w;
		bounds.height = h;
	}
	
	@Override
	public void paintControl(PaintEvent e) {
		Color prevFg = e.gc.getForeground();
		Color prevBg = e.gc.getBackground();
		int prevLineWidth = e.gc.getLineWidth();
		int prevAlpha = e.gc.getAlpha();

		e.gc.setLineWidth(borderThickness);
		
		if ((Main.tltmPointer.getSelection() || Main.tltmGib.getSelection()) && borderColor != null) {
			e.gc.setForeground(borderColor);
		} else if (Main.tltmRoom.getSelection()) {
			if (room_canBePlaced) {
				e.gc.setForeground(greenColor);
				e.gc.setBackground(greenColor);
			} else {
				e.gc.setForeground(redColor);
				e.gc.setBackground(redColor);
			}
			e.gc.setAlpha(64);
			e.gc.fillRectangle(bounds);
		} else if (Main.tltmDoor.getSelection()) {
			if (door_canBePlaced) {
				e.gc.setForeground(greenColor);
				e.gc.setBackground(greenColor);
			} else {
				e.gc.setForeground(redColor);
				e.gc.setBackground(redColor);
			}
			e.gc.setAlpha(64);
			e.gc.fillRectangle(bounds);
		} else if (Main.tltmMount.getSelection()) {
			if (mount_canBePlaced) {
				e.gc.setForeground(greenColor);
				e.gc.setBackground(greenColor);
			} else {
				e.gc.setForeground(redColor);
				e.gc.setBackground(redColor);
			}
			e.gc.setAlpha(64);
			e.gc.fillRectangle(bounds);
		} else if (Main.tltmSystem.getSelection()) {
			if (slot_canBePlaced) {
				e.gc.setForeground(greenColor);
				e.gc.setBackground(greenColor);
			} else {
				e.gc.setForeground(redColor);
				e.gc.setBackground(redColor);
			}
			e.gc.setAlpha(64);
			e.gc.fillRectangle(bounds);
		} else if (Main.tltmGib.getSelection()) {
		}

		e.gc.setAlpha(255);
		e.gc.drawRectangle(bounds.x+1, bounds.y+1, bounds.width-2, bounds.height-2);
		
		e.gc.setForeground(prevFg);
		e.gc.setBackground(prevBg);
		e.gc.setLineWidth(prevLineWidth);
		e.gc.setAlpha(prevAlpha);
	}
	
	@Override
	public void paintBorder(PaintEvent e) {}
	
	public int round(double d) {
		return (int) Math.floor(d);
	}
	
	@Override
	public void mouseUp(MouseEvent e)
	{
		lastClick.x = e.x; lastClick.y = e.y;
		if (e.button == 1) {
			if (Main.tltmRoom.getSelection() && room_canBePlaced) {
				FTLRoom r = new FTLRoom(Main.fixRect(bounds));
	
				r.id = Main.getLowestId();
				r.add(Main.ship);
				
				Main.canvasRedraw(r.getBounds(), false);
				
			} else if (Main.tltmDoor.getSelection() && door_canBePlaced) {
				FTLDoor d = new FTLDoor(bounds.x, bounds.y, bounds.width > 10);
				d.setLocationAbsolute(bounds.x + (d.horizontal ? 0 : 35), bounds.y + (d.horizontal ? 35 : 0));
				d.fixRectOrientation();
				
				d.add(Main.ship);
				
			} else if (Main.tltmMount.getSelection()) {
				if (Main.modShift) {
					Main.mountToolMirror = !Main.mountToolMirror; 
				} else if (mount_canBePlaced) {
					FTLMount m = new FTLMount();
					m.pos.x = e.x - Main.ship.imageRect.x;
					m.pos.y = e.y - Main.ship.imageRect.y;
					m.setRotated(Main.mountToolHorizontal);
					m.setMirrored(Main.mountToolMirror);
					m.gib = 0;
					m.slide = Main.mountToolSlide;
					
					m.setLocation(e.x, e.y);
					
					m.add(Main.ship);
					
					//mount_canBePlaced = Main.ship.mounts.size() < (Main.ship.weaponSlots + (Main.isSystemAssigned(Systems.ARTILLERY) ? 1 : 0));
					mount_canBePlaced = Main.ship.mounts.size() < 8;
				}
				
			} else if (Main.tltmSystem.getSelection()) {
				if (Main.modShift) {
					if (Main.ship.slotMap.get(slot_sys) != -2) {
						Slide tempSlide = Main.ship.slotDirMap.get(slot_sys);
						
						tempSlide = ((tempSlide.equals(Slide.UP))
										? (Slide.RIGHT)
										: (tempSlide.equals(Slide.RIGHT))
											? (Slide.DOWN)
											: (tempSlide.equals(Slide.DOWN))
												? (Slide.LEFT)
												: (tempSlide.equals(Slide.LEFT))
													? (Slide.UP)
													: (tempSlide));
						
						Main.ship.slotDirMap.put(slot_sys, tempSlide);
						
						Main.canvasRedraw(Main.getRoomWithSystem(slot_sys).getBounds(), false);
					}
				} else {
					if (slot_canBePlaced) {
						Main.ship.slotMap.put(slot_sys, Main.getStationFromRect(Main.getRectAt(e.x,e.y)));
						Main.getRoomWithSystem(slot_sys).slot = Main.ship.slotMap.get(slot_sys);
						
						Main.canvasRedraw(Main.getRoomWithSystem(slot_sys).getBounds(), false);
					}
				}
			}
			
		} else if (e.button == 3) {
			if (Main.tltmMount.getSelection()) {
				if (Main.modShift) {
					Main.mountToolSlide = ((Main.mountToolSlide.equals(Slide.UP))
								? (Slide.RIGHT)
								: (Main.mountToolSlide.equals(Slide.RIGHT))
									? (Slide.DOWN)
									: (Main.mountToolSlide.equals(Slide.DOWN))
										? (Slide.LEFT)
										: (Main.mountToolSlide.equals(Slide.LEFT))
											? (Slide.NO)
											: (Main.mountToolSlide.equals(Slide.NO))
												? (Slide.UP)
												: Main.mountToolSlide);
				} else {
					Rectangle oldBounds = Main.cloneRect(bounds);
					Main.mountToolHorizontal = !Main.mountToolHorizontal;
					
					bounds.width = (Main.mountToolHorizontal) ? 50 : 16;
					bounds.height = (Main.mountToolHorizontal) ? 16 : 50;
					bounds.x = e.x - bounds.width/2;
					bounds.y = e.y - bounds.height/2;
					
					Main.canvas.redraw(bounds.x-3, bounds.y-3, bounds.width+6, bounds.height+6, false);
					Main.canvas.redraw(oldBounds.x-3, oldBounds.y-3, oldBounds.width+6, oldBounds.height+6, false);
				}
				
			} else if (Main.tltmSystem.getSelection()) {
				Main.ship.slotMap.put(slot_sys, -2);
				Main.getRoomWithSystem(slot_sys).slot = Main.ship.slotMap.get(slot_sys);
				
				Main.canvasRedraw(Main.getRoomWithSystem(slot_sys).getBounds(), false);
			}
		}
	}

	@Override
	public void mouseDown(MouseEvent e)
	{
		lastClick.x = e.x; lastClick.y = e.y;
	}

	@Override
	public void mouseMove(MouseEvent e)
	{
		if (isVisible()) {
			Rectangle oldBounds = Main.cloneRect(bounds);
			Rectangle temp;
			
			if (Main.tltmPointer.getSelection()) {
				if (Main.anchor.getBox().contains(e.x, e.y)) {
					bounds.width = 0; bounds.height = 0;
				} else {
					temp = Main.getDoorAt(e.x, e.y);
					if (temp != null && Main.ship != null && Main.isDoorAtWall(temp) && Main.showRooms) {
						Main.copyRect(temp, bounds);
					} else {
						temp = Main.getRectAt(e.x, e.y);
						Main.copyRect(temp, bounds);
						if (Main.ship != null && Main.doesRectOverlap(bounds, null) && Main.getRoomContainingRect(bounds).isVisible()) {
							Main.copyRect(Main.getRoomContainingRect(bounds).getBounds(), bounds);
						} else if (Main.ship != null && Main.getMountFromPoint(e.x, e.y) != null && Main.showMounts) {
							Main.copyRect(Main.getMountFromPoint(e.x, e.y).getBounds(), bounds);
						} else if (Main.ship != null && Main.hullBox.getBounds().contains(e.x,e.y) && Main.hullBox.isVisible() && (Main.showHull || Main.showFloor)) {
							Main.copyRect(Main.hullBox.getBounds(), bounds);
						} else if (Main.ship != null && Main.shieldBox.getBounds().contains(e.x,e.y) && Main.shieldBox.isVisible() && Main.showShield) {
							Main.copyRect(Main.shieldBox.getBounds(), bounds);
						}
					}
				}
				
				Main.canvas.redraw(oldBounds.x, oldBounds.y, oldBounds.width+2, oldBounds.height+2, false);
				Main.canvasRedraw(bounds, false);
				
			} else if (Main.tltmRoom.getSelection()) {
				setVisible(true);
				temp = Main.getRectAt(e.x, e.y);
				if (temp != null && !Main.leftMouseDown) {
					Main.copyRect(temp, bounds);
				} else if (temp != null) {
					bounds.x = Main.downToGrid(lastClick.x) + ((e.x > lastClick.x) ? (0) : (35));
					bounds.y = Main.downToGrid(lastClick.y) + ((e.y > lastClick.y) ? (0) : (35));
					
					int x = ((e.x > lastClick.x)
							? Math.min(Main.GRID_W * 35 - bounds.x, Main.upToGrid(e.x - bounds.x)+35)
							: Main.upToGrid(Main.mousePos.x - bounds.x)-35);
					int y = ((e.y > lastClick.y)
							? Math.min(Main.GRID_H * 35 - bounds.y, Main.upToGrid(e.y - bounds.y)+35)
							: Main.upToGrid(Main.mousePos.y - bounds.y)-35);
					
					bounds.width = x;
					bounds.height = y;
				}
				
				room_canBePlaced = canPlaceRoom();

				Rectangle tempRect = Main.fixRect(bounds);
				oldBounds = Main.fixRect(oldBounds);
				Main.canvas.redraw(tempRect.x-3, tempRect.y-3, tempRect.width+6, tempRect.height+6, false);
				Main.canvas.redraw(oldBounds.x-3, oldBounds.y-3, oldBounds.width+6, oldBounds.height+6, false);
				
			} else if (Main.tltmDoor.getSelection()) {
				temp = Main.getDoorAt(e.x, e.y);
				door_canBePlaced = false;
				if (temp != null) {
					Main.copyRect(temp, bounds);
					door_canBePlaced = Main.isDoorAtWall(temp) && Main.wallToDoor(temp) == null;
				} else {
					bounds.width = -10;
					bounds.height = -10;
				}
				
				Main.canvas.redraw(bounds.x-3, bounds.y-3, bounds.width+6, bounds.height+6, false);
				Main.canvas.redraw(oldBounds.x-3, oldBounds.y-3, oldBounds.width+6, oldBounds.height+6, false);
				
			} else if (Main.tltmMount.getSelection()) {
				// 16 x 50
				bounds.width = (Main.mountToolHorizontal) ? 50 : 16;
				bounds.height = (Main.mountToolHorizontal) ? 16 : 50;
				bounds.x = e.x - bounds.width/2;
				bounds.y = e.y - bounds.height/2;
				
				//mount_canBePlaced = Main.ship.mounts.size() < (Main.ship.weaponSlots + (Main.isSystemAssigned(Systems.ARTILLERY) ? 1 : 0));
				mount_canBePlaced = Main.ship.mounts.size() < 8;
				
				Main.canvas.redraw(bounds.x-3, bounds.y-3, bounds.width+6, bounds.height+6, false);
				Main.canvas.redraw(oldBounds.x-3, oldBounds.y-3, oldBounds.width+6, oldBounds.height+6, false);
				
			} else if (Main.tltmSystem.getSelection()) {
				temp = Main.getRectAt(e.x, e.y);
				if (temp != null) {
					Main.copyRect(temp, bounds);
					FTLRoom tempRoom = Main.getRoomContainingRect(temp);
					if (tempRoom != null) {
						slot_sys = tempRoom.getSystem();
					}
					slot_canBePlaced = tempRoom != null && canPlaceSlot(tempRoom);
				}
				
				Main.canvas.redraw(bounds.x-3, bounds.y-3, bounds.width+6, bounds.height+6, false);
				Main.canvas.redraw(oldBounds.x-3, oldBounds.y-3, oldBounds.width+6, oldBounds.height+6, false);
			} else if (Main.tltmGib.getSelection()) {
				temp = Main.getRectAt(e.x, e.y);
				Main.copyRect(temp, bounds);
				if (Main.ship != null && Main.showMounts && Main.getMountFromPoint(e.x, e.y) != null) {
					Main.copyRect(Main.getMountFromPoint(e.x, e.y).getBounds(), bounds);
				} else {
					PaintBox box = Main.layeredPainter.getBottomBoxAt(e.x, e.y, LayeredPainter.GIB);
					if (box != null)
						Main.copyRect(box.getBounds(), bounds);
				}
				
				Main.canvas.redraw(bounds.x-3, bounds.y-3, bounds.width+6, bounds.height+6, false);
				Main.canvas.redraw(oldBounds.x-3, oldBounds.y-3, oldBounds.width+6, oldBounds.height+6, false);
			}
		} else {
			setLocation(e.x, e.y);
		}
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {}

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
	
	private boolean canPlaceRoom() {
		return (!Main.leftMouseDown || (Main.leftMouseDown && lastClick.x >= Main.ship.anchor.x && lastClick.y >= Main.ship.anchor.y))
				&& Main.mousePos.x > Main.ship.anchor.x+1 && Main.mousePos.y > Main.ship.anchor.y+1
				&& Main.mousePos.x <= Main.GRID_W*35 && Main.mousePos.y <= Main.GRID_H*35
				&& !Main.doesRectOverlap(Main.fixRect(bounds), null);
	}
	
	private boolean canPlaceSlot(FTLRoom r) {
		return (r.getSystem().equals(Systems.PILOT) || r.getSystem().equals(Systems.SHIELDS) || r.getSystem().equals(Systems.WEAPONS)
				|| r.getSystem().equals(Systems.ENGINES) || r.getSystem().equals(Systems.MEDBAY));
	}

	@Override
	public void dispose() {
		Cache.checkInColor(this, border_rgb);
		Cache.checkInColor(this, green_rgb);
		Cache.checkInColor(this, red_rgb);
		super.dispose();
	}

}
