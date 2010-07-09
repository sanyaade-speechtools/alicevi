package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

public class PanelSelectableListener implements MouseListener, KeyListener, FocusListener {

	private JComponent comp;
	
	public PanelSelectableListener(JComponent c) {
		comp = c;
	}
	
	public void selected() {
		if(comp instanceof Selectable)
			((Selectable) comp).select();
	}
	
	private void addCompBorder() {
		comp.setBorder(BorderFactory.createLineBorder(new Color(0.2f, 0.2f, 1f), 4));
	}
	
	private void removeCompBorder() {
		comp.setBorder(null);
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		selected();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		comp.requestFocusInWindow();
		addCompBorder();
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		removeCompBorder();
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() == KeyEvent.VK_ENTER)
			selected();
	}

	@Override
	public void focusGained(FocusEvent e) {
		addCompBorder();
	}

	@Override
	public void focusLost(FocusEvent e) {
		removeCompBorder();
	}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}
}
