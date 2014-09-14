<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">


	<xsl:template match="/">
		<documents>
			<document>
				<contentMeta name="wantedPeople">
					<xsl:value-of select="//H1[@class='linter_title_1']" />
				</contentMeta>

				<contentMeta name="searcherPeople">
					<xsl:value-of select="//DIV[@class='grid_left w40']/DIV[@class='grid_row']/DIV[@class='grid_last']/A" />
				</contentMeta>

				<contentMeta name="description">
					<xsl:value-of select="normalize-space(//DIV[@class='typeComment_wanted']/DIV[@class='grid_last']/P/text())" />
				</contentMeta>

				
			</document>
		</documents>
	</xsl:template>

</xsl:stylesheet>