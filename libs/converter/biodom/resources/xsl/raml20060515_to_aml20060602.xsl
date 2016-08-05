<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
        xmlns:raml="http://hobit.sourceforge.net/xsds/20060515/rnastructAlignmentML" 
        xmlns:aml="http://hobit.sourceforge.net/xsds/20060602/alignmentML" 
        xml:lang="en" version="1.0">
    
    <xsl:output  indent="yes" method="xml"/>
    
    <xsl:template match="raml:rnastructAlignmentML">
        <aml:alignmentML>
             <xsl:apply-templates select="raml:rnastructurealignment"/>
        </aml:alignmentML>
    </xsl:template>
    
    <xsl:template match="raml:rnastructurealignment">
        <aml:alignment score="{@score}">
            <xsl:apply-templates select="raml:sequence"/>
        </aml:alignment>
    </xsl:template>
    
    <xsl:template match="raml:sequence">
        <aml:sequence seqID="{@seqID}">
            <xsl:apply-templates select="raml:name"/>
            <xsl:apply-templates select="raml:synonyms"/>
            <xsl:apply-templates select="raml:description"/>
            <xsl:apply-templates select="raml:alignedNucleicAcidSequence"/>
            <xsl:apply-templates select="raml:alignedFreeSequence"/>
            <xsl:apply-templates select="raml:emptySeqence"/>
        </aml:sequence>
    </xsl:template>
    
    <xsl:template match="raml:name">
        <aml:name><xsl:value-of select="."/></aml:name>
    </xsl:template>
    
    <xsl:template match="raml:synonyms">
        <aml:synonyms><xsl:value-of select="."/></aml:synonyms>
    </xsl:template>
    
    <xsl:template match="raml:description">
        <aml:description><xsl:value-of select="."/></aml:description>
    </xsl:template>
    
    <xsl:template match="raml:alignedNucleicAcidSequence">
        <aml:alignedNucleicAcidSequence><xsl:value-of select="."/></aml:alignedNucleicAcidSequence>
    </xsl:template>
    
    <xsl:template match="raml:alignedFreeSequence">
        <aml:alignedFreeSequence><xsl:value-of select="."/></aml:alignedFreeSequence>
    </xsl:template>
    
    <xsl:template match="raml:emptySequence">
        <aml:emptySequence><xsl:value-of select="."/></aml:emptySequence>
    </xsl:template>
    
</xsl:stylesheet>
