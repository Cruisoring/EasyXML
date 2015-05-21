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
import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

/**
 * 
 * Test of Element and Attribute.
 * 
 * @author William JIANG
 * 
 *         15 May 2015
 * 
 * 
 * 
 * @version $Id$
 */

public class ElementTest {

    /**
     * 
     * Test of Attribute Constructor.
     */

    @Test
    public void testAttribute_ConstructorNormal() {

	Attribute attr = new Attribute(null, "AttributeName", "AttributeValue");

	Assert.assertEquals(attr.getName(), "AttributeName");

	Assert.assertEquals(attr.getValue(), "AttributeValue");

    }

    /**
     * 
     * Test of Attribute Constructor with empty value.
     */

    @Test
    public void testAttribute_Constructor_EmptyValue() {

	Attribute attr = new Attribute(null, "AttributeName", "\t \n");

	Assert.assertEquals(attr.getValue(), "");

    }

    /**
     * 
     * Test of Attribute Constructor with empty value.
     */

    @Test
    public void testAttribute_Constructor_EmptyValue2() {

	Attribute attr = new Attribute(null, "AttributeName", "\t & >< \n");

	Assert.assertEquals(attr.getValue(), "& ><");

    }

    @DataProvider
    private Object[][] testAttribute_ConstructorDataProvider()

    {

	Object[][] data = {

		{ null, null, "AttributeValue",
			"Test of Attribute Constructor with null name" }

		,
		{ null, "\t \n", "AttributeValue",
			"Test of Attribute Constructor with blank name" }

		,
		{ null, "AttributeName", null,
			"Test of Attribute Constructor with null value" }

	};

	return data;

    }

    /**
     * 
     * Test of Attribute Constructor with invalid argument,
     * InvalidParameterException is expected.
     */

    @Test(expectedExceptions = { InvalidParameterException.class }, dataProvider = "testAttribute_ConstructorDataProvider")
    public void testAttribute_Constructor_InvalidArgument(Element element,
	    String name, String value, String description) {

	System.out.println(description);

	Attribute attr = new Attribute(element, name, value);

    }

    /**
     * 
     * Test of Attribute toString()
     */

    @Test
    public void testAttribute_toString_Normal() {

	Attribute attr = new Attribute(null, "AttributeName", "AttributeValue");

	Assert.assertEquals(attr.toString(), "AttributeName=\"AttributeValue\"");

    }

    /**
     * 
     * Test of Element Constructor with valid name and value.
     */

    @Test
    public void testElement_Constructor_Normal() {

	Element element = new Element("ElementName", null, ">");

	Assert.assertEquals(element.getName(), "ElementName");

	Assert.assertEquals(element.getValue(), ">");

	Assert.assertEquals(element.getParent(), null);

	Element child = new Element("ChildName", element, "&");

	Assert.assertEquals(child.getName(), "ChildName");

	Assert.assertEquals(child.getValue(), "&");

	Assert.assertEquals(child.getParent(), element);

	Element anotherChild = new Element("ChildName", element, null);

	Assert.assertEquals(anotherChild.getName(), "ChildName");

	Assert.assertEquals(anotherChild.getValue(), null);

	Assert.assertEquals(anotherChild.getParent(), element);

    }

    /**
     * 
     * DataProvider to provide abnormal Element Constructor parameters.
     * 
     * @return
     */

    @DataProvider
    private Object[][] testElement_ConstructorInvalidParameter_DataProvider()

    {

	Object[][] data = {

		{ null, null, "", "Test of Element Constructor with null name" }

		,
		{ "\t \n", null, null,
			"Test of Element Constructor with blank name" }

		,
		{ "Name>", null, null,
			"Test of Element Constructor with name containing illegal char" }

		,
		{ "<", null, null,
			"Test of Element Constructor with name containing illegal char" }

		,
		{ "And&", null, null,
			"Test of Element Constructor with name containing illegal char" }

		,
		{ "Quo\"", null, null,
			"Test of Element Constructor with name containing illegal char" }

		,
		{ "Quo\'", null, null,
			"Test of Element Constructor with name containing illegal char" }

	};

	return data;

    }

