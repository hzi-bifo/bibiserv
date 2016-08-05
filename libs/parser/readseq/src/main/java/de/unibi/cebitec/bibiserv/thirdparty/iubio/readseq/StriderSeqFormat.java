//iubio/readseq/StriderSeqFormat.java
//split4javac// iubio/readseq/CommonSeqFormat.java date=04-Jun-2003

// iubio.readseq.CommonSeqFormat.java -- was seqread1.java
// low level readers & writers : sequential formats
// d.g.gilbert, 1990-1999

package de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq;
//package iubio.readseqF;

import java.io.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Vector;

import de.unibi.cebitec.bibiserv.thirdparty.flybase.OpenString;
import de.unibi.cebitec.bibiserv.thirdparty.flybase.Debug;

import de.unibi.cebitec.bibiserv.thirdparty.Acme.Fmt;
		 
//import iubio.readseq.*;
	// interfaces
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.BioseqReaderIface;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.BioseqWriterIface;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.BioseqDoc;

import de.unibi.cebitec.bibiserv.thirdparty.iubio.bioseq.BaseKind;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.bioseq.SeqInfo;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.bioseq.Bioseq;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.bioseq.BioseqFiled;

	// can we do w/o these?
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.SeqFileInfo;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.GenbankDoc;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.EmblDoc;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.SwissDoc;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.BioseqFormat;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.BioseqWriter;

	
//========= sequential BioseqReader subclasses ==========

//split4javac// iubio/readseq/CommonSeqFormat.java line=1271
public class  StriderSeqFormat extends BioseqFormat
{
	public String formatName() { return "DNAStrider"; }  
	public String formatSuffix() { return ".strider"; } 
	public String contentType() { return "biosequence/strider"; } 
	public BioseqReaderIface newReader() { return new StriderSeqReader(); }
	public BioseqWriterIface newWriter() { return new StriderSeqWriter(); }
	public boolean canread() { return true; }
	public boolean canwrite() { return true; }
	public boolean formatTestLine(OpenString line, int atline, int skiplines) 
	{
		if (line.length()>0 && line.charAt(0) == ';' && line.indexOf("Strider")>0) {
      formatLikelihood= 75;
      if (recordStartline==0) recordStartline= atline;
      return true;
      }
    else
    	return false;
	}

}

//public
class StriderSeqReader  extends BioseqReader
{
	public StriderSeqReader() {
		margin	=  0;
		addfirst= true;
		addend 	= false;  
		ungetend= false;
		//formatId= 6;
		}

	public boolean endOfSequence() {
	  return ( indexOfBuf("//")>=0);
		}

	protected void read() throws IOException
	{
	  while (!allDone) {
      getline();
      sWaiting= sWaiting.trim(); 
      nWaiting= sWaiting.length();
	    if (sWaiting.indexOf("; DNA sequence  ")==0)
    		seqid= sWaiting.substring(16).toString();
	    else if (nWaiting > 0)
    		seqid= sWaiting.substring(1).toString();
			while (!(endOfFile() || (nWaiting>0 && sWaiting.charAt(0) != ';' ) )) {
	      getline();
	      sWaiting= sWaiting.trim(); 
	      nWaiting= sWaiting.length();
	    	}
	    
	    if (!endOfFile()) {
	      readLoop();
	      }
	    if (endOfFile()) allDone = true;
	  }
	}
};


//public
class StriderSeqWriter  extends BioseqWriter
{		
	public void writeRecordEnd() { writeln("//"); }

	public void writeDoc()
	{
		writeln( "; ### from DNA Strider ;-)");
		writeString( "; DNA sequence  ");
		writeString( seqid); 
		writeln( "  " + seqlen + " bases " + checksumString());
		writeln( ";");
		//linesout += 3;
	}
	
};






