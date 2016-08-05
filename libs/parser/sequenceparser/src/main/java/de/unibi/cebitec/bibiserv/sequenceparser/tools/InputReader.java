/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.cebitec.bibiserv.sequenceparser.tools;


import de.unibi.cebitec.bibiserv.sequenceparser.Parser;
import de.unibi.cebitec.bibiserv.sequenceparser.ParserFactory;
import de.unibi.cebitec.bibiserv.sequenceparser.enums.ParserInputFormat;
import de.unibi.cebitec.bibiserv.sequenceparser.parser.exception.ParserException;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.exception.ParserToolException;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.wrapper.SequenceCollection;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.wrapper.SequenceCollectionImpl;
import de.unibi.cebitec.bibiserv.sequenceparser.tools.wrapper.SequenceTypeInterface;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.BioseqRecord;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.Readseq;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.SeqFileInfo;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.Testseq;
import java.io.BufferedReader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * Class to read biological information from many different wellknown formats to
 * to datatypes which can be used by the BioDOM classes
 * @author rmadsack, mrumming
 */
public class InputReader {


    /**
     * Constructor, returns a new instance of the InputReader
     */
    public InputReader() {
    }


    /**
     * Reads sequence information from a String representation of a IG file
     * @param ig sequence information in IG format as String
     * @param outputType concrete type to return
     * @return Collection of sequence information, elements are from type: outputType
     * @throws ParserToolException input is not valide
     * @throws ParserException input can not be read with the GDE based parser factory
     */
    public SequenceCollection readSequenceIG(final String ig, SequenceTypeInterface outputType) throws ParserException, ParserToolException {
        return readSequenceIG(new StringReader(ig), outputType);
    }

    /**
     * Reads sequence information from an IG file, using a reader
     * @param ig Reader, reading an IG file
     * @param outputType concrete type to return
     * @return Collection of sequence information, elements are from type: outputType
     * @throws IOException inputdata cannot be read
     * @throws ParserToolException input is not valide
     * @throws ParserException input can not be read with the GDE based parser factory
     */
    public SequenceCollection readSequenceIG(final Reader ig, SequenceTypeInterface outputType) throws ParserException, ParserToolException {

        SequenceCollection sequenceCollection = new SequenceCollectionImpl();
        SequenceTypeInterface newSequenceType = outputType;

        LinkedList<BioseqRecord> sequenceList = this.useParserFactory(ig, ParserInputFormat.IG);

        String id;
        String description;

        for (BioseqRecord bioseq : sequenceList) {
            //clear description
            description = "";
            //get ID
            if (bioseq.getID() != null) {
                id = bioseq.getID();
                if (!sequenceCollection.idIsUnique(id)) {
                    //TODO:
                    //sequence has no unique ID, create it or throw exception??
                    //id = id + "_" + data.generateId();
                    throw new ParserToolException("A sequence with ID: " + id + " already exists, unique IDs are neccesary!");
                }
            } else {
                //sequence has NO Id
                //id = data.generateId();
                throw new ParserToolException("Sequence has no ID!");
            }
            //get description, if given
            if (bioseq.getTitle() != null && !id.equals(bioseq.getTitle())) {
                String[] idDesc = bioseq.getTitle().split(" ");
                for (int i = 1; i < idDesc.length; i++) {
                    description += idDesc[i] + " ";
                }
                description = description.trim();
            } else {
                //no description given, set to null
                description = null;
            }

            //fill sequenceTypeImpl
            newSequenceType = getSequenceTypeInstance(newSequenceType, id, description, bioseq.getseq().toString().toUpperCase());
            //Add sequenceTypeImpl to sequenceCollection
            sequenceCollection.addSequence(newSequenceType);
        }
        return sequenceCollection;
    }

    /**
     * Reads sequence information from a String representation of a Dialign file
     * @param dialign sequence information in Dialign format as String
     * @param outputType concrete type to return
     * @return Collection of sequence information, elements are from type: outputType
     * @throws ParserToolException input is not valide
     * @throws ParserException input can not be read with the GDE based parser factory
     */
    public SequenceCollection readSequenceDialign(final String dialign, SequenceTypeInterface outputType) throws ParserException, ParserToolException {
        return readSequenceDialign(new StringReader(dialign), outputType);
    }

