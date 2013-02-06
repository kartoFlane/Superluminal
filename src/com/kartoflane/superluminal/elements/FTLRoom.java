package com.kartoflane.superluminal.elements;

import java.io.Serializable;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Class representing a single room in a ship.
 *
 */
public class FTLRoom extends Placeable implements Serializable, Comparable<FTLRoom>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2768589659254502754L;
	public int id;
	public Point[] corners = new Point[4];
	public Systems sys;
	public int slot;
	public Slide dir;
	public String img;
	public Image sysImg;
	
	public FTLRoom(int x, int y, int w, int h) {
		this.rect = new Rectangle(0,0,0,0);
		this.rect.x = x;
		this.rect.y = y;
		this.rect.width = w;
		this.rect.height = h;
		this.id = -1;
		this.corners[0] = new Point(x, y);
		this.corners[1] = new Point(x+w, y);
		this.corners[2] = new Point(x, y+h);
		this.corners[3] = new Point(x+w, y+h);
		this.sys = Systems.EMPTY;
		this.slot = -2;
		this.dir = Slide.UP;
	}
	
	public FTLRoom(Rectangle rect) {
		this.rect = new Rectangle(0,0,1,1);
		this.rect.x = rect.x;
		this.rect.y = rect.y;
		this.rect.width = rect.width;
		this.rect.height = rect.height;
		this.id = -1;
		this.corners[0] = new Point(rect.x, rect.y);
		this.corners[1] = new Point(rect.x+rect.width, rect.y);
		this.corners[2] = new Point(rect.x, rect.y+rect.height);
		this.corners[3] = new Point(rect.x+rect.width, rect.y+rect.height);
		this.sys = Systems.EMPTY;
		this.slot = -2;
		this.dir = Slide.UP;
	}
	
	public void updateCorners() {
		this.corners[0] = new Point(this.rect.x, this.rect.y);
		this.corners[1] = new Point(this.rect.x+this.rect.width, this.rect.y);
		this.corners[2] = new Point(this.rect.x, this.rect.y+this.rect.height);
		this.corners[3] = new Point(this.rect.x+this.rect.width, this.rect.y+this.rect.height);
	}
	
	/**
	 * "Lower" room has lower ID, equal rooms have equal IDs (are the same room), etc
	 */
	public int compareTo(FTLRoom r) {
		return id - r.id;
	}
	
	public static int getDefaultSlot(Systems sys) {
		int i = (sys.equals(Systems.ENGINES))
					? 2
					: (sys.equals(Systems.PILOT))
						? 0
						: (sys.equals(Systems.SHIELDS))
							? 0
							: (sys.equals(Systems.WEAPONS))
								? 1
								: (sys.equals(Systems.MEDBAY))
									? 1
									: -2;
		return i;
	}
	
	public static Slide getDefaultDir(Systems sys) {
		Slide dir = (sys.equals(Systems.ENGINES))
						? Slide.DOWN
						: (sys.equals(Systems.PILOT))
							? Slide.RIGHT
							: (sys.equals(Systems.SHIELDS))
								? Slide.LEFT
								: (sys.equals(Systems.WEAPONS))
									? Slide.UP
									: Slide.NO;
		return dir;
	}
	
	public static void draw() {
		
	}
}
