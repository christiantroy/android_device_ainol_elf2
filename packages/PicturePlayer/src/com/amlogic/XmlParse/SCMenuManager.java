package com.amlogic.XmlParse;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



public class SCMenuManager extends DefaultHandler {
	private TreeNode<SCMenuItem> scMenuRoot;
	private String _sceneName;
	private TreeNode<SCMenuItem> currentMenu;
	private List<TreeNode<SCMenuItem>> parentStack;
	public SCMenuManager(InputStream im, String sceneName)
	{
		scMenuRoot = new TreeNode<SCMenuItem>(null);
		currentMenu = scMenuRoot;
		_sceneName = sceneName;
		parentStack = new ArrayList<TreeNode<SCMenuItem>>();
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = spf.newSAXParser();
			saxParser.parse(im, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * implements xml parser handler
	 */
//	@Override
	public void startDocument() throws SAXException 
	{ 
	}

//	@Override
	public void endDocument() throws SAXException 
	{ 
	}
	public TreeNode<SCMenuItem> getMenuRoot()
	{
		return scMenuRoot;
	}
//	@Override
	public void startElement(String uri, String localName, String qName, 
		Attributes attributes) throws SAXException {
		if (qName == "SCMenu")
		{
			parentStack.add(currentMenu);
			if (attributes.getValue("Scenes").contains(_sceneName))
			{
				TreeNode<SCMenuItem> node = new TreeNode<SCMenuItem>(currentMenu);
				SCMenuItem item = new SCMenuItem();
				item.id = attributes.getValue("ID");
				item.valueType = attributes.getValue("ValueType");
				item.uiType = attributes.getValue("UIType");
				item.value = attributes.getValue("Value"); 
				node.attachData(item);
				currentMenu = node;
			}
		}

	}
//	@Override
	public void endElement(String uri, String localName, String qName) 
	throws SAXException 
	{ 
		if (qName == "SCMenu")
		{
			currentMenu = parentStack.remove(parentStack.size() -1);
		}
	}
}
