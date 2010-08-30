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

public class CopyMode extends DefaultMoveMode {
	protected Class[] classesToShare = {
		edu.cmu.cs.stage3.alice.core.Sound.class,
		edu.cmu.cs.stage3.alice.core.TextureMap.class
	};

	public CopyMode() {
		super();
	}

	public CopyMode( edu.cmu.cs.stage3.alice.authoringtool.MainUndoRedoStack undoRedoStack, edu.cmu.cs.stage3.alice.core.Scheduler scheduler  ) {
		super( undoRedoStack, scheduler );
	}

	private boolean hasBeenDragged;
	
	public void selected( edu.cmu.cs.stage3.alice.core.Transformable pickedTransformable, edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pickInfo, Point p ) {
		undoRedoStack.startCompound();
		if( pickedTransformable != null ) {
			String name = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getNameForNewChild( pickedTransformable.name.getStringValue(), pickedTransformable.getParent() );
			int index = pickedTransformable.getParent().getIndexOfChild( pickedTransformable ) + 1;
			pickedTransformable = (edu.cmu.cs.stage3.alice.core.Transformable)pickedTransformable.HACK_createCopy( name, pickedTransformable.getParent(), index, classesToShare, null );
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.addElementToAppropriateProperty( pickedTransformable, pickedTransformable.getParent() );
		}
		super.selected( pickedTransformable, pickInfo, p );
		hasBeenDragged = false;
	}

	public void released(Point p ) {
		if( !hasBeenDragged ) {
			edu.cmu.cs.stage3.alice.core.response.MoveAnimation moveAnimation = new edu.cmu.cs.stage3.alice.core.response.MoveAnimation();
			moveAnimation.subject.set( pickedTransformable );
			moveAnimation.direction.set( edu.cmu.cs.stage3.alice.core.Direction.FORWARD );
			moveAnimation.amount.set( new Double( 1 ) );
			moveAnimation.isScaledBySize.set( Boolean.TRUE );

			edu.cmu.cs.stage3.alice.core.response.MoveAnimation undoAnimation = new edu.cmu.cs.stage3.alice.core.response.MoveAnimation();
			undoAnimation.subject.set( pickedTransformable );
			undoAnimation.direction.set( edu.cmu.cs.stage3.alice.core.Direction.FORWARD );
			undoAnimation.amount.set( new Double( -1 ) );
			undoAnimation.isScaledBySize.set( Boolean.TRUE );

			edu.cmu.cs.stage3.alice.core.Property[] affectedProperties = new edu.cmu.cs.stage3.alice.core.Property[] { 
					pickedTransformable.localTransformation
			};

			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getInstance().performOneShot( moveAnimation, undoAnimation, affectedProperties );
		}
		undoRedoStack.stopCompound();
	}
	
	public void dragged( int dx, int dy, boolean isControlDown, boolean isShiftDown ) {
		super.dragged( dx, dy, isControlDown, isShiftDown );
		hasBeenDragged = true;
	}
}