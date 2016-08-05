<?xml version="1.0" encoding="UTF-8"?>
<!-- Stylesheet to extract sequence information from TSeqXML to SequenceML -->
<xsl:stylesheet 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:sequenceml="http://hobit.sourceforge.net/xsds/20060201/sequenceML"
    xml:lang="en" version="1.0">
    <xsl:output indent="yes" method="xml"/>
    
    <!-- find the main tag and replace it by the SequenceML main tag, adding schemalocation on the way -->
    <xsl:template match="/">
        <sequenceml:sequenceML>
            <xsl:attribute namespace="http://www.w3.org/2001/XMLSchema-instance" name="schemaLocation">
                <xsl:text>http://hobit.sourceforge.net/xsds/20060201/sequenceML http://bibiserv.techfak.uni-bielefeld.de/xsd/net/sourceforge/hobit/20060201/sequenceML.xsd</xsl:text>
            </xsl:attribute>
            <!-- search + extract sequences -->
            <xsl:apply-templates select="//TSeq"/>
        </sequenceml:sequenceML>
    </xsl:template>
    
    <xsl:template match="TSeq">
        <!-- for the matched sequence tag, extract the necessary subinformation -->
        <sequenceml:sequence>
            <!-- set ID from various ids number -->
            <xsl:variable name="gi" select="./TSeq_gi"/>
            <xsl:variable name="gb" select="./TSeq_accver"/>
            <xsl:attribute name="seqID">
                <xsl:if test="$gi!=''">
                    <xsl:text>gi|</xsl:text><xsl:value-of select="$gi"/><xsl:text>|</xsl:text>
                </xsl:if>
                <xsl:if test="$gb!=''">
                    <xsl:text>gb|</xsl:text><xsl:value-of select="$gb"/><xsl:text>|</xsl:text>
                </xsl:if>
            </xsl:attribute>
            <!-- set name from orgname, if available -->
            <xsl:variable name="name" select="./TSeq_orgname"/>
            <xsl:if test="$name!=''">
                <sequenceml:name><xsl:value-of select="$name"/></sequenceml:name>
            </xsl:if>
            <!-- set description from defline, if available -->
            <xsl:variable name="desc" select="./TSeq_defline"/>
            <xsl:if test="$desc!=''">
                <sequenceml:description><xsl:value-of select="$desc"/></sequenceml:description>
            </xsl:if>
            <!-- select sequence content according to given sequence type parameter -->
            <xsl:variable name="seqdat" select="./TSeq_sequence"/>
            <xsl:variable name="seqtype" select="./TSeq_seqtype/@value"/>
            <xsl:variable name="tr" select="translate($seqdat,'abcdefghijklmnopqrstuvwxz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
            <xsl:choose>
                <xsl:when test="$seqtype='nucleotide'">
                    <sequenceml:nucleicAcidSequence><xsl:value-of select="$tr"/> </sequenceml:nucleicAcidSequence>
                </xsl:when>
                <xsl:when test="$seqtype='protein'">
                    <sequenceml:aminoAcidSequence><xsl:value-of select="$tr"/></sequenceml:aminoAcidSequence>
                </xsl:when>
                <xsl:otherwise>
                    <sequenceml:freeSequence><xsl:value-of select="$tr"/></sequenceml:freeSequence>
                </xsl:otherwise>
            </xsl:choose>         
        </sequenceml:sequence>
    </xsl:template>
</xsl:stylesheet>