    /**
     * Reads sequence information from a Dialign file, using a reader
     * @param dialign Reader, reading a Dialign file
     * @param outputType concrete type to return
     * @return Collection of sequence information, elements are from type: outputType
     * @throws IOException inputdata cannot be read
     * @throws ParserToolException input is not valide
     * @throws ParserException input can not be read with the GDE based parser factory
     */
    public SequenceCollection readSequenceDialign(final Reader dialign, SequenceTypeInterface outputType) throws ParserException, ParserToolException {

        SequenceCollection sequenceCollection = new SequenceCollectionImpl();
        SequenceTypeInterface newSequenceType = outputType;

        LinkedList<BioseqRecord> sequenceList = this.useParserFactory(dialign, ParserInputFormat.DIALIGN);

        String id;
        String description;

        for (BioseqRecord bioseq : sequenceList) {
            //clear description
            description = "";
            //get ID
            if (bioseq.getID() != null) {
                id = bioseq.getID();
                if (!sequenceCollection.idIsUnique(id)) {
                    //TODO:
                    //sequence has no unique ID, create it or throw exception??
                    //id = id + "_" + data.generateId();
                    throw new ParserToolException("A sequence with ID: " + id + " already exists, unique IDs are neccesary!");
                }
            } else {
                //sequence has NO Id
                //id = data.generateId();
                throw new ParserToolException("Sequence has no ID!");
            }
            //get description, if given
            if (bioseq.getTitle() != null && !id.equals(bioseq.getTitle())) {
                String[] idDesc = bioseq.getTitle().split(" ");
                for (int i = 1; i < idDesc.length; i++) {
                    description += idDesc[i] + " ";
                }
                description = description.trim();
            } else {
                //no description given, set to null
                description = null;
            }

            //fill sequenceTypeImpl
            newSequenceType = getSequenceTypeInstance(newSequenceType, id, description, bioseq.getseq().toString().toUpperCase());
            //Add sequenceTypeImpl to sequenceCollection
            sequenceCollection.addSequence(newSequenceType);
        }
        return sequenceCollection;
    }

    /**
     * Reads sequence information from a String representation of a Phylip file
     * @param phylip sequence information in Phylip format as String
     * @param outputType concrete type to return
     * @return Collection of sequence information, elements are from type: outputType
     * @throws ParserException input can not be read with the GDE based parser factory
     * @throws ParserToolException input is not valide
     */
    public SequenceCollection readSequencePhylip(final String phylip, SequenceTypeInterface outputType) throws ParserToolException, ParserException {
        return readSequencePhylip(new StringReader(phylip), outputType);
    }

    /**
     * Reads sequence information from a Phylip file, using a reader
     * @param phylip Reader, reading a Phylip file
     * @param outputType concrete type to return
     * @return Collection of sequence information, elements are from type: outputType
     * @throws ParserException input can not be read with the GDE based parser factory
     * @throws ParserToolException input is not valide
     */
    public SequenceCollection readSequencePhylip(final Reader phylip, SequenceTypeInterface outputType) throws ParserToolException, ParserException {

        SequenceCollection sequenceCollection = new SequenceCollectionImpl();
        SequenceTypeInterface newSequenceType = outputType;

        LinkedList<BioseqRecord> sequenceList = this.useParserFactory(phylip, ParserInputFormat.PHYLIP);

        String id;
        String description;

        for (BioseqRecord bioseq : sequenceList) {
            //clear description
            description = "";
            //get ID
            if (bioseq.getID() != null) {
                id = bioseq.getID();
                if (!sequenceCollection.idIsUnique(id)) {
                    //TODO:
                    //sequence has no unique ID, create it or throw exception??
                    //id = id + "_" + data.generateId();
                    throw new ParserToolException("A sequence with ID: " + id + " already exists, unique IDs are neccesary!");
                }
            } else {
                //sequence has NO Id
                //id = data.generateId();
                throw new ParserToolException("Sequence has no ID!");
            }
            //get description, if given
            if (bioseq.getTitle() != null && !id.equals(bioseq.getTitle())) {
                String[] idDesc = bioseq.getTitle().split(" ");
                for (int i = 1; i < idDesc.length; i++) {
                    description += idDesc[i] + " ";
                }
                description = description.trim();
            } else {
                //no description given, set to null
                description = null;
            }

            //fill sequenceTypeImpl
            newSequenceType = getSequenceTypeInstance(newSequenceType, id, description, bioseq.getseq().toString().toUpperCase());
            //Add sequenceTypeImpl to sequenceCollection
            sequenceCollection.addSequence(newSequenceType);
        }
        return sequenceCollection;
    }

    /**
     * Reads sequence information from a String representation of a GDE file
     * @param gde sequence information in GDE plain format as String
     * @param outputType concrete type to return
     * @return Collection of sequence information, elements are from type: outputType
     * @throws ParserException input can not be read with the GDE based parser factory
     * @throws ParserToolException input is not valide
     */
    public SequenceCollection readSequenceGCG9_RSF(final String gcg9_rsf, SequenceTypeInterface outputType) throws ParserToolException, ParserException {
        return readSequenceGCG9_RSF(new StringReader(gcg9_rsf), outputType);
    }

