package org.apache.nutch.parse.amisfinder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
public class TestCopainsdavantParse extends TestCase {

	/** The logger */
	public static final Logger LOG = LoggerFactory
			.getLogger(TestCopainsdavantParse.class);

	/**
	 * 
	 * @param filePath
	 * @param url
	 * @return the resulting content after the crawl
	 */
	private static ParseResult simulateCrawl(String filePath, String url) {
		ParseResult result = null;
		FileInputStream is = null;
		try {
			// Opening test file
			File file = new File(filePath);
			is = new FileInputStream(file);
			byte[] bytes = new byte[0];

			// Setting the void content
			Content content = new Content(url, "", bytes, "text/html",
					new Metadata(), new Configuration());

			// Initializing html document
			DocumentFragment document = parseTagSoup(new InputSource(is));
			ParseData data = new ParseData();

			// Initializing the parse result
			ParseResult parseResult = ParseResult.createParseResult(url,
					new ParseImpl("no text", data));

			// Extracting metadata
			AmisfinderParseFilter filter = new AmisfinderParseFilter();
			result = filter.filter(content, parseResult, null, document);
		} catch (Exception e) {
			LOG.error("Cannot simulate crawl", e);
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
	 * Asserts that the following metadata have the expected values.
	 * 
	 * @param metadata
	 *            the metadata object containing parsed values.
	 * @param firstName
	 * @param lastName
	 * @param gender
	 * @param city
	 * @param country
	 * @param birthDate
	 */
	protected void assertMetadataPeople(Metadata metadata, String firstName,
			String lastName, String gender, String city, String country,
			String birthDate) {
		// Testing first name
		assertEquals(firstName,
				metadata.get(CommonMetadata.META_PEOPLE_FIRST_NAME));

		// Testing last name
		assertEquals(lastName,
				metadata.get(CommonMetadata.META_PEOPLE_LAST_NAME));

		// Testing gender
		assertEquals(gender,
				metadata.get(CommonMetadata.META_PEOPLE_GENDER));

		// Testing city
		assertEquals(city, metadata.get(CommonMetadata.META_PEOPLE_CITY));

		// Testing country
		assertEquals(country,
				metadata.get(CommonMetadata.META_PEOPLE_COUNTRY));

		// Testing birth date
		assertEquals(birthDate,
				metadata.get(CommonMetadata.META_PEOPLE_BIRTH_DATE));
	}

	/**
	 * 
	 * @param metadata
	 * @param wantedPeople
	 * @param searcherPeople
	 * @param description
	 */
	protected void assertMetadataWantedPeople(Metadata metadata,
			String wantedPeople, String searcherPeople, String description) {

		assertEquals(searcherPeople,
				metadata.get(CommonMetadata.META_SEARCHER_PEOPLE));
		assertEquals(wantedPeople,
				metadata.get(CommonMetadata.META_WANTED_PEOPLE));
		assertEquals(description,
				metadata.get(CommonMetadata.META_DESCRIPTION));
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testPeople1() throws Exception {

		String url = "http://copainsdavant.linternaute.com/p/dominique-a-6984602";
		String filePath = "src/plugin/parse-filter-amisfinder/src/tests/files/copainsdavant/people1.html";
		ParseResult parseResult = simulateCrawl(filePath, url);
		assertNotNull(parseResult);

		Metadata parsedMetadata = parseResult.get(url).getData().getParseMeta();
		assertNotNull(parsedMetadata);

		this.assertMetadataPeople(parsedMetadata, "Dominique", "A.", "male",
				"BRETIGNY SUR ORGE", "France", "1977");
	}

	/**
	 * Wrong url shall be ignored (no data added).
	 * 
	 * @throws Exception
	 */
	public void testPeople2() throws Exception {
		String url = "http://copainsdavant.linternaute.com/glossary/users/a-1";
		String filePath = "src/plugin/parse-filter-amisfinder/src/tests/files/copainsdavant/people1.html";
		ParseResult parseResult = simulateCrawl(filePath, url);
		assertNotNull(parseResult.get(url));
		Metadata parsedMetadata = parseResult.get(url).getData().getParseMeta();
		this.assertMetadataPeople(parsedMetadata, null, null, null, null, null,
				null);

	}

	/**
	 * Tests the fetch of a wanted people.
	 * 
	 * @throws Exception
	 */
	public void testWantedPeople() throws Exception {
		String url = "http://copainsdavant.linternaute.com/recherche-amis/danielle-uchi-6460";
		String filePath = "src/plugin/parse-filter-amisfinder/src/tests/files/copainsdavant/wanted1.html";
		ParseResult parseResult = simulateCrawl(filePath, url);
		assertNotNull(parseResult.get(url));
		Metadata parsedMetadata = parseResult.get(url).getData().getParseMeta();
		this.assertMetadataWantedPeople(
				parsedMetadata,
				"Danielle Uchi",
				"Jean Michel LE CLERC",
				"Jean Michel LE CLERC Pendant les cours, je dessinai un personnage un peu bizarre sur des petits bouts de papier, notre domaine, à la sortie de l'ESD, était Capoulade, le boul. St-Michel... il y a 2 jours");

	}

	/**
	 * Constructs a an html DOM structure that can be browsed using walker.
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	private static DocumentFragment parseTagSoup(InputSource input)
			throws Exception {
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
