package edu.cmu.cs.stage3.alice.scenegraph.colorstate;

import edu.cmu.cs.stage3.alice.scenegraph.Color;

/**
 * Interface for translating RGB values.
 * 
 * @author Brett Snare (bws7783@rit.edu)
 * @author Brandon Pastuszek (bjp5129@rit.edu)
 */
public interface ColorState {
	public float getRed(Color color);
	public float getGreen(Color color);
	public float getBlue(Color color);
	public java.awt.Color translateColor(java.awt.Color  color);
}
