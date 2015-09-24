package com.kelltontech.parser;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


public abstract class AbstractSAXParser extends DefaultHandler implements IParser {
	
	protected StringBuffer sb = null;
	protected String parent;

	protected SAXParserFactory factory;
	protected SAXParser saxParser;

	public void init() throws ParserConfigurationException, SAXException {
		factory = SAXParserFactory.newInstance();
		saxParser = factory.newSAXParser();
	}

	//abstract methods
	public abstract Object serialize(byte[] response) throws Exception;
	
	public abstract void onStartElement(String namespaceURI, String localName,String qName, Attributes atts) throws SAXException,IllegalArgumentException;

	public abstract void onEndElement(String namespaceURI, String localName, String qName) throws SAXException;

	//public methods.
	@Override
	public void warning(SAXParseException e) {
		System.err.println("warning:" + e.getMessage());
	}

	@Override
	public void error(SAXParseException e) {
		System.err.println("error:" + e.getMessage());
	}

	@Override
	public void fatalError(SAXParseException e) {
		System.err.println("fatalError:" + e.getMessage());
	}

	@Override
	public void startDocument() throws SAXException {
		sb = new StringBuffer();
		parent = "";
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}

	@Override
	public void startElement(String namespaceURI, String localName,String qName, Attributes atts) throws SAXException,
	IllegalArgumentException {

		//call to abstract method
		onStartElement(namespaceURI, localName, qName, atts);

		sb = new StringBuffer();

		// Keep the trace of the parent nodes
		if (parent.length() > 0) {
			parent = parent.concat(",");
		}
		parent = parent.concat(localName);
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		// remove the end element from stack of parent elements
		int index = parent.lastIndexOf(',');
		if (index > 0) {
			parent = parent.substring(0, index);
		}
		onEndElement(namespaceURI, localName, qName);
	}

	@Override
	public void characters(char ch[], int start, int length) {
		String theString = new String(ch, start, length);
		sb.append(theString);
	}
}
