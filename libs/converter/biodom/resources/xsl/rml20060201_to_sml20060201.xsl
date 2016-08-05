<?xml version="1.0" encoding="UTF-8"?>
<!-- Stylesheet to extract sequence information from RNAStructML to SequenceML -->
<xsl:stylesheet 
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
        xmlns:rnastructml="http://hobit.sourceforge.net/xsds/20060201/rnastructML" 
        xmlns:sequenceml="http://hobit.sourceforge.net/xsds/20060201/sequenceML" 
        xml:lang="en" version="1.0">
    <xsl:output indent="yes" method="xml"/>
    
    <!-- find the main tag and replace it by the SequenceML main tag -->
    <xsl:template match="rnastructml:rnastructML">
        <sequenceml:sequenceML>
            <!-- search + extract sequences -->
            <xsl:apply-templates select="//rnastructml:sequence"/>
        </sequenceml:sequenceML>
    </xsl:template>
    
    <xsl:template match="rnastructml:sequence">
        <!-- for each matched sequence tag, extract it's subinformation -->
        <sequenceml:sequence>
            <xsl:apply-templates select="@*"/>
            <!-- nam, synonyms and description fields -->
            <xsl:apply-templates select="rnastructml:name"/>
            <xsl:apply-templates select="rnastructml:synonyms"/>
            <xsl:apply-templates select="rnastructml:description"/>
            <!-- the three possible sequence data types -->
            <xsl:apply-templates select="rnastructml:nucleicAcidSequence"/>
            <xsl:apply-templates select="rnastructml:freeSequence"/>
            <xsl:apply-templates select="rnastructml:emptySequence"/>
            <!-- the comment field -->
            <xsl:apply-templates select="rnastructml:comment"/>
        </sequenceml:sequence>
    </xsl:template>
    
    <xsl:template match="rnastructml:name">
        <sequenceml:name><xsl:value-of select="."/></sequenceml:name>
    </xsl:template>
    
    <xsl:template match="rnastructml:synonyms">
        <sequenceml:synonyms><xsl:value-of select="."/></sequenceml:synonyms>
    </xsl:template>
    
    <xsl:template match="rnastructml:description">
        <sequenceml:description><xsl:value-of select="."/></sequenceml:description>
    </xsl:template>
    
    <xsl:template match="rnastructml:nucleicAcidSequence">
        <sequenceml:nucleicAcidSequence><xsl:value-of select="."/></sequenceml:nucleicAcidSequence>
    </xsl:template>
    <xsl:template match="rnastructml:freeSequence">
        <sequenceml:freeSequence><xsl:value-of select="."/></sequenceml:freeSequence>
    </xsl:template>
    <xsl:template match="rnastructml:emptySequence">
        <sequenceml:emptySequence><xsl:value-of select="."/></sequenceml:emptySequence>
    </xsl:template>
    
    <xsl:template match="rnastructml:comment">
        <sequenceml:comment><xsl:value-of select="."/></sequenceml:comment>
    </xsl:template>
    
    <!-- an attribute match for the seuqence ID -->
    <xsl:template match="@seqID">
        <xsl:attribute name="seqID">
            <xsl:value-of select="."/>
        </xsl:attribute>
    </xsl:template>
</xsl:stylesheet>
