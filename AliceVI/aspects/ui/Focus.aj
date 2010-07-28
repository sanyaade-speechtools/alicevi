package ui;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
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

	after() returning(JComponent j): call(*.new(..)) {
		j.addFocusListener(new VisualFocusListener(j));
	}
	
	after(JButton b, String t): target(b) && call(* setText(String)) && args(t) {
		System.out.println("Button text: " + t);
		if(t.equalsIgnoreCase("add objects"))
			System.out.println("\tFound it!");
	}
}
