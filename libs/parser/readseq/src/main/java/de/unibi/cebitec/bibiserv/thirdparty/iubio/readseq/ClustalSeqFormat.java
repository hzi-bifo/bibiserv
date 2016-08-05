//iubio/readseq/ClustalSeqFormat.java
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

 

//split4javac// iubio/readseq/InterleavedSeqReader.java line=677
public class ClustalSeqFormat extends BioseqFormat
{		
	public String formatName() { return "Clustal";  }  
	public String formatSuffix() { return ".aln"; }  
	public String contentType() { return "biosequence/clustal"; }  
	public boolean canread() { return true; }   
	public boolean canwrite() { return true; }  
	public boolean interleaved() { return true; }  

	public BioseqWriterIface newWriter() { return new ClustalSeqWriter(); }		
	public BioseqReaderIface newReader() { return new ClustalSeqReader(); }

	public boolean formatTestLine(OpenString line, int atline, int skiplines) 
	{
		////CLUSTAL W (1.8) multiple sequence alignment
		if ( line.startsWith("CLUSTAL ") ) {
	      formatLikelihood  = 95;
        if (recordStartline==0) recordStartline= atline;
	      //if (line.indexOf("multiple sequence alignment")>0) return true; else 
	      return true; // ??
	      }
    else
    	return false;
	}
}

/*
CLUSTAL W (1.8) multiple sequence alignment


HBB_HUMAN       --------VHLTPEEKSAVTALWGKVN--VDEVGGEALGRLLVVYPWTQRFFESFGDLST
HBB_HORSE       --------VQLSGEEKAAVLALWDKVN--EEEVGGEALGRLLVVYPWTQRFFDSFGDLSN
                          *:  :   :   *  .           :  .:   * :   *  :   .

HBB_HUMAN       PDAVMGNPKVKAHGKKVLGAFSDGLAHLDN-----LKGTFATLSELHCDKLHVDPENFRL
HBB_HORSE       PGAVMGNPKVKAHGKKVLHSFGEGVHHLDN-----LKGTFAALSELHCDKLHVDPENFRL
*/

//public
class ClustalSeqReader  extends InterleavedSeqReader  
{
	final static int kNameWidth= 15;  
	
	public ClustalSeqReader() {
		margin	=  0; //? or kNameWidth
		addfirst= false;
		addend 	= false;  
		ungetend= true;
		}

	public boolean endOfSequence() {
		return false;
	}

	protected void read() throws IOException
	{
  	int  iline= 0;
	 	addit = (choice > 0);
	  if (addit) seqlen = seqlencount= 0;
	  boolean  done, first= true;
	  do {
	    getline();
	    done = endOfFile();
	    if (done && nWaiting==0) break;

			OpenString sid= null;
			if (nWaiting>kNameWidth)
	   		sid= sWaiting.substring(0,kNameWidth).trim();
	   		
	    if (sid==null || sid.length()==0) { // includes blanks and conserved line after seqs
	    	if (atseq>0) first= false;
	    	}
	    else {
   	 		if (first) {  
	        if (firstpass) nseq++; /// this is bad - need to stop incrementing 1st time thru
	        atseq++;  
	        if (choice == kListSequences)  addinfo(sid.toString());
	        else if ( atseq == choice ) {
	          addseq( getreadchars(), getreadcharofs()+kNameWidth, nWaiting-kNameWidth); 
	       		seqid= sid.toString();
						} 
    			}
	      else if ( iline % atseq == choice - 1 ) 
	        addseq( getreadchars(), getreadcharofs()+kNameWidth, nWaiting-kNameWidth);
	        
	  		iline++;
	    	}
	  } while (!done);
	  
		allDone = true;
	}

};


//public
class ClustalSeqWriter extends InterleavedSeqWriter //PhylipSeqWriter //?
{
	//int lastlen;
	//String lenerr;

	public void writeRecordStart() {
		super.writeRecordStart();
   	opts.spacer = 0;
   	opts.seqwidth= 60;
  	opts.nameleft = true;
  	opts.nameflags= Fmt.LJ;
  	opts.namewidth= ClustalSeqReader.kNameWidth;  
   	opts.tab = 1; 
  	//opts.tab = ClustalSeqReader.kNameWidth+1; 
		}
 
	public void writeDoc() {
		super.writeDoc();
		//? writeString( Fmt.fmt( idword, ClustalSeqReader.kNameWidth, Fmt.TR + Fmt.LJ) + "  ");
 		}

	protected void interleaf(int leaf) {
		writeln( Fmt.fmt( " ", ClustalSeqReader.kNameWidth+1, Fmt.TR + Fmt.LJ));  // conserved line:           *:  :   :   *  .           :  .:   * :   *  :   . 
		writeln();  
		}

	protected void interleaveHeader() {
 		writeln("CLUSTAL W (1.8) multiple sequence alignment"); 
 		writeln(); 
 		writeln(); 
	}

//	public boolean setSeq( Object seqob, int offset, int length, String seqname,
//						 Object seqdoc, int atseq, int basepart) 
//	{
//		if (lastlen > 0 && length != lastlen) {
//			if (lenerr==null) lenerr= String.valueOf(lastlen) + " != ";
//			lenerr += String.valueOf(length) + ", ";
//			length= lastlen; // can we pad/trunc to first length?
//			}
//		else lastlen= length;
//		return super.setSeq(seqob,offset,length,seqname,seqdoc,atseq, basepart);
//	}

//	public void writeTrailer()  { 
//		if (lenerr!=null) {
//			BioseqReader.message("Warning: this format requires equal sequence lengths.");
//			BioseqReader.message("       : lengths are padded/truncated to "+lastlen);
//			BioseqReader.message("       : " + lenerr);
//			}
//		super.writeTrailer();
//	}
	
};






