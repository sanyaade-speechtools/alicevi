/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 * 
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */

package edu.cmu.cs.stage3.alice.authoringtool;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * @author Jason Pratt
 */
public class Actions {
	public AbstractAction newWorldAction;
	public AbstractAction openWorldAction;
	public AbstractAction openExampleWorldAction;
	public AbstractAction saveWorldAction;
	public AbstractAction saveWorldAsAction;
	public AbstractAction saveForWebAction;
	public AbstractAction importObjectAction;
	public AbstractAction quitAction;
	public AbstractAction cutAction;
	public AbstractAction copyAction;
	public AbstractAction pasteAction;
	public AbstractAction undoAction;
	public AbstractAction redoAction;
	public AbstractAction aboutAction;
	public AbstractAction playAction;
	public AbstractAction addCharacterAction;
	public AbstractAction add3DTextAction;
	public AbstractAction exportMovieAction;
	public AbstractAction trashAction;
	public AbstractAction helpAction;
	public AbstractAction onScreenHelpAction;
	public AbstractAction preferencesAction;
	public AbstractAction makeBillboardAction;
	public AbstractAction showWorldInfoAction;
	public AbstractAction launchTutorialAction;
	public AbstractAction launchTutorialFileAction;
	public AbstractAction showStdOutDialogAction;
	public AbstractAction showStdErrDialogAction;
	public AbstractAction showPrintDialogAction;
	public AbstractAction pauseWorldAction;
	public AbstractAction resumeWorldAction;
	public AbstractAction restartWorldAction;
	public AbstractAction stopWorldAction;
	public AbstractAction takePictureAction;
	public AbstractAction restartStopWorldAction;
	
	protected AuthoringTool authoringTool;
	protected JAliceFrame jAliceFrame;
	protected LinkedList applicationActions = new LinkedList();
	public LinkedList renderActions = new LinkedList();

	public Actions( AuthoringTool authoringTool, JAliceFrame jAliceFrame ) {
		this.authoringTool = authoringTool;
		this.jAliceFrame = jAliceFrame;
		actionInit();
		keyInit();
		undoAction.setEnabled( false );
		redoAction.setEnabled( false );
	}

