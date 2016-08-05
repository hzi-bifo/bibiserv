//iubio/readseq/NbrfSeqFormat.java
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

//split4javac// iubio/readseq/CommonSeqFormat.java line=797
public class NbrfSeqFormat extends BioseqFormat
{
	public String formatName() { return "NBRF"; }  
	public String formatSuffix() { return ".nbrf"; } 
	public String contentType() { return "biosequence/nbrf"; } 
	public BioseqReaderIface newReader() { return new NbrfSeqReader(); }
	public BioseqWriterIface newWriter() { return new NbrfSeqWriter(); }
	public boolean canread() { return true; }
	public boolean canwrite() { return true; }

	public boolean formatTestLine(OpenString line, int atline, int skiplines) 
	{
		if (line.length()>0 && line.charAt(0) == '>' && line.charAt(3) == ';') {
      formatLikelihood += 70; //?
      if (recordStartline==0) recordStartline= atline;
      return false; //?
      }
    else
    	return false;
	}

}


//public
class NbrfSeqReader  extends BioseqReader
{
	public NbrfSeqReader() {
		margin	=  0;
		addfirst= false;
		addend 	= false;  
		ungetend= true;
		//formatId= 3;
		}
		
	public boolean endOfSequence()
	{
		int starat= indexOfBuf('*');
	  if (starat >= 0) { /* end of 1st seqq */
	    /* "*" can be valid base symbol, drop it here */
	    sWaiting= bufSubstring(0,starat); //?? don't need unless needString set
	    nWaiting= starat;
	   	addend  = true;
	   	ungetend= false;
	    return(true);
	    }
	  else if (getreadbuf(0) == '>') { /* start of next seqq */
	    addend  = false;
	    ungetend= true;
	    return(true);
	    }
	  else
	    return(false);
	}
	  

	protected void read() throws IOException
	{
	  while (!allDone) {
	    if (nWaiting > 4) seqid= sWaiting.substring(4).toString();
	    getline();   /*skip title-junk line*/
	    readLoop();
	    if (!allDone) {
	     	while (!(endOfFile() || (nWaiting > 0 && getreadbuf(0) == '>')))
	   			getline();
	      }
	    if (endOfFile()) allDone = true;
	  }
	}
};


//public
class NbrfSeqWriter  extends BioseqWriter
{

	public void writeRecordStart()
	{
		super.writeRecordStart();
    opts.spacer = 10;
	}
		
	public void writeSeqEnd() { writeString("*");  }
	
	public void writeDoc()
	{
    String tag;
    if (bioseq.getSeqtype() == Bioseq.kAmino)  tag= ">P1;"; else tag= ">DL;";
		writeln( tag + idword);
		writeln( seqid + "  " + seqlen + " bases  " + checksumString());
    //linesout += 3;
   }
   
};



