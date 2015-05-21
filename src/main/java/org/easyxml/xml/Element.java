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

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.easyxml.util.Utility;
import org.xml.sax.SAXException;

/**
 * 
 * Base class of XML element.
 * 
 * @author William JIANG
 * 
 *         15 May 2015
 * 
 * 
 * 
 * @version $Id$
 */

public class Element {

    // Default behavior of if show empty attribute when it is empty.

    public static final Boolean DefaultDisplayEmptyAttribute = true;

    // Default behavior of if show empty attribute when it is empty.

    public static final Boolean DefaultDisplayEmeptyElement = true;

    // Keywords

    public static final String DefaultElementPathSign = ">";

    public static final String Value = "Value";

    public static final String NewLine = "\n";

    public static final String ElementValueHolder = null;

    public static final String ClosingTagFormat = "</%s>";

    public static final String AppendInnerTextFormat = "%s %s";

    public static final String DefaultIndentHolder = "    ";

    public static final String DefaultListLeading = "{";

    public static final String DefaultListEnding = "}";

    public static final String DefaultListSeperator = ", ";

    public static final Boolean IgnoreLeadingSpace = true;

    public static final char[] EntityReferenceKeys = { '<', '>', '&', '\'',
	    '\"' };

    public static final String[] EntityReferences = { "&lt;", "&gt;", "&amp;",
	    "&apos;", "&quot;" };

    /**
     * 
     * Get the unique path of an Element.
     * 
     * @param element
     *            - Element under evaluation.
     * 
     * @return The path within the whole XML document.
     * 
     *         For example: supposing the common SOAP document (<SOAP:Envelope>
     *         as root, with <SOAP:Header> and <SOAP:Body>)
     * 
     *         contains one Element <Child> under <SOAP:Body>, and two
     *         <GrandChild> under 'Child', then
     * 
     *         For <Child> element, the result would be
     *         "SOAP:Envelope>SOAP:Body>Child";
     * 
     *         For both <GrandChild> element, the result would be
     *         "SOAP:Envelope>SOAP:Body>Child>GrandChild" with default output
     *         format.
     */

    public static String getElementPath(Element element) {

	return getElementPath(element, null);

    }

    /**
     * 
     * Get the unique path of an Element.
     * 
     * @param element
     *            - Element under evaluation.
     * 
     * @param refParent
     *            - Relative root for evaluation.
     * 
     * @return The path within the parent element of the whole XML document.
     * 
     *         For example: supposing the common SOAP document (<SOAP:Envelope>
     *         as root, with <SOAP:Header> and <SOAP:Body>)
     * 
     *         contains one Element <Child> under <SOAP:Body>, and two
     *         <GrandChild> under 'Child', then if 'refParent'
     * 
     *         is set to <SOAP:Body>
     * 
     *         For <Child> element, the result would be "Child";
     * 
     *         For both <GrandChild> element, the result would be
     *         "Child>GrandChild" with default output format.
     */

    public static String getElementPath(Element element, Element refParent) {

	StringBuilder sb = new StringBuilder(element.getName());

	Element directParent = element.getParent();

	while (directParent != null && directParent != refParent) {

	    sb.insert(0, directParent.getName() + DefaultElementPathSign);

	    directParent = directParent.getParent();

	}
	;

	return sb.toString();

    }

    /**
     * 
     * Get the name of the element specified by the path.
     * 
     * @param path
     *            - The path within the parent element.
     * 
     * @return Name of the target element.
     */

    public static String elementNameOf(String path) {

	if (StringUtils.isBlank(path))

	    return "";

	String[] elementNames = path.split(DefaultElementPathSign);

	return elementNames[elementNames.length - 1];

    }

    // Name or Tag of the element

    protected String name;

    // Keep the innerText value

    protected String value;

    // Container Element

    protected Element parent = null;

    // Map to keep its attributes

    protected Map<String, Attribute> attributes = null;

    // Map to keep its direct children elements