    /**
     * Reads multiple sequence information from a GCG9/RSF file, using a reader
     * @param gcg9_rsf reader, reading a GCG9/RSF rich file
     * @param outputType concrete type to return
     * @return SequenceCollection, elements are from type: outputType
     * @throws ParserToolException
     * @throws ParserException input can not be read with the GDE based parser factory
     */
    public SequenceCollection readSequenceGCG9_RSF(final Reader gcg9_rsf, SequenceTypeInterface outputType) throws ParserToolException, ParserException {

        SequenceCollection sequenceCollection = new SequenceCollectionImpl();
        SequenceTypeInterface newSequenceType = outputType;

        LinkedList<BioseqRecord> sequenceList = this.useParserFactory(gcg9_rsf, ParserInputFormat.RSF);

        String id;
        String description;

        for (BioseqRecord bioseq : sequenceList) {
            //clear description
            description = "";
            //get ID
            if (bioseq.getID() != null) {
                id = bioseq.getID();
                if (!sequenceCollection.idIsUnique(id)) {
                    //TODO:
                    //sequence has no unique ID, create it or throw exception??
                    //id = id + "_" + data.generateId();
                    throw new ParserToolException("A sequence with ID: " + id + " already exists, unique IDs are neccesary!");
                }
            } else {
                //sequence has NO Id
                //id = data.generateId();
                throw new ParserToolException("Sequence has no ID!");
            }
            //get description, if given
            if (bioseq.getTitle() != null && !id.equals(bioseq.getTitle())) {
                description = bioseq.getTitle().toString().trim();
            } else {
                //no description given, set to null
            
                description = null;
            }

            //fill sequenceTypeImpl
            newSequenceType = getSequenceTypeInstance(newSequenceType, id, description, bioseq.getseq().toString().toUpperCase());
            //Add sequenceTypeImpl to sequenceCollection
            sequenceCollection.addSequence(newSequenceType);
        }
        return sequenceCollection;

    }

    /**
     * Reads sequence information from a String representation of a GDE file
     * @param gde sequence information in GDE plain format as String
     * @param outputType concrete type to return
     * @return Collection of sequence information, elements are from type: outputType
     * @throws ParserException input can not be read with the GDE based parser factory
     * @throws ParserToolException input is not valide
     */
    public SequenceCollection readSequenceGDE(final String gde, SequenceTypeInterface outputType) throws ParserException, ParserToolException {
        return readSequenceGDE(new StringReader(gde), outputType);

    }

    /**
     * Reads multiple sequence information from a GDE file, using a reader
     * @param gde reader, reading a GDE rich file
     * @param outputType concrete type to return
     * @return SequenceCollection, elements are from type: outputType
     * @throws ParserToolException
     * @throws ParserException input can not be read with the GDE based parser factory
     */
    public SequenceCollection readSequenceGDE(final Reader gde, SequenceTypeInterface outputType) throws ParserException, ParserToolException {

        SequenceCollection sequenceCollection = new SequenceCollectionImpl();
        SequenceTypeInterface newSequenceType = outputType;

        LinkedList<BioseqRecord> sequenceList = this.useParserFactory(gde, ParserInputFormat.GDE);

        String id;
        String description;

        for (BioseqRecord bioseq : sequenceList) {
            //clear description
            description = "";
            //get ID
            if (bioseq.getID() != null) {
                id = bioseq.getID();
                if (!sequenceCollection.idIsUnique(id)) {
                    //TODO:
                    //sequence has no unique ID, create it or throw exception??
                    //id = id + "_" + data.generateId();
                    throw new ParserToolException("A sequence with ID: " + id + " already exists, unique IDs are neccesary!");
                }
            } else {
                //sequence has NO Id
                //id = data.generateId();
                throw new ParserToolException("Sequence has no ID!");
            }
            //get description, if given
            if (bioseq.getdoc().getDocumentText().trim().contains("\n")) {
                String sTemp = bioseq.getdoc().getDocumentText().split("\n")[1].trim();
                description = sTemp.substring(sTemp.indexOf(" ") + 1, sTemp.length());
            } else {
                //no description given, set to null
             
                description = null;
            }

            //fill sequenceTypeImpl
            newSequenceType = getSequenceTypeInstance(newSequenceType, id, description, bioseq.getseq().toString().toUpperCase());
            //Add sequenceTypeImpl to sequenceCollection
            sequenceCollection.addSequence(newSequenceType);
        }
        return sequenceCollection;

    }

    /**
     * Reads sequence information from a String representation of a GCG/MSF file
     * @param gcgmsf_plain sequence information in GCG/MSF plain format as String
     * @param outputType concrete type to return
     * @return Collection of sequence information, elements are from type: outputType
     * @throws ParserException input can not be read with the GDE based parser factory
     * @throws ParserToolException input is not valide
     */
    public SequenceCollection readSequenceGCG_MSF_Plain(final String gcgmsf_plain, SequenceTypeInterface outputType) throws ParserToolException, ParserException {
        return readSequenceGCG_MSF_Plain(new StringReader(gcgmsf_plain), outputType);

    }

    /**
     * Reads sequence information from a String representation of a GCG/MSF file
     * @param gcgmsf_rich sequence information in GCG/MSF rich format as String
     * @param outputType concrete type to return
     * @return Collection of sequence information, elements are from type: outputType
     * @throws ParserException input can not be read with the GDE based parser factory
     * @throws ParserToolException input is not valide
     */
    public SequenceCollection readSequenceGCG_MSF_Rich(final String gcgmsf_rich, SequenceTypeInterface outputType) throws ParserToolException, ParserException {
        return readSequenceGCG_MSF_Rich(new StringReader(gcgmsf_rich), outputType);

    }

