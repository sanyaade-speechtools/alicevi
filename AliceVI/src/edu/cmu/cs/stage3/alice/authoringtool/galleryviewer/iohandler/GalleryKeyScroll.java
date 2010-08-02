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
	}
	

	@Override
	public void focusGained(FocusEvent evt) {
	}

	@Override
	public void focusLost(FocusEvent evt) {
	}
	
	@Override
	public void mouseExited(MouseEvent m) {
	}

	@Override
    public void mouseEntered(MouseEvent m){
    }
	
	@Override
	public void mouseClicked(MouseEvent m) {
		System.out.println(galleryObject);
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
