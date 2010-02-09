package edu.cmu.cs.stage3.xml;

import java.awt.Color;

public class Style {
	String name;
	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getFontSize() {
		return fontSize;
	}



	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}



	public Color getForgroundColor() {
		return forgroundColor;
	}



	public void setForgroundColor(Color forgroundColor) {
		this.forgroundColor = forgroundColor;
	}



	public Color getBackgroundColor() {
		return backgroundColor;
	}



	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}



	String fontSize;
	Color forgroundColor;
	Color backgroundColor;
		
	public Style(String name, String fontSize, Color forgroundColor, Color backgroundColor){
		this.name = name;
		this.fontSize = fontSize;
		this.forgroundColor = forgroundColor;
	}


	
	public String toString(){
		String toReturn = "";
		toReturn +="Name is: "+ this.name;
		toReturn +="\n font size is: "+ this.fontSize;
		toReturn += "\n foreground color is: "+ this.forgroundColor;
		return toReturn;
	}

}
