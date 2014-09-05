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
					<xsl:value-of select="//H4[text()='Né le :']/../P" />
					<!-- Need to upgrade to XSLT 2.0 <xsl:analyze-string select="//H4[text()='Né 
						le :']/../P" regex="\d{4}"> <xsl:matching-substring> <xsl:value-of select="regex-group(0)" 
						/> </xsl:matching-substring> </xsl:analyze-string> -->
				</contentMeta>



			</document>
		</documents>
	</xsl:template>

</xsl:stylesheet>