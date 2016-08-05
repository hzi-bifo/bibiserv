
package de.unibi.cebitec.bibiserv.sequence.parser.fastq;


import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternType;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/**
 * The Parser for FastQSanger
 * @author Thomas Gatter - tgatter@cebitec.uni-bielefeld.de
 */
public class FastQSangerParser extends FastQParserBase {

    
    public FastQSangerParser(BufferedReader input, BufferedWriter output,
            PatternType patternType) {
        super(input, output, patternType);
    }
    
    @Override
    protected String getName() {
       return "Sanger";
    }

    @Override
    protected FastQVariants getVariant() {
        return FastQVariants.Sanger;
    }
    
}
