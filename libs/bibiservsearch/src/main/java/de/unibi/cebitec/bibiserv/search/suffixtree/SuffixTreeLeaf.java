/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
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
 * "Portions Copyrighted 2012 BiBiServ Curator Team"
 *
 * Contributor(s): Benjamin Paassen
 *
 */
package de.unibi.cebitec.bibiserv.search.suffixtree;

import de.unibi.cebitec.bibiserv.search.index.WordID;
import java.util.HashSet;

/**
 * This node implements the end of a suffix tree branch with no further edges
 * starting from it.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class SuffixTreeLeaf implements SuffixTreeNode {

    /**
     * Ids of words this branch of the suffix tree is a suffix of.
     */
    private HashSet<WordID> wordIDs = new HashSet<>();

    /**
     * Constructs a new empty end node. If you want to add a word reference,
     * please use the "addWordID" method.
     */
    public SuffixTreeLeaf() {
    }

    /**
     * Add a new word id to this end node.
     *
     * @param id a new word id.
     */
    public void addWordID(WordID id) {
        wordIDs.add(id);
    }

    /**
     *
     * @return a copy of the word ids referenced by this end node.
     */
    public HashSet<WordID> getWordIDs() {
        HashSet<WordID> returnSet = new HashSet<>();
        returnSet.addAll(wordIDs);
        return returnSet;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("(end ->{");
        if (!wordIDs.isEmpty()) {
            for (WordID id : wordIDs) {
                //print all references
                builder.append("\"");
                builder.append(id.getWordReference().getContent());
                builder.append("\",");
            }
            //delete last comma.
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("})");
        return builder.toString();
    }

    @Override
    public String prettyPrint(int depth) {
        return this.toString();
    }

    @Override
    public boolean isEnd() {
        return true;
    }
}