    protected Map<String, List<Element>> children = null;

    public String getName() {

	return name;

    }

    public void setName(String name) {

	this.name = name;

    }

    /**
     * 
     * Get the innerText of the element
     * 
     * @return innerText un-escaped
     */

    public String getValue() {

	return StringEscapeUtils.unescapeXml(value);

    }

    /**
     * 
     * Set the innerText of the element
     * 
     * @param value
     *            - New innerText value.
     */

    public void setValue(String value) {

	this.value = StringEscapeUtils.escapeXml10(StringUtils.trim(value));

    }

    /**
     * 
     * Append new Text Node to existing innerText with default format, it is
     * called to merge multiple text node as the innerText.
     * 
     * That is, if the value has been set to "oldValue", then
     * appendValue("newValue") would set it to "oldValue newValue".
     * 
     * @param value
     */

    public void appendValue(String value) {

	appendValue(value, AppendInnerTextFormat);

    }

    /**
     * 
     * Append new Text Node to existing innerText, it is called to merge
     * multiple text node as the innerText.
     * 
     * @param value
     *            - Text to be appended.
     * 
     * @param appendFormat
     *            - Format of how to append new text node to existing text node.
     * 
     *            The first '%s' denote the existing text, the second '%s' would
     *            be replaced with formatted value.
     */

    public void appendValue(String value, String appendFormat) {

	String formattedValue = StringEscapeUtils.escapeXml10(StringUtils
		.trim(value));

	if (this.value == null || this.value.length() == 0) {

	    this.value = formattedValue;

	} else if (!StringUtils.isBlank(formattedValue)) {

	    this.value = String
		    .format(appendFormat, this.value, formattedValue);

	}

    }

    /**
     * 
     * Get the parent Element.
     * 
     * @return
     */

    public Element getParent() {

	return parent;

    }

    /**
     * 
     * Set the parent Element after detecting looping reference, and update the
     * Children map to keep reference of the new child element.
     * 
     * @param parent
     */

    public void setParent(Element parent) {

	// Check to prevent looping reference

	Element itsParent = parent.getParent();

	while (itsParent != null) {

	    if (itsParent == this)

		throw new InvalidParameterException(
			"The parent cannot be a descendant of this element!");

	    itsParent = itsParent.getParent();

	}

	this.parent = parent;

	Element container = this;

	String path = this.name;

	while (container.getParent() != null) {

	    container = container.getParent();

	    Map<String, List<Element>> upperChildren = container.getChildren();

	    if (upperChildren == null) {

		upperChildren = new LinkedHashMap<String, List<Element>>();

	    }

	    if (!upperChildren.containsKey(path)) {

		List<Element> newList = new ArrayList<Element>();

		newList.add(this);

		upperChildren.put(path, newList);

	    } else {

		List<Element> upperList = upperChildren.get(path);

		if (!upperList.contains(this)) {

		    upperList.add(this);

		}

	    }
	    
	    //Append this.children to parent.children with adjusted path
	    if (this.children != null) {
		for (Map.Entry<String, List<Element>> entry : this.children.entrySet()) {
		    String childPath = String.format("%s%s%s", path, Element.DefaultElementPathSign, entry.getKey());
		    if (!upperChildren.containsKey(childPath)) {        
			List<Element> newList = new ArrayList<Element>(entry.getValue());
			upperChildren.put(childPath, newList);
			
		    } else {
			List<Element> upperList = upperChildren.get(childPath);
			upperList.addAll(entry.getValue());        
		    }
		    
		}
		
	    }

	    String containerName = container.getName();

	    path = containerName + DefaultElementPathSign + path;

	}

    }

    public Map<String, Attribute> getAttributes() {

	return attributes;

    }

    public Map<String, List<Element>> getChildren() {

	return children;

    }

