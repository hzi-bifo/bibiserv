/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.sequence.parser.AlignmentML;

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
public class AlignmentMLXMLParser extends AbstractXMLParser {

    private int sequenceLength;
    private int currentLength;
    private Set<String> ids;
    private boolean sequence;

    public AlignmentMLXMLParser(BufferedReader input, BufferedWriter output,
            PatternType patternType, String sequenceTypeName) {
        super(input, output, patternType, sequenceTypeName);
        ids = new HashSet<>();
    }

    @Override
    public void parseAndValidate() throws SequenceParserException, ForcedAbortOfPartValidation {

        PipedInputStream xmlInput = new PipedInputStream();
        copyStreams(input, xmlInput);

        try {

            XMLInputFactory2 ifact =  (XMLInputFactory2)XMLInputFactory.newInstance();
            XMLStreamReader2 sr = (XMLStreamReader2) ifact.createXMLStreamReader(xmlInput);

            int count = 0;
            Stack<String> stack = new Stack<>();

            while (sr.hasNext()) {
                
                 // abort condition in case of part validation
                checkLength();
                
                int event = sr.next();

                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        String tag = sr.getLocalName();
                        
                        if(stack.empty() && !tag.equals("alignmentML")) {
                            throw new SequenceParserException("Tag alignmentML expected but found "+tag+" instead.");
                        }
                        if (tag.equals("alignment")) {
                            // reset sequence length for every alignment
                            sequenceLength = -1;
                            if (!stack.peek().equals("alignmentML")) {
                                throw new SequenceParserException("Invalid start of alignment tag.");
                            }                         
                        } else if (tag.equals("sequence")) {
                            
                            if (!stack.peek().equals("alignment")) {
                                throw new SequenceParserException("Invalid start of sequence tag.");
                            }  
                            
                            //check id
                            String id = sr.getAttributeValue("", "seqID");
                            testHeader(id);
                            // reset currentLength
                            currentLength = 0;
                        } else if (AlignmentMLSequenceToken.contains(tag)) {
                            
                            if (!stack.peek().equals("sequence")) {
                                throw new SequenceParserException("Invalid start of "+tag+" tag.");
                            }  
                            
                            if(!tag.equals(sequenceTypeName)) {
                                throw new SequenceParserException("One or more sequences do not contain sequence data of the needed type: "+sequenceTypeName+".");
                            }
                            count++;
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
                        if(sequenceLength==-1) {
                            sequenceLength = currentLength;
                        } else if (sequenceLength!= currentLength){
                            throw new SequenceParserException("The sequences are not equal in length.");
                        }
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        if(sequence) { 
                            String sequenceStr = sr.getText();
                            currentLength += sequenceStr.length();
                            if (!SequenceValidator.validate(patternType, sequenceStr)) {
                                throw new SequenceParserException("Data was erroneous, did not correctly validate as " + patternType.name() + ".");
                            }
                        }
                        break;
                }

            }
            
            if(count<2) {
                throw new SequenceParserException("Multiple sequences expected.");
            }
            
        } catch (XMLStreamException ex) {
            throw new SequenceParserException("A validation error occured: "+ex);
        }
    }

    private void testHeader(String id) throws SequenceParserException {
        if (ids.contains(id)) {
            throw new SequenceParserException("Id '" + id + "' exists more than once.");
        }
        ids.add(id);
    }
}
