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

package edu.cmu.cs.stage3.alice.authoringtool.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.authoringtool.JAlice;
import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent;
import edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener;
import edu.cmu.cs.stage3.alice.core.Decorator;
import edu.cmu.cs.stage3.io.FileUtilities;
import edu.cmu.cs.stage3.swing.ContentPane;
import edu.cmu.cs.stage3.swing.DialogManager;

/**
 * @author Jason Pratt, brought over to the dark side by Dave Culyba
 *
 * This bastardizes the whole configuration situation even further.
 * At some point, some sanity will have to be brought to bear on the matter.
 * Sanity? We're so far beyond the sanity barrier it's crazy.
 */

@SuppressWarnings("serial")
public class PreferencesContentPane extends ContentPane {
	protected HashMap<Object, String> checkBoxToConfigKeyMap = new HashMap<Object, String>();
	protected AuthoringTool authoringTool;
	private Package authoringToolPackage = Package.getPackage( "edu.cmu.cs.stage3.alice.authoringtool" );
	private Package decoratorPackage = Decorator.DECORATOR_PACKAGE;
	private Package colorStatePackage = Package.getPackage("edu.cmu.cs.stage3.alice.scenegraph.colorstate");
	protected JFileChooser browseFileChooser = new JFileChooser();
	protected HashMap<Object, String> rendererStringMap = new HashMap<Object, String>();
	protected boolean restartRequired = false;
	protected boolean reloadRequired = false;
	protected boolean shouldListenToRenderBoundsChanges = true;
	protected boolean changedCaptureDirectory = false;
	protected Frame owner;
	private Vector<ActionListener> m_okActionListeners = new Vector<ActionListener>();
	private final String FOREVER_INTERVAL_STRING = "Forever";
	private final String INFINITE_BACKUPS_STRING = "Infinite";

	public final AbstractAction okayAction = new AbstractAction() {
		public void actionPerformed( ActionEvent ev ) {
			if( PreferencesContentPane.this.validateInput() ) {
				fireOKActionListeners();
			}
		}
	};

	public final AbstractAction cancelAction = new AbstractAction() {
		public void actionPerformed( ActionEvent ev ) {
		}
	};
	
	public final DocumentListener captureDirectoryChangeListener = new DocumentListener() {
		public void changedUpdate(DocumentEvent e) {
			changedCaptureDirectory = true;
		}
		public void insertUpdate(DocumentEvent e) {
			changedCaptureDirectory = true;
		}
		public void removeUpdate(DocumentEvent e) {
			changedCaptureDirectory = true;
		}
	};
	
	public final DocumentListener renderDialogBoundsChecker = new DocumentListener() {
		public void changedUpdate(DocumentEvent e) {
			if (shouldListenToRenderBoundsChanges){
				checkAndUpdateRenderBounds();
			}
		}
		public void insertUpdate(DocumentEvent e) {
			if (shouldListenToRenderBoundsChanges){
				checkAndUpdateRenderBounds();
			}
		}
		public void removeUpdate(DocumentEvent e) {
			if (shouldListenToRenderBoundsChanges){
				checkAndUpdateRenderBounds();
			}
		}
	};
	
	public final DocumentListener renderDialogWidthChecker = new DocumentListener() {
		public void changedUpdate(DocumentEvent e) {
			if (shouldListenToRenderBoundsChanges){
				checkAndUpdateRenderWidth();
			}
		}
		public void insertUpdate(DocumentEvent e) {
			if (shouldListenToRenderBoundsChanges){
				checkAndUpdateRenderWidth();
			}
		}
		public void removeUpdate(DocumentEvent e) {
			if (shouldListenToRenderBoundsChanges){
				checkAndUpdateRenderWidth();
			}
		}
	};
	
	public final DocumentListener renderDialogHeightChecker = new DocumentListener() {
		public void changedUpdate(DocumentEvent e) {
			if (shouldListenToRenderBoundsChanges){
				checkAndUpdateRenderHeight();
			}
		}
		public void insertUpdate(DocumentEvent e) {
			if (shouldListenToRenderBoundsChanges){
				checkAndUpdateRenderHeight();
			}
		}
		public void removeUpdate(DocumentEvent e) {
			if (shouldListenToRenderBoundsChanges){
				checkAndUpdateRenderHeight();
			}
		}
	};

	public PreferencesContentPane() {
		super();
		jbInit();
		actionInit();
		guiInit();
		checkBoxMapInit();
		miscInit();
		updateGUI();
		scaleFont(this);
	}
	
	private void scaleFont(Component currentComponent){
		currentComponent.setFont(new Font("SansSerif", Font.BOLD, 12));
		if (currentComponent instanceof Container){
			for (int i=0; i<((Container)currentComponent).getComponentCount(); i++){
				scaleFont(((Container)currentComponent).getComponent(i));
			}
		}	
	}
	
	public void setAuthoringTool(AuthoringTool authoringTool){
		this.authoringTool = authoringTool;
	}
	
	public String getTitle() {
		return "Preferences";
	}

	public void preDialogShow( JDialog dialog ) {
		super.preDialogShow( dialog );
		updateGUI();
		changedCaptureDirectory = false;
	}

	public void postDialogShow( JDialog dialog ) {
		super.postDialogShow( dialog );
	}	

	public void addOKActionListener( ActionListener l ) {
		m_okActionListeners.addElement( l );
	}
	public void removeOKActionListener( ActionListener l ) {
		m_okActionListeners.removeElement( l );
	}
	public void addCancelActionListener(ActionListener l ) {
		cancelButton.addActionListener( l );
	}
	public void removeCancelActionListener( ActionListener l ) {
		cancelButton.removeActionListener( l );
	}