    /**
     * 
     * Constructor with Element tag name, its parent element and innerText.
     * 
     * @param name
     *            - Tag name of the element.
     * 
     * @param parent
     *            - Container of this element.
     * 
     * @param value
     *            - InnerText, notice that is would be escaped before stored to
     *            this.value.
     */

    public Element(String name, Element parent, String value) {

	if (StringUtils.isBlank(name)) {

	    throw new InvalidParameterException(
		    "Name of an element cannot be null or blank!");

	}

	if (!StringUtils.containsNone(name, EntityReferenceKeys)) {

	    throw new InvalidParameterException(
		    String.format(
			    "\"{s}\" cannot contain any of the 5 chars: \'<\', \'>\', \'&\', \'\'\', \'\"\'"

			    , name));

	}

	this.name = name;

	setValue(value);

	if (parent != null) {

	    setParent(parent);

	    // Update the Children map of the parent element after setting name

	    this.parent.addChildElement(this);

	}

    }

    public Element(String name, Element parent) {

	this(name, parent, ElementValueHolder);

    }

    public Element(String name) {

	this(name, null, ElementValueHolder);

    }

    /**
     * 
     * Method to append one constructed Element as its direct child by updating
     * its Children Map and the child's parent.
     * 
     * @param child
     *            - Constructed Element.
     * 
     * @return This element for cascading processing.
     */

    public Element addChildElement(Element child) {

	// Do nothing if child is null

	if (child == null)
	    return this;

	if (this.children == null) {

	    this.children = new LinkedHashMap<String, List<Element>>();

	}

	String childName = child.getName();

	if (!this.children.containsKey(childName)) {

	    List<Element> elements = new ArrayList<Element>();

	    elements.add(child);

	    this.children.put(childName, elements);

	}

	child.setParent(this);

	if (!this.children.get(childName).contains(child))

	    this.children.get(childName).add(child);

	return this;

    }

    /**
     * 
     * Update the Children map to keep reference of the new child element.
     * 
     * @param childName
     */

    protected void updateChildrenMap(Element child) {

	String childName = child.getName();

	List<Element> elements = new ArrayList<Element>();

	this.children.put(childName, elements);

	Element container = this;

	String path = childName;

	while (container.getParent() != null) {

	    String containerName = container.getName();

	    path = containerName + DefaultElementPathSign + path;

	    // Map<String, List<Element>> childrenOfParent =
	    // container.getParent().getChildren();

	    // if (childrenOfParent == null ||
	    // !childrenOfParent.containsKey(containerName)) {

	    // try {

	    // throw new
	    // InvalidAttributesException("The parent doesn't contain key of " +
	    // containerName);

	    // } catch (InvalidAttributesException e) {

	    // e.printStackTrace();

	    // continue;

	    // }

	    // }

	    container = container.getParent();

	    Map<String, List<Element>> upperChildren = container.getChildren();

	    if (!upperChildren.containsKey(path)) {

		upperChildren.put(path, elements);

	    } else {

		List<Element> upperList = upperChildren.get(path);

		if (!upperList.contains(child)) {

		    upperList.add(child);

		}

	    }

	}

    }

    /**
     * 
     * Check to see if there is a direct child with the name specified.
     * 
     * Notice that since there is no operation of removing child element, for
     * simplicity, there is no checking of if the map is empty.
     * 
     * @param name
     *            - Name of the element under evaluation.
     * 
     * @return 'true' if there exist such direct children.
     */

    public Boolean containsElement(String name) {

	return (this.children != null && this.children.containsKey(name))
		|| name.length() == 0;

    }

    /**
     * 
     * Get all elements with the path specified.
     * 
     * Notice this path is not the absolute path of the target element.
     * 
     * @param path
     *            - The path uniquely identify the relative location of the
     *            element expected within the DOM tree.
     * 
     * @return
     * 
     *         null when path is null;
     * 
     *         this when path is an empty String;
     * 
     *         null if no such element exists
     * 
     *         a list of elements matched with the path.
     */

