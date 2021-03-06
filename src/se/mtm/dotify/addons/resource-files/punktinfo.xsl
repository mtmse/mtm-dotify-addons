<?xml version="1.0"?>
<!--
	punktinfo
		Version
			2015-09-29

		Description
			Prepares a dtbook for use in braille-production at MTM by:
				* Adding a production notice
				* Flyttar pagenum innuti ord till efter ordet (undantaget xml:space='preserve')

			Note that:
				* dc:Publisher must be either TPB, MTM or SIT
				* the main language must be swedish or english

			To change the phrasing in a particular language,
				see ./localizations/punktinfo.xml

		Nodes
			* level1[@class=colophon]
			* pagenum
			* text()

		Namespaces
			( ) ""
			(x) "http://www.daisy.org/z3986/2005/dtbook/"

		Doctype assignment
			(x) DTBook
			( ) None

		Author
			Joel Håkansson
-->
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/"
	xmlns:d="http://www.tpb.se/stylesheets/dtbinfo"
	xmlns:xtd="https://www.ologolo.org/ns/doc/xsl"
	xmlns="http://www.daisy.org/z3986/2005/dtbook/"
	exclude-result-prefixes="dtb d xtd">

	<xsl:include href="./lib/recursive-copy.xsl"/>
	<xsl:include href="./lib/dtbook-output.xsl"/>
	<xsl:include href="./lib/validation-tests.xsl"/>
	<xsl:include href="./lib/localization.xsl"/>
	
	<xsl:param name="year" select="format-date(current-date(), '[Y0001]')" xtd:desc="The year for the product on the form YYYY."/>
	<xsl:param name="identifier" select="'P??????'" xtd:desc="The product identifier" xtd:default="P?????"/>
	<!-- keep/remove captions -->
	<xsl:param name="captions" select="'keep'" xtd:desc="Remove imagegroups that does not contain prodnotes" xtd:values="keep/remove" xtd:default="keep"/>
	<xsl:param name="l10nLang" select="'sv'"/>

	<xsl:variable name="lang">
		<xsl:choose>
			<xsl:when test="starts-with($l10nLang, 'sv')">sv</xsl:when>
			<xsl:when test="starts-with($l10nLang, 'en')">en</xsl:when>
			<xsl:otherwise>en</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	
	<xsl:variable name="data" select="document('./localizations/punktinfo.xml')//language[lang($lang)]"/>

	<xsl:template match="dtb:dtbook">
		<xsl:call-template name="verify-dtbook-ns"/>
		<xsl:call-template name="verify-xml-lang-on-root"/>
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>

	<!-- These are processed in the template below -->
	<xsl:template match="dtb:level1[@class='colophon']">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:call-template name="addNotice"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
	
	<!-- Remove imggroup if set to remove and imagegroup does not contain prodnote -->
	<xsl:template match="dtb:imggroup[dtb:caption and not(dtb:prodnote)]">
		<xsl:choose>
			<xsl:when test="$captions!='remove'">
			<xsl:call-template name="copy"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:copy>
				<xsl:copy-of select="@*"/>
				<xsl:apply-templates select="node()[not(self::dtb:caption)]"/>
			</xsl:copy>
		</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Pagenum processing -->
	<!-- Ignore these text nodes, they are processed below -->
	<xsl:template match="text()[normalize-space()!='' and preceding-sibling::node()[1][self::dtb:pagenum] and
							preceding-sibling::node()[2][self::text() and normalize-space()!=''] and not(ancestor-or-self::*[@xml:space][1]/@xml:space='preserve')]"/>
							
	<xsl:template match="dtb:pagenum">
		<xsl:choose>
			<xsl:when test="preceding-sibling::node()[1][self::text() and normalize-space()!=''] and following-sibling::node()[1][self::text() and normalize-space()!=''] and not(ancestor-or-self::*[@xml:space][1]/@xml:space='preserve')">
				<xsl:call-template name="movePagenumInWords"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="copy"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="movePagenumInWords">
		<xsl:variable name="A1" select="translate(following-sibling::node()[1], '&#x9;&#xa;&#xd;', '   ')"/>
		<xsl:variable name="A2" select="translate(preceding-sibling::node()[1], '&#x9;&#xa;&#xd;', '   ')"/>
		<xsl:choose>
			<!--  ends-with: substring($A, string-length($A) - string-length($B) + 1) = $B
                  Se XSLT programmers reference, second edition, Michael Kay, sidan 541 -->
			<!-- Om föregående textnod slutar med mellanslag eller om nästkommande textnod börjar med mellanslag så ska denna tagg inte flyttas. -->
			<xsl:when test="starts-with($A1, ' ') or substring($A2, string-length($A2))=' '">
				<xsl:call-template name="copy"/>
				<xsl:value-of select="following-sibling::node()[1]"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="contains($A1,' ')">
						<xsl:value-of select="substring-before($A1,' ')"/>
						<xsl:call-template name="copy"/>
						<xsl:value-of select="concat(' ',substring-after($A1,' '))"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="following-sibling::node()[1]"/>
						<xsl:call-template name="copy"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>	
	</xsl:template>
	<!-- /Pagenum processing -->
	
	<xsl:template name="addNotice">
			<xsl:element name="div">
				<xsl:attribute name="class">pgroup</xsl:attribute>
				<xsl:element name="p"><xsl:call-template name="localizeString">
					<xsl:with-param name="context" select="$data/string[@ref='line01']"/>
					<xsl:with-param name="arg1" select="//dtb:meta[@name='dc:Publisher']/@content"/>
					<xsl:with-param name="arg2" select="$year"/>
				</xsl:call-template></xsl:element>
			</xsl:element>
			<xsl:element name="div">
				<xsl:attribute name="class">pgroup</xsl:attribute>
				<xsl:element name="p">
					<xsl:call-template name="localizeString">
						<xsl:with-param name="context" select="$data/string[@ref='line02']"/>
						<xsl:with-param name="arg1" select="$identifier"/>
					</xsl:call-template>
				</xsl:element>
			</xsl:element>
			<xsl:if test="count(/dtb:dtbook/dtb:book/dtb:frontmatter/dtb:docauthor)&gt;3">
				<xsl:element name="div">
					<xsl:attribute name="class">pgroup</xsl:attribute>
					<xsl:element name="p">Samtliga författare:</xsl:element>
					<xsl:element name="list">
						<xsl:attribute name="type">pl</xsl:attribute>
						<xsl:for-each select="/dtb:dtbook/dtb:book/dtb:frontmatter/dtb:docauthor">
							<xsl:element name="li">
								<xsl:value-of select="."/>
							</xsl:element>
						</xsl:for-each>
					</xsl:element>
				</xsl:element>
			</xsl:if>
	</xsl:template>
	
</xsl:stylesheet>