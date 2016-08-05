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
    version="1.0">
    <xsl:output method="text" />
    
  
    <!-- XSL script that extract all text nodes from item, linkedItem, runnableItem and category
        beside "support", "responsibleAuthor", "author"  elements from [linked]item and
        "executable", -->
    
    <xsl:template match="cms:support"/>
    <xsl:template match="cms:responsibleAuthor"/>  
    <xsl:template match="cms:author"/>
    
    <xsl:template match="cms:executable"/>
    <xsl:template match="cms:downloadable"/>
    <xsl:template match="cms:view"/>
    <xsl:template match="cms:manual"/>  
    
    <xsl:template match="cms:references"/>
    
    
       
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    
  
</xsl:stylesheet>