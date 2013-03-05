package com.kartoflane.superluminal.elements;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public interface DraggableBox {	
	public Rectangle getBounds();
	
	public void setVisible(boolean vis);
	
	public boolean isVisible();
	
	public void setLocation(int x, int y);
	
	public Point getLocation();
	
	public void setSize(int w, int h);
	
	public Point getSize();

	/**
	 * Sets the "original" point where the dragee was prior to being moved, NOT THE OFFSET.
	 */
	public void setOffset(int x, int y);
	
	public Point getOffset();
	
	public void mouseUp(MouseEvent e);
	
	public void mouseDown(MouseEvent e);
	
	public void mouseMove(MouseEvent e);
	
	public void mouseDoubleClick(MouseEvent e);
	
	public void mouseHover(MouseEvent e);
	
	public void select();
	
	public void deselect();
}
