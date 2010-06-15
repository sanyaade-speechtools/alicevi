package edu.cmu.cs.stage3.alice.authoringtool;

import junit.framework.TestCase;

public class TestJAlice extends TestCase 
{
	public void testBackgroundColor()
	{
		assertEquals("edu.cmu.cs.stage3.alice.scenegraph.Color[r=0.0,g=0.30588236,b=0.59607846,a=1.0]", 
				JAlice.buildBackgroundColor("0,78,152"));
	}
	
	public void testVersion()
	{
		assertEquals("2.2  8/1/2009", JAlice.initVersion());
	}
}
