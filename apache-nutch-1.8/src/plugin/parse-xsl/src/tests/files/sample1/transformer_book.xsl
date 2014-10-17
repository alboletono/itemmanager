<?xml version="1.0" encoding="UTF-8"?>

<!-- This file will transform a book.html to an xml document compounded of 
	specific fields. Each field will then be indexed (by default) -->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">


	<xsl:template match="/">
		<documents>
			<document>

				<field name="title">
					<xsl:value-of select="/HTML/BODY/H1" />
				</field>

				<field name="description">
					<xsl:value-of select="//DIV[@id='description']" />
				</field>

				<field name="isbn">
					<xsl:variable name="fullDivText"
						select="//DIV[starts-with(text(), 'Isbn:')]/text()" />
					<xsl:value-of select="substring-after($fullDivText, 'Isbn: ')" />
				</field>

				<!-- Adding several Author fields -->
				<xsl:for-each select="/HTML/BODY/UL[starts-with(text(),'Authors')]/LI">
					<field name="author">
						<xsl:value-of select="." />
					</field>
				</xsl:for-each>

				<field name="price">
					<xsl:variable name="fullSpanText"
						select="//SPAN[starts-with(text(), 'Price:')]/text()" />
					<xsl:value-of select="substring-after($fullSpanText, 'Price: ')" />
				</field>

				<field name="collection">
					<xsl:value-of select="//DIV[@class='.collection']" />
				</field>


			</document>
		</documents>
	</xsl:template>

</xsl:stylesheet>