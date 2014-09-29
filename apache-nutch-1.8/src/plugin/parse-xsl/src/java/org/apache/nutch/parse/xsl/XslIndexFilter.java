package org.apache.nutch.parse.xsl;

import java.util.HashMap;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.indexer.IndexingException;
import org.apache.nutch.indexer.IndexingFilter;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.parse.Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * This class allows to:
 * <ul>
 * <li>index automatically fields defined in rules file.
 * <li>exlude urls that are not declared in the rules file.
 */
public class XslIndexFilter implements IndexingFilter {

	private Configuration conf;

	private static final Logger LOG = LoggerFactory.getLogger(XslParseFilter.class);

	private static HashMap<String, List<String>> transformers = new HashMap<String, List<String>>();

	/**
	 * @return the current configuration.
	 */
	@Override
	public Configuration getConf() {
		return this.conf;
	}

	/**
	 * Sets the current configuration.
	 */
	@Override
	public void setConf(Configuration conf) {
		this.conf = conf;
	}

	@Override
	public NutchDocument filter(NutchDocument doc, Parse parse, Text url, CrawlDatum datum, Inlinks inlinks) throws IndexingException {

		NutchDocument result = null;

		try {

			// Rules manager that contains url corresponding transformer.
			RulesManager manager = RulesManager.getInstance(this.conf);

			// Getting transformer file path associated to rule if exists
			String xsltFilePath = manager.getTransformerFilePath(url.toString());

			// The url matches a rule, we keep it
			if (xsltFilePath != null) {
				List<String> fields = XslIndexFilter.transformers.get(xsltFilePath);
				
				
				
			}

		} catch (Exception e) {
			String message = "Cannot index data";
			if (url != null && url.toString() != null) {
				message += " from " + url.toString();
			}
			LOG.error(message, e);
		}

		return result;
	}

}
