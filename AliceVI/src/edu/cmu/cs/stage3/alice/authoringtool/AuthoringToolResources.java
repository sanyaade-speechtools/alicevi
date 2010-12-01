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


import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.ImageIcon;

import org.python.core.Py;
import org.python.core.PySystemState;
import org.python.core.__builtin__;

import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer;
import edu.cmu.cs.stage3.alice.core.Behavior;
import edu.cmu.cs.stage3.alice.core.Billboard;
import edu.cmu.cs.stage3.alice.core.Camera;
import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.Dummy;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Expression;
import edu.cmu.cs.stage3.alice.core.Group;
import edu.cmu.cs.stage3.alice.core.Light;
import edu.cmu.cs.stage3.alice.core.Model;
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
import edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera;
import edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray;
import edu.cmu.cs.stage3.alice.core.light.AmbientLight;
import edu.cmu.cs.stage3.alice.core.light.DirectionalLight;
import edu.cmu.cs.stage3.alice.core.light.PointLight;
import edu.cmu.cs.stage3.alice.core.property.DictionaryProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;
import edu.cmu.cs.stage3.alice.core.question.PropertyValue;
import edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion;
import edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment;
import edu.cmu.cs.stage3.alice.core.reference.PropertyReference;
import edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse;
import edu.cmu.cs.stage3.alice.core.response.DirectionAmountTransformAnimation;
import edu.cmu.cs.stage3.alice.core.response.PoseAnimation;
import edu.cmu.cs.stage3.alice.core.response.PropertyAnimation;
import edu.cmu.cs.stage3.alice.core.response.ResizeAnimation;
import edu.cmu.cs.stage3.alice.core.response.SayAnimation;
import edu.cmu.cs.stage3.alice.core.response.SoundResponse;
import edu.cmu.cs.stage3.alice.core.response.ThinkAnimation;
import edu.cmu.cs.stage3.alice.core.response.TransformAnimation;
import edu.cmu.cs.stage3.alice.core.response.TransformResponse;
import edu.cmu.cs.stage3.alice.core.response.Wait;
import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.util.StringObjectPair;
import edu.cmu.cs.stage3.util.StringTypePair;

/**
 * @author Jason Pratt
 */
public class AuthoringToolResources {
	public final static long startTime = System.currentTimeMillis();
	public final static String QUESTION_STRING = "function";
	public static edu.cmu.cs.stage3.util.Criterion characterCriterion = new edu.cmu.cs.stage3.util.Criterion() {
		public boolean accept( Object o ) {
			return o instanceof Sandbox;
		}
	};
	public static FileFilter resourceFileFilter = new java.io.FileFilter() {
		public boolean accept( java.io.File file ) {
			return file.isFile() && file.canRead() && file.getName().toLowerCase().endsWith( ".py" );
		}
	};

	// preferences
	protected static Configuration authoringToolConfig = Configuration.getLocalConfiguration( AuthoringTool.class.getPackage() );

	@SuppressWarnings("serial")
	public static class Resources implements java.io.Serializable {
		public Vector<?> propertyStructure;
		public Vector<?> oneShotStructure;
		public Vector<?> questionStructure;
		public Vector<?> worldTreeChildrenPropertiesStructure;
		public Vector<?> behaviorParameterPropertiesStructure;
		public HashMap<Object, String> nameMap = new HashMap<Object, String>();
		public HashMap<Object, String> htmlNameMap = new HashMap<Object, String>();
		public HashMap<Object, String> formatMap = new HashMap<Object, String>();
		public HashMap<String, HashMap<?, ?>> propertyValueFormatMap = new HashMap<String, HashMap<?, ?>>();
		public HashMap<String, String> unitMap = new HashMap<String, String>();
		public Class[] classesToOmitNoneFor;
		public StringTypePair[] propertiesToOmitNoneFor;
		public StringTypePair[] propertiesToIncludeNoneFor;
		public StringTypePair[] propertyNamesToOmit;
		public StringTypePair[] propertiesToOmitScriptDefinedFor;
		public Vector<?> defaultPropertyValuesStructure;
		public edu.cmu.cs.stage3.util.StringTypePair[] defaultVariableTypes;
		public String[] defaultAspectRatios;
		public Class[] behaviorClasses;
		public String[] parameterizedPropertiesToOmit;
		public String[] responsePropertiesToOmit;
		public String[] oneShotGroupsToInclude;
		public String[] questionPropertiesToOmit;
		public HashMap<String, Color> colorMap = new HashMap<String, Color>();
		public java.text.DecimalFormat decimalFormatter = new java.text.DecimalFormat( "#0.##" );
		public HashMap<String, Image> stringImageMap = new HashMap<String, Image>();
		public HashMap<String, ImageIcon> stringIconMap = new HashMap<String, ImageIcon>();
		public HashMap<ImageIcon, ImageIcon> disabledIconMap = new HashMap<ImageIcon, ImageIcon>();
		public Class[] importers;
		public Class[] editors;
		public HashMap<Class<?>, DataFlavor> flavorMap = new HashMap<Class<?>, DataFlavor>();
		public HashMap<Integer, String> keyCodesToStrings = new HashMap<Integer, String>();
		public boolean experimentalFeaturesEnabled;
		public HashMap<Object, Object> miscMap = new HashMap<Object, Object>();
		public URL mainWebGalleryURL = null;
		public File mainDiskGalleryDirectory = null;
		public File mainCDGalleryDirectory = null;
		}
	protected static Resources resources;
	
	

	protected static File resourcesDirectory;
	protected static File resourcesCacheFile;
	protected static File resourcesPyFile;
	
	
	//filter the content of the resource directory 
	protected static FilenameFilter pyFilenameFilter = new FilenameFilter() {//used to filter the file list
		public boolean accept( File dir, String name ) {//returns true iff the file name ends with '.pu'. it basically retrieve
			return name.toLowerCase().endsWith( ".py" );//python files from the given directory 
		}
	};
	
	
	static {
		//get the full bath of the 'resource' file which contains all the style files
		resourcesDirectory = new File( JAlice.getAliceHomeDirectory(), "resources" ).getAbsoluteFile();
		
		//get the chache file full bath. its location i sresoucesCache.bin
		resourcesCacheFile = new File( resourcesDirectory, "resourcesCache.bin" ).getAbsoluteFile();
		
		//get the properties retrieved from python files. if not found, load properties from 'Alice Style.py'
		resourcesPyFile = new File( resourcesDirectory, authoringToolConfig.getValue( "resourceFile" ) ).getAbsoluteFile();
		if (!resourcesPyFile.canRead()){//if the resource file cannot be read, load 'Alice Style.py' as the default style
			resourcesPyFile = new File(resourcesDirectory, "Alice Style.py").getAbsoluteFile();
		}
		
		//initialize Python stuff , URL, and key-board mapping
		loadResourcesPy();
	}

	
	
	/////////////////////////////////////////// DataFlavorSupport
	/**
	 * ???
	 * @param dtde
	 * @param flavor
	 * @return
	 */
	public static boolean safeIsDataFlavorSupported(DropTargetDragEvent dtde, DataFlavor flavor){
		try{
			boolean toReturn = dtde.isDataFlavorSupported(flavor);
			return toReturn;
		} catch (Throwable t){
			return false;
		}
	}
	
	/**
	 * ???????
	 * @param dtde
	 * @return
	 */
	public static DataFlavor[] safeGetCurrentDataFlavors(DropTargetDropEvent dtde){
		try{
			return dtde.getCurrentDataFlavors();
		} catch (Throwable t){
			return null;
		}
	}
	
	/**
	 * ????
	 * @param dtde
	 * @return
	 */
	public static DataFlavor[] safeGetCurrentDataFlavors(DropTargetDragEvent dtde){
		try{
			return dtde.getCurrentDataFlavors();
		} catch (Throwable t){
			return null;
		}
	}
	
	/**
	 * ??
	 * @param dtde
	 * @param flavor
	 * @return
	 */
	public static boolean safeIsDataFlavorSupported(DropTargetDropEvent dtde, DataFlavor flavor){
		try{
			boolean toReturn = dtde.isDataFlavorSupported(flavor);
			return toReturn;
		} catch (Throwable t){
			return false;
		}
	}
	
