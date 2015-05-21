/*
 * Created on May 17, 2015
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
package org.easyxml.util;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import org.easyxml.parser.EasySAXParser;
import org.easyxml.xml.Document;
import org.testng.annotations.Test;

/**
 * 
 * @author William JIANG
 * 
 *         17 May 2015
 * 
 * @version $Id$
 */

public class JsonTest {

    @Test
    public void testDocument_toJSON() {

	URL url = Thread.currentThread().getContextClassLoader()
		.getResource("books.xml");

	Document doc = EasySAXParser.parse(url);
	
	Map<String, String> pathes = new LinkedHashMap<String, String>();	
	pathes.put("ID", "book<id");	
	pathes.put("AUTHOR", "book>author");	
	pathes.put("TITLE", "book>title");	
	pathes.put("GENRE", "book>genre");

	String json = doc.toJSON("book", pathes);	
	System.out.println(json);
    }

    @Test
    public void testDocument_toJSON2() {

	URL url = Thread.currentThread().getContextClassLoader()
		.getResource("books.xml");

	Document doc = EasySAXParser.parse(url);

	String json = doc.toJSON("book");

	System.out.println(json);
    }

    @Test
    public void testDocument_toJSON3() {

	URL url = Thread.currentThread().getContextClassLoader()
		.getResource("books.xml");

	Document doc = EasySAXParser.parse(url);

	String json = doc.toJSON();

	System.out.println(json);
    }

    /**
     * 
     * ****************************************************************
     * 
     * The following tests are based on XML and JSON samples on
     * http://json.org/example
     */

    @Test
    public void testDocument_toJSON4() {

	URL url = Thread.currentThread().getContextClassLoader()
		.getResource("glossary.xml");

	Document doc = EasySAXParser.parse(url);

	String json = doc.toJSON();

	System.out.println(json);

	/*
	 * 
	 * With output like this:
"glossary":{
    "title":"example glossary",
    "GlossDiv":{
        "title":"S",
        "GlossList":{
            "GlossEntry":{
                "ID": "SGML",
                "SortAs": "SGML",
                "GlossTerm":"Standard Generalized Markup Language",
                "Acronym":"SGML",
                "Abbrev":"ISO 8879:1986",
                "GlossDef":{
                    "para":"A meta-markup language, used to create markup
						languages such as DocBook.",
                    "GlossSeeAlso":[
                        {
                            "OtherTerm": "GML"
                        },
                        {
                            "OtherTerm": "XML"
                        }
                    ]
                },
                "GlossSee":{
                    "OtherTerm": "markup"
                }
            }
        }
    }
}
	 */

    }

    @Test
    public void testDocument_toJSON5() {

	URL url = Thread.currentThread().getContextClassLoader()
		.getResource("menu.xml");

	Document doc = EasySAXParser.parse(url);

	String json = doc.toJSON();

	System.out.println(json);

	/*
	 * 
	 * With output like this:
"menu":{
    "id": "file",
    "value": "File",
    "popup":{
        "menuitem":[
            {
                "value": "New",
                "onclick": "CreateNewDoc()"
            },
            {
                "value": "Open",
                "onclick": "OpenDoc()"
            },
            {
                "value": "Close",
                "onclick": "CloseDoc()"
            }
        ]
    }
}
	 */

    }

    @Test
    public void testDocument_toJSON6() {

	URL url = Thread.currentThread().getContextClassLoader()
		.getResource("widget.xml");

	Document doc = EasySAXParser.parse(url);

	String json = doc.toJSON();

	System.out.println(json);

	/*
	 * 
	 * With output like this:
"widget":{
    "debug":"on",
    "window":{
        "title": "Sample Konfabulator Widget",
        "name":"main_window",
        "width":"500",
        "height":"500"
    },
    "image":{
        "src": "Images/Sun.png",
        "name": "sun1",
        "hOffset":"250",
        "vOffset":"250",
        "alignment":"center"
    },
    "text":{
        "data": "Click Here",
        "size": "36",
        "style": "bold",
        "name":"text1",
        "hOffset":"250",
        "vOffset":"100",
        "alignment":"center",
        "onMouseUp":"sun1.opacity = (sun1.opacity / 100) * 90;"
    }
}
	 */

    }

    /**
     * 
     * This is to validate toJSON() works with complex XML file
     */

    @Test
    public void testDocument_toJSON7() {

	URL url = Thread.currentThread().getContextClassLoader()
		.getResource("app.xml");

	Document doc = EasySAXParser.parse(url);

	String json = doc.toJSON();

	System.out.println(json);

    }

}
