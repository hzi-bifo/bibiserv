<?xml version="1.0" encoding="UTF-8"?>
<!-- Stylesheet to extract sequence information from RNAStructML to SequenceML -->
<xsl:stylesheet 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:sequenceml="http://hobit.sourceforge.net/xsds/20060201/sequenceML"
    xml:lang="en" version="1.0">
    <xsl:output indent="yes" method="xml"/>
    <xsl:param name="seqtype" select="''"/>
    
    <!-- find the main tag and replace it by the SequenceML main tag, adding schemalocation on the way -->
    <xsl:template match="EMBL_Services">
        <sequenceml:sequenceML>
            <xsl:attribute namespace="http://www.w3.org/2001/XMLSchema-instance" name="schemaLocation">
                <xsl:text>http://hobit.sourceforge.net/xsds/20060201/sequenceML http://bibiserv.techfak.uni-bielefeld.de/xsd/net/sourceforge/hobit/20060201/sequenceML.xsd</xsl:text>
            </xsl:attribute>
            <!-- search + extract sequences -->
            <xsl:apply-templates select="//sequence"/>
        </sequenceml:sequenceML>
    </xsl:template>
    
    <xsl:template match="sequence">
        <!-- for the matched sequence tag, extract the necessary subinformation -->
        <sequenceml:sequence>
            <!-- set ID from Accession number -->
            <xsl:attribute name="seqID">
                <xsl:value-of select="../../entry/@accession"/>
            </xsl:attribute>
            <!-- set description from description, if available -->
            <xsl:variable name="desc" select="../description"/>
            <xsl:if test="$desc!=''">
                <sequenceml:description><xsl:value-of select="$desc"/></sequenceml:description>
            </xsl:if>
            <!-- select sequence content according to given sequence type parameter -->
            <xsl:variable name="seqdat" select="."/>
            <xsl:variable name="tr" select="translate($seqdat,'abcdefghijklmnopqrstuvwxz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
            <xsl:choose>
                <xsl:when test="$seqtype=0">
                    <sequenceml:nucleicAcidSequence><xsl:value-of select="$tr"/> </sequenceml:nucleicAcidSequence>
                </xsl:when>
                <xsl:when test="$seqtype=1">
                    <sequenceml:aminoAcidSequence><xsl:value-of select="$tr"/></sequenceml:aminoAcidSequence>
                </xsl:when>
                <xsl:when test="$seqtype=2">
                    <sequenceml:freeSequence><xsl:value-of select="$tr"/></sequenceml:freeSequence>
                </xsl:when>
                <xsl:otherwise>
                    <sequenceml:freeSequence><xsl:value-of select="$tr"/></sequenceml:freeSequence>
                </xsl:otherwise>
            </xsl:choose>
            
        </sequenceml:sequence>
    </xsl:template>
</xsl:stylesheet>
