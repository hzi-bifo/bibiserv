/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 */
package de.unibi.cebitec.bibiserv.utils;

import de.unibi.cebitec.bibiserv.util.convert.ConversionException;
import de.unibi.cebitec.bibiserv.util.convert.Converter;
import de.unibi.cebitec.bibiserv.util.convert.SequenceConverter;
import de.unibi.cebitec.bibiserv.util.validate.SequenceValidator;
import de.unibi.techfak.bibiserv.exception.BiBiToolsException;
import de.unibi.techfak.bibiserv.util.convert.factory.ConverterFactory;
import de.unibi.techfak.bibiserv.util.convert.factory.ConverterFactoryException;
import de.unibi.techfak.bibiserv.util.ontoaccess.TypeOntoQuestioner;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiEdge;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.OntoRepresentation;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl.OntoAccessException;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import de.unibi.cebitec.bibiserv.util.streamconvert.StreamConverter;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl.OntoRepresentationImplementation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * This class contains a method to convert a representation of one type to
 * another type if a converterchain exists. All data is cached on first use for
 * faster access later.
 *
 * @author Thomas Gatter - tgatter(at)cebitec.uni-bielefeld.de
 */
public class UniversalConverter {

    /**
     * A HashMap for caching a converter chain for known key-pairs. Order is
     * From, To, Chain.
     */
    private static Map<String, Map<String, List<BiBiEdge>>> chainCache = new HashMap<>();
    /**
     * A HashMap for caching a converter chain for known key-pairs for
     * Streaming. Order is From, To, Chain.
     */
    private static Map<String, Map<String, List<BiBiEdge>>> chainStreamConverterCache  = new HashMap<>();
    /**
     * A HashMap for caching a stream converter chain for known key-pairs. Order
     * is From, To, Chain.
     */
    private static Map<String, Map<String, List<String>>> streamChainCache = new HashMap<>();

    /**
     * Converts the given object from "from" to "to".
     *
     * @param object Object to convert. Type is given by from.
     * @param from Representation to convert from.
     * @param to Representation to convert object into.
     * @return the converter object now of type to
     */
    public static Object convert(Object object, OntoRepresentation from, OntoRepresentation to)
            throws BiBiToolsException, ConversionException {

        // test trivial case from = to, just return object then
        if (from == to) {
            return object;
        }

        // are these types implemented?
        if (from.getImplementationType() == null) {
            throw new BiBiToolsException(703, "Conversion Exception: No ImplementationsType for type " + from.getKey()
                    + " available.");
        }
        if (to.getImplementationType() == null) {
            throw new BiBiToolsException(703, "Conversion Exception: No ImplementationsType for type " + to.getKey()
                    + " available.");
        }

        // initialize keychain as empty
        List<BiBiEdge> typechain = null;

        // first check if this is already cached
        if (chainCache.containsKey(from.getKey())) {
            Map<String, List<BiBiEdge>> innerMap = chainCache.get(from.getKey());
            if (innerMap.containsKey(to.getKey())) {
                typechain = innerMap.get(to.getKey());
            }
        }
        // if not already cached, create chain from scratch
        if (typechain == null) {
            // get new chain from ontoaccess
            try {
                typechain = TypeOntoQuestioner.getConverterChainFromKeyToKey(from.getKey(), to.
                        getKey());
            } catch (OntoAccessException ex) {
                throw new BiBiToolsException(703, "Could not find converter for " + from.getKey()
                        + " to " + to.getKey());
            }

            // insert chain into caching data
            Map<String, List<BiBiEdge>> innerMap;
            if (chainCache.containsKey(from.getKey())) {
                innerMap = chainCache.get(from.getKey());
            } else {
                innerMap = new HashMap<String, List<BiBiEdge>>();
                chainCache.put(from.getKey(), innerMap);
            }
            innerMap.put(to.getKey(), typechain);
        }

        // now try to use the chain to execute the conversion
        ListIterator<BiBiEdge> li = typechain.listIterator();
        while (li.hasNext()) {

            //fetch keys from next entry in converter path list
            BiBiEdge edge = li.next();
            String key_a = edge.getStart();
            String key_b = edge.getEnd();

            // get the next converter
            Converter converter;
            try {
                converter = ConverterFactory.makeConverter(key_a, key_b);
            } catch (ConverterFactoryException ex) {
                throw new BiBiToolsException(703, "Could not find converter for step " + key_a
                        + " to " + key_b + " in converter-chain " + from.getKey()
                        + " to " + to.getKey());
            }

            if (converter instanceof SequenceConverter) {
                switch (from.getContent()) {
                    case AA:
                        ((SequenceConverter) converter).setContent(SequenceValidator.CONTENT.AA);
                        break;
                    case NA:
                        ((SequenceConverter) converter).setContent(SequenceValidator.CONTENT.NA);
                        break;
                    case DNA:
                        ((SequenceConverter) converter).setContent(SequenceValidator.CONTENT.DNA);
                        break;
                    case RNA:
                        ((SequenceConverter) converter).setContent(SequenceValidator.CONTENT.RNA);
                        break;
                    case UNKNOWN:
                        break;
                }
            }

            // apply the converter
            object = converter.convert(object);
        }
        return object;
    }

