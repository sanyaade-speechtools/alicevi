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

import java.awt.Component;
import java.awt.Point;

/**
 * @author Jason Pratt
 */
public class RenderTargetMultiManipulator extends RenderTargetPickManipulator {
	protected edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetManipulatorMode mode;

	public RenderTargetMultiManipulator( edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget renderTarget ) {
		super( renderTarget );
		setPopupEnabled( true );
	}

	public edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetManipulatorMode getMode() {
		return mode;
	}

	public void setMode( edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetManipulatorMode mode ) {
		this.mode = mode;

		if( mode != null ) {
			setHideCursorOnDrag( mode.hideCursorOnDrag() );
			if( renderTarget.getAWTComponent() != null ) {
				if( mode.getPreferredCursor() != null ) {
					renderTarget.getAWTComponent().setCursor( mode.getPreferredCursor() );
				} else {
					renderTarget.getAWTComponent().setCursor( java.awt.Cursor.getDefaultCursor() );
				}
			}
		}
	}

	public void mousePressed( java.awt.event.MouseEvent ev ) {
		if( enabled ) {
			super.mousePressed( ev );

			
		}
	}
	
	@Override
	public void selected(Component c, Point p) {
		super.selected(c, p);
		if( mode != null ) {
			if( mode.requiresPickedObject() && (ePickedTransformable != null) && (! ePickedTransformable.doEventsStopAscending()) && ascendTreeEnabled ) {
				abortAction();
			} else if( mode.requiresPickedObject() && (ePickedTransformable == null) ) {
				abortAction();
			} else {
				mode.setRenderTarget( renderTarget );
				mode.selected( ePickedTransformable, pickInfo, p );
			}
		} else {
			abortAction();
		}
	}

	public void mouseReleased( java.awt.event.MouseEvent ev ) {
		if( enabled && (! isActionAborted()) ) {
			mode.setRenderTarget( renderTarget );
			if( ! ev.isPopupTrigger() ) 
				mode.released(null );
		}
		super.mouseReleased( ev );
	}

	public void mouseDragged( java.awt.event.MouseEvent ev ) {
		if( enabled && (! isActionAborted()) ) {
			super.mouseDragged( ev );
			if( mouseIsDown ) {
				dragged( ev.getComponent(), ev.getPoint(), ev.isControlDown(), ev.isShiftDown() );
			}
		}
	}
	
	public void dragged(Component comp, Point p, boolean isControlDown, boolean isShiftDown) {
		System.out.println(dx + " " + dy);
		super.dragged(comp, p, isControlDown, isShiftDown);
		mode.setRenderTarget( renderTarget );
		mode.dragged( dx, dy, isControlDown, isShiftDown );
	}
}
