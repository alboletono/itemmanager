/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nutch.parse.marmiton;

import java.io.File;
import java.io.FileInputStream;

import junit.framework.TestCase;

import org.apache.hadoop.conf.Configuration;
import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.ParseData;
import org.apache.nutch.parse.ParseImpl;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.parse.html.DOMBuilder;
import org.apache.nutch.protocol.Content;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DocumentFragment;
import org.xml.sax.InputSource;

/**
 * A simple class to extract some data from a http://www.marmiton.com page. This
 * is dedicated to meta extraction for recipes.
 */
public class TestMarmitonParseFilter extends TestCase {
	public static final Logger LOG = LoggerFactory
			.getLogger(TestMarmitonParseFilter.class);

	public void testMarmiton1() throws Exception {
		MarmitonParseFilter filter = new MarmitonParseFilter();

		System.out.println("coucouc");
		// Opening test file
		File file = new File(
				"src/plugin/parse-filter-marmiton/src/tests/files/marm1.html");
		System.out.println(file.getAbsolutePath());
		FileInputStream is = new FileInputStream(file);
		byte[] bytes = new byte[0];
		Content content = new Content("marmiton1", "", bytes, "text/html",
				new Metadata(), new Configuration());

		DocumentFragment doc = parseTagSoup(new InputSource(is));

		ParseData data = new ParseData();

		ParseResult parseResult = ParseResult.createParseResult("marmiton1",
				new ParseImpl("no text", data));
		filter.filter(content, parseResult, null, doc);

		// Testing title
		String title = data.getContentMeta().get(
				MarmitonParseFilter.MARMITON_TITLE);
		assertEquals("Lasagnes basques", title);

		String preptime = data.getContentMeta().get(
				MarmitonParseFilter.MARMITON_PREPTIME);
		assertEquals("PT60M", preptime);

		String cooktime = data.getContentMeta().get(
				MarmitonParseFilter.MARMITON_COOKTIME);
		assertEquals("PT40M", cooktime);

		String peopleNumber = data.getContentMeta().get(
				MarmitonParseFilter.MARMITON_PEOPLE_NUMBER);
		assertEquals("4", peopleNumber);

		//
	}

	private DocumentFragment parseTagSoup(InputSource input) throws Exception {
		HTMLDocumentImpl doc = new HTMLDocumentImpl();
		DocumentFragment frag = doc.createDocumentFragment();
		DOMBuilder builder = new DOMBuilder(doc, frag);
		org.ccil.cowan.tagsoup.Parser reader = new org.ccil.cowan.tagsoup.Parser();
		reader.setContentHandler(builder);
		reader.setFeature(org.ccil.cowan.tagsoup.Parser.ignoreBogonsFeature,
				true);
		reader.setFeature(org.ccil.cowan.tagsoup.Parser.bogonsEmptyFeature,
				false);
		reader.setProperty("http://xml.org/sax/properties/lexical-handler",
				builder);
		reader.parse(input);
		return frag;
	}

}