    /**
     * Reads sequence information from a String representation of a GCG/MSF file
     * @param gcgmsf_pileup sequence information in GCG/MSF PileUp format as String
     * @param outputType concrete type to return
     * @return Collection of sequence information, elements are from type: outputType
     * @throws ParserException input can not be read with the GDE based parser factory
     * @throws ParserToolException input is not valide
     */
    public SequenceCollection readSequenceGCG_MSF_PileUp(final String gcgmsf_pileup, SequenceTypeInterface outputType) throws ParserToolException, ParserException {
        return readSequenceGCG_MSF_PileUp(new StringReader(gcgmsf_pileup), outputType);

    }

    /**
     * Reads multiple sequence information from a GCG/MSF file, using a reader
     * @param gcgmsf_rich reader, reading a GCG/MSF rich file
     * @param outputType concrete type to return
     * @return SequenceCollection, elements are from type: outputType
     * @throws ParserToolException
     * @throws ParserException input can not be read with the GDE based parser factory
     */
    public SequenceCollection readSequenceGCG_MSF_Rich(final Reader gcgmsf_rich, SequenceTypeInterface outputType) throws ParserToolException, ParserException {

        SequenceCollection sequenceCollection = new SequenceCollectionImpl();
        SequenceTypeInterface newSequenceType = outputType;

        LinkedList<BioseqRecord> sequenceList = this.useParserFactory(gcgmsf_rich, ParserInputFormat.MSF);




        String id;
        String description;

        for (BioseqRecord bioseq : sequenceList) {
            //clear description
            description = "";
            //get ID
            if (bioseq.getID() != null) {
                id = bioseq.getID();
                if (!sequenceCollection.idIsUnique(id)) {
                    //TODO:
                    //sequence has no unique ID, create it or throw exception??
                    //id = id + "_" + data.generateId();
                    throw new ParserToolException("A sequence with ID: " + id + " already exists, unique IDs are neccesary!");
                }
            } else {
                //sequence has NO Id
                //id = data.generateId();
                throw new ParserToolException("Sequence has no ID!");
            }
            //get description, if given
            if (bioseq.getTitle() != null && !id.equals(bioseq.getTitle())) {
                description = bioseq.getTitle().toString().trim();
            } else {
                //no description given, set to null
           
                description = null;
            }

            //fill sequenceTypeImpl
            newSequenceType = getSequenceTypeInstance(newSequenceType, id, description, bioseq.getseq().toString().toUpperCase());
            //Add sequenceTypeImpl to sequenceCollection
            sequenceCollection.addSequence(newSequenceType);
        }
        return sequenceCollection;

    }

    /**
     * Reads multiple sequence information from a GCG/MSF file, using a reader
     * @param gcgmsf_pileup reader, reading a GCG/MSF PileUp file
     * @param outputType concrete type to return
     * @return SequenceCollection, elements are from type: outputType
     * @throws ParserToolException
     * @throws ParserException input can not be read with the GDE based parser factory
     */
    public SequenceCollection readSequenceGCG_MSF_PileUp(final Reader gcgmsf_pileup, SequenceTypeInterface outputType) throws ParserToolException, ParserException {

        SequenceCollection sequenceCollection = new SequenceCollectionImpl();
        SequenceTypeInterface newSequenceType = outputType;

        LinkedList<BioseqRecord> sequenceList = this.useParserFactory(gcgmsf_pileup, ParserInputFormat.MSF);


        String id;
        String description;

        for (BioseqRecord bioseq : sequenceList) {
            //clear description
            description = "";
            //get ID
            if (bioseq.getID() != null) {
                id = bioseq.getID();
                if (!sequenceCollection.idIsUnique(id)) {
                    //TODO:
                    //sequence has no unique ID, create it or throw exception??
                    //id = id + "_" + data.generateId();
                    throw new ParserToolException("A sequence with ID: " + id + " already exists, unique IDs are neccesary!");
                }
            } else {
                //sequence has NO Id
                //id = data.generateId();
                throw new ParserToolException("Sequence has no ID!");
            }
            //get description, if given
            if (bioseq.getTitle() != null && !id.equals(bioseq.getTitle())) {
                description = bioseq.getTitle().toString().trim();
            } else {
                //no description given, set to null
              
                description = null;
            }

            //fill sequenceTypeImpl
            newSequenceType = getSequenceTypeInstance(newSequenceType, id, description, bioseq.getseq().toString().toUpperCase());
            //Add sequenceTypeImpl to sequenceCollection
            sequenceCollection.addSequence(newSequenceType);
        }
        return sequenceCollection;

    }

