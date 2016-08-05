package de.unibi.cebitec.bibiserv.sequence.parser.msf;

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
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A parser for the alignment only format msf in all varients (pileup, rich and
 * normal).
 *
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class MsfParser extends AbstractSequenceParser {

    private MsfParserState state;
    private Map<String, Integer> idToLength;
    private Map<String, String> idToSequenceTmp;
    private Map<String, Boolean> idToFound;
    private String lineTmp;
    /**
     * True Amino Acid, False Nucleid Acid
     */
    private boolean aa;
    private int expectedLength;
    private MsfFileType type;
    private Map<String, String> idToWrite;
    private String conservationIndent;
    private boolean lastSequenceRead;
    private boolean firstRead;
    private static final int SEQUENCE_WIDTH = 50;
    private static final int JUSTREAD_AND_DECIDE_LATER = 1;
    private static final int MAXIMUM_LINE_LENGTH = 1000000;
    private static Pattern GAPS = Pattern.compile("[~.]");
    private static Pattern IGNORED_IN_SEQUENCE = Pattern.compile("[\\s]+");
    private static Pattern NUMBER_LINE = Pattern.compile("[\\d\\s]*");
    private static final Pattern SPACE_INSERT = Pattern.compile("..........");

    /**
     * Inherit the super constructor.
     */
    public MsfParser(BufferedReader input, BufferedWriter output,
            PatternType patternType, boolean aa) {
        super(input, output, patternType);
        state = MsfParserState.readHeader;
        idToSequenceTmp = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        idToFound = new HashMap<String, Boolean>();
        idToLength = new HashMap<String,Integer>();

        lineTmp = "";
        lastSequenceRead = false;
        firstRead = true;
        this.aa = aa;
        expectedLength = -1;
    }

    @Override
    public int parseAndValidateNextBlock() throws SequenceParserException, ForcedAbortOfPartValidation {
        // is there another possible block?
        if (lastSequenceRead) {
            return -1;
        }

        if (!firstRead) {
            state = MsfParserState.seekFirstSequence;
        }
        ReadLineMessage m;
        while ((m = readLine()) == ReadLineMessage.lineRead) {
            // just do it, validation is called automatically
        }

        if (m == ReadLineMessage.noMoreLines && state == MsfParserState.readSequence) {
            lineEmpty();
        }

        if (state != MsfParserState.allDone) {
            throw new SequenceParserException("No sequence or end of sequence was found!");
        }

        if (m == ReadLineMessage.noMoreLines) {
            validateEnd();
            writeBlock(true);
            lastSequenceRead = true;
        }
        firstRead = false;
        return idToFound.size();
    }

    @Override
    protected int lineBeginSize() {
        // can't be decided by any fixed number, so just make it the smallest
        // this is because an infinite number of front spaces is allowed, too bad
        return 1;
    }

    @Override
    protected void lineEmpty() throws SequenceParserException {
        switch (state) {
            case readSequence:
                validateBlock();
                writeBlock(false);
                state = MsfParserState.allDone;
                break;
            case readHeader:
                throw new SequenceParserException("Header line was expected but empty line found.");
        }
    }

    @Override
    protected int lineBegin(String value) throws SequenceParserException {
        switch (state) {
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
            throw new SequenceParserException("Msf usually contains only 50 sequence-symbols per line. This line  number " + lineNumber + " is now over " + MAXIMUM_LINE_LENGTH + " characters long and is thus beyond autocorrection.");
        }
    }

    @Override
    protected void lineEnd(int ident, String value) throws SequenceParserException {
        lineTmp += value;
        switch (state) {
            case readHeader:
                lineTmp = lineTmp.trim();
                type = MsfFileType.getType(lineTmp);
                if (type == MsfFileType.Unkown) {
                    throw new SequenceParserException("File does not start with a valid MSF token.");
                }
                if(type == MsfFileType.RichAA && !aa) {
                    throw new SequenceParserException("File is specified containing AA but NA is needed.");
                } else if(type == MsfFileType.RichNA && aa) {
                    throw new SequenceParserException("File is specified containing NA but AA is needed.");
                }
                write(lineTmp);
                writeNewLine();
                writeNewLine();
                state = MsfParserState.seekNames;
                break;
            case seekNames:
                lineTmp = lineTmp.trim();
                if (!lineTmp.startsWith("Name:")) {
                    write(lineTmp);
                    writeNewLine();
                    return;
                }
                // first name found!
                state = MsfParserState.readNames;
            case readNames:
                lineTmp = lineTmp.trim();
                if (lineTmp.equals("//")) {
                    state = MsfParserState.seekFirstSequence;
                    write("//");
                    writeNewLine();
                    return;
                }
                String[] split = lineTmp.split("\\s+");

                if (!split[0].equals("Name:")) {
                    throw new SequenceParserException("Sequence definition starting with \"Name:\" eexpected but found somethign else on line " + lineNumber + ".");
                }

                if (split.length < 4) {
                    throw new SequenceParserException("Sequence definition does not contain all needed values (\"Name:\" and \"Len:\" on line " + lineNumber + ".");
                }
                // add the id
                addId(split[1]);

                // search the Len:
                for (int i = 2; i < split.length - 1; i++) {
                    if (split[i].equals("Len:")) {
                        try {
                            int newExpectedLength = Integer.parseInt(split[i + 1]);
                            if(expectedLength!=-1 && newExpectedLength!=expectedLength){
                                 throw new SequenceParserException("Sequence definitions contain different values in the \"Len:\" token on line " + lineNumber + ".");
                            }
                            expectedLength = newExpectedLength;
                            return;
                        } catch (NumberFormatException e) {
                            throw new SequenceParserException("Sequence definition contain the \"Len:\" token but content was not parseable as integer on line " + lineNumber + ".");
                        }

                    }
                }
                throw new SequenceParserException("Sequence definition does not contain all needed values (\"Name:\" and \"Len:\" on line " + lineNumber + ".");
            case seekFirstSequence:
                // test if this is just a line containing numbers
                Matcher m = NUMBER_LINE.matcher(lineTmp);
                if (m.matches()) {
                    //just ignore those lines
                    return;
                }
                state = MsfParserState.readSequence;
            case readSequence:
                split = lineTmp.trim().split("\\s+", 2);
                if (split.length < 2) {
                    throw new SequenceParserException("Sequence line was expected but something else was found " + lineNumber + ".");
                }
                addToId(split[0], split[1]);
                break;
        }
    }

    private void addId(String id) throws SequenceParserException {
        if (idToFound.containsKey(id)) {
            throw new SequenceParserException("Same ID multiple times in one block on line " + lineNumber + ".");
        }
        idToFound.put(id, false);
        idToSequenceTmp.put(id, "");
        idToLength.put(id, 0);
    }

    private void addToId(String id, String sequence) throws SequenceParserException {

        Matcher m = IGNORED_IN_SEQUENCE.matcher(sequence);
        sequence = m.replaceAll("");

        m = GAPS.matcher(sequence);
        sequence = m.replaceAll("-");
        
        if (!SequenceValidator.validate(patternType, sequence)) {
            throw new SequenceParserException("Data was erroneous, did not correctly validate as " + patternType.name() + " on line " + lineNumber + ".");
        }

        if (!idToFound.containsKey(id)) {
            throw new SequenceParserException("ID found that was not introduced in first block on line " + lineNumber + ".");
        }
        if (idToFound.get(id)) {
            throw new SequenceParserException("Same ID multiple times in one block on line " + lineNumber + ".");
        }
        idToFound.put(id, true);
        idToSequenceTmp.put(id, idToSequenceTmp.get(id) + sequence);
        idToLength.put(id, idToLength.get(id) + sequence.length());

    }

    private void writeBlock(boolean finalize) throws SequenceParserException {
        // first time we write a block?
        if (firstRead) {
            //Initialize idention data
            int frontSpaces = 0;
            for (String key : idToFound.keySet()) {
                if (key.length() > frontSpaces) {
                    frontSpaces = key.length();
                }
            }
            frontSpaces += 2;

            idToWrite = new HashMap<String, String>();

            for (String id : idToFound.keySet()) {
                StringBuilder tmp = new StringBuilder();
                tmp.append(id);
                for (int i = 0; i < frontSpaces - id.length(); i++) {
                    tmp.append(" ");
                }
                idToWrite.put(id, tmp.toString());
            }
            StringBuilder tmp = new StringBuilder();
            for (int i = 0; i < frontSpaces; i++) {
                tmp.append(" ");
            }
            conservationIndent = tmp.toString();
        }

        while (true) {
            // write sequences, TreeMap keeps them in order
            for (Map.Entry<String, String> entry : idToSequenceTmp.entrySet()) {
                if (entry.getValue().length() < SEQUENCE_WIDTH) {
                    if (finalize) {
                        finalizeFile();
                    }
                    return;
                }
                // write id
                write(idToWrite.get(entry.getKey()));
                
                // add spaces
                String line = entry.getValue().substring(0,SEQUENCE_WIDTH);
                Matcher m = SPACE_INSERT.matcher(line);
                line = m.replaceAll("$0 ");
                
                write(line);

                writeNewLine();

                entry.setValue(entry.getValue().substring(SEQUENCE_WIDTH));
            }
            // write conservation
            writeNewLine();
        }
    }

    private void finalizeFile() throws SequenceParserException {
        for (Map.Entry<String, String> entry : idToSequenceTmp.entrySet()) {
            write(idToWrite.get(entry.getKey()));
             String line = entry.getValue();
                Matcher m = SPACE_INSERT.matcher(line);
                line = m.replaceAll("$0 ");
                
            write(line);
            writeNewLine();
            writeNewLine();
            entry.setValue("");
        }
        
    }

    private void validateBlock() throws SequenceParserException {
        // test if ids where not found
        if (idToFound.containsValue(false)) {
            throw new SequenceParserException("One or more id is missing in a block.");
        }
        // reset found value
        for (Map.Entry<String, Boolean> entry : idToFound.entrySet()) {
            entry.setValue(false);
        }

        int len = -1;
        // check that all sequences have the same length
        for (int slen : idToLength.values()) {
            if (len == -1) {
                len = slen;
            }
            if (len != slen) {
                throw new SequenceParserException("The block before line " + lineNumber + " contains sequences of different length.");
            }
        }
    }
    
    private void validateEnd() throws SequenceParserException {
        // test if ids where not found
        for (int slen : idToLength.values()) {
            if (expectedLength != slen) {
                throw new SequenceParserException("Expected length of sequences and actually found length sequences is different.");
            }
        }
    }
}
