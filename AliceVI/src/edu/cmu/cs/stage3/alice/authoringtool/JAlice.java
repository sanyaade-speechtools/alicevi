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

import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.alice.authoringtool.util.SplashScreen;
import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.alice.scenegraph.colorstate.ColorblindColorState;
import edu.cmu.cs.stage3.alice.scenegraph.colorstate.NormalColorState;
import edu.cmu.cs.stage3.alice.scenegraph.renderer.DefaultRenderTargetFactory;
import gnu.getopt.LongOpt;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;
import java.util.StringTokenizer;
import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;
import edu.cmu.cs.stage3.alice.core.Decorator;

import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.Engine;
import javax.speech.EngineException;
import javax.speech.EngineList;
import javax.speech.EngineStateError;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.SynthesizerProperties;
import javax.speech.synthesis.Voice;



/**
 * @author Jason Pratt
 */
public class JAlice {
	// version information
	private static String version = "Unknown version";

	private static String backgroundColor =  new Color( 127.0/255.0, 138.0/255.0, 209.0/255.0 ).toString();
	
	private static String directory = null;
	
	protected static String initVersion() {
		try {

			File versionFile = new File( getAliceHomeDirectory(), "etc/version.txt" ).getAbsoluteFile();
			if( versionFile.exists() ) {
				if( versionFile.canRead() ) {
					BufferedReader br = new BufferedReader( new java.io.FileReader( versionFile ) );
					String versionString = br.readLine();
					String colorString = br.readLine();
					directory = br.readLine();
					br.close();
					if (colorString != null) {
						colorString = colorString.trim();

						if (colorString.length() > 0) {
							try {

								// java.awt.Color newColor =
								// java.awt.Color.decode(colorString);
								// +++++++++++++++++++++++ my change
								// ++++++++++++++++++++
								backgroundColor = buildBackgroundColor(colorString);
							} catch (NumberFormatException numberE) {
								System.err
										.println("Color initialization string is not correct");
							} catch (Throwable colorT) {
								colorT.printStackTrace();
							}
						}
					}
					if (versionString != null) {
						versionString = versionString.trim();
						if (versionString.length() > 0) {
							return versionString;
						} else {
							return "Unknown version [first line of version.txt empty]";
						}
					} else {
						return "Unknown version [version.txt is empty]";
					}
				} else {
					return "Unknown version [cannot read version.txt]";
				}
			} else {
				return "Unknown version [version.txt does not exist]";
			}
		} catch (Throwable t) {
			t.printStackTrace();
			return "Unknown version [error while reading version.txt]";
		}
	}
	
	protected static String buildBackgroundColor(String colorString)
	{
		StringTokenizer tok = new StringTokenizer(
				colorString, ",");
		int r = Integer.parseInt(tok.nextToken());
		int b = Integer.parseInt(tok.nextToken());
		int g = Integer.parseInt(tok.nextToken());
		Color newColor = new Color(r/255.0, b/255.0, g/255.0);
		// +++++++++++++++++++++++end my change
		// ++++++++++++++++++++
		return new edu.cmu.cs.stage3.alice.scenegraph.Color(
				newColor).toString();
	}

	public static String getVersion() {
		return version;
	}

	static File aliceHomeDirectory = null;
	static File aliceUserDirectory = null;

	static SplashScreen splashScreen;
	static File defaultWorld;
	static File worldToLoad = null;
	static boolean stdOutToConsole = false;
	static boolean stdErrToConsole = false;
	static String defaultRendererClassname = null;
	static AuthoringTool authoringTool;

	static boolean mainHasFinished = false;

	// ////////////////////
	// main
	// ////////////////////