	private void actionInit() {
		newWorldAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.newWorld();
			}
		};

		openWorldAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.openWorld();
			}
		};

		openExampleWorldAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.openExampleWorld();
			}
		};

		saveWorldAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.saveWorld();
			}
		};

		saveWorldAsAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.saveWorldAs();
			}
		};

		saveForWebAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.saveForWeb();
			}
		};

		importObjectAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.getImportFileChooser().setFileFilter( authoringTool.getImportFileChooser().getAcceptAllFileFilter() );
				authoringTool.importElement();
			}
		};

		quitAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.quit();
			}
		};

		cutAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				//TODO
			}
		};

		copyAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				//TODO
			}
		};

		pasteAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				//TODO
			}
		};

		undoAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.getUndoRedoStack().undo();
			}
		};

		redoAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.getUndoRedoStack().redo();
			}
		};

		aboutAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.showAbout();
			}
		};

		playAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.play();
			}
		};

		addCharacterAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.loadAndAddCharacter();
			}
		};

		add3DTextAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.add3DText();
			}
		};

		exportMovieAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.exportMovie();
			}
		};
		
		trashAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				//TODO
			}
		};

		helpAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
			}
		};

		onScreenHelpAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.showOnScreenHelp();
			}
		};

		preferencesAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.showPreferences();
			}
		};

		makeBillboardAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.makeBillboard();
			}
		};

		showWorldInfoAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.showWorldInfoDialog();
			}
		};

		launchTutorialAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.launchTutorial();
			}
		};

		launchTutorialFileAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.openTutorialWorld();
			}
		};

		showStdOutDialogAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.showStdErrOutDialog();
			}
		};

		showStdErrDialogAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.showStdErrOutDialog();
			}
		};

		showPrintDialogAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.showPrintDialog();
			}
		};

		pauseWorldAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.pause();
			}
		};

		resumeWorldAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.resume();
			}
		};

		restartWorldAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				authoringTool.restartWorld();
			}
		};

		restartStopWorldAction = new AbstractAction(){
			public void actionPerformed( ActionEvent e ) {
				authoringTool.restartWorld();
				authoringTool.pause();
			}
		
		};
		
		stopWorldAction = new AbstractAction() {
			public void actionPerformed( final ActionEvent e ) {
				authoringTool.stopWorld();
			}
		};

		takePictureAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				takePictureAction.setEnabled(false);
				authoringTool.takePicture();
				takePictureAction.setEnabled(true);
			}
		};
		
		

		newWorldAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, java.awt.Event.CTRL_MASK) );
		newWorldAction.putValue( Action.ACTION_COMMAND_KEY, "newWorld" );
		newWorldAction.putValue( Action.MNEMONIC_KEY, new Integer( 'N' ) );
		newWorldAction.putValue( Action.NAME, "New World" );
		newWorldAction.putValue( Action.SHORT_DESCRIPTION, "Create a new world" );
		newWorldAction.putValue( Action.SMALL_ICON, AuthoringToolResources.getIconForString( "new" ) );
		applicationActions.add( newWorldAction );

		openWorldAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, java.awt.Event.CTRL_MASK) );
		openWorldAction.putValue( Action.ACTION_COMMAND_KEY, "openWorld" );
		openWorldAction.putValue( Action.MNEMONIC_KEY, new Integer( 'O' ) );
		openWorldAction.putValue( Action.NAME, "Open World..." );
		openWorldAction.putValue( Action.SHORT_DESCRIPTION, "Open an existing world" );
		openWorldAction.putValue( Action.SMALL_ICON, AuthoringToolResources.getIconForString( "open" ) );
		applicationActions.add( openWorldAction );

		saveWorldAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, java.awt.Event.CTRL_MASK) );
		saveWorldAction.putValue( Action.ACTION_COMMAND_KEY, "saveWorld" );
		saveWorldAction.putValue( Action.MNEMONIC_KEY, new Integer( 'S' ) );
		saveWorldAction.putValue( Action.NAME, "Save World" );
		saveWorldAction.putValue( Action.SHORT_DESCRIPTION, "Save the current world" );
		saveWorldAction.putValue( Action.SMALL_ICON, AuthoringToolResources.getIconForString( "save" ) );
		applicationActions.add( saveWorldAction );

		//saveWorldAsAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, java.awt.Event.CTRL_MASK) );
		saveWorldAsAction.putValue( Action.ACTION_COMMAND_KEY, "saveWorldAs" );
		saveWorldAsAction.putValue( Action.MNEMONIC_KEY, new Integer( 'v' ) );
		saveWorldAsAction.putValue( Action.NAME, "Save World As..." );
		saveWorldAsAction.putValue( Action.SHORT_DESCRIPTION, "Save the current world" );
		//saveWorldAsAction.putValue( Action.SMALL_ICON, AuthoringToolResources.getIconForString( "save" ) );
		applicationActions.add( saveWorldAsAction );

		//saveForWebAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, java.awt.Event.CTRL_MASK) );
		saveForWebAction.putValue( Action.ACTION_COMMAND_KEY, "saveForWeb" );
		saveForWebAction.putValue( Action.MNEMONIC_KEY, new Integer( 'w' ) );
		saveForWebAction.putValue( Action.NAME, "Export As A Web Page..." );
		saveForWebAction.putValue( Action.SHORT_DESCRIPTION, "Export as a web page" );
		//saveForWebAction.putValue( Action.SMALL_ICON, AuthoringToolResources.getIconForString( "save" ) );
		applicationActions.add( saveForWebAction );

		//importObjectAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_, java.awt.Event.CTRL_MASK) );
		importObjectAction.putValue( Action.ACTION_COMMAND_KEY, "importObject" );
		importObjectAction.putValue( Action.MNEMONIC_KEY, new Integer( 'I' ) );
		importObjectAction.putValue( Action.NAME, "Import..." );
		importObjectAction.putValue( Action.SHORT_DESCRIPTION, "Import" );
		importObjectAction.putValue( Action.SMALL_ICON, AuthoringToolResources.getIconForString( "import" ) );
		applicationActions.add( importObjectAction );

		//quitAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_, java.awt.Event.CTRL_MASK) );
		quitAction.putValue( Action.ACTION_COMMAND_KEY, "quit" );
		quitAction.putValue( Action.MNEMONIC_KEY, new Integer( 'x' ) );
		quitAction.putValue( Action.NAME, "Exit" );
		quitAction.putValue( Action.SHORT_DESCRIPTION, "Exit Alice" );
		//quitAction.putValue( Action.SMALL_ICON,  );
		applicationActions.add( quitAction );

		cutAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, java.awt.Event.CTRL_MASK) );
		cutAction.putValue( Action.ACTION_COMMAND_KEY, "cut" );
		cutAction.putValue( Action.MNEMONIC_KEY, new Integer( 't' ) );
		cutAction.putValue( Action.NAME, "Cut" );
		cutAction.putValue( Action.SHORT_DESCRIPTION, "Cut" );
		cutAction.putValue( Action.SMALL_ICON, AuthoringToolResources.getIconForString( "cut" ) );
		applicationActions.add( cutAction );

		copyAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, java.awt.Event.CTRL_MASK) );
		copyAction.putValue( Action.ACTION_COMMAND_KEY, "copy" );
		copyAction.putValue( Action.MNEMONIC_KEY, new Integer( 'C' ) );
		copyAction.putValue( Action.NAME, "Copy" );
		copyAction.putValue( Action.SHORT_DESCRIPTION, "Copy" );
		copyAction.putValue( Action.SMALL_ICON, AuthoringToolResources.getIconForString( "copy" ) );
		applicationActions.add( copyAction );

		pasteAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, java.awt.Event.CTRL_MASK) );
		pasteAction.putValue( Action.ACTION_COMMAND_KEY, "paste" );
		pasteAction.putValue( Action.MNEMONIC_KEY, new Integer( 'P' ) );
		pasteAction.putValue( Action.NAME, "Paste" );
		pasteAction.putValue( Action.SHORT_DESCRIPTION, "Paste" );
		pasteAction.putValue( Action.SMALL_ICON, AuthoringToolResources.getIconForString( "paste" ) );
		applicationActions.add( pasteAction );

		undoAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, java.awt.Event.CTRL_MASK) );
		undoAction.putValue( Action.ACTION_COMMAND_KEY, "undo" );
		undoAction.putValue( Action.MNEMONIC_KEY, new Integer( 'U' ) );
		undoAction.putValue( Action.NAME, "Undo" );
		undoAction.putValue( Action.SHORT_DESCRIPTION, "<html><font face=arial size=-1>Undo the Last Action</font></html>" );
		undoAction.putValue( Action.SMALL_ICON, AuthoringToolResources.getIconForString( "undo" ) );
		applicationActions.add( undoAction );

		redoAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Y, java.awt.Event.CTRL_MASK) );
		redoAction.putValue( Action.ACTION_COMMAND_KEY, "redo" );
		redoAction.putValue( Action.MNEMONIC_KEY, new Integer( 'R' ) );
		redoAction.putValue( Action.NAME, "Redo" );
		redoAction.putValue( Action.SHORT_DESCRIPTION, "Redo" );
		redoAction.putValue( Action.SMALL_ICON, AuthoringToolResources.getIconForString( "redo" ) );
		applicationActions.add( redoAction );

		//aboutAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_, java.awt.Event.CTRL_MASK) );
		aboutAction.putValue( Action.ACTION_COMMAND_KEY, "about" );
		aboutAction.putValue( Action.MNEMONIC_KEY, new Integer( 'A' ) );
		aboutAction.putValue( Action.NAME, "About Alice" );
		aboutAction.putValue( Action.SHORT_DESCRIPTION, "About Alice" );
		aboutAction.putValue( Action.SMALL_ICON, AuthoringToolResources.getIconForString( "about" ) );
		applicationActions.add( aboutAction );

		//onScreenHelpAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_, java.awt.Event.CTRL_MASK) );
		onScreenHelpAction.putValue( Action.ACTION_COMMAND_KEY, "onScreenHelp" );
		onScreenHelpAction.putValue( Action.MNEMONIC_KEY, new Integer( 'O' ) );
		onScreenHelpAction.putValue( Action.NAME, "On-Screen Help (experimental)" );
		onScreenHelpAction.putValue( Action.SHORT_DESCRIPTION, "Experimental Tutorial Editor" );
		//onScreenHelpAction.putValue( Action.SMALL_ICON, AuthoringToolResources.getIconForString( "onScreenHelp" ) );
		applicationActions.add( onScreenHelpAction );

		playAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F5, java.awt.Event.CTRL_MASK) );
		playAction.putValue( Action.ACTION_COMMAND_KEY, "play" );
		//playAction.putValue( Action.MNEMONIC_KEY, new Integer( 'P' ) );
		playAction.putValue( Action.NAME, "Play" );
		playAction.putValue( Action.SHORT_DESCRIPTION, "<html><font face=arial size=-1>Play the world.<p><p>Opens the play window and<p>starts the world running.</font></html>" );
		playAction.putValue( Action.SMALL_ICON, AuthoringToolResources.getIconForString( "play" ) );
		applicationActions.add( playAction );

		//addCharacterAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_, java.awt.Event.CTRL_MASK) );
		addCharacterAction.putValue( Action.ACTION_COMMAND_KEY, "addObject" );
		//addCharacterAction.putValue( Action.MNEMONIC_KEY, new Integer( 'A' ) );
		addCharacterAction.putValue( Action.NAME, "Add Object..." );
		addCharacterAction.putValue( Action.SHORT_DESCRIPTION, "Add a previously stored Object" );
		//addCharacterAction.putValue( Action.SMALL_ICON,  );
		applicationActions.add( addCharacterAction );

		//add3DTextAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_, java.awt.Event.CTRL_MASK) );
		add3DTextAction.putValue( Action.ACTION_COMMAND_KEY, "add3DText" );
		//add3DTextAction.putValue( Action.MNEMONIC_KEY, new Integer( 'A' ) );
		add3DTextAction.putValue( Action.NAME, "Add 3D Text..." );
		add3DTextAction.putValue( Action.SHORT_DESCRIPTION, "Add Text extruded into 3D" );
		//add3DTextAction.putValue( Action.SMALL_ICON,  );
		applicationActions.add( add3DTextAction );
		
	    exportMovieAction.putValue( Action.ACTION_COMMAND_KEY, "exportVideo" );
		exportMovieAction.putValue( Action.NAME, "Export Video..." );
		exportMovieAction.putValue( Action.SHORT_DESCRIPTION, "Export the current world as a video" );
	  	applicationActions.add( exportMovieAction );

		//trashAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_, java.awt.Event.CTRL_MASK) );
		trashAction.putValue( Action.ACTION_COMMAND_KEY, "trash" );
		//trashAction.putValue( Action.MNEMONIC_KEY, new Integer( 'T' ) );
		//trashAction.putValue( Action.NAME, "Trash" );
		trashAction.putValue( Action.SHORT_DESCRIPTION, "<html><font face=arial size=-1>Trash<p><p>Drag and drop tiles here to delete them.</font></html>" );
		//trashAction.putValue( Action.SMALL_ICON,   );
		applicationActions.add( trashAction );

		//openExampleWorldAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_, java.awt.Event.CTRL_MASK) );
		openExampleWorldAction.putValue( Action.ACTION_COMMAND_KEY, "openExampleWorld" );
		//openExampleWorldAction.putValue( Action.MNEMONIC_KEY, new Integer( 'T' ) );
		openExampleWorldAction.putValue( Action.NAME, "Example Worlds" );
		openExampleWorldAction.putValue( Action.SHORT_DESCRIPTION, "Open an Example World" );
		//openExampleWorldAction.putValue( Action.SMALL_ICON,  );
		applicationActions.add( openExampleWorldAction );

		helpAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F1, 0 ) );
		helpAction.putValue( Action.ACTION_COMMAND_KEY, "help" );
		helpAction.putValue( Action.MNEMONIC_KEY, new Integer( 'H' ) );
		helpAction.putValue( Action.NAME, "Help Topics" );
		helpAction.putValue( Action.SHORT_DESCRIPTION, "Alice Documentation" );
		//helpAction.putValue( Action.SMALL_ICON,  );
		applicationActions.add( helpAction );

		//preferencesAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F1, 0 ) );
		preferencesAction.putValue( Action.ACTION_COMMAND_KEY, "preferences" );
		preferencesAction.putValue( Action.MNEMONIC_KEY, new Integer( 'P' ) );
		preferencesAction.putValue( Action.NAME, "Preferences" );
		preferencesAction.putValue( Action.SHORT_DESCRIPTION, "Set Preferences" );
		//preferencesAction.putValue( Action.SMALL_ICON, );
		applicationActions.add( preferencesAction );

		//makeBillboardAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F1, 0 ) );
		makeBillboardAction.putValue( Action.ACTION_COMMAND_KEY, "makeBillboard" );
		makeBillboardAction.putValue( Action.MNEMONIC_KEY, new Integer( 'B' ) );
		makeBillboardAction.putValue( Action.NAME, "Make Billboard..." );
		makeBillboardAction.putValue( Action.SHORT_DESCRIPTION, "Make a billboard object from an image" );
		//makeBillboardAction.putValue( Action.SMALL_ICON, );
		applicationActions.add( makeBillboardAction );

		//showWorldInfoAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F1, 0 ) );
		showWorldInfoAction.putValue( Action.ACTION_COMMAND_KEY, "showWorldInfo" );
		showWorldInfoAction.putValue( Action.MNEMONIC_KEY, new Integer( 'I' ) );
		showWorldInfoAction.putValue( Action.NAME, "World Statistics" );
		showWorldInfoAction.putValue( Action.SHORT_DESCRIPTION, "Show information about the current world" );
		//showWorldInfoAction.putValue( Action.SMALL_ICON, );
		applicationActions.add( showWorldInfoAction );

		//launchTutorialAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F1, 0 ) );
		launchTutorialAction.putValue( Action.ACTION_COMMAND_KEY, "launchTutorial" );
		launchTutorialAction.putValue( Action.MNEMONIC_KEY, new Integer( 'T' ) );
		launchTutorialAction.putValue( Action.NAME, "Teach Me" );
		launchTutorialAction.putValue( Action.SHORT_DESCRIPTION, "Launch the Tutorial" );
		//launchTutorialAction.putValue( Action.SMALL_ICON, );
		applicationActions.add( launchTutorialAction );

		//launchTutorialFileAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F1, 0 ) );
		launchTutorialFileAction.putValue( Action.ACTION_COMMAND_KEY, "launchTutorialFile" );
		launchTutorialFileAction.putValue( Action.MNEMONIC_KEY, new Integer( 'T' ) );
		launchTutorialFileAction.putValue( Action.NAME, "Tutorial" );
		launchTutorialFileAction.putValue( Action.SHORT_DESCRIPTION, "Open a tutorial" );
		//launchTutorialFileAction.putValue( Action.SMALL_ICON, );
		applicationActions.add( launchTutorialFileAction );

		//showStdOutDialogAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F1, 0 ) );
		showStdOutDialogAction.putValue( Action.ACTION_COMMAND_KEY, "showStdOutDialog" );
		showStdOutDialogAction.putValue( Action.MNEMONIC_KEY, new Integer( 'O' ) );
		showStdOutDialogAction.putValue( Action.NAME, "Text Output" );
		showStdOutDialogAction.putValue( Action.SHORT_DESCRIPTION, "Show text output window" );
		//showStdOutDialogAction.putValue( Action.SMALL_ICON, );
		applicationActions.add( showStdOutDialogAction );

		//showStdErrDialogAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F1, 0 ) );
		showStdErrDialogAction.putValue( Action.ACTION_COMMAND_KEY, "showStdErrDialog" );
		showStdErrDialogAction.putValue( Action.MNEMONIC_KEY, new Integer( 'E' ) );
		showStdErrDialogAction.putValue( Action.NAME, "Error Console" );
		showStdErrDialogAction.putValue( Action.SHORT_DESCRIPTION, "Show error console window" );
		//showStdErrDialogAction.putValue( Action.SMALL_ICON, );
		applicationActions.add( showStdErrDialogAction );

		//showPrintDialogAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F1, 0 ) );
		showPrintDialogAction.putValue( Action.ACTION_COMMAND_KEY, "showPrintDialog" );
		showPrintDialogAction.putValue( Action.MNEMONIC_KEY, new Integer( 'P' ) );
		showPrintDialogAction.putValue( Action.NAME, "Export Code For Printing..." );
		showPrintDialogAction.putValue( Action.SHORT_DESCRIPTION, "Export user-defined methods and questions for printing" );
		//showPrintDialogAction.putValue( Action.SMALL_ICON, );
		applicationActions.add( showPrintDialogAction );

		pauseWorldAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_PAUSE, 0 ) );
		pauseWorldAction.putValue( Action.ACTION_COMMAND_KEY, "pauseWorld" );
