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

package edu.cmu.cs.stage3.alice.scenegraph;

/**
 * Class representing pairs of vertices with which to
 * create lines as well as a width for all of the lines.
 * 
 * @author Brett Snare
 * @author Brandon Pastuszek
 * @author Dennis Cosgrove
 */
public class LineArray extends VertexGeometry {
	
	/**
	 * Property representing a LineArray.  Used in the Property
	 * class to call getLineArray() and sends the result to LineArrayProxy
	 * in order to update the line vertices, color and weight.
	 */
	public static final Property LINE_ARRAY_PROPERTY = new Property(
			LineArray.class, "LINE_ARRAY");
	
	private float lineWidth = 0f;
	
	/**
	 * Gets the weight of the lines
	 * @return the weight of the lines
	 */
	public float getLineWidth() {
		return lineWidth; 
	}
	
	/**
	 * Sets the vertices used to create the lines.
	 * Triggers a line array changed event.
	 * 
	 * @param vertices vertices used to create the lines
	 */
	public void setVertices(Vertex3d[] vertices) {
		setVertices(vertices, LINE_ARRAY_PROPERTY);
	}
	
	/**
	 * Sets the line width used to create the lines.
	 * 
	 * @param lineWidth the width of all lines
	 */
	public void setLineWidth(float lineWidth) {
		this.lineWidth = lineWidth;
	}
	
	/**
	 * Sets the vertex and line width value of this LineArray.
	 * Required by the Property class.
	 * 
	 * @param lnArray LineArray from which to get the width
	 * and set the vertex values.
	 */
	public void setLineArray(LineArray lnArray){
		lineWidth = lnArray.getLineWidth();
		setVertices(lnArray.getVertices(),LINE_ARRAY_PROPERTY);
	}
	
	/**
	 * Gets the LineArray.  Required by the Property class.
	 * 
	 * @return this instance of the LineArray class.
	 */
	public LineArray getLineArray(){
		return this;
	}
}
