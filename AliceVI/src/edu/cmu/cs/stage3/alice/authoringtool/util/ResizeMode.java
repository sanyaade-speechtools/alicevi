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
public class ResizeMode extends RenderTargetManipulatorMode {
	protected edu.cmu.cs.stage3.alice.core.Transformable pickedTransformable;
	protected edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack undoRedoStack;
	protected edu.cmu.cs.stage3.alice.core.Scheduler scheduler;
	protected javax.vecmath.Vector3d oldSize;

	public ResizeMode( edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack undoRedoStack, edu.cmu.cs.stage3.alice.core.Scheduler scheduler  ) {
		this.undoRedoStack = undoRedoStack;
		this.scheduler = scheduler;
	}

	public boolean requiresPickedObject() {
		return true;
	}

	public boolean hideCursorOnDrag() {
		return true;
	}

	public void selected( edu.cmu.cs.stage3.alice.core.Transformable pickedTransformable, edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pickInfo, Point p ) {
		this.pickedTransformable = pickedTransformable;
		if( pickedTransformable != null ) {
			oldSize = pickedTransformable.getSize();
		}
	}

	public void released(Point p ) {
		if( (pickedTransformable != null) && (undoRedoStack != null)  ) {
			undoRedoStack.push( new SizeUndoableRedoable( pickedTransformable, oldSize, pickedTransformable.getSize(), scheduler ) );
			if( pickedTransformable.poses.size() > 0 ) {
				edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog( "Warning: resizing objects with poses may make those poses unusable.", "Pose warning", javax.swing.JOptionPane.WARNING_MESSAGE );
			}
		}
	}

	public void dragged( int dx, int dy, boolean isControlDown, boolean isShiftDown ) {
		if( (pickedTransformable != null) && (dy != 0) ) {
			double divisor = isShiftDown ? 1000.0 : 50.0;
			double scaleFactor = 1.0 - ((double)dy)/divisor;
			pickedTransformable.resizeRightNow( scaleFactor );
		}
	}
}