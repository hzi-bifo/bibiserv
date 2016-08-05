/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Benjamin Paassen, CeBiTec,
 * http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2012 Benjamin Paassen"
 *
 * Contributor(s): Benjamin Paassen
 *
 */
package de.unibi.cebitec.bibiserv.search;

import java.util.List;

/**
 * This is an API class representing search results to the outside world.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class OutputSearchResult {

    /**
     * This is the document identifier of this search result.
     */
    private final String ident;
    /**
     * The matches that were found in this document.
     */
    private final List<MatchRepresentation> matches;

    /**
     *
     * @param ident This is the document identifier of this search result.
     * @param matches The matches that were found in this document.
     */
    public OutputSearchResult(String ident, List<MatchRepresentation> matches) {
        this.ident = ident;
        this.matches = matches;
    }

    public List<MatchRepresentation> getMatches() {
        return matches;
    }

    public String getIdent() {
        return ident;
    }
}
