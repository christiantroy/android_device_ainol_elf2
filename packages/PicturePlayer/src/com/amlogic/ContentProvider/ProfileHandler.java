package com.amlogic.ContentProvider;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class ProfileHandler extends DefaultHandler {

	public List<ProfileItem> Pitems;
	public ProfileHandler(InputStream is)
	{
		Pitems=new ArrayList<ProfileItem>();
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
	
	
	
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (qName == "Data"){
			ProfileItem item=new ProfileItem();
			item.name=attributes.getValue("ID");
			item.value=attributes.getValue("Value");
			item.comment=attributes.getValue("Comment");
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
