package org.apache.nutch.parse.amisfinder;

import java.util.ArrayList;

import org.apache.nutch.parse.Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The common parser that will implements basic and common methods.
 * 
 * @author avigier
 * 
 */
public abstract class AbstractAmisfinderParser implements IAmisfinderParser {

	/** Urls that won't be parsed but outlinks will be extracted */
	protected ArrayList<String> blacklistedUrls = null;
	private static final Logger LOG = LoggerFactory
			.getLogger(AbstractAmisfinderParser.class);

	/**
	 * Default constructor.
	 */
	public AbstractAmisfinderParser() {
		this.blacklistedUrls = new ArrayList<String>();
	}

	/**
	 * @param url the url to check
	 * @return true if the url is to be proceeded, false otherwise.
	 */
	protected boolean canProceedUrl(String url) {
		boolean result = true;

		if (this.blacklistedUrls != null && url != null) {
			for (String pattern : this.blacklistedUrls) {
				result = !url.matches(pattern);
				if (result == false) {
					LOG.info(String
							.format("The url %s matches the pattern %s so it is not parsed",
									url, pattern));
					break;
				}
			}
		}

		return result;
	}

	/**
	 * Adds the given metadata if the value is not null.
	 * 
	 * @param parse
	 *            the parse object to fill with new metadata
	 * @param name
	 *            the new metadata name
	 * @param value
	 *            the new metadata value
	 */
	protected static void addMetadata(Parse parse, String name, String value) {
		if (value != null && value.length() > 0) {
			parse.getData().getParseMeta().add(name, value.trim());
		}
	}
}