	private void fireOKActionListeners() {
		ActionEvent e = new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "OK" );
		for( int i=0; i<m_okActionListeners.size(); i++ ) {
			ActionListener l = (ActionListener)m_okActionListeners.elementAt( i );
			l.actionPerformed( e );
		}
	}

	public void finalizeSelections(){
		setInput();
		if( restartRequired ) {
			DialogManager.showMessageDialog("You will have to restart Alice in order for these settings to take effect.", "Restart Required", JOptionPane.INFORMATION_MESSAGE );
			restartRequired = false;
		} 
		 else if( reloadRequired ) {
			DialogManager.showMessageDialog("You will have to reload the current world in order for these settings to take effect.", "Reload Required", JOptionPane.INFORMATION_MESSAGE );
			reloadRequired = false;
		} 
		if (configTabbedPane != null && generalPanel != null){
			configTabbedPane.setSelectedComponent(generalPanel);
		}
	}

	private void actionInit() {
		okayAction.putValue( Action.NAME, "OK" );
		okayAction.putValue( Action.SHORT_DESCRIPTION, "Accept preference changes" );

		cancelAction.putValue( Action.NAME, "Cancel" );
		cancelAction.putValue( Action.SHORT_DESCRIPTION, "Close dialog without accepting changes" );

		okayButton.setAction( okayAction );
		cancelButton.setAction( cancelAction );
	}

	private void guiInit() {
		
		this.setPreferredSize(new Dimension( 600, 600 ));
		numDigitsComboBox.addItem( "1" );
		numDigitsComboBox.addItem( "2" );
		numDigitsComboBox.addItem( "3" );
		numDigitsComboBox.addItem( "4" );
		numDigitsComboBox.addItem( "5" );
		numDigitsComboBox.addItem( "6" );

		codecComboBox.addItem( "jpeg" );
		codecComboBox.addItem( "png" );

		rendererList.setModel( new ConfigListModel( authoringToolPackage, "rendering.orderedRendererList" ) );
		rendererList.setSelectedIndex( 0 );

		//fill the style combobox with the file names from resources folder
		File resourceDirectory = new File( JAlice.getAliceHomeDirectory(), "resources" ).getAbsoluteFile();
		File[] resourceFiles = resourceDirectory.listFiles( AuthoringToolResources.resourceFileFilter );
		for( int i = 0; i < resourceFiles.length; i++ ) {
			resourceFileComboBox.addItem( resourceFiles[i].getName() );
		}
	}

	private void checkBoxMapInit() {
		checkBoxToConfigKeyMap.put( showStartUpDialogCheckBox, "showStartUpDialog" );
		checkBoxToConfigKeyMap.put( enableHighContrastCheckBox, "enableHighContrastMode" );
		checkBoxToConfigKeyMap.put( showWebWarningCheckBox, "showWebWarningDialog" );
		checkBoxToConfigKeyMap.put( loadSavedTabsCheckBox, "loadSavedTabs" );
//		checkBoxToConfigKeyMap.put( reloadWorldScriptCheckBox, "reloadWorldScriptOnRun" );
		checkBoxToConfigKeyMap.put( saveThumbnailWithWorldCheckBox, "saveThumbnailWithWorld" );
		checkBoxToConfigKeyMap.put( forceSoftwareRenderingCheckBox, "rendering.forceSoftwareRendering" );
		checkBoxToConfigKeyMap.put( showFPSCheckBox, "rendering.showFPS" );
		checkBoxToConfigKeyMap.put( deleteFiles, "rendering.deleteFiles" ); // Aik Min added this.
		checkBoxToConfigKeyMap.put( useBorderlessWindowCheckBox, "rendering.useBorderlessWindow" );
//		checkBoxToConfigKeyMap.put( renderWindowMatchesSceneEditorCheckBox, "rendering.renderWindowMatchesSceneEditor" );
		checkBoxToConfigKeyMap.put( constrainRenderDialogAspectCheckBox, "rendering.constrainRenderDialogAspectRatio" );
		checkBoxToConfigKeyMap.put( ensureRenderDialogIsOnScreenCheckBox, "rendering.ensureRenderDialogIsOnScreen" );
		checkBoxToConfigKeyMap.put( createNormalsCheckBox, "importers.aseImporter.createNormalsIfNoneExist" );
		checkBoxToConfigKeyMap.put( createUVsCheckBox, "importers.aseImporter.createUVsIfNoneExist" );
		checkBoxToConfigKeyMap.put( useSpecularCheckBox, "importers.aseImporter.useSpecular" );
		checkBoxToConfigKeyMap.put( groupMultipleRootObjectsCheckBox, "importers.aseImporter.groupMultipleRootObjects" );
		checkBoxToConfigKeyMap.put( colorToWhiteWhenTexturedCheckBox, "importers.aseImporter.colorToWhiteWhenTextured" );
		checkBoxToConfigKeyMap.put( watcherPanelEnabledCheckBox, "watcherPanelEnabled" );
		checkBoxToConfigKeyMap.put( runtimeScratchPadEnabledCheckBox, "rendering.runtimeScratchPadEnabled" );
		checkBoxToConfigKeyMap.put( infiniteBackupsCheckBox, "saveInfiniteBackups" );
		checkBoxToConfigKeyMap.put( doProfilingCheckBox, "doProfiling" );
//		checkBoxToConfigKeyMap.put( scriptTypeInEnabledCheckBox, "editors.sceneeditor.showScriptComboWidget" );
		checkBoxToConfigKeyMap.put( showWorldStatsCheckBox, "showWorldStats" );
		checkBoxToConfigKeyMap.put( enableScriptingCheckBox, "enableScripting" );
		checkBoxToConfigKeyMap.put( pickUpTilesCheckBox, "gui.pickUpTiles" );
		checkBoxToConfigKeyMap.put( useAlphaTilesCheckBox, "gui.useAlphaTiles" );
//		checkBoxToConfigKeyMap.put( useJavaSyntaxCheckBox, "useJavaSyntax" );
		checkBoxToConfigKeyMap.put( saveAsSingleFileCheckBox, "useSingleFileLoadStore" );
		checkBoxToConfigKeyMap.put( clearStdOutOnRunCheckBox, "clearStdOutOnRun" );
		checkBoxToConfigKeyMap.put( screenCaptureInformUserCheckBox, "screenCapture.informUser" );
//		checkBoxToConfigKeyMap.put( printingFillBackgroundCheckBox, "printing.fillBackground" );
	}

	private void miscInit() {
		browseFileChooser.setApproveButtonText( "Set Directory" );
		browseFileChooser.setDialogTitle( "Choose Directory..." );
		browseFileChooser.setDialogType( JFileChooser.OPEN_DIALOG );
		browseFileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );

		Configuration.addConfigurationListener(
			new ConfigurationListener() {
				public void changing( ConfigurationEvent ev ) {}
				
				public void changed( ConfigurationEvent ev ) {
					if( ev.getKeyName().endsWith( "rendering.orderedRendererList" ) ) {
						restartRequired = true;
					} else if( ev.getKeyName().endsWith( "rendering.forceSoftwareRendering" ) ) {
						restartRequired = true;
					} else if( ev.getKeyName().endsWith( "resourceFile" ) ) {
						restartRequired = true;
					}
				}
			}
		);
	}

	protected boolean isValidRenderBounds(int x, int y, int w, int h){
		if( (x < 0) || (y < 0) || (w <= 0) || (h <= 0) ) {
			return false;
		}	
		return true;
	}
	
	protected void checkAndUpdateRenderWidth(){
		int w = 0, h = 0;
		boolean isOK = true;
		try{
			w = Integer.parseInt( boundsWidthTextField.getText() );
			if (w > 0){
				boundsWidthTextField.setForeground(java.awt.Color.black);
			} else{
				boundsWidthTextField.setForeground(java.awt.Color.red);
				isOK = false;
			}
		}catch (NumberFormatException e ){
			boundsWidthTextField.setForeground(java.awt.Color.red);
			isOK = false;
		}
		try{
			h = Integer.parseInt( boundsHeightTextField.getText() );
			if (h <= 0){
				isOK = false;
			}
		}catch (NumberFormatException e ){
			isOK = false;
		}
		if (constrainRenderDialogAspectCheckBox.isSelected() && isOK && authoringTool != null){
			double currentAspectRatio = authoringTool.getAspectRatio();
			h = (int)Math.round( w/currentAspectRatio );
			if (h<=0){
				h = 1;
			}
			shouldListenToRenderBoundsChanges = false;
			boundsHeightTextField.setText(Integer.toString(h));
			shouldListenToRenderBoundsChanges = true;
		}
		okayButton.setEnabled(isOK);
	}
	
	protected void checkAndUpdateRenderHeight(){
		int w = 0, h = 0;
		boolean isOK = true;
		try{
			h = java.lang.Integer.parseInt( boundsHeightTextField.getText() );
			if (h > 0){
				boundsHeightTextField.setForeground(Color.black);
			} else{
				boundsHeightTextField.setForeground(Color.red);
				isOK = false;
			}
		}catch (NumberFormatException e ){
			boundsHeightTextField.setForeground(Color.red);
			isOK = false;
		}
		try{
			w = Integer.parseInt( boundsWidthTextField.getText() );
			if (w <= 0){
				isOK = false;
			}
		}catch (NumberFormatException e ){
			isOK = false;
		}
		if (constrainRenderDialogAspectCheckBox.isSelected() && isOK && authoringTool != null){
			double currentAspectRatio = authoringTool.getAspectRatio();
			w = (int)Math.round( h*currentAspectRatio );
			if (w <= 0){
				w = 1;
			}
			shouldListenToRenderBoundsChanges = false;
			boundsWidthTextField.setText(Integer.toString(w));
			shouldListenToRenderBoundsChanges = true;
		}
		okayButton.setEnabled(isOK);
	}
	
	protected void checkAndUpdateRenderBounds(){
		int x = 0,y = 0,w = 0,h = 0;
		boolean isOK = true;
		try{
			x = Integer.parseInt( boundsXTextField.getText() );
			if (x >= 0){
				boundsXTextField.setForeground(Color.black);
			} else{
				boundsXTextField.setForeground(Color.red);
				isOK = false;
			}
		}catch (NumberFormatException e ){
			boundsXTextField.setForeground(Color.red);
			isOK = false;
		}
		try{
			y = Integer.parseInt( boundsYTextField.getText() );
			if (y >= 0){
				boundsYTextField.setForeground(Color.black);
			} else{
				boundsYTextField.setForeground(Color.red);
				isOK = false;
			}
		}catch (NumberFormatException e ){
			boundsYTextField.setForeground(Color.red);
			isOK = false;
		}
		try{
			w = Integer.parseInt( boundsWidthTextField.getText() );
			if (w > 0){
				boundsWidthTextField.setForeground(Color.black);
			} else{
				boundsWidthTextField.setForeground(Color.red);
				isOK = false;
			}
		}catch (NumberFormatException e ){
			boundsWidthTextField.setForeground(Color.red);
			isOK = false;
		}
		try{
			h = Integer.parseInt( boundsHeightTextField.getText() );
			if (h > 0){
				boundsHeightTextField.setForeground(Color.black);
			} else{
				boundsHeightTextField.setForeground(Color.red);
				isOK = false;
			}
		}catch (NumberFormatException e ){
			boundsHeightTextField.setForeground(Color.red);
			isOK = false;
		}
		if (constrainRenderDialogAspectCheckBox.isSelected() && isOK && authoringTool != null){
			double currentAspectRatio = authoringTool.getAspectRatio();
			if (currentAspectRatio > 1.0){
				w = (int)Math.round( h*currentAspectRatio );
				if (w <= 0){
					w = 1;
				}
				shouldListenToRenderBoundsChanges = false;
				boundsWidthTextField.setText(Integer.toString(w));
				shouldListenToRenderBoundsChanges = true;
			} else{
				h = (int)Math.round( w/currentAspectRatio );
				if (h <=0){
					h = 1;
				}
				shouldListenToRenderBoundsChanges = false;
				boundsHeightTextField.setText(Integer.toString(h));
				shouldListenToRenderBoundsChanges = true;
			}
		}
		okayButton.setEnabled(isOK);
	}

	protected boolean validateInput() {
		try {
			int i = Integer.parseInt( maxRecentWorldsTextField.getText() );
			if( (i < 0) || (i > 30) ) {
				throw new NumberFormatException();
			}
		} catch( NumberFormatException e ) {
			DialogManager.showMessageDialog( "the maximum number of recent worlds must be between 0 and 30" , "Invalid Clipboard Number", JOptionPane.INFORMATION_MESSAGE );
			return false;
		}

		try {
			int i = Integer.parseInt( numClipboardsTextField.getText() );
			if( (i < 0) || (i > 30) ) {
				throw new NumberFormatException();
			}
		} catch( NumberFormatException e ) {
			DialogManager.showMessageDialog( "the number of clipboards must be between 0 and 30", "Invalid Clipboard Number", JOptionPane.INFORMATION_MESSAGE );
			return false;
		}

		try {
			int x = Integer.parseInt( boundsXTextField.getText() );
			int y = Integer.parseInt( boundsYTextField.getText() );
			int w = Integer.parseInt( boundsWidthTextField.getText() );
			int h = Integer.parseInt( boundsHeightTextField.getText() );
			if( !isValidRenderBounds(x,y,w,h) ) {
				throw new NumberFormatException();
			}
		} catch( NumberFormatException e ) {
			DialogManager.showMessageDialog("all of the render window bounds values must be integers greater than 0", "Bad Render Bounds", JOptionPane.INFORMATION_MESSAGE );
			return false;
		}

		// bad directories are just given warnings
		File worldDirectoryFile = new File( worldDirectoryTextField.getText() );
		if( (!worldDirectoryFile.exists()) || (!worldDirectoryFile.isDirectory()) || (!worldDirectoryFile.canRead()) ) {
			int result = DialogManager.showConfirmDialog(worldDirectoryFile.getAbsolutePath() + " is not valid.  The worlds directory must be a directory that exists and can be read.  Would you like to fix this now?", "Bad Directory", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );
			if( result != JOptionPane.NO_OPTION ) {
				return false;
			}

		}

		File importDirectoryFile = new java.io.File( importDirectoryTextField.getText() );
		if( (!importDirectoryFile.exists()) || (!importDirectoryFile.isDirectory()) || (!importDirectoryFile.canRead()) ) {
			int result = DialogManager.showConfirmDialog( importDirectoryFile.getAbsolutePath() + " is not valid.  The import directory must be a directory that exists and can be read.  Would you like to fix this now?", "Bad Directory", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );
			if( result != JOptionPane.NO_OPTION ) {
				return false;
			}
		}


		if (changedCaptureDirectory){
			File captureDirectoryFile = new File( captureDirectoryTextField.getText() );
			int directoryCheck = FileUtilities.isWritableDirectory(captureDirectoryFile);
			if (directoryCheck == FileUtilities.DIRECTORY_IS_NOT_WRITABLE){
				DialogManager.showMessageDialog( "The capture directory specified can not be written to. Please choose another directory.", "Bad Directory", JOptionPane.INFORMATION_MESSAGE);
				return false;
			} else if(directoryCheck == FileUtilities.DIRECTORY_DOES_NOT_EXIST || directoryCheck == FileUtilities.BAD_DIRECTORY_INPUT){
				DialogManager.showMessageDialog( "The capture directory must be a directory that exists.", "Bad Directory", JOptionPane.INFORMATION_MESSAGE );
				return false;
			}
		}

		if( baseNameTextField.getText().trim().equals( "" ) ) {
			DialogManager.showMessageDialog( "The capture base name must not be empty.", "Bad Base Name", JOptionPane.INFORMATION_MESSAGE );
			return false;
		}

		char[] badChars = { '\\', '/', ':', '*', '?', '"', '<', '>', '|' };  // TODO: make this more platform independent
		String baseName = baseNameTextField.getText().trim();
		for( int i = 0; i < badChars.length; i++ ) {
			if( baseName.indexOf( badChars[i] ) != -1 ) {
				StringBuffer message = new StringBuffer( "Filenames may not contain the following characters:" );
				for( int j = 0; j < badChars.length; j++ ) {
					message.append( " " );
					message.append( badChars[j] );
				}
				DialogManager.showMessageDialog( message.toString(), "Bad Filename", JOptionPane.INFORMATION_MESSAGE );
				return false;
			}
		}
		
		String saveIntervalString = (String)saveIntervalComboBox.getSelectedItem();
		if (!saveIntervalString.equalsIgnoreCase(FOREVER_INTERVAL_STRING)){
			try{
				Integer.parseInt(saveIntervalString); //make sure that 'saveIntervalString' is a number
				
			} catch (Throwable t){
				DialogManager.showMessageDialog( "You must enter a valid number for the time to wait before prompting to save.", "Bad Prompt To Save Interval", JOptionPane.INFORMATION_MESSAGE );
				return false;
			}
		}
		
		String backupCountString = (String)backupCountComboBox.getSelectedItem();
		if (!backupCountString.equalsIgnoreCase(INFINITE_BACKUPS_STRING)){
			try{
				Integer.parseInt(backupCountString);//make sure that 'backupCountString' is a number
			} catch (Throwable t){
				DialogManager.showMessageDialog( "You must enter a valid number for the number of backups you want Alice to save.", "Bad Backup Count Value", JOptionPane.INFORMATION_MESSAGE );
				return false;
			}
		}
		
		
		
		String fontSizeString = (String)fontSizeComboBox.getSelectedItem();
		try{
			Integer.parseInt(fontSizeString);//make sure that 'fontSizeString' is a number
		} catch (Throwable t){
			DialogManager.showMessageDialog( "You must enter a valid number for the font size.", "Bad Backup Font Size", JOptionPane.INFORMATION_MESSAGE );
			return false;
		}
		
		String pivotLineWidthString = (String)pivotLineWidthCB.getSelectedItem();
		try{
			Integer.parseInt(pivotLineWidthString);//make sure that 'fontSizeString' is a number
		} catch (Throwable t){
			DialogManager.showMessageDialog( "You must enter a valid number for the pivot line width.", "Bad Backup Pivote Line Width", JOptionPane.INFORMATION_MESSAGE );
			return false;
		}



		return true;
	}

	protected void setInput() {
		boolean oldContrast = Configuration.getValue( authoringToolPackage, "enableHighContrastMode" ).equalsIgnoreCase( "true" );
		for( java.util.Iterator<Object> iter = checkBoxToConfigKeyMap.keySet().iterator(); iter.hasNext(); ) {
			JCheckBox checkBox = (JCheckBox)iter.next();
			String currentValue = Configuration.getValue( authoringToolPackage, (String)checkBoxToConfigKeyMap.get( checkBox ) );
			if( currentValue == null ) {
				AuthoringTool.showErrorDialog( "Warning: no value found for preference: " + checkBoxToConfigKeyMap.get( checkBox ), null );
				currentValue = "false";
				Configuration.setValue( authoringToolPackage, (String)checkBoxToConfigKeyMap.get( checkBox ), currentValue );
			}
			if( currentValue.equalsIgnoreCase( "true" ) != checkBox.isSelected() ) {
				Configuration.setValue( authoringToolPackage, (String)checkBoxToConfigKeyMap.get( checkBox ), checkBox.isSelected() ? "true" : "false" );
			}
		}

		if( ! Configuration.getValue( authoringToolPackage, "recentWorlds.maxWorlds" ).equals( maxRecentWorldsTextField.getText() ) ) {
			Configuration.setValue( authoringToolPackage, "recentWorlds.maxWorlds", maxRecentWorldsTextField.getText() );
		}
		if( ! Configuration.getValue( authoringToolPackage, "numberOfClipboards" ).equals( numClipboardsTextField.getText() ) ) {
			Configuration.setValue( authoringToolPackage, "numberOfClipboards", numClipboardsTextField.getText() );
		}
		String boundsString = boundsXTextField.getText() + ", " + boundsYTextField.getText() + ", " + boundsWidthTextField.getText() + ", " + boundsHeightTextField.getText();
		if( ! Configuration.getValue( authoringToolPackage, "rendering.renderWindowBounds" ).equals( boundsString ) ) {
			Configuration.setValue( authoringToolPackage, "rendering.renderWindowBounds", boundsString );
		}
		if( ! Configuration.getValue( authoringToolPackage, "directories.worldsDirectory" ).equals( worldDirectoryTextField.getText() ) ) {
			Configuration.setValue( authoringToolPackage, "directories.worldsDirectory", worldDirectoryTextField.getText() );
		}
		if( ! Configuration.getValue( authoringToolPackage, "directories.importDirectory" ).equals( worldDirectoryTextField.getText() ) ) {
			Configuration.setValue( authoringToolPackage, "directories.importDirectory", worldDirectoryTextField.getText() );
		}

		
		if( ! Configuration.getValue( authoringToolPackage, "screenCapture.directory" ).equals( captureDirectoryTextField.getText() ) ) {
			Configuration.setValue( authoringToolPackage, "screenCapture.directory", captureDirectoryTextField.getText() );
		}
		if( ! Configuration.getValue( authoringToolPackage, "screenCapture.baseName" ).equals( baseNameTextField.getText() ) ) {
			Configuration.setValue( authoringToolPackage, "screenCapture.baseName", baseNameTextField.getText() );
		}
		if( ! Configuration.getValue( authoringToolPackage, "screenCapture.numDigits" ).equals( (String)numDigitsComboBox.getSelectedItem() ) ) {
			Configuration.setValue( authoringToolPackage, "screenCapture.numDigits", (String)numDigitsComboBox.getSelectedItem() );
		}
		if( ! Configuration.getValue( authoringToolPackage, "screenCapture.codec" ).equals( (String)codecComboBox.getSelectedItem() ) ) {
			Configuration.setValue( authoringToolPackage, "screenCapture.codec", (String)codecComboBox.getSelectedItem() );
		}
		if( ! Configuration.getValue( authoringToolPackage, "resourceFile" ).equals( (String)resourceFileComboBox.getSelectedItem() ) ) {
			Configuration.setValue( authoringToolPackage, "resourceFile", (String)resourceFileComboBox.getSelectedItem() );
		}

		
		
		String saveIntervalString = (String)saveIntervalComboBox.getSelectedItem();
		if (saveIntervalString.equalsIgnoreCase(FOREVER_INTERVAL_STRING)){
			Configuration.setValue( authoringToolPackage, "promptToSaveInterval", Integer.toString(Integer.MAX_VALUE) );
		} else{
			Configuration.setValue( authoringToolPackage, "promptToSaveInterval", saveIntervalString );
		}
		
		String backupCountString = (String)backupCountComboBox.getSelectedItem();
		if (saveIntervalString.equalsIgnoreCase(FOREVER_INTERVAL_STRING)){
			Configuration.setValue( authoringToolPackage, "maximumWorldBackupCount", Integer.toString(Integer.MAX_VALUE) );
		} else{
			Configuration.setValue( authoringToolPackage, "maximumWorldBackupCount", backupCountString );
		}
		
		
		int oldFontSize = ((Font)UIManager.get("Label.font")).getSize();
		String fontSizeString = (String)fontSizeComboBox.getSelectedItem();
		Configuration.setValue( authoringToolPackage, "fontSize", fontSizeString );
		int newFontSize = Integer.valueOf(fontSizeString).intValue();
		if (oldContrast != enableHighContrastCheckBox.isSelected() || oldFontSize != newFontSize){
			restartRequired = true;
		}
		
		
		float oldLineWidth = Float.parseFloat(Configuration.getValue(decoratorPackage , "pivotAndBoundingBoxLineWidth"));
		String newLineWidth = (String)pivotLineWidthCB.getSelectedItem();
		Configuration.setValue( decoratorPackage, "pivotAndBoundingBoxLineWidth", newLineWidth );
		int newLineWidthInt = Integer.valueOf(newLineWidth).intValue();
		//if (oldContrast != enableHighContrastCheckBox.isSelected() || oldLineWidth != newLineWidthInt){
		//	restartRequired = true;
		//}

		String oldState = Configuration.getValue(colorStatePackage, "colorBlindState");
		String newState = "";
		if(colorblindMode.isSelected())
		{
			newState = "1";
		}
		else{
			newState = "0";
		}
		
		if(!oldState.equals(newState)){
			Configuration.setValue(colorStatePackage, "colorBlindState" , newState);
			restartRequired = true;
		}

		
		//TODO: currently the rendererList updates its data immediately...

		try {
			Configuration.storeConfig();
		} catch( IOException e ) {
			AuthoringTool.showErrorDialog( "Error storing preferences.", e );
		}
	}

	protected void updateGUI() {
		for( java.util.Iterator<Object> iter = checkBoxToConfigKeyMap.keySet().iterator(); iter.hasNext(); ) {
			JCheckBox checkBox = (JCheckBox)iter.next();
			boolean value;
			try {
				value = Configuration.getValue( authoringToolPackage, (String)checkBoxToConfigKeyMap.get( checkBox ) ).equalsIgnoreCase( "true" );
			} catch( Exception e ) {
				value = false;
			}
			checkBox.setSelected( value );
		}
		
		setSaveIntervalValues();
		initSaveIntervalComboBox();
		setBackupCountValues();
		initBackupCountComboBox();
		setFontSizeValues();
		initFontSizeComboBox();
		setPivotLineWidthValues();
		initPivotLineWidthComboBox();

		maxRecentWorldsTextField.setText( Configuration.getValue( authoringToolPackage, "recentWorlds.maxWorlds" ) );
		numClipboardsTextField.setText( Configuration.getValue( authoringToolPackage, "numberOfClipboards" ) );



		String boundsString = Configuration.getValue( authoringToolPackage, "rendering.renderWindowBounds" );
		StringTokenizer st = new StringTokenizer( boundsString, " \t," );
		if( st.countTokens() == 4 ) {
			boundsXTextField.setText( st.nextToken() );
			boundsYTextField.setText( st.nextToken() );
			boundsWidthTextField.setText( st.nextToken() );
			boundsHeightTextField.setText( st.nextToken() );
		}

		String worldDirectory = Configuration.getValue( authoringToolPackage, "directories.worldsDirectory" );
		worldDirectoryTextField.setText( worldDirectory );
		String importDirectory = Configuration.getValue( authoringToolPackage, "directories.importDirectory" );
		importDirectoryTextField.setText( importDirectory );

		String captureDirectory = Configuration.getValue( authoringToolPackage, "screenCapture.directory" );
		captureDirectoryTextField.setText( captureDirectory );

		baseNameTextField.setText( Configuration.getValue( authoringToolPackage, "screenCapture.baseName" ) );
		numDigitsComboBox.setSelectedItem( Configuration.getValue( authoringToolPackage, "screenCapture.numDigits" ) );
		codecComboBox.setSelectedItem( Configuration.getValue( authoringToolPackage, "screenCapture.codec" ) );
		resourceFileComboBox.setSelectedItem( Configuration.getValue( authoringToolPackage, "resourceFile" ) );

		
	}

	public void setVisible( boolean b ) {
		if( b ) {
			updateGUI();
		}
		super.setVisible( b );
	}

	protected class ConfigListModel implements ListModel, ConfigurationListener {
		protected Package configPackage;
		protected String configKey;
		protected java.util.Set<ListDataListener> listenerSet = new java.util.HashSet<ListDataListener>();

		public ConfigListModel( Package configPackage, String configKey ) {
			this.configPackage = configPackage;
			this.configKey = configKey;
			Configuration.addConfigurationListener( this );
		}

		public void addListDataListener( ListDataListener listener ) {
			listenerSet.add( listener );
		}

		public void removeListDataListener( ListDataListener listener ) {
			listenerSet.remove( listener );
		}

		public int getSize() {
			return Configuration.getValueList( configPackage, configKey ).length;
		}

		public Object getElementAt( int index ) {
			String item = Configuration.getValueList( configPackage, configKey )[index];
			return AuthoringToolResources.getReprForValue( item );
		}

		public void moveIndexHigher( int index ) {
			String[] valueList = Configuration.getValueList( configPackage, configKey );
			if( (index > 0) && (index < valueList.length) ) {
				String[] newValueList = new String[valueList.length];
				System.arraycopy( valueList, 0, newValueList, 0, valueList.length );
				String temp = newValueList[index];
				newValueList[index] = newValueList[index - 1];
				newValueList[index - 1] = temp;
				Configuration.setValueList( configPackage, configKey, newValueList );
			}
		}

		public void moveIndexLower( int index ) {
			String[] valueList = Configuration.getValueList( configPackage, configKey );
			if( (index >= 0) && (index < (valueList.length - 1)) ) {
				String[] newValueList = new String[valueList.length];
				System.arraycopy( valueList, 0, newValueList, 0, valueList.length );
				String temp = newValueList[index];
				newValueList[index] = newValueList[index + 1];
				newValueList[index + 1] = temp;
				Configuration.setValueList( configPackage, configKey, newValueList );
			}
		}

		public void changing( ConfigurationEvent ev ) {}
		public void changed( ConfigurationEvent ev ) {
			if( ev.getKeyName().endsWith( "rendering.orderedRendererList" ) ) {
				int upperRange = 0;
				if( ev.getOldValueList() != null ) {
					upperRange = Math.max( upperRange, ev.getOldValueList().length );
				}
				if( ev.getNewValueList() != null ) {
					upperRange = Math.max( upperRange, ev.getNewValueList().length );
				}
				ListDataEvent listDataEvent = new ListDataEvent( this, ListDataEvent.CONTENTS_CHANGED, 0, upperRange );
				for( Iterator<ListDataListener> iter = listenerSet.iterator(); iter.hasNext(); ) {
					iter.next().contentsChanged( listDataEvent );
				}
			}
		}
	}
	
	private int getValueForString(String numString){
		if (numString.equalsIgnoreCase(FOREVER_INTERVAL_STRING) || numString.equalsIgnoreCase(INFINITE_BACKUPS_STRING)){
			return Integer.MAX_VALUE;
		}
		try{
			int toReturn = Integer.parseInt(numString);
			return toReturn;
		} catch (NumberFormatException nfe){
			return -1;
		}
	}
	
	private void setSaveIntervalValues(){
		saveIntervalOptions.removeAllElements();
		saveIntervalOptions.add("15");
		saveIntervalOptions.add("30");
		saveIntervalOptions.add("45");
		saveIntervalOptions.add("60");
		saveIntervalOptions.add(FOREVER_INTERVAL_STRING);
		String intervalString = Configuration.getValue( authoringToolPackage, "promptToSaveInterval" );
		int interval = -1;
		try{
			interval = Integer.parseInt(intervalString);
		} catch (Throwable t){}
		addComboBoxValueValue(interval, saveIntervalOptions);
	}
	
	private void addComboBoxValueValue(int toAdd, Vector<String> toAddTo){
		if (toAdd > 0){
			boolean isThere = false;
			int location = toAddTo.size()-1;
			for (int i=0; i<toAddTo.size(); i++){
				int currentValue = getValueForString( toAddTo.get(i) );
				if (toAdd == currentValue){
					isThere = true;
				} else if (toAdd < currentValue && location > i){
					location = i;
				}
			}
			if (!isThere){
				Integer currentValue = new Integer(toAdd);
				toAddTo.insertElementAt(currentValue.toString(), location);
			}
		}
	}
	
	private void initSaveIntervalComboBox(){
		saveIntervalComboBox.removeAllItems();
		String intervalString = Configuration.getValue( authoringToolPackage, "promptToSaveInterval" );
		for (int i=0; i<saveIntervalOptions.size(); i++){
			saveIntervalComboBox.addItem(saveIntervalOptions.get(i));
			if (intervalString.equalsIgnoreCase(saveIntervalOptions.get(i).toString())){
				saveIntervalComboBox.setSelectedIndex(i);
			} else if (intervalString.equalsIgnoreCase(Integer.toString(Integer.MAX_VALUE)) && ((String)saveIntervalOptions.get(i)).equalsIgnoreCase(FOREVER_INTERVAL_STRING)){
				saveIntervalComboBox.setSelectedIndex(i);
			}
		}
	}
	
	private void setBackupCountValues(){
		backupCountOptions.removeAllElements();
		backupCountOptions.add("0");
		backupCountOptions.add("1");
		backupCountOptions.add("2");
		backupCountOptions.add("3");
		backupCountOptions.add("4");
		backupCountOptions.add("5");
		backupCountOptions.add("10");
		backupCountOptions.add(INFINITE_BACKUPS_STRING);
		String intervalString = Configuration.getValue( authoringToolPackage, "maximumWorldBackupCount" );
		int interval = -1;
		try{
			interval = Integer.parseInt(intervalString);
		} catch (Throwable t){}
		addComboBoxValueValue(interval, backupCountOptions);
	}


	private void initBackupCountComboBox(){
		backupCountComboBox.removeAllItems();
		String intervalString = Configuration.getValue( authoringToolPackage, "maximumWorldBackupCount" );
		for (int i=0; i<backupCountOptions.size(); i++){
			backupCountComboBox.addItem(backupCountOptions.get(i));
			if (intervalString.equalsIgnoreCase(backupCountOptions.get(i).toString())){
				backupCountComboBox.setSelectedIndex(i);
			} else if (intervalString.equalsIgnoreCase(Integer.toString(Integer.MAX_VALUE)) && ((String)backupCountOptions.get(i)).equalsIgnoreCase(INFINITE_BACKUPS_STRING)){
				backupCountComboBox.setSelectedIndex(i);
			}
		}
	}
	
	private void setFontSizeValues(){
		fontSizeOptions.removeAllElements();
		fontSizeOptions.add("8");
		fontSizeOptions.add("10");
		fontSizeOptions.add("12");
		fontSizeOptions.add("14");
		fontSizeOptions.add("16");
		fontSizeOptions.add("18");
		fontSizeOptions.add("20");
	}


	private void initFontSizeComboBox(){
		fontSizeComboBox.removeAllItems();
		String intervalString = Configuration.getValue( authoringToolPackage, "fontSize" );
		for (int i=0; i<fontSizeOptions.size(); i++){
			fontSizeComboBox.addItem(fontSizeOptions.get(i));
			if (intervalString.equalsIgnoreCase(fontSizeOptions.get(i).toString())){
				fontSizeComboBox.setSelectedIndex(i);
			} 
		}
	}

	private void setPivotLineWidthValues(){
		pivoteLineWidthOptions.removeAllElements();
		pivoteLineWidthOptions.add("1");
		pivoteLineWidthOptions.add("2");
		pivoteLineWidthOptions.add("3");
		pivoteLineWidthOptions.add("4");
		pivoteLineWidthOptions.add("5");
		pivoteLineWidthOptions.add("6");
		pivoteLineWidthOptions.add("7");
		pivoteLineWidthOptions.add("8");
		pivoteLineWidthOptions.add("10");
		pivoteLineWidthOptions.add("12");
		pivoteLineWidthOptions.add("14");
		pivoteLineWidthOptions.add("16");
		pivoteLineWidthOptions.add("18");
		pivoteLineWidthOptions.add("20");
	}


	private void initPivotLineWidthComboBox(){
		pivotLineWidthCB.removeAllItems();
		String lineWidthString = Configuration.getValue( decoratorPackage , "pivotAndBoundingBoxLineWidth" );
		for (int i=0; i<pivoteLineWidthOptions.size(); i++){
			pivotLineWidthCB.addItem(pivoteLineWidthOptions.get(i));
			if (lineWidthString != null && lineWidthString.equalsIgnoreCase(pivoteLineWidthOptions.get(i).toString())){
				pivotLineWidthCB.setSelectedIndex(i);
			} 
		}
	}
	
	/////////////////
	// Callbacks
	/////////////////


	void worldDirectoryBrowseButton_actionPerformed( ActionEvent ev ) {
		File parent = new java.io.File( Configuration.getValue( authoringToolPackage, "directories.worldsDirectory" ) ).getParentFile();
		browseFileChooser.setCurrentDirectory( parent );
		int returnVal = browseFileChooser.showOpenDialog( this );

		if( returnVal == JFileChooser.APPROVE_OPTION ) {
			File file = browseFileChooser.getSelectedFile();
			worldDirectoryTextField.setText( file.getAbsolutePath() );
		}
	}

	void importDirectoryBrowseButton_actionPerformed( ActionEvent ev ) {
		File parent = new File( Configuration.getValue( authoringToolPackage, "directories.importDirectory" ) ).getParentFile();
		browseFileChooser.setCurrentDirectory( parent );
		int returnVal = browseFileChooser.showOpenDialog( this );

		if( returnVal == JFileChooser.APPROVE_OPTION ) {
			java.io.File file = browseFileChooser.getSelectedFile();
			importDirectoryTextField.setText( file.getAbsolutePath() );
		}
	}

	void browseButton_actionPerformed(ActionEvent e) {
		boolean done = false;
		String finalFilePath = captureDirectoryTextField.getText();
		while (!done){
			File parent = new File( finalFilePath );
			if (!parent.exists()){
				parent =  new File( Configuration.getValue( authoringToolPackage, "screenCapture.directory" ));
			}
			browseFileChooser.setCurrentDirectory( parent );
			int returnVal = browseFileChooser.showOpenDialog( this );
	
			if( returnVal == JFileChooser.APPROVE_OPTION ) {
				java.io.File captureDirectoryFile = browseFileChooser.getSelectedFile();
				int directoryCheck = FileUtilities.isWritableDirectory(captureDirectoryFile);
				if (directoryCheck == FileUtilities.DIRECTORY_IS_NOT_WRITABLE){
					done = false;
					DialogManager.showMessageDialog( "The capture directory specified can not be written to. Please choose another directory.", "Bad Directory", JOptionPane.INFORMATION_MESSAGE);
				} else if(directoryCheck == FileUtilities.DIRECTORY_DOES_NOT_EXIST || directoryCheck == FileUtilities.BAD_DIRECTORY_INPUT){
					done = false;
					DialogManager.showMessageDialog( "The capture directory must be a directory that exists.", "Bad Directory", JOptionPane.INFORMATION_MESSAGE );
				} else {
					finalFilePath = captureDirectoryFile.getAbsolutePath();
					done = true;
				}
			} else{
				finalFilePath = parent.getAbsolutePath();
				done = true;
			}
		}
		captureDirectoryTextField.setText( finalFilePath );
	}



	void rendererMoveUpButton_actionPerformed( ActionEvent ev ) {
		Object selectedItem = rendererList.getSelectedValue();
		((ConfigListModel)rendererList.getModel()).moveIndexHigher( rendererList.getSelectedIndex() );
		rendererList.setSelectedValue( selectedItem, false );
	}

	void rendererMoveDownButton_actionPerformed( ActionEvent ev ) {
		Object selectedItem = rendererList.getSelectedValue();
		((ConfigListModel)rendererList.getModel()).moveIndexLower( rendererList.getSelectedIndex() );
		rendererList.setSelectedValue( selectedItem, false );
	}




	///////////////////////
	// Autogenerated GUI
	///////////////////////

	JTabbedPane configTabbedPane = new JTabbedPane();
	
	JPanel seldomUsedPanel = new JPanel();
	JPanel renderingPanel = new JPanel();
	JPanel directoriesPanel = new JPanel();
	JPanel aseImporterPanel = new JPanel();
	Border etchedBorder;
	Border emptyBorder;
	BorderLayout borderLayout4 = new BorderLayout();
	BorderLayout borderLayout5 = new BorderLayout();
	BorderLayout borderLayout6 = new BorderLayout();
	JPanel buttonPanel = new JPanel();
	Box buttonBox;
	Component component2;
	JButton okayButton = new JButton();
	Component component1;
	BorderLayout borderLayout7 = new BorderLayout();
	Box directoriesBox;
	
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	
	JLabel importDirectoryLabel = new JLabel();
	
	JTextField importDirectoryTextField = new JTextField();
	
	JButton importDirectoryBrowseButton = new JButton();
	Component component7;
	Box aseImporterBox;
	JCheckBox createNormalsCheckBox = new JCheckBox();
	JCheckBox createUVsCheckBox = new JCheckBox();
	JCheckBox useSpecularCheckBox = new JCheckBox();
	JCheckBox groupMultipleRootObjectsCheckBox = new JCheckBox();
	JCheckBox colorToWhiteWhenTexturedCheckBox = new JCheckBox();
	JButton cancelButton = new JButton();
	Component component8;

	JPanel screenGrabPanel = new JPanel();
	GridBagLayout gridBagLayout2 = new GridBagLayout();
	JLabel captureDirectory = new JLabel();
	JTextField captureDirectoryTextField = new JTextField();
	JButton browseButton = new JButton();
	Component component9;
	JLabel baseNameLabel = new JLabel();
	JTextField baseNameTextField = new JTextField();
	JLabel numDigitsLabel = new JLabel();
	JComboBox numDigitsComboBox = new JComboBox();
	JLabel codecLabel = new JLabel();
	JComboBox codecComboBox = new JComboBox();
	JLabel usageLabel = new JLabel();
	JCheckBox doProfilingCheckBox = new JCheckBox();
	GridBagLayout gridBagLayout3 = new GridBagLayout();
	JCheckBox forceSoftwareRenderingCheckBox = new JCheckBox();
	JCheckBox showFPSCheckBox = new JCheckBox();
	JCheckBox deleteFiles = new JCheckBox(); // Aik Min added this.

	JLabel boundsXLabel = new JLabel();
	JTextField boundsXTextField = new JTextField();
	JLabel boundsWidthLabel = new JLabel();
	JLabel boundsYLabel = new JLabel();
	JTextField boundsWidthTextField = new JTextField();
	JLabel renderWindowBoundsLabel = new JLabel();
	JLabel boundsHeightLabel = new JLabel();
	JPanel renderWindowBoundsPanel = new JPanel();
	JTextField boundsYTextField = new JTextField();
	JTextField boundsHeightTextField = new JTextField();
	JCheckBox useBorderlessWindowCheckBox = new JCheckBox();
	JLabel rendererListLabel = new JLabel();
	JList rendererList = new JList();
	Component component6;
	JButton rendererMoveUpButton = new JButton();
	JButton rendererMoveDownButton = new JButton();
	GridBagLayout gridBagLayout4 = new GridBagLayout();
	JCheckBox infiniteBackupsCheckBox = new JCheckBox();
	
	
	
	BorderLayout borderLayout8 = new BorderLayout();

	BorderLayout borderLayout2 = new BorderLayout();
	
	JCheckBox runtimeScratchPadEnabledCheckBox = new JCheckBox();
	
	JCheckBox enableScriptingCheckBox = new JCheckBox();
	

	Border border1;
	JCheckBox saveAsSingleFileCheckBox = new JCheckBox();
	
	
	
	
	GridBagLayout gridBagLayout5 = new GridBagLayout();
	JCheckBox watcherPanelEnabledCheckBox = new JCheckBox();
	
	
	// General tab components//////////////////////
	JPanel maxRecentWorldsPanel = new JPanel();
	JPanel generalPanel = new JPanel();
	JPanel resourcesPanel = new JPanel();
	JPanel inputDirectoriesPanel = new JPanel();
	JPanel fontSizePanel = new JPanel();
	
	JPanel viStyleFontPanel = new ViStyleFontPanel();//newly added panel to read/write to an XML file
	
	JLabel maxRecentWorldsLabel = new JLabel();
	JTextField maxRecentWorldsTextField = new JTextField();
	
	JLabel resourcesLabel = new JLabel();
	JComboBox resourceFileComboBox = new JComboBox();
	
	JLabel worldDirectoryLabel = new JLabel();
	JTextField worldDirectoryTextField = new JTextField();
	JButton worldDirectoryBrowseButton = new JButton();
	
	JComboBox fontSizeComboBox = new JComboBox();
	JLabel fontSizeLabel = new JLabel();
	
	JPanel pivoteDecoratorPanel = new JPanel();	
	JComboBox pivotLineWidthCB = new JComboBox();
	JLabel pivotLineWidthLbl = new JLabel();
	
	Component component5;
	//+++++++ end general tab components +++++++++++
	
	
	
	
	
	///// seldom used tab components
	JCheckBox showStartUpDialogCheckBox = new JCheckBox();
	JCheckBox showWebWarningCheckBox = new JCheckBox();
	private JCheckBox loadSavedTabsCheckBox = new JCheckBox();
	JCheckBox pickUpTilesCheckBox = new JCheckBox();
	JCheckBox useAlphaTilesCheckBox = new JCheckBox();
	private JCheckBox saveThumbnailWithWorldCheckBox = new JCheckBox();
	JCheckBox showWorldStatsCheckBox = new JCheckBox();
	JCheckBox clearStdOutOnRunCheckBox = new JCheckBox();
	JCheckBox enableHighContrastCheckBox = new JCheckBox();
	JPanel numClipboardsPanel = new JPanel();
	private JPanel saveIntervalPanel = new JPanel();
	private JPanel backupCountPanel = new JPanel();
	JTextField numClipboardsTextField = new JTextField();
	JLabel numClipboardsLabel = new JLabel();
	///++++++ end  seldom used tab components ++++++++++
	
	
	
	
	
	
	
	
	
	JCheckBox constrainRenderDialogAspectCheckBox = new JCheckBox();
	JCheckBox ensureRenderDialogIsOnScreenCheckBox = new JCheckBox();
	
	
	private JCheckBox screenCaptureInformUserCheckBox = new JCheckBox();

	private JComboBox saveIntervalComboBox = new JComboBox();
	private JLabel saveIntervalLabelEnd = new JLabel();
	
	private java.util.Vector<String> saveIntervalOptions = new java.util.Vector<String>();

	private JComboBox backupCountComboBox = new JComboBox();
	private JLabel backupCountLabel = new JLabel();
	
	private JCheckBox colorblindMode;
	private java.util.Vector<String> backupCountOptions = new java.util.Vector<String>();
	
	
	private java.util.Vector<String> fontSizeOptions = new java.util.Vector<String>();
	
	private java.util.Vector<String> pivoteLineWidthOptions = new java.util.Vector<String>();

	private void jbInit() {
		etchedBorder = BorderFactory.createEtchedBorder();
		emptyBorder = BorderFactory.createEmptyBorder(10,10,10,10);
		buttonBox = Box.createHorizontalBox();
		component2 = Box.createGlue();
		component1 = Box.createGlue();
		directoriesBox = Box.createVerticalBox();
		component7 = Box.createGlue();
		aseImporterBox = Box.createVerticalBox();
		component8 = Box.createHorizontalStrut(8);
		component9 = Box.createGlue();
		component6 = Box.createGlue();
		
		
		border1 = BorderFactory.createEmptyBorder(0,6,0,0);
		
		
		
		
		
		//// init General tab //////////////////////////////////////////
		component5 = Box.createGlue();
		generalPanel.setBorder(emptyBorder);
		generalPanel.setLayout(gridBagLayout4);
		inputDirectoriesPanel.setLayout(gridBagLayout1);
		worldDirectoryLabel.setText("save and load from:");
		worldDirectoryBrowseButton.setText("Browse...");
		worldDirectoryBrowseButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				worldDirectoryBrowseButton_actionPerformed(e);
			}
		});
		maxRecentWorldsPanel.setLayout(borderLayout8);
		maxRecentWorldsPanel.setAlignmentX((float) 0.0);
		maxRecentWorldsPanel.setMaximumSize(new Dimension(2147483647, 25));
		maxRecentWorldsPanel.setMinimumSize(new Dimension(0, 0));
		
		maxRecentWorldsTextField.setMaximumSize(new Dimension(4, 21));
		maxRecentWorldsTextField.setColumns(4);
		
		//General tab components
		maxRecentWorldsLabel.setText("maximum number of worlds kept in the recent worlds menu");
		maxRecentWorldsLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		//+++++++ end General tab components+++++++
		resourcesLabel.setText("display my program:");
		resourcesPanel.setLayout(gridBagLayout5);
		fontSizeLabel.setText(" general font size (default value is 12)");
		fontSizeComboBox.setEditable(true);
		fontSizeComboBox.setPreferredSize(new java.awt.Dimension(55, 25));
		fontSizeComboBox.setMaximumRowCount(9);
		
		pivotLineWidthLbl.setText(" Pivot Line Width");
		pivotLineWidthCB.setEditable(true);
		pivotLineWidthCB.setPreferredSize(new java.awt.Dimension(55, 25));
		pivotLineWidthCB.setMaximumRowCount(20);
		
		
		
		fontSizePanel.setOpaque(false);
		fontSizePanel.setBorder(null);
		fontSizePanel.add(fontSizeComboBox);
		fontSizePanel.add(fontSizeLabel);
		pivoteDecoratorPanel.add(pivotLineWidthCB);
		pivoteDecoratorPanel.add(pivotLineWidthLbl);
		//Added by Alberto Pareja-Lecaros
		//A quick hack to add colorblind mode to the application. The ActionEvent must still be filled.
		JPanel colorblindPanel = new JPanel();
		colorblindMode = new JCheckBox();
		colorblindMode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (arg0.getSource().equals(PreferencesContentPane.this) && colorblindMode.isSelected()) {
					System.out.println("Colorblind code clicked!");
					String newValue = "";
					if(colorblindMode.isSelected()){
						newValue = "1";
					}
					else{
						newValue = "0";
					}
					
					if(! Configuration.getValue(colorStatePackage, "colorBlindState").equals(newValue)){
						Configuration.setValue(colorStatePackage, "colorBlindState", newValue);
						restartRequired = true;
					}
						
					
				}
			}
			
		});
		addOKActionListener(colorblindMode.getActionListeners()[0]);
		
		String state = Configuration.getValue(colorStatePackage, "colorBlindState");
		if(state.equals("1")){
			colorblindMode.setSelected(true);
		}
		JLabel colorblindLabel = new JLabel("Colorblind Mode");
		colorblindPanel.add(colorblindMode);
		colorblindPanel.add(colorblindLabel);
		//colorblindPanel.add(colorblindPanel);
		inputDirectoriesPanel.add(worldDirectoryLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));

			inputDirectoriesPanel.add(worldDirectoryTextField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(4, 4, 4, 4), 0, 0));

			inputDirectoriesPanel.add(worldDirectoryBrowseButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
			
			
		resourceFileComboBox.setRenderer(new javax.swing.ListCellRenderer(){
			public java.awt.Component getListCellRendererComponent(javax.swing.JList list, Object value, int index, boolean isSelected, boolean cellHasFocus){
				javax.swing.JLabel toReturn = new javax.swing.JLabel("No Name");
				toReturn.setOpaque(true);


				String name = value.toString();
				if (name.equals("Alice Style.py")){
					name = "Alice Style";
				} else if (name.equals("Java Style.py")){
					name = "Java Style in Color";
				} else if (name.equals("Java Text Style.py")){
					name = "Java Style in Black & White";
				} else{
					int dotIndex = name.lastIndexOf(".");
					if (dotIndex > -1){
						name = name.substring(0, dotIndex);
					}
				}
				toReturn.setText(name);
				if (isSelected) {
					toReturn.setBackground(list.getSelectionBackground());
					toReturn.setForeground(list.getSelectionForeground());
				} else {
					toReturn.setBackground(list.getBackground());
					toReturn.setForeground(list.getForeground());
				}

				return toReturn;
			}
		});
		
		resourcesPanel.add(resourceFileComboBox,   new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			resourcesPanel.add(resourcesLabel,        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 2, 6), 0, 0));
		
		maxRecentWorldsPanel.add(maxRecentWorldsTextField, BorderLayout.WEST);
		maxRecentWorldsPanel.add(maxRecentWorldsLabel, BorderLayout.CENTER);
		
		
		generalPanel.add(maxRecentWorldsPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 0, 4, 0), 0, 0));

		generalPanel.add(resourcesPanel, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0
			,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		generalPanel.add(inputDirectoriesPanel, new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0
			,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(4, 0, 0, 0), 0, 0));
		generalPanel.add(fontSizePanel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		generalPanel.add(pivoteDecoratorPanel , new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		generalPanel.add(colorblindPanel, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		generalPanel.add(component5, new GridBagConstraints(0, 8, 1, 1, 1.0, 1.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		
		generalPanel.add(viStyleFontPanel, new GridBagConstraints(0, 9, 1, 1, 1.0, 1.0
				,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		
	  //// end init General tab +++++++++++++++++++++++++++
		
		
		
		
		
		
		
		
		
		
		
		
		
		seldomUsedPanel.setBorder(emptyBorder);
		seldomUsedPanel.setLayout(new GridBagLayout());
		renderingPanel.setLayout(gridBagLayout3);
		directoriesPanel.setLayout(borderLayout4);
		aseImporterPanel.setLayout(borderLayout5);
		renderingPanel.setBorder(emptyBorder);
		directoriesPanel.setBorder(emptyBorder);
		aseImporterPanel.setBorder(emptyBorder);
		configTabbedPane.setBackground(new Color(204, 204, 204));
		this.setLayout(borderLayout6);
		okayButton.setText("Okay");
		buttonPanel.setLayout(borderLayout7);
		buttonPanel.setBorder(emptyBorder);
		
		importDirectoryLabel.setText("import directory:");
		
		importDirectoryBrowseButton.setText("Browse...");
		importDirectoryBrowseButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importDirectoryBrowseButton_actionPerformed(e);
			}
		});
		createNormalsCheckBox.setText("create normals if none exist");
		createUVsCheckBox.setText("create uv coordinates if none exist");
		useSpecularCheckBox.setText("use specular information if given in ASE file");
		groupMultipleRootObjectsCheckBox.setText("group multiple root objects");
		colorToWhiteWhenTexturedCheckBox.setText("set ambient and diffuse color to white if object is textured");
		cancelButton.setText("Cancel");

		screenGrabPanel.setLayout(gridBagLayout2);
		captureDirectory.setText("directory to capture to:");
		browseButton.setText("Browse...");
		browseButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				browseButton_actionPerformed(e);
			}
		});
		screenGrabPanel.setBorder(emptyBorder);
		baseNameLabel.setText("base filename:");
		baseNameTextField.setMinimumSize(new Dimension(100, 28));
		baseNameTextField.setPreferredSize(new Dimension(100, 28));
		numDigitsLabel.setText("number of digits to append:");
		codecLabel.setText("image format:");
		usageLabel.setText("Note: use Ctrl-G to grab a frame while the world is running.");
		doProfilingCheckBox.setText("profile world");
		forceSoftwareRenderingCheckBox.setText("force software rendering (slower and safer)");
		showFPSCheckBox.setText("show frames per second");
		deleteFiles.setText("delete frames folder after exporting video"); // Aik Min added this.

		boundsXLabel.setHorizontalAlignment(SwingConstants.CENTER);
		boundsXLabel.setText("horizontal position:");
		boundsXLabel.setBounds(new Rectangle(16, 28, 113, 17));
		boundsXTextField.setColumns(5);
		boundsXTextField.setBounds(new Rectangle(130, 26, 60, 22));
		boundsXTextField.getDocument().addDocumentListener(renderDialogBoundsChecker);
		boundsWidthLabel.setToolTipText("");
		boundsWidthLabel.setHorizontalAlignment(SwingConstants.CENTER);
		boundsWidthLabel.setText("width:");
		boundsWidthLabel.setBounds(new Rectangle(90, 78, 39, 17));
		boundsYLabel.setBounds(new Rectangle(29, 53, 100, 17));
		boundsYLabel.setHorizontalAlignment(SwingConstants.CENTER);
		boundsYLabel.setText("vertical position:");
		boundsWidthTextField.setColumns(5);
		boundsWidthTextField.setBounds(new Rectangle(130, 76, 60, 22));
		boundsWidthTextField.getDocument().addDocumentListener(renderDialogWidthChecker);
		renderWindowBoundsLabel.setText("render window position and size:");
		renderWindowBoundsLabel.setBounds(new Rectangle(1, 1, 192, 24));
		boundsHeightLabel.setText("height:");
		boundsHeightLabel.setBounds(new Rectangle(88, 103, 43, 17));
		renderWindowBoundsPanel.setAlignmentX((float) 0.0);
		renderWindowBoundsPanel.setMaximumSize(new Dimension(32767, 125));
		renderWindowBoundsPanel.setPreferredSize(new Dimension(300, 125));
		renderWindowBoundsPanel.setLayout(null);
		boundsYTextField.setColumns(5);
		boundsYTextField.setBounds(new Rectangle(130, 51, 60, 22));
		boundsYTextField.getDocument().addDocumentListener(renderDialogBoundsChecker);
		boundsHeightTextField.setColumns(5);
		boundsHeightTextField.setBounds(new Rectangle(130, 101, 60, 22));
		boundsHeightTextField.getDocument().addDocumentListener(renderDialogHeightChecker);
		useBorderlessWindowCheckBox.setText("use a borderless render window");
		rendererListLabel.setText("renderer order (top item will be tried first, bottom item will be " +
	"tried last):");
		rendererList.setBorder(BorderFactory.createLineBorder(Color.black));
		rendererList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rendererMoveUpButton.setText("move up");
		rendererMoveUpButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rendererMoveUpButton_actionPerformed(e);
			}
		});
		rendererMoveDownButton.setText("move down");
		rendererMoveDownButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rendererMoveDownButton_actionPerformed(e);
			}
		});
		infiniteBackupsCheckBox.setText("save infinite number of backup scripts");
		
		
		
		
		
		
		
		
		runtimeScratchPadEnabledCheckBox.setText("show scratch pad when world runs");

		
		
		
		
		
		borderLayout8.setHgap(8);
		
		borderLayout2.setHgap(8);
		
		enableScriptingCheckBox.setToolTipText("");
		enableScriptingCheckBox.setActionCommand("enable jython scripting");
		enableScriptingCheckBox.setText("enable jython scripting");
		

		saveAsSingleFileCheckBox.setText("always save worlds as single files");
		
		
		watcherPanelEnabledCheckBox.setText("show variable watcher when world runs");
		
		constrainRenderDialogAspectCheckBox.setText("constrain render window\'s aspect ratio");
		constrainRenderDialogAspectCheckBox.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent ae){
				checkAndUpdateRenderBounds();
			}
		});
		ensureRenderDialogIsOnScreenCheckBox.setText("make sure the render window is always on the screen");
		
		screenCaptureInformUserCheckBox.setText("show information dialog when capture is made");
		captureDirectoryTextField.getDocument().addDocumentListener(captureDirectoryChangeListener);

		saveIntervalLabelEnd.setText(" number of minutes to wait before displaying save reminder");
		saveIntervalComboBox.setEditable(true);
		saveIntervalComboBox.setPreferredSize(new java.awt.Dimension(75, 25));
		
		
		backupCountLabel.setText(" number of backups of each world to save");
		backupCountComboBox.setEditable(true);
		backupCountComboBox.setPreferredSize(new java.awt.Dimension(75, 25));
		backupCountComboBox.setMaximumRowCount(9);
		
		
		
		
		
		
		
		
		
		
		this.add(configTabbedPane, BorderLayout.CENTER);
		
		configTabbedPane.add(generalPanel, "General");
		configTabbedPane.add(renderingPanel, "Rendering");

		configTabbedPane.add(screenGrabPanel, "Screen Grab");
