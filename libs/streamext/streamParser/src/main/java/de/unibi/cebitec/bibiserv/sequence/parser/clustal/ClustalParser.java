package de.unibi.cebitec.bibiserv.sequence.parser.clustal;

import de.unibi.cebitec.bibiserv.sequence.parser.AbstractSequenceParser;
import de.unibi.cebitec.bibiserv.sequence.parser.ForcedAbortOfPartValidation;
import de.unibi.cebitec.bibiserv.sequence.parser.ReadLineMessage;
import de.unibi.cebitec.bibiserv.sequence.parser.SequenceParserException;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.PatternType;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.SequenceValidator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A parser for the alignment only format clustal.
 *
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class ClustalParser extends AbstractSequenceParser {

    private ClustalParserState state;
    private Map<String, String> idToSequenceTmp;
    private Map<String, Boolean> idToFound;
    private String conservationInfo;
    private String lineTmp;
    // always stores how many bases remain from last block in tmp
    private int lengthTmp;
    // how many bases have already been written to output
    private int writtenLength;
    private Map<String,String> idToWrite;
    private String conservationIndent;
    
    private boolean lastSequenceRead;
    private boolean firstRead;
    private static final int SEQUENCE_WIDTH = 60;
    private static final String FILEBEGIN = "CLUSTAL";
    private static final int JUSTREAD_AND_DECIDE_LATER = 1;
    private static final int MAXIMUM_LINE_LENGTH = 1000000;
    private static final Pattern CONSERVATION_PATTERN = Pattern.compile("[:.* ]*");

    /**
     * Inherit the super constructor.
     */
    public ClustalParser(BufferedReader input, BufferedWriter output,
            PatternType patternType) {
        super(input, output, patternType);
        state = ClustalParserState.readHeader;
        idToSequenceTmp = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        idToFound = new HashMap<String, Boolean>();
        conservationInfo = "";
        lineTmp = "";
        lastSequenceRead = false;
        firstRead = true;
        lengthTmp = 0;
        writtenLength = 0;
    }

    @Override
    public int parseAndValidateNextBlock() throws SequenceParserException, ForcedAbortOfPartValidation {
        // is there another possible block?
        if (lastSequenceRead) {
            return -1;
        }
        
        if(!firstRead){
            state = ClustalParserState.seekFirstSequence;
        }
        ReadLineMessage m;
        while ((m = readLine()) == ReadLineMessage.lineRead) {
            // just do it, validation is called automatically
        }
        
        if( m == ReadLineMessage.noMoreLines && state == ClustalParserState.readSequence){
            lineEmpty();
        }
        
        if (state != ClustalParserState.allDone && state != ClustalParserState.readOneEmptyLine) {
            throw new SequenceParserException("No sequence or end of sequence was found!");
        }
        
        if (m == ReadLineMessage.noMoreLines) {
            writeBlock(true);
            lastSequenceRead = true;
        }
        firstRead = false;
        return idToFound.size();
    }

    @Override
    protected int lineBeginSize() {
        // can't be decided by any fixed number, so just make it the smallest
        return 1;
    }

    @Override
    protected void lineEmpty() throws SequenceParserException {
        switch (state) {
            case readOneEmptyLine:
                state = ClustalParserState.allDone;
                break;
            case readSequence:
                // this can be interpreted in a way that there is no conservation line
                state = ClustalParserState.allDone;
                try {
                    int len = idToSequenceTmp.values().iterator().next().length() - lengthTmp;
                    StringBuffer outputBuffer = new StringBuffer(len);
                    for (int i = 0; i < len; i++) {
                        outputBuffer.append(" ");
                    }
                    conservationInfo += outputBuffer.toString();
                    validateBlock();
                    writeBlock(false);
                    state = ClustalParserState.allDone;
                } catch (NoSuchElementException e) {
                    throw new SequenceParserException("No sequence data found before conservation.");
                }
                break;
            case readHeader:
                throw new SequenceParserException("Header line was expected but empty line found.");
        }
    }

    @Override
    protected int lineBegin(String value) throws SequenceParserException {
        switch (state) {
            case readOneEmptyLine:
                throw new SequenceParserException("Empty line expected but found something else on line " + lineNumber + ".");
            case allDone:
                return END_READLINE;
        }

        lineTmp = "";
        return JUSTREAD_AND_DECIDE_LATER;
    }

    @Override
    protected void lineTmp(int ident, String value) throws SequenceParserException {
        lineTmp += value;
        if (lineTmp.length() > MAXIMUM_LINE_LENGTH) {
            throw new SequenceParserException("Clustal only allows 60 sequence-Symbols per line. This line  number " + lineNumber + " is now over " + MAXIMUM_LINE_LENGTH + " characters long and is thus beyond autocorrection.");
        }
    }

    @Override
    protected void lineEnd(int ident, String value) throws SequenceParserException {
        lineTmp += value;
        switch (state) {
            case readHeader:
                if (!value.startsWith(FILEBEGIN)) {
                    throw new SequenceParserException("File does not start with the CLUSTAL token.");
                }
                write(lineTmp);
                writeNewLine();
                writeNewLine();
                state = ClustalParserState.seekFirstSequence;
                break;
            case seekFirstSequence:
                state = ClustalParserState.readSequence;
            case readSequence:
                // test if this is the conservation string
                Matcher m = CONSERVATION_PATTERN.matcher(lineTmp);
                if (m.matches()) {
                    // this is the conservation string, hopefully...
                    try {
                        int len = idToSequenceTmp.values().iterator().next().length() - lengthTmp;
                        if (len > lineTmp.length()) {
                            throw new SequenceParserException("Not enough chars in conservation on line " + lineNumber + ".");
                        }
                        int pos = lineTmp.length() - len;
                        if (!lineTmp.substring(0, pos).matches("\\s+|^$")) {
                            throw new SequenceParserException("The conservation string could not be determined, possibly because of trailing spaces, on line " + lineNumber + ".");
                        }
                        conservationInfo += lineTmp.substring(pos);

                        validateBlock();
                        writeBlock(false);
                        state = ClustalParserState.readOneEmptyLine;
                        return;
                    } catch (NoSuchElementException e) {
                        throw new SequenceParserException("No sequence data found before conservation.");
                    }
                }

                // This should be normal sequence line, or possibly an errounous conservation line
                // possibilities are:
                // [id sequence], [id sequence number], [ id sequence number], [ id sequence], errounous line
                String[] split = lineTmp.trim().split("\\s+");
                if (split.length < 2) {
                    throw new SequenceParserException("Sequence line was expected but something else was found " + lineNumber + ". This can be caused by an erronous conservation line.");
                }

                if (!SequenceValidator.validate(patternType, split[1])) {
                        throw new SequenceParserException("Data was erroneous, did not correctly validate as " + patternType.name() +" on line "+lineNumber+".");
                }
                addId(split[0], split[1]);
                break;
        }
    }

    private void addId(String id, String sequence) throws SequenceParserException {
        // this is the first time we read a block
        // no entrys already exist, that must be found
        if (firstRead) {
            if (idToFound.containsKey(id)) {
                throw new SequenceParserException("Same ID multiple times in one block on line " + lineNumber + ".");
            }
            idToFound.put(id, true);
            idToSequenceTmp.put(id, sequence);
        } else {
            if (!idToFound.containsKey(id)) {
                throw new SequenceParserException("ID found that was not introduced in first block on line " + lineNumber + ".");
            }
            if (idToFound.get(id)) {
                throw new SequenceParserException("Same ID multiple times in one block on line " + lineNumber + ".");
            }
            idToFound.put(id, true);
            idToSequenceTmp.put(id, idToSequenceTmp.get(id) + sequence);
        }
    }

    private void writeBlock(boolean finalize) throws SequenceParserException {
        // first time we write a block?
        if(firstRead){
            //Initialize idention data
            int frontSpaces = 0;
            for(String key :idToFound.keySet()){
                if(key.length() > frontSpaces) {
                    frontSpaces = key.length();
                }
            }
            frontSpaces += 2;
            
            idToWrite = new HashMap<String,String>();
            
            for(String id: idToFound.keySet()) {
                StringBuilder tmp = new StringBuilder();
                tmp.append(id);
                for(int i=0;i<frontSpaces-id.length();i++){
                     tmp.append(" ");
                }
                idToWrite.put(id, tmp.toString());
            }
            StringBuilder tmp = new StringBuilder();
            for(int i=0;i<frontSpaces;i++){
                     tmp.append(" ");
             }
            conservationIndent = tmp.toString();
        }
        
        while (true) {
            // write sequences, TreeMap keeps them in order
            for(Map.Entry<String,String> entry: idToSequenceTmp.entrySet()){
                if(entry.getValue().length()<SEQUENCE_WIDTH){
                    lengthTmp = entry.getValue().length();
                    if (finalize) {
                        finalizeFile();
                    }
                    return;
                }
                write(idToWrite.get(entry.getKey()));
                write(entry.getValue().substring(0,SEQUENCE_WIDTH));
                write(" "+ (writtenLength+60));
                writeNewLine();
                
                entry.setValue(entry.getValue().substring(SEQUENCE_WIDTH));
            }
            // write conservation
            write(conservationIndent);
            write(conservationInfo);
            conservationInfo = conservationInfo.substring(SEQUENCE_WIDTH);
            
            writeNewLine();
            writeNewLine();
        
            writtenLength +=60;
        }
    }
    
    private void finalizeFile()  throws SequenceParserException{
        for(Map.Entry<String,String> entry: idToSequenceTmp.entrySet()){
                write(idToWrite.get(entry.getKey()));
                write(entry.getValue());
                write(" "+ (writtenLength+entry.getValue().length()));
                writeNewLine();
                
                entry.setValue("");
            }
            // write conservation
            write(conservationIndent);
            write(conservationInfo);
            writeNewLine();
            writeNewLine();
    }
    

    private void validateBlock() throws SequenceParserException{
        // test if ids where not found
        if(idToFound.containsValue(false)){
            throw new SequenceParserException("One or more id is missing in a block.");
        }
        // reset found value
        for(Map.Entry<String,Boolean> entry: idToFound.entrySet()){
            entry.setValue(false);
        }
        
        int len = -1;
        // check that all sequences have the same length
        for(String sequence: idToSequenceTmp.values()){
            if(len ==-1){
                len= sequence.length();
            }
            if(len!=sequence.length()){
                throw new SequenceParserException("The block before line "+lineNumber+" contains sequences of different length.");
            }
        }
        if(len!=conservationInfo.length()){
             throw new SequenceParserException("The block before line "+lineNumber+" contains a conservation of illegal length");
        } 
    }

    
}
