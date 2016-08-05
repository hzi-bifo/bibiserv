<?xml version="1.0" encoding="UTF-8"?>
<!--
 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 
 Copyright 2012 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
 All rights reserved.
 
 The contents of this file are subject to the terms of the Common
 Development and Distribution License("CDDL") (the "License"). You
 may not use this file except in compliance with the License. You can
 obtain a copy of the License at http://www.sun.com/cddl/cddl.html
 
 See the License for the specific language governing permissions and
 limitations under the License.  When distributing the software, include
 this License Header Notice in each file.  If applicable, add the following
 below the License Header, with the fields enclosed by brackets [] replaced
  by your own identifying information:
  
 "Portions Copyrighted 2012 BiBiServ Curator Team"
 
 Contributor(s): Jan Krueger
 
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:cms="bibiserv:de.unibi.techfak.bibiserv.cms"
    xmlns:microhtml="bibiserv:de.unibi.techfak.bibiserv.cms.microhtml"
    xmlns:minihtml="bibiserv:de.unibi.techfak.bibiserv.cms.minihtml"
    version="1.0">
    
    <!-- XSL script that extract all human readable text elements like
        name, shortDescription, Description and customContent-->
    
    <xsl:template match="cms:name">
        <xsl:apply-templates/>
    </xsl:template>
       
    <xsl:template match="cms:description">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="cms:shortDescription">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="cms:customContent">
        <xsl:apply-templates/>
    </xsl:template>
       
    <xsl:template match="microhtml:*">
        <xsl:apply-templates/>
    </xsl:template>
    
    
    <xsl:template match="minihtml:*">
        <xsl:apply-templates/>        
    </xsl:template>
    
    <xsl:template match="/">
        <xsl:apply-templates select="//cms:name"/>
        <xsl:apply-templates select="//cms:shortDescription"/>
        <xsl:apply-templates select="//cms:description"/>
        <xsl:apply-templates select="//cms:customContent"/>
    </xsl:template>
    
    <xsl:template match="text()">
        <xsl:value-of select="."/>
    </xsl:template>
    
    <xsl:template match="*"/>

</xsl:stylesheet>