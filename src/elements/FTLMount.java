package elements;

import java.io.Serializable;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Weapon mount class.
 */
public class FTLMount extends Placeable implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6519211644795332479L;
	public static final int MOUNT_WIDTH = 60;
	public static final int MOUNT_HEIGHT = 20;
	
	public Point pos;
	
	/**
	 * True means the weapons are oriented horizontally, while false means they're oriented vertically
	 * (Bascially true for player ships, false for enemy ships)
	 */
	public boolean rotate;
	
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
	 * Gib id to which the weapon is attached when the ship explodes or relative to which it is attached? TODO
	 */
	public int gib;
	
	
	public FTLMount() {
		super();
		pos = new Point(0,0);
	}
	
	public static void drawMirror(PaintEvent e, boolean rotate, boolean mirror, Rectangle rect) {
		final int BASE = 16;
		// HEIGHT = BASE / 2 * sqrt(3);
		// sqrt(3) = ~1.732
		// HEIGHT = BASE * ~1.155
		
		Point center = new Point(0,0);
		center.x = rect.x + rect.width/2;
		center.y = rect.y + rect.height/2;
		
		if (rotate) {
			if (mirror) {
				int[] arrow = { (int) (center.x+rect.width*0.75), center.y+((int)(BASE*1.155))/2, (int) (center.x+rect.width*0.75+BASE/2), center.y+((int)(BASE*1.155))/2,
								(int) (center.x+rect.width*0.75), center.y-((int)(BASE*1.155))/3, (int) (center.x+rect.width*0.75-BASE/2), center.y+((int)(BASE*1.155))/2};
				
				e.gc.fillPolygon(arrow);
				e.gc.drawPolygon(arrow);
			} else {
				int[] arrow = { (int) (center.x+rect.width*0.75), center.y-((int)(BASE*1.155))/2, (int) (center.x+rect.width*0.75+BASE/2), center.y-((int)(BASE*1.155))/2,
								(int) (center.x+rect.width*0.75), center.y+((int)(BASE*1.155))/3, (int) (center.x+rect.width*0.75-BASE/2), center.y-((int)(BASE*1.155))/2};
		
				e.gc.fillPolygon(arrow);
				e.gc.drawPolygon(arrow);
			}
		} else {
			if (mirror) {
				int[] arrow = { center.x+((int)(BASE*1.155))/2, (int)(center.y-rect.height*0.75), (int) center.x+((int)(BASE*1.155))/2, (int)(center.y-rect.height*0.75-BASE/2),
								center.x-((int)(BASE*1.155))/3, (int)(center.y-rect.height*0.75), (int) center.x+((int)(BASE*1.155))/2, (int)(center.y-rect.height*0.75+BASE/2)};
				
				e.gc.fillPolygon(arrow);
				e.gc.drawPolygon(arrow);
			} else {
				int[] arrow = { center.x-((int)(BASE*1.155))/2, (int)(center.y-rect.height*0.75), (int) center.x-((int)(BASE*1.155))/2, (int)(center.y-rect.height*0.75-BASE/2),
								center.x+((int)(BASE*1.155))/3, (int)(center.y-rect.height*0.75), (int) center.x-((int)(BASE*1.155))/2, (int)(center.y-rect.height*0.75+BASE/2)};
		
				e.gc.fillPolygon(arrow);
				e.gc.drawPolygon(arrow);
			}
		}
	}
	
	public static void drawDirection(PaintEvent e, Slide s, Rectangle rect) {
		final int LENGTH = 30;
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
}
