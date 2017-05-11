<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:output method="text" />

	<xsl:template match="/">
		<xsl:apply-templates select="modules" />
	</xsl:template>

	<xsl:template match="modules">
		<xsl:variable name="mods" select="." />
		<xsl:variable name="packages" select="module/@name" />

		<xsl:text>{&#10;</xsl:text>

		<xsl:text>"packageNames": [</xsl:text>
		<xsl:for-each select="$packages">
			<xsl:sort select="." />

			<xsl:if test="position() > 1">
				<xsl:text>,</xsl:text>
			</xsl:if>
			<xsl:text>&#10;&#9;"</xsl:text>
			<xsl:value-of select="fn:replace(., '\\', '\\\\')" />
			<xsl:text>"</xsl:text>
		</xsl:for-each>
		<xsl:text>],&#10;</xsl:text>

		<xsl:text>"matrix": [</xsl:text>
		<xsl:for-each select="$packages">
			<xsl:sort select="." />

			<xsl:if test="position() > 1">
				<xsl:text>,</xsl:text>
			</xsl:if>
			<xsl:text>&#10;&#9;</xsl:text>

			<xsl:variable name="name" select="." />
			<xsl:call-template name="depMatrix">
				<xsl:with-param name="packages" select="$packages" />
				<xsl:with-param name="mod" select="$mods/module[@name = $name]" />
			</xsl:call-template>

		</xsl:for-each>
		<xsl:text>&#10;]&#10;}</xsl:text>
	</xsl:template>

	<xsl:template name="depMatrix">
		<xsl:param name="packages" />
		<xsl:param name="mod" />
		<xsl:text>[</xsl:text>

		<xsl:for-each select="$packages">
			<xsl:sort select="." />

			<xsl:if test="position() > 1">
				<xsl:text>,</xsl:text>
			</xsl:if>

			<xsl:variable name="name" select="." />
			<xsl:variable name="dep" select="$mod/import[@module = $name]" />
			<xsl:choose>
				<xsl:when test="$dep/@count">
					<xsl:value-of select="$dep/@count" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>0</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>

		<xsl:text>]</xsl:text>
	</xsl:template>

</xsl:stylesheet>