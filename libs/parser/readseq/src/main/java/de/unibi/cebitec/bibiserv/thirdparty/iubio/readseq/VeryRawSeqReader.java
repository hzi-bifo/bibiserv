//iubio/readseq/VeryRawSeqReader.java
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

//split4javac// iubio/readseq/CommonSeqFormat.java line=178
public class VeryRawSeqReader  extends PlainSeqReader
{
	// very simpler format -- no newlines, or anything but seq chars - use w/ BioseqFiled for large files
	protected Vector fileseqv;
	protected BioseqFiled filedseq;
	protected static Integer gNullfile= new Integer(0);
	protected boolean endOfFile;
	
	public VeryRawSeqReader() {
		margin	=  0;
		addfirst= true;
		addend 	= true;  
		ungetend= false;
		fileseqv= new Vector();
		}

	public boolean endOfFile() { if (inputFile!=null) return endOfFile; else return super.endOfFile(); }
	
	protected void read() throws IOException 
	{ 
		if (inputFile!=null) {
			atseq++;
		  if (choice == kListSequences) addit = false;
		  else addit = (atseq == choice);
		  if (addit) seqlen = seqlencount= 0;
		  
			seqid= SeqFileInfo.cleanSeqID(inputFile.getName());  
			seqid= SeqFileInfo.getNextBlankID(seqid);

		  if (addit) {
				filedseq= new BioseqFiled( inputFile, false);
				int foundends= filedseq.scanForEnd( atseq, (atseq>1) ? fileseqv.elementAt(atseq-2) : null);
					//^ scanForEnd sets file offset, length for seq
				if (foundends<atseq || filedseq.endOfFile()) { endOfFile= true; allDone= true; }  
				
				seqlencount= seqlen= filedseq.length();
				
				while (fileseqv.size()<atseq) fileseqv.addElement( gNullfile);
				fileseqv.setElementAt( filedseq, atseq-1);
				}
			
			
			//atseq= 1; // only 1? -- NO, one per line !
			//seqlencount= seqlen= filedseq.length();
			//allDone= true;
			
		  if (choice == kListSequences) addinfo(seqid); 
		  else {
		   	allDone = (atseq >= choice);
		    }
			
			Debug.println("VeryRawSeqReader from BioseqFiled, id="+seqid+", length="+seqlen+", file="+inputFile);
			}
		else
			super.read(); 
	}	

	public void copyto( SeqFileInfo si) 
	{
		if ( filedseq==null ) { super.copyto(si); return; }
		if (si==null) return;
		if (si.err==0) si.err= err;
		si.seqlen= seqlen;
		si.seqdoc= seqdoc;
		if (si.err==0 && (seqlen>0 || seqdoc!=null)) { 
			si.atseq= atseq; //<< counted to current sequence in file
			if (atseq>si.nseq) si.nseq= atseq;
			si.seqid= seqid;
			si.checkSeqID();
			//? si.offset= seqoffset; // will this bugger up output - fix

			si.seq= filedseq;
			//if (seqlen>0) si.seq= new Bioseq( bases, 0, seqlen); //, useBioseqBytes
			//else si.seq= null;
				
			clearSeqBuffer(); // sets bases= null - filedseq= null?
			filedseq= null;
			}		
	}

}


