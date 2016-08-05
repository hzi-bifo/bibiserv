/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.sequenceparser.tools;


import de.unibi.cebitec.bibiserv.sequenceparser.tools.exception.ParserToolException;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.wrapper.SequenceCollection;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.wrapper.SequenceTypeInterface;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.BioseqRecord;

import java.util.List;

/**
 *  Class offers methods to check, if a given sequence can be validated as a
 * specific sequence type
 * @author rmadsack
 */
public class SequenceValidator {

    /**
     *
     */
    private static PatternController controller;

    //validation methods for ambiguous sequence Types
    /**
     * Checks if the given sequence matches the pattern: ^[ARNDCQEGHILKMFPSTUWYVXBZ]+$
     * @param sequence the sequence to check
     * @return if pattern matches: true, otherwise: false
     */
    @Deprecated
    public static boolean validateAmbiguousAminoAcidOneLetterSequence(String sequence) {
        return validate(PatternType.BT_ambiguousAminoAcidOneLetterSequence, sequence);
    }

    /**
     * Checks if the given sequence matches the pattern: ^[AGCTRYMKSWHBVDNX-]+$
     * @param sequence the sequence to check
     * @return if pattern matches: true, otherwise: false
     */
    @Deprecated
    public static boolean validateAmbiguousDnaSequence(String sequence) {
        return validate(PatternType.BT_ambiguousDnaSequence, sequence);
    }

    /**
     * Checks if the given sequence matches the pattern: ^[AGCTURYMKSWHBVDNX-]+$
     * @param sequence the sequence to check
     * @return if pattern matches: true, otherwise: false
     */
    @Deprecated
    public static boolean validateAmbiguousNucleotideSequence(String sequence) {
        return validate(PatternType.BT_ambiguousNucleotideSequence, sequence);
    }

    /**
     * Checks if the given sequence matches the pattern: ^[AGCURYMKSWHBVDNX-]+$
     * @param sequence the sequence to check
     * @return if pattern matches: true, otherwise: false
     */
    @Deprecated
    public static boolean validateAmbiguousRnaSequence(String sequence) {
        return validate(PatternType.BT_ambiguousRnaSequence, sequence);
    }

    //validation methods for normal sequence Types
    /**
     * Checks if the given sequence matches the pattern: ^[AGCT]+$
     * @param sequence the sequence to check
     * @return if pattern matches: true, otherwise: false
     */
    @Deprecated
    public static boolean validateDnaSequence(String sequence) {
        return validate(PatternType.BT_dnaSequence, sequence);
    }

    /**
     * Checks if the given sequence matches the pattern: ^[AGCU]+$
     * @param sequence the sequence to check
     * @return if pattern matches: true, otherwise: false
     */
    @Deprecated
    public static boolean validateRnaSequence(String sequence) {
        return validate(PatternType.BT_rnaSequence, sequence);
    }

    /**
     * Checks if the given sequence matches the pattern: ^[ARNDCQEGHILKMFPSTUWYV*]+$
     * @param sequence the sequence to check
     * @return if pattern matches: true, otherwise: false
     */
    @Deprecated
    public static boolean validateTerminalAminoAcidOneLetterSequence(String sequence) {
        return validate(PatternType.BT_terminalAminoAcidOneLetterSequence, sequence);
    }

    /**
     * Checks if the given sequence matches the pattern: ^[AGCTU]+$
     * @param sequence the sequence to check
     * @return if pattern matches: true, otherwise: false
     */
    @Deprecated
    public static boolean validateNucleotideSequence(String sequence) {
        return validate(PatternType.BT_nucleotideSequence, sequence);
    }

    //validation methods for gapped sequence Types
    /**
     * Checks if the given sequence matches the pattern: ^[ARNDCQEGHILKMFPSTUWYVXBZ-]+$
     * @param sequence the sequence to check
     * @return if pattern matches: true, otherwise: false
     */
    @Deprecated
    public static boolean validateGappedAmbiguousAminoAcidOneLetterSequence(String sequence) {
        return validate(PatternType.BT_gappedAmbiguousAminoAcidOneLetterSequence, sequence);
    }

    /**
     * Checks if the given sequence matches the pattern: ^[AGCTRYMKSWHBVDNX-]+$
     * @param sequence the sequence to check
     * @return if pattern matches: true, otherwise: false
     */
    @Deprecated
    public static boolean validateGappedAmbiguousDnaSequence(String sequence) {
        return validate(PatternType.BT_gappedAmbiguousDnaSequence, sequence);
    }

    /**
     * Checks if the given sequence matches the pattern: ^[AGCTURYMKSWHBVDNX-]+$
     * @param sequence the sequence to check
     * @return if pattern matches: true, otherwise: false
     */
    @Deprecated
    public static boolean validateGappedAmbiguousNucleotideSequence(String sequence) {
        return validate(PatternType.BT_gappedAmbiguousNucleotideSequence, sequence);
    }

    /**
     * Checks if the given sequence matches the pattern: ^[AGCURYMKSWHBVDNX-]+$
     * @param sequence the sequence to check
     * @return if pattern matches: true, otherwise: false
     */
    @Deprecated
    public static boolean validateGappedAmbiguousRnaSequence(String sequence) {
        return validate(PatternType.BT_gappedAmbiguousRnaSequence, sequence);
    }

    /**
     * Checks if the given sequence matches the pattern: ^[ARNDCQEGHILKMFPSTUWYVXBZ-*]+$
     * @param sequence the sequence to check
     * @return if pattern matches: true, otherwise: false
     */
    @Deprecated
    public static boolean validateGappedAmbiguousTerminalAminoAcidOneLetterSequence(String sequence) {
        return validate(PatternType.BT_gappedAmbiguousTerminalAminoAcidOneLetterSequence, sequence);
    }

