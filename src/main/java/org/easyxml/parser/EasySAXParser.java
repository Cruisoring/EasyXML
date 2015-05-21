/*
 * Copyright (C) 2014 William JIANG
 * Created on May 18, 2015
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.easyxml.parser;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringUtils;
import org.easyxml.xml.Document;
import org.easyxml.xml.Element;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

/*
 *    Created on May 18, 2015 by William JIANG
 */
public class EasySAXParser extends DefaultHandler2 {

    public static final String DefaultElementPathSign = ">";

    /**
     * 
     * Parse a simple XML document represented as a String.
     * 
     * @param xml
     *            - String content of the XML document.
     * 
     * @return org.easyxml.xml.Document instance if it is parsed successfully,
     *         otherwise null.
     */

    public static Document parseText(String xml) {

	InputSource is = new InputSource(new StringReader(xml));

	return parse(is);
    }

    /**
     * 
     * Parse XML as an InputSource. If external schema is referred, then it
     * would fail to locate the DTD file.
     * 
     * @param is
     *            - InputSource to be parsed.
     * 
     * @return org.easyxml.xml.Document instance if it is parsed successfully,
     *         otherwise null.
     */
    public static Document parse(InputSource is) {

	SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
	try {
	    saxParserFactory.setValidating(true);
	    saxParserFactory.setFeature("http://apache.org/xml/features/validation/schema", true);
	    SAXParser saxParser = saxParserFactory.newSAXParser();
	    EasySAXParser handler = new EasySAXParser();
	    saxParser.parse(is, handler);
	    return handler.getDocument();
	} catch (SAXException | IOException | ParserConfigurationException e) {
	    e.printStackTrace();
	}

	return null;
    }

    /**
     * 
     * Parse XML file specified by the url. If external schema is referred, then
     * it must be located at the same directory of the XML.
     * 
     * @param url
     *            - URL of the XML file to be parsed.
     * 
     * @return org.easyxml.xml.Document instance if it is parsed successfully,
     *         otherwise null.
     */
    public static Document parse(URL url) {
	SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
	try {
	    SAXParser saxParser = saxParserFactory.newSAXParser();
	    EasySAXParser handler = new EasySAXParser();
	    String path = url.getFile();
	    saxParser.parse(path, handler);

	    return handler.getDocument();
	} catch (SAXException | IOException | ParserConfigurationException e) {
	    e.printStackTrace();
	}
	return null;
    }

    private String path = null;
    private StringBuilder innerText = null;
    private Document easyDocument = null;
    private Element lastContainer = null;
    private Element currentElement = null;
    
    public Document getDocument() {
	return easyDocument;
    }

    @Override
    public void startDocument() throws SAXException {
	super.startDocument();
	path = "";
	innerText = new StringBuilder();
    }

    @Override
    public void endDocument() throws SAXException {
	super.endDocument();
	if (!StringUtils.isBlank(path))
	    throw new SAXException("There are still some path unsolved: " + path);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) 
	    throws SAXException {

	if (easyDocument == null) {
	    easyDocument = new Document(qName);
	    currentElement = easyDocument;
	    path = qName;
	} else {
	    currentElement = new Element(qName, lastContainer);
	    path += Element.DefaultElementPathSign + qName;
	}

	innerText.setLength(0);
	lastContainer = currentElement;

	int length = attributes.getLength();
	for (int i = 0; i < length; i++) {
	    String name = attributes.getQName(i);
	    String value = attributes.getValue(i);
	    currentElement.addAttribute(name, value);
	}
    }

    @Override
    public void endElement(String uri, String localName, String qName)
	    throws SAXException {

	int lastSignPos = path.lastIndexOf(Element.DefaultElementPathSign);
	if (!path.endsWith(qName)) {
	    String lastTag = path.substring(lastSignPos + 1);
	    throw new SAXException("Now the expected endElement is </" + lastTag + ">, instead of: " + qName);
	}

	// Remove the tag from path
	path = path.substring(0, lastSignPos == -1 ? 0 : lastSignPos);
	if (innerText.length() > 0) {
	    currentElement.setValue(innerText.toString());
	    // Reset the StringBuilder
	    innerText.setLength(0);
	}

	lastContainer = lastContainer.getParent();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

	String newText = new String(ch, start, length);
	currentElement.appendValue(newText);
    }
}
