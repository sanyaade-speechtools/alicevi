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

public class MoveAwayFromAnimation extends AbstractMoveInDirectionOfAnimation {
	public class RuntimeMoveAwayFromAnimation extends RuntimeAbstractMoveInDirectionOfAnimationAnimation {
        protected double getActualAmountValue() {
            return -MoveAwayFromAnimation.this.amount.doubleValue();
        }
        
        /**
		 * Returns the string representation of this object to be used in
		 * vocalizing it to non-seeing users.
		 * 
		 * @return "[subject] moves away from [target]."
		 */
		public String toString() {
			// Declare the return structure for the value
			StringBuilder retVal = new StringBuilder();
			
			// Generate return string
			retVal.append(this.m_subject.getRepr());
			retVal.append(" moves away from ");
			retVal.append(this.m_target.getRepr());
			retVal.append('.');
			
			// Return
			return retVal.toString();
		}
    }
}