//		TODO: config for bvw
//			  configTabbedPane.add(aseImporterPanel, "ASE Importer");
		configTabbedPane.add(seldomUsedPanel, "Seldom Used");
		
		renderingPanel.add(forceSoftwareRenderingCheckBox,  new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
		renderingPanel.add(showFPSCheckBox,  new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
		// Aik Min added this.
		renderingPanel.add(deleteFiles,  new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
			

		renderingPanel.add(renderWindowBoundsPanel,  new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(8, 4, 8, 0), 0, 0));
		renderWindowBoundsPanel.add(boundsWidthLabel, null);
		renderWindowBoundsPanel.add(renderWindowBoundsLabel, null);
		renderWindowBoundsPanel.add(boundsXLabel, null);
		renderWindowBoundsPanel.add(boundsXTextField, null);
		renderWindowBoundsPanel.add(boundsYTextField, null);
		renderWindowBoundsPanel.add(boundsYLabel, null);
		renderWindowBoundsPanel.add(boundsWidthTextField, null);
		renderWindowBoundsPanel.add(boundsHeightTextField, null);
		renderWindowBoundsPanel.add(boundsHeightLabel, null);
		renderingPanel.add(constrainRenderDialogAspectCheckBox,  new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
		renderingPanel.add(ensureRenderDialogIsOnScreenCheckBox,  new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
		renderingPanel.add(rendererListLabel,  new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(8, 4, 0, 0), 0, 0));
		renderingPanel.add(rendererList,  new GridBagConstraints(0, 7, 2, 1, 1.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 4, 0, 0), 0, 0));
		renderingPanel.add(rendererMoveUpButton,  new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 8, 0), 0, 0));
		renderingPanel.add(rendererMoveDownButton,  new GridBagConstraints(1, 8, 1, 1, 1.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 8, 0), 0, 0));
		renderingPanel.add(component6,  new GridBagConstraints(0, 10, 2, 1, 0.0, 1.0
			,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		

		directoriesBox.add(component7, null);
		
		screenGrabPanel.add(captureDirectory,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		screenGrabPanel.add(captureDirectoryTextField,  new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
			,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(4, 4, 4, 4), 0, 0));
		screenGrabPanel.add(browseButton,  new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
			,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		screenGrabPanel.add(component9,  new GridBagConstraints(0, 5, 3, 1, 1.0, 1.0
			,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		screenGrabPanel.add(baseNameLabel,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(4, 4, 0, 4), 0, 0));
		screenGrabPanel.add(baseNameTextField,  new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		screenGrabPanel.add(numDigitsLabel,  new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		screenGrabPanel.add(numDigitsComboBox,  new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		screenGrabPanel.add(codecLabel,  new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		screenGrabPanel.add(codecComboBox,  new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		screenGrabPanel.add(usageLabel,  new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0
			,GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
		screenGrabPanel.add(screenCaptureInformUserCheckBox,    new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		aseImporterPanel.add(aseImporterBox, BorderLayout.CENTER);
		aseImporterBox.add(createNormalsCheckBox, null);
		aseImporterBox.add(createUVsCheckBox, null);
		aseImporterBox.add(useSpecularCheckBox, null);
		aseImporterBox.add(groupMultipleRootObjectsCheckBox, null);
		aseImporterBox.add(colorToWhiteWhenTexturedCheckBox, null);
		this.add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.add(buttonBox, BorderLayout.CENTER);
		buttonBox.add(component1, null);
		buttonBox.add(okayButton, null);
		buttonBox.add(component8, null);
		buttonBox.add(cancelButton, null);
		buttonBox.add(component2, null);
		

		
		

		
		
		
		
		
		
		
		/////////////////////////// init seldom used tab
		numClipboardsPanel.setLayout(borderLayout2);
		showWorldStatsCheckBox.setText("show world statistics");
		pickUpTilesCheckBox.setText("pick up tiles while dragging and dropping (reduces performance)");
		useAlphaTilesCheckBox.setText("use alpha blending in picked up tiles (really reduces performance)");
		clearStdOutOnRunCheckBox.setText("clear text output on play");
		showStartUpDialogCheckBox.setText("show startup dialog when Alice launches");
		enableHighContrastCheckBox.setText("enable high contrast mode for projectors");
		showWebWarningCheckBox.setText("show warning when browsing the web gallery");
		loadSavedTabsCheckBox.setText("open tabs that were previously open on world load");
		saveThumbnailWithWorldCheckBox.setText("save thumbnail with world");
		saveIntervalPanel.setOpaque(false);
		saveIntervalPanel.setBorder(null);
		saveIntervalPanel.add(saveIntervalComboBox);
		saveIntervalPanel.add(saveIntervalLabelEnd);
		backupCountPanel.setOpaque(false);
		backupCountPanel.setBorder(null);
		backupCountPanel.add(backupCountComboBox);
		backupCountPanel.add(backupCountLabel);
		numClipboardsTextField.setColumns(4);
		numClipboardsLabel.setText("number of clipboards");
		numClipboardsLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		
		
		numClipboardsPanel.add(numClipboardsTextField, BorderLayout.WEST);
		numClipboardsPanel.add(numClipboardsLabel, BorderLayout.CENTER);
	
		{//seldome used tab GUI 
			Insets allZerooInsets = new Insets(0,0,0,0);
			seldomUsedPanel.add(showStartUpDialogCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.NONE,allZerooInsets, 0, 0));
			seldomUsedPanel.add(showWebWarningCheckBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.NONE, allZerooInsets, 0, 0));
			seldomUsedPanel.add(loadSavedTabsCheckBox, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.NONE, allZerooInsets, 0, 0));
			seldomUsedPanel.add(pickUpTilesCheckBox, new GridBagConstraints(0,3, 1, 1, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.NONE, allZerooInsets, 0, 0));
			seldomUsedPanel.add(useAlphaTilesCheckBox, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.NONE, allZerooInsets, 0, 0));
			seldomUsedPanel.add(saveThumbnailWithWorldCheckBox, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.NONE, allZerooInsets, 0, 0));
			seldomUsedPanel.add(showWorldStatsCheckBox, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, allZerooInsets, 0, 0));
	
			seldomUsedPanel.add(clearStdOutOnRunCheckBox, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.NONE, allZerooInsets, 0, 0));
			seldomUsedPanel.add(enableHighContrastCheckBox, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.NONE, allZerooInsets, 0, 0));
			seldomUsedPanel.add(numClipboardsPanel, new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 0, 4, 0), 0, 0));
			seldomUsedPanel.add(saveIntervalPanel, new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.NONE, allZerooInsets, 0, 0));
			seldomUsedPanel.add(backupCountPanel, new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0
				,GridBagConstraints.WEST, GridBagConstraints.NONE, allZerooInsets, 0, 0));
			seldomUsedPanel.add(javax.swing.Box.createVerticalGlue(), new GridBagConstraints(0, 15, 1, 1, 1.0, 1.0
				,GridBagConstraints.CENTER, GridBagConstraints.BOTH, allZerooInsets, 0, 0));
		}
	}
}