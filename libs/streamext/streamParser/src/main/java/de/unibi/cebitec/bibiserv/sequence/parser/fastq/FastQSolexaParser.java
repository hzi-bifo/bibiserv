
package de.unibi.cebitec.bibiserv.sequence.parser.fastq;

import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternType;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/**
 * The Parser for FastQSolexa
 * @author Thomas Gatter - tgatter@cebitec.uni-bielefeld.de
 */
public class FastQSolexaParser extends FastQParserBase {

    
    public FastQSolexaParser(BufferedReader input, BufferedWriter output,
            PatternType patternType) {
        super(input, output, patternType);
    }
    
    @Override
    protected String getName() {
       return "Solexa";
    }

    @Override
    protected FastQVariants getVariant() {
        return FastQVariants.Solexa;
    }
    
}