    public List<Element> getElementsOf(String path) {

	if (this.children == null)

	    return null;

	else if (path.length() == 0) {

	    List<Element> result = new ArrayList<Element>();

	    result.add(this);

	    return result;

	}

	else if (!children.containsKey(path))

	    return null;

	return children.get(path);

    }

    /**
     * 
     * Method to evaluate the path by split the path to one path to locate the
     * element, and optional another part to locate its attribute.
     * 
     * @param path
     *            - The whole path of a Element or a Attribute.
     * 
     *            For example, "SOAP:Envelope>SOAP:Body>Child" denotes <Child>
     *            element/elements under <SOAP:Body>.
     * 
     *            "SOAP:Envelope>SOAP:Body>Child<Attr" denotes the "Attr"
     *            attribute of the above <Child> element/elements.
     * 
     * @return At most two segments, first for the element, optional second for
     *         the attribute.
     * 
     *         When the path denotes some elements, then only one segment would
     *         be returned.
     * 
     *         When the path denotes an attribute of some elements, then two
     *         segments would be returned.
     */

    protected String[] parsePath(String path) {

	if (StringUtils.isBlank(path))

	    throw new InvalidParameterException(
		    "Blank path cannot be used to locate elements!");

	String[] segments = path.split(Attribute.DefaultAttributePathSign);

	int segmentLength = segments.length;

	if (segmentLength > 2)

	    throw new InvalidParameterException(
		    String.format(
			    "The path \'%s\' shall have at most one \'%s\' to denote the attribute of a kind of element."

			    , path, Attribute.DefaultAttributePathSign));

	return segments;

    }

    /**
     * 
     * Get the values of innerText/attribute/childElements/childAttributes
     * specified by 'path'.
     * 
     * @param path
     *            - The whole path of a Element or a Attribute.
     * 
     *            For example, "SOAP:Envelope>SOAP:Body>Child" denotes <Child>
     *            element/elements under <SOAP:Body>.
     * 
     *            "SOAP:Envelope>SOAP:Body>Child<Attr" denotes the "Attr"
     *            attribute of the above <Child> element/elements.
     * 
     * @return When path
     * 
     *         1) equals to "Value": then only the innerText of this element
     *         would be returned.
     * 
     *         2) equals to some existing attribute name: then only value of
     *         that attribute would be returned.
     * 
     *         3) is parsed as some elements, then their innerText would be
     *         returned as an array.
     * 
     *         4) is parsed as some attributes, then these attribute values
     *         would be returned as an array.
     * 
     *         5) Otherwise returns null.
     */

    public String[] getValuesOf(String path) {

	String[] values = null;

	if (path == Value) {

	    // When path is "Value", then only the innerText of this element
	    // would be returned.

	    values = new String[1];

	    values[0] = getValue();

	    return values;

	} else if (attributes != null && attributes.containsKey(path)) {

	    // If the path denotes one existing attribute of this element, then
	    // only value of that attribute would be returned.

	    values = new String[1];

	    values[0] = getAttributeValue(path);

	    return values;

	}

	try {

	    String[] segments = parsePath(path);

	    // Get the target elements or elements of the target attributes
	    // first

	    String elementPath = segments[0];

	    List<Element> elements = getElementsOf(elementPath);

	    if (elements == null)

		return null;

	    int size = elements.size();

	    values = new String[size];

	    if (segments.length == 1) {

		// The path identify Element, thus return their innerText as an
		// array

		for (int i = 0; i < size; i++) {

		    values[i] = elements.get(i).getValue();

		}

	    } else {

		// Otherwise, the path denotes some attributes, then return the
		// attributes values as an array

		String attributeName = segments[1];

		for (int i = 0; i < size; i++) {

		    values[i] = elements.get(i)
			    .getAttributeValue(attributeName);

		}

	    }

	} catch (InvalidParameterException ex) {

	    // If there is anything unexpected, return null

	    ex.printStackTrace();

	    return null;

	}

	return values;

    }

