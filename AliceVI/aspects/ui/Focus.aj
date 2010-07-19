package ui;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.FileNotFoundException;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

public aspect Focus {
	
	private class VisualFocusListener implements FocusListener {
		private Border prevBorder;
		private JComponent jc;
		
		public VisualFocusListener(JComponent jc) {
			this.jc = jc;
			this.prevBorder = null;
		}

		public void focusGained(FocusEvent fe) {
			if(fe.getSource() == jc && prevBorder == null) {
				JComponent src = ((JComponent) fe.getSource());
				prevBorder = src.getBorder();
				src.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
				
			}
		}

		public void focusLost(FocusEvent fe) {
			if(fe.getSource() instanceof JComponent) {
				JComponent src = ((JComponent) fe.getSource());
				src.setBorder(null);
				src.setBorder(prevBorder);
				prevBorder = null;
			}
		}
	}

	after() returning(JComponent j): call(*.new(..)) {
		j.addFocusListener(new VisualFocusListener(j));
	}
}