	/**
	 * ???
	 * @param transferable
	 * @param flavor
	 * @return
	 */
	public static boolean safeIsDataFlavorSupported(Transferable transferable, DataFlavor flavor){
		try{
			boolean toReturn = transferable.isDataFlavorSupported(flavor);
			return toReturn;
		} catch (Throwable t){
			return false;
		}
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	

	/**
	 * returns true if the resources are cached
	 */
	public static boolean isResourcesCacheCurrent() {
		long cacheTime = resourcesCacheFile.exists() ? resourcesCacheFile.lastModified() : 0L;
		long mostCurrentPy = getMostCurrentPyTime( resourcesDirectory, 0L );

		return (cacheTime > mostCurrentPy);
	}

	/**
	 * get time of the last modified file in the given directory (TBV)
	 * @param directory
	 * @param mostCurrentPy
	 * @return
	 */
	private static long getMostCurrentPyTime( File directory, long mostCurrentPy ) {
		java.io.File[] files = directory.listFiles();
		for( int i = 0; i < files.length; i++ ) {
			if( pyFilenameFilter.accept( directory, files[i].getName() ) ) {
				mostCurrentPy = Math.max( mostCurrentPy, files[i].lastModified() );
			} else if( files[i].isDirectory() ) {
				mostCurrentPy = Math.max( mostCurrentPy, getMostCurrentPyTime( files[i], mostCurrentPy ) );
			}
		}
		return mostCurrentPy;
	}

	/**
	 * loads all properties from Python files located in the 'resources' folder into the local variables.
	 * It calls ResourceTransfer.py file which calls the static methods in AuthoringToolResources class 
	 * to load the properties
	 * 
	 * intializes the key-event  String mapping. 
	 * i.e Key-event_0_VI is mapped to "0" <-- human readable
	 * 
	 * tries to find alice gallery URL from specific file and initializes the URL variable
	 */
	public static void loadResourcesPy() {
		resources = new Resources();
		PySystemState.initialize();
		PySystemState pySystemState = Py.getSystemState();
		__builtin__.execfile( resourcesPyFile.getAbsolutePath(), pySystemState.builtins, pySystemState.builtins );
		
		AuthoringToolResources.initKeyCodesToStrings();//maps key event int value with its String value. 
		initWebGalleryURL();
	}
	
	
	/**
	 * get cached information from 'resourcesCache.bin' file
	 * @throws Exception
	 */
	public static void loadResourcesCache() throws Exception {
		ObjectInputStream ois = new ObjectInputStream( new BufferedInputStream( new FileInputStream( resourcesCacheFile ) ) );
		resources = (Resources)ois.readObject();
		ois.close();
	}

	/**
	 * save the resources back to the cache file 'resourcesCache.bin'
	 */
	public static void saveResourcesCache() {
		try {
			ObjectOutputStream oos = new ObjectOutputStream( new BufferedOutputStream( new FileOutputStream( resourcesCacheFile ) ) );
			oos.writeObject( resources );
			oos.flush();
			oos.close();
		} catch( Throwable t ) {
			AuthoringTool.showErrorDialog( "Unable to save resources cache to " + resourcesCacheFile.getAbsolutePath(), t );
		}
	}

	/**
	 * clear the cache file 'resourcesCache.bin'
	 */
	public static void deleteResourcesCache() {
		try {
			resourcesCacheFile.delete();
		} catch( Throwable t ) {
			AuthoringTool.showErrorDialog( "Unable to delete resources cache " + resourcesCacheFile.getAbsolutePath(), t );
		}
	}

	/**
	 * Iterate through the given list to make sure that all the properties are of type StringObjectPair. 
	 * Each string-object pair should be used to create a valid object. 
	 * @param propertyStructure
	 */
	public static void setPropertyStructure( Vector<?> propertyStructure ) {
		if( propertyStructure != null ) {
			for( Iterator<?> iter = propertyStructure.iterator(); iter.hasNext(); ) {
				Object o = iter.next();
				if( o instanceof StringObjectPair ) {
					String className = ((StringObjectPair)o).getString();
					try {
						Class.forName( className );
					} catch( java.lang.ClassNotFoundException e ) {
						throw new IllegalArgumentException( "propertyStructure error: " + className + " is not a Class" );
					}
				} else {
					throw new IllegalArgumentException( "Unexpected object found in propertyStructure: " + o );
				}
			}
		}

		AuthoringToolResources.resources.propertyStructure = propertyStructure;
	}
	
	
	
	/**
	 * return a vector containing  the properties structure for the given class .
	 * @param elementClass
	 * @return
	 */
	public static Vector<StringObjectPair> getPropertyStructure( Class<?> elementClass ) {
		if( AuthoringToolResources.resources.propertyStructure != null ) {
			for( Iterator<?> iter = AuthoringToolResources.resources.propertyStructure.iterator(); iter.hasNext(); ) {
				Object o = iter.next();
				if( o instanceof StringObjectPair ) {
					String className = ((StringObjectPair)o).getString();
					try {
						Class<?> c = Class.forName( className );
						if( c.isAssignableFrom( elementClass ) ) {
							return (Vector)((StringObjectPair)o).getObject();
						}
					} catch( java.lang.ClassNotFoundException e ) {
						AuthoringTool.showErrorDialog( "Can't find class " + className, e );
					}
				} else {
					AuthoringTool.showErrorDialog( "Unexpected object found in propertyStructure: " + o, null );
				}
			}
		}
		return null;
	}

	/**
	 * return a vector containing  the properties structure for the given class (overloaded method).
	 * @param element
	 * @param includeLeftovers. if true, include the leftover.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Vector<StringObjectPair> getPropertyStructure( Element element, boolean includeLeftovers ) {
		Vector<StringObjectPair> structure = getPropertyStructure( element.getClass() );

		if( includeLeftovers && (structure != null) ) {
			Vector<Property> usedProperties = new Vector<Property>();
			for( Iterator<StringObjectPair> iter = structure.iterator(); iter.hasNext(); ) {
				StringObjectPair sop = iter.next();
				Vector<String> propertyNames = (Vector<String>) sop.getObject();
				if( propertyNames != null ) {
					for( Iterator<String> jter = propertyNames.iterator(); jter.hasNext(); ) {
						String name = jter.next();
						Property property = element.getPropertyNamed( name );
						if( property != null ) {
							usedProperties.add( property );
						}
					}
				}
			}

			Vector<String> leftovers = new Vector<String>();
			Property[] properties = element.getProperties();
			for( int i = 0; i < properties.length; i++ ) {
				if( ! usedProperties.contains( properties[i] ) ) {
					leftovers.add( properties[i].getName() );
				}
			}

			if( leftovers.size() > 0 ) {
				structure.add( new StringObjectPair( "leftovers", leftovers ) );
			}
		}

		return structure;
	}


	/**
	 * make sure that all the object in 'oneShoutStructure' are valid and sets oneShotStructure property
	 * @param oneShotStructure 
	 */
	public static void setOneShotStructure( Vector<Object> oneShotStructure ) {
		// validate structure 
		if( oneShotStructure != null ) {
			for( Iterator<Object> iter = oneShotStructure.iterator(); iter.hasNext(); ) {
				
				Object classChunk = iter.next();
				
				if( classChunk instanceof StringObjectPair ) {
					String className = ((StringObjectPair)classChunk).getString();
					Object groups = ((StringObjectPair)classChunk).getObject();

						if( groups instanceof Vector<?> ) {
							for( Iterator<Object> jter = ((Vector)groups).iterator(); jter.hasNext(); ) {
								Object groupChunk = jter.next();
								if( groupChunk instanceof StringObjectPair ) {
									Object responseClasses = ((StringObjectPair)groupChunk).getObject();
									if( responseClasses instanceof Vector ) {
										for( Iterator<Object> kter = ((Vector)responseClasses).iterator(); kter.hasNext(); ) {
											Object className2 = kter.next();
											if( (className2 instanceof String) || (className2 instanceof StringObjectPair) ) {
												// do nothing
											} else {
												throw new IllegalArgumentException( "oneShotStructure error: expected String or StringObjectPair, got: " + className );
											}
										}
									}
								} else {
									throw new IllegalArgumentException( "Unexpected object found in oneShotStructure: " + groupChunk );
								}
							}
						} else {
							throw new IllegalArgumentException( "oneShotStructure error: expected Vector, got: " + groups );
						}
				} else {
					throw new IllegalArgumentException( "Unexpected object found in oneShotStructure: " + classChunk );
				}
			
			}
		}

		AuthoringToolResources.resources.oneShotStructure = oneShotStructure;
	}

	/**
	 * get the structure of the passed element
	 * @param elementClass 
	 * @return structure as a vector
	 */
	public static Vector<Object> getOneShotStructure( Class elementClass ) {
		if( AuthoringToolResources.resources.oneShotStructure != null ) {
			for( Iterator iter = AuthoringToolResources.resources.oneShotStructure.iterator(); iter.hasNext(); ) {
				Object o = iter.next();
				if( o instanceof StringObjectPair ) {
					String className = ((StringObjectPair)o).getString();
					try {
						Class c = Class.forName( className );
						if( c.isAssignableFrom( elementClass ) ) {
							return (Vector<Object>)((StringObjectPair)o).getObject();
						}
					} catch( ClassNotFoundException e ) {
						AuthoringTool.showErrorDialog( "Can't find class " + className, e );
					}
				} else {
					AuthoringTool.showErrorDialog( "Unexpected object found in oneShotStructure: " + o, null );
				}
			}
		}

		return null;
	}

	
	public static void setQuestionStructure( Vector questionStructure ) {
		// validate structure
		if( questionStructure != null ) {
			for( Iterator iter = questionStructure.iterator(); iter.hasNext(); ) {
				Object classChunk = iter.next();
				if( classChunk instanceof StringObjectPair ) {
					String className = ((StringObjectPair)classChunk).getString();
					Object groups = ((StringObjectPair)classChunk).getObject();
//					try {
//						Class c = Class.forName( className );
						if( groups instanceof Vector ) {
							for( Iterator jter = ((Vector)groups).iterator(); jter.hasNext(); ) {
								Object groupChunk = jter.next();
								if( groupChunk instanceof StringObjectPair ) {
									Object questionClasses = ((StringObjectPair)groupChunk).getObject();
									if( questionClasses instanceof Vector ) {
										for( Iterator kter = ((Vector)questionClasses).iterator(); kter.hasNext(); ) {
											Object className2 = kter.next();
											if( className2 instanceof String ) {
												try {
													Class.forName( (String)className2 );
												} catch( ClassNotFoundException e ) {
													throw new IllegalArgumentException( "questionStructure error: " + className2 + " is not a Class" );
												}
											} else {
												throw new IllegalArgumentException( "questionStructure error: expected String, got: " + className );
											}
										}
									}
								} else {
									throw new IllegalArgumentException( "Unexpected object found in questionStructure: " + groupChunk );
								}
							}
						} else {
							throw new IllegalArgumentException( "questionStructure error: expected Vector, got: " + groups );
						}
//					} catch( java.lang.ClassNotFoundException e ) {
//						throw new IllegalArgumentException( "questionStructure error: " + className + " is not a Class" );
//					}
				} else {
					throw new IllegalArgumentException( "Unexpected object found in questionStructure: " + classChunk );
				}
			}
		}

		AuthoringToolResources.resources.questionStructure = questionStructure;
	}

	public static Vector getQuestionStructure( Class elementClass ) {
		if( AuthoringToolResources.resources.questionStructure != null ) {
			for( Iterator iter = AuthoringToolResources.resources.questionStructure.iterator(); iter.hasNext(); ) {
				Object o = iter.next();
				if( o instanceof edu.cmu.cs.stage3.util.StringObjectPair ) {
					String className = ((edu.cmu.cs.stage3.util.StringObjectPair)o).getString();
					try {
						Class c = Class.forName( className );
						if( c.isAssignableFrom( elementClass ) ) {
							return (Vector)((edu.cmu.cs.stage3.util.StringObjectPair)o).getObject();
						}
					} catch( java.lang.ClassNotFoundException e ) {
						AuthoringTool.showErrorDialog( "Can't find class " + className, e );
					}
				} else {
					AuthoringTool.showErrorDialog( "Unexpected object found in questionStructure: " + o, null );
				}
			}
		}

		return null;
	}

	/**
	 * check if all the objects in the given victor are valid properties and then set the default properties
	 * @param defaultPropertyValuesStructure
	 */
	public static void setDefaultPropertyValuesStructure( Vector defaultPropertyValuesStructure ) {
		// validate structure
		if( defaultPropertyValuesStructure != null ) {
			for( Iterator iter = defaultPropertyValuesStructure.iterator(); iter.hasNext(); ) {
				Object classChunk = iter.next();
				if( classChunk instanceof StringObjectPair ) {
					Object properties = ((StringObjectPair)classChunk).getObject();

						if( properties instanceof Vector ) {
							for( Iterator jter = ((Vector)properties).iterator(); jter.hasNext(); ) {
								Object propertyChunk = jter.next();
								if( propertyChunk instanceof StringObjectPair ) {
									Object values = ((StringObjectPair)propertyChunk).getObject();
									if( ! (values instanceof Vector) ) {
										throw new IllegalArgumentException( "defaultPropertyValuesStructure error: expected Vector, got: " + values );
									}
								} else {
									throw new IllegalArgumentException( "defaultPropertyValuesStructure error: expected StringObjectPair, got: " + propertyChunk );
								}
							}
						} else {
							throw new IllegalArgumentException( "defaultPropertyValuesStructure error: expected Vector, got: " + properties );
						}

				} else {
					throw new IllegalArgumentException( "defaultPropertyValuesStructure error: expected StringObjectPair, got: " + classChunk );
				}
			}
		}

		AuthoringToolResources.resources.defaultPropertyValuesStructure = defaultPropertyValuesStructure;
	}

	/**
	 * get a specific property set from a specific class
	 * @param elementClass   class containing the property set
	 * @param propertyName   the required property name
	 * @return
	 */
	public static Vector getDefaultPropertyValues( Class elementClass, String propertyName ) {
		if( AuthoringToolResources.resources.defaultPropertyValuesStructure != null ) {
			for( Iterator iter = AuthoringToolResources.resources.defaultPropertyValuesStructure.iterator(); iter.hasNext(); ) {
				StringObjectPair classChunk = (StringObjectPair)iter.next();
				String className = classChunk.getString();
				try {
					Class c = Class.forName( className );
					if( c.isAssignableFrom( elementClass ) ) {
						Vector properties = (Vector)classChunk.getObject();
						for( Iterator jter = properties.iterator(); jter.hasNext(); ) {
							StringObjectPair propertyChunk = (StringObjectPair)jter.next();
							if( propertyName.equals( propertyChunk.getString() ) ) {
								return (Vector)propertyChunk.getObject();
							}
						}
					}
				} catch( java.lang.ClassNotFoundException e ) {
					AuthoringTool.showErrorDialog( "Can't find class " + className, e );
				}
			}
		}

		return null;
	}
	

	public static void putName( Object key, String prettyName ) {
		AuthoringToolResources.resources.nameMap.put( key, prettyName );
	}

	public static String getName( Object key ) {
		return (String)AuthoringToolResources.resources.nameMap.get( key );
	}

	public static boolean nameMapContainsKey( Object key ) {
		return AuthoringToolResources.resources.nameMap.containsKey( key );
	}
	
	public static void putHTMLName( Object key, String prettyName ) {
		AuthoringToolResources.resources.htmlNameMap.put( key, prettyName );
	}

	public static String getHTMLName( Object key ) {
		return (String)AuthoringToolResources.resources.htmlNameMap.get( key );
	}

	public static boolean htmlNameMapContainsKey( Object key ) {
		return AuthoringToolResources.resources.htmlNameMap.containsKey( key );
	}

	public static void putFormat( Object key, String formatString ) {
		AuthoringToolResources.resources.formatMap.put( key, formatString );
	}

	public static String getFormat( Object key ) {
		return (String)AuthoringToolResources.resources.formatMap.get( key );
	}

	/**
	 * return a plain decoded format of the value.
	 * @param key
	 * @return   decoded string. For example '&lt;' is decoded to '<' character 
	 */
	public static String getPlainFormat( Object key ) {
		String format = (String)AuthoringToolResources.resources.formatMap.get( key );
		StringBuffer sb = new StringBuffer();
		FormatTokenizer tokenizer = new FormatTokenizer( format );
		while( tokenizer.hasMoreTokens() ) {
			String token = tokenizer.nextToken();
			if( (! token.startsWith( "<<" )) || token.startsWith( "<<<" ) ) {
				while( token.indexOf( "&lt;" ) > -1 ) {
					token = new StringBuffer( token ).replace( token.indexOf( "&lt;" ), token.indexOf( "&lt;" ) + 4, "<" ).toString();
				}
				sb.append( token );
			}
		}
		return sb.toString();
	}

	public static boolean formatMapContainsKey( Object key ) {
		return AuthoringToolResources.resources.formatMap.containsKey( key );
	}

	public static void putPropertyValueFormatMap( String propertyKey, HashMap valueReprMap ) {
		AuthoringToolResources.resources.propertyValueFormatMap.put( propertyKey, valueReprMap );
	}

	public static HashMap getPropertyValueFormatMap( String propertyKey ) {
		return (HashMap)AuthoringToolResources.resources.propertyValueFormatMap.get( propertyKey );
	}

	public static boolean propertyValueFormatMapContainsKey( String propertyKey ) {
		return AuthoringToolResources.resources.propertyValueFormatMap.containsKey( propertyKey );
	}

	public static void putUnitString( String key, String unitString ) {
		AuthoringToolResources.resources.unitMap.put( key, unitString );
	}

	public static String getUnitString( String key ) {
		return (String)AuthoringToolResources.resources.unitMap.get( key );
	}

	public static boolean unitMapContainsKey( String key ) {
		return AuthoringToolResources.resources.unitMap.containsKey( key );
	}

	public static Set getUnitMapKeySet() {
		return AuthoringToolResources.resources.unitMap.keySet();
	}

	public static Collection getUnitMapValues() {
		return AuthoringToolResources.resources.unitMap.values();
	}

	public static void setClassesToOmitNoneFor( Class[] classesToOmitNoneFor ) {
		AuthoringToolResources.resources.classesToOmitNoneFor = classesToOmitNoneFor;
	}


	public static void setPropertiesToOmitNoneFor( edu.cmu.cs.stage3.util.StringTypePair[] propertiesToOmitNoneFor ) {
		AuthoringToolResources.resources.propertiesToOmitNoneFor = propertiesToOmitNoneFor;
	}
	
	public static void setPropertiesToIncludeNoneFor( edu.cmu.cs.stage3.util.StringTypePair[] propertiesToIncludeNoneFor ) {
			AuthoringToolResources.resources.propertiesToIncludeNoneFor = propertiesToIncludeNoneFor;
		}

	public static boolean shouldGUIOmitNone( Property property ) {
		return !shouldGUIIncludeNone(property);
	}
	
	public static boolean shouldGUIIncludeNone( Property property ) {
		if( AuthoringToolResources.resources.propertiesToIncludeNoneFor != null ) {
			Class elementClass = property.getOwner().getClass();
			String propertyName = property.getName();
			for( int i = 0; i < AuthoringToolResources.resources.propertiesToIncludeNoneFor.length; i++ ) {
				if( AuthoringToolResources.resources.propertiesToIncludeNoneFor[i].getType().isAssignableFrom( elementClass ) && AuthoringToolResources.resources.propertiesToIncludeNoneFor[i].getString().equals( propertyName ) ) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean shouldGUIIncludeNone( Class elementClass, String propertyName ) {
		if( AuthoringToolResources.resources.propertiesToIncludeNoneFor != null ) {
			for( int i = 0; i < AuthoringToolResources.resources.propertiesToIncludeNoneFor.length; i++ ) {
				if( AuthoringToolResources.resources.propertiesToIncludeNoneFor[i].getType().isAssignableFrom( elementClass ) && AuthoringToolResources.resources.propertiesToIncludeNoneFor[i].getString().equals( propertyName ) ) {
					return true;
				}
			}
		}
		return false;
	}

	public static void setPropertyNamesToOmit( StringTypePair[] propertyNamesToOmit ) {
		AuthoringToolResources.resources.propertyNamesToOmit = propertyNamesToOmit;
	}

	public static boolean shouldGUIOmitPropertyName( Property property ) {
		if( AuthoringToolResources.resources.propertyNamesToOmit != null ) {
			Class elementClass = property.getOwner().getClass();
			String propertyName = property.getName();
			for( int i = 0; i < AuthoringToolResources.resources.propertyNamesToOmit.length; i++ ) {
				if( AuthoringToolResources.resources.propertyNamesToOmit[i].getType().isAssignableFrom( elementClass ) && AuthoringToolResources.resources.propertyNamesToOmit[i].getString().equals( propertyName ) ) {
					return true;
				}
			}
		}
		return false;
	}

	public static void setPropertiesToOmitScriptDefinedFor( StringTypePair[] propertiesToOmitScriptDefinedFor ) {
		AuthoringToolResources.resources.propertiesToOmitScriptDefinedFor = propertiesToOmitScriptDefinedFor;
	}

	public static boolean shouldGUIOmitScriptDefined( Property property ) {
		if( ! authoringToolConfig.getValue( "enableScripting" ).equalsIgnoreCase( "true" ) ) {
			return true;
		} else if( AuthoringToolResources.resources.propertiesToOmitScriptDefinedFor != null ) {
			Class elementClass = property.getOwner().getClass();
			String propertyName = property.getName();
			for( int i = 0; i < AuthoringToolResources.resources.propertiesToOmitScriptDefinedFor.length; i++ ) {
				if( AuthoringToolResources.resources.propertiesToOmitScriptDefinedFor[i].getType().isAssignableFrom( elementClass ) && AuthoringToolResources.resources.propertiesToOmitScriptDefinedFor[i].getString().equals( propertyName ) ) {
					return true;
				}
			}
		}
		return false;
	}

	//get repr in the context of a property
	public static String getReprForValue( Object value, Property property ) {
		return getReprForValue( value, property, null );
	}
	public static String getReprForValue( Object value, Class elementClass, String propertyName ) {
		return getReprForValue( value, elementClass, propertyName, null );
	}
	public static String getReprForValue( Object value, Property property, Object extraContextInfo ) {
		Class elementClass = property.getOwner().getClass();
		String propertyName = property.getName();
		if( (property.getOwner() instanceof PropertyAnimation) && property.getName().equals( "value" ) ) {
			PropertyAnimation propertyAnimation = (PropertyAnimation)property.getOwner();
			Object e = propertyAnimation.element.get();
			if( e instanceof Expression ) {
				elementClass = ((Expression)e).getValueClass();
			} else {
				Object elementValue = propertyAnimation.element.getElementValue();
				if( elementValue != null ) {
					elementClass = elementValue.getClass();
				} else {
					elementClass = null;
				}
			}
			propertyName = propertyAnimation.propertyName.getStringValue();
		} else if( (property.getOwner() instanceof PropertyAssignment) && property.getName().equals( "value" ) ) {
			PropertyAssignment propertyAssignment = (PropertyAssignment)property.getOwner();
			elementClass = propertyAssignment.element.getElementValue().getClass();
			propertyName = propertyAssignment.propertyName.getStringValue();
		}
		return getReprForValue( value, elementClass, propertyName, extraContextInfo );
	}
	
	
	
	public static String getReprForValue( Object value, Class elementClass, String propertyName, Object extraContextInfo ) {
		boolean verbose = false;
		Class valueClass = null;
		try {
			valueClass = Element.getValueClassForPropertyNamed( elementClass, propertyName );
		} catch( Exception e ) { // a bit hackish
			valueClass = Object.class;
		}
		if( valueClass == null ) { // another hack
			valueClass = Object.class;
		}
		if( (elementClass == null) || (propertyName == null) ) {
			return getReprForValue( value );
		}

		if( (CallToUserDefinedResponse.class.isAssignableFrom( elementClass ) && propertyName.equals( "userDefinedResponse" )) ||
			(CallToUserDefinedQuestion.class.isAssignableFrom( elementClass ) && propertyName.equals( "userDefinedQuestion" )) )
		{
			verbose = true;
		}
		if( (value instanceof Variable) && (((Variable)value).getParent() instanceof Sandbox) ) {
			verbose = true;
		}

		try {
			while( Element.class.isAssignableFrom( elementClass ) ) {
				String propertyKey = elementClass.getName() + "." + propertyName;

				String userRepr = null;
				if( (extraContextInfo != null) && extraContextInfo.equals( "menuContext" ) ) { // if the repr is going to be shown in a menu
					if( propertyValueFormatMapContainsKey( propertyKey + ".menuContext" ) ) {
						propertyKey = propertyKey + ".menuContext";
					}
				} else if( extraContextInfo instanceof DictionaryProperty ) { // if there is extra info stored in the element's data property
					DictionaryProperty data = (DictionaryProperty)extraContextInfo;
					if( data.getName().equals( "data" ) ) {  // sanity check
						Object repr = data.get( "edu.cmu.cs.stage3.alice.authoringtool.userRepr." + propertyName );
						if( repr != null ) {
							if( repr instanceof String ) {
								if( Number.class.isAssignableFrom( valueClass ) && (value instanceof Double) ) { // if it's a number, check to make sure the string is still valid
									Double d = AuthoringToolResources.parseDouble( (String)repr );
									if( (d != null) && d.equals( value ) ) {
										userRepr = (String)repr;
									} else {
										data.remove( "edu.cmu.cs.stage3.alice.authoringtool.userRepr." + propertyName );
									}
								} else {
									userRepr = (String)repr;
								}
							}
						}
					}
				}

				String reprString = null;
				if( propertyValueFormatMapContainsKey( propertyKey ) ) {
					HashMap map = getPropertyValueFormatMap( propertyKey );
					if( map.containsKey( value ) ) {
						reprString = (String)map.get( value );
					} else if( value == null ) { // is this right for all cases?
						reprString = null;
					} else if( map.containsKey( "default" ) ) {
						reprString = (String)map.get( "default" );
					}
				}

				if( reprString != null ) {
					for( Iterator iter = AuthoringToolResources.resources.unitMap.keySet().iterator(); iter.hasNext(); ) {
						String key = (String)iter.next();
						String unitString = getUnitString( key );
						String unitExpression = "<" + key + ">";
						while( reprString.indexOf( unitExpression ) > -1 ) {
							StringBuffer sb = new StringBuffer( reprString );
							sb.replace( reprString.indexOf( unitExpression ), reprString.indexOf( unitExpression ) + unitExpression.length(), unitString );
							reprString = sb.toString();
						}
					}

					while( reprString.indexOf( "<value>" ) > -1 ) {
						String valueString = (userRepr != null) ? userRepr : getReprForValue( value );
						StringBuffer sb = new StringBuffer( reprString );
						sb.replace( reprString.indexOf( "<value>" ), reprString.indexOf( "<value>" ) + "<value>".length(), valueString );
						reprString = sb.toString();
					}
					while( (reprString.indexOf( "<percentValue>" ) > -1) && (value instanceof Double) ) {
						double v = ((Double)value).doubleValue() * 100.0;
						String valueString = AuthoringToolResources.resources.decimalFormatter.format( v ) + "%";
						StringBuffer sb = new StringBuffer( reprString );
						sb.replace( reprString.indexOf( "<percentValue>" ), reprString.indexOf( "<percentValue>" ) + "<percentValue>".length(), valueString );
						reprString = sb.toString();
					}
					while( (reprString.indexOf( "<keyCodeValue>" ) > -1) && (value instanceof Integer) ) {
						String valueString = java.awt.event.KeyEvent.getKeyText( ((Integer)value).intValue() );
						StringBuffer sb = new StringBuffer( reprString );
						sb.replace( reprString.indexOf( "<keyCodeValue>" ), reprString.indexOf( "<keyCodeValue>" ) + "<keyCodeValue>".length(), valueString );
						reprString = sb.toString();
					}

					return reprString;
				}

				elementClass = elementClass.getSuperclass();
			}
		} catch( Throwable t ) {
			AuthoringTool.showErrorDialog( "Error finding repr for " + value, t );
		}
		return getReprForValue( value, verbose );
	}
	

	public static String getReprForValue( Object value ) {
		return getReprForValue( value, false );
	}

	/**
	 * Searches for 'Alice/etc/AliceWebBalleryURL.txt' file and 
	 * tries to parse and retrieve alice url from that file
	 */
	protected static void initWebGalleryURL(){
		URL galleryURL = null;
		try {
			galleryURL = new URL("http://www.alice.org/gallery/");
			File urlFile = new File( JAlice.getAliceHomeDirectory(), "etc/AliceWebGalleryURL.txt" ).getAbsoluteFile();
			if( urlFile.exists() ) {
				if( urlFile.canRead() ) {//if the URL file exists and can be read
					BufferedReader br = new BufferedReader( new FileReader( urlFile ) );
					String urlString = null;
					while (true){
						urlString = br.readLine();
						if (urlString == null){
							break;
						} else if (urlString.length() > 0 && urlString.charAt(0) != '#'){
							break;
						}
					}//end while
					br.close();
					
					if( urlString != null ) {
						urlString = urlString.trim();
						if( urlString.length() > 0 ) {
							try{
								galleryURL = new URL( urlString );
							} 
							catch (java.net.MalformedURLException badURL){
								if (urlString.startsWith("www")){
									urlString = "http://"+urlString;
									try{
										galleryURL = new URL( urlString );
									} 
									catch (MalformedURLException badURLAgain){}
								}
							}
								
						} 
					}//end if
					
				} 
			} 
		}//end try 
		catch( Throwable t ) {}
		finally{
			if (galleryURL != null){
				setMainWebGalleryURL(galleryURL);
			}
		}//end finally
	}//end method
	
	protected static String stripUnnamedsFromName(Object value){
		String toStrip = new String(value.toString());
		String toReturn = "";
		String toMatch = "__Unnamed";
		boolean notDone = true;
		while (notDone){
			int nextIndex = toStrip.indexOf(toMatch);
			if (nextIndex >= 0){
				String toAdd = toStrip.substring(0, nextIndex);
				if (toAdd != null){
					toReturn += toAdd;
				}
				String newToStrip = toStrip.substring(nextIndex, toStrip.length());
				if (newToStrip != null){
					toStrip = newToStrip;
				}
				else{
					notDone = false;
					break;
				}
				nextIndex = toStrip.indexOf(".");
				if (nextIndex >= 0){
					newToStrip = toStrip.substring(nextIndex+1, toStrip.length());
					if (newToStrip != null){
						toStrip = newToStrip;
					}
					else{
						notDone = false;
						break;
					}
				}else{
					notDone = false;
					break;
				}
			}
			else{
				toReturn += toStrip;
				notDone = false;
				break;
			}
		}
		return toStrip;
	}

	public static String getReprForValue( Object value, boolean verbose ) {
		if( nameMapContainsKey( value ) ) {
			value = getName( value );
		}
		if( formatMapContainsKey( value ) ) {
			value = getPlainFormat( value );
		}
		if( value instanceof Class ) {
			value = ((Class)value).getName();
			if( nameMapContainsKey( value ) ) {
				value = getName( value );
			}
		}
		if( value instanceof edu.cmu.cs.stage3.util.Enumerable ) {
			value = ((edu.cmu.cs.stage3.util.Enumerable)value).getRepr();
		}
		if( value instanceof PropertyValue ) {
			String propertyName = ((PropertyValue)value).propertyName.getStringValue();
			Element element = (Element)((PropertyValue)value).element.get();
			Class valueClass = element.getClass();
			if( element instanceof Expression ) {
				valueClass = ((Expression)element).getValueClass();
			}
			try {
				Class declaringClass = valueClass.getField( propertyName ).getDeclaringClass();
				if( declaringClass != null ) {
					String key = declaringClass.getName() + "." + propertyName;
					if( nameMapContainsKey( key ) ) {
						propertyName = getName( key );
					}
				}
			} catch( NoSuchFieldException e ) {
				AuthoringTool.showErrorDialog( "Error representing PropertyValue: can't find " + propertyName + " on " + valueClass, e );
			}

			value = getReprForValue( element, false ) + "." + propertyName;
		}
		if( (value instanceof Question) && formatMapContainsKey( value.getClass() ) ) {
			String questionRepr = "";
			Question question = (Question)value;
			String format = getFormat( value.getClass() );
			FormatTokenizer formatTokenizer = new FormatTokenizer( format );
//			int i = 0;
			while( formatTokenizer.hasMoreTokens() ) {
				String token = formatTokenizer.nextToken();
				if( token.startsWith( "<" ) && token.endsWith( ">" ) ) {
					Property property = question.getPropertyNamed( token.substring( token.lastIndexOf( "<" ) + 1, token.indexOf( ">" ) ) );
					if( property != null ) {
						questionRepr += getReprForValue( property.get(), property );
					}
				} else {
					while( token.indexOf( "&lt;" ) > -1 ) {
						token = new StringBuffer( token ).replace( token.indexOf( "&lt;" ), token.indexOf( "&lt;" ) + 4, "<" ).toString();
					}
					questionRepr += token;
				}
			}

			if( questionRepr.length() > 0 ) {
				value = questionRepr;
			}
		}
		if( value instanceof Element ) {
			if( verbose ) {
				Element ancestor = ((Element)value).getSandbox();
				if( ancestor != null ) {
					ancestor = ancestor.getParent();
				}
				value = ((Element)value).getKey( ancestor );
				value = stripUnnamedsFromName(value);
			} else {
				value = ((Element)value).name.getStringValue();
			}
		}
		if( value instanceof Number ) {
			double d = ((Number)value).doubleValue();
			value = AuthoringToolResources.resources.decimalFormatter.format( d );
		}
		if( value instanceof javax.vecmath.Vector3d ) {
			javax.vecmath.Vector3d vec = (javax.vecmath.Vector3d)value;
			value = "Vector3( " + AuthoringToolResources.resources.decimalFormatter.format( vec.x ) + ", " + AuthoringToolResources.resources.decimalFormatter.format( vec.y ) + ", " + AuthoringToolResources.resources.decimalFormatter.format( vec.z ) + " )";
		}
		if( value instanceof javax.vecmath.Matrix4d ) {
			edu.cmu.cs.stage3.math.Matrix44 m = new edu.cmu.cs.stage3.math.Matrix44( (javax.vecmath.Matrix4d)value );
			edu.cmu.cs.stage3.math.Vector3 position = m.getPosition();
			edu.cmu.cs.stage3.math.Quaternion quaternion = m.getAxes().getQuaternion();
			value = "position: " + AuthoringToolResources.resources.decimalFormatter.format( position.x ) + ", " + AuthoringToolResources.resources.decimalFormatter.format( position.y ) + ", " + AuthoringToolResources.resources.decimalFormatter.format( position.z ) + ";  " +
					"orientation: (" + AuthoringToolResources.resources.decimalFormatter.format( quaternion.x ) + ", " + AuthoringToolResources.resources.decimalFormatter.format( quaternion.y ) + ", " + AuthoringToolResources.resources.decimalFormatter.format( quaternion.z ) + ") " + AuthoringToolResources.resources.decimalFormatter.format( quaternion.w );
		}
		if( value instanceof edu.cmu.cs.stage3.math.Quaternion ) {
			edu.cmu.cs.stage3.math.Quaternion quaternion = (edu.cmu.cs.stage3.math.Quaternion)value;
			value = "(" + AuthoringToolResources.resources.decimalFormatter.format( quaternion.x ) + ", " + AuthoringToolResources.resources.decimalFormatter.format( quaternion.y ) + ", " + AuthoringToolResources.resources.decimalFormatter.format( quaternion.z ) + ") " + AuthoringToolResources.resources.decimalFormatter.format( quaternion.w );
		}
		if( value instanceof edu.cmu.cs.stage3.alice.scenegraph.Color ) {
			edu.cmu.cs.stage3.alice.scenegraph.Color color = (edu.cmu.cs.stage3.alice.scenegraph.Color)value;
			if( color.equals( edu.cmu.cs.stage3.alice.scenegraph.Color.BLACK ) ) {
				value = "black";
			} else if( color.equals( edu.cmu.cs.stage3.alice.scenegraph.Color.BLUE ) ) {
				value = "blue";
			} else if( color.equals( edu.cmu.cs.stage3.alice.scenegraph.Color.BROWN ) ) {
				value = "brown";
			} else if( color.equals( edu.cmu.cs.stage3.alice.scenegraph.Color.CYAN ) ) {
				value = "cyan";
			} else if( color.equals( edu.cmu.cs.stage3.alice.scenegraph.Color.DARK_GRAY ) ) {
				value = "dark gray";
			} else if( color.equals( edu.cmu.cs.stage3.alice.scenegraph.Color.GRAY ) ) {
				value = "gray";
			} else if( color.equals( edu.cmu.cs.stage3.alice.scenegraph.Color.GREEN ) ) {
				value = "green";
			} else if( color.equals( edu.cmu.cs.stage3.alice.scenegraph.Color.LIGHT_GRAY ) ) {
				value = "light gray";
			} else if( color.equals( edu.cmu.cs.stage3.alice.scenegraph.Color.MAGENTA ) ) {
				value = "magenta";
			} else if( color.equals( edu.cmu.cs.stage3.alice.scenegraph.Color.ORANGE ) ) {
				value = "orange";
			} else if( color.equals( edu.cmu.cs.stage3.alice.scenegraph.Color.PINK ) ) {
				value = "pink";
			} else if( color.equals( edu.cmu.cs.stage3.alice.scenegraph.Color.PURPLE ) ) {
				value = "purple";
			} else if( color.equals( edu.cmu.cs.stage3.alice.scenegraph.Color.RED ) ) {
				value = "red";
			} else if( color.equals( edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE ) ) {
				value = "white";
			} else if( color.equals( edu.cmu.cs.stage3.alice.scenegraph.Color.YELLOW ) ) {
				value = "yellow";
			} else {
				value = "Color(r:" + AuthoringToolResources.resources.decimalFormatter.format( color.getRed() ) + ", g:" + AuthoringToolResources.resources.decimalFormatter.format( color.getGreen() ) + ", b:" + AuthoringToolResources.resources.decimalFormatter.format( color.getBlue() ) + ", a:" + AuthoringToolResources.resources.decimalFormatter.format( color.getAlpha() ) + ")";
			}
		}
		if( value instanceof Property ) {
			String simpleName = ((Property)value).getName();
			if( ((Property)value).getDeclaredClass() != null ) {
				String key = ((Property)value).getDeclaredClass().getName() + "." + ((Property)value).getName();
				if( nameMapContainsKey( key ) ) {
					simpleName = getName( key );
				} else {
					simpleName = ((Property)value).getName();
				}
			}

			if( ((Property)value).getOwner() instanceof Variable ) {
				value = getReprForValue( ((Property)value).getOwner(), verbose );
			} else if( verbose && (((Property)value).getOwner() != null) ) {
				value = getReprForValue( ((Property)value).getOwner() ) + "." + simpleName;
			} else {
				value = simpleName;
			}
		}
		if( value == null ) {
			value = "<None>";
		}

		return value.toString();
	}

	public static String getFormattedReprForValue( Object value, edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues ) {
		String format = (String)AuthoringToolResources.resources.formatMap.get( value );
		StringBuffer sb = new StringBuffer();
		edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer tokenizer = new edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer( format );
		while( tokenizer.hasMoreTokens() ) {
			String token = tokenizer.nextToken();
			if( token.startsWith( "<<<" ) && token.endsWith( ">>>" ) ) {
				String propertyName = token.substring( token.lastIndexOf( "<" ) + 1, token.indexOf( ">" ) );
				for( int i = 0; i < knownPropertyValues.length; i++ ) {
					if( knownPropertyValues[i].getString().equals( propertyName ) ) {
						sb.append( AuthoringToolResources.getReprForValue( knownPropertyValues[i].getObject(), true ) );
						break;
					}
				}
			} else if( token.startsWith( "<<" ) && token.endsWith( ">>" ) ) {
				// leave blank
			} else if( token.startsWith( "<" ) && token.endsWith( ">" ) ) {
				String propertyName = token.substring( token.lastIndexOf( "<" ) + 1, token.indexOf( ">" ) );
				boolean appendedValue = false;
				for( int i = 0; i < knownPropertyValues.length; i++ ) {
					if( knownPropertyValues[i].getString().equals( propertyName ) ) {
						sb.append( AuthoringToolResources.getReprForValue( knownPropertyValues[i].getObject(), true ) );
						appendedValue = true;
						break;
					}
				}
				if( ! appendedValue ) {
					sb.append( token );
				}
			} else {
				sb.append( token );
			}
		}
		return sb.toString();
	}

	public static String getNameInContext( Element element, Element context ) {
		//DEBUG System.out.println( "element: " + element );
		//DEBUG System.out.println( "context: " + context );
		if( element instanceof Variable ) {
			if( element.getParent() != null ) {
				Element variableRoot = element.getParent();
				//DEBUG System.out.println( "variableRoot: " + variableRoot );
				if( (variableRoot instanceof Response) && (context.isDescendantOf( variableRoot ) || (context == variableRoot)) ) {
					return element.name.getStringValue();
				}
			}
		} else if( (element instanceof Sound) && (context instanceof SoundResponse) ) {
			Sound sound = (Sound)element;
			double t = Double.NaN;
			edu.cmu.cs.stage3.media.DataSource dataSourceValue = sound.dataSource.getDataSourceValue();
			if( dataSourceValue != null ) {
				t = dataSourceValue.getDuration( edu.cmu.cs.stage3.media.DataSource.USE_HINT_IF_NECESSARY );
//				t = dataSourceValue.waitForDuration( 100 );
//				if( Double.isNaN( t ) ) {
//					t = dataSourceValue.getDurationHint();
//				}
			}
			return getReprForValue( element, true ) + " (" + formatTime( t ) + ")";
		}

		return getReprForValue( element, true );
	}

	public static void setDefaultVariableTypes( edu.cmu.cs.stage3.util.StringTypePair[] defaultVariableTypes ) {
		AuthoringToolResources.resources.defaultVariableTypes = defaultVariableTypes;
	}

	public static edu.cmu.cs.stage3.util.StringTypePair[] getDefaultVariableTypes() {
		return AuthoringToolResources.resources.defaultVariableTypes;
	}

	public static void setDefaultAspectRatios( String[] defaultAspectRatios ) {
		AuthoringToolResources.resources.defaultAspectRatios = defaultAspectRatios;
	}

	public static String[] getDefaultAspectRatios() {
		return AuthoringToolResources.resources.defaultAspectRatios;
	}

	public static String[] getInitialVisibleProperties( Class elementClass ) {
		LinkedList<String> visible = new LinkedList<String>();
		String format = AuthoringToolResources.getFormat( elementClass );
		FormatTokenizer tokenizer = new FormatTokenizer( format );
		while( tokenizer.hasMoreTokens() ) {
			String token = tokenizer.nextToken();
			if( token.startsWith( "<<<" ) && token.endsWith( ">>>" ) ) {
				visible.add( token.substring( token.lastIndexOf( "<" ) + 1, token.indexOf( ">" ) ) );
			} else if( token.startsWith( "<<" ) && token.endsWith( ">>" ) ) {
				visible.add( token.substring( token.lastIndexOf( "<" ) + 1, token.indexOf( ">" ) ) );
			} else if( token.startsWith( "<" ) && token.endsWith( ">" ) ) {
				visible.add( token.substring( token.lastIndexOf( "<" ) + 1, token.indexOf( ">" ) ) );
			}
		}

		return (String[])visible.toArray( new String[0] );
	}

	public static String[] getDesiredProperties( Class elementClass ) {
		LinkedList desired = new LinkedList();
		String format = AuthoringToolResources.getFormat( elementClass );
		edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer tokenizer = new edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer( format );
		while( tokenizer.hasMoreTokens() ) {
			String token = tokenizer.nextToken();
			if( token.startsWith( "<<<" ) && token.endsWith( ">>>" ) ) {
				// skip this one
				// should be in knownPropertyValues
			} else if( token.startsWith( "<<" ) && token.endsWith( ">>" ) ) {
				desired.add( token.substring( token.lastIndexOf( "<" ) + 1, token.indexOf( ">" ) ) );
			} else if( token.startsWith( "<" ) && token.endsWith( ">" ) ) {
				desired.add( token.substring( token.lastIndexOf( "<" ) + 1, token.indexOf( ">" ) ) );
			}
		}

		return (String[])desired.toArray( new String[0] );
	}

	public static void setBehaviorClasses( Class[] behaviorClasses ) {
		AuthoringToolResources.resources.behaviorClasses = behaviorClasses;
	}

	public static Class[] getBehaviorClasses() {
		return AuthoringToolResources.resources.behaviorClasses;
	}

	public static void setParameterizedPropertiesToOmit( String[] parameterizedPropertiesToOmit ) {
		AuthoringToolResources.resources.parameterizedPropertiesToOmit = parameterizedPropertiesToOmit;
	}

	public static String[] getParameterizedPropertiesToOmit() {
		return AuthoringToolResources.resources.parameterizedPropertiesToOmit;
	}
	
	public static void setOneShotGroupsToInclude(String[] oneShotGroupsToInclude){
		AuthoringToolResources.resources.oneShotGroupsToInclude = oneShotGroupsToInclude;
	}
	
	public static String[] getOneShotGroupsToInclude(){
		return AuthoringToolResources.resources.oneShotGroupsToInclude;
	}


	public static void setBehaviorParameterPropertiesStructure( Vector behaviorParameterPropertiesStructure ) {
		AuthoringToolResources.resources.behaviorParameterPropertiesStructure = behaviorParameterPropertiesStructure;
	}

	public static String[] getBehaviorParameterProperties( Class behaviorClass ) {
		if( AuthoringToolResources.resources.behaviorParameterPropertiesStructure != null ) {
			for( Iterator iter = AuthoringToolResources.resources.behaviorParameterPropertiesStructure.iterator(); iter.hasNext(); ) {
				Object o = iter.next();
				if( o instanceof edu.cmu.cs.stage3.util.StringObjectPair ) {
					String className = ((edu.cmu.cs.stage3.util.StringObjectPair)o).getString();
					try {
						Class c = Class.forName( className );
						if( c.isAssignableFrom( behaviorClass ) ) {
							return (String[])((edu.cmu.cs.stage3.util.StringObjectPair)o).getObject();
						}
					} catch( java.lang.ClassNotFoundException e ) {
						AuthoringTool.showErrorDialog( "Can't find class " + className, e );
					}
				} else {
					AuthoringTool.showErrorDialog( "Unexpected object found in behaviorParameterPropertiesStructure: " + o, null );
				}
			}
		}

		return null;
	}
	
	public static void putColor( String key, java.awt.Color color ) {
		putColor( key, new Color(color) );
	}
	
	public static void putColor( String key, Color color ) {
		AuthoringToolResources.resources.colorMap.put( key, color );
	}

	private static float[] rgbToHSL( Color rgb ) {
		//float[] rgbF = rgb.getRGBColorComponents(null);
		float[] rgbF = {rgb.getRed() ,rgb.getGreen() , rgb.getBlue() };
		float[] hsl = new float[3];
		float min = Math.min(rgbF[0], Math.min(rgbF[1], rgbF[2]));
		float max = Math.max(rgbF[0], Math.max(rgbF[1], rgbF[2]));
		float delta = max - min;
		
		hsl[2] = (max + min) /2;
		
		if (delta == 0){
			hsl[0] = 0.0f;
			hsl[1] = 0.0f;
		} else{
			if (hsl[2] < 0.5){
				hsl[1] = delta/(max + min);
//				System.out.println("B: min: "+min+", max: "+max+", delta: "+delta+", H: "+hsl[0]+", S: "+hsl[1]+", L: "+hsl[2]);
			} else{
				hsl[1] = delta/(2 - max - min);
//				System.out.println("A: min: "+min+", max: "+max+", delta: "+delta+", H: "+hsl[0]+", S: "+hsl[1]+", L: "+hsl[2]);
			}
			float delR = (( ( max - rgbF[0]) / 6 ) + (delta / 2)) / delta;
			float delG = (( ( max - rgbF[1]) / 6 ) + (delta / 2)) / delta;
			float delB = (( ( max - rgbF[2]) / 6 ) + (delta / 2)) / delta;
			if (rgbF[0] == max){
				hsl[0] = delB - delG;
			} else if (rgbF[1] == max){
				hsl[0] = (1.0f/3) + delR  - delB;
			} else if (rgbF[2] == max){
				hsl[0] = (2.0f/3) + delG - delR;
			}
			
			if (hsl[0] < 0){
				hsl[0] += 1;
			}
			if (hsl[0] > 1){
				hsl[0] -= 1;
			}
		}
//		System.out.println("For RGB: "+rgb+" HSL = "+hsl[0]+", "+hsl[1]+", "+hsl[2]);
		return hsl;
	}
		
	private static float hueToRGB( float v1, float v2, float vH){
		if ( vH < 0 ) vH += 1;
		if ( vH > 1 ) vH -= 1;
	 	if ( ( 6 * vH ) < 1 ) return ( v1 + ( v2 - v1 ) * 6 * vH );
	 	if ( ( 2 * vH ) < 1 ) return ( v2 );
	 	if ( ( 3 * vH ) < 2 ) return ( v1 + ( v2 - v1 ) * ( ( 2.0f / 3 ) - vH ) * 6 );
	 	return v1; 
	}
	
	private static Color hslToRGB( float[] hsl ) {
		Color rgb = new Color(0,0,0);
		if (hsl[1] == 0){
//			System.out.println("For HSL: "+hsl[0]+", "+hsl[1]+", "+hsl[2]+" RGB = "+hsl[2]+", "+hsl[2]+", "+hsl[2]);
			return new Color(hsl[2],hsl[2],hsl[2]);
		} else{
			float var_2 = 0.0f;
			if ( hsl[2] < 0.5 ){
				var_2 = hsl[2] * ( 1 + hsl[1] );
			} else{
				var_2 = ( hsl[2] + hsl[1] ) - ( hsl[1] * hsl[2] );
			}
			float var_1 = 2 * hsl[2] - var_2;
			float R = Math.min(1.0f, hueToRGB( var_1, var_2, hsl[0] + ( 1.0f / 3 ) ));
			float G = Math.min(1.0f, hueToRGB( var_1, var_2, hsl[0] ));
			float B = Math.min(1.0f, hueToRGB( var_1, var_2, hsl[0] - ( 1.0f / 3 ) ));
//			System.out.println("For HSL: "+hsl[0]+", "+hsl[1]+", "+hsl[2]+" RGB = "+R+", "+G+", "+B);
			return new Color(R,G,B);
		}
	}

	public static Color getColor( String key ) {
		Color toReturn = new Color (AuthoringToolResources.resources.colorMap.get( key ));
		try{
		if( toReturn == null ){
			throw new Exception( key );
		}
		}catch( Exception e ){
			System.err.println( key );
			e.printStackTrace();
		}
		
		if (authoringToolConfig.getValue( "enableHighContrastMode" ).equalsIgnoreCase( "true" ) && 
			!key.equalsIgnoreCase("mainFontColor") &&
			!key.equalsIgnoreCase("objectTreeDisabledText") &&
			!key.equalsIgnoreCase("objectTreeSelectedText") &&
			!key.equalsIgnoreCase("disabledHTMLText") &&
			!key.equalsIgnoreCase("disabledHTML") &&
			!key.equalsIgnoreCase("stdErrTextColor") &&
			!key.equalsIgnoreCase("commentForeground") &&
			!key.equalsIgnoreCase("objectTreeSelected") &&
			!key.equalsIgnoreCase("dndHighlight") &&
			!key.equalsIgnoreCase("dndHighlight2") &&
			!key.equalsIgnoreCase("dndHighlight3") &&
			!key.equalsIgnoreCase("guiEffectsShadow") &&
			!key.equalsIgnoreCase("guiEffectsEdge") &&
			!key.equalsIgnoreCase("guiEffectsTroughShadow") &&
			!key.equalsIgnoreCase("guiEffectsDisabledLine") &&
			!key.equalsIgnoreCase("makeSceneEditorBigBackground") &&
			!key.equalsIgnoreCase("makeSceneEditorSmallBackground") &&
			!key.equalsIgnoreCase("objectTreeText")){
				float[] hsl = rgbToHSL(toReturn);
				hsl[2] = Math.max(hsl[2], .95f);
				// converts from AWT color to edu.cmu.cs.stage3.alice.scenegraph.Color
				Color convertedColor = new Color(hslToRGB(hsl));
				/*there is no need to create a new color because the color object has already been created
				 * new Color(convertedColor.getRed(), convertedColor.getGreen(), convertedColor.getBlue(), toReturn.getAlpha());
				 */
				return convertedColor;
		} else{
			return toReturn;
		}
	}

	public static void setMainWebGalleryURL( java.net.URL url ) {
		AuthoringToolResources.resources.mainWebGalleryURL = url;
	}

	public static java.net.URL getMainWebGalleryURL() {
		return AuthoringToolResources.resources.mainWebGalleryURL;
	}

	public static void setMainDiskGalleryDirectory( java.io.File file ) {
		AuthoringToolResources.resources.mainDiskGalleryDirectory = file;
	}

	public static java.io.File getMainDiskGalleryDirectory() {
		return AuthoringToolResources.resources.mainDiskGalleryDirectory;
	}

	public static void setMainCDGalleryDirectory( java.io.File file ) {
		AuthoringToolResources.resources.mainCDGalleryDirectory = file;
	}

	public static java.io.File getMainCDGalleryDirectory() {
		return AuthoringToolResources.resources.mainCDGalleryDirectory;
	}

	public static void autodetectMainCDGalleryDirectory( String galleryName ) {
		java.io.File[] cdRoots = edu.cmu.cs.stage3.alice.authoringtool.util.CDUtil.getCDRoots();

		for( int i = 0; i < cdRoots.length; i++ ) {
			if( cdRoots[i].exists() && cdRoots[i].canRead() ) {
				java.io.File potentialDir = new java.io.File( cdRoots[i], galleryName );
				if( potentialDir.exists() && potentialDir.canRead() ) {
					setMainCDGalleryDirectory( potentialDir );
					break;
				}
			}
		}
	}

	public static java.awt.Image getAliceSystemIconImage() {
		return getImageForString( "aliceHead" );
	}
	public static javax.swing.ImageIcon getAliceSystemIcon() {
		return getIconForString( "aliceHead" );
	}
	

	public static Image getImageForString( String s ) {
		if( ! AuthoringToolResources.resources.stringImageMap.containsKey( s ) ) {
			java.net.URL resource = AuthoringToolResources.class.getResource( "images/" + s + ".gif" );
			if( resource == null ) {
				resource = AuthoringToolResources.class.getResource( "images/" + s + ".png" );
			}
			if( resource == null ) {
				resource = AuthoringToolResources.class.getResource( "images/" + s + ".jpg" );
			}
			if( resource != null ) {
				java.awt.Image image = java.awt.Toolkit.getDefaultToolkit().getImage( resource );
				AuthoringToolResources.resources.stringImageMap.put( s, image );
			} else {
				return null;
			}
		}

		return (java.awt.Image)AuthoringToolResources.resources.stringImageMap.get( s );
	}

	public static javax.swing.ImageIcon getIconForString( String s ) {
		if( ! AuthoringToolResources.resources.stringIconMap.containsKey( s ) ) {
			java.net.URL resource = AuthoringToolResources.class.getResource( "images/" + s + ".gif" );
			if( resource == null ) {
				resource = AuthoringToolResources.class.getResource( "images/" + s + ".png" );
			}
			if( resource == null ) {
				resource = AuthoringToolResources.class.getResource( "images/" + s + ".jpg" );
			}
			if( resource != null ) {
				AuthoringToolResources.resources.stringIconMap.put( s, new javax.swing.ImageIcon( resource ) );
			} else {
				return null;
			}
		}

		return (javax.swing.ImageIcon)AuthoringToolResources.resources.stringIconMap.get( s );
	}

	static final javax.swing.ImageIcon cameraIcon = getIconForString( "camera" );
	static final javax.swing.ImageIcon ambientLightIcon = getIconForString( "ambientLight" );
	static final javax.swing.ImageIcon directionalLightIcon = getIconForString( "directionalLight" );
	static final javax.swing.ImageIcon pointLightIcon = getIconForString( "pointLight" );
	static final javax.swing.ImageIcon defaultLightIcon = getIconForString( "pointLight" );
	static final javax.swing.ImageIcon modelIcon = getIconForString( "model" );
	static final javax.swing.ImageIcon subpartIcon = getIconForString( "subpart" );
	static final javax.swing.ImageIcon sceneIcon = getIconForString( "scene" );
	static final javax.swing.ImageIcon folderIcon = getIconForString( "folder" );
	static final javax.swing.ImageIcon defaultIcon = getIconForString( "default" );
	
	public static javax.swing.ImageIcon getIconForValue( Object value ) {
		if( value instanceof Camera ) { //TODO: perspective and orthographic
			return cameraIcon;
		} else if( value instanceof AmbientLight ) {
			return ambientLightIcon;
		} else if( value instanceof DirectionalLight ) {
			return directionalLightIcon;
		} else if( value instanceof PointLight ) {
			return pointLightIcon;
		} else if( value instanceof Light ) {
			return defaultLightIcon;
		} else if( value instanceof Transformable ) {
			if( ((Transformable)value).getParent() instanceof Transformable ) {
				return subpartIcon;
			} else {
				return modelIcon;
			}
		} else if( value instanceof World ) {
			return sceneIcon;
		} else if( value instanceof Group ) {
			return folderIcon;
		} else if( value instanceof java.awt.Image ) {
			return new javax.swing.ImageIcon( (java.awt.Image)value );
		} else if( value instanceof String ) {
			return getIconForString( (String)value );
		} else if( value instanceof Integer ) {
			String s = (String)AuthoringToolResources.resources.keyCodesToStrings.get( value );
			if( s != null ) {
				return getIconForString( "keyboardKeys/" + s );
			} else {
				return null;
			}
		} else {
			return defaultIcon;
		}
	}


	public static javax.swing.ImageIcon getDisabledIcon( javax.swing.ImageIcon inputIcon ) {
		return getDisabledIcon( inputIcon, 70 );
	}

	public static javax.swing.ImageIcon getDisabledIcon( javax.swing.ImageIcon inputIcon, int percentGray ) {
		javax.swing.ImageIcon disabledIcon = (javax.swing.ImageIcon)AuthoringToolResources.resources.disabledIconMap.get( inputIcon );

		if( disabledIcon == null ) {
			javax.swing.GrayFilter filter = new javax.swing.GrayFilter( true, percentGray );
			java.awt.image.ImageProducer producer = new java.awt.image.FilteredImageSource( inputIcon.getImage().getSource(), filter );
			java.awt.Image grayImage = java.awt.Toolkit.getDefaultToolkit().createImage( producer );
			disabledIcon = new javax.swing.ImageIcon( grayImage );
			AuthoringToolResources.resources.disabledIconMap.put( inputIcon, disabledIcon );
		}

		return disabledIcon;
	}

	public static void openURL( String urlString ) throws java.io.IOException {
		if( (System.getProperty( "os.name" ) != null) && System.getProperty( "os.name" ).startsWith( "Windows" ) ) {
			String[] cmdarray = new String[3];
			cmdarray[0] = "rundll32";
			cmdarray[1] = "url.dll,FileProtocolHandler";
			cmdarray[2] = urlString;

			if( urlString.indexOf( "&stacktrace" ) > -1 ) {
				try {
					java.io.File tempURL = java.io.File.createTempFile( "tempURLHolder", ".url" );
					tempURL = tempURL.getAbsoluteFile();
					tempURL.deleteOnExit();
					java.io.PrintWriter urlWriter = new java.io.PrintWriter( new java.io.BufferedWriter( new java.io.FileWriter( tempURL ) ) );
					urlWriter.println( "[InternetShortcut]" );
					urlWriter.println( "URL=" + urlString );
					urlWriter.flush();
					urlWriter.close();
					cmdarray[2] = tempURL.getAbsolutePath();
				} catch( Throwable t ) {
					cmdarray[2] = urlString.substring( 0, urlString.indexOf( "&stacktrace" ) );
				}
			}

			Runtime.getRuntime().exec( cmdarray );


		} else {
			// try netscape
		    try {
				String[] cmd = new String[] { "netscape", urlString };
				Runtime.getRuntime().exec( cmd );
		    } catch( Throwable t ) {
		        String lcOSName = System.getProperty( "os.name" ).toLowerCase();
		        if( lcOSName.startsWith( "mac os x" ) ) {
		            Runtime.getRuntime().exec( "open " + urlString );
		        }
		    }
		}
	}



	public static boolean equals( Object o1, Object o2 ) {
		if( o1 == null ) {
			return o2 == null;
		} else {
			return o1.equals( o2 );
		}
	}

	public static Double parseDouble( String doubleString ) {
		Double number = null;
		if( doubleString.trim().equalsIgnoreCase( "infinity" ) ) {
			number = new Double( Double.POSITIVE_INFINITY );
		} else if( doubleString.trim().equalsIgnoreCase( "-infinity" ) ) {
			number = new Double( Double.NEGATIVE_INFINITY );
		} else if( doubleString.indexOf( '/' ) > -1 ) {
			if( doubleString.lastIndexOf( '/' ) == doubleString.indexOf( '/' ) ) {
				String numeratorString = doubleString.substring( 0, doubleString.indexOf( '/' ) );
				String denominatorString = doubleString.substring( doubleString.indexOf( '/' ) + 1 );
				try {
					number = new Double( Double.parseDouble( numeratorString ) / Double.parseDouble( denominatorString ) );
				} catch( NumberFormatException e ) {}
			}
		} else {
			try {
				number = Double.valueOf( doubleString );
			} catch( NumberFormatException e ) {}
		}

		return number;
	}

	/**
	 * gets the the world's dummy object group, and creates it if necessary
	 */
	public static Group getDummyObjectGroup( World world ) {
		Element[] groups = world.getChildren( Group.class );
		for( int i = 0; i < groups.length; i++ ) {
			if( (groups[i].data.get( "dummyObjectGroup" ) != null) && groups[i].data.get( "dummyObjectGroup" ).equals( "true" ) && world.groups.contains( groups[i] ) ) {
				return (Group)groups[i];
			}
		}

		Group dummyObjectGroup = new Group();
		dummyObjectGroup.name.set( "Dummy Objects" );
		dummyObjectGroup.data.put( "dummyObjectGroup", "true" );
		dummyObjectGroup.valueClass.set( Dummy.class );
		world.addChild( dummyObjectGroup );
		world.groups.add( dummyObjectGroup );
		return dummyObjectGroup;
	}

	public static boolean hasDummyObjectGroup( World world ) {
		if( world != null ) {
			Element[] groups = world.getChildren( Group.class );
			for( int i = 0; i < groups.length; i++ ) {
				if( (groups[i].data.get( "dummyObjectGroup" ) != null) && groups[i].data.get( "dummyObjectGroup" ).equals( "true" ) && world.groups.contains( groups[i] ) ) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isMethodHookedUp( Response response, World world ) {
		return isMethodHookedUp( response, world, new Vector() );
	}

	private static boolean isMethodHookedUp( Response response, World world, Vector checkedMethods ) {
		PropertyReference[] references = response.getRoot().getPropertyReferencesTo( response, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS, false, true );
		for( int i = 0; i < references.length; i++ ) {
			Element referrer = references[i].getProperty().getOwner();
			if( world.behaviors.contains( referrer ) ) {
				return true;
			} else if( (referrer instanceof Response) && (! checkedMethods.contains( referrer )) ) {
				checkedMethods.add( referrer );
				if( isMethodHookedUp( (Response)referrer, world, checkedMethods ) ) {
					return true;
				}
			}
		}

		return false;
	}

	public static Response createUndoResponse( Response response ) {
		Response undoResponse = null;

		Class responseClass = response.getClass();
		if( response instanceof ResizeAnimation ) {
			ResizeAnimation resizeResponse = (ResizeAnimation)response;
			ResizeAnimation undoResizeResponse = new ResizeAnimation();

			undoResizeResponse.amount.set( new Double( 1.0/resizeResponse.amount.doubleValue() ) );
			undoResizeResponse.asSeenBy.set( resizeResponse.asSeenBy.get() );
			undoResizeResponse.dimension.set( resizeResponse.dimension.get() );
			undoResizeResponse.likeRubber.set( resizeResponse.likeRubber.get() );
			undoResizeResponse.subject.set( resizeResponse.subject.get() );

			undoResponse = undoResizeResponse;
		} else if( response instanceof DirectionAmountTransformAnimation ) {
			try {
				undoResponse = (DirectionAmountTransformAnimation)responseClass.newInstance();
				Direction direction = (Direction)((DirectionAmountTransformAnimation)response).direction.getValue();
				Direction opposite = new Direction(
					(direction.getMoveAxis() == null) ? null : edu.cmu.cs.stage3.math.Vector3.negate( direction.getMoveAxis() ),
					(direction.getTurnAxis() == null) ? null : edu.cmu.cs.stage3.math.Vector3.negate( direction.getTurnAxis() ),
					(direction.getRollAxis() == null) ? null : edu.cmu.cs.stage3.math.Vector3.negate( direction.getRollAxis() )
				);
				((DirectionAmountTransformAnimation)undoResponse).subject.set( ((DirectionAmountTransformAnimation)response).subject.get() );
				((DirectionAmountTransformAnimation)undoResponse).amount.set( ((DirectionAmountTransformAnimation)response).amount.get() );
				((DirectionAmountTransformAnimation)undoResponse).direction.set( opposite );
				((DirectionAmountTransformAnimation)undoResponse).asSeenBy.set( ((DirectionAmountTransformAnimation)response).asSeenBy.get() );
				((DirectionAmountTransformAnimation)undoResponse).style.set( ((DirectionAmountTransformAnimation)response).style.get() );
			} catch( IllegalAccessException e ) {
				AuthoringTool.showErrorDialog( "Error creating new response: " + responseClass, e );
			} catch( InstantiationException e ) {
				AuthoringTool.showErrorDialog( "Error creating new response: " + responseClass, e );
			}
		} else if( response instanceof TransformAnimation ) {
			undoResponse = new PropertyAnimation();
			Transformable transformable = (Transformable)((TransformAnimation)response).subject.getValue();
			edu.cmu.cs.stage3.math.Matrix44 localTransformation = transformable.getLocalTransformation();
			((PropertyAnimation)undoResponse).element.set( transformable );
			((PropertyAnimation)undoResponse).propertyName.set( transformable.localTransformation.getName() );
			((PropertyAnimation)undoResponse).value.set( localTransformation );
			((PropertyAnimation)undoResponse).howMuch.set( edu.cmu.cs.stage3.util.HowMuch.INSTANCE );
		} else if( response instanceof PropertyAnimation ) {
			undoResponse = new PropertyAnimation();
			Element element = ((PropertyAnimation)response).element.getElementValue();
			((PropertyAnimation)undoResponse).element.set( element );
			((PropertyAnimation)undoResponse).propertyName.set( ((PropertyAnimation)response).propertyName.get() );
			((PropertyAnimation)undoResponse).value.set( element.getPropertyNamed( ((PropertyAnimation)response).propertyName.getStringValue() ).getValue() );
			((PropertyAnimation)undoResponse).howMuch.set( ((PropertyAnimation)response).howMuch.get() );
		} else if( response instanceof SayAnimation ||
				   response instanceof ThinkAnimation ||
				   response instanceof Wait ||
				   response instanceof SoundResponse) {
			undoResponse = new Wait();
			undoResponse.duration.set( new Double( 0.0 ) );
		}else if( response instanceof PoseAnimation){
			PoseAnimation poseAnim = (PoseAnimation)response;
			undoResponse = new PoseAnimation();
			Transformable subject = (Transformable)poseAnim.subject.get();
			Pose currentPose = Pose.manufacturePose( subject, subject );
			((PoseAnimation)undoResponse).subject.set(subject);
			((PoseAnimation)undoResponse).pose.set(currentPose);
//			TODO: handle CompositeAnimations... and everything else...
		}

		if( undoResponse != null ) {
			undoResponse.duration.set( response.duration.get() );
		} else {
			undoResponse = new Wait();
			undoResponse.duration.set( new Double( 0.0 ) );
			AuthoringTool.showErrorDialog( "Could not create undoResponse for " + response, null );
		}

		return undoResponse;
	}

	public static void addAffectedProperties( List affectedProperties, Element element, String propertyName, edu.cmu.cs.stage3.util.HowMuch howMuch ) {
		Property property = element.getPropertyNamed( propertyName );
		if( property != null ) {
			affectedProperties.add( property );
		}
		if( howMuch.getDescend() ) {
			for( int i = 0;i < element.getChildCount(); i++ ) {
				Element child = element.getChildAt( i );
				if( child.isFirstClass.booleanValue() && howMuch.getRespectDescendant() ) {
					//respect descendant
				} else {
					addAffectedProperties( affectedProperties, child, propertyName, howMuch );
				}
			}
		}
	}

	/**
	 * this method only handles some cases.  you cannot depend on it to return the correct Property array for all responses.
	 */
	public static Property[] getAffectedProperties( Response response ) {
		Property[] properties = null;

		if( response instanceof ResizeAnimation ) {
			Transformable transformable = (Transformable)((TransformAnimation)response).subject.getElementValue();
			Vector pVector = new Vector();
			pVector.add( transformable.localTransformation );
			if( transformable instanceof Model ) {
				pVector.add( ((Model)transformable).visualScale );
			}
			Transformable[] descendants = (Transformable[])transformable.getDescendants( Transformable.class );
			for( int i = 0; i < descendants.length; i++ ) {
				pVector.add( descendants[i].localTransformation );
				if( descendants[i] instanceof Model ) {
					pVector.add( ((Model)descendants[i]).visualScale );
				}
			}
			properties = (Property[])pVector.toArray( new Property[0] );
		} else if( response instanceof TransformAnimation ) {
			Transformable transformable = (Transformable)((TransformAnimation)response).subject.getElementValue();
			properties = new Property[] { transformable.localTransformation };
		} else if( response instanceof TransformResponse ) {
			Transformable transformable = (Transformable)((TransformResponse)response).subject.getElementValue();
			properties = new Property[] { transformable.localTransformation };
		} else if( response instanceof PropertyAnimation ) {
			Element element = ((PropertyAnimation)response).element.getElementValue();
			String propertyName = ((PropertyAnimation)response).propertyName.getStringValue();
			edu.cmu.cs.stage3.util.HowMuch howMuch = (edu.cmu.cs.stage3.util.HowMuch)((PropertyAnimation)response).howMuch.getValue();

			LinkedList propertyList = new LinkedList();
			addAffectedProperties( propertyList, element, propertyName, howMuch );
			properties = (Property[])propertyList.toArray( new Property[0] );
		} //TODO: handle everything else

		if( properties == null ) {
			properties = new Property[0];
		}

		return properties;
	}

	public static Billboard makeBillboard( TextureMap textureMap, boolean makeTextureChild ) {
		java.awt.image.ImageObserver sizeObserver = new java.awt.image.ImageObserver() {
			public boolean imageUpdate( java.awt.Image img, int infoflags, int x, int y, int width, int height ) {
				return (infoflags & java.awt.image.ImageObserver.WIDTH & java.awt.image.ImageObserver.HEIGHT) > 0;
			}
		};

		if( textureMap != null ) {
			int imageWidth = textureMap.image.getImageValue().getWidth( sizeObserver );
			int imageHeight = textureMap.image.getImageValue().getHeight( sizeObserver );
			double aspectRatio = (double)imageWidth/(double)imageHeight;
			double width, height;
			if( aspectRatio < 1.0 ) {
				width = 1.0;
				height = 1.0/aspectRatio;
			} else {
				width = aspectRatio;
				height = 1.0;
			}

			edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] vertices = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] {
				edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(  width/2.0, 0.0,    0.0,  0.0, 0.0, 1.0,   0.0f, 0.0f ),
				edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(  width/2.0, height, 0.0,  0.0, 0.0, 1.0,   0.0f, 1.0f ),
				edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV( -width/2.0, height, 0.0,  0.0, 0.0, 1.0,   1.0f, 1.0f ),
				edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV( -width/2.0, 0.0,    0.0,  0.0, 0.0, 1.0,   1.0f, 0.0f ),
				edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV( -width/2.0, 0.0,    0.0,  0.0, 0.0, -1.0,  1.0f, 0.0f ),
				edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV( -width/2.0, height, 0.0,  0.0, 0.0, -1.0,  1.0f, 1.0f ),
				edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(  width/2.0, height, 0.0,  0.0, 0.0, -1.0,  0.0f, 1.0f ),
				edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(  width/2.0, 0.0,    0.0,  0.0, 0.0, -1.0,  0.0f, 0.0f ),
			};
			int[] indices = new int[] {
				0, 1, 2,
				0, 2, 3,
				4, 5, 6,
				4, 6, 7
			};

			IndexedTriangleArray geom = new IndexedTriangleArray();
			geom.vertices.set( vertices );
			geom.indices.set( indices );

			Billboard billboard = new Billboard();
			billboard.isFirstClass.set( true );
			billboard.geometries.add( geom );
			billboard.geometry.set( geom );
			billboard.addChild( geom );

			if( makeTextureChild ) {
				if( textureMap.getParent() != null ) {
					textureMap.removeFromParent();
				}
				billboard.addChild( textureMap );
				billboard.textureMaps.add( textureMap );
				billboard.diffuseColorMap.set( textureMap );
				billboard.name.set( textureMap.name.getStringValue() );
				textureMap.name.set( textureMap.name.getStringValue() + "_Texture" );
			} else {
				billboard.name.set( textureMap.name.getStringValue() + "_Billboard" );
				billboard.diffuseColorMap.set( textureMap );
			}

			return billboard;
		}

		return null;
	}

	public static void centerComponentOnScreen( java.awt.Component c ) {
		java.awt.Dimension size = c.getSize();
		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

		int x = (screenSize.width/2) - (size.width/2);
		int y = (screenSize.height/2) - (size.height/2);

		c.setLocation( x, y );
	}

	public static void ensureComponentIsOnScreen( java.awt.Component c ) {
		java.awt.Point location = c.getLocation();
		java.awt.Dimension size = c.getSize();
		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		screenSize.height -= 28; // hack for standard Windows Task Bar

		if( ! (c instanceof java.awt.Window) ) {
			javax.swing.SwingUtilities.convertPointToScreen( location, c.getParent() );
		}

		if( location.x < 0 ) {
			location.x = 0;
		} else if( location.x + size.width > screenSize.width ) {
			location.x -= (location.x + size.width) - screenSize.width;
		}
		if( location.y < 0 ) {
			location.y = 0;
		} else if( location.y + size.height > screenSize.height ) {
			location.y -= (location.y + size.height) - screenSize.height;
		}

		if( ! (c instanceof java.awt.Window) ) {
			javax.swing.SwingUtilities.convertPointFromScreen( location, c.getParent() );
		}

		c.setLocation( location );
	}

	public static String getNameForNewChild( String baseName, Element parent ) {
		String name = baseName;

		if( (name == null) || (parent == null) ) {
			return name;
		}

		if( (parent.getChildNamedIgnoreCase( name ) == null) && (parent.getChildNamedIgnoreCase( name + 1 ) == null) ) {
			return name;
		}

		if( baseName.length() < 1 ) {
			baseName = "copy";
		}

		// take baseName, strip a number off the end if necessary, and use next available number after the stripped number
		int begin = baseName.length() - 1;
		int end = baseName.length();
		int endDigit = 2;
		while( begin >= 0 ) {
			try {
				endDigit = Integer.parseInt( baseName.substring( begin, end ) );
				name = baseName.substring( 0, begin );
				begin--;
			} catch( NumberFormatException e ) {
				break;
			}
		}
		baseName = name;
		for( int i = endDigit; i < Integer.MAX_VALUE; i++ ) {
			name = baseName + i;
			if( parent.getChildNamedIgnoreCase( name ) == null ) {
				return name;
			}
		}

		throw new RuntimeException( "Unable to find a suitable new name; baseName = " + baseName + ", parent = " + parent );
	}

	/*
	public static String[] convertToStringArray( Object[] arr ) {
		String[] strings = new String[arr.length];
		for( int i = 0; i < arr.length; i++ ) {
			strings[i] = (String)arr[i];
		}
		return strings;
	}
	*/

	public static void setWorldTreeChildrenPropertiesStructure( Vector worldTreeChildrenPropertiesStructure ) {
		AuthoringToolResources.resources.worldTreeChildrenPropertiesStructure = worldTreeChildrenPropertiesStructure;
	}

	public static String[] getWorldTreeChildrenPropertiesStructure( Class elementClass ) {
		if( AuthoringToolResources.resources.worldTreeChildrenPropertiesStructure != null ) {
			for( Iterator iter = AuthoringToolResources.resources.worldTreeChildrenPropertiesStructure.iterator(); iter.hasNext(); ) {
				Object o = iter.next();
				if( o instanceof edu.cmu.cs.stage3.util.StringObjectPair ) {
					String className = ((edu.cmu.cs.stage3.util.StringObjectPair)o).getString();
					try {
						Class c = Class.forName( className );
						if( c.isAssignableFrom( elementClass ) ) {
							return (String[])((edu.cmu.cs.stage3.util.StringObjectPair)o).getObject();
						}
					} catch( java.lang.ClassNotFoundException e ) {
						AuthoringTool.showErrorDialog( "Can't find class " + className, e );
					}
				} else {
					AuthoringTool.showErrorDialog( "Unexpected object found in worldTreeChildrenPropertiesStructure: " + o, null );
				}
			}
		}

		return null;
	}

	public static void addElementToAppropriateProperty( Element element, Element parent ) {
		ObjectArrayProperty oap = null;

		if( element instanceof Transformable ) {
			if( parent instanceof World ) {
				oap = ((World)parent).sandboxes;
			} else if( parent instanceof Transformable ) {
				oap = ((Transformable)parent).parts;
			} else if( parent instanceof Group ) {
				oap = ((Group)parent).values;
			}
		} else if( element instanceof Response ) {
			if( parent instanceof Sandbox ) {
				oap = ((Sandbox)parent).responses;
			}
		} else if( element instanceof Behavior ) {
			if( parent instanceof Sandbox ) {
				oap = ((Sandbox)parent).behaviors;
			}
		} else if( element instanceof Variable ) {
			if( parent instanceof Sandbox ) {
				oap = ((Sandbox)parent).variables;
			}
		} else if( element instanceof Question ) {
			if( parent instanceof Sandbox ) {
				oap = ((Sandbox)parent).questions;
			}
		} else if( element instanceof Sound ) {
			if( parent instanceof Sandbox ) {
				oap = ((Sandbox)parent).sounds;
			}
		} else if( element instanceof TextureMap ) {
			if( parent instanceof Sandbox ) {
				oap = ((Sandbox)parent).textureMaps;
			}
		} else if( element instanceof Pose ) {
			if( parent instanceof Transformable ) {
				oap = ((Transformable)parent).poses;
			}
		} else {
			if( parent instanceof Sandbox ) {
				oap = ((Sandbox)parent).misc;
			}
		}

		if( oap != null ) {
			if( ! oap.contains( element ) ) {
				oap.add( element );
			}
		}
	}

	public static double getAspectRatio( World world ) {
		if( world != null ) {
			SymmetricPerspectiveCamera[] spCameras = (SymmetricPerspectiveCamera[])world.getDescendants( SymmetricPerspectiveCamera.class );
			if( spCameras.length > 0 ) {
				return spCameras[0].horizontalViewingAngle.doubleValue()/spCameras[0].verticalViewingAngle.doubleValue();
			}
		}
		return 0.0;
	}

	public static double getCurrentTime() {
		long timeMillis = System.currentTimeMillis() - startTime;
		return timeMillis/1000.0;
	}

	public static void setImporterClasses( Class[] importers ) {
		AuthoringToolResources.resources.importers = importers;
	}

	public static Class[] getImporterClasses() {
		return AuthoringToolResources.resources.importers;
	}

	public static void setEditorClasses( Class[] editors ) {
		AuthoringToolResources.resources.editors = editors;
	}

	public static Class[] getEditorClasses() {
		return AuthoringToolResources.resources.editors;
	}

	public static void findAssignables( Class baseClass, Set result, boolean includeInterfaces ) {
		if( baseClass != null ) {
			if( ! result.contains( baseClass ) ) {
				result.add( baseClass );

				if( includeInterfaces ) {
					Class[] interfaces = baseClass.getInterfaces();
					for( int i = 0; i < interfaces.length; i++ ) {
						findAssignables( interfaces[i], result, includeInterfaces );
					}
				}

				findAssignables( baseClass.getSuperclass(), result, includeInterfaces );
			}
		}
	}

	public static DataFlavor getReferenceFlavorForClass( Class c ) {
		if( ! AuthoringToolResources.resources.flavorMap.containsKey( c ) ) {
			try {
				AuthoringToolResources.resources.flavorMap.put( c, new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType + "; class=" + c.getName() ) );
			} catch( ClassNotFoundException e ) {
				AuthoringTool.showErrorDialog( "Can't find class " + c.getName(), e );
			}
		}
		return (DataFlavor)AuthoringToolResources.resources.flavorMap.get( c );
	}

	public static Object getDefaultValueForClass( Class cls ) {
		if( cls == Boolean.class ) {
			return Boolean.TRUE;
		} else if( cls == Number.class ) {
			return new Double( 1 );
		} else if( cls == String.class ) {
			return new String( "default string" );
		} else if( cls == javax.vecmath.Vector3d.class ) {
			return edu.cmu.cs.stage3.math.MathUtilities.createXAxis();
		} else if( cls == edu.cmu.cs.stage3.math.Vector3.class ) {
			return new edu.cmu.cs.stage3.math.Vector3();
		} else if( cls == edu.cmu.cs.stage3.math.Quaternion.class ) {
			return new edu.cmu.cs.stage3.math.Quaternion();
		} else if( javax.vecmath.Matrix4d.class.isAssignableFrom( cls ) ) {
			return new edu.cmu.cs.stage3.math.Matrix44();
		} else if( cls == Color.class ) {
			return Color.WHITE;
		} else if( cls == edu.cmu.cs.stage3.alice.scenegraph.Color.class ) {
			return edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE;
		} else if( edu.cmu.cs.stage3.util.Enumerable.class.isAssignableFrom( cls ) ) {
			edu.cmu.cs.stage3.util.Enumerable[] items = edu.cmu.cs.stage3.util.Enumerable.getItems( cls );
			if( items.length > 0 ) {
				return items[ 0 ];
			} else {
				return null;
			}
		} else if( cls == ReferenceFrame.class ) {
			return AuthoringTool.getInstance().getWorld();
		} else {
			return null;
		}
	}

//	public static javax.vecmath.Matrix4d getAGoodLookAtMatrix( Transformable transformable, camera.SymmetricPerspectiveCamera camera ) {
//		if( (transformable != null) && (camera != null) ) {
//			Transformable getAGoodLookDummy = new Transformable();
//			getAGoodLookDummy.vehicle.set( camera.vehicle.get() );
//			edu.cmu.cs.stage3.math.Sphere bs = transformable.getBoundingSphere();
//			double radius = bs.getRadius();
//			if( (radius == 0.0) || Double.isNaN( radius ) ) {
//				radius = 1.0;
//			}
//			double theta = Math.min( camera.horizontalViewingAngle.doubleValue(), camera.verticalViewingAngle.doubleValue() );
//			double dist = radius/Math.sin( theta/2.0 );
//			double offset = dist/Math.sqrt( 3.0 );
//			javax.vecmath.Vector3d center = bs.getCenter();
//			if( center == null ) { // this should be unnecessary
//				center = transformable.getPosition();
//			}
//
//			if( center != null ) {
//				if( (! Double.isNaN( center.x ) ) && (! Double.isNaN( center.y ) ) && (! Double.isNaN( center.z ) ) && (! Double.isNaN( offset ) ) ) {
//					getAGoodLookDummy.setPositionRightNow( center.x - offset, center.y + offset, center.z + offset, transformable );
//					getAGoodLookDummy.pointAtRightNow( transformable, new edu.cmu.cs.stage3.math.Vector3( center ) );
//					javax.vecmath.Matrix4d result = getAGoodLookDummy.getLocalTransformation();
//					getAGoodLookDummy.vehicle.set( null );
//					return result;
//				} else {
//					AuthoringTool.showErrorDialog( "bad bounding sphere center: " + center, null );
//				}
//			} else {
//				AuthoringTool.showErrorDialog( "bounding sphere returned null center", null );
//			}
//		}
//
//		return null;
//	}

	public static double distanceToBackAfterGetAGoodLookAt( Transformable transformable, SymmetricPerspectiveCamera camera ) {
		if( (transformable != null) && (camera != null) ) {
			edu.cmu.cs.stage3.math.Sphere bs = transformable.getBoundingSphere();
			double radius = bs.getRadius();
			double theta = Math.min( camera.horizontalViewingAngle.doubleValue(), camera.verticalViewingAngle.doubleValue() );
			return radius/Math.sin( theta/2.0 ) + radius;
		}

		return 0.0;
	}

	public static boolean areExperimentalFeaturesEnabled() {
		return AuthoringToolResources.resources.experimentalFeaturesEnabled;
	}

	public static void setExperimentalFeaturesEnabled( boolean enabled ) {
		AuthoringToolResources.resources.experimentalFeaturesEnabled = enabled;
	}

	public static void putMiscItem( Object key, Object item ) {
		AuthoringToolResources.resources.miscMap.put( key, item );
	}

	public static Object getMiscItem( Object key ) {
		return AuthoringToolResources.resources.miscMap.get( key );
	}

	public static void garbageCollectIfPossible( PropertyReference[] references ) {
		for( int i = 0; i < references.length; i++ ) {
			Element element = references[i].getProperty().getOwner();
//			if( element instanceof CallToUserDefinedResponse ) {
				PropertyReference[] metaReferences = element.getRoot().getPropertyReferencesTo( element, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS, false, true );
				if( metaReferences.length == 0 ) {
					element.getParent().removeChild( element );
				}
//			}
		}
	}

	public static String formatMemorySize( long bytes ) {
		String sizeString = null;
		if( bytes < 1024 ) {
			sizeString = AuthoringToolResources.resources.decimalFormatter.format( bytes ) + " bytes";
		} else if( bytes < 1024L*1024L ) {
			sizeString = AuthoringToolResources.resources.decimalFormatter.format( ((double)bytes)/((double)1024) ) + " KB";
		} else if( bytes < 1024L*1024L*1024L ) {
			sizeString = AuthoringToolResources.resources.decimalFormatter.format( ((double)bytes)/((double)1024L*1024L) ) + " MB";
		} else if( bytes < 1024L*1024L*1024L*1024L ) {
			sizeString = AuthoringToolResources.resources.decimalFormatter.format( ((double)bytes)/((double)1024L*1024L*1024L) ) + " GB";
		} else {
			sizeString = AuthoringToolResources.resources.decimalFormatter.format( ((double)bytes)/((double)1024L*1024L*1024L*1024L) ) + " TB";
		}
		return sizeString;
	}

	public static String formatTime( double seconds ) {
		if( Double.isNaN( seconds ) ) {
			return "?:??";
		} else {
			java.text.DecimalFormat decFormatter = new java.text.DecimalFormat( ".000" );
			java.text.DecimalFormat secMinFormatter1 = new java.text.DecimalFormat( "00" );
			java.text.DecimalFormat secMinFormatter2 = new java.text.DecimalFormat( "#0" );

			double secondsFloored = (int)Math.floor( seconds );
			double decimal = seconds - secondsFloored;
			double secs = secondsFloored % 60.0;
			double minutes = ((secondsFloored - secs)/60.0) % 60.0;
			double hours = (secondsFloored - 60.0*minutes - secs)/(60.0*60.0);

			String timeString = secMinFormatter1.format( secs ) + decFormatter.format( decimal );
			if( hours > 0.0 ) {
				timeString = secMinFormatter1.format( minutes ) + ":" + timeString;
				timeString = secMinFormatter2.format( hours ) + ":" + timeString;
			} else {
				timeString = secMinFormatter2.format( minutes ) + ":" + timeString;
			}

			return timeString;
		}
	}

	public static void printHierarchy( java.awt.Component c ) {
		printHierarchy( c, 0 );
	}
	private static void printHierarchy( java.awt.Component c, int level ) {
		String tabs = "";
		for( int i = 0; i < level; i++ ) {
			tabs += "--";
		}
		System.out.println( tabs + c.getClass().getName() + "_" + c.hashCode() );

		if( c instanceof java.awt.Container ) {
			java.awt.Component[] children = ((java.awt.Container)c).getComponents();
			for( int i = 0; i < children.length; i++ ) {
				printHierarchy( children[i], level + 1 );
			}
		}
	}

	private static void initKeyCodesToStrings() {
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_0 ), "0" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_1 ), "1" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_2 ), "2" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_3 ), "3" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_4 ), "4" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_5 ), "5" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_6 ), "6" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_7 ), "7" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_8 ), "8" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_9 ), "9" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_A ), "A" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_B ), "B" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_C ), "C" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_D ), "D" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_E ), "E" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_F ), "F" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_G ), "G" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_H ), "H" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_I ), "I" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_J ), "J" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_K ), "K" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_L ), "L" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_M ), "M" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_N ), "N" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_O ), "O" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_P ), "P" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_Q ), "Q" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_R ), "R" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_S ), "S" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_T ), "T" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_U ), "U" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_V ), "V" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_W ), "W" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_X ), "X" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_Y ), "Y" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_Z ), "Z" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_ENTER ), "enter" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_SPACE ), "space" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_UP ), "upArrow" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_DOWN ), "downArrow" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_LEFT ), "leftArrow" );
		AuthoringToolResources.resources.keyCodesToStrings.put( new Integer(  KeyEvent.VK_RIGHT ), "rightArrow" );
	}

	public static void copyFile( java.io.File from, java.io.File to ) throws java.io.IOException {
		if( ! to.exists() ) {
			to.createNewFile();
		}
		java.io.BufferedInputStream in = new java.io.BufferedInputStream( new java.io.FileInputStream( from ) );
		java.io.BufferedOutputStream out = new java.io.BufferedOutputStream( new java.io.FileOutputStream( to ) );

		int b = in.read();
		while( b != -1 ) {
			out.write( b );
			b = in.read();
		}

		in.close();
		out.flush();
		out.close();
	}

	/////////////////////////////
	// HACK code for stencils
	/////////////////////////////

	public static String getPrefix( String token ) {
		if( (token.indexOf( "<" ) > -1) && (token.indexOf( ">" ) > token.indexOf( "<" )) ) {
			return token.substring( 0, token.indexOf( "<" ) );
		} else {
			return token;
		}
	}

	public static String getSpecifier( String token ) {
		if( (token.indexOf( "<" ) > -1) && (token.indexOf( ">" ) > token.indexOf( "<" )) ) {
			return token.substring( token.indexOf( "<" ) + 1, token.indexOf( ">" ) );
		} else {
			return null;
		}
	}

	public static java.awt.Component findElementDnDPanel( java.awt.Container root, final Element element ) {
		edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			public boolean accept( Object o ) {
				if( o instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel ) {
					try {
						Transferable transferable = ((edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel)o).getTransferable();
						if( (transferable != null) && AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor ) ) {
							Element e = (Element)transferable.getTransferData( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor );
							if( element.equals( e ) ) {
								return true;
							}
						}
					} catch( Exception e ) {
						AuthoringTool.showErrorDialog( "Error finding ElementDnDPanel.", e );
					}
				}
				return false;
			}
		};
		java.awt.Component toReturn = findComponent( root, criterion );
		if (toReturn instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel){
			return ((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel)toReturn).getWorkSpace();
		}
		else{
			return toReturn;
		}
	}

	public static java.awt.Component findPropertyDnDPanel( java.awt.Container root, final Element element, final String propertyName ) {
		edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			public boolean accept( Object o ) {
				if( o instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel ) {
					try {
						Transferable transferable = ((edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel)o).getTransferable();
						if( (transferable != null) && AuthoringToolResources.safeIsDataFlavorSupported( transferable,  edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor ) ) {
							Property p = (Property)transferable.getTransferData( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor );
							Element e = p.getOwner();
							if( element.equals( e ) && p.getName().equals( propertyName ) ) {
								return true;
							}
						}
					} catch( Exception e ) {
						AuthoringTool.showErrorDialog( "Error finding PropertyDnDPanel.", e );
					}
				}
				return false;
			}
		};

		return findComponent( root, criterion );
	}

	public static java.awt.Component findUserDefinedResponseDnDPanel( java.awt.Container root, final Response actualResponse ) {
		edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			public boolean accept( Object o ) {
				if( o instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel ) {
					try {
						Transferable transferable = ((edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel)o).getTransferable();
						if( (transferable != null) && AuthoringToolResources.safeIsDataFlavorSupported( transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor ) ) {
							edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype p = (edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype)transferable.getTransferData( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor );
							if( p.getActualResponse().equals( actualResponse ) ) {
								return true;
							}
						}
					} catch( Exception e ) {
						AuthoringTool.showErrorDialog( "Error finding UserDefinedResponseDnDPanel.", e );
					}
				}
				return false;
			}
		};

		return findComponent( root, criterion );
	}

	public static java.awt.Component findUserDefinedQuestionDnDPanel( java.awt.Container root, final Question actualQuestion ) {
		edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			public boolean accept( Object o ) {
				if( o instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel ) {
					try {
						Transferable transferable = ((edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel)o).getTransferable();
						if( (transferable != null) && AuthoringToolResources.safeIsDataFlavorSupported( transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor ) ) {
							edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype p = (edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype)transferable.getTransferData( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor );
							if( p.getActualQuestion().equals( actualQuestion ) ) {
								return true;
							}
						}
					} catch( Exception e ) {
						AuthoringTool.showErrorDialog( "Error finding UserDefinedQuestionDnDPanel.", e );
					}
				}
				return false;
			}
		};

		return findComponent( root, criterion );
	}

	public static java.awt.Component findPrototypeDnDPanel( java.awt.Container root, final Class elementClass ) {
		edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			public boolean accept( Object o ) {
				if( o instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel ) {
					try {
						Transferable transferable = ((edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel)o).getTransferable();
						if( (transferable != null) && AuthoringToolResources.safeIsDataFlavorSupported( transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor ) ) {
							edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype p = (edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype)transferable.getTransferData( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor );
							if( p.getElementClass().equals( elementClass ) ) {
								return true;
							}
						}
					} catch( Exception e ) {
						AuthoringTool.showErrorDialog( "Error finding PrototypeDnDPanel.", e );
					}
				}
				return false;
			}
		};

		return findComponent( root, criterion );
	}

	public static java.awt.Component findPropertyViewController( java.awt.Container root, final Element element, final String propertyName ) {
		edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			public boolean accept( Object o ) {
				if( o instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController) {
					Property p = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController)o).getProperty();
					if( p.getOwner().equals( element ) && p.getName().equals( propertyName ) ) {
						return true;
					}
				}
				else if ( o instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.CollectionPropertyViewController) {
					Property p = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.CollectionPropertyViewController)o).getProperty();
					if( p.getOwner().equals( element ) && p.getName().equals( propertyName ) ) {
						return true;
					}
				}
				return false;
			}
		};

		return findComponent( root, criterion );
	}

	public static java.awt.Component findButton( java.awt.Container root, final String buttonText ) {
		edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			public boolean accept( Object o ) {
				if( o instanceof javax.swing.JButton ) {
					if( ((javax.swing.JButton)o).getText().equals( buttonText ) ) {
						return true;
					}
				}
				return false;
			}
		};

		return findComponent( root, criterion );
	}

	public static java.awt.Component findEditObjectButton( java.awt.Container root, final Element element ) {
		edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			public boolean accept( Object o ) {
				if( o instanceof edu.cmu.cs.stage3.alice.authoringtool.util.EditObjectButton ) {
					if( ((edu.cmu.cs.stage3.alice.authoringtool.util.EditObjectButton)o).getObject().equals( element ) ) {
						return true;
					}
				}
				return false;
			}
		};

		return findComponent( root, criterion );
	}

	public static java.awt.Component findGalleryObject( java.awt.Container root, final String uniqueIdentifier ) {
		edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			public boolean accept( Object o ) {
				if( o instanceof edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject ) {
					if( ((edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject)o).getUniqueIdentifier().equals( uniqueIdentifier ) ) {
						return true;
					}
				}
				return false;
			}
		};

		return findComponent( root, criterion );
	}

	public static java.awt.Component findComponent( java.awt.Container root, edu.cmu.cs.stage3.util.Criterion criterion ) {
		if( criterion.accept( root ) ) {
			return root;
		}

		java.awt.Component[] children = root.getComponents();
		for( int i = 0; i < children.length; i++ ) {
			if( children[i] instanceof java.awt.Container ) {
				java.awt.Component result = findComponent( (java.awt.Container)children[i], criterion );
				if( result != null ) {
					return result;
				}
			}
		}

		return null;
	}	
}