    /**
     * 
     * Set the values of innerText/attribute/childElements/childAttributes
     * specified by 'path'.
     * 
     * This would facilitate creation of a layered DOM tree conveniently.
     * 
     * For example, run setValuesOf("Child>GrandChild<Id", "1st", "2nd") of an
     * empty element would:
     * 
     * 1) Create a new <Child> element;
     * 
     * 2) Create TWO new <GrandChild> element under the newly created <Child>
     * element;
     * 
     * 3) First <GrandChild> element would have "Id" attribute of "1st", and
     * second <GrandChild> of "2nd".
     * 
     * If then call setValuesOf("Child>GrandChild<Id", "1", "2", "3"), no a new
     * <GrandChild> element would be created
     * 
     * and the attribute values of these <GrandChild> elements would be set to
     * "1", "2" and "3" respectively.
     * 
     * @param path
     *            - The whole path of a Element or a Attribute.
     * 
     *            For example, "SOAP:Envelope>SOAP:Body>Child" denotes <Child>
     *            element/elements under <SOAP:Body>.
     * 
     *            "SOAP:Envelope>SOAP:Body>Child<Attr" denotes the "Attr"
     *            attribute of the above <Child> element/elements.
     * 
     * @param values
     *            - The values to be set when 'path':
     * 
     *            1) equals to "Value": then set the new value of the innerText
     *            of this element.
     * 
     *            2) equals to some existing attribute name: then set the new
     *            value of that attribute.
     * 
     *            3) is parsed as some elements, then their innerText would be
     *            set.
     * 
     *            4) is parsed as some attributes, then these attribute values
     *            would be set.
     * 
     * @return 'true' if setting is successful, 'false' if failed.
     */

    public Boolean setValuesOf(String path, String... values) {

	try {

	    if (path == Value) {

		if (values.length != 1)

		    throw new InvalidParameterException(
			    "Only one String value is expected to set the value of this element");

		// Change the innerText value of this element

		setValue(values[0]);

		return true;

	    } else if (attributes != null && attributes.containsKey(path)) {

		if (values.length != 1)

		    throw new InvalidParameterException(
			    "Only one String value is expected to set the attribute value.");

		// Set the attribute value of this element

		setAttributeValue(path, values[0]);

		return true;

	    }

	    String[] segments = parsePath(path);

	    String elementPath = segments[0];

	    // Try to treat the path as a key to its direct children elements
	    // first

	    List<Element> elements = getElementsOf(elementPath);

	    Element newElement = null;

	    if (elements == null) {

		// 'path' is not a key for its own direct children, thus split
		// it with '>'

		// For instance, "Child>GrandChild" would be split to {"Child",
		// "GrandChild"} then this element would:

		// 1) search "Child" as its direct children elements;

		// 2) if no <Child> element exists, create one;

		// 3) search "Child>GrandChild" as its direct children elements;

		// 4) if no exists, create new <GrandChild> as a new child of
		// <Child> element.

		String[] containers = elementPath.split(DefaultElementPathSign);

		String next = "";

		Element last = this;

		for (int i = 0; i < containers.length; i++) {

		    next += containers[i];

		    if (getElementsOf(next) == null) {

			newElement = new Element(containers[i], last);

		    }

		    last = getElementsOf(next).get(0);

		    next += DefaultElementPathSign;

		}

		elements = getElementsOf(elementPath);

	    }

	    int size = values.length;

	    if (size > elements.size()) {

		// In case the existing elements are less than values.length,
		// create new elements under the last container

		int lastChildMark = elementPath
			.lastIndexOf(DefaultElementPathSign);

		String lastContainerPath = lastChildMark == -1 ? ""
			: elementPath.substring(0, lastChildMark);

		String lastElementName = elementPath
			.substring(lastChildMark + 1);

		Element firstContainer = getElementsOf(lastContainerPath)
			.get(0);

		for (int i = elements.size(); i < size; i++) {

		    newElement = new Element(lastElementName, firstContainer);

		}

		elements = getElementsOf(elementPath);

	    }

	    if (segments.length == 1) {

		// The path identify Element, thus set their innerText

		for (int i = 0; i < size; i++) {

		    elements.get(i).setValue(values[i]);

		}

	    } else {

		// The path identify Attribute, thus set values of these
		// attribute accordingly

		String attributeName = segments[1];

		for (int i = 0; i < size; i++) {

		    elements.get(i).setAttributeValue(attributeName, values[i]);

		}

	    }

	    return true;

	} catch (InvalidParameterException ex) {

	    ex.printStackTrace();

	    return false;

	}

    }

