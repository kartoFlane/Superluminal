package com.kartoflane.superluminal.elements;
import javax.swing.event.UndoableEditEvent;

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
import com.kartoflane.superluminal.undo.Undoable;
import com.kartoflane.superluminal.undo.UndoableCreateEdit;
import com.kartoflane.superluminal.undo.UndoableDirectionEdit;
import com.kartoflane.superluminal.undo.UndoableSlotEdit;


@SuppressWarnings("serial")
public class CursorBox extends PaintBox implements DraggableBox {
	private Point lastClick;
	private Color greenColor;
	private RGB green_rgb = new RGB(0,255,0);
	private Color redColor;
	private RGB red_rgb = new RGB(255,0,0);
	
	private boolean room_canBePlaced = false;
	private boolean door_canBePlaced = false;
	public boolean mount_canBePlaced = false;
	private boolean slot_canBePlaced = false;
	private Systems slot_sys = null;
	
	private PaintBox createdBox = null;
	
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
			/*
			PaintBox box = Main.getSelected();
			if (box != null && box.isPinned())
				e.gc.setForeground(pinColor);
			else */
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
			
			if (Main.modShift && (Main.leftMouseDown || Main.rightMouseDown)) {
				e.gc.setAlpha(255);
				int prevWidth = e.gc.getLineWidth();
				e.gc.setLineWidth(3);
				
				if (Main.selectedDoor != null)
					e.gc.drawLine(Main.selectedDoor.getBounds().x + Main.selectedDoor.getBounds().width/2,
							Main.selectedDoor.getBounds().y + Main.selectedDoor.getBounds().height/2,
							Main.mousePos.x, Main.mousePos.y);
				
				e.gc.setLineWidth(prevWidth);
			} else {
				e.gc.setAlpha(64);
				e.gc.fillRectangle(bounds);
			}
			
		} else if (Main.tltmMount.getSelection()) {
			if (mount_canBePlaced) {
				e.gc.setForeground(greenColor);
				e.gc.setBackground(greenColor);
			} else {
				e.gc.setForeground(redColor);
				e.gc.setBackground(redColor);
			}
			e.gc.setAlpha(255);
			FTLMount.drawDirection(e, Main.mountToolSlide, bounds);
			
			e.gc.setAlpha(64);
			e.gc.fillRectangle(bounds);
			
			e.gc.setFont(Main.appFont);
			e.gc.setAlpha(255);
			e.gc.drawString(Main.mountToolMirror
								? (Main.mountToolHorizontal
										? "/\\"
										: " <")
								: (Main.mountToolHorizontal
										? "\\/"
										: " >"),
					bounds.x + (Main.mountToolHorizontal ? 3 : (bounds.width-e.gc.stringExtent("__").x)/2),
					bounds.y + (Main.mountToolHorizontal ? (bounds.height-e.gc.stringExtent("__").y)/2-1 : 2),
					true);
			
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
	public void registerDown(int undoable) {
		if (undoListener != null) {
			if (undoable == Undoable.DIRECTION) {
				ume = new UndoableDirectionEdit(slot_sys);
				undoListener.undoableEditHappened(new UndoableEditEvent(Main.getRoomWithSystem(slot_sys).sysBox, ume));
			} else if (undoable == Undoable.SLOT) {
				ume = new UndoableSlotEdit(Main.getRoomWithSystem(slot_sys).sysBox);
				undoListener.undoableEditHappened(new UndoableEditEvent(Main.getRoomWithSystem(slot_sys).sysBox, ume));
			}
		}
	}
	
	@Override
	public void registerUp(int undoable) {
		if (ume != null) {
			if (undoable == Undoable.DIRECTION && ume instanceof UndoableDirectionEdit) {
				Slide temp = ((UndoableDirectionEdit) ume).getOldSlide();
				if (temp != Main.ship.slotDirMap.get(slot_sys)) {
					((UndoableDirectionEdit) ume).setCurrentSlide(Main.ship.slotDirMap.get(slot_sys));
					Main.addEdit(ume);
				}
			} else if (undoable == Undoable.SLOT) {
				int temp = ((UndoableSlotEdit) ume).getOldValue();
				if (temp != Main.ship.slotMap.get(slot_sys)) {
					((UndoableSlotEdit) ume).setCurrentValue(Main.ship.slotMap.get(slot_sys));
					Main.addEdit(ume);
				}
			}
		} else if ((undoable == Undoable.CREATE_ROOM || undoable == Undoable.CREATE_DOOR || undoable == Undoable.CREATE_MOUNT) && createdBox != null) {
			ume = new UndoableCreateEdit(createdBox);
			undoListener.undoableEditHappened(new UndoableEditEvent(createdBox, ume));
			Main.addEdit(ume);
			ume = null;
			createdBox = null;
		}
	}
	
	@Override
	public void mouseUp(MouseEvent e) {
		lastClick.x = e.x; lastClick.y = e.y;
		Main.dragDir = null;
		
		if (e.button == 1) {
			if (Main.tltmRoom.getSelection() && room_canBePlaced) {
				FTLRoom r = null;
				if (Main.modShift) {
					r = Main.getRoomContainingRect(bounds);
					if (bounds.width > 20) {
						r.split((bounds.y-r.getBounds().y)/35 + 1, AxisFlag.X);
					} else {
						r.split((bounds.x-r.getBounds().x)/35 + 1, AxisFlag.Y);
					}
					room_canBePlaced = !Main.isDoorAtWall(bounds);
					Main.canvas.redraw();
				} else {
					r = new FTLRoom(Main.fixRect(bounds));

					r.id = Main.getLowestId();
					r.add(Main.ship);
					
					Main.canvasRedraw(r.getBounds(), false);
					
					createdBox = r;
					registerUp(Undoable.CREATE_ROOM);
					Main.savedSinceAction = false;
				}
			} else if (Main.tltmDoor.getSelection()) {
				if (door_canBePlaced && !Main.modShift) {
					FTLDoor d = new FTLDoor(bounds.x, bounds.y, bounds.width > 10);
					d.setLocationAbsolute(bounds.x + (d.horizontal ? 0 : 35), bounds.y + (d.horizontal ? 35 : 0));
					d.fixRectOrientation();
					
					d.add(Main.ship);
					
					createdBox = d;
					registerUp(Undoable.CREATE_DOOR);
					Main.savedSinceAction = false;
				}
			} else if (Main.tltmMount.getSelection()) {
				if (mount_canBePlaced) {
					FTLMount m = new FTLMount();
					m.pos.x = e.x - Main.ship.imageRect.x;
					m.pos.y = e.y - Main.ship.imageRect.y;
					m.setRotated(Main.mountToolHorizontal);
					m.setMirrored(Main.mountToolMirror);
					m.gib = 0;
					m.slide = Main.mountToolSlide;
					
					m.setLocation(e.x, e.y);
					
					m.add(Main.ship);
					
					createdBox = m;
					registerUp(Undoable.CREATE_MOUNT);
					Main.savedSinceAction = false;
					
					//mount_canBePlaced = Main.ship.mounts.size() < (Main.ship.weaponSlots + (Main.isSystemAssigned(Systems.ARTILLERY) ? 1 : 0));
					mount_canBePlaced = Main.ship.mounts.size() < Main.MAX_MOUNTS;
				}
				
			} else if (Main.tltmSystem.getSelection()) {
				if (Main.modShift) {
					if (Main.ship.slotMap.get(slot_sys) != -2) {
						registerDown(Undoable.DIRECTION);
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

						registerUp(Undoable.DIRECTION);
						Main.canvasRedraw(Main.getRoomWithSystem(slot_sys).getBounds(), false);
					}
				} else {
					if (slot_canBePlaced) {
						registerDown(Undoable.SLOT);
						Main.ship.slotMap.put(slot_sys, Main.getStationFromRect(Main.getRectAt(e.x,e.y)));
						Main.getRoomWithSystem(slot_sys).slot = Main.ship.slotMap.get(slot_sys);
						
						registerUp(Undoable.SLOT);
						Main.canvasRedraw(Main.getRoomWithSystem(slot_sys).getBounds(), false);
					}
				}
			}
			
		} else if (e.button == 3) {
			if (Main.tltmMount.getSelection()) {
				if (Main.modAlt) {
					Main.mountToolMirror = !Main.mountToolMirror;
					Main.canvasRedraw(bounds, false);
				} else if (Main.modShift) {
					Slide s = Main.mountToolSlide;
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
					FTLMount.redrawLoc(bounds, bounds, s);
					FTLMount.redrawLoc(bounds, bounds, Main.mountToolSlide);
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
				registerDown(Undoable.SLOT);
				Main.ship.slotMap.put(slot_sys, -2);
				Main.getRoomWithSystem(slot_sys).slot = Main.ship.slotMap.get(slot_sys);

				registerUp(Undoable.SLOT);
				Main.canvasRedraw(Main.getRoomWithSystem(slot_sys).getBounds(), false);
			}
		}
		
		if (Main.tltmDoor.getSelection() && Main.selectedDoor != null) {
			if (Main.selectedDoor.getBounds().contains(Main.mousePos)) return;
			// left mouse -> left ID, right mouse -> right ID
			FTLRoom r = Main.getRoomAt(e.x, e.y);
			if (e.button == 1) {
				Main.selectedDoor.leftId = (r==null ? -2 : r.id);
			} else if (e.button == 3) {
				Main.selectedDoor.rightId = (r==null ? -2 : r.id);
			}
			
			if (Main.modShift)
				Main.canvas.redraw(Math.min(Main.mousePos.x, Main.selectedDoor.getBounds().x + Main.selectedDoor.getBounds().width/2) -5,
						Math.min(Main.mousePos.y, Main.selectedDoor.getBounds().y + Main.selectedDoor.getBounds().height/2) -5,
						Math.max(Main.mousePos.x, Main.selectedDoor.getBounds().x + Main.selectedDoor.getBounds().width/2) + 10,
						Math.max(Main.mousePos.y, Main.selectedDoor.getBounds().y + Main.selectedDoor.getBounds().height/2) + 10,
						false);
		
			Main.selectedDoor.deselect();
			Main.selectedDoor = null;
		}
	}

	@Override
	public void mouseDown(MouseEvent e) {
		lastClick.x = e.x; lastClick.y = e.y;

		if (Main.tltmDoor.getSelection()) {
			if (Main.selectedDoor!=null) Main.selectedDoor.deselect();
			if (Main.modShift) {
				Main.selectedDoor = Main.getDoorAt(Main.mousePos.x, Main.mousePos.y);
				if (Main.selectedDoor!=null) Main.selectedDoor.selectNoMove();
			}
		}
	}

	@Override
	public void mouseMove(MouseEvent e) {
		if (isVisible()) {
			Rectangle oldBounds = Main.cloneRect(bounds);
			Rectangle temp;
			
			if (Main.tltmPointer.getSelection()) {
				if (Main.anchor.getBox().contains(e.x, e.y)) {
					bounds.width = 0; bounds.height = 0;
				} else {
					temp = Main.getDoorRectAt(e.x, e.y);
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

				if (Main.modShift) {
					temp = Main.getDoorRectAt(e.x, e.y);
					if (temp != null) {
						FTLRoom r = Main.getRoomContainingRect(temp);
						if (r != null) {
							if (temp.width > 20) { // horizontal split
								temp.x = r.getBounds().x + 1;
								temp.width = r.getBounds().width - 2;
							} else { // vertical split
								temp.y = r.getBounds().y + 1;
								temp.height = r.getBounds().height - 2;
							}
							Main.copyRect(temp, bounds);
							
							room_canBePlaced = !Main.isDoorAtWall(bounds);
						} else {
							bounds.x = 0;
							bounds.y = 0;
							bounds.width = 0;
							bounds.height = 0;
						}
					}
				} else {
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
				}

				Rectangle tempRect = Main.fixRect(bounds);
				oldBounds = Main.fixRect(oldBounds);
				Main.canvas.redraw(tempRect.x-3, tempRect.y-3, tempRect.width+6, tempRect.height+6, false);
				Main.canvas.redraw(oldBounds.x-3, oldBounds.y-3, oldBounds.width+6, oldBounds.height+6, false);
				
			} else if (Main.tltmDoor.getSelection()) {
				door_canBePlaced = false;
				
				if (Main.modShift && Main.selectedDoor != null && (Main.leftMouseDown || Main.rightMouseDown)) {
					temp = Main.getRectAt(e.x, e.y);
					if (temp != null) {
						FTLRoom r = Main.getRoomAt(e.x, e.y);
						Main.copyRect((r==null ? temp : r.getBounds()), bounds);
					}
					
					door_canBePlaced = canPlaceDoor(temp);
				} else {
					temp = Main.getDoorRectAt(e.x, e.y);
					if (temp != null) {
						Main.copyRect(temp, bounds);
						door_canBePlaced = canPlaceDoor(temp);
					} else {
						bounds.width = 0;
						bounds.height = 0;
					}
				}
				
				if (Main.modShift && Main.selectedDoor != null) {
					Main.canvas.redraw(Math.min(e.x, Main.selectedDoor.getBounds().x + Main.selectedDoor.getBounds().width/2) -15,
							Math.min(e.y, Main.selectedDoor.getBounds().y + Main.selectedDoor.getBounds().height/2) -15,
							Math.max(e.x, Main.selectedDoor.getBounds().x + Main.selectedDoor.getBounds().width/2) + 30,
							Math.max(e.y, Main.selectedDoor.getBounds().y + Main.selectedDoor.getBounds().height/2) + 30,
							false);
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
				mount_canBePlaced = Main.ship.mounts.size() < Main.MAX_MOUNTS;
				
				Main.canvas.redraw(bounds.x-3, bounds.y-3, bounds.width+6, bounds.height+6, false);
				Main.canvas.redraw(oldBounds.x-3, oldBounds.y-3, oldBounds.width+6, oldBounds.height+6, false);
				FTLMount.redrawLoc(oldBounds, bounds, Main.mountToolSlide);
				
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
	
	private boolean canPlaceDoor(Rectangle temp) {
		return Main.modShift
				? ((Main.isDoorAtWall(temp) && Main.wallToDoor(temp) != null)
						|| (Main.selectedDoor != null && Main.getRoomContainingRect(Main.getRectAt(Main.mousePos.x, Main.mousePos.y)) != null))
				: (Main.isDoorAtWall(temp) && Main.wallToDoor(temp) == null);
	}

	@Override
	public void dispose() {
		Cache.checkInColor(this, border_rgb);
		Cache.checkInColor(this, green_rgb);
		Cache.checkInColor(this, red_rgb);
		super.dispose();
	}

}
