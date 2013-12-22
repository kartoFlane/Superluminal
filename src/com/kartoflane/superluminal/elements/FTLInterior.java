package com.kartoflane.superluminal.elements;

import java.io.Serializable;

public class FTLInterior implements Serializable {
	private static final long serialVersionUID = -4323115239281306665L;
	
	public String interiorPath = "";
	public String glowPath1 = "";
	public String glowPath2 = "";
	public String glowPath3 = "";
	
	public FTLInterior() {
		interiorPath = new String("");
		glowPath1 = new String("");
		glowPath2 = new String("");
		glowPath3 = new String("");
	}
}
