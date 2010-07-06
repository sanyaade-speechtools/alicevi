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

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyVetoException;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Vector;

import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.speech.synthesis.JSMLException;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.text.JTextComponent;

import movieMaker.SoundStorage;

import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyFile;

import edu.cmu.cs.stage3.alice.authoringtool.dialog.AboutContentPane;
import edu.cmu.cs.stage3.alice.authoringtool.dialog.Add3DTextPanel;
import edu.cmu.cs.stage3.alice.authoringtool.dialog.CaptureContentPane;
import edu.cmu.cs.stage3.alice.authoringtool.dialog.CapturedImageContentPane;
import edu.cmu.cs.stage3.alice.authoringtool.dialog.ErrorContentPane;
import edu.cmu.cs.stage3.alice.authoringtool.dialog.ExportCodeForPrintingContentPane;
import edu.cmu.cs.stage3.alice.authoringtool.dialog.LoadElementProgressPane;
import edu.cmu.cs.stage3.alice.authoringtool.dialog.NewVariableContentPane;
import edu.cmu.cs.stage3.alice.authoringtool.dialog.OutputComponent;
import edu.cmu.cs.stage3.alice.authoringtool.dialog.PreferencesContentPane;
import edu.cmu.cs.stage3.alice.authoringtool.dialog.RenderContentPane;
import edu.cmu.cs.stage3.alice.authoringtool.dialog.SaveForWebContentPane;
import edu.cmu.cs.stage3.alice.authoringtool.dialog.SimulationExceptionPanel;
import edu.cmu.cs.stage3.alice.authoringtool.dialog.SoundRecorder;
import edu.cmu.cs.stage3.alice.authoringtool.dialog.StartUpContentPane;
import edu.cmu.cs.stage3.alice.authoringtool.dialog.StdErrOutContentPane;
import edu.cmu.cs.stage3.alice.authoringtool.dialog.StoreElementProgressPane;
import edu.cmu.cs.stage3.alice.authoringtool.dialog.WorldInfoContentPane;
import edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.BehaviorGroupsEditor;
import edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.SceneEditor;
import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent;
import edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener;
import edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject;
import edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryViewer;
import edu.cmu.cs.stage3.alice.authoringtool.util.CollectionEditorPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.alice.authoringtool.util.DefaultScheduler;
import edu.cmu.cs.stage3.alice.authoringtool.util.DnDClipboard;
import edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.EditorUtilities;
import edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype;
import edu.cmu.cs.stage3.alice.authoringtool.util.ExpandablePanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionFileFilter;
import edu.cmu.cs.stage3.alice.authoringtool.util.ExtensionGroupFileFilter;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIEffects;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.GuiNavigator;
import edu.cmu.cs.stage3.alice.authoringtool.util.ImagePanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.OneShotScheduler;
import edu.cmu.cs.stage3.alice.authoringtool.util.OneShotSimpleBehavior;
import edu.cmu.cs.stage3.alice.authoringtool.util.OneShotUndoableRedoable;
import edu.cmu.cs.stage3.alice.authoringtool.util.PointOfViewUndoableRedoable;
import edu.cmu.cs.stage3.alice.authoringtool.util.PostImportRunnable;
import edu.cmu.cs.stage3.alice.authoringtool.util.RectangleAnimator;
import edu.cmu.cs.stage3.alice.authoringtool.util.StencilStateCapsule;
import edu.cmu.cs.stage3.alice.authoringtool.util.TrashComponent;
import edu.cmu.cs.stage3.alice.authoringtool.util.WatcherPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.WorldDifferencesCapsule;
import edu.cmu.cs.stage3.alice.authoringtool.util.WorldTreeModel;
import edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent;
import edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener;
import edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.RenderTarget;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.clock.DefaultClock;
import edu.cmu.cs.stage3.alice.core.event.ChildrenEvent;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion;
import edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion;
import edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse;
import edu.cmu.cs.stage3.alice.core.response.CompositeResponse;
import edu.cmu.cs.stage3.alice.core.response.DoInOrder;
import edu.cmu.cs.stage3.alice.core.response.DoTogether;
import edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation;
import edu.cmu.cs.stage3.alice.core.response.PropertyAnimation;
import edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse;
import edu.cmu.cs.stage3.alice.core.response.Wait;
import edu.cmu.cs.stage3.alice.core.util.WorldListener;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.DefaultRenderTargetFactory;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.OffscreenRenderTarget;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTargetFactory;
import edu.cmu.cs.stage3.alice.scripting.ScriptingFactory;
import edu.cmu.cs.stage3.caitlin.stencilhelp.application.StencilApplication;
import edu.cmu.cs.stage3.caitlin.stencilhelp.client.StencilManager;
import edu.cmu.cs.stage3.scheduler.AbstractScheduler;
import edu.cmu.cs.stage3.scheduler.Scheduler;
import edu.cmu.cs.stage3.scheduler.SchedulerThread;
import edu.cmu.cs.stage3.swing.DialogManager;
import edu.cmu.cs.stage3.util.Criterion;
import edu.cmu.cs.stage3.util.HowMuch;

public class AuthoringTool implements ClipboardOwner, StencilApplication {
	// file extensions
	public static final String CHARACTER_EXTENSION = "a2c";
	public static final String WORLD_EXTENSION = "a2w";

	// python standard out/err
	private static PyFile pyStdOut;
	private static PyFile pyStdErr;

	// core components
	private RenderTargetFactory renderTargetFactory;

	private World world;
	private File defaultWorld;
	private JAliceFrame jAliceFrame;
	private EditorManager editorManager;

	private DefaultScheduler scheduler;
	private OneShotScheduler oneShotScheduler;

	private Runnable worldScheduleRunnable;
	private DefaultClock worldClock;

	private MainUndoRedoStack undoRedoStack;
	private Actions actions;
	private Importing importing;
	public OutputComponent stdOutOutputComponent;
	public OutputComponent stdErrOutputComponent;
	private WatcherPanel watcherPanel;

	private JFileChooser importFileChooser;
	private JFileChooser addCharacterFileChooser;
	private JFileChooser browseFileChooser;

	private JFileChooser saveWorldFileChooser;
	private FileDialog saveCharacterFileDialog;

	private LoadElementProgressPane worldLoadProgressPane;
	private StoreElementProgressPane worldStoreProgressPane;
	private LoadElementProgressPane characterLoadProgressPane;
	private StoreElementProgressPane characterStoreProgressPane;

	private PreferencesContentPane preferencesContentPane;
	private AboutContentPane aboutContentPane;
	private WorldInfoContentPane worldInfoContentPane;
	public StdErrOutContentPane stdErrOutContentPane;
	private ExportCodeForPrintingContentPane exportCodeForPrintingContentPane;
	private StartUpContentPane startUpContentPane;
	private SaveForWebContentPane saveForWebContentPane;
	private NewVariableContentPane newVariableContentPane;

	private RenderContentPane renderContentPane;
	private CaptureContentPane captureContentPane;
	private JPanel renderPanel;

	// file filters
	private FileFilter worldFileFilter;
	private FileFilter characterFileFilter;

	// misc
	private File currentWorldLocation;
	private boolean worldHasBeenModified = false;
	private long lastSaveTime;
	private SoundStorage soundStorage = null;
	private File worldDirectory; // only needed for saving backup files
	private HashMap extensionStringsToFileFilterMap;
	private Configuration authoringToolConfig;
	private RenderTarget renderTarget;
	private ScriptingFactory scriptingFactory;

	private WindowListener jAliceFrameWindowListener;

	private boolean saveTabsEnabled = false;
	private long worldLoadedTime;
	private double speedMultiplier = 1.0;

	private RectangleAnimator rectangleAnimator;

	private boolean stdOutToConsole;
	private boolean stdErrToConsole;
	public int numEncoded = 0;
	// Madeleine added

	private WorldListener userDefinedParameterListener = new WorldListener() {
		private Object m_previousPropertyValue = null;

		private CallToUserDefinedResponse[] getCallsTo(
				final UserDefinedResponse userDefined) {
			Vector v = new Vector();
			this.getWorld().internalSearch(new Criterion() {
				public boolean accept(Object o) {
					if (o instanceof CallToUserDefinedResponse) {
						CallToUserDefinedResponse call = (CallToUserDefinedResponse) o;
						if (call.userDefinedResponse
								.getUserDefinedResponseValue() == userDefined) {
							return true;
						}
					}
					return false;
				}
			}, HowMuch.INSTANCE_AND_ALL_DESCENDANTS, v);
			CallToUserDefinedResponse[] calls = new CallToUserDefinedResponse[v
			                                                                  .size()];
			v.copyInto(calls);
			return calls;
		}

		private CallToUserDefinedQuestion[] getCallsTo(
				final UserDefinedQuestion userDefined) {
			Vector v = new Vector();
			this.getWorld().internalSearch(new Criterion() {
				public boolean accept(Object o) {
					if (o instanceof CallToUserDefinedQuestion) {
						CallToUserDefinedQuestion call = (CallToUserDefinedQuestion) o;
						if (call.userDefinedQuestion
								.getUserDefinedQuestionValue() == userDefined) {
							return true;
						}
					}
					return false;
				}
			}, HowMuch.INSTANCE_AND_ALL_DESCENDANTS, v);
			CallToUserDefinedQuestion[] calls = new CallToUserDefinedQuestion[v
			                                                                  .size()];
			v.copyInto(calls);
			return calls;
		}

		protected void handleChildrenChanging(ChildrenEvent e) {
		}

		protected void handleChildrenChanged(ChildrenEvent e) {
		}

		protected void handlePropertyChanging(PropertyEvent e) {
			m_previousPropertyValue = e.getProperty().get();
		}

		protected void handlePropertyChanged(PropertyEvent e) {
			Property property = e.getProperty();
			Element owner = property.getOwner();
			if (owner instanceof Variable) {
				Variable variable = (Variable) owner;
				if (property.getName().equals("name")) {
					Element parent = variable.getParent();
					if (parent instanceof UserDefinedResponse) {
						UserDefinedResponse userDefined = (UserDefinedResponse) parent;
						CallToUserDefinedResponse[] calls = getCallsTo(userDefined);
						for (int i = 0; i < calls.length; i++) {
							for (int j = 0; j < calls[i].requiredActualParameters
							.size(); j++) {
								Variable actualParameterJ = (Variable) calls[i].requiredActualParameters
								.get(j);
								String nameJ = actualParameterJ.name
								.getStringValue();
								if (nameJ != null
										&& nameJ
										.equals(m_previousPropertyValue)) {
									actualParameterJ.name.set(e.getValue());
								}
							}
						}
					} else if (parent instanceof UserDefinedQuestion) {
						UserDefinedQuestion userDefined = (UserDefinedQuestion) parent;
						CallToUserDefinedQuestion[] calls = getCallsTo(userDefined);
						for (int i = 0; i < calls.length; i++) {
							for (int j = 0; j < calls[i].requiredActualParameters
							.size(); j++) {
								Variable actualParameterJ = (Variable) calls[i].requiredActualParameters
								.get(j);
								String nameJ = actualParameterJ.name
								.getStringValue();
								if (nameJ != null
										&& nameJ
										.equals(m_previousPropertyValue)) {
									actualParameterJ.name.set(e.getValue());
								}
							}
						}
					}
				}
			}
		}

		protected void handleObjectArrayPropertyChanging(
				ObjectArrayPropertyEvent e) {
		}

		protected void handleObjectArrayPropertyChanged(
				ObjectArrayPropertyEvent e) {
			ObjectArrayProperty oap = e.getObjectArrayProperty();
			Element owner = oap.getOwner();
			if (owner instanceof UserDefinedResponse) {
				UserDefinedResponse userDefined = (UserDefinedResponse) owner;
				if (oap.getName().equals("requiredFormalParameters")) {
					Object item = e.getItem();
					if (item instanceof Variable) {
						Variable formalParameter = (Variable) item;
						String formalParameterName = formalParameter.name
						.getStringValue();
						CallToUserDefinedResponse[] calls = getCallsTo(userDefined);
						switch (e.getChangeType()) {
						case ObjectArrayPropertyEvent.ITEM_INSERTED:
							for (int i = 0; i < calls.length; i++) {
								Variable actualParameter = new Variable();
								actualParameter.name.set(formalParameter.name
										.get());
								Class cls = formalParameter.valueClass
								.getClassValue();
								actualParameter.valueClass.set(cls);
								actualParameter.value
								.set(AuthoringToolResources
										.getDefaultValueForClass(cls));
								boolean tempListening = AuthoringTool.this
								.getUndoRedoStack().getIsListening();
								AuthoringTool.this.getUndoRedoStack()
								.setIsListening(false);
								calls[i].addChild(actualParameter);
								calls[i].requiredActualParameters
								.add(actualParameter);
								AuthoringTool.this.getUndoRedoStack()
								.setIsListening(tempListening);
							}
							break;
						case ObjectArrayPropertyEvent.ITEM_REMOVED:
							for (int i = 0; i < calls.length; i++) {
								for (int j = 0; j < calls[i].requiredActualParameters
								.size(); j++) {
									Variable actualParameterJ = (Variable) calls[i].requiredActualParameters
									.get(j);
									String nameJ = actualParameterJ.name
									.getStringValue();
									if (nameJ != null
											&& nameJ
											.equals(formalParameterName)) {
										boolean tempListening = AuthoringTool.this
										.getUndoRedoStack()
										.getIsListening();
										AuthoringTool.this.getUndoRedoStack()
										.setIsListening(false);
										actualParameterJ.removeFromParent();
										AuthoringTool.this.getUndoRedoStack()
										.setIsListening(tempListening);
									}
								}
							}
							break;
						case ObjectArrayPropertyEvent.ITEM_SHIFTED:
							for (int i = 0; i < calls.length; i++) {
								calls[i].requiredActualParameters.shift(e
										.getOldIndex(), e.getNewIndex());
							}
							break;
						}

					}
				}
			} else if (owner instanceof UserDefinedQuestion) {
				UserDefinedQuestion userDefined = (UserDefinedQuestion) owner;
				if (oap.getName().equals("requiredFormalParameters")) {
					Object item = e.getItem();
					if (item instanceof Variable) {
						Variable formalParameter = (Variable) item;
						String formalParameterName = formalParameter.name
						.getStringValue();
						CallToUserDefinedQuestion[] calls = getCallsTo(userDefined);
						switch (e.getChangeType()) {
						case ObjectArrayPropertyEvent.ITEM_INSERTED:
							for (int i = 0; i < calls.length; i++) {
								Variable actualParameter = new Variable();
								actualParameter.name.set(formalParameter.name
										.get());
								Class cls = formalParameter.valueClass
								.getClassValue();
								actualParameter.valueClass.set(cls);
								actualParameter.value
								.set(AuthoringToolResources
										.getDefaultValueForClass(cls));
								calls[i].addChild(actualParameter);
								calls[i].requiredActualParameters
								.add(actualParameter);
							}
							break;
						case ObjectArrayPropertyEvent.ITEM_REMOVED:
							for (int i = 0; i < calls.length; i++) {
								for (int j = 0; j < calls[i].requiredActualParameters
								.size(); j++) {
									Variable actualParameterJ = (Variable) calls[i].requiredActualParameters
									.get(j);
									String nameJ = actualParameterJ.name
									.getStringValue();
									if (nameJ != null
											&& nameJ
											.equals(formalParameterName)) {
										actualParameterJ.removeFromParent();
									}
								}
							}
							break;
						case ObjectArrayPropertyEvent.ITEM_SHIFTED:
							for (int i = 0; i < calls.length; i++) {
								calls[i].requiredActualParameters.shift(e
										.getOldIndex(), e.getNewIndex());
							}
							break;
						}
					}
				}
			}
		}

		protected boolean isPropertyListeningRequired(Property property) {
			return true;
		}

		protected boolean isObjectArrayPropertyListeningRequired(
				ObjectArrayProperty oap) {
			return true;
		}
	};

	// selected element
	private Element selectedElement;
	private HashSet selectionListeners = new HashSet();

	// AuthoringTool state listening
	private HashSet stateListeners = new HashSet();

	public static PyFile getPyStdOut() {
		return pyStdOut;
	}

	public static PyFile getPyStdErr() {
		return pyStdErr;
	}

	private static AuthoringTool hack;

	public static AuthoringTool getHack() {
		return hack;
	}

	// constructor
	public AuthoringTool(File defaultWorld, File worldToLoad,
			boolean stdOutToConsole, boolean stdErrToConsole) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

			class CustomButtonBorder extends javax.swing.border.AbstractBorder
			implements javax.swing.plaf.UIResource {
				protected Insets insets = new Insets(3, 3, 3, 3);
				protected javax.swing.border.Border line = javax.swing.BorderFactory
				.createLineBorder(java.awt.Color.black, 1);
				protected javax.swing.border.Border spacer = javax.swing.BorderFactory
				.createEmptyBorder(2, 4, 2, 4);
				protected javax.swing.border.Border raisedBevel = javax.swing.BorderFactory
				.createBevelBorder(javax.swing.border.BevelBorder.RAISED);
				protected javax.swing.border.Border loweredBevel = javax.swing.BorderFactory
				.createBevelBorder(javax.swing.border.BevelBorder.LOWERED);
				protected javax.swing.border.Border raisedBorder = javax.swing.BorderFactory
				.createCompoundBorder(javax.swing.BorderFactory
						.createCompoundBorder(line, raisedBevel),
						spacer);
				protected javax.swing.border.Border loweredBorder = javax.swing.BorderFactory
				.createCompoundBorder(javax.swing.BorderFactory
						.createCompoundBorder(line, loweredBevel),
						spacer);

				public void paintBorder(Component c, java.awt.Graphics g,
						int x, int y, int w, int h) {
					javax.swing.JButton button = (javax.swing.JButton) c;
					javax.swing.ButtonModel model = button.getModel();

					if (model.isEnabled()) {
						if (model.isPressed() && model.isArmed()) {
							loweredBorder.paintBorder(button, g, x, y, w, h);
						} else {
							raisedBorder.paintBorder(button, g, x, y, w, h);
						}
					} else {
						raisedBorder.paintBorder(button, g, x, y, w, h);
					}
				}

				public Insets getBorderInsets(Component c) {
					return insets;
				}
			}
			UIManager.put("Button.border",
					new BorderUIResource.CompoundBorderUIResource(
							new CustomButtonBorder(),
							new BasicBorders.MarginBorder()));

			UIManager.put("Label.font", new Font("SansSerif", Font.BOLD, 12));
			UIManager.put("Label.foreground", AuthoringToolResources
					.getColor("mainFontColor"));
			UIManager.put("TabbedPane.selected", new java.awt.Color(255, 255,
					255, 0));
			UIManager.put("TabbedPane.tabInsets", new Insets(1, 4, 1, 3));

