package com.kartoflane.superluminal.elements;

import java.io.Serializable;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.painter.Cache;
import com.kartoflane.superluminal.painter.ImageBox;

public class SystemBox extends ImageBox implements Serializable {
	private static final long serialVersionUID = -7733605782672884932L;
	private FTLRoom room;
	private Systems sys;
	private boolean availableAtStart = true;
	private int sysLevel = 0;

	public Image interior = null;
	public FTLInterior interiorData = null;

	public SystemBox(Systems name) {
		super();
		interiorData = new FTLInterior();
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
			setLocation(room.getBounds().x + room.getBounds().width / 2 - bounds.width / 2, room.getBounds().y + room.getBounds().height / 2 - bounds.height / 2);

			Main.canvasRedraw(oldBounds, false);
			Main.canvasRedraw(bounds, false);
		}
	}

	public void paintControl(PaintEvent e) {
		if (!Main.tltmGib.getSelection())
			super.paintControl(e);
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
		if (room != null)
			room.interiorData = null;
		room = r;
		room.interiorData = interiorData;
		updateLocation();
	}

	public void setGlowImage(String path, int number) {
		if (room == null) {
			Main.debug("Error - setGlowImage: system is not assigned to any room");
			return;
		}

		if (number == 1) {
			interiorData.glowPath1 = path;
		} else if (number == 2) {
			interiorData.glowPath2 = path;
		} else if (number == 3) {
			interiorData.glowPath3 = path;
		} else {
			Main.debug("Warning - setGlowImage: illegal number provided: " + number);
		}
	}

	public void unassign() {
		if (room != null)
			room.interiorData = null;
		room = null;
		setVisible(false);
	}

	public FTLRoom getRoom() {
		return room;
	}

	public void clearInterior() {
		if (interior != null)
			Cache.checkInImageAbsolute(this, interiorData.interiorPath);
		interior = null;
		if (interiorData != null) {
			interiorData.interiorPath = null;
			interiorData.glowPath1 = null;
			interiorData.glowPath2 = null;
			interiorData.glowPath3 = null;
		}
	}

	public void dispose() {
		clearInterior();
		unassign();
		super.dispose();
	}
}
