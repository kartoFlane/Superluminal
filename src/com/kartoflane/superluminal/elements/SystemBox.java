package com.kartoflane.superluminal.elements;

import java.io.Serializable;

import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.painter.ImageBox;


public class SystemBox extends ImageBox implements Serializable {
	private static final long serialVersionUID = -7733605782672884932L;
	private FTLRoom room;
	private Systems sys;
	private boolean availableAtStart = true;
	private int sysLevel = 0;
	
	public SystemBox(Systems name) {
		super();
		sys = name;
		bounds.x = 0;
		bounds.y = 0;
		bounds.width = 32;
		bounds.height = 32;
		setVisible(false);
	}
	
	public void updateLocation() {
		if (room != null) {
			Rectangle oldBounds = Main.cloneRect(redrawBounds);
			setLocation(room.getBounds().x + room.getBounds().width/2 - bounds.width/2, room.getBounds().y + room.getBounds().height/2 - bounds.height/2);
			
			Main.canvasRedraw(oldBounds,false);
			Main.canvasRedraw(bounds, false);
		}
	}
	
	public void setLevel(int l) {
		sysLevel = l;
	}
	
	public int getLevel() {
		return sysLevel;
	}
	
	public void setAvailable(boolean avail) {
		availableAtStart = avail;
	}
	
	public boolean isAvailable() {
		return availableAtStart;
	}
	
	public Systems getSystemName() {
		return sys;
	}
	
	public void setRoom(FTLRoom r) {
		room = r;
		updateLocation();
	}
	
	public void unassign() {
		room = null;
		setVisible(false);
	}
	
	public FTLRoom getRoom() {
		return room;
	}
	
	public void dispose() {
		super.dispose();
	}
}
