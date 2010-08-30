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
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class ScreenWrappingMouseListener implements MouseListener, MouseMotionListener, KeyListener {
	protected int pressedx = 0;
	protected int pressedy = 0;
	protected int lastx = 0;
	protected int lasty = 0;
	protected int offsetx = 0;
	protected int offsety = 0;
	protected int dx = 0;
	protected int dy = 0;
	protected boolean mouseIsDown = false;
	private int leftEdge;
	private int rightEdge;
	private int topEdge;
	private int bottomEdge;
	protected boolean doWrap = false;
	private java.awt.Point tempPoint = new java.awt.Point();
	private boolean actionAborted = false;
	protected java.awt.Component component;

	synchronized public boolean isMouseDown() {
		return mouseIsDown;
	}

	synchronized public int getPressedX() {
		return pressedx;
	}

	synchronized public int getPressedY() {
		return pressedy;
	}

	synchronized public int getOffsetX() {
		return offsetx;
	}

	synchronized public int getOffsetY() {
		return offsety;
	}

	synchronized public int getDX() {
		return dx;
	}

	synchronized public int getDY() {
		return dy;
	}

	public boolean isActionAborted() {
		return actionAborted;
	}

	synchronized public void abortAction() {
		actionAborted = true;
		mouseIsDown = false;
		component.removeMouseMotionListener( this );
	}

	synchronized public void mousePressed( java.awt.event.MouseEvent ev ) {
		selected(ev.getComponent(), ev.getPoint());
		mouseIsDown = true;
		component.addMouseMotionListener( this );
	}
	
	public void selected(Component comp, Point p)
	{
		component = comp;
		initForDrag(p.x, p.y);
	}

	private void initForDrag(int startingX, int startingY) {
		if( edu.cmu.cs.stage3.awt.AWTUtilities.isSetCursorLocationSupported() ) {
			doWrap = true;
		} else {
			doWrap = false;
		}

		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int)screenSize.getWidth();
		int screenHeight = (int)screenSize.getHeight();
		leftEdge = 0;
		rightEdge = screenWidth - 1;
		topEdge = 0;
		bottomEdge = screenHeight - 1;

		pressedx = lastx = startingX;
		pressedy = lasty = startingY;
		offsetx = 0;
		offsety = 0;
	}

	synchronized public void mouseReleased( java.awt.event.MouseEvent ev ) {
		if( ! actionAborted ) {
			mouseIsDown = false;
			ev.getComponent().removeMouseMotionListener( this );
		} else {
			actionAborted = false;
		}
	}
	
	synchronized public void released(Component c, Point p) {
		
	}

	synchronized public void mouseDragged( java.awt.event.MouseEvent ev ) {
		if(mouseIsDown)
			dragged(ev.getComponent(), new Point(ev.getX(), ev.getY()), false, false);
	}
	
	public void dragged(Component comp, Point p, boolean isControlDown, boolean isShiftDown) {
		moveTo(comp, p);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			selected(e.getComponent(), new Point(0,0));
			initForDrag(0, 0);
			return;
		}
		int dx, dy = 0;
		switch(e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			dx = -10;
			dy = 0;
			break;
		case KeyEvent.VK_RIGHT:
			dx = 10;
			dy = 0;
			break;
		case KeyEvent.VK_UP:
			dx = 0;
			dy = -10;
			break;
		case KeyEvent.VK_DOWN:
			dx = 0;
			dy = 10;
			break;
		default:
			dx = dy = 0;
		}
		moveDelta(e.getComponent(), dx, dy);
	}
	
	public void moveDelta(Component  c, int howMuchX, int howMuchY) {
		moveTo(c, new Point(lastx + howMuchX, lasty + howMuchY));
	}

	public void moveTo(Component c, Point p) {
		offsetx = p.x - pressedx;
		offsety = p.y - pressedy;

		dx = p.x - lastx;
		dy = p.y - lasty;

		lastx = p.x;
		lasty = p.y;

		if( !doWrap ) return;
		
		tempPoint.setLocation( p.x, p.y );
		javax.swing.SwingUtilities.convertPointToScreen( tempPoint, c );
		moveX();
		moveY();
		System.out.println(tempPoint);
	}

	private void moveY() {
		if( tempPoint.y <= topEdge ) {
			tempPoint.y = (bottomEdge - 1) - (topEdge - tempPoint.y);
			lasty += bottomEdge - topEdge;
			pressedy += bottomEdge - topEdge;
			edu.cmu.cs.stage3.awt.AWTUtilities.setCursorLocation( tempPoint );
		} else if ( tempPoint.y >= bottomEdge ) {
			tempPoint.y = (topEdge + 1) + (tempPoint.y - bottomEdge);
			lasty -= bottomEdge - topEdge;
			pressedy -= bottomEdge - topEdge;
			edu.cmu.cs.stage3.awt.AWTUtilities.setCursorLocation( tempPoint );
		}
	}

	private void moveX() {
		if( tempPoint.x <= leftEdge ) {
			tempPoint.x = (rightEdge - 1) - (leftEdge - tempPoint.x);
			lastx += rightEdge - leftEdge;
			pressedx += rightEdge - leftEdge;
			edu.cmu.cs.stage3.awt.AWTUtilities.setCursorLocation( tempPoint );
		} else if ( tempPoint.x >= rightEdge ) {
			tempPoint.x = (leftEdge + 1) + (tempPoint.x - rightEdge);
			lastx -= rightEdge - leftEdge;
			pressedx -= rightEdge - leftEdge;
			edu.cmu.cs.stage3.awt.AWTUtilities.setCursorLocation( tempPoint );
		}
	}
	
	public void mouseClicked( java.awt.event.MouseEvent ev ) {}
	public void mouseEntered( java.awt.event.MouseEvent ev ) {}
	public void mouseExited( java.awt.event.MouseEvent ev ) {}
	public void mouseMoved( java.awt.event.MouseEvent ev ) {}

	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}
}