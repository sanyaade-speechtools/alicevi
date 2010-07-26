package ui;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import edu.cmu.cs.stage3.alice.authoringtool.util.EditObjectButton;

public aspect AccessibleButton {
	public static final int BORDER_THICKNESS = 4;
	
	public final static Color FOCUSED_BORDER_COLOR = new Color(0.2f, 0.2f, 0.2f);
	public final static Color FOCUSED_BG_COLOR = new Color(1f, 1f, 1f);
	public final static Color FOCUSED_FG_COLOR = new Color(0f, 0f, 0f);
	
	public final static Color UNFOCUSED_BG_COLOR = FOCUSED_FG_COLOR;
	public final static Color UNFOCUSED_FG_COLOR = FOCUSED_BG_COLOR;
	public final static Color UNFOCUSED_BORDER_COLOR = UNFOCUSED_BG_COLOR;
	
	private FocusListener listener = new FocusListener() {
		@Override
		public void focusGained(FocusEvent fe) {
			JButton button = (JButton) fe.getSource();
			button.setBackground(UNFOCUSED_BG_COLOR);
			button.setForeground(UNFOCUSED_FG_COLOR);
			button.setBorder(BorderFactory.createLineBorder(UNFOCUSED_BORDER_COLOR, BORDER_THICKNESS));
		}

		@Override
		public void focusLost(FocusEvent fe) {
			JButton button = (JButton) fe.getSource();
			button.setBackground(FOCUSED_BG_COLOR);
			button.setForeground(FOCUSED_FG_COLOR);
			button.setBorder(BorderFactory.createLineBorder(FOCUSED_BORDER_COLOR, BORDER_THICKNESS));
		}
	};
	
	private void init(JButton button) {
		button.setMargin(new Insets(2,2,2,2));
		button.addFocusListener(listener);
		button.setBackground(UNFOCUSED_BG_COLOR);
		button.setForeground(UNFOCUSED_FG_COLOR);
	}
	
	after() returning(JButton button): call(*.new(..)) || call(* newInstance(..)) {
		init(button);
	}
}
