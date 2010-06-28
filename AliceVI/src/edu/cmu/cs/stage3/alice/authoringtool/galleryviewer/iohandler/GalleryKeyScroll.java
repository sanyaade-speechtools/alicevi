package edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.iohandler;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject;
import edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryViewer;

public class GalleryKeyScroll extends KeyAdapter {

	private GalleryViewer galleryViewer;
	private GalleryObject galleryObject;
	
	public GalleryKeyScroll(GalleryViewer gv, GalleryObject go) {
		galleryViewer = gv;
		galleryObject = go;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			galleryViewer.moveFocusLeft(galleryObject);
		} 
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			galleryViewer.moveFocusRight(galleryObject);
		} 
		else if(e.getKeyCode() == KeyEvent.VK_UP) {
			galleryViewer.goUpOneLevel();
		} 
		else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			galleryObject.respondToMouse();
		}
	}
}
