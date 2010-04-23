/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 * 
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */

package edu.cmu.cs.stage3.alice.core;

import java.io.IOException;

import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;

public abstract class Decorator {
	
	//Constants for PivotDecorator and BoundingBoxDecorator line widths
	private static final String PIVOT_BOUNDING_BOX_KEY = "pivotAndBoundingBoxLineWidth";
	public static final Package DECORATOR_PACKAGE = Decorator.class.getPackage();
	private static final float DEFAULT_PIVOT_AND_BOUNDING_BOX_LINE_WIDTH = 0f;
	
	////Mohammed
	private static final Configuration decoratorConfig = Configuration.getLocalConfiguration(DECORATOR_PACKAGE);
	
	protected edu.cmu.cs.stage3.alice.scenegraph.Visual m_sgVisual = null;
	protected edu.cmu.cs.stage3.alice.scenegraph.Appearance m_sgAppearance = null;

	protected abstract ReferenceFrame getReferenceFrame();

	private boolean m_isDirty = true;

	public void markDirty() {
		setIsDirty(true);
	}

	public void setIsDirty(boolean isDirty) {
		m_isDirty = isDirty;
		if (isDirty) {
			updateIfShowing();
		}
	}

	public boolean isDirty() {
		return m_isDirty;
	}

	protected void update() {
		ReferenceFrame referenceFrame = getReferenceFrame();
		if (referenceFrame != null) {
			if (m_sgAppearance == null) {
				m_sgAppearance = new edu.cmu.cs.stage3.alice.scenegraph.Appearance();
				m_sgAppearance
						.setShadingStyle(edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.NONE);
				m_sgAppearance.setBonus(referenceFrame);
			}
			if (m_sgVisual == null) {
				m_sgVisual = new edu.cmu.cs.stage3.alice.scenegraph.Visual();
				m_sgVisual.setFrontFacingAppearance(m_sgAppearance);
				m_sgVisual.setIsShowing(false);
				m_sgVisual.setBonus(referenceFrame);
			}
			m_sgVisual.setParent(referenceFrame.getSceneGraphContainer());
		}
	}

	protected void updateIfShowing() {
		if (isShowing()) {
			update();
		}
	}

	public void internalRelease(int pass) {
		switch (pass) {
		case 1:
			if (m_sgVisual != null) {
				m_sgVisual.setFrontFacingAppearance(null);
				m_sgVisual.setGeometry(null);
				m_sgVisual.setParent(null);
			}
			break;
		case 2:
			if (m_sgVisual != null) {
				m_sgVisual.release();
				m_sgVisual = null;
			}
			if (m_sgAppearance != null) {
				m_sgAppearance.release();
				m_sgAppearance = null;
			}
			break;
		}
	}

	public boolean isShowing() {
		if (m_sgVisual == null) {
			return false;
		} else {
			return m_sgVisual.getIsShowing();
		}
	}

	public void setIsShowing(boolean value) {
		if (value) {
			update();
			showRightNow();
		} else {
			hideRightNow();
		}
	}

	public void setIsShowing(Boolean value) {
		setIsShowing(value != null && value.booleanValue());
	}

	protected void showRightNow() {
		if (m_sgVisual != null) {
			m_sgVisual.setIsShowing(true);
		}
	}

	protected void hideRightNow() {
		if (m_sgVisual != null) {
			m_sgVisual.setIsShowing(false);
		}
	}
	
	/**
	 * Gets the line width used for the PivotDecorator and
	 * BoundingBoxDecorator from the Alice configuration file.
	 * 
	 * @return the line width used for the PivotDecorator and 
	 * BoundingBoxDecorator from the Alice configuration file.
	 */
	public static float getPivotAndBoundingBoxLineWidth(){
		float ret = DEFAULT_PIVOT_AND_BOUNDING_BOX_LINE_WIDTH;

		String value;// = Configuration.getValue(DECORATOR_PACKAGE, PIVOT_BOUNDING_BOX_KEY);
		if(decoratorConfig.getValue(PIVOT_BOUNDING_BOX_KEY) == null)
		{
			System.out.println("Line width is null");
			decoratorConfig.setValue(PIVOT_BOUNDING_BOX_KEY, Float.toString(ret));
			value = Float.toHexString(ret);
		
			try {
				decoratorConfig.storeConfig();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			value = decoratorConfig.getValue(PIVOT_BOUNDING_BOX_KEY);
		}
		try{
			ret = Float.parseFloat(value);
			
			//If negative, set value back to default
			if (ret < 0){
				ret = DEFAULT_PIVOT_AND_BOUNDING_BOX_LINE_WIDTH;
				setPivotAndBoundingBoxLineWidth(ret);
			}
		}catch(Exception e){
			//Set value back to default in case of Exception
			setPivotAndBoundingBoxLineWidth(ret);
		}
		
		return ret;
	}
	
	/**
	 * Sets the line width to use for the PivotDecorator and
	 * BoundingBoxDecorator in the Alice configuration properties.
	 * 
	 * @param lineWidth width to make the lines
	 * @throws IllegalArgumentException when the value is a negative number
	 */
	public static void setPivotAndBoundingBoxLineWidth(float lineWidth){
		if (lineWidth < 0)
			throw new IllegalArgumentException("A line width can not be a negative number.");
		
		Configuration.setValue(DECORATOR_PACKAGE, PIVOT_BOUNDING_BOX_KEY, lineWidth+"");
	}
}
