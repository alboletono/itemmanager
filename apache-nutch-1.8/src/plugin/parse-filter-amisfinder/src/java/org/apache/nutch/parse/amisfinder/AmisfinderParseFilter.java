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
package org.apache.nutch.parse.amisfinder;

import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.PropertyConfigurator;
import org.apache.nutch.parse.HTMLMetaTags;
import org.apache.nutch.parse.HtmlParseFilter;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.parse.ParseText;
import org.apache.nutch.protocol.Content;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DocumentFragment;

/**
 * A simple class to extract some data from a http://www.marmiton.com page. This
 * is dedicated to meta extraction for recipes.
 */
public class AmisfinderParseFilter implements HtmlParseFilter {

	static {
		PropertyConfigurator.configure("conf/log4j.properties");
	}

	private static final Logger LOG = LoggerFactory
			.getLogger(AmisfinderParseFilter.class);

	private Configuration conf;


	
	/**
	 * @param content
	 * @param parseResult
	 * @param metaTags
	 * @param doc
	 * @return the resulting {@link ParseResult}
	 */
	@Override
	public ParseResult filter(Content content, ParseResult parseResult,
			HTMLMetaTags metaTags, DocumentFragment doc) {

		Parse parse = parseResult.get(content.getUrl());
	
		IAmisfinderParser parser = AmisfinderParserFactory.createParser(content.getUrl());
		
		parse = parser.parse(doc, parse, content.getUrl());
		
		// This url has been black listed (used for links but not for parsing content)
		if (parse == null) {
			LOG.info(String.format("url %s is not parsed", content.getUrl()));
		} else {
			// Overriding the parse result with old data + new metadata
			parseResult.put(content.getUrl(), new ParseText(parse.getText()),
					parse.getData());
		}
		
		return parseResult;
	}

	/**
	 * @param conf the configuration file to load.
	 */
	@Override
	public void setConf(Configuration conf) {
		this.conf = conf;
	}

	/**
	 * @return the current configuration.
	 */
	@Override
	public Configuration getConf() {
		return this.conf;
	}
}
