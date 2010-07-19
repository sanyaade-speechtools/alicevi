package ui;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComponent;

public aspect Focus {
	
	private class VisualFocusListener implements FocusListener {
		Color prevColor = Color.BLACK;

		public void focusGained(FocusEvent fe) {
			if(fe.getSource() instanceof JComponent) {
				prevColor = ((JComponent) fe.getSource()).getBackground();
				((JComponent) fe.getSource()).setBackground(Color.WHITE);
				System.out.println(prevColor);
			}
		}

		public void focusLost(FocusEvent fe) {
			if(fe.getSource() instanceof JComponent) {
				((JComponent) fe.getSource()).setBackground(prevColor);
			}
		}
	}

	pointcut init(JComponent j):
		this(j) &&
		initialization(*.new());

	after(JComponent j) returning: init(j) {
		j.addFocusListener(new VisualFocusListener());
	}
}
