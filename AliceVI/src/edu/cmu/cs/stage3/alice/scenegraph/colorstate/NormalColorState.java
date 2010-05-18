package edu.cmu.cs.stage3.alice.scenegraph.colorstate;

import edu.cmu.cs.stage3.alice.scenegraph.Color;


/**
 * Class representing untranslated color values.
 * Methods in this class return untranslated (normal)
 * color values.
 * 
 * @author Brett Snare (bws7783@rit.edu)
 * @author Brandon Pastuszek (bjp5129@rit.edu)
 */
public class NormalColorState implements ColorState {

	@Override
	public float getRed(Color color) {
		return color.getRawRed();
	}
	@Override
	public float getGreen(Color color) {
		return color.getRawGreen();
	}
	@Override
	public float getBlue(Color color) {
		return color.getRawBlue();
	}
	@Override
	public java.awt.Color translateColor(java.awt.Color color) {
		return color;
	}

}
