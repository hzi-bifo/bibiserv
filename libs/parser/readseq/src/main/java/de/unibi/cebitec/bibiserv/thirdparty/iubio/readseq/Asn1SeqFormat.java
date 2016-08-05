//iubio/readseq/Asn1SeqFormat.java
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

//split4javac// iubio/readseq/CommonSeqFormat.java line=1221
public class  Asn1SeqFormat extends BioseqFormat
{
	public String formatName() { return "ASN.1"; }  
	public String formatSuffix() { return ".asn"; } 
	public String contentType() { return "biosequence/asn1"; } 
	public BioseqReaderIface newReader() { return new Asn1SeqReader(); }
	public BioseqWriterIface newWriter() { return new Asn1SeqWriter(); }
	public boolean canread() { return false; }
	public boolean canwrite() { return false; }

	public boolean formatTestLine(OpenString line, int atline, int skiplines) 
	{
		if ( line.indexOf("::=")>=0 &&
       ( line.indexOf("Bioseq")>=0 ||       // Bioseq or Bioseq-set 
        line.indexOf("Seq-entry")>=0 ||
        line.indexOf("Seq-submit")>=0 ) ) { 
		      formatLikelihood= 90;
		      return true;
		      }
    else
    	return false;
	}
}

//public
class Asn1SeqReader  extends BioseqReader
{
	public Asn1SeqReader() {
		margin	=  0;
		addfirst= true;
		addend 	= true;  
		ungetend= false;
		//formatId= 16;
		}
};

//public
class Asn1SeqWriter  extends BioseqWriter
{
	public void writeHeader()  throws IOException { 
		super.writeHeader();
		writeln("Bioseq-set ::= {\nseq-set {"); 
		}
	public void writeTrailer() { 
		writeln("} }"); 
		super.writeTrailer();
		}
};


