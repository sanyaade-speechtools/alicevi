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

package edu.cmu.cs.stage3.alice.core.decorator;

import edu.cmu.cs.stage3.alice.core.Decorator;
import edu.cmu.cs.stage3.alice.core.ReferenceFrame;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.alice.scenegraph.Vertex3d;
import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.alice.scenegraph.LineArray;
import javax.vecmath.Vector3d;
import edu.cmu.cs.stage3.math.MathUtilities;
import javax.vecmath.Point3d;

public class PivotDecorator extends Decorator {
	private LineArray m_sgLineArray = null;
	private Transformable m_transformable = null;
	private Box m_overrideBoundingBox = null;

	protected ReferenceFrame getReferenceFrame() {
		return getTransformable();
	}

	public Transformable getTransformable() {
		return m_transformable;
	}

	public void setTransformable(Transformable transformable) {
		if (transformable != m_transformable) {
			m_transformable = transformable;
			markDirty();
			updateIfShowing();
		}
	}

	public Box getOverrideBoundingBox() {
		return m_overrideBoundingBox;
	}

	public void setOverrideBoundingBox(Box overrideBoundingBox) {
		m_overrideBoundingBox = overrideBoundingBox;
		markDirty();
	}

	public void internalRelease(int pass) {
		switch (pass) {
		case 2:
			if (m_sgLineArray != null) {
				m_sgLineArray.release();
				m_sgLineArray = null;
			}
			break;
		}
		super.internalRelease(pass);
	}

	protected void update() {
		super.update();

		Box box;
		if (m_overrideBoundingBox == null) {
			box = m_transformable.getBoundingBox();
		} else {
			box = m_overrideBoundingBox;
		}
		if (box == null || box.getMinimum() == null || box.getMaximum() == null) {
			return;
		}

		boolean requiresVerticesToBeUpdated = isDirty();
		if (m_sgLineArray == null) {
			m_sgLineArray = new LineArray();
			m_sgVisual.setGeometry(m_sgLineArray);
			m_sgLineArray.setBonus(getTransformable());
			requiresVerticesToBeUpdated = true;
		}
		if (requiresVerticesToBeUpdated) {
			Vector3d min = box.getMinimum();
			Vector3d max = box.getMaximum();
			double distanceAcross = MathUtilities.subtract(max, min).length();
			double delta = distanceAcross * 0.1;
			
			// Pivot Line Colors are set here
			// Brett Snare RIT
			Color xColor = Color.RED;
			Color yColor = Color.GREEN;
			Color zColor = Color.BLUE;
			// End Pivot Line Colors
			
			Vertex3d[] vertices = new Vertex3d[6];
			vertices[0] = new Vertex3d(new Point3d(0, 0, 0), null, xColor,
					null, null);
			vertices[1] = new Vertex3d(new Point3d(max.x + delta, 0, 0), null,
					xColor, null, null);
			vertices[2] = new Vertex3d(new Point3d(0, 0, 0), null, yColor,
					null, null);
			vertices[3] = new Vertex3d(new Point3d(0, max.y + delta, 0), null,
					yColor, null, null);
			vertices[4] = new Vertex3d(new Point3d(0, 0, 0), null, zColor,
					null, null);
			vertices[5] = new Vertex3d(new Point3d(0, 0, max.z + delta), null,
					zColor, null, null);
			m_sgLineArray.setVertices(vertices);
		}
		setIsDirty(false);
	}
}