    /**
     * 
     * Method to add a new Attribute to this element.
     * 
     * @param name
     *            - Name of the new Attribute
     * 
     * @param value
     *            - Value of the new Attribute
     * 
     * @return This element for cascading processing.
     * 
     * @throws SAXException
     */

    public Element addAttribute(String name, String value) throws SAXException {

	if (this.attributes == null)

	    this.attributes = new LinkedHashMap<String, Attribute>();

	// The name of an attribute cannot be duplicated according to XML
	// specification

	if (this.attributes.containsKey(name))

	    throw new SAXException(
		    "Attribute name must be unique within an Element.");

	if (value != null)

	    this.attributes.put(name, new Attribute(this, name, value));

	return this;

    }

    /**
     * 
     * Method to get the value of an attribute specified by the attributeName.
     * 
     * @param attributeName
     *            - Name of the concerned attribute.
     * 
     * @return null if there is no such attribute, or its value when it exists.
     */

    public String getAttributeValue(String attributeName) {

	if (this.attributes != null
		&& this.attributes.containsKey(attributeName)) {

	    return this.attributes.get(attributeName).getValue();

	}

	return null;

    }

    /**
     * 
     * Set the attribute value.
     * 
     * @param attributeName
     *            - Name of the concerned attribute.
     * 
     * @param newValue
     *            - New value of the attribute.
     * 
     * @return
     * 
     *         'false' if there is any confliction with XML specification.
     * 
     *         'true' set attribute value successfully.
     */

    public Boolean setAttributeValue(String attributeName, String newValue) {

	if (this.attributes != null
		&& this.attributes.containsKey(attributeName)) {

	    this.attributes.get(attributeName).setValue(newValue);

	    return true;

	}

	try {

	    this.addAttribute(attributeName, newValue);

	    return true;

	} catch (SAXException e) {

	    e.printStackTrace();

	    return false;

	}

    }

    /**
     * 
     * Get the level of this element in the DOM tree. 0 for the root element.
     * 
     * @return
     */

    public int getLevel() {

	if (this.parent == null)

	    return 0;

	return this.parent.getLevel() + 1;

    }

    /**
     * 
     * Evaluate if this element contain any innerText, non-empty attribute or
     * children.
     * 
     * @return 'true' for empty.
     */

    public Boolean isEmpty() {

	// If this element has some non-blank innerText, returns false.

	if (!StringUtils.isBlank(getValue()))

	    return false;

	// If this element has some non-blank attribute, returns false.

	if (attributes != null && !attributes.isEmpty()) {

	    Iterator<Entry<String, Attribute>> iterator = attributes.entrySet()
		    .iterator();

	    while (iterator.hasNext()) {

		Attribute attribute = iterator.next().getValue();

		if (!attribute.isEmpty())

		    return false;

	    }

	}

	// If this element has some non-blank element, returns false.

	if (children != null && !children.isEmpty()) {

	    Iterator<Entry<String, List<Element>>> iterator2 = children
		    .entrySet().iterator();

	    while (iterator2.hasNext()) {

		List<Element> elements = iterator2.next().getValue();

		for (int i = 0; i < elements.size(); i++) {

		    if (!elements.get(i).isEmpty())

			return false;

		}

	    }

	}

	// Otherwise, treat this element as empty.

	return true;

    }