    /**
     * Returns the edges for scriptgeneration for a streamconverter chains.
     *
     * @param from Representation to start from.
     * @param to Representation to end in
     * @return edges for scriptgeneration
     * @throws BiBiToolsException
     */
    public static List<String> getStreamConverterOrder(OntoRepresentation from, OntoRepresentation to)
            throws BiBiToolsException {

        // test trivial case from = to, just return empty chain
        if (from == to) {
            return new ArrayList<String>();
        }

        // are these types implemented?
        if (from.getImplementationType() == null) {
            throw new BiBiToolsException(703, "Conversion Exception: No ImplementationsType for type " + from.getKey()
                    + " available.");
        }
        if (to.getImplementationType() == null) {
            throw new BiBiToolsException(703, "Conversion Exception: No ImplementationsType for type " + to.getKey()
                    + " available.");
        }

        // first check if this is already cached
        if (streamChainCache.containsKey(from.getKey())) {
            Map<String, List<String>> innerMap = streamChainCache.get(from.getKey());
            if (innerMap.containsKey(to.getKey())) {
                return innerMap.get(to.getKey());
            }
        }
        // if not already cached, create chain from scratch
        List<String> typechain = new ArrayList<String>();
        List<BiBiEdge> bibichain = null;
        // get new chain from ontoaccess
        try {
            bibichain = TypeOntoQuestioner.getStreamConverterChainFromKeyToKey(from.getKey(), to.getKey());
        } catch (OntoAccessException ex) {
            throw new BiBiToolsException(703, "Could not find converter for " + from.getKey()
                    + " to " + to.getKey());
        }

        // now get the implementations of the chain and add to typechain
        ListIterator<BiBiEdge> li = bibichain.listIterator();
        while (li.hasNext()) {

            //fetch keys from next entry in converter path list
            BiBiEdge edge = li.next();
            String classImpl;

            try {
                classImpl = TypeOntoQuestioner.getStreamConverterClassnameFor(edge.getStart(), edge.getEnd());
            } catch (OntoAccessException ex) {
                throw new BiBiToolsException(703, "Could not find stream converter for step " + edge.getStart()
                        + " to " + edge.getEnd() + " in converter-chain " + from.getKey()
                        + " to " + to.getKey());
            }
            // apply the converter
            typechain.add(classImpl);
        }

        // insert chain into caching data
        Map<String, List<String>> innerMap;
        if (streamChainCache.containsKey(from.getKey())) {
            innerMap = streamChainCache.get(from.getKey());
        } else {
            innerMap = new HashMap<String, List<String>>();
            streamChainCache.put(from.getKey(), innerMap);
        }
        innerMap.put(to.getKey(), typechain);

        return typechain;

    }

