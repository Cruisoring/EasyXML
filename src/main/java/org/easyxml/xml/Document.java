/*
 * Created on May 15, 2015
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
package org.easyxml.xml;

import java.nio.charset.Charset;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

/**
 * 
 * Utility to support XML Document based on Element class.
 * 
 * @author William JIANG
 * 
 *         15 May 2015
 * 
 * 
 * 
 * @version $Id$
 */

public class Document extends Element {

    // Constants for XML Declaration composing

    public static final Charset DefaultCharset = Charset.forName("UTF-8");

    private static final String DefaultDeclarationFormat = "<?xml version=\"1.0\" encoding=\"%s\"?>\n";

    // Usually we have only one element for all valid children element, then it
    // could be specified as this to facilitate most operations.

    protected Element defaultContainer = null;

    public Element getDefaultContainer() {

	return defaultContainer;

    }

    public Document(String name) {

	super(name);

    }

    /**
     * 
     * Set the DefaultContainer
     * 
     * @param container
     *            - The Element to be regarded as DefaultContainer.
     */

    protected void setDefaultContainer(Element container) {

	try {

	    if (container == null) {

		throw new InvalidParameterException(
			"The container is not valid!");

	    }

	    // Iterate through the DOM tree to make sure the container is a
	    // descendant of this XmlDocument

	    if (container != this) {

		Element parent = container;

		do {

		    parent = parent.getParent();

		    if (parent == this)

			break;

		    else if (parent == null)

			throw new InvalidParameterException(
				"The root of the element must be this XmlDocument !");

		} while (parent != null);

		this.defaultContainer = container;

	    }

	} catch (InvalidParameterException e) {

	    e.printStackTrace();

	}

    }

    public Boolean setDefaultContainerByPath(String path) {

	List<Element> elements = getElementsOf(path);

	if (elements == null || elements.size() != 1)

	    return false;

	setDefaultContainer(elements.get(0));

	return true;

    }

    /**
     * 
     * If there is a solid defaultContainer, then the child would be appended to
     * it directly.
     * 
     * {@inheritDoc}
     * 
     * 
     * 
     * @see org.easyxml.xml.Element#addChildElement(org.easyxml.xml.Element)
     */

    @Override
    public Element addChildElement(Element child) {

	if (defaultContainer != null)

	    defaultContainer.addChildElement(child);

	else

	    super.addChildElement(child);

	return this;

    }

    /**
     * 
     * If there is a solid defaultContainer, then the attribute would be
     * appended to it directly.
     * 
     * {@inheritDoc}
     * 
     * 
     * 
     * @see org.easyxml.xml.Element#addAttribute(java.lang.String,
     *      java.lang.String)
     */

    @Override
    public Element addAttribute(String name, String value) {

	try {

	    if (defaultContainer != null) {

		defaultContainer.addAttribute(name, value);

	    } else {

		super.addAttribute(name, value);

	    }

	} catch (SAXException ex) {

	    ex.printStackTrace();

	}

	return this;

    }

    /**
     * 
     * If there is a solid defaultContainer, then the attributeName be used to
     * search its attributes first.
     * 
     * {@inheritDoc}
     * 
     * 
     * 
     * @see org.easyxml.xml.Element#getAttributeValue(java.lang.String)
     */

    @Override
    public String getAttributeValue(String attributeName) {

	return (defaultContainer != null)

	? defaultContainer.getAttributeValue(attributeName)

	: super.getAttributeValue(attributeName);

    }

    /**
     * 
     * If there is a solid defaultContainer, then the path be used to search its
     * attributes/elements first.
     * 
     * {@inheritDoc}
     * 
     * 
     * 
     * @see org.easyxml.xml.Element#getElementsOf(java.lang.String)
     */

    @Override
    public List<Element> getElementsOf(String path) {

	List<Element> result = null;

	if (defaultContainer != null) {

	    result = defaultContainer.getElementsOf(path);

	    if (result != null)

		return result;

	}

	return super.getElementsOf(path);

    }

    /**
     * 
     * If there is a solid defaultContainer, then the path be used to search its
     * attributes/elements first.
     * 
     * {@inheritDoc}
     * 
     * 
     * 
     * @see org.easyxml.xml.Element#getValuesOf(java.lang.String)
     */

    @Override
    public String[] getValuesOf(String path) {

	return (defaultContainer != null) ?

	defaultContainer.getValuesOf(path) : super.getValuesOf(path);

    }

