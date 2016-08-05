/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.web.beans.runinthecloud.jsonInstancesUpdater;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 *
 * @author Johannes Steiner <jsteiner@cebitec.uni-bielefeld.de>
 */
public class JsonFormatReader extends Reader {

    /**
     * State of the Json-Reading-Automaton. Whereby 'LISTANDEND' is the
     * endstate.
     */
    public enum State {

        START, KEY, VALUE, LISTANDEND, LISTO
    }

    /**
     * The Streamreader we are grabbing from.
     */
    private InputStreamReader isr = null;
    /**
     * State of the Automaton.
     */
    private State state;
    /**
     * Completely read and modified String.
     */
    private String finalString;
    /**
     * Buffer for the Key.
     */
    private String bufferKey;
    /**
     * Buffer for the Value.
     */
    private String bufferValue;
    /**
     * Finished indicator.
     */
    private boolean isFinished = false;

    /**
     * Std c'tor.
     *
     * @param isr
     */
    public JsonFormatReader(InputStreamReader isr) {
        this.isr = isr;
        this.state = State.START;
        finalString = "";
        bufferKey = "";
        bufferValue = "";
    }

    /**
     * Automaton Parsing from isr.
     *
     * @return 0 -> success; 1 -> failure
     * @throws java.io.IOException
     */
    @Override
    public int read() throws IOException {
        int i = 0;
        try {
            char c;
            while (!isFinished) {
                switch (c = (char) isr.read()) {
                    case '{':
                        switch (state) {
                            case START:
                                state = State.KEY;
                                writeKeystone('{');
                                break;
                            case LISTANDEND:
                                state = State.KEY;
                                writeKeystone('{');
                                break;
                            case VALUE:
                                state = State.KEY;
                                writeKeystone('{');
                                break;
                            default:
                                break;
                        }
                        break;
                    case ':':
                        if (state == State.KEY) {
                            state = State.VALUE;
                            // write bufferkey
                            writeBuffer(bufferKey);
                            writeKeystone(':');
                            bufferKey = "";
                        }
                        break;
                    case '}':
                        switch (state) {
                            case KEY:
                                state = State.LISTANDEND;
                                writeKeystone('}');
                                break;
                            case VALUE:
                                state = State.KEY;
                                // write buffervalue
                                writeBuffer(bufferValue);
                                writeKeystone('}');
                                bufferValue = "";
                                break;
                            default:
                                // failure state
                                break;
                        }
                        break;
                    case ',':
                        switch (state) {
                            case KEY:
                                state = State.LISTANDEND;
                                writeKeystone(',');
                                break;
                            case VALUE:
                                state = State.KEY;
                                // write buffervalue
                                writeBuffer(bufferValue);
                                writeKeystone(',');
                                bufferValue = "";
                                break;
                            case LISTO:
                                state = State.LISTANDEND;
                                writeBuffer(bufferValue);
                                writeKeystone(',');
                                bufferValue = "";
                                break;
                            default:
                                // failure state
                                break;
                        }
                        break;
                    case '[':
                        state = State.LISTANDEND;
                        writeKeystone('[');
                        break;
                    case ']':
                        state = State.VALUE;
                        writeBuffer(bufferValue);
                        writeKeystone(']');
                        bufferValue = "";
                        break;
                    default:
                        // filling the buffers
                        switch (state) {
                            case KEY:
                                bufferKey += Character.toLowerCase(c);
                                break;
                            case VALUE:
                                bufferValue += Character.toLowerCase(c);
                                break;
                            case LISTO:
                                bufferValue += Character.toLowerCase(c);
                                break;
                            case LISTANDEND:
                                if (c == ')') {
                                    isFinished = true;
                                    break;
                                }
                                bufferValue += Character.toLowerCase(c);
                                state = State.LISTO;
                                break;
                            default:
                                // failure state
                                break;
                        }
                        break;
                }
                i++;
            }
        } catch (Exception e) {
            StackTraceElement ste = e.getStackTrace()[0];
            System.out.println("Error at: "
                    + ste.getClassName()
                    + ste.getMethodName()
                    + ":" + ste.getLineNumber());
            return 1;
        }
        return 0;
    }

    /**
     * Write content of Buffer to finalString.
     *
     * @param buffer - buffer to push on finalString
     */
    public final void writeBuffer(String buffer) {
        if (!buffer.isEmpty()) {
            finalString += "\"" + buffer.replaceAll("\"", "").trim() + "\"";
        }
    }

    /**
     * Write a simple keystone to the finalString.
     * @param keystone - Keystone (char) to set
     */
    public final void writeKeystone(char keystone) {
        finalString += keystone;
    }

    @Override
    @Deprecated
    public int read(char[] chars, int i, int i1) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() throws IOException {
        isr.close();
    }

    public String getFinalString() {
        return finalString;
    }
}