    /**
     * 
     * Test of Attribute Constructor with invalid argument,
     * InvalidParameterException is expected.
     */

    @Test(expectedExceptions = { InvalidParameterException.class }, dataProvider = "testElement_ConstructorInvalidParameter_DataProvider")
    public void testElement_Constructor_InvalidParameter(String name,
	    Element parent, String value, String description) {

	System.out.println(description);

	Element attr = new Element(name, parent, value);

    }

    /**
     * 
     * Validate Element.getValue() and Element.setValue()
     * 
     * @throws SAXException
     */

    @Test
    public void testElement_setGetValue() throws SAXException {

	Element element = new Element("ElementName")

	.addAttribute("attr1", "attrvalue1")

	.addAttribute("attr2", "attrvalue2")

	.addAttribute("attr3", "attrvalue3");

	Assert.assertEquals(element.getValue(), null);

	element.setValue("SomeValue");

	Assert.assertEquals(element.getValue(), "SomeValue");

	element.setValue(null);

	Assert.assertEquals(element.getValue(), null);

	element.setValue("><&\"\'\"");

	Assert.assertEquals(element.getValue(), "><&\"\'\"");

    }

    /**
     * 
     * Validate Element.appendValue() with different prefix/suffix
     * 
     * @throws SAXException
     */

    @Test
    public void testElement_appendValue() throws SAXException {

	Element element = new Element("ElementName", null, "SomeValue");

	Assert.assertEquals(element.getValue(), "SomeValue");

	element.appendValue("First");

	Assert.assertEquals(element.getValue(), "SomeValue First");

	// New text of null shall be neglected

	element.appendValue(null);

	Assert.assertEquals(element.getValue(), "SomeValue First");

	// New text of blank string shall be neglected

	element.appendValue("\t \n");

	Assert.assertEquals(element.getValue(), "SomeValue First");

	// Blank chars of the new value shall be trimmed

	element.appendValue("\t\t& \r");

	Assert.assertEquals(element.getValue(), "SomeValue First &");

	// Special chars of XML could be escaped/un-escaped

	element.appendValue("><&\"\'\"");

	Assert.assertEquals(element.getValue(), "SomeValue First & ><&\"\'\"");

	Assert.assertEquals(
		element.toString(),
		"<ElementName>SomeValue First &amp; &gt;&lt;&amp;&quot;&apos;&quot;</ElementName>");

	// Set element to null, and getValue() would return null

	element.setValue(null);

	Assert.assertEquals(element.getValue(), null);

	// Append EMPTY string, and getValue() shall return ""

	element.appendValue("");

	Assert.assertEquals(element.getValue(), "");

	// Append null to empty string, it shall replace the existing value, and
	// getValue() shall return null

	element.appendValue(null);

	Assert.assertEquals(element.getValue(), null);

	// Append new text to null, and getValue() shall return the trimmed new
	// text

	element.setValue(null);

	element.appendValue("\tSecond ");

	Assert.assertEquals(element.getValue(), "Second");

    }

    /**
     * 
     * Validate Element.appendValue() with different prefix/suffix
     * 
     * @throws SAXException
     */

