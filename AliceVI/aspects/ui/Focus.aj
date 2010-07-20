package ui;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

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
				prevBorder = jc.getBorder();
				jc.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
			}
		}

		public void focusLost(FocusEvent fe) {
			if(fe.getSource() == jc) {
				jc.setBorder(prevBorder);
				prevBorder = null;
			}
		}
	}

	after() returning(JComponent j): call(*.new(..)) {
		j.addFocusListener(new VisualFocusListener(j));
	}
}
