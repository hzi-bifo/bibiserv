//iubio/readseq/BioseqDoc.java
//split4javac// iubio/readseq/BioseqDoc.java date=20-May-2001

// BioseqDoc.java
// sequence format information (document,features) handlers
// d.g.gilbert, 1997++


package de.unibi.cebitec.bibiserv.thirdparty.iubio.readseq;


import de.unibi.cebitec.bibiserv.thirdparty.flybase.FastVector;
import de.unibi.cebitec.bibiserv.thirdparty.flybase.OpenString;
import java.io.*;
import java.util.*;

import de.unibi.cebitec.bibiserv.thirdparty.Acme.Fmt;


//split4javac// iubio/readseq/BioseqDoc.java line=80
public interface BioseqDoc
	extends BioseqDocVals
{
	public String getID();
	public String getTitle();
	public String getFieldName(int kind); //? change to keys - or enumerate keys?
	public String getDocField(int kind);  
	public String getBiodockey(String field); 

	public void addBasicName(String line);
	public void addDocLine(String line); 	
	public void addDocLine(OpenString line); 	
	public void addDocField(String field, String value, int level, boolean append);
				// ^^ drop leve, append from interface ?
				
	public FastVector documents(); 	//? change to enumeration?
	public FastVector features(); 	//?  ""
	
			//?? add these methdods to iface from Impl
	// setWantedFeatures(exfeatures);
	// SeqRange featsr= bdi.getFeatureRanges(seqlen);
	// replaceDocItem( BioseqDocVals.kSeqlen, ...);

}