	public static void main(String[] args) {
		version = initVersion();
		try {
			String[] mp3args = new String[0];
			System.out.println("attempting to register mp3 capability... ");
			com.sun.media.codec.audio.mp3.JavaDecoder.main(mp3args);
			
			/**
			 * This is an example of how to use the voice synthesizer.
			 * 
			 *
			SynthesizerModeDesc desc = new SynthesizerModeDesc(
	                null,          // engine name
	                "general",     // mode name
	                Locale.US,     // locale
	                null,          // running
	                null);         // voice
			
			Synthesizer synth = Central.createSynthesizer(desc);
			synth.allocate();
			synth.resume();
			desc = (SynthesizerModeDesc)synth.getEngineModeDesc();
			Voice[] voices = desc.getVoices();
			synth.getSynthesizerProperties().setVoice(voices[0]);
			
			synth.speakPlainText("Hello World! I am the java void synthesizer!", null);
			synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
			synth.deallocate();
			**/
			
		} catch (Throwable t) {
			System.out.println("FAILED.");
			t.printStackTrace(System.out);
		}
		try {
			boolean useJavaBasedSplashScreen = true;
			String useSplashScreenString = System
					.getProperty("alice.useJavaBasedSplashScreen");
			if ((useSplashScreenString != null)
					&& (!useSplashScreenString.equalsIgnoreCase("true"))) {
				useJavaBasedSplashScreen = false;
			}
			parseCommandLineArgs(args);
			if (useJavaBasedSplashScreen) {
				splashScreen = initSplashScreen();
				splashScreen.showSplash();
			}
			defaultWorld = new File( getAliceHomeDirectory(), "etc/default.a2w" ).getAbsoluteFile();
			Class.forName( "edu.cmu.cs.stage3.alice.authoringtool.util.Configuration" );

			configInit();
			try{
				File aliceHasNotExitedFile = new File(JAlice.getAliceUserDirectory(), "aliceHasNotExited.txt");
				if (aliceHasNotExitedFile.exists()){
			
					aliceHasNotExitedFile.delete();
				}
				aliceHasNotExitedFile.createNewFile();

				OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(aliceHasNotExitedFile));

				writer.write("Alice has not exited propertly yet.");
				writer.flush();
				writer.close();
			} catch (Exception e) {
			}
			Class.forName("edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources");
			Class.forName("edu.cmu.cs.stage3.alice.authoringtool.util.EditorUtilities");
			authoringTool = AuthoringTool.getInstance();
			authoringTool.init(defaultWorld, worldToLoad,
					stdOutToConsole, stdErrToConsole);
			if (useJavaBasedSplashScreen) {
				splashScreen.hideSplash();
			}
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit( 1 );
		}

