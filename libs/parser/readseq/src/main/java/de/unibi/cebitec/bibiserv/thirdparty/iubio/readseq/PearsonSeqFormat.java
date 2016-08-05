//iubio/readseq/PearsonSeqFormat.java
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

//split4javac// iubio/readseq/CommonSeqFormat.java line=346
public class PearsonSeqFormat extends BioseqFormat
{
	public String formatName() { return "Pearson|Fasta|fa"; }  
	public String formatSuffix() { return ".fasta"; } 
	public String contentType() { return "biosequence/fasta"; } 
	public BioseqReaderIface newReader() { return new PearsonSeqReader(); }
	public BioseqWriterIface newWriter() { return new PearsonSeqWriter(); }
	public boolean canread() { return true; }
	public boolean canwrite() { return true; }
	//public boolean hasdoc() { return true; } //?? need to get defline

	public boolean formatTestLine(OpenString line, int atline, int skiplines) 
	{
		if (line.length()>0 && line.charAt(0) == '>') {
      formatLikelihood = 55;
      if (recordStartline==0) recordStartline= atline;
      return false; //!
      }
    else
    	return false;
	}

}

//public
class PearsonSeqReader  extends BioseqReader
{
  // add BasicBioseqDoc doc; 
  // for defline seqid, description, any field parsing in defline?
  
	public PearsonSeqReader() {
		margin	=  0;
		addfirst= false;
		addend 	= false;  
		ungetend= true;
		//formatId= 8;
		}

	public boolean endOfSequence() {
	  return (nWaiting > 0 && getreadbuf(0) == '>');
		}

	protected void read() throws IOException
	{
	  while (!allDone) {
	    if (nWaiting > 0) {
	      seqid= sWaiting.substring(1).toString();
// default superclass read handles BasicBioseqDoc from seqid
//	      if (false) {
//		    doc= new BasicBioseqDoc(); 
//		    if (skipdocs) doc.setSkipDocs(skipdocs);
//		    seqdoc= doc;
//	      doc.addBasicName( seqid); // this parses seqid parts
//	      }
	      }
	    readLoop();
	    if (!allDone) {
	    	while (!(endOfFile() || (nWaiting > 0 && getreadbuf(0) == '>') ))
	        getline();
	      }
	    if (endOfFile()) allDone = true;
	  }
	}
};


//public
class PearsonSeqWriter  extends BioseqWriter
{
	final static int kSeqwidth= 60; // up from default 50, jul'99 - but let cmdline override
	int seqwidth= kSeqwidth;//? need this?
	
	public void setOpts(WriteseqOpts newopts) { 
		if (newopts!=null) {
			// pick only relevant opts .seqwidth
			opts.seqwidth= seqwidth= newopts.seqwidth;
			}
		}

	public void writeRecordStart() {
		super.writeRecordStart();
   	opts.seqwidth = seqwidth; 
		}

	public void writeRecordEnd() { } // no extra newline!

	public void writeSeq() {  
		// writeLoop(); // ? replace w/ simpler one for this format? just dump seqq, 60/line
		int i, nout= 0;
		boolean newline= true;
		if (bioseq.isBytes() && testbaseKind != kUseTester) {
			byte[] ba= bioseq.toBytes();
			for (i= 0; i < seqlen; i++) {
				writeByte( (char)ba[ offset+i]); newline= false; 
				if (i % seqwidth == seqwidth-1) { 
					writeln(); newline= true; 
					}
				}
			
			}
		else {
			for (i= 0; i < seqlen; i++) {
		   	char bc= bioseq.base(offset+i,fBasePart);
	      if (testbaseKind == kUseTester) bc= (char) testbase.outSeqChar( bc);
				if (bc>0) {
					writeByte( bc); newline= false; nout++;
					if ( (nout-1) % seqwidth == seqwidth-1) { 
						writeln(); newline= true; 
						}
					}
				}
			}
		if (!newline) writeln();
	}
		
		
	public void writeDoc() {
   	writeString(">");
   	writeString(seqid);
   	// add some other doc here if available - EMBL/GB def line
		if (seqdoc instanceof BioseqDoc) {
		  Debug.println("fasta doc from "+seqdoc);
			String title=  ((BioseqDoc)seqdoc).getTitle();
			if (title!=null) {
				title= title.replace('\n',';').replace('\r',';'); // make sure no newlines
			 	writeString(" "); writeString( title ); 
			 	}
			}
   	writeString(" ");
   	writeString( String.valueOf(seqlen));
   	writeString(" bp ");
   	writeln(checksumString());
		//linesout += 1;
		}
};



