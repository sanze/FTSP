<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xslthl="http://xslthl.sf.net" xmlns:d="http://docbook.org/ns/docbook"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:date="http://exslt.org/dates-and-times"
    exclude-result-prefixes="date xslthl"
    version="1.0">

	<xsl:import href="urn:docbkx:stylesheet" />

	<!-- 设置html的css -->
 	<xsl:param name="html.stylesheet" select="'css/style.css'" />
 	
 	<xsl:param name="chunker.output.encoding" select="'utf-8'"/>

	<!-- 图表标题居中 --> 	
	<!-- copy from docbook-xsl-ns-1.76.1\html\formal.xsl -->
	<xsl:template name="formal.object.heading">
	  <xsl:param name="object" select="."/>
	  <xsl:param name="title">
	    <xsl:apply-templates select="$object" mode="object.title.markup">
	      <xsl:with-param name="allow-anchors" select="1"/>
	    </xsl:apply-templates>
	  </xsl:param>
	 
	  <xsl:choose>
	    <xsl:when test="$make.clean.html != 0">
	      <xsl:variable name="html.class" select="concat(local-name($object),'-title')"/>
	      <div class="{$html.class}" style="text-align: center; ">
	        <xsl:copy-of select="$title"/>
	      </div>
	    </xsl:when>
	    <xsl:otherwise>
	      <p class="title" align="center">
	        <b>
	          <xsl:copy-of select="$title"/>
	        </b>
	      </p>
	    </xsl:otherwise>
	  </xsl:choose>
	</xsl:template>
	
	<!--图表等标题的放置位置-->
	<xsl:param name="formal.title.placement">
		figure after
		example before
		equation before
		table before
		procedure before
	</xsl:param>
	
	<!-- 目录深度 -->  
    <xsl:param name="toc.section.depth" select="2" />
    
    <!-- 章节自动编号 -->
    <xsl:param name="section.autolabel" select="1" />  
    <xsl:param name="section.label.includes.component.label" select="1" />
    
    <!-- 设置警示图标 -->
	<xsl:param name="admon.graphics" select="1" />  
    <xsl:param name="admon.graphics.path" select="'assets/images/'" />  
    <xsl:param name="admon.graphics.extension" select="'.svg'" />  
    <xsl:param name="admon.textlabel" select="0" />
    
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
        </l:l10n>  
    </l:i18n> 
    
    <!-- 允许html中table列宽自定义 -->
    <xsl:param name="use.extensions" select="1" />
    <xsl:param name="tablecolumns.extension" select="1" />
    
    <!-- 去除图表清单目录 -->
    <xsl:param name="generate.toc">
		appendix  toc,title
		article/appendix  nop
		article   toc,title
		book      toc,title,example,equation
		chapter   toc,title
		part      toc,title
		preface   toc,title
		qandadiv  toc
		qandaset  toc
		reference toc,title
		sect1     toc
		sect2     toc
		sect3     toc
		sect4     toc
		sect5     toc
		section   toc
		set       toc,title
	</xsl:param>
	
	<!-- 给文件增加时间戳 -->
	<xsl:template name="user.head.content">
	  <meta name="date">
	    <xsl:attribute name="content">
	      <xsl:call-template name="datetime.format">
	        <xsl:with-param name="date" select="date:date-time()"/>
	        <xsl:with-param name="format" select="'Y-m-d'"/>
	      </xsl:call-template>
	    </xsl:attribute>
	  </meta>
	</xsl:template>
    
</xsl:stylesheet>