    /**
     * 
     * If there is a solid defaultContainer, then the path be used to search its
     * attributes/elements first.
     * 
     * {@inheritDoc}
     * 
     * 
     * 
     * @see org.easyxml.xml.Element#setValuesOf(java.lang.String,
     *      java.lang.String[])
     */

    @Override
    public Boolean setValuesOf(String path, String... values) {

	return (defaultContainer != null) ?

	defaultContainer.setValuesOf(path, values) : super.setValuesOf(path,
		values);

    }

    /**
     * 
     * If there is a solid defaultContainer, then the attributeName be used to
     * search its attributes/elements first.
     * 
     * {@inheritDoc}
     * 
     * 
     * 
     * @see org.easyxml.xml.Element#setAttributeValue(java.lang.String,
     *      java.lang.String)
     */

    @Override
    public Boolean setAttributeValue(String attributeName, String newValue) {

	return (defaultContainer != null) ?

	defaultContainer.setAttributeValue(attributeName, newValue) : super
		.setAttributeValue(attributeName, newValue);

    }

    @Override
    public Boolean containsElement(String name) {

	Boolean result = false;

	if (defaultContainer != null) {

	    result = defaultContainer.containsElement(name);

	    if (result)

		return result;

	}

	return super.containsElement(name);

    }

    @Override
    public String toString() {

	return this.toString(DefaultCharset, DefaultDisplayEmeptyElement,
		DefaultDisplayEmeptyElement);

    }

    /**
     * 
     * Get well-formatted string of this XML document.
     * 
     * @param charset
     *            - Charset to be specified in the XML Declaration.
     * 
     * @param keepEmptyElments
     *            - Specify if Empty Elements shall be displayed.
     * 
     * @param keepSpace
     *            - Specify if SPACE like ' ', '\t', '\n' shall be displayed.
     * 
     * @return Output string.
     */

    public String toString(Charset charset, Boolean keepEmptyElments,
	    Boolean keepSpace) {

	if (charset == null) {

	    System.out
		    .print("Invalid charset specified, using the Default Charset instead.");

	    charset = DefaultCharset;

	}

	String xml = String.format(DefaultDeclarationFormat, charset.name()) +

	super.toString(0, keepEmptyElments, keepEmptyElments);

	if (!keepSpace) {

	    xml = xml.replaceAll("\\s+", "");

	}

	return xml;

    }

    /**
     * 
     * Get the byte array of this XmlDocument.
     * 
     * @param charset
     *            - Charset to be specified in the XML Declaration.
     * 
     * @param keepEmptyElments
     *            - Specify if Empty Elements shall be displayed.
     * 
     * @param keepSpace
     *            - Specify if SPACE like ' ', '\t', '\n' shall be displayed.
     * 
     * @return Output byte array by chosen charset.
     */

    public byte[] getBytes(Charset charset, Boolean keepEmptyElments,
	    Boolean keepSpace) {

	String xml = this.toString(charset, keepEmptyElments, keepSpace);

	byte[] bytes = xml.getBytes(charset);

	return bytes;

    }

    /**
     * 
     * Extract values of this document.
     * 
     * @param pathMap
     *            - A map of the display name (as key) and element path (as
     *            value).
     * 
     * @return Map<String, String[]> instance whose key stores the display name,
     *         and its value keeps all values of whose path.
     */

    public Map<String, String[]> extractValues(Map<String, String> pathMap) {

	if (pathMap == null)

	    return null;

	Map<String, String[]> result = new LinkedHashMap<String, String[]>();

	Iterator<Entry<String, String>> iterator = pathMap.entrySet()
		.iterator();

	while (iterator.hasNext()) {

	    Entry<String, String> next = iterator.next();

	    String path = next.getKey();

	    String[] values = getValuesOf(path);

	    result.put(next.getValue(), values);

	}

	return result;

    }

    public List<? extends Map<String, String>> mapOf(String objectPath) {

	return mapOf(objectPath, null);

    }