//		pauseWorldAction.putValue( Action.MNEMONIC_KEY, new Integer( 'P' ) );
		pauseWorldAction.putValue( Action.NAME, "Pause" );
		pauseWorldAction.putValue( Action.SHORT_DESCRIPTION, "Pause the running of the world (Pause/Break)" );
		//pauseWorldAction.putValue( Action.SMALL_ICON, );
		renderActions.add( pauseWorldAction );

		resumeWorldAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_PAGE_UP, 0 ) );
		resumeWorldAction.putValue( Action.ACTION_COMMAND_KEY, "resumeWorld" );
//		resumeWorldAction.putValue( Action.MNEMONIC_KEY, new Integer( 'R' ) );
		resumeWorldAction.putValue( Action.NAME, "Resume" );
		resumeWorldAction.putValue( Action.SHORT_DESCRIPTION, "Resume the running of the world  (Page Up)" );
		//resumeWorldAction.putValue( Action.SMALL_ICON, );
		renderActions.add( resumeWorldAction );

		restartWorldAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_BACK_SPACE, 0 ) );
		restartWorldAction.putValue( Action.ACTION_COMMAND_KEY, "restartWorld" );
//		restartWorldAction.putValue( Action.MNEMONIC_KEY, new Integer( 'T' ) );
		restartWorldAction.putValue( Action.NAME, "Restart" );
		restartWorldAction.putValue( Action.SHORT_DESCRIPTION, "Restart the world (Backspace)" );
		//restartWorldAction.putValue( Action.SMALL_ICON, );
		renderActions.add( restartWorldAction );

		restartStopWorldAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_BACK_SPACE, 0 ) );
		restartStopWorldAction.putValue( Action.ACTION_COMMAND_KEY, "restartWorld" );