    @Test
    public void testElement_appendValueWithFormat() throws SAXException {

	Element element = new Element("ElementName", null, "SomeValue");

	Assert.assertEquals(element.getValue(), "SomeValue");

	// With default format

	element.appendValue("First", "%s %s");

	Assert.assertEquals(element.getValue(), "SomeValue First");

	// New text of null shall be neglected

	element.appendValue(null, "%s\t%s");

	Assert.assertEquals(element.getValue(), "SomeValue First");

	// New text of blank string shall be neglected

	element.appendValue("\t \n", "%s\n%s");

	Assert.assertEquals(element.getValue(), "SomeValue First");

	// Blank chars of the new value shall be trimmed, and "\t" is used
	// before "&"

	element.appendValue("\t\t& \r", "%s\t%s");

	Assert.assertEquals(element.getValue(), "SomeValue First\t&");

	// Set element to null

	element.setValue(null);

	// Append new text to null, the format shall be neglected, and
	// getValue() shall return the trimmed new text

	element.appendValue("\tSecond ", "Should Not Be Displayed");

	Assert.assertEquals(element.getValue(), "Second");

	// Append EMPTY string, and getValue() shall return ""

	element.appendValue("\t \r\n    ", "Should Not Be Displayed");

	Assert.assertEquals(element.getValue(), "Second");

	// Append null to empty string, and getValue() shall return null

	element.appendValue(null, "Should Not Be Displayed");

	Assert.assertEquals(element.getValue(), "Second");

	// Append new text to null, and getValue() shall return the trimmed new
	// text

	element.appendValue("\tThird \n", "%s***%s");

	Assert.assertEquals(element.getValue(), "Second***Third");

	// Special chars of XML could be escaped/un-escaped

	element.appendValue("><&\"\'\"", "%s\n %s");

	Assert.assertEquals(element.getValue(), "Second***Third\n ><&\"\'\"");

	Assert.assertEquals(element.toString(),
		"<ElementName>Second***Third\n &gt;&lt;&amp;&quot;&apos;&quot;</ElementName>");

    }

    /**
     * 
     * Validate that setParent() could prevent setting one direct child element
     * as its parent
     */

    @Test(expectedExceptions = { InvalidParameterException.class })
    public void testElement_setParent_LoopingParent() {

	Element root = new Element("Root");

	Element first = new Element("First", root);

	root.setParent(first);

    }

    /**
     * 
     * Validate that setParent() could prevent setting one descendant element as
     * its parent
     */

    @Test(expectedExceptions = { InvalidParameterException.class })
    public void testElement_setParent_LoopingParent2() {

	Element root = new Element("Root");

	Element first = new Element("First", root);

	Element second = new Element("Second", first);

	root.setParent(second);

    }

    /**
     * 
     * Validate Element.addAttribute()
     * 
     * @throws SAXException
     */

    @Test
    public void testElement_addAttributeNormal() throws SAXException {

	Element element = new Element("ElementName", null, "ElementValue")

	.addAttribute("attr1", "attrvalue1")

	.addAttribute("attr2", "attrvalue2")

	.addAttribute("attr3", "attrvalue3");

	Assert.assertEquals(element.getAttributeValue("attr1"), "attrvalue1");

	Assert.assertEquals(element.getAttributeValue("attr2"), "attrvalue2");

	Assert.assertEquals(element.getAttributeValue("attr3"), "attrvalue3");

    }

    /**
     * 
     * Validate Element.addChildElement
     * 
     * @throws SAXException
     */

    @Test
    public void testElement_addChildElementNormal() throws SAXException {

	Element child2 = new Element("Child", null, "Child2").addAttribute(
		"attr", "2");

	Element element = new Element("ElementName", null, "ElementValue")

	.addChildElement(new Element("Child", null, "Child1"))

	.addChildElement(child2);

	element.addChildElement(new Element("Child", element, "Child3"));

	Assert.assertEquals(3, element.getChildren().get("Child").size());

    }

    @Test
    public void testElement_isEmpty() throws SAXException {

	Element element = new Element("ElementName");

	Assert.assertTrue(element.isEmpty());

	element.setValue("SomeValue");

	Assert.assertFalse(element.isEmpty());

	element = new Element("ElementName").addAttribute("Attr1", " \n");

	Assert.assertTrue(element.isEmpty());

	element = element.addAttribute("Attr2", "Something");

	Assert.assertFalse(element.isEmpty());

	element = new Element("ElementName").addChildElement(new Element(
		"child"));

	Assert.assertTrue(element.isEmpty());

	element.addChildElement(new Element("Child", element, "Child3"));

	Assert.assertFalse(element.isEmpty());

    }

