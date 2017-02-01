<?xml version="1.0" encoding="UTF-8"?>
<!--
	Modifies DTBook linegroups.

		Note: '-' below represents both hyphen-minus and en-dash 
		
		A linegroup becomes a single p unless some lines start with '- ' or are mixed with
		other elements. In that case the linegroup becomes a div with two or more p. New 
		p elements start whenever a line starts with '- ' or when an element other than
		line is encountered.
		
		A line with attributes are replaced by a span with the same attributes.
		
		Param paragraph-type can be used to set the class for created p elements.
		Param clear-p-class can be set to false() to retain class attributes for
		pre-existing p elements (this param does not affect the created p elements
		described above).
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

	<!-- paragraph type for linegroups -->
	<xsl:param name="paragraph-type" select="''"/> <!-- precedingemptyline, indented, precedingseparator, no-indent -->
	<!-- remove @class on p elements -->
	<xsl:param name="clear-p-class" select="true()" as="xs:boolean"/>
	
	<!-- excludes poem from linegroup processing -->
	<xsl:template match="dtb:poem">
		<xsl:if test="descendant::dtb:linegroup">
			<xsl:message terminate="no">INFO: Linegroups inside a poem was not merged.</xsl:message>
		</xsl:if>
		<xsl:copy-of select="."/>
	</xsl:template>
	
	<xsl:template match="dtb:linegroup">
		<xsl:variable name="linegroup">
			<xsl:apply-templates/>
		</xsl:variable>
		<xsl:choose>
			<!-- There's a single node and we added it -->
			<xsl:when test="count(dtb:line)>0 and count($linegroup/node())=1 and $paragraph-type=''">
				<!-- Replace it with a new node with attributes from the linegroup -->
				<xsl:element name="p" namespace="http://www.daisy.org/z3986/2005/dtbook/">
					<xsl:copy-of select="@*"/>
					<!-- unwrap the top node that we added -->
					<xsl:copy-of select="$linegroup/node()/node()"/>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="div" namespace="http://www.daisy.org/z3986/2005/dtbook/">
					<!-- div and linegroup support the same attributes -->
					<xsl:copy-of select="@*"/>
					<xsl:copy-of select="$linegroup"/>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Only process lines in linegroup -->
	<xsl:template match="dtb:line[parent::dtb:linegroup]">
    	<xsl:if test="not(preceding-sibling::*[1][self::dtb:line]) or dotify:starts-paragraph(.)">
    		<xsl:element name="p" namespace="http://www.daisy.org/z3986/2005/dtbook/">
    			<xsl:if test="$paragraph-type!=''">
    				<xsl:attribute name="class" select="$paragraph-type"/>
    			</xsl:if>
    			<xsl:apply-templates select="." mode="mergeLines"/>
    		</xsl:element>
    	</xsl:if>
    </xsl:template>
	
	<!-- processed in mergeLines mode -->
	<xsl:template match="processing-instruction()[parent::dtb:linegroup]|comment()[parent::dtb:linegroup]"/>

	<xsl:template match="processing-instruction()|comment()" mode="mergeLines">
		<xsl:copy-of select="."/>
		<!-- continue until we get to the next line -->
		<xsl:apply-templates select="following-sibling::node()[1]" mode="mergeLines"/>
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
		<xsl:if test="(following-sibling::*[1])[self::dtb:line and not(dotify:starts-paragraph(.))]">
			<xsl:text> </xsl:text>
			<xsl:apply-templates select="following-sibling::node()[1]" mode="mergeLines"/>
		</xsl:if>
	</xsl:template>

	<xsl:function name="dotify:starts-paragraph" as="xs:boolean">
		<xsl:param name="element"></xsl:param>
		<xsl:value-of select="matches(string($element), '^\s*[-&#x2013;]\s')"/>
	</xsl:function>
	
	<xsl:template match="dtb:p[@class and $clear-p-class]">
		<xsl:copy>
			<xsl:copy-of select="@*[local-name()!='class']"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>

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