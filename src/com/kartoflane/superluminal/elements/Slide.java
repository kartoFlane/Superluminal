package com.kartoflane.superluminal.elements;

import org.eclipse.swt.graphics.Point;

public enum Slide {
	UP,
	DOWN,
	LEFT, 
	RIGHT,
	
	/**
	 * Used for artillery
	 */
	NO;
	
	public Point getVector() {
		Point p = new Point(0,0);
		p.x = (this==UP || this==DOWN || this==NO) ? 0 : (this==LEFT ? -1 : 1);
		p.y = (this==LEFT || this==RIGHT || this==NO) ? 0 : (this==UP ? -1 : 1);
		
		return p;
	}
	
	/**
	 * Returns the given point as the vector.
	 */
	public Point getVector(Point p) {
		p.x = (this==UP || this==DOWN || this==NO) ? 0 : (this==LEFT ? -1 : 1);
		p.y = (this==LEFT || this==RIGHT || this==NO) ? 0 : (this==UP ? -1 : 1);
		
		return p;
	}
	
	/**
	 * Multiplies and returns the given point's values by the vector
	 */
	public Point multiplyByVector(Point p) {
		p.x *= (this==UP || this==DOWN || this==NO) ? 0 : (this==LEFT ? -1 : 1);
		p.y *= (this==LEFT || this==RIGHT || this==NO) ? 0 : (this==UP ? -1 : 1);
		
		return p;
	}
}
