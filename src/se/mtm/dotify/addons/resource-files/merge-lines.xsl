<?xml version="1.0" encoding="UTF-8"?>
<!--
	Modifies DTBook lines.

		A line beginning with a lower case letter will be merged with the previous line, if any.

-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/"
    xmlns:dotify="http://brailleapps.github.io/ns/dotify"
    xmlns="http://www.daisy.org/ns/2011/obfl"
    exclude-result-prefixes="xs dtb dotify"    
    version="2.0">
    <xsl:output doctype-public="-//NISO//DTD dtbook 2005-3//EN" doctype-system="http://www.daisy.org/z3986/2005/dtbook-2005-3.dtd"/>
	<xsl:strip-space elements="dtb:linegroup"/>
	
	<!-- Processed in mergeLines mode -->
	<xsl:template match="processing-instruction()[parent::dtb:linegroup]|comment()[parent::dtb:linegroup]"/>
	
	<!-- Only process lines in linegroup -->
	<xsl:template match="dtb:line[parent::dtb:linegroup]">
		<xsl:if test="not(preceding-sibling::*[1][self::dtb:line]) or not(dotify:begins-lower-case(.))">
			<!-- Create a new line element, attributes are moved to child -->
			<xsl:element name="line" namespace="http://www.daisy.org/z3986/2005/dtbook/">
				<xsl:apply-templates select="." mode="mergeLines"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>

	<xsl:template match="dtb:line" mode="mergeLines">
		<xsl:choose>
			<xsl:when test="count(@*)>0">
				<xsl:element name="span" namespace="http://www.daisy.org/z3986/2005/dtbook/">
					<!-- span and line support the same attributes -->
					<xsl:copy-of select="@*"/>
					<xsl:apply-templates/>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise><xsl:apply-templates/></xsl:otherwise>
		</xsl:choose>
		<xsl:if test="(following-sibling::*[1])[self::dtb:line and dotify:begins-lower-case(.)]">
			<xsl:text> </xsl:text>
			<xsl:apply-templates select="following-sibling::node()[1]" mode="mergeLines"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="processing-instruction()|comment()" mode="mergeLines">
		<xsl:copy-of select="."/>
		<!-- continue until we get to the next line -->
		<xsl:apply-templates select="following-sibling::node()[1]" mode="mergeLines"/>
	</xsl:template>

	<xsl:function name="dotify:begins-lower-case" as="xs:boolean">
		<xsl:param name="element"></xsl:param>
		<xsl:value-of select="matches(string($element), '^\s*[\p{Ll}]')"/>
	</xsl:function>
	
    <xsl:template match="*|comment()|processing-instruction()">
        <xsl:call-template name="copy"/>
    </xsl:template>

    <xsl:template name="copy">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>