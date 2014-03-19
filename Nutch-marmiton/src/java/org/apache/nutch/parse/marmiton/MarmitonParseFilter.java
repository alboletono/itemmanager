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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.PropertyConfigurator;
import org.apache.nutch.parse.HTMLMetaTags;
import org.apache.nutch.parse.HtmlParseFilter;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.parse.ParseText;
import org.apache.nutch.protocol.Content;
import org.apache.nutch.util.NodeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;

/**
 * A simple class to extract some data from a http://www.marmiton.com page. This
 * is dedicated to meta extraction for recipes.
 */
public class MarmitonParseFilter implements HtmlParseFilter {
	protected static final String MARMITON_COOKTIME = "marmiton_cooktime";

	protected static final String MARMITON_PREPTIME = "marmiton_preptime";

	protected static final String MARMITON_TITLE = "marmiton_title";

	protected static final String MARMITON_PEOPLE_NUMBER = "marmiton_people_nb";

	static {
		PropertyConfigurator.configure("conf/log4j.properties");
	}

	public static final Logger LOG = LoggerFactory
			.getLogger(MarmitonParseFilter.class);

	private Configuration conf;

	public ParseResult filter(Content content, ParseResult parseResult,
			HTMLMetaTags metaTags, DocumentFragment doc) {

		Parse parse = parseResult.get(content.getUrl());

		// Getting the byte content (different from the text, this is the raw
		// html
		// String bytes = new String(content.getContent());

		// bytes.
		this.extractMetadata(doc, parse);

		// Overriding the parse result with old data + new metadata
		parseResult.put(content.getUrl(), new ParseText(parse.getText()),
				parse.getData());
		return parseResult;
	}

	private void extractMetadata(Node parent, Parse parse) {
		System.out.println(parent.getNodeName());
		LOG.debug("test");
		NodeWalker walker = new NodeWalker(parent);

		while (walker.hasNext()) {

			NodeWrapper n = new NodeWrapper(walker.nextNode());
			NodeWrapper result = null;

			// Title
			if ((result = n.matchesTagWithAttribute("h1", "class", "m_title")) != NodeWrapper.NULL_NODE) {

				parse.getData()
						.getContentMeta()
						.add(MARMITON_TITLE,
								result.getNode().getTextContent().trim());

				walker.skipChildren();
			}

			else
			// Preparation time
			if ((result = n
					.matchesTagWithAttribute("span", "class", "preptime")
					.getChildWithTag("span", "title")) != NodeWrapper.NULL_NODE) {

				parse.getData()
						.getContentMeta()
						.add(MARMITON_PREPTIME,
								result.getNode().getAttributes()
										.getNamedItem("title").getNodeValue());
			} else
			// Cooking time
			if ((result = n
					.matchesTagWithAttribute("span", "class", "cooktime")
					.getChildWithTag("span", "title")) != NodeWrapper.NULL_NODE) {

				parse.getData()
						.getContentMeta()
						.add(MARMITON_COOKTIME,
								result.getNode().getAttributes()
										.getNamedItem("title").getNodeValue());
			}

			else
			// Number of people
			if ((result = n.matchesTagWithAttribute("p", "class",
					"m_content_recette_ingredients")) != NodeWrapper.NULL_NODE) {

				Pattern pattern = Pattern.compile(".*pour (%d) personnes.*");
				Matcher matcher = pattern.matcher(result
						.getChildWithTag("span").getNode().getTextContent()
						.trim());
				if (matcher.find()) {

					parse.getData().getContentMeta()
							.add(MARMITON_PEOPLE_NUMBER, matcher.group(1));

				}
				/*
				 * parse.getData() .getContentMeta()
				 * .add(MARMITON_PEOPLE_NUMBER,
				 * result.getChildWithTag("span").getNode()
				 * .getNodeValue().trim());
				 */
			}
		}

	}

	public static void main(String[] args) throws Exception {
		/*
		 * if (args.length < 2) {
		 * System.err.println(JSParseFilter.class.getName() +
		 * " file.js baseURL"); return; } InputStream in = new
		 * FileInputStream(args[0]); BufferedReader br = new BufferedReader(new
		 * InputStreamReader(in, "UTF-8")); StringBuffer sb = new
		 * StringBuffer(); String line = null; while ((line = br.readLine()) !=
		 * null) sb.append(line + "\n"); br.close();
		 * 
		 * JSParseFilter parseFilter = new JSParseFilter();
		 * parseFilter.setConf(NutchConfiguration.create()); Outlink[] links =
		 * parseFilter.getJSLinks(sb.toString(), "", args[1]);
		 * System.out.println("Outlinks extracted: " + links.length); for (int i
		 * = 0; i < links.length; i++) System.out.println(" - " + links[i]);
		 */
	}

	public void setConf(Configuration conf) {
		this.conf = conf;
	}

	public Configuration getConf() {
		return this.conf;
	}
}
