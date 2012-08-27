package com.amlogic.XmlParse;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class StringManager extends DefaultHandler {

	public List<StringItem> Pitems;
	public StringManager(InputStream is)
	{
		Pitems=new ArrayList<StringItem>();
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = spf.newSAXParser();
			saxParser.parse(is, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
	}
	
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}
	
	public List<StringItem> getStringItems(){
		return Pitems;
	}
	
	
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (qName == "string"){
			StringItem item=new StringItem();
			item.name=attributes.getValue("name");
			item.value=attributes.getValue("value");
			Pitems.add(item);
		}

	}
	
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
	}
	
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		new String(ch, start, length);

	}
	
}
