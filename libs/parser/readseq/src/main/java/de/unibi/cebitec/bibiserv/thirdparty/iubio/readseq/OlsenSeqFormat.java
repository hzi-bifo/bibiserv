//iubio/readseq/OlsenSeqFormat.java
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

 

//split4javac// iubio/readseq/InterleavedSeqReader.java line=833
public class OlsenSeqFormat extends BioseqFormat
{		
	public String formatName() { return "Olsen";  }  
	public String formatSuffix() { return ".olsen"; } // .gb ?
	public String contentType() { return "biosequence/olsen"; } // genbank ?
	
	public boolean canread() { return false; } //! need some work!
	public boolean canwrite() { return false; } // do as genbank
	public boolean interleaved() { return true; }

	public BioseqWriterIface newWriter() { 
		// Olsen editor can read GenBank
		return BioseqFormats.newWriter( 
			BioseqFormats.formatFromContentType("biosequence/genbank"),-1);
		}
		
	public BioseqReaderIface newReader() { return new OlsenSeqReader(); }

	public boolean formatTestLine(OpenString line, int atline, int skiplines) 
	{
		if (line.indexOf("identity:   Data:") >=0) {
      formatLikelihood += 95;
      return true;
      }
    else
    	return false;
	}
}

//public
class OlsenSeqReader  extends InterleavedSeqReader //InterleavedSeqreader
{
	public OlsenSeqReader() {
		margin	=  0;
		addfirst= false;
		addend 	= false;  
		ungetend= true;
		//formatLabel= "Olsen";
		//formatId= 10;
		}

	public boolean endOfSequence() {
		return false;
		}

	protected void read() throws IOException
	{
			// needs some work...
	}
	
};






