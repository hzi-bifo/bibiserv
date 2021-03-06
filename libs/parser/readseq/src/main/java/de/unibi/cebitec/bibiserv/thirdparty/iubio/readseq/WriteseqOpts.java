//iubio/readseq/WriteseqOpts.java
//split4javac// iubio/readseq/WriteseqOpts.java date=20-May-2001

// iubio.readseq.WriteseqOpts.java
// d.g.gilbert, 1990-1999

	
package de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq;

import de.unibi.cebitec.bibiserv.thirdparty.iubio.bioseq.BaseKind;
//import iubio.bioseq.Bioseq;
//import iubio.bioseq.SeqRange;



//split4javac// iubio/readseq/WriteseqOpts.java line=13
public class WriteseqOpts 
{
  public boolean isactive, userchoice, baseonlynum, reversed,
		numright, numleft, numtop, numbot,
		nameright, nameleft, nametop,
		noleaves, domatch, degap, blankpad;
  public char  matchchar, gapchar;
  public int numline, atseq, origin,
		namewidth, numwidth, nameflags, numflags,
		interline, spacer, seqwidth, tab;

	public WriteseqOpts() 
	{
		plainInit();
	}
	
	public void userset() { userchoice= true; }
	
	public void plainInit()
	{
		isactive = false ; 
		baseonlynum = false ; 
		blankpad = false;
		reversed= false;
		origin = 1 ; 
		numline = 0 ; 
		numright = numtop = false ; 
		numleft = numbot = false ; 
		nameright = false ; 
		nameleft = nametop = false ; 
		noleaves = domatch = degap = false ; 
		matchchar = '.' ; 
		gapchar = BaseKind.indelHard ; 
		namewidth = 8 ; numwidth = 8 ; 
		nameflags = 0; numflags = 0;
		interline = 1 ; spacer = 0 ; 
		seqwidth = 50 ; tab = 0 ; 
	}
	
		// only for PrettySeqreader subclass?
	public void prettyInit()
	{ 
		isactive = false ; 
		baseonlynum = true ; 
		blankpad = true;
		reversed= false;
		origin = 1 ; 
		numline = 0 ; 
		numright = numtop = true ; 
		numleft = numbot = false ; 
		nameright = true ; 
		nameleft = nametop = false ; 
		noleaves = domatch = degap = false ; 
		matchchar = '.' ;  
		gapchar = BaseKind.indelHard ; 
		namewidth = 8 ; numwidth = 5 ; 
		nameflags = 0 ; numflags = 0 ;
		interline = 1 ; spacer = 10 ; 
		seqwidth = 50 ; tab = 1 ; 
	} 

} 

