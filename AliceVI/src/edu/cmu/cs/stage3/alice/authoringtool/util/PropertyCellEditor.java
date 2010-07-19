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

package edu.cmu.cs.stage3.alice.authoringtool.util;

import edu.cmu.cs.stage3.util.StringObjectPair;

/**
 * @deprecated
 */
public class PropertyCellEditor implements javax.swing.table.TableCellEditor, javax.swing.event.CellEditorListener, javax.swing.event.PopupMenuListener {
	protected EnumerableEditor enumerableEditor = new EnumerableEditor();
	protected ColorEditor colorEditor = new ColorEditor();
	protected ElementEditor elementEditor = new ElementEditor();
	protected NumberEditor numberEditor = new NumberEditor();
	protected BooleanEditor booleanEditor = new BooleanEditor();
	protected javax.swing.DefaultCellEditor stringEditor = new javax.swing.DefaultCellEditor( new javax.swing.JTextField() );
	protected DefaultEditor defaultEditor = new DefaultEditor();

	protected javax.swing.table.TableCellEditor currentEditor = null;
	protected Class currentValueClass = null;
	protected boolean isNullValid;
	protected javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();

	protected java.util.Hashtable classesToEditors = new java.util.Hashtable();
	protected edu.cmu.cs.stage3.alice.core.Element element = null;

	public PropertyCellEditor() {
		classesToEditors.put( java.awt.Color.class, colorEditor );
		classesToEditors.put( edu.cmu.cs.stage3.alice.scenegraph.Color.class, colorEditor );
		classesToEditors.put( java.lang.Boolean.class, booleanEditor );
		classesToEditors.put( edu.cmu.cs.stage3.util.Enumerable.class, enumerableEditor );
		classesToEditors.put( edu.cmu.cs.stage3.alice.core.Element.class, elementEditor );
		classesToEditors.put( Number.class, numberEditor );
		classesToEditors.put( String.class, stringEditor );
		classesToEditors.put( edu.cmu.cs.stage3.alice.core.ReferenceFrame.class, elementEditor );

		for( java.util.Enumeration enum0 = classesToEditors.elements(); enum0.hasMoreElements(); ) {
			javax.swing.table.TableCellEditor editor = (javax.swing.table.TableCellEditor)enum0.nextElement();
			editor.removeCellEditorListener( this );
			editor.addCellEditorListener( this );
		}
		defaultEditor.addCellEditorListener( this );
	}

	public edu.cmu.cs.stage3.alice.core.Element getElement() {
		return element;
	}

	public void setElement( edu.cmu.cs.stage3.alice.core.Element element ) {
		this.element = element;
	}

	public Object getCellEditorValue() {
		if( currentEditor != null ) {
			return currentEditor.getCellEditorValue();
		} else {
			return null;
		}
	}

	public boolean isCellEditable( java.util.EventObject ev ) {
		if( ev instanceof java.awt.event.MouseEvent ) {
			return ((java.awt.event.MouseEvent)ev).getClickCount() >= 1;
		}
		return true;
	}

	public boolean shouldSelectCell( java.util.EventObject anEvent ) {
		return true;
	}

	public boolean stopCellEditing() {
		if( currentEditor != null ) {
			return currentEditor.stopCellEditing();
		}
		return true;
	}

	public void cancelCellEditing() {
		if( currentEditor != null ) {
			currentEditor.cancelCellEditing();
		}
	}

	public void addCellEditorListener( javax.swing.event.CellEditorListener l ) {
		listenerList.add( javax.swing.event.CellEditorListener.class, l );
	}

	public void removeCellEditorListener( javax.swing.event.CellEditorListener l ) {
		listenerList.remove( javax.swing.event.CellEditorListener.class, l );
	}

