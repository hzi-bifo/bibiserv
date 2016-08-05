
package de.unibi.cebitec.bibiserv.sequence.parser.SequenceML;

import de.unibi.cebitec.bibiserv.sequence.parser.AbstractXMLParser;
import de.unibi.cebitec.bibiserv.sequence.parser.ForcedAbortOfPartValidation;
import de.unibi.cebitec.bibiserv.sequence.parser.SequenceParserException;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternType;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.SequenceValidator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PipedInputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;

/**
 * This Parser clones the inputstream and validates it against a slighly
 * modified xsd file by using Woodstox StAX2.
 *
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class SequenceMLXMLParser extends AbstractXMLParser {

    private int numberOfSequences;
    private Set<String> ids;
    private boolean multi;
    private boolean single;
    private boolean sequence;

    public SequenceMLXMLParser(BufferedReader input, BufferedWriter output,
            PatternType patternType, String sequenceTypeName, boolean multi, boolean single) {
        super(input, output, patternType, sequenceTypeName);
        ids = new HashSet<>();
        numberOfSequences = 0;
        this.multi = multi;
        this.single = single;
    }

    @Override
    public void parseAndValidate() throws SequenceParserException, ForcedAbortOfPartValidation {

        PipedInputStream xmlInput = new PipedInputStream();
        copyStreams(input, xmlInput);

        try {

            XMLInputFactory2 ifact = (XMLInputFactory2) XMLInputFactory.newInstance();
            XMLStreamReader2 sr = (XMLStreamReader2) ifact.createXMLStreamReader(xmlInput);
            
           Stack<String> stack = new Stack<>();

            while (sr.hasNext()) {

                // abort condition in case of part validation
                checkLength();
                
                int event = sr.next();

                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        String tag = sr.getLocalName();
                        
                        if(stack.empty() && !tag.equals("sequenceML")) {
                            throw new SequenceParserException("Tag sequenceML expected but found "+tag+" instead.");
                        }

                        if (tag.equals("sequence")) {
                            
                            if (!stack.peek().equals("sequenceML")) {
                                throw new SequenceParserException("Invalid start of sequence tag.");
                            }    
                            
                            //check id
                            String id = sr.getAttributeValue("", "seqID");
                            testHeader(id);
                            // reset increase number of sequences
                            numberOfSequences++;
                            if(single && numberOfSequences>1) {
                                throw new SequenceParserException("Data is in SequenceML format but contains more than one sequences!");
                            }
                        } else if (SequenceMLSequenceToken.contains(tag)) {
                            
                            if (!stack.peek().equals("sequence")) {
                                throw new SequenceParserException("Invalid start of "+tag+" tag.");
                            }  
                            
                            if(!tag.equals(sequenceTypeName)) {
                                throw new SequenceParserException("One or more sequences do not contain sequence data of the needed type: "+sequenceTypeName+".");
                            }
                            sequence = true;
                        }
                        stack.push(tag);
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        String open = stack.pop();
                        tag = sr.getLocalName();
                        if(!open.equals(tag)) {
                            throw new SequenceParserException("Tag "+open+" not closed. Found closing tag "+tag+" instead.");
                        }
                        sequence = false;
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        if (sequence) {
                            String sequenceStr = sr.getText();;
                             if(!SequenceValidator.validate(patternType, sequenceStr)) {
                                throw new SequenceParserException("Data was erroneous, did not correctly validate as " + patternType.name() + ".");
                            }
                        }
                        break;
                }

            }
        } catch (XMLStreamException ex) {
            throw new SequenceParserException("A validation error occured: "+ex);
        }
        if(single && numberOfSequences!=1) {
            throw new SequenceParserException("Data is in SequenceML format but contains no sequences!");
        } else if(multi && numberOfSequences<2) {
             throw new SequenceParserException("Data is in SequenceML format but contains less than two sequences!");
        }
        
    }

    private void testHeader(String id) throws SequenceParserException {
        if (ids.contains(id)) {
            throw new SequenceParserException("Id '" + id + "' exists more than once.");
        }
        ids.add(id);
    }
}
