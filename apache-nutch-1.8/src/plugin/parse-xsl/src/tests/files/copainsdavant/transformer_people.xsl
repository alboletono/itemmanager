<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">


	<xsl:template match="/">
		<documents>
			<document>
				<contentMeta name="lastName">
					<xsl:value-of select="//META[@property='profile:last_name']/@content" />
				</contentMeta>

				<contentMeta name="firstName">
					<xsl:value-of select="//META[@property='profile:first_name']/@content" />
				</contentMeta>

				<contentMeta name="gender">
					<xsl:value-of select="//META[@property='profile:gender']/@content" />
				</contentMeta>

				<contentMeta name="city">
					<xsl:value-of select="//SPAN[@class='locality']" />
				</contentMeta>

				<contentMeta name="country">
					<xsl:value-of select="//SPAN[@class='country-name']" />
				</contentMeta>

				<contentMeta name="birthDate">
					<xsl:variable name="extractedValue" select="//H4[text()='Né le :']/../P" />
					<xsl:value-of select="normalize-space(substring-after(substring-before($extractedValue, '('), '.'))" />
				</contentMeta>



			</document>
		</documents>
	</xsl:template>

</xsl:stylesheet>