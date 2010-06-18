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

package edu.cmu.cs.stage3.alice.core.response;

//TODO: Figure out if there are any models that accurately use this
public class ThinkAnimation extends AbstractBubbleAnimation {
    public class RuntimeThinkAnimation extends RuntimeAbstractBubbleAnimation {
		protected edu.cmu.cs.stage3.alice.core.bubble.Bubble createBubble() {
			return new edu.cmu.cs.stage3.alice.core.bubble.ThoughtBubble();
		}
		
		/**
		 * Returns the string representation of this object to be used in
		 * vocalizing it to non-seeing users.
		 * 
		 * @return "[subject] thinks \"[thought]\"."
		 */
		public String toString() {
			// Declare the return structure for the value
			StringBuilder retVal = new StringBuilder();
			
			// Generate return string "[subject] thinks \"[thought]\"."
			retVal.append(subject.getOwner().getRepr());
			retVal.append(" thinks \"");
			retVal.append(what.getStringValue());
			retVal.append("\".");
			
			// Return
			return retVal.toString();
		}
    }
}