		mainHasFinished = true;
	}


	private static SplashScreen initSplashScreen() {
		File splashFile = new File( getAliceHomeDirectory(), "etc/AliceSplash.jpg" ).getAbsoluteFile();
		Image splashImage = Toolkit.getDefaultToolkit().getImage( splashFile.getAbsolutePath() );
		return new SplashScreen( splashImage );

	}

	/**
	 * Initializes configuration and gives default value if configuration is null
	 */	
	@SuppressWarnings(value = "unchecked")
	private static void configInit() {

		final Configuration authoringtoolConfig = Configuration.getLocalConfiguration( JAlice.class.getPackage() );
		final Configuration decoratorConfig =Configuration.getLocalConfiguration( Decorator.DECORATOR_PACKAGE);
		final Configuration colorBlindConfig = Configuration.getLocalConfiguration(
				Package.getPackage("edu.cmu.cs.stage3.alice.scenegraph.colorstate"));

		authoringtoolConfig.setValue( "backgroundColor", backgroundColor );
		if( authoringtoolConfig.getValue( "recentWorlds.maxWorlds" ) == null ) {
			authoringtoolConfig.setValue( "recentWorlds.maxWorlds", Integer.toString( 8 ) );


		}
		if (authoringtoolConfig.getValueList("recentWorlds.worlds") == null) {
			authoringtoolConfig.setValueList("recentWorlds.worlds",
					new String[] {});
		}

		if (authoringtoolConfig.getValue("enableHighContrastMode") == null) {
			authoringtoolConfig.setValue("enableHighContrastMode", "false");
		}

		if (authoringtoolConfig.getValue("fontSize") == null) {
			authoringtoolConfig.setValue("fontSize", Integer.toString(12));
		}

		if (authoringtoolConfig.getValue("showObjectLoadFeedback") == null) {
			authoringtoolConfig.setValue("showObjectLoadFeedback", "true");
		}

		if (authoringtoolConfig.getValue("maximumWorldBackupCount") == null) {
			authoringtoolConfig.setValue("maximumWorldBackupCount", Integer
					.toString(5));
		}

		if (authoringtoolConfig.getValue("maxRecentlyUsedValues") == null) {
			authoringtoolConfig.setValue("maxRecentlyUsedValues", Integer
					.toString(5));
		}

		if (authoringtoolConfig.getValue("numberOfClipboards") == null) {
			authoringtoolConfig.setValue("numberOfClipboards", Integer
					.toString(1));
		}

		if (authoringtoolConfig.getValue("showWorldStats") == null) {
			authoringtoolConfig.setValue("showWorldStats", "false");
		}

		if (authoringtoolConfig.getValue("enableScripting") == null) {
			authoringtoolConfig.setValue("enableScripting", "false");
		}

		if (authoringtoolConfig.getValue("promptToSaveInterval") == null) {
			authoringtoolConfig.setValue("promptToSaveInterval", Integer
					.toString(15));
		}

		if (authoringtoolConfig.getValue("doNotShowUnhookedMethodWarning") == null) {
			authoringtoolConfig.setValue("doNotShowUnhookedMethodWarning",
					"false");
		}

		if (authoringtoolConfig.getValue("clearStdOutOnRun") == null) {
			authoringtoolConfig.setValue("clearStdOutOnRun", "true");
		}

		if (authoringtoolConfig.getValue("resourceFile") == null) {
			authoringtoolConfig.setValue("resourceFile", "Alice Style.py");
		}

		if (authoringtoolConfig.getValue("watcherPanelEnabled") == null) {
			authoringtoolConfig.setValue("watcherPanelEnabled", "false");
		}

		if (authoringtoolConfig.getValue("showStartUpDialog") == null) {
			authoringtoolConfig.setValue("showStartUpDialog", "true");
		}

		if (authoringtoolConfig.getValue("showWebWarningDialog") == null) {
			authoringtoolConfig.setValue("showWebWarningDialog", "true");
		}

		if (authoringtoolConfig.getValue("showStartUpDialog_OpenTab") == null) {
			authoringtoolConfig
					.setValue(
							"showStartUpDialog_OpenTab",
							Integer
									.toString(edu.cmu.cs.stage3.alice.authoringtool.dialog.StartUpContentPane.TUTORIAL_TAB_ID));
		}

		if (authoringtoolConfig.getValue("loadSavedTabs") == null) {
			authoringtoolConfig.setValue("loadSavedTabs", "false");
		}

		if (authoringtoolConfig.getValue("saveThumbnailWithWorld") == null) {
			authoringtoolConfig.setValue("saveThumbnailWithWorld", "true");
		}


		if (authoringtoolConfig.getValue("mainWindowBounds") == null) {
			int screenWidth = (int) java.awt.Toolkit.getDefaultToolkit()
					.getScreenSize().getWidth();
			int screenHeight = (int) java.awt.Toolkit.getDefaultToolkit()
					.getScreenSize().getHeight();
			int x = 0;
			int y = 0;
			int height = screenHeight - 30;
			authoringtoolConfig.setValue("mainWindowBounds", (x + 80) + ", "
					+ y + ", " + (screenWidth - 80) + ", " + height);
		}


		
		if( authoringtoolConfig.getValueList( "rendering.orderedRendererList" ) == null ) {
			Class[] rendererClasses =  DefaultRenderTargetFactory.getPotentialRendererClasses();

			String[] list = new String[rendererClasses.length];
			for (int i = 0; i < rendererClasses.length; i++) {
				list[i] = rendererClasses[i].getName();
			}
			authoringtoolConfig.setValueList("rendering.orderedRendererList",
					list);
		}

		if (authoringtoolConfig.getValue("rendering.showFPS") == null) {
			authoringtoolConfig.setValue("rendering.showFPS", "false");
		}

		if (authoringtoolConfig.getValue("rendering.forceSoftwareRendering") == null) {
			authoringtoolConfig.setValue("rendering.forceSoftwareRendering",
					"false");
		}

		if (authoringtoolConfig.getValue("rendering.deleteFiles") == null) {
			authoringtoolConfig.setValue("rendering.deleteFiles", "true");
		}

		if (authoringtoolConfig
				.getValue("rendering.renderWindowMatchesSceneEditor") == null) {
			authoringtoolConfig.setValue(
					"rendering.renderWindowMatchesSceneEditor", "true");
		}

		if (authoringtoolConfig
				.getValue("rendering.ensureRenderDialogIsOnScreen") == null) {
			authoringtoolConfig.setValue(
					"rendering.ensureRenderDialogIsOnScreen", "true");
		}

		if (authoringtoolConfig.getValue("rendering.renderWindowBounds") == null) {
			int screenWidth = (int) java.awt.Toolkit.getDefaultToolkit()
					.getScreenSize().getWidth();
			int screenHeight = (int) java.awt.Toolkit.getDefaultToolkit()
					.getScreenSize().getHeight();
			int width = (int) (screenWidth * .5);
			int height = (int) Math.round(((double) width)
					/ (screenWidth / screenHeight));
			int x = (screenWidth - width) / 2;
			int y = (screenHeight - height) / 2;

			authoringtoolConfig.setValue("rendering.renderWindowBounds", x
					+ ", " + y + ", " + width + ", " + height);
		}

		if (authoringtoolConfig.getValue("rendering.runtimeScratchPadEnabled") == null) {
			authoringtoolConfig.setValue("rendering.runtimeScratchPadEnabled",
					"false");
		}

		if (authoringtoolConfig.getValue("rendering.runtimeScratchPadHeight") == null) {
			authoringtoolConfig.setValue("rendering.runtimeScratchPadHeight",
					"300");
		}

		if (authoringtoolConfig.getValue("rendering.useBorderlessWindow") == null) {
			authoringtoolConfig.setValue("rendering.useBorderlessWindow",
					"false");
		}

		if (authoringtoolConfig
				.getValue("rendering.constrainRenderDialogAspectRatio") == null) {
			authoringtoolConfig.setValue(
					"rendering.constrainRenderDialogAspectRatio", "true");
		}

		

		if (authoringtoolConfig.getValue("gui.pickUpTiles") == null) {
			authoringtoolConfig.setValue("gui.pickUpTiles", "true");
		}

		if (authoringtoolConfig.getValue("gui.useAlphaTiles") == null) {
			authoringtoolConfig.setValue("gui.useAlphaTiles", "false");
		}

		if (authoringtoolConfig.getValue("useSingleFileLoadStore") == null) {
			authoringtoolConfig.setValue("useSingleFileLoadStore", "true");
		}

		if (authoringtoolConfig.getValue("directories.worldsDirectory") == null) {
			// TODO: be more cross-platform aware
			String dir = System.getProperty("user.home")
					+ System.getProperty("file.separator") + "Desktop";
			authoringtoolConfig.setValue("directories.worldsDirectory", dir);
		}



		if (authoringtoolConfig.getValue("directories.importDirectory") == null) {
			// TODO: be more cross-platform aware
			String dir = System.getProperty("user.home")
					+ System.getProperty("file.separator") + "Desktop";
			authoringtoolConfig.setValue("directories.importDirectory", dir);
		}

		if (authoringtoolConfig.getValue("directories.examplesDirectory") == null) {
			authoringtoolConfig.setValue("directories.examplesDirectory",
					"exampleWorlds");
		}

		if (authoringtoolConfig.getValue("directories.charactersDirectory") == null) {
			String dir = System.getProperty("user.home")
					+ System.getProperty("file.separator") + "Desktop";
			java.io.File captureDir = new java.io.File(dir);
			if (captureDir.canWrite()) {
				authoringtoolConfig.setValue("directories.charactersDirectory",
						dir);
			} else {
				authoringtoolConfig.setValue("directories.charactersDirectory",
						null);
			}
		}

		if (authoringtoolConfig.getValue("directories.templatesDirectory") == null) {
			authoringtoolConfig.setValue("directories.templatesDirectory",
					"templateWorlds");
		}

		if (authoringtoolConfig
				.getValue("directories.textbookExamplesDirectory") == null) {
			authoringtoolConfig.setValue(
					"directories.textbookExamplesDirectory",
					"textbookExampleWorlds");
		}


		if (authoringtoolConfig.getValue("screenCapture.directory") == null) {
			String dir = System.getProperty("user.home")
					+ System.getProperty("file.separator") + "Desktop";
			authoringtoolConfig.setValue("screenCapture.directory", dir);
		}
		if (authoringtoolConfig.getValue("screenCapture.baseName") == null) {
			authoringtoolConfig.setValue("screenCapture.baseName", "capture");
		}
		if (authoringtoolConfig.getValue("screenCapture.numDigits") == null) {
			authoringtoolConfig.setValue("screenCapture.numDigits", "2");
		}
		if (authoringtoolConfig.getValue("screenCapture.codec") == null) {
			authoringtoolConfig.setValue("screenCapture.codec", "jpeg");
		}
		if (authoringtoolConfig.getValue("screenCapture.codec")
				.equalsIgnoreCase("gif")) {
			authoringtoolConfig.setValue("screenCapture.codec", "jpeg");
		}
		if (authoringtoolConfig.getValue("screenCapture.informUser") == null) {
			authoringtoolConfig.setValue("screenCapture.informUser", "true");
		}

		if (authoringtoolConfig.getValue("saveInfiniteBackups") == null) {
			authoringtoolConfig.setValue("saveInfiniteBackups", "false");
		}

		if (authoringtoolConfig.getValue("doProfiling") == null) {
			authoringtoolConfig.setValue("doProfiling", "false");
		}
		
		// create a default pivot line width
		if (decoratorConfig.getValue("pivotAndBoundingBoxLineWidth") == null){
			decoratorConfig.setValue("pivotAndBoundingBoxLineWidth", "9");
		}
		
		//make the color blind mode the default
		if(colorBlindConfig.getValue("colorBlindState") == null){
			colorBlindConfig.setValue("colorBlindState" , "1");
			Color.setColorState(new ColorblindColorState());
		}
		else{
			String value = colorBlindConfig.getValue("colorBlindState");
			if(value.equals("1"))
			{
				Color.setColorState(new ColorblindColorState());
			}
			else{
				Color.setColorState(new NormalColorState());
			}
		}
		
	}

	private static void parseCommandLineArgs(String[] args) {
		int c;

		//String arg;
		LongOpt[] options = {
			new LongOpt("stdOutToConsole", LongOpt.NO_ARGUMENT, null, 'o'),
			new LongOpt("stdErrToConsole", LongOpt.NO_ARGUMENT, null, 'e'),
			new LongOpt("defaultRenderer", LongOpt.REQUIRED_ARGUMENT, null, 'r'),
			//new gnu.getopt.LongOpt("customStartupClass", gnu.getopt.LongOpt.REQUIRED_ARGUMENT, null, 'c'),
			new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'),
		};


		String helpMessage = ""
				+ "\nUsage: JAlice <options> <world>\n"
				+ "\n"
				+ "options:\n"
				+ "    --stdOutToConsole|-o:\n"
				+ "        directs System.stdOut to the console instead of the output text area.\n"
				+ "    --stdErrToConsole|-e:\n"
				+ "        directs System.stdOut to the console instead of the output text area.\n"
				+ "    --defaultRenderer|-r <classname>:\n"
				+ "        the Renderer specified by <classname> will be used as the default Renderer\n"
				+
				// "    --customStartupClass|-c <classname>:\n" +
				// "        calls <classname>.customSetup( String [] args, <JAlice instance>,\n"
				// +
				// "                  <world instance> )\n" +
				// "        during system initialization\n" +
				"    --help|-h:\n"
				+ "        prints this help message\n"
				+ "\n"
				+ "world:\n"
				+ "    a pathname to a world on disk to be loaded at startup.\n";

		// for the options string:
		// --a lone character has no options
		// --a character preceded by a colon has a required argument
		// --a character preceded by two colons has a non-required argument
		// --if the whole string starts with a colon, then ':' is returned for
		// valid options that do not have their required argument
		gnu.getopt.Getopt g = new gnu.getopt.Getopt("JAlice", args, ":oeh",
				options);
		while ((c = g.getopt()) != -1) {
			switch (c) {
			case 'o': // stdOut to console...
				stdOutToConsole = true;
				break;
			case 'e': // stdErr to console...
				stdErrToConsole = true;
				break;
			case 'r': // default Renderer Class...
				defaultRendererClassname = g.getOptarg();
				break;
			/*
			 * case 'c': //custom Startup class arg = g.getOptarg(); try { Class
			 * cls = Class.forName( arg ); Object [] argValues = { args, f,
			 * f.world }; Class [] argClasses = new Class[argValues.length];
			 * for( int i=0; i<argClasses.length; i++ ) { argClasses[i] =
			 * argValues[i].getClass(); } java.lang.reflect.Method method =
			 * cls.getMethod( "customSetup", argClasses ); method.invoke( null,
			 * argValues ); } catch( Exception e ) { e.printStackTrace(); }
			 * break;
			 */
			case 'h': // help
			case '?':
				System.err.println(helpMessage);
				System.exit(0);
				break;
			default:
				System.err.println("ignoring " + c + " on the command line.");
				break;
			}
		}

		int i = g.getOptind();
		if ((i >= 0) && (i < args.length)) {
			worldToLoad = new java.io.File(args[i]).getAbsoluteFile();
		}
	}

	public static boolean isMainFinished() {
		return mainHasFinished;
	}

	public static void setAliceHomeDirectory(java.io.File file) {
		aliceHomeDirectory = file;
	}

	public static File getAliceHomeDirectory() {

		if( aliceHomeDirectory == null ) {
			if( System.getProperty( "alice.home" ) != null ) {
				setAliceHomeDirectory( new File( System.getProperty( "alice.home" ) ).getAbsoluteFile() );

			} else {

				setAliceHomeDirectory( new File( System.getProperty( "user.dir" ) ).getAbsoluteFile() );

			}
		}

		return aliceHomeDirectory;
	}


	public static void setAliceUserDirectory( File file ) {

		aliceUserDirectory = file;
	}

	public static File getAliceUserDirectory() {
		if (directory != null)
			aliceUserDirectory = new File(directory);
		else if (aliceUserDirectory == null) {
			java.io.File dirFromProperties = null;

			if( System.getProperty( "alice.userDir" ) != null ) {
				dirFromProperties = new File( System.getProperty( "alice.userDir" ) ).getAbsoluteFile();

			}

			File userHome = new File( System.getProperty( "user.home" ) ).getAbsoluteFile();
			File aliceHome = getAliceHomeDirectory();
			File aliceUser = null;

			if (dirFromProperties != null) {
				aliceUser = dirFromProperties;

			} else if( userHome.exists() && userHome.canRead() && userHome.canWrite() ) {
				aliceUser = new File( userHome, ".alice2" );
			} else if( (aliceHome != null) && aliceHome.exists() && aliceHome.canRead() && aliceHome.canWrite() ) {
				aliceUser = new File( aliceHome, ".alice2" );

			}

			if (aliceUser != null) {
				if (aliceUser.exists()) {
					setAliceUserDirectory(aliceUser);
				} else if (aliceUser.mkdir()) {
					setAliceUserDirectory(aliceUser);
				}
			}
		}
		return aliceUserDirectory;
	} 
}
