<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">


	<xsl:template match="/">
		<documents>
			<document>
				<xsl:variable name="extractFirstName" select="//DIV[@id='TabbedPanels1']/UL/LI/SPAN/text()" />
				<xsl:variable name="firstName" select="normalize-space(substring-after($extractFirstName, 'sur'))" />
				<contentMeta name="firstName">
					<xsl:value-of select="$firstName" />
				</contentMeta>

				<contentMeta name="lastName">
					<xsl:variable name="fullName" select="//STRONG[@class='index_centre_tableau_titre_orange']/text()" />
					<xsl:value-of select="normalize-space(substring-after($fullName, $firstName))" />
				</contentMeta>

				<contentMeta name="gender">
					<xsl:value-of select="//TD[@class='index_centre_tableau_10_typo_descriptif' and text() = 'Sexe :']/../TD[2]/text()" />
				</contentMeta>

				<contentMeta name="city">
					<xsl:value-of select="//TD[@class='index_centre_tableau_10_typo_descriptif' and text() = 'Ville :']/../TD[2]/text()" />
				</contentMeta>

				<contentMeta name="country">
					<xsl:value-of select="//SPAN[@class='country-name']" />
				</contentMeta>

				<!-- No birth date -->
				<contentMeta name="birthDate">
					
				</contentMeta>
				
				<contentMeta name="age">
					<xsl:variable name="extract" select="//TD[@class='index_centre_tableau_10_typo_descriptif' and text() = 'Age :']/../TD[2]/text()"></xsl:variable>
					<xsl:value-of select="normalize-space(substring-before($extract, 'ans'))" />
				</contentMeta>


			</document>
		</documents>
	</xsl:template>

</xsl:stylesheet>