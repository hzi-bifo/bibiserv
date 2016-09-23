Dependency Parser

Der Dependency Parser ist ein in JavaCC 4.0 geschriebener Parser um Abhaengigkeiten zwischen
Parameter bzw. Parametergruppen beschreiben zu koennen. Eine Beschreibung zu dem Parser
bzw. der BNF findet sich im Wiki unter : 

http://wiki.techfak.uni-bielefeld.de/bibiserv/BiBiServ_V2_XMLSServerDescription

Vorraussetzungen :

Java 1.5 oder neuer
JavaCC 4 oder neuer
Netbeans 6.x oder neuer fuer das TreeBeispiel
Xerces im Pfad fuer das Commandline Beispiel

Dateien im Repository:

DependencyParser.jj - DependencyParser in JacaCC 4.0 Syntax
build.xml           - zentrales Build um aus dem .jj File den Parser zu bauen
                    - unterstuetzte Targets:
                      package (default) compiliert und erzeugt ein Jar File im dist ordner
		      compile compiliert den Parser (javacc und javac)
		      clean loescht die generierten Sourcen und Klassen
		      clean_dist loescht alles (clean + pakcage distribution)
		      run (ruft compile auf) startet einen builtin CMDLine Client auf den Beispiel
		          Daten (unter sample)
sample		    - enthaelt zwei Beispieldateien fuer den CMDline Client und ein NetbeansProjekt
		      (Netbeans >= 6.x)

(jkrueger@techfak.uni-bielefeld.de)
