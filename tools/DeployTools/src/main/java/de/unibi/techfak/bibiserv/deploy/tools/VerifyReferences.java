/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
 * All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License("CDDL") (the "License"). You
 * may not use this file except in compliance with the License. You can
 * obtain a copy of the License at http://www.sun.com/cddl/cddl.html
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.  When distributing the software, include
 * this License Header Notice in each file.  If applicable, add the following
 * below the License Header, with the fields enclosed by brackets [] replaced
 *  by your own identifying information:
 *
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 */
package de.unibi.techfak.bibiserv.deploy.tools;

import de.unibi.cebitec.bibiserv.util.bibtexparser.BibtexParser;
import de.unibi.cebitec.bibiserv.util.bibtexparser.ParseException;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiPublication;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;

/**
 *
 * @author jkrueger
 */
public class VerifyReferences extends AbstractVerifyTask {

    public VerifyReferences() {
    }
    
    public VerifyReferences(File file) {
        try {
            setRunnableitemfile(file);
        } catch (Exception ex) {
            throw new BuildException(ex);
        }
    }

    
    
    @Override
    public void execute() throws BuildException {
        super.execute();


        //extract all references from runnableitem

        if (getRunnableitem().isSetReferences() && getRunnableitem().getReferences().isSetReference()) {



            List<BiBiPublication> retlist = new ArrayList<>();
            StringBuilder refbuf = new StringBuilder();
            for (String string : getRunnableitem().getReferences().getReference()) {
                refbuf.append(string);
                refbuf.append("\n");
            }
            BibtexParser parser = new BibtexParser(new StringReader(refbuf.toString()));
            try {
                parser.parse();
                retlist = parser.getPublicationObjects();
            } catch (ParseException ex) {
                System.err.println("Given String could not be parsed as bibreference. Error was:\n" + ex.getLocalizedMessage());
                throw new BuildException(ex);
            }
        }

    }
}