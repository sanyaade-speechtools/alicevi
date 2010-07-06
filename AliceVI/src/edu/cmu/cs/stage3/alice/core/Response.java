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

// The package we're part of
package edu.cmu.cs.stage3.alice.core;

// Imports to check specific property changes
import java.beans.PropertyVetoException;

// Import for numeric properties for responses
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;

// Imports for speech support
import java.util.Locale;
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
 * Response - An object which encapsulates the behavior of animated /
 *            programmable objects within Alice. These are evaluated
 *            at runtime to give objects their unique behaviors, from
 *            moving to turning, speaking, and the like.
 */
public abstract class Response extends Code {
	// The static synthesizer for voicing out a response during runtime
	public static Synthesizer Synth;
	
	// The duration that the response lasts during runtime
	public final NumberProperty duration = new NumberProperty( this, "duration", getDefaultDuration() );
	
	/**
	 * Initialize the voice synthesizer for the responses to the system statically,
	 * making it accessible to all responses.
	 */
	static {
		SynthesizerModeDesc desc = new SynthesizerModeDesc(
                null,          // engine name
                "general",     // mode name
                Locale.US,     // locale
                null,          // running
                null);         // voice
		
		try {
			Synth = Central.createSynthesizer(desc);
			Synth.allocate();
			Synth.resume();
			desc = (SynthesizerModeDesc)Synth.getEngineModeDesc();
			Voice[] voices = desc.getVoices();
			Synth.getSynthesizerProperties().setVoice(voices[1]);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (EngineException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		} catch (AudioException e) {
			e.printStackTrace();
		} catch (EngineStateError e) {
			e.printStackTrace();
		}
	}
	
	protected Number getDefaultDuration() {
		return new Double( 1 );
	}

	protected Class getRuntimeResponseClass() {
		Class cls = getClass();
		Class[] declaredClasses = cls.getDeclaredClasses();
		for( int i=0; i<declaredClasses.length; i++ ) {
			if( RuntimeResponse.class.isAssignableFrom( declaredClasses[i] ) ) {
				return declaredClasses[i];
			}
		}
		return null;
	}
	public RuntimeResponse manufactureRuntimeResponse() {
		Class runtimeResponseClass = getRuntimeResponseClass();
		if( runtimeResponseClass!=null ) {
			java.lang.reflect.Constructor[] constructors = runtimeResponseClass.getConstructors();
			if( constructors.length>0 ) {
				try {
					Object[] initargs = { this };
					RuntimeResponse runtimeResponse = (RuntimeResponse)constructors[0].newInstance( initargs );
					return runtimeResponse;
				} catch( IllegalAccessException iae ) {
					throw new ExceptionWrapper( iae, null );
				} catch( InstantiationException ie ) {
					throw new ExceptionWrapper( ie, null );
				} catch( java.lang.reflect.InvocationTargetException ite ) {
					throw new ExceptionWrapper( ite, null ); //todo? ite.getTargetException()
				}
			}
		}
		return null;
	}

	public abstract class RuntimeResponse {
		private boolean HACK_m_isMarkedForRemoval = false;
		public void HACK_markForRemoval() {
			HACK_m_isMarkedForRemoval = true;
		}
		public boolean HACK_isMarkedForRemoval() {
			return HACK_m_isMarkedForRemoval;
		}

		private boolean m_isActive = false;
		private double m_t0 = Double.NaN;
		private double m_tPrev = Double.NaN;
		private double m_duration = Double.NaN;
		private double m_dt = Double.NaN;

		public boolean isActive() {
			return m_isActive;
		}
		protected double getDuration() {
			return m_duration;
		}
		protected void setDuration( double duration ) {
			m_duration = duration;
		}
		protected double getTimeElapsed( double t ) {
			return t-m_t0;
		}
		protected double getDT() {
			return m_dt;
		}
		public double getTimeRemaining( double t ) {
			return m_duration - getTimeElapsed( t );
		}
		public void prologue( double t ) {
			m_t0 = t;
			m_tPrev = t;
			m_dt = 0;
			m_duration = Response.this.duration.doubleValue( Double.NaN );
			m_isActive = true;
		}
		public void update( double t ) {
			m_dt = t - m_tPrev;
			m_tPrev = t;
		}
		public void epilogue( double t ) {
			m_isActive = false;
			
			// TODO: Print Out / Say what the response is doing.
			//       Need some form of text-to-audio converter.
			Synth.speakPlainText(this.toString(), null);
		}
		public void stop( double t ) {
			if( isActive() ) {
				epilogue( t );
			}
		}
		public void finish() {
			m_t0 = Double.NEGATIVE_INFINITY;
		}

		protected edu.cmu.cs.stage3.alice.core.Behavior getCurrentBehavior() {
			edu.cmu.cs.stage3.alice.core.World world = Response.this.getWorld();
			if( world != null ) {
				edu.cmu.cs.stage3.alice.core.Sandbox sandbox = world.getCurrentSandbox();
				if( sandbox != null ) {
					return sandbox.getCurrentBehavior();
				}
			}
			return null;
		}

		protected java.util.Stack getCurrentStack() {
			edu.cmu.cs.stage3.alice.core.Behavior behavior = getCurrentBehavior();
			if( behavior != null ) {
				return behavior.getCurrentStack();
			} else {
				return null;				
			}
		}

		public String toString() {
			return "";//this.getClass().getSimpleName();
		}
	}
}