	public java.awt.Component getTableCellEditorComponent( javax.swing.JTable table, Object value, boolean isSelected, int row, int column ) {
		Class valueClass = null;
		javax.swing.table.TableModel model = table.getModel();
		if( model instanceof edu.cmu.cs.stage3.alice.authoringtool.util.TypedTableModel ) {
			valueClass = ((edu.cmu.cs.stage3.alice.authoringtool.util.TypedTableModel)model).getTypeAt( row, column );
			isNullValid = ((edu.cmu.cs.stage3.alice.authoringtool.util.TypedTableModel)model).isNullValidAt( row, column );
		} else {
			isNullValid = true;
		}
		if( valueClass == null ) {
			if( value != null ) {
				valueClass = value.getClass();
			}
		}

		currentValueClass = valueClass;
		if( valueClass != null ) {
			currentEditor = (javax.swing.table.TableCellEditor)classesToEditors.get( valueClass );
		} else {
			valueClass = Object.class;
			currentEditor = null;
		}
		/* the following isn't perfect...
		 * if there isn't an exact match of valueClass to editor, we just find the first thing that works.
		 * no attempt is made to find the best editor available.
		 * TODO: find best editor for class
		 */
		if( currentEditor == null ) {
			for( java.util.Enumeration enum0 = classesToEditors.keys(); enum0.hasMoreElements(); ) {
				Class editorClass = (Class)enum0.nextElement();
				if( editorClass.isAssignableFrom( valueClass ) ) {
					currentEditor = (javax.swing.table.TableCellEditor)classesToEditors.get( editorClass );
					break;
				}
			}
		}
		if( currentEditor == null ) {
			currentEditor = defaultEditor;
		}

		java.awt.Component editorComponent = currentEditor.getTableCellEditorComponent( table, value, isSelected, row, column );
		return editorComponent;
	}

	public void editingStopped( javax.swing.event.ChangeEvent changeEvent ) {
		// hack for bug: 4234793
		hackPopupTimer.stop();

		Object[] listeners = listenerList.getListenerList();
		for( int i = listeners.length - 2; i >= 0; i -= 2 ) {
			if( listeners[i] == javax.swing.event.CellEditorListener.class ) {
				if( changeEvent == null ) {
					changeEvent = new javax.swing.event.ChangeEvent( this );
				}
				((javax.swing.event.CellEditorListener)listeners[i+1]).editingStopped( changeEvent );
			}
		}
	}

	public void editingCanceled( javax.swing.event.ChangeEvent changeEvent ) {
		Object[] listeners = listenerList.getListenerList();
		for( int i = listeners.length - 2; i >= 0; i -= 2 ) {
			if( listeners[i] == javax.swing.event.CellEditorListener.class ) {
				if( changeEvent == null ) {
					changeEvent = new javax.swing.event.ChangeEvent( this );
				}
				((javax.swing.event.CellEditorListener)listeners[i+1]).editingCanceled( changeEvent );
			}
		}
	}

	//////////////////////////////////////
	// PopupMenuListener interface
	//////////////////////////////////////

	public void popupMenuCanceled( javax.swing.event.PopupMenuEvent ev ) {
		// currently not working because of bug: 4234793
		if( currentEditor != null ) {
			currentEditor.cancelCellEditing();
		}
	}

