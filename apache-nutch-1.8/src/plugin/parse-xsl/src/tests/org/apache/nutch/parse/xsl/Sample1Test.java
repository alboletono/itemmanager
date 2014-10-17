package org.apache.nutch.parse.xsl;

import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.parse.xsl.XslParseFilter.PARSER;

/**
 * 
 * This sample test will show you how to test the crawling of a page by
 * simulating a crawl. All the thing that you have to do is to inherit from
 * AbstractCrawlTest.
 * 
 */
public class Sample1Test extends AbstractCrawlTest {

	/**
	 * Loads the rules xml file that will route your transformers from urls.
	 */
	public Sample1Test() {
		this.getConfiguration().set(RulesManager.CONF_XML_RULES, "src/plugin/parse-xsl/src/tests/files/sample1/rules.xml");
	}

	public void testBook1() {
		String url = "http://www.sample1.com/book?1245";

		try {
			ParseResult parseResult = simulateCrawl(PARSER.NEKO, "src/plugin/parse-xsl/src/tests/files/sample1/book1.html", url);
			assertNotNull(parseResult);

			Metadata parsedMetadata = parseResult.get(url).getData().getParseMeta();
			// Asserts we have metadata
			assertNotNull(parsedMetadata);
			// Title check
			assertEquals("Nutch for dummies", parsedMetadata.get("title"));
			// Description check
			assertEquals("The ultimate book to master all nutch powerful mechanisms !", parsedMetadata.get("description"));
			// Isbn check
			assertEquals("123654987789", parsedMetadata.get("isbn"));
			// Authors check
			assertEquals("Mr Allan A.", parsedMetadata.getValues("author")[0]);
			assertEquals("Mrs Mulan B.", parsedMetadata.getValues("author")[1]);
			// Price check
			assertEquals("free", parsedMetadata.get("price"));
			// Collection check
			assertEquals("Collection from nowhere", parsedMetadata.get("collection"));

		} catch (Exception e) {
			e.printStackTrace();
			fail("testBook1 exception");
		}
	}

}
