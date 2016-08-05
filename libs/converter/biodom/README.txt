Copyright 2006 BioDOM Team

******************************************************************************
* About BioDOM                                                               *
******************************************************************************
BioDOM is a JAVA library for creating XML files for bioinformatic date according
to the HOBIT XML Schema definitions and for converting native non-XML output
from various bioinformatic tools to these XML formats.
It is freely available for download and may be included in your own programs 
under the conditions of the Apache Licence 2.0. 

Current situation
Various bioinformatic tools use a great variety of different file formats for
reading data and storing their results. While some formats, like FASTA or
CLUSTAL, are common to several programs, others are utilized by only 
a single tool. Even worse, prevalent formats often lack consistence, e.g. 
there are many different "interpretations" of the FASTA format in use. This 
can probably be accredited to the fact that the majority of formats are not 
well described - a formal definition is missing.

BioDOM formats are formally defined by XSD Schema. The BioDOM package enables
developers to create files in these formats and supplies converters which can
transform common native formats to these XML formats. 
With BioDOM it is easy to define a interface between different bioinformatic 
tools.

Note that BioDOM is a growing product. Currently it implements only the most 
common bioinformatical data types and file formats converters. Once need arises
it can be extended by additional converters and more XML formats.

******************************************************************************
* Getting started															 *
******************************************************************************
-> Untar/unzip the archive to a location of your choice
   I guess you have already done that ... ;) 
   
-> Depending on the package that you have downloaded, you find some files 
   and several folders where you extracted the archive:
   * biodom_1-2-1.jar (file): contains the current BioDOM library and can be used 
     in your applications. All packages execept for the api-release contain
     this file.
   * LICENCE.txt (file): BioDOM is distributed under the Apache Licence 2.0.
     This file explains the licence to you.
   * api (folder): the API to BioDOM can be found in this folder. You can 
     browse it using any webbrowser. The index.html in the folder is the main
     page of the API.
     If there is no folder 'api', then you have probably downloaded the bin
     release. This is smaller, but without the API. You can still use the
     online API or download the API seperately or with another package.
   * src (folder): this folder contains the source code to BioDOM. It is only 
     included in the full release. All other releases are without sources. 
     Sources allow you to modify BioDOM according to your own needs, e.g. 
     implement converters not yet integrated. If you do so... Well, we would
     be happy to hear about that. Perhaps your converter could be integrated in
     the next BioDOM release... ;) 

******************************************************************************
* Supported XSDs															 *
******************************************************************************
In the current version biodom_1-2-2 these XML schemas are supported:

Schema-Name: 	AlignmentML
Namespace: 		http://hobit.sourceforge.net/xsds/20060602/alignmentML
Description:	XML schema for representing multiple sequence alignments

Schema-Name:	SequenceML 	
Namespace:		http://hobit.sourceforge.net/xsds/20060201/sequenceML
Description:	XML schema for representing multiple sequences with some meta 
				information (FASTA replacement)
				
Schema-Name:	RNAStructML
Namespace:		http://hobit.sourceforge.net/xsds/20060201/rnastructML
Description:	XML schema for representing RNA secondary structure information

Schema-Name:	RNAStructAlignmentML
Namespace:		http://hobit.sourceforge.net/xsds/20060515/rnastructAlignmentML
Description:	XML schema for representing multiple RNA secondary structure 
				alignments
	
******************************************************************************
* Changelog																	 *
******************************************************************************
2006-11-7: BioDOM 1.2.2 released
Version 1.2.2 of the BioDOM Library is purely a bugfix update.
a) an error in the bundled sequenceML Schema was fixed

2006-09-04: BioDOM 1.2.1 released
Version 1.2.1 of the BioDOM Library is mainly a bugfix update.
a) building of sequence index in SequenceML has been re-added
b) SequenceML.tofasta() has been overloaded to allow for specification of line-break length in FASTA output
c) some new XSLT scripts for transformation from INSDseq and TinySeq to SequenceML 

2006-08-28: BioDOM 1.2 released
Version 1.2 of the BioDOM Library is mainly a stability update. 
Changes are: 
a) usage of ONLY JAXP interfaces for XML work, thereby dropping dependence
   on any particular Java XML implementation
b) correction of regular expression constants for sequence characters
c) global DocumentBuilder
d) changed default validation state for getDom method to 'ON'
e) own Resolver implementation (optionally, an external Resolver is used if
   available)
f) several new XSL-based import filters.
g) Bugfixes, comments, etc...

2006-07-05: BioDOM 1.1 released
Version 1.1 of the BioDOM Library is a substantial rewrite, aimed at improved
stability and speed. Besides many small code improvements and bugfixes, the
major new features are:
a) XSL based transformation of supported XML Schemas
b) all supported XSDs in all (former and current) versions are now included
   in the release files for easy validation of XML files
c) removed dependencies on 3rd party libraries (log4j, resolver)
d) properties-based configuration of XML namespaces and namespace locations

2006-03-22: BioDOM 1.0 released
We are happy to annonce the release of version 1.0 of BioDOM. This release 
includes support for SequenceML, AlignmentML, RNAStructML and 
RNAStructAlignmentML.