    public List<? extends Map<String, String>> mapOf(String objectPath,
	    Map<String, String> pathMap) {

	if (objectPath == null || !containsElement(objectPath))

	    return null;

	List<Element> objectElements = getElementsOf(objectPath);

	if (pathMap == null) {

	    pathMap = new HashMap<String, String>();

	    String thisPath = this.defaultContainer == null ? this.getPath()
		    : this.defaultContainer.getPath();

	    for (Element element : objectElements) {

		if (element.attributes != null) {

		    for (Map.Entry<String, Attribute> attribute : element.attributes
			    .entrySet()) {

			String key = attribute.getKey();

			if (!pathMap.containsKey(key))

			    pathMap.put(key, key);

		    }

		}

		if (element.children != null) {

		    for (Map.Entry<String, List<Element>> child : element.children
			    .entrySet()) {

			String key = child.getKey();

			int lastSignPos = StringUtils.lastIndexOfAny(key,
				Attribute.DefaultAttributePathSign,
				Element.DefaultElementPathSign);

			String alias = lastSignPos == -1 ? key : key
				.substring(lastSignPos + 1);

			if (!pathMap.containsKey(key))

			    pathMap.put(key, alias);

		    }

		}

	    }

	}

	ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();

	for (Element element : objectElements) {

	    HashMap<String, String> map = new HashMap<String, String>();

	    for (Map.Entry<String, String> entry : pathMap.entrySet()) {

		String[] values = element.getValuesOf(entry.getKey());

		if (values == null || values.length == 0)

		    continue;

		String theValue = (values.length == 1) ? values[0] : "["
			+ StringUtils.join(values, ", " + "]");

		String theAlias = entry.getValue();

		if (map.containsKey(theAlias)) {

		    theAlias = entry.getKey().replaceAll("<|>", "_");

		}

		map.put(theAlias, theValue);

	    }

	    result.add(map);

	}

	return result;

    }

    public String toJSON(String objectPath, Map<String, String> pathMap) {

	if (objectPath == null || !containsElement(objectPath)
		|| pathMap == null)

	    return null;

	Map<String, String> pathToKeys = new LinkedHashMap<String, String>();

	for (Map.Entry<String, String> entry : pathMap.entrySet()) {

	    String key = entry.getKey();

	    String originalPath = entry.getValue();

	    if (!originalPath.startsWith(objectPath)) {

		continue;

	    }

	    String relativePath = originalPath
		    .substring(objectPath.length() + 1);

	    pathToKeys.put(relativePath, key);

	}

	if (!pathToKeys.containsKey("")) {

	    pathToKeys.put("", elementNameOf(objectPath));

	}

	List<Element> objectElements = getElementsOf(objectPath);

	return getJsonStringOf(objectElements, pathToKeys);

    }

    public String toJSON(String objectPath) {

	if (objectPath == null || !containsElement(objectPath))

	    return null;

	Map<String, String> pathToKeys = new LinkedHashMap<String, String>();

	for (Map.Entry<String, List<Element>> entry : this.children.entrySet()) {

	    String originalPath = entry.getKey();

	    if (!originalPath.startsWith(objectPath)) {

		continue;

	    }

	    String relativePath = originalPath.equals(objectPath) ? ""
		    : originalPath.substring(objectPath.length() + 1);

	    String elementName = elementNameOf(originalPath);

	    if (!pathToKeys.containsKey(relativePath)) {

		pathToKeys.put(relativePath, elementName);

	    } else

		throw new InvalidParameterException(
			"Duplicated definitionf of " + entry);

	}

	List<Element> objectElements = getElementsOf(objectPath);

	return getJsonStringOf(objectElements, pathToKeys);

    }

    @Override
    public String toJSON() {

	if (this.defaultContainer != null) {

	    return this.defaultContainer.toJSON();

	} else {

	    // Map<String, String> pathToKeys = new LinkedHashMap<String,
	    // String>();

	    // List<String> firstLevelPathes = new ArrayList<String>();

	    // for(Map.Entry<String, List<Element>> entry :
	    // this.children.entrySet()) {

	    // String originalPath = entry.getKey();

	    // int firstSignPos = StringUtils.indexOfAny(originalPath, '>',
	    // '<');

	    //

	    // String elementName = elementNameOf(originalPath);

	    // if (firstSignPos == -1) {

	    // firstLevelPathes.add(originalPath);

	    // } else {

	    // String relativePath = originalPath.substring(firstSignPos+1);

	    // pathToKeys.put(relativePath, elementName);

	    // }

	    // }

	    //

	    // List<Element> objectElements = new ArrayList<Element>();

	    // for(String firstLevelPath : firstLevelPathes) {

	    // List<Element> elements = this.getElementsOf(firstLevelPath);

	    // objectElements.addAll(elements);

	    // }

	    // return getJsonStringOf(objectElements, pathToKeys);

	    return super.toJSON();

	}

    }

}