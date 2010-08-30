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

package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.awt.Point;

/**
 * @author Jason Pratt
 */
public class OrthographicZoomMode extends RenderTargetManipulatorMode {
	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	protected edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack undoRedoStack;
	protected edu.cmu.cs.stage3.alice.core.Scheduler scheduler;
	protected java.awt.Point pressPoint = new java.awt.Point();
	protected java.awt.Dimension renderSize = new java.awt.Dimension();
	protected edu.cmu.cs.stage3.alice.core.Camera camera = null;

	public OrthographicZoomMode( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool, edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack undoRedoStack, edu.cmu.cs.stage3.alice.core.Scheduler scheduler ) {
		this.authoringTool = authoringTool;
		this.undoRedoStack = undoRedoStack;
		this.scheduler = scheduler;
	}

	public boolean requiresPickedObject() {
		return false;
	}

	public boolean hideCursorOnDrag() {
		return true;
	}

	public void selected( edu.cmu.cs.stage3.alice.core.Transformable pickedTransformable, edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pickInfo, Point p ) {
		camera = (edu.cmu.cs.stage3.alice.core.Camera)pickInfo.getSource().getBonus();
		pressPoint.setLocation( p );
	}

	public void released(Point p) {
		if( p.equals( pressPoint ) ) {
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog( "Click and drag to zoom.", "Zoom Message", javax.swing.JOptionPane.INFORMATION_MESSAGE );
		} 
	}

	public void dragged( int dx, int dy, boolean isControlDown, boolean isShiftDown ) {
		if( camera instanceof edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera ) {
			if( (dx != 0) || (dy != 0) ) {
				double divisor = isShiftDown ? 1000.0 : 50.0;
				double scaleFactor;
				if( Math.abs( dx ) > Math.abs( dy ) ) {
					scaleFactor = 1.0 - ((double)dx)/divisor;
				} else {
					scaleFactor = 1.0 - ((double)dy)/divisor;
				}

				edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera orthoCamera = (edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera)camera;
				renderTarget.getAWTComponent().getSize( renderSize );

				double oldMinY = orthoCamera.minimumY.getNumberValue().doubleValue();
				double oldMaxY = orthoCamera.maximumY.getNumberValue().doubleValue();
				double oldPosX = orthoCamera.getPosition().x;
				double oldPosY = orthoCamera.getPosition().y;
				double oldHeight = oldMaxY - oldMinY;
				double pixelHeight = oldHeight/renderSize.getHeight();

				// (pressDX,pressDY) is vector from camera position to clicked point in world space
				double pressDX = (pressPoint.getX() - (renderSize.getWidth()/2.0))*pixelHeight;
				double pressDY = -(pressPoint.getY() - (renderSize.getHeight()/2.0))*pixelHeight;
				double pressX = oldPosX + pressDX;
				double pressY = oldPosY + pressDY;

				double newPosX = pressX - scaleFactor*pressDX;
				double newPosY = pressY - scaleFactor*pressDY;

				double newHeight = oldHeight*scaleFactor;
				double newMinY = -newHeight/2.0;
				double newMaxY = -newMinY;

				orthoCamera.setPositionRightNow( newPosX, newPosY, 0.0 );
				orthoCamera.minimumY.set( new Double( newMinY ) );
				orthoCamera.maximumY.set( new Double( newMaxY ) );
			}
		}
	}
}