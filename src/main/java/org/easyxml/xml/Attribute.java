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
package org.easyxml.xml;

import java.security.InvalidParameterException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/*
 *    Created on May 18, 2015 by William JIANG
 */
public class Attribute {
    // Default place holder value when creating a new attribute.
    public static final String XmlAttributeValueHolder = "";
    // Default attribute path sign.
    public static final String DefaultAttributePathSign = "<";
    // Default XML attribute output format, first %s is the attribute name,
    // second %s is its value.
    public static final String DefaultXmlAttributeDisplayFormat = "%s=\"%s\"";

    // Name of the attribute.
    private String name;
    // Value of the attribute.
    private String value;
    // XMLElement containing this XmlAttribute.
    private Element element;

    public Element getElement() {
	return element;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /**
     * Get un-escaped value of this attribute.
     * 
     * @return Original text value.
     */
    public String getValue() {
	return StringEscapeUtils.unescapeXml(value);
    }

    /**
     * Set the value of this attribute.
     * 
     * @param value
     *            - New value to be set.
     */
    public void setValue(String value) {
	if (value == null) {
	    throw new InvalidParameterException(
		    "Value of an attribute cannot be null!");
	}
	this.value = StringEscapeUtils.escapeXml10(StringUtils.trim(value));
    }

    /**
     * Constructor of a new Attribute of a XmlElemwent. Notice that for
     * simplicity, there is no checking of if there is already an attribute with
     * the same name existed within the element.
     * 
     * @param element
     *            - Element containing the attribute.
     * @param name
     *            - Name of the new attribute.
     * @param value
     *            - Value of the new attribute.
     */
    public Attribute(Element element, String name, String value) {
	if (StringUtils.isBlank(name)) {
	    throw new InvalidParameterException(
		    "Name of an attribute cannot be null or blank!");
	}
	if (value == null) {
	    throw new InvalidParameterException(
		    "Value of an attribute cannot be null!");
	}
	if (!StringUtils.containsNone(name, Element.EntityReferenceKeys)) {
	    throw new InvalidParameterException(
		    String.format(
			    "\"{s}\" cannot contain any of the 5 chars: \'<\', \'>\', \'&\', \'\'\', \'\"\'",
			    name));
	}
	this.name = name;
	setValue(value);
    }

    /**
     * Function to check if there is meaningful value.
     * 
     * @return 'true' if value is null or empty.
     */
    public Boolean isEmpty() {
	return StringUtils.isBlank(getValue());
    }

    /**
     * Get the path to locate the attribute.
     * 
     * @return
     */
    public String getPath() {
	return this.element == null ? "" : this.element.getPath()
		+ DefaultAttributePathSign + this.name;
    }

    @Override
    public String toString() {
	return String.format(DefaultXmlAttributeDisplayFormat, name, getValue());
    }
}