    @Test
    public void testElement_getLevel() {

	Element element = new Element("ElementName", null, "ElementValue");

	Element child = new Element("ChildName", element, "first");

	Element anotherChild = new Element("ChildName", element, "second");

	Element grandChild = new Element("GrandChildName", child, "grand");

	Assert.assertEquals(element.getLevel(), 0);

	Assert.assertEquals(child.getLevel(), 1);

	Assert.assertEquals(anotherChild.getLevel(), 1);

	Assert.assertEquals(grandChild.getLevel(), 2);

    }

    @Test
    public void testElement_toString() throws SAXException {

	Element element = new Element("ElementName", null, "ElementValue");

	Assert.assertEquals(element.toString(),
		"<ElementName>ElementValue</ElementName>");

	Assert.assertEquals(element.toString(2, true, true),
		"        <ElementName>ElementValue</ElementName>");

	Assert.assertEquals(element.toString(2, false, false),
		"        <ElementName>ElementValue</ElementName>");

	element.setValue(null);

	Assert.assertEquals(element.toString(), "<ElementName/>");

	element.setValue("><&\"\'\"");

	Assert.assertEquals(element.toString(),
		"<ElementName>&gt;&lt;&amp;&quot;&apos;&quot;</ElementName>");

	element.setValue("");

	Assert.assertEquals(element.toString(), "<ElementName/>");

	Element child = new Element("ChildName", element, "first");

	element.setValue("ElementValue");

	Assert.assertEquals(
		element.toString(),
		"<ElementName>\n    <ChildName>first</ChildName>\n    ElementValue\n</ElementName>");

	element.setValue("");

	Assert.assertEquals(element.toString(),
		"<ElementName>\n    <ChildName>first</ChildName>\n</ElementName>");

	Element anotherChild = new Element("ChildName", element, "second");

	Assert.assertEquals(
		element.toString(),
		"<ElementName>\n    <ChildName>first</ChildName>\n    <ChildName>second</ChildName>\n</ElementName>");

	Element grandChild = new Element("GrandChildName", child, "grand");

	Assert.assertEquals(
		element.toString(),
		"<ElementName>\n    <ChildName>\n        <GrandChildName>grand</GrandChildName>\n"

			+ "        first\n    </ChildName>\n    <ChildName>second</ChildName>\n</ElementName>");

	grandChild.addAttribute("attr", "value");

	Assert.assertEquals(
		element.toString(),
		"<ElementName>\n    <ChildName>\n        <GrandChildName attr=\"value\">grand</GrandChildName>\n"

			+ "        first\n    </ChildName>\n    <ChildName>second</ChildName>\n</ElementName>");

    }

    @Test
    public void testElement_getValuesOf() throws SAXException {

	Element element = new Element("ElementName", null, "ElementValue")
		.addAttribute("attr", "slxl");

	Element child = new Element("ChildName", element, "first");

	Element anotherChild = new Element("ChildName", element, "second")
		.addAttribute("attr", "ofAnother");

	Element grandChild = new Element("GrandChildName", child, "grand")
		.addAttribute("abc", "ofGrandChild");

	String[] values = element.getValuesOf("ChildName");

	Assert.assertEquals(2, values.length);

	values = element.getValuesOf(Element.Value);

	Assert.assertEquals(values[0], "ElementValue");

	values = element.getValuesOf("attr");

	Assert.assertEquals(values[0], "slxl");

	values = anotherChild.getValuesOf("Nonexisted");

	Assert.assertNull(values);

	values = element.getValuesOf("ChildName>GrandChildName");

	Assert.assertEquals(1, values.length);

	Assert.assertEquals(values[0], "grand");

	values = child.getValuesOf("GrandChildName");

	Assert.assertEquals(1, values.length);

	Assert.assertEquals(values[0], "grand");

	values = anotherChild.getValuesOf("GrandChildName");

	Assert.assertNull(values);

	values = element.getValuesOf("ChildName<attr");

	Assert.assertNull(values[0]);

	Assert.assertEquals(values[1], "ofAnother");

	values = element.getValuesOf("ChildName>GrandChildName<abc");

	Assert.assertEquals(values[0], "ofGrandChild");

	values = grandChild.getValuesOf("attr");

	Assert.assertNull(values);

    }

