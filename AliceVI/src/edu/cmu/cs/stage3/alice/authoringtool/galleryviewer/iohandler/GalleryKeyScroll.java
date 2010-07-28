package edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.iohandler;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject;

public class GalleryKeyScroll implements MouseListener, KeyListener, FocusListener {

	private GalleryObject galleryObject;
	
	public GalleryKeyScroll(GalleryObject go) {
		galleryObject = go;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			galleryObject.getMainViewer().moveFocusLeft(galleryObject);
		} 
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			galleryObject.getMainViewer().moveFocusRight(galleryObject);
		} 
		else if(e.getKeyCode() == KeyEvent.VK_UP) {
			galleryObject.getMainViewer().goUpOneLevel();
		} 
		else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			galleryObject.respondToMouse();
		}
		else if(e.getKeyCode() == KeyEvent.VK_TAB) {
			galleryObject.removeHighlight();
		}
	}
	

	@Override
	public void focusGained(FocusEvent evt) {
		galleryObject.highlight();
	}

	@Override
	public void focusLost(FocusEvent evt) {
		galleryObject.removeHighlight();
	}
	
	@Override
	public void mouseExited(MouseEvent m){}

	@Override
    public void mouseEntered(MouseEvent m){
    	galleryObject.highlight();
    	galleryObject.requestFocusInWindow();
    }
	
	@Override
	public void mouseClicked(MouseEvent m)
	{
		galleryObject.respondToMouse();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}
}
