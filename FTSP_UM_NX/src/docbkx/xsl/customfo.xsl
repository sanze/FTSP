<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xslthl="http://xslthl.sf.net" xmlns:d="http://docbook.org/ns/docbook"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    exclude-result-prefixes="xslthl" version="1.0">

    <xsl:import href="urn:docbkx:stylesheet"/>

    <xsl:param name="fop1.extensions">1</xsl:param>    
  
    <!-- Formatting source code listings, see here:
             http://www.sagehill.net/docbookxsl/ProgramListings.html#FormatListings
    -->
    <xsl:attribute-set name="monospace.verbatim.properties">
      <xsl:attribute name="font-family">monospace</xsl:attribute>
      <xsl:attribute name="font-size">9pt</xsl:attribute>
      <xsl:attribute name="keep-together.within-column">always</xsl:attribute>
    </xsl:attribute-set>

    <xsl:param name="shade.verbatim" select="1"/>

    <xsl:attribute-set name="shade.verbatim.style">
      <xsl:attribute name="background-color">#E0E0E0</xsl:attribute>
      <xsl:attribute name="border-width">0.5pt</xsl:attribute>
      <xsl:attribute name="border-style">solid</xsl:attribute>
      <xsl:attribute name="border-color">#575757</xsl:attribute>
      <xsl:attribute name="padding">3pt</xsl:attribute>
    </xsl:attribute-set>

	<!-- 字体设置 -->
	<!-- 符号字体 -->
	<xsl:param name="symbol.font.family">Symbol,ZapfDingbats</xsl:param>
	<!-- 标题字体 -->
	<xsl:param name="title.font.family">msyh</xsl:param>
	<!-- 正文字体 -->
	<xsl:param name="body.font.family">simsun</xsl:param>
	<!-- 等宽字体 -->
	<xsl:param name="monospace.font.family">simsun</xsl:param>
	<!-- 无衬线字体 -->
	<xsl:param name="sans.font.family">sans-serif</xsl:param>
	<!-- 装饰字体 -->
	<xsl:param name="dingbat.font.family">serif</xsl:param>

	<!--节标题自动编号-->
	<xsl:param name="section.autolabel">1</xsl:param>
	<xsl:param name="section.label.includes.component.label">1</xsl:param>

	<!--图表等标题的放置位置-->
	<xsl:param name="formal.title.placement">
		figure after
		example before
		equation before
		table before
		procedure before
	</xsl:param>

	<!--插图标题居中-->
	<!-- copy from docbook-xsl-ns-1.76.1\fo\formal.xsl -->
	<!-- http://www.sagehill.net/docbookxsl/TitleFontSizes.html#FormalTitleProperties -->
	<xsl:attribute-set name="formal.title.properties"
	                   use-attribute-sets="normal.para.spacing">
	  <xsl:attribute name="font-weight">bold</xsl:attribute>
	  <xsl:attribute name="font-size">10pt</xsl:attribute>
	  <xsl:attribute name="hyphenate">false</xsl:attribute>
	  <xsl:attribute name="space-after.minimum">0.4em</xsl:attribute>
	  <xsl:attribute name="space-after.optimum">0.6em</xsl:attribute>
	  <xsl:attribute name="space-after.maximum">0.8em</xsl:attribute>
	  <xsl:attribute name="text-align">center</xsl:attribute>
	</xsl:attribute-set>
	
	<!-- 设置警示图标 -->
	<xsl:param name="admon.graphics" select="1" />  
    <xsl:param name="admon.graphics.path" select="'assets/images/'" />  
    <xsl:param name="admon.graphics.extension" select="'.svg'" />  
    <xsl:param name="admon.textlabel" select="1" />
    
    <!-- 设置标注图标 -->
	<xsl:param name="callout.graphics" select="1" />
	<xsl:param name="callout.graphics.extension" select="'.svg'" />
	<xsl:param name="callout.graphics.path" select="'assets/images/callouts/'" />
	
    <xsl:param name="local.l10n.xml" select="document('')"/>  
    <l:i18n xmlns:l="http://docbook.sourceforge.net/xmlns/l10n/1.0">  
        <l:l10n language="zh_cn">  
            <l:context name="title-numbered">  
                <l:template name="chapter" text="第 %n 章 %t"/>  
                <l:template name="section" text="%n %t"/>  
            </l:context>
            <l:context name="title">
            	<l:template name="figure" text="图 %n %t"/>
            </l:context>
            <l:context name="xref-number-and-title">
            	<l:template name="section" text="第%n节 “%t”"/>
            </l:context>
        </l:l10n>  
    </l:i18n> 
    
</xsl:stylesheet>

