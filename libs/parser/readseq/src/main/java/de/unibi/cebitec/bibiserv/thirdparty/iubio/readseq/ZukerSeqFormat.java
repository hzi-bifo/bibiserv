//iubio/readseq/ZukerSeqFormat.java
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

//split4javac// iubio/readseq/CommonSeqFormat.java line=1185
public class ZukerSeqFormat extends BioseqFormat
{
	public String formatName() { return "Zuker"; }  
	public String formatSuffix() { return ".zuker"; } 
	public String contentType() { return "biosequence/zuker"; } 
	public BioseqReaderIface newReader() { return new ZukerSeqReader(); }
	//public BioseqWriterIface newWriter() { return new ZukerSeqWriter(); }
	public boolean canread() { return false; }
	public boolean canwrite() { return false; }

	public boolean formatTestLine(OpenString line, int atline, int skiplines) 
	{
		if (line.length()>0 && line.charAt(0) == '(') {
      formatLikelihood= 25;
      if (recordStartline==0) recordStartline= atline;
      return false; //!
      }
    else
    	return false;
	}
}

//public
class ZukerSeqReader  extends BioseqReader
{
	public ZukerSeqReader() {
		margin	=  0;
		addfirst= true;
		addend 	= true;  
		ungetend= false;
		//formatId= 9;
		}
};