    /**
     * Reads multiple sequence information from a GCG/MSF file, using a reader
     * @param gcgmsf_plain reader, reading a GCG/MSF plain file
     * @param outputType concrete type to return
     * @return SequenceCollection, elements are from type: outputType
     * @throws ParserToolException
     * @throws ParserException input can not be read with the GDE based parser factory
     */
    public SequenceCollection readSequenceGCG_MSF_Plain(final Reader gcgmsf_plain, SequenceTypeInterface outputType) throws ParserToolException, ParserException {

        SequenceCollection sequenceCollection = new SequenceCollectionImpl();
        SequenceTypeInterface newSequenceType = outputType;

        LinkedList<BioseqRecord> sequenceList = this.useParserFactory(gcgmsf_plain, ParserInputFormat.MSF);


        String id;
        String description;

        for (BioseqRecord bioseq : sequenceList) {
            //clear description
            description = "";
            //get ID
            if (bioseq.getID() != null) {
                id = bioseq.getID();
                if (!sequenceCollection.idIsUnique(id)) {
                    //TODO:
                    //sequence has no unique ID, create it or throw exception??
                    //id = id + "_" + data.generateId();
                    throw new ParserToolException("A sequence with ID: " + id + " already exists, unique IDs are neccesary!");
                }
            } else {
                //sequence has NO Id
                //id = data.generateId();
                throw new ParserToolException("Sequence has no ID!");
            }
            //get description, if given
            if (bioseq.getTitle() != null && !id.equals(bioseq.getTitle())) {
                description = bioseq.getTitle().toString().trim();
            } else {
                //no description given, set to null
              
                description = null;
            }

            //fill sequenceTypeImpl
            newSequenceType = getSequenceTypeInstance(newSequenceType, id, description, bioseq.getseq().toString().toUpperCase());
            //Add sequenceTypeImpl to sequenceCollection
            sequenceCollection.addSequence(newSequenceType);
        }
        return sequenceCollection;

    }

    /**
     * Reads sequence information from a String representation of a
     * SWISSPROT/UniProt file
     * @param swissprot sequence information in SWISSPROT/UniProt format as String
     * @param outputType concrete type to return
     * @return Collection of sequence information, elements are from type: outputType
     * @throws IOException inputdata cannot be read
     * @throws ParserToolException input is not valide
     */
    public SequenceCollection readSequenceSWISSPROT_UNIPROT(final String swissprot, SequenceTypeInterface outputType) throws IOException, ParserToolException {
        return readSequenceSWISSPROT_UNIPROT(new StringReader(swissprot), outputType);

    }

    /**
     * Reads multiple sequence information from a SWISSPROT/UniProt file, using a reader
     * @param swissprot reader, reading a SWISSPROT/UniProt file
     * @param outputType concrete type to return
     * @return SequenceCollection, elements are from type: outputType
     * @throws ParserToolException
     * @throws IOException input cannot be read
     */
    public SequenceCollection readSequenceSWISSPROT_UNIPROT(final Reader swissprot, SequenceTypeInterface outputType) throws ParserToolException, IOException {
        SequenceCollection sequenceCollection = new SequenceCollectionImpl();
        SequenceTypeInterface newSequenceType = outputType;

        LinkedList<BioseqRecord> sequenceList = readSeq(swissprot,ReadSeqFormat.SWISSPROT_UNIPROT.getFormatInt());


        String id;
        String description;

        for (BioseqRecord bioseq : sequenceList) {
            //clear description
            description = "";
            //get ID
            if (bioseq.getID() != null) {
                id = bioseq.getID();
                if (!sequenceCollection.idIsUnique(id)) {
                    //TODO:
                    //sequence has no unique ID, create it or throw exception??
                    //id = id + "_" + data.generateId();
                    throw new ParserToolException("A sequence with ID: " + id + " already exists, unique IDs are neccesary!");
                }
            } else {
                //sequence has NO Id
                //id = data.generateId();
                throw new ParserToolException("Sequence has no ID!");
            }
            //get description, if given
            if (bioseq.getTitle() != null && !id.equals(bioseq.getTitle())) {
                description = bioseq.getTitle().toString().trim();
            } else {
                //no description given, set to null
             
                description = null;
            }

            //fill sequenceTypeImpl
            newSequenceType = getSequenceTypeInstance(newSequenceType, id, description, bioseq.getseq().toString().toUpperCase());
            //Add sequenceTypeImpl to sequenceCollection
            sequenceCollection.addSequence(newSequenceType);
        }
        return sequenceCollection;


    }

    /**
     * Reads sequence information from a String representation of a EMBL file
     * @param embl sequence information in EMBL format as String
     * @param outputType concrete type to return
     * @return Collection of sequence information, elements are from type: outputType
     * @throws IOException inputdata cannot be read
     * @throws ParserToolException input is not valide
     */
    public SequenceCollection readSequenceEMBL(final String embl, SequenceTypeInterface outputType) throws IOException, ParserToolException {


        return (readSequenceEMBL(new StringReader(embl), outputType));
    }

