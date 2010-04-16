package edu.cmu.cs.stage3.alice.scenegraph.colorstate;



import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.alice.scenegraph.util.ColorblindConverter;

public class ColorblindColorState implements ColorState {


	@Override
	public float getBlue(Color color) {
		return ColorblindConverter.convertToColorblind(color.createRawAWTColor()).getBlue()/255.0f;
	}

	@Override
	public float getGreen(Color color) {
		return ColorblindConverter.convertToColorblind(color.createRawAWTColor()).getGreen()/255.0f;
	}

	@Override
	public float getRed(Color color) {
		return ColorblindConverter.convertToColorblind(color.createRawAWTColor()).getRed()/255.0f;
	}

	@Override
	public java.awt.Color translateColor(java.awt.Color color) {
		return ColorblindConverter.convertToColorblind(color);
	}

}