//		restartWorldAction.putValue( Action.MNEMONIC_KEY, new Integer( 'T' ) );
		restartStopWorldAction.putValue( Action.NAME, "Restart" );
		restartStopWorldAction.putValue( Action.SHORT_DESCRIPTION, "Restart the world (Backspace)" );
		//restartWorldAction.putValue( Action.SMALL_ICON, );
		renderActions.add( restartStopWorldAction );

		
		stopWorldAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ) );
		stopWorldAction.putValue( Action.ACTION_COMMAND_KEY, "stopWorld" );
//		stopWorldAction.putValue( Action.MNEMONIC_KEY, new Integer( 'S' ) );
		stopWorldAction.putValue( Action.NAME, "Stop" );
		stopWorldAction.putValue( Action.SHORT_DESCRIPTION, "Stop the running of the world (Esc)" );
		//stopWorldAction.putValue( Action.SMALL_ICON, );
		renderActions.add( stopWorldAction );

		takePictureAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_G, KeyEvent.CTRL_MASK ) );
		takePictureAction.putValue( Action.ACTION_COMMAND_KEY, "takePicture" );
//		takePictureAction.putValue( Action.MNEMONIC_KEY, new Integer( 'c' ) );
		takePictureAction.putValue( Action.NAME, "Take Picture" );
		takePictureAction.putValue( Action.SHORT_DESCRIPTION, "Take a screenshot of the current scene (Ctrl-G)" );
		//takePictureAction.putValue( Action.SMALL_ICON, );
		renderActions.add( takePictureAction );
		
	}

	private void keyInit() {
		KeyStroke keyStroke;
		String commandKey;

		for( Iterator iter = applicationActions.iterator(); iter.hasNext(); ) {
			Action action = (Action)iter.next();

			try {
				keyStroke = (KeyStroke)action.getValue( Action.ACCELERATOR_KEY );
				commandKey = (String)action.getValue( Action.ACTION_COMMAND_KEY );
			} catch( ClassCastException e ) {
				continue;
			}

			if( (keyStroke != null) && (commandKey != null) ) {
				jAliceFrame.registerKeyboardAction( action, commandKey, keyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW );
				// below is the new way of doing things, but it doesn't seem to work...
				//applicationPanel.getInputMap().put( keyStroke, commandKey );
				//applicationPanel.getActionMap().put( commandKey, action );
			}
		}
	}
}