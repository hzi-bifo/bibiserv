//iubio/readseq/TestBiobase.java
//split4javac// iubio/readseq/BioseqReader.java date=28-Jun-2002

// iubio.readseq.BioseqReader.java -- was seqreader.java
// d.g.gilbert, 1990-1999

// ? fast version - read chunks to OpenString
	
package de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq;

import java.io.*;

import de.unibi.cebitec.bibiserv.thirdparty.flybase.Debug;
import de.unibi.cebitec.bibiserv.thirdparty.flybase.OpenString;
import de.unibi.cebitec.bibiserv.thirdparty.flybase.Utils;

	// interfaces
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.BioseqReaderIface;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.BioseqWriterIface;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.BioseqDoc;

import de.unibi.cebitec.bibiserv.thirdparty.iubio.bioseq.BaseKind;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.bioseq.Bioseq;

	// can we do w/o these?
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.SeqFileInfo;
import de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq.GenbankDoc;
	

 
//split4javac// iubio/readseq/BioseqReader.java line=669
class TestBiobase
{
	protected TestBiobase outtest, intest;
	
	public void setInTest(TestBiobase intest) { this.intest= intest; }
	public void setOutTest(TestBiobase outtest) { this.outtest= outtest; }
	
		/** do any needed base translation, including user base changes, here */
	public int isSeqChar(int c) { 
		//return BaseKind.isSeqChar(c);  //<< BAD!?
		//if (Character.isSpace((char)c) || Character.isDigit((char)c)) return 0; //<slow
		if (c<=' ' || (c >= '0' && c <= '9')) return 0;
		else if (intest!=null) return intest.isSeqChar(c); // order of chain is critical - 1st? or last
		else return c;
		}

			// output translation
	public int outSeqChar(int c) { 
		if (outtest!=null) return outtest.outSeqChar(c);  // order of chain is critical - 1st? or last
		else return c; 
		}
	
}

//public 
class TestAnychar extends TestBiobase
{
	public int isSeqChar(int c) {
		return BaseKind.isAnyChar(c); 
		}
}

