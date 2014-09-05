package org.apache.nutch.parse.amisfinder;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.parse.amisfinder.AmisfinderParseFilterXsl.PARSER;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * A simple class to extract some data from a http://www.marmiton.com page. This
 * is dedicated to meta extraction for recipes.
 */
@RunWith(Parameterized.class)
public class CopainsdavantTest extends AbstractCrawlTest {

	private long startDate;
	
	/** The extraction method to use */
	public enum EXTRACT_METHOD {
		/** Metadata are extracted using XSLT */
		XSLT_EXTRACTION,
		/** Metadata are extracted using NodeWalker (DOM iteration) */
		NODE_WALKER_EXTRACTION
	}

	/** The current extract method to use */
	protected EXTRACT_METHOD extract = null;

	/** The current parser to use */
	protected PARSER parser = null;

	/**
	 * 
	 * @param parser
	 *            the parser to use
	 * @param extract
	 *            the extract method to use
	 */
	public CopainsdavantTest(PARSER parser, EXTRACT_METHOD extract) {
		this.parser = parser;
		// Depending of the parameters, the configuration will be built
		// differently */
		if (parser == PARSER.NEKO) {
			this.getConfiguration().set(AmisfinderParseFilterXsl.CONF_HTML_PARSER, PARSER.NEKO.toString());

		} else {
			this.getConfiguration().set(AmisfinderParseFilterXsl.CONF_HTML_PARSER, PARSER.TAGSOUP.toString());
		}
		this.extract = extract;
		if (this.extract == EXTRACT_METHOD.XSLT_EXTRACTION) {
			this.getConfiguration().set(AmisfinderParseFilterXsl.CONF_XSLT_FILE, "src/plugin/parse-filter-amisfinder/src/tests/files/copainsdavant/transformer.xsl");
		}
		
	}

	
	@Parameters(name="{index}: parser {0}, extractor {1}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { PARSER.TAGSOUP, EXTRACT_METHOD.NODE_WALKER_EXTRACTION }, { PARSER.TAGSOUP, EXTRACT_METHOD.XSLT_EXTRACTION },
				{ PARSER.NEKO, EXTRACT_METHOD.NODE_WALKER_EXTRACTION }, { PARSER.NEKO, EXTRACT_METHOD.XSLT_EXTRACTION } });
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
	protected void assertMetadataPeople(Metadata metadata, String firstName, String lastName, String gender, String city, String country, String birthDate) {
		// Testing first name
		assertEquals(firstName, metadata.get(CommonMetadata.META_PEOPLE_FIRST_NAME));

		// Testing last name
		assertEquals(lastName, metadata.get(CommonMetadata.META_PEOPLE_LAST_NAME));

		// Testing gender
		assertEquals(gender, metadata.get(CommonMetadata.META_PEOPLE_GENDER));

		// Testing city
		assertEquals(city, metadata.get(CommonMetadata.META_PEOPLE_CITY));

		// Testing country
		assertEquals(country, metadata.get(CommonMetadata.META_PEOPLE_COUNTRY));

		// Testing birth date
		assertEquals(birthDate, metadata.get(CommonMetadata.META_PEOPLE_BIRTH_DATE));
	}

	/**
	 * 
	 * @param metadata
	 * @param wantedPeople
	 * @param searcherPeople
	 * @param description
	 */
	protected void assertMetadataWantedPeople(Metadata metadata, String wantedPeople, String searcherPeople, String description) {

		assertEquals(searcherPeople, metadata.get(CommonMetadata.META_SEARCHER_PEOPLE));
		assertEquals(wantedPeople, metadata.get(CommonMetadata.META_WANTED_PEOPLE));
		assertEquals(description, metadata.get(CommonMetadata.META_DESCRIPTION));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPeople1() throws Exception {

		String url = "http://copainsdavant.linternaute.com/p/dominique-a-6984602";
		String filePath = "src/plugin/parse-filter-amisfinder/src/tests/files/copainsdavant/people1.html";
		ParseResult parseResult = simulateCrawl(this.parser, this.extract, filePath, url);
		assertNotNull(parseResult);

		Metadata parsedMetadata = parseResult.get(url).getData().getParseMeta();
		assertNotNull(parsedMetadata);

		this.assertMetadataPeople(parsedMetadata, "Dominique", "A.", "male", "BRETIGNY SUR ORGE", "France", "1977");
	}

	/**
	 * Wrong url shall be ignored (no data added).
	 * 
	 * @throws Exception
	 */
	public void testPeople2() throws Exception {
		String url = "http://copainsdavant.linternaute.com/glossary/users/a-1";
		String filePath = "src/plugin/parse-filter-amisfinder/src/tests/files/copainsdavant/people1.html";
		ParseResult parseResult = simulateCrawl(this.parser, this.extract, filePath, url);
		assertNotNull(parseResult.get(url));
		Metadata parsedMetadata = parseResult.get(url).getData().getParseMeta();
		this.assertMetadataPeople(parsedMetadata, null, null, null, null, null, null);

	}

	/**
	 * Tests the fetch of a wanted people.
	 * 
	 * @throws Exception
	 */
	public void testWantedPeople() throws Exception {
		String url = "http://copainsdavant.linternaute.com/recherche-amis/danielle-uchi-6460";
		String filePath = "src/plugin/parse-filter-amisfinder/src/tests/files/copainsdavant/wanted1.html";
		ParseResult parseResult = simulateCrawl(this.parser, this.extract, filePath, url);
		assertNotNull(parseResult.get(url));
		Metadata parsedMetadata = parseResult.get(url).getData().getParseMeta();
		this.assertMetadataWantedPeople(
				parsedMetadata,
				"Danielle Uchi",
				"Jean Michel LE CLERC",
				"Jean Michel LE CLERC Pendant les cours, je dessinai un personnage un peu bizarre sur des petits bouts de papier, notre domaine, à la sortie de l'ESD, était Capoulade, le boul. St-Michel... il y a 2 jours");

	}
	
	private void displayMemoryUsage() {
		Runtime runtime = Runtime.getRuntime();

		NumberFormat format = NumberFormat.getInstance();

		long maxMemory = runtime.maxMemory();
		long allocatedMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();

		System.out.println("free memory: " + format.format(freeMemory / 1024));
		System.out.println("allocated memory: " + format.format(allocatedMemory / 1024));
		System.out.println("max memory: " + format.format(maxMemory / 1024));
		System.out.println("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024));
	}

	private void startTest() {
		System.out.println("Starting test...");
		this.displayMemoryUsage();
		this.startDate = new Date().getTime();
	}

	private void endTest() {
		this.displayMemoryUsage();
		System.out.println("Test took " + (new Date().getTime() - this.startDate) + " ms");
		System.out.println("Test ended.");
	}

}
