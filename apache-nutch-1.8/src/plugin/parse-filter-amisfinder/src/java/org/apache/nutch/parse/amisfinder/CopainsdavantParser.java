package org.apache.nutch.parse.amisfinder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.nutch.parse.Parse;
import org.apache.nutch.util.NodeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

/**
 * Parser dedicated to Copainsdavant site.
 * 
 * @author avigier
 * 
 */
public class CopainsdavantParser extends AbstractAmisfinderParser {

	private static final Logger LOG = LoggerFactory
			.getLogger(CopainsdavantParser.class);

	/**
	 * Default constructor.
	 */
	public CopainsdavantParser() {
		// initialization of the blacklisted urls
		// TODO Find a better way, perhaps a whitelist applied before the parsing
		// @see RegexRule
		this.blacklistedUrls
				.add("http://copainsdavant\\.linternaute\\.com/glossary/users.*");
		this.blacklistedUrls
				.add("http://copainsdavant\\.linternaute\\.com/recherche-amis/");
		this.blacklistedUrls
				.add("http://copainsdavant\\.linternaute\\.com/recherche-amis/\\?page=\\d+&total=\\d+");
	}

	/**
	 * 
	 * @param parent
	 * @param parse
	 */
	protected static void parsePeople(Node parent, Parse parse) {

		long date = System.currentTimeMillis();

		NodeWalker walker = new NodeWalker(parent);

		while (walker.hasNext()) {

			NodeWrapper n = new NodeWrapper(walker.nextNode());
			NodeWrapper result = null;

			// First name
			if ((result = n.matchesTagWithAttribute("meta", "property",
					"profile:first_name")) != NodeWrapper.NULL_NODE) {
				String value = result.getAttributeValue("content");
				addMetadata(parse, CommonMetadata.META_PEOPLE_FIRST_NAME, value);
				walker.skipChildren();
			} else
			// Last name
			if ((result = n.matchesTagWithAttribute("meta", "property",
					"profile:last_name")) != NodeWrapper.NULL_NODE) {
				String value = result.getAttributeValue("content");
				addMetadata(parse, CommonMetadata.META_PEOPLE_LAST_NAME, value);
				walker.skipChildren();
			} else
			// gender
			if ((result = n.matchesTagWithAttribute("meta", "property",
					"profile:gender")) != NodeWrapper.NULL_NODE) {
				String value = result.getAttributeValue("content");
				addMetadata(parse, CommonMetadata.META_PEOPLE_GENDER, value);
				walker.skipChildren();
			} else
			// city
			if ((result = n
					.matchesTagWithAttribute("span", "class", "locality")) != NodeWrapper.NULL_NODE) {
				String value = result.getTextContent();
				addMetadata(parse, CommonMetadata.META_PEOPLE_CITY, value);
				walker.skipChildren();

			} else
			// country
			if ((result = n.matchesTagWithAttribute("span", "class",
					"country-name")) != NodeWrapper.NULL_NODE) {
				String value = result.getTextContent();
				addMetadata(parse, CommonMetadata.META_PEOPLE_COUNTRY, value);
				walker.skipChildren();
			} else
			// birth date
			if ((result = n.matchesTagWithContent("h4", "Né le :")) != NodeWrapper.NULL_NODE) {
				walker.skipChildren();

				// Normally the birth date is after the specific title
				NodeWrapper next = n.findNextElement(walker, "p");

				if (next != NodeWrapper.NULL_NODE) {
					String value = next.getTextContent();
					Pattern pattern = Pattern.compile(".*(\\d{4}).*");
					Matcher matcher = pattern.matcher(value);
					if (matcher.matches() && matcher.groupCount() == 1) {
						addMetadata(parse, CommonMetadata.META_PEOPLE_BIRTH_DATE,
								matcher.group(1));
					}
				}
				walker.skipChildren();
			}

		}

		LOG.info("Extraction took: " + (System.currentTimeMillis() - date));
	}

	/**
	 * 
	 * @param parent
	 * @param parse
	 */
	protected static void parseWantedPeople(Node parent, Parse parse) {

		long date = System.currentTimeMillis();

		NodeWalker walker = new NodeWalker(parent);

		while (walker.hasNext()) {

			NodeWrapper n = new NodeWrapper(walker.nextNode());
			NodeWrapper result = null;

			// Searched people
			if ((result = n.matchesTagWithAttribute("h1", "class",
					"linter_title_1")) != NodeWrapper.NULL_NODE) {
				String value = result.getTextContent();
				addMetadata(parse, CommonMetadata.META_WANTED_PEOPLE, value);
				walker.skipChildren();
			} else
			// Wanted request owner (searcher)
			if ((result = n.matchesTagWithAttribute("div", "class",
					"grid_left w40").getChildWithTag("div", "class", "grid_row").getChildWithTag("div", "class", "grid_last").getChildWithTag("a")) != NodeWrapper.NULL_NODE) {
				String value = result.getTextContent();
				addMetadata(parse, CommonMetadata.META_SEARCHER_PEOPLE, value);
				walker.skipChildren();
			} else
			// Description of the people searched
			if ((result = n.matchesTagWithAttribute("div", "class", "typeComment_wanted")) != NodeWrapper.NULL_NODE) {
				String value = result.getTextContent();
				addMetadata(parse, CommonMetadata.META_DESCRIPTION, value);
				walker.skipChildren();
			}
		}

		LOG.info("Extraction took: " + (System.currentTimeMillis() - date));
	}

	@Override
	public Parse parse(Node parent, Parse parse, String url) {
		// System.out.println(parent.getNodeName());
		if (LOG.isDebugEnabled())
			LOG.debug("Parsing a copainsdavant page");

		if (!this.canProceedUrl(url))
			return null;

		if (url.contains("http://copainsdavant.linternaute.com/p/")) {
			parsePeople(parent, parse);
		} else if (url.contains("http://copainsdavant.linternaute.com/recherche-amis")){
			parseWantedPeople(parent, parse);
		}

		return parse;
	}

}
