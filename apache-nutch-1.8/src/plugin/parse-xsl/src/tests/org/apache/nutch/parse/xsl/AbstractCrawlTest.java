package org.apache.nutch.parse.xsl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.hadoop.conf.Configuration;
import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.HtmlParseFilter;
import org.apache.nutch.parse.ParseData;
import org.apache.nutch.parse.ParseImpl;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.parse.amisfinder.AmisfinderParseFilterXsl.PARSER;
import org.apache.nutch.parse.html.DOMBuilder;
import org.apache.nutch.protocol.Content;
import org.apache.nutch.util.NutchConfiguration;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DocumentFragment;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A class to group all classic methods to simulate a crawl without running
 * Nutch like setting a configuration, providing a DocumentFragment, etc...
 * 
 * @author avigier
 * 
 */
public abstract class AbstractCrawlTest extends TestCase {

	/** The logger used for current and derived classes */
	protected static final Logger LOG = LoggerFactory.getLogger(AbstractCrawlTest.class);
	
	/** the configuration to use with current crawler 
	 * Never access this property. @see AbstractCrawlTest#getConfiguration() */
	private Configuration configuration = null;

	/**
	 * @param parseFilter the filter to use
	 * @param filePath
	 * @param url
	 * @return the resulting content after the crawl
	 * @throws Exception 
	 */
	protected ParseResult simulateCrawl(PARSER parseFilter, String filePath, String url) throws Exception {
		ParseResult result = null;
		FileInputStream is = null;
		try {
			// Opening test file
			File file = new File(filePath);
			is = new FileInputStream(file);
			byte[] bytes = new byte[0];

			//config.addResource(new Path("src/plugin/parse-filter-amisfinder/src/tests/files/configuration.xml"));

			// Setting the void content
			Content content = new Content(url, "", bytes, "text/html", new Metadata(), this.getConfiguration());

			// Parse document with related parser
			DocumentFragment document = null;
			if (parseFilter == PARSER.NEKO) {
				document = parseNeko(new InputSource(is));
				
			} else {
				document = parseTagSoup(new InputSource(is));
			}
			
			// Creates a parser with dedicated method 
			HtmlParseFilter filter = new XslParseFilter();

			ParseData data = new ParseData();

			// Initializing the parse result
			ParseResult parseResult = ParseResult.createParseResult(url, new ParseImpl("no text", data));

			// Setting configuration
			filter.setConf(this.getConfiguration());

			// Extracting metadata
			result = filter.filter(content, parseResult, null, document);
		} catch (Exception e) {
			throw new Exception("Cannot simulate crawl", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					LOG.error("Cannot close input stream", e);
				}
			}
		}
		return result;
	}

	/**
	 * Constructs a an html DOM structure that can be browsed using walker.
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	private static DocumentFragment parseTagSoup(InputSource input) throws Exception {
		HTMLDocumentImpl doc = new HTMLDocumentImpl();
		DocumentFragment frag = doc.createDocumentFragment();
		DOMBuilder builder = new DOMBuilder(doc, frag);
		org.ccil.cowan.tagsoup.Parser reader = new org.ccil.cowan.tagsoup.Parser();
		reader.setContentHandler(builder);
		reader.setFeature(org.ccil.cowan.tagsoup.Parser.ignoreBogonsFeature, true);
		reader.setFeature(org.ccil.cowan.tagsoup.Parser.bogonsEmptyFeature, false);
		reader.setProperty("http://xml.org/sax/properties/lexical-handler", builder);
		reader.parse(input);
		return frag;
	}

	private static DocumentFragment parseNeko(InputSource input) throws Exception {
		DOMFragmentParser parser = new DOMFragmentParser();
		try {
			parser.setFeature("http://cyberneko.org/html/features/scanner/allow-selfclosing-iframe", true);
			parser.setFeature("http://cyberneko.org/html/features/augmentations", true);
			parser.setProperty("http://cyberneko.org/html/properties/default-encoding", "UTF-8");
			parser.setFeature("http://cyberneko.org/html/features/scanner/ignore-specified-charset", true);
			parser.setFeature("http://cyberneko.org/html/features/balance-tags/ignore-outside-content", false);
			parser.setFeature("http://cyberneko.org/html/features/balance-tags/document-fragment", true);
			parser.setFeature("http://cyberneko.org/html/features/balance-tags", true);
			parser.setFeature("http://cyberneko.org/html/features/report-errors", true);
			parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");

			System.out.println(LOG.isTraceEnabled());

		} catch (SAXException e) {
			LOG.error("Cannot set parser features", e);
		}
		// convert Document to DocumentFragment
		HTMLDocumentImpl doc = new HTMLDocumentImpl();
		doc.setErrorChecking(false);
		DocumentFragment res = doc.createDocumentFragment();
		DocumentFragment frag = doc.createDocumentFragment();
		parser.parse(input, frag);
		res.appendChild(frag);

		try {
			while (true) {
				frag = doc.createDocumentFragment();
				parser.parse(input, frag);
				if (!frag.hasChildNodes())
					break;
				// if (LOG.isInfoEnabled()) {
				LOG.info(" - new frag, " + frag.getChildNodes().getLength() + " nodes.");
				System.out.println(" - new frag, " + frag.getChildNodes().getLength() + " nodes.");
				// }
				res.appendChild(frag);
			}
		} catch (Exception e) {
			LOG.error("Error: ", e);
			System.out.println(e);
		}

		return res;
	}
	
	/**
	 * 
	 * @return the current configuration.
	 */
	public Configuration getConfiguration() {
		if (this.configuration == null) {
			this.configuration = NutchConfiguration.create();
		}
		return this.configuration;
	}

}
