package edu.cmu.cs.stage3.alice.scenegraph.colorstate;

import java.awt.Color;

import edu.cmu.cs.stage3.alice.scenegraph.util.ColorblindConverter;

public class ColorblindColorState extends ColorState {

	public ColorblindColorState(edu.cmu.cs.stage3.alice.scenegraph.Color color) {
		super(color);
	}

	@Override
	public float getBlue() {
		return ColorblindConverter.convertToColorblind(color.createRawAWTColor()).getBlue()/255.0f;
	}

	@Override
	public float getGreen() {
		return ColorblindConverter.convertToColorblind(color.createRawAWTColor()).getGreen()/255.0f;
	}

	@Override
	public float getRed() {
		return ColorblindConverter.convertToColorblind(color.createRawAWTColor()).getRed()/255.0f;
	}

	@Override
	public Color translateColor(Color color) {
		return ColorblindConverter.convertToColorblind(color);
	}

}
