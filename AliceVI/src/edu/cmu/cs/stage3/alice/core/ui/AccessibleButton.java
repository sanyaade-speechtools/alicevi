package edu.cmu.cs.stage3.alice.core.ui;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;

public class AccessibleButton extends JButton implements FocusListener {

	private static final long serialVersionUID = -8350785150810763387L;

	public static final int BORDER_THICKNESS = 4;
	
	public final static Color FOCUSED_BORDER_COLOR = new Color(0.2f, 0.2f, 1f);
	public final static Color FOCUSED_BG_COLOR = new Color(1f, 1f, 1f);
	public final static Color FOCUSED_FG_COLOR = new Color(0f, 0f, 0f);
	
	public final static Color UNFOCUSED_BG_COLOR = FOCUSED_FG_COLOR;
	public final static Color UNFOCUSED_FG_COLOR = FOCUSED_BG_COLOR;

	public AccessibleButton() {
		init();
	}

	public AccessibleButton(Icon arg0) {
		super(arg0);
		init();
	}

	public AccessibleButton(String arg0) {
		super(arg0);
		init();
	}

	public AccessibleButton(Action arg0) {
		super(arg0);
		init();
	}

	public AccessibleButton(String arg0, Icon arg1) {
		super(arg0, arg1);
		init();
	}
	
	private void init() {
		this.addFocusListener(this);
		this.setBackground(UNFOCUSED_BG_COLOR);
		this.setForeground(UNFOCUSED_FG_COLOR);
	}

	@Override
	public void focusGained(FocusEvent e) {
		this.setBackground(FOCUSED_BG_COLOR);
		this.setForeground(FOCUSED_FG_COLOR);
		this.setBorder(BorderFactory.createLineBorder(FOCUSED_BORDER_COLOR, BORDER_THICKNESS));
	}

	@Override
	public void focusLost(FocusEvent e) {
		this.setBackground(UNFOCUSED_BG_COLOR);
		this.setForeground(UNFOCUSED_FG_COLOR);
		this.setBorder(BorderFactory.createLineBorder(UNFOCUSED_BG_COLOR, BORDER_THICKNESS));
	}

}
