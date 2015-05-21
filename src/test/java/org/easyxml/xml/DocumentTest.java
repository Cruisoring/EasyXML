/*
 * Created on May 16, 2015
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

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.easyxml.parser.EasySAXParser;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

/**
 * 
 * @author William JIANG
 * 
 *         19 May 2015
 * 
 * 
 * 
 * @version $Id$
 */

public class DocumentTest {

    public static final String SoapEnvelope = "SOAP:Envelop";

    public static final String SoapHeader = "SOAP:Header";

    public static final String SoapBody = "SOAP:Body";

    Document soapDoc = null;

    @Test
    public void testDocument_composeSOAP() throws SAXException {

	soapDoc = (Document) new Document(SoapEnvelope)

		.addAttribute("xmlns:SOAP",
			"http://schemas.xmlsoap.org/soap/envelope/")

		.addAttribute("xmlns:SOAP_ENC",
			"http://schemas.xmlsoap.org/soap/encoding/")

		.addChildElement(new Element(SoapHeader));

	// This is equal to Element soapBody = new Element(SoapBody, soapDoc);

	soapDoc.addChildElement(new Element(SoapBody));

	// Create a new empty node under SoapBody

	soapDoc.setValuesOf(SoapBody + ">SampleSoapRequest");

	// Set this new node as the default container for following operations.

	soapDoc.setDefaultContainerByPath(SoapBody + ">SampleSoapRequest");

	// Now the path would be relative to the default container, and add
	// three User with ID/Password defined as their attributes

	soapDoc.setValuesOf("Credential>User<ID", "test01", "test02", "test03");

	// Use null as placeholder, that won't set Password attribute to the
	// second user

	soapDoc.setValuesOf("Credential>User<Password", "password01", null,
		"password03");

	soapDoc.setValuesOf("Credential>URL", "http:\\localhost:8080");

	// The existing element can also be used to append new Attribute

	soapDoc.setValuesOf("Credential<ValidDays", "33");

	// Attributes would be allocated to different elements

	soapDoc.setValuesOf("RequestData<ID", "item1038203", "s893hwkldja");

	soapDoc.setValuesOf("RequestData<Date", null, "12-12-2005");

	// While text would be appended to the first matched container element
	// "RequestData"

	soapDoc.setValuesOf("RequestData>Text", "DSOdji 23 djusu8 adaad adssd",
		"Another text");

	System.out.println(soapDoc.toString());

	/*
	 * Output is show below:
	 * 
<?xml version="1.0" encoding="UTF-8"?>
<SOAP:Envelop xmlns:SOAP="http://schemas.xmlsoap.org/soap/envelope/" xmlns:SOAP_ENC="http://schemas.xmlsoap.org/soap/encoding/">
    <SOAP:Header/>
    <SOAP:Body>
        <SampleSoapRequest>
            <Credential ValidDays="33">
                <User ID="test01" Password="password01"/>
                <User ID="test02"/>
                <User ID="test03" Password="password03"/>
                <URL>http:\localhost:8080</URL>
            </Credential>
            <RequestData ID="item1038203">
                <Text>DSOdji 23 djusu8 adaad adssd</Text>
                <Text>Another text</Text>
            </RequestData>
            <RequestData ID="s893hwkldja" Date="12-12-2005"/>
        </SampleSoapRequest>
    </SOAP:Body>
</SOAP:Envelop>
	 */

    }

    @Test
    public void testDocument_mapOfDeepElement() throws SAXException {

	if (soapDoc == null) {

	    testDocument_composeSOAP();

	}

	List<? extends Map<String, String>> maps = soapDoc
		.mapOf("Credential>User");

	System.out.println(maps);

	/*
	 * Output is shown below:
	 * 
[{ID=test01, Password=password01}, {ID=test02}, {ID=test03, Password=password03}]
	 */

    }

