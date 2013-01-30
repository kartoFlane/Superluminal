package elements;

import java.io.Serializable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;


/**
 * Class representing a single door in a ship.
 *
 */
public class FTLDoor extends Placeable implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4263579449630517639L;
	public boolean horizontal;
	
	public FTLDoor(Point pos, boolean horizontal) {
		super();
		this.rect = (horizontal) ? (new Rectangle(pos.x, pos.y, 31, 6)) : (new Rectangle(pos.x, pos.y, 6, 31));
		this.horizontal = horizontal;
	}
	
	public FTLDoor(int x, int y, boolean horizontal) {
		super();
		this.rect = (horizontal) ? (new Rectangle(x, y, 31, 6)) : (new Rectangle(x, y, 6, 31));
		this.horizontal = horizontal;
	}
	
	public FTLDoor() {
		super();
	}
	
	public void fixRectOrientation() {
		this.rect.width = (horizontal) ? (31) : (6);
		this.rect.height = (horizontal) ? (6) : (31);
	}
	
	public void drawDoor(PaintEvent e) {
		Color c;
		c = e.display.getSystemColor(SWT.COLOR_BLACK);
		e.gc.setBackground(c);
		c.dispose();
		c = e.display.getSystemColor(SWT.COLOR_BLACK);
		e.gc.setForeground(c);
		c.dispose();
		
		if (horizontal) {
			e.gc.fillRectangle(rect.x, rect.y+1, 5, 4);
			e.gc.fillRectangle(rect.x+rect.width-4, rect.y+1, 5, 4);
			e.gc.drawRectangle(rect.x+5, rect.y, 21, 5);
	
			c = new Color(e.display, 255, 150, 48);
			e.gc.setBackground(c);
			e.gc.fillRectangle(rect.x+6, rect.y+1, 20, 4);
			c.dispose();
			e.gc.drawRectangle(rect.x+15, rect.y, 1, 5);
		} else {
			e.gc.fillRectangle(rect.x+1, rect.y, 4, 5);
			e.gc.fillRectangle(rect.x+1, rect.y+rect.height-4, 4, 5);
			e.gc.drawRectangle(rect.x, rect.y+5, 5, 21);
		
			c = new Color(e.display, 255, 150, 48);
			e.gc.setBackground(c);
			e.gc.fillRectangle(rect.x+1, rect.y+6, 4, 20);
			c.dispose();
			e.gc.drawRectangle(rect.x, rect.y+15, 5, 1);
		}
	}
}
