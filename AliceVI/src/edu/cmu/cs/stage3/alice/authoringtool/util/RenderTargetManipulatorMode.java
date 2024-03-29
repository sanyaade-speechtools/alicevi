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

import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo;

/**
 * @author Jason Pratt
 */
public abstract class RenderTargetManipulatorMode {
	protected edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget renderTarget;
	protected java.awt.Cursor preferredCursor;

	public edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget getRenderTarget() {
		return renderTarget;
	}

	public void setRenderTarget( edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget renderTarget ) {
		this.renderTarget = renderTarget;
	}

	public java.awt.Cursor getPreferredCursor() {
		return preferredCursor;
	}

	public void setPreferredCursor( java.awt.Cursor preferredCursor ) {
		this.preferredCursor = preferredCursor;
	}

	public abstract boolean requiresPickedObject();
	public abstract boolean hideCursorOnDrag();

	public abstract void selected( Transformable pickedTransformable, PickInfo pickInfo, Point p );
	public abstract void released(Point p);
	public abstract void dragged( int dx, int dy, boolean isControlDown, boolean isShiftDown );
}