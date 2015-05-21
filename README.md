#EasyXML#

[EasyXML](https://github.com/Cruisoring/EasyXML) is a compact JAVA XML library means to provide three funtions:

1. Compose well formed XML documents with simplified components and approaches.
2. Convert XML content as list of String Maps.
3. Generate XML documents based on existing XML template.
4. Convert XML to JSON.


##Create XML with Easy##
[EasyXML](https://github.com/Cruisoring/EasyXML) makes creating complex XML documents efficient. For example, for the function listed below:

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
    }

An XML Document could be generated like this:

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

With popular [Document Object Model (DOM)](http://docs.oracle.com/javase/7/docs/api/org/w3c/dom/package-summary.html), there are multiple types of node defined, as well as multiple functions to be called to get the very same DOM tree, such as [createAttribute(String name)](http://docs.oracle.com/javase/7/docs/api/org/w3c/dom/Document.html#createAttribute(java.lang.String)) and [Element createElement(String tagName)](http://docs.oracle.com/javase/7/docs/api/org/w3c/dom/Document.html#createElement(java.lang.String)). However, it could be error-prone to create every single attribute/element/textNode by calling at least one method, especially when it is not easy to monitor the changes of the DOM tree because the DOM objects have not exposed how the final XML document/element looks like.

With EasyXML, the XML documents are simplified dramatically by concerning only about [XML Element](http://www.w3schools.com/xml/xml_elements.asp) and [XML Attribute](http://www.w3schools.com/xml/xml_attributes.asp). Conventionally, the value of XML Element is always null and text between opening and closing tags of an Element is defined as [Text](http://docs.oracle.com/javase/7/docs/api/org/w3c/dom/Text.html), EasyXML treats textual content, if not blank, as the value of its direct parent XML Element, and all other nodes (Entity, Notation and etc.) are simply ignored. Accordingly, EasyXML defines two classes: Element and Attribute. Attribute is simply a name value pair of an Element; an Element could contain multiple Attributes, multiple children Elements and a value of String type that could be null (by default, with no innerText) or a single non-blank String text. To locate a specific type of ELement or Attribute, there are two kinds of path:

+ Element Path by concat Element names with `>`:

    For instance, *root`>`body`>`message* denotes Elements of <*message*>, whose parent is Elements of <*body*> under the <*root*>.

+ Attribute Path by concat Element path and Attribute name with `<`:
    
    For example, *root`>`body`>`message`<`id* denotes the **id** attribute of all Elements of <*root*><*body*><*message*>.

In addition, EasyXML is designed to be debugging-friendly: you can clearly see how the Element or Document would look like with a well-formatted fashion. That is, the ELement would be displayed just as how it is shown in the XML document.

##Parse XML to String Maps

Although it could bring some convenience to create an XML document from scratch, compared with operating conventional DOM objects, it could still be tedious and less straightforward than modifying values of an existing XML template to get a new XML document. Actually, this is also possible with EasyXML. However, to achieve that goal, EasyXML must be capable of parsing XML effectively.

This is actually my initial intention to develop this toolkit to carry out some SOAP service API test: with another data mining framework, the test data input to the SOAP requests and outcome from the responses is presented as one or multiple Map<String, String> instances, if the SOAP responses could be converted to a list of Map<String, String>, comparing them by iterating the keys could be stream-lined and much easier.

With current EasyXML, you can extract specific content like the example below shows how the [Microsoft Sample XML File (books.xml)](https://msdn.microsoft.com/en-us/library/ms762271%28v=vs.85%29.aspx) could be handled:

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
    }

The output would be (notice there are 12 IDs, but only 11 prices):

	ID: [bk101, bk102, bk103, bk104, bk105, bk106, bk107, bk108, bk109, bk110, bk111, bk112]
	PRICE: [5.95, 5.95, 5.95, 5.95, 4.95, 4.95, 4.95, 6.95, 36.95, 36.95, 49.95]

However, the Sting matrix could be hard to translated to objects, such as the books that have properties like ID and PRICE. 

Converting each object to a standalone map would make it more sensible, and it is very simple: parsing an XML document could be implemented by merely calling a function with only one String parameter to specify the target Element. For the previous books.xml, you can specify the Elements to be parsed as "**book**":

    @Test
    public void testDocument_mapOf() {
	
		URL url = Thread.currentThread().getContextClassLoader()
			.getResource("books.xml");
	
		Document doc = EasySAXParser.parse(url);
	
		List<? extends Map<String, String>> maps = doc.mapOf("book");
	
		System.out.println(maps.get(0));
	
		System.out.println(maps.get(1));
    }

The result is 12 Map<String, String> for the 12 books, for the first two XML `<book>` elements shown below:

	   <book id="bk101">
	      <author>Gambardella, Matthew</author>
	      <title>XML Developer's Guide</title>
	      <genre>Computer</genre>
	<!--       <price>44.95</price> -->
	      <publish_date>2000-10-01</publish_date>
	      <description>An in-depth look at creating applications 
	      with XML.</description>
	   </book>
	   <book id="bk102">
	      <author>Ralls, Kim</author>
	      <title>Midnight Rain</title>
	      <genre>Fantasy</genre>
	      <price>5.95</price>
	      <publish_date>2000-12-16</publish_date>
	      <description>A former architect battles corporate zombies, 
	      an evil sorceress, and her own childhood to become queen 
	      of the world.</description>
	   </book>

The corresponding first two Map<String, String> are printed out:

	{author=Gambardella, Matthew, genre=Computer, description=An in-depth look at creating applications 
	      with XML., id=bk101, title=XML Developer's Guide, publish_date=2000-10-01}
	{author=Ralls, Kim, price=5.95, genre=Fantasy, description=A former architect battles corporate zombies, 
	      an evil sorceress, and her own childhood to become queen of the world., id=bk102, title=Midnight Rain, publish_date=2000-12-16}

Straightforward and no need to worry that some `<book>` have more or less properties than others.

In addition, supposing that you have already defined how to map Element/Attribute to some standard keys, then you can call the method with a Map to include entries concerned and how to generate keys of the outcome like this:

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
    }

Then the first two books would be presented as following two maps:

	{DATE=2000-10-01, TITLE=XML Developer's Guide, GENRE=Computer, ID=bk101, AUTHOR=Gambardella, Matthew}
	{DATE=2000-12-16, PRICE=5.95, TITLE=Midnight Rain, GENRE=Fantasy, ID=bk102, AUTHOR=Ralls, Kim}

Notice the first parameter of `"book"` could be replaced with any kind of Element, then their attributes/childElements would be used to compose the Map<> automatically.

For the SOAP document generated in section 1 for instance:

    @Test
    public void testDocument_mapOfDeepElement() throws SAXException {
	
		if (soapDoc == null) {
	
		    testDocument_composeSOAP();
	
		}
	
		List<? extends Map<String, String>> maps = soapDoc
			.mapOf("Credential>User");
	
		System.out.println(maps);
    }

The path of `Credential>User` would be matched by `<SampleSoapRequest>` - the defaultContainer of the Document, then it is matched with the `<User>` elements to get mapping of their contents:

	[{ID=test01, Password=password01}, {ID=test02}, {ID=test03, Password=password03}]

##XML from Template

As mentioned earlier, using XML could be easier if without needing to compose an XML document from scratch, especially if there are already some template available.

EasyXML uses either conventional DOM or SAX parser to convert an XML to its own Document objects. URL of the XML source file is preferred: you can put the .DTD along with the .XML file and both DOM and SAX parser can validate the schema automatically by calling the static function of either DomParser or EasySAXParser:

	Document doc = EasySAXParser.parse(url);

Or

	Document doc = DomParser.parse(url);

Then you can use the Elements of the parsed Document to build new Documents conveniently like the example below:

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
    }

The `newDoc` instance is composed by the first four `<book>` Elements of the parsed Document `doc`, then changing their `id` and clearing `description` could be carried out in batches to get the following output:

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

Notice that for the `newDoc` Document, its `<book>` Elements still contain some empty `<description>` nodes, because EasyXML, for simplicity, has not implemented removing/inserting/renaming Attributes or Child Elements yet. However, `newDoc.toString(0, false, false)` could prevent them from being displayed.

For the same reason, the original `doc` Document would still hold 12 `<book>` Elements and the first four would be shared with the the `newDoc` Document.

In this way, it is fully possible to "borrow" and "tailor" Elements from multiple Documents parsed from multiple .XML files to compose an extremely complex Document, and if you are really worried about the hidden Element like those `<descripttion>` nodes, then the commented `EasySAXParser.parseText(outcomeXml)` would provide a standalone Document effortlessly.

##Convert XML to JSON

EasyXML can also be used as a tools to convert XML Element/Document to JSON strings.

JsonTest.java provides enough samples. Taken [glossary.xml](http://json.org/example) as example, following codes:

    @Test
    public void testDocument_toJSON4() {

		URL url = Thread.currentThread().getContextClassLoader()
			.getResource("glossary.xml");
	
		Document doc = EasySAXParser.parse(url);
	
		String json = doc.toJSON();
	
		System.out.println(json);
    }

would get this output:

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

It is also possible to just convert some specific Elements to JSON with predefined keys like this:

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

Output:

		{book{"ID":"bk101", 
		"AUTHOR":"Gambardella, Matthew", 
		"TITLE":"XML Developer's Guide", 
		"GENRE":"Computer"},
		book{"ID":"bk102", 
		"AUTHOR":"Ralls, Kim", 
		"TITLE":"Midnight Rain", 
		"GENRE":"Fantasy"},
		book{"ID":"bk103", 
		"AUTHOR":"Corets, Eva", 
		"TITLE":"Maeve Ascendant", 
		"GENRE":"Fantasy"},
		book{"ID":"bk104", 
		"AUTHOR":"Corets, Eva", 
		"TITLE":"Oberon's Legacy", 
		"GENRE":"Fantasy"},
		book{"ID":"bk105", 
		"AUTHOR":"Corets, Eva", 
		"TITLE":"The Sundered Grail", 
		"GENRE":"Fantasy"},
		book{"ID":"bk106", 
		"AUTHOR":"Randall, Cynthia", 
		"TITLE":"Lover Birds", 
		"GENRE":"Romance"},
		book{"ID":"bk107", 
		"AUTHOR":"Thurman, Paula", 
		"TITLE":"Splish Splash", 
		"GENRE":"Romance"},
		book{"ID":"bk108", 
		"AUTHOR":"Knorr, Stefan", 
		"TITLE":"Creepy Crawlies", 
		"GENRE":"Horror"},
		book{"ID":"bk109", 
		"AUTHOR":"Kress, Peter", 
		"TITLE":"Paradox Lost", 
		"GENRE":"Science Fiction"},
		book{"ID":"bk110", 
		"AUTHOR":"O'Brien, Tim", 
		"TITLE":"Microsoft .NET: The Programming Bible", 
		"GENRE":"Computer"},
		book{"ID":"bk111", 
		"AUTHOR":"O'Brien, Tim", 
		"TITLE":"MSXML3: A Comprehensive Guide", 
		"GENRE":"Computer"},
		book{"ID":"bk112", 
		"AUTHOR":"Galos, Mike", 
		"TITLE":"Visual Studio 7: A Comprehensive Guide", 
		"GENRE":"Computer"}}

That is quite different from the output of the codes below:

    @Test
    public void testDocument_toJSON3() {

		URL url = Thread.currentThread().getContextClassLoader()
			.getResource("books.xml");
	
		Document doc = EasySAXParser.parse(url);
	
		String json = doc.toJSON();
	
		System.out.println(json);
    }

That is:
	
	"catalog":{
	    "book":[
	        {
	            "id": "bk101",
	            "author":"Gambardella, Matthew",
	            "title":"XML Developer's Guide",
	            "genre":"Computer",
	            "publish_date":"2000-10-01",
	            "description":"An in-depth look at creating applications 
	      with XML."
	        },
			...Omitted here........
	        {
	            "id": "bk112",
	            "author":"Galos, Mike",
	            "title":"Visual Studio 7: A Comprehensive Guide",
	            "genre":"Computer",
	            "price":"49.95",
	            "publish_date":"2001-04-16",
	            "description":"Microsoft Visual Studio 7 is explored in depth,
	      looking at how Visual Basic, Visual C++, C#, and ASP+ are integrated into a comprehensive development 
	      environment."
	        }
	    ]
	}

This is the end of the brief applications of EasyXML.