    //######################################################
    //#                 Java Stream converting             #
    //######################################################
    /**
     * Converts the given object from "from" to "to".
     *
     * @param object Object to convert. Type is given by from.
     * @param from Representation to convert from.
     * @param to Representation to convert object into.
     * @return the converter object now of type to
     */
    public static InputStream streamConvert(InputStream input, OntoRepresentation from, OntoRepresentation to) throws BiBiToolsException, ConversionException {

        // test trivial case from = to, just return object then
        if (from == to) {
            return input;
        }

        // are these types implemented?
        if (from.getImplementationType() == null) {
            throw new BiBiToolsException(703, "Conversion Exception: No ImplementationsType for type " + from.getKey()
                    + " available.");
        }
        if (to.getImplementationType() == null) {
            throw new BiBiToolsException(703, "Conversion Exception: No ImplementationsType for type " + to.getKey()
                    + " available.");
        }

        // initialize keychain as empty
        List<BiBiEdge> typechain = null;

        // first check if this is already cached
        if (chainStreamConverterCache.containsKey(from.getKey())) {
            Map<String, List<BiBiEdge>> innerMap = chainStreamConverterCache.get(from.getKey());
            if (innerMap.containsKey(to.getKey())) {
                typechain = innerMap.get(to.getKey());
            }
        }
        // if not already cached, create chain from scratch
        if (typechain == null) {
            // get new chain from ontoaccess
            try {
                typechain = TypeOntoQuestioner.getStreamConverterChainFromKeyToKey(from.getKey(), to.
                        getKey());
            } catch (OntoAccessException ex) {
                throw new BiBiToolsException(703, "Could not find converter for " + from.getKey()
                        + " to " + to.getKey());
            }

            // insert chain into caching data
            Map<String, List<BiBiEdge>> innerMap;
            if (chainStreamConverterCache.containsKey(from.getKey())) {
                innerMap = chainStreamConverterCache.get(from.getKey());
            } else {
                innerMap = new HashMap<String, List<BiBiEdge>>();
                chainStreamConverterCache.put(from.getKey(), innerMap);
            }
            innerMap.put(to.getKey(), typechain);
        }

        // now try to use the chain to execute the conversion
        ListIterator<BiBiEdge> li = typechain.listIterator();
        while (li.hasNext()) {

            //fetch keys from next entry in converter path list
            BiBiEdge edge = li.next();
            String key_a = edge.getStart();
            String key_b = edge.getEnd();

            // get the next converter
            input = streamConvertStep(from, to, key_a, key_b, input);
        }
        return input;
    }

    private static PipedInputStream streamConvertStep(OntoRepresentation from, OntoRepresentation to, final String key_a, final String key_b, final InputStream inStream) throws BiBiToolsException {

        final PipedInputStream nextInput = new PipedInputStream();
        final PipedOutputStream inPipe;
        try {
            // open pie end to write into inputstream
            inPipe = new PipedOutputStream(nextInput);
        } catch (IOException ex) {
            throw new BiBiToolsException(703, "Conversion Exception: Could not set up converterstreams.");
        }

        // create writer from new piped output
        final Writer out = new BufferedWriter(new OutputStreamWriter(inPipe));

        final StreamConverter converter;
        try {
            converter = (StreamConverter) ConverterFactory.makeConverter(key_a, key_b);
        } catch (ConverterFactoryException ex) {
            throw new BiBiToolsException(703, "Could not find converter for step " + key_a
                    + " to " + key_b + " in converter-chain " + from.getKey()
                    + " to " + to.getKey());
        }

        switch (from.getContent()) {
            case AA:
                ((SequenceConverter) converter).setContent(SequenceValidator.CONTENT.AA);
                break;
            case NA:
                ((SequenceConverter) converter).setContent(SequenceValidator.CONTENT.NA);
                break;
            case DNA:
                ((SequenceConverter) converter).setContent(SequenceValidator.CONTENT.DNA);
                break;
            case RNA:
                ((SequenceConverter) converter).setContent(SequenceValidator.CONTENT.RNA);
                break;
            case UNKNOWN:
                break;
        }

        final Reader in = new BufferedReader(new InputStreamReader(inStream));

        Thread convert = new Thread(
                new Runnable() {
            public void run() {
                try {
                    converter.convert(in, out);
                    inPipe.close();
                } catch (IOException | ConversionException ex) {
                    System.out.println("Con Error: " + key_a + " to " + key_b);
                    try {
                        nextInput.close();
                        inPipe.close();
                    } catch (IOException ex1) {
                        System.out.println("Really?");
                    }
                }
            }
        });
        convert.setUncaughtExceptionHandler(null);
        convert.start();

        return nextInput;
    }

    public static void main(String[] args) throws URISyntaxException, OntoAccessException, BiBiToolsException, ConversionException, IOException {

        OntoRepresentationImplementation from = new OntoRepresentationImplementation("FastQAll_RNA");
        OntoRepresentationImplementation to = new OntoRepresentationImplementation("NBRF_RNA");
        try {
            InputStream in = new FileInputStream("fastq_illumina_valid.fastq");

            InputStream out = streamConvert(in, from, to);

            int ch;
            StringBuilder sb = new StringBuilder();
            while ((ch = out.read()) != -1) {
                sb.append((char) ch);
            }

            System.out.println(sb);
            //System.out.println(sb.length());

        } catch (FileNotFoundException ex) {
        }

    }

}
