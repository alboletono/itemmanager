package org.apache.nutch.parse.amisfinder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.nutch.parse.Parse;
import org.apache.nutch.util.NodeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

/**
 * Parser dedicated to Trombi site.
 * @author avigier
 *
 */
public class JeTeRechercheParser extends AbstractAmisfinderParser {

	private static final Logger LOG = LoggerFactory
			.getLogger(CopainsdavantParser.class);
	
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
			NodeWrapper tableNode = null;

			// All the metadata are under a table
			if ((tableNode = n.matchesTagWithAttribute("table", "class",
					"recherche_personne_fiche_membre_tableau_1").getChildWithTag("tr")) != NodeWrapper.NULL_NODE) {
				
				NodeWrapper result = null;
				String firstName = null;
				
				// Specific first name + last name metadata
				if ((result = tableNode.getChildWithTag("td").getChildWithTag("img")) != NodeWrapper.NULL_NODE) {
				
					firstName = result.getAttributeValue("alt");
					//addMetadata(parse, CommonMetadata.META_PEOPLE_FIRST_NAME, value);
					//walker.skipChildren();
				} else 
				// All other metadata
				if ((tableNode = tableNode.getChildWithTag("td").getChildWithTag("table")) != NodeWrapper.NULL_NODE) {
					
					//
					if ((result = tableNode.getChildWithTag("tr").getChildWithTag("td")) != NodeWrapper.NULL_NODE) {
						
					}
					
				}
					
				}

			}
		/*
        <table border="0" cellpadding="4" cellspacing="0" class="recherche_personne_fiche_membre_tableau_1">
        <tr>
          <td colspan="2" align="center" valign="top" class="index_centre_tableau_10_typo_descriptif"><span class="lien_couleur_bleu_perdue_de_vue_recherche_personne_recherche_objet">
           
           <img src="../jeterecherche_360_graphique/index_centre/Polaroide_homme.png" width="250" alt="Faycel Mejri" />
           
          </span></td>
          <td width="784" rowspan="4" align="left" valign="top" class="lien_couleur_bleu_perdue_de_vue_recherche_personne_recherche_objet"><table width="100%" border="0" cellpadding="4" cellspacing="0">
            <tr>
              <td width="160" align="right" valign="top" class="index_centre_tableau_10_typo_descriptif">Sexe :</td>
              <td width="608" align="left" valign="top" class="index_centre_tableau_10_typo_nom_prenom">masculin</td>
            </tr>
                            <tr>
              <td align="right" class="index_centre_tableau_10_typo_descriptif">Ville :</td>
              <td align="left" class="index_centre_tableau_10_typo_nom_prenom"></td>
            </tr>
            <tr>
              <td align="right" class="index_centre_tableau_10_typo_descriptif">Pr&eacute;nom M&egrave;re :</td>
              <td align="left" class="index_centre_tableau_10_typo_nom_prenom"></td>
            </tr>
            <tr>
              <td align="right" class="index_centre_tableau_10_typo_descriptif">Pr&eacute;nom p&egrave;re :</td>
              <td align="left" class="index_centre_tableau_10_typo_nom_prenom"></td>
            </tr>
            <tr>
              <td align="right" class="index_centre_tableau_10_typo_descriptif">Nom de remariage :</td>
              <td align="left" class="index_centre_tableau_10_typo_nom_prenom"></td>
            </tr>
            <tr>
              <td align="right" valign="top" class="index_centre_tableau_10_typo_descriptif"><span class="style181">Nom de jeune fille :</span></td>
              <td align="left" class="index_centre_tableau_10_typo_nom_prenom"></td>
            </tr>
          </table></td>
        </tr>
      </table>
      */

		LOG.info("Extraction took: " + (System.currentTimeMillis() - date));
	}
	
	@Override
	public Parse parse(Node parent, Parse parse, String url) {
		// System.out.println(parent.getNodeName());
		if (LOG.isDebugEnabled())
			LOG.debug("Parsing a copainsdavant page");

		if (!this.canProceedUrl(url))
			return null;

		// http://www.jeterecherche.com/jeterecherche_2010/recherche_personne/recherche_personne_result.php?login=Mejri
		if (url.contains("http://www.jeterecherche.com/jeterecherche_2010/recherche_personne")) {
			parsePeople(parent, parse);
		} else if (url.contains("http://copainsdavant.linternaute.com/recherche-amis")){
			//parseWantedPeople(parent, parse);
		}

		return parse;
	}


}
