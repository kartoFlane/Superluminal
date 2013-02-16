package com.kartoflane.superluminal.painter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;


public class ColorBox extends PaintBox {
	private Color color = null;
	private RGB color_rgb = null;
	
	public ColorBox() {
	}

	public ColorBox(RGB rgb) {
		this();
		setColor(rgb);
	}

	public void setColor(RGB rgb) {
		if (color_rgb != null)
			Cache.checkInColor(this, color_rgb);
		color = Cache.checkOutColor(this, rgb);
		color_rgb = rgb;
	}
	
	@Override
	public void paintControl(PaintEvent e) {
		super.paintControl(e);
		
		if (color != null) {
			Color prevBgColor = e.gc.getBackground();
			int prevAlpha = e.gc.getAlpha();
			
			e.gc.setBackground(color);
			e.gc.setAlpha(alpha);
	
			// Rect graphics funcs include the max,
			// so 0 <= W would have been W+1 pixels wide.
			e.gc.fillRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	
			e.gc.setBackground(prevBgColor);
			e.gc.setAlpha(prevAlpha);
		}
	}

	@Override
	public void dispose() {
		Cache.checkInColor(this, color_rgb);
		super.dispose();
	}
}
