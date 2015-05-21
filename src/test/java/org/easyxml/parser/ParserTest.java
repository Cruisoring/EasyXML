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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.easyxml.xml.Document;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * 
 * @author William JIANG
 * 
 *         15 May 2015
 * 
 * 
 * 
 * @version $Id$
 */

public class ParserTest {

    /**
     * 
     * Provide names of the xml files for evaluation.
     * 
     * @see 
     *      <a>https://msdn.microsoft.com/en-us/library/ms762271(v=vs.85).aspx</a
     *      >
     * 
     * @see 
     *      <a>http://docs.oracle.com/cd/B10501_01/appdev.920/a96621/adx04paj.htm
     *      </a>
     * 
     * @see 
     *      <a>http://www.oracle.com/technetwork/articles/jain-xmldb-086052.html<
     *      /a>
     * 
     * @return
     */

    @DataProvider
    private Object[][] getTestXMLFilename() {

	return new Object[][] {

	{ "books.xml" }

	, { "class.xml" }

	, {"employee.xml"}

	, {"family.xml"}

	};

    }

    @Test(dataProvider = "getTestXMLFilename")
    public void testEasySAXParser_parse(String xmlFilename) throws IOException,
	    URISyntaxException {

	URL url = Thread.currentThread().getContextClassLoader()
		.getResource(xmlFilename);

	Document doc = EasySAXParser.parse(url);

	String output = doc.toString();

    }

    @Test
    public void testEasySAXParser_parseWithDtd() throws IOException,
	    URISyntaxException {

	URL url = Thread.currentThread().getContextClassLoader()
		.getResource("family.xml");

	Document doc = EasySAXParser.parse(url);

	String output = doc.toString();

    }

    @Test(dataProvider = "getTestXMLFilename")
    public void testDOMParser_parse(String xmlFilename) throws IOException,
	    URISyntaxException {

	URL url = Thread.currentThread().getContextClassLoader()
		.getResource(xmlFilename);

	Document doc = DomParser.parse(url);

	String output = doc.toString();

	// Document docFromSAX = EasySAXParser.parse(url);

	// Assert.assertEquals(output, docFromSAX.toString());

    }

    @Test
    public void testDOMParser_extractInfo() {

	URL url = Thread.currentThread().getContextClassLoader()
		.getResource("books.xml");

	Document doc = EasySAXParser.parse(url);

	Map<String, String> pathes = new LinkedHashMap<String, String>();

	pathes.put("book<id", "ID");

	pathes.put("book>author", "AUTHOR");

	pathes.put("book>title", "TITLE");

	pathes.put("book>genre", "GENRE");

	pathes.put("book>price", "PRICE");

	pathes.put("book>publish_date", "DATE");

	pathes.put("book>description", "DESCRIPTION");

	pathes.put("book>author", "AUTHOR");

	Map<String, String[]> values = doc.extractValues(pathes);

	System.out.println(String.format("%s: %s", "ID",
		"[" + StringUtils.join(values.get("ID"), ", ") + "]"));

	System.out.println(String.format("%s: %s", "PRICE",
		"[" + StringUtils.join(values.get("PRICE"), ", ") + "]"));

	/*
	 * Output is shown below, notice that there are only 11 prices for the
	 * 12 books.
	 * 
	 * ID: [bk101, bk102, bk103, bk104, bk105, bk106, bk107, bk108, bk109,
	 * bk110, bk111, bk112]
	 * 
	 * PRICE: [5.95, 5.95, 5.95, 5.95, 4.95, 4.95, 4.95, 6.95, 36.95, 36.95,
	 * 49.95]
	 */

    }

}
