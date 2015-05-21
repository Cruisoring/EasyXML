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
package org.easyxml.util;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/*
 *    Created on May 18, 2015 by William JIANG
 */
public class Utility {
    public static final String JSON_OBJECT_START = "{";
    public static final String JSON_OBJECT_END = "}";
    public static final String JSON_ARRAY_START = "[";
    public static final String JSON_ARRAY_END = "]";
    public static final String JSON_SPLITTER = ", ";
    public static final String JSON_PAIR_SPLITTER = JSON_SPLITTER + "\n";
    public static final String JSON_NAME_VALUE_FORMAT = "\"%s\":\"%s\""  + JSON_PAIR_SPLITTER;
    public static final String JSON_NAME_ARRAY_FORMAT = "\"%s\":[%s]" + JSON_PAIR_SPLITTER;

    public static final String JSON_DOUBLE_QUOTE = "\"";
    public static final String JSON_COLON = ":";
    
    public static List<? extends Map<String, String>> normalize(Map<String, String[]> mappedValues) {

	if (mappedValues == null) {
	    return null;
	}

	int size = mappedValues.size();
	String[] names = new String[size];
	String[][] values = new String[size][];
	Iterator<Entry<String, String[]>> iterator = mappedValues.entrySet().iterator();

	int i = 0;
	size = -1;
	while (iterator.hasNext()) {
	    Entry<String, String[]> next = iterator.next();
	    names[i] = next.getKey();
	    values[i] = next.getValue();
	    if (size == -1) {
		size = values[i].length;
	    } else {
		if (size != values[i].length) {
		    throw new InvalidParameterException("Mismatched values size!");
		}
	    }
	    i++;
	}

	ArrayList<LinkedHashMap<String, String>> result = new ArrayList<LinkedHashMap<String, String>>();

	for (i = 0; i < size; i++) {
	    LinkedHashMap<String, String> map = new LinkedHashMap<>();
	    for (int j = 0; j < names.length; j++) {
		map.put(names[j], values[j][i]);
	    }
	    result.add(map);
	}
	return result;
    }

    public static String toJSON(Map<String, String> map) {
	if (map == null)
	    return null;
	else if (map.size() == 0)
	    return "{}";

	// Append the object name if it is identified by key of an empty String
	StringBuilder jsonBuilder = new StringBuilder(map.containsKey("") ? map.get("") : "");
	jsonBuilder.append(JSON_OBJECT_START);
	for (Map.Entry<String, String> entry : map.entrySet()) {
	    String key = entry.getKey();
	    String value = entry.getValue();
	    if (key.length() != 0) {
		// Simply guess by detecting of "[" and "]"
		// TODO: More robust way
		String format = (value.startsWith(JSON_ARRAY_START) && value.endsWith(JSON_ARRAY_END))
			? JSON_NAME_ARRAY_FORMAT : JSON_NAME_VALUE_FORMAT;

		jsonBuilder.append(String.format(format, key, entry.getValue()));
	    }
	}

	// Remove the last JSON_PAIR_SPLITTER
	if (jsonBuilder.indexOf(JSON_PAIR_SPLITTER) != -1) {
	    jsonBuilder.setLength(jsonBuilder.length() - JSON_PAIR_SPLITTER.length());
	}

	jsonBuilder.append(JSON_OBJECT_END);
	return jsonBuilder.toString();
    }

    public static String toJSON(String objectName, Object[] objects) {
	if (objectName == null || objects == null)
	    return null;

	String json = String.format(JSON_NAME_VALUE_FORMAT, objectName, toJSON(objects));

	return json;
    }

    public static String toJSON(Object[] objects) {
	StringBuilder jsonBuilder = new StringBuilder();
	int length = objects.length;
	for (int i = 0; i < length; i++) {
	    jsonBuilder.append(String.format("\"%s\"", objects[i].toString()));
	    if (i != length - 1) {
		jsonBuilder.append(JSON_SPLITTER);
	    }
	}

	return jsonBuilder.toString();
    }
}
