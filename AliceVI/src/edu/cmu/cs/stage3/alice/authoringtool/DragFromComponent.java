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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;

import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable;
import edu.cmu.cs.stage3.alice.authoringtool.dialog.NewQuestionContentPane;
import edu.cmu.cs.stage3.alice.authoringtool.dialog.NewResponseContentPane;
import edu.cmu.cs.stage3.alice.authoringtool.editors.variablegroupeditor.VariableGroupEditor;
import edu.cmu.cs.stage3.alice.authoringtool.event.ElementSelectionListener;
import edu.cmu.cs.stage3.alice.authoringtool.util.AliceTabbedPaneUI;
import edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype;
import edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype;
import edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.EditObjectButton;
import edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype;
import edu.cmu.cs.stage3.alice.authoringtool.util.ExpandablePanel;
import edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer;
import edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory;
import edu.cmu.cs.stage3.alice.authoringtool.util.Releasable;
import edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype;
import edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.CallToUserDefinedResponsePrototypeDnDPanel;
import edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementDnDPanel;
import edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ObjectArrayPropertyPanel;
import edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.SoundsPanel;
import edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.TextureMapsPanel;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Pose;
import edu.cmu.cs.stage3.alice.core.Property;
import edu.cmu.cs.stage3.alice.core.Question;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Sandbox;
import edu.cmu.cs.stage3.alice.core.Sound;
import edu.cmu.cs.stage3.alice.core.TextureMap;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.Variable;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.core.event.ChildrenEvent;
import edu.cmu.cs.stage3.alice.core.event.ChildrenListener;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener;
import edu.cmu.cs.stage3.alice.core.event.PropertyEvent;
import edu.cmu.cs.stage3.alice.core.event.PropertyListener;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.question.PartKeyed;
import edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion;
import edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion;
import edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse;
import edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation;
import edu.cmu.cs.stage3.alice.core.response.PropertyAnimation;
import edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse;
import edu.cmu.cs.stage3.alice.core.ui.AccessibleButton;
import edu.cmu.cs.stage3.swing.ContentPane;
import edu.cmu.cs.stage3.swing.DialogManager;
import edu.cmu.cs.stage3.util.StringObjectPair;

// TODO

/**
 * @author Jason Pratt
 * @author Brett Snare
 */
