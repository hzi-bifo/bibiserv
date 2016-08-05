//iubio/readseq/Phylip2SeqFormat.java
//split4javac// iubio/readseq/InterleavedSeqReader.java date=13-Jun-2003

// InterleavedSeqReader.java -- was seqread2.java
// low level readers & writers : interleaved formats
// d.g.gilbert, 1990-1999


package de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq;


import java.io.*;
import java.util.Date;
import java.text.SimpleDateFormat;

import de.unibi.cebitec.bibiserv.thirdparty.Acme.Fmt;
import de.unibi.cebitec.bibiserv.thirdparty.flybase.Debug;
import de.unibi.cebitec.bibiserv.thirdparty.flybase.OpenString;
//import flybase.Native;

import de.unibi.cebitec.bibiserv.thirdparty.iubio.bioseq.BaseKind;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.bioseq.SeqInfo;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.bioseq.Bioseq;

// PaupSeqFormat reader needs work/test
// PrettySeqFormat not readable
// OlsenSeqFormat not readable yet

 

//split4javac// iubio/readseq/InterleavedSeqReader.java line=278
public class Phylip2SeqFormat extends PhylipSeqFormat 
{
	public Phylip2SeqFormat() { super(); }
	public String formatName() { return "Phylip3.2"; }  
	public String formatSuffix() { return ".phylip2"; } 
	public String contentType() { return "biosequence/phylip2"; } 
	public BioseqWriterIface newWriter() { 
		PhylipSeqWriter c= new PhylipSeqWriter(); 
		c.setinterleaved(false); // always for this format?
		return c; 
		}
}