    /**
     * Reads sequence information from a EMBL file, using a reader
     * @param embl Reader, reading a EMBL file
     * @param outputType concrete type to return
     * @return Collection of sequence information, elements are from type: outputType
     * @throws IOException inputdata cannot be read
     * @throws ParserToolException input is not valide
     */
    public SequenceCollection readSequenceEMBL(final Reader embl, SequenceTypeInterface outputType) throws IOException, ParserToolException {
        SequenceCollection sequenceCollection = new SequenceCollectionImpl();
        SequenceTypeInterface newSequenceType = outputType;

        LinkedList<BioseqRecord> sequenceList = readSeq(embl, ReadSeqFormat.EMBL.getFormatInt());


        String id;
        String description;

        for (BioseqRecord bioseq : sequenceList) {
            //clear description
            description = "";
            //get ID
            if (bioseq.getID() != null) {
                id = bioseq.getID();
                if (!sequenceCollection.idIsUnique(id)) {
                    //TODO:
                    //sequence has no unique ID, create it or throw exception??
                    //id = id + "_" + data.generateId();
                    throw new ParserToolException("A sequence with ID: " + id + " already exists, unique IDs are neccesary!");
                }
            } else {
                //sequence has NO Id
                //id = data.generateId();
                throw new ParserToolException("Sequence has no ID!");
            }
            //get description, if given
            if (bioseq.getTitle() != null && !id.equals(bioseq.getTitle())) {
                String[] idDesc = bioseq.getTitle().split(" ");
                for (int i = 1; i < idDesc.length; i++) {
                    description += idDesc[i] + " ";
                }
                description = description.trim();
            } else {
                //no description given, set to null
              
                description = null;
            }

            //fill sequenceTypeImpl
            newSequenceType = getSequenceTypeInstance(newSequenceType, id, description, bioseq.getseq().toString().toUpperCase());
            //Add sequenceTypeImpl to sequenceCollection
            sequenceCollection.addSequence(newSequenceType);
        }
        return sequenceCollection;
    }

    /**
     * Reads sequence information from a String representation of a NBRF file
     * @param nbrf sequence information in NBRF format as String
     * @param outputType concrete type to return
     * @return Collection of sequence information, elements are from type: outputType
     * @throws IOException inputdata cannot be read
     * @throws ParserToolException input is not valide
     */
    public SequenceCollection readSequenceNBRF(final String nbrf, SequenceTypeInterface outputType) throws IOException, ParserToolException {
        return (readSequenceNBRF(new StringReader(nbrf), outputType));
    }

    /**
     * Reads sequence information from a NBRF file, using a reader
     * @param nbrf Reader, reading a NBRF file
     * @param outputType concrete type to return
     * @return Collection of sequence information, elements are from type: outputType
     * @throws IOException inputdata cannot be read
     * @throws ParserToolException input is not valide
     */
    public SequenceCollection readSequenceNBRF(final Reader nbrf, SequenceTypeInterface outputType) throws IOException, ParserToolException {
        SequenceCollection sequenceCollection = new SequenceCollectionImpl();
        LinkedList<BioseqRecord> sequenceList = readSeq(nbrf, ReadSeqFormat.NBRF.getFormatInt());

        for (BioseqRecord sequence : sequenceList) {
            if ("".equals(sequence.getID()) || sequence.getID()==null) {
                throw new ParserToolException("Sequence without ID detected!");
            } else {
                if (sequenceCollection.idIsUnique(sequence.getID())) {
                    sequenceCollection.addSequence(getSequenceTypeInstance(outputType, sequence.getID(), null, sequence.getseq().toString().toUpperCase()));
                } else {
                    throw new ParserToolException("A sequence with ID: " + sequence.getID() + " already exists, unique IDs are neccesary!");
                }
            }
        }
        return sequenceCollection;
    }

        /**
     * Reads sequence information from a String representation of a CODATA/PIR file
     * @param codata sequence information in CODATA format as String
     * @param outputType concrete type to return
     * @return Collection of sequence information, elements are from type: outputType
     * @throws IOException inputdata cannot be read
     * @throws ParserToolException input is not valide
     */
    public SequenceCollection readSequenceCODATA(final String codata, SequenceTypeInterface outputType) throws IOException, ParserToolException {
        return (readSequenceNBRF(new StringReader(codata), outputType));
    }