			if ((System.getProperty("os.name") != null)
					&& System.getProperty("os.name").startsWith("Windows")) {
				UIManager.put("FileChooserUI",
				"com.sun.java.swing.plaf.windows.WindowsFileChooserUI");
			}
		} catch (Exception e) {
			showErrorDialog("Error configuring Look and Feel.", e);
		}

		AuthoringTool.hack = this;
		this.defaultWorld = defaultWorld;
		if (!(defaultWorld.exists() && defaultWorld.canRead())) {
			this.defaultWorld = null;
			DialogManager
			.showMessageDialog(
					defaultWorld.getAbsolutePath()
					+ " does not exist or cannot be read!  No starting world will be available.",
					"Warning", JOptionPane.WARNING_MESSAGE);
		}

		filterInit();
		configInit();
		try {
			int fontSize = Integer.parseInt(authoringToolConfig
					.getValue("fontSize"));
			UIManager.put("Label.font", new Font("SansSerif", Font.BOLD,
					fontSize));
			UIManager.put("Button.font", new Font("SansSerif", Font.BOLD,
					fontSize));
			UIManager.put("Checkbox.font", new Font("SansSerif", Font.BOLD,
					fontSize));
			UIManager.put("ColorChooser.font", new Font("SansSerif", Font.BOLD,
					fontSize));
			UIManager.put("ComboBox.font", new Font("SansSerif", Font.BOLD,
					fontSize));
			UIManager.put("EditorPane.font", new Font("SansSerif", Font.BOLD,
					fontSize));
			UIManager.put("Menu.font", new Font("SansSerif", Font.BOLD,
					fontSize));
			UIManager.put("List.font", new Font("SansSerif", Font.BOLD,
					fontSize));
			UIManager.put("MenuBar.font", new Font("SansSerif", Font.BOLD,
					fontSize));
			UIManager.put("MenuItem.font", new Font("SansSerif", Font.BOLD,
					fontSize));
			UIManager.put("OptionPane.font", new Font("SansSerif", Font.BOLD,
					fontSize));
			UIManager.put("Panel.font", new Font("SansSerif", Font.BOLD,
					fontSize));
			UIManager.put("PasswordField.font", new Font("SansSerif",
					Font.BOLD, fontSize));
			UIManager.put("PopupMenu.font", new Font("SansSerif", Font.BOLD,
					fontSize));
			UIManager.put("ProgressBar.font", new Font("SansSerif", Font.BOLD,
					fontSize));
			UIManager.put("RadioButton.font", new Font("SansSerif", Font.BOLD,
					fontSize));
			UIManager.put("ScrollPane.font", new Font("SansSerif", Font.BOLD,
					fontSize));
			UIManager.put("Table.font", new Font("SansSerif", Font.BOLD,
					fontSize));
			UIManager.put("TableHeader.font", new Font("SansSerif", Font.BOLD,
					fontSize));
			UIManager.put("Text.font", new Font("SansSerif", Font.BOLD,
					fontSize));
			UIManager.put("TextArea.font", new Font("SansSerif", Font.BOLD,
					fontSize));
			UIManager.put("TextField.font", new Font("SansSerif", Font.BOLD,
					fontSize));
			UIManager.put("TextPane.font", new Font("SansSerif", Font.BOLD,
					fontSize));
			UIManager.put("TitledBorder.font", new Font("SansSerif", Font.BOLD,
					fontSize));
			UIManager.put("ToggleButton.font", new Font("SansSerif", Font.BOLD,
					fontSize));
			UIManager.put("ToolBar.font", new Font("SansSerif", Font.BOLD,
					fontSize));
			UIManager.put("ToolTip.font", new Font("SansSerif", Font.BOLD,
					fontSize));
			UIManager.put("Tree.font", new Font("SansSerif", Font.BOLD,
					fontSize));

			if (authoringToolConfig.getValue("enableHighContrastMode")
					.equalsIgnoreCase("true")) {
				UIManager.put("Label.foreground", java.awt.Color.black);
			}
		} catch (Exception e) {
		}
		mainInit();
		this.stdOutToConsole = stdOutToConsole;
		this.stdErrToConsole = stdErrToConsole;
		initializeOutput(stdOutToConsole, stdErrToConsole);
		pyInit();
		dialogInit();
		undoRedoInit();
		miscInit();
		importInit();
		worldInit(worldToLoad);
		stencilInit();

		Scheduler s = new AbstractScheduler() {
			protected void handleCaughtThowable(Runnable source, Throwable t) {
				markEachFrameRunnableForRemoval(source);
				showErrorDialog(source.toString(), t);
			}
		};
		s.addEachFrameRunnable(scheduler);
		s.addEachFrameRunnable(oneShotScheduler);

		SchedulerThread schedulerThread = new SchedulerThread(s);
		schedulerThread.start();

		jAliceFrame.setVisible(true);
		if (worldToLoad == null) {
			if (authoringToolConfig.getValue("showStartUpDialog")
					.equalsIgnoreCase("true")) {
				showStartUpDialog(StartUpContentPane.DO_NOT_CHANGE_TAB_ID);
			}
		}
	}

	private void filterInit() {
		worldFileFilter = new ExtensionFileFilter(WORLD_EXTENSION,
				WORLD_EXTENSION.toUpperCase() + " (Alice World Files)");
		characterFileFilter = new ExtensionFileFilter(CHARACTER_EXTENSION,
				CHARACTER_EXTENSION.toUpperCase() + " (Alice Object Files)");
	}

	private void mainInit() {
		editorManager = new EditorManager(this);
		scheduler = new DefaultScheduler();
		undoRedoStack = new MainUndoRedoStack(this);
		oneShotScheduler = new OneShotScheduler();
		jAliceFrame = new JAliceFrame(this);
		actions = new Actions(this, jAliceFrame);
		jAliceFrame.actionInit(actions);
		DialogManager.initialize(jAliceFrame);
		importing = new Importing();
		stdOutOutputComponent = new OutputComponent();
		stdErrOutputComponent = new OutputComponent();
		watcherPanel = new WatcherPanel();
	}

	private void pyInit() {
		scriptingFactory = new edu.cmu.cs.stage3.alice.scripting.jython.ScriptingFactory();
		scriptingFactory.setStdOut(System.out);
		scriptingFactory.setStdErr(System.err);
	}

	private void worldsDirectoryChanged() {
		String worldsDirPath = authoringToolConfig
		.getValue("directories.worldsDirectory");
		if (worldsDirPath != null) {
			File worldsDir = new File(worldsDirPath);
			if (worldsDir != null && worldsDir.exists()
					&& worldsDir.isDirectory()) {
				try {
					saveWorldFileChooser.setCurrentDirectory(worldsDir);
				} catch (IndexOutOfBoundsException ioobe) {
					// for some reason this can potentially fail in jdk1.4.2_04
				}
			} else {
				// TODO: ?
			}
		} else {
			// TODO: what to do when the directory is null?
		}
	}

	private void importDirectoryChanged() {
		String importDirPath = authoringToolConfig
		.getValue("directories.importDirectory");
		if (importDirPath != null) {
			File importDir = new File(importDirPath);
			if (importDir != null && importDir.exists()
					&& importDir.isDirectory()) {
				try {
					importFileChooser.setCurrentDirectory(importDir);
				} catch (IndexOutOfBoundsException aioobe) {
					// for some reason this can potentially fail in jdk1.4.2_04
				}
			} else {
				// TODO: ?
			}
		} else {
			// TODO: what to do when the directory is null?
		}
	}

	private void charactersDirectoryChanged() {
		String charactersDirPath = authoringToolConfig
		.getValue("directories.charactersDirectory");
		if (charactersDirPath != null) {
			File charactersDir = new File(charactersDirPath);
			if (charactersDir != null && charactersDir.exists()
					&& charactersDir.isDirectory()) {
				try {
					addCharacterFileChooser.setCurrentDirectory(charactersDir);
					saveCharacterFileDialog.setDirectory(charactersDir
							.getAbsolutePath());
				} catch (IndexOutOfBoundsException aioobe) {
					// for some reason this can potentially fail in jdk1.4.2_04
				}
			} else {
				// TODO: ?
			}
		} else {
			// TODO: what to do when the directory is null?
		}
	}

	private void configInit() {
		authoringToolConfig = Configuration
		.getLocalConfiguration(AuthoringTool.class.getPackage());
		Configuration.addConfigurationListener(new ConfigurationListener() {
			public void changing(ConfigurationEvent ev) {
			}

			public void changed(ConfigurationEvent ev) {
				if (ev
						.getKeyName()
						.equals(
								"edu.cmu.cs.stage3.alice.authoringtool.recentWorlds.maxWorlds")) {
					AuthoringTool.this.jAliceFrame.updateRecentWorlds();
				} else if (ev
						.getKeyName()
						.equals(
						"edu.cmu.cs.stage3.alice.authoringtool.numberOfClipboards")) {
					AuthoringTool.this.jAliceFrame.updateClipboards();
				} else if (ev.getKeyName().equals(
				"edu.cmu.cs.stage3.alice.authoringtool.showWorldStats")) {
					AuthoringTool.this.jAliceFrame.showStatusPanel(ev
							.getNewValue().equalsIgnoreCase("true"));
				} else if (ev
						.getKeyName()
						.equals(
						"edu.cmu.cs.stage3.alice.authoringtool.directories.worldsDirectory")) {
					AuthoringTool.this.worldsDirectoryChanged();
				} else if (ev
						.getKeyName()
						.equals(
						"edu.cmu.cs.stage3.alice.authoringtool.directories.importDirectory")) {
					AuthoringTool.this.importDirectoryChanged();
				} else if (ev
						.getKeyName()
						.equals(
						"edu.cmu.cs.stage3.alice.authoringtool.directories.charactersDirectory")) {
					AuthoringTool.this.charactersDirectoryChanged();
				}
			}
		});
	}

	private void dialogInit() {
		importFileChooser = new JFileChooser();
		saveWorldFileChooser = new JFileChooser() {
			public void approveSelection() {
				File desiredFile = getSelectedFile();
				if (currentWorldLocation == null
						|| currentWorldLocation.equals(desiredFile)
						|| !desiredFile.exists()) {
					if (shouldAllowOverwrite(desiredFile)) {
						super.approveSelection();
					} else {
						DialogManager
						.showMessageDialog("That is protected Alice file and you can not overwrite it. Please choose another file.");
					}
				} else if (desiredFile.exists()) {
					if (shouldAllowOverwrite(desiredFile)) {
						int n = DialogManager
						.showConfirmDialog(
								"You are about to save over an existing file. Are you sure you want to?",
								"Save Over Warning",
								JOptionPane.YES_NO_OPTION);
						if (n == JOptionPane.YES_OPTION) {
							super.approveSelection();
						}
					} else {
						DialogManager
						.showMessageDialog("That is protected Alice file and you can not overwrite it. Please choose another file.");
					}

				}
			}
		};
		addCharacterFileChooser = new JFileChooser();

		browseFileChooser = new JFileChooser();

		saveCharacterFileDialog = new FileDialog(jAliceFrame, "Save Object",
				FileDialog.SAVE);

		FilenameFilter worldFilenameFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith("." + WORLD_EXTENSION);
			}
		};
		FilenameFilter characterFilenameFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith("." + CHARACTER_EXTENSION);
			}
		};
		saveCharacterFileDialog.setFilenameFilter(characterFilenameFilter);

		worldLoadProgressPane = new LoadElementProgressPane("Loading World...",
		"Loading: ");
		worldStoreProgressPane = new StoreElementProgressPane(
				"Saving World...", "Saving: ");
		characterLoadProgressPane = new LoadElementProgressPane(
				"Loading Object...", "Loading: ");
		characterStoreProgressPane = new StoreElementProgressPane(
				"Saving Object...", "Saving: ");

		preferencesContentPane = new PreferencesContentPane();
		preferencesContentPane.setAuthoringTool(this);
		captureContentPane = new CaptureContentPane(this);
		renderContentPane = new RenderContentPane(this);
		renderPanel = new JPanel();

		aboutContentPane = new AboutContentPane();

		renderPanel.setLayout(new java.awt.BorderLayout());

		importFileChooser.setApproveButtonText("Import");
		importFileChooser.setDialogTitle("Import...");
		importFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		importFileChooser.setPreferredSize(new Dimension(615, 402));

		saveWorldFileChooser.setApproveButtonText("Save World As");
		saveWorldFileChooser.setDialogTitle("Save World As...");
		saveWorldFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		saveWorldFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		saveWorldFileChooser.setFileFilter(worldFileFilter);
		saveWorldFileChooser.setPreferredSize(new Dimension(615, 402));

		addCharacterFileChooser.setApproveButtonText("Add Object");
		addCharacterFileChooser.setDialogTitle("Add Object...");
		addCharacterFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		addCharacterFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		addCharacterFileChooser.setFileFilter(characterFileFilter);
		addCharacterFileChooser.setPreferredSize(new Dimension(500, 300));

		saveCharacterFileDialog.setMode(FileDialog.SAVE);

		worldsDirectoryChanged();
		importDirectoryChanged();
		charactersDirectoryChanged();

		worldInfoContentPane = new WorldInfoContentPane();

		stdErrOutContentPane = new StdErrOutContentPane(this);

		exportCodeForPrintingContentPane = new ExportCodeForPrintingContentPane(
				this);

		saveForWebContentPane = new SaveForWebContentPane(this);

		newVariableContentPane = new NewVariableContentPane();

		startUpContentPane = new StartUpContentPane(this);
	}

	private void undoRedoInit() {
		addAuthoringToolStateListener(undoRedoStack);

		undoRedoStack
		.addUndoRedoListener(new edu.cmu.cs.stage3.alice.authoringtool.event.UndoRedoListener() {
			public void onChange() {
				int currentIndex = AuthoringTool.this.undoRedoStack
				.getCurrentUndoableRedoableIndex();
				if (currentIndex == -1) {
					AuthoringTool.this.actions.undoAction
					.setEnabled(false);
				} else {
					AuthoringTool.this.actions.undoAction
					.setEnabled(true);
				}

				if (currentIndex == (AuthoringTool.this.undoRedoStack
						.size() - 1)) {
					AuthoringTool.this.actions.redoAction
					.setEnabled(false);
				} else {
					AuthoringTool.this.actions.redoAction
					.setEnabled(true);
				}

				AuthoringTool.this.worldHasBeenModified = (currentIndex != AuthoringTool.this.undoRedoStack
						.getUnmodifiedIndex())
						|| AuthoringTool.this.undoRedoStack
						.isScriptDirty();
				AuthoringTool.this.updateTitle();
			}
		});
	}

	private void miscInit() {
		extensionStringsToFileFilterMap = new HashMap();
		scheduler.addEachFrameRunnable(oneShotScheduler);

		// try to quit on window close
		jAliceFrameWindowListener = new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				AuthoringTool.this.quit();
			}
		};
		jAliceFrame.addWindowListener(jAliceFrameWindowListener);

		worldClock = new DefaultClock();

		worldScheduleRunnable = new Runnable() {
			public void run() {
				try {
					worldClock.schedule();
				} catch (Throwable t) {
					stopWorldAndShowDialog(t);
				}
			}
		};

		// for running the world
		// worldScheduleRunnable = new Runnable() {
		// public void run() {
		// try {
		// AuthoringTool.this.world.schedule(
		// AuthoringToolResources.getCurrentTime() -
		// AuthoringTool.this.timeDifferential );
		// } catch( final edu.cmu.cs.stage3.alice.core.SimulationException e ) {
		// javax.swing.SwingUtilities.invokeLater( new Runnable() {
		// public void run() {
		// AuthoringTool.showRuntimeErrorDialog( e, AuthoringTool.this );
		// }
		// } );
		// } catch( final edu.cmu.cs.stage3.alice.core.ExceptionWrapper e ) {
		// javax.swing.SwingUtilities.invokeLater( new Runnable() {
		// public void run() {
		// Exception wrappedException = e.getWrappedException();
		// if( wrappedException instanceof
		// edu.cmu.cs.stage3.alice.core.SimulationException ) {
		// AuthoringTool.showRuntimeErrorDialog(
		// (edu.cmu.cs.stage3.alice.core.SimulationException)wrappedException,
		// AuthoringTool.this );
		// } else {
		// AuthoringTool.showErrorDialog( "Error during simulation.",
		// wrappedException );
		// }
		// }
		// } );
		// } catch( final PyException e ) {
		// javax.swing.SwingUtilities.invokeLater( new Runnable() {
		// public void run() {
		// if( Py.matchException( e,
		// Py.SystemExit ) ) {
		// //just quit
		// } else {
		// AuthoringTool.showErrorDialog( "Jython error during world run.", e,
		// false );
		// }
		// }
		// } );
		// } catch( final Throwable t ) {
		// javax.swing.SwingUtilities.invokeLater( new Runnable() {
		// public void run() {
		// // renderWindowListener.windowClosing( null ); // somewhat hackish
		// AuthoringTool.showErrorDialog( "Error during simulation.", t );
		// }
		// } );
		// }
		// }
		// };

		// track framerate
		javax.swing.Timer fpsTimer = new javax.swing.Timer(500,
				new java.awt.event.ActionListener() {
			java.text.DecimalFormat formater = new java.text.DecimalFormat(
					"#0.00");

			public void actionPerformed(ActionEvent ev) {
				String fps = formater
				.format(AuthoringTool.this.scheduler
						.getSimulationFPS())
						+ " fps";
				if (authoringToolConfig.getValue("rendering.showFPS")
						.equalsIgnoreCase("true")) {
					AuthoringTool.this.renderContentPane
					.setTitle("World Running...  " + fps);
				}
			}
		});
		fpsTimer.start();

		// prompt to save
		lastSaveTime = System.currentTimeMillis();
		javax.swing.Timer promptToSaveTimer = new javax.swing.Timer(60000,
				new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				// this check is a little hackish. the idea is to not
				// throw up the save dialog if a modal dialog is already
				// showing.
				boolean modalShowing = false;
				java.awt.Window[] ownedWindows = AuthoringTool.this.jAliceFrame
				.getOwnedWindows();
				for (int i = 0; i < ownedWindows.length; i++) {
					// System.out.println(ownedWindows[i]+", "+ownedWindows[i].isShowing());
					if (ownedWindows[i].isShowing()) {
						modalShowing = true;
						break;
					}
				}

				// skip tutorial worlds
				boolean skipThisWorld = false;
				if (currentWorldLocation != null) {
					if (currentWorldLocation.getAbsolutePath()
							.startsWith(
									getTutorialDirectory()
									.getAbsolutePath())) {
						skipThisWorld = true;
					}
				}

				// skip unmodified worlds
				if (!AuthoringTool.this.worldHasBeenModified) {
					skipThisWorld = true;
				}

				if ((!modalShowing) && (!skipThisWorld)) {
					long time = System.currentTimeMillis();
					long dt = time - lastSaveTime;
					int interval = Integer.parseInt(authoringToolConfig
							.getValue("promptToSaveInterval"));
					long intervalMillis = ((long) interval)
					* ((long) 60000);
					if (dt > intervalMillis) {
						// DialogManager.showMessageDialog(
						// "You have not saved in more than" + interval
						// +
						// " minutes.\nIt is recommended that you save early and often to avoid losing work."
						// );
						int result = DialogManager
						.showOptionDialog(
								"You have not saved in more than"
								+ interval
								+ " minutes.\nIt is recommended that you save early and often to avoid losing work.",
								"Save?",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.WARNING_MESSAGE,
								null, new Object[] {
										"Save right now",
								"Remind me later" },
						"Save right now");
						if (result == JOptionPane.YES_OPTION) {
							AuthoringTool.this.getActions().saveWorldAction
							.actionPerformed(null);
						}
						lastSaveTime = System.currentTimeMillis();
					}
				}
			}
		});
		promptToSaveTimer.start();
		addAuthoringToolStateListener(new edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateAdapter() {
			public void worldLoaded(AuthoringToolStateChangedEvent ev) {
				lastSaveTime = System.currentTimeMillis();
			}

			public void worldSaved(AuthoringToolStateChangedEvent ev) {
				lastSaveTime = System.currentTimeMillis();
			}
		});

		// global mouse listening
		if (edu.cmu.cs.stage3.awt.AWTUtilities.mouseListenersAreSupported()
				|| edu.cmu.cs.stage3.awt.AWTUtilities
				.mouseMotionListenersAreSupported()) {
			scheduler.addEachFrameRunnable(new Runnable() {
				public void run() {
					edu.cmu.cs.stage3.awt.AWTUtilities
					.fireMouseAndMouseMotionListenersIfNecessary();
				}
			});
		}

		// for animating ui changes
		rectangleAnimator = new RectangleAnimator(this);

		watcherPanel.setMinimumSize(new Dimension(0, 0));

		// tooltips
		javax.swing.ToolTipManager.sharedInstance().setLightWeightPopupEnabled(
				false);
		javax.swing.ToolTipManager.sharedInstance().setDismissDelay(
				Integer.MAX_VALUE);
	}

	private void importInit() {
		java.util.List importers = importing.getImporters();
		ExtensionGroupFileFilter imageFiles = new ExtensionGroupFileFilter(
		"Image Files");
		extensionStringsToFileFilterMap.put("Image Files", imageFiles);
		ExtensionGroupFileFilter soundFiles = new ExtensionGroupFileFilter(
		"Sound Files");
		extensionStringsToFileFilterMap.put("Sound Files", soundFiles);
		java.util.TreeSet extensions = new java.util.TreeSet();
		for (java.util.Iterator iter = importers.iterator(); iter.hasNext();) {
			edu.cmu.cs.stage3.alice.authoringtool.Importer importer = (edu.cmu.cs.stage3.alice.authoringtool.Importer) iter
			.next();
			java.util.Map map = importer.getExtensionMap();
			for (java.util.Iterator jter = map.keySet().iterator(); jter
			.hasNext();) {
				String extension = (String) jter.next();
				String description = extension + " (" + map.get(extension)
				+ ")";
				ExtensionFileFilter ext = new ExtensionFileFilter(extension,
						description);
				extensions.add(ext);
				extensionStringsToFileFilterMap.put(extension, ext);
				if (importer instanceof edu.cmu.cs.stage3.alice.authoringtool.importers.ImageImporter) {
					imageFiles.addExtensionFileFilter(ext);
				} else if (importer instanceof edu.cmu.cs.stage3.alice.authoringtool.importers.MediaImporter) {
					soundFiles.addExtensionFileFilter(ext);
				}
			}
		}
		importFileChooser.addChoosableFileFilter(characterFileFilter);
		importFileChooser.addChoosableFileFilter(imageFiles);
		importFileChooser.addChoosableFileFilter(soundFiles);
		for (java.util.Iterator iter = extensions.iterator(); iter.hasNext();) {
			ExtensionFileFilter ext = (ExtensionFileFilter) iter.next();
			importFileChooser.addChoosableFileFilter(ext);
		}
		importFileChooser.setFileFilter(importFileChooser
				.getAcceptAllFileFilter());
	}

	private void worldInit(File worldToLoad) {
		if (worldToLoad != null) {
			if (worldToLoad.exists()) {
				if (worldToLoad.canRead()) {
					int retVal = loadWorld(worldToLoad, false);
					if (retVal == Constants.SUCCEEDED) {
						return;
					}
				} else {
					AuthoringTool.showErrorDialog("cannot read world: "
							+ worldToLoad, null, false);
				}
			} else {
				AuthoringTool.showErrorDialog("world doesn't exist: "
						+ worldToLoad, null, false);
			}
		}

		// if that fails
		loadWorld(defaultWorld, false);
	}

	private void initializeOutput(boolean stdOutToConsole,
			boolean stdErrToConsole) {
		if (stdOutToConsole) {
			AuthoringTool.pyStdOut = new PyFile(System.out);
		} else {
			PrintStream stdOutStream = stdOutOutputComponent.getStdOutStream();
			System.setOut(stdOutStream);
			AuthoringTool.pyStdOut = new PyFile(stdOutStream);
		}
		if (stdErrToConsole) {
			AuthoringTool.pyStdErr = new PyFile(System.err);
		} else {
			PrintStream stdErrStream = stdErrOutputComponent.getStdErrStream();
			System.setErr(stdErrStream);
			AuthoringTool.pyStdErr = new PyFile(stdErrStream);
		}
	}

	public JAliceFrame getJAliceFrame() {
		return jAliceFrame;
	}

	public DefaultScheduler getScheduler() {
		return scheduler;
	}

	public OneShotScheduler getOneShotScheduler() {
		return oneShotScheduler;
	}

	public MainUndoRedoStack getUndoRedoStack() {
		return undoRedoStack;
	}

	public Actions getActions() {
		return actions;
	}

	public Configuration getConfig() {
		return authoringToolConfig;
	}

	public RenderTargetFactory getRenderTargetFactory() {
		if (renderTargetFactory == null) {
			Class rendererClass = null;
			boolean isSoftwareEmulationForced = false;
			try {
				String[] renderers = authoringToolConfig
				.getValueList("rendering.orderedRendererList");
				rendererClass = Class.forName(renderers[0]);
			} catch (Throwable t) {
				// todo: inform user of configuration problem?
				// pass
			}
			try {
				String s = authoringToolConfig
				.getValue("rendering.forceSoftwareRendering");
				if (s != null) {
					isSoftwareEmulationForced = s.equals("true");
				}
			} catch (Throwable t) {
				// todo: inform user of configuration problem?
				// pass
			}
			String commandLineOption = System
			.getProperty("alice.forceSoftwareRendering");
			if (commandLineOption != null
					&& commandLineOption.equalsIgnoreCase("true")) {
				isSoftwareEmulationForced = true;
			}
			renderTargetFactory = new DefaultRenderTargetFactory(rendererClass);
			renderTargetFactory
			.setIsSoftwareEmulationForced(isSoftwareEmulationForced);
		}
		return renderTargetFactory;
	}

	public Object getContext() {
		// TODO
		return null;
	}

	public void setContext(Object context) {
		// TODO
	}

	public OutputComponent getStdOutOutputComponent() {
		return stdOutOutputComponent;
	}

	public OutputComponent getStdErrOutputComponent() {
		return stdErrOutputComponent;
	}

	public boolean isStdOutToConsole() {
		return stdOutToConsole;
	}

	public boolean isStdErrToConsole() {
		return stdErrToConsole;
	}

	public WatcherPanel getWatcherPanel() {
		return watcherPanel;
	}

	public EditorManager getEditorManager() {
		return editorManager;
	}

	// /////////////
	// Selection
	// /////////////

	public void setSelectedElement(Element element) {
		if (element == null) { // is this too much of a hack?
			element = getWorld();
		}
		if (this.selectedElement != element) {
			this.selectedElement = element;
			fireElementSelected(element);
		}
	}

	public Element getSelectedElement() {
		return selectedElement;
	}

	public void addElementSelectionListener(
			edu.cmu.cs.stage3.alice.authoringtool.event.ElementSelectionListener listener) {
		selectionListeners.add(listener);
	}

	public void removeElementSelectionListener(
			edu.cmu.cs.stage3.alice.authoringtool.event.ElementSelectionListener listener) {
		selectionListeners.remove(listener);
	}

	protected void fireElementSelected(Element element) {
		for (java.util.Iterator iter = selectionListeners.iterator(); iter
		.hasNext();) {

			((edu.cmu.cs.stage3.alice.authoringtool.event.ElementSelectionListener) iter
					.next()).elementSelected(element);
		}
	}

	// /////////////////////
	// State listening
	// /////////////////////

	public void addAuthoringToolStateListener(
			edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener listener) {
		stateListeners.add(listener);
	}

	public void removeAuthoringToolStateListener(
			edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener listener) {
		stateListeners.remove(listener);
	}

	protected void fireStateChanging(int previousState, int currentState) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(
				previousState, currentState, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter
		.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter
			.next();
			try {
				listener.stateChanging(ev);
			} catch (Throwable t) {
				AuthoringTool
				.showErrorDialog(
						"Error in listener responding to an authoring tool state change.",
						t);
			}
		}
	}

	protected void fireWorldLoading(World world) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(
				AuthoringToolStateChangedEvent.AUTHORING_STATE,
				AuthoringToolStateChangedEvent.AUTHORING_STATE, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter
		.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter
			.next();
			try {
				listener.worldLoading(ev);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(
						"Error in listener responding to world load.", t);
			}
		}
	}

	protected void fireWorldUnLoading(World world) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(
				AuthoringToolStateChangedEvent.AUTHORING_STATE,
				AuthoringToolStateChangedEvent.AUTHORING_STATE, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter
		.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter
			.next();
			try {
				listener.worldUnLoading(ev);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(
						"Error in listener responding to world unload.", t);
			}
		}
	}

	protected void fireWorldStarting(int previousState, int currentState,
			World world) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(
				previousState, currentState, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter
		.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter
			.next();
			try {
				listener.worldStarting(ev);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(
						"Error in listener responding to world starting.", t);
			}
		}
	}

	protected void fireWorldStopping(int previousState, int currentState,
			World world) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(
				previousState, currentState, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter
		.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter
			.next();
			try {
				listener.worldStopping(ev);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(
						"Error in listener responding to world stopping.", t);
			}
		}
	}

	protected void fireWorldPausing(int previousState, int currentState,
			World world) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(
				previousState, currentState, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter
		.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter
			.next();
			try {
				listener.worldPausing(ev);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(
						"Error in listener responding to world pausing.", t);
			}
		}
	}

	protected void fireWorldSaving(World world) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(
				AuthoringToolStateChangedEvent.AUTHORING_STATE,
				AuthoringToolStateChangedEvent.AUTHORING_STATE, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter
		.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter
			.next();
			try {
				listener.worldSaving(ev);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(
						"Error in listener responding to world saving.", t);
			}
		}
	}

	protected void fireStateChanged(int previousState, int currentState) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(
				previousState, currentState, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter
		.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter
			.next();
			try {
				listener.stateChanged(ev);
			} catch (Throwable t) {
				AuthoringTool
				.showErrorDialog(
						"Error in listener responding to authoring tool state changed.",
						t);
			}
		}
	}

	protected void fireWorldLoaded(World world) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(
				AuthoringToolStateChangedEvent.AUTHORING_STATE,
				AuthoringToolStateChangedEvent.AUTHORING_STATE, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter
		.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter
			.next();
			try {
				listener.worldLoaded(ev);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(
						"Error in listener responding to world loaded.", t);
			}
		}
	}

	protected void fireWorldUnLoaded(World world) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(
				AuthoringToolStateChangedEvent.AUTHORING_STATE,
				AuthoringToolStateChangedEvent.AUTHORING_STATE, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter
		.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter
			.next();
			try {
				listener.worldUnLoaded(ev);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(
						"Error in listener responding to world unloaded.", t);
			}
		}
	}

	protected void fireWorldStarted(int previousState, int currentState,
			World world) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(
				previousState, currentState, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter
		.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter
			.next();
			try {
				listener.worldStarted(ev);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(
						"Error in listener responding to world started.", t);
			}
		}
	}

	protected void fireWorldStopped(int previousState, int currentState,
			World world) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(
				previousState, currentState, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter
		.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter
			.next();
			try {
				listener.worldStopped(ev);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(
						"Error in listener responding to world stopped.", t);
			}
		}
	}

	protected void fireWorldPaused(int previousState, int currentState,
			World world) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(
				previousState, currentState, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter
		.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter
			.next();
			try {
				listener.worldPaused(ev);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(
						"Error in listener responding to world paused.", t);
			}
		}
	}

	protected void fireWorldSaved(World world) {
		AuthoringToolStateChangedEvent ev = new AuthoringToolStateChangedEvent(
				AuthoringToolStateChangedEvent.AUTHORING_STATE,
				AuthoringToolStateChangedEvent.AUTHORING_STATE, world);
		for (java.util.Iterator iter = stateListeners.iterator(); iter
		.hasNext();) {
			AuthoringToolStateListener listener = (AuthoringToolStateListener) iter
			.next();
			try {
				listener.worldSaved(ev);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog(
						"Error in listener responding to world saved.", t);
			}
		}
	}

	// ////////////////////////////
	// Editors
	// ////////////////////////////

	public void editObject(Object object) {
		editObject(object, true);
	}

	public void editObject(Object object, boolean switchToNewTab) {
		Class editorClass = null;
		if (object != null) {
			editorClass = EditorUtilities.getBestEditor(object.getClass());
		}
		editObject(object, editorClass, switchToNewTab);
	}

	public void editObject(Object object, Class editorClass,
			boolean switchToNewTab) {
		jAliceFrame.getTabbedEditorComponent().editObject(object, editorClass,
				switchToNewTab);
		saveTabs();
		if (switchToNewTab
				&& (getJAliceFrame().getGuiMode() != JAliceFrame.SCENE_EDITOR_SMALL_MODE)) {
			getJAliceFrame().setGuiMode(JAliceFrame.SCENE_EDITOR_SMALL_MODE);
		}
	}

	public void editObject(final Object object,
			javax.swing.JComponent componentToAnimateFrom) {
		if (!isObjectBeingEdited(object)) {
			animateEditOpen(componentToAnimateFrom);
		}
		editObject(object);
		// SwingWorker worker = new
		// SwingWorker() {
		// public Object construct() {
		// editObject( object );
		// return null;
		// }
		// };
		// worker.start();
	}

	public void editObject(final Object object, final boolean switchToNewTab,
			javax.swing.JComponent componentToAnimateFrom) {
		if (!isObjectBeingEdited(object)) {
			animateEditOpen(componentToAnimateFrom);
		}
		editObject(object, switchToNewTab);
		// SwingWorker worker = new
		// SwingWorker() {
		// public Object construct() {
		// editObject( object, switchToNewTab );
		// return null;
		// }
		// };
		// worker.start();
	}

	public void editObject(final Object object, final Class editorClass,
			final boolean switchToNewTab,
			javax.swing.JComponent componentToAnimateFrom) {
		if (!isObjectBeingEdited(object)) {
			animateEditOpen(componentToAnimateFrom);
		}
		editObject(object, editorClass, switchToNewTab);
		// SwingWorker worker = new
		// SwingWorker() {
		// public Object construct() {
		// editObject( object, editorClass, switchToNewTab );
		// return null;
		// }
		// };
		// worker.start();
	}

	protected void animateEditOpen(javax.swing.JComponent componentToAnimateFrom) {
		Rectangle sourceBounds = componentToAnimateFrom.getBounds();
		Point sourceLocation = sourceBounds.getLocation();
		javax.swing.SwingUtilities.convertPointToScreen(sourceLocation,
				componentToAnimateFrom);
		sourceBounds.setLocation(sourceLocation);
		Rectangle targetBounds = jAliceFrame.getTabbedEditorComponent()
		.getBounds();
		Point targetLocation = targetBounds.getLocation();
		javax.swing.SwingUtilities.convertPointToScreen(targetLocation,
				jAliceFrame.getTabbedEditorComponent());
		targetBounds.setLocation(targetLocation);
		java.awt.Color color = componentToAnimateFrom.getBackground();
		rectangleAnimator.animate(sourceBounds, targetBounds, color);
	}

	public Object getObjectBeingEdited() {
		return jAliceFrame.getTabbedEditorComponent().getObjectBeingEdited();
	}

	public Object[] getObjectsBeingEdited() {
		return jAliceFrame.getTabbedEditorComponent().getObjectsBeingEdited();
	}

	public boolean isObjectBeingEdited(Object object) {
		return jAliceFrame.getTabbedEditorComponent().isObjectBeingEdited(
				object);
	}

	// /////////////////////////
	// General Functionality
	// /////////////////////////

	private int showStartUpDialog(int tabID) {
		int retVal = askForSaveIfNecessary();
		if (retVal != Constants.CANCELED) {
			startUpContentPane.setTabID(tabID);
			if (DialogManager.showDialog(startUpContentPane) == edu.cmu.cs.stage3.swing.ContentPane.OK_OPTION) {
				File file = startUpContentPane.getFile();
				if (startUpContentPane.isTutorial()) {
					launchTutorialFile(file);
				} else {
					// loadWorld(file, startUpContentPane.isSaveNeeded());
					loadWorld(file, false);
				}
			}
			return Constants.SUCCEEDED;
		} else {
			return retVal;
		}
	}

	public int newWorld() {
		watcherPanel.clear();
		return showStartUpDialog(StartUpContentPane.TEMPLATE_TAB_ID);
	}

	public int openWorld() {
		return showStartUpDialog(StartUpContentPane.OPEN_TAB_ID);
	}

	public int openExampleWorld() {
		return showStartUpDialog(StartUpContentPane.EXAMPLE_TAB_ID);
	}

	public int openTutorialWorld() {
		return showStartUpDialog(StartUpContentPane.TUTORIAL_TAB_ID);
	}

	public int saveWorld() {
		if ((currentWorldLocation != null)
				&& shouldAllowOverwrite(currentWorldLocation)
				&& currentWorldLocation.canWrite()) {
			try {
				return saveWorldToFile(currentWorldLocation, false);
			} catch (IOException e) {
				AuthoringTool.showErrorDialog("Unable to save world: "
						+ currentWorldLocation.getAbsolutePath(), e);
				return Constants.FAILED;
			}
		} else {
			return saveWorldAs();
		}
	}

	public int saveWorldAs() {
		int returnVal = DialogManager.showSaveDialog(saveWorldFileChooser);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = saveWorldFileChooser.getSelectedFile();
			if (!file.getName().endsWith("." + WORLD_EXTENSION)) {
				file = new File(file.getParent(), file.getName() + "."
						+ WORLD_EXTENSION);
			}
			try {
				return saveWorldToFile(file, false);
			} catch (IOException e) {
				AuthoringTool.showErrorDialog("Unable to save world: " + file,
						e);
				file.delete();
				return Constants.FAILED;
			}
		} else if (returnVal == JFileChooser.APPROVE_OPTION) {
			return Constants.CANCELED;
		} else {
			return Constants.FAILED;
		}
	}

	private void finalCleanUp() {
		try {
			Rectangle bounds = jAliceFrame.getBounds();
			authoringToolConfig.setValue("mainWindowBounds", bounds.x + ", "
					+ bounds.y + ", " + bounds.width + ", " + bounds.height);
			Configuration.storeConfig();
			renderTargetFactory.release();
		} catch (Throwable t) {
			AuthoringTool.showErrorDialog(
					"Error encountered during final cleanup.", t);
		}
	}

	public int quit() {
		try {
			int retVal = leaveWorld(true);
			if (retVal == Constants.SUCCEEDED) {
				finalCleanUp();
				File aliceHasNotExitedFile = new File(
						edu.cmu.cs.stage3.alice.authoringtool.JAlice
						.getAliceUserDirectory(),
						"aliceHasNotExited.txt");
				boolean deleteTheFile = aliceHasNotExitedFile.delete();
				System.exit(0);
				return Constants.SUCCEEDED; // never reached
			} else if (retVal == Constants.CANCELED) {
				return Constants.CANCELED;
			} else if (retVal == Constants.FAILED) {
				int result = DialogManager
				.showConfirmDialog("Alice failed to correctly save and/or close the current world.  Would you still like to quit?");
				if (result == JOptionPane.YES_OPTION) {
					finalCleanUp();
					System.exit(1);
				}
			}
		} catch (Throwable t) {
			try {
				int result = DialogManager
				.showConfirmDialog("Error encountered while attempting to close Alice.  Would you like to force the close?"); // TODO:
				// give
				// information
				// about
				// the
				// error
				if (result == JOptionPane.YES_OPTION) {
					finalCleanUp();
					System.exit(1);
				}
			} catch (Throwable t2) {
				finalCleanUp();
				System.exit(1);
			}
		}
		return Constants.FAILED;
	}

	public int askForSaveIfNecessary() {
		if (worldHasBeenModified) {
			if ((currentWorldLocation != null)
					&& currentWorldLocation.getAbsolutePath().startsWith(
							getTutorialDirectory().getAbsolutePath())) { // skip
				// tutorial
				// worlds
				return Constants.SUCCEEDED;
			} else if ((currentWorldLocation == null)
					|| (!shouldAllowOverwrite(currentWorldLocation))) {
				String question = "The world has not been saved.  Would you like to save it?";
				int n = DialogManager.showConfirmDialog(question,
						"Save World?", JOptionPane.YES_NO_CANCEL_OPTION);
				if (n == JOptionPane.YES_OPTION) {
					int retVal = saveWorldAs();
					if (retVal == Constants.CANCELED) {
						return askForSaveIfNecessary();
					} else if (retVal == Constants.FAILED) {
						return Constants.FAILED;
					}
				} else if (n == JOptionPane.NO_OPTION) {
					return Constants.SUCCEEDED;
				} else if (n == JOptionPane.CANCEL_OPTION) {
					return Constants.CANCELED;
				} else if (n == JOptionPane.CLOSED_OPTION) {
					return Constants.CANCELED;
				}
			} else {
				String question = "The world has been modified.  Would you like to save it?";
				int n = DialogManager.showConfirmDialog(question,
						"Save World?", JOptionPane.YES_NO_CANCEL_OPTION);
				if (n == JOptionPane.YES_OPTION) {
					int retVal = saveWorld();
					if (retVal == Constants.CANCELED) {
						return Constants.CANCELED;
					} else if (retVal == Constants.FAILED) {
						return Constants.FAILED;
					}
				} else if (n == JOptionPane.NO_OPTION) {
					return Constants.SUCCEEDED;
				} else if (n == JOptionPane.CANCEL_OPTION) {
					return Constants.CANCELED;
				} else if (n == JOptionPane.CLOSED_OPTION) {
					return Constants.CANCELED;
				}
			}
			worldHasBeenModified = false;
			undoRedoStack.setUnmodified();
		}
		return Constants.SUCCEEDED;
	}

	// public int askForSaveIfNecessaryForMovie() {
	// if (worldHasBeenModified) {
	// if ((currentWorldLocation != null) &&
	// currentWorldLocation.getAbsolutePath().startsWith(getTutorialDirectory().getAbsolutePath()))
	// { // skip tutorial worlds
	// return Constants.SUCCEEDED;
	// } else if ((currentWorldLocation == null) ||
	// (!shouldAllowOverwrite(currentWorldLocation))) {
	// String question =
	// "The world has not been saved.  You need to save it before making a movie.";
	// int n = DialogManager.showConfirmDialog(question,
	// "Save World?", JOptionPane.OK_CANCEL_OPTION);
	// if (n == JOptionPane.YES_OPTION) {
	// int retVal = saveWorldAs();
	// if (retVal == Constants.CANCELED) {
	// return askForSaveIfNecessaryForMovie();
	// } else if (retVal == Constants.FAILED) {
	// return Constants.FAILED;
	// }
	// } else if (n == JOptionPane.CANCEL_OPTION) {
	// return Constants.CANCELED;
	// } else if (n == JOptionPane.CLOSED_OPTION) {
	// return Constants.CANCELED;
	// }
	// } else {
	// String question =
	// "The world has been modified.  You need to save it before making a movie.";
	// int n = DialogManager.showConfirmDialog(question,
	// "Save World?", JOptionPane.OK_CANCEL_OPTION);
	// if (n == JOptionPane.YES_OPTION) {
	// int retVal = saveWorld();
	// if (retVal == Constants.CANCELED) {
	// return Constants.CANCELED;
	// } else if (retVal == Constants.FAILED) {
	// return Constants.FAILED;
	// }
	// } else if (n == JOptionPane.CANCEL_OPTION) {
	// return Constants.CANCELED;
	// } else if (n == JOptionPane.CLOSED_OPTION) {
	// return Constants.CANCELED;
	// }
	// }
	// worldHasBeenModified = false;
	// undoRedoStack.setUnmodified();
	// }
	// return Constants.SUCCEEDED;
	// }

	public int leaveWorld(boolean askForSaveIfNecessary) {
		try {
			if (askForSaveIfNecessary) {
				int retVal = askForSaveIfNecessary();
				if (retVal == Constants.CANCELED) {
					return Constants.CANCELED;
				} else if (retVal == Constants.FAILED) {
					return Constants.FAILED;
				}
			}

			fireWorldUnLoading(world);

			saveTabsEnabled = false;
			undoRedoStack.clear();
			jAliceFrame.setWorld(null);
			userDefinedParameterListener.setWorld(null);
			setCurrentWorldLocation(null);
			editObject(null);
			if (world != null) {
				world.release();
				fireWorldUnLoaded(world);
			}

			world = null;

			return Constants.SUCCEEDED;
		} catch (Exception e) {
			AuthoringTool.showErrorDialog(
					"Error encountered while leaving current world.", e);
			return Constants.FAILED;
		}
	}

	// private edu.cmu.cs.stage3.progress.ProgressObserver
	// m_backupProgressObserver = null;
	public void waitForBackupToFinishIfNecessary(File file) {
	}

	public void backupWorld(final File src, final int maxBackups) {
		if (edu.cmu.cs.stage3.io.FileUtilities.isFileCopySupported()) {
			new Thread() {
				public void run() {
					File parentDir = src.getParentFile();
					String name = src.getName();
					if (name.endsWith(".a2w")) {
						name = name.substring(0, name.length() - 4);
					}
					File dstDir = new File(parentDir, "Backups of " + name);

					StringBuffer sb = new StringBuffer();
					java.util.Calendar calendar = java.util.Calendar
					.getInstance();
					sb.append(name);
					sb.append(" backed up on ");
					switch (calendar.get(java.util.Calendar.MONTH)) {
					case java.util.Calendar.JANUARY:
						sb.append("Jan ");
						break;
					case java.util.Calendar.FEBRUARY:
						sb.append("Feb ");
						break;
					case java.util.Calendar.MARCH:
						sb.append("Mar ");
						break;
					case java.util.Calendar.APRIL:
						sb.append("Apr ");
						break;
					case java.util.Calendar.MAY:
						sb.append("May ");
						break;
					case java.util.Calendar.JUNE:
						sb.append("Jun ");
						break;
					case java.util.Calendar.JULY:
						sb.append("Jul ");
						break;
					case java.util.Calendar.AUGUST:
						sb.append("Aug ");
						break;
					case java.util.Calendar.SEPTEMBER:
						sb.append("Sep ");
						break;
					case java.util.Calendar.OCTOBER:
						sb.append("Oct ");
						break;
					case java.util.Calendar.NOVEMBER:
						sb.append("Noc ");
						break;
					case java.util.Calendar.DECEMBER:
						sb.append("Dec ");
						break;
					}
					sb.append(calendar.get(java.util.Calendar.DAY_OF_MONTH));
					sb.append(" ");
					sb.append(calendar.get(java.util.Calendar.YEAR));
					sb.append(" at ");
					sb.append(calendar.get(java.util.Calendar.HOUR));
					sb.append("h");
					sb.append(calendar.get(java.util.Calendar.MINUTE));
					sb.append("m");
					sb.append(calendar.get(java.util.Calendar.SECOND));
					switch (calendar.get(java.util.Calendar.AM_PM)) {
					case java.util.Calendar.AM:
						sb.append("s AM.a2w");
						break;
					case java.util.Calendar.PM:
						sb.append("s PM.a2w");
						break;
					}

					File dst = new File(dstDir, sb.toString());
					// m_backupProgressObserver = new
					// edu.cmu.cs.stage3.progress.ProgressObserver();
					try {
						edu.cmu.cs.stage3.io.FileUtilities.copy(src, dst, true);
						File[] siblings = dstDir
						.listFiles(new FilenameFilter() {
							public boolean accept(File dir, String name) {
								return name.endsWith(".a2w");
							}
						});
						if (siblings.length > maxBackups) {
							File fileToDelete = siblings[0];
							long fileToDeleteLastModified = fileToDelete
							.lastModified();
							for (int i = 1; i < siblings.length; i++) {
								long lastModified = siblings[i].lastModified();
								if (lastModified < fileToDeleteLastModified) {
									fileToDelete = siblings[i];
									fileToDeleteLastModified = lastModified;
								}
							}
							fileToDelete.delete();
						}
					} finally {
						// m_backupProgressObserver = null;
					}
				}
			}.start();
		}
	}

	public int saveWorldToFile(File file, boolean saveForWeb)
	throws IOException {
		if (file.exists() && (!file.canWrite())) {
			DialogManager.showMessageDialog("Cannot save world.  "
					+ file.getAbsolutePath() + " is read-only.",
					"Cannot Save World", JOptionPane.ERROR_MESSAGE);
			return Constants.FAILED;
		}
		if (saveForWeb && file.exists()) {
			file.delete();
		}
		if (file.exists()) {
			worldStoreProgressPane.setIsCancelEnabled(false);
		} else {
			worldStoreProgressPane.setIsCancelEnabled(true);
			waitForBackupToFinishIfNecessary(file);
		}
		fireWorldSaving(world);

		worldDirectory = null;
		boolean tempListening = AuthoringTool.this.getUndoRedoStack()
		.getIsListening();
		undoRedoStack.setIsListening(false);
		try {
			// save which tabs are open
			saveTabsEnabled = true;
			saveTabs();

			// save count
			countSomething("edu.cmu.cs.stage3.alice.authoringtool.saveCount");

			// world open time
			updateWorldOpenTime();

			// store the world
			java.util.Dictionary map = new java.util.Hashtable();
			if (authoringToolConfig.getValue("saveThumbnailWithWorld")
					.equalsIgnoreCase("true")) {
				try {
					edu.cmu.cs.stage3.alice.core.Camera[] cameras = (edu.cmu.cs.stage3.alice.core.Camera[]) world
					.getDescendants(edu.cmu.cs.stage3.alice.core.Camera.class);
					if (cameras.length > 0) {
						OffscreenRenderTarget rt = getRenderTargetFactory()
						.createOffscreenRenderTarget();
						rt.setSize(120, 90);
						rt.addCamera(cameras[0].getSceneGraphCamera());
						rt.clearAndRenderOffscreen();
						java.awt.Image image = rt.getOffscreenImage();
						if (image != null) {
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							edu.cmu.cs.stage3.image.ImageIO.store("png", baos,
									image);
							map.put("thumbnail.png", baos.toByteArray());
						}
						rt.release();
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			worldStoreProgressPane.setElement(world);
			worldStoreProgressPane.setFile(file);
			worldStoreProgressPane.setFilnameToByteArrayMap(map);
			int result = DialogManager.showDialog(worldStoreProgressPane);
			if (result == edu.cmu.cs.stage3.swing.ContentPane.OK_OPTION) {
				if (worldStoreProgressPane.wasSuccessful()) {
					jAliceFrame.HACK_standDownFromRedAlert();
					worldHasBeenModified = false;
					undoRedoStack.setUnmodified();
					if (!file.equals(defaultWorld)
							&& !isTemplateWorld(file.getAbsolutePath())) {
						setCurrentWorldLocation(file);

						if (file.isDirectory()) {
							worldDirectory = file;
						}
						if (saveForWeb) {
							// pass
						} else {
							jAliceFrame.updateRecentWorlds(file
									.getAbsolutePath());
							int backupCount = 0;
							try {
								backupCount = Integer
								.parseInt(authoringToolConfig
										.getValue("maximumWorldBackupCount"));
							} catch (Throwable t) {
								t.printStackTrace();
							}
							if (backupCount > 0) {
								backupWorld(file, backupCount);
							}
						}
					} else {
						setCurrentWorldLocation(null);
					}
					fireWorldSaved(world);
					return Constants.SUCCEEDED;
				} else {
					return Constants.FAILED;
				}
			} else {
				file.delete();
				return Constants.CANCELED;
			}
		} catch (Throwable t) {
			AuthoringTool.showErrorDialog("Unable to store world to file: "
					+ file, t);
			// file.delete();
			return Constants.FAILED;
		} finally {
			undoRedoStack.setIsListening(tempListening);
		}
	}

	public void saveForWeb() {
		if (DialogManager.showDialog(saveForWebContentPane) == edu.cmu.cs.stage3.swing.ContentPane.OK_OPTION) {
			File directory = saveForWebContentPane.getExportDirectory();
			if (!directory.exists()) {
				directory.mkdir();
			}
			String fileName = saveForWebContentPane.getExportFileName();
			File file = new File(directory, fileName);
			int width = saveForWebContentPane.getExportWidth();
			int height = saveForWebContentPane.getExportHeight();
			String authorName = saveForWebContentPane.getExportAuthorName();

			try {
				String htmlCode = null;
				if (saveForWebContentPane.isCodeToBeExported()) {
					StringBuffer buffer = new StringBuffer();
					exportCodeForPrintingContentPane.initialize(authorName);
					exportCodeForPrintingContentPane.getHTML(buffer, file,
							false, true, null);
					htmlCode = buffer.toString();
				}
				saveWorldForWeb(file, width, height, authorName, htmlCode);
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog("Error saving for the web.", t);
			}
		}
	}

	public int saveWorldForWeb(File htmlFile, int width, int height,
			String authorName, String code) throws IOException {
		String baseName = htmlFile.getName();
		int dotIndex = htmlFile.getName().lastIndexOf(".");
		if (dotIndex > 0) {
			baseName = htmlFile.getName().substring(0, dotIndex);
		}

		File worldFile = new File(htmlFile.getParentFile(), baseName + "."
				+ WORLD_EXTENSION);

		if (htmlFile.exists() && (!htmlFile.canWrite())) {
			DialogManager.showMessageDialog("Cannot save web page.  "
					+ htmlFile.getAbsolutePath() + " is read-only",
					"Cannot Save", JOptionPane.ERROR_MESSAGE);
			return Constants.FAILED;
		}
		if (worldFile.exists() && (!worldFile.canWrite())) {
			DialogManager.showMessageDialog("Cannot save world.  "
					+ worldFile.getAbsolutePath() + " is read-only",
					"Cannot Save", JOptionPane.ERROR_MESSAGE);
			return Constants.FAILED;
		}
		if (authorName != null) {
			authorName = "<h2>Created by " + authorName + "</h2>\n";
		} else {
			authorName = " ";
		}
		if (code == null) {
			code = " ";
		}
		HashMap replacements = new HashMap();
		replacements.put("__worldname__", baseName);
		replacements.put("__code__", code);
		replacements.put("__authorname__", authorName);
		replacements.put("__worldfile__", worldFile.getName());
		replacements.put("__width__", Integer.toString(width));
		replacements.put("__height__", Integer.toString(height));

		// write web page
		File templateFile = new File(JAlice.getAliceHomeDirectory(),
		"etc/appletTemplate.html");
		if (!templateFile.exists()) {
			templateFile.createNewFile();
		}
		BufferedReader templateReader = new BufferedReader(new FileReader(
				templateFile));
		PrintWriter webPageWriter = new PrintWriter(new BufferedWriter(
				new FileWriter(htmlFile)));

		String line = templateReader.readLine();
		while (line != null) {
			for (java.util.Iterator iter = replacements.keySet().iterator(); iter
			.hasNext();) {
				String from = (String) iter.next();
				String to = (String) replacements.get(from);
				while (line.indexOf(from) > 0) {
					line = line.substring(0, line.indexOf(from))
					+ to
					+ line
					.substring(line.indexOf(from)
							+ from.length());
				}
			}
			webPageWriter.println(line);
			line = templateReader.readLine();
		}
		templateReader.close();
		webPageWriter.flush();
		webPageWriter.close();

		// write world
		int saveWorldResult = saveWorldToFile(worldFile, true);
		if (saveWorldResult != Constants.SUCCEEDED) {
			return saveWorldResult;
		}

		// write applet
		File appletSourceFile = new File(JAlice.getAliceHomeDirectory(),
		"etc/aliceapplet.jar");
		File appletDestinationFile = new File(htmlFile.getParentFile(),
		"aliceapplet.jar");
		AuthoringToolResources
		.copyFile(appletSourceFile, appletDestinationFile);

		return Constants.SUCCEEDED;
	}

	public int loadWorld(String filename, boolean askForSaveIfNecessary) {
		return loadWorld(new File(filename), askForSaveIfNecessary);
	}

	public int loadWorld(final File file, boolean askForSaveIfNecessary) {
		int result;
		if (file.isFile()) {
			result = loadWorld(new edu.cmu.cs.stage3.io.ZipFileTreeLoader(),
					file, askForSaveIfNecessary);
		} else if (file.isDirectory()) {
			result = loadWorld(new edu.cmu.cs.stage3.io.FileSystemTreeLoader(),
					file, askForSaveIfNecessary);
		} else {
			AuthoringTool.showErrorDialog("not a file or directory: " + file,
					null);
			result = Constants.FAILED;
		}
		return result;
	}

	public boolean isTemplateWorld(String filename) {
		return filename.startsWith(getTemplateWorldsDirectory()
				.getAbsolutePath());
	}

	public int loadWorld(final edu.cmu.cs.stage3.io.DirectoryTreeLoader loader,
			Object path, boolean askForSaveIfNecessary) {
		if (askForSaveIfNecessary) {
			int retVal = askForSaveIfNecessary();
			if (retVal == Constants.CANCELED) {
				return Constants.CANCELED;
			} else if (retVal == Constants.FAILED) {
				int result = DialogManager
				.showConfirmDialog("Alice failed to correctly save the current world.  Would you still like to load a new world?");
				if (result != JOptionPane.YES_OPTION) {
					return Constants.CANCELED;
				}
			}
		}

		fireWorldLoading(null);

		worldDirectory = null;

		World tempWorld = null;
		try {
			loader.open(path);
			try {
				if (path.equals(defaultWorld)) {
					tempWorld = (World) Element.load(loader, null, null);
				} else {
					worldLoadProgressPane.setLoader(loader);
					worldLoadProgressPane.setExternalRoot(null);
					DialogManager.showDialog(worldLoadProgressPane);
					tempWorld = (World) worldLoadProgressPane
					.getLoadedElement();
				}
			} finally {
				loader.close();
			}
		} catch (Throwable t) {
			AuthoringTool.showErrorDialog("Unable to load world: " + path, t);
		}
		//
		// try {
		// loader.open(path);
		// } catch (IOException ioe) {
		// AuthoringTool.showErrorDialog("Unable to open world: " + path, ioe);
		// }
		//
		// try {
		// tempWorld = (World)
		// Element.load(loader, null,
		// progressPane);
		// } catch
		// (edu.cmu.cs.stage3.alice.core.UnresolvablePropertyReferencesException
		// upre) {
		// edu.cmu.cs.stage3.alice.core.reference.PropertyReference[]
		// propertyReferences = upre.getPropertyReferences();
		// System.err.println("Unable to load world: " + path +
		// ".  Couldn't resolve the following references:");
		// for (int i = 0; i < propertyReferences.length; i++) {
		// System.err.println("\t" + propertyReferences[i]);
		// }
		// tempWorld = (World) upre.getElement();
		// } catch (edu.cmu.cs.stage3.progress.ProgressCancelException pce) {
		// return Constants.CANCELED;
		// } catch (Throwable t) {
		// AuthoringTool.showErrorDialog("Unable to load world: " + path, t);
		// } finally {
		// try {
		// loader.close();
		// } catch (IOException ioe) {
		// AuthoringTool.showErrorDialog("Unable to close world: " + path, ioe);
		// }
		// }

		if (tempWorld != null) {
			final java.awt.Cursor prevCursor = getJAliceFrame().getCursor();
			getJAliceFrame().setCursor(java.awt.Cursor.WAIT_CURSOR);
			try {
				leaveWorld(false);
				world = tempWorld;
				worldClock.setWorld(world);
				world.setClock(worldClock);
				world.setScriptingFactory(scriptingFactory);
				worldLoadedTime = System.currentTimeMillis();
				jAliceFrame.setWorld(world);
				userDefinedParameterListener.setWorld(world);

				world.setRenderTargetFactory(getRenderTargetFactory());

				// Element[] elements =
				// world.search(new
				// criterion.InstanceOfCriterion(RenderTarget.class));
				Element[] elements = world.getDescendants(RenderTarget.class);
				if (elements.length > 0) {
					renderPanel.removeAll();
					renderTarget = (RenderTarget) elements[0];
					renderPanel.add(renderTarget.getAWTComponent(),
							java.awt.BorderLayout.CENTER);
					renderPanel.revalidate();
					renderPanel.repaint();
				}

				setSelectedElement(world);

				loadTabs();
				if (!world.responses.isEmpty()) {
					editObject(world.responses.get(0), true);
				}

				if ((!path.equals(defaultWorld)) && (path instanceof File)
						&& !isTemplateWorld(((File) path).getAbsolutePath())) {
					setCurrentWorldLocation(((File) path));

					if (((File) path).isDirectory()) {
						worldDirectory = (File) path;
					}
					jAliceFrame.updateRecentWorlds(((File) path)
							.getAbsolutePath());
				} else {
					setCurrentWorldLocation(null);
				}

				undoRedoStack.setUnmodified();
				fireWorldLoaded(world);
			} finally {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						getJAliceFrame().setCursor(prevCursor);
					}
				});
			}
			return Constants.SUCCEEDED;
		} else {
			return Constants.FAILED;
		}
	}

	protected boolean shouldAllowOverwrite(File file) {
		if (file != null) {
			if (file.getAbsolutePath().startsWith(
					getTutorialDirectory().getAbsolutePath())) {
				return false;
			} else if (file.getAbsolutePath().startsWith(
					getExampleWorldsDirectory().getAbsolutePath())) {
				return false;
			} else if (isTemplateWorld(file.getAbsolutePath())) {
				return false;
			}

		}

		return true;
	}

	public Element loadAndAddCharacter() {
		// addCharacterFileDialog.setVisible( true );
		// AuthoringToolResources.centerComponentOnScreen(
		// addCharacterFileDialog );
		// if( addCharacterFileDialog.getFile() != null ) {
		// String filename = addCharacterFileDialog.getFile();
		// File openFile = new File(
		// addCharacterFileDialog.getDirectory(), filename );
		// return loadCharacter( openFile );
		// } else {
		// return Constants.CANCELED;
		// }

		Element character = null;

		addCharacterFileChooser.rescanCurrentDirectory();
		int returnVal = DialogManager.showDialog(addCharacterFileChooser, null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = addCharacterFileChooser.getSelectedFile();
			character = loadAndAddCharacter(file);
		}

		return character;
	}

	public int add3DText() {
		Add3DTextPanel add3DTextPanel = new Add3DTextPanel();
		// this.setTitle("Add 3D Text");

		if (DialogManager.showConfirmDialog(add3DTextPanel, "Add 3D Text",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
			edu.cmu.cs.stage3.alice.core.Text3D text3D = add3DTextPanel
			.createText3D();
			if (text3D != null) {
				undoRedoStack.startCompound();

				text3D.name.set(AuthoringToolResources.getNameForNewChild(
						text3D.name.getStringValue(), world));

				world.addChild(text3D);
				world.sandboxes.add(text3D);

				if (getCurrentCamera() instanceof edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) {
					animateAddModel(
							text3D,
							world,
							(edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) getCurrentCamera());
				} else {
					text3D.vehicle.set(world);
				}

				undoRedoStack.stopCompound();

				return Constants.SUCCEEDED;
			} else {
				return Constants.CANCELED;
			}
		} else {
			return Constants.CANCELED;
		}

	}

	public int exportMovie() {

		// mpitsch ~ Get Directory for Movie Panel
		// should be worldDirectory.getAbsolutePath();
		// / Get Dimensions and Location of Movie
		int boundsX = 0, boundsY = 0, boundsWidth = 0, boundsHeight = 0;

		Package authoringToolPackage = Package
		.getPackage("edu.cmu.cs.stage3.alice.authoringtool");
		String dimensions = Configuration.getValue(authoringToolPackage,
		"rendering.renderWindowBounds");

		java.util.StringTokenizer st = new java.util.StringTokenizer(
				dimensions, " \t,");
		if (st.countTokens() == 4) {
			boundsX = Integer.parseInt(st.nextToken()) + 5;
			boundsY = Integer.parseInt(st.nextToken()) + 65;
			boundsWidth = Integer.parseInt(st.nextToken());
			boundsHeight = Integer.parseInt(st.nextToken());
		}
		// System.out.println("x : " + boundsX + " y:" + boundsY + " width" +
		// boundsWidth + " height " + boundsHeight);
		// Possibly for Layout

		// direct from barb's main

		// create a Shape Panel

		// maybe instead make user save to file
		// exits entire thing! eek

		// if(currentWorldLocation==null)
		if (saveWorldAs() != Constants.SUCCEEDED)
			return Constants.CANCELED;
		String directory = currentWorldLocation.getParent();
		directory = directory.replace('\\', '/');
		File dir = new File(directory + "/frames");
		dir.mkdir();

		soundStorage = new SoundStorage();
		playWhileEncoding(directory);

		if (authoringToolConfig.getValue("rendering.deleteFiles")
				.equalsIgnoreCase("true") == true) {
			captureContentPane.removeFiles(directory + "/frames/");
			dir.delete();
			// if (dir.exists()==true)
			// showErrorDialog("Error removing temporary folder.","Cannot delete the frames folder. One or more files in the folder is being used. "
			// +
			// "Try recording a few seconds of an empty world and then hit the clear button to remove all the files in the temporary folder.");
		}

		soundStorage = null;
		// AliceMoviePanel p = new
		// AliceMoviePanel(renderContentPane,directory,this);
		// int result = DialogManager.showDialog(p);
		// create a panel to deal...
		// sent worldLocation and bounds, authoringtool

		// //DialogManager.showMessageDialog("Export movie is not implemented at this time.");
		// // int retVal = askForSaveIfNecessaryForMovie();
		// // if( retVal == Constants.CANCELED ) {
		// // return Constants.CANCELED;
		// // } else if( retVal == Constants.FAILED ) {
		// // int result =
		// DialogManager.showConfirmDialog( jAliceFrame,
		// "Alice failed to correctly save the current world.  Would you still like to load a new world?"
		// );
		// // if( result != JOptionPane.YES_OPTION ) {
		// // return Constants.CANCELED;
		// // }
		// // }
		//		
		// DialogManager.showMessageDialog(getJAliceFrame(),
		// "The movie maker currently makes uncompressed movies that may get very large.\n"
		// +"This might make it difficult to share the movies over the internet.",
		// "Uncompressed Movie Warning",
		// JOptionPane.WARNING_MESSAGE);
		//		
		// undoRedoStack.setIsListening(false);
		//
		// fireStateChanging( AuthoringToolStateChangedEvent.AUTHORING_STATE,
		// AuthoringToolStateChangedEvent.RUNTIME_STATE );
		// fireWorldStarting( AuthoringToolStateChangedEvent.AUTHORING_STATE,
		// AuthoringToolStateChangedEvent.RUNTIME_STATE, world );
		//		
		// fireStateChanged( AuthoringToolStateChangedEvent.AUTHORING_STATE,
		// AuthoringToolStateChangedEvent.RUNTIME_STATE );
		// fireWorldStarted( AuthoringToolStateChangedEvent.AUTHORING_STATE,
		// AuthoringToolStateChangedEvent.RUNTIME_STATE, world );
		//
		// edu.cmu.cs.stage3.alice.moviemaker.MoviePlayer movieMaker = new
		// edu.cmu.cs.stage3.alice.moviemaker.MoviePlayer(this.world);
		// movieMaker.record();
		// undoRedoStack.setIsListening(true);
		//		
		// fireStateChanged( AuthoringToolStateChangedEvent.RUNTIME_STATE,
		// AuthoringToolStateChangedEvent.AUTHORING_STATE );
		// fireWorldStopping( AuthoringToolStateChangedEvent.RUNTIME_STATE,
		// AuthoringToolStateChangedEvent.AUTHORING_STATE, world );
		//
		// fireStateChanged( AuthoringToolStateChangedEvent.RUNTIME_STATE,
		// AuthoringToolStateChangedEvent.AUTHORING_STATE );
		// fireWorldStopped( AuthoringToolStateChangedEvent.RUNTIME_STATE,
		// AuthoringToolStateChangedEvent.AUTHORING_STATE, world );
		//
		//		
		return Constants.SUCCEEDED;
	}

	public Element loadAndAddCharacter(java.net.URL url) {
		return loadAndAddCharacter(new edu.cmu.cs.stage3.io.ZipTreeLoader(),
				url, null);
	}

	public Element loadAndAddCharacter(File file) {
		Element character = null;
		if (file.isFile()) {
			character = loadAndAddCharacter(
					new edu.cmu.cs.stage3.io.ZipFileTreeLoader(), file, null);
		} else if (file.isDirectory()) {
			character = loadAndAddCharacter(
					new edu.cmu.cs.stage3.io.FileSystemTreeLoader(), file, null);
		} else {
			AuthoringTool.showErrorDialog("not a file or directory: " + file,
					null);
		}
		return character;
	}

	public Element loadAndAddCharacter(java.net.URL url,
			edu.cmu.cs.stage3.math.Matrix44 targetTransformation) {
		return loadAndAddCharacter(new edu.cmu.cs.stage3.io.ZipTreeLoader(),
				url, targetTransformation);
	}

	public Element loadAndAddCharacter(File file,
			edu.cmu.cs.stage3.math.Matrix44 targetTransformation) {
		Element character = null;
		if (file.isFile()) {
			character = loadAndAddCharacter(
					new edu.cmu.cs.stage3.io.ZipFileTreeLoader(), file,
					targetTransformation);
		} else if (file.isDirectory()) {
			character = loadAndAddCharacter(
					new edu.cmu.cs.stage3.io.FileSystemTreeLoader(), file,
					targetTransformation);
		} else {
			AuthoringTool.showErrorDialog("not a file or directory: " + file,
					null);
		}
		return character;
	}

	public Element loadAndAddCharacter(
			edu.cmu.cs.stage3.io.DirectoryTreeLoader loader, Object pathname,
			edu.cmu.cs.stage3.math.Matrix44 targetTransformation) {
		undoRedoStack.startCompound();

		Element character = null;

		try {
			loader.open(pathname);
			try {
				characterLoadProgressPane.setLoader(loader);
				characterLoadProgressPane.setExternalRoot(world);
				DialogManager.showDialog(characterLoadProgressPane);
				character = characterLoadProgressPane.getLoadedElement();
			} finally {
				loader.close();
			}
			if (character != null) {
				addCharacter(character, targetTransformation);
			}
		} catch (java.util.zip.ZipException e) {
			AuthoringTool.showErrorDialog("File is not a valid "
					+ CHARACTER_EXTENSION + ": " + pathname, e, false);
		} catch (Exception e) {
			AuthoringTool.showErrorDialog("Unable to load object: " + pathname,
					e);
		} finally {
			undoRedoStack.stopCompound();
		}
		return character;
	}
	
	public void speak(Element element)
	{
		SynthesizerModeDesc desc = new SynthesizerModeDesc(
                null,          // engine name
                "general",     // mode name
                Locale.US,     // locale
                null,          // running
                null);         // voice
		try 
		{
			Synthesizer synth = Central.createSynthesizer(desc);
			synth.allocate();
			synth.resume();
			desc = (SynthesizerModeDesc)synth.getEngineModeDesc();
			Voice[] voices = desc.getVoices();
			synth.getSynthesizerProperties().setVoice(voices[0]);

			synth.speak("<?xml version=\"1.0\"?><jsml>" + element.getKey() + 
					"<break size=\"large\"/> has been added" + "</jsml>", null);
		}
		catch(IllegalArgumentException e)
		{
			e.printStackTrace();
		} 
		catch (EngineException e)
		{
			e.printStackTrace();
		} 
		catch (AudioException e) 
		{
			e.printStackTrace();
		} 
		catch (EngineStateError e) 
		{
			e.printStackTrace();
		} 
		catch (PropertyVetoException e) 
		{
			e.printStackTrace();
		} 
		catch (JSMLException e) 
		{
			e.printStackTrace();
		} 
	}

	public void addCharacter(Element element,
			edu.cmu.cs.stage3.math.Matrix44 targetTransformation,
			edu.cmu.cs.stage3.alice.core.ReferenceFrame asSeenBy) {
		if (element != null) {
			element.name.set(AuthoringToolResources.getNameForNewChild(
					element.name.getStringValue(), world));
			
			speak(element);

			world.addChild(element);
			world.sandboxes.add(element);

			if (element instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
				int animateStyle = 0;
				edu.cmu.cs.stage3.alice.core.Transformable model = (edu.cmu.cs.stage3.alice.core.Transformable) element;
				if (targetTransformation != null) {
					try {
						boolean tempListening = AuthoringTool.this
						.getUndoRedoStack().getIsListening();
						AuthoringTool.this.getUndoRedoStack().setIsListening(
								false);
						model.vehicle.set(world);
						edu.cmu.cs.stage3.math.Box boundingBox = model
						.getBoundingBox();
						javax.vecmath.Vector3d insertionPoint = boundingBox
						.getCenterOfBottomFace();
						model
						.setAbsoluteTransformationRightNow(targetTransformation);
						model.moveRightNow(edu.cmu.cs.stage3.math.MathUtilities
								.negate(boundingBox.getCenterOfBottomFace()));
						// model.vehicle.set(null);
						animateStyle = 2;
						AuthoringTool.this.getUndoRedoStack().setIsListening(
								tempListening);
					} catch (Exception e) {
						animateStyle = 1;
					}
				} else {
					animateStyle = 1;
				}

				promptForVisualizationInfo(element);
				if (animateStyle > 0) {
					if (animateStyle == 1
							&& getCurrentCamera() instanceof edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) {
						animateAddModel(
								model,
								world,
								(edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) getCurrentCamera());
					} else if (animateStyle == 2
							|| !(getCurrentCamera() instanceof edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera)) {
						animateAddModel(model, world, null);
					}
				}
			}
		} else {
			AuthoringTool.showErrorDialog("null Element encountered", null);
		}
	}

	public void addCharacter(Element element,
			edu.cmu.cs.stage3.math.Matrix44 targetTransformation) {
		addCharacter(element, targetTransformation, null);
	}

	public void addCharacter(Element element) {
		addCharacter(element, null, null);
	}

	public void promptForVisualizationInfo(Element element) {
		if (element instanceof edu.cmu.cs.stage3.alice.core.visualization.CollectionOfModelsVisualization) {
			String typeString = "array";
			if (element instanceof edu.cmu.cs.stage3.alice.core.visualization.ListOfModelsVisualization) {
				typeString = "list";
			} else if (element instanceof edu.cmu.cs.stage3.alice.core.visualization.ArrayOfModelsVisualization) {
				typeString = "array";
			}
			edu.cmu.cs.stage3.alice.core.visualization.CollectionOfModelsVisualization visualization = (edu.cmu.cs.stage3.alice.core.visualization.CollectionOfModelsVisualization) element;

			CollectionEditorPanel collectionEditorPanel = GUIFactory
			.getCollectionEditorPanel();
			collectionEditorPanel.setCollection(visualization
					.getItemsCollection());
			DialogManager.showMessageDialog(collectionEditorPanel,
					"Initialize " + typeString, JOptionPane.PLAIN_MESSAGE);
		}
	}

	public void saveCharacter(Element element) {
		String characterFilename = element.name.getStringValue() + "."
		+ CHARACTER_EXTENSION;
		characterFilename = characterFilename.substring(0, 1).toUpperCase()
		+ characterFilename.substring(1);
		saveCharacterFileDialog.setFile(characterFilename);
		saveCharacterFileDialog.setVisible(true);
		AuthoringToolResources.centerComponentOnScreen(saveCharacterFileDialog);
		if (saveCharacterFileDialog.getFile() != null) {
			String filename = saveCharacterFileDialog.getFile();
			if (!filename.endsWith("." + CHARACTER_EXTENSION)) {
				filename = filename + "." + CHARACTER_EXTENSION;
			}
			File openFile = new File(saveCharacterFileDialog.getDirectory(),
					filename);
			saveCharacter(element, openFile);
		}
	}

	public void saveCharacter(Element element, File file) {
		characterStoreProgressPane.setElement(element);
		characterStoreProgressPane.setFile(file);
		characterStoreProgressPane.setFilnameToByteArrayMap(null);
		DialogManager.showDialog(characterStoreProgressPane);
	}

	public Element importElement() {
		return importElement(null);
	}

	public Element importElement(Object path) {
		return importElement(path, null);
	}

	public Element importElement(Object path, Element parent) {
		return importElement(path, parent, null);
	}

	public Element importElement(Object path, Element parent,
			PostImportRunnable postImportRunnable) {
		return importElement(path, parent, postImportRunnable, true);
	}

	public Element importElement(Object path, Element parent,
			PostImportRunnable postImportRunnable, boolean animateOnAdd) {
		Element element = null;

		if (path == null) {
			importFileChooser.rescanCurrentDirectory();
			int returnVal = DialogManager.showDialog(importFileChooser, null);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				final File file = importFileChooser.getSelectedFile();
				if (file.getAbsolutePath().toLowerCase().endsWith(
						AuthoringTool.CHARACTER_EXTENSION)) { // special case
					// Alice objects
					return loadAndAddCharacter(file);
				} else {
					path = file;
				}
			}
		}
		if (path != null) {
			String ext = null, s;
			if (path instanceof String) {
				s = (String) path;
				ext = s.substring(s.lastIndexOf('.') + 1).toUpperCase();
			} else if (path instanceof File) {
				s = ((File) path).getAbsolutePath();
				ext = s.substring(s.lastIndexOf('.') + 1).toUpperCase();
				path = ((File) path).getAbsoluteFile();
			} else if (path instanceof java.net.URL) {
				s = ((java.net.URL) path).getPath();
				ext = s.substring(s.lastIndexOf('.') + 1).toUpperCase();
			} else {
				throw new IllegalArgumentException(
				"path must be a String, File, or java.net.URL");
			}

			if (parent == null) {
				parent = world;
			}

			Importer importerToUse = null;
			for (java.util.Iterator iter = importing.getImporters().iterator(); iter
			.hasNext();) {
				Importer importer = (Importer) iter.next();

				if (importer.getExtensionMap().get(ext) != null) {
					importerToUse = importer;
					break;
				}
			}

			if (importerToUse != null) {
				AuthoringTool.this.undoRedoStack.startCompound();

				try {
					if (path instanceof String) {
						element = importerToUse.load((String) path);
					} else if (path instanceof File) {
						element = importerToUse.load((File) path);
					} else if (path instanceof java.net.URL) {
						element = importerToUse.load((java.net.URL) path);
					}

					if (element != null) {
						if (element.getParent() != parent) {
							String name = element.name.getStringValue();
							element.name.set(AuthoringToolResources
									.getNameForNewChild(name, parent));
							if ((parent != null)) {
								parent.addChild(element);
								AuthoringToolResources
								.addElementToAppropriateProperty(
										element, parent);
							}
							if (animateOnAdd) {
								animateAddModelIfPossible(element, parent);
							} else {
								// makeIDVisible();
							}
							if (postImportRunnable != null) {
								postImportRunnable.setImportedElement(element);
								postImportRunnable.run();
							}
						}
					} else {
						AuthoringTool.showErrorDialog(
								"Corrupted file or incorrect file type.", null,
								false);
					}
				} catch (IOException e) {
					AuthoringTool.showErrorDialog(
							"Error while importing object.", e);
				}
				AuthoringTool.this.undoRedoStack.stopCompound();
			} else {
				DialogManager.showMessageDialog(
						"No importer found to load given file type.",
						"Import error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			return null;
		}

		return element;
	}

	private void animateAddModelIfPossible(Element element, Element parent) {
		if (element instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
			if (parent instanceof edu.cmu.cs.stage3.alice.core.ReferenceFrame) {
				if (AuthoringTool.this.getCurrentCamera() instanceof edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) {
					animateAddModel(
							(edu.cmu.cs.stage3.alice.core.Transformable) element,
							(edu.cmu.cs.stage3.alice.core.ReferenceFrame) parent,
							(edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) AuthoringTool.this
							.getCurrentCamera());
				} else {
					((edu.cmu.cs.stage3.alice.core.Transformable) element).vehicle
					.set((edu.cmu.cs.stage3.alice.core.ReferenceFrame) parent);
				}
			} else {
				if (AuthoringTool.this.getCurrentCamera() instanceof edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) {
					animateAddModel(
							(edu.cmu.cs.stage3.alice.core.Transformable) element,
							world,
							(edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) AuthoringTool.this
							.getCurrentCamera());
				} else {
					((edu.cmu.cs.stage3.alice.core.Transformable) element).vehicle
					.set(world);
				}
			}
		}
	}

	public void animateAddModel(
			edu.cmu.cs.stage3.alice.core.Transformable transformable,
			edu.cmu.cs.stage3.alice.core.ReferenceFrame vehicle,
			edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera camera) {
		if (transformable instanceof edu.cmu.cs.stage3.alice.core.Model) {
			boolean shouldAnimateCamera = (camera != null);
			edu.cmu.cs.stage3.alice.core.Model model = (edu.cmu.cs.stage3.alice.core.Model) transformable;

			HashMap opacityMap = new HashMap();
			Vector properties = new Vector();
			if (shouldAnimateCamera) {
				properties.add(camera.localTransformation);
				properties.add(camera.farClippingPlaneDistance);
			}
			properties.add(model.vehicle);
			Element[] descendants = model.getDescendants(
					edu.cmu.cs.stage3.alice.core.Model.class,
					HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
			for (int i = 0; i < descendants.length; i++) {
				opacityMap
				.put(
						descendants[i],
						((edu.cmu.cs.stage3.alice.core.Model) descendants[i]).opacity
						.get());
				properties
				.add(((edu.cmu.cs.stage3.alice.core.Model) descendants[i]).opacity);
			}
			edu.cmu.cs.stage3.alice.core.Property[] affectedProperties = (edu.cmu.cs.stage3.alice.core.Property[]) properties
			.toArray(new edu.cmu.cs.stage3.alice.core.Property[0]);
			boolean tempListening = AuthoringTool.this.getUndoRedoStack()
			.getIsListening();
			undoRedoStack.setIsListening(false);
			model.opacity.set(new Double(0.0),
					HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
			javax.vecmath.Matrix4d goodLook = null;
			if (shouldAnimateCamera) {
				model.vehicle.set(vehicle);
				goodLook = camera.calculateGoodLookAt(model);
			}
			model.vehicle.set(null);
			undoRedoStack.setIsListening(tempListening);

			double distanceToBackOfObject = 0.0;
			boolean needToChangeFarClipping = false;
			if (shouldAnimateCamera) {
				distanceToBackOfObject = AuthoringToolResources
				.distanceToBackAfterGetAGoodLookAt(model, camera);
				needToChangeFarClipping = distanceToBackOfObject > camera.farClippingPlaneDistance
				.doubleValue();
			}

			// getAGoodLook response
			PropertyAnimation setupOpacity = new PropertyAnimation();
			setupOpacity.element.set(model);
			setupOpacity.propertyName.set("opacity");
			setupOpacity.value.set(new Double(0.0));
			setupOpacity.duration.set(new Double(0.0));
			setupOpacity.howMuch.set(HowMuch.INSTANCE_AND_ALL_DESCENDANTS);
			PropertyAnimation vehicleAnimation = new PropertyAnimation();
			vehicleAnimation.element.set(model);
			vehicleAnimation.propertyName.set("vehicle");
			vehicleAnimation.value.set(vehicle);
			vehicleAnimation.duration.set(new Double(0.0));
			vehicleAnimation.howMuch.set(HowMuch.INSTANCE);
			PointOfViewAnimation getAGoodLook = new PointOfViewAnimation();
			if (shouldAnimateCamera) {
				getAGoodLook.subject.set(camera);
				getAGoodLook.pointOfView.set(goodLook);
			}

			PointOfViewAnimation cameraGoBack = new PointOfViewAnimation();
			if (shouldAnimateCamera) {
				cameraGoBack.subject.set(camera);
				cameraGoBack.pointOfView.set(camera.getLocalTransformation());
				cameraGoBack.duration.set(new Double(.5));
			}
			Wait wait = new Wait();
			wait.duration.set(new Double(.7));
			Wait wait2 = new Wait();
			wait2.duration.set(new Double(.2));
			PropertyAnimation farClipping = new PropertyAnimation();
			if (shouldAnimateCamera) {
				farClipping.element.set(camera);
				farClipping.propertyName.set("farClippingPlaneDistance");
				farClipping.value.set(new Double(distanceToBackOfObject));
			}
			PropertyAnimation farClipping2 = new PropertyAnimation();
			if (shouldAnimateCamera) {
				farClipping2.element.set(camera);
				farClipping2.propertyName.set("farClippingPlaneDistance");
				farClipping2.value.set(camera.farClippingPlaneDistance.get());
			}
			DoTogether opacityDoTogether = new DoTogether();
			for (java.util.Iterator iter = opacityMap.keySet().iterator(); iter
			.hasNext();) {
				edu.cmu.cs.stage3.alice.core.Model m = (edu.cmu.cs.stage3.alice.core.Model) iter
				.next();
				Object opacity = opacityMap.get(m);
				PropertyAnimation opacityAnimation = new PropertyAnimation();
				opacityAnimation.element.set(m);
				opacityAnimation.propertyName.set("opacity");
				opacityAnimation.value.set(opacity);
				opacityAnimation.howMuch.set(HowMuch.INSTANCE);
				opacityDoTogether.componentResponses.add(opacityAnimation);
			}
			DoInOrder waitOpacityDoInOrder = new DoInOrder();
			waitOpacityDoInOrder.componentResponses.add(wait);
			waitOpacityDoInOrder.componentResponses.add(opacityDoTogether);
			waitOpacityDoInOrder.componentResponses.add(wait2);
			DoTogether cameraOpacityDoTogether = new DoTogether();
			if (needToChangeFarClipping) {
				cameraOpacityDoTogether.componentResponses.add(farClipping);
			}
			cameraOpacityDoTogether.componentResponses.add(getAGoodLook);
			cameraOpacityDoTogether.componentResponses
			.add(waitOpacityDoInOrder);
			DoInOrder response = new DoInOrder();
			response.componentResponses.add(setupOpacity);
			response.componentResponses.add(vehicleAnimation);
			if (shouldAnimateCamera) {
				response.componentResponses.add(cameraOpacityDoTogether);
				response.componentResponses.add(cameraGoBack);
				response.componentResponses.add(farClipping2);
			} else {
				response.componentResponses.add(opacityDoTogether);
			}
			// getAGoodLook undoResponse
			PropertyAnimation undoVehicleAnimation = new PropertyAnimation();
			undoVehicleAnimation.element.set(model);
			undoVehicleAnimation.propertyName.set("vehicle");
			undoVehicleAnimation.value.set(null);
			undoVehicleAnimation.duration.set(new Double(0.0));
			undoVehicleAnimation.howMuch.set(HowMuch.INSTANCE);
			PointOfViewAnimation undoGetAGoodLook = new PointOfViewAnimation();
			if (shouldAnimateCamera) {
				undoGetAGoodLook.subject.set(camera);
				undoGetAGoodLook.pointOfView.set(goodLook);
				undoGetAGoodLook.duration.set(new Double(.5));
			}
			PointOfViewAnimation undoCameraGoBack = new PointOfViewAnimation();
			if (shouldAnimateCamera) {
				undoCameraGoBack.subject.set(camera);
				undoCameraGoBack.pointOfView.set(camera
						.getLocalTransformation());
			}
			Wait undoWait = new Wait();
			undoWait.duration.set(new Double(.9));
			Wait undoWait2 = new Wait();
			undoWait2.duration.set(new Double(.2));
			PropertyAnimation undoFarClipping = new PropertyAnimation();
			if (shouldAnimateCamera) {
				undoFarClipping.element.set(camera);
				undoFarClipping.propertyName.set("farClippingPlaneDistance");
				undoFarClipping.value.set(new Double(distanceToBackOfObject));
				undoFarClipping.duration.set(new Double(.2));
			}
			PropertyAnimation undoFarClipping2 = new PropertyAnimation();
			if (shouldAnimateCamera) {
				undoFarClipping2.element.set(camera);
				undoFarClipping2.propertyName.set("farClippingPlaneDistance");
				undoFarClipping2.value.set(camera.farClippingPlaneDistance
						.get());
				undoFarClipping2.duration.set(new Double(.2));
			}
			DoInOrder undoCameraGoBackWaitDoInOrder = new DoInOrder();
			undoCameraGoBackWaitDoInOrder.componentResponses.add(undoWait);
			undoCameraGoBackWaitDoInOrder.componentResponses
			.add(undoCameraGoBack);
			DoTogether undoOpacityDoTogether = new DoTogether();
			for (java.util.Iterator iter = opacityMap.keySet().iterator(); iter
			.hasNext();) {
				edu.cmu.cs.stage3.alice.core.Model m = (edu.cmu.cs.stage3.alice.core.Model) iter
				.next();
				PropertyAnimation opacityAnimation = new PropertyAnimation();
				opacityAnimation.element.set(m);
				opacityAnimation.propertyName.set("opacity");
				opacityAnimation.value.set(new Double(0.0));
				opacityAnimation.howMuch.set(HowMuch.INSTANCE);
				undoOpacityDoTogether.componentResponses.add(opacityAnimation);
			}
			DoInOrder undoOpacityWaitDoInOrder = new DoInOrder();
			undoOpacityWaitDoInOrder.componentResponses.add(undoWait2);
			undoOpacityWaitDoInOrder.componentResponses
			.add(undoOpacityDoTogether);
			DoTogether undoCameraOpacityDoTogether = new DoTogether();
			undoCameraOpacityDoTogether.componentResponses
			.add(undoOpacityWaitDoInOrder);
			undoCameraOpacityDoTogether.componentResponses
			.add(undoCameraGoBackWaitDoInOrder);
			if (needToChangeFarClipping) {
				undoCameraOpacityDoTogether.componentResponses
				.add(undoFarClipping);
			}
			DoInOrder undoResponse = new DoInOrder();
			if (shouldAnimateCamera) {
				undoResponse.componentResponses.add(undoGetAGoodLook);
				undoResponse.componentResponses
				.add(undoCameraOpacityDoTogether);
				if (needToChangeFarClipping) {
					undoResponse.componentResponses.add(undoFarClipping2);
				}
			} else {
				undoResponse.componentResponses.add(undoOpacityWaitDoInOrder);
			}
			undoResponse.componentResponses.add(undoVehicleAnimation);
			// displayDurations(undoResponse);
			performOneShot(response, undoResponse, affectedProperties);
		} else {
			transformable.vehicle.set(vehicle);
		}
	}

	private void displayDurations(edu.cmu.cs.stage3.alice.core.Response r) {
		if (r instanceof CompositeResponse) {
			CompositeResponse c = (CompositeResponse) r;
			System.out.println("COMPOSITE(");
			for (int i = 0; i < c.componentResponses.size(); i++) {
				displayDurations((edu.cmu.cs.stage3.alice.core.Response) c.componentResponses
						.get(i));
			}
			System.out.println(")");
		} else {
			System.out.println(r.duration.doubleValue());
		}
	}

	public void getAGoodLookAt(
			edu.cmu.cs.stage3.alice.core.Transformable transformable,
			final edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera camera) {
		edu.cmu.cs.stage3.alice.core.Property[] affectedProperties = { camera.localTransformation };

		// javax.vecmath.Matrix4d goodLook =
		// AuthoringToolResources.getAGoodLookAtMatrix( transformable, camera );
		javax.vecmath.Matrix4d goodLook = camera
		.calculateGoodLookAt(transformable);

		PointOfViewAnimation getAGoodLook = new PointOfViewAnimation();
		getAGoodLook.subject.set(camera);
		getAGoodLook.pointOfView.set(goodLook);

		PointOfViewAnimation undoResponse = new PointOfViewAnimation();
		undoResponse.subject.set(camera);
		undoResponse.pointOfView.set(camera.getLocalTransformation());

		performOneShot(getAGoodLook, undoResponse, affectedProperties);

		final double distanceToBackOfObject = AuthoringToolResources
		.distanceToBackAfterGetAGoodLookAt(transformable, camera);

		if (distanceToBackOfObject > camera.farClippingPlaneDistance
				.doubleValue()) {
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						AuthoringTool.showErrorDialog(
								"Interrupted during clipping plane operation.",
								e);
					}
					int result = DialogManager
					.showConfirmDialog(
							"The camera's far clipping plane is too close to see all of the object.  Would you like to move it back?",
							"Alter camera's far clipping plane?",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.INFORMATION_MESSAGE);
					if (result == JOptionPane.YES_OPTION) {
						edu.cmu.cs.stage3.alice.core.Property[] affectedProperties = { camera.farClippingPlaneDistance };

						PropertyAnimation farClippingAnimation = new PropertyAnimation();
						farClippingAnimation.element.set(camera);
						farClippingAnimation.propertyName
						.set("farClippingPlaneDistance");
						farClippingAnimation.value.set(new Double(
								distanceToBackOfObject));

						PropertyAnimation undoResponse = new PropertyAnimation();
						undoResponse.element.set(camera);
						undoResponse.propertyName
						.set("farClippingPlaneDistance");
						undoResponse.value.set(camera.farClippingPlaneDistance
								.get());

						performOneShot(farClippingAnimation, undoResponse,
								affectedProperties);
					}
				}
			};
			javax.swing.SwingUtilities.invokeLater(runnable);
		}
	}

	public void saveTabs() {
		if (saveTabsEnabled && (world != null)) {
			Object[] objects = jAliceFrame.getTabbedEditorComponent()
			.getObjectsBeingEdited();
			String tabObjectsString = "";
			for (int i = 0; i < objects.length; i++) {
				if (objects[i] instanceof Element) { // TODO:
					// handle
					// non
					// Elements?
					tabObjectsString += ((Element) objects[i]).getKey() + ":";
				}
			}
			world.data.put("edu.cmu.cs.stage3.alice.authoringtool.tabObjects",
					tabObjectsString);
		}
	}

	public void loadTabs() {
		if (authoringToolConfig.getValue("loadSavedTabs").equalsIgnoreCase(
		"true")) {
			if (world != null) {
				String tabObjectsString = (String) world.data
				.get("edu.cmu.cs.stage3.alice.authoringtool.tabObjects");
				if (tabObjectsString != null) {
					java.util.StringTokenizer st = new java.util.StringTokenizer(
							tabObjectsString, ":");
					getJAliceFrame()
					.setCursor(
							java.awt.Cursor
							.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
					while (st.hasMoreTokens()) {
						String key = st.nextToken();
						key = key.substring(world.getKey().length()
								+ (key.equals(world.getKey()) ? 0 : 1));
						Element element = world.getDescendantKeyed(key);
						if (element != null) {
							editObject(element, false); // TODO: handle
							// different types of
							// editors
						}
					}
					getJAliceFrame().setCursor(
							java.awt.Cursor.getDefaultCursor());
				}
			}
		}
	}

	public boolean isImportable(String extension) {
		for (java.util.Iterator iter = importing.getImporters().iterator(); iter
		.hasNext();) {
			edu.cmu.cs.stage3.alice.authoringtool.Importer importer = (edu.cmu.cs.stage3.alice.authoringtool.Importer) iter
			.next();
			java.util.Map map = importer.getExtensionMap();
			if (map.get(extension.toUpperCase()) != null) {
				return true;
			}
		}

		return false;
	}

	public void setImportFileFilter(String extensionString) {
		FileFilter filter = (FileFilter) extensionStringsToFileFilterMap
		.get(extensionString);
		if (filter != null) {
			importFileChooser.setFileFilter(filter);
		}
	}

	public void showWorldInfoDialog() {
		updateWorldOpenTime();
		worldInfoContentPane.setWorld(world);
		DialogManager.showDialog(worldInfoContentPane);
	}

	private java.text.DecimalFormat captureFormatter = new java.text.DecimalFormat();

	public void storeCapturedImage(java.awt.Image image) {
		File dir = new File(authoringToolConfig
				.getValue("screenCapture.directory"));

		int numDigits = Integer.parseInt(authoringToolConfig
				.getValue("screenCapture.numDigits"));
		StringBuffer pattern = new StringBuffer(authoringToolConfig
				.getValue("screenCapture.baseName"));
		String codec = authoringToolConfig.getValue("screenCapture.codec");
		pattern.append("#");
		for (int i = 0; i < numDigits; i++) {
			pattern.append("0");
		}
		pattern.append(".");
		pattern.append(codec);
		captureFormatter.applyPattern(pattern.toString());

		int i = 0;
		File file = new File(dir, captureFormatter.format(i));
		boolean writable;
		if (file.exists()) {
			writable = file.canWrite();
		} else {
			try {
				boolean success = file.createNewFile();
				writable = success;
				if (success) {
					file.delete();
				}
			} catch (Throwable e) {
				writable = false;
			}
		}

		if (!writable) {
			Object[] options = { "Yes, let me select a directory",
			"No, I don't want to take a picture anymore" };
			int dialogVal = DialogManager
			.showOptionDialog(
					"Alice can not save the captured image. Do you want to select a new directory?",
					"Alice can't save the file",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options,
					options[0]);
			if (dialogVal == JOptionPane.YES_OPTION) {
				File parent = dir.getParentFile();
				try {
					browseFileChooser.setCurrentDirectory(parent);
				} catch (ArrayIndexOutOfBoundsException aioobe) {
					// for some reason this can potentially fail in jdk1.4.2_04
				}
				boolean done = false;
				while (!done) {
					int returnVal = DialogManager
					.showOpenDialog(browseFileChooser);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File selectedFile = browseFileChooser.getSelectedFile();
						File testCaptureFile = new File(selectedFile,
								"test.jpg");
						if (testCaptureFile.exists()) {
							writable = testCaptureFile.canWrite();
						} else {
							try {
								boolean success = testCaptureFile
								.createNewFile();
								writable = success;
								if (success) {
									testCaptureFile.delete();
								}
							} catch (Exception e) {
								writable = false;
							}
						}
						if (!writable) {
							DialogManager
							.showMessageDialog("The capture directory specified can not be written to. Please choose another directory.");
						} else {
							done = true;
							dir = selectedFile;
						}
					} else {
						DialogManager
						.showMessageDialog(
								"You have not selected a writable directory to save pictures to.\n"
								+ "Alice will not be able to take pictures until you do so. You can go to Preferences->Screen Grab to set a directory.",
								"No directory set",
								JOptionPane.WARNING_MESSAGE);
						return;
					}
				}
			} else {
				DialogManager
				.showMessageDialog(
						"You have not selected a writable directory to save pictures to.\n"
						+ "Alice will not be able to take pictures until you do so. You can go to Preferences->Screen Grab to set a directory.",
						"No directory set", JOptionPane.WARNING_MESSAGE);
				return;
			}
			file = new File(dir, captureFormatter.format(i));
		}
		while (file.exists()) {
			i++;
			file = new File(dir, captureFormatter.format(i));
		}
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			DataOutputStream dos = new DataOutputStream(bos);
			edu.cmu.cs.stage3.image.ImageIO.store(codec, dos, image);
			dos.flush();
			fos.close();

			if (authoringToolConfig.getValue("screenCapture.informUser")
					.equalsIgnoreCase("true")) {
				java.awt.image.BufferedImage scaledImage = GUIEffects
				.getImageScaledToLongestDimension(image, 128);
				CapturedImageContentPane capturedImageContentPane = new CapturedImageContentPane();
				capturedImageContentPane.setStoreLocation(file
						.getCanonicalPath());
				capturedImageContentPane.setImage(scaledImage);
				DialogManager.showDialog(capturedImageContentPane);
			}
			// } catch( InterruptedException ie ) {
			// } catch( IOException ioe ) {
		} catch (Throwable t) {
			AuthoringTool.showErrorDialog(
					"Error while storing screen capture.", t);
		}
	}

	public void performPointOfViewOneShot(
			edu.cmu.cs.stage3.alice.core.Transformable transformable,
			edu.cmu.cs.stage3.math.Matrix44 newTransformation) {
		edu.cmu.cs.stage3.math.Matrix44 oldTransformation = transformable.localTransformation
		.getMatrix44Value();

		PointOfViewAnimation povAnimation = new PointOfViewAnimation();
		povAnimation.subject.set(transformable);
		povAnimation.pointOfView.set(newTransformation);

		OneShotSimpleBehavior oneShotBehavior = new OneShotSimpleBehavior();
		oneShotBehavior.setResponse(povAnimation);
		oneShotBehavior
		.setAffectedProperties(new edu.cmu.cs.stage3.alice.core.Property[] { transformable.localTransformation });
		oneShotBehavior.start(oneShotScheduler);

		PointOfViewUndoableRedoable undo = new PointOfViewUndoableRedoable(
				transformable, oldTransformation, newTransformation,
				oneShotScheduler);
		undoRedoStack.push(undo);
	}

	public void performOneShot(edu.cmu.cs.stage3.alice.core.Response response,
			edu.cmu.cs.stage3.alice.core.Response undoResponse,
			edu.cmu.cs.stage3.alice.core.Property[] affectedProperties) {
		OneShotSimpleBehavior oneShotBehavior = new OneShotSimpleBehavior();
		oneShotBehavior.setResponse(response);
		oneShotBehavior.setAffectedProperties(affectedProperties);
		oneShotBehavior.start(oneShotScheduler);

		OneShotUndoableRedoable undo = new OneShotUndoableRedoable(response,
				undoResponse, oneShotBehavior, oneShotScheduler);
		undoRedoStack.push(undo);
	}

	public void lostOwnership(java.awt.datatransfer.Clipboard clipboard,
			java.awt.datatransfer.Transferable contents) {
		// TODO: store a reference to CastMembers until ownership is lost, then
		// put the whole thing in the clipboard
	}

	/*
	 * public void editCharacter( edu.cmu.cs.stage3.alice.core.Transformable
	 * character ) { characterEditorDialog.setCharacter( character ); if( !
	 * characterEditorDialog.isShowing() ) { characterEditorDialog.show(); } }
	 */

	public void makeBillboard() {
		setImportFileFilter("Image Files");
		importElement(null, null, new PostImportRunnable() {
			public void run() {
				edu.cmu.cs.stage3.alice.core.TextureMap textureMap = (edu.cmu.cs.stage3.alice.core.TextureMap) getImportedElement();
				if (textureMap != null) {
					textureMap.removeFromParent();
					textureMap.name.set(AuthoringToolResources
							.getNameForNewChild(textureMap.name
									.getStringValue(), world));
					edu.cmu.cs.stage3.alice.core.Billboard billboard = AuthoringToolResources
					.makeBillboard(textureMap, true);
					animateAddModelIfPossible(billboard, world);
					world.addChild(billboard);
					billboard.vehicle.set(world);
					world.sandboxes.add(billboard);
				}
			}
		}, true);
		// importObject( null, new
		// BillboardPostWorker( world
		// ) );
	}

	public void setElementScope(Element element) {
		jAliceFrame.getWorldTreeComponent().setCurrentScope(element);
		if (element != null) {
			if (selectedElement != null) {
				if (!selectedElement.isDescendantOf(element)) {
					setSelectedElement(element);
				}
			} else {
				setSelectedElement(element);
			}
		}
	}

	// TODO: handle this correctly
	public edu.cmu.cs.stage3.alice.core.Camera getCurrentCamera() {
		// Element[] e = world.search(new
		// criterion.InstanceOfCriterion(edu.cmu.cs.stage3.alice.core.Camera.class));
		Element[] e = world
		.getDescendants(edu.cmu.cs.stage3.alice.core.Camera.class);
		if (e != null) {
			return (edu.cmu.cs.stage3.alice.core.Camera) e[0];
		} else {
			return null;
		}
	}

	public void showAbout() {
		DialogManager.showDialog(aboutContentPane);
	}

	public void showPreferences() {
		int result = DialogManager.showDialog(preferencesContentPane);
		if (result == edu.cmu.cs.stage3.swing.ContentPane.OK_OPTION) {
			preferencesContentPane.finalizeSelections();
		}
	}

	/*
	 * public static void showSaveErrorDialog(String message, Throwable t) {
	 * getHack().jAliceFrame.HACK_goToRedAlert(); SaveErrorContentPane errorPane
	 * = new SaveErrorContentPane(); errorPane.setMessage(message);
	 * errorPane.setSubmitBugButtonEnabled( true ); errorPane.setThrowable(t);
	 * int result = DialogManager.showDialog(errorPane); if
	 * (getHack().isStdErrToConsole() && (t != null)) {
	 * t.printStackTrace(System.err); } }
	 */
	public static void showErrorDialog(String message, Object t) {
		showErrorDialog(message, t, false);
	}

	public static void showErrorDialog(String message, Object t,
			boolean showSubmitBugButton) {
		ErrorContentPane errorPane = new ErrorContentPane();
		errorPane.setMessage(message);
		// errorPane.setSubmitBugButtonEnabled(showSubmitBugButton);
		if (t instanceof Throwable)
			errorPane.setThrowable((Throwable) t);
		else
			errorPane.setDetails((String) t);
		int result = DialogManager.showDialog(errorPane);
		if (t instanceof Throwable) {
			if (getHack().isStdErrToConsole() && (t != null)) {
				Throwable tt = (Throwable) t;
				tt.printStackTrace(System.err);
			}
		}
	}

	/*
	 * public static void showErrorDialogWithDetails(String message, String
	 * details) { showErrorDialogWithDetails(message, details, true); }
	 * 
	 * public static void showErrorDialogWithDetails(String message, String
	 * details, boolean showSubmitBugButton) { ErrorContentPane errorPane = new
	 * ErrorContentPane(); errorPane.setMessage(message);
	 * errorPane.setSubmitBugButtonEnabled(showSubmitBugButton); int result =
	 * DialogManager.showDialog(errorPane); // if( getHack().isStdErrToConsole()
	 * && (t != null) ) { // t.printStackTrace( System.err ); // } }
	 */
	private void stopWorldAndShowDialog(Throwable throwable) {
		stopWorld();
		if (throwable instanceof edu.cmu.cs.stage3.alice.core.ExceptionWrapper) {
			throwable = ((edu.cmu.cs.stage3.alice.core.ExceptionWrapper) throwable)
			.getWrappedException();
		}
		final Throwable t = throwable;
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (t instanceof edu.cmu.cs.stage3.alice.core.SimulationException) {
					showSimulationExceptionDialog((edu.cmu.cs.stage3.alice.core.SimulationException) t);
				} else {
					showErrorDialog("Error during simulation.", t);
				}
			}
		});
	}

	public void showSimulationExceptionDialog(
			edu.cmu.cs.stage3.alice.core.SimulationException simulationException) {
		SimulationExceptionPanel simulationExceptionPanel = new SimulationExceptionPanel(
				this);
		simulationExceptionPanel.setSimulationException(simulationException);
		simulationExceptionPanel.setErrorHighlightingEnabled(true);
		DialogManager.showMessageDialog(simulationExceptionPanel,
				"Problem Detected", JOptionPane.ERROR_MESSAGE,
				AuthoringToolResources.getAliceSystemIcon());
		simulationExceptionPanel.setErrorHighlightingEnabled(false);
	}

	public JFileChooser getImportFileChooser() {
		return importFileChooser;
	}

	public World getWorld() {
		return world;
	}

	public void showOnScreenHelp() {
		showStencils();
	}

	public void showStdErrOutDialog() {
		stdErrOutContentPane.setMode(StdErrOutContentPane.HISTORY_MODE);
		stdErrOutContentPane.showStdErrOutDialog();
	}

	public void updateWorldOpenTime() {
		if (world != null) {
			String worldOpenTimeString = (String) world.data
			.get("edu.cmu.cs.stage3.alice.authoringtool.worldOpenTime");
			long worldOpenTime = 0;
			if (worldOpenTimeString != null) {
				worldOpenTime = Long.parseLong(worldOpenTimeString);
			}
			worldOpenTime += System.currentTimeMillis() - worldLoadedTime;
			worldLoadedTime = System.currentTimeMillis();
			world.data.put(
					"edu.cmu.cs.stage3.alice.authoringtool.worldOpenTime", Long
					.toString(worldOpenTime));
		}
	}

	public void countSomething(String dataKey) {
		String countString = (String) world.data.get(dataKey);
		int count = 0;
		if (countString != null) {
			count = Integer.parseInt(countString);
		}
		count++;
		world.data.put(dataKey, Integer.toString(count));
	}

	public void showPrintDialog() {
		int result = DialogManager.showDialog(exportCodeForPrintingContentPane);
		if (result == edu.cmu.cs.stage3.swing.ContentPane.OK_OPTION) {
			final File fileToExportTo = exportCodeForPrintingContentPane
			.getFileToExportTo();
			edu.cmu.cs.stage3.progress.ProgressPane progressPane = new edu.cmu.cs.stage3.progress.ProgressPane(
					"Saving HTML...", "Saving: ") {
				protected void construct()
				throws edu.cmu.cs.stage3.progress.ProgressCancelException {
					try {
						StringBuffer htmlOutput = new StringBuffer();
						exportCodeForPrintingContentPane.getHTML(htmlOutput,
								fileToExportTo, true, true, this);
						OutputStreamWriter writer = new OutputStreamWriter(
								new FileOutputStream(fileToExportTo));
						writer.write(htmlOutput.toString());
						writer.flush();
						writer.close();
					} catch (edu.cmu.cs.stage3.progress.ProgressCancelException pce) {
						fileToExportTo.delete();
						throw pce;
					} catch (Throwable t) {
						AuthoringTool.showErrorDialog(
								"Unable to store world to file: "
								+ fileToExportTo, t);
						fileToExportTo.delete();
					}
				}
			};
			DialogManager.showDialog(progressPane);
		}
	}

	// /////////////////////////////
	// Private methods
	// /////////////////////////////

	private void setCurrentWorldLocation(File file) {
		currentWorldLocation = file;
		updateTitle();
	}

	private void updateTitle() {
		String path = "";
		if (currentWorldLocation != null) {
			try {
				path = currentWorldLocation.getCanonicalPath();
			} catch (IOException e) {
				path = currentWorldLocation.getAbsolutePath();
			}
		}

		String modifiedStatus = "";
		if (worldHasBeenModified) {
			modifiedStatus = " [Modified]";
		}

		jAliceFrame.setTitle("Alice (" + JAlice.getVersion() + ") - " + path
				+ modifiedStatus);
	}

	public Component getEditorForElement(Element elementToEdit) {
		int index = jAliceFrame.tabbedEditorComponent
		.getIndexOfObject(elementToEdit);
		if (index > -1) {
			return jAliceFrame.tabbedEditorComponent.getEditorAt(index)
			.getJComponent();
		} else {
			Class editorClass = EditorUtilities.getBestEditor(elementToEdit
					.getClass());
			Editor editor = editorManager.getEditorInstance(editorClass);
			EditorUtilities.editObject(editor, elementToEdit);
			return editor.getJComponent();
		}
	}

	// /////////////
	// Stencils
	// /////////////
	protected HashMap componentMap = new HashMap();
	protected StencilManager stencilManager;
	protected HashSet classesToStopOn = new HashSet();
	protected javax.swing.Timer updateTimer;
	protected boolean stencilDragging = false;
	protected Component dragStartSource;
	protected File tutorialOne;
	protected File tutorialDirectory = new File(JAlice.getAliceHomeDirectory(),
	"tutorial").getAbsoluteFile();
	protected java.util.ArrayList wayPoints = new java.util.ArrayList();

	private void stencilInit() {
		stencilManager = new StencilManager(this);
		jAliceFrame.setGlassPane(stencilManager.getStencilComponent());
		((javax.swing.JComponent) stencilManager.getStencilComponent())
		.setOpaque(false); // TODO: remove

		classesToStopOn.add(JMenu.class);
		classesToStopOn.add(AbstractButton.class);
		classesToStopOn.add(JComboBox.class);
		classesToStopOn.add(JList.class);
		classesToStopOn.add(JMenu.class);
		classesToStopOn.add(JSlider.class);
		classesToStopOn.add(JTextComponent.class);
		classesToStopOn.add(JTabbedPane.class);
		classesToStopOn.add(JTree.class);
		classesToStopOn.add(JTable.class);
		classesToStopOn
		.add(edu.cmu.cs.stage3.alice.authoringtool.DragFromComponent.class);
		classesToStopOn.add(DnDGroupingPanel.class);
		classesToStopOn.add(TrashComponent.class);
		classesToStopOn.add(PropertyViewController.class);
		classesToStopOn.add(BehaviorGroupsEditor.class);
		classesToStopOn.add(SceneEditor.class);
		classesToStopOn.add(GalleryViewer.class);
		classesToStopOn.add(GalleryObject.class);
		// classesToStopOn.add(
		// GuiNavigator.class );

		componentMap.put("fileMenu", jAliceFrame.fileMenu);
		componentMap.put("editMenu", jAliceFrame.editMenu);
		componentMap.put("toolsMenu", jAliceFrame.toolsMenu);
		componentMap.put("helpMenu", jAliceFrame.helpMenu);
		componentMap.put("playButton", jAliceFrame.playButton);
		componentMap.put("addObjectButton", jAliceFrame.addObjectButton);
		componentMap.put("undoButton", jAliceFrame.undoButton);
		componentMap.put("redoButton", jAliceFrame.redoButton);
		componentMap.put("trashComponent", jAliceFrame.trashComponent);
		componentMap.put("clipboardPanel", jAliceFrame.clipboardPanel);
		componentMap.put("objectTree", jAliceFrame.worldTreeComponent);
		componentMap.put("sceneEditor", jAliceFrame.sceneEditor);
		componentMap.put("details", jAliceFrame.dragFromComponent);
		componentMap.put("behaviors", jAliceFrame.behaviorGroupsEditor);
		componentMap.put("editors", jAliceFrame.tabbedEditorComponent);

		updateTimer = new javax.swing.Timer(100,
				new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				stencilManager.update();
				// System.out.println( "update: " +
				// System.currentTimeMillis() );
			}
		});
		updateTimer.setRepeats(false);
		AWTEventListener updateListener = new AWTEventListener() {
			public void eventDispatched(AWTEvent ev) {
				if (ev.getSource() instanceof Component) {
					if ((ev.getSource() == jAliceFrame)
							|| jAliceFrame.isAncestorOf((Component) ev
									.getSource())) {
						updateTimer.restart();
					}
				}
			}
		};
		Toolkit.getDefaultToolkit().addAWTEventListener(
				updateListener, /* AWTEvent.PAINT_EVENT_MASK | */
				AWTEvent.CONTAINER_EVENT_MASK | AWTEvent.HIERARCHY_EVENT_MASK
				| AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK);

		// special case updates
		jAliceFrame.worldTreeComponent.worldTree
		.addTreeExpansionListener(new TreeExpansionListener() {
			public void treeCollapsed(TreeExpansionEvent ev) {
				updateTimer.restart();
			}

			public void treeExpanded(TreeExpansionEvent ev) {
				updateTimer.restart();
			}
		});

		tutorialOne = new File(JAlice.getAliceHomeDirectory(), "tutorial"
				+ File.separator + "Tutorial1.stl");
	}

	public void hackStencilUpdate() { // used in specific places in
		// authoringtool code to notify stencils
		// of updates not otherwise caught
		if (updateTimer != null) {
			updateTimer.restart();
		}
	}

	protected Dimension oldDimension;
	protected Point oldPosition;
	protected int oldLeftRightSplitPaneLocation;
	protected int oldWorldTreeDragFromSplitPaneLocation;
	protected int oldEditorBehaviorSplitPaneLocation;
	protected int oldSmallSceneBehaviorSplitPaneLocation;
	protected Dimension oldRenderWindowSize;
	protected Point oldRenderWindowPosition;
	protected boolean oldShouldConstrain;
	protected Rectangle oldRenderBounds;

	public void showStencils() {
		setLayout();
		jAliceFrame.removeKeyListener(stencilManager);
		jAliceFrame.addKeyListener(stencilManager);
		jAliceFrame.requestFocus();
		stencilManager.showStencils(true);
		authoringToolConfig.setValue("doNotShowUnhookedMethodWarning", "true");
	}

	protected void restoreLayout() {
		jAliceFrame.setSize(oldDimension);
		jAliceFrame.setLocation(oldPosition);
		jAliceFrame.leftRightSplitPane
		.setDividerLocation(oldLeftRightSplitPaneLocation);
		jAliceFrame.worldTreeDragFromSplitPane
		.setDividerLocation(oldWorldTreeDragFromSplitPaneLocation);
		jAliceFrame.editorBehaviorSplitPane
		.setDividerLocation(oldEditorBehaviorSplitPaneLocation);
		jAliceFrame.smallSceneBehaviorSplitPane
		.setDividerLocation(oldSmallSceneBehaviorSplitPaneLocation);
		if (oldShouldConstrain) {
			authoringToolConfig.setValue(
					"rendering.constrainRenderDialogAspectRatio", "true");
		} else {
			authoringToolConfig.setValue(
					"rendering.constrainRenderDialogAspectRatio", "false");
		}
		renderContentPane.saveRenderBounds(oldRenderBounds);
		jAliceFrame.doLayout();
		jAliceFrame.setResizable(true);
	}

	protected void setLayout() {
		int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize()
		.getWidth();
		int screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize()
		.getHeight();
		int height = screenHeight - 28;

		Dimension d = getScreenSize();
		oldDimension = new Dimension(d.width, d.height);
		oldPosition = jAliceFrame.getLocation();
		Point newPosition = new Point(oldPosition.x, oldPosition.y);
		int newWidth = d.width;
		int newHeight = d.height;
		if (d.width > 1032) {
			newWidth = 1032;
		} else if (d.width < 1024 && screenWidth >= 1024) {
			newWidth = 1032;
		}
		if (d.height > 776) {
			newHeight = 740;
		} else if (d.height < 740 && screenWidth >= 768) {
			newHeight = 740;
		}
		if (oldPosition.x + newWidth > (screenWidth + 4)) {
			newPosition.x = (screenWidth - newWidth) / 2;
		}
		if (oldPosition.y + newHeight > (screenHeight + 4)) {
			newPosition.y = (screenHeight - 28 - newHeight) / 2;
		}
		boolean shouldModifyStuff = true;
		if (screenWidth < 1024 || screenHeight < 740) {
			DialogManager
			.showMessageDialog(
					"Your screen resolution is lower than what we recommend for running the tutorial.\n"
					+ "Alice will still run the tutorial, but some of the objects may not line up well.",
					"Low Resolution Warning",
					JOptionPane.WARNING_MESSAGE, null);
			shouldModifyStuff = false;
		}
		oldLeftRightSplitPaneLocation = jAliceFrame.leftRightSplitPane
		.getDividerLocation();
		oldWorldTreeDragFromSplitPaneLocation = jAliceFrame.worldTreeDragFromSplitPane
		.getDividerLocation();
		oldEditorBehaviorSplitPaneLocation = jAliceFrame.editorBehaviorSplitPane
		.getDividerLocation();
		oldSmallSceneBehaviorSplitPaneLocation = jAliceFrame.smallSceneBehaviorSplitPane
		.getDividerLocation();
		oldRenderWindowSize = renderContentPane.getSize();
		oldRenderWindowPosition = renderContentPane.getLocation();

		oldShouldConstrain = authoringToolConfig.getValue(
				"rendering.constrainRenderDialogAspectRatio").equalsIgnoreCase(
				"true");
		authoringToolConfig.setValue(
				"rendering.constrainRenderDialogAspectRatio", "false");

		oldRenderBounds = renderContentPane.getRenderBounds();
		renderContentPane.saveRenderBounds(new Rectangle(191, 199, 400, 300));

		if ((newHeight != oldDimension.height || newWidth != oldDimension.width)
				&& shouldModifyStuff) {
			DialogManager
			.showMessageDialog(
					"Alice is going to adjust your screen size to make the tutorial fit on the screen better.\n"
					+ "When you exit the tutorial your original screen size will be restored.",
					"Different Resolution Warning",
					JOptionPane.WARNING_MESSAGE, null);

		}
		if (shouldModifyStuff) {
			jAliceFrame.leftRightSplitPane.setDividerLocation(230);
			jAliceFrame.worldTreeDragFromSplitPane.setDividerLocation(237);
			jAliceFrame.editorBehaviorSplitPane.setDividerLocation(204);
			jAliceFrame.smallSceneBehaviorSplitPane.setDividerLocation(224);
			jAliceFrame.setSize(newWidth, newHeight);
			jAliceFrame.setLocation(newPosition);
		}

		Dimension targetDimension = new Dimension(newWidth, newHeight);
		jAliceFrame.doLayout();
		jAliceFrame.setResizable(false);
	}

	public void hideStencils() {
		restoreLayout();
		stencilManager.showStencils(false);
		authoringToolConfig.setValue("doNotShowUnhookedMethodWarning", "false");
		jAliceFrame.removeKeyListener(stencilManager);
		jAliceFrame.requestFocus();
	}

	public void setGlassPane(Component c) {
		jAliceFrame.setGlassPane(c);
	}

	public void setVisible(boolean visible) {
		if (visible) {
			setLayout();
			authoringToolConfig.setValue("doNotShowUnhookedMethodWarning",
			"true");
			jAliceFrame.removeKeyListener(stencilManager);
			jAliceFrame.addKeyListener(stencilManager);
			jAliceFrame.requestFocus();
		} else {
			restoreLayout();
			authoringToolConfig.setValue("doNotShowUnhookedMethodWarning",
			"false");
			jAliceFrame.removeKeyListener(stencilManager);
			jAliceFrame.requestFocus();
		}
	}

	// Caitlin's code

	public edu.cmu.cs.stage3.caitlin.stencilhelp.application.StateCapsule getStateCapsuleFromString(
			String capsuleString) {
		StencilStateCapsule sc = new StencilStateCapsule();
		sc.parse(capsuleString);
		return (edu.cmu.cs.stage3.caitlin.stencilhelp.application.StateCapsule) sc;
	}

	public edu.cmu.cs.stage3.caitlin.stencilhelp.application.StateCapsule getCurrentState() {
		if (wayPoints.size() > 0) {
			WorldDifferencesCapsule currentWayPoint = (WorldDifferencesCapsule) wayPoints
			.get(0);
			StencilStateCapsule capsule = currentWayPoint.getStateCapsule();

			boolean stateMatches = this.doesStateMatch(capsule);
			// System.out.println("state matches: " + stateMatches);

			return capsule;
		}
		return null;
	}

	// public void newSlide() {}
	// public void clearSlide() {}

	protected Component getValidComponent(Component c) {
		while (c != null) {
			// special cases
			if ((c instanceof javax.swing.JButton)
					&& (c.getParent() instanceof PropertyViewController)) {
				c = c.getParent();
			} else if (c instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementNamePropertyViewController) {
				c = c.getParent();
			} else if ((c instanceof AbstractButton)
					&& (c.getParent() instanceof JComboBox)) {
				c = c.getParent();
			} else if ((c instanceof javax.swing.JTextField)
					&& (c.getParent() instanceof JComboBox)) {
				c = c.getParent();
			} else if ((c instanceof ImagePanel)
					&& (c.getParent() instanceof GuiNavigator)) {
				return c;
			} else if ((c instanceof javax.swing.JLabel)
					&& "more...".equals(((javax.swing.JLabel) c).getText())) {
				return c;
			} else if (c instanceof SceneEditor) {
				return ((SceneEditor) c).getRenderPanel();
				// } else if( (c instanceof javax.swing.JScrollPane) &&
				// (c.getParent() instanceof
				// edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)
				// && (c ==
				// ((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)c.getParent()).getWorkSpace())
				// ) {
				// return c;
				// } else if( (c instanceof JPanel) &&
				// (c.getParent() != null) && (c.getParent().getParent()
				// instanceof
				// edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)
				// && (c ==
				// ((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)c.getParent().getParent()).getParameterPanel())
				// ) {
				// return c;
				// } else if( (c instanceof JPanel) &&
				// (c.getParent() != null) && (c.getParent().getParent()
				// instanceof
				// edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)
				// && (c ==
				// ((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)c.getParent().getParent()).getVariablePanel())
				// ) {
				// return c;
				// } else if( (c instanceof JPanel) &&
				// (c.getParent() != null) && (c.getParent().getParent()
				// instanceof
				// edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)
				// && (c ==
				// ((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)c.getParent().getParent()).getDoNothingPanel())
				// ) {
				// return c;
			}

			// default cases
			for (java.util.Iterator iter = classesToStopOn.iterator(); iter
			.hasNext();) {
				Class stopClass = (Class) iter.next();
				if (stopClass.isAssignableFrom(c.getClass())) {
					return c;
				}
			}

			// look up one level
			c = c.getParent();
		}

		return null;
	}

	public String getIDForPoint(Point p, boolean dropSite) {
		p = javax.swing.SwingUtilities.convertPoint(jAliceFrame.getGlassPane(),
				p, jAliceFrame.getLayeredPane()); // shouldn't be necessary;
		// just being careful
		Component c = jAliceFrame.getRootPane().getLayeredPane()
		.findComponentAt(p);
		c = getValidComponent(c);
		if (c == null) {
			return null;
		}
		Point localPoint = javax.swing.SwingUtilities.convertPoint(jAliceFrame
				.getRootPane().getLayeredPane(), p, c);

		String key = null;
		if ((c instanceof JTree)
				&& jAliceFrame.worldTreeComponent.isAncestorOf(c)) {
			JTree tree = (JTree) c;
			javax.swing.tree.TreePath treePath = tree
			.getClosestPathForLocation(localPoint.x, localPoint.y);
			Rectangle bounds = tree.getPathBounds(treePath);
			key = "objectTree";
			if (bounds.contains(localPoint)) {
				String elementKey = ((Element) treePath.getLastPathComponent())
				.getKey(world);
				key += "<" + elementKey + ">";
			}
		} else if (c instanceof DnDClipboard) {
			key = "clipboard";
			Component[] components = c.getParent().getComponents();
			int index = -1;
			for (int i = 0; i < components.length; i++) {
				if (components[i] == c) {
					index = i;
				}
			}
			key += "<" + Integer.toString(index) + ">";

		} else if (jAliceFrame.sceneEditor.isAncestorOf(c)
				|| (c == jAliceFrame.sceneEditor)) {
			if (jAliceFrame.sceneEditor.getGuiMode() == SceneEditor.LARGE_MODE) {
				key = "sceneEditor<large>";
			} else {
				key = "sceneEditor<small>";
			}
			if (jAliceFrame.sceneEditor.getViewMode() == edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.CameraViewPanel.QUAD_VIEW_MODE) {
				key += ":quadView";
			} else {
				key += ":singleView";
			}

			if (jAliceFrame.sceneEditor.getGalleryViewer().isAncestorOf(c)
					|| (c == jAliceFrame.sceneEditor.getGalleryViewer())) {
				key += ":galleryViewer<"
					+ jAliceFrame.sceneEditor.getGalleryViewer()
					.getDirectory() + ">";

				if (c instanceof javax.swing.JButton) {
					key += ":button<" + ((javax.swing.JButton) c).getText()
					+ ">";
				} else if (c instanceof GalleryObject) {
					key += ":galleryObject<"
						+ ((GalleryObject) c).getUniqueIdentifier() + ">";
				} else if (c != jAliceFrame.sceneEditor.getGalleryViewer()) {
					key = null;
				}
			} else {
				String id = jAliceFrame.sceneEditor.getIdForComponent(c);
				// System.out.println( c + " --> " + id );
				if (id != null) {
					key += ":" + id;
				} else if (c != jAliceFrame.sceneEditor) {
					key = null;
				}
			}
		} else if (jAliceFrame.dragFromComponent.isAncestorOf(c)
				|| (c == jAliceFrame.dragFromComponent)) {
			if (jAliceFrame.dragFromComponent.getElement() == null) {
				key = "details";
			} else {
				key = "details<"
					+ jAliceFrame.dragFromComponent.getElement().getKey(
							world) + ">";
				if (c instanceof JTabbedPane) {
					int whichTab = ((JTabbedPane) c).getUI().tabForCoordinate(
							(JTabbedPane) c, localPoint.x, localPoint.y);
					if (whichTab > -1) {
						key += ":tab<" + Integer.toString(whichTab) + ">";
					}
				} else if (c != jAliceFrame.dragFromComponent) {
					key += ":"
						+ jAliceFrame.dragFromComponent
						.getKeyForComponent(c);
				}
			}
		} else if (jAliceFrame.behaviorGroupsEditor.isAncestorOf(c)) {
			key = "behaviors";
			if (c instanceof DnDGroupingPanel) {
				try {
					java.awt.datatransfer.Transferable transferable = ((DnDGroupingPanel) c)
					.getTransferable();
					if (AuthoringToolResources
							.safeIsDataFlavorSupported(
									transferable,
									edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor)) {
						Element e = (Element) transferable
						.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor);
						key += ":elementTile<" + e.getKey(world) + ">";
					} else {
						key = null;
					}
				} catch (Exception e) {
					AuthoringTool.showErrorDialog(
							"Error while examining DnDGroupingPanel.", e);
					key = null;
				}
			} else if (c instanceof PropertyViewController) {
				Property property = ((PropertyViewController) c).getProperty();
				Element element = property.getOwner();
				key += ":elementTile<" + element.getKey(world) + ">";
				key += ":property<" + property.getName() + ">";
				// TODO: handle user-defined parameters
			} else if ((c instanceof javax.swing.JLabel)
					&& ((javax.swing.JLabel) c).getText().equals("more...")) {
				if (c.getParent().getParent().getParent() instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) {
					edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController vc = (edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) c
					.getParent().getParent().getParent();
					Element element = vc.getElement();
					key += ":elementTile<" + element.getKey(world) + ">";
					key += ":more";
				} else {
					key = null;
				}
			} else if (c instanceof javax.swing.JButton) {
				if (((javax.swing.JButton) c).getText().equals(
				"create new event")) { // HACK
					key += ":createNewEventButton";
				} else {
					key = null;
				}
			}
		} else if (jAliceFrame.tabbedEditorComponent.isAncestorOf(c)) {
			key = "editors";
			if (c instanceof JTabbedPane) {
				int whichTab = ((JTabbedPane) c).getUI().tabForCoordinate(
						(JTabbedPane) c, localPoint.x, localPoint.y);
				if (whichTab > -1) {
					Object o = jAliceFrame.tabbedEditorComponent
					.getObjectBeingEditedAt(whichTab);
					if (o instanceof Element) {
						key += ":element<" + ((Element) o).getKey(world) + ">";
					} else {
						key = null;
					}
				}
			} else {
				Object o = jAliceFrame.tabbedEditorComponent
				.getObjectBeingEdited();
				if (o instanceof Element) {
					key += ":element<" + ((Element) o).getKey(world) + ">";
					// edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.ResponseEditor
					// responseEditor = null;
					// if( jAliceFrame.tabbedEditorComponent.getCurrentEditor()
					// instanceof
					// edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.ResponseEditor
					// ) {
					// responseEditor =
					// (edu.cmu.cs.stage3.alice.authoringtool.editors.responseeditor.ResponseEditor)jAliceFrame.tabbedEditorComponent.getCurrentEditor();
					// }
					// if( (c instanceof javax.swing.JScrollPane) &&
					// (c.getParent() instanceof
					// edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)
					// && (c ==
					// ((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)c.getParent()).getWorkSpace())
					// ) {
					// key += ":compositeEditorWorkSpace";
					// } else if( (c instanceof JPanel) &&
					// (c.getParent() != null) && (c.getParent().getParent()
					// instanceof
					// edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)
					// && (c ==
					// ((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)c.getParent().getParent()).getParameterPanel())
					// ) {
					// key += ":compositeEditorParameterPanel";
					// } else if( (c instanceof JPanel) &&
					// (c.getParent() != null) && (c.getParent().getParent()
					// instanceof
					// edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)
					// && (c ==
					// ((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)c.getParent().getParent()).getVariablePanel())
					// ) {
					// key += ":compositeEditorVariablePanel";
					// } else if( (c instanceof JPanel) &&
					// (c.getParent() != null) && (c.getParent().getParent()
					// instanceof
					// edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)
					// && (c ==
					// ((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)c.getParent().getParent()).getDoNothingPanel())
					// ) {
					// key += ":compositeEditorDoNothingPanel";
					if (c instanceof javax.swing.JButton) {
						key += ":button<" + ((javax.swing.JButton) c).getText()
						+ ">";
					} else if (c instanceof DnDGroupingPanel) {
						try {
							java.awt.datatransfer.Transferable transferable = ((DnDGroupingPanel) c)
							.getTransferable();
							if (AuthoringToolResources
									.safeIsDataFlavorSupported(
											transferable,
											edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor)) {
								Element e = (Element) transferable
								.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor);
								key += ":elementTile<" + e.getKey(world) + ">";
							} else if (AuthoringToolResources
									.safeIsDataFlavorSupported(
											transferable,
											edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor)) {
								ElementPrototype ep = (ElementPrototype) transferable
								.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor);
								key += ":elementPrototypeTile<"
									+ ep.getElementClass().getName() + ">";
							} else {
								key = null;
							}
							// TODO: handle other DnDGroupingPanels
						} catch (Exception e) {
							AuthoringTool.showErrorDialog(
									"Error while examining DnDGroupingPanel.",
									e);
							key = null;
						}
					} else if (c instanceof PropertyViewController) {
						Property property = ((PropertyViewController) c)
						.getProperty();
						Element element = property.getOwner();
						key += ":elementTile<" + element.getKey(world) + ">";
						key += ":property<" + property.getName() + ">";
						// TODO: handle user-defined parameters
					} else if ((c instanceof javax.swing.JLabel)
							&& ((javax.swing.JLabel) c).getText().equals(
							"more...")) {
						if (c.getParent().getParent().getParent() instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) {
							edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController vc = (edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) c
							.getParent().getParent().getParent();
							Element element = vc.getElement();
							key += ":elementTile<" + element.getKey(world)
							+ ">";
							key += ":more";
						} else {
							key = null;
						}
					}
				} else {
					key = null;
				}
			}
		} else if (componentMap.containsValue(c)) {
			for (java.util.Iterator iter = componentMap.keySet().iterator(); iter
			.hasNext();) {
				String k = (String) iter.next();
				if (c.equals(componentMap.get(k))) {
					key = k;
					break;
				}
			}
		}
		// System.out.println( key );
		return key;
	}

	private java.awt.Image getComponentImage(Component c) {
		Rectangle bounds = c.getBounds();
		java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
				bounds.width, bounds.height,
				java.awt.image.BufferedImage.TYPE_INT_ARGB);
		java.awt.Graphics2D g = image.createGraphics();
		c.paintAll(g);
		return image;
	}

	public java.awt.Image getImageForID(String id) {
		Rectangle r = null;
		java.awt.Image image = null;
		java.util.StringTokenizer st = new java.util.StringTokenizer(id, ":",
				false);
		if (st.hasMoreTokens()) {
			String token = st.nextToken();
			String prefix = AuthoringToolResources.getPrefix(token);
			String spec = AuthoringToolResources.getSpecifier(token);
			if (prefix.equals("objectTree")) {
				if (spec != null) {
					Element element = world.getDescendantKeyed(spec);
					if (element != null) {
						JTree tree = jAliceFrame.worldTreeComponent.worldTree;
						WorldTreeModel worldTreeModel = (WorldTreeModel) tree
						.getModel();
						r = tree.getPathBounds(new javax.swing.tree.TreePath(
								worldTreeModel.getPath(element)));
						if ((r != null) && (!worldTreeModel.isLeaf(element))) { // HACK
							// to
							// include
							// expand
							// handle
							r.x -= 15;
							r.width += 15;
						}
						if (r != null) {
							r = javax.swing.SwingUtilities.convertRectangle(
									tree, r, jAliceFrame.getGlassPane());
						}
					}
				} else {
					r = jAliceFrame.worldTreeComponent.getBounds();
					r = javax.swing.SwingUtilities.convertRectangle(
							jAliceFrame.worldTreeComponent.getParent(), r,
							jAliceFrame.getGlassPane());
				}
			} else if (prefix.equals("clipboard")) {
				if (spec != null) {
					try {
						int index = Integer.parseInt(spec);
						if (index > -1) {
							Component c = jAliceFrame.clipboardPanel
							.getComponent(index);
							if (c != null) {
								image = getComponentImage(c);
							}
						}
					} catch (Exception e) {
					}
				}
			} else if (prefix.equals("sceneEditor")) {
				if (st.hasMoreTokens()) {
					token = st.nextToken(); // pull off singleView/quadView,
					// assume we're in the right mode
					if (st.hasMoreTokens()) {
						token = st.nextToken();
						prefix = AuthoringToolResources.getPrefix(token);
						spec = AuthoringToolResources.getSpecifier(token);
						if (prefix.equals("galleryViewer")) {
							jAliceFrame.sceneEditor.getGalleryViewer()
							.setDirectory(spec);
							if (st.hasMoreTokens()) {
								token = st.nextToken();
								prefix = AuthoringToolResources
								.getPrefix(token);
								spec = AuthoringToolResources
								.getSpecifier(token);
								if (prefix.equals("button")) {
									Component c = AuthoringToolResources
									.findButton(jAliceFrame.sceneEditor
											.getGalleryViewer(), spec);
									if (c != null) {
										image = getComponentImage(c);
									}
								} else if (prefix.equals("galleryObject")) {
									Component c = AuthoringToolResources
									.findGalleryObject(
											jAliceFrame.sceneEditor
											.getGalleryViewer(),
											spec);
									if (c != null) {
										image = getComponentImage(c);
									}
								}
							}
						} else {
							Component c = jAliceFrame.sceneEditor
							.getComponentForId(token);
							if ((c != null)
									&& isComponentVisible((javax.swing.JComponent) c)) {
								image = getComponentImage(c);
							}
						}
					}
				}
			} else if (prefix.equals("details")) {
				if (spec != null) {
					Element element = world.getDescendantKeyed(spec);
					if (jAliceFrame.dragFromComponent.getElement().equals(
							element)) {
						if (st.hasMoreTokens()) {
							token = st.nextToken();
							prefix = AuthoringToolResources.getPrefix(token);
							spec = AuthoringToolResources.getSpecifier(token);
							if (prefix.equals("viewController")) {
								if (st.hasMoreTokens()) {
									token = st.nextToken();
									prefix = AuthoringToolResources
									.getPrefix(token);
									spec = AuthoringToolResources
									.getSpecifier(token);
									Component c = jAliceFrame.dragFromComponent
									.getPropertyViewComponentForKey(token);
									if ((c != null)
											&& isComponentVisible((javax.swing.JComponent) c)) {
										image = getComponentImage(c);
									}
								}
							} else {
								Component c = jAliceFrame.dragFromComponent
								.getComponentForKey(token);
								if ((c != null)
										&& isComponentVisible((javax.swing.JComponent) c)) {
									image = getComponentImage(c);
								}
							}
						} else {
							image = getComponentImage(jAliceFrame.dragFromComponent);
						}
					}
				} else {
					image = getComponentImage(jAliceFrame.dragFromComponent);
				}
			} else if (prefix.equals("behaviors")) {
				if (st.hasMoreTokens()) {
					token = st.nextToken();
					prefix = AuthoringToolResources.getPrefix(token);
					spec = AuthoringToolResources.getSpecifier(token);
					if (prefix.equals("createNewEventButton")) {
						Component c = AuthoringToolResources.findButton(
								jAliceFrame.behaviorGroupsEditor,
						"create new event");
						if (c != null) {
							image = getComponentImage(c);
						}
					} else if (prefix.equals("elementTile") && (spec != null)) {
						Element element = world.getDescendantKeyed(spec);
						if (element != null) {
							if (st.hasMoreTokens()) {
								token = st.nextToken();
								prefix = AuthoringToolResources
								.getPrefix(token);
								spec = AuthoringToolResources
								.getSpecifier(token);
								if (prefix.equals("property") && (spec != null)) {
									Component c = AuthoringToolResources
									.findPropertyViewController(
											jAliceFrame.behaviorGroupsEditor,
											element, spec);
									if (c != null) {
										image = getComponentImage(c);
									}
								} else if (prefix.equals("more")) {
									Component dndPanel = AuthoringToolResources
									.findElementDnDPanel(
											jAliceFrame.behaviorGroupsEditor,
											element);
									if (dndPanel instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) {
										Component moreTile = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) dndPanel)
										.getMoreTile();
										if ((moreTile != null)
												&& moreTile.isShowing()) {
											image = getComponentImage(moreTile);
										}
									}
								}
							} else {
								Component c = AuthoringToolResources
								.findElementDnDPanel(
										jAliceFrame.behaviorGroupsEditor,
										element);
								if (c != null) {
									image = getComponentImage(c);
								}
							}
						}
					}
				}
			} else if (prefix.equals("editors")) {
				if (st.hasMoreTokens()) {
					token = st.nextToken();
					prefix = AuthoringToolResources.getPrefix(token);
					spec = AuthoringToolResources.getSpecifier(token);
					if (prefix.equals("element")) {
						Element elementBeingEdited = world
						.getDescendantKeyed(spec);
						if (st.hasMoreTokens()) {
							if ((jAliceFrame.tabbedEditorComponent
									.getObjectBeingEdited() != null)
									&& jAliceFrame.tabbedEditorComponent
									.getObjectBeingEdited().equals(
											elementBeingEdited)) {
								// Editor editor =
								// jAliceFrame.tabbedEditorComponent.getEditorAt(
								// jAliceFrame.tabbedEditorComponent.getIndexOfObject(
								// elementBeingEdited ) );
								java.awt.Container container = (java.awt.Container) jAliceFrame.tabbedEditorComponent.tabbedPane
								.getComponentAt(jAliceFrame.tabbedEditorComponent
										.getIndexOfObject(elementBeingEdited));
								token = st.nextToken();
								prefix = AuthoringToolResources
								.getPrefix(token);
								spec = AuthoringToolResources
								.getSpecifier(token);
								if (prefix.equals("button")) {
									Component c = AuthoringToolResources
									.findButton(container, spec);
									if (c != null) {
										image = getComponentImage(c);
									}
								} else if (prefix.equals("elementTile")
										&& (spec != null)) {
									Element element = world
									.getDescendantKeyed(spec);
									if (element != null) {
										if (st.hasMoreTokens()) {
											token = st.nextToken();
											prefix = AuthoringToolResources
											.getPrefix(token);
											spec = AuthoringToolResources
											.getSpecifier(token);
											if (prefix.equals("property")
													&& (spec != null)) {
												Component c = AuthoringToolResources
												.findPropertyViewController(
														container,
														element, spec);
												if (c != null) {
													image = getComponentImage(c);
												}
											} else if (prefix.equals("more")) {
												Component dndPanel = AuthoringToolResources
												.findElementDnDPanel(
														container,
														element);
												if (dndPanel instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) {
													Component moreTile = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) dndPanel)
													.getMoreTile();
													if ((moreTile != null)
															&& moreTile
															.isShowing()) {
														image = getComponentImage(moreTile);
													}
												}
											}
										} else {
											Component c = AuthoringToolResources
											.findElementDnDPanel(
													container, element);
											if (c != null) {
												image = getComponentImage(c);
											}
										}
									}
								} else if (prefix
										.equals("elementPrototypeTile")
										&& (spec != null)) {
									try {
										Class elementClass = Class
										.forName(spec);
										if (elementClass != null) {
											Component c = AuthoringToolResources
											.findPrototypeDnDPanel(
													container,
													elementClass);
											if (c != null) {
												image = getComponentImage(c);
											}
										}
									} catch (Exception e) {
										AuthoringTool.showErrorDialog(
												"Error while looking for ProtoypeDnDPanel using class "
												+ spec, e);
									}
								}
							}
						} else {
							int tabIndex = jAliceFrame.tabbedEditorComponent
							.getIndexOfObject(elementBeingEdited);
							if ((tabIndex >= 0)
									&& (tabIndex < jAliceFrame.tabbedEditorComponent.tabbedPane
											.getComponentCount())) {
								r = jAliceFrame.tabbedEditorComponent.tabbedPane
								.getUI()
								.getTabBounds(
										jAliceFrame.tabbedEditorComponent.tabbedPane,
										tabIndex);
								r = javax.swing.SwingUtilities
								.convertRectangle(
										jAliceFrame.tabbedEditorComponent.tabbedPane,
										r, jAliceFrame.getGlassPane());
							}
						}
					}
				} else {
					r = jAliceFrame.tabbedEditorComponent.getBounds();
					r = javax.swing.SwingUtilities.convertRectangle(
							jAliceFrame.tabbedEditorComponent.getParent(), r,
							jAliceFrame.getGlassPane());
				}
			} else if (componentMap.containsKey(prefix)) {
				Component c = (Component) componentMap.get(prefix);
				if (c != null) {
					image = getComponentImage(c);
				}
			}
		}
		return image;
	}

	public Rectangle getBoxForID(String id)
	throws edu.cmu.cs.stage3.caitlin.stencilhelp.application.IDDoesNotExistException {
		Rectangle r = null;
		java.util.StringTokenizer st = new java.util.StringTokenizer(id, ":",
				false);
		if (st.hasMoreTokens()) {
			String token = st.nextToken();
			String prefix = AuthoringToolResources.getPrefix(token);
			String spec = AuthoringToolResources.getSpecifier(token);
			if (prefix.equals("objectTree")) {
				if (spec != null) {
					Element element = world.getDescendantKeyed(spec);
					if (element != null) {
						JTree tree = jAliceFrame.worldTreeComponent.worldTree;
						WorldTreeModel worldTreeModel = (WorldTreeModel) tree
						.getModel();
						r = tree.getPathBounds(new javax.swing.tree.TreePath(
								worldTreeModel.getPath(element)));
						if ((r != null) && (!worldTreeModel.isLeaf(element))) { // HACK
							// to
							// include
							// expand
							// handle
							r.x -= 15;
							r.width += 15;
						}
						if (r != null) {
							r = javax.swing.SwingUtilities.convertRectangle(
									tree, r, jAliceFrame.getGlassPane());
						}
					}
				} else {
					r = jAliceFrame.worldTreeComponent.getBounds();
					r = javax.swing.SwingUtilities.convertRectangle(
							jAliceFrame.worldTreeComponent.getParent(), r,
							jAliceFrame.getGlassPane());
				}
			} else if (prefix.equals("clipboard")) {
				if (spec != null) {
					try {
						int index = Integer.parseInt(spec);
						if (index > -1) {
							Component c = jAliceFrame.clipboardPanel
							.getComponent(index);
							if (c != null) {
								r = c.getBounds();
								r = javax.swing.SwingUtilities
								.convertRectangle(c.getParent(), r,
										jAliceFrame.getGlassPane());
							}
						}
					} catch (Exception e) {
					}
				}

			} else if (prefix.equals("sceneEditor")) {
				if (st.hasMoreTokens()) {
					token = st.nextToken(); // pull off singleView/quadView,
					// assume we're in the right mode
					if (st.hasMoreTokens()) {
						token = st.nextToken();
						prefix = AuthoringToolResources.getPrefix(token);
						spec = AuthoringToolResources.getSpecifier(token);
						if (prefix.equals("galleryViewer")) {
							jAliceFrame.sceneEditor.getGalleryViewer()
							.setDirectory(spec);
							if (st.hasMoreTokens()) {
								token = st.nextToken();
								prefix = AuthoringToolResources
								.getPrefix(token);
								spec = AuthoringToolResources
								.getSpecifier(token);
								if (prefix.equals("button")) {
									Component c = AuthoringToolResources
									.findButton(jAliceFrame.sceneEditor
											.getGalleryViewer(), spec);
									if (c != null) {
										r = c.getBounds();
										r = javax.swing.SwingUtilities
										.convertRectangle(
												c.getParent(), r,
												jAliceFrame
												.getGlassPane());
									}
								} else if (prefix.equals("galleryObject")) {
									Component c = AuthoringToolResources
									.findGalleryObject(
											jAliceFrame.sceneEditor
											.getGalleryViewer(),
											spec);
									if (c != null) {
										r = c.getBounds();
										r = javax.swing.SwingUtilities
										.convertRectangle(
												c.getParent(), r,
												jAliceFrame
												.getGlassPane());
									}
								}
							} else {
								r = jAliceFrame.sceneEditor.getGalleryViewer()
								.getBounds();
								r = javax.swing.SwingUtilities
								.convertRectangle(
										jAliceFrame.sceneEditor
										.getGalleryViewer()
										.getParent(), r,
										jAliceFrame.getGlassPane());
							}
						} else {
							Component c = jAliceFrame.sceneEditor
							.getComponentForId(token);
							if ((c != null)
									&& isComponentVisible((javax.swing.JComponent) c)) {
								r = c.getBounds();
								r = javax.swing.SwingUtilities
								.convertRectangle(c.getParent(), r,
										jAliceFrame.getGlassPane());
							}
						}
					} else {
						r = jAliceFrame.sceneEditor.getBounds();
						r = javax.swing.SwingUtilities.convertRectangle(
								jAliceFrame.sceneEditor.getParent(), r,
								jAliceFrame.getGlassPane());
					}
				} else {
					r = jAliceFrame.sceneEditor.getBounds();
					r = javax.swing.SwingUtilities.convertRectangle(
							jAliceFrame.sceneEditor.getParent(), r, jAliceFrame
							.getGlassPane());
				}
			} else if (prefix.equals("details")) {
				if (spec != null) {
					Element element = world.getDescendantKeyed(spec);
					if (jAliceFrame.dragFromComponent.getElement().equals(
							element)) {
						if (st.hasMoreTokens()) {
							token = st.nextToken();
							prefix = AuthoringToolResources.getPrefix(token);
							spec = AuthoringToolResources.getSpecifier(token);
							if (prefix.equals("tab")) {
								int tabIndex = Integer.parseInt(spec);
								r = jAliceFrame.dragFromComponent.tabbedPane
								.getUI()
								.getTabBounds(
										jAliceFrame.dragFromComponent.tabbedPane,
										tabIndex);
								r = javax.swing.SwingUtilities
								.convertRectangle(
										jAliceFrame.dragFromComponent.tabbedPane,
										r, jAliceFrame.getGlassPane());
								return r;
							} else if (prefix.equals("viewController")) {
								if (st.hasMoreTokens()) {
									token = st.nextToken();
									prefix = AuthoringToolResources
									.getPrefix(token);
									spec = AuthoringToolResources
									.getSpecifier(token);
									Component c = jAliceFrame.dragFromComponent
									.getPropertyViewComponentForKey(token);
									if ((c != null)
											&& isComponentVisible((javax.swing.JComponent) c)) {
										r = c.getBounds();
										r = javax.swing.SwingUtilities
										.convertRectangle(
												c.getParent(), r,
												jAliceFrame
												.getGlassPane());
									}
								}
							} else {
								Component c = jAliceFrame.dragFromComponent
								.getComponentForKey(token);
								if ((c != null)
										&& isComponentVisible((javax.swing.JComponent) c)) {
									r = c.getBounds();
									r = javax.swing.SwingUtilities
									.convertRectangle(c.getParent(), r,
											jAliceFrame.getGlassPane());
								}
							}
						} else {
							r = jAliceFrame.dragFromComponent.getBounds();
							r = javax.swing.SwingUtilities.convertRectangle(
									jAliceFrame.dragFromComponent.getParent(),
									r, jAliceFrame.getGlassPane());
						}
					}
				} else {
					r = jAliceFrame.dragFromComponent.getBounds();
					r = javax.swing.SwingUtilities.convertRectangle(
							jAliceFrame.dragFromComponent.getParent(), r,
							jAliceFrame.getGlassPane());
				}
			} else if (prefix.equals("behaviors")) {
				if (st.hasMoreTokens()) {
					token = st.nextToken();
					prefix = AuthoringToolResources.getPrefix(token);
					spec = AuthoringToolResources.getSpecifier(token);
					if (prefix.equals("createNewEventButton")) {
						Component c = AuthoringToolResources.findButton(
								jAliceFrame.behaviorGroupsEditor,
						"create new event");
						if (c != null) {
							r = c.getBounds();
							r = javax.swing.SwingUtilities
							.convertRectangle(c.getParent(), r,
									jAliceFrame.getGlassPane());
						}
					} else if (prefix.equals("elementTile") && (spec != null)) {
						Element element = world.getDescendantKeyed(spec);
						if (element != null) {
							if (st.hasMoreTokens()) {
								token = st.nextToken();
								prefix = AuthoringToolResources
								.getPrefix(token);
								spec = AuthoringToolResources
								.getSpecifier(token);
								if (prefix.equals("property") && (spec != null)) {
									Component c = AuthoringToolResources
									.findPropertyViewController(
											jAliceFrame.behaviorGroupsEditor,
											element, spec);
									if (c != null) {
										r = c.getBounds();
										r = javax.swing.SwingUtilities
										.convertRectangle(
												c.getParent(), r,
												jAliceFrame
												.getGlassPane());
									}
								} else if (prefix.equals("more")) {
									Component dndPanel = AuthoringToolResources
									.findElementDnDPanel(
											jAliceFrame.behaviorGroupsEditor,
											element);
									if (dndPanel instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) {
										Component moreTile = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) dndPanel)
										.getMoreTile();
										if ((moreTile != null)
												&& moreTile.isShowing()) {
											r = moreTile.getBounds();
											r = javax.swing.SwingUtilities
											.convertRectangle(
													moreTile
													.getParent(),
													r,
													jAliceFrame
													.getGlassPane());
										}
									}
								}
							} else {
								Component c = AuthoringToolResources
								.findElementDnDPanel(
										jAliceFrame.behaviorGroupsEditor,
										element);
								if (c != null) {
									r = c.getBounds();
									r = javax.swing.SwingUtilities
									.convertRectangle(c.getParent(), r,
											jAliceFrame.getGlassPane());
								}
							}
						}
					}
				} else {
					r = jAliceFrame.behaviorGroupsEditor.getBounds();
					r = javax.swing.SwingUtilities.convertRectangle(
							jAliceFrame.behaviorGroupsEditor.getParent(), r,
							jAliceFrame.getGlassPane());
				}
			} else if (prefix.equals("editors")) {
				if (st.hasMoreTokens()) {
					token = st.nextToken();
					prefix = AuthoringToolResources.getPrefix(token);
					spec = AuthoringToolResources.getSpecifier(token);
					if (prefix.equals("element")) {
						Element elementBeingEdited = world
						.getDescendantKeyed(spec);
						if (st.hasMoreTokens()) {
							if ((jAliceFrame.tabbedEditorComponent
									.getObjectBeingEdited() != null)
									&& jAliceFrame.tabbedEditorComponent
									.getObjectBeingEdited().equals(
											elementBeingEdited)) {
								// Editor editor =
								// jAliceFrame.tabbedEditorComponent.getEditorAt(
								// jAliceFrame.tabbedEditorComponent.getIndexOfObject(
								// elementBeingEdited ) );
								java.awt.Container container = (java.awt.Container) jAliceFrame.tabbedEditorComponent.tabbedPane
								.getComponentAt(jAliceFrame.tabbedEditorComponent
										.getIndexOfObject(elementBeingEdited));
								token = st.nextToken();
								prefix = AuthoringToolResources
								.getPrefix(token);
								spec = AuthoringToolResources
								.getSpecifier(token);
								if (prefix.equals("button")) {
									Component c = AuthoringToolResources
									.findButton(container, spec);
									if (c != null) {
										r = c.getBounds();
										r = javax.swing.SwingUtilities
										.convertRectangle(
												c.getParent(), r,
												jAliceFrame
												.getGlassPane());
									}
								} else if (prefix.equals("elementTile")
										&& (spec != null)) {
									Element element = world
									.getDescendantKeyed(spec);
									if (element != null) {
										if (st.hasMoreTokens()) {
											token = st.nextToken();
											prefix = AuthoringToolResources
											.getPrefix(token);
											spec = AuthoringToolResources
											.getSpecifier(token);
											if (prefix.equals("property")
													&& (spec != null)) {
												Component c = AuthoringToolResources
												.findPropertyViewController(
														container,
														element, spec);
												if (c != null) {
													r = c.getBounds();
													r = javax.swing.SwingUtilities
													.convertRectangle(
															c
															.getParent(),
															r,
															jAliceFrame
															.getGlassPane());
												}
											} else if (prefix.equals("more")) {
												Component dndPanel = AuthoringToolResources
												.findElementDnDPanel(
														container,
														element);
												if (dndPanel instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) {
													Component moreTile = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) dndPanel)
													.getMoreTile();
													if ((moreTile != null)
															&& moreTile
															.isShowing()) {
														r = moreTile
														.getBounds();
														r = javax.swing.SwingUtilities
														.convertRectangle(
																moreTile
																.getParent(),
																r,
																jAliceFrame
																.getGlassPane());
													}
												}
											}
										} else {
											Component c = AuthoringToolResources
											.findElementDnDPanel(
													container, element);
											if (c != null) {
												r = c.getBounds();
												r = javax.swing.SwingUtilities
												.convertRectangle(
														c.getParent(),
														r,
														jAliceFrame
														.getGlassPane());
											}
										}
									}
								} else if (prefix
										.equals("elementPrototypeTile")
										&& (spec != null)) {
									try {
										Class elementClass = Class
										.forName(spec);
										if (elementClass != null) {
											Component c = AuthoringToolResources
											.findPrototypeDnDPanel(
													container,
													elementClass);
											if (c != null) {
												r = c.getBounds();
												r = javax.swing.SwingUtilities
												.convertRectangle(
														c.getParent(),
														r,
														jAliceFrame
														.getGlassPane());
											}
										}
									} catch (Exception e) {
										AuthoringTool.showErrorDialog(
												"Error while looking for ProtoypeDnDPanel using class "
												+ spec, e);
									}
								}
							}
						} else {
							int tabIndex = jAliceFrame.tabbedEditorComponent
							.getIndexOfObject(elementBeingEdited);
							if ((tabIndex >= 0)
									&& (tabIndex < jAliceFrame.tabbedEditorComponent.tabbedPane
											.getComponentCount())) {
								r = jAliceFrame.tabbedEditorComponent.tabbedPane
								.getUI()
								.getTabBounds(
										jAliceFrame.tabbedEditorComponent.tabbedPane,
										tabIndex);
								r = javax.swing.SwingUtilities
								.convertRectangle(
										jAliceFrame.tabbedEditorComponent.tabbedPane,
										r, jAliceFrame.getGlassPane());
							}
						}
					}
				} else {
					r = jAliceFrame.tabbedEditorComponent.getBounds();
					r = javax.swing.SwingUtilities.convertRectangle(
							jAliceFrame.tabbedEditorComponent.getParent(), r,
							jAliceFrame.getGlassPane());
				}
			} else if (componentMap.containsKey(prefix)) {
				Component c = (Component) componentMap.get(prefix);
				if (c != null) {
					r = c.getBounds();
					r = javax.swing.SwingUtilities.convertRectangle(c
							.getParent(), r, jAliceFrame.getGlassPane());
				}
			}
		}

		if (r == null) {
			throw new edu.cmu.cs.stage3.caitlin.stencilhelp.application.IDDoesNotExistException(
					id);
		}

		return r;
	}

	public boolean isComponentVisible(javax.swing.JComponent c) {
		if (c == null) {
			return false;
		}
		Rectangle visibleR = c.getVisibleRect();
		Rectangle ourRect = c.getBounds();
		return (ourRect.width == visibleR.width && ourRect.height == visibleR.height);
		// Component parent = c.getParent();
		// while (parent != null && !(parent instanceof
		// javax.swing.JScrollPane)){
		// parent = parent.getParent();
		// }
		// if (parent == null){
		// return c.isVisible();
		// } else{
		// javax.swing.JScrollPane parentScrollPane =
		// (javax.swing.JScrollPane)parent;
		// return false;
		// }
	}

	public boolean isIDVisible(String id)
	throws edu.cmu.cs.stage3.caitlin.stencilhelp.application.IDDoesNotExistException {
		java.util.StringTokenizer st = new java.util.StringTokenizer(id, ":",
				false);

		if (st.hasMoreTokens()) {
			String token = st.nextToken();
			String prefix = AuthoringToolResources.getPrefix(token);
			String spec = AuthoringToolResources.getSpecifier(token);
			if (prefix.equals("objectTree")) {
				if (spec != null) {
					Element element = world.getDescendantKeyed(spec);
					if (element != null) {
						JTree tree = jAliceFrame.worldTreeComponent.worldTree;
						WorldTreeModel worldTreeModel = (WorldTreeModel) tree
						.getModel();
						Rectangle r = tree
						.getPathBounds(new javax.swing.tree.TreePath(
								worldTreeModel.getPath(element)));
						if ((r != null) && tree.getVisibleRect().contains(r)) {
							return true;
						} else {
							return false;
						}
					}
				}
			} else if (prefix.equals("clipboard")) {
				if (spec != null) {
					try {
						int index = Integer.parseInt(spec);
						if (index > -1) {
							Component c = jAliceFrame.clipboardPanel
							.getComponent(index);
							if (c != null
									&& isComponentVisible((javax.swing.JComponent) c)) {
								return true;
							} else {
								return false;
							}
						} else {
							return false;
						}
					} catch (Exception e) {
						return false;
					}
				} else {
					return false;
				}

			} else if (prefix.equals("sceneEditor")) {
				if (st.hasMoreTokens()) {
					token = st.nextToken(); // pull off singleView/quadView
					if (st.hasMoreTokens()) {
						token = st.nextToken();
						prefix = AuthoringToolResources.getPrefix(token);
						spec = AuthoringToolResources.getSpecifier(token);
						if (prefix.equals("galleryViewer")) {
							if (jAliceFrame.sceneEditor.getGalleryViewer()
									.getDirectory().equals(spec)) {
								if (st.hasMoreTokens()) {
									token = st.nextToken();
									prefix = AuthoringToolResources
									.getPrefix(token);
									spec = AuthoringToolResources
									.getSpecifier(token);
									if (prefix.equals("button")) {
										Component c = AuthoringToolResources
										.findButton(
												jAliceFrame.sceneEditor
												.getGalleryViewer(),
												spec);
										if ((c != null)
												&& isComponentVisible((javax.swing.JComponent) c)) {
											return true;
										} else {
											return false;
										}
									} else if (prefix.equals("galleryObject")) {
										Component c = AuthoringToolResources
										.findGalleryObject(
												jAliceFrame.sceneEditor
												.getGalleryViewer(),
												spec);
										if ((c != null)
												&& isComponentVisible((javax.swing.JComponent) c)) {
											return true;
										} else {
											return false;
										}
									}
								} else {
									if (jAliceFrame.sceneEditor
											.getGalleryViewer().isShowing()) {
										return true;
									} else {
										return false;
									}
								}
							} else {
								return false;
							}
						} else {
							Component c = jAliceFrame.sceneEditor
							.getComponentForId(token);
							if ((c != null)
									&& isComponentVisible((javax.swing.JComponent) c)) {
								return true;
							} else {
								return false;
							}
						}
					}
				}
			} else if (prefix.equals("details")) {
				if (spec == null) {
					spec = "";
				}
				Element element = world.getDescendantKeyed(spec);
				if (jAliceFrame.dragFromComponent.getElement().equals(element)) {
					if (st.hasMoreTokens()) {
						token = st.nextToken();
						prefix = AuthoringToolResources.getPrefix(token);
						spec = AuthoringToolResources.getSpecifier(token);
						if (prefix.equals("tab")) {
							return true;
						} else {
							Component c = jAliceFrame.dragFromComponent
							.getComponentForKey(token);
							// System.out.println(c+", "+isComponentVisible((javax.swing.JComponent)c)+", "+c.isVisible());
							Rectangle boundss = c.getBounds();
							boundss = javax.swing.SwingUtilities
							.convertRectangle(c.getParent(), boundss,
									jAliceFrame);
							// System.out.println(boundss);
							// System.out.println(jAliceFrame.getBounds());
							if ((c != null)
									&& isComponentVisible((javax.swing.JComponent) c)) {
								Rectangle bounds = c.getBounds();
								javax.swing.SwingUtilities.convertRectangle(c
										.getParent(), bounds,
										jAliceFrame.dragFromComponent);
								if (jAliceFrame.dragFromComponent
										.getVisibleRect().contains(bounds)) {
									// System.out.println("returning true");
									return true;
								} else {
									return false;
								}
							} else {
								return false;
							}
						}
					} else {
						return false;
					}
				} else {
					// TODO is this safe?
					return false;
				}
			} else if (prefix.equals("behaviors")) {
				if (st.hasMoreTokens()) {
					token = st.nextToken();
					prefix = AuthoringToolResources.getPrefix(token);
					spec = AuthoringToolResources.getSpecifier(token);
					if (prefix.equals("createNewBehaviorButton")) {
						Component c = AuthoringToolResources.findButton(
								jAliceFrame.behaviorGroupsEditor,
						"create new behavior");
						if ((c != null) && c.isShowing()) {
							return true;
						} else {
							return false;
						}
					} else if (prefix.equals("elementTile") && (spec != null)) {
						Element element = world.getDescendantKeyed(spec);
						if (element != null) {
							if (st.hasMoreTokens()) {
								token = st.nextToken();
								prefix = AuthoringToolResources
								.getPrefix(token);
								spec = AuthoringToolResources
								.getSpecifier(token);
								if (prefix.equals("property") && (spec != null)) {
									Component c = AuthoringToolResources
									.findPropertyViewController(
											jAliceFrame.behaviorGroupsEditor,
											element, spec);
									if ((c != null) && c.isShowing()) {
										Rectangle bounds = c.getBounds();
										bounds = javax.swing.SwingUtilities
										.convertRectangle(
												c.getParent(),
												bounds,
												jAliceFrame.behaviorGroupsEditor
												.getScrollPane());
										if (jAliceFrame.behaviorGroupsEditor
												.getScrollPaneVisibleRect()
												.contains(bounds)) {
											return true;
										} else {
											return false;
										}
										// return true;
										// } else {
										// return false;
									} else {
										return false;
									}
								} else if (prefix.equals("more")) {
									Component dndPanel = AuthoringToolResources
									.findElementDnDPanel(
											jAliceFrame.behaviorGroupsEditor,
											element);
									if (dndPanel instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) {
										Component moreTile = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) dndPanel)
										.getMoreTile();
										if ((moreTile != null)
												&& moreTile.isShowing()) {
											Rectangle bounds = moreTile
											.getBounds();
											javax.swing.SwingUtilities
											.convertRectangle(
													moreTile
													.getParent(),
													bounds,
													jAliceFrame.behaviorGroupsEditor
													.getScrollPane());
											if (jAliceFrame.behaviorGroupsEditor
													.getScrollPaneVisibleRect()
													.contains(bounds)) {
												return true;
											} else {
												return false;
											}
											// return true;
											// } else {
											// return false;
										} else {
											return false;
										}
									}
								}
							} else {
								Component c = AuthoringToolResources
								.findElementDnDPanel(
										jAliceFrame.behaviorGroupsEditor,
										element);
								boolean visibleRectNotEmpty = true;
								if (c instanceof javax.swing.JComponent) {
									visibleRectNotEmpty = !((javax.swing.JComponent) c)
									.getVisibleRect().isEmpty();
								}
								if ((c != null) && c.isShowing()
										&& visibleRectNotEmpty) {
									Rectangle bounds = c.getBounds();
									javax.swing.SwingUtilities
									.convertRectangle(
											c.getParent(),
											bounds,
											jAliceFrame.behaviorGroupsEditor
											.getScrollPane());
									if (jAliceFrame.behaviorGroupsEditor
											.getScrollPaneVisibleRect()
											.contains(bounds)) {
										return true;
									} else {
										return false;
									}
									// return true;
								} else {
									return false;
								}
							}
						}
					}
				}
			} else if (prefix.equals("editors")) {
				if (st.hasMoreTokens()) {
					token = st.nextToken();
					prefix = AuthoringToolResources.getPrefix(token);
					spec = AuthoringToolResources.getSpecifier(token);
					if (prefix.equals("element")) {
						Element elementBeingEdited = world
						.getDescendantKeyed(spec);
						if (st.hasMoreTokens()) {
							if ((jAliceFrame.tabbedEditorComponent
									.getObjectBeingEdited() != null)
									&& jAliceFrame.tabbedEditorComponent
									.getObjectBeingEdited().equals(
											elementBeingEdited)) {
								// Editor editor =
								// jAliceFrame.tabbedEditorComponent.getEditorAt(
								// jAliceFrame.tabbedEditorComponent.getIndexOfObject(
								// elementBeingEdited ) );
								java.awt.Container container = (java.awt.Container) jAliceFrame.tabbedEditorComponent.tabbedPane
								.getComponentAt(jAliceFrame.tabbedEditorComponent
										.getIndexOfObject(elementBeingEdited));
								token = st.nextToken();
								prefix = AuthoringToolResources
								.getPrefix(token);
								spec = AuthoringToolResources
								.getSpecifier(token);
								if (prefix.equals("button")) {
									Component c = AuthoringToolResources
									.findButton(container, spec);
									if ((c != null) && c.isShowing()) {
										return true;
									} else {
										return false;
									}
								} else if (prefix.equals("elementTile")
										&& (spec != null)) {
									Element element = world
									.getDescendantKeyed(spec);
									if (element != null) {
										if (st.hasMoreTokens()) {
											token = st.nextToken();
											prefix = AuthoringToolResources
											.getPrefix(token);
											spec = AuthoringToolResources
											.getSpecifier(token);
											if (prefix.equals("property")
													&& (spec != null)) {
												Component c = AuthoringToolResources
												.findPropertyViewController(
														container,
														element, spec);
												if ((c != null)
														&& isComponentVisible((javax.swing.JComponent) c)) {
													return true;
												} else {
													return false;
												}
											} else if (prefix.equals("more")) {
												Component dndPanel = AuthoringToolResources
												.findElementDnDPanel(
														container,
														element);
												if (dndPanel instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) {
													Component moreTile = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) dndPanel)
													.getMoreTile();
													if ((moreTile != null)
															&& moreTile
															.isShowing()) {
														return true;
													} else {
														return false;
													}
												}
											}
										} else {
											Component c = AuthoringToolResources
											.findElementDnDPanel(
													container, element);
											if ((c != null)
													&& isComponentVisible((javax.swing.JComponent) c)) {
												return true;
											} else {
												return false;
											}
										}
									}
								} else if (prefix
										.equals("elementPrototypeTile")
										&& (spec != null)) {
									try {
										Class elementClass = Class
										.forName(spec);
										if (elementClass != null) {
											Component c = AuthoringToolResources
											.findPrototypeDnDPanel(
													container,
													elementClass);
											if ((c != null) && c.isShowing()) {
												return true;
											} else {
												return false;
											}
										}
									} catch (Exception e) {
										AuthoringTool.showErrorDialog(
												"Error while looking for ProtoypeDnDPanel using class "
												+ spec, e);
									}
								}
							} else {
								return false;
							}
						} else {
							int tabIndex = jAliceFrame.tabbedEditorComponent
							.getIndexOfObject(elementBeingEdited);
							if ((tabIndex >= 0)
									&& (tabIndex < jAliceFrame.tabbedEditorComponent.tabbedPane
											.getComponentCount())) {
								return true;
							} else {
								return false;
							}
						}
					}
				}
			} else if (componentMap.containsKey(prefix)) {
				Component c = (Component) componentMap.get(prefix);
				if ((c != null) && c.isShowing()) {
					return true;
				} else {
					return false;
				}
			}
		}

		return true;
	}

	public void makeIDVisible(String id)
	throws edu.cmu.cs.stage3.caitlin.stencilhelp.application.IDDoesNotExistException {
		java.util.StringTokenizer st = new java.util.StringTokenizer(id, ":",
				false);

		if (st.hasMoreTokens()) {
			String token = st.nextToken();
			String prefix = AuthoringToolResources.getPrefix(token);
			String spec = AuthoringToolResources.getSpecifier(token);

			if (prefix.equals("objectTree")) {
				if (spec != null) {
					Element element = world.getDescendantKeyed(spec);
					if (element != null) {
						JTree tree = jAliceFrame.worldTreeComponent.worldTree;
						WorldTreeModel worldTreeModel = (WorldTreeModel) tree
						.getModel();
						tree.scrollPathToVisible(new javax.swing.tree.TreePath(
								worldTreeModel.getPath(element)));
					}
				}
			} else if (prefix.equals("clipboard")) {
				// Do nothing

			} else if (prefix.equals("sceneEditor")) {
				if (spec.equals("large")) {
					jAliceFrame.sceneEditor.setGuiMode(SceneEditor.LARGE_MODE);
					// System.out.println("just made scene editor large");
				} else if (spec.equals("small")) {
					jAliceFrame.sceneEditor.setGuiMode(SceneEditor.SMALL_MODE);
					// System.out.println("just made scene editor small");
				}
				if (st.hasMoreTokens()) {
					token = st.nextToken(); // pull off singleView/quadView
					if (token.equals("singleView")) {
						jAliceFrame.sceneEditor
						.setViewMode(edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.CameraViewPanel.SINGLE_VIEW_MODE);
						// System.out.println("just set single view");
					} else if (token.equals("quadView")) {
						jAliceFrame.sceneEditor
						.setViewMode(edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.CameraViewPanel.QUAD_VIEW_MODE);
						// System.out.println("just made set quad view");
					}

					if (st.hasMoreTokens()) {
						token = st.nextToken();
						prefix = AuthoringToolResources.getPrefix(token);
						spec = AuthoringToolResources.getSpecifier(token);
						if (prefix.equals("galleryViewer")) {
							// jAliceFrame.sceneEditor.getGalleryViewer().setDirectory(
							// spec );
							// System.out.println("trying to show gallery viewer");
							if (st.hasMoreTokens()) {
								token = st.nextToken();
								prefix = AuthoringToolResources
								.getPrefix(token);
								spec = AuthoringToolResources
								.getSpecifier(token);
								if (prefix.equals("button")) {
									Component c = AuthoringToolResources
									.findButton(jAliceFrame.sceneEditor
											.getGalleryViewer(), spec);
									if ((c != null)
											&& c.isShowing()
											&& (c instanceof javax.swing.JComponent)) {
										((javax.swing.JComponent) c)
										.scrollRectToVisible(c
												.getBounds());
									}
								} else if (prefix.equals("galleryObject")) {
									Component c = AuthoringToolResources
									.findGalleryObject(
											jAliceFrame.sceneEditor
											.getGalleryViewer(),
											spec);
									if ((c != null)
											&& c.isShowing()
											&& (c instanceof javax.swing.JComponent)) {
										((javax.swing.JComponent) c)
										.scrollRectToVisible(c
												.getBounds());
									}
								}
							} else {
								// System.out.println("no more tokens");
							}
						} else {
							Component c = jAliceFrame.sceneEditor
							.getComponentForId(token);
							if ((c != null) && c.isShowing()
									&& (c instanceof javax.swing.JComponent)) {
								((javax.swing.JComponent) c)
								.scrollRectToVisible(c.getBounds());
							}
						}
					}
				}
			} else if (prefix.equals("details")) {
				if (spec == null) {
					spec = "";
				}
				Element element = world.getDescendantKeyed(spec);
				if (!jAliceFrame.dragFromComponent.getElement().equals(element)) {
					jAliceFrame.dragFromComponent.setElement(element);
				}
				if (jAliceFrame.dragFromComponent.getElement().equals(element)) {
					if (st.hasMoreTokens()) {
						token = st.nextToken();
						prefix = AuthoringToolResources.getPrefix(token);
						spec = AuthoringToolResources.getSpecifier(token);

						if (!prefix.equals("tab")) {
							boolean isViewController = false;
							if (prefix.equals("viewController")) {
								if (st.hasMoreTokens()) {
									token = st.nextToken();
									prefix = AuthoringToolResources
									.getPrefix(token);
									spec = AuthoringToolResources
									.getSpecifier(token);
									isViewController = true;
								}
							}
							if (prefix.equals("property")
									|| prefix.equals("variable")
									|| prefix.equals("textureMap")
									|| prefix.equals("sound")
									|| prefix.equals("other")) {
								jAliceFrame.dragFromComponent
								.selectTab(edu.cmu.cs.stage3.alice.authoringtool.DragFromComponent.PROPERTIES_TAB);
							}
							if (prefix.equals("userDefinedResponse")
									|| prefix.equals("responsePrototype")) {
								jAliceFrame.dragFromComponent
								.selectTab(edu.cmu.cs.stage3.alice.authoringtool.DragFromComponent.ANIMATIONS_TAB);
							}
							if (prefix.equals("userDefinedQuestion")
									|| prefix.equals("questionPrototype")) {
								jAliceFrame.dragFromComponent
								.selectTab(edu.cmu.cs.stage3.alice.authoringtool.DragFromComponent.QUESTIONS_TAB);
							}
							Component c = null;
							if (isViewController) {
								c = jAliceFrame.dragFromComponent
								.getPropertyViewComponentForKey(token);
							} else {
								c = jAliceFrame.dragFromComponent
								.getComponentForKey(token);
							}
							if (c != null
									&& c.getParent() instanceof ExpandablePanel) {
								ExpandablePanel ep = (ExpandablePanel) c
								.getParent();
								ep.setExpanded(true);
							}
							if ((c != null) && c.isShowing()
									&& (c instanceof javax.swing.JComponent)) {
								((javax.swing.JComponent) c)
								.scrollRectToVisible(c.getBounds());
							}
						}
					}
				}
			} else if (prefix.equals("behaviors")) {
				if (st.hasMoreTokens()) {
					token = st.nextToken();
					prefix = AuthoringToolResources.getPrefix(token);
					spec = AuthoringToolResources.getSpecifier(token);
					if (prefix.equals("elementTile") && (spec != null)) {
						Element element = world.getDescendantKeyed(spec);
						if (element != null) {
							if (st.hasMoreTokens()) {
								token = st.nextToken();
								prefix = AuthoringToolResources
								.getPrefix(token);
								spec = AuthoringToolResources
								.getSpecifier(token);
								if (prefix.equals("property") && (spec != null)) {
									Component c = AuthoringToolResources
									.findPropertyViewController(
											jAliceFrame.behaviorGroupsEditor,
											element, spec);
									if ((c != null)
											&& c.isShowing()
											&& (c instanceof javax.swing.JComponent)) {
										((javax.swing.JComponent) c)
										.scrollRectToVisible(c
												.getBounds());
									}
								} else if (prefix.equals("more")) {
									Component dndPanel = AuthoringToolResources
									.findElementDnDPanel(
											jAliceFrame.behaviorGroupsEditor,
											element);
									if (dndPanel instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) {
										Component moreTile = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) dndPanel)
										.getMoreTile();
										if ((moreTile != null)
												&& moreTile.isShowing()
												&& (moreTile instanceof javax.swing.JComponent)) {
											((javax.swing.JComponent) moreTile)
											.scrollRectToVisible(moreTile
													.getBounds());
										}
									}
								}
							} else {
								Component c = AuthoringToolResources
								.findElementDnDPanel(
										jAliceFrame.behaviorGroupsEditor,
										element);
								if ((c != null)
										&& c.isShowing()
										&& (c instanceof javax.swing.JComponent)) {
									// ((javax.swing.JComponent)c).scrollRectToVisible(
									// c.getBounds() );
									((javax.swing.JComponent) c)
									.scrollRectToVisible(new Rectangle(
											0, 0, c.getWidth(), c
											.getHeight()));
								}
							}
						}
					}
				}
			} else if (prefix.equals("editors")) {
				if (st.hasMoreTokens()) {
					token = st.nextToken();
					prefix = AuthoringToolResources.getPrefix(token);
					spec = AuthoringToolResources.getSpecifier(token);
					if (prefix.equals("element")) {
						Element elementBeingEdited = world
						.getDescendantKeyed(spec);
						if (elementBeingEdited == null) {
							throw new edu.cmu.cs.stage3.caitlin.stencilhelp.application.IDDoesNotExistException(
									spec);
						}
						if (st.hasMoreTokens()) {
							if (jAliceFrame.tabbedEditorComponent
									.getObjectBeingEdited() != elementBeingEdited) {
								editObject(elementBeingEdited);
							}

							// Editor editor =
							// jAliceFrame.tabbedEditorComponent.getEditorAt(
							// jAliceFrame.tabbedEditorComponent.getIndexOfObject(
							// elementBeingEdited ) );
							java.awt.Container container = (java.awt.Container) jAliceFrame.tabbedEditorComponent.tabbedPane
							.getComponentAt(jAliceFrame.tabbedEditorComponent
									.getIndexOfObject(elementBeingEdited));
							token = st.nextToken();
							prefix = AuthoringToolResources.getPrefix(token);
							spec = AuthoringToolResources.getSpecifier(token);
							if (prefix.equals("button")) {
								Component c = AuthoringToolResources
								.findButton(container, spec);
								if ((c != null)
										&& c.isShowing()
										&& (c instanceof javax.swing.JComponent)) {
									((javax.swing.JComponent) c)
									.scrollRectToVisible(c.getBounds());
								}
							} else if (prefix.equals("elementTile")
									&& (spec != null)) {
								Element element = world
								.getDescendantKeyed(spec);
								if (element != null) {
									if (st.hasMoreTokens()) {
										token = st.nextToken();
										prefix = AuthoringToolResources
										.getPrefix(token);
										spec = AuthoringToolResources
										.getSpecifier(token);
										if (prefix.equals("property")
												&& (spec != null)) {
											Component c = AuthoringToolResources
											.findPropertyViewController(
													container, element,
													spec);
											if ((c != null)
													&& c.isShowing()
													&& (c instanceof javax.swing.JComponent)) {
												((javax.swing.JComponent) c)
												.scrollRectToVisible(c
														.getBounds());
											}
										} else if (prefix.equals("more")) {
											Component dndPanel = AuthoringToolResources
											.findElementDnDPanel(
													container, element);
											if (dndPanel instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) {
												Component moreTile = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.FormattedElementViewController) dndPanel)
												.getMoreTile();
												if ((moreTile != null)
														&& moreTile.isShowing()
														&& (moreTile instanceof javax.swing.JComponent)) {
													((javax.swing.JComponent) moreTile)
													.scrollRectToVisible(moreTile
															.getBounds());
												}
											}
										}
									} else {
										Component c = AuthoringToolResources
										.findElementDnDPanel(container,
												element);
										if ((c != null)
												&& c.isShowing()
												&& (c instanceof javax.swing.JComponent)) {
											((javax.swing.JComponent) c)
											.scrollRectToVisible(c
													.getBounds());
										}
									}
								}
							} else if (prefix.equals("elementPrototypeTile")
									&& (spec != null)) {
								try {
									Class elementClass = Class.forName(spec);
									if (elementClass != null) {
										Component c = AuthoringToolResources
										.findPrototypeDnDPanel(
												container, elementClass);
										if ((c != null)
												&& c.isShowing()
												&& (c instanceof javax.swing.JComponent)) {
											((javax.swing.JComponent) c)
											.scrollRectToVisible(c
													.getBounds());
										}
									}
								} catch (Exception e) {
									AuthoringTool.showErrorDialog(
											"Error while looking for ProtoypeDnDPanel using class "
											+ spec, e);
								}
							}
						} else {
							int tabIndex = jAliceFrame.tabbedEditorComponent
							.getIndexOfObject(elementBeingEdited);
							if ((tabIndex < 0)
									|| (tabIndex >= jAliceFrame.tabbedEditorComponent.tabbedPane
											.getComponentCount())) {
								editObject(elementBeingEdited);
							}
						}
					}
				}
			} else if (componentMap.containsKey(prefix)) {
				Component c = (Component) componentMap.get(prefix);
				if ((c != null) && c.isShowing()
						&& (c instanceof javax.swing.JComponent)) {
					((javax.swing.JComponent) c).scrollRectToVisible(c
							.getBounds());
				}
			}
		}
	}

	synchronized public void makeWayPoint() {
		if (wayPoints.size() > 0) {
			WorldDifferencesCapsule currentWayPoint = (WorldDifferencesCapsule) wayPoints
			.get(0);
			currentWayPoint.stopListening();
		}

		WorldDifferencesCapsule wayPoint = new WorldDifferencesCapsule(this,
				world);
		wayPoints.add(0, wayPoint);
	}

	synchronized public void goToPreviousWayPoint() {
		if (wayPoints.size() > 0) {
			WorldDifferencesCapsule currentWayPoint = (WorldDifferencesCapsule) wayPoints
			.get(0);
			currentWayPoint.restoreWorld();
			currentWayPoint.dispose();
			wayPoints.remove(0);
		}

		if (wayPoints.size() > 0) {
			WorldDifferencesCapsule previousWayPoint = (WorldDifferencesCapsule) wayPoints
			.get(0);
			previousWayPoint.restoreWorld();
			previousWayPoint.startListening();
		}
	}

	synchronized public void clearWayPoints() {
		for (java.util.Iterator iter = wayPoints.iterator(); iter.hasNext();) {
			WorldDifferencesCapsule wayPoint = (WorldDifferencesCapsule) iter
			.next();
			wayPoint.dispose();
		}
		wayPoints.clear();
	}

	public boolean doesStateMatch(
			edu.cmu.cs.stage3.caitlin.stencilhelp.application.StateCapsule capsule) {
		if (capsule instanceof StencilStateCapsule) {
			StencilStateCapsule stencilStateCapsule = (StencilStateCapsule) capsule;

			String[] existantElements = stencilStateCapsule
			.getExistantElements();
			String[] nonExistantElements = stencilStateCapsule
			.getNonExistantElements();
			java.util.Set propertyValueKeys = stencilStateCapsule
			.getPropertyValueKeySet();
			java.util.Set elementPositions = stencilStateCapsule
			.getElementPositionKeySet();

			// check for all the elements that need to exist
			for (int i = 0; i < existantElements.length; i++) {
				if (world.getDescendantKeyed(existantElements[i]) == null) {
					return false;
				}
			}

			// System.out.println("elements exist ok");

			// make sure all the elements that shouldn't exist don't
			for (int i = 0; i < nonExistantElements.length; i++) {
				if (world.getDescendantKeyed(nonExistantElements[i]) != null) {
					return false;
				}
			}

			// System.out.println("elements don't exist ok");

			// check that elements are in the right positions
			for (java.util.Iterator iter = elementPositions.iterator(); iter
			.hasNext();) {
				String elementKey = (String) iter.next();
				int position = stencilStateCapsule
				.getElementPosition(elementKey);

				Element element = world.getDescendantKeyed(elementKey);
				if (element != null) {
					int actualPosition = element.getParent().getIndexOfChild(
							element);
					if (element instanceof UserDefinedResponse) {
						Property resp = element.getParent().getPropertyNamed(
								"responses");
						if (resp instanceof ObjectArrayProperty) {
							actualPosition = ((ObjectArrayProperty) resp)
							.indexOf(element);
							// System.out.println("index in responses: " +
							// actualPosition);
						}
					} else if (element instanceof edu.cmu.cs.stage3.alice.core.Response) {
						Property resp = element.getParent().getPropertyNamed(
						"componentResponses");
						if (resp instanceof ObjectArrayProperty) {
							actualPosition = ((ObjectArrayProperty) resp)
							.indexOf(element);
							// System.out.println("index in componentResponses: "
							// + actualPosition);
						}
					} else if (element instanceof edu.cmu.cs.stage3.alice.core.Behavior) {
						Property resp = element.getParent().getPropertyNamed(
						"behaviors");
						if (resp instanceof ObjectArrayProperty) {
							actualPosition = ((ObjectArrayProperty) resp)
							.indexOf(element);
							// System.out.println("index in responses: " +
							// actualPosition);
						}
					}

					// element isn't in the right place wrt to its parent
					if (position != actualPosition) {
						// System.out.println("actual position: " +
						// actualPosition + " correct position: " + position);
						return false;
					}
				} else {
					// element doesn't exist and it should
					return false;
				}
			}

			// System.out.println("elements in correct positions ok");

			for (java.util.Iterator iter = propertyValueKeys.iterator(); iter
			.hasNext();) {
				String propertyKey = (String) iter.next();
				String valueRepr = stencilStateCapsule
				.getPropertyValue(propertyKey);
				int dotIndex = propertyKey.lastIndexOf(".");
				String elementKey = propertyKey.substring(0, dotIndex);
				String propertyName = propertyKey.substring(dotIndex + 1);

				Element propertyOwner = world.getDescendantKeyed(elementKey);
				if (propertyOwner != null) {

					// getting "properties" of a call to a user defined response
					if (propertyOwner instanceof CallToUserDefinedResponse) {
						Property requiredParams = propertyOwner
						.getPropertyNamed("requiredActualParameters");

						Object udobj = requiredParams.getValue();
						if (udobj instanceof Variable[]) {
							Variable vars[] = (Variable[]) udobj;
							if (vars != null) {
								for (int i = 0; i < vars.length; i++) {
									if (vars[i].getKey(world).equals(
											propertyKey)) {
										String actualValueRepr = AuthoringToolResources
										.getReprForValue(vars[i]
										                      .getValue(), true);
										if (!actualValueRepr.equals(valueRepr)) {
											return false;
										}
									}
								}
							}
						}
					} else {
						Object value = propertyOwner.getPropertyNamed(
								propertyName).get();
						String actualValueRepr = AuthoringToolResources
						.getReprForValue(value, true);
						if (actualValueRepr != null) {
							if (!actualValueRepr.equals(valueRepr)) {
								return false;
							}
						} else {
							return false;
						}
					}
				} else {
					return false;
				}
			}

			// System.out.println("property value checks ok");

			// if we've arrived here, it means that all the new conditions have
			// been met. check to make sure user hasn't done
			// anything else that's strange.

			WorldDifferencesCapsule currentWayPoint = (WorldDifferencesCapsule) wayPoints
			.get(0);

			if ((currentWayPoint.otherPropertyChangesMade(propertyValueKeys))
					|| (currentWayPoint.otherElementsInsertedOrDeleted(
							existantElements, nonExistantElements))
							|| (currentWayPoint.otherElementsShifted(elementPositions))) {
				return false;
			}

			return true;

		}
		return true;
	}

	public void handleMouseEvent(java.awt.event.MouseEvent ev) {
		Point p = ev.getPoint();
		p = javax.swing.SwingUtilities.convertPoint((Component) ev.getSource(),
				p, jAliceFrame.getLayeredPane());
		Component newSource = jAliceFrame.getLayeredPane().findComponentAt(p);

		if ((newSource instanceof javax.swing.JLabel)
				|| (newSource instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementPrototypeDnDPanel.Tile)) {
			newSource = newSource.getParent(); // is this the right way to
			// handle this?
		}

		switch (ev.getID()) {
		case java.awt.event.MouseEvent.MOUSE_DRAGGED:
			if (stencilDragging) {
				newSource = dragStartSource;
			}
			break;
		case java.awt.event.MouseEvent.MOUSE_PRESSED:
			stencilDragging = true;
			dragStartSource = newSource;
			break;
		case java.awt.event.MouseEvent.MOUSE_RELEASED:
			if (stencilDragging) {
				newSource = dragStartSource;
				dragStartSource = null;
			}
			stencilDragging = false;
			break;
		case java.awt.event.MouseEvent.MOUSE_CLICKED:
		case java.awt.event.MouseEvent.MOUSE_ENTERED:
		case java.awt.event.MouseEvent.MOUSE_EXITED:
		case java.awt.event.MouseEvent.MOUSE_MOVED:
		default:
			break;
		}
		if (newSource != null) {
			p = javax.swing.SwingUtilities.convertPoint(jAliceFrame
					.getLayeredPane(), p, newSource);
			java.awt.event.MouseEvent newEv = new java.awt.event.MouseEvent(
					newSource, ev.getID(), ev.getWhen(), ev.getModifiers(),
					p.x, p.y, ev.getClickCount(), ev.isPopupTrigger());
			ev.consume();
			newSource.dispatchEvent(newEv);
			// }
		}
	}

	public void deFocus() {
		jAliceFrame.getContentPane().requestFocus();
	}

	public void performTask(String task) {
		String prefix = AuthoringToolResources.getPrefix(task);
		String spec = AuthoringToolResources.getSpecifier(task);

		if (prefix.equals("loadWorld")) {
			// boolean askForSave = !((currentWorldLocation != null) &&
			// currentWorldLocation.getAbsolutePath().startsWith(tutorialDirectory.getAbsolutePath()));
			boolean askForSave = false;
			loadWorld(spec, askForSave);
		}
	}

	public Dimension getScreenSize() {
		return jAliceFrame.getSize();
	}

	public void launchTutorial() {
		launchTutorialFile(null);
	}

	public void launchTutorialFile(File tutorialFile) {
		if (tutorialFile == null) {
			tutorialFile = tutorialOne;
		}
		tutorialFile = tutorialFile.getAbsoluteFile(); // BIG HACK
		showStencils();
		stencilManager.loadStencilTutorial(tutorialFile);
	}

	public File getTutorialDirectory() {
		return tutorialDirectory;
	}

	public File getExampleWorldsDirectory() {
		return new File(authoringToolConfig
				.getValue("directories.examplesDirectory")).getAbsoluteFile();
	}

	public File getTemplateWorldsDirectory() {
		return new File(authoringToolConfig
				.getValue("directories.templatesDirectory")).getAbsoluteFile();
	}

	public FileFilter getWorldFileFilter() {
		return worldFileFilter;
	}

	public FileFilter getCharacterFileFilter() {
		return characterFileFilter;
	}

	// Dialog Handling

	public Variable showNewVariableDialog(String title, Element context) {
		newVariableContentPane.reset(context);
		newVariableContentPane.setTitle(title);
		newVariableContentPane.setListsOnly(false);
		newVariableContentPane.setShowValue(true);
		return showNewVariableDialog(newVariableContentPane, context);
	}

	public Variable showNewVariableDialog(String title, Element context,
			boolean listsOnly, boolean showValue) {
		newVariableContentPane.reset(context);
		newVariableContentPane.setListsOnly(listsOnly);
		newVariableContentPane.setShowValue(showValue);
		newVariableContentPane.setTitle(title);
		return showNewVariableDialog(newVariableContentPane, context);
	}

	protected Variable showNewVariableDialog(
			NewVariableContentPane newVariablePaneToShow, Element context) {
		int result = DialogManager.showDialog(newVariablePaneToShow);
		switch (result) {
		case edu.cmu.cs.stage3.swing.ContentPane.OK_OPTION:
			return newVariablePaneToShow.getVariable();
		case edu.cmu.cs.stage3.swing.ContentPane.CANCEL_OPTION:
			return null;
		default:
			return null;
		}
	}

	public edu.cmu.cs.stage3.alice.core.Sound promptUserForRecordedSound(
			edu.cmu.cs.stage3.alice.core.Sandbox parent) {
		final SoundRecorder soundRecorder = new SoundRecorder();
		soundRecorder.setParentToCheckForNameValidity(parent);

		int result = DialogManager.showDialog(soundRecorder);
		edu.cmu.cs.stage3.alice.core.Sound sound = null;

		switch (result) {
		case edu.cmu.cs.stage3.swing.ContentPane.OK_OPTION:
			sound = soundRecorder.getSound();
			if (sound != null) {
				getUndoRedoStack().startCompound();
				try {
					parent.addChild(sound);
					parent.sounds.add(sound);
				} finally {
					getUndoRedoStack().stopCompound();
				}
			}
			break;
		}
		return sound;
	}

	public File getCurrentWorldLocation() {
		return currentWorldLocation;
	}

	public String getCurrentRendererText() {
		return renderTargetFactory.toString();
	}

	private boolean worldRun() {
		undoRedoStack.setIsListening(false);

		fireStateChanging(AuthoringToolStateChangedEvent.AUTHORING_STATE,
				AuthoringToolStateChangedEvent.RUNTIME_STATE);
		fireWorldStarting(AuthoringToolStateChangedEvent.AUTHORING_STATE,
				AuthoringToolStateChangedEvent.RUNTIME_STATE, world);

		// if( authoringToolConfig.getValue( "reloadWorldScriptOnRun"
		// ).equalsIgnoreCase( "true" ) ) {
		// try {
		// if( world.script.isAssociatedWithFile() ) {
		// world.script.loadFromAssociatedFile();
		// }
		// } catch( IOException e ) {
		// AuthoringTool.showErrorDialog(
		// "Error while loading script from associated file.", e );
		// }
		// }

		// play count
		countSomething("edu.cmu.cs.stage3.alice.authoringtool.playCount");

		world.preserve();
		try {
			// scheduler.addDoOnceRunnable(new Runnable() {
			// public void run() {
			// double t = AuthoringToolResources.getCurrentTime();
			// world.start(t);
			// //System.out.println( "world.start( " + t + " )" );
			// }
			// });

			worldClock.start();
			scheduler.addEachFrameRunnable(worldScheduleRunnable);
			actions.pauseWorldAction.setEnabled(true);
			actions.resumeWorldAction.setEnabled(false);
			fireStateChanged(AuthoringToolStateChangedEvent.AUTHORING_STATE,
					AuthoringToolStateChangedEvent.RUNTIME_STATE);
			fireWorldStarted(AuthoringToolStateChangedEvent.AUTHORING_STATE,
					AuthoringToolStateChangedEvent.RUNTIME_STATE, world);
		} catch (PyException e) {
			world.restore();
			AuthoringTool.showErrorDialog("Error during world start.", null);
			if (Py.matchException(e, Py.SystemExit)) {
				// TODO
			} else {
				Py.printException(e, null, pyStdErr);
			}
			return false;
		} catch (edu.cmu.cs.stage3.alice.core.SimulationException e) {
			world.restore();
			showSimulationExceptionDialog(e);
			return false;
		} catch (edu.cmu.cs.stage3.alice.core.ExceptionWrapper e) {
			world.restore();
			Exception wrappedException = e.getWrappedException();
			if (wrappedException instanceof edu.cmu.cs.stage3.alice.core.SimulationException) {
				showSimulationExceptionDialog((edu.cmu.cs.stage3.alice.core.SimulationException) wrappedException);
			} else {
				AuthoringTool.showErrorDialog("Error during world start.",
						wrappedException);
			}
			return false;
		} catch (Throwable t) {
			world.restore();
			showErrorDialog("Error during world start.", t);
			return false;
		}
		return true;
	}

	public void worldStopRunning() {
		fireStateChanging(AuthoringToolStateChangedEvent.RUNTIME_STATE,
				AuthoringToolStateChangedEvent.AUTHORING_STATE);
		fireWorldStopping(AuthoringToolStateChangedEvent.RUNTIME_STATE,
				AuthoringToolStateChangedEvent.AUTHORING_STATE, world);
		// Thread.dumpStack();
		scheduler.removeEachFrameRunnable(worldScheduleRunnable);
		try {
			// world.stop(AuthoringToolResources.getCurrentTime());
			worldClock.stop();
		} catch (PyException e) {
			AuthoringTool.showErrorDialog("Error during world stop.", null);
			if (Py.matchException(e, Py.SystemExit)) {
				// TODO
			} else {
				Py.printException(e, null, pyStdErr);
			}
		} catch (Throwable t) {
			AuthoringTool.showErrorDialog("Error during world stop.", t);
		}
		world.restore();
		undoRedoStack.setIsListening(true);
		fireStateChanged(AuthoringToolStateChangedEvent.RUNTIME_STATE,
				AuthoringToolStateChangedEvent.AUTHORING_STATE);
		fireWorldStopped(AuthoringToolStateChangedEvent.RUNTIME_STATE,
				AuthoringToolStateChangedEvent.AUTHORING_STATE, world);
	}

	public double getAspectRatio() {
		double aspectRatio = 0.0;
		if (getCurrentCamera() instanceof edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) {
			edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera cam = (edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) getCurrentCamera();
			Number hAngle = cam.horizontalViewingAngle.getNumberValue();
			Number vAngle = cam.verticalViewingAngle.getNumberValue();
			if ((hAngle != null) && (vAngle != null)) {
				aspectRatio = hAngle.doubleValue() / vAngle.doubleValue();
			}
		}
		return aspectRatio;
	}

	private void checkForUnreferencedCurrentMethod() {
		Object object = getObjectBeingEdited();
		if (object instanceof UserDefinedResponse) {
			if (!AuthoringToolResources.isMethodHookedUp(
					(UserDefinedResponse) object, world)
					&& !authoringToolConfig.getValue(
					"doNotShowUnhookedMethodWarning").equalsIgnoreCase(
					"true")) {
				String objectRepr = AuthoringToolResources.getReprForValue(
						object, true);
				DialogManager
				.showMessageDialog(
						"The current method ("
						+ objectRepr
						+ ") is not called by any events or by any other methods which might be called by any events.",
						"Warning: Current method will not be called.",
						JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	public void play() {
		jAliceFrame.playButton.setEnabled(false);

		checkForUnreferencedCurrentMethod();

		if (worldRun()) {
			// if( authoringToolConfig.getValue(
			// "rendering.renderWindowMatchesSceneEditor" ).equalsIgnoreCase(
			// "true" ) ) {
			// SceneEditor
			// sceneEditor =
			// AuthoringTool.this.jAliceFrame.getTabbedEditorComponent().getCurrentSceneEditor();
			// if( sceneEditor != null ) {
			// Rectangle bounds = renderContentPane.getRenderBounds();
			// authoringToolConfig.setValue( "rendering.renderWindowBounds",
			// bounds.x + ", " + bounds.y + ", " +
			// sceneEditor.getRenderSize().width + ", " +
			// sceneEditor.getRenderSize().height );
			// }
			// }
			double aspectRatio = getAspectRatio();
			stdErrOutContentPane.stopReactingToPrint();
			renderContentPane.setAspectRatio(aspectRatio);
			renderContentPane.getRenderPanel().add(renderPanel,
					java.awt.BorderLayout.CENTER);

			int result = DialogManager.showDialog(renderContentPane);
			stdErrOutContentPane.startReactingToPrint();
		}
		jAliceFrame.playButton.setEnabled(true);
	}

	public static boolean isresizable = true;

	public JPanel playWhileEncoding(String directory) {
		jAliceFrame.playButton.setEnabled(false);

		checkForUnreferencedCurrentMethod();

		if (worldRun()) {
			// if( authoringToolConfig.getValue(
			// "rendering.renderWindowMatchesSceneEditor" ).equalsIgnoreCase(
			// "true" ) ) {
			// SceneEditor
			// sceneEditor =
			// AuthoringTool.this.jAliceFrame.getTabbedEditorComponent().getCurrentSceneEditor();
			// if( sceneEditor != null ) {
			// Rectangle bounds = renderContentPane.getRenderBounds();
			// authoringToolConfig.setValue( "rendering.renderWindowBounds",
			// bounds.x + ", " + bounds.y + ", " +
			// sceneEditor.getRenderSize().width + ", " +
			// sceneEditor.getRenderSize().height );
			// }
			// }
			double aspectRatio = getAspectRatio();
			stdErrOutContentPane.stopReactingToPrint();
			captureContentPane.setExportDirectory(directory);
			captureContentPane.captureInit();
			captureContentPane.setAspectRatio(aspectRatio);
			captureContentPane.getRenderPanel().add(renderPanel,
					java.awt.BorderLayout.CENTER);
			jAliceFrame.sceneEditor.makeDirty();
			isresizable = false;
			int result = DialogManager.showDialog(captureContentPane);
			stdErrOutContentPane.startReactingToPrint();
		}
		jAliceFrame.playButton.setEnabled(true);
		isresizable = true;
		return captureContentPane;
	}

	public void pause() {
		// if (timeOfPause == 0.0) {
		// timeOfPause = AuthoringToolResources.getCurrentTime();
		// scheduler.removeEachFrameRunnable(worldScheduleRunnable);
		// actions.pauseWorldAction.setEnabled(false);
		// actions.resumeWorldAction.setEnabled(true);
		// renderTarget.getAWTComponent().requestFocus();
		// }
		worldClock.pause();
		actions.pauseWorldAction.setEnabled(false);
		actions.resumeWorldAction.setEnabled(true);
		renderTarget.getAWTComponent().requestFocus();
	}

	public DefaultClock getWorldClock() {
		return worldClock;

	}

	public void resume() {
		// if (timeOfPause != 0.0) {
		// timeDifferential += AuthoringToolResources.getCurrentTime() -
		// timeOfPause;
		// timeOfPause = 0.0;
		// scheduler.addEachFrameRunnable(worldScheduleRunnable);
		// actions.pauseWorldAction.setEnabled(true);
		// actions.resumeWorldAction.setEnabled(false);
		// renderTarget.getAWTComponent().requestFocus();
		// }
		worldClock.resume();
		actions.pauseWorldAction.setEnabled(true);
		actions.resumeWorldAction.setEnabled(false);
		renderTarget.getAWTComponent().requestFocus();
	}

	public void restartWorld() {
		try {
			worldClock.stop();
			world.restore();
			actions.pauseWorldAction.setEnabled(true);
			actions.resumeWorldAction.setEnabled(false);

			renderContentPane.setSpeedSliderValue(0);
			// worldClock.setSpeed( 1 );
			worldClock.start();

		} catch (PyException e) {
			AuthoringTool
			.showErrorDialog("Error while restarting world.", null);
			if (Py.matchException(e, Py.SystemExit)) {
				// TODO
			} else {
				Py.printException(e, null, pyStdErr);
			}
		} catch (edu.cmu.cs.stage3.alice.core.SimulationException e) {
			showSimulationExceptionDialog(e);
		} catch (edu.cmu.cs.stage3.alice.core.ExceptionWrapper e) {
			Exception wrappedException = e.getWrappedException();
			if (wrappedException instanceof edu.cmu.cs.stage3.alice.core.SimulationException) {
				showSimulationExceptionDialog((edu.cmu.cs.stage3.alice.core.SimulationException) wrappedException);
			} else {
				AuthoringTool.showErrorDialog("Error while restarting world.",
						wrappedException);
			}
		} catch (Throwable t) {
			AuthoringTool.showErrorDialog("Error while restarting world.", t);
		}
		renderTarget.getAWTComponent().requestFocus();
	}

	public void stopWorld() {
		renderContentPane.stopWorld();
	}

	public void setWorldSpeed(double newSpeed) {
		worldClock.setSpeed(newSpeed);
	}

	public void takePicture() {
		try {
			storeCapturedImage(renderTarget.getOffscreenImage());
		} catch (Throwable t) {
			AuthoringTool.showErrorDialog("Error capturing image.", t);
		}
		renderTarget.getAWTComponent().requestFocus();
	}

	public SoundStorage getSoundStorage() {
		return soundStorage;
	}

	public void setSoundStorage(SoundStorage myS) {
		soundStorage = myS;
	}

}