//iubio/readseq/InterleavedSeqReader.java
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

 

//split4javac// iubio/readseq/InterleavedSeqReader.java line=28
public class InterleavedSeqReader  extends BioseqReader
{
	protected int skipHeaderLines;
	protected boolean firstpass= true;
	
	public void readTo( BioseqWriterIface writer, int skipHeaderLines)  throws IOException 
	{
		this.skipHeaderLines= skipHeaderLines;
		super.readTo( writer, skipHeaderLines);
		firstpass= false;
	}
	
	public SeqFileInfo readOne( int whichEntry) throws IOException
	{
		this.reset();  
		this.skipPastHeader( skipHeaderLines);  
		SeqFileInfo si= super.readOne( whichEntry);
		firstpass= false;
		return si;
	} 

	public void setInput(Reader ins) {  
		super.setInput(ins);
		firstpass= true; // cant do in reset()
		}

	public boolean endOfFile() { //?
	  return super.endOfFile();
	  //return ((atseq==0 || firstpass) ? super.endOfFile() : (atseq >= nseq)); //return fEof;
		}
}

		 
