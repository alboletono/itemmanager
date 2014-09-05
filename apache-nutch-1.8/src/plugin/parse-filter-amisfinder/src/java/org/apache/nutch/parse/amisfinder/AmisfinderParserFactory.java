package org.apache.nutch.parse.amisfinder;


/**
 * This factory will instanciate the right parser according to the url.
 * 
 * @author avigier
 * 
 */
public class AmisfinderParserFactory {

	private static IAmisfinderParser copainsdavantParser = null;
	
	private static IAmisfinderParser trombiParser = null;
	
	/**
	 * 
	 * @param url
	 *            of the page to parse.
	 * @return the related parser.
	 */
	public static IAmisfinderParser createParser(String url) {
		IAmisfinderParser parser = null;

		if (url != null) {
			if (url.contains("copainsdavant.linternaute.com")) {
				parser = getCopainsdavantParser(url);
			} else if (url.contains("trombi.com")) {
				parser = getTrombiParser(url);
			}
		}

		return parser;

	}

	/**
	 * @param url the url to parse.
	 * @return the parser dedicated to copainsdavant.
	 */
	private static IAmisfinderParser getCopainsdavantParser(String url) {
		if (copainsdavantParser == null) {
			copainsdavantParser = new CopainsdavantParser();
		}
		return copainsdavantParser;
	}

	/**
	 * @param url the url to parse.
	 * @return the parser dedicated to trombi.
	 */
	private static IAmisfinderParser getTrombiParser(String url) {
		if (trombiParser == null) {
			trombiParser = new JeTeRechercheParser();
		}
		return trombiParser;
	}
	
}
