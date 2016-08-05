/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010/11 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
 * All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License("CDDL") (the "License"). You
 * may not use this file except in compliance with the License. You can
 * obtain a copy of the License at http://www.sun.com/cddl/cddl.html
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.  When distributing the software, include
 * this License Header Notice in each file.  If applicable, add the following
 * below the License Header, with the fields enclosed by brackets [] replaced
 *  by your own identifying information:
 *
 * "Portions Copyrighted 2010/11 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de"
 *
 * Contributor(s): Richard Madsack, Madis Rumming, Jan Krueger
 *
 */
package de.unibi.cebitec.bibiserv.util.streamvalidate;

import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternType;



/**
 * AbstractSequenceValidator implements SequenceValidator Interface.
 * This is thought for sequence formats that allow aligned and unaligned types.
 * For alignment only formats please see AbstractAlignmentOnlyValidator!
 * 
 * @author Jan Krueger - jkrueger(at)cebitec.uni-bielefeld.de 
 *         Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public abstract class AbstractAlignmentValidator extends AbstractSequenceValidator implements AlignmentStreamValidator {

    public AbstractAlignmentValidator() {
    }

    @Override
   protected void validationStep(int sequenceLength, int nextSequenceLength, int numSequences) throws ValidationException {
        super.validationStep(sequenceLength, nextSequenceLength, numSequences);
        if(isAlignment() && sequenceLength != nextSequenceLength) {
            throw new ValidationException("Alignment is specified but sequences differ in length.");
        }
    }
    
    // ############################## private ##############################
    private PatternType patterntype;

    @Override
    protected PatternType getPatterntype() {

        if (patterntype == null) {
            if (isAlignment()) {
                switch (getContent()) {
                    case NA:
                        if (getStrictness().equals(STRICTNESS.strict)) {
                            patterntype = PatternType.BT_gappedNucleotideSequence;
                        } else {
                            patterntype = PatternType.BT_gappedAmbiguousNucleotideSequence;
                        }
                        break;
                    case DNA:
                        if (getStrictness().equals(STRICTNESS.strict)) {
                            patterntype = PatternType.BT_gappedDnaSequence;
                        } else {
                            patterntype = PatternType.BT_gappedAmbiguousDnaSequence;
                        }
                        break;
                    case RNA:
                        if (getStrictness().equals(STRICTNESS.strict)) {
                            patterntype = PatternType.BT_gappedRnaSequence;
                        } else {
                            patterntype = PatternType.BT_gappedAmbiguousRnaSequence;
                        }
                        break;
                    case AA:
                        if (getStrictness().equals(STRICTNESS.strict)) {
                            patterntype = PatternType.BT_gappedAminoAcidOneLetterSequence;
                        } else {
                            patterntype = PatternType.BT_gappedAmbiguousAminoAcidOneLetterSequence;
                        }
                        break;
                }
            } else {
                patterntype = super.getPatterntype();
            }

        }
        return patterntype;
    }
    // ############################## getter/setter ##############################
    boolean alignment;

    @Override
    public boolean isAlignment() {
        return alignment;
    }

    @Override
    public void setAlignment(boolean alignment) {
        this.alignment = alignment;
        if (alignment) {
            setCardinality(CARDINALITY.multi);
        }
    }
}