    /**
     * Checks if the given sequence matches the pattern: ^[ARNDCQEGHILKMFPSTUWYV-]+$
     * @param sequence the sequence to check
     * @return if pattern matches: true, otherwise: false
     */
    @Deprecated
    public static boolean validateGappedAminoAcidOneLetterSequence(String sequence) {
        return validate(PatternType.BT_gappedAminoAcidOneLetterSequence, sequence);
    }

    /**
     * Checks if the given sequence matches the pattern: ^[AGCT|-]+$
     * @param sequence the sequence to check
     * @return if pattern matches: true, otherwise: false
     */
    @Deprecated
    public static boolean validateGappedDnaSequence(String sequence) {
        return validate(PatternType.BT_gappedDnaSequence, sequence);
    }

    /**
     * Checks if the given sequence matches the pattern: ^[AGCTU|-]+
     * @param sequence the sequence to check
     * @return if pattern matches: true, otherwise: false
     */
    @Deprecated
    public static boolean validateGappedNucleotideSequence(String sequence) {
        return validate(PatternType.BT_gappedNucleotideSequence, sequence);
    }

    /**
     * Checks if the given sequence matches the pattern: ^[AGCU-]+
     * @param sequence the sequence to check
     * @return if pattern matches: true, otherwise: false
     */
    @Deprecated
    public static boolean validateGappedRnaSequence(String sequence) {
        return validate(PatternType.BT_gappedRnaSequence, sequence);
    }

    //Rna-Structure
    /**
     * Checks if the given string matches the pattern: ^[\\(\\)\\[\\]\\{\\}&lt;&gt;\\.]+$
     * @param sequence the sequence to check
     * @return if pattern matches: true, otherwise: false
     */
    @Deprecated
    public static boolean validateRnaSecondaryStructureSequence(String sequence) {
        return validate(PatternType.BT_rnaSecondaryStructureSequence, sequence);
    }

    /**
     * Checks if the given string matches the pattern: "^[\\(\\)\\[\\]\\{\\}\\<\\>.]+?\\s+\\(?-?\\d+\\.\\d*?\\)?\\s*$
     * @param sequence the sequence to check
     * @return if pattern matches: true, otherwise: false
     */
    @Deprecated
    public static boolean validateRnaSecondaryStructureSequenceWithEnergy(String sequence) {
        return validate(PatternType.BT_rnaSecondaryStructureSequenceWithEnergy, sequence);
    }

    /**
     * Checks if the given string matches the pattern: ^[\\(\\)\\[\\]\\{\\}&lt;&gt;_]+$
     * @param sequence the sequence to check
     * @return if pattern matches: true, otherwise: false
     */
    @Deprecated
    public static boolean validateRnaSecondaryStructureSequenceClass(String sequence) {
        return validate(PatternType.BT_rnaSecondaryStructureSequenceClass, sequence);
    }

    //FASTA
    /**
     *
     * @param line
     * @return
     */
    @Deprecated
    public static boolean isFastaHeader(String line) {
        return validate(PatternType.FASTA_header, line);
    }

    /**
     *
     * @param data
     * @return
     * @throws ParserToolException
     */
    public static boolean validateSequenceCollection(SequenceCollection data) throws ParserToolException {
        return validateSequenceCollection(data, data.getPatternType());
    }

    /**
     * Validates every single sequence of a SequenceCollection against the given PatternType
     * @param data collection of sequences to be checked
     * @param pattern regular expression, allowed characters in this SequenceCollection
     * @return if all sequences are valide: true, otherwise: Exception is thrown
     * @throws ParserToolException non-valide sequence detected
     */
    public static boolean validateSequenceCollection(SequenceCollection data, PatternType pattern) throws ParserToolException {
        boolean valide = true;
        for (SequenceTypeInterface seqType : data.getSequences().values()) {
            if (valide) {
                valide = validate(pattern, seqType.getSequence());
                if (valide) {
                    continue;
                } else {
                    throw new ParserToolException("Unallowed character detected in sequence with ID: " + seqType.getSeqID() + " Pattern used(" + pattern.name() + "): " + pattern.getPattern());
                }
            }

        }
        return valide;
    }

    /**
     * Validates every single sequence of a List of BioseqRecords against the given PatternType
     * @param sequenceList list of BioseqRecords to be checked
     * @param pattern regular expression, allowed characters in this SequenceCollection
     * @return if all sequences are valide: true, otherwise: Exception is thrown
     * @throws ParserToolException non-valide sequence detected
     */
    public static boolean validateReadSeqCollection(List<BioseqRecord> sequenceList, PatternType pattern) throws ParserToolException {
        boolean valide = true;
        for (BioseqRecord sequence : sequenceList) {
            if (valide) {
                valide = validate(pattern, sequence.getseq().toString());
                if (valide) {
                    continue;
                } else {
                    throw new ParserToolException("Unallowed character detected in sequence with ID: " + sequence.getID() + " Pattern used(" + pattern.name() + "): " + pattern.getPattern());
                }
            }

        }
        return valide;
    }

    //private methods
    /**
     *
     * @return
     */
    private static PatternController getPatternController() {
        if (controller == null) {
            controller = new PatternController();
        }
        return controller;
    }

    /**
     * 
     * @param type
     * @param sequence
     * @return
     */
    public static boolean validate(PatternType type, String sequence) {
        return getPatternController().getPattern(type).matcher(sequence).matches();
    }
}
