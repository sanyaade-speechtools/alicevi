package edu.cmu.cs.stage3.alice.scenegraph.colorstate;

import edu.cmu.cs.stage3.alice.scenegraph.Color;

public abstract class ColorState {
	
	protected Color color;
	
	public ColorState(Color color){
		this.color = color;
	}
	
	public abstract float getRed();
	public abstract float getGreen();
	public abstract float getBlue();
	public abstract java.awt.Color translateColor(java.awt.Color  color);
}
