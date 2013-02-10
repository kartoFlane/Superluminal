package com.kartoflane.superluminal.elements;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;

public interface DraggableBox
{
	public Rectangle getBounds();
	
	public void setLocation(int x, int y);
	
	public void setSize(int w, int h);
	
	public void mouseUp(MouseEvent e);
	
	public void mouseDown(MouseEvent e);
	
	public void mouseMove(MouseEvent e);
	
	public void mouseDoubleClick(MouseEvent e);
}
