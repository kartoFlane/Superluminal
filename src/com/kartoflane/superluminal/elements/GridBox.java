package com.kartoflane.superluminal.elements;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.kartoflane.superluminal.painter.Cache;
import com.kartoflane.superluminal.painter.PaintBox;


public class GridBox extends PaintBox {
	private Color gridColor;
	private RGB grid_rgb;

	public GridBox() {
		setGridColor(new RGB(128, 128, 128));
		setSize(35, 35);
	}
	
	public void setGridColor(RGB rgb) {
		gridColor = Cache.checkOutColor(this, rgb);
		grid_rgb = rgb;
	}
	
	public Color getGridColor(Object customer) {
		// not returned via Cache, since grid will never be disposed before rooms.
		return gridColor;
	}
	
	@Override
	public void paintControl(PaintEvent e) {
		super.paintControl(e);
		Color prevBgColor = e.gc.getForeground();
		
		int prevAlpha = e.gc.getAlpha();
		int prevWidth = e.gc.getLineWidth();

		e.gc.setForeground(gridColor);

		e.gc.setAlpha(255);
		e.gc.setLineWidth(1);
		
		e.gc.drawRectangle(bounds);
		
		/*
		for (int i=0; i <= Main.GRID_W; i++)
			e.gc.drawLine(i*35, 0, i*35, Main.canvas.getSize().y);
		for (int i=0; i <= Main.GRID_H; i++)
			e.gc.drawLine(0, i*35, Main.canvas.getSize().x, i*35);
		*/

		e.gc.setForeground(prevBgColor);
		e.gc.setAlpha(prevAlpha);
		e.gc.setLineWidth(prevWidth);
	}

	@Override
	public void dispose() {
		Cache.checkInColor(this, grid_rgb);
		super.dispose();
	}
}