public class DragFromComponent extends JPanel implements
		ElementSelectionListener {

	BorderLayout borderLayout1 = new BorderLayout();
	JPanel propertySubPanel = new JPanel();
	BorderLayout borderLayout2 = new BorderLayout();
	Border border1;
	// JScrollPane propertyScrollPane = new JScrollPane();
	Border border2;
	Border border3;
	Border border4;
	Border border5;
	Border border6;
	Border border7;
	JPanel comboPanel = new JPanel();
	JLabel ownerLabel = new JLabel();
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	JTabbedPane tabbedPane = new JTabbedPane();
	JScrollPane propertiesScrollPane = new JScrollPane();
	JScrollPane animationsScrollPane = new JScrollPane();
	GridBagLayout gridBagLayout2 = new GridBagLayout();
	JPanel propertiesPanel = new JPanel();
	GridBagLayout gridBagLayout3 = new GridBagLayout();
	JPanel animationsPanel = new JPanel();
	JScrollPane questionsScrollPane = new JScrollPane();
	JPanel questionsPanel = new JPanel();
	GridBagLayout gridBagLayout4 = new GridBagLayout();
	JScrollPane otherScrollPane = new JScrollPane();
	JPanel otherPanel = new JPanel();
	GridBagLayout gridBagLayout5 = new GridBagLayout();
	Border border8;
	Border border9;
	// //////////////////
	// END Autogenerated
	// //////////////////

	public final static int PROPERTIES_TAB = 0;
	public final static int ANIMATIONS_TAB = 1;
	public final static int QUESTIONS_TAB = 2;
	public final static int OTHER_TAB = 3;

	protected Element element;
	protected VariableGroupEditor variableGroupEditor = new VariableGroupEditor();
	protected NewResponseContentPane newResponseContentPane;
	protected NewQuestionContentPane newQuestionContentPane;
	protected ObjectArrayProperty vars;
	protected ObjectArrayProperty responses;
	protected ResponsesListener responsesListener = new ResponsesListener();
	protected ObjectArrayProperty questions;
	protected QuestionsListener questionsListener = new QuestionsListener();
	protected ObjectArrayProperty poses;
	protected PosesListener posesListener = new PosesListener();
	protected GridBagConstraints constraints = new GridBagConstraints(0, 0, 1,
			1, 1.0, 0.0, GridBagConstraints.CENTER,
			GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);

	protected GridBagConstraints glueConstraints = new GridBagConstraints(0, 0,
			1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0);

	protected Border spacingBorder = BorderFactory
			.createEmptyBorder(4, 0, 8, 0);
	protected ChildrenListener parentListener = new ChildrenListener() {
		private Element parent;

		public void childrenChanging(ChildrenEvent ev) {
			if ((ev.getChild() == DragFromComponent.this.element)
					&& (ev.getChangeType() == ChildrenEvent.CHILD_REMOVED)) {
				parent = DragFromComponent.this.element.getParent();
			}
		}

		public void childrenChanged(ChildrenEvent ev) {
			if ((ev.getChild() == DragFromComponent.this.element)
					&& (ev.getChangeType() == ChildrenEvent.CHILD_REMOVED)) {
				DragFromComponent.this.setElement(null);
				parent.removeChildrenListener(this);
			}
		}
	};
	protected PropertyListener nameListener = new PropertyListener() {
		public void propertyChanging(PropertyEvent ev) {
		}

		public void propertyChanged(PropertyEvent ev) {
			DragFromComponent.this.ownerLabel.setText(ev.getValue().toString()
					+ "'s details");
		}
	};
	protected JButton newAnimationButton = new AccessibleButton("create new method");
	protected JButton newQuestionButton = new AccessibleButton("create new "
			+ AuthoringToolResources.QUESTION_STRING);
	protected JButton capturePoseButton = new AccessibleButton("capture pose");
	protected UserDefinedResponse newlyCreatedAnimation;
	protected UserDefinedQuestion newlyCreatedQuestion;
	protected Pose newlyCreatedPose;
	protected AuthoringTool authoringTool;
	protected SoundsPanel soundsPanel;
	protected TextureMapsPanel textureMapsPanel;
	protected ObjectArrayPropertyPanel miscPanel;

	protected HashSet panelsToClean = new HashSet();

	public DragFromComponent(AuthoringTool authoringTool) {
		this.authoringTool = authoringTool;
		variableGroupEditor.setAuthoringTool(authoringTool);
		newResponseContentPane = new NewResponseContentPane();
		newQuestionContentPane = new NewQuestionContentPane();
		soundsPanel = new SoundsPanel(authoringTool);
		textureMapsPanel = new TextureMapsPanel(authoringTool);
		miscPanel = new ObjectArrayPropertyPanel("Misc", authoringTool);
		jbInit();
		guiInit();
	}

	private void guiInit() {
		newAnimationButton.setMargin(new Insets(2, 4, 2, 4));
		newAnimationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (responses != null) {
					newResponseContentPane.reset(responses.getOwner());
					int result = DialogManager
							.showDialog(newResponseContentPane);
					if (result == ContentPane.OK_OPTION) {
						authoringTool.getUndoRedoStack().startCompound();
						try {
							UserDefinedResponse response = new UserDefinedResponse();
							response.name.set(newResponseContentPane
									.getNameValue());
							responses.getOwner().addChild(response);
							responses.add(response);
						} finally {
							authoringTool.getUndoRedoStack().stopCompound();
						}
					}
				}
			}
		});
		newQuestionButton.setMargin(new Insets(2, 4, 2, 4));
		newQuestionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (questions != null) {
					newQuestionContentPane.reset(questions.getOwner());
					int result = DialogManager
							.showDialog(newQuestionContentPane);
					if (result == ContentPane.OK_OPTION) {
						authoringTool.getUndoRedoStack().startCompound();
						try {
							UserDefinedQuestion question = new UserDefinedQuestion();
							question.name.set(newQuestionContentPane
									.getNameValue());
							question.valueClass.set(newQuestionContentPane
									.getTypeValue());
							questions.getOwner().addChild(question);
							questions.add(question);
						} finally {
							authoringTool.getUndoRedoStack().stopCompound();
						}
					}
				}
			}
		});

		capturePoseButton.setMargin(new Insets(2, 4, 2, 4));
		capturePoseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (poses != null) {
					authoringTool.getUndoRedoStack().startCompound();
					try {
						Transformable transformable = (Transformable) poses
								.getOwner();
						Pose pose = Pose.manufacturePose(transformable,
								transformable);
						pose.name.set(AuthoringToolResources
								.getNameForNewChild("pose", poses.getOwner()));
						poses.getOwner().addChild(pose);
						poses.add(pose);
					} finally {
						authoringTool.getUndoRedoStack().stopCompound();
					}
				}
			}
		});

		tabbedPane.setUI(new AliceTabbedPaneUI());
		tabbedPane.setOpaque(false);
		tabbedPane.setSelectedIndex(ANIMATIONS_TAB);

		// to make tab color match
		propertiesScrollPane.setBackground(Color.white);
		animationsScrollPane.setBackground(Color.white);
		questionsScrollPane.setBackground(Color.white);
		otherScrollPane.setBackground(Color.white);

		soundsPanel.setExpanded(false);
		textureMapsPanel.setExpanded(false);
		miscPanel.setExpanded(false);

		// tooltips
		String cappedQuestionString = AuthoringToolResources.QUESTION_STRING
				.substring(0, 1).toUpperCase()
				+ AuthoringToolResources.QUESTION_STRING.substring(1);

		comboPanel
				.setToolTipText("<html><font face=arial size=-1>Details of the"
						+ "<p>Selected Object.</font></html>");

		tabbedPane.setToolTipTextAt(PROPERTIES_TAB,
				"<html><font face=arial size=-1>"
						+ "<p>View and edit<p>the Properties of the "
						+ "Selected Object.</font></html>");

		tabbedPane
				.setToolTipTextAt(
						ANIMATIONS_TAB,
						"<html><font face=arial size=-1>"
								+ "<p>View and edit<p>the Methods of the Selected "
								+ "Object.</font></html>");

		tabbedPane.setToolTipTextAt(QUESTIONS_TAB,
				"<html><font face=arial size=-1>"
						+ "<p>View and edit<p>the "
						+ cappedQuestionString + "s"
						+ " of the Selected Object." + "</font></html>");

		newAnimationButton
				.setToolTipText("<html><font face=arial size=-1>Create a New Blank Method<p>"
						+ "and Open it for Editing.</font></html>");

		newQuestionButton
				.setToolTipText("<html><font face=arial size=-1>Create a New Blank "
						+ cappedQuestionString
						+ "<p>and Open it for Editing."
						+ "</font></html>");

		propertiesPanel
				.setToolTipText("<html><font face=arial size=-1>View and edit<p>the Properties of the Selected "
						+ "Object.</font></html>");

		animationsPanel
				.setToolTipText("<html><font face=arial size=-1><p>Methods are "
						+ "the actions that an object knows how to do.</font></html>");

		questionsPanel.setToolTipText("<html><font face=arial size=-1>"
				+ "<p>"
				+ cappedQuestionString + "s"
				+ " are the things that an object can<p>answer about "
				+ "themselves or the world.</font></html>");

	}

	public void paintComponent(java.awt.Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.black);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
	}

	// ElementSelectionListener interface
	public void elementSelected(Element element) {
		setElement(element);
		authoringTool.hackStencilUpdate();
	}

	// the swing worker was removed to fix alice locking up if you don't wait
	// for her to
	// settle down after a world load. as a bonus, the every now and then
	// selection failure
	// on world load also went a away.
	// TODO: figure out why the worker was necessary in the first place
	// dennisc
	// public void elementSelected( final Element element ) {
	// edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker worker = new
	// edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker() {
	// public Object construct() {
	// setElement( element );
	// authoringTool.hackStencilUpdate();
	// return null;
	// }
	// };
	// worker.start();
	// }

	public Element getElement() {
		return element;
	}

	synchronized public void setElement(Element element) {
		vars = null;
		if (responses != null) {
			responses.removeObjectArrayPropertyListener(responsesListener);
			responses = null;
		}
		if (questions != null) {
			questions.removeObjectArrayPropertyListener(questionsListener);
			questions = null;
		}
		if (poses != null) {
			poses.removeObjectArrayPropertyListener(posesListener);
			poses = null;
		}
		if (this.element != null) {
			if (this.element.getParent() != null) {
				this.element.getParent().removeChildrenListener(parentListener);
			}
			this.element.name.removePropertyListener(nameListener);
		}

		this.element = element;

		if (element != null) {
			ownerLabel.setText(AuthoringToolResources.getReprForValue(element)
					+ "'s details");
			if (element.getParent() != null) {
				element.getParent().addChildrenListener(parentListener);
			}
			element.name.addPropertyListener(nameListener);
			if (element.getSandbox() == element) { // HACK: only show
				// user-defined stuff for
				// sandboxes
				vars = (ObjectArrayProperty) element
						.getPropertyNamed("variables");
				responses = (ObjectArrayProperty) element
						.getPropertyNamed("responses");
				if (responses != null) {
					responses.addObjectArrayPropertyListener(responsesListener);
				}
				questions = (ObjectArrayProperty) element
						.getPropertyNamed("questions");
				if (questions != null) {
					questions.addObjectArrayPropertyListener(questionsListener);
				}
			}
			if (element instanceof Transformable) {
				poses = ((Transformable) element).poses;
				if (poses != null) {
					poses.addObjectArrayPropertyListener(posesListener);
				}
			}
		} else {
			ownerLabel.setText("");
		}

		int selectedIndex = tabbedPane.getSelectedIndex();
		refreshGUI();
		tabbedPane.setSelectedIndex(selectedIndex);
	}

	public void selectTab(int index) {
		tabbedPane.setSelectedIndex(index);
	}

	public int getSelectedTab() {
		return tabbedPane.getSelectedIndex();
	}

	public String getKeyForComponent(Component c) {
		World world = authoringTool.getWorld();
		if (c == variableGroupEditor.getNewVariableButton()) {
			return "newVariableButton";
		} else if (c == newAnimationButton) {
			return "newAnimationButton";
		} else if (c == newQuestionButton) {
			return "newQuestionButton";
		} else if (c instanceof DnDGroupingPanel) {
			try {
				java.awt.datatransfer.Transferable transferable = ((DnDGroupingPanel) c)
						.getTransferable();
				if (AuthoringToolResources.safeIsDataFlavorSupported(
						transferable,
						ElementReferenceTransferable.variableReferenceFlavor)) {
					Variable v = (Variable) transferable
							.getTransferData(ElementReferenceTransferable.variableReferenceFlavor);
					return "variable<" + v.getKey(world) + ">";
				} else if (AuthoringToolResources.safeIsDataFlavorSupported(
						transferable,
						ElementReferenceTransferable.textureMapReferenceFlavor)) {
					TextureMap t = (TextureMap) transferable
							.getTransferData(ElementReferenceTransferable.textureMapReferenceFlavor);
					return "textureMap<" + t.getKey(world) + ">";
				} else if (AuthoringToolResources.safeIsDataFlavorSupported(
						transferable, AuthoringToolResources
								.getReferenceFlavorForClass(Sound.class))) {
					Sound s = (Sound) transferable
							.getTransferData(AuthoringToolResources
									.getReferenceFlavorForClass(Sound.class));
					return "sound<" + s.getKey(world) + ">";
				} else if (AuthoringToolResources.safeIsDataFlavorSupported(
						transferable,
						ElementReferenceTransferable.elementReferenceFlavor)) {
					Element e = (Element) transferable
							.getTransferData(ElementReferenceTransferable.elementReferenceFlavor);
					return "misc<" + e.getKey(world) + ">";
				} else if (AuthoringToolResources.safeIsDataFlavorSupported(
						transferable,
						PropertyReferenceTransferable.propertyReferenceFlavor)) {
					Property p = (Property) transferable
							.getTransferData(PropertyReferenceTransferable.propertyReferenceFlavor);
					return "property<" + p.getName() + ">";
				} else if (AuthoringToolResources
						.safeIsDataFlavorSupported(
								transferable,
								CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor)) {
					CallToUserDefinedResponsePrototype p = (CallToUserDefinedResponsePrototype) transferable
							.getTransferData(CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor);
					return "userDefinedResponse<"
							+ p.getActualResponse().getKey(world) + ">";
				} else if (AuthoringToolResources
						.safeIsDataFlavorSupported(
								transferable,
								CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor)) {
					CallToUserDefinedQuestionPrototype p = (CallToUserDefinedQuestionPrototype) transferable
							.getTransferData(CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor);
					return "userDefinedQuestion<"
							+ p.getActualQuestion().getKey(world) + ">";
				} else if (AuthoringToolResources
						.safeIsDataFlavorSupported(
								transferable,
								ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor)) {
					ElementPrototype p = (ElementPrototype) transferable
							.getTransferData(ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor);
					if (Response.class.isAssignableFrom(p.getElementClass())) {
						return "responsePrototype<"
								+ p.getElementClass().getName() + ">";
					} else if (Question.class.isAssignableFrom(p
							.getElementClass())) {
						return "questionPrototype<"
								+ p.getElementClass().getName() + ">";
					} else {
						return null;
					}
				} else {
					return null;
				}
			} catch (Exception e) {
				AuthoringTool.showErrorDialog(
						"Error examining DnDGroupingPanel.", e);
				return null;
			}
		} else if (c instanceof EditObjectButton) {
			Object o = ((EditObjectButton) c).getObject();
			if (o instanceof Element) {
				Element e = (Element) o;
				return "editObjectButton<" + e.getKey(world) + ">";
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public Component getComponentForKey(String key) {
		String prefix = AuthoringToolResources.getPrefix(key);
		String spec = AuthoringToolResources.getSpecifier(key);
		World world = authoringTool.getWorld();
		if (key.equals("newVariableButton")) {
			return variableGroupEditor.getNewVariableButton();
		} else if (key.equals("newAnimationButton")) {
			return newAnimationButton;
		} else if (key.equals("newQuestionButton")) {
			return newQuestionButton;
		} else if (prefix.equals("variable") && (spec != null)) {
			Element e = world.getDescendantKeyed(spec);
			if (e != null) {
				return AuthoringToolResources.findElementDnDPanel(
						variableGroupEditor, e);
			}
		} else if (prefix.equals("textureMap") && (spec != null)) {
			Element e = world.getDescendantKeyed(spec);
			if (e != null) {
				return AuthoringToolResources.findElementDnDPanel(
						textureMapsPanel, e);
			}
		} else if (prefix.equals("sound") && (spec != null)) {
			Element e = world.getDescendantKeyed(spec);
			if (e != null) {
				return AuthoringToolResources.findElementDnDPanel(soundsPanel,
						e);
			}
		} else if (prefix.equals("misc") && (spec != null)) {
			Element e = world.getDescendantKeyed(spec);
			if (e != null) {
				return AuthoringToolResources.findElementDnDPanel(miscPanel, e);
			}
		} else if (prefix.equals("property") && (spec != null)) {
			return AuthoringToolResources.findPropertyDnDPanel(propertiesPanel,
					this.element, spec);
		} else if (prefix.equals("userDefinedResponse") && (spec != null)) {
			Response actualResponse = (Response) world.getDescendantKeyed(spec);
			if (actualResponse != null) {
				return AuthoringToolResources.findUserDefinedResponseDnDPanel(
						animationsPanel, actualResponse);
			}
		} else if (prefix.equals("userDefinedQuestion") && (spec != null)) {
			Question actualQuestion = (Question) world.getDescendantKeyed(spec);
			if (actualQuestion != null) {
				return AuthoringToolResources.findUserDefinedQuestionDnDPanel(
						questionsPanel, actualQuestion);
			}
		} else if (prefix.equals("responsePrototype") && (spec != null)) {
			try {
				Class elementClass = Class.forName(spec);
				if (elementClass != null) {
					return AuthoringToolResources.findPrototypeDnDPanel(
							animationsPanel, elementClass);
				}
			} catch (Exception e) {
				AuthoringTool.showErrorDialog(
						"Error while looking for ProtoypeDnDPanel using class "
								+ spec, e);
			}
		} else if (prefix.equals("questionPrototype") && (spec != null)) {
			try {
				Class elementClass = Class.forName(spec);
				if (elementClass != null) {
					return AuthoringToolResources.findPrototypeDnDPanel(
							questionsPanel, elementClass);
				}
			} catch (Exception e) {
				AuthoringTool.showErrorDialog(
						"Error while looking for ProtoypeDnDPanel using class "
								+ spec, e);
			}
		} else if (prefix.equals("editObjectButton") && (spec != null)) {
			Element e = world.getDescendantKeyed(spec);
			if (e != null) {
				if (e instanceof UserDefinedResponse) {
					return AuthoringToolResources.findEditObjectButton(
							animationsPanel, e);
				} else if (e instanceof UserDefinedQuestion) {
					return AuthoringToolResources.findEditObjectButton(
							questionsPanel, e);
				} else {
					return AuthoringToolResources.findEditObjectButton(
							propertiesPanel, e);
				}
			}
		}

		return null;
	}

	public Component getPropertyViewComponentForKey(String key) {
		String prefix = AuthoringToolResources.getPrefix(key);
		String spec = AuthoringToolResources.getSpecifier(key);
		World world = authoringTool.getWorld();
		if (key.equals("newVariableButton")) {
			return variableGroupEditor.getNewVariableButton();
		} else if (key.equals("newAnimationButton")) {
			return newAnimationButton;
		} else if (key.equals("newQuestionButton")) {
			return newQuestionButton;
		} else if (prefix.equals("variable") && (spec != null)) {
			Element e = world.getDescendantKeyed(spec);
			if (e != null) {
				return AuthoringToolResources.findPropertyViewController(
						variableGroupEditor, e, "value");
			}
		} else if (prefix.equals("textureMap") && (spec != null)) {
			Element e = world.getDescendantKeyed(spec);
			if (e != null) {
				return AuthoringToolResources.findElementDnDPanel(
						textureMapsPanel, e);
			}
		} else if (prefix.equals("sound") && (spec != null)) {
			Element e = world.getDescendantKeyed(spec);
			if (e != null) {
				return AuthoringToolResources.findElementDnDPanel(soundsPanel,
						e);
			}
		} else if (prefix.equals("misc") && (spec != null)) {
			Element e = world.getDescendantKeyed(spec);
			if (e != null) {
				return AuthoringToolResources.findElementDnDPanel(miscPanel, e);
			}
		} else if (prefix.equals("property") && (spec != null)) {
			return AuthoringToolResources.findPropertyViewController(
					propertiesPanel, this.element, spec);
		} else if (prefix.equals("userDefinedResponse") && (spec != null)) {
			Response actualResponse = (Response) world.getDescendantKeyed(spec);
			if (actualResponse != null) {
				return AuthoringToolResources.findUserDefinedResponseDnDPanel(
						animationsPanel, actualResponse);
			}
		} else if (prefix.equals("userDefinedQuestion") && (spec != null)) {
			Question actualQuestion = (Question) world.getDescendantKeyed(spec);
			if (actualQuestion != null) {
				return AuthoringToolResources.findUserDefinedQuestionDnDPanel(
						questionsPanel, actualQuestion);
			}
		} else if (prefix.equals("responsePrototype") && (spec != null)) {
			try {
				Class elementClass = Class.forName(spec);
				if (elementClass != null) {
					return AuthoringToolResources.findPrototypeDnDPanel(
							animationsPanel, elementClass);
				}
			} catch (Exception e) {
				AuthoringTool.showErrorDialog(
						"Error while looking for ProtoypeDnDPanel using class "
								+ spec, e);
			}
		} else if (prefix.equals("questionPrototype") && (spec != null)) {
			try {
				Class elementClass = Class.forName(spec);
				if (elementClass != null) {
					return AuthoringToolResources.findPrototypeDnDPanel(
							questionsPanel, elementClass);
				}
			} catch (Exception e) {
				AuthoringTool.showErrorDialog(
						"Error while looking for ProtoypeDnDPanel using class "
								+ spec, e);
			}
		} else if (prefix.equals("editObjectButton") && (spec != null)) {
			Element e = world.getDescendantKeyed(spec);
			if (e != null) {
				if (e instanceof UserDefinedResponse) {
					return AuthoringToolResources.findEditObjectButton(
							animationsPanel, e);
				} else if (e instanceof UserDefinedQuestion) {
					return AuthoringToolResources.findEditObjectButton(
							questionsPanel, e);
				} else {
					return AuthoringToolResources.findEditObjectButton(
							propertiesPanel, e);
				}
			}
		}

		return null;
	}

	protected void cleanPanels() {
		for (Iterator iter = panelsToClean.iterator(); iter.hasNext();) {
			JPanel panel = (JPanel) iter.next();
			Component[] children = panel.getComponents();
			for (int i = 0; i < children.length; i++) {
				if (children[i] instanceof Releasable) {
					((Releasable) children[i]).release();
				}
			}
			panel.removeAll();
		}
		panelsToClean.clear();
	}

	synchronized public void refreshGUI() {
		cleanPanels();
		propertiesPanel.removeAll();
		animationsPanel.removeAll();
		questionsPanel.removeAll();
		if (element != null) {
			constraints.gridy = 0;
			// Variable panel
			if (vars != null) {
				variableGroupEditor.setVariableObjectArrayProperty(vars);
				propertiesPanel.add(variableGroupEditor, constraints);
				constraints.gridy++;
			}

			// poses
			if (poses != null) {
				JPanel subPanel = new JPanel();
				subPanel.setBackground(Color.white);
				subPanel.setLayout(new GridBagLayout());
				subPanel.setBorder(spacingBorder);
				panelsToClean.add(subPanel);

				int count = 0;
				Object[] poseArray = poses.getArrayValue();
				for (int i = 0; i < poseArray.length; i++) {
					Pose pose = (Pose) poseArray[i];

					javax.swing.JComponent gui = GUIFactory.getGUI(pose);
					if (gui != null) {
						GridBagConstraints constraints = new GridBagConstraints(
								0, count, 1, 1, 1.0, 0.0,
								GridBagConstraints.WEST,
								GridBagConstraints.NONE,
								new Insets(2, 2, 2, 2), 0, 0);
						subPanel.add(gui, constraints);
						count++;
						if ((newlyCreatedPose == pose)
								&& (gui instanceof ElementDnDPanel)) {
							((ElementDnDPanel) gui).editName();
							newlyCreatedPose = null;
						}
					} else {
						AuthoringTool.showErrorDialog(
								"Unable to create gui for pose: " + pose, null);
					}
				}

				GridBagConstraints c = new GridBagConstraints(0, count, 1, 1,
						1.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.NONE, new Insets(4, 2, 2, 2), 0, 0);
				subPanel.add(capturePoseButton, c);

				propertiesPanel.add(subPanel, constraints);
				constraints.gridy++;
			}

			// property panels
			Vector propertyStructure = AuthoringToolResources
					.getPropertyStructure(element, false);
			if (propertyStructure != null) {
				for (Iterator iter = propertyStructure.iterator(); iter
						.hasNext();) {
					StringObjectPair sop = (StringObjectPair) iter.next();
					String groupName = sop.getString();
					Vector propertyNames = (Vector) sop.getObject();

					JPanel subPanel = new JPanel();
					JPanel toAdd = subPanel;
					subPanel.setBackground(Color.white);
					subPanel.setLayout(new GridBagLayout());
					subPanel.setBorder(spacingBorder);
					panelsToClean.add(subPanel);
					if (groupName.compareTo("seldom used properties") == 0) {
						ExpandablePanel expandPanel = new ExpandablePanel();
						expandPanel.setTitle("Seldom Used Properties");
						expandPanel.setContent(subPanel);
						expandPanel.setExpanded(false);
						toAdd = expandPanel;
					}

					if (propertyNames != null) {
						int i = 0;
						for (Iterator jter = propertyNames.iterator(); jter
								.hasNext();) {
							String name = (String) jter.next();
							final Property property = element
									.getPropertyNamed(name);
							if (property != null) {
								PopupItemFactory oneShotFactory = new PopupItemFactory() {
									public Object createItem(final Object o) {
										return new Runnable() {
											public void run() {
												if ((property.getOwner() instanceof Transformable)
														&& (property == ((Transformable) property
																.getOwner()).vehicle)) {
													((Transformable) property
															.getOwner())
															.setVehiclePreservingAbsoluteTransformation((ReferenceFrame) o);

													// Animate and undo the
													// point of view when set
												} else if (property
														.getName()
														.equals(
																"localTransformation")) {
													PointOfViewAnimation povAnim = new PointOfViewAnimation();
													povAnim.subject
															.set(property
																	.getOwner());
													povAnim.pointOfView.set(o);
													povAnim.asSeenBy
															.set(element
																	.getParent());
													PointOfViewAnimation undoResponse = new PointOfViewAnimation();
													undoResponse.subject
															.set(property
																	.getOwner());
													undoResponse.pointOfView
															.set(((Transformable) property
																	.getOwner()).localTransformation
																	.getMatrix4dValue());
													undoResponse.asSeenBy
															.set(((Transformable) property
																	.getOwner()).vehicle
																	.getValue());
													Property[] properties = new Property[] { ((Transformable) property
															.getOwner()).localTransformation };
													authoringTool
															.performOneShot(
																	povAnim,
																	undoResponse,
																	properties);

												} else {
													PropertyAnimation response = new PropertyAnimation();
													response.element
															.set(property
																	.getOwner());
													response.propertyName
															.set(property
																	.getName());
													response.value.set(o);
													PropertyAnimation undoResponse = new PropertyAnimation();
													undoResponse.element
															.set(property
																	.getOwner());
													undoResponse.propertyName
															.set(property
																	.getName());
													undoResponse.value
															.set(property
																	.getValue());
													// this is over-reaching for
													// some properties
													Vector pVector = new Vector();
													pVector.add(property);
													Element[] descendants = property
															.getOwner()
															.getDescendants();
													for (int i = 0; i < descendants.length; i++) {
														Property p = descendants[i]
																.getPropertyNamed(property
																		.getName());
														if (p != null) {
															pVector.add(p);
														}
													}
													Property[] properties = (Property[]) pVector
															.toArray(new Property[0]);
													authoringTool
															.performOneShot(
																	response,
																	undoResponse,
																	properties);
												}
											}
										};
									}
								};
								javax.swing.JComponent gui = GUIFactory
										.getPropertyGUI(property, true, false,
												oneShotFactory);
								if (gui != null) {
									GridBagConstraints constraints = new GridBagConstraints(
											0, i, 1, 1, 1.0, 0.0,
											GridBagConstraints.WEST,
											GridBagConstraints.NONE,
											new Insets(2, 2, 2, 2), 0, 0);
									subPanel.add(gui, constraints);
									i++;
								} else {
									AuthoringTool.showErrorDialog(
											"Unable to create gui for property: "
													+ property, null);
								}
							} else {
								AuthoringTool.showErrorDialog("no property on "
										+ element + " named " + name, null);
							}
						}
					}

					propertiesPanel.add(toAdd, constraints);
					constraints.gridy++;
				}
			}

			// sounds/texture/misc
			if (element instanceof Sandbox) {
				Sandbox sandbox = (Sandbox) element;
				soundsPanel.setSounds(sandbox.sounds);
				propertiesPanel.add(soundsPanel, constraints);
				constraints.gridy++;
				textureMapsPanel.setTextureMaps(sandbox.textureMaps);
				propertiesPanel.add(textureMapsPanel, constraints);
				constraints.gridy++;
				if (sandbox.misc.size() > 0) {
					miscPanel.setObjectArrayProperty(sandbox.misc);
					propertiesPanel.add(miscPanel, constraints);
					constraints.gridy++;
				}
				propertiesPanel.add(Box.createVerticalStrut(8), constraints);
				constraints.gridy++;
			}

			if (element.data.get("modeled by") != null) {
				propertiesPanel.add(new JLabel("modeled by: "
						+ element.data.get("modeled by")), constraints);
				constraints.gridy++;
			}
			if (element.data.get("painted by") != null) {
				propertiesPanel.add(new JLabel("painted by: "
						+ element.data.get("painted by")), constraints);
				constraints.gridy++;
			}
			if (element.data.get("programmed by") != null) {
				propertiesPanel.add(new JLabel("programmed by: "
						+ element.data.get("programmed by")), constraints);
				constraints.gridy++;
			}

			glueConstraints.gridy = constraints.gridy;
			propertiesPanel.add(Box.createGlue(), glueConstraints);

			constraints.gridy = 0;

			// user-defined responses
			if (responses != null) {
				JPanel subPanel = new JPanel();
				subPanel.setBackground(Color.white);
				subPanel.setLayout(new GridBagLayout());
				subPanel.setBorder(spacingBorder);
				panelsToClean.add(subPanel);

				int count = 0;
				Object[] responseArray = responses.getArrayValue();
				for (int i = 0; i < responseArray.length; i++) {
					Class responseClass = CallToUserDefinedResponse.class;
					Response response = (Response) responseArray[i];

					if (response instanceof UserDefinedResponse) {
						CallToUserDefinedResponsePrototype callToUserDefinedResponsePrototype = new CallToUserDefinedResponsePrototype(
								(UserDefinedResponse) response);
						javax.swing.JComponent gui = GUIFactory
								.getGUI(callToUserDefinedResponsePrototype);
						if (gui != null) {
							EditObjectButton editButton = GUIFactory
									.getEditObjectButton(response, gui);
							editButton
									.setToolTipText("<html><font face=arial size=-1>Open this method for editing.</font></html>");
							JPanel guiPanel = new JPanel();
							panelsToClean.add(guiPanel);
							guiPanel.setBackground(Color.white);
							guiPanel.setLayout(new GridBagLayout());
							guiPanel.add(gui, new GridBagConstraints(0, 0, 1,
									1, 0.0, 0.0, GridBagConstraints.SOUTHWEST,
									GridBagConstraints.NONE, new Insets(0, 0,
											0, 0), 0, 0));
							guiPanel.add(editButton, new GridBagConstraints(1,
									0, 1, 1, 1.0, 0.0,
									GridBagConstraints.SOUTHWEST,
									GridBagConstraints.NONE, new Insets(0, 4,
											0, 0), 0, 0));
							GridBagConstraints constraints = new GridBagConstraints(
									0, count, 1, 1, 1.0, 0.0,
									GridBagConstraints.WEST,
									GridBagConstraints.NONE, new Insets(2, 2,
											2, 2), 0, 0);
							subPanel.add(guiPanel, constraints);
							count++;
							if ((newlyCreatedAnimation == response)
									&& (gui instanceof CallToUserDefinedResponsePrototypeDnDPanel)) {
								// ((CallToUserDefinedResponsePrototypeDnDPanel)gui).editName();
								authoringTool.editObject(newlyCreatedAnimation);
								newlyCreatedAnimation = null;
							}
						} else {
							AuthoringTool
									.showErrorDialog(
											"Unable to create gui for callToUserDefinedResponsePrototype: "
													+ callToUserDefinedResponsePrototype,
											null);
						}
					} else {
						AuthoringTool.showErrorDialog(
								"Response is not a userDefinedResponse: "
										+ response, null);
					}
				}

				GridBagConstraints c = new GridBagConstraints(0, count, 1, 1,
						1.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.NONE, new Insets(4, 2, 2, 2), 0, 0);
				subPanel.add(newAnimationButton, c);

				animationsPanel.add(subPanel, constraints);
				constraints.gridy++;
			}

			// response panels
			Vector oneShotStructure = AuthoringToolResources
					.getOneShotStructure(element.getClass());
			if (oneShotStructure != null) {
				for (Iterator iter = oneShotStructure.iterator(); iter
						.hasNext();) {

					StringObjectPair sop = (StringObjectPair) iter.next();
					String groupName = sop.getString();
					Vector responseNames = (Vector) sop.getObject();
					JPanel subPanel = new JPanel();
					JPanel toAdd = subPanel;
					subPanel.setBackground(Color.white);
					subPanel.setLayout(new GridBagLayout());
					subPanel.setBorder(spacingBorder);
					panelsToClean.add(subPanel);

					if (responseNames != null) {

						int i = 0;
						for (Iterator jter = responseNames.iterator(); jter
								.hasNext();) {
							Object item = jter.next();
							if (item instanceof String) { // ignore hierarchy
															// for now
								String className = (String) item;
								try {
									if (!className
											.startsWith("edu.cmu.cs.stage3.alice.core.response.PropertyAnimation")) { // ignore
																				// property
																				// animations
																				// for
																				// now
										Class responseClass = Class
												.forName(className);
										LinkedList known = new LinkedList();
										String format = AuthoringToolResources
												.getFormat(responseClass);
										FormatTokenizer tokenizer = new FormatTokenizer(
												format);

										while (tokenizer.hasMoreTokens()) {
											String token = tokenizer
													.nextToken();
											if (token.startsWith("<<<")
													&& token.endsWith(">>>")) {
												String propertyName = token
														.substring(
																token
																		.lastIndexOf("<") + 1,
																token
																		.indexOf(">"));
												known.add(new StringObjectPair(
														propertyName, element));
											}
										}

										StringObjectPair[] knownPropertyValues = (StringObjectPair[]) known
												.toArray(new StringObjectPair[0]);

										String[] desiredProperties = AuthoringToolResources
												.getDesiredProperties(responseClass);

										ResponsePrototype responsePrototype = new ResponsePrototype(
												responseClass,
												knownPropertyValues,
												desiredProperties);

										javax.swing.JComponent gui = GUIFactory
												.getGUI(responsePrototype);

										if (gui != null) {
											GridBagConstraints constraints = new GridBagConstraints(
													0, i, 1, 1, 1.0, 0.0,
													GridBagConstraints.WEST,
													GridBagConstraints.NONE,
													new Insets(2, 2, 2, 2), 0,
													0);
											subPanel.add(gui, constraints);
											i++;

										} else {
											AuthoringTool
													.showErrorDialog(
															"Unable to create gui for responsePrototype: "
																	+ responsePrototype,
															null);
										}
									}
								} catch (ClassNotFoundException e) {
									AuthoringTool.showErrorDialog(
											"Error while looking for class "
													+ className, e);
								}
							}
						}
					}

					animationsPanel.add(toAdd, constraints);
					constraints.gridy++;
				}
			}
			glueConstraints.gridy = constraints.gridy;
			animationsPanel.add(Box.createGlue(), glueConstraints);

			// user-defined questions
			constraints.gridy = 0;
			if (questions != null) {
				JPanel subPanel = new JPanel();
				subPanel.setBackground(Color.white);
				subPanel.setLayout(new GridBagLayout());
				subPanel.setBorder(spacingBorder);
				panelsToClean.add(subPanel);

				int count = 0;
				Object[] questionsArray = questions.getArrayValue();
				for (int i = 0; i < questionsArray.length; i++) {
					Class questionClass = CallToUserDefinedQuestion.class;
					Question question = (Question) questionsArray[i];

					if (question instanceof UserDefinedQuestion) {
						CallToUserDefinedQuestionPrototype callToUserDefinedQuestionPrototype = new CallToUserDefinedQuestionPrototype(
								(UserDefinedQuestion) question);
						javax.swing.JComponent gui = GUIFactory
								.getGUI(callToUserDefinedQuestionPrototype);
						if (gui != null) {
							EditObjectButton editButton = GUIFactory
									.getEditObjectButton(question, gui);
							editButton
									.setToolTipText("<html><font face=arial size=-1>Open this question for editing.</font></html>");
							JPanel guiPanel = new JPanel();
							panelsToClean.add(guiPanel);
							guiPanel.setBackground(Color.white);
							guiPanel.setLayout(new GridBagLayout());
							guiPanel.add(gui, new GridBagConstraints(0, 0, 1,
									1, 0.0, 0.0, GridBagConstraints.SOUTHWEST,
									GridBagConstraints.NONE, new Insets(0, 0,
											0, 0), 0, 0));
							guiPanel.add(editButton, new GridBagConstraints(1,
									0, 1, 1, 1.0, 0.0,
									GridBagConstraints.SOUTHWEST,
									GridBagConstraints.NONE, new Insets(0, 4,
											0, 0), 0, 0));
							GridBagConstraints constraints = new GridBagConstraints(
									0, count, 1, 1, 1.0, 0.0,
									GridBagConstraints.WEST,
									GridBagConstraints.NONE, new Insets(2, 2,
											2, 2), 0, 0);
							subPanel.add(guiPanel, constraints);
							count++;
							if (newlyCreatedQuestion == question) {
								authoringTool.editObject(newlyCreatedQuestion);
								newlyCreatedQuestion = null;
							}
						} else {
							AuthoringTool
									.showErrorDialog(
											"Unable to create gui for callToUserDefinedQuestionPrototype: "
													+ callToUserDefinedQuestionPrototype,
											null);
						}
					} else {
						throw new RuntimeException(
								"ERROR: question is not a userDefinedQuestion");
					}
				}

				GridBagConstraints c = new GridBagConstraints(0, count, 1, 1,
						1.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.NONE, new Insets(4, 2, 2, 2), 0, 0);
				subPanel.add(newQuestionButton, c);

				questionsPanel.add(subPanel, constraints);
				constraints.gridy++;
			}

			// question panels
			Vector questionStructure = AuthoringToolResources
					.getQuestionStructure(element.getClass());
			if (questionStructure != null) {
				for (Iterator iter = questionStructure.iterator(); iter
						.hasNext();) {
					StringObjectPair sop = (StringObjectPair) iter.next();
					String groupName = sop.getString();
					Vector questionNames = (Vector) sop.getObject();

					JPanel subPanel = new JPanel();
					subPanel.setBackground(Color.white);
					subPanel.setLayout(new GridBagLayout());
					panelsToClean.add(subPanel);

					ExpandablePanel expandPanel = new ExpandablePanel();
					expandPanel.setTitle(groupName);
					expandPanel.setContent(subPanel);

					if (questionNames != null) {
						int i = 0;
						for (Iterator jter = questionNames.iterator(); jter
								.hasNext();) {
							String className = (String) jter.next();
							try {
								Class questionClass = Class.forName(className);
								Question tempQuestion = (Question) questionClass
										.newInstance();
								LinkedList known = new LinkedList();
								String format = AuthoringToolResources
										.getFormat(questionClass);
								FormatTokenizer tokenizer = new FormatTokenizer(
										format);
								while (tokenizer.hasMoreTokens()) {
									String token = tokenizer.nextToken();
									if (token.startsWith("<<<")
											&& token.endsWith(">>>")) {
										String propertyName = token.substring(
												token.lastIndexOf("<") + 1,
												token.indexOf(">"));
										known.add(new StringObjectPair(
												propertyName, element));
									}
								}
								if (PartKeyed.class
										.isAssignableFrom(questionClass)) { // special
									// case
									// hack
									known.add(new StringObjectPair("key", ""));
								}
								StringObjectPair[] knownPropertyValues = (StringObjectPair[]) known
										.toArray(new StringObjectPair[0]);

								String[] desiredProperties = AuthoringToolResources
										.getDesiredProperties(questionClass);
								if (PartKeyed.class
										.isAssignableFrom(questionClass)) { // special
									// case
									// hack
									desiredProperties = new String[0];
								}
								ElementPrototype elementPrototype = new ElementPrototype(
										questionClass, knownPropertyValues,
										desiredProperties);
								javax.swing.JComponent gui = GUIFactory
										.getGUI(elementPrototype);
								if (gui != null) {
									GridBagConstraints constraints = new GridBagConstraints(
											0, i, 1, 1, 1.0, 0.0,
											GridBagConstraints.WEST,
											GridBagConstraints.NONE,
											new Insets(2, 2, 2, 2), 0, 0);
									subPanel.add(gui, constraints);
									i++;
								} else {
									AuthoringTool.showErrorDialog(
											"Unable to create gui for elementPrototype: "
													+ elementPrototype, null);
								}
							} catch (ClassNotFoundException e) {
								AuthoringTool.showErrorDialog(
										"Unable to create gui for class: "
												+ className, e);
							} catch (IllegalAccessException e) {
								AuthoringTool.showErrorDialog(
										"Unable to create gui for class: "
												+ className, e);
							} catch (InstantiationException e) {
								AuthoringTool.showErrorDialog(
										"Unable to create gui for class: "
												+ className, e);
							}
						}
					}

					questionsPanel.add(expandPanel, constraints);
					constraints.gridy++;
				}
			}
			glueConstraints.gridy = constraints.gridy;
			questionsPanel.add(Box.createGlue(), glueConstraints);

			// other panels
			// constraints.gridy = 0;
			// if( element instanceof Sandbox ) {
			// Sandbox sandbox =
			// (Sandbox)element;
			// soundsPanel.setSounds( sandbox.sounds );
			// otherPanel.add( soundsPanel, constraints );
			// constraints.gridy++;
			// textureMapsPanel.setTextureMaps( sandbox.textureMaps );
			// otherPanel.add( textureMapsPanel, constraints );
			// constraints.gridy++;
			// miscPanel.setObjectArrayProperty( sandbox.misc );
			// otherPanel.add( miscPanel, constraints );
			// constraints.gridy++;
			// }
			// glueConstraints.gridy = constraints.gridy;
			// otherPanel.add( Box.createGlue(), glueConstraints );
		}
		revalidate();
		repaint();
	}

	public class ResponsesListener implements ObjectArrayPropertyListener {
		public void objectArrayPropertyChanging(ObjectArrayPropertyEvent ev) {
		}

		public void objectArrayPropertyChanged(ObjectArrayPropertyEvent ev) {
			if (ev.getChangeType() == ObjectArrayPropertyEvent.ITEM_INSERTED) {
				newlyCreatedAnimation = (UserDefinedResponse) ev.getItem();
			}
			int selectedIndex = tabbedPane.getSelectedIndex();
			refreshGUI();
			tabbedPane.setSelectedIndex(selectedIndex);
		}
	}

	public class QuestionsListener implements ObjectArrayPropertyListener {
		public void objectArrayPropertyChanging(ObjectArrayPropertyEvent ev) {
		}

		public void objectArrayPropertyChanged(ObjectArrayPropertyEvent ev) {
			if (ev.getChangeType() == ObjectArrayPropertyEvent.ITEM_INSERTED) {
				newlyCreatedQuestion = (UserDefinedQuestion) ev.getItem();
			}
			int selectedIndex = tabbedPane.getSelectedIndex();
			refreshGUI();
			tabbedPane.setSelectedIndex(selectedIndex);
		}
	}

	public class PosesListener implements ObjectArrayPropertyListener {
		public void objectArrayPropertyChanging(ObjectArrayPropertyEvent ev) {
		}

		public void objectArrayPropertyChanged(ObjectArrayPropertyEvent ev) {
			if (ev.getChangeType() == ObjectArrayPropertyEvent.ITEM_INSERTED) {
				newlyCreatedPose = (Pose) ev.getItem();
			}
			int selectedIndex = tabbedPane.getSelectedIndex();
			refreshGUI();
			tabbedPane.setSelectedIndex(selectedIndex);
		}
	}

	private void jbInit() {
		border1 = BorderFactory.createEmptyBorder(2, 0, 0, 0);
		border2 = BorderFactory.createLineBorder(SystemColor.controlText, 1);
		border3 = BorderFactory.createCompoundBorder(border2, border1);
		border4 = BorderFactory.createEmptyBorder(8, 8, 8, 8);
		border5 = BorderFactory.createEmptyBorder(2, 2, 2, 2);
		border6 = BorderFactory.createLineBorder(SystemColor.controlText, 1);
		border7 = BorderFactory.createCompoundBorder(border6, border5);
		border8 = BorderFactory.createEmptyBorder();
		border9 = BorderFactory.createLineBorder(Color.black, 1);
		this.setLayout(borderLayout1);
		propertySubPanel.setLayout(borderLayout2);
		propertySubPanel.setBorder(border1);
		propertySubPanel.setMinimumSize(new Dimension(0, 0));
		propertySubPanel.setOpaque(false);
		this.setBackground(new Color(204, 204, 204));
		this.setMinimumSize(new Dimension(0, 0));
		borderLayout2.setHgap(8);
		borderLayout2.setVgap(6);
		comboPanel.setLayout(gridBagLayout1);
		ownerLabel.setForeground(Color.black);
		ownerLabel.setText("owner\'s details");
		propertiesPanel.setBackground(Color.white);
		propertiesPanel.setLayout(gridBagLayout2);
		animationsPanel.setBackground(Color.white);
		animationsPanel.setLayout(gridBagLayout3);
		questionsPanel.setBackground(Color.white);
		questionsPanel.setLayout(gridBagLayout4);
		otherPanel.setBackground(Color.white);
		otherPanel.setLayout(gridBagLayout5);
		propertiesScrollPane.getViewport().setBackground(Color.white);
		propertiesScrollPane.setBorder(null);
		animationsScrollPane.getViewport().setBackground(Color.white);
		animationsScrollPane.setBorder(null);
		questionsScrollPane.getViewport().setBackground(Color.white);
		questionsScrollPane.setBorder(null);
		otherScrollPane.getViewport().setBackground(Color.white);
		otherScrollPane.setBorder(null);
		comboPanel.setOpaque(false);
		this.add(propertySubPanel, BorderLayout.CENTER);
		propertySubPanel.add(comboPanel, BorderLayout.NORTH);
		comboPanel.add(ownerLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 8, 0, 0), 0, 0));
		propertySubPanel.add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.add(propertiesScrollPane, "properties");
		propertiesScrollPane.getViewport().add(propertiesPanel, null);
		tabbedPane.add(animationsScrollPane, "methods");
		tabbedPane.add(questionsScrollPane,
				AuthoringToolResources.QUESTION_STRING + "s");
		// tabbedPane.add(otherScrollPane, "other");
		otherScrollPane.getViewport().add(otherPanel, null);
		questionsScrollPane.getViewport().add(questionsPanel, null);
		animationsScrollPane.getViewport().add(animationsPanel, null);
	}
}