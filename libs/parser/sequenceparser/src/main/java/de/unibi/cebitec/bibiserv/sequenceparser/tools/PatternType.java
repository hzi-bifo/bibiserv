/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.unibi.cebitec.bibiserv.sequenceparser.tools;


/**
 *  Collection of regular expressions describing SequenceTypes defined in the
 * BioTypes project (http://bioschemas.sourceforge.net/index.php/BioTypes)
 * @author rmadsack, mrumming
 */
public enum PatternType {

    //nucleotide pattern
    BT_gappedAmbiguousNucleotideSequence("^[AGCTURYMKSWHBVDN\\-]+$", PatternCategory.NucleicAcid),
    BT_ambiguousNucleotideSequence("^[AGCTURYMKSWHBVDN]+$", PatternCategory.NucleicAcid),
    BT_gappedNucleotideSequence("^[AGCTU|\\-]+$", PatternCategory.NucleicAcid),
    BT_nucleotideSequence("^[AGCTU]+$", PatternCategory.NucleicAcid),

    //DNA pattern
    BT_gappedAmbiguousDnaSequence("^[AGCTRYMKSWHBVDN\\-]+$", PatternCategory.NucleicAcid),
    BT_gappedDnaSequence("^[AGCT|\\-]+$", PatternCategory.NucleicAcid),
    BT_ambiguousDnaSequence("^[AGCTRYMKSWHBVDN]+$", PatternCategory.NucleicAcid),
    BT_dnaSequence("^[AGCT]+$", PatternCategory.NucleicAcid),

    //RNA pattern
    BT_gappedAmbiguousRnaSequence("^[AGCURYMKSWHBVDN\\-]+$", PatternCategory.NucleicAcid),
    BT_gappedRnaSequence("^[AGCU\\-]+$", PatternCategory.NucleicAcid),
    BT_ambiguousRnaSequence("^[AGCURYMKSWHBVDN]+$", PatternCategory.NucleicAcid),
    BT_rnaSequence("^[AGCU]+$", PatternCategory.NucleicAcid),

    //AminoAcid pattern
    BT_gappedAmbiguousTerminalAminoAcidOneLetterSequence("^[ARNDCQEGHILKMFPSTUWYVBZX\\-*]+$", PatternCategory.AminoAcid),
    BT_gappedAmbiguousAminoAcidOneLetterSequence("^[ARNDCQEGHILKMFPSTUWYVBZX\\-]+$", PatternCategory.AminoAcid),
    BT_ambiguousAminoAcidOneLetterSequence("^[ARNDCQEGHILKMFPSTUWYVBZX]+$", PatternCategory.AminoAcid),
    BT_gappedAminoAcidOneLetterSequence("^[ARNDCQEGHILKMFPSTUWYV\\-]+$", PatternCategory.AminoAcid),
    BT_terminalAminoAcidOneLetterSequence("^[ARNDCQEGHILKMFPSTUWYV*]+$", PatternCategory.AminoAcid),
    BT_aminoAcidOneLetterSequence("^[ARNDCQEGHILKMFPSTUWYV]+$", PatternCategory.AminoAcid),

    //Structure + Shape
    BT_rnaSecondaryStructureSequence("^[\\(\\)\\[\\]\\{\\}<\\>\\.]+$", PatternCategory.Structure),
    BT_rnaSecondaryStructureSequenceWithEnergy("^[\\(\\)\\[\\]\\{\\}\\<\\>\\.]+?\\s+\\(?-?\\d+\\.\\d*?\\)?\\s*$", PatternCategory.Structure),
    BT_rnaSecondaryStructureSequenceClass("^[\\(\\)\\[\\]\\{\\}<\\>\\_]+$", PatternCategory.Shape),
    BT_gappedRnaSecondaryStructureSequence("^[\\(\\)\\[\\]\\{\\}<\\>\\.\\-]+$", PatternCategory.Structure),
    BT_gappedRnaSecondaryStructureSequenceWithEnergy("^([\\(\\)\\[\\]\\{\\}\\<\\>\\.\\-]+?)(?:\\s+\\(?(-?\\d+\\.\\d*?)\\)?\\s*)?$", PatternCategory.Structure),
    BT_gappedRnaSecondaryStructureSequenceClass("^[\\(\\)\\[\\]\\{\\}<\\>\\_\\-]+$", PatternCategory.Shape),

    //general/fasta header
    FASTA_header("^>\\s?\\S+\\s+[\\S\\s]*", PatternCategory.General),
    isLowercase("[a-z]+", PatternCategory.General),
    isFreeSequence(".+", PatternCategory.Free),
    isClustalLine("\\w+\\s+[a-zA-Z[-]]+", PatternCategory.General),
    isClustalHeader("^CLUSTAL.*", PatternCategory.General),

    //MSF
    MSF_gappedRnaSequence("^[AGCU\\.\\~\\-]+$", PatternCategory.NucleicAcid),
    MSF_gappedAminoAcidOneLetterSequence("^[ARNDCQEGHILKMFPSTUWYV\\~\\.\\-]+$", PatternCategory.AminoAcid),
    MSF_gappedDnaSequence("^[AGCT\\.\\~\\-]+$", PatternCategory.NucleicAcid)
            ;

    /*
    BT_terminalAminoAcidThreeLetterSequence("^[(ALA|ARG|ASN|ASP|CYS|GLN|GLU|GLY|HIS|ILE|LEU|LYS|MET|PHE|PRO|SER|THR|SEC|TRP|TYR|VAL|TER)]+$"),
    BT_aminoAcidThreeLetterSequence("^[(ALA|ARG|ASN|ASP|CYS|GLN|GLU|GLY|HIS|ILE|LEU|LYS|MET|PHE|PRO|SER|THR|SEC|TRP|TYR|VAL)]+$"),
    BT_gappedAmbiguousSecondaryStructureSequence("^[CHGIEBTS_X\\-]+$"),
    BT_ambiguousSecondaryStructureSequence("^[CHGIEBTS_X]+$"),
    BT_gappedSecondaryStructureSequence("^[CHGIEBTS_\\-]+$"),
    BT_secondaryStructureSequence("^[CHGIEBTS_]+$"),
    BT_gappedRnaSecondaryStructureSequence("^[\(\)\[\]\{\}&lt;&gt;\.\-]+$"),
    
    */        

    private final String pattern;
    private final PatternCategory category;

    PatternType(String pattern, PatternCategory category) {
        this.pattern = pattern;
        this.category = category;
    }

    public String getPattern(){
        return pattern;
    }

    public PatternCategory getCategory(){
        return category;
    }


}
