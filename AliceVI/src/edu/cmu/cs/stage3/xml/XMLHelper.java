package edu.cmu.cs.stage3.xml;



import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
public class XMLHelper {

	//No generics
	private static  ArrayList<Style> customStyles;
	private static DocumentBuilderFactory dbf;
	private static DocumentBuilder db ;
	//String fileName;
	
	private static  Document dom;

	public XMLHelper(){
		//this.fileName = fileName;
		initialize();
	}

	private static  void initialize(){
		//get the factory
		customStyles = new ArrayList<Style>();
		dbf = DocumentBuilderFactory.newInstance();
		try {	
			//Using factory get an instance of document builder
			db = dbf.newDocumentBuilder();
			
			//parse using builder to get DOM representation of the XML file
			dom = db.parse("resources/Styles.xml");
			
			

		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
	}

	
	private  static void loadStylesToList(){
		
		
		//get the root elememt
		Element docEle = dom.getDocumentElement();
		
		//get a nodelist of <employee> elements
		NodeList styleList = docEle.getElementsByTagName("style");
		if(styleList != null && styleList.getLength() > 0) {
			for(int i = 0 ; i < styleList.getLength();i++) {
				
				//get the employee element
				Element styleElement = (Element)styleList.item(i);
				
				//get the Employee object
				Style tempStyle = getStyle(styleElement);
				
				//add it to list
				customStyles.add(tempStyle);
			}
			
			
		}
	}


	/**
	 * I take an employee element and read the values in, create
	 * an Employee object and return it
	 * @param empEl
	 * @return
	 */
	private static  Style getStyle(Element styleEl) {
		
		//for each <style> element get text or int values of 
		//name ,fontSize, and fontColor
		
		String name = getTextValue(styleEl,"name");
		
		// get font properties
		NodeList fontList = styleEl.getElementsByTagName("font");
		Element fontElement = (Element)fontList.item(0);
		String size = getTextValue(fontElement,"size");	
		String colorString =getTextValue(fontElement,"color"); 
			
		StringTokenizer tok = new StringTokenizer(colorString , ",");
		int r= Integer.parseInt(tok.nextToken(), 16);
		int  b= Integer.parseInt(tok.nextToken(), 16);
		int g= Integer.parseInt(tok.nextToken(), 16);

		//Create a new Employee with the value read from the xml nodes
		Style style = new Style(name,size, new Color(r,g,b), new Color(r,g,b));
		
		return style;
	}



	private static String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}
	
	private static Element createStyleElement(Style style){

		Element styleEle = dom.createElement("style");
		//styleEle.setAttribute("Subject", style.getName());

		//create name and attach it to the style element
		Element nameEle = dom.createElement("name");
		Text nameText = dom.createTextNode(style.getName());
		nameEle.appendChild(nameText);
		styleEle.appendChild(nameEle);

		//create font element and attach it to the style element
		Element fontEle = dom.createElement("font");
		
		Element sizeEle = dom.createElement("size");
		Text sizeText = dom.createTextNode(style.getFontSize());
		sizeEle.appendChild(sizeText);
		fontEle.appendChild(sizeEle);
		
		
		Element colorEle = dom.createElement("color");
		Color color = style.getForgroundColor();
		//Text colorText = dom.createTextNode(""+color.getRed()+","+color.getGreen()+","+color.getBlue());
		String r= Integer.toHexString(color.getRed());
		String g= Integer.toHexString(color.getGreen());
		String b= Integer.toHexString(color.getBlue());
		
		System.out.println("font clr "+color);
		System.out.println("rgb clr "+r+","+g+","+b);
		
		Text colorText = dom.createTextNode("ff,ff,ff");
		colorEle.appendChild(colorText);
		fontEle.appendChild(colorEle);
		
		styleEle.appendChild(fontEle);
		

		return styleEle;

	}


	private static void writeTreeToXMLFile(){
		
		try{
			Source source = new DOMSource(dom);
			
			File file = new File("resources/Styles.xml");
			Result result = new StreamResult(file);
			
			
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			
			xformer.transform(source, result);
			System.out.println("End of write method");
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

	
	
	public static ArrayList<String> getStyleNameList(){
		ArrayList<String> namesList = new ArrayList<String>();
		initialize();
		loadStylesToList();
		//printData();
		
		
		
		//get the root elememt
		Element docEle = dom.getDocumentElement();
		
		//get a nodelist of <employee> elements
		NodeList styleList = docEle.getElementsByTagName("style");
		if(styleList != null && styleList.getLength() > 0) {
			for(int i = 0 ; i < styleList.getLength();i++) {
				
				//get the employee element
				Element styleElement = (Element)styleList.item(i);
				
				//get the Employee object
				Style tempStyle = getStyle(styleElement);
				
				//add it to list
				namesList.add(tempStyle.getName());
			}
			
			
		}
		return namesList;
	}
	
	public static ArrayList<String> getFontSizeList(){
		ArrayList<String> fontList = new ArrayList<String>();
		DocumentBuilderFactory dbfFont = null;
		DocumentBuilder dbFont = null ;
		Document domFont= null;
		
		try {
			dbfFont =DocumentBuilderFactory.newInstance();
			String fontSizeFileName="resources/FontSizes.xml";
		
		
		
			
			//Using factory get an instance of document builder
			dbFont = dbfFont.newDocumentBuilder();
			
			//parse using builder to get DOM representation of the XML file
			domFont = dbFont.parse(fontSizeFileName);
			

		
		
		//get the root elememt
		Element docEle = domFont.getDocumentElement();
		
		//get a nodelist of <sizes> elements
		NodeList sizeList = docEle.getElementsByTagName("size");
		if(sizeList != null && sizeList.getLength() > 0) {
			for(int i = 0 ; i < sizeList.getLength();i++) {
				
				//get the employee element
				Element sizeElement = (Element)sizeList.item(i);
				fontList.add(sizeElement.getTextContent());
			}
			
			
		}
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
		return fontList;
		
	}
	public static void saveStyle(String styleName, String fontSize, Color forgroundColor ,Color backgroundColor){
		
		Style style = new Style(styleName,  fontSize,  forgroundColor,backgroundColor );
		/* initialize();
		 loadStylesToList();
		 customStyles.add(style);*/
		
		//get the root elememt
		Element docEle = dom.getDocumentElement();
		
	   Node rootNode = dom.getDocumentElement();
		 
	   rootNode.appendChild(createStyleElement(style));
		 
		 writeTreeToXMLFile();
		
		
	}
	
	public static Style getStyleByName(String styleName){
		initialize();
		loadStylesToList();
		
		//get the root elememt
		Element docEle = dom.getDocumentElement();
		
		//get a nodelist of <employee> elements
		NodeList styleList = docEle.getElementsByTagName("style");
		if(styleList != null && styleList.getLength() > 0) {
			for(int i = 0 ; i < styleList.getLength();i++) {
				
				//get the employee element
				Element styleElement = (Element)styleList.item(i);
				
				//get the Employee object
				Style tempStyle = getStyle(styleElement);
				if(tempStyle.getName().equals(styleName)){
					System.out.println("FOUND: \n"+tempStyle);
					return tempStyle;
				}
			}
			
			
		}
		return null;
	}

	
	public static void main(String[] args){
		XMLHelper helper = new XMLHelper();
		helper.writeTreeToXMLFile();
		System.out.println(helper.getStyleNameList());
		System.out.println(XMLHelper.getFontSizeList());
	}

}

