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

package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;

public class Matrix33Property extends ObjectProperty {
	public Matrix33Property( Element owner, String name, javax.vecmath.Matrix3d defaultValue ) {
		super( owner, name, defaultValue, javax.vecmath.Matrix3d.class );
	}
	public javax.vecmath.Matrix3d getMatrix3dValue() {
		return (javax.vecmath.Matrix3d)getValue();
	}
	public edu.cmu.cs.stage3.math.Matrix33 getMatrix33Value() {
		javax.vecmath.Matrix3d m = getMatrix3dValue();
		if( m == null || m instanceof edu.cmu.cs.stage3.math.Matrix33 ) {
			return (edu.cmu.cs.stage3.math.Matrix33)m;
		} else {
			return new edu.cmu.cs.stage3.math.Matrix33( m );
		}
	}
    protected void decodeObject( org.w3c.dom.Element node, edu.cmu.cs.stage3.io.DirectoryTreeLoader loader, java.util.Vector referencesToBeResolved, double version ) throws java.io.IOException {
        javax.vecmath.Matrix3d m = new javax.vecmath.Matrix3d();
        org.w3c.dom.NodeList rowNodeList = node.getElementsByTagName( "row" );
        for( int i=0; i<3; i++ ) {
            org.w3c.dom.Element rowNode = (org.w3c.dom.Element)rowNodeList.item( i );
            org.w3c.dom.NodeList itemNodeList = rowNode.getElementsByTagName( "item" );
            for( int j=0; j<3; j++ ) {
                org.w3c.dom.Node itemNode = itemNodeList.item( j );
                m.setElement( i, j, Double.parseDouble( getNodeText( itemNode ) ) );
            }
        }
        set( m );
    }
    protected void encodeObject( org.w3c.dom.Document document, org.w3c.dom.Element node, edu.cmu.cs.stage3.io.DirectoryTreeStorer storer, edu.cmu.cs.stage3.alice.core.ReferenceGenerator referenceGenerator ) throws java.io.IOException {
        javax.vecmath.Matrix3d m = getMatrix33Value();
        for( int rowIndex=0; rowIndex<3; rowIndex++ ) {
            org.w3c.dom.Element rowNode = document.createElement( "row" );
            //rowNode.setAttribute( "index", Integer.toString( rowIndex ) );
            for( int colIndex=0; colIndex<3; colIndex++ ) {
                org.w3c.dom.Element itemNode = document.createElement( "item" );
                //itemNode.setAttribute( "index", Integer.toString( colIndex ) );
                itemNode.appendChild( createNodeForString( document, Double.toString( m.getElement( rowIndex, colIndex ) ) ) );
                rowNode.appendChild( itemNode );
            }
            node.appendChild( rowNode );
        }
    }
}
