package com.kartoflane.superluminal.painter;

import java.io.Serializable;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;


public class ImageBox extends PaintBox implements Serializable {
	private static final long serialVersionUID = -8262925655442154701L;
	
	protected String path;
	protected Image image;
	protected int alpha = 255;
	protected int rotation = 0;
	protected Rectangle redrawBounds;
	protected boolean shrinkWrap = false;
	protected Point imageLoc = new Point(0,0);
	
	public void stripUnserializable() {
		super.stripUnserializable();
		Cache.checkInImage(this, path);
		image = null;
	}
	
	public void loadUnserializable() {
		super.loadUnserializable();
		image = Cache.checkOutImage(this, path);
		image = Cache.checkOutImageAbsolute(this, path);
	}
	
	public ImageBox() {
		super();
		redrawBounds = new Rectangle(bounds.x,bounds.y,bounds.width,bounds.height);
	}
	
	public void setImage(String path, boolean shrinkWrap) {
		if (this.path != null) {
			Cache.checkInImage(this, this.path);
		}

		this.shrinkWrap = shrinkWrap;
		this.path = path;
		image = Cache.checkOutImage(this, path);
		setSize(image.getBounds().width, image.getBounds().height);
	}
	
	public String getPath() {
		return path;
	}
	
	public void setShrinkBounds(boolean shrink) {
		shrinkWrap = shrink;
	}

	public void setAlpha(int newAlpha) {
		alpha = newAlpha;
	}
	
	/**
	 * Sets rotation, in degrees counterclockwise.
	 */
	public void setRotation(int rotation) {
		this.rotation = rotation;
	}
	
	public int getRotation() {
		return rotation;
	}
	
	public void setLocation(int x, int y) {
		bounds.x = x;
		bounds.y = y;
	}
	
	private void setRedrawSize(int imageW, int imageH) {
		if (shrinkWrap) {
			redrawBounds.width = imageW;
			redrawBounds.height = imageH;
		} else {
			// Make bounds big enough for any rotation.
			int diagDist = (int)Math.ceil(Math.sqrt(imageW*imageW+imageH*imageH));
			redrawBounds.width = diagDist;
			redrawBounds.height = diagDist;
		}
	}
	
	public void setSize(int w, int h) {
		bounds.width = w;
		bounds.height = h;
		setRedrawSize(w, h);
	}
	
	public Rectangle getBounds() {
		return bounds;
	}
	
	public Rectangle getRedrawBounds() {
		redrawBounds.x = Math.round((float) (bounds.x - Math.abs(bounds.width*Math.sin(Math.toRadians(rotation*2))/2)));
		redrawBounds.y = Math.round((float) (bounds.y - Math.abs(bounds.height*Math.sin(Math.toRadians(rotation*2))/2)));
		redrawBounds.width = Math.round((float) (bounds.width + Math.abs(bounds.width*Math.sin(Math.toRadians(rotation*2)))));
		redrawBounds.height = Math.round((float) (bounds.height + Math.abs(bounds.height*Math.sin(Math.toRadians(rotation*2)))));
		
		return redrawBounds;
	}
	
	/**
	 * Used more to store the image's location, not actually set it.
	 */
	public void setImageLoc(int x, int y) {
		imageLoc.x = x;
		imageLoc.y = y;
	}
	
	public Point getImageLoc() {
		return imageLoc;
	}
	
	@Override
	public void paintControl(PaintEvent e) {
		super.paintControl(e);
	
		int prevAlpha = e.gc.getAlpha();

		e.gc.setAdvanced(true);
		e.gc.setAlpha(alpha);

		Transform transform = null;
		if (!shrinkWrap) {
			transform = new Transform(e.gc.getDevice());
			transform.translate(bounds.x + bounds.width/2, bounds.y + bounds.height/2);
			transform.rotate(rotation);
			transform.translate(-bounds.x - bounds.width/2, -bounds.y - bounds.height/2);
			e.gc.setTransform(transform);
		}

		// The transform applies in reverse order:
		//   Shift coords so the middle of the bounding rect is 0,0.
		//   Spin around 0,0.
		//   Shift back to where you started.

		// Everything you draw will be sent through the funhouse mirror
		// until the transform is disposed.

		// The image will shift, spin, and shift back. No trig needed,
		// and it will appear to have rotated about the bounds' center.

		e.gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, bounds.x, bounds.y, bounds.width, bounds.height);

		if (!shrinkWrap)
			transform.dispose();
		e.gc.setAlpha(prevAlpha);
		e.gc.setAdvanced(false);
	}

	@Override
	public void dispose() {
		if (path != null) Cache.checkInImage(this, path);
		super.dispose();
		path = null;
	}
}
