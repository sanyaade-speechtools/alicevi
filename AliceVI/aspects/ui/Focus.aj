package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

public aspect Focus {
	
	private class VisualFocusListener implements FocusListener {
		private final static int BORDER_SIZE = 4;
		
		private Border prevBorder;
		private JComponent jc;
		
		public VisualFocusListener(JComponent jc) {
			this.jc = jc;
			this.prevBorder = null;
		}

		public void focusGained(FocusEvent fe) {
			if(fe.getSource() == jc && prevBorder == null) {
				prevBorder = jc.getBorder();
				jc.setBorder(BorderFactory.createLineBorder(Color.BLACK, BORDER_SIZE));
			}
		}

		public void focusLost(FocusEvent fe) {
			if(fe.getSource() == jc) {
				jc.setBorder(prevBorder);
				prevBorder = null;
			}
		}
	}
	
	private class ScrollListener implements FocusListener {
		private JScrollPane scrollPane;
		
		public ScrollListener(JScrollPane jsp) {
			scrollPane = jsp;
		}
		
		@Override
		public void focusGained(FocusEvent evt) {
			Component src = (Component) evt.getSource();
			scrollPane.getHorizontalScrollBar().setValue(src.getX());
			scrollPane.getVerticalScrollBar().setValue(src.getY());
		}
		
		@Override
		public void focusLost(FocusEvent evt) {}
	}

	after() returning(JComponent j): call(*.new(..)) {
		j.addFocusListener(new VisualFocusListener(j));
	}
	
	after(JPanel j): target(j) && call(* add(..)) {
		if(j.getComponents().length == 1)
			j.addFocusListener(new VisualFocusListener(j));
	}
}
