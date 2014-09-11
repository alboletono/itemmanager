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
package org.apache.nutch.parse.xsl;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.bind.JAXB;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.PropertyConfigurator;
import org.apache.nutch.parse.HTMLMetaTags;
import org.apache.nutch.parse.HtmlParseFilter;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.parse.ParseText;
import org.apache.nutch.parse.amisfinder.AmisfinderParseFilter;
import org.apache.nutch.parse.amisfinder.xml.Documents;
import org.apache.nutch.parse.amisfinder.xml.TContentMeta;
import org.apache.nutch.parse.amisfinder.xml.TDocument;
import org.apache.nutch.parse.html.HtmlParser;
import org.apache.nutch.protocol.Content;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;

import com.sun.org.apache.xpath.internal.XPathAPI;

/**
 * This is a parse filter plugin (@see HtmlParseFilter) A class to apply an xsl
 * transformation on an html page. Instead of coding java, a simple xpath can be
 * used.
 * 
 */
public class XslParseFilter implements HtmlParseFilter {

	/** Specifies wether to use html parse TagSoup or NekoHtml */
	public enum PARSER {
		/** TagSoup parser */
		TAGSOUP {
			@Override
			public String toString() {
				return "tagsoup";
			}
		},
		/** Neko parser */
		NEKO {
			@Override
			public String toString() {
				return "neko";
			}
		}
	}

	/**
	 * The output of the tranformation for debug purpose (log level "DEBUG"
	 * shall be activated)
	 */
	public static final String CONF_XSLT_OUTPUT_DEBUG_FILE = "amisfinder.xslt.output.debug.file";

	/** The XSLT file to use for transformation */
	public static final String CONF_XSLT_FILE = "amisfinder.xslt.file";

	/** Wether to use Saxon or Standard JVM XSLT parser */
	public static final String CONF_XSLT_USE_SAXON = "amisfinder.xslt.useSaxon";

	/**
	 * Wether to use Neko or Tagsoup.
	 * 
	 * @Warning this configuration property is set by nutch and not by the
	 *          current plugin.
	 * @see HtmlParser
	 */
	public static final String CONF_HTML_PARSER = "parser.html.impl";

	static {
		PropertyConfigurator.configure("conf/log4j.properties");
	}

	private static final Logger LOG = LoggerFactory.getLogger(AmisfinderParseFilter.class);

	private Configuration conf;

	/**
	 * @param content
	 *            full content to parse
	 * @param parseResult
	 *            result of the parse process
	 * @param metaTags
	 *            metatags set in the document
	 * @param document
	 *            the DOM document to parse
	 * @return the resulting {@link ParseResult}
	 */
	@Override
	public ParseResult filter(Content content, ParseResult parseResult, HTMLMetaTags metaTags, DocumentFragment document) {

		try {
			// We are selecting the HTML tag with a XPath to convert the
			// DocumentFragment to a more natural
			// HTML document that can be further processed with XSL.
			String xpath = "//html";

			// For neko, all tags are UPPER CASE.
			// For tagsoup, it is in lower case.
			// This is decided by the html parser plugin
			if (this.conf.get(CONF_HTML_PARSER, PARSER.NEKO.toString()).equals(PARSER.NEKO.toString())) {
				xpath = xpath.toUpperCase();
			} else {
				throw new Exception("tag soup parser not implemented.");
			}

			Node doc = XPathAPI.selectSingleNode(document, xpath);

			Parse parse = parseResult.get(content.getUrl());

			// Do we use saxon for xslt 2.0 compliancy?
			if (this.getConf().getBoolean(CONF_XSLT_USE_SAXON, false)) {
				System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
			}

			String xsltFileName = this.conf.get(CONF_XSLT_FILE);
			if (xsltFileName == null) {
				throw new Exception("You have to define the " + CONF_XSLT_FILE + " configuration parameter");
			}

			// Loading the XSL used for transformation
			Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xsltFileName));

			DOMResult result = new DOMResult();
			// At this state, thanks to the HtmlParser that is using
			// HtmlParseFilter interface, we got
			// a DOM object properly built (with Neko or TagSoup).
			transformer.transform(new DOMSource(doc), result);

			// Storing the xml output for debug purpose
			if (LOG.isDebugEnabled()) {
				String debugFile = this.conf.get(CONF_XSLT_OUTPUT_DEBUG_FILE);
				if (debugFile != null)
					XslParseFilter.saveDOMOutput(result.getNode(), new File(debugFile));
			}

			XslParseFilter.updateMetadata(result.getNode(), parse);

			// Setting updated metadata.
			parseResult.put(content.getUrl(), new ParseText(parse.getText()), parse.getData());

		} catch (Exception e) {
			LOG.warn("Cannot extract HTML tags. The XSL processing will not be run.", e);
		}

		return parseResult;
	}

	/**
	 * @param node
	 *            the node that is used to provide metadata information.
	 * @param data
	 *            the data to update This is a simple format like the following:
	 *            {@literal 
	 *            <documents> 
	 *            	<document>
	 *            		<content name="meta1">value1</content>
	 *            		...
	 *            		<content name="metaN">valueN</content>
	 *            	</document>
	 *            </documents>
	 * 			  }
	 */
	protected static void updateMetadata(Node node, Parse data) {

		Documents documents = JAXB.unmarshal(new DOMSource(node), Documents.class);

		// No document unmarshalled
		if (documents == null) {
			LOG.debug("No metadata to update");
			return;
		}

		// Browsing documents
		for (TDocument document : documents.getDocument()) {

			// There are metadata to process
			for (TContentMeta content : document.getContentMeta()) {
				String value = content.getValue();
				if (value != null)
					value.trim();
				data.getData().getParseMeta().add(content.getName(), value);
			}
		}

	}

	/**
	 * 
	 * @param node
	 *            the DOM node to save.
	 * @param file
	 *            the file where to write the DOM.
	 */
	private static void saveDOMOutput(Node node, File file) {
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(file);

			TransformerFactory.newInstance().newTransformer().transform(new DOMSource(node), new StreamResult(fos));
		} catch (Exception e) {
			LOG.warn("Cannot store DOM node to file: " + file.getAbsolutePath(), e);
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (Exception e) {
					LOG.warn("Cannot close xml file stream.", e);
				}
		}
	}

	/**
	 * @param conf
	 *            the configuration file to load.
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
