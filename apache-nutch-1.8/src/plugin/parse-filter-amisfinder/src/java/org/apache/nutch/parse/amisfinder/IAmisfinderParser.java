package org.apache.nutch.parse.amisfinder;

import org.apache.nutch.parse.Parse;
import org.w3c.dom.Node;

/**
 * Common interface to parse pages from different sites.
 * @author avigier
 *
 */
public interface IAmisfinderParser {

	/**
	 * 
	 * @param parent the html root node
	 * @param parse the object containing all already parsed data.
	 * @param url the url to parse
	 * @return The Parse object is returned with additional data if any, null otherwise.
	 */
	Parse parse(Node parent, Parse parse, String url);

}