	// hack for bug: 4234793
	private final javax.swing.Timer hackPopupTimer = new javax.swing.Timer( 200,
		new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent ev ) {
				if( PropertyCellEditor.this.currentEditor != null ) {
					PropertyCellEditor.this.currentEditor.cancelCellEditing();
				}
			}
		}
	);

	public void popupMenuWillBecomeInvisible( javax.swing.event.PopupMenuEvent ev ) {
		// hack for bug: 4234793
		hackPopupTimer.setRepeats( false );
		hackPopupTimer.start();
	}
	public void popupMenuWillBecomeVisible( javax.swing.event.PopupMenuEvent ev ) {
		// hack for bug: 4234793
		hackPopupTimer.stop();
	}

	/////////////////////////
	// Sub editors
	/////////////////////////

	class DefaultEditor extends javax.swing.DefaultCellEditor {
		protected Object currentObject = null;
		//protected edu.cmu.cs.stage3.alice.authoringtool.RunnableFactory objectRunnableFactory;

		public DefaultEditor() {
			super( new javax.swing.JCheckBox() );

			final javax.swing.JButton button = new javax.swing.JButton( "" );
			button.setBackground( java.awt.Color.white );
			button.setBorderPainted( false );
			button.setMargin( new java.awt.Insets( 0,0,0,0 ) );

			this.editorComponent = button;

			button.addActionListener( createActionListener() );
		}

		// the following protected methods allow subclasses to jump in at the appropriate level
		// for customized behavior, while still using the DefaultEditor's functionality where appropriate
		protected java.awt.event.ActionListener createActionListener() {
			return new java.awt.event.ActionListener() {
				public void actionPerformed( java.awt.event.ActionEvent e ) {
					javax.swing.JPopupMenu popup = createPopupMenu();
					if( popup != null ) {
						popup.show( editorComponent, 0, 0 );
						PopupMenuUtilities.ensurePopupIsOnScreen( popup );
						popup.addPopupMenuListener( PropertyCellEditor.this );
					}
				}
			};
		}

		protected javax.swing.JPopupMenu createPopupMenu() {
			java.util.Vector structure = createPopupStructure();
			if( structure != null ) {
				return PopupMenuUtilities.makePopupMenu( structure );
			} else {
				return null;
			}
		}

		protected java.util.Vector createPopupStructure() {
			java.util.Vector structure = createExpressionStructure();
			if( (structure != null) && isNullValid ) {
				if( structure.size() > 0 ) {
					structure.insertElementAt( new StringObjectPair( "Separator", javax.swing.JSeparator.class ), 0 );
				}
			}
			return structure;
		}

		protected java.util.Vector createExpressionStructure() {
			return null;
		}

		public Object getCellEditorValue() {
			return currentObject;
		}

		public java.awt.Component getTableCellEditorComponent( javax.swing.JTable table, Object value, boolean isSelected, int row, int column ) {
			currentObject = value;
			return editorComponent;
		}

		class ObjectRunnable implements Runnable {
			Object object;

			public ObjectRunnable( Object object ) {
				this.object = object;
			}

			public void run() {
				DefaultEditor.this.currentObject = object;
				fireEditingStopped();
			}
		}
	}

	class EnumerableEditor extends DefaultEditor {
		protected java.util.Vector createPopupStructure() {
			java.util.Vector structure = new java.util.Vector();
			edu.cmu.cs.stage3.util.Enumerable[] items = edu.cmu.cs.stage3.util.Enumerable.getItems( PropertyCellEditor.this.currentValueClass );

			java.util.Vector expressionStructure = createExpressionStructure();
			if( (expressionStructure != null) && (expressionStructure.size() > 0) ) {
				String className = PropertyCellEditor.this.currentValueClass.getName();
				structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Seperator", javax.swing.JSeparator.class ) );
				structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Expressions which evaluate to " + className, expressionStructure ) );
			}

			if( isNullValid ) {
				if( structure.size() > 0 ) {
					structure.insertElementAt( new StringObjectPair( "Separator", javax.swing.JSeparator.class ), 0 );
				}
			}

			return structure;
		}
	}

	class ColorEditor extends DefaultEditor {
		final javax.swing.JColorChooser colorChooser = new javax.swing.JColorChooser();
		java.awt.event.ActionListener okListener = new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				ColorEditor.this.currentObject = colorChooser.getColor();
			}
		};
		final javax.swing.JDialog dialog = javax.swing.JColorChooser.createDialog( editorComponent, "Pick a Color", true, colorChooser, okListener, null );

		Runnable customRunnable = new Runnable() {
			public void run() {
				// hack for bug: 4234793
				PropertyCellEditor.this.hackPopupTimer.stop();
				if( currentObject instanceof java.awt.Color ) {
					colorChooser.setColor( (java.awt.Color)currentObject );
				}
				dialog.show();
				fireEditingStopped();
			}
		};

		protected javax.swing.JPopupMenu createPopupMenu() {
			javax.swing.JMenu menu = new javax.swing.JMenu( "" );

			return menu.getPopupMenu();
		}

		public java.awt.Component getTableCellEditorComponent( javax.swing.JTable table, Object value, boolean isSelected, int row, int column ) {
			if( value instanceof edu.cmu.cs.stage3.alice.scenegraph.Color ) {
				edu.cmu.cs.stage3.alice.scenegraph.Color c = (edu.cmu.cs.stage3.alice.scenegraph.Color)value;
				value = new java.awt.Color( c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha() );
			}
			return super.getTableCellEditorComponent( table, value, isSelected, row, column );
		}
	}

	class ElementEditor extends DefaultEditor {
		protected java.util.Vector createPopupStructure() {
			return null;
		}
	}

	class NumberEditor extends javax.swing.DefaultCellEditor {
		protected Object currentNumber = null;
		protected javax.swing.JPanel panel = new javax.swing.JPanel();
		protected javax.swing.JTextField textField = new javax.swing.JTextField();
		protected javax.swing.JButton button = new javax.swing.JButton( "Element..." );

		public NumberEditor() {
			super( new javax.swing.JCheckBox() );

			panel.setLayout( new java.awt.BorderLayout() );
			panel.add( textField, java.awt.BorderLayout.CENTER );
			panel.add( button, java.awt.BorderLayout.EAST );

			this.editorComponent = panel;

			textField.addActionListener(
				new java.awt.event.ActionListener() {
					public void actionPerformed( java.awt.event.ActionEvent e ) {
						String input = textField.getText();
						try {
							Double value = Double.valueOf( input );
							NumberEditor.this.currentNumber = value;
						} catch( NumberFormatException ex ) {
							//TODO: load from Element name
							if( NumberEditor.this.currentNumber != null ) {
								textField.setText( currentNumber.toString() );
							} else {
								textField.setText( "" );
							}
						}
						NumberEditor.this.fireEditingStopped();
					}
				}
			);

			button.addActionListener(
				new java.awt.event.ActionListener() {
					public void actionPerformed( java.awt.event.ActionEvent e ) {
						java.util.Vector structure = null;
						if( structure != null ) {
							javax.swing.JMenu menu = PopupMenuUtilities.makeMenu( "", structure );
							if( menu != null ) {
								javax.swing.JPopupMenu popup = menu.getPopupMenu();
								popup.show( button, 0, 0 );
								PopupMenuUtilities.ensurePopupIsOnScreen( popup );
								popup.addPopupMenuListener( PropertyCellEditor.this );
							}
						}
					}
				}
			);
		}

		public Object getCellEditorValue() {
			return currentNumber;
		}

		public java.awt.Component getTableCellEditorComponent( javax.swing.JTable table, Object value, boolean isSelected, int row, int column ) {
			currentNumber = value;

			if( currentNumber instanceof Number ) {
				textField.setText( currentNumber.toString() );
			} else {
				textField.setText( "" );
			}

			edu.cmu.cs.stage3.alice.core.Element[] elements = element.getRoot().search( new edu.cmu.cs.stage3.alice.core.criterion.ExpressionIsAssignableToCriterion( Number.class ) );
			if( elements.length > 0 ) {
				button.setEnabled( true );
			} else {
				button.setEnabled( false );
			}

			return editorComponent;
		}

		class NumberExpressionRunnable implements Runnable {
			edu.cmu.cs.stage3.alice.core.Expression expression;

			public NumberExpressionRunnable( edu.cmu.cs.stage3.alice.core.Expression expression ) {
				this.expression = expression;
			}

			public void run() {
				if( Number.class.isAssignableFrom( expression.getValueClass() ) ) {
					NumberEditor.this.currentNumber = expression;
				}
				NumberEditor.this.fireEditingStopped();
			}
		}
	}

	class BooleanEditor extends DefaultEditor {
		protected java.util.Vector createPopupStructure() {
			java.util.Vector structure = new java.util.Vector();

			java.util.Vector expressionStructure = createExpressionStructure();
			if( (expressionStructure != null) && (expressionStructure.size() > 0) ) {
				structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Seperator", javax.swing.JSeparator.class ) );
				structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Expressions which evaluate to Boolean", expressionStructure ) );
			}

			return structure;
		}
	}
}