    /**
     * Reads sequence information from a CODATA/PIR file, using a reader
     * @param codata Reader, reading a CODATA/PIR file
     * @param outputType concrete type to return
     * @return Collection of sequence information, elements are from type: outputType
     * @throws IOException inputdata cannot be read
     * @throws ParserToolException input is not valide
     */
    public SequenceCollection readSequenceCODATA(final Reader codata, SequenceTypeInterface outputType) throws IOException, ParserToolException {

        SequenceCollection sequenceCollection = new SequenceCollectionImpl();
        LinkedList<BioseqRecord> sequenceList = readSeq(codata, ReadSeqFormat.CODATA.getFormatInt());

        for (BioseqRecord sequence : sequenceList) {
            if ("".equals(sequence.getID()) || sequence.getID()==null) {
                throw new ParserToolException("Sequence without ID detected!");
            } else {
                if (sequenceCollection.idIsUnique(sequence.getID())) {
                    sequenceCollection.addSequence(getSequenceTypeInstance(outputType, sequence.getID(), null, sequence.getseq().toString().toUpperCase()));
                } else {
                    throw new ParserToolException("A sequence with ID: " + sequence.getID() + " already exists, unique IDs are neccesary!");
                }
            }
        }
        return sequenceCollection;
    }

    
    /**
     * Reads sequence information from a String representation of a FASTA file
     * @param fasta sequence information in FASTA format as String
     * @param outputType concrete type to return
     * @return Collection of sequence information, elements are from type: outputType
     * @throws IOException inputdata cannot be read
     * @throws ParserToolException input is not valide
     */
    public SequenceCollection readSequenceFasta(final String fasta, SequenceTypeInterface outputType) throws IOException, ParserToolException {
        return readSequenceFasta(new StringReader(fasta), outputType);

    }

    /**
     * Reads sequence information from a FASTA file, using a reader
     * @param fasta Reader, reading a FASTA file
     * @param outputType concrete type to return
     * @return Collection of sequence information, elements are from type: outputType
     * @throws IOException inputdata cannot be read
     * @throws ParserToolException input is not valide
     */
    public SequenceCollection readSequenceFasta(final Reader fasta, SequenceTypeInterface outputType) throws IOException, ParserToolException {

        SequenceCollection sequenceCollection = new SequenceCollectionImpl();
        SequenceTypeInterface newSequenceType = outputType;

        LinkedList<BioseqRecord> sequenceList = readSeq(fasta,ReadSeqFormat.Fasta.getFormatInt());

        String id;
        String description;

        for (BioseqRecord bioseq : sequenceList) {
            //clear description
            description = "";
            //get ID
            if (bioseq.getID() != null) {
                id = bioseq.getID();
                if (!sequenceCollection.idIsUnique(id)) {
                    //TODO:
                    //sequence has no unique ID, create it or throw exception??
                    //id = id + "_" + data.generateId();
                    throw new ParserToolException("A sequence with ID: " + id + " already exists, unique IDs are neccesary!");
                }
            } else {
                //sequence has NO Id
                //id = data.generateId();
                throw new ParserToolException("Sequence has no ID!");
            }
            //get description, if given
            if (bioseq.getTitle() != null && !id.equals(bioseq.getTitle())) {
                String[] idDesc = bioseq.getTitle().split(" ");
                for (int i = 1; i < idDesc.length; i++) {
                    description += idDesc[i] + " ";
                }
                description = description.trim();
            } else {
                //no description given, set to null
             
                description = null;
            }

            //fill sequenceTypeImpl
            newSequenceType = getSequenceTypeInstance(newSequenceType, id, description, bioseq.getseq().toString().toUpperCase());
            //Add sequenceTypeImpl to sequenceCollection
            sequenceCollection.addSequence(newSequenceType);
        }
        return sequenceCollection;
    }

    /**
     * Reads multiple sequence information from a String representation of a CLUSTAL file
     * @param clustal string representation of a CLUSTAL file
     * @param outputType concrete type to return
     * @return SequenceCollection, elements are from type: outputType
     * @throws ParserToolException
     * @throws IOException input cannot be read
     */
    public SequenceCollection readClustal(final String clustal, SequenceTypeInterface outputType) throws ParserToolException, IOException {
        return readClustal(new StringReader(clustal), outputType);
    }

    /**
     * Reads multiple sequence information from a CLUSTAL file, using a reader
     * @param clustal reader, reading a CLUSTAL file
     * @param outputType concrete type to return
     * @return SequenceCollection, elements are from type: outputType
     * @throws ParserToolException
     * @throws IOException input cannot be read
     */
    public SequenceCollection readClustal(final Reader clustal, SequenceTypeInterface outputType) throws ParserToolException, IOException {

        SequenceCollection sequenceCollection = new SequenceCollectionImpl();
        LinkedList<BioseqRecord> sequenceList = readSeq(clustal,ReadSeqFormat.CLUSTAL.getFormatInt());

        for (BioseqRecord sequence : sequenceList) {
            if ("".equals(sequence.getID()) || sequence.getID()==null) {
                throw new ParserToolException("Sequence without ID detected!");
            } else {
                if (sequenceCollection.idIsUnique(sequence.getID())) {
                    sequenceCollection.addSequence(getSequenceTypeInstance(outputType, sequence.getID(), null, sequence.getseq().toString().toUpperCase()));
                } else {
                    throw new ParserToolException("A sequence with ID: " + sequence.getID() + " already exists, unique IDs are neccesary!");
                }
            }
        }
        return sequenceCollection;

    }
    
    
//    public SequenceCollection readGenBank(final Reader genbank, SequenceTypeInterface outputType) throws ParserToolException, IOException {
//        SequenceCollection sequenceCollection = new SequenceCollectionImpl();
//        BufferedReader br = new BufferedReader(genbank);
//        Pattern seqdata = Pattern.compile("\\s*\\d+(\\s\\D+)+");
//        // read linewise
//        String line;
//        while ((line = br.readLine()) != null) {
//            
//        }
//        
//        
//        
//    
//    }

