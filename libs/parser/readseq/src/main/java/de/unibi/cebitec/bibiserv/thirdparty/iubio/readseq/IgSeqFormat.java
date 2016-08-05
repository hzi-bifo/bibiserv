//iubio/readseq/IgSeqFormat.java
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

//split4javac// iubio/readseq/CommonSeqFormat.java line=261
public class IgSeqFormat extends BioseqFormat
{
	public String formatName() { return "IG|Stanford"; }  
	public String formatSuffix() { return ".ig"; } 
	public String contentType() { return "biosequence/ig"; } 
	public BioseqReaderIface newReader() { return new IgSeqReader(); }
	public BioseqWriterIface newWriter() { return new IgSeqWriter(); }
	public boolean canread() { return true; }
	public boolean canwrite() { return true; }

	public boolean formatTestLine(OpenString line, int atline, int skiplines) 
	{
		if (line.length()>0 && line.charAt(0) == ';') {
      formatLikelihood= 55;
      if (recordStartline==0) recordStartline= atline;
      return false; //!
      }
    else
    	return false;
	}

}

//public
class IgSeqReader  extends BioseqReader
{
	public IgSeqReader() {
		margin	=  0;
		addfirst= false;
		addend 	= true;  
		ungetend= false;
		//formatId= 1;
		}

	public boolean endOfSequence() {
	  return ( indexOfBuf('1')>=0 || indexOfBuf('2')>=0);
		}

	protected void read() throws IOException
	{
	  while (!allDone) {
	    do {
	      getline();
	      sWaiting= sWaiting.trim(); 
	      nWaiting= sWaiting.length();
	    } while (!(endOfFile() || (nWaiting>0 && sWaiting.charAt(0) != ';' ) ));
	    
	    if (!endOfFile()) {
	      seqid= sWaiting.toString();
	      readLoop();
	      }
	    if (endOfFile()) allDone = true;
	  }
	}
};

//public
class IgSeqWriter  extends BioseqWriter
{
	public void writeSeqEnd() { writeString("1"); }
	
	public void writeDoc()
	{
		//writeln( ";" + seqid + "  " + seqlen + " bases  " + checksumString());
		writeString( ";");
		writeString( seqid);
		writeString("  ");
		writeString( String.valueOf( seqlen));
		writeString(  " bases  ");
		writeln( checksumString());

		if (seqdoc instanceof BioseqDoc) {
			String title= ((BioseqDoc)seqdoc).getTitle();
			if (title!=null) { writeString(";"); writeln( title );  }
			}

		writeln( idword);
  	//linesout += 2;
	}
   
};