    @Test
    public void testElement_setValuesOf() throws SAXException {

	Element element = new Element("ElementName", null, "ElementValue")
		.addAttribute("abc", "attrValue");

	element.setValuesOf(Element.Value, "NewElementValue");

	Assert.assertEquals(element.getValue(), "NewElementValue");

	element.setValuesOf("abc", "NewAttrValue");

	Assert.assertEquals(element.getAttributeValue("abc"), "NewAttrValue");

	element.addAttribute("attr1", "attr1value&");

	element.setValuesOf("attr1", "NewAttrValue1");

	Assert.assertEquals(element.getAttributeValue("attr1"), "NewAttrValue1");

	Element child = new Element("ChildName", element, "fi>rst");

	Element anotherChild = new Element("ChildName", element, "second");

	String[] values = element.getValuesOf("ChildName");

	element.setValuesOf("ChildName", "1", "2");

	values = element.getValuesOf("ChildName");

	Assert.assertTrue(Arrays.asList(values).contains("1"));

	Assert.assertTrue(Arrays.asList(values).contains("2"));

	child.addAttribute("att", "attValue");

	element.setValuesOf("ChildName<att", "newAttr");

	values = element.getValuesOf("ChildName<att");

	Assert.assertEquals(values[0], "newAttr");

	Assert.assertNull(values[1]);

	Assert.assertNull(anotherChild.getAttributeValue("att"));

	element.setValuesOf("ChildName<att", "1234", "");

	values = element.getValuesOf("ChildName<att");

	Assert.assertEquals(values[0], "1234");

	Assert.assertEquals(values[1], "");

	Assert.assertTrue(element.setValuesOf("ddd", ""));

	Assert.assertTrue(element
		.setValuesOf("NewChild>NewGrand<attr", "sidks"));

	values = element.getValuesOf("NewChild>NewGrand<attr");

	Assert.assertEquals(values[0], "sidks");

	Assert.assertTrue(element.setValuesOf(Element.Value, ""));

	Assert.assertTrue(element.setValuesOf("NewChild>NewGrand>More", "1st",
		"2nd"));

	values = element.getValuesOf("NewChild>NewGrand>More");

	Assert.assertEquals(values[0], "1st");

	Assert.assertTrue(element.setValuesOf("Child>GrandChild<Id", "1st",
		"2nd"));

	values = element.getValuesOf("Child>GrandChild<Id");

	Assert.assertEquals(values[0], "1st");

	Assert.assertTrue(element.setValuesOf("Child>GrandChild<Id", "1", "2",
		"3"));

	values = element.getValuesOf("Child>GrandChild<Id");

	Assert.assertEquals(values[2], "3");

    }

    /**
     * 
     * Test of Element.containsElement().
     */

    @Test
    public void testElement_containsElement() {

	Element element = new Element("ElementName", null, "ElementValue");

	Element child = new Element("ChildName", element, "first");

	Element anotherChild = new Element("ChildName", element, "second");

	Element grandChild = new Element("GrandChildName", child, "grand");

	Element grandGrandChild = new Element("GrandGrandChildName",
		grandChild, "last");

	Assert.assertTrue(element.containsElement("ChildName"));

	Assert.assertTrue(element.containsElement("ChildName>GrandChildName"));

	Assert.assertTrue(element
		.containsElement("ChildName>GrandChildName>GrandGrandChildName"));

	Assert.assertTrue(child.containsElement("GrandChildName"));

	Assert.assertTrue(child
		.containsElement("GrandChildName>GrandGrandChildName"));

	Assert.assertFalse(anotherChild.containsElement("GrandChildName"));

	element.setValuesOf("ChildName>GrandChildName>NewNode", "1", "2", "3");

    }

}