    public LinkedList<BioseqRecord> useParserFactory(Reader inputFile, ParserInputFormat pif) throws ParserToolException, ParserException {
        LinkedList<BioseqRecord> sequenceList = new LinkedList<BioseqRecord>();


        Parser gde = ParserFactory.createParser(pif, inputFile);

        if (gde.isKnownFormat()) {
            while (gde.readNext()) {
                SeqFileInfo info = gde.nextSeq();
                if (info != null && info.hasid()) {
                    sequenceList.add(new BioseqRecord(info));
                } else {
                    break;
                }
            }
        }

        if (sequenceList.isEmpty() || !sequenceList.getFirst().hasseq()) {
            throw new ParserToolException("No sequence information detected!");
        }

        return sequenceList;

    }

    /**
     * Reads biological information in different input formats to a List of BioseqRecords
     * @param inputFile the input to be read
     * @param format coded as int
     * @return List of sequenceinformation, any entry contains information about a single sequence
     * @throws IOException input could not be read
     * @throws ParserToolException no sequence information detected
     */
    public LinkedList<BioseqRecord> readSeq(Reader inputFile, int format) throws IOException, ParserToolException {

        LinkedList<BioseqRecord> sequenceList = new LinkedList<BioseqRecord>();

        Readseq rd = new Readseq();
        rd.setInputObject(inputFile);    
        
        // first test if the file is in correct format
        SeqFileInfo si = new SeqFileInfo();
        Testseq formatTestor = new Testseq();
        int foundFormat = formatTestor.testFormat(rd.getInput(), si);
        
        if(foundFormat!=format) {
            throw new ParserToolException("Wrong format detected!"+foundFormat);
        }
        
        inputFile.reset();
        rd = new Readseq();
        rd.setInputObject(inputFile);
        
        // read in the sequences to check them
        if (rd.isKnownFormat() && rd.readInit()) {
            while (rd.readNext()) {
                SeqFileInfo info = rd.nextSeq();
                if (info != null && info.hasid()) {
                    sequenceList.add(new BioseqRecord(info));
                } else {
                    break;
                }
            }
        }

        if (sequenceList.isEmpty() || !sequenceList.getFirst().hasseq()) {
            throw new ParserToolException("No sequence information detected!");
        }

        return sequenceList;
    }

    private SequenceTypeInterface getSequenceTypeInstance(SequenceTypeInterface concreteType, String id, String description, String sequence) throws ParserToolException {
        SequenceTypeInterface newSequenceType;

        try {
            newSequenceType = concreteType.getClass().newInstance();
        } catch (InstantiationException ex) {
            throw new ParserToolException("An InstantiationException occured while reading sequence information, error was: " + ex.getLocalizedMessage());
        } catch (IllegalAccessException ex) {
            throw new ParserToolException("An IllegalAccessException occured while reading sequence information, error was: " + ex.getLocalizedMessage());
        }

        PatternType pattern = concreteType.getPatternType();
        if (pattern.equals(PatternType.isFreeSequence)) {
           
            pattern = approximatePatternType(sequence, id);
        }
        newSequenceType.setPatternType(pattern);

        newSequenceType.setSeqID(id);
        newSequenceType.setDescription(description);
        newSequenceType.setSequence(sequence);

        return newSequenceType;
    }

    private PatternType approximatePatternType(String sequence, String id) {
        if (!SequenceValidator.validateGappedAmbiguousAminoAcidOneLetterSequence(sequence)) { // unallowed char ->
            // free sequence
            
            return PatternType.isFreeSequence;
        } else if (!SequenceValidator.validateGappedAmbiguousNucleotideSequence(sequence)) { // aminoacid (contains chars that are only allowed in aminoacids)
           
            return PatternType.BT_gappedAmbiguousAminoAcidOneLetterSequence;
        } else { // aminoacid or nucleicacid? -> depends on percentage of
            // ACGTU (current >= 90%)
            final String uSeq = sequence.toUpperCase();
            int cnt = 0, length = uSeq.length();
            for (int j = 0; j < uSeq.length(); j++) {
                final char c = uSeq.charAt(j);
                switch (c) {
                    case '-':
                        length--;
                        break;
                    case 'A':
                    case 'C':
                    case 'G':
                    case 'T':
                    case 'U':
                        cnt++;
                        break;
                }
            }
            if ((double) cnt / (double) length >= 0.9) { // over 90% of
                // sequence are
                // ACGTU ->
                // probably
                // nucleicacid

               
                return PatternType.BT_gappedAmbiguousNucleotideSequence;
            } else { // over 10% of sequence are not ACGTU -> probably
                // aminoacid
               
                return PatternType.BT_gappedAmbiguousAminoAcidOneLetterSequence;
            }
        }
    }
    
    
    
}
