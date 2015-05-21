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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.easyxml.xml.Document;
import org.easyxml.xml.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/*
 *    Created on May 18, 2015 by William JIANG
 */
public class DomParser {
    
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
     * Parse InputSource to generate a org.easyxml.xml.Document instance.
     * @param is - InputSource of the XML document.
     * @return An org.easyxml.xml.Document instance if parsing is success, or null if failed.
     */
    public static Document parse(InputSource is) {

	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	try {

	    DocumentBuilder dBuilder = factory.newDocumentBuilder();
	    org.w3c.dom.Document doc = dBuilder.parse(is);
	    org.w3c.dom.Element root = doc.getDocumentElement();
	    NodeList children = root.getChildNodes();
	    NamedNodeMap attributes = root.getAttributes();
	    Document easyDocument = new Document(root.getNodeName());
	    traverse(easyDocument, children, attributes);
	    return easyDocument;
	} catch (SAXException | IOException | ParserConfigurationException e) {
	    e.printStackTrace();
	}
	return null;
    }

    /**
     * Parse and XML file to get a org.easyxml.xml.Document instance.
     * Notice that if DTD is used, it shall be placed on the same folder as the XML file.
     * @param url - URL of the XML resource file.
     * @return An org.easyxml.xml.Document instance if parsing is success, or null if failed.
     */
    public static Document parse(URL url) {

	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	factory.setValidating(true);

	try {
	    DocumentBuilder dBuilder = factory.newDocumentBuilder();
	    String path = url.getFile();
	    org.w3c.dom.Document doc = dBuilder.parse(path);
	    org.w3c.dom.Element root = doc.getDocumentElement();
	    NodeList children = root.getChildNodes();
	    NamedNodeMap attributes = root.getAttributes();
	    Document easyDocument = new Document(root.getNodeName());
	    traverse(easyDocument, children, attributes);
	    return easyDocument;
	} catch (SAXException | IOException | ParserConfigurationException e) {
	    e.printStackTrace();
	}
	return null;
    }

    /**
     * Traverse one element of the DOM tree to generate an org.easyxml.xml.Element accordingly.
     * @param container - The target org.easyxml.xml.Element.
     * @param children - NodeList from the DOM parser.
     * @param attributes - Attributes from the DOM parser.
     * @return The org.easyxml.xml.Element after appending attributes and child elements.
     * @throws SAXException
     */
    private static Element traverse(Element container, NodeList children,
	    NamedNodeMap attributes) throws SAXException {

	int length = attributes.getLength();
	for (int i = 0; i < length; i++) {
	    Node attribute = attributes.item(i);
	    container.addAttribute(attribute.getNodeName(), attribute.getNodeValue());
	}

	if (children == null)
	    return container;

	length = children.getLength();
	for (int i = 0; i < length; i++) {
	    Node child = children.item(i);
	    String name = child.getNodeName();
	    String value = child.getNodeValue();
	    switch (child.getNodeType()) {
	    case Node.ELEMENT_NODE:
		org.w3c.dom.Element domElement = (org.w3c.dom.Element) child;
		NamedNodeMap elementAttributes = domElement.getAttributes();
		NodeList list = domElement.getChildNodes();
		Element elt = new Element(name, container);
		traverse(elt, list, elementAttributes);
		break;

	    case Node.TEXT_NODE:
		container.appendValue(value);
		break;

	    default:
		break;
	    }
	}
	return container;
    }
}