    /**
     * 
     * Get path relative to the root.
     * 
     * @return Path as a formatted string.
     */

    public String getPath() {

	return getElementPath(this);

    }

    private String getAttributeString() {

	return allAttributesAsString(DefaultDisplayEmptyAttribute);

    }

    private String allAttributesAsString(Boolean outputEmptyAttribute) {

	Iterator<Entry<String, Attribute>> iterator = attributes.entrySet()
		.iterator();

	StringBuilder sb = new StringBuilder();

	while (iterator.hasNext()) {

	    Attribute attribute = iterator.next().getValue();

	    if (outputEmptyAttribute || !attribute.isEmpty()) {

		sb.append(" " + attribute);

	    }

	}

	return sb.toString();

    }

    @Override
    public String toString() {

	return toString(0, true, true);

    }

    /**
     * 
     * Method to display this element as a well-formatted String.
     * 
     * @param indent
     *            - Indent count for this element.
     * 
     * @param outputEmptyAttribute
     *            - Specify if empty attribute shall be displayed.
     * 
     * @param outputEmptyElement
     *            - Specify if empty children element shall be displayed.
     * 
     * @return String form of this XML element.
     */

    public String toString(int indent, Boolean outputEmptyAttribute,
	    Boolean outputEmptyElement) {

	// If this is an empty element and no need to output empty element,
	// return "" immediately.

	if (!outputEmptyElement && this.isEmpty())

	    return "";

	String indentString = StringUtils.repeat(DefaultIndentHolder, indent);

	StringBuilder sb = new StringBuilder(indentString + "<" + name);

	// If this element has only attributes

	if ((children == null || children.size() == 0)
		&& (value == null || value.trim().length() == 0)) {

	    // Compose the empty element tag

	    if (attributes != null) {

		sb.append(allAttributesAsString(outputEmptyAttribute));

	    }

	    sb.append("/>");

	} else {

	    // Compose the opening tag

	    if (attributes != null) {

		sb.append(allAttributesAsString(outputEmptyAttribute));

	    }

	    sb.append('>');

	    // Include the children elements by order

	    if (children != null && !children.isEmpty()) {

		sb.append(NewLine);

		Iterator<Entry<String, List<Element>>> iterator2 = children
			.entrySet().iterator();

		while (iterator2.hasNext()) {

		    Entry<String, List<Element>> next = iterator2.next();

		    String path = next.getKey();

		    if (path.contains(DefaultElementPathSign))

			continue;

		    List<Element> elements = next.getValue();

		    for (int i = 0; i < elements.size(); i++) {

			Element element = elements.get(i);

			if (outputEmptyElement || !elements.get(i).isEmpty()) {

			    sb.append(element.toString(indent + 1,
				    outputEmptyAttribute, outputEmptyElement)
				    + NewLine);

			}

		    }

		}

	    }

	    // Include the inner text of this element

	    if (StringUtils.isNotBlank(value)) {

		if (children != null && !children.isEmpty()) {

		    sb.append(IgnoreLeadingSpace ? indentString
			    + DefaultIndentHolder + value + NewLine : value
			    + NewLine);

		} else {

		    sb.append(value);

		}

	    }

	    // Include the closing tag

	    if (children != null && !children.isEmpty()) {

		sb.append(indentString);

	    }

	    sb.append(String.format(ClosingTagFormat, name));

	}

	return sb.toString();

    }