    @Test
    public void testDocument_mapOf() {

	URL url = Thread.currentThread().getContextClassLoader()
		.getResource("books.xml");

	Document doc = EasySAXParser.parse(url);

	List<? extends Map<String, String>> maps = doc.mapOf("book");

	System.out.println(maps.get(0));

	System.out.println(maps.get(1));

	/*
	 * Output is show below (the output order may be different on your machine).
	 * Notice that the first map has no entry of 'price' when the XML part is commented out.
	 * 
{author=Gambardella, Matthew, genre=Computer, description=An in-depth look at creating applications 
      with XML., id=bk101, title=XML Developer's Guide, publish_date=2000-10-01}
{author=Ralls, Kim, price=5.95, genre=Fantasy, description=A former architect battles corporate zombies, 
      an evil sorceress, and her own childhood to become queen of the world., id=bk102, title=Midnight Rain, publish_date=2000-12-16}
	 */

    }

    @Test
    public void testDocument_mapOf_WithAliasSpecified() {

	URL url = Thread.currentThread().getContextClassLoader()
		.getResource("books.xml");

	Document doc = EasySAXParser.parse(url);

	Map<String, String> pathes = new LinkedHashMap<String, String>();

	pathes.put("id", "ID");

	pathes.put("author", "AUTHOR");

	pathes.put("title", "TITLE");

	pathes.put("genre", "GENRE");

	pathes.put("price", "PRICE");

	pathes.put("publish_date", "DATE");

	//pathes.put("description", "DESCRIPTION");

	pathes.put("author", "AUTHOR");

	List<? extends Map<String, String>> maps = doc.mapOf("book", pathes);

	System.out.println(maps.get(0));

	System.out.println(maps.get(1));

	/*
	 * Output is show below (the output order may be different on your machine).
	 * Notice that the first map has no entry of 'price' when the XML part is commented out.
	 * 
{DATE=2000-10-01, TITLE=XML Developer's Guide, GENRE=Computer, ID=bk101, AUTHOR=Gambardella, Matthew}
{DATE=2000-12-16, PRICE=5.95, TITLE=Midnight Rain, GENRE=Fantasy, ID=bk102, AUTHOR=Ralls, Kim}
	 */

    }
    
    /**
     * This test demonstrates how convenient it could be to create a new XML document based on some existing template.
     */
    @Test
    public void testDocument_createNewBasedOnExisting() {

	URL url = Thread.currentThread().getContextClassLoader()
		.getResource("books.xml");

	Document doc = EasySAXParser.parse(url);
	List<Element> books = doc.getElementsOf("book");
	
	Document newDoc = new Document("books");
	for (int i = 0; i < 4; i ++) {
	    newDoc.addChildElement(books.get(i));
	}
	
	newDoc.setValuesOf("book<id", "101", "102", "103", "104");
	newDoc.setValuesOf("book>description", "", "", "", "");

	String outcomeXml = newDoc.toString(0, false, false);
	System.out.println(outcomeXml);
	
//	Document brandnewDoc = EasySAXParser.parseText(outcomeXml);
//	System.out.print(brandnewDoc.toString());

	/*
	 * Output is shown below:
<books>
    <book id="101">
        <author>Gambardella, Matthew</author>
        <title>XML Developer&apos;s Guide</title>
        <genre>Computer</genre>
        <publish_date>2000-10-01</publish_date>
    </book>
    <book id="102">
        <author>Ralls, Kim</author>
        <title>Midnight Rain</title>
        <genre>Fantasy</genre>
        <price>5.95</price>
        <publish_date>2000-12-16</publish_date>
    </book>
    <book id="103">
        <author>Corets, Eva</author>
        <title>Maeve Ascendant</title>
        <genre>Fantasy</genre>
        <price>5.95</price>
        <publish_date>2000-11-17</publish_date>
    </book>
    <book id="104">
        <author>Corets, Eva</author>
        <title>Oberon&apos;s Legacy</title>
        <genre>Fantasy</genre>
        <price>5.95</price>
        <publish_date>2001-03-10</publish_date>
    </book>
</books>
	 */

    }


}