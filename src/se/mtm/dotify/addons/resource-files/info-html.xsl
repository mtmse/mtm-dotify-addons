<?xml version="1.0"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:html="http://www.w3.org/1999/xhtml"
	xmlns:epub="http://www.idpf.org/2007/ops"
	xmlns:d="http://www.tpb.se/stylesheets/dtbinfo"
	xmlns:xtd="https://www.ologolo.org/ns/doc/xsl"
	xmlns="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="html epub d xtd">

	<xsl:include href="lib/recursive-copy.xsl"/>
	<xsl:include href="lib/localization.xsl"/>
	<xsl:include href="lib/messages/errors.xsl"/>
	
	<xsl:param name="year" select="format-date(current-date(), '[Y0001]')" xtd:desc="The year for the product on the form YYYY."/>
	<xsl:param name="identifier" select="'P??????'" xtd:desc="The product identifier" xtd:default="P??????"/>
	<!-- keep/remove captions -->
	<xsl:param name="captions" select="'keep'"/>
	<xsl:param name="l10nLang" select="'sv'"/>
	
	<xsl:function name="epub:types" as="xs:string*">
		<xsl:param name="node" as="element()"/>
		<xsl:sequence select="tokenize($node/@epub:type, '\s+')" />
	</xsl:function>
	
	<xsl:variable name="lang">
		<xsl:choose>
			<xsl:when test="starts-with($l10nLang, 'sv')">sv</xsl:when>
			<xsl:when test="starts-with($l10nLang, 'en')">en</xsl:when>
			<xsl:otherwise>en</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	
	<xsl:variable name="data" select="document('./localizations/punktinfo.xml')//language[lang($lang)]"/>

	<xsl:template match="html:html">
		<!-- In the dtbook version, there were validation tests here -->
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>

	<!-- These are processed in the template below dtb:level1[@class='colophon'] -->
	<xsl:template match="html:*[epub:types(.)=('colophon')]">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:call-template name="addNotice"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
	
	<!-- Remove imggroup if set to remove and imagegroup does not contain prodnote 
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
	-->

	<!-- Pagenum processing -->
	<!-- Ignore these text nodes, they are processed below 
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
	</xsl:template>-->
	
	<!-- 
	<xsl:template name="movePagenumInWords">
		<xsl:variable name="A1" select="translate(following-sibling::node()[1], '&#x9;&#xa;&#xd;', '   ')"/>
		<xsl:variable name="A2" select="translate(preceding-sibling::node()[1], '&#x9;&#xa;&#xd;', '   ')"/>
		<xsl:choose>
			-  ends-with: substring($A, string-length($A) - string-length($B) + 1) = $B
                  Se XSLT programmers reference, second edition, Michael Kay, sidan 541 -
			- Om föregående textnod slutar med mellanslag eller om nästkommande textnod börjar med mellanslag så ska denna tagg inte flyttas. -
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
	</xsl:template>-->
	<!-- /Pagenum processing -->
	
	<xsl:template name="addNotice">
			<xsl:element name="div">
				<xsl:attribute name="class">pgroup</xsl:attribute>
				<xsl:element name="p"><xsl:call-template name="localizeString">
					<xsl:with-param name="context" select="$data/string[@ref='line01']"/>
					<xsl:with-param name="arg1" select="//html:meta[@name='dc:publisher']/@content"/>
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
			<xsl:if test="count(//*[@epub:type='z3998:author'])&gt;3">
				<xsl:element name="div">
					<xsl:attribute name="class">pgroup</xsl:attribute>
					<xsl:element name="p">Samtliga författare:</xsl:element>
					<xsl:element name="list">
						<xsl:attribute name="type">pl</xsl:attribute>
						<xsl:for-each select="//*[@epub:type='z3998:author']">
							<xsl:element name="li">
								<xsl:value-of select="."/>
							</xsl:element>
						</xsl:for-each>
					</xsl:element>
				</xsl:element>
			</xsl:if>
	</xsl:template>
	
</xsl:stylesheet>