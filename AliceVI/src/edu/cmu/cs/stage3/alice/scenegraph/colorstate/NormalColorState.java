package edu.cmu.cs.stage3.alice.scenegraph.colorstate;

import java.awt.Color;

public class NormalColorState extends ColorState {

	public NormalColorState(edu.cmu.cs.stage3.alice.scenegraph.Color color){
		super(color);
	}

	@Override
	public float getRed() {
		return color.getRawRed();
	}
	@Override
	public float getGreen() {
		return color.getRawGreen();
	}
	@Override
	public float getBlue() {
		return color.getRawBlue();
	}
	@Override
	public Color translateColor(Color color) {
		return color;
	}

}
