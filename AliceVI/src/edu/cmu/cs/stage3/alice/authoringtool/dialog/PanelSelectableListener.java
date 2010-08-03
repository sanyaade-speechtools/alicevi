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
import javax.swing.JScrollPane;

public class PanelSelectableListener implements MouseListener, KeyListener, FocusListener {

	private JComponent comp;
	private JScrollPane compScroller;
	private boolean scrollOn = true;
	
	public PanelSelectableListener(JComponent c, JScrollPane s) {
		comp = c;
		compScroller = s;
	}
	
	public PanelSelectableListener(JComponent c, JScrollPane s, boolean addListenersToAll)
	{
		this(c, s);
		if(addListenersToAll)
		{
			comp.addMouseListener(this);
			comp.addKeyListener(this);
			comp.addFocusListener(this);
		}
	}
	
	public void setScrollPane(JScrollPane jsp) {
		compScroller = jsp;
	}
	
	public JScrollPane getScrollPane() {
		return compScroller;
	}
	
	public void selected() {
		if(comp instanceof Selectable) ((Selectable) comp).select();
	}
	
	private void scrollTo() {
		if(scrollOn && compScroller != null)
		{
			compScroller.getVerticalScrollBar().setValue(comp.getY());
			compScroller.getHorizontalScrollBar().setValue(comp.getX());
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		selected();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		scrollOn = false;
		comp.requestFocusInWindow();
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		scrollOn = true;
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() == KeyEvent.VK_ENTER)
			selected();
	}

	@Override
	public void focusGained(FocusEvent e) {
		if(comp instanceof Selectable) ((Selectable) comp).highlight();
		scrollTo();
	}

	@Override
	public void focusLost(FocusEvent e) {
		if(comp instanceof Selectable) ((Selectable) comp).removeHighlight();
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
