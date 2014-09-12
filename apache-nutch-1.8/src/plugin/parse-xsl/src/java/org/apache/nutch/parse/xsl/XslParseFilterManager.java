package org.apache.nutch.parse.xsl;

import java.io.File;
import java.util.HashMap;

import javax.xml.bind.JAXB;

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.parse.xsl.xml.rule.Rules;
import org.apache.nutch.parse.xsl.xml.rule.TRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class allows to manage a set of XslParseFilter singletons. It allows to
 * avoid having several instances of XslParseFilter with XSL to load each time
 * for performance matter. The decision to use a given XslParseFilter is
 * determined by a set of rules (@see Rules)
 * 
 * @see XslParseFilter
 * @author avigier
 * 
 */
public class XslParseFilterManager {

	/** All the rules used to determine which xsl parser to use */
	protected Rules rules = null;

	/** A map containing all transformers given their file name as key */
	protected HashMap<String, XslParseFilter> filters;

	/** The XSLT file to use for transformation */
	public static final String CONF_XSLT_FILE = "parser.xsl.file";
	
	private static final Logger LOG = LoggerFactory.getLogger(XslParseFilterManager.class);

	/**
	 * Instanciates an object using the apache nutch configuration (that
	 * contains the property defining the rules).
	 * 
	 * @param configuration
	 * @throws Exception
	 */
	public XslParseFilterManager(Configuration configuration) throws Exception {
		super();

		// Getting rules file
		String rulesFile = configuration.get(CONF_XSLT_FILE);
		if (rulesFile == null)
			throw new Exception("The rules file shall be set in your configuration file");

		// Loading rules object
		try {
			this.rules = JAXB.unmarshal(new File(rulesFile), Rules.class);
		} catch (Exception e) {
			throw new Exception("Cannot load the rules file, please check it.", e);
		}

	}

	/**
	 * 
	 * @param url
	 *            the url to filter
	 * @return the xsl parse filter that suits the rules
	 * @throws Exception 
	 */
	public XslParseFilter getParseFilter(String url) throws Exception {
		XslParseFilter filter = null;

		// Search for a matching rule by applying defined regex
		// The first matching rule will be applied
		for (TRule rule : this.rules.getRule()) {
			if (url.matches(rule.getMatches())) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("A rule is matching the regex: " + rule.getMatches());
				}
				String xslFile = rule.getTransformer().getFile();
				// Creating map if needed
				if (this.filters == null) {
					this.filters = new HashMap<String, XslParseFilter>();
				}
				filter = this.filters.get(xslFile);
				// Getting xsl file
				if (filter == null) {
					filter = new XslParseFilter(xslFile);
					this.filters.put(xslFile, filter);
				} 
				break;
			}
		}
		if (filter == null) {
			throw new Exception("No filter found for url: " + url);
		}
		
		return filter;
	}

}
