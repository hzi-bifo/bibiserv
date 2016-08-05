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
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:cms="bibiserv:de.unibi.techfak.bibiserv.cms"
    version="1.0">
    
    <xsl:output method="xml" indent="yes"/>
    <!-- xslt script that extract put all categories into a list, consider text nodes -->
        
    <xsl:template match="/">
        <list>
            <xsl:apply-templates select="//cms:category"/>
        </list>
    </xsl:template>
    
    <xsl:template match="cms:category">
        <xsl:element name="category">
            <xsl:attribute name="id">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
            <xsl:apply-templates select="cms:name|cms:categoryDescription|cms:categoryRepresentation"/>
        </xsl:element>
    </xsl:template>

    
    <!-- rewrite default text node template, add an white space character as prefix for each text node, to avoid collapsing word ... -->
    <xsl:template match="text()">
        <xsl:text> </xsl:text>
        <xsl:value-of select="."/>        
    </xsl:template>

</xsl:stylesheet>