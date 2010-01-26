package edu.cmu.cs.stage3.alice.authoringtool;


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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import edu.cmu.cs.stage3.alice.core.Element;

public abstract class AbstractImporter implements Importer {
	private Object location = null;
	protected String plainName = null;

	public abstract Map<?, ?> getExtensionMap();

	public Element load( String filename ) throws IOException {
		location = new File( filename ).getParentFile();
		String fullName = new File( filename ).getName();
		plainName = fullName.substring( 0, fullName.indexOf( '.' ) );
		if( Element.isPotentialNameValid( plainName ) ) {
			//pass
		} else {
			plainName = Element.generateValidName( plainName );
		}
		FileInputStream fis = new FileInputStream( filename );
		Element castMember = load( fis, getExtension( filename ) );
		fis.close();
		plainName = null;
		return castMember;
	}

	public Element load( File file ) throws IOException {
		location = file.getParentFile();
		String fullName = file.getName();
		plainName = fullName.substring( 0, fullName.indexOf( '.' ) );
		if( Element.isPotentialNameValid( plainName ) ) {
			//pass
		} else {
			plainName = Element.generateValidName( plainName );
		}
		FileInputStream fis = new FileInputStream( file );
		Element castMember = load( fis, getExtension( file.getName() ) );
		fis.close();
		plainName = null;
		return castMember;
	}

	public Element load( URL url ) throws IOException {
		String externalForm = url.toExternalForm();
		location = new URL( externalForm.substring( 0, externalForm.lastIndexOf( '/' ) + 1 ) );
		String fullName = externalForm.substring( externalForm.lastIndexOf( '/' ) + 1 );
		plainName = fullName.substring( 0, fullName.lastIndexOf( '.' ) );
		if( Element.isPotentialNameValid( plainName ) ) {
			//pass
		} else {
			plainName = Element.generateValidName( plainName );
		}
		Element castMember = load( url.openStream(), getExtension( url.getFile() ) );
		plainName = null;
		return castMember;
	}

	protected abstract Element load( InputStream istream, String ext ) throws IOException;

	public Object getLocation() {
		return location;
	}

	private String getExtension( String filename ) {
		if( filename == null ) {
			throw new IllegalArgumentException( "null filename encountered" );
		}
		filename.trim();
		int i = filename.lastIndexOf( "." );
		if( i == -1 ) {
			throw new IllegalArgumentException( "unable to determine the extension of " + filename );
		}
		String ext = filename.substring( i + 1 );
		if( ext.length() < 1 ) {
			throw new IllegalArgumentException( "unable to determine the extension of " + filename );
		}
		ext = ext.toUpperCase();
		if( getExtensionMap().get( ext ) == null ) {
			throw new IllegalArgumentException( ext + " files are not supported by this Importer" );
		}
		return ext;
	}
}