package com.kartoflane.superluminal.painter;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;


public class ImageBox extends PaintBox {
	protected String path;
	protected Image image;
	protected int alpha = 255;
	protected int rotation = 0;
	protected Rectangle redrawBounds;
	protected boolean shrinkWrap = false;
	
	public ImageBox() {
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
		//redrawBounds.x = Math.round((float) (bounds.x - Math.abs(bounds.width*Math.sin(Math.toRadians(rotation*2))/2)));
		//redrawBounds.y = Math.round((float) (bounds.y - Math.abs(bounds.height*Math.sin(Math.toRadians(rotation*2))/2)));
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
		//redrawBounds.width = Math.round((float) (w + Math.abs(w*Math.sin(Math.toRadians(rotation*2)))));
		//redrawBounds.height = Math.round((float) (h + Math.abs(h*Math.sin(Math.toRadians(rotation*2)))));
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
		// The image's bounds are used to determine its size, but also the area that needs redrawing. When an image
		// is rotated, some of its parts may exceed the bounds, thus leaving marks on the canvas when the image is dragged.
		// Returning a modified bounds rectangle to correct for that, without messing with the image's size / position.
		
		// Multiplying rotation by 2 so that the max value of sine is reached at 45 degrees, instead of 90 (when the biggest
		// correction is required)
		// Using inherited getBounds() so that no casting from PaintBox to ImageBox is required to access these modified bounds.
		
		// Perhaps alternatively, the ImageBox's own paintControl could be used to redraw the "rotated" area around the image?
		// Would require less hassle modifying existing selection function to accomodate.
		/*
		return new Rectangle(Math.round((float) (bounds.x - Math.abs(bounds.width*Math.sin(Math.toRadians(rotation*2))/2))),
							Math.round((float) (bounds.y - Math.abs(bounds.height*Math.sin(Math.toRadians(rotation*2))/2))),
							Math.round((float) (bounds.width + Math.abs(bounds.width*Math.sin(Math.toRadians(rotation*2))))),
							Math.round((float) (bounds.height + Math.abs(bounds.height*Math.sin(Math.toRadians(rotation*2))))));
							*/
		
		// It's probably better to just keep a single instance of Rectangle per each ImageBox, which gets updated only when accessed,
		// than create a new instane each time the method is called.
		redrawBounds.x = Math.round((float) (bounds.x - Math.abs(bounds.width*Math.sin(Math.toRadians(rotation*2))/2)));
		redrawBounds.y = Math.round((float) (bounds.y - Math.abs(bounds.height*Math.sin(Math.toRadians(rotation*2))/2)));
		redrawBounds.width = Math.round((float) (bounds.width + Math.abs(bounds.width*Math.sin(Math.toRadians(rotation*2)))));
		redrawBounds.height = Math.round((float) (bounds.height + Math.abs(bounds.height*Math.sin(Math.toRadians(rotation*2)))));
		
		return redrawBounds;
	}
	
	@Override
	public void paintControl(PaintEvent e) {
		super.paintControl(e);
	
		int prevAlpha = e.gc.getAlpha();

		e.gc.setAdvanced(true);
		e.gc.setAlpha(alpha);

		Transform transform = new Transform(e.gc.getDevice());
		transform.translate(bounds.x + bounds.width/2, bounds.y + bounds.height/2);
		transform.rotate(rotation);
		transform.translate(-bounds.x - bounds.width/2, -bounds.y - bounds.height/2);
		e.gc.setTransform(transform);

		// The transform applies in reverse order:
		//   Shift coords so the middle of the bounding rect is 0,0.
		//   Spin around 0,0.
		//   Shift back to where you started.

		// Everything you draw will be sent through the funhouse mirror
		// until the transform is disposed.

		// The image will shift, spin, and shift back. No trig needed,
		// and it will appear to have rotated about the bounds' center.

		e.gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height,
		               bounds.x, bounds.y, bounds.width, bounds.height);

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
