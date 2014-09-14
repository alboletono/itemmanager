package org.apache.nutch.parse.xsl;

import java.util.Arrays;
import java.util.Collection;

import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.xsl.XslParseFilter.PARSER;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * A simple class to extract some data from a http://www.marmiton.com page. This
 * is dedicated to meta extraction for recipes.
 */
@RunWith(Parameterized.class)
public class JeTeRechercheTest extends AbstractAmisFinder {

	/**
	 * 
	 * @param parser
	 *            the parser to use the extract method to use
	 */
	public JeTeRechercheTest(PARSER parser) {
		super(parser);
		this.getConfiguration().set(XslParseFilterManager.CONF_XSLT_FILE,
				"src/plugin/parse-xsl/src/tests/files/jeterecherche/rules.xml");
	}

	@Parameters(name = "{index}: parser {0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { PARSER.NEKO } });
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPeople() throws Exception {

		Metadata metadata = super
				.startTestPeople(
						"http://www.jeterecherche.com/jeterecherche_2010/recherche_personne/recherche_personne_result.php?login=Mejri&ami=1",
						"src/plugin/parse-xsl/src/tests/files/jeterecherche/people1.html");

		this.assertMetadataPeople(metadata, "laetitia", "GUICHARD", "féminin",
				"montpellier taverny paris", null, null, "33");
	}

	/**
	 * Tests the fetch of a wanted people.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testWantedPeople() throws Exception {

		Metadata metadata = super
				.startTestWantedPeople(
						"http://www.jeterecherche.com/jeterecherche_2010/avis_de_recherche/avis_de_recherche_fiche.php?q_avis=63694",
						"src/plugin/parse-xsl/src/tests/files/jeterecheche/wanted1.html");

		this.assertMetadataWantedPeople(
				metadata,
				"Danielle Uchi",
				"Jean Michel LE CLERC",
				"Jean Michel LE CLERC Pendant les cours, je dessinai un personnage un peu bizarre sur des petits bouts de papier, notre domaine, à la sortie de l'ESD, était Capoulade, le boul. St-Michel... il y a 2 jours");

	}
}
