package edu.cmu.cs.stage3.alice.scenegraph.colorstate;



import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.alice.scenegraph.util.ColorblindConverter;

/**
 * Class representing a colorblind color state.  Colors
 * provided are translated via an external colorblind
 * translation algorithm.
 * 
 * @author Brett Snare (bws7783@rit.edu)
 * @author Brandon Pastuszek (bjp5129@rit.edu)
 */
public class ColorblindColorState implements ColorState {


	@Override
	public float getBlue(Color color) {
		return translateColor(color.createRawAWTColor()).getBlue()/255.0f;
	}

	@Override
	public float getGreen(Color color) {
		return translateColor(color.createRawAWTColor()).getGreen()/255.0f;
	}

	@Override
	public float getRed(Color color) {
		return translateColor(color.createRawAWTColor()).getRed()/255.0f;
	}

	@Override
	public java.awt.Color translateColor(java.awt.Color color) {
		return ColorblindConverter.convertToColorblind(color);
	}

}