    protected String getJsonStringOf(List<Element> objectElements,
	    Map<String, String> pathToKeys) {

	int elementSize = objectElements.size();

	String[] elementJSONs = new String[elementSize];

	for (int i = 0; i < elementSize; i++) {

	    Element element = objectElements.get(i);

	    Map<String, String> elementMap = new LinkedHashMap<>();

	    for (Map.Entry<String, String> entry : pathToKeys.entrySet()) {

		String relativePath = entry.getKey();

		String displayName = entry.getValue();

		if (relativePath.equals(Value)) {

		    elementMap.put(displayName, this.getValue());

		} else if (relativePath.length() == 0) {

		    elementMap.put(relativePath, displayName);

		} else {

		    String[] pathValues = element.getValuesOf(relativePath);

		    if (pathValues == null)

			continue;

		    String theValue = pathValues.length == 1 ? pathValues[0]
			    : Utility.toJSON(pathValues);

		    elementMap.put(displayName, theValue);

		}

	    }

	    if (!elementMap.containsKey("")) {

		elementMap.put("", element.getName());

	    }

	    String elementValue = Utility.toJSON(elementMap);

	    elementJSONs[i] = elementValue;

	}

	String json = String.format("{%s}",
		StringUtils.join(elementJSONs, ",\n"));

	return json;

    }

    public String toJSON() {

	return toJSON(0);

    }

    public String toJSON(int indent) {

	String indentString = StringUtils.repeat(DefaultIndentHolder, indent);

	StringBuilder sb = new StringBuilder();

	sb.append(String.format("%s\"%s\":", indentString, this.getName()));

	String thisPath = this.getPath();

	// If there is valid text node, return it immediately

	if (!StringUtils.isBlank(this.value)) {

	    sb.append(String.format("\"%s\"", this.getValue()));

	    return sb.toString();

	}

	// Output as an object

	sb.append("{");

	// Otherwise, consider both the attributes and its children elements for
	// output

	if (this.attributes != null) {

	    String attrIndent = StringUtils.repeat(DefaultIndentHolder,
		    indent + 1);

	    for (Map.Entry<String, Attribute> entry : this.attributes
		    .entrySet()) {

		String attrName = entry.getKey();

		String attrValue = this.getAttributeValue(attrName);

		sb.append(String.format("\n%s\"%s\": \"%s\",", attrIndent,
			attrName, attrValue));

	    }

	}

	if (this.children != null) {

	    List<String> firstLevelPathes = new ArrayList<String>();

	    for (Map.Entry<String, List<Element>> entry : this.children
		    .entrySet()) {

		String originalPath = entry.getKey();

		int firstSignPos = StringUtils.indexOfAny(originalPath, '>',
			'<');

		String elementName = elementNameOf(originalPath);

		if (firstSignPos == -1) {

		    firstLevelPathes.add(originalPath);

		}

	    }

	    for (String firstLevelPath : firstLevelPathes) {

		List<Element> elements = this.getElementsOf(firstLevelPath);

		// Check to see if elements could be converted to JSON array

		if (elements.size() > 1) {

		    Boolean asArray = true;

		    for (Element e : elements) {

			if (!StringUtils.isBlank(e.getValue())) {

			    asArray = false;

			    break;

			}

		    }

		    // If they could be treated as array

		    if (asArray) {

			String jsonKey = String.format("\"%s\":",
				elements.get(0).getName());

			sb.append(String.format("\n%s%s[\n",

			StringUtils.repeat(DefaultIndentHolder, indent + 1),
				jsonKey));

			for (Element e : elements) {

			    sb.append(String.format("%s,\n",
				    e.toJSON(indent + 2).replace(jsonKey, "")));

			}

			// Remove the last ','

			sb.setLength(sb.length() - 2);

			sb.append(String.format("\n%s],", StringUtils.repeat(
				DefaultIndentHolder, indent + 1)));

			continue;

		    }

		}

		// Otherwise, output the elements one by one

		for (Element e : elements) {

		    sb.append(String.format("\n%s,", e.toJSON(indent + 1)));

		}

	    }

	    //

	    // //Remove the last ',' and append '}'

	    // sb.setLength(sb.length()-1);

	    // sb.append("\n" + indentString + "}");

	}

	if (sb.toString().endsWith(",")) {

	    sb.setLength(sb.toString().length() - 1);

	    sb.append(String.format("\n%s}", indentString));

	}

	return sb.toString();

    }

}