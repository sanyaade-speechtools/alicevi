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
import java.awt.event.KeyEvent;

public abstract class RenderTargetPickManipulator extends ScreenWrappingMouseListener {
	protected edu.cmu.cs.stage3.alice.core.Transformable ePickedTransformable = null;
	protected edu.cmu.cs.stage3.alice.core.Transformable lastEPickedTransformable = null;
	protected edu.cmu.cs.stage3.alice.scenegraph.Transformable sgPickedTransformable = null;
	protected edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget renderTarget = null;
	protected java.util.HashSet objectsOfInterest = new java.util.HashSet();
	protected java.util.HashSet listeners = new java.util.HashSet();
	protected java.awt.Cursor invisibleCursor = java.awt.Toolkit.getDefaultToolkit().createCustomCursor( java.awt.Toolkit.getDefaultToolkit().getImage(""), new java.awt.Point( 0, 0 ), "invisible cursor" );
	protected java.awt.Cursor savedCursor = java.awt.Cursor.getDefaultCursor();
	protected java.awt.Point originalMousePoint;
	protected boolean hideCursorOnDrag = true;
	protected boolean popupEnabled = false;
	protected boolean enabled = true;
	protected boolean pickAllForOneObjectOfInterest = true;
	protected boolean ascendTreeEnabled = true;
	protected edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pickInfo;

	public RenderTargetPickManipulator( edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget renderTarget ) {
		setRenderTarget( renderTarget );
	}

	public void setEnabled( boolean b ) {
		enabled = b;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setPickAllForOneObjectOfInterestEnabled( boolean b ) {
		pickAllForOneObjectOfInterest = b;
	}

	public boolean isPickAllForOneObjectOfInterestEnabled() {
		return pickAllForOneObjectOfInterest;
	}

	public void setRenderTarget( edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget renderTarget ) {
		if( this.renderTarget != null ) {
			this.renderTarget.getAWTComponent().removeMouseListener( this );
			this.renderTarget.getAWTComponent().removeKeyListener(this);
			if( popupEnabled ) {
				this.renderTarget.getAWTComponent().removeMouseListener( popupMouseListener );
			}
		}

		this.renderTarget = renderTarget;
		if( renderTarget != null ) {
			renderTarget.getAWTComponent().addMouseListener( this );
			renderTarget.getAWTComponent().addKeyListener(this);
			if( popupEnabled ) {
				this.renderTarget.getAWTComponent().addMouseListener( popupMouseListener );
			}
		}
	}

	public edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget getRenderTarget() {
		return renderTarget;
	}

	public edu.cmu.cs.stage3.alice.core.Transformable getCorePickedTransformable() {
		return ePickedTransformable;
	}

	public edu.cmu.cs.stage3.alice.scenegraph.Transformable getSceneGraphPickedTransformable() {
		return sgPickedTransformable;
	}

	public void addRenderTargetPickManipulatorListener( edu.cmu.cs.stage3.alice.authoringtool.util.event.RenderTargetPickManipulatorListener listener ) {
		if( listener != null ) {
			listeners.add( listener );
		}
	}

	public void removeRenderTargetPickManipulatorListener( edu.cmu.cs.stage3.alice.authoringtool.util.event.RenderTargetPickManipulatorListener listener ) {
		if( listener != null ) {
			listeners.remove( listener );
		}
	}

	/**
	 * when objectsOfInterest is empty, all objects are of interest.
	 * when objectsOfInterest has one element, it will always be picked.
	 * when objectsOfInterest has more than one element, mouseDragged behavior will only occur if an objectOfInterest is picked
	 * only firstClass objects will be considered
	 */
	public boolean addObjectOfInterest( edu.cmu.cs.stage3.alice.core.Transformable trans ) {
		return objectsOfInterest.add( trans );
	}

	/**
	 * when objectsOfInterest is empty, all objects are of interest.
	 * when objectsOfInterest has one element, it will always be picked.
	 * when objectsOfInterest has more than one element, mouseDragged behavior will only occur if an objectOfInterest is picked
	 * only firstClass objects will be considered
	 */
	public boolean removeObjectOfInterest( edu.cmu.cs.stage3.alice.core.Transformable trans ) {
		return objectsOfInterest.remove( trans );
	}

	public void clearObjectsOfInterestList() {
		objectsOfInterest.clear();
	}

	public boolean getHideCursorOnDrag() {
		return hideCursorOnDrag;
	}

	public void setHideCursorOnDrag( boolean b ) {
		hideCursorOnDrag = b;
	}

	public void setAscendTreeEnabled( boolean b ) {
		ascendTreeEnabled = b;
	}

	public boolean isAscendTreeEnabled() {
		return ascendTreeEnabled;
	}

	public boolean isPopupEnabled() {
		return popupEnabled;
	}

	public synchronized void setPopupEnabled( boolean b ) {
		if( renderTarget != null ) {
			if( b && (! popupEnabled) ) {
				renderTarget.getAWTComponent().addMouseListener( popupMouseListener );
			} else if( (! b) && popupEnabled ) {
				renderTarget.getAWTComponent().removeMouseListener( popupMouseListener );
			}
		}
		popupEnabled = b;
	}

	@Override
	public void mousePressed( java.awt.event.MouseEvent ev ) {
		if( enabled ) {
			super.mousePressed( ev );
		}
	}

	public void selected(Component comp, Point p) {
		super.selected(comp, p);
		firePrePick();
		if( (objectsOfInterest.size() == 1) && pickAllForOneObjectOfInterest ) {
			ePickedTransformable = (edu.cmu.cs.stage3.alice.core.Transformable)objectsOfInterest.iterator().next();
			sgPickedTransformable = ePickedTransformable.getSceneGraphTransformable();
		} else {
			// implementors are responsible for pushing their own undos onto the stack
			if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getInstance() != null ) {
				if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getInstance().getUndoRedoStack() != null ) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getInstance().getUndoRedoStack().setIsListening( false );
				}
			}
			pickInfo = renderTarget.pick( p.x, p.y, edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget.SUB_ELEMENT_IS_NOT_REQUIRED, edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget.ONLY_FRONT_MOST_VISUAL_IS_REQUIRED );

			if( pickInfo != null ) {
				edu.cmu.cs.stage3.alice.scenegraph.Visual[] visuals = pickInfo.getVisuals();
				if( (visuals != null) && (visuals.length >= 1) ) {

					ePickedTransformable = (edu.cmu.cs.stage3.alice.core.Transformable)visuals[0].getBonus();
					if( ePickedTransformable == null ) {
						sgPickedTransformable = (edu.cmu.cs.stage3.alice.scenegraph.Transformable)visuals[0].getParent();
						ePickedTransformable = (edu.cmu.cs.stage3.alice.core.Transformable)sgPickedTransformable.getBonus();
					} else {
						sgPickedTransformable = ePickedTransformable.getSceneGraphTransformable();
					}
					if( ascendTreeEnabled ) {
						while( (ePickedTransformable != null) && (ePickedTransformable.getParent() instanceof edu.cmu.cs.stage3.alice.core.Transformable) && (! ePickedTransformable.doEventsStopAscending()) && (! objectsOfInterest.contains( ePickedTransformable )) ) {
							sgPickedTransformable = ((edu.cmu.cs.stage3.alice.core.Transformable)ePickedTransformable.getParent()).getSceneGraphTransformable();
							ePickedTransformable = (edu.cmu.cs.stage3.alice.core.Transformable)sgPickedTransformable.getBonus();
						}
					}

					if( (! objectsOfInterest.isEmpty()) && (! objectsOfInterest.contains( ePickedTransformable )) ) {
						abortAction();
					}
				} else {
					sgPickedTransformable = null;
					ePickedTransformable = null;
				}
			} else {
				sgPickedTransformable = null;
				ePickedTransformable = null;
			}
		}
		firePostPick( pickInfo );

		originalMousePoint = p;
		if( (! isActionAborted()) && hideCursorOnDrag && doWrap && (! component.getCursor().equals( invisibleCursor )) ) {
			savedCursor = component.getCursor();
			component.setCursor( invisibleCursor );
		}
	}

	public void mouseReleased( java.awt.event.MouseEvent ev ) {
		released(ev.getComponent(), ev.getPoint());
		super.mouseReleased( ev );
	}

	@Override
	synchronized public void released(Component c, Point p) {
		if( (! isActionAborted()) && hideCursorOnDrag && doWrap ) {
			component.setCursor( savedCursor );
			javax.swing.SwingUtilities.convertPointToScreen( p, component );
			javax.swing.SwingUtilities.convertPointToScreen( originalMousePoint, component );
			edu.cmu.cs.stage3.awt.AWTUtilities.setCursorLocation( p.x, originalMousePoint.y );
		}

		lastEPickedTransformable = ePickedTransformable;

		ePickedTransformable = null;
		sgPickedTransformable = null;
		pickInfo = null;
		if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getInstance() != null ) {
			if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getInstance().getUndoRedoStack() != null ) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getInstance().getUndoRedoStack().setIsListening( true );
			}
		}
	}

	public void abortAction() {
		component.setCursor( savedCursor );
		super.abortAction();
	}

	protected void firePrePick() {
		edu.cmu.cs.stage3.alice.authoringtool.util.event.RenderTargetPickManipulatorEvent ev = new edu.cmu.cs.stage3.alice.authoringtool.util.event.RenderTargetPickManipulatorEvent( renderTarget, null );
		for( java.util.Iterator iter = listeners.iterator(); iter.hasNext(); ) {
			((edu.cmu.cs.stage3.alice.authoringtool.util.event.RenderTargetPickManipulatorListener)iter.next()).prePick( ev );
		}
	}

	protected void firePostPick( edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pickInfo ) {
		edu.cmu.cs.stage3.alice.authoringtool.util.event.RenderTargetPickManipulatorEvent ev = new edu.cmu.cs.stage3.alice.authoringtool.util.event.RenderTargetPickManipulatorEvent( renderTarget, pickInfo );
		for( java.util.Iterator iter = listeners.iterator(); iter.hasNext(); ) {
			((edu.cmu.cs.stage3.alice.authoringtool.util.event.RenderTargetPickManipulatorListener)iter.next()).postPick( ev );
		}
	}

	//TODO: this should be handled elsewhere
	protected final java.awt.event.MouseListener popupMouseListener = new edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter() {
		Runnable emptyRunnable = new Runnable() {
			public void run() {}
		};

		protected void popupResponse( java.awt.event.MouseEvent e ) {
			if( lastEPickedTransformable != null ) {
				javax.swing.JPopupMenu popup = createPopup( lastEPickedTransformable );
				popup.show( e.getComponent(), e.getX(), e.getY() );
				PopupMenuUtilities.ensurePopupIsOnScreen( popup );
			}
		}

		private javax.swing.JPopupMenu createPopup( edu.cmu.cs.stage3.alice.core.Element element ) {
			java.util.Vector popupStructure = ElementPopupUtilities.getDefaultStructure( element );
			return edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.makeElementPopupMenu( element, popupStructure );
		}
	};
}