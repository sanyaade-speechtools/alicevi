package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.JComponent;

public class PanelSwitcher extends KeyAdapter {
	
	private static PanelSwitcher instance = null;
	
	private HashMap<Character, JComponent> actionMap = new HashMap<Character, JComponent>();
	
	private PanelSwitcher() {}
	
	public static PanelSwitcher getInstance() {
		if(instance == null) instance = new PanelSwitcher();
		return instance;
	}
	
	public boolean isActionKeyAvailable(Character actionKey) {
		return !actionMap.containsKey(actionKey);
	}
	
	public static JComponent register(Character actionKey, JComponent comp) {
		if(getInstance().actionMap.containsKey(actionKey)) return null;
		else {
			comp.setFocusable(true);
			comp.addKeyListener(getInstance());
			return getInstance().actionMap.put(actionKey, comp);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if((e.getModifiers() & KeyEvent.ALT_MASK) == KeyEvent.ALT_MASK) {
			if(actionMap.containsKey(e.getKeyChar())) {
				System.out.println(actionMap.get(e.getKeyChar()));
				actionMap.get(e.getKeyChar()).requestFocusInWindow();
			}
		}
